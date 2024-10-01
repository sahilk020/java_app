package com.pay10.crm.interceptor;

import java.util.Map;

import org.slf4j.Logger; import org.slf4j.LoggerFactory;

import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;
import com.pay10.commons.user.User;
import com.pay10.commons.user.UserType;
import com.pay10.commons.util.Constants;

public class SuperAdminAuthorizationInterceptor extends AbstractInterceptor {

	/**
	 * @Neeraj
	 */
	private static final long serialVersionUID = -8710333311272045683L;
	private static Logger logger = LoggerFactory.getLogger(SuperAdminAuthorizationInterceptor.class.getName());
	@Override
	public String intercept(ActionInvocation actionInvocation) throws Exception {
		try {
			Map<String, Object> sessionMap = actionInvocation.getInvocationContext().getSession();
			Object userObject = sessionMap.get(Constants.USER.getValue());
			if (null == userObject) {
				logger.info("User Object{}",userObject);
				return Action.LOGIN;
			}
			User user = (User) userObject;
			if (!(user.getUserType().equals(UserType.SUPERADMIN) || user.getUserType().equals(UserType.SUBSUPERADMIN))) {
				return Action.LOGIN;
			}
			return actionInvocation.invoke();
		} catch (Exception exception) {
			logger.error("Exception", exception);
		}
		return Action.ERROR;
	}

}
