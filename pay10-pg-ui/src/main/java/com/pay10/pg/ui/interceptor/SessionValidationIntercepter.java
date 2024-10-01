package com.pay10.pg.ui.interceptor;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;
import com.pay10.commons.user.User;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.UserStatusType;

/**
 * @author Puneet
 * 
 */

public class SessionValidationIntercepter extends AbstractInterceptor {

	private static Logger logger = LoggerFactory.getLogger(SessionValidationIntercepter.class.getName());
	private static final long serialVersionUID = 6475203929530326688L;

	@Override
	public String intercept(ActionInvocation actionInvocation) {
		//logger.info("Session Validation Interceptor");
		try {
			Map<String, Object> sessionMap = actionInvocation
					.getInvocationContext().getSession();
			Object userObject = sessionMap.get(Constants.USER.getValue());
			if (null == userObject) {
				//logger.info("User Object is null");
				return Action.LOGIN;
			}

			User user = (User) userObject;
			if(!(user.getUserStatus().equals(UserStatusType.ACTIVE)
					|| user.getUserStatus().equals(UserStatusType.TRANSACTION_BLOCKED))){
				return Action.LOGIN;
			}

			return actionInvocation.invoke();
		} catch (Exception exception) {
			//logger.error("Exception", exception);
			return Action.ERROR;
		}
	}
}
