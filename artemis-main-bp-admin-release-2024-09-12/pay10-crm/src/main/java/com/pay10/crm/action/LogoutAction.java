package com.pay10.crm.action;


import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.Logger; 
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.gson.Gson;
import com.pay10.commons.user.LoginHistory;
import com.pay10.commons.user.LoginHistoryDao;
import com.pay10.commons.user.User;
import com.pay10.commons.user.UserDao;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.CrmActions;
import com.pay10.crm.actionBeans.SessionCleaner;

public class LogoutAction extends AbstractSecureAction {
	
	@Autowired
	private UserDao userDao;

	private static Logger logger = LoggerFactory.getLogger(LogoutAction.class.getName());
	private static final long serialVersionUID = 9203388873112676406L;
	@Autowired
	private LoginHistoryDao loginHistoryDao;
	
	public String logout() {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String formattedDate = sdf.format(new Date());
			
			User user = (User) sessionMap.get(Constants.USER.getValue());
			
			if(user!=null){
				//updating lastActionName as home 
				user = userDao.findPayId(user.getPayId());
				String lastActionName = CrmActions.HOME.getValue();
				user.setLastActionName(lastActionName);
				userDao.update(user);
				
				MDC.put(Constants.CRM_LOG_USER_PREFIX.getValue(), user.getEmailId());
				logger.info("logged out");
		
				LoginHistory loginHistory1=loginHistoryDao.findLastLoginHistory(user.getEmailId());

				LoginHistory loginHistory=loginHistoryDao.findLastLoginHistory(user.getEmailId());
				loginHistory.setLogoutReason("Logout by user");
				loginHistory.setLogoutTimeStamp(formattedDate);
				logger.info("loginHistory : " + new Gson().toJson(loginHistory));
				
				loginHistoryDao.update(loginHistory);
			}
			
			
		
			SessionCleaner.cleanSession(sessionMap);	
			
			return SUCCESS;
		} catch (Exception exception) {
			logger.error("Exception", exception);
			return ERROR;
		}
	}
}
