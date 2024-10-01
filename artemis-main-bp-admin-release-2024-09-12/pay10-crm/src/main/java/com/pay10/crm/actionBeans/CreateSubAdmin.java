package com.pay10.crm.actionBeans;

import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import com.pay10.commons.dao.ResetMerchantKeyDao;
import com.pay10.commons.entity.ResetMerchantKey;
import com.pay10.commons.user.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.pay10.commons.api.Hasher;
import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.exception.SystemException;
import com.pay10.commons.util.ConfigurationConstants;
import com.pay10.commons.util.SaltFactory;
import com.pay10.commons.util.SaltFileManager;
import com.pay10.commons.util.TransactionManager;
import com.pay10.commons.util.UserStatusType;

@Service
public class CreateSubAdmin {

	@Autowired
	UserDao userDao;

	@Autowired
	CompanyProfileDao companyProfileDao;

	@Autowired
	Hasher hasher;

	@Autowired
	@Qualifier("saltFileManager")
	SaltFileManager saltFileManager;

	@Autowired
	MerchantKeySaltDao merchantKeySaltDao;

	@Autowired
	ResetMerchantKeyDao resetMerchantKeyDao;

	private static final int emailExpiredInTime = ConfigurationConstants.EMAIL_EXPIRED_HOUR.getValues();

	private static Logger logger = LoggerFactory.getLogger(CreateSubAdmin.class.getName());

	public ResponseObject createNewSubAdmin(User user, UserType userType, String parentPayId)
			throws SystemException {
		logger.info("inside createNewSubadmin CreateSubAdmin ");
		ResponseObject responseObject = new ResponseObject();
		ResponseObject responseActionObject = new ResponseObject();
		CheckExistingUser checkExistingUser = new CheckExistingUser();
		Date date = new Date();
		String salt = SaltFactory.generateRandomSalt();
		logger.info("CREATE SUB ADMIN SALT : {}", salt);
		responseObject = checkExistingUser.checkuser(user.getEmailId());
		logger.info(" is existing user ? " + responseObject.getResponseMessage());
		if (ErrorType.USER_AVAILABLE.getResponseCode().equals(responseObject.getResponseCode())) {
			user.setUserType(userType);
			user.setUserStatus(UserStatusType.ACTIVE);
			user.setPayId(getpayId());
			user.setAccountValidationKey(TransactionManager.getNewTransactionId());
			user.setEmailValidationFlag(false);
			user.setExpressPayFlag(false);
			user.setRegistrationDate(date);
			user.setBusinessName(user.getFirstName());
			setExpiryTime(user);
			MerchantKeySalt merchantKeySalt = new MerchantKeySalt();
			ResetMerchantKey resetMerchantKey=new ResetMerchantKey();
			merchantKeySalt.setPayId(user.getPayId());
			merchantKeySalt.setSalt(salt);
			merchantKeySalt.setKeySalt(salt);
			resetMerchantKey.setPayId(user.getPayId());
			resetMerchantKey.setSalt(salt);
			resetMerchantKey.setKeySalt(salt);
			resetMerchantKey.setUpdatedOn(date);
			resetMerchantKey.setUpdatedBy(user.getPayId());
			resetMerchantKey.setStatus("Active");
			resetMerchantKey.setStartDate(new Date());
			//merchantKeySalt.setEncryptionKey(merchantEncKey);
			//resetMerchantKey.setEncryptionKey(merchantEncKey);
			merchantKeySaltDao.addMerchantInfo(merchantKeySalt);
			resetMerchantKeyDao.saveOrUpdate(resetMerchantKey);
			// This condition is created for Agent
			if (null != user.getPassword()) {
				user.setPassword(Hasher.getHash(user.getPassword().concat(salt)));
			} else {
				user.setPassword("");// tp prevent password from being set null
			}
			user.setParentPayId(parentPayId);
			/*
			 * Set<Roles> roles = new HashSet<Roles>(); Roles role = new Roles();
			 * role.setName(UserType.ADMIN.name()); roles.add(role);
			 *
			 * user.setRoles(roles);
			 */

			userDao.create(user);

			// Insert salt in salt.properties
			boolean isSaltInserted = saltFileManager.insertSalt(user.getPayId(), salt);
			logger.info("Is Salt Inserted : {}", isSaltInserted);
			if (!isSaltInserted) {
				// Rollback user creation
				userDao.delete(user);
				throw new SystemException(ErrorType.INTERNAL_SYSTEM_ERROR,
						ErrorType.INTERNAL_SYSTEM_ERROR.getResponseMessage());
			}
			responseActionObject.setResponseCode(ErrorType.SUCCESS.getResponseCode());
			responseActionObject.setAccountValidationID(user.getAccountValidationKey());
			responseActionObject.setEmail(user.getEmailId());
			logger.info("Create new subadmin success , email id = " + user.getEmailId());
		} else {
			responseActionObject.setResponseCode(ErrorType.USER_UNAVAILABLE.getResponseCode());
			responseActionObject.setResponseMessage(ErrorType.USER_UNAVAILABLE.getResponseMessage());
		}
		return responseActionObject;
	}

	public ResponseObject createNewCompanyProfile(CompanyProfile cp) {
		logger.info("inside createNewCompanyProfile  ");
		ResponseObject responseActionObject = new ResponseObject();
		companyProfileDao.create(cp);
		responseActionObject.setResponseCode(ErrorType.SUCCESS.getResponseCode());
		responseActionObject.setEmail(cp.getEmailId());
		logger.info("Create New Company Profile success , email id = " + cp.getEmailId());
		return responseActionObject;
	}

	public User setExpiryTime(User user){
		Date currnetDate = new Date();
		Calendar c = Calendar.getInstance();
		c.setTime(currnetDate);
		c.add(Calendar.HOUR, emailExpiredInTime);
		Date expiryDate = c.getTime();
		user.setEmailExpiryTime(expiryDate);
		return user;
	}

	private String getpayId() {
		return TransactionManager.getNewTransactionId();
	}

	public ResponseObject createNewSubSuperAdmin(User user, UserType userType, String parentPayId) throws SystemException {

		logger.info("inside createNewSubadmin CreateSubAdmin ");
		ResponseObject responseObject = new ResponseObject();
		ResponseObject responseActionObject = new ResponseObject();
		CheckExistingUser checkExistingUser = new CheckExistingUser();
		Date date = new Date();
		String salt = SaltFactory.generateRandomSalt();
		responseObject = checkExistingUser.checkuser(user.getEmailId());
		logger.info(" is existing user ? " + responseObject.getResponseMessage());
		if (ErrorType.USER_AVAILABLE.getResponseCode().equals(responseObject.getResponseCode())) {
			user.setUserType(userType);
			user.setUserStatus(UserStatusType.ACTIVE);
			user.setPayId(getpayId());
			user.setAccountValidationKey(TransactionManager.getNewTransactionId());
			user.setEmailValidationFlag(false);
			user.setExpressPayFlag(false);
			user.setRegistrationDate(date);
			user.setBusinessName(user.getFirstName());
			setExpiryTime(user);
			// This condition is created for Agent
			if (null != user.getPassword()) {
				user.setPassword(Hasher.getHash(user.getPassword().concat(salt)));
			} else {
				user.setPassword("");// tp prevent password from being set null
			}
			user.setParentPayId(parentPayId);

			Set<Roles> roles = new HashSet<Roles>();
			Roles role = new Roles();

			role.setName(UserType.SUPERADMIN.name());
			roles.add(role);

			user.setRoles(roles);
			userDao.create(user);

			// Insert salt in salt.properties
			boolean isSaltInserted = saltFileManager.insertSalt(user.getPayId(), salt);

			if (!isSaltInserted) {
				// Rollback user creation
				userDao.delete(user);
				throw new SystemException(ErrorType.INTERNAL_SYSTEM_ERROR,
						ErrorType.INTERNAL_SYSTEM_ERROR.getResponseMessage());
			}
			responseActionObject.setResponseCode(ErrorType.SUCCESS.getResponseCode());
			responseActionObject.setAccountValidationID(user.getAccountValidationKey());
			responseActionObject.setEmail(user.getEmailId());
			logger.info("Create new subadmin success , email id = " + user.getEmailId());
		} else {
			responseActionObject.setResponseCode(ErrorType.USER_UNAVAILABLE.getResponseCode());
			responseActionObject.setResponseMessage(ErrorType.USER_UNAVAILABLE.getResponseMessage());
		}
		return responseActionObject;

	}

}
