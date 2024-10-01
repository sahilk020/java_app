package com.pay10.crm.action;

import java.util.Calendar;
import java.util.Date;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.ServletActionContext;
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
import com.pay10.commons.user.MerchantKeySalt;
import com.pay10.commons.user.MerchantKeySaltDao;
import com.pay10.commons.user.User;
import com.pay10.commons.user.UserDao;
import com.pay10.commons.user.UserRecordsDao;
import com.pay10.commons.user.UserType;
import com.pay10.commons.util.ConfigurationConstants;
import com.pay10.commons.util.CrmValidator;
import com.pay10.commons.util.PropertiesManager;
import com.pay10.commons.util.TransactionManager;
import com.pay10.commons.util.UserStatusType;
import com.pay10.crm.actionBeans.CheckOldPassword;
import com.pay10.crm.util.PasswordDecryptor;

/**
 * @author Neeraj
 */
public class ForgetPasswordAction extends AbstractSecureAction implements ServletRequestAware {

	private static final int emailExpiredInMinute = ConfigurationConstants.EMAIL_EXPIRED_MINUTE.getValues();

	@Autowired
	private UserDao userDao;

	@Autowired
	private CrmValidator validator;

	@Autowired
	private UserRecordsDao userRecordsDao;

	@Autowired
	private CheckOldPassword checkOldPassword;

	private static Logger logger = LoggerFactory.getLogger(ForgetPasswordAction.class.getName());
	private static final long serialVersionUID = 4184065113906121002L;
	@Autowired
	private EmailControllerServiceProvider emailControllerServiceProvider;
	
	@Autowired
	private PasswordDecryptor passwordDecryptor;
	
	@Autowired
	private MerchantKeySaltDao merchantKeySaltDao; 

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
	HttpSession session;

	private User user = new User();

	@Override
	@SkipValidation
	public String execute() {
		try {
			if (!validateEmail()) {
				String accountValidationKey = TransactionManager.getNewTransactionId();
				user = userDao.findPayIdByEmail(getEmailId());
				if (user != null) {
					if ((user.getUserStatus().toString()).equals(UserStatusType.ACTIVE.toString())) {
						String payId = user.getPayId();
						if (!StringUtils.isEmpty(payId)) {
							Date currnetDate = new Date();
							Calendar c = Calendar.getInstance();
							c.setTime(currnetDate);
							c.add(Calendar.MINUTE, emailExpiredInMinute);
							currnetDate = c.getTime();
							// Sending Email for Email Validation
							emailControllerServiceProvider.passwordReset(accountValidationKey, getEmailId());
							userDao.updateAccountValidationKey(accountValidationKey, payId);
							userDao.enterEmailExpiryTime(currnetDate, payId);
							setResponse(ErrorType.RESET_LINK_SENT.getResponseMessage());
						} else {
							setResponse(ErrorType.INVALID_EMAIL.getResponseMessage());
							setErrorMessage(ErrorType.INVALID_EMAIL.getResponseMessage());
						}
					} else {
						setResponse(ErrorType.VERIFY_EMAIL_ID.getResponseMessage());
						setErrorMessage(ErrorType.VERIFY_EMAIL_ID.getResponseMessage());

					}

				} else {
					setResponse(ErrorType.INVALID_EMAIL.getResponseMessage());
					setErrorMessage(ErrorType.INVALID_EMAIL.getResponseMessage());
				}
			} else {
				setResponse(getErrorMessage());
			}
		} catch (Exception exception) {
			logger.error("Exception", exception);
			return SUCCESS;
		}
		return SUCCESS;
	}

	@SkipValidation
	public String resetPassword() {
		try {
			user = userDao.findByAccountValidationKey(getId());
			logger.info("reset password getId.....={},user....={}",getId(),user);

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
				request = ServletActionContext.getRequest();
				session = request.getSession();
				session.setAttribute("PayId",user.getPayId());
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

	public String resetUserPassword() {
		request = ServletActionContext.getRequest();
		session = request.getSession();
		if (StringUtils.isNotBlank(getPayId())) {
			user = userDao.findPayId(getPayId());
		}
		else if(session.getAttribute("PayId") != null){
			user = userDao.findPayId((String) session.getAttribute("PayId"));
			session.removeAttribute("PayId");
		}
		try {
			if (!validateFields()) {
				logger.info("validation success");
				if (user == null) {
					return ERROR;
				}
				userRecordsDao.createDetails(user.getEmailId(), user.getPassword(), user.getPayId());
				 MerchantKeySalt merchantKeySaltDetails= merchantKeySaltDao.find(user.getPayId());
				//String salt = (new PropertiesManager()).getSalt(user.getPayId());
					String salt = merchantKeySaltDetails.getSalt();
					logger.info("geting salt of merchant from database....={}",salt);

				if (null == salt) {
					throw new SystemException(ErrorType.AUTHENTICATION_UNAVAILABLE,
							ErrorType.AUTHENTICATION_UNAVAILABLE.getResponseCode());
				}
				String hashedPassword = Hasher.getHash(getNewPassword().concat(salt));
				logger.info("geting new hashedPassword of merchant ....={}",hashedPassword);
				user.setPassword(hashedPassword);
				user.setEmailValidationFlag(true);
				
				if(user.getUserStatus().equals(UserStatusType.PENDING))
				{
					setResponse(ErrorType.USER_STATUS.getResponseMessage());
					return SUCCESS;
				}
				
				if (user.getUserType().equals(UserType.SUBUSER) || user.getUserType().equals(UserType.SUBACQUIRER)
						|| user.getUserType().equals(UserType.SUBADMIN)) {
					
					user.setUserStatus(UserStatusType.ACTIVE);
				}
				userDao.update(user);
				setResponse(ErrorType.PASSWORD_RESET.getResponseMessage());
				setErrorCode(ErrorType.PASSWORD_RESET.getCode());
				emailControllerServiceProvider.passwordResetConfirmationEmail(user);
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

	public boolean validateFields() throws SystemException {
	
		try {
			newPassword=passwordDecryptor.decrypt(newPassword, PasswordDecryptor.SECRET_KEY);
			confirmNewPassword=passwordDecryptor.decrypt(confirmNewPassword, PasswordDecryptor.SECRET_KEY);
			setNewPassword(newPassword);
			setConfirmNewPassword(confirmNewPassword);
		}catch (Exception e) {
			logger.info("Decryption Exception : " + e);
		}
		
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
