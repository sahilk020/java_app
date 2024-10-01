package com.pay10.crm.session;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger; import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.gson.Gson;
import com.pay10.commons.user.LoginHistory;
import com.pay10.commons.user.LoginHistoryDao;
import com.pay10.commons.user.User;
import com.pay10.commons.user.UserDao;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.CrmFieldConstants;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;

/**
 * @author Surender
 *
 */

public class SessionEventListener implements HttpSessionListener{
	private static Logger logger = LoggerFactory.getLogger(SessionEventListener.class
			.getName());
	@Autowired
	private SessionTimeoutHandler timeOutHandler; 
	@Autowired
	private LoginHistoryDao loginHistoryDao;
	@Override
	public void sessionCreated(HttpSessionEvent event) {
		
		System.out.println("Session Create : " + new Gson().toJson(event.getSession()));
		// Do nothing
	}
	// Session Destroyed
	@Override
	public void sessionDestroyed(HttpSessionEvent event) {
	 	try {
	 		logger.info("sessionDestroyed : " + new Gson().toJson(event.getSession()));
	 		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String formattedDate = sdf.format(new Date());
			Object sessionObj = event.getSession();
			HttpSession session = (HttpSession) sessionObj;
			System.out.println("Seesion OBJ print  : " + new Gson().toJson(session));
			String txnCompleteFlag = null;
			String origTxnId = null;
			Fields fields = null;
			User user;
			UserDao userDao = new UserDao();
			
			Object fieldsObj = session
					.getAttribute(Constants.FIELDS.getValue());
			logger.info("sessionDestroyed 1 " + new Gson().toJson(fieldsObj));
			if (null == fieldsObj) {
				 //saving state of user activity to database
				 	Object userObj =  session.getAttribute(Constants.USER.getValue());
				 	logger.info("sessionDestroyed 2 " + new Gson().toJson(userObj));
				 	if(null!= userObj){
				 		user = (User) userObj;
						user = userDao.findPayId(user.getPayId());
						String lastActionName =(String) session.getAttribute(CrmFieldConstants.LAST_ACTION_NAME.getValue());
						user.setLastActionName(lastActionName);
						userDao.update(user);
						
						LoginHistory loginHistory=loginHistoryDao.findLastLoginHistory(user.getEmailId());
						loginHistory.setLogoutReason("Session time out");
						loginHistory.setLogoutTimeStamp(formattedDate);
						logger.info("loginHistory : " + new Gson().toJson(loginHistory));
						
						loginHistoryDao.update(loginHistory);
						return;
				 	}else{
				 		return;
				 	}
				 	
			} else {
				fields = (Fields) fieldsObj;
				logger.info("sessionDestroyed 3 " + new Gson().toJson(fields));
			}

			Object origTxnIdObj = null;
			origTxnIdObj = session
					.getAttribute(FieldType.INTERNAL_ORIG_TXN_ID.getName());

			if (null != origTxnIdObj) {
				origTxnId = (String) origTxnIdObj;
				fields.put(FieldType.INTERNAL_ORIG_TXN_ID.getName(), origTxnId);
			} else {
				origTxnId = fields.get(FieldType.INTERNAL_ORIG_TXN_ID.getName());
				//Put txn id as ORIG_TXN_ID if not found
				if(StringUtils.isEmpty(origTxnId)){					
					fields.put(FieldType.INTERNAL_ORIG_TXN_ID.getName(),
							fields.get(FieldType.TXN_ID.getName()));
				}
				
			}

			Object txnCompleteFlagObj = session
					.getAttribute(Constants.TRANSACTION_COMPLETE_FLAG
							.getValue());

			if (null != txnCompleteFlagObj) {
				txnCompleteFlag = (String) txnCompleteFlagObj;
				fields.put(Constants.TRANSACTION_COMPLETE_FLAG.getValue(),
						txnCompleteFlag);
			} else {
				fields.put(Constants.TRANSACTION_COMPLETE_FLAG.getValue(),
						Constants.N_FLAG.getValue());
			}
			logger.info("sessionDestroyed 4 " + new Gson().toJson(fields));
			timeOutHandler.handleTimeOut(fields);
		} catch (Exception exception) {
			logger.error("Error processing timeout " + exception);
		} 
	}
}
