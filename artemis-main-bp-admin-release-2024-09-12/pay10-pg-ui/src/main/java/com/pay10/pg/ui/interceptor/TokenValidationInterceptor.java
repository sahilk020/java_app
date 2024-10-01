package com.pay10.pg.ui.interceptor;

import java.util.Map;

import org.apache.struts2.dispatcher.Parameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;

/**
 * @author Puneet
 *
 */
public class TokenValidationInterceptor extends AbstractInterceptor {

	private static Logger logger = LoggerFactory.getLogger(TokenValidationInterceptor.class.getName());
	private static final long serialVersionUID = 3443707353188932224L;
	private static final String FRONT_END_NAME = "customToken";
	public static final String SERVER_END_NAME = "customToken";
	public static final String INVALID_TOKEN_CODE = "invalid.token";

	@SuppressWarnings("rawtypes")
	@Override
	public String intercept(ActionInvocation invocation) {
		try {

			Map<String, Parameter> params = ActionContext.getContext().getParameters();

			Parameter tokenObjectFE = params.get(FRONT_END_NAME);
			String tokenFE = tokenObjectFE.getValue();
			if (null == tokenFE) {
				//logger.info("tokenFE null");
				return INVALID_TOKEN_CODE;
			}
			Map session = ActionContext.getContext().getSession();
			String sessionToken = (String) session.get(SERVER_END_NAME);

			if (!tokenFE.equals(sessionToken)) {
				//logger.info(tokenFE+" TokeFE is not Equals to sessionToken "+sessionToken);
				return INVALID_TOKEN_CODE;
			}
			return invocation.invoke();
		} catch (Exception exception) {
		//	logger.error("exception",exception);
			return Action.ERROR;
		}
	}
}
