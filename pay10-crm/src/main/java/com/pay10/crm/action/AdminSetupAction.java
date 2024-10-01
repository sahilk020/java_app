package com.pay10.crm.action;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.opensymphony.xwork2.ModelDriven;
import com.pay10.commons.user.User;
import com.pay10.commons.user.UserDao;

/**
 * @ Rajendra
 */


public class AdminSetupAction extends AbstractSecureAction {
		
	@Autowired
	private UserDao userDao;
	
	private static final long serialVersionUID = 8870322503068475573L;
	private static Logger logger = LoggerFactory.getLogger(AdminSetupAction.class.getName());
	private User user = new User();
	private String emailId;
	
	@Override
	public String execute() {
		try {
		  setUser(userDao.findPayIdByEmail(getEmailId()));
		  getUser();
			return SUCCESS;
		} catch (Exception exception) {
			logger.error("Exception", exception);
			return ERROR;     
		}
	}
	
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getEmailId() {
		return emailId;
	}
	
	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}
	
	

}
