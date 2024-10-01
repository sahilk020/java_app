package com.pay10.crm.action;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.IntStream;

import org.apache.commons.collections4.Trie;
import org.apache.commons.collections4.trie.PatriciaTrie;
import org.apache.commons.collections4.trie.UnmodifiableTrie;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.pay10.commons.api.EmailControllerServiceProvider;
import com.pay10.commons.audittrail.ActionMessageByAction;
import com.pay10.commons.audittrail.AuditTrail;
import com.pay10.commons.audittrail.AuditTrailService;
import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.user.ResponseObject;
import com.pay10.commons.user.User;
import com.pay10.commons.user.UserDao;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.CrmFieldConstants;
import com.pay10.commons.util.CrmValidator;
import com.pay10.commons.util.MerchantKeySaltService;
import com.pay10.commons.util.SaltFactory;
import com.pay10.crm.actionBeans.PasswordHasher;
import com.pay10.crm.util.PasswordDecryptor;

public class MerchantResetPasswordAction extends ForwardAction {

	@Autowired
	private CrmValidator validator;

	@Autowired
	private UserDao userDao;

	@Autowired
	private AuditTrailService auditTrailService;

	@Autowired
	private EmailControllerServiceProvider emailControllerServiceProvider;
	
	@Autowired
	private PasswordDecryptor passwordDecryptor;
	
	@Autowired 
	MerchantKeySaltService merchantKeySaltService;

	private static Logger logger = LoggerFactory.getLogger(ChangePasswordAction.class.getName());
	private static final long serialVersionUID = -6122140077608015851L;
	private String newPassword;
	private String confirmNewPassword;
	private String response;
	private String merchantPayId;

	@SuppressWarnings("unchecked")
	@Override
	public String execute() {
		ResponseObject responseObject = new ResponseObject();
		try {

			String businessName = userDao.getMerchantByPayId(merchantPayId);
			String emailId = userDao.getEmailIdByBusinessName(businessName);
			responseObject = changePassword(emailId, newPassword);

			Map<String, ActionMessageByAction> actionMessagesByAction = (Map<String, ActionMessageByAction>) sessionMap
					.get(Constants.ACTION_MESSAGE_BY_ACTION.getValue());

			// store the emailId of updated user as we can not store the password in db for
			// audit trail
			String emailIdObj = "{\"emailId\":\"" + emailId + "\"}";
			AuditTrail auditTrail = new AuditTrail(emailIdObj, null,
					actionMessagesByAction.get("merchantResetPasswordAction"));
			auditTrailService.saveAudit(request, auditTrail);

			if (responseObject.getResponseCode().equals(ErrorType.PASSWORD_MISMATCH.getResponseCode())) {
				addFieldError(CrmFieldConstants.OLD_PASSWORD.getValue(),
						ErrorType.PASSWORD_MISMATCH.getResponseMessage());
				setResponse(ErrorType.PASSWORD_MISMATCH.getResponseMessage());// TODO
				return SUCCESS;
			} else if (responseObject.getResponseCode().equals(ErrorType.OLD_PASSWORD_MATCH.getResponseCode())) {
				addFieldError(CrmFieldConstants.OLD_PASSWORD.getValue(),
						ErrorType.OLD_PASSWORD_MATCH.getResponseMessage());
				setResponse(ErrorType.OLD_PASSWORD_MATCH.getResponseMessage());
				return SUCCESS;
			} else if (responseObject.getResponseCode()
					.equals(ErrorType.INVALID_PASSWORDCOMPLEXITY.getResponseCode())) {
				setResponse(ErrorType.INVALID_PASSWORDCOMPLEXITY.getResponseMessage());
				return SUCCESS;
			} else if (responseObject.getResponseCode().equals(ErrorType.REPEATOLD_PASSWORD_MATCH.getResponseCode())) {
				setResponse(ErrorType.REPEATOLD_PASSWORD_MATCH.getResponseMessage());
				return SUCCESS;
			} else if (responseObject.getResponseCode().equals(ErrorType.EMAIL_NEWPASSWORD_MATCH.getResponseCode())) {
				setResponse(ErrorType.EMAIL_NEWPASSWORD_MATCH.getResponseMessage());
				return SUCCESS;
			} else if (responseObject.getResponseCode().equals(ErrorType.PAYID_NEWPASSWORD_MATCH.getResponseCode())) {
				setResponse(ErrorType.PAYID_NEWPASSWORD_MATCH.getResponseMessage());
				return SUCCESS;
			} else if (responseObject.getResponseCode().equals(ErrorType.SALT_NEWPASSWORD_MATCH.getResponseCode())) {
				setResponse(ErrorType.SALT_NEWPASSWORD_MATCH.getResponseMessage());
				return SUCCESS;
			}

			setResponse(ErrorType.PASSWORD_CHANGED.getResponseMessage());
			// SessionCleaner.cleanSession(sessionMap);
			return SUCCESS;

		} catch (Exception exception) {
			logger.error("Exception", exception);
			setResponse("error");
			return ERROR;
		}
	}

	@Override
	public void validate() {

		// Validate blank fields and invalid fields
		
		try {
			setNewPassword(passwordDecryptor.decrypt(newPassword, PasswordDecryptor.SECRET_KEY));
			setConfirmNewPassword(passwordDecryptor.decrypt(confirmNewPassword, PasswordDecryptor.SECRET_KEY));
		}catch (Exception e) {
			logger.info("Decryption Exception : " + e);
		}

		// check fields have valid and match confirm password with new password
		if (validator.validateBlankField(getNewPassword())) {
			setResponse(ErrorType.EMPTY_FIELD.getResponseMessage());
			addFieldError(CrmFieldConstants.NEW_PASSWORD.getValue(), ErrorType.EMPTY_FIELD.getResponseMessage());
		} else if (!(validator.isValidPasword(getNewPassword()))) {
			setResponse(ErrorType.INVALID_FIELD_VALUE.getResponseMessage());
			addFieldError(CrmFieldConstants.NEW_PASSWORD.getValue(),
					ErrorType.INVALID_FIELD_VALUE.getResponseMessage());
		}

		if (validator.validateBlankField(getConfirmNewPassword())) {
			setResponse(ErrorType.EMPTY_FIELD.getResponseMessage());
			addFieldError(CrmFieldConstants.CONFIRM_NEW_PASSWORD.getValue(),
					ErrorType.EMPTY_FIELD.getResponseMessage());

		} else if (!(getNewPassword().equals(getConfirmNewPassword()))) {
			setResponse(ErrorType.INVALID_FIELD_VALUE.getResponseMessage());
			addFieldError(CrmFieldConstants.CONFIRM_NEW_PASSWORD.getValue(),
					ErrorType.INVALID_FIELD_VALUE.getResponseMessage());
		} else if (!(validator.isValidPasword(getNewPassword()))) {
			setResponse(ErrorType.INVALID_FIELD_VALUE.getResponseMessage());
			addFieldError(CrmFieldConstants.CONFIRM_NEW_PASSWORD.getValue(),
					ErrorType.INVALID_FIELD_VALUE.getResponseMessage());
		}
	}

	public String getMerchantPayId() {
		return merchantPayId;
	}

	public void setMerchantPayId(String merchantPayId) {
		this.merchantPayId = merchantPayId;
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

	public ResponseObject changePassword(String emailId, String newPassword) throws Exception {
		String salt;
		String newPasswordString = newPassword;
		User user = new User();
		ResponseObject responseObject = new ResponseObject();

		user = userDao.find(emailId);

		newPassword = (PasswordHasher.hashPassword(newPassword, user.getPayId()));

		Trie<String, String> trie = new UnmodifiableTrie<>(new PatriciaTrie<>(fillMap()));

		ArrayList<String> arrayList = extractDictMatches(trie, newPasswordString);
		int arraySize = arrayList.size();

		if (!newPasswordString.isEmpty()) {
			for (int i = 0; i < arraySize; i++) {
				if (newPasswordString.contains(arrayList.get(i))) {
					responseObject.setResponseCode(ErrorType.INVALID_PASSWORDCOMPLEXITY.getResponseCode());
					return responseObject;
				}
			}
		}

		if (newPasswordString.contains(emailId)) {
			responseObject.setResponseCode(ErrorType.EMAIL_NEWPASSWORD_MATCH.getResponseCode());
			return responseObject;
		}
		if (newPasswordString.contains(user.getPayId())) {
			responseObject.setResponseCode(ErrorType.PAYID_NEWPASSWORD_MATCH.getResponseCode());
			return responseObject;
		}
		//salt = SaltFactory.getSaltProperty(user);
		salt = merchantKeySaltService.getMerchantKeySalt(user.getPayId()).getSalt();
		if (newPasswordString.contains(salt)) {
			responseObject.setResponseCode(ErrorType.SALT_NEWPASSWORD_MATCH.getResponseCode());
			return responseObject;
		}

		user.setPassword(newPassword);
		user.setPasswordExpired(true);
		userDao.update(user);
		responseObject.setResponseCode(ErrorType.PASSWORD_CHANGED.getResponseCode());
		// Sending Email for CRM password change notification
		emailControllerServiceProvider.passwordChange(responseObject, emailId);

		return responseObject;
	}

	private static Map<String, String> fillMap() {
		String filePath = System.getenv("BPGATE_PROPS") + "common-password-combinations.txt";
		HashMap<String, String> map = new HashMap<String, String>();

		String line;
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(filePath));

			while ((line = reader.readLine()) != null) {
				String[] parts = line.split(",", 2);
				if (parts.length >= 2) {
					String key = parts[1];
					String value = parts[0];
					map.put(key, value);
				}
			}
			reader.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			logger.info("Exception in ChangeUserPassword" + e);
		}
		return map;
	}

	private static ArrayList<String> extractDictMatches(Trie<String, String> trie, String pwd) {
		return IntStream.range(0, pwd.length()).collect(ArrayList::new, (objects, i) -> {
			String suffix = pwd.substring(i);
			IntStream.rangeClosed(0, suffix.length()).forEach(j -> {
				String suffixCut = suffix.substring(0, j);
				if (suffixCut.length() > 2) {
					if (trie.containsKey(suffixCut)) {
						objects.add(suffixCut);
					}
				}
			});
		}, (objects, i) -> {
		});
	}

}
