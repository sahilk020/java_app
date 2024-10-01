package com.pay10.crm.actionBeans;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import com.pay10.commons.dao.ResetMerchantKeyDao;
import com.pay10.commons.entity.ResetMerchantKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.pay10.commons.api.Hasher;
import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.exception.SystemException;
import com.pay10.commons.user.MerchantKeySalt;
import com.pay10.commons.user.MerchantKeySaltDao;
import com.pay10.commons.user.MerchantStatusLog;
import com.pay10.commons.user.MerchantStatusLogDao;
import com.pay10.commons.user.ResponseObject;
import com.pay10.commons.user.Roles;
import com.pay10.commons.user.User;
import com.pay10.commons.user.UserDao;
import com.pay10.commons.user.UserType;
import com.pay10.commons.util.ConfigurationConstants;
import com.pay10.commons.util.SaltFactory;
import com.pay10.commons.util.SaltFileManager;
import com.pay10.commons.util.TransactionManager;
import com.pay10.commons.util.UserStatusType;

/**
 * @author Chandan
 *
 */

@Service
public class CreateSubUser {

	@Autowired
	private Hasher hasher;

	@Autowired
	private UserDao userDao;

	@Autowired
	@Qualifier("saltFileManager")
	private SaltFileManager saltFileManager;

	@Autowired
	private CheckExistingUser checkExistingUser;
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	@Autowired
	private MerchantStatusLogDao merchantStatusLogDao;
	@Autowired
	MerchantKeySaltDao merchantKeySaltDao;

	@Autowired
	ResetMerchantKeyDao resetMerchantKeyDao;
	
	private static final int emailExpiredInTime = ConfigurationConstants.EMAIL_EXPIRED_HOUR.getValues();

	public ResponseObject createUser(User user, UserType userType, String parentPayId)
			throws SystemException {

		ResponseObject responseObject = new ResponseObject();
		ResponseObject responseActionObject = new ResponseObject();
		Date date = new Date();
		String salt = SaltFactory.generateRandomSalt();

		responseObject = checkExistingUser.checkuser(user.getEmailId());
		if (ErrorType.USER_AVAILABLE.getResponseCode().equals(responseObject.getResponseCode())) {
			user.setUserType(userType);
			if(user.getUserStatus() == null) {
				user.setUserStatus(UserStatusType.PENDING);	
			}			
			user.setPayId(getpayId());
			user.setAccountValidationKey(TransactionManager.getNewTransactionId());
			user.setEmailValidationFlag(false);
			user.setExpressPayFlag(false);
			user.setRegistrationDate(date);
			setExpiryTime(user);
			// This condition is created for subuser
			if (null != user.getPassword()) {
				user.setPassword(Hasher.getHash(user.getPassword().concat(salt)));
			} else {
				user.setPassword("");// tp prevent password from being set null
			}
			user.setParentPayId(parentPayId);

			Set<Roles> roles = new HashSet<Roles>();
			Roles role = new Roles();

			role.setName(UserType.SUBUSER.name());
			roles.add(role);

			user.setRoles(roles);

			userDao.create(user);

			System.out.println("merchantlog createUser : " + user.getEmailId() + "\t" + responseObject.getEmail());
			if(UserType.SUBUSER.equals(user.getUserType().SUBUSER)) {
				//add to log
				MerchantStatusLog merchantStatusLog=new MerchantStatusLog();
				merchantStatusLog.setEmailId(user.getEmailId());
				merchantStatusLog.setTimeStamp(sdf.format(new Date()));
				merchantStatusLog.setMessage("Sub Merchant " + user.getUserStatus().getStatus());
				merchantStatusLog.setStatus(user.getUserStatus().getStatus());
				merchantStatusLogDao.saveMerchantStatusLog(merchantStatusLog);
			}


			// Insert salt in salt.properties
//			boolean isSaltInserted = saltFileManager.insertSalt(user.getPayId(), salt);
			String keySalt = SaltFactory.generateRandomSalt();
			//added salt & keySalt into Mysql db.
			ResetMerchantKey resetMerchantKey=new ResetMerchantKey();
			MerchantKeySalt merchantKeySalt = new MerchantKeySalt();
			merchantKeySalt.setPayId(user.getPayId());
			merchantKeySalt.setSalt(salt);
			merchantKeySalt.setKeySalt(keySalt);
			resetMerchantKey.setPayId(user.getPayId());
			resetMerchantKey.setSalt(salt);
			resetMerchantKey.setKeySalt(keySalt);
			resetMerchantKey.setUpdatedBy(user.getPayId());
			resetMerchantKey.setUpdatedOn(date);
			resetMerchantKey.setStatus("Active");
			resetMerchantKey.setStartDate(new Date());
			//merchantKeySalt.setEncryptionKey(merchantEncKey);
			//resetMerchantKey.setEncryptionKey(merchantEncKey);
			merchantKeySaltDao.addMerchantInfo(merchantKeySalt);
			resetMerchantKeyDao.saveOrUpdate(resetMerchantKey);
			boolean merchantKeySaltResult = merchantKeySaltDao.checkuser(user.getPayId());
			
			if (!merchantKeySaltResult) {
				// Rollback user creation
				userDao.delete(user);
				throw new SystemException(ErrorType.INTERNAL_SYSTEM_ERROR,
						ErrorType.INTERNAL_SYSTEM_ERROR.getResponseMessage());
			}
			responseActionObject.setResponseCode(ErrorType.SUCCESS.getResponseCode());
			responseActionObject.setAccountValidationID(user.getAccountValidationKey());
			responseActionObject.setEmail(user.getEmailId());
		} else {
			responseActionObject.setResponseCode(ErrorType.USER_UNAVAILABLE.getResponseCode());
			responseActionObject.setResponseMessage(ErrorType.USER_UNAVAILABLE.getResponseMessage());
		}
		return responseActionObject;
	}
	
	public User setExpiryTime(User user){
		Date currnetDate = new Date();
		Calendar c = Calendar.getInstance(); 
		c.setTime(currnetDate); 
		c.add(Calendar.HOUR, emailExpiredInTime);
		currnetDate = c.getTime();
		user.setEmailExpiryTime(currnetDate);
		return user;
	}

	private String getpayId() {
		return TransactionManager.getNewTransactionId();
	}
}
