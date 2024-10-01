package com.pay10.crm.action;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.pay10.commons.api.TransactionControllerServiceProvider;
import com.pay10.commons.dao.BeneficiaryAccountsDao;
import com.pay10.commons.dao.NodalAccountDetailsDao;
import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.user.BeneficiaryAccounts;
import com.pay10.commons.user.NodalAccountDetails;
import com.pay10.commons.user.User;
import com.pay10.commons.user.UserDao;
import com.pay10.commons.util.AccountStatus;
import com.pay10.commons.util.AcquirerTypeNodal;
import com.pay10.commons.util.BeneficiaryTypes;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.CrmFieldType;
import com.pay10.commons.util.CrmValidator;
import com.pay10.commons.util.CurrencyTypes;
import com.pay10.commons.util.DateCreater;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.NodalPaymentTypes;
import com.pay10.commons.util.NodalPaymentTypesYesBankFT3;
import com.pay10.commons.util.SettlementTransactionType;
import com.pay10.commons.util.TransactionManager;

/**
 * 
 * @author shubhamchauhan
 *
 */
public class MerchantNodalPayoutAccountDetailsAction extends AbstractSecureAction {
	
	private static final long serialVersionUID = -5862895125960466023L;

	private static Logger logger = LoggerFactory.getLogger(MerchantNodalPayoutAccountDetailsAction.class.getName());
	
	List<String> aaData;
	
	@Autowired
	CrmValidator validator;
	
	@Autowired
	BeneficiaryAccountsDao beneficiaryAccountsDao;
	
	@Autowired
	NodalAccountDetailsDao nodalAccountDetailsDao;
	
	@Autowired
	private TransactionControllerServiceProvider transactionControllerServiceProvider;
	
	@Autowired
	UserDao userDao;
	
	private String payId;
	private String response;
	private String acquirer;
	
	@SuppressWarnings("unchecked")
	@Override
	public String execute() {
		try {

			logger.info("Fetching merchant payout account details for pay id : " + payId);
			User user = userDao.findPayId(payId);
			
			if(user == null) {
				logger.info("User not found for pay id : " + payId);
				setResponse("User not found.");
				return SUCCESS;
			}
			
			List<BeneficiaryAccounts> beneficiaryAccountsList = beneficiaryAccountsDao.findMerchantTypeBeneficiary(payId, BeneficiaryTypes.Merchant.getCode(), AccountStatus.ACTIVE.getCode());
			
			BeneficiaryAccounts beneAccount = null;
			JSONObject paymentTypes = new JSONObject();
			for(BeneficiaryAccounts beneficiaryAccounts : beneficiaryAccountsList) {
				if(beneficiaryAccounts.getBeneAccountNo().trim().equals(user.getAccountNo().trim()) && beneficiaryAccounts.getIfscCode().trim().equals(user.getIfscCode().trim()) && beneficiaryAccounts.getStatus().equals(AccountStatus.ACTIVE)) {
					paymentTypes.put(beneficiaryAccounts.getPaymentType().getCode(), beneficiaryAccounts.getPaymentType().getName());
					beneAccount = beneficiaryAccounts;
				}
			}
			
			if(paymentTypes.isEmpty()) {
				logger.info("No beneficiary found for merchant current account number : " + user.getAccountNo() + " : IFSC Code : " + user.getIfscCode() + "and  pay id : " + payId);
				
				// Add Beneficiary for user current account number and ifsc
				String addBeneResponse = addBeneficiary(user);
				if(!(addBeneResponse.equalsIgnoreCase("success"))) {
					logger.info("Unable to add beneficiary for merchant current account number : " + user.getAccountNo());
//					setResponse("Unable to add beneficiary for merchant current account number : " + user.getAccountNo());
					return SUCCESS;
				}
				
				beneficiaryAccountsList = beneficiaryAccountsDao.findMerchantTypeBeneficiary(payId, BeneficiaryTypes.Merchant.getCode(), AccountStatus.ACTIVE.getCode());
				for(BeneficiaryAccounts beneficiaryAccounts : beneficiaryAccountsList) {
					if(beneficiaryAccounts.getBeneAccountNo().trim().equals(user.getAccountNo().trim()) && beneficiaryAccounts.getIfscCode().trim().equals(user.getIfscCode().trim()) && beneficiaryAccounts.getStatus().equals(AccountStatus.ACTIVE)) {
						paymentTypes.put(beneficiaryAccounts.getPaymentType().getCode(), beneficiaryAccounts.getPaymentType().getName());
						beneAccount = beneficiaryAccounts;
					}
				}
			}
			
			if(paymentTypes.isEmpty()) {
				logger.info("No beneficiary found for merchant current account number : " + user.getAccountNo() + " : IFSC code : " + user.getIfscCode());
				setResponse("No beneficiary found for merchant current account number : " + user.getAccountNo() + " : IFSC code : " + user.getIfscCode());
				return SUCCESS;
			}
			
			if(beneAccount == null) {
				logger.info("No beneficiary found for merchant current account number : " + user.getAccountNo() + " : IFSC code : " + user.getIfscCode());
				setResponse("No beneficiary found for merchant current account number : " + user.getAccountNo() + " : IFSC code : " + user.getIfscCode());
				return SUCCESS;
			}
			String merchantProvidedCode = beneAccount.getMerchantProvidedId();
			String beneficiaryAccountNumber = beneAccount.getBeneAccountNo();
			String beneficiaryIfscCode = beneAccount.getIfscCode();
			
			JSONObject json = new JSONObject();
			aaData = new ArrayList<>();
			json.put("beneficiaryCode", merchantProvidedCode);
			json.put("beneficiaryAccountNumber", beneficiaryAccountNumber);
			json.put("beneficiaryIfscCode", beneficiaryIfscCode);
			json.put("currentAccountNumber", user.getAccountNo());
			json.put("currentAccountHolderName", user.getAccHolderName());
			json.put("currentAccountIfscCode", user.getIfscCode());
			json.put("currentAccountBankName", user.getBankName());
			json.put("currentAccountMerchantEmailId", user.getEmailId());
			json.put("paymentType", paymentTypes);
			
			aaData.add(json.toString());
			setResponse("");
		} catch(Exception e) {
			logger.error("Exception while getting merchant nodal payout account details.");
			logger.error(e.getMessage());
			setResponse("Failed to get account details.");
		}
		
		return SUCCESS;
	}
	
	private String addBeneficiary(User merchant) {
		logger.info("Adding merchant type beneficiary for account number : " + merchant.getAccountNo() + " : IFSC code : " + merchant.getIfscCode());
		String failed = "Failed";
		String success = "Success";
		String pending = "Pending";
		try {
			User sessionUser = (User) sessionMap.get(Constants.USER.getValue());
			
			String merchantPayId = merchant.getPayId();
			String sessionUserEmailId = sessionUser.getEmailId();
			String businessName = merchant.getBusinessName();
			String ifscCode = merchant.getIfscCode();
			String merchantAccountNumber = merchant.getAccountNo();
			String beneCodeNameId = TransactionManager.getNewTransactionId();
			String merchantProvidedCode = "BC" + beneCodeNameId;
			String bankName = merchant.getBankName();
			String mobileNumber = "";
			String aadharNumber = "";
			String merchantAddress = "";
			String merchantEmail = merchant.getEmailId();
			String currency = CurrencyTypes.INR.getCode();
			
			if(StringUtils.isBlank(ifscCode)) {
				setResponse("IFSC code is mandatory.");
				return failed;
			}

			if(StringUtils.isBlank(bankName)) {
				setResponse("Bank name is mandatory.");
				return failed;
			}

			if(StringUtils.isBlank(merchantAccountNumber)) {
				setResponse("Account Number is mandatory.");
				return failed;
			}

			if(StringUtils.isBlank(currency)) {
				currency = merchant.getDefaultCurrency();
			}
			Map<String, String> responseMap = new HashMap<>();
			
			logger.info("Merchant Provided code generated : " + merchantProvidedCode);
			
			List<String> nodalPaymentTypesList = new ArrayList<>();
			for(NodalPaymentTypesYesBankFT3 pt : NodalPaymentTypesYesBankFT3.values()) {
				// Don't add payment type FT for ifsc code other than yes bank
				if(!(ifscCode.startsWith("YESB")) && pt.equals(NodalPaymentTypesYesBankFT3.FT)) {
					continue;
				}
				nodalPaymentTypesList.add(pt.getCode());
			}
			
			List<AccountStatus> statusList = new ArrayList<>();
			statusList.add(AccountStatus.ACTIVE);
			statusList.add(AccountStatus.INACTIVE);
			statusList.add(AccountStatus.PENDING);
			statusList.add(AccountStatus.IN_PROCESS);
			
			// Validate if beneficiary Code already used or not.
			List<BeneficiaryAccounts> beneList = beneficiaryAccountsDao.findByMerchantProvidedCodePayIdPaymentTypeStatus(merchantProvidedCode, merchantPayId, nodalPaymentTypesList, statusList);
			for(BeneficiaryAccounts beneAccount : beneList) {
				if(nodalPaymentTypesList.contains(beneAccount.getPaymentType().getCode())) {
					nodalPaymentTypesList.remove(beneAccount.getPaymentType().getCode());
					responseMap.put(NodalPaymentTypesYesBankFT3.getInstancefromCode(beneAccount.getPaymentType().getCode()).getName(), "Beneficiary code already present for this payment type : Status : " + beneAccount.getStatus().getCode());
				}
			}
			
			if(nodalPaymentTypesList.isEmpty()){
				logger.info("Beneficiary code already present : " + merchantProvidedCode);
				logger.info(responseMap.toString());
				return failed;
			}
			
			// Validate if account number and IFSC code are already in use.
			Long count = beneficiaryAccountsDao.findByPayIdAccountNumberIfscCode(merchantPayId, merchantAccountNumber, ifscCode, statusList, BeneficiaryTypes.Merchant.getCode());
			// Uncomment below line to send request forward if bene is not added for any payment type.
//			if(nodalPaymentTypesList.isEmpty() && count > 0) {
			if(count > 0) {
				logger.info("Beneficiary already exists for this Account number and IFSC code.");
				return success;
			}
			
			NodalAccountDetails nodalAccountDetails = nodalAccountDetailsDao.find(acquirer);
			
			if(nodalAccountDetails == null) {
				logger.info("Nodal account not found in database for acquirer : " + acquirer);
				return success;
			}
			
			String date = DateCreater.formatDateForDb(new Date());
			Fields fields = new Fields();
			fields.put(FieldType.BENE_NAME.getName(), beneCodeNameId);
			fields.put(FieldType.BENEFICIARY_CD.getName(), beneCodeNameId);
			fields.put(FieldType.CUST_ID_BENEFICIARY.getName(), nodalAccountDetails.getCustId());
			fields.put(FieldType.PAY_ID.getName(), merchantPayId); // get from session
			fields.put(FieldType.BENE_MERCHANT_PROVIDED_ID.getName(), merchantProvidedCode);
			fields.put(FieldType.BENE_MERCHANT_PROVIDED_NAME.getName(), merchantProvidedCode);
			fields.put(FieldType.BENE_ACCOUNT_NO.getName(), merchantAccountNumber);
			fields.put(FieldType.IFSC_CODE.getName(), ifscCode);
			fields.put(FieldType.BENE_TYPE.getName(), BeneficiaryTypes.Merchant.getBankCode());
			fields.put(FieldType.CURRENCY_CD.getName(), currency);
			fields.put(FieldType.NODAL_ACQUIRER.getName(), getAcquirer());
			fields.put(FieldType.BANK_NAME.getName(), bankName);
			fields.put(FieldType.SRC_ACCOUNT_NO.getName(), nodalAccountDetails.getAccountNumber());
			fields.put(FieldType.TXNTYPE.getName(), SettlementTransactionType.ADD_BENEFICIARY.getName());
			fields.put(FieldType.BENE_EXPIRY_DATE.getName(), "");
			fields.put(FieldType.BENE_MOBILE_NUMBER.getName(), mobileNumber);
			fields.put(FieldType.BENE_EMAIL_ID.getName(), merchantEmail);
			fields.put(FieldType.BENE_ADDRESS_1.getName(), merchantAddress);
			fields.put(FieldType.BENE_ADDRESS_2.getName(), merchantAddress);
			fields.put(FieldType.BENE_AADHAR_NO.getName(), aadharNumber);

			
			List<BeneficiaryAccounts> beneAccountList = new ArrayList<>();
			String pgResponse = "";
			// Loop for Nodal Payment Types
			for(String payType : nodalPaymentTypesList) {
				// Don't add bene for payment type FT. Handled above
				fields.put(FieldType.BENE_ID.getName(), TransactionManager.getNewTransactionId());
				NodalPaymentTypesYesBankFT3 pt = NodalPaymentTypesYesBankFT3.getInstancefromCode(payType);
				fields.put(FieldType.PAYMENT_TYPE.getName(), pt.getName());
				fields.put(FieldType.BENE_TRANSACTION_LIMIT.getName(), pt.getDefaultTransactionLimit());
				fields.logAllFields("Merchant Nodal Add beneficiary Request : ");
				Map<String, String> respMap = transactionControllerServiceProvider.nodalSettlementTransact(fields);
				logger.info("Response received from WS for add beneficiary : " + respMap);
				if(respMap.isEmpty()) {
					// turn off pgws and check response
					responseMap.put(pt.getName(), "Unknown Error");
					continue;
				}
				String responseMessage = "";
				if (StringUtils.isNotBlank(respMap.get(FieldType.RESPONSE_MESSAGE.getName()))) {
					responseMessage = respMap.get(FieldType.RESPONSE_MESSAGE.getName());
				} else {
					responseMessage = AccountStatus.REJECTED.getName();
				} 
				
				if (StringUtils.isNotBlank(respMap.get(FieldType.PG_TXN_MESSAGE.getName()))) {
					pgResponse = respMap.get(FieldType.PG_TXN_MESSAGE.getName());
				} 
				
				String rrn = "";
				if (StringUtils.isNotBlank(respMap.get(FieldType.RRN.getName()))) {
					rrn = respMap.get(FieldType.RRN.getName());
				}
				logger.info("Inside save beneficiary , rrn  = " + rrn);
				BeneficiaryAccounts beneficiaryAccounts = new BeneficiaryAccounts();
				if (responseMessage.equalsIgnoreCase("SUCCESS")) {
					beneficiaryAccounts.setStatus(AccountStatus.ACTIVE);
					responseMap.put(pt.getName(), success);
				} else { 
					beneficiaryAccounts.setStatus(AccountStatus.PENDING);
					responseMap.put(pt.getName(), pending);
				}
				
				beneficiaryAccounts.setId(fields.get(FieldType.BENE_ID.getName()));
				// TODO Get data from fields not from response map.
				beneficiaryAccounts.setCustId(respMap.get(FieldType.CUST_ID_BENEFICIARY.getName()));
				beneficiaryAccounts.setSrcAccountNo(respMap.get(FieldType.SRC_ACCOUNT_NO.getName()));
				beneficiaryAccounts.setBeneName(fields.get(FieldType.BENE_NAME.getName()));
				beneficiaryAccounts.setBeneAccountNo(fields.get(FieldType.BENE_ACCOUNT_NO.getName()));
				beneficiaryAccounts.setIfscCode(fields.get(FieldType.IFSC_CODE.getName()));
				beneficiaryAccounts.setPaymentType(NodalPaymentTypes.getInstancefromName(respMap.get(FieldType.PAYMENT_TYPE.getName())));
				beneficiaryAccounts.setBeneType(BeneficiaryTypes.Merchant.getCode());
				beneficiaryAccounts.setCurrencyCd(CurrencyTypes.getInstancefromCode(currency));
				beneficiaryAccounts.setAcquirer(getAcquirer());
				beneficiaryAccounts.setBeneficiaryCd(fields.get(FieldType.BENEFICIARY_CD.getName()));
				beneficiaryAccounts.setRrn(rrn);
				beneficiaryAccounts.setResponseMessage(responseMessage);
				beneficiaryAccounts.setCreatedDate(date);
				beneficiaryAccounts.setUpdatedDate(date);
				beneficiaryAccounts.setRequestedBy(sessionUserEmailId); // get from session user.
				beneficiaryAccounts.setProcessedBy(sessionUserEmailId); // get from session user.
				beneficiaryAccounts.setBankName(bankName);
				beneficiaryAccounts.setMerchantPayId(merchantPayId);
				beneficiaryAccounts.setMerchantProvidedId(fields.get(FieldType.BENE_MERCHANT_PROVIDED_ID.getName()));
				beneficiaryAccounts.setMerchantProvidedName(fields.get(FieldType.BENE_MERCHANT_PROVIDED_NAME.getName()));
				beneficiaryAccounts.setBeneExpiryDate(fields.get(FieldType.BENE_EXPIRY_DATE.getName()));
				beneficiaryAccounts.setMobileNo(fields.get(FieldType.BENE_MOBILE_NUMBER.getName()));
				beneficiaryAccounts.setEmailId(fields.get(FieldType.BENE_EMAIL_ID.getName()));
				beneficiaryAccounts.setAddress1(fields.get(FieldType.BENE_ADDRESS_1.getName()));
				beneficiaryAccounts.setAddress2(fields.get(FieldType.BENE_ADDRESS_2.getName()));
				beneficiaryAccounts.setTransactionLimit(fields.get(FieldType.BENE_TRANSACTION_LIMIT.getName()));
				beneficiaryAccounts.setAadharNo(fields.get(FieldType.BENE_AADHAR_NO.getName()));
				beneficiaryAccounts.setMerchantBusinessName(businessName); 
				beneficiaryAccounts.setPgRespCode(respMap.get(FieldType.PG_RESP_CODE.getName()));
				beneficiaryAccounts.setPgTxnMessage(respMap.get(FieldType.PG_TXN_MESSAGE.getName()));
				beneficiaryAccounts.setBankRequest(respMap.get(FieldType.INTERNAL_REQUEST_FIELDS.getName()));
				beneficiaryAccounts.setBankResponse(respMap.get(FieldType.INTERNAL_RESPONSE_FIELDS.getName()));
				
				beneAccountList.add(beneficiaryAccounts);
			}
			boolean hasSuccess = false;
			for(Map.Entry<String, String> keySet : responseMap.entrySet()) {
				if(keySet.getValue().equalsIgnoreCase(success)) {
					hasSuccess = true;
				}
			}
			
			if(!hasSuccess) {
				for(Map.Entry<String, String> keySet : responseMap.entrySet()) {
					responseMap.put(keySet.getKey(), failed + " : " + pgResponse);
				}
				logger.info(responseMap.toString());
				return SUCCESS;
			}
			
			for(BeneficiaryAccounts beneficiaryAccounts : beneAccountList) {
				if(beneficiaryAccountsDao.create(beneficiaryAccounts)) {
					logger.info("Inserted bene successfully : " + beneficiaryAccounts.getMerchantProvidedId() + " : " + beneficiaryAccounts.getPaymentType());
				}
			}
			logger.info(responseMap.toString());
		} catch(Exception e) {
			logger.error("Failed to add beneficiary");
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		return success;
	}

	@Override
	public void validate() {
		if (validator.validateBlankField(getPayId()) || !(validator.validateField(CrmFieldType.PAY_ID, getPayId()))) {
			addFieldError(CrmFieldType.PAY_ID.getName(), validator.getResonseObject().getResponseMessage());
		}
		
		if(validator.validateBlankField(getAcquirer())) {
			addFieldError(CrmFieldType.BENE_NODAL_ACQUIRER.getName(), validator.getResonseObject().getResponseMessage());
		}else if (AcquirerTypeNodal.getInstancefromCode(getAcquirer()) == null) {
			addFieldError(CrmFieldType.BENE_NODAL_ACQUIRER.getName(), ErrorType.INVALID_FIELD_VALUE.getResponseMessage());
		}
	}

	public String getPayId() {
		return payId;
	}

	public void setPayId(String payId) {
		this.payId = payId;
	}

	public String getResponse() {
		return response;
	}

	public void setResponse(String response) {
		this.response = response;
	}

	public List<String> getAaData() {
		return aaData;
	}

	public void setAaData(List<String> aaData) {
		this.aaData = aaData;
	}

	public String getAcquirer() {
		return acquirer;
	}

	public void setAcquirer(String acquirer) {
		this.acquirer = acquirer;
	}
}
