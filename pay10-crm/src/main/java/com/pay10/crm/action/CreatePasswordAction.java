package com.pay10.crm.action;

import java.util.Calendar;
import java.util.Date;
import javax.servlet.http.HttpServletRequest;

import com.pay10.commons.user.*;
import com.pay10.commons.util.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.validation.SkipValidation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.pay10.commons.api.EmailControllerServiceProvider;
import com.pay10.commons.api.Hasher;
import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.exception.SystemException;
import com.pay10.crm.actionBeans.CheckOldPassword;

/**
 * @author Neeraj
 */
public class CreatePasswordAction extends AbstractSecureAction implements ServletRequestAware {

	private static final int emailExpiredInMinute = ConfigurationConstants.EMAIL_EXPIRED_MINUTE.getValues();

	@Autowired
	MerchantKeySaltService merchantKeySaltService;
	@Autowired
	private UserDao userDao;

	@Autowired
	private CrmValidator validator;

	@Autowired
	private UserRecordsDao userRecordsDao;

	@Autowired
	private CheckOldPassword checkOldPassword;

	private static Logger logger = LoggerFactory.getLogger(CreatePasswordAction.class.getName());
	private static final long serialVersionUID = 4184065113906121002L;
	@Autowired
	private PropertiesManager propertiesManager;

	private String emailId;
	private String id;
	private String payId;
	private String newPassword;
	private String confirmNewPassword;
	private String response;
	private String errorMessage;
	private String errorCode;
	private String captcha;
	private HttpServletRequest request;



	//Currently not used
	@SkipValidation
	public String createPassword() {
		User user = new User();
		try {
			user = userDao.findByAccountValidationKey(getId());

			if (user == null) {
				return ERROR;
			}
			if (user.isEmailValidationFlag()) {
				addActionMessage(ErrorType.ALREADY_PASSWORD_RESET.getResponseMessage());
				return "reset";
			}
			Date expiryTime = user.getEmailExpiryTime();
			Date currentTime = new Date();
			int result = currentTime.compareTo(expiryTime);

			if (result < 0 || result == 0) {
				setPayId(user.getPayId());
			} else {
				logger.error("Email link has been expired");
				return "linkExpired";
			}
			return SUCCESS;
		} catch (Exception exception) {
			logger.error("Exception", exception);
			return ERROR;
		}
	}
	public String createUserPassword() {
		User user = new User();
		if(StringUtils.isNotBlank(id)) {
			user = userDao.findByAccountValidationKey(getId());
		}
		try {
			if (!validateFields(user)) {
				logger.info("validation success");
				if (user == null) {
					return ERROR;
				}
				//If user is not active do not allow password set
				if(!(user.getUserStatus().equals(UserStatusType.ACTIVE))){
					setResponse(ErrorType.USER_STATUS.getResponseMessage());
					return SUCCESS;
				}
				userRecordsDao.createDetails(user.getEmailId(), user.getPassword(), user.getPayId());
				String salt = propertiesManager.getSalt(user.getPayId());
				if(salt == null){
					salt = merchantKeySaltService.getSalt(user.getPayId());
				}
				if (null == salt) {
					throw new SystemException(ErrorType.AUTHENTICATION_UNAVAILABLE,
							ErrorType.AUTHENTICATION_UNAVAILABLE.getResponseCode());
				}
				String hashedPassword = Hasher.getHash(getNewPassword().concat(salt));
				user.setPassword(hashedPassword);
				user.setEmailValidationFlag(true);
				userDao.update(user);
				setResponse(ErrorType.PASSWORD_SET.getResponseMessage());
				setErrorCode(ErrorType.PASSWORD_SET.getCode());
				logger.info("Alert is ----> "+ ErrorType.PASSWORD_SET.getResponseMessage());
			} else {
				setResponse(getErrorMessage());
			}

			return SUCCESS;
		} catch (Exception exception) {
			logger.error("Exception", exception);
			return SUCCESS;
		}
	}

	public boolean validateEmail() {
		if (validator.validateBlankField(getCaptcha())) {
			setErrorMessage(ErrorType.INVALID_CAPTCHA.getResponseMessage());
		} else {
			String sessionCaptcha = (String) sessionMap.get(CaptchaServlet.CAPTCHA_KEY);
			if (!captcha.equalsIgnoreCase(sessionCaptcha)) {
				setCaptcha("");
				setErrorMessage(ErrorType.INVALID_CAPTCHA.getResponseMessage());
				return true;
			}
		}

		// Validate blank and invalid fields
		if (validator.validateBlankField(getEmailId())) {
			setErrorMessage(validator.getResonseObject().getResponseMessage());
			return true;
		} else if (!(validator.isValidEmailId(getEmailId()))) {
			setErrorMessage(ErrorType.INVALID_EMAIL_ID.getResponseMessage());
			return true;
		}
		return false;
	}

	public boolean validateFields(User user) throws SystemException {

		// check fields have valid and match confirm password with new password
		if (validator.validateBlankField(getNewPassword())) {
			setErrorMessage(ErrorType.NEW_PASSWORD.getResponseMessage());
			return true;
		} else if (validator.validateBlankField(getConfirmNewPassword())) {
			setErrorMessage(ErrorType.CONFIRM_NEW_PASSWORD.getResponseMessage());
			return true;
		} else if (!(getNewPassword().equals(getConfirmNewPassword()))) {
			setErrorMessage(ErrorType.PASSWORD_MISMATCH.getResponseMessage());
			return true;
		} else if (!(validator.isValidPasword(getNewPassword()))) {
			setErrorMessage(ErrorType.INVALID_FIELD_VALUE.getResponseMessage());
			return true;
		}

		if (user != null && user.getPassword() != null && !user.getPassword().isEmpty()) {

			if (newPassword.equals(user.getPassword())) {
				setErrorMessage(ErrorType.OLD_PASSWORD_MATCH.getResponseMessage());
				return true;
			}
		}

		if (user != null && checkOldPassword.isUsedPassword(newPassword, user.getEmailId())) {
			setErrorMessage(ErrorType.OLD_PASSWORD_MATCH.getResponseMessage());
			return true;
		}

		return false;
	}

	public String getCaptcha() {
		return captcha;
	}

	public void setCaptcha(String captcha) {
		this.captcha = captcha;
	}

	public String getEmailId() {
		return emailId;
	}

	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getPayId() {
		return payId;
	}

	public void setPayId(String payId) {
		this.payId = payId;
	}

	public String getNewPassword() {
		return newPassword;
	}

	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}

	public String getConfirmNewPassword() {
		return confirmNewPassword;
	}

	public void setConfirmNewPassword(String confirmNewPassword) {
		this.confirmNewPassword = confirmNewPassword;
	}

	public String getResponse() {
		return response;
	}

	public void setResponse(String response) {
		this.response = response;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	@Override
	public void setServletRequest(HttpServletRequest request) {
		// TODO Auto-generated method stub

	}
}
