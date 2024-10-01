package com.pay10.crm.action;

import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.pay10.commons.api.Hasher;
import com.pay10.commons.dao.TransactionDetailsService;
import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.user.MerchantKeySalt;
import com.pay10.commons.user.MerchantKeySaltDao;
import com.pay10.commons.user.User;
import com.pay10.commons.user.UserDao;
import com.pay10.commons.user.UserType;
import com.pay10.commons.util.CustTransactionAuthentication;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.MerchantKeySaltService;
import com.pay10.commons.util.PropertiesManager;
import com.pay10.commons.util.UserStatusType;

/**
 * @author Sunil
 *
 */

public class EmailValidationAction extends AbstractSecureAction {

	private static Logger logger = LoggerFactory.getLogger(EmailValidationAction.class.getName());
	private static final long serialVersionUID = 5995449017764989418L;

	private String id;
	private UserDao userDao = new UserDao();
	private User user = new User();
	@Autowired
	PropertiesManager propertiesManager;
	
	@Autowired
	MerchantKeySaltDao merchantKeySaltDao;
	@Override
	public String execute() {

		try {
			if (StringUtils.isEmpty(getId())) {
				logger.info("Invalid Link.");
				return ERROR;
			}
			String hash = getId().substring(16);
			setId(getId().substring(0, 16));

			user = userDao.findByAccountValidationKey(getId());


			if (user == null) {
				addActionMessage(ErrorType.ALREADY_VALIDATE_EMAIL.getResponseMessage());
				return "validate";
			} else if (user.isEmailValidationFlag()) {
				addActionMessage(ErrorType.ALREADY_VALIDATE_EMAIL.getResponseMessage());
				return "validate";
			}
			String salt = propertiesManager.getSalt(user.getPayId());
			
			if(StringUtils.isBlank(salt)) {
				MerchantKeySalt  merchantKeySalt = merchantKeySaltDao.find(user.getPayId());

				salt=merchantKeySalt.getSalt();
			}


			String userHash = Hasher.getHash(user.getPayId() + salt);

			Date expiryTime = user.getEmailExpiryTime();
			Date currentTime = new Date();
			int result = currentTime.compareTo(expiryTime);

			if (!hash.equals(userHash)) {
				logger.error("Hash Mismatch while validating via link.");
				return ERROR;
			} else if (result < 0 || result == 0) {
				if (user.getUserType().equals(UserType.ADMIN)) {

					userDao.updateEmailValidation(getId(), UserStatusType.ACTIVE, true);

				} else {
					userDao.updateEmailValidation(getId(), UserStatusType.SUSPENDED, true);
				}

			} else {
				logger.info("Activation link has been expired");
				return "linkExpired";
			}
			return SUCCESS;
		} catch (Exception exception) {
			logger.error("Exception in Email Validation action");
			logger.error(exception.getMessage());
			return ERROR;
		}
	}

	public String transactionAuthentication() {
		try {
			if (getId() == null) {
				return ERROR;
			}
			TransactionDetailsService transactionDetailsService = new TransactionDetailsService();
			String txnAuthentication = transactionDetailsService.getTransactionAuthentication(getId());

			if (txnAuthentication.equals(CustTransactionAuthentication.SUCCESS.getAuthenticationName())) {
				return "alreadyAuthenticate";
			}
			transactionDetailsService.updateTransactionAuthentication(
					CustTransactionAuthentication.SUCCESS.getAuthenticationName(), getId());
			return SUCCESS;
		} catch (Exception exception) {
			logger.error("Exception", exception);
			return ERROR;
		}
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	public static void main(String[] args) {
		String hash = "10123312151220489822FA9A6D335826986673A82C7232530E9010CCFA8A9B851BF03B5ADF2D0AE6".substring(16);
		
		System.out.println(hash.substring(0, 16));
	}

}
