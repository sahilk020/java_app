package com.pay10.crm.action;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.pay10.commons.api.SmsSender;
import com.pay10.commons.audittrail.ActionMessageByAction;
import com.pay10.commons.audittrail.AuditTrail;
import com.pay10.commons.audittrail.AuditTrailService;
import com.pay10.commons.dao.HibernateSessionProvider;
import com.pay10.commons.dao.RouterConfigurationDao;
import com.pay10.commons.user.AccountCurrencyRegion;
import com.pay10.commons.user.RouterConfiguration;
import com.pay10.commons.user.User;
import com.pay10.commons.user.UserDao;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.CrmValidator;
import com.pay10.commons.util.TDRStatus;

/**
 * @author Shaiwal
 *
 */
public class SmartRouterApproveRejectAction extends AbstractSecureAction {

	@Autowired
	private RouterConfigurationDao routerConfigurationDao;

	@Autowired
	private CrmValidator validator;

	@Autowired
	private UserDao userDao;

	@Autowired
	private SmsSender smsSender;

	@Autowired
	private AuditTrailService auditTrailService;

	private static Logger logger = LoggerFactory.getLogger(SmartRouterApproveRejectAction.class.getName());
	private static final long serialVersionUID = -6517340843571949786L;

	private String paymentType;
	private String payId;
	private String mopType;
	private String acquirer;
	private String operation;
	private String response;
	private String paymentsRegion;

	private String idArray;
	private String otp;
	private User sessionUser = new User();

	public Date currentDate = new Date();

	@Override
	public String execute() {
		Date date = new Date();
		Set<String> mobileNumbers = new HashSet<String>();
		sessionUser = (User) sessionMap.get(Constants.USER.getValue());
		AccountCurrencyRegion acr = null;
		String[] idSplit = idArray.split(",");
		List<RouterConfiguration> routerConfigurationToUpdateVerify = new ArrayList<RouterConfiguration>();
		for (String idSplitUnit : idSplit) {

			List<RouterConfiguration> routerConfigurationToUpdate = routerConfigurationDao
					.findPendingRulesByIdentifier(idSplitUnit);
			routerConfigurationToUpdateVerify.addAll(routerConfigurationToUpdate);
		}

		// Verify OTP for all

		for (RouterConfiguration pendingRouterConfiguration : routerConfigurationToUpdateVerify) {

			if (pendingRouterConfiguration.getOtpExpiryTime().compareTo(date) > 0
					&& pendingRouterConfiguration.getOtp().equalsIgnoreCase(otp)) {

			} else {
				setResponse("Incorrect or expired OTP entered. Please generate new OTP and try again");
				return SUCCESS;
			}

		}

		for (RouterConfiguration pendingRouterConfiguration : routerConfigurationToUpdateVerify) {

			if (StringUtils.isNotBlank(pendingRouterConfiguration.getRequestedBy())) {
				User user = userDao.findPayIdByEmail(pendingRouterConfiguration.getRequestedBy());
				if (StringUtils.isNotBlank(user.getMobile())) {
					mobileNumbers.add(user.getMobile());
				}
			}

		}

		if (StringUtils.isNotBlank(sessionUser.getMobile())) {
			mobileNumbers.add(sessionUser.getMobile());
		}

		if (operation.equalsIgnoreCase("accept")) {

			// Deavtivate Active Configurations
			List<RouterConfiguration> activeRouterConfigurationList = new ArrayList<RouterConfiguration>();
			for (String idSplitUnit : idSplit) {

				List<RouterConfiguration> routerConfigurationActive = routerConfigurationDao
						.findRulesByIdentifier(idSplitUnit);
				activeRouterConfigurationList.addAll(routerConfigurationActive);
			}

			for (RouterConfiguration activeRouterConfiguration : activeRouterConfigurationList) {
				routerConfigurationDao.delete(activeRouterConfiguration.getId(), sessionUser);
			}

			for (RouterConfiguration pendingRouterConfiguration : routerConfigurationToUpdateVerify) {
				try {

					Session session = null;
					session = HibernateSessionProvider.getSession();
					Transaction tx = session.beginTransaction();
					Long id = pendingRouterConfiguration.getId();
					session.load(pendingRouterConfiguration, pendingRouterConfiguration.getId());
					RouterConfiguration sch = session.get(RouterConfiguration.class, id);
					sch.setStatus(TDRStatus.ACTIVE);
					sch.setUpdatedDate(currentDate);
					sch.setUpdatedBy(sessionUser.getEmailId());
					session.update(sch);
					tx.commit();
					session.close();

				} catch (HibernateException e) {
					logger.error("Exception: " + e);
				} finally {

				}

			}
			addAuditTrailEntry(routerConfigurationToUpdateVerify);
			// Send Approved SMS
			StringBuilder SMSBodyAccept = new StringBuilder();
			SMSBodyAccept.append("Smart Router update Request(s) processed and Approved for Id(s) " + idArray + "  by  "
					+ sessionUser.getEmailId());

			for (String mobile : mobileNumbers) {
				try {
					smsSender.sendSMS(mobile, SMSBodyAccept.toString());
				} catch (IOException e) {
					logger.error("Exception in sending sms");
				}
			}

			setResponse("Smart Router update request Approved and Updated");
			return SUCCESS;

		} else {

			for (RouterConfiguration pendingRouterConfiguration : routerConfigurationToUpdateVerify) {

				try {

					Session session = null;
					session = HibernateSessionProvider.getSession();
					Transaction tx = session.beginTransaction();
					Long id = pendingRouterConfiguration.getId();
					session.load(pendingRouterConfiguration, pendingRouterConfiguration.getId());
					RouterConfiguration routerConfig = session.get(RouterConfiguration.class, id);
					routerConfig.setStatus(TDRStatus.REJECTED);
					routerConfig.setUpdatedDate(currentDate);
					routerConfig.setUpdatedBy(sessionUser.getEmailId());
					session.update(routerConfig);
					tx.commit();
					session.close();

				} catch (HibernateException e) {
					logger.error("Exception: " + e);
				} finally {

				}
			}
			addAuditTrailEntry(routerConfigurationToUpdateVerify);
			setResponse("Smart Router update request Rejected and Removed");
			return SUCCESS;
		}
	}
	
	@SuppressWarnings("unchecked")
	private void addAuditTrailEntry(List<RouterConfiguration> routerConfigurationToUpdateVerify) {
		Map<String, ActionMessageByAction> actionMessagesByAction = (Map<String, ActionMessageByAction>) sessionMap
				.get(Constants.ACTION_MESSAGE_BY_ACTION.getValue());
		try {
			AuditTrail auditTrail = new AuditTrail(mapper.writeValueAsString(routerConfigurationToUpdateVerify), null,
					actionMessagesByAction.get("approveRejectSmartRouter"));
			auditTrailService.saveAudit(request, auditTrail);
		} catch (JsonProcessingException e1) {
			logger.warn("Exception: ", e1);
		}
	}

	@Override
	public void validate() {

		/*
		 * if ((validator.validateBlankField(getPaymentType()))) {
		 * addFieldError(CrmFieldType.PAYMENT_TYPE.getName(),
		 * validator.getResonseObject().getResponseMessage()); } else if
		 * (!(validator.validateField(CrmFieldType.PAYMENT_TYPE, getPaymentType()))) {
		 * addFieldError(CrmFieldType.PAYMENT_TYPE.getName(),
		 * validator.getResonseObject().getResponseMessage()); } if
		 * ((validator.validateBlankField(getPayId()))) { //
		 * addFieldError(CrmFieldType.PAY_ID.getName(),
		 * validator.getResonseObject().getResponseMessage()); } else if
		 * (!(validator.validateField(CrmFieldType.PAY_ID, getPayId()))) {
		 * addFieldError(CrmFieldType.PAY_ID.getName(),
		 * validator.getResonseObject().getResponseMessage()); } if
		 * ((validator.validateBlankField(getMopType()))) { //
		 * addFieldError(CrmFieldType.MOP_TYPE.getName(),
		 * validator.getResonseObject().getResponseMessage()); } else if
		 * (!(validator.validateField(CrmFieldType.MOP_TYPE, getMopType()))) {
		 * addFieldError(CrmFieldType.MOP_TYPE.getName(),
		 * validator.getResonseObject().getResponseMessage()); } if
		 * ((validator.validateBlankField(getAcquirer()))) { //
		 * addFieldError(CrmFieldType.ACQUIRER.getName(),
		 * validator.getResonseObject().getResponseMessage()); } else if
		 * (!(validator.validateField(CrmFieldType.ACQUIRER, getAcquirer()))) {
		 * addFieldError(CrmFieldType.ACQUIRER.getName(),
		 * validator.getResonseObject().getResponseMessage()); } if
		 * ((validator.validateBlankField(getEmailId()))) { //
		 * addFieldError(CrmFieldType.EMAILID.getName(),
		 * validator.getResonseObject().getResponseMessage()); } else if
		 * (!(validator.validateField(CrmFieldType.EMAILID, getEmailId()))) {
		 * addFieldError(CrmFieldType.EMAILID.getName(),
		 * validator.getResonseObject().getResponseMessage()); } if
		 * ((validator.validateBlankField(getUserType()))) { //
		 * addFieldError(CrmFieldType.USER_TYPE.getName(),
		 * validator.getResonseObject().getResponseMessage()); } else if
		 * (!(validator.validateField(CrmFieldType.USER_TYPE, getUserType()))) {
		 * addFieldError(CrmFieldType.USER_TYPE.getName(),
		 * validator.getResonseObject().getResponseMessage()); } if
		 * ((validator.validateBlankField(getOperation()))) { //
		 * addFieldError(CrmFieldType.OPERATION.getName(),
		 * validator.getResonseObject().getResponseMessage()); } else if
		 * (!(validator.validateField(CrmFieldType.OPERATION, getOperation()))) {
		 * addFieldError(CrmFieldType.OPERATION.getName(),
		 * validator.getResonseObject().getResponseMessage()); } if
		 * ((validator.validateBlankField(getResponse()))) { //
		 * addFieldError(CrmFieldType.RESPONSE.getName(),
		 * validator.getResonseObject().getResponseMessage()); } else if
		 * (!(validator.validateField(CrmFieldType.RESPONSE, getResponse()))) {
		 * addFieldError(CrmFieldType.RESPONSE.getName(),
		 * validator.getResonseObject().getResponseMessage()); }
		 */

	}

	public String getOperation() {
		return operation;
	}

	public void setOperation(String operation) {
		this.operation = operation;
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

	public String getMopType() {
		return mopType;
	}

	public void setMopType(String mopType) {
		this.mopType = mopType;
	}

	public String getAcquirer() {
		return acquirer;
	}

	public void setAcquirer(String acquirer) {
		this.acquirer = acquirer;
	}

	public String getResponse() {
		return response;
	}

	public void setResponse(String response) {
		this.response = response;
	}

	public String getPaymentsRegion() {
		return paymentsRegion;
	}

	public void setPaymentsRegion(String paymentsRegion) {
		this.paymentsRegion = paymentsRegion;
	}

	public String getIdArray() {
		return idArray;
	}

	public void setIdArray(String idArray) {
		this.idArray = idArray;
	}

	public String getOtp() {
		return otp;
	}

	public void setOtp(String otp) {
		this.otp = otp;
	}

}
