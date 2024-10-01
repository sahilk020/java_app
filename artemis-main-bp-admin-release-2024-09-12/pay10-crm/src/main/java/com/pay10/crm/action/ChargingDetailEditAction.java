package com.pay10.crm.action;


import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.opensymphony.xwork2.ModelDriven;
import com.pay10.commons.audittrail.ActionMessageByAction;
import com.pay10.commons.audittrail.AuditTrail;
import com.pay10.commons.audittrail.AuditTrailService;
import com.pay10.commons.dao.ChargingDetailsFactory;
import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.user.ChargingDetails;
import com.pay10.commons.user.ChargingDetailsDao;
import com.pay10.commons.user.User;
import com.pay10.commons.user.UserDao;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.CrmFieldType;
import com.pay10.commons.util.CrmValidator;
import com.pay10.crm.actionBeans.ChargingDetailsMaintainer;

/**
 * @author Puneet
 *
 */
public class ChargingDetailEditAction extends AbstractSecureAction implements ModelDriven<ChargingDetails> {

	@Autowired
	private CrmValidator validator;

	@Autowired
	private ChargingDetailsMaintainer editChargingDetails;

	@Autowired
	private ChargingDetailsDao chargingDetailsDao;

	@Autowired
	private UserDao userDao;

	@Autowired
	private ChargingDetailsFactory chargingDetailProvider;

	@Autowired
	private AuditTrailService auditTrailService;

	private static Logger logger = LoggerFactory.getLogger(ChargingDetailEditAction.class.getName());
	private static final long serialVersionUID = -6517340843571949786L;

	private ChargingDetails chargingDetails = new ChargingDetails();
	private String emailId;
	private String acquirer;
	private String response;
	private String userType;
	private String loginUserEmailId;
	private String paymentRegion;
	private String cardHolderType;
	private User sessionUser = null;

	@SuppressWarnings("unchecked")
	@Override
	public String execute() {
		try {

			/*
			 * if(chargingDetails.getMerchantTDR() == 0.0) {
			 * chargingDetails.setResponse("proper values not entered"); return SUCCESS; }
			 */
			boolean isDomestic = StringUtils.equals(getPaymentRegion(), "DOMESTIC");
			boolean isConsumer = StringUtils.equals(getCardHolderType(), "CONSUMER");
			ChargingDetails prevChargingDetailsFromDb = chargingDetailsDao.find(chargingDetails.getId());
			String prevValue = prevChargingDetailsFromDb != null ? mapper.writeValueAsString(prevChargingDetailsFromDb)
					: null;
			editChargingDetails.editChargingDetail(emailId, acquirer, chargingDetails, userType, loginUserEmailId,
					isDomestic, isConsumer);
			
			ChargingDetails latestActivechargingDetailFromDb = chargingDetailsDao.findActiveChargingDetail(
					prevChargingDetailsFromDb.getMopType(), prevChargingDetailsFromDb.getPaymentType(),
					prevChargingDetailsFromDb.getTransactionType(), prevChargingDetailsFromDb.getAcquirerName(),
					prevChargingDetailsFromDb.getCurrency(), prevChargingDetailsFromDb.getPayId());
			if (null != latestActivechargingDetailFromDb) {
				Long id = latestActivechargingDetailFromDb.getId();
				chargingDetails.setId(id);
			}
			Map<String, ActionMessageByAction> actionMessagesByAction = (Map<String, ActionMessageByAction>) sessionMap
					.get(Constants.ACTION_MESSAGE_BY_ACTION.getValue());
			AuditTrail auditTrail = new AuditTrail(mapper.writeValueAsString(latestActivechargingDetailFromDb),
					prevValue, actionMessagesByAction.get("editChargingDetail"));
			auditTrailService.saveAudit(request, auditTrail);

		} catch (Exception exception) {
			setResponse(ErrorType.CHARGINGDETAIL_NOT_SAVED.getResponseMessage());
			logger.error("Exception", exception);
			return ERROR;
		}
		return SUCCESS;
	}

	public String updateAll() {
		logger.info("Request Received For TDR Update ALL");
		sessionUser = (User) sessionMap.get(Constants.USER.getValue());
		logger.info("Update ALL TDR Setting By User : "+sessionUser.getEmailId());
		try {
			editChargingDetails.editAllChargingDetails(emailId, acquirer, chargingDetails, userType, sessionUser.getEmailId());
			setResponse("All details saved successfully ");
		} catch (Exception exception) {
			setResponse(ErrorType.CHARGINGDETAIL_NOT_SAVED.getResponseMessage());
			logger.error("Exception", exception);
			return ERROR;
		}
		return SUCCESS;
	}

	@Override
	public void validate() {
		if ((validator.validateBlankField(getAcquirer()))) {
		} else if (!validator.validateField(CrmFieldType.ACQUIRER, getAcquirer())) {
			addFieldError(CrmFieldType.ACQUIRER.getName(), ErrorType.INVALID_FIELD.getResponseMessage());
		}

		if ((validator.validateBlankField(getEmailId()))) {
		} else if (!validator.validateField(CrmFieldType.EMAILID, getEmailId())) {
			addFieldError(CrmFieldType.ACQUIRER.getName(), ErrorType.INVALID_FIELD.getResponseMessage());
		}

		if ((validator.validateBlankField(getEmailId()))) {
		} else if (!validator.validateField(CrmFieldType.MERCHANT_EMAIL_ID, getEmailId())) {
			addFieldError(CrmFieldType.ACQUIRER.getName(), ErrorType.INVALID_FIELD.getResponseMessage());
		}

		if (validator.validateBlankField(getEmailId())) {
		} else if (!validator.validateField(CrmFieldType.EMAILID, getEmailId())) {
			addFieldError(CrmFieldType.EMAILID.getName(), ErrorType.INVALID_FIELD.getResponseMessage());
		}
		if ((validator.validateBlankField(chargingDetails.getAcquirerName()))) {
		} else if (!validator.validateField(CrmFieldType.ACQUIRER, chargingDetails.getAcquirerName())) {
			addFieldError(CrmFieldType.ACQUIRER.getName(), ErrorType.INVALID_FIELD.getResponseMessage());
		}
		if ((validator.validateBlankField(chargingDetails.getCurrency()))) {
		} else if (!validator.validateField(CrmFieldType.CURRENCY, chargingDetails.getCurrency())) {
			addFieldError(CrmFieldType.CURRENCY.getName(), ErrorType.INVALID_FIELD.getResponseMessage());
		}
		if ((validator.validateBlankField(chargingDetails.getPayId()))) {
		} else if (!validator.validateField(CrmFieldType.PAY_ID, chargingDetails.getCurrency())) {
			addFieldError(CrmFieldType.PAY_ID.getName(), ErrorType.INVALID_FIELD.getResponseMessage());
		}
		if ((validator.validateBlankField(getResponse()))) {
		} else if (!validator.validateField(CrmFieldType.RESPONSE, getResponse())) {
			addFieldError(CrmFieldType.RESPONSE.getName(), ErrorType.INVALID_FIELD.getResponseMessage());
		}
		/*
		 * if(!validator.validateChargingDetailsValues(chargingDetails)){
		 * addFieldError(CrmFieldType.RESPONSE.getName(),
		 * ErrorType.INVALID_FIELD.getResponseMessage()); }
		 */
	}

	public String getEmailId() {
		return emailId;
	}

	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}

	public String getAcquirer() {
		return acquirer;
	}

	public void setAcquirer(String acquirer) {
		this.acquirer = acquirer;
	}

	@Override
	public ChargingDetails getModel() {
		return chargingDetails;
	}

	public ChargingDetails getChargingDetails() {
		return chargingDetails;
	}

	public void setChargingDetails(ChargingDetails chargingDetails) {
		this.chargingDetails = chargingDetails;
	}

	public String getResponse() {
		return response;
	}

	public void setResponse(String response) {
		this.response = response;
	}

	public String getUserType() {
		return userType;
	}

	public void setUserType(String userType) {
		this.userType = userType;
	}

	public String getLoginUserEmailId() {
		return loginUserEmailId;
	}

	public void setLoginUserEmailId(String loginUserEmailId) {
		this.loginUserEmailId = loginUserEmailId;
	}

	public String getPaymentRegion() {
		return paymentRegion;
	}

	public void setPaymentRegion(String paymentRegion) {
		this.paymentRegion = paymentRegion;
	}

	public String getCardHolderType() {
		return cardHolderType;
	}

	public void setCardHolderType(String cardHolderType) {
		this.cardHolderType = cardHolderType;
	}

}
