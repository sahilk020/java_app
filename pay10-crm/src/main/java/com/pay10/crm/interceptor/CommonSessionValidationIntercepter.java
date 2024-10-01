package com.pay10.crm.interceptor;

import java.util.Map;

import org.slf4j.Logger; import org.slf4j.LoggerFactory;

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

public class CommonSessionValidationIntercepter extends AbstractInterceptor {

	private static final long serialVersionUID = 2602636143018673409L;
	private static Logger logger = LoggerFactory.getLogger(CommonSessionValidationIntercepter.class.getName());


	@Override
	public String intercept(ActionInvocation actionInvocation) {
		try {
		//	logger.info("Common Session Validation Intercepter in Home Action");
			Map<String, Object> sessionMap = actionInvocation
					.getInvocationContext().getSession();
			Object userObject = sessionMap.get(Constants.USER.getValue());

			if (null == userObject) {
				//logger.info("User Object is null return login");
				return Action.LOGIN;
			}

			User user = (User) userObject;
			if(!(user.getUserStatus().equals(UserStatusType.ACTIVE)
					|| user.getUserStatus().equals(UserStatusType.TRANSACTION_BLOCKED) || user.getUserStatus().equals(UserStatusType.SUSPENDED))){
				return Action.LOGIN;
			}

			return actionInvocation.invoke();
		} catch (Exception exception) {
		//	logger.error("Exception", exception);
			return Action.ERROR;
		}
	}
}
