package com.pay10.crm.action;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.pay10.commons.api.EmailControllerServiceProvider;
import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.user.ResponseObject;
import com.pay10.commons.user.User;
import com.pay10.commons.user.UserDao;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.CrmFieldConstants;
import com.pay10.commons.util.CrmValidator;
import com.pay10.crm.actionBeans.ChangeUserPassword;
import com.pay10.crm.util.PasswordDecryptor;

/**
 * @author ROHIT
 *
 */

public class ChangeExpirePasswordAction extends AbstractSecureAction {
	
	@Autowired
	private CrmValidator validator;
	
	@Autowired
	private UserDao userDao;

	@Autowired
	private ChangeUserPassword changeUserPassword;
	
	@Autowired
	private EmailControllerServiceProvider emailControllerServiceProvider;
	
	@Autowired
	private PasswordDecryptor passwordDecryptor;
	
	private static Logger logger = LoggerFactory.getLogger(ChangeExpirePasswordAction.class.getName());
	private static final long serialVersionUID = -6122140077609784851L;
	private String oldPassword;
	private String newPassword;
	private String confirmNewPassword;
	private String response;
	private String emailId;

	@Override
	public String execute() {
		ResponseObject responseObject = new ResponseObject();
		try {
			User sessionUser ;
			
			sessionUser = userDao.find(emailId);
			//Need to decrypt here both password
			
			//oldPassword=passwordDecryptor.decrypt(oldPassword, PasswordDecryptor.SECRET_KEY);
			//newPassword=passwordDecryptor.decrypt(newPassword, PasswordDecryptor.SECRET_KEY);
			responseObject = changeUserPassword.changePassword(
					emailId, oldPassword, newPassword);

			if (responseObject.getResponseCode().equals(
					ErrorType.PASSWORD_MISMATCH.getResponseCode())) {
				addFieldError(CrmFieldConstants.OLD_PASSWORD.getValue(),
						ErrorType.PASSWORD_MISMATCH.getResponseMessage());
				setResponse(ErrorType.PASSWORD_MISMATCH.getResponseMessage()); 
				return SUCCESS;
			} else if (responseObject.getResponseCode().equals(
					ErrorType.OLD_PASSWORD_MATCH.getResponseCode())) {
				 addFieldError(CrmFieldConstants.OLD_PASSWORD.getValue(),
						ErrorType.OLD_PASSWORD_MATCH.getResponseMessage()); 
				 setResponse(ErrorType.OLD_PASSWORD_MATCH.getResponseMessage());
				return SUCCESS;
			} else if (responseObject.getResponseCode().equals(
					ErrorType.INVALID_PASSWORDCOMPLEXITY.getResponseCode())) {
				 addFieldError(CrmFieldConstants.NEW_PASSWORD.getValue(),
						ErrorType.INVALID_PASSWORDCOMPLEXITY.getResponseMessage()); 
				 setResponse(ErrorType.INVALID_PASSWORDCOMPLEXITY.getResponseMessage());
				return SUCCESS;
			} else if (responseObject.getResponseCode().equals(
					ErrorType.EMAIL_NEWPASSWORD_MATCH.getResponseCode())) {
				 addFieldError(CrmFieldConstants.NEW_PASSWORD.getValue(),
						ErrorType.EMAIL_NEWPASSWORD_MATCH.getResponseMessage()); 
				 setResponse(ErrorType.EMAIL_NEWPASSWORD_MATCH.getResponseMessage());
				return SUCCESS;
			} else if (responseObject.getResponseCode().equals(
					ErrorType.PAYID_NEWPASSWORD_MATCH.getResponseCode())) {
				 addFieldError(CrmFieldConstants.NEW_PASSWORD.getValue(),
						ErrorType.PAYID_NEWPASSWORD_MATCH.getResponseMessage()); 
				 setResponse(ErrorType.PAYID_NEWPASSWORD_MATCH.getResponseMessage());
				return SUCCESS;
			} else if (responseObject.getResponseCode().equals(
					ErrorType.SALT_NEWPASSWORD_MATCH.getResponseCode())) {
				 addFieldError(CrmFieldConstants.NEW_PASSWORD.getValue(),
						ErrorType.SALT_NEWPASSWORD_MATCH.getResponseMessage()); 
				 setResponse(ErrorType.SALT_NEWPASSWORD_MATCH.getResponseMessage());
				return SUCCESS;
			}  else if (responseObject.getResponseCode().equals(
					ErrorType.REPEATOLD_PASSWORD_MATCH.getResponseCode())) {
				 addFieldError(CrmFieldConstants.NEW_PASSWORD.getValue(),
						ErrorType.REPEATOLD_PASSWORD_MATCH.getResponseMessage()); 
				 setResponse(ErrorType.REPEATOLD_PASSWORD_MATCH.getResponseMessage());
				return SUCCESS;
			}
			
			
			setResponse(ErrorType.PASSWORD_CHANGED.getResponseMessage()); 
			emailControllerServiceProvider.passwordChangeConfirmationEmail(sessionUser);
			//SessionCleaner.cleanSession(sessionMap);
   			return SUCCESS;

		} catch (Exception exception) {
			logger.error("Exception", exception);
			setResponse("error");
			return ERROR;
		}
	}

	@Override
	public void validate() {

		try {
			oldPassword=passwordDecryptor.decrypt(oldPassword, PasswordDecryptor.SECRET_KEY);
			newPassword=passwordDecryptor.decrypt(newPassword, PasswordDecryptor.SECRET_KEY);
			confirmNewPassword=passwordDecryptor.decrypt(confirmNewPassword, PasswordDecryptor.SECRET_KEY);
		}catch (Exception e) {
			logger.info("Decryption Exception : " + e);
		}
		
		// Validate blank fields and invalid fields
		if ((validator.validateBlankField(getOldPassword()))) {
			setResponse(ErrorType.EMPTY_FIELD.getResponseMessage());
			addFieldError(CrmFieldConstants.OLD_PASSWORD.getValue(),
					ErrorType.EMPTY_FIELD.getResponseMessage());
			
		} else if (!(validator.isValidPasword(getOldPassword()))) {
			setResponse(ErrorType.INVALID_FIELD_VALUE.getResponseMessage());
			addFieldError(CrmFieldConstants.OLD_PASSWORD.getValue(),
					ErrorType.INVALID_FIELD_VALUE.getResponseMessage());
					}

		// check fields have valid and match confirm password with new password
		if (validator.validateBlankField(getNewPassword())) {
			setResponse(ErrorType.EMPTY_FIELD.getResponseMessage());
			addFieldError(CrmFieldConstants.NEW_PASSWORD.getValue(),
					ErrorType.EMPTY_FIELD.getResponseMessage());
		}
		else if (!(validator.isValidPasword(getNewPassword()))) {
			setResponse(ErrorType.INVALID_FIELD_VALUE.getResponseMessage());
			addFieldError(CrmFieldConstants.NEW_PASSWORD.getValue(),
					ErrorType.INVALID_FIELD_VALUE.getResponseMessage());
					}
			
		 if (validator.validateBlankField(getConfirmNewPassword())) {
				setResponse(ErrorType.EMPTY_FIELD.getResponseMessage());
				addFieldError(CrmFieldConstants.CONFIRM_NEW_PASSWORD.getValue(),
						ErrorType.EMPTY_FIELD.getResponseMessage());
				
			}
		else if (!(getNewPassword().equals(getConfirmNewPassword()))) {
			setResponse(ErrorType.INVALID_FIELD_VALUE.getResponseMessage());
			addFieldError(CrmFieldConstants.CONFIRM_NEW_PASSWORD.getValue(),
					ErrorType.INVALID_FIELD_VALUE.getResponseMessage());
		} else if (!(validator.isValidPasword(getNewPassword()))) {
			setResponse(ErrorType.INVALID_FIELD_VALUE.getResponseMessage());
			addFieldError(CrmFieldConstants.CONFIRM_NEW_PASSWORD.getValue(),
					ErrorType.INVALID_FIELD_VALUE.getResponseMessage());
		}
	}

	public String getOldPassword() {
		return oldPassword;
	}

	public void setOldPassword(String oldPassword) {
		this.oldPassword = oldPassword;
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

	public String getEmailId() {
		return emailId;
	}

	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}
	

}
