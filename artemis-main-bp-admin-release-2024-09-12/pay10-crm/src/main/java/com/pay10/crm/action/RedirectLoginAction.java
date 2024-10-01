package com.pay10.crm.action;

import com.pay10.commons.user.LoginHistory;
import com.pay10.commons.user.LoginHistoryDao;
import com.pay10.commons.user.User;
import com.pay10.commons.user.UserDao;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.CrmActions;
import com.pay10.crm.actionBeans.SessionCleaner;
import org.apache.tomcat.util.bcel.Const;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.SimpleDateFormat;
import java.util.Date;

public class RedirectLoginAction extends AbstractSecureAction {


    @Autowired
    LoginHistoryDao loginHistoryDao;

    @Autowired
    UserDao userDao;
    Logger logger = LoggerFactory.getLogger(RedirectLoginAction.class.getName());

    @Override
    public String execute() {
        try {
            User user = (User) sessionMap.get(Constants.USER.getValue());
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String logoutTime = sdf.format(new Date());
            if (user != null) {
                user = userDao.findByPayId(user.getPayId());
                user.setLastActionName(CrmActions.HOME.name());
                userDao.update(user);
                MDC.put(Constants.CRM_LOG_USER_PREFIX.getValue(), user.getEmailId());
                LoginHistory loginHistory = loginHistoryDao.findLastLoginHistory(user.getEmailId());
                loginHistory.setLogoutReason("Logout Due to Inactivity");
                loginHistory.setLogoutTimeStamp(logoutTime);
                loginHistoryDao.update(loginHistory);
                SessionCleaner.cleanSession(sessionMap);
                logger.info("User Logged out Successfully");

            }
        } catch (Exception e) {
            logger.error("Exception while Redirect Login ", e);
        }
        return SUCCESS;

    }
}
