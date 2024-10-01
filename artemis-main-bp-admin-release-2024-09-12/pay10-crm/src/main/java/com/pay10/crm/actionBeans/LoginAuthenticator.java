package com.pay10.crm.actionBeans;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.exception.SystemException;
import com.pay10.commons.user.LoginHistory;
import com.pay10.commons.user.LoginHistoryDao;
import com.pay10.commons.user.LoginOtp;
import com.pay10.commons.user.LoginOtpDao;
import com.pay10.commons.user.ResponseObject;
import com.pay10.commons.user.User;
import com.pay10.commons.user.UserDao;
import com.pay10.commons.util.UserStatusType;

/**
 * @author Puneet
 *
 */
@Service
public class LoginAuthenticator {

	@Autowired
	@Qualifier("userDao")
	private UserDao userDao;

	@Autowired
	private LoginOtpDao otpDao;

	@Autowired
	private LoginHistoryDao loginHistoryDao;

	private User user = null;

	public ResponseObject authenticate(String emailId, String password, String agent, String ip)
			throws SystemException {

		ResponseObject responseObject = new ResponseObject();
		boolean status;
		String failureReason = null;

		//user = userDao.find(emailId);
		user = userDao.findByEmailId(emailId);

		if (null == user) {
			// If user is not found, userid is invalid
			responseObject.setResponseMessage(ErrorType.USER_NOT_FOUND.getResponseMessage());
			responseObject.setResponseCode(ErrorType.USER_NOT_FOUND.getResponseCode());
			return responseObject;
		}

		UserStatusType userStatus = user.getUserStatus();

		// Userid is valid
		if ((userStatus.equals(UserStatusType.PENDING) || userStatus.equals(UserStatusType.TERMINATED))) {
			responseObject.setResponseMessage(ErrorType.USER_INACTIVE.getResponseMessage());
			responseObject.setResponseCode(ErrorType.USER_INACTIVE.getResponseCode());

			status = false;
			failureReason = ErrorType.USER_INACTIVE.getInternalMessage();
			loginHistoryDao.saveLoginDetails(agent, status, user, ip, failureReason);
			return responseObject;
		}

		password = PasswordHasher.hashPassword(password, user.getPayId());
		System.out.println("password in Login Authentication......"+password);
		String userDBPassword = user.getPassword();
		System.out.println("userDBPassword in Login Authentication......"+userDBPassword);
		if (StringUtils.isEmpty(userDBPassword)) {
			status = false;
			failureReason = ErrorType.USER_PASSWORD_NOT_SET.getInternalMessage();
			responseObject.setResponseMessage(ErrorType.USER_PASSWORD_NOT_SET.getResponseMessage());
			responseObject.setResponseCode(ErrorType.USER_PASSWORD_NOT_SET.getResponseCode());
		} else if (userDBPassword.equals(password)) {
			status = true;
			responseObject.setResponseCode(ErrorType.SUCCESS.getResponseCode());
		} else {
			status = false;
			failureReason = ErrorType.USER_PASSWORD_INCORRECT.getInternalMessage();
			responseObject.setResponseMessage(ErrorType.USER_PASSWORD_INCORRECT.getResponseMessage());
			responseObject.setResponseCode(ErrorType.USER_PASSWORD_INCORRECT.getResponseCode());
		}

		// check for user login attempt and for lock the account
		String respoMessage = loginHistoryDao.findLastLoginHisByEmail(user.getEmailId(), responseObject);

		if (!respoMessage.equals("fail") && !respoMessage.contains("Incorrect Username or Password")) {

			failureReason = ErrorType.USER_ACCOUNT_LOCK.getInternalMessage();
			responseObject.setResponseMessage(respoMessage);
			responseObject.setResponseCode(ErrorType.USER_ACCOUNT_LOCK.getResponseCode());
			String failReason = loginHistoryDao.findLastLoginRecord(user.getEmailId());
			if (!failReason.contains("Account Lock")) {
				status = true;
				loginHistoryDao.saveLoginDetails(agent, status, user, ip, failureReason);
			}

		} else {
			if (respoMessage.contains("Incorrect Username or Password")) {
				failureReason = respoMessage;
				responseObject.setResponseMessage(failureReason);
				responseObject.setResponseCode(ErrorType.USER_PASSWORD_INCORRECT.getResponseCode());
			}
			loginHistoryDao.saveLoginDetails(agent, status, user, ip, failureReason);
		}

		return responseObject;
	}

	public ResponseObject authenticateOtp(String otp, String emailId, String agent, String ip) {
		ResponseObject responseObject = new ResponseObject();
		boolean status;
		String failureReason = null;

		user = userDao.find(emailId);

		if (null == user) {
			// If user is not found, userid is invalid
			responseObject.setResponseMessage(ErrorType.USER_NOT_FOUND.getResponseMessage());
			responseObject.setResponseCode(ErrorType.USER_NOT_FOUND.getResponseCode());
			return responseObject;
		}

		// Userid is valid
		if (!(user.getUserStatus().equals(UserStatusType.ACTIVE)
				|| user.getUserStatus().equals(UserStatusType.TRANSACTION_BLOCKED)
				|| user.getUserStatus().equals(UserStatusType.SUSPENDED))
				|| user.getUserStatus().equals(UserStatusType.PENDING)) {
			responseObject.setResponseMessage(ErrorType.USER_INACTIVE.getResponseMessage());
			responseObject.setResponseCode(ErrorType.USER_INACTIVE.getResponseCode());

			status = false;
			failureReason = ErrorType.USER_INACTIVE.getInternalMessage();
			loginHistoryDao.saveLoginDetails(agent, status, user, ip, failureReason);
			return responseObject;
		}
		try {
			LoginOtp loginOtp = otpDao.checkExpireOtp(otp, emailId);
			if (loginOtp != null) {
				DateFormat df = new SimpleDateFormat("dd/MM/yy HH:mm:ss");
				Calendar calobj = Calendar.getInstance();
				String startTime = df.format(calobj.getTime());
				String expiryTime = loginOtp.getExpiryDate();
				Date d1 = null;
				Date d2 = null;
				try {
					d1 = df.parse(startTime);
					d2 = df.parse(expiryTime);
				} catch (Exception e) {
					e.printStackTrace();
				}
				long diff = d2.getTime() - d1.getTime();
				long diffMinutes = diff / (60 * 1000) % 60;
				long diffHours = diff / (60 * 60 * 1000);
				if (diffHours >= 0 && diffMinutes >= 0) {
					status = true;
					responseObject.setResponseCode(ErrorType.SUCCESS.getResponseCode());
					loginOtp.setStatus("InActive");
					otpDao.update(loginOtp);
				} else {
					loginOtp.setStatus("InActive");
					otpDao.update(loginOtp);
					status = false;
					responseObject.setResponseMessage(ErrorType.EXPIRED_OTP.getResponseMessage());
					responseObject.setResponseCode(ErrorType.EXPIRED_OTP.getResponseCode());
				}
			} else {
				status = false;
				failureReason = ErrorType.OTP_NOT_SET.getInternalMessage();

				responseObject.setResponseMessage(ErrorType.INACTIVE_OTP.getResponseMessage());
				responseObject.setResponseCode(ErrorType.INACTIVE_OTP.getResponseCode());
			}
		} catch (Exception ex) {

			status = false;
			failureReason = ErrorType.OTP_NOT_SET.getInternalMessage();

			responseObject.setResponseMessage(ErrorType.INACTIVE_OTP.getResponseMessage());
			responseObject.setResponseCode(ErrorType.INACTIVE_OTP.getResponseCode());
		}

		loginHistoryDao.saveLoginDetails(agent, status, user, ip, failureReason);
		return responseObject;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

}
