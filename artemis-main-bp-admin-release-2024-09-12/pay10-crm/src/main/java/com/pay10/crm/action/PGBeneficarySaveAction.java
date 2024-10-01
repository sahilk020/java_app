package com.pay10.crm.action;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.pay10.commons.api.TransactionControllerServiceProvider;
import com.pay10.commons.dao.BeneficiaryAccountsDao;
import com.pay10.commons.dao.NodalAccountDetailsDao;
import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.user.BeneficiaryAccounts;
import com.pay10.commons.user.NodalAccountDetails;
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
import com.pay10.commons.util.PropertiesManager;
import com.pay10.commons.util.SettlementTransactionType;
import com.pay10.commons.util.TransactionManager;

/**
 * 
 * @author shubhamchauhan
 *
 */
public class PGBeneficarySaveAction extends AbstractSecureAction implements ServletRequestAware {

	private static final long serialVersionUID = 5654089288878456659L;
	
	private static Logger logger = LoggerFactory.getLogger(PGBeneficarySaveAction.class.getName());
	
	@Autowired
	NodalAccountDetailsDao nodalAccountDetailsDao;
	
	@Autowired
	private TransactionControllerServiceProvider transactionControllerServiceProvider;
	
	@Autowired
	private BeneficiaryAccountsDao beneficiaryAccountsDao;
	
	@Autowired
	private CrmValidator validator;
	
	private String merchantProvidedCode;
	private String merchantProvidedName;
	private String expiryDate;
	private String currencyCode;
	private String bankName;
	private String ifscCode;
	private String accountNumber;
	private String mobileNumber;
	private String emailId;
	private String address1;
	private String address2;
	private String aadharNumber;
	private String payId;
	private String acquirer;
	private String beneficiaryType;
	private String initatedBy;
	private String processedBy;
	private String businessName;
	private HttpServletRequest httpRequest;
	
	private String response;
	
	Map<String, String> responseMap = new HashMap<>();
	
	
	@Override
	public String execute() {
		logger.info("Adding PG type beneficiary.");
		String failed = "Failed";
		String success = "Success";
		String pending = "Pending";
		try {
			String reqToken = httpRequest.getHeader("Authorization");
			String uptimeToken = PropertiesManager.propertiesMap.get(Constants.INTERNAL_API_TOKEN.getValue());
			if (uptimeToken == null || !uptimeToken.equals(reqToken)) {
				logger.info("Request Token : " + reqToken);
				logger.error("Token mismatch");
				setResponse("Token mismatch");
				return SUCCESS;
			}
			
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
			List<BeneficiaryAccounts> beneList = beneficiaryAccountsDao.findByMerchantProvidedCodePayIdPaymentTypeStatus(getMerchantProvidedCode(), payId, nodalPaymentTypesList, statusList);
			for(BeneficiaryAccounts beneAccount : beneList) {
				if(nodalPaymentTypesList.contains(beneAccount.getPaymentType().getCode())) {
					nodalPaymentTypesList.remove(beneAccount.getPaymentType().getCode());
					responseMap.put(NodalPaymentTypesYesBankFT3.getInstancefromCode(beneAccount.getPaymentType().getCode()).getName(), "Beneficiary code already present for this payment type : Status : " + beneAccount.getStatus().getCode());
				}
			}
			
			if(nodalPaymentTypesList.isEmpty()){
				logger.info("Beneficiary code already present.");
				setResponse("Beneficiary code already present.");
				return SUCCESS;
			}
			
			// Validate if account number and IFSC code are already in use.
			Long count = beneficiaryAccountsDao.findByPayIdAccountNumberIfscCode(payId, getAccountNumber(), getIfscCode(), statusList, null);
			// Uncomment below line to send request forward if bene is not added for any payment type.
//			if(nodalPaymentTypesList.isEmpty() && count > 0) {
			if(count > 0) {
				logger.info("Beneficiary already exists for this Account number and IFSC code.");
				setResponse("Beneficiary already exists for this Account number and IFSC code.");
				return SUCCESS;
			}
			
			NodalAccountDetails nodalAccountDetails = nodalAccountDetailsDao.find(acquirer);
			
			if(nodalAccountDetails == null) {
				logger.info("Nodal account not found in database for acquirer : " + acquirer);
				setResponse("Nodal Account details not found");
				return SUCCESS;
			}
			
			String date = DateCreater.formatDateForDb(new Date());
			Fields fields = new Fields();
			
			String beneCodeNameId = TransactionManager.getNewTransactionId();
			fields.put(FieldType.BENE_NAME.getName(), beneCodeNameId);
			fields.put(FieldType.BENEFICIARY_CD.getName(), beneCodeNameId);
			fields.put(FieldType.CUST_ID_BENEFICIARY.getName(), nodalAccountDetails.getCustId());
			fields.put(FieldType.PAY_ID.getName(), payId); 
			fields.put(FieldType.BENE_MERCHANT_PROVIDED_ID.getName(), getMerchantProvidedCode());
			fields.put(FieldType.BENE_MERCHANT_PROVIDED_NAME.getName(), getMerchantProvidedName());
			fields.put(FieldType.BENE_ACCOUNT_NO.getName(), getAccountNumber());
			fields.put(FieldType.IFSC_CODE.getName(), getIfscCode());
			fields.put(FieldType.BENE_TYPE.getName(), BeneficiaryTypes.getInstancefromCode(getBeneficiaryType()).getBankCode());
			fields.put(FieldType.CURRENCY_CD.getName(), getCurrencyCode());
			fields.put(FieldType.NODAL_ACQUIRER.getName(), getAcquirer());
			fields.put(FieldType.BANK_NAME.getName(), getBankName());
			fields.put(FieldType.SRC_ACCOUNT_NO.getName(), nodalAccountDetails.getAccountNumber());
			fields.put(FieldType.TXNTYPE.getName(), SettlementTransactionType.ADD_BENEFICIARY.getName());
			fields.put(FieldType.BENE_EXPIRY_DATE.getName(), getExpiryDate());
			fields.put(FieldType.BENE_MOBILE_NUMBER.getName(), getMobileNumber());
			fields.put(FieldType.BENE_EMAIL_ID.getName(), getEmailId());
			fields.put(FieldType.BENE_ADDRESS_1.getName(), getAddress1());
			fields.put(FieldType.BENE_ADDRESS_2.getName(), getAddress2());
			fields.put(FieldType.BENE_AADHAR_NO.getName(), getAadharNumber());
			fields.put(FieldType.REQUEST_CHANNEL.getName(), "CRM");
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
				beneficiaryAccounts.setBeneType(getBeneficiaryType());
				beneficiaryAccounts.setCurrencyCd(CurrencyTypes.getInstancefromCode(getCurrencyCode()));
				beneficiaryAccounts.setAcquirer(getAcquirer());
				beneficiaryAccounts.setBeneficiaryCd(fields.get(FieldType.BENEFICIARY_CD.getName()));
				beneficiaryAccounts.setRrn(rrn);
				beneficiaryAccounts.setResponseMessage(responseMessage);
				beneficiaryAccounts.setCreatedDate(date);
				beneficiaryAccounts.setUpdatedDate(date);
				beneficiaryAccounts.setRequestedBy(initatedBy); 
				beneficiaryAccounts.setProcessedBy(processedBy); 
				beneficiaryAccounts.setBankName(getBankName());
				beneficiaryAccounts.setMerchantPayId(payId);
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
				setResponse(failed);
				return SUCCESS;
			}
			
			for(BeneficiaryAccounts beneficiaryAccounts : beneAccountList) {
				if(beneficiaryAccountsDao.create(beneficiaryAccounts)) {
					logger.info("Inserted bene successfully : " + beneficiaryAccounts.getMerchantProvidedId() + " : " + beneficiaryAccounts.getPaymentType());
				}
			}
			setResponse("");
		
			
			
			
		} catch(Exception e) {
			logger.info("Failed to add beneficiary.");
			logger.info(e.getMessage());
			e.printStackTrace();
			setResponse("Failed to add beneficiary.");
		}
		return SUCCESS;
	}
	
	@Override
	public void validate() {
		setBeneficiaryType(BeneficiaryTypes.PG.getCode());
		setAcquirer(AcquirerTypeNodal.YESBANKFT3.getCode());
		
		if ((validator.validateBlankField(getMerchantProvidedCode())) || !(validator.validateField(CrmFieldType.BENEFICIARY_CD, getMerchantProvidedCode()))) {
			addFieldError(CrmFieldType.BENEFICIARY_CD.getName(), validator.getResonseObject().getResponseMessage());
		}  else if (validator.allZerosCharacters(getMerchantProvidedCode())) {
			addFieldError(CrmFieldType.BENEFICIARY_CD.getName(), ErrorType.INVALID_FIELD_VALUE.getResponseMessage());
		} 

 		if ((validator.validateBlankField(getMerchantProvidedName())) || !(validator.validateField(CrmFieldType.BENE_NAME, getMerchantProvidedName()))) {
			addFieldError(CrmFieldType.BENE_NAME.getName(), validator.getResonseObject().getResponseMessage());
		}

		if ((validator.validateBlankField(getExpiryDate()))) {
		} else {
			SimpleDateFormat sdformat = new SimpleDateFormat("yyyy-MM-dd");
			sdformat.setLenient(false);
			ParsePosition position = new ParsePosition(0);
			try {
				Date d1 = sdformat.parse(getExpiryDate(), position);
				if (d1 == null || position.getIndex() != getExpiryDate().length()) {
				    addFieldError(CrmFieldType.BENE_EXPIRY_DATE.getName(), ErrorType.INVALID_FIELD_VALUE.getResponseMessage());
				} else {
					d1 = sdformat.parse(getExpiryDate());
					String d2f = sdformat.format(new Date());
					Date d2 = sdformat.parse(d2f);
					
					if(d1.compareTo(d2) <= 0) {
						addFieldError(CrmFieldType.BENE_EXPIRY_DATE.getName(), "Expiry Date should be greater than current date");
					}
				}
			} catch (Exception e) {
				addFieldError(CrmFieldType.BENE_EXPIRY_DATE.getName(), ErrorType.INVALID_FIELD.getResponseMessage());
			}
		}

		if ((validator.validateBlankField(getCurrencyCode())) || CurrencyTypes.getInstancefromCode(getCurrencyCode()) == null) {
			addFieldError(CrmFieldType.CURRENCY_CD.getName(), validator.getResonseObject().getResponseMessage());
		}

		if ((validator.validateBlankField(getBankName())) || !(validator.validateField(CrmFieldType.BENE_BANK_NAME, getBankName()))) {
			addFieldError(CrmFieldType.BENE_BANK_NAME.getName(), validator.getResonseObject().getResponseMessage());
		}

		if ((validator.validateBlankField(getIfscCode())) || !(validator.validateField(CrmFieldType.IFSC_CODE, getIfscCode()))) {
			addFieldError(CrmFieldType.IFSC_CODE.getName(), validator.getResonseObject().getResponseMessage());
		}

		if ((validator.validateBlankField(getAccountNumber())) || !(validator.validateField(CrmFieldType.BENE_ACCOUNT_NO, getAccountNumber()))) {
			addFieldError(CrmFieldType.BENE_ACCOUNT_NO.getName(), validator.getResonseObject().getResponseMessage());
		} else if(validator.allZerosCharacters(getAccountNumber())) {
			addFieldError(CrmFieldType.BENE_ACCOUNT_NO.getName(), ErrorType.INVALID_FIELD_VALUE.getResponseMessage());
		}

		if (validator.validateBlankField(getMobileNumber())) {
		} else if (!(validator.validateField(CrmFieldType.BENE_MOBILE_NUMBER, getMobileNumber()))) {
			addFieldError(CrmFieldType.BENE_MOBILE_NUMBER.getName(), validator.getResonseObject().getResponseMessage());
		} else if (validator.allZerosCharacters(getMobileNumber())) {
			addFieldError(CrmFieldType.BENE_MOBILE_NUMBER.getName(), ErrorType.INVALID_FIELD_VALUE.getResponseMessage());
		}
		

		if (validator.validateBlankField(getEmailId())) {
		} else if (!(validator.validateField(CrmFieldType.BENE_EMAIL_ID, getEmailId()))) {
			addFieldError(CrmFieldType.BENE_EMAIL_ID.getName(), ErrorType.INVALID_FIELD_VALUE.getResponseMessage());
		}
		
		if (validator.validateBlankField(getInitatedBy())) {
			addFieldError(FieldType.BENE_REQUESTED_BY.getName(), ErrorType.EMPTY_FIELD.getResponseMessage());
		} else if (!(validator.validateField(CrmFieldType.BENE_EMAIL_ID, getInitatedBy()))) {
			addFieldError(FieldType.BENE_REQUESTED_BY.getName(), ErrorType.INVALID_FIELD_VALUE.getResponseMessage());
		}
		
		if (validator.validateBlankField(getProcessedBy())) {
			addFieldError(FieldType.BENE_PROCESSED_BY.getName(), ErrorType.EMPTY_FIELD.getResponseMessage());
		} else if (!(validator.validateField(CrmFieldType.BENE_EMAIL_ID, getProcessedBy()))) {
			addFieldError(FieldType.BENE_PROCESSED_BY.getName(), ErrorType.INVALID_FIELD_VALUE.getResponseMessage());
		}

		if (validator.validateBlankField(getAddress1())) {
		} else if (!(validator.validateField(CrmFieldType.BENE_ADDRESS_1, getAddress1()))) {
			addFieldError(CrmFieldType.BENE_ADDRESS_1.getName(), validator.getResonseObject().getResponseMessage());
		}

		if (validator.validateBlankField(getAddress2())) {
		} else if (!(validator.validateField(CrmFieldType.BENE_ADDRESS_2, getAddress2()))) {
			addFieldError(CrmFieldType.BENE_ADDRESS_2.getName(), validator.getResonseObject().getResponseMessage());
		}

		if (validator.validateBlankField(getAadharNumber())) {
		} else if (!(validator.validateField(CrmFieldType.BENE_AADHAR_NO, getAadharNumber()))) {
			addFieldError(CrmFieldType.BENE_AADHAR_NO.getName(), validator.getResonseObject().getResponseMessage());
		} else if(validator.allZerosCharacters(getAadharNumber())){
			addFieldError(CrmFieldType.BENE_AADHAR_NO.getName(),  ErrorType.INVALID_FIELD_VALUE.getResponseMessage());
		}
		
		if (validator.validateBlankField(getPayId())) {
			addFieldError(CrmFieldType.PAY_ID.getName(), ErrorType.EMPTY_FIELD.getResponseMessage());
		}
		
		if (validator.validateBlankField(getBusinessName())) {
			addFieldError(CrmFieldType.BUSINESS_NAME.getName(), ErrorType.EMPTY_FIELD.getResponseMessage());
		}
		
		if (validator.validateBlankField(getAcquirer())) {
			addFieldError(CrmFieldType.BENE_NODAL_ACQUIRER.getName(), ErrorType.EMPTY_FIELD.getResponseMessage());
		} else if ((AcquirerTypeNodal.getAcquirerName(getAcquirer()) == null)) {
			addFieldError(CrmFieldType.BENE_NODAL_ACQUIRER.getName(), ErrorType.INVALID_FIELD_VALUE.getResponseMessage());
		}
		
		if (validator.validateBlankField(getBeneficiaryType())) {
			addFieldError(CrmFieldType.BENE_TYPE.getName(), ErrorType.EMPTY_FIELD.getResponseMessage());
		} else if ((BeneficiaryTypes.getInstancefromCode(getBeneficiaryType()) == null)) {
			addFieldError(CrmFieldType.BENE_TYPE.getName(), ErrorType.INVALID_FIELD_VALUE.getResponseMessage());
		}
	}
	
	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.httpRequest = request;
	}
	public String getResponse() {
		return response;
	}
	public void setResponse(String response) {
		this.response = response;
	}
	public String getMerchantProvidedCode() {
		return merchantProvidedCode;
	}
	public void setMerchantProvidedCode(String merchantProvidedCode) {
		this.merchantProvidedCode = merchantProvidedCode;
	}
	public String getMerchantProvidedName() {
		return merchantProvidedName;
	}
	public void setMerchantProvidedName(String merchantProvidedName) {
		this.merchantProvidedName = merchantProvidedName;
	}
	public String getExpiryDate() {
		return expiryDate;
	}
	public void setExpiryDate(String expiryDate) {
		this.expiryDate = expiryDate;
	}
	public String getCurrencyCode() {
		return currencyCode;
	}
	public void setCurrencyCode(String currencyCode) {
		this.currencyCode = currencyCode;
	}
	public String getBankName() {
		return bankName;
	}
	public void setBankName(String bankName) {
		this.bankName = bankName;
	}
	public String getIfscCode() {
		return ifscCode;
	}
	public void setIfscCode(String ifscCode) {
		this.ifscCode = ifscCode;
	}
	public String getAccountNumber() {
		return accountNumber;
	}
	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}
	public String getMobileNumber() {
		return mobileNumber;
	}
	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}
	public String getEmailId() {
		return emailId;
	}
	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}
	public String getAddress1() {
		return address1;
	}
	public void setAddress1(String address1) {
		this.address1 = address1;
	}
	public String getAddress2() {
		return address2;
	}
	public void setAddress2(String address2) {
		this.address2 = address2;
	}
	public String getAadharNumber() {
		return aadharNumber;
	}
	public void setAadharNumber(String aadharNumber) {
		this.aadharNumber = aadharNumber;
	}
	public Map<String, String> getResponseMap() {
		return responseMap;
	}
	public void setResponseMap(Map<String, String> responseMap) {
		this.responseMap = responseMap;
	}

	public String getPayId() {
		return payId;
	}

	public void setPayId(String payId) {
		this.payId = payId;
	}

	public String getAcquirer() {
		return acquirer;
	}

	public void setAcquirer(String acquirer) {
		this.acquirer = acquirer;
	}

	public String getBeneficiaryType() {
		return beneficiaryType;
	}

	public void setBeneficiaryType(String beneficiaryType) {
		this.beneficiaryType = beneficiaryType;
	}

	public String getProcessedBy() {
		return processedBy;
	}

	public void setProcessedBy(String processedBy) {
		this.processedBy = processedBy;
	}

	public String getBusinessName() {
		return businessName;
	}

	public void setBusinessName(String businessName) {
		this.businessName = businessName;
	}

	public String getInitatedBy() {
		return initatedBy;
	}

	public void setInitatedBy(String initatedBy) {
		this.initatedBy = initatedBy;
	}
}
