package com.pay10.crm.action;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

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
import com.pay10.commons.util.CrmFieldType;
import com.pay10.commons.util.CrmValidator;
import com.pay10.commons.util.NodalPaymentTypes;
import com.pay10.commons.util.NodalPaymentTypesYesBankFT3;
import com.pay10.commons.util.UserStatusType;

/**
 * 
 * @author shubhamchauhan
 *
 */
public class GetBeneficiaryDetailsAction extends AbstractSecureAction{

	private static final long serialVersionUID = -7012214501958894250L;

	private static Logger logger = LoggerFactory.getLogger(GetBeneficiaryDetailsAction.class.getName());
	
	@Autowired
	BeneficiaryAccountsDao benficiaryAccountsDao;
	
	@Autowired
	NodalAccountDetailsDao nodalAccountDetailsDao;
	
	@Autowired
	UserDao userDao;
	
	@Autowired
	private CrmValidator validator;
	
	private String merchantProvidedCode;
	private String paymentType;
	private String payId;
	private String response;
	private String acquirer;
	private String amount;
	private String nodalAccountNumber;
	private String nodalAccountHolderName;
	private String merchantName;
	private String beneficiaryAccountNumber;
	private String beneficiaryName;
	private String beneficiaryIfscCode;
	private String comments;

	@Override
	public String execute() {
		try {
			logger.info("Getting beneficiary Account detials");
			List<BeneficiaryAccounts> beneficiaryAccountsList = benficiaryAccountsDao.findByMerchantPvdCdAndStatusAndPaymentTypeAndPayId(getMerchantProvidedCode(), getPayId(), getPaymentType(), AccountStatus.ACTIVE.getName());
			
			if(beneficiaryAccountsList.isEmpty()) {
				logger.info("Beneficiary not found for Beneficiary Code : " + getMerchantProvidedCode());
				setResponse("Beneficiary not found for Beneficiary Code : " + getMerchantProvidedCode());
				return SUCCESS;
			}
			
			if(beneficiaryAccountsList.size() > 1) {
				logger.info("More than 1 entries found for Beneficiary Code : " + getMerchantProvidedCode());
				setResponse("None or more than 1 entries found for Beneficiary Code : " + getMerchantProvidedCode());
				return SUCCESS;
			}
			BeneficiaryAccounts beneficiaryAccounts = beneficiaryAccountsList.get(0);
			NodalAccountDetails nodalAccountDetails = nodalAccountDetailsDao.find(getAcquirer());
			if(nodalAccountDetails == null) {
				logger.info("Invalid Acquirer : " + getAcquirer());
				setResponse("Invalid Acquirer : " + getAcquirer());
				return SUCCESS;
			}
			User user = null;
			if(!beneficiaryAccounts.getBeneType().equals(BeneficiaryTypes.PG.getCode())) {
				user = userDao.findPayId(getPayId());
				if(user == null) {
					logger.info("No user found for Pay ID : " + getPayId());
					setResponse("No user found for Pay ID : " + getPayId());
					return SUCCESS;
				}
				
				if(!(user.getUserStatus().equals(UserStatusType.ACTIVE))) {
					logger.info("User is not active for Pay ID : " + getPayId());
					setResponse("User is not active for Pay ID : " + getPayId());
					return SUCCESS;
				}
			}
			
			setNodalAccountNumber(nodalAccountDetails.getAccountNumber());
			setNodalAccountHolderName(nodalAccountDetails.getAccountHolderName());
			if(user == null) {
				setMerchantName("");
			}else {
				setMerchantName(user.getBusinessName());
			}
			
			setPaymentType(NodalPaymentTypesYesBankFT3.getInstancefromCode(getPaymentType()).getName());
			setBeneficiaryAccountNumber(beneficiaryAccounts.getBeneAccountNo());
			setBeneficiaryName(beneficiaryAccounts.getMerchantProvidedName());
			setBeneficiaryIfscCode(beneficiaryAccounts.getIfscCode());
			setResponse("");
		}catch(Exception e) {
			logger.error("Exception while getting beneficiary account data.");
			setResponse("Failed to get account details");
		}
		return SUCCESS;
	}
	
	public void validate() {

	if (validator.validateBlankField(getMerchantProvidedCode())) {
		addFieldError(CrmFieldType.BENE_MERCHANT_PROVIDED_ID.getName(), validator.getResonseObject().getResponseMessage());
		setResponse("Invalid Merchant Provided Code");
		return;
	} else if (!(validator.validateField(CrmFieldType.BENE_MERCHANT_PROVIDED_ID, getMerchantProvidedCode()))) {
		addFieldError(CrmFieldType.BENE_MERCHANT_PROVIDED_ID.getName(), validator.getResonseObject().getResponseMessage());
		setResponse("Invalid Merchant Provided Code");
		return;
	}

	if (validator.validateBlankField(getAcquirer())) {
		addFieldError(CrmFieldType.BENE_NODAL_ACQUIRER.getName(), validator.getResonseObject().getResponseMessage());
		setResponse("Invalid Beneficiary Nodal Acquirer");
		return;
	} else if ((AcquirerTypeNodal.getAcquirerName(getAcquirer()) == null)) {
		addFieldError(CrmFieldType.BENE_NODAL_ACQUIRER.getName(), validator.getResonseObject().getResponseMessage());
		setResponse("Invalid Beneficiary Nodal Acquirer");
		return;
	}
	if (validator.validateBlankFields(getPaymentType())) {
		addFieldError(CrmFieldType.PAYMENT_TYPE.getName(), validator.getResonseObject().getResponseMessage());
		setResponse("Invalid Payment Type");
		return;
	} else if ((NodalPaymentTypes.getInstancefromCode(getPaymentType()) == null)) {
		addFieldError(CrmFieldType.PAYMENT_TYPE.getName(), validator.getResonseObject().getResponseMessage());
		setResponse("Invalid Payment Type");
		return;
	}

	if(validator.validateBlankFields(getAmount())) {
		addFieldError(CrmFieldType.BENE_AMOUNT.getName(), validator.getResonseObject().getResponseMessage());
		setResponse("Amount Field is mandatory");
		return;
	} else if(!validator.validateField(CrmFieldType.BENE_AMOUNT, getAmount())) {
		setResponse("Invalid Amount");
		addFieldError(CrmFieldType.BENE_AMOUNT.getName(), validator.getResonseObject().getResponseMessage());

		return;
	} else if(!(validator.validateAmountByYesBankNodalPaymentType(getPaymentType(), getAmount()))) {
		addFieldError(CrmFieldType.BENE_AMOUNT.getName(), validator.getResonseObject().getResponseMessage());
		setResponse("Invalid amount limit or payment type");
		return;
	}
	
	if (validator.validateBlankFields(getComments())) {
		addFieldError(CrmFieldType.BENE_COMMENT.getName(), validator.getResonseObject().getResponseMessage());
		setResponse("Comments field is mandatory.");
	} else if (!(validator.validateField(CrmFieldType.BENE_COMMENT, getComments())) && !comments.equalsIgnoreCase("ALL")) {
		addFieldError(CrmFieldType.BENE_COMMENT.getName(), validator.getResonseObject().getResponseMessage());
		setResponse("Invalid Comments");
	} else if(!getComments().matches(".*[a-zA-Z0-9]+.*")) {
		addFieldError(CrmFieldType.BENE_COMMENT.getName(), ErrorType.INVALID_FIELD_VALUE.getResponseMessage());
		setResponse("Invalid Comments");
		return;
	}
}

	public String getMerchantProvidedCode() {
		return merchantProvidedCode;
	}

	public void setMerchantProvidedCode(String merchantProvidedCode) {
		this.merchantProvidedCode = merchantProvidedCode;
	}

	public String getPaymentType() {
		return paymentType;
	}

	public void setPaymentType(String paymentType) {
		this.paymentType = paymentType;
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


	public String getAcquirer() {
		return acquirer;
	}

	public void setAcquirer(String acquirer) {
		this.acquirer = acquirer;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public String getNodalAccountNumber() {
		return nodalAccountNumber;
	}

	public void setNodalAccountNumber(String nodalAccountNumber) {
		this.nodalAccountNumber = nodalAccountNumber;
	}

	public String getNodalAccountHolderName() {
		return nodalAccountHolderName;
	}

	public void setNodalAccountHolderName(String nodalAccountHolderName) {
		this.nodalAccountHolderName = nodalAccountHolderName;
	}

	public String getMerchantName() {
		return merchantName;
	}

	public void setMerchantName(String merchantName) {
		this.merchantName = merchantName;
	}

	public String getBeneficiaryAccountNumber() {
		return beneficiaryAccountNumber;
	}

	public void setBeneficiaryAccountNumber(String beneficiaryAccountNumber) {
		this.beneficiaryAccountNumber = beneficiaryAccountNumber;
	}

	public String getBeneficiaryName() {
		return beneficiaryName;
	}

	public void setBeneficiaryName(String beneficiaryName) {
		this.beneficiaryName = beneficiaryName;
	}

	public String getBeneficiaryIfscCode() {
		return beneficiaryIfscCode;
	}

	public void setBeneficiaryIfscCode(String beneficiaryIfscCode) {
		this.beneficiaryIfscCode = beneficiaryIfscCode;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

}
