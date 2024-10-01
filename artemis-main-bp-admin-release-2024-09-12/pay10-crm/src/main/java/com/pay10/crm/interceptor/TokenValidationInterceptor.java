package com.pay10.crm.interceptor;

import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.dispatcher.Parameter;
import org.slf4j.Logger; import org.slf4j.LoggerFactory;
import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;

/**
 * @author Puneet
 *
 */
public class TokenValidationInterceptor extends AbstractInterceptor {

	private static Logger logger = LoggerFactory
			.getLogger(TokenValidationInterceptor.class.getName());
	private static final long serialVersionUID = 3443707353188932224L;
	public static final String FRONT_END_NAME = "token";
	public static final String SERVER_END_NAME = "customToken";
	public static final String INVALID_TOKEN_CODE = "invalid.token";

	@SuppressWarnings("rawtypes")
	@Override
	public String intercept(ActionInvocation invocation) {
		try {
			//logger.info("Token Validation Interceptor");
			Map session = ActionContext.getContext().getSession();
			Map<String, Parameter> params = ActionContext.getContext().getParameters();

			if (params.isEmpty()
					&& StringUtils.equalsAnyIgnoreCase(invocation.getProxy().getActionName(), "editRouterRule","onusoffusRulesSetupPayout","editRouterRulePayout", "onusoffusRulesSetup")) {
				return invocation.invoke();
			}

			Parameter tokenObjectFE = params.get(FRONT_END_NAME);
			String tokenFE="";
			if(null!=tokenObjectFE) {
				tokenFE = tokenObjectFE.getValue();
			}else {
				tokenFE = (String) session.get(FRONT_END_NAME);
				 if(StringUtils.isBlank(tokenFE)) {
					// logger.info("Tokken is empty or null "+tokenFE);
					 return INVALID_TOKEN_CODE;
				 }
			}

			String sessionToken = (String) session.get(SERVER_END_NAME);

			if (!tokenFE.equals(sessionToken)) {
				//logger.info("Token is not equals to session token "+tokenFE+"Session Token"+sessionToken);
				return INVALID_TOKEN_CODE;
			}
			return invocation.invoke();
		} catch (Exception exception) {
			//logger.error("error in token validation interceptor: ",exception);
			return Action.ERROR;
		}
	}
}
