package com.pay10.crm.action;

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

import com.pay10.commons.api.SmsSender;
import com.pay10.commons.audittrail.ActionMessageByAction;
import com.pay10.commons.audittrail.AuditTrail;
import com.pay10.commons.audittrail.AuditTrailService;
import com.pay10.commons.dao.HibernateSessionProvider;
import com.pay10.commons.dao.ServiceTaxDao;
import com.pay10.commons.user.ServiceTax;
import com.pay10.commons.user.User;
import com.pay10.commons.user.UserDao;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.CrmValidator;
import com.pay10.commons.util.PendingRequestEmailProcessor;
import com.pay10.commons.util.TDRStatus;

/**
 * @author Shaiwal
 *
 */
public class ServiceTaxApproveRejectAction extends AbstractSecureAction {

	@Autowired
	private ServiceTaxDao serviceTaxDao;

	@Autowired
	private PendingRequestEmailProcessor pendingRequestEmailProcessor;

	@Autowired
	private CrmValidator validator;

	@Autowired
	private UserDao userDao;

	@Autowired
	private SmsSender smsSender;
	
	@Autowired
	private AuditTrailService auditTrailService;

	private static Logger logger = LoggerFactory.getLogger(ServiceTaxApproveRejectAction.class.getName());
	private static final long serialVersionUID = -6517340843571949786L;

	private String businessType;
	private String operation;
	private Date currentDate = new Date();
	private User sessionUser = new User();

	private String idArray;
	private String otp;
	private String response;

	@Override
	public String execute() {
		sessionUser = (User) sessionMap.get(Constants.USER.getValue());
		StringBuilder permissions = new StringBuilder();
		permissions.append(sessionMap.get(Constants.USER_PERMISSION.getValue()));

		try {
			ServiceTax serviceTaxToUpdate = new ServiceTax();
			serviceTaxToUpdate = serviceTaxDao.findPendingRequest(businessType);

			if (serviceTaxToUpdate != null) {

				if (operation.equals("accept")) {

					logger.info("Service tax accepted");
					ServiceTax activeServiceTax = new ServiceTax();
					activeServiceTax = serviceTaxDao.findServiceTax(businessType);

					if (activeServiceTax != null) {

						updateServiceTax(activeServiceTax, TDRStatus.INACTIVE);
					}
					updateServiceTax(serviceTaxToUpdate, TDRStatus.ACTIVE);
					logger.info("Service tax accept updated , business Type = " + businessType);

					// pendingRequestEmailProcessor.processServiceTaxApproveRejectEmail("Approved",
					// emailId, userType,
					// businessType, serviceTaxToUpdate.getRequestedBy());

				} else {
					updateServiceTax(serviceTaxToUpdate, TDRStatus.REJECTED);
					// pendingRequestEmailProcessor.processServiceTaxApproveRejectEmail("Rejected",
					// emailId, userType,
					// businessType, serviceTaxToUpdate.getRequestedBy());
				}

			}
		} catch (Exception e) {
			logger.error("Service tax operaion failed");
			return ERROR;
		}

		return SUCCESS;
	}

	@SuppressWarnings("unchecked")
	public String approveRejectGst() {

		Date date = new Date();
		sessionUser = (User) sessionMap.get(Constants.USER.getValue());

		Set<String> mobileNumbers = new HashSet<String>();

		try {
			// Verify OTP

			String[] idArraySplit = idArray.split(",");

			List<ServiceTax> pendingServiceTaxList = new ArrayList<ServiceTax>();

			for (String id : idArraySplit) {
				ServiceTax serviceTax = serviceTaxDao.find(Long.valueOf(id));

				if (serviceTax != null) {
					pendingServiceTaxList.add(serviceTax);
				}
			}

			for (ServiceTax serviceTax : pendingServiceTaxList) {

				if (serviceTax.getOtpExpiryTime().compareTo(date) > 0 && serviceTax.getOtp().equalsIgnoreCase(otp)) {

				} else {
					setResponse("Incorrect or expired OTP entered. Please generate new OTP and try again");
					return SUCCESS;
				}
			}

			for (ServiceTax serviceTax : pendingServiceTaxList) {

				if (StringUtils.isNotBlank(serviceTax.getRequestedBy())) {
					User user = userDao.findPayIdByEmail(serviceTax.getRequestedBy());
					if (StringUtils.isNotBlank(user.getMobile())) {
						mobileNumbers.add(user.getMobile());
					}
				}

			}
			if (StringUtils.isNotBlank(sessionUser.getMobile())) {
				mobileNumbers.add(sessionUser.getMobile());
			}

			// Deactivate Active Service Tax and Active Pending Service Tax
			for (ServiceTax serviceTaxToUpdate : pendingServiceTaxList) {

				if (serviceTaxToUpdate != null) {

					if (operation.equals("accept")) {

						logger.info("Service tax accepted");
						ServiceTax activeServiceTax = new ServiceTax();
						activeServiceTax = serviceTaxDao.findServiceTax(serviceTaxToUpdate.getBusinessType());

						if (activeServiceTax != null) {
							updateServiceTax(activeServiceTax, TDRStatus.INACTIVE);
						}
						updateServiceTax(serviceTaxToUpdate, TDRStatus.ACTIVE);
						logger.info("Service tax accept updated , business Type = " + businessType);

						// pendingRequestEmailProcessor.processServiceTaxApproveRejectEmail("Approved",
						// emailId, userType,
						// businessType, serviceTaxToUpdate.getRequestedBy());

						setResponse("GST Approved and updated successfully");

						// Send Approved SMS
						StringBuilder SMSBodyAccept = new StringBuilder();
						SMSBodyAccept.append("GST update Request(s) processed and Approved for Id(s) " + idArray
								+ "  by  " + sessionUser.getEmailId());

						for (String mobile : mobileNumbers) {
							smsSender.sendSMS(mobile, SMSBodyAccept.toString());
						}

					} else {
						updateServiceTax(serviceTaxToUpdate, TDRStatus.REJECTED);

						setResponse("GST update request Rejected and Removed");

						// Send Rejected SMS
						StringBuilder SMSBodyReject = new StringBuilder();
						SMSBodyReject.append("GST update Request(s) rejected for Id(s) " + idArray + "  by  "
								+ sessionUser.getEmailId());

						for (String mobile : mobileNumbers) {
							smsSender.sendSMS(mobile, SMSBodyReject.toString());
						}

						// pendingRequestEmailProcessor.processServiceTaxApproveRejectEmail("Rejected",
						// emailId, userType,
						// businessType, serviceTaxToUpdate.getRequestedBy());
					}
					
					Map<String, ActionMessageByAction> actionMessagesByAction = (Map<String, ActionMessageByAction>) sessionMap
							.get(Constants.ACTION_MESSAGE_BY_ACTION.getValue());
					AuditTrail auditTrail = new AuditTrail(mapper.writeValueAsString(serviceTaxToUpdate), null,
							actionMessagesByAction.get("approveRejectGst"));
					auditTrailService.saveAudit(request, auditTrail);

				}

			}
		} catch (Exception e) {
			logger.error("Service tax operaion failed");
			return ERROR;
		}

		return SUCCESS;
	}

	@Override
	public void validate() {
		/*
		 * if ((validator.validateBlankField(getBusinessType()))) { //
		 * addFieldError(CrmFieldType.BUSINESS_TYPE.getName(),
		 * validator.getResonseObject().getResponseMessage()); } else if
		 * (!(validator.validateField(CrmFieldType.BUSINESS_NAME, getBusinessType()))) {
		 * addFieldError(CrmFieldType.BUSINESS_NAME.getName(),
		 * validator.getResonseObject().getResponseMessage()); } if
		 * (validator.validateBlankField(getEmailId())) { //
		 * addFieldError(CrmFieldType.EMAILID.getName(),
		 * validator.getResonseObject().getResponseMessage()); } else if
		 * (!(validator.isValidEmailId(getEmailId()))) {
		 * addFieldError(CrmFieldType.EMAILID.getName(),
		 * ErrorType.INVALID_FIELD_VALUE.getResponseMessage()); } if
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
		 * validator.getResonseObject().getResponseMessage()); }
		 */
	}

	public void updateServiceTax(ServiceTax serviceTax, TDRStatus status) {

		Session session = null;
		try {
			session = HibernateSessionProvider.getSession();
			Transaction tx = session.beginTransaction();
			Long id = serviceTax.getId();
			session.load(serviceTax, serviceTax.getId());
			ServiceTax sTax = session.get(ServiceTax.class, id);
			sTax.setStatus(status);
			sTax.setUpdatedDate(currentDate);
			sTax.setProcessedBy(sessionUser.getEmailId());
			session.update(sTax);
			tx.commit();
			session.close();

		} catch (HibernateException e) {
			logger.error("Exception ", e);
		} finally {
			session.close();
		}

	}

	public String getOperation() {
		return operation;
	}

	public void setOperation(String operation) {
		this.operation = operation;
	}

	public String getBusinessType() {
		return businessType;
	}

	public void setBusinessType(String businessType) {
		this.businessType = businessType;
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

	public String getResponse() {
		return response;
	}

	public void setResponse(String response) {
		this.response = response;
	}

}
