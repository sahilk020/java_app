package com.pay10.crm.action;

import java.util.Date;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.gson.Gson;
import com.pay10.commons.dao.HibernateSessionProvider;
import com.pay10.commons.dao.PendingMappingRequestDao;
import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.user.AccountCurrency;
import com.pay10.commons.user.PendingMappingRequest;
import com.pay10.commons.user.UserDao;
import com.pay10.commons.util.AcquirerType;
import com.pay10.commons.util.CrmFieldType;
import com.pay10.commons.util.CrmValidator;
import com.pay10.commons.util.PendingRequestEmailProcessor;
import com.pay10.commons.util.TDRStatus;
import com.pay10.crm.actionBeans.UserMappingEditor;

/**
 * @author Shaiwal
 *
 */
public class MappingApproveRejectAction extends AbstractSecureAction {

	@Autowired
	private UserMappingEditor userMappingEditor;

	@Autowired
	private PendingMappingRequestDao pendingMappingRequestDao;

	@Autowired
	private PendingRequestEmailProcessor pendingRequestEmailProcessor;

	@Autowired
	private UserDao userDao;
	@Autowired
	private CrmValidator validator;

	private static Logger logger = LoggerFactory.getLogger(MappingApproveRejectAction.class.getName());
	private static final long serialVersionUID = -6517340843571949786L;

	private String emailId;
	private String userType;
	private String operation;
	private String merchant;

	private String merchantEmailId;
	private String merchantPayId;
	private String mapString;
	private String acquirer;
	private String response;
	private String accountCurrencySet;

	public Date currentDate = new Date();

	@Override
	public String execute() {

		try {
			merchantEmailId = userDao.getEmailIdByBusinessName(merchant);
			merchantPayId = userDao.getPayIdByEmailId(merchantEmailId);
			AcquirerType.getInstancefromName(acquirer).getName();
			PendingMappingRequest existingPMR = pendingMappingRequestDao.findPendingMappingRequest(merchantEmailId,
					AcquirerType.getInstancefromName(acquirer).getCode());
			PendingMappingRequest existingActivePMR = pendingMappingRequestDao.findActiveMappingRequest(merchantEmailId,
					AcquirerType.getInstancefromName(acquirer).getCode());
			if (existingPMR != null) {

				if (operation.equalsIgnoreCase("reject")) {
					updateMapping(existingPMR, TDRStatus.REJECTED, "", emailId);
					// pendingRequestEmailProcessor.processMappingApproveRejectEmail("Rejected",
					// emailId, userType, merchant, merchantPayId, existingPMR.getRequestedBy());
					setResponse(ErrorType.MAPPING_REQUEST_REJECTED.getResponseMessage());
					return SUCCESS;
				}
				updateMapping(existingPMR, TDRStatus.ACTIVE, "", emailId);
				updateMapping(existingActivePMR, TDRStatus.INACTIVE, "", emailId);
				setMapString(existingPMR.getMapString());
				setMerchantEmailId(existingPMR.getMerchantEmailId());
				setAcquirer(existingPMR.getAcquirer());
				setAccountCurrencySet(existingPMR.getAccountCurrencySet());

				if (mapString != null && merchantEmailId != null && acquirer != null) {
					processMapString();
					// pendingRequestEmailProcessor.processMappingApproveRejectEmail("Approved",
					// emailId, userType, merchant, merchantPayId, existingPMR.getRequestedBy());
					setResponse(ErrorType.MAPPING_SAVED.getResponseMessage());
					return SUCCESS;

				}
			} else {

				setResponse(ErrorType.MAPPING_REQUEST_NOT_FOUND.getResponseMessage());
				return SUCCESS;
			}

		} catch (Exception exception) {
			logger.error("Exception", exception);
			setResponse(ErrorType.MAPPING_NOT_SAVED.getResponseMessage());
			return SUCCESS;
		}
		setResponse(ErrorType.MAPPING_NOT_SAVED.getResponseMessage());
		return SUCCESS;

	}

	@Override
	public void validate() {
		if (validator.validateBlankField(getEmailId())) {
			/*
			 * addFieldError(CrmFieldType.EMAILID.getName(), validator
			 * .getResonseObject().getResponseMessage());
			 */
		} else if (!(validator.isValidEmailId(getEmailId()))) {
			addFieldError(CrmFieldType.EMAILID.getName(), ErrorType.INVALID_FIELD_VALUE.getResponseMessage());
		}
		if ((validator.validateBlankField(getUserType()))) {
			/*
			 * addFieldError(CrmFieldType.USER_TYPE.getName(), validator
			 * .getResonseObject().getResponseMessage());
			 */
		} else if (!(validator.validateField(CrmFieldType.USER_TYPE, getUserType()))) {
			addFieldError(CrmFieldType.USER_TYPE.getName(), validator.getResonseObject().getResponseMessage());
		}
		if ((validator.validateBlankField(getOperation()))) {
			/*
			 * addFieldError(CrmFieldType.OPERATION.getName(), validator
			 * .getResonseObject().getResponseMessage());
			 */
		} else if (!(validator.validateField(CrmFieldType.OPERATION, getOperation()))) {
			addFieldError(CrmFieldType.OPERATION.getName(), validator.getResonseObject().getResponseMessage());
		}
		if ((validator.validateBlankField(getMerchant()))) {
			/*
			 * addFieldError(CrmFieldType.MERCHANT.getName(), validator
			 * .getResonseObject().getResponseMessage());
			 */
		} else if (!(validator.validateField(CrmFieldType.MERCHANT, getMerchant()))) {
			addFieldError(CrmFieldType.MERCHANT.getName(), validator.getResonseObject().getResponseMessage());
		}
		if ((validator.validateBlankField(getMerchantEmailId()))) {
			/*
			 * addFieldError(CrmFieldType.MERCHANT_EMAIL_ID.getName(), validator
			 * .getResonseObject().getResponseMessage());
			 */
		} else if (!(validator.validateField(CrmFieldType.MERCHANT_EMAIL_ID, getMerchantEmailId()))) {
			addFieldError(CrmFieldType.MERCHANT_EMAIL_ID.getName(), validator.getResonseObject().getResponseMessage());
		}
		if ((validator.validateBlankField(getMerchantPayId()))) {
			/*
			 * addFieldError(CrmFieldType.MERCHANT_ID.getName(), validator
			 * .getResonseObject().getResponseMessage());
			 */
		} else if (!(validator.validateField(CrmFieldType.MERCHANT_ID, getMerchantPayId()))) {
			addFieldError(CrmFieldType.MERCHANT_ID.getName(), validator.getResonseObject().getResponseMessage());
		}
		if ((validator.validateBlankField(getMapString()))) {
			/*
			 * addFieldError(CrmFieldType.MAP_STRING.getName(), validator
			 * .getResonseObject().getResponseMessage());
			 */
		} else if (!(validator.validateField(CrmFieldType.MAP_STRING, getMapString()))) {
			addFieldError(CrmFieldType.MAP_STRING.getName(), validator.getResonseObject().getResponseMessage());
		}
		if ((validator.validateBlankField(getAcquirer()))) {
			/*
			 * addFieldError(CrmFieldType.ACQUIRER.getName(), validator
			 * .getResonseObject().getResponseMessage());
			 */
		} else if (!(validator.validateField(CrmFieldType.ACQUIRER, getAcquirer()))) {
			addFieldError(CrmFieldType.ACQUIRER.getName(), validator.getResonseObject().getResponseMessage());
		}
		if ((validator.validateBlankField(getResponse()))) {
			/*
			 * addFieldError(CrmFieldType.RESPONSE.getName(), validator
			 * .getResonseObject().getResponseMessage());
			 */
		} else if (!(validator.validateField(CrmFieldType.RESPONSE, getResponse()))) {
			addFieldError(CrmFieldType.RESPONSE.getName(), validator.getResonseObject().getResponseMessage());
		}
		if ((validator.validateBlankField(getAccountCurrencySet()))) {
			/*
			 * addFieldError(CrmFieldType.ACCOUNT_CURRENCY_SET.getName(), validator
			 * .getResonseObject().getResponseMessage());
			 */
		} else if (!(validator.validateField(CrmFieldType.ACCOUNT_CURRENCY_SET, getAccountCurrencySet()))) {
			addFieldError(CrmFieldType.ACCOUNT_CURRENCY_SET.getName(),
					validator.getResonseObject().getResponseMessage());
		}

	}

	private void processMapString() {
		Gson gson = new Gson();
		AccountCurrency[] accountCurrencies = gson.fromJson(accountCurrencySet, AccountCurrency[].class);
		userMappingEditor.decideAccountChange(getMerchantEmailId(), getMapString(), getAcquirer(), accountCurrencies, false, true);
	}

	public void updateMapping(PendingMappingRequest pmr, TDRStatus status, String requestedBy, String processedBy) {

		Date currentDate = new Date();
		try {

			Session session = null;
			session = HibernateSessionProvider.getSession();
			Transaction tx = session.beginTransaction();
			Long id = pmr.getId();
			session.load(pmr, pmr.getId());
			PendingMappingRequest pendingRequest = session.get(PendingMappingRequest.class, id);
			pendingRequest.setStatus(status);
			pendingRequest.setUpdatedDate(currentDate);
			if (!requestedBy.equalsIgnoreCase("")) {
				pendingRequest.setRequestedBy(requestedBy);
			}
			if (!processedBy.equalsIgnoreCase("")) {
				pendingRequest.setProcessedBy(processedBy);
			}

			session.update(pendingRequest);
			tx.commit();
			session.close();

		} catch (HibernateException e) {
			e.printStackTrace();
		} finally {

		}

	}

	public String getMerchantEmailId() {
		return merchantEmailId;
	}

	public void setMerchantEmailId(String merchantEmailId) {
		this.merchantEmailId = merchantEmailId;
	}

	public String getMapString() {
		return mapString;
	}

	public void setMapString(String mapString) {
		this.mapString = mapString;
	}

	public String getResponse() {
		return response;
	}

	public void setResponse(String response) {
		this.response = response;
	}

	public String getAccountCurrencySet() {
		return accountCurrencySet;
	}

	public void setAccountCurrencySet(String accountCurrencySet) {
		this.accountCurrencySet = accountCurrencySet;
	}

	public String getUserType() {
		return userType;
	}

	public void setUserType(String userType) {
		this.userType = userType;
	}

	public String getEmailId() {
		return emailId;
	}

	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}

	public String getOperation() {
		return operation;
	}

	public void setOperation(String operation) {
		this.operation = operation;
	}

	public String getMerchant() {
		return merchant;
	}

	public void setMerchant(String merchant) {
		this.merchant = merchant;
	}

	public String getAcquirer() {
		return acquirer;
	}

	public void setAcquirer(String acquirer) {
		this.acquirer = acquirer;
	}

	public String getMerchantPayId() {
		return merchantPayId;
	}

	public void setMerchantPayId(String merchantPayId) {
		this.merchantPayId = merchantPayId;
	}

}
