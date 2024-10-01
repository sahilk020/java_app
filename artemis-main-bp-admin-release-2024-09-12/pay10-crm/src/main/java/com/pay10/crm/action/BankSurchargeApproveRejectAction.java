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
import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.user.AccountCurrencyRegion;
import com.pay10.commons.user.Surcharge;
import com.pay10.commons.user.SurchargeDao;
import com.pay10.commons.user.User;
import com.pay10.commons.user.UserDao;
import com.pay10.commons.user.UserType;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.CrmFieldConstants;
import com.pay10.commons.util.CrmValidator;
import com.pay10.commons.util.TDRStatus;

/**
 * @author Shaiwal
 *
 */
public class BankSurchargeApproveRejectAction extends AbstractSecureAction {

	@Autowired
	private SurchargeDao surchargeDao;

	@Autowired
	private CrmValidator validator;

	@Autowired
	private SmsSender smsSender;

	@Autowired
	private UserDao userDao;

	@Autowired
	private AuditTrailService auditTrailService;

	private static Logger logger = LoggerFactory.getLogger(BankSurchargeApproveRejectAction.class.getName());
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
		sessionUser = (User) sessionMap.get(Constants.USER.getValue());

		List<Surcharge> surchargeToUpdate = new ArrayList<Surcharge>();

		surchargeToUpdate = surchargeDao.findPendingSurchargeListByPayIdAcquirerName(payId, paymentType, acquirer,
				mopType);

		if (surchargeToUpdate != null) {

			if (operation.equals("accept")) {
				List<Surcharge> activeSurchargeList = new ArrayList<Surcharge>();
				activeSurchargeList = surchargeDao.findSurchargeListByPayIdAcquirerName(payId, paymentType, acquirer,
						mopType);

				if (activeSurchargeList != null) {

					for (Surcharge surcharge : activeSurchargeList) {
						updateSurcharge(surcharge, TDRStatus.INACTIVE);
					}
				}

				for (Surcharge surcharge : surchargeToUpdate) {
					updateSurcharge(surcharge, TDRStatus.ACTIVE);
				}
				setResponse(ErrorType.BANK_SURCHARGE_UPDATED.getResponseMessage());
				return SUCCESS;

			} else {
				for (Surcharge surcharge : surchargeToUpdate) {
					updateSurcharge(surcharge, TDRStatus.REJECTED);
				}
				setResponse(ErrorType.BANK_SURCHARGE_REJECTED.getResponseMessage());
				return SUCCESS;
			}

		}
		return SUCCESS;
	}

	public String approveRejectBankSurcharge() {

		Set<String> mobileNumbers = new HashSet<String>();
		Date date = new Date();
		sessionUser = (User) sessionMap.get(Constants.USER.getValue());
		AccountCurrencyRegion acr = null;
		StringBuilder permissions = new StringBuilder();
		permissions.append(sessionMap.get(Constants.USER_PERMISSION.getValue()));

		String[] idSplit = idArray.split(";");

		if (permissions.toString().contains(CrmFieldConstants.CREATE_SURCHARGE.getValue())
				|| sessionUser.getUserType().equals(UserType.ADMIN)) {

			List<Surcharge> surchargeToUpdateVerify = new ArrayList<Surcharge>();
			for (String idSplitUnit : idSplit) {

				String[] idSplitArray = idSplitUnit.split(",");

				String paymentType = idSplitArray[0];
				String payId = idSplitArray[1];
				String mopType = idSplitArray[2];
				String acquirer = idSplitArray[3];
				String paymentsRegion = idSplitArray[4];

				if (paymentsRegion.equalsIgnoreCase("INTERNATIONAL")) {
					acr = AccountCurrencyRegion.INTERNATIONAL;
				} else {
					acr = AccountCurrencyRegion.DOMESTIC;
				}

				List<Surcharge> surchargeToUpdate = surchargeDao
						.findPendingSurchargeListByPayIdAcquirerNameRegion(payId, paymentType, acquirer, mopType, acr);
				surchargeToUpdateVerify.addAll(surchargeToUpdate);
			}

			// Verify OTP for all

			for (Surcharge pendingSurcharge : surchargeToUpdateVerify) {

				if (pendingSurcharge.getOtpExpiryTime().compareTo(date) > 0
						&& pendingSurcharge.getOtp().equalsIgnoreCase(otp)) {

				} else {
					setResponse("Incorrect or expired OTP entered. Please generate new OTP and try again");
					return SUCCESS;
				}

			}

			for (Surcharge pendingSurcharge : surchargeToUpdateVerify) {

				if (StringUtils.isNotBlank(pendingSurcharge.getRequestedBy())) {
					User user = userDao.findPayIdByEmail(pendingSurcharge.getRequestedBy());
					if (StringUtils.isNotBlank(user.getMobile())) {
						mobileNumbers.add(user.getMobile());
					}
				}

			}
			if (StringUtils.isNotBlank(sessionUser.getMobile())) {
				mobileNumbers.add(sessionUser.getMobile());
			}

			for (String idSplitUnit : idSplit) {

				String[] idSplitArray = idSplitUnit.split(",");

				String paymentType = idSplitArray[0];
				String payId = idSplitArray[1];
				String mopType = idSplitArray[2];
				String acquirer = idSplitArray[3];
				String paymentsRegion = idSplitArray[4];

				if (paymentsRegion.equalsIgnoreCase("INTERNATIONAL")) {
					acr = AccountCurrencyRegion.INTERNATIONAL;
				} else {
					acr = AccountCurrencyRegion.DOMESTIC;
				}

				List<Surcharge> surchargeToUpdate = new ArrayList<Surcharge>();

				surchargeToUpdate = surchargeDao.findPendingSurchargeListByPayIdAcquirerNameRegion(payId, paymentType,
						acquirer, mopType, acr);

				if (surchargeToUpdate != null) {

					if (operation.equals("accept")) {
						List<Surcharge> activeSurchargeList = new ArrayList<Surcharge>();
						activeSurchargeList = surchargeDao.findSurchargeListByPayIdAcquirerNameRegion(payId,
								paymentType, acquirer, mopType, acr);

						if (activeSurchargeList != null) {

							for (Surcharge surcharge : activeSurchargeList) {
								updateSurcharge(surcharge, TDRStatus.INACTIVE);
							}
						}

						for (Surcharge surcharge : surchargeToUpdate) {
							updateSurcharge(surcharge, TDRStatus.ACTIVE);
						}
						setResponse("Bank Surcharge update request Approved and Updated");

						// Send Approved SMS
						StringBuilder SMSBodyAccept = new StringBuilder();
						SMSBodyAccept.append("Bank Surcharge update Request(s) processed and Approved for Id(s) "
								+ idArray + "  by  " + sessionUser.getEmailId());

						for (String mobile : mobileNumbers) {
							try {
								smsSender.sendSMS(mobile, SMSBodyAccept.toString());
							} catch (IOException e) {
								logger.error("Unable to send SMS");
							}
						}
						addAuditTrailEntry(surchargeToUpdate);
						return SUCCESS;

					} else {

						String[] idSplitReject = idArray.split(";");
						List<Surcharge> surchargeToReject = new ArrayList<Surcharge>();
						for (String idSplitUnitReject : idSplitReject) {

							String[] idSplitArrayReject = idSplitUnitReject.split(",");

							String paymentTypeReject = idSplitArrayReject[0];
							String payIdReject = idSplitArrayReject[1];
							String mopTypeReject = idSplitArrayReject[2];
							String acquirerReject = idSplitArrayReject[3];
							String paymentsRegionReject = idSplitArrayReject[4];

							if (paymentsRegionReject.equalsIgnoreCase("INTERNATIONAL")) {
								acr = AccountCurrencyRegion.INTERNATIONAL;
							} else {
								acr = AccountCurrencyRegion.DOMESTIC;
							}

							List<Surcharge> surchargeReject = surchargeDao
									.findPendingSurchargeListByPayIdAcquirerNameRegion(payIdReject, paymentTypeReject,
											acquirerReject, mopTypeReject, acr);
							surchargeToReject.addAll(surchargeReject);
						}

						for (Surcharge surcharge : surchargeToReject) {
							updateSurcharge(surcharge, TDRStatus.REJECTED);
						}
						setResponse("Bank Surcharge update request Rejected and Removed");

						// Send Rejected SMS
						StringBuilder SMSBodyReject = new StringBuilder();
						SMSBodyReject.append("Bank Surcharge update Request(s) rejected for Id(s) " + idArray + "  by  "
								+ sessionUser.getEmailId());

						for (String mobile : mobileNumbers) {
							try {
								smsSender.sendSMS(mobile, SMSBodyReject.toString());
							} catch (IOException e) {
								logger.error("Exception in sending SMS ");
							}
						}
						addAuditTrailEntry(surchargeToReject);
						return SUCCESS;
					}

				}
			}

		}

		else {
			setResponse("Bank Surcharge update request denied , only Authorized User can Approve / Reject request");
			return SUCCESS;

		}

		return SUCCESS;
	}

	@SuppressWarnings("unchecked")
	private void addAuditTrailEntry(List<Surcharge> payload) {
		Map<String, ActionMessageByAction> actionMessagesByAction = (Map<String, ActionMessageByAction>) sessionMap
				.get(Constants.ACTION_MESSAGE_BY_ACTION.getValue());
		try {
			AuditTrail auditTrail = new AuditTrail(mapper.writeValueAsString(payload), null,
					actionMessagesByAction.get("approveRejectBankSurcharge"));
			auditTrailService.saveAudit(request, auditTrail);
		} catch (JsonProcessingException e1) {
			logger.warn("Exception: ", e1);
		}
	}

	public void updateSurcharge(Surcharge surcharge, TDRStatus status) {

		try {

			Session session = null;
			session = HibernateSessionProvider.getSession();
			Transaction tx = session.beginTransaction();
			Long id = surcharge.getId();
			session.load(surcharge, surcharge.getId());
			Surcharge sch = session.get(Surcharge.class, id);
			sch.setStatus(status);
			sch.setUpdatedDate(currentDate);
			sch.setProcessedBy(sessionUser.getEmailId());
			session.update(sch);
			tx.commit();
			session.close();

		} catch (HibernateException e) {
			logger.error("Exception: " + e);
		} finally {

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
