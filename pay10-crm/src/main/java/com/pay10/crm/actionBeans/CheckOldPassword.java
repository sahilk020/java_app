package com.pay10.crm.actionBeans;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger; import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pay10.commons.user.UserRecordsDao;

/**
 * @author Chandan
 */

@Service
public class CheckOldPassword {
	
	private Logger logger = LoggerFactory.getLogger(CheckOldPassword.class.getName());
	
	@Autowired
	private UserRecordsDao userRecordsDao;

	public  boolean isUsedPassword(String newPassword, String emailId) {
		
		logger.info("Inside CheckOldPassword , emailId = " + emailId);
		List<String> oldPasswords = new ArrayList<String>();
		oldPasswords = userRecordsDao.getOldPasswords(emailId);
		if (oldPasswords.isEmpty()){
			logger.info("old passwords is empty");
		}
		if (!oldPasswords.isEmpty()){
			for (String password : oldPasswords) {
				if (null == password) {
					continue;
				}
				if (password.equals(newPassword)) {
					return true;
				}
			}
		}
		
		return false;
	}
}
