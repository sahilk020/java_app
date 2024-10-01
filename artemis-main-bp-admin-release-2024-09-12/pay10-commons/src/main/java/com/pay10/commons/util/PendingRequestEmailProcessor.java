package com.pay10.commons.util;

import org.slf4j.Logger; import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pay10.commons.api.EmailControllerServiceProvider;
import com.pay10.commons.dao.ServiceTaxDao;
import com.pay10.commons.user.User;
import com.pay10.commons.user.UserDao;
import com.pay10.commons.user.UserType;

@Service
public class PendingRequestEmailProcessor {

	@Autowired
	private EmailControllerServiceProvider emailControllerServiceProvider;

	@Autowired
	private UserDao userDao;

	@Autowired
	private ServiceTaxDao serviceTaxDao;

	private String parentEmailId;
	private String body;

	PropertiesManager propertiesManager = new PropertiesManager();
	private static Logger logger = LoggerFactory.getLogger(PendingRequestEmailProcessor.class.getName());

	public void processServiceTaxEmail(String requestStatus, String loginEmailId, String loginUserType,
			String businessType) {

		logger.info("Process email");
		User user = new User();
		user = userDao.findPayIdByEmail(loginEmailId);
		String subject = "Service tax update notification";
		if (loginUserType.equals(UserType.SUBADMIN.toString())) {
			String parentUser = userDao.getEmailIdByPayId(user.getParentPayId());
			setParentEmailId(parentUser);
		}

		switch (requestStatus) {
		case "Active":
			setBody(propertiesManager.getPendingMessages("ServiceTaxUpdated") + businessType);

			if (loginUserType.equals(UserType.SUBADMIN.toString())) {

				logger.info("Process email active , parentEmailid = " + getParentEmailId() + "  subject = " + subject
						+ "     body = " + getBody());
				emailControllerServiceProvider.emailPendingRequest(getParentEmailId(), subject, getBody());
			} else {

			}
			break;

		case "Pending":
			setBody(propertiesManager.getPendingMessages("ServiceTaxUpdateRequest") + businessType);
			logger.info("Process email pending , parentEmailid = " + getParentEmailId() + "  subject = " + subject
					+ "     body = " + getBody());
			emailControllerServiceProvider.emailPendingRequest(getParentEmailId(), subject, getBody());

			break;

		}
	}

	public void processServiceTaxApproveRejectEmail(String requestStatus, String loginEmailId, String loginUserType,
			String businessType, String requestedByEmailId) {

		logger.info("processServiceTaxApproveRejectEmails");

		String subject = "Service Tax Update notification";
		logger.info("processServiceTaxApproveRejectEmails requested by " + requestedByEmailId);

		switch (requestStatus) {

		case "Approved":
			setBody(propertiesManager.getPendingMessages("ServiceTaxUpdateApprove") + businessType + " by Admin.");

			logger.info("Process email active , requestedByEmailId = " + requestedByEmailId + "  subject = " + subject
					+ "     body = " + getBody());
			emailControllerServiceProvider.emailPendingRequest(requestedByEmailId, subject, getBody());

			break;

		case "Rejected":
			setBody(propertiesManager.getPendingMessages("ServiceTaxUpdateReject") + businessType + " by Admin.");
			logger.info("Process email pending , parentEmailid = " + loginEmailId + "  subject = " + subject
					+ "     body = " + getBody());

			logger.info("processServiceTaxApproveRejectEmails requested by " + requestedByEmailId);
			emailControllerServiceProvider.emailPendingRequest(requestedByEmailId, subject, getBody());

			break;

		}
	}
	
	public void processTDRRequestEmail(String requestStatus, String loginEmailId, String loginUserType,
			String merchantName ,String merchantPayId) {

		logger.info("processTDRRequestEmail");
		User user = new User();
		user = userDao.findPayIdByEmail(loginEmailId);
		String subject = "Merchant TDR Update Notification";
		if (loginUserType.equals(UserType.SUBADMIN.toString())) {
			String parentUser = userDao.getEmailIdByPayId(user.getParentPayId());
			setParentEmailId(parentUser);
		}

		switch (requestStatus) {
		case "ACTIVE":
			setBody(propertiesManager.getPendingMessages("TDRUpdated") + " "+merchantName +" with Pay Id "+merchantPayId + " by "+loginEmailId);

			if (loginUserType.equals(UserType.SUBADMIN.toString())) {

				emailControllerServiceProvider.emailPendingRequest(getParentEmailId(), subject, getBody());
			} else {

			}
			break;

		case "PENDING":
			setBody(propertiesManager.getPendingMessages("TDRUpdateRequest") + " "+merchantName +" with Pay Id "+merchantPayId + " by Sub admin "+loginEmailId);
			logger.info("getParentEmailId() " + getParentEmailId() +" subject "+ subject +" getBody() "+ getBody());
			emailControllerServiceProvider.emailPendingRequest(getParentEmailId(), subject, getBody());

			break;

		}
	}

	public void processMappingEmail(String requestStatus, String loginEmailId, String loginUserType,
			String merchantEmailId) {

		logger.info(" processTDREmail");
		User user = new User();
		User merchant = new User();
		user = userDao.findPayIdByEmail(loginEmailId);
		merchant = userDao.findPayIdByEmail(merchantEmailId);
		String subject = "Merchant Mapping Update Notification";

		if (loginUserType.equals(UserType.SUBADMIN.toString())) {
			String parentUser = userDao.getEmailIdByPayId(user.getParentPayId());
			setParentEmailId(parentUser);
		}

		switch (requestStatus) {
		case "Active":

			if (loginUserType.equals(UserType.SUBADMIN.toString())) {

				setBody(propertiesManager.getPendingMessages("MerchantMappingUpdated") + merchant.getBusinessName() + " by Sub Admin "
						+ loginEmailId);
				emailControllerServiceProvider.emailPendingRequest(getParentEmailId(), subject, getBody());

			} else if (loginUserType.equals(UserType.ADMIN.toString())) {

				setBody(propertiesManager.getPendingMessages("MerchantMappingUpdated") + merchant.getBusinessName() + "by Admin");
				emailControllerServiceProvider.emailPendingRequest(getParentEmailId(), subject, getBody());
			}

			break;

		case "Pending":
			setBody(propertiesManager.getPendingMessages("MerchantMappingUpdateRequest") + merchant.getBusinessName() + " by Sub Admin "
					+ loginEmailId);
			logger.info("Process email pending , parentEmailid = " + getParentEmailId() + "  subject = " + subject
					+ "     body = " + getBody());
			emailControllerServiceProvider.emailPendingRequest(getParentEmailId(), subject, getBody());

			break;

		}
	}

	public void processTDRApproveRejectEmail(String requestStatus, String loginEmailId, String loginUserType,
			String merchantName, String merchantPayId, String requestedByEmailId) {

		logger.info("inside processTDRApproveRejectEmail");

		String subject = "Merchant TDR Update notification";
		logger.info("processTDRApproveRejectEmail requested by " + requestedByEmailId);

		switch (requestStatus) {

		case "Active":
			setBody(propertiesManager.getPendingMessages("TDRUpdateApprove") + " "+merchantName + " with payId "
					+ merchantPayId + " by Admin.");
			emailControllerServiceProvider.emailPendingRequest(requestedByEmailId, subject, getBody());

			break;

		case "Rejected":
			setBody(propertiesManager.getPendingMessages("TDRUpdateReject") + " "+merchantName + " with payId "
					+ merchantPayId + " by Admin.");
			emailControllerServiceProvider.emailPendingRequest(requestedByEmailId, subject, getBody());

			break;

		}
	}
	
	
	public void processMappingApproveRejectEmail(String requestStatus, String loginEmailId, String loginUserType,
			String merchantName, String merchantPayId, String requestedByEmailId) {

		logger.info("inside processMappingApproveRejectEmail");

		String subject = "Merchant Mapping Details Update Notification";
		logger.info("processMappingApproveRejectEmail requested by " + requestedByEmailId);

		switch (requestStatus) {

		case "Approved":
			setBody(propertiesManager.getPendingMessages("MerchantMappingUpdateApprove") + " "+merchantName + " with payId "
					+ merchantPayId + " by Admin.");
			emailControllerServiceProvider.emailPendingRequest(requestedByEmailId, subject, getBody());

			break;

		case "Rejected":
			setBody(propertiesManager.getPendingMessages("MerchantMappingUpdateReject") + " "+merchantName + " with payId "
					+ merchantPayId + " by Admin.");
			emailControllerServiceProvider.emailPendingRequest(requestedByEmailId, subject, getBody());

			break;

		}
	}
	
	public void processMerchantSurchargeRequestEmail(String requestStatus, String loginEmailId, String loginUserType,
			String merchantName ,String merchantPayId) {

		User user = new User();
		user = userDao.findPayIdByEmail(loginEmailId);
		String subject = "Merchant Surcharge Update";
		if (loginUserType.equals(UserType.SUBADMIN.toString())) {
			String parentUser = userDao.getEmailIdByPayId(user.getParentPayId());
			setParentEmailId(parentUser);
		}

		switch (requestStatus) {
		case "Active":
			setBody(propertiesManager.getPendingMessages("MerchantSurchargeUpdated") + " "+merchantName +" with Pay Id "+merchantPayId + " by "+loginEmailId);

			if (loginUserType.equals(UserType.SUBADMIN.toString())) {

				emailControllerServiceProvider.emailPendingRequest(getParentEmailId(), subject, getBody());
			} else {

			}
			break;

		case "Pending":
			setBody(propertiesManager.getPendingMessages("MerchantSurchargeUpdateRequest") + "  "+merchantName +"  with Pay Id  "+merchantPayId + " by Sub admin "+loginEmailId);
			logger.info("getParentEmailId() " + getParentEmailId() +" subject "+ subject +" getBody() "+ getBody());
			emailControllerServiceProvider.emailPendingRequest(getParentEmailId(), subject, getBody());

			break;

		}
	}
	
	
	public void processMerchantSurchargeApproveRejectEmail(String requestStatus, String loginEmailId, String loginUserType,
			String merchantName, String merchantPayId, String requestedByEmailId) {


		String subject = "Merchant Surcharge Update Notification";

		switch (requestStatus) {

		case "Approved":
			setBody(propertiesManager.getPendingMessages("MerchantSurchargeUpdateApprove") + "  "+merchantName + "  with payId "
					+ merchantPayId + " by Admin.");
			emailControllerServiceProvider.emailPendingRequest(requestedByEmailId, subject, getBody());

			break;

		case "Rejected":
			setBody(propertiesManager.getPendingMessages("MerchantSurchargeUpdateReject") + "  "+merchantName + "  with payId  "
					+ merchantPayId + " by Admin.");
			emailControllerServiceProvider.emailPendingRequest(requestedByEmailId, subject, getBody());

			break;

		}
	}
	
	
	public void processBankSurchargeRequestEmail(String requestStatus, String loginEmailId, String loginUserType,
			String merchantName ,String merchantPayId) {

		User user = new User();
		user = userDao.findPayIdByEmail(loginEmailId);
		String subject = "Bank Surcharge Update";
		if (loginUserType.equals(UserType.SUBADMIN.toString())) {
			String parentUser = userDao.getEmailIdByPayId(user.getParentPayId());
			setParentEmailId(parentUser);
		}

		switch (requestStatus) {
		case "Active":
			setBody(propertiesManager.getPendingMessages("BankSurchargeUpdated") + " "+merchantName +" with Pay Id "+merchantPayId + " by "+loginEmailId);

			if (loginUserType.equals(UserType.SUBADMIN.toString())) {

				emailControllerServiceProvider.emailPendingRequest(getParentEmailId(), subject, getBody());
			} else {

			}
			break;

		case "Pending":
			setBody(propertiesManager.getPendingMessages("BankSurchargeUpdateRequest") + "  "+merchantName +"  with Pay Id  "+merchantPayId + " by Sub admin "+loginEmailId);
			logger.info("getParentEmailId() " + getParentEmailId() +" subject "+ subject +" getBody() "+ getBody());
			emailControllerServiceProvider.emailPendingRequest(getParentEmailId(), subject, getBody());

			break;

		}
	}
	
	
	public void processBankSurchargeApproveRejectEmail(String requestStatus, String loginEmailId, String loginUserType,
			String merchantName, String merchantPayId, String requestedByEmailId) {


		String subject = "Bank Surcharge Update Notification";

		switch (requestStatus) {

		case "Approved":
			setBody(propertiesManager.getPendingMessages("BankSurchargeUpdateApprove") + "  "+merchantName + "  with payId "
					+ merchantPayId + " by Admin.");
			emailControllerServiceProvider.emailPendingRequest(requestedByEmailId, subject, getBody());

			break;

		case "Rejected":
			setBody(propertiesManager.getPendingMessages("BankSurchargeUpdateReject") + "  "+merchantName + "  with payId  "
					+ merchantPayId + " by Admin.");
			emailControllerServiceProvider.emailPendingRequest(requestedByEmailId, subject, getBody());

			break;

		}
	}

	public String getParentEmailId() {
		return parentEmailId;
	}

	public void setParentEmailId(String parentEmailId) {
		this.parentEmailId = parentEmailId;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

}
