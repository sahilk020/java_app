package com.pay10.notification.txnpush.generator;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger; import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.exception.SystemException;
import com.pay10.commons.user.PermissionsDao;
import com.pay10.commons.user.User;
import com.pay10.commons.user.UserDao;
import com.pay10.commons.user.UserType;
import com.pay10.commons.util.NotificationStatusType;
import com.pay10.notification.txnpush.Controller.NotificationTxnPushEmailController;
import com.pay10.notification.txnpush.pushtxnemailbuilder.NotificationDao;
import com.pay10.notification.txnpush.pushtxnemailbuilder.NotificationDetail;

/**
 * @author Shaiwal
 *
 */

public class NotificationGenerator {
	
	@Autowired
	private NotificationDao notificationDao;
	
	@Autowired
	private PermissionsDao permissionsDao;
	
	@Autowired
	private UserDao userDao;
	
	@Autowired
	private NotificationTxnPushEmailController notificationTxnPushEmailController;
	

	private final Logger logger = LoggerFactory.getLogger(NotificationGenerator.class.getName());

	public void updateRequestNotification(String subject, String loginEmailId, String Message) {

		try {

			String payId = userDao.getPayIdByEmailId(loginEmailId);
			User loginUser = new User();
			String notifierEmailId = null;
			if (payId != null) {
				loginUser = userDao.findPayId(payId);
			} else {
				throw new SystemException(ErrorType.USER_NOT_FOUND, "User Not found");
			}

			// Get logged in users' parent User
			String parentPayId = loginUser.getParentPayId();
			User parentUser = userDao.findPayId(parentPayId);

			// Get permissions granted to parent user
			List<String> permissionsList = new ArrayList<String>();
			// permissionsList = new

			permissionsList = permissionsDao.getAllPermissionsByEmailId(parentUser.getEmailId());

			// If parent user is the admin , raise notification for Admin
			if (parentUser.getUserType().toString().equalsIgnoreCase("ADMIN")) {
				notifierEmailId = parentUser.getEmailId();
				
			}

			// If parent user has the permissions , send notification to that
			// user
			else if (checkPermissionToApprove(permissionsList, subject)) {
				notifierEmailId = parentUser.getEmailId();
			}

			// Else send notification to parent of parent user , i.e Admin
			else {
				User parentParentUser = userDao.findPayId(parentUser.getParentPayId());
				notifierEmailId = parentParentUser.getEmailId();
			}

			Date currentDate = new Date();
			NotificationDetail notificationDetail = new NotificationDetail();
			notificationDetail.setCreateDate(currentDate);
			notificationDetail.setSubmittedBy(loginEmailId);
			notificationDetail.setNotifierEmailId(notifierEmailId);
			notificationDetail.setStatus(NotificationStatusType.UNREAD.getName());
			notificationDetail.setMessage(Message);

			notificationDao.create(notificationDetail);
           //TODO send Email 
			notificationTxnPushEmailController.updateRequestNotificationEmail(notificationDetail);
			
			
		} catch (Exception exception) {
			logger.error("error" + exception);
		}
	}

	@SuppressWarnings("unused")
	public void updateApproveRejectNotificationForServiceTax(User user, String Message, String businessType,
			String requestedBy, String result) {

		if (result.equalsIgnoreCase("accept")) {

			List<String> usersToBeNotified = new ArrayList<String>();
			usersToBeNotified = userDao.getMerchantEmailIdListByBusinessType(businessType);

			if (user.getUserType().equals(UserType.ADMIN)) {
				usersToBeNotified.add(requestedBy);
			}

			else if (user.getUserType().equals(UserType.SUBADMIN)) {
				String parentAdminPayId = user.getParentPayId();
				String parentAdminEmailId = userDao.getEmailIdByPayId(parentAdminPayId);
				usersToBeNotified.add(parentAdminEmailId);
				usersToBeNotified.add(requestedBy);
			} else if (user.getUserType().equals(UserType.ASSOCIATE)) {
				String parentSubAdminPayId = user.getParentPayId();
				User parentSubAdmin = userDao.findPayId(parentSubAdminPayId);
				String parentSubAdminEmailId = parentSubAdmin.getEmailId();

				User parentAdmin = userDao.findPayId(parentSubAdmin.getParentPayId());
				String parentAdminEmailId = parentAdmin.getEmailId();

				usersToBeNotified.add(parentSubAdminEmailId);
				usersToBeNotified.add(parentAdminEmailId);

			}

			for (String emailId : usersToBeNotified) {

				Date currentDate = new Date();
				NotificationDetail notificationDetail = new NotificationDetail();
				notificationDetail.setCreateDate(currentDate);
				notificationDetail.setSubmittedBy(user.getEmailId());
				notificationDetail.setNotifierEmailId(requestedBy);
				notificationDetail.setStatus(NotificationStatusType.UNREAD.getName());
				notificationDetail.setMessage(Message);

				notificationDao.create(notificationDetail);
			    //TODO send Email 
				notificationTxnPushEmailController.updateApproveRejectNotificationForServiceTaxEmail(notificationDetail);
			}
		}

		else {

			Date currentDate = new Date();
			NotificationDetail notificationDetail = new NotificationDetail();
			notificationDetail.setCreateDate(currentDate);
			notificationDetail.setSubmittedBy(null);
			notificationDetail.setNotifierEmailId(user.getEmailId());
			notificationDetail.setStatus(NotificationStatusType.UNREAD.getName());
			notificationDetail.setMessage(Message);

			notificationDao.create(notificationDetail);
		    //TODO send Email 
		   notificationTxnPushEmailController.updateApproveRejectNotificationForServiceTaxEmail(notificationDetail);
		}
	}

	public boolean checkPermissionToApprove(List<String> permissionsList, String subject) {

		boolean isAllowed = false;

		for (String permission : permissionsList) {
			if (permission.equals(subject)) {
				isAllowed = true;
			}
		}

		return isAllowed;
	}
}
