package com.pay10.crm.action;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.pay10.commons.api.TransactionControllerServiceProvider;
import com.pay10.commons.dao.BeneficiaryAccountsDao;
import com.pay10.commons.user.BeneficiaryAccounts;
import com.pay10.commons.user.User;
import com.pay10.commons.user.UserDao;
import com.pay10.commons.user.UserType;
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
import com.pay10.commons.util.YesBankFT3ResultType;

public class EditBeneficiaryAction extends AbstractSecureAction {

	private static final long serialVersionUID = -5029111815204022566L;

	@Autowired
	private UserDao userDao;

	@Autowired
	private TransactionControllerServiceProvider transactionControllerServiceProvider;

	@Autowired
	private BeneficiaryAccountsDao beneficiaryAccountsDao;

	@Autowired
	private CrmValidator validator;

	@Autowired
	private Fields fields;

	private static Logger logger = LoggerFactory.getLogger(EditBeneficiaryAction.class.getName());
	private String param;
	private String frmId;

	private String beneficiaryCd;
	private String srcAccountNo;
	private String beneName;
	private String beneAccountNo;
	private String ifscCode;
	private String paymentType;
	private String beneType;
	private String currencyCd;
	private String custId;
	private String bankName;
	private String status;
	private String transactionLimit;
	private String action;
	private String action2;
	private String request;
	private String acquirer;
	private String merchantPayId;
	private String response;
	private String responseCode;
	private String mobileNo;
	private String emailId;
	private String aadharNo;
	private String address1;
	private String address2;
	private String merchantProvidedId;
	private String merchantBusinessName = "";
	private ArrayList<String> paymentTypeArray = new ArrayList<>();
	private ArrayList<String> filterPaymentTypeArray = new ArrayList<>();
	private List<String> successTransaction = new ArrayList<String>();
	private List<String> failTransaction = new ArrayList<String>();

	@Override
	@SuppressWarnings("unchecked")
	public String execute() {

		logger.debug("edit beneficiary");
		 action2 = action;

		User sessionUser = (User) sessionMap.get(Constants.USER.getValue());
		List<BeneficiaryAccounts> respList = new ArrayList<BeneficiaryAccounts>();
		//setPaymentType(NodalPaymentTypes.getInstancefromName(getPaymentType()).getCode());

		if (action.equalsIgnoreCase(SettlementTransactionType.MODIFY_BENEFICIARY.getCode())
				|| action.equalsIgnoreCase(SettlementTransactionType.ENABLE_BENEFICIARY.getCode())) {
//|| action.equalsIgnoreCase(SettlementTransactionType.VERIFY_BENEFICIARY.getCode())
			
			if (action.equalsIgnoreCase(SettlementTransactionType.MODIFY_BENEFICIARY.getCode())) {
				
				BeneficiaryAccounts beneficiary = null;
				try {
					 beneficiary =  beneficiaryAccountsDao.findById_Disable(getMerchantProvidedId(),
								getAcquirer(), getMerchantBusinessName());
				}catch (Exception e) {
					// TODO: handle exception
					setResponse("Beneficiary Not found.");
					return SUCCESS;
				}
				setMerchantPayId(beneficiary.getMerchantPayId());
				setBeneficiaryCd(beneficiary.getBeneficiaryCd());
				//setPaymentType(NodalPaymentTypes.getInstancefromCode(getPaymentType()).getName());
				setBeneType(getBeneType());
				setCurrencyCd(getCurrencyCd());
				setTransactionLimit(beneficiary.getTransactionLimit());
				setAcquirer(getAcquirer());
				setBeneAccountNo(beneficiary.getBeneAccountNo());
				setIfscCode(beneficiary.getIfscCode());

				setBeneName(getBeneName());
				setBankName(getBankName());
				setMobileNo(getMobileNo());
				setEmailId(getEmailId());
				setAadharNo(getAadharNo());
				setAddress1(getAddress1());
				setAddress2(getAddress2());
				setAction(SettlementTransactionType.MODIFY_BENEFICIARY.getName());

				fields.put(FieldType.BENE_MOBILE_NUMBER.getName(), getMobileNo());
				fields.put(FieldType.BENE_EMAIL_ID.getName(), getEmailId());
				fields.put(FieldType.BENE_AADHAR_NO.getName(), getAadharNo());
				fields.put(FieldType.BENE_ADDRESS_1.getName(), getAddress1());
				fields.put(FieldType.BENE_ADDRESS_2.getName(), getAddress2());

			}
//			else if (action.equalsIgnoreCase(SettlementTransactionType.VERIFY_BENEFICIARY.getCode())) {
//
//				BeneficiaryAccounts beneficiary = beneficiaryAccountsDao.findById_Enable(getMerchantProvidedId(),
//						getAcquirer(), getMerchantBusinessName());
//				setMerchantPayId(beneficiary.getMerchantPayId());
//				setBeneficiaryCd(beneficiary.getBeneficiaryCd());
//				// setPaymentType(beneficiary.getPaymentType().getName());
//				setBeneName(beneficiary.getBeneName());
//				setBeneType(beneficiary.getBeneType());
//				setCurrencyCd(beneficiary.getCurrencyCd().getCode());
//				setTransactionLimit(beneficiary.getTransactionLimit());
//				setBankName(beneficiary.getBankName());
//				setIfscCode(beneficiary.getIfscCode());
//				setBeneAccountNo(beneficiary.getBeneAccountNo());
//				setAcquirer(beneficiary.getAcquirer());
//				setAction(SettlementTransactionType.VERIFY_BENEFICIARY.getName());
//
//			}
			else if (action.equalsIgnoreCase(SettlementTransactionType.ENABLE_BENEFICIARY.getCode())) {
				
				BeneficiaryAccounts beneficiary = null;
				try {
					 beneficiary = beneficiaryAccountsDao.findById_Enable(getMerchantProvidedId(),
								getAcquirer(), getMerchantBusinessName());
				}catch (Exception e) {
					// TODO: handle exception
					setResponse("Beneficiary Not found.");
					return SUCCESS;
				}

				setMerchantPayId(beneficiary.getMerchantPayId());
				setBeneficiaryCd(beneficiary.getBeneficiaryCd());
				// setPaymentType(beneficiary.getPaymentType().getName());
				setBeneName(beneficiary.getBeneName());
				setBeneType(beneficiary.getBeneType());
				setCurrencyCd(beneficiary.getCurrencyCd().getCode());
				setTransactionLimit(beneficiary.getTransactionLimit());
				setBankName(beneficiary.getBankName());
				setIfscCode(beneficiary.getIfscCode());
				setBeneAccountNo(beneficiary.getBeneAccountNo());
				setAcquirer(beneficiary.getAcquirer());
				setAction(SettlementTransactionType.MODIFY_BENEFICIARY.getName());

			}

			fields.put(FieldType.BENEFICIARY_CD.getName(), getBeneficiaryCd());
			// fields.put(FieldType.PAYMENT_TYPE.getName(), getPaymentType());
			fields.put(FieldType.BENE_NAME.getName(), getBeneName());
			fields.put(FieldType.BENE_TYPE.getName(), getBeneType());
			fields.put(FieldType.CURRENCY_CD.getName(), getCurrencyCd());
			fields.put(FieldType.BENE_TRANSACTION_LIMIT.getName(), getTransactionLimit());
			fields.put(FieldType.BANK_NAME.getName(), getBankName());
			fields.put(FieldType.IFSC_CODE.getName(), getIfscCode());
			fields.put(FieldType.BENE_ACCOUNT_NO.getName(), getBeneAccountNo());
			fields.put(FieldType.NODAL_ACQUIRER.getName(), getAcquirer());
			fields.put(FieldType.TXNTYPE.getName(), getAction());
			fields.put(FieldType.PAY_ID.getName(), getMerchantPayId());

		}

		else if (action.equals(SettlementTransactionType.DISABLE_BENEFICIARY.getCode())
				|| action.equalsIgnoreCase(SettlementTransactionType.DISABLE_BENEFICIARY.getCode())) {
			BeneficiaryAccounts beneficiary = null;
			try {
				 beneficiary = beneficiaryAccountsDao.findById_Disable(getMerchantProvidedId(), getAcquirer(),
						getMerchantBusinessName());
			}catch (Exception e) {
				// TODO: handle exception
				setResponse("Beneficiary Not found.");
				return SUCCESS;
			}
			
			setMerchantPayId(beneficiary.getMerchantPayId());
			setBeneficiaryCd(beneficiary.getBeneficiaryCd());
			// setPaymentType(beneficiary.getPaymentType().getName());
			setAction(SettlementTransactionType.DISABLE_BENEFICIARY.getName());
			setAcquirer(beneficiary.getAcquirer());

			fields.put(FieldType.BENEFICIARY_CD.getName(), getBeneficiaryCd());
			// fields.put(FieldType.PAYMENT_TYPE.getName(), getPaymentType());
			fields.put(FieldType.NODAL_ACQUIRER.getName(), getAcquirer());
			fields.put(FieldType.TXNTYPE.getName(), getAction());
			fields.put(FieldType.PAY_ID.getName(), getMerchantPayId());
		}

		if (getAcquirer().equals(AcquirerTypeNodal.YESBANKFT3.getCode())) {
			NodalPaymentTypesYesBankFT3 paymentTypes[] = NodalPaymentTypesYesBankFT3.values();

			for (NodalPaymentTypesYesBankFT3 pt : paymentTypes) {
				if (!ifscCode.contains("YESB") && pt.getName().equals("FT")) {
					continue;
				}
				paymentTypeArray.add(NodalPaymentTypesYesBankFT3.getPgNodalInstance(pt.getName()).toString());
			}
		}
		List<BeneficiaryAccounts> beneficiaryFromDb = null;
		if (action2.equals(SettlementTransactionType.DISABLE_BENEFICIARY.getCode())
				|| action2.equals(SettlementTransactionType.VERIFY_BENEFICIARY.getCode())
				|| action2.equals(SettlementTransactionType.MODIFY_BENEFICIARY.getCode())) {
			beneficiaryFromDb = beneficiaryAccountsDao.findByBeneficiaryCdAndStatusActive(getMerchantProvidedId(),
					getMerchantPayId());
		}
		if (action2.equals(SettlementTransactionType.ENABLE_BENEFICIARY.getCode())) {
			beneficiaryFromDb = beneficiaryAccountsDao.findByBeneficiaryCdAndStatusInActive(getMerchantProvidedId(),
					getMerchantPayId());
		}

		for (BeneficiaryAccounts listBeneAcc : beneficiaryFromDb) {
			if (paymentTypeArray.contains(listBeneAcc.getPaymentType().toString())) {
				filterPaymentTypeArray.add(listBeneAcc.getPaymentType().toString());
			}
		}

		try {
			for (String pt : filterPaymentTypeArray) {
				fields.put(FieldType.PAYMENT_TYPE.getName(), pt);
				logger.info("Request for maintainbene = " + fields.toString());
				Map<String, String> responseMap = transactionControllerServiceProvider.nodalSettlementTransact(fields);

				String responseMessage = "";
				String rrn = "";
				String pgRespCode = "";
				String pgTxnMessage = "";
				String beneficiaryCd = "";
				String respMsg = "";
				String date = DateCreater.formatDateForDb(new Date());
				logger.info("Responce for maintainbene = " + responseMap);

				if (responseMap.isEmpty() || responseMap == null) {
					respMsg = "Response not recieved from Bank Server";
					return SUCCESS;
				}

				if (StringUtils.isNotBlank(responseMap.get(FieldType.BENEFICIARY_CD.getName()))) {
					beneficiaryCd = responseMap.get(FieldType.BENEFICIARY_CD.getName());
				}
				if (StringUtils.isNotBlank(responseMap.get(FieldType.PG_TXN_MESSAGE.getName()))) {
					pgTxnMessage = responseMap.get(FieldType.PG_TXN_MESSAGE.getName());
				}

				if (StringUtils.isNotBlank(responseMap.get(FieldType.PG_RESP_CODE.getName()))) {
					pgRespCode = responseMap.get(FieldType.PG_RESP_CODE.getName());
				}
				if (!pgRespCode.isEmpty()) {
					pgRespCode = YesBankFT3ResultType.getInstanceFromCode(pgRespCode).getMessage();
				}

				if (StringUtils.isNotBlank(responseMap.get(FieldType.RESPONSE_MESSAGE.getName()))) {
					responseMessage = responseMap.get(FieldType.RESPONSE_MESSAGE.getName());
				} else {
					responseMessage = AccountStatus.REJECTED.getName();
				}

				logger.info("Inside edit beneficiary , response message = " + responseMessage);
				if (StringUtils.isNotBlank(responseMap.get(FieldType.RRN.getName()))) {
					rrn = responseMap.get(FieldType.RRN.getName());

				} else {
					rrn = "NA";
				}

				logger.info("Inside save beneficiary , rrn  = " + rrn);

				BeneficiaryAccounts beneficiaryAccounts = new BeneficiaryAccounts();
				if (responseMessage.equalsIgnoreCase("SUCCESS") && action2.equalsIgnoreCase(SettlementTransactionType.MODIFY_BENEFICIARY.getCode())) {
					beneficiaryAccounts.setStatus(AccountStatus.ACTIVE);
				} else if (!responseMessage.equalsIgnoreCase("SUCCESS") && action2.equalsIgnoreCase(SettlementTransactionType.MODIFY_BENEFICIARY.getCode())) {
					beneficiaryAccounts.setStatus(AccountStatus.IN_PROCESS);
				}
				
				if (responseMessage.equalsIgnoreCase("SUCCESS") && !action2.equalsIgnoreCase(SettlementTransactionType.MODIFY_BENEFICIARY.getCode())) {
					beneficiaryAccounts.setStatus(AccountStatus.ACTIVE);
				} else if (!responseMessage.equalsIgnoreCase("SUCCESS") && !action2.equalsIgnoreCase(SettlementTransactionType.MODIFY_BENEFICIARY.getCode())) {
					beneficiaryAccounts.setStatus(AccountStatus.INACTIVE);
				}

				if (action2.equalsIgnoreCase(SettlementTransactionType.DISABLE_BENEFICIARY.getCode())) {
					if (responseMessage.equalsIgnoreCase("SUCCESS")) {
						beneficiaryAccounts.setStatus(AccountStatus.INACTIVE);
						respMsg = "Disabled Succesfully";
					} else {
						respMsg = "Disabled Unsuccesfully";
					}
				}
				if (action2.equalsIgnoreCase(SettlementTransactionType.ENABLE_BENEFICIARY.getCode())) {
					if (responseMessage.equalsIgnoreCase("SUCCESS")) {
						beneficiaryAccounts.setStatus(AccountStatus.ACTIVE);
						respMsg = "Enabled Succesfully";
					} else {
						respMsg = "Enabled Unsuccesfully";
					}
				}
				if (action2.equalsIgnoreCase(SettlementTransactionType.MODIFY_BENEFICIARY.getCode())) {
					if (responseMessage.equalsIgnoreCase("SUCCESS")) {
						beneficiaryAccounts.setStatus(AccountStatus.ACTIVE);
						respMsg = "Modified Succesfully";
					} else {
						respMsg = "Modified Unsuccesfully";
					}
				}
//				if (action.equalsIgnoreCase(SettlementTransactionType.VERIFY_BENEFICIARY.getName())) {
//					if (responseMessage.equalsIgnoreCase("SUCCESS")) {
//						beneficiaryAccounts.setStatus(AccountStatus.ACTIVE);
//						respMsg = "Beneficiary Verify Succesfully";
//					} else {
//						respMsg = "Beneficiary Verify Unsuccesfully";
//					}
//				}

			if (action2.equalsIgnoreCase(SettlementTransactionType.MODIFY_BENEFICIARY.getCode())) {
				
					setPaymentType(NodalPaymentTypes.getInstancefromName(pt).getCode());
					BeneficiaryAccounts beneficiary = beneficiaryAccountsDao.findById_EnableAndDisable(getMerchantProvidedId(),getPaymentType(),getAcquirer(), getMerchantBusinessName());

					beneficiaryAccounts.setId(beneficiary.getId());
					beneficiaryAccounts.setCustId(beneficiary.getCustId());
					beneficiaryAccounts.setBeneName(beneficiary.getBeneficiaryCd());
					beneficiaryAccounts.setPaymentType(NodalPaymentTypes.getInstancefromName(pt));
					beneficiaryAccounts.setBeneType(beneficiary.getBeneType());
							//BeneficiaryTypes.getInstancefromCode(fields.get(FieldType.BENE_TYPE.getName())).getCode());
					beneficiaryAccounts.setCurrencyCd(beneficiary.getCurrencyCd());
							//CurrencyTypes.getInstancefromCode(getCurrencyCd()));
					beneficiaryAccounts.setAcquirer(beneficiary.getAcquirer());
					beneficiaryAccounts.setBeneficiaryCd(beneficiary.getBeneficiaryCd());
					beneficiaryAccounts.setRrn(rrn);
					beneficiaryAccounts.setResponseMessage(responseMessage);
					beneficiaryAccounts.setCreatedDate(date);
					beneficiaryAccounts.setUpdatedDate(date);
					beneficiaryAccounts.setRequestedBy(sessionUser.getEmailId());
					beneficiaryAccounts.setProcessedBy(sessionUser.getEmailId());
					beneficiaryAccounts.setSrcAccountNo(beneficiary.getSrcAccountNo());
					beneficiaryAccounts.setMerchantPayId(beneficiary.getMerchantPayId());
					beneficiaryAccounts.setMerchantProvidedId(beneficiary.getMerchantProvidedId());
					beneficiaryAccounts.setTransactionLimit(beneficiary.getTransactionLimit());
					beneficiaryAccounts.setMerchantBusinessName(beneficiary.getMerchantBusinessName());
					beneficiaryAccounts.setIfscCode(beneficiary.getIfscCode());
					beneficiaryAccounts.setBeneAccountNo(beneficiary.getBeneAccountNo());
					beneficiaryAccounts.setBeneExpiryDate(beneficiary.getBeneExpiryDate());
					if(responseMessage.equalsIgnoreCase("SUCCESS")) {
						beneficiaryAccounts.setBankName(fields.get(FieldType.BANK_NAME.getName()));
						beneficiaryAccounts.setMerchantProvidedName(fields.get(FieldType.BENE_NAME.getName()));
						beneficiaryAccounts.setMobileNo(fields.get(FieldType.BENE_MOBILE_NUMBER.getName()));
						beneficiaryAccounts.setEmailId(fields.get(FieldType.BENE_EMAIL_ID.getName()));
						beneficiaryAccounts.setAddress1(fields.get(FieldType.BENE_ADDRESS_1.getName()));
						beneficiaryAccounts.setAddress2(fields.get(FieldType.BENE_ADDRESS_2.getName()));
						beneficiaryAccounts.setAadharNo(fields.get(FieldType.BENE_AADHAR_NO.getName()));
					}else {
						beneficiaryAccounts.setBankName(beneficiary.getBankName());
						beneficiaryAccounts.setMerchantProvidedName(beneficiary.getMerchantProvidedId());
						beneficiaryAccounts.setMobileNo(beneficiary.getMobileNo());
						beneficiaryAccounts.setEmailId(beneficiary.getEmailId());
						beneficiaryAccounts.setAddress1(beneficiary.getAddress1());
						beneficiaryAccounts.setAddress2(beneficiary.getAddress2());
						beneficiaryAccounts.setAadharNo(beneficiary.getAadharNo());
					}
					

					beneficiaryAccounts = beneficiaryAccountsDao.updateAfterEditBeneficiary(beneficiaryAccounts);
				
			}

				if (responseMessage.equalsIgnoreCase("SUCCESS")) {
					// setResponse(respMsg);
					successTransaction.add("Beneficiary: "+action2 + " for PaymentType: " + pt);
				} else {
					// setResponse(respMsg + " Due to: " + pgRespCode);
					failTransaction.add("Beneficiary: "+action2 + " for PaymentType: " + pt + "," + respMsg + " Due to: " + pgRespCode);
				}
			}

			setResponse(successTransaction.toString() + failTransaction.toString());
			setContentToNull();
		} catch (Exception e) {
			logger.info(e + "exception in edit beneficiary");
			return SUCCESS;
		}

		return SUCCESS;
	}

	@Override
	public void validate() {
		// Bene name : Alpha numeric
		if ((validator.validateBlankField(getBeneName()))) {
			addFieldError(CrmFieldType.BENE_NAME.getName(), validator.getResonseObject().getResponseMessage());
		} else if (!(validator.validateField(CrmFieldType.BENE_NAME, getBeneName()))) {
			addFieldError(CrmFieldType.BENE_NAME.getName(), validator.getResonseObject().getResponseMessage());
		}

		// Bene email id:
		if (!getEmailId().equals("NA")) {
			if ((validator.validateBlankField(getEmailId()))) {
			} else if (!(validator.validateField(CrmFieldType.BENE_EMAIL_ID, getEmailId()))) {
				addFieldError(CrmFieldType.BENE_EMAIL_ID.getName(), validator.getResonseObject().getResponseMessage());
			}
		} else {
			setEmailId("");
		}

		// Bene Mobile Number:
		if (!getMobileNo().equals("NA")) {
			if ((validator.validateBlankField(getMobileNo()))) {
			} else if (!(validator.validateField(CrmFieldType.BENE_MOBILE_NUMBER, getMobileNo()))) {
				addFieldError(CrmFieldType.BENE_MOBILE_NUMBER.getName(),
						validator.getResonseObject().getResponseMessage());
			}
		} else {
			setMobileNo("");
		}

		// Bene address 1:
		if (!getAddress1().equals("NA")) {
			if ((validator.validateBlankField(getAddress1()))) {
			} else if (!(validator.validateField(CrmFieldType.BENE_ADDRESS_1, getAddress1()))) {
				addFieldError(CrmFieldType.BENE_ADDRESS_1.getName(), validator.getResonseObject().getResponseMessage());
			}
		} else {
			setAddress1("");
		}

		// Bene address 2:
		if (!getAddress2().equals("NA")) {
			if ((validator.validateBlankField(getAddress2()))) {
			} else if (!(validator.validateField(CrmFieldType.BENE_ADDRESS_2, getAddress2()))) {
				addFieldError(CrmFieldType.BENE_ADDRESS_2.getName(), validator.getResonseObject().getResponseMessage());
			}
		} else {
			setAddress2("");
		}

		// Aadhar:
		if (!getAadharNo().equals("NA")) {
			if ((validator.validateBlankField(getAadharNo()))) {
			} else if (!(validator.validateField(CrmFieldType.BENE_AADHAR_NO, getAadharNo()))) {
				addFieldError(CrmFieldType.BENE_AADHAR_NO.getName(), validator.getResonseObject().getResponseMessage());
			}
		} else {
			setAadharNo("");
		}

	}

	public String getParam() {
		return param;
	}

	public void setParam(String param) {
		this.param = param;
	}

	public String getFrmId() {
		return frmId;
	}

	public void setFrmId(String frmId) {
		this.frmId = frmId;
	}

	public String getBeneficiaryCd() {
		return beneficiaryCd;
	}

	public void setBeneficiaryCd(String beneficiaryCd) {
		this.beneficiaryCd = beneficiaryCd;
	}

	public String getSrcAccountNo() {
		return srcAccountNo;
	}

	public void setSrcAccountNo(String srcAccountNo) {
		this.srcAccountNo = srcAccountNo;
	}

	public String getBeneName() {
		return beneName;
	}

	public void setBeneName(String beneName) {
		this.beneName = beneName;
	}

	public String getBeneAccountNo() {
		return beneAccountNo;
	}

	public void setBeneAccountNo(String beneAccountNo) {
		this.beneAccountNo = beneAccountNo;
	}

	public String getIfscCode() {
		return ifscCode;
	}

	public void setIfscCode(String ifscCode) {
		this.ifscCode = ifscCode;
	}

	public String getPaymentType() {
		return paymentType;
	}

	public void setPaymentType(String paymentType) {
		this.paymentType = paymentType;
	}

	public String getBeneType() {
		return beneType;
	}

	public void setBeneType(String beneType) {
		this.beneType = beneType;
	}

	public String getCustId() {
		return custId;
	}

	public void setCustId(String custId) {
		this.custId = custId;
	}

	public String getBankName() {
		return bankName;
	}

	public void setBankName(String bankName) {
		this.bankName = bankName;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getTransactionLimit() {
		return transactionLimit;
	}

	public void setTransactionLimit(String transactionLimit) {
		this.transactionLimit = transactionLimit;
	}

	public String getRequest() {
		return request;
	}

	public void setRequest(String request) {
		this.request = request;
	}

	public String getAcquirer() {
		return acquirer;
	}

	public void setAcquirer(String acquirer) {
		this.acquirer = acquirer;
	}

	public String getCurrencyCd() {
		return currencyCd;
	}

	public void setCurrencyCd(String currencyCd) {
		this.currencyCd = currencyCd;
	}

	public String getMerchantPayId() {
		return merchantPayId;
	}

	public void setMerchantPayId(String merchantPayId) {
		this.merchantPayId = merchantPayId;
	}

	public String getResponse() {
		return response;
	}

	public void setResponse(String response) {
		this.response = response;
	}

	public String getResponseCode() {
		return responseCode;
	}

	public void setResponseCode(String responseCode) {
		this.responseCode = responseCode;
	}

	private void setContentToNull() {
		setBeneficiaryCd(null);
		setBeneAccountNo(null);
		setBeneType(null);
		setIfscCode(null);
		setBankName(null);
		setAcquirer(null);
		setCustId(null);
		setTransactionLimit(null);
	}

	public String getMobileNo() {
		return mobileNo;
	}

	public void setMobileNo(String mobileNo) {
		this.mobileNo = mobileNo;
	}

	public String getAadharNo() {
		return aadharNo;
	}

	public void setAadharNo(String aadharNo) {
		this.aadharNo = aadharNo;
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

	public String getEmailId() {
		return emailId;
	}

	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}

	public String getMerchantProvidedId() {
		return merchantProvidedId;
	}

	public void setMerchantProvidedId(String merchantProvidedId) {
		this.merchantProvidedId = merchantProvidedId;
	}

	public String getMerchantBusinessName() {
		return merchantBusinessName;
	}

	public void setMerchantBusinessName(String merchantBusinessName) {
		this.merchantBusinessName = merchantBusinessName;
	}

}
