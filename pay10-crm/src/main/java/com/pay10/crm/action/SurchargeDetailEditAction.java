package com.pay10.crm.action;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.pay10.commons.audittrail.ActionMessageByAction;
import com.pay10.commons.audittrail.AuditTrail;
import com.pay10.commons.audittrail.AuditTrailService;
import com.pay10.commons.dao.HibernateSessionProvider;
import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.user.AccountCurrencyRegion;
import com.pay10.commons.user.SurchargeDao;
import com.pay10.commons.user.SurchargeDetails;
import com.pay10.commons.user.SurchargeDetailsDao;
import com.pay10.commons.user.User;
import com.pay10.commons.user.UserDao;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.CrmFieldType;
import com.pay10.commons.util.CrmValidator;
import com.pay10.commons.util.PaymentType;
import com.pay10.commons.util.PendingRequestEmailProcessor;
import com.pay10.commons.util.TDRStatus;

public class SurchargeDetailEditAction extends AbstractSecureAction {

	@Autowired
	private SurchargeDao surchargeDao;

	@Autowired
	private SurchargeDetailsDao surchargeDetailsDao;

	@Autowired
	private UserDao userDao;

	@Autowired
	private PendingRequestEmailProcessor pendingRequestEmailProcessor;

	@Autowired
	private CrmValidator validator;
	
	@Autowired
	private AuditTrailService auditTrailService;

	private static Logger logger = LoggerFactory.getLogger(SurchargeDetailEditAction.class.getName());
	private static final long serialVersionUID = -6517340843571949786L;

	private SurchargeDetails surchargeDetails = new SurchargeDetails();
	private String emailId;
	private String paymentType;
	private String response;

	private BigDecimal surchargeAmount;
	private BigDecimal surchargePercentage;
	private BigDecimal minTransactionAmount;
	private String paymentTypeName;
	private String paymentsRegion;
	private String loginEmailId;
	private String userType;
	private Boolean isHigher;
	private Date currentDate = new Date();

	@Override
	public String execute() {

		User user = userDao.findPayIdByEmail(emailId);
		paymentTypeName = PaymentType.getpaymentName(paymentType);
		String payId = user.getPayId();
		try {

			SurchargeDetails surchargeDetailsFromDb = surchargeDetailsDao.findDetailsByRegion(payId, paymentTypeName,
					paymentsRegion);
			String prevSurcharge = mapper.writeValueAsString(surchargeDetailsFromDb);
			if (surchargeDetailsFromDb != null) {

				// If userType is admin , change surcharge status to inactive
				// and create new surcharge
				disablePreviousCharge(surchargeDetailsFromDb);
				cancelPendingCharge(payId, paymentTypeName);
				createNewSurcharge(payId, TDRStatus.ACTIVE);
				// pendingRequestEmailProcessor.processMerchantSurchargeRequestEmail(TDRStatus.ACTIVE.getName(),
				// loginEmailId, userType, merchantName, payId);
			} else {
				cancelPendingCharge(payId, paymentTypeName);
				createNewSurcharge(payId, TDRStatus.ACTIVE);
			}
			addAuditTrailEntry(prevSurcharge);

		} catch (Exception exception) {
			logger.error("Exception", exception);
		}
		setResponse(ErrorType.MERCHANT_SURCHARGE_UPDATED.getResponseMessage());
		return SUCCESS;
	}

	@SuppressWarnings("unchecked")
	private void addAuditTrailEntry(String prevSurcharge) throws JsonProcessingException {
		if (StringUtils.equals(prevSurcharge, "null")) {
			prevSurcharge = null;
		}
		Map<String, ActionMessageByAction> actionMessagesByAction = (Map<String, ActionMessageByAction>) sessionMap
				.get(Constants.ACTION_MESSAGE_BY_ACTION.getValue());
		AuditTrail auditTrail = new AuditTrail(mapper.writeValueAsString(surchargeDetails), prevSurcharge,
				actionMessagesByAction.get("editSurchargeDetail"));
		auditTrailService.saveAudit(request, auditTrail);
	}

	@Override
	public void validate() {
		if ((validator.validateBlankField(getEmailId()))) {
			addFieldError(CrmFieldType.EMAILID.getName(), validator.getResonseObject().getResponseMessage());
		} else if (!(validator.validateField(CrmFieldType.EMAILID, getEmailId()))) {
			addFieldError(CrmFieldType.EMAILID.getName(), validator.getResonseObject().getResponseMessage());
		}
		if ((validator.validateBlankField(getPaymentType()))) {
			addFieldError(CrmFieldType.PAYMENT_TYPE.getName(), validator.getResonseObject().getResponseMessage());
		} else if (!(validator.validateField(CrmFieldType.PAYMENT_TYPE, getPaymentType()))) {
			addFieldError(CrmFieldType.PAYMENT_TYPE.getName(), validator.getResonseObject().getResponseMessage());
		}
		if ((validator.validateBlankField(getResponse()))) {
			// addFieldError(CrmFieldType.RESPONSE.getName(),
			// validator.getResonseObject().getResponseMessage());
		} else if (!(validator.validateField(CrmFieldType.RESPONSE, getResponse()))) {
			addFieldError(CrmFieldType.RESPONSE.getName(), validator.getResonseObject().getResponseMessage());
		}
		if ((validator.validateBlankField(getLoginEmailId()))) {
			addFieldError(CrmFieldType.EMAILID.getName(), validator.getResonseObject().getResponseMessage());
		} else if (!(validator.validateField(CrmFieldType.EMAILID, getLoginEmailId()))) {
			addFieldError(CrmFieldType.EMAILID.getName(), validator.getResonseObject().getResponseMessage());
		}
		if ((validator.validateBlankField(getUserType()))) {
			addFieldError(CrmFieldType.USER_TYPE.getName(), validator.getResonseObject().getResponseMessage());
		} else if (!(validator.validateField(CrmFieldType.USER_TYPE, getUserType()))) {
			addFieldError(CrmFieldType.USER_TYPE.getName(), validator.getResonseObject().getResponseMessage());
		}

	}

	public void disablePreviousCharge(SurchargeDetails surchargeDetailsFromDb) {

		Session session = null;
		Long id = surchargeDetailsFromDb.getId();
		session = HibernateSessionProvider.getSession();
		Transaction tx = session.beginTransaction();
		session.load(surchargeDetailsFromDb, surchargeDetailsFromDb.getId());
		try {
			SurchargeDetails surchargeDetails = session.get(SurchargeDetails.class, id);
			surchargeDetails.setStatus(TDRStatus.INACTIVE);
			surchargeDetails.setUpdatedDate(currentDate);
			surchargeDetails.setProcessedBy(loginEmailId);
			session.update(surchargeDetails);
			tx.commit();
		} catch (HibernateException e) {
			if (tx != null)
				tx.rollback();
			logger.error("Exception " + e);
		} finally {
			session.close();
		}

	}

	public boolean checkExistingSurchargeRequest(String payId, String paymentTypeName, String paymentsRegion) {

		SurchargeDetails surchargeDetails = new SurchargeDetails();
		surchargeDetails = surchargeDetailsDao.findPendingDetailsByRegion(payId, paymentTypeName, paymentsRegion);
		if (surchargeDetails != null) {
			return true;
		}
		return false;
	}

	public void cancelPendingCharge(String payId, String paymentType) {

		Session session = null;

		SurchargeDetails surchargeDetailsFromDb = surchargeDetailsDao.findPendingDetailsByRegion(payId, paymentType,
				paymentsRegion);
		if (surchargeDetailsFromDb != null) {
			Long id = surchargeDetailsFromDb.getId();
			session = HibernateSessionProvider.getSession();
			Transaction tx = session.beginTransaction();
			session.load(surchargeDetailsFromDb, surchargeDetailsFromDb.getId());
			try {
				SurchargeDetails surchargeDetails = session.get(SurchargeDetails.class, id);
				surchargeDetails.setStatus(TDRStatus.CANCELLED);
				surchargeDetails.setUpdatedDate(currentDate);
				surchargeDetails.setProcessedBy(loginEmailId);
				session.update(surchargeDetails);
				tx.commit();
			} catch (HibernateException e) {
				if (tx != null)
					tx.rollback();
				e.printStackTrace();
			} finally {
				session.close();
			}
		}
	}

	public void createNewSurcharge(String payId, TDRStatus status) {

		SurchargeDetails newSurchargeDetails = new SurchargeDetails();
		newSurchargeDetails.setPayId(payId);
		newSurchargeDetails.setPaymentType(paymentTypeName);
		if (String.valueOf(surchargeAmount).equals("")) {
			newSurchargeDetails.setSurchargeAmount(BigDecimal.ZERO);
		} else {
			newSurchargeDetails.setSurchargeAmount(surchargeAmount);
		}

		if (String.valueOf(surchargePercentage).equals("")) {
			newSurchargeDetails.setSurchargePercentage(BigDecimal.ZERO);
		} else {
			newSurchargeDetails.setSurchargePercentage(surchargePercentage);
		}

		if (String.valueOf(surchargePercentage).equals("")) {
			newSurchargeDetails.setMinTransactionAmount(BigDecimal.ZERO);
		} else {
			newSurchargeDetails.setMinTransactionAmount(minTransactionAmount);
		}
		newSurchargeDetails.setIsHigher(getIsHigher());
		newSurchargeDetails.setStatus(status);
		newSurchargeDetails.setCreatedDate(currentDate);
		newSurchargeDetails.setUpdatedDate(currentDate);

		if (paymentsRegion.equals(AccountCurrencyRegion.DOMESTIC.toString())) {

			newSurchargeDetails.setPaymentsRegion(AccountCurrencyRegion.DOMESTIC);
		} else {
			newSurchargeDetails.setPaymentsRegion(AccountCurrencyRegion.INTERNATIONAL);
		}

		newSurchargeDetails.setId(null);
		newSurchargeDetails.setRequestedBy(loginEmailId);
		if (status.equals(TDRStatus.ACTIVE)) {
			newSurchargeDetails.setProcessedBy(loginEmailId);
		}
		surchargeDetailsDao.create(newSurchargeDetails);

	}

	public BigDecimal getSurchargeAmount() {
		return surchargeAmount;
	}

	public void setSurchargeAmount(BigDecimal surchargeAmount) {
		this.surchargeAmount = surchargeAmount;
	}

	public BigDecimal getSurchargePercentage() {
		return surchargePercentage;
	}

	public void setSurchargePercentage(BigDecimal surchargePercentage) {
		this.surchargePercentage = surchargePercentage;
	}

	public String getPaymentType() {
		return paymentType;
	}

	public void setPaymentType(String paymentType) {
		this.paymentType = paymentType;
	}

	public String getEmailId() {
		return emailId;
	}

	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}

	public SurchargeDetails getSurchargeDetails() {
		return surchargeDetails;
	}

	public void setSurchargeDetails(SurchargeDetails surchargeDetails) {
		this.surchargeDetails = surchargeDetails;
	}

	public String getLoginEmailId() {
		return loginEmailId;
	}

	public void setLoginEmailId(String loginEmailId) {
		this.loginEmailId = loginEmailId;
	}

	public String getUserType() {
		return userType;
	}

	public void setUserType(String userType) {
		this.userType = userType;
	}

	public String getResponse() {
		return response;
	}

	public void setResponse(String response) {
		this.response = response;
	}

	public BigDecimal getMinTransactionAmount() {
		return minTransactionAmount;
	}

	public void setMinTransactionAmount(BigDecimal minTransactionAmount) {
		this.minTransactionAmount = minTransactionAmount;
	}

	public String getPaymentsRegion() {
		return paymentsRegion;
	}

	public void setPaymentsRegion(String paymentsRegion) {
		this.paymentsRegion = paymentsRegion;
	}

	public Boolean getIsHigher() {
		return isHigher;
	}

	public void setIsHigher(Boolean isHigher) {
		this.isHigher = isHigher;
	}

	
}
