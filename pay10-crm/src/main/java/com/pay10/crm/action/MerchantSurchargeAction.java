package com.pay10.crm.action;

import java.util.Date;
import java.util.Map;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.pay10.commons.audittrail.ActionMessageByAction;
import com.pay10.commons.audittrail.AuditTrail;
import com.pay10.commons.audittrail.AuditTrailService;
import com.pay10.commons.dao.HibernateSessionProvider;
import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.user.SurchargeDetails;
import com.pay10.commons.user.SurchargeDetailsDao;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.CrmFieldType;
import com.pay10.commons.util.CrmValidator;
import com.pay10.commons.util.TDRStatus;

/**
 * @author Shaiwal
 *
 */
public class MerchantSurchargeAction extends AbstractSecureAction {

	@Autowired
	private SurchargeDetailsDao surchargeDetailsDao;

	@Autowired
	private CrmValidator validator;

	@Autowired
	private AuditTrailService auditTrailService;

	private static Logger logger = LoggerFactory.getLogger(MerchantSurchargeAction.class.getName());
	private static final long serialVersionUID = -6517340843571949786L;

	private String paymentType;
	private String payId;
	private String emailId;
	private String userType;
	private String operation;
	public Date currentDate = new Date();

	@SuppressWarnings("unchecked")
	@Override
	public String execute() {
		try {
			SurchargeDetails surchargeToUpdate = new SurchargeDetails();
			surchargeToUpdate = surchargeDetailsDao.findPendingDetails(payId, paymentType);

			if (surchargeToUpdate != null) {
				if (operation.equals("accept")) {
					SurchargeDetails activeSurcharge = new SurchargeDetails();
					activeSurcharge = surchargeDetailsDao.findDetails(payId, paymentType);

					if (activeSurcharge != null) {

						updateSurchargeDetail(activeSurcharge, TDRStatus.INACTIVE);
					}
					updateSurchargeDetail(surchargeToUpdate, TDRStatus.ACTIVE);
				} else {
					updateSurchargeDetail(surchargeToUpdate, TDRStatus.REJECTED);
				}
					Map<String, ActionMessageByAction> actionMessagesByAction = (Map<String, ActionMessageByAction>) sessionMap
							.get(Constants.ACTION_MESSAGE_BY_ACTION.getValue());
					AuditTrail auditTrail = new AuditTrail(mapper.writeValueAsString(surchargeToUpdate), null,
							actionMessagesByAction.get("merchantSurchargeApproveReject"));
					auditTrailService.saveAudit(request, auditTrail);
			}
			return SUCCESS;
		} catch (Exception exception) {
			logger.error("Exception", exception);
			return ERROR;
		}
	}

	public void updateSurchargeDetail(SurchargeDetails surchargeDetails, TDRStatus status) {

		try {

			Session session = null;
			session = HibernateSessionProvider.getSession();
			Transaction tx = session.beginTransaction();
			Long id = surchargeDetails.getId();
			session.load(surchargeDetails, surchargeDetails.getId());
			SurchargeDetails surchargeDetail = session.get(SurchargeDetails.class, id);
			surchargeDetail.setStatus(status);
			surchargeDetail.setUpdatedDate(currentDate);
			surchargeDetail.setProcessedBy(emailId);
			session.update(surchargeDetail);
			tx.commit();
			session.close();

		} catch (HibernateException e) {
			e.printStackTrace();
		} finally {

		}

	}

	@Override
	public void validate() {
		if ((validator.validateBlankField(getPaymentType()))) {
			// addFieldError(CrmFieldType.PAYMENT_TYPE.getName(),
			// validator.getResonseObject().getResponseMessage());
		} else if (!(validator.validateField(CrmFieldType.PAYMENT_TYPE, getPaymentType()))) {
			addFieldError(CrmFieldType.PAYMENT_TYPE.getName(), validator.getResonseObject().getResponseMessage());
		}
		if ((validator.validateBlankField(getPayId()))) {
			// addFieldError(CrmFieldType.PAY_ID.getName(),
			// validator.getResonseObject().getResponseMessage());
		} else if (!(validator.validateField(CrmFieldType.PAY_ID, getPayId()))) {
			addFieldError(CrmFieldType.PAY_ID.getName(), validator.getResonseObject().getResponseMessage());
		}
		if (validator.validateBlankField(getEmailId())) {
			// addFieldError(CrmFieldType.EMAILID.getName(),
			// validator.getResonseObject().getResponseMessage());
		} else if (!(validator.isValidEmailId(getEmailId()))) {
			addFieldError(CrmFieldType.EMAILID.getName(), ErrorType.INVALID_FIELD_VALUE.getResponseMessage());
		}
		if ((validator.validateBlankField(getUserType()))) {
			// addFieldError(CrmFieldType.USER_TYPE.getName(),
			// validator.getResonseObject().getResponseMessage());
		} else if (!(validator.validateField(CrmFieldType.USER_TYPE, getUserType()))) {
			addFieldError(CrmFieldType.USER_TYPE.getName(), validator.getResonseObject().getResponseMessage());
		}
		if ((validator.validateBlankField(getOperation()))) {
			// addFieldError(CrmFieldType.OPERATION.getName(),
			// validator.getResonseObject().getResponseMessage());
		} else if (!(validator.validateField(CrmFieldType.OPERATION, getOperation()))) {
			addFieldError(CrmFieldType.OPERATION.getName(), validator.getResonseObject().getResponseMessage());
		}
	}

	public String getUserType() {
		return userType;
	}

	public void setUserType(String userType) {
		this.userType = userType;
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

}
