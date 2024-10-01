package com.pay10.crm.actionBeans;

import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.pay10.commons.api.Hasher;
import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.exception.SystemException;
import com.pay10.commons.user.PermissionType;
import com.pay10.commons.user.Permissions;
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

@Service
public class CreateNewAdmin {
	
	@Autowired
	private UserDao userDao;
	
	@Autowired
	@Qualifier("saltFileManager")
	private SaltFileManager saltFileManager;
	
	@Autowired
	private CheckExistingUser checkExistingUser;
	
	private static final int emailExpiredInTime = ConfigurationConstants.EMAIL_EXPIRED_HOUR.getValues();
	
	public ResponseObject createUser(User user, UserType userType) throws SystemException {
		ResponseObject responseObject = new ResponseObject();
		ResponseObject responseActionObject = new ResponseObject();
		Date date = new Date();
		String salt = SaltFactory.generateRandomSalt();
		responseObject = checkExistingUser.checkuser(user.getEmailId());
		if (ErrorType.USER_AVAILABLE.getResponseCode().equals(responseObject.getResponseCode())) {
			Permissions permission1 = new Permissions();
			Permissions permission2 = new Permissions();
			Permissions permission3 = new Permissions();
			Set<Permissions> permissions = new HashSet<Permissions>();
			permission1.setPermissionType(PermissionType.CREATEUSER);
			permission2.setPermissionType(PermissionType.DELETEUSER);
			permission3.setPermissionType(PermissionType.LOGIN);
			permissions.add(permission1);
			permissions.add(permission2);
			permissions.add(permission3);
			Set<Roles> roles = new HashSet<Roles>();
			Roles role = new Roles();
			role.setPermissions(permissions);
			role.setName(UserType.ADMIN.name());
			roles.add(role);
			user.setRoles(roles);
			user.setUserType(userType);
			user.setUserStatus(UserStatusType.PENDING);
			user.setPayId(getpayId());
			user.setAccountValidationKey(TransactionManager.getNewTransactionId());
			user.setEmailValidationFlag(false);
			user.setExpressPayFlag(false);
			user.setRegistrationDate(date);
			user.setPasswordExpired(true);
			setExpiryTime(user);
			// This condition is created for subuser
			if (null != user.getPassword()) {
				user.setPassword(Hasher.getHash(user.getPassword().concat(salt)));
			}
			userDao.create(user);

			// Insert salt in salt.properties
			boolean isSaltInserted = saltFileManager.insertSalt(user.getPayId(), salt);
			
			if(!isSaltInserted){
				// Rollback user creation
				userDao.delete(user);
				throw new SystemException(ErrorType.INTERNAL_SYSTEM_ERROR,
						ErrorType.INTERNAL_SYSTEM_ERROR.getResponseMessage());
			}
			responseActionObject.setResponseCode(ErrorType.SUCCESS.getResponseCode());
			responseActionObject.setAccountValidationID(user.getAccountValidationKey() + Hasher.getHash(user.getPayId() + salt));
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
