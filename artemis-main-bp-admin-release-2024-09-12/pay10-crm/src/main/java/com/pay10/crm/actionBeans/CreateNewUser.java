package com.pay10.crm.actionBeans;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import com.pay10.commons.dao.ResetMerchantKeyDao;
import com.pay10.commons.dao.RoleDao;
import com.pay10.commons.dao.UserGroupDao;
import com.pay10.commons.entity.ResetMerchantKey;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.pay10.commons.api.Hasher;
import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.exception.SystemException;
import com.pay10.commons.user.MerchantKeySalt;
import com.pay10.commons.user.MerchantKeySaltDao;
import com.pay10.commons.user.ResponseObject;
import com.pay10.commons.user.Role;
import com.pay10.commons.user.User;
import com.pay10.commons.user.UserDao;
import com.pay10.commons.user.UserGroup;
import com.pay10.commons.user.UserType;
import com.pay10.commons.util.ConfigurationConstants;
import com.pay10.commons.util.CrmFieldConstants;
import com.pay10.commons.util.OrderIdType;
import com.pay10.commons.util.PropertiesManager;
import com.pay10.commons.util.SaltFactory;
import com.pay10.commons.util.SaltFileManager;
import com.pay10.commons.util.TransactionManager;
import com.pay10.commons.util.UserStatusType;
/**
 * @author Puneet
 *
 */

@Service
public class CreateNewUser {
	
	private static Logger logger = LoggerFactory.getLogger(CreateNewUser.class.getName());
	
	private static final int emailExpiredInTime = ConfigurationConstants.EMAIL_EXPIRED_HOUR.getValues();
	
	@Autowired
	private Hasher hasher;
	
	@Autowired
	private UserDao userDao;
	
	@Autowired
	@Qualifier("saltFileManager")
	private SaltFileManager saltFileManager;
	
	@Autowired
	private CheckExistingUser checkExistingUser;
	
	@Autowired
	MerchantKeySaltDao merchantKeySaltDao;
	@Autowired
	ResetMerchantKeyDao resetMerchantKeyDao;
	
	@Autowired
	private RoleDao roleDao;

	@Autowired
	private UserGroupDao userGroupDao;
	
	
	public ResponseObject createUser(User user, UserType userType, String parentPayId) throws SystemException{
		logger.info("create user with usertype " + userType + "  and parent pay Id "+ parentPayId);

		ResponseObject responseObject= new ResponseObject();
		ResponseObject responseActionObject= new ResponseObject();
		Date date = new Date(); 
		String salt = SaltFactory.generateRandomSalt();
		String keySalt = SaltFactory.generateRandomSalt();
		logger.info("Checking user");
		responseObject = checkExistingUser.checkuser(user.getEmailId());
		logger.info("Response code " + responseObject.getResponseCode());
		
		UserGroup groups = userGroupDao.getUserGroupByUserType(userType);
		Role role = roleDao.getRoleByUserType(userType);
		
		if(ErrorType.USER_AVAILABLE.getResponseCode().equals(responseObject.getResponseCode())){	
		
			user.setUserType(userType);
			user.setUserStatus(UserStatusType.PENDING);
			user.setPayId(getpayId());
			if(userType.equals(UserType.RESELLER)){
				user.setResellerId(user.getPayId());
			}
			
			else if(userType.equals(UserType.SMA)){
				
				logger.info("groups list.....={}",groups);
			
				user.setSmaId(user.getPayId());
				user.setRole(role);
				user.setUserGroup(groups);
				//user.setUserGroup();
			}
			else if(userType.equals(UserType.MA)){
				user.setRole(role);
				user.setUserGroup(groups);
				user.setMaId(user.getPayId());
			}
			else if(userType.equals(UserType.Agent)){
				user.setRole(role);
				user.setUserGroup(groups);
				user.setAgentId(user.getPayId());
			}
			user.setAccountValidationKey(TransactionManager.getNewTransactionId()+ Hasher.getHash(user.getPayId() + salt));
			user.setEmailValidationFlag(false);
			user.setExpressPayFlag(false);
			user.setRegistrationDate(date);
			user.setSkipOrderIdForRefund(false);
			user.setAllowDuplicateOrderId(OrderIdType.NEVER);
			user.setPaymentMessageSlab("0");
			//This condition is created for subuser
			if(null != user.getPassword()) {
				user.setPassword(Hasher.getHash(user.getPassword().concat(salt))); 
			}
			user.setParentPayId(parentPayId);
			setExpiryTime(user);
			
			if(userType.equals(UserType.MERCHANT)){
				String pyamentLinkurl = PropertiesManager.propertiesMap.get(CrmFieldConstants.MERCHANT_PAYMENT_FORM_URL.getValue())
						+ user.getPayId();
				user.setPaymentLink(pyamentLinkurl);
			}
			
			userDao.create(user);
			
			//Insert salt in salt.properties			
			//boolean isSaltInserted = saltFileManager.insertSalt(user.getPayId(), salt);
			//boolean isKeySaltInserted = saltFileManager.insertKeySalt(user.getPayId(), keySalt);
			
			logger.info("CreateNewUser:: PayId ={}, Salt = {}, MerchantKey = {}",user.getPayId(), salt, keySalt);
			//added salt & keySalt into Mysql db.
			MerchantKeySalt merchantKeySalt = new MerchantKeySalt();
			ResetMerchantKey resetMerchantKey=new ResetMerchantKey();
			merchantKeySalt.setPayId(user.getPayId());
			merchantKeySalt.setSalt(salt);
			merchantKeySalt.setKeySalt(keySalt);
			resetMerchantKey.setPayId(user.getPayId());
			resetMerchantKey.setSalt(salt);
			resetMerchantKey.setKeySalt(keySalt);
			resetMerchantKey.setStartDate(new Date());
			resetMerchantKey.setUpdatedBy(user.getPayId());
			resetMerchantKey.setUpdatedOn(new Date());
			resetMerchantKey.setStatus("Active");
			//merchantKeySalt.setEncryptionKey(merchantEncKey);
			//resetMerchantKey.setEncryptionKey(merchantEncKey);
			merchantKeySaltDao.addMerchantInfo(merchantKeySalt);
			resetMerchantKeyDao.saveOrUpdate(resetMerchantKey);
			
			boolean merchantKeySaltResult = merchantKeySaltDao.checkuser(user.getPayId());
			
			if(!merchantKeySaltResult){
			//if(!isSaltInserted || !isKeySaltInserted){
				//  Rollback user creation				
		     	userDao.delete(user);				
				throw new SystemException(ErrorType.INTERNAL_SYSTEM_ERROR,ErrorType.INTERNAL_SYSTEM_ERROR.getResponseMessage());
			}						
			responseActionObject.setResponseCode(ErrorType.SUCCESS.getResponseCode());
			responseActionObject.setAccountValidationID(user.getAccountValidationKey());
			responseActionObject.setEmail(user.getEmailId());
		  } else{
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
		
	private String getpayId(){
		return TransactionManager.getNewTransactionId();		
	}
		
}
