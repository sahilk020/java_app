package com.pay10.crm.action;

import java.util.Date;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.pay10.commons.dao.HibernateSessionProvider;
import com.pay10.commons.user.ChargingDetails;
import com.pay10.commons.user.ChargingDetailsDao;
import com.pay10.commons.user.UserDao;
import com.pay10.commons.util.CrmFieldType;
import com.pay10.commons.util.CrmValidator;
import com.pay10.commons.util.PendingRequestEmailProcessor;
import com.pay10.commons.util.TDRStatus;

/**
 * @author Shaiwal
 *
 */
public class TDRApproveRejectAction extends AbstractSecureAction {

	@Autowired
	private ChargingDetailsDao chargingDetailsDao;

	@Autowired
	private PendingRequestEmailProcessor pendingRequestEmailProcessor;

	@Autowired
	private UserDao userDao;

	@Autowired
	private CrmValidator validator;

	private static Logger logger = LoggerFactory.getLogger(TDRApproveRejectAction.class.getName());
	private static final long serialVersionUID = -6517340843571949786L;

	private Long id;
	private String emailId;
	private String userType;
	private String operation;
	private Date currentDate = new Date();

	@Override
	public String execute() {
		try {
			ChargingDetails chargingDetailsToUpdate = new ChargingDetails();
			ChargingDetails activeChargingDetail = new ChargingDetails();

			chargingDetailsToUpdate = chargingDetailsDao.find(id);
			activeChargingDetail = chargingDetailsDao.findActiveChargingDetail(chargingDetailsToUpdate.getMopType(),
					chargingDetailsToUpdate.getPaymentType(), chargingDetailsToUpdate.getTransactionType(),
					chargingDetailsToUpdate.getAcquirerName(), chargingDetailsToUpdate.getCurrency(),
					chargingDetailsToUpdate.getPayId());

			if (chargingDetailsToUpdate != null) {

				String merchantName = userDao.getBusinessNameByPayId(chargingDetailsToUpdate.getPayId());
				if (operation.equals("accept")) {

					if (activeChargingDetail != null) {
						updateChargingDetails(activeChargingDetail, TDRStatus.INACTIVE);
					}
					updateChargingDetails(chargingDetailsToUpdate, TDRStatus.ACTIVE);
					// pendingRequestEmailProcessor.processTDRApproveRejectEmail(TDRStatus.ACTIVE.getName(),
					// emailId,
					// userType, merchantName, chargingDetailsToUpdate.getPayId(),
					// chargingDetailsToUpdate.getRequestedBy());

				} else {
					updateChargingDetails(chargingDetailsToUpdate, TDRStatus.REJECTED);
					// pendingRequestEmailProcessor.processTDRApproveRejectEmail(TDRStatus.REJECTED.getName(),
					// emailId,
					// userType, merchantName, chargingDetailsToUpdate.getPayId(),
					// chargingDetailsToUpdate.getRequestedBy());
				}

			}
			return SUCCESS;
		}

		catch (Exception e) {
			logger.error("Exception " + e);
			return ERROR;
		}

	}

	@Override
	public void validate() {
		if ((validator.validateBlankField(getUserType()))) {
			addFieldError(CrmFieldType.USER_TYPE.getName(), validator.getResonseObject().getResponseMessage());
		} else if (!(validator.validateField(CrmFieldType.USER_TYPE, getUserType()))) {
			addFieldError(CrmFieldType.USER_TYPE.getName(), validator.getResonseObject().getResponseMessage());
		}
		if ((validator.validateBlankField(getEmailId()))) {
			addFieldError(CrmFieldType.EMAILID.getName(), validator.getResonseObject().getResponseMessage());
		} else if (!(validator.validateField(CrmFieldType.EMAILID, getEmailId()))) {
			addFieldError(CrmFieldType.EMAILID.getName(), validator.getResonseObject().getResponseMessage());
		}
		if ((validator.validateBlankField(getOperation()))) {
			addFieldError(CrmFieldType.OPERATION.getName(), validator.getResonseObject().getResponseMessage());
		} else if (!(validator.validateField(CrmFieldType.OPERATION, getOperation()))) {
			addFieldError(CrmFieldType.OPERATION.getName(), validator.getResonseObject().getResponseMessage());
		}

	}

	public void updateChargingDetails(ChargingDetails chargingDetails, TDRStatus status) {

		try {

			Session session = null;
			session = HibernateSessionProvider.getSession();
			Transaction tx = session.beginTransaction();
			Long id = chargingDetails.getId();
			session.load(chargingDetails, chargingDetails.getId());
			ChargingDetails cd = session.get(ChargingDetails.class, id);
			cd.setStatus(status);
			cd.setUpdatedDate(currentDate);
			session.update(cd);
			tx.commit();
			session.close();

		} catch (HibernateException e) {
			logger.error("Exception " + e);
		} finally {

		}

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

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

}
