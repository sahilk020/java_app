package com.pay10.crm.action;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
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
import com.pay10.commons.dao.ServiceTaxDao;
import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.user.ServiceTax;
import com.pay10.commons.user.User;
import com.pay10.commons.user.UserDao;
import com.pay10.commons.user.UserType;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.CrmValidator;
import com.pay10.commons.util.PendingRequestEmailProcessor;
import com.pay10.commons.util.TDRStatus;

/**
 * @author Shaiwal
 *
 */
public class ServiceTaxEditAction extends AbstractSecureAction {

	@Autowired
	private ServiceTaxDao serviceTaxDao;

	@Autowired
	private PendingRequestEmailProcessor pendingRequestEmailProcessor;

	@Autowired
	private CrmValidator validator;

	@Autowired
	private AuditTrailService auditTrailService;

	private static Logger logger = LoggerFactory.getLogger(ServiceTaxEditAction.class.getName());
	private static final long serialVersionUID = -6517340843571949786L;

	private Long id;
	private String businessType;
	private String status;
	private BigDecimal serviceTax;
	private String response;
	private String loginEmailId;
	private Date currentDate = new Date();
	private User sessionUser = new User();
	private ServiceTax payloadequest = new ServiceTax();

	@Autowired
	private UserDao userDao;

	@Autowired
	private SmsSender smsSender;

	@Override
	public String execute() {

		logger.info("Call ServiceTaxEditAction BusinessType : "+businessType);

		Session session = null;
		sessionUser = (User) sessionMap.get(Constants.USER.getValue());
		ServiceTax findPendingRequest = new ServiceTax();
		String prevTax = null;
		// Cancel Pending Request


		findPendingRequest = serviceTaxDao.findPendingRequest(businessType);

		logger.info("Call ServiceTaxEditAction FindPendingRequest : "+new GsonBuilder().disableHtmlEscaping().create().toJson(findPendingRequest));


		if (findPendingRequest != null) {
			try {

				session = HibernateSessionProvider.getSession();
				Transaction tx = session.beginTransaction();
				id = findPendingRequest.getId();

				session.load(findPendingRequest, findPendingRequest.getId());

				ServiceTax serviceTaxDetails = session.get(ServiceTax.class, id);

				logger.info("Call ServiceTaxEditAction findPendingRequest serviceTaxDetails : "+new Gson().toJson(serviceTaxDetails));

				serviceTaxDetails.setStatus(TDRStatus.CANCELLED);
				serviceTaxDetails.setUpdatedDate(currentDate);
				serviceTaxDetails.setProcessedBy(loginEmailId);
				session.update(serviceTaxDetails);
				tx.commit();
				session.close();
			} catch (HibernateException e) {

				logger.error("Service Tax edit failed");
				return ERROR;
			}

		}

		ServiceTax activeServiceTax = new ServiceTax();
		activeServiceTax = serviceTaxDao.findServiceTax(businessType);

		logger.info("Call ServiceTaxEditAction ActiveServiceTax : "+new GsonBuilder().disableHtmlEscaping().create().toJson(activeServiceTax));

		// Deactivate active service tax
		if (activeServiceTax != null) {
			try {

				prevTax = mapper.writeValueAsString(activeServiceTax);
				session = HibernateSessionProvider.getSession();
				Transaction txn = session.beginTransaction();
				id = activeServiceTax.getId();
				session.load(activeServiceTax, activeServiceTax.getId());
				ServiceTax serviceTaxDetail = session.get(ServiceTax.class, id);

				logger.info("Call ServiceTaxEditAction activeServiceTax serviceTaxDetails : "+new Gson().toJson(serviceTaxDetail));

				serviceTaxDetail.setStatus(TDRStatus.INACTIVE);
				serviceTaxDetail.setUpdatedDate(currentDate);
				serviceTaxDetail.setProcessedBy(loginEmailId);
				session.update(serviceTaxDetail);
				txn.commit();
				session.close();
			} catch (Exception e) {
				logger.error("Service Tax Edit Failed " + e);
				return ERROR;
			}
		}

		try {

			createNewServiceTax(businessType, serviceTax, sessionUser);
			addAuditTrailEntry(prevTax);
		} catch (Exception e) {
			logger.error("Service Tax Edit Failed " + e);
		}

		setResponse(ErrorType.SERVICE_TAX_DETAILS_SAVED.getResponseMessage());
		return SUCCESS;
	}
	
	@SuppressWarnings("unchecked")
	private void addAuditTrailEntry(String previous) throws JsonProcessingException {
		Map<String, ActionMessageByAction> actionMessagesByAction = (Map<String, ActionMessageByAction>) sessionMap
				.get(Constants.ACTION_MESSAGE_BY_ACTION.getValue());
		AuditTrail auditTrail = new AuditTrail(mapper.writeValueAsString(payloadequest), previous,
				actionMessagesByAction.get("editServiceTax"));
		auditTrailService.saveAudit(request, auditTrail);
	}

	@Override
	public void validate() {
		/*
		 * if (validator.validateBlankField(getBusinessType())) { //
		 * addFieldError(CrmFieldType.BUSINESS_TYPE.getName(),
		 * validator.getResonseObject().getResponseMessage()); } else if
		 * (!(validator.validateField(CrmFieldType.BUSINESS_NAME, getBusinessType()))) {
		 * addFieldError(CrmFieldType.BUSINESS_NAME.getName(),
		 * validator.getResonseObject().getResponseMessage()); } if
		 * (validator.validateBlankField(getStatus())) { //
		 * addFieldError(CrmFieldType.STATUS.getName(),
		 * validator.getResonseObject().getResponseMessage()); } else if
		 * (!(validator.validateField(CrmFieldType.STATUS, getStatus()))) {
		 * addFieldError(CrmFieldType.STATUS.getName(),
		 * validator.getResonseObject().getResponseMessage()); } if
		 * (validator.validateBlankField(getResponse())) { //
		 * addFieldError(CrmFieldType.RESPONSE.getName(),
		 * validator.getResonseObject().getResponseMessage()); } else if
		 * (!(validator.validateField(CrmFieldType.RESPONSE, getResponse()))) {
		 * addFieldError(CrmFieldType.RESPONSE.getName(),
		 * validator.getResonseObject().getResponseMessage()); } if
		 * (validator.validateBlankField(getUserType())) { //
		 * addFieldError(CrmFieldType.USER_TYPE.getName(),
		 * validator.getResonseObject().getResponseMessage()); } else if
		 * (!(validator.validateField(CrmFieldType.USER_TYPE, getUserType()))) {
		 * addFieldError(CrmFieldType.USER_TYPE.getName(),
		 * validator.getResonseObject().getResponseMessage()); } if
		 * (validator.validateBlankField(getLoginEmailId())) { //
		 * addFieldError(CrmFieldType.EMAILID.getName(),
		 * validator.getResonseObject().getResponseMessage()); } else if
		 * (!(validator.validateField(CrmFieldType.EMAILID, getLoginEmailId()))) {
		 * addFieldError(CrmFieldType.EMAILID.getName(),
		 * validator.getResonseObject().getResponseMessage()); }
		 */
	}

	private void createNewServiceTax(String businessType, BigDecimal serviceTax, User sessionUser) {

		try {

			StringBuilder permissions = new StringBuilder();
			permissions.append(sessionMap.get(Constants.USER_PERMISSION.getValue()));
			ServiceTax newServiceTax = new ServiceTax();
			newServiceTax.setCreatedDate(currentDate);
			newServiceTax.setBusinessType(businessType);

			if (sessionUser.getUserType().equals(UserType.ADMIN) || sessionUser.getUserType().equals(UserType.SUBADMIN)  || permissions.toString().contains("Create Service Tax")) {
				newServiceTax.setStatus(TDRStatus.ACTIVE);
				newServiceTax.setProcessedBy(loginEmailId);
				newServiceTax.setRequestedBy(loginEmailId);
			} else {
				newServiceTax.setStatus(TDRStatus.PENDING);
				newServiceTax.setRequestedBy(loginEmailId);

			}
			newServiceTax.setUpdatedDate(currentDate);
			newServiceTax.setServiceTax(serviceTax);
			logger.info("Sending service tax email");
			serviceTaxDao.create(newServiceTax);
			payloadequest = newServiceTax;
			if (sessionUser.getUserType().equals(UserType.ADMIN)
					|| permissions.toString().contains("Create Service Tax")) {
				logger.info("Sending service tax email from admin");
				// pendingRequestEmailProcessor.processServiceTaxEmail("Active", loginEmailId,
				// userType, businessType);

			} else {
				logger.info("Sending service tax email from sub-admin");
				// pendingRequestEmailProcessor.processServiceTaxEmail("Pending", loginEmailId,
				// userType, businessType);
			}

		}

		catch (Exception e) {
			logger.error("Service tax edit failed , error = " + e);
		}

	}

	public BigDecimal getServiceTax() {
		return serviceTax;
	}

	public void setServiceTax(BigDecimal serviceTax) {
		this.serviceTax = serviceTax;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getBusinessType() {
		return businessType;
	}

	public void setBusinessType(String businessType) {
		this.businessType = businessType;
	}

	public String getLoginEmailId() {
		return loginEmailId;
	}

	public void setLoginEmailId(String loginEmailId) {
		this.loginEmailId = loginEmailId;
	}

	public String getResponse() {
		return response;
	}

	public void setResponse(String response) {
		this.response = response;
	}
}
