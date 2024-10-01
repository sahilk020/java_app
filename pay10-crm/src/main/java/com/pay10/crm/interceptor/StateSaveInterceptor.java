package com.pay10.crm.interceptor;

import java.util.Map;

import org.slf4j.Logger; import org.slf4j.LoggerFactory;

import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.CrmActions;
import com.pay10.commons.util.CrmFieldConstants;

/**
 * @author Harpreet
 *
 */
public class StateSaveInterceptor extends AbstractInterceptor {
	 
	private static final long serialVersionUID = -7169667296028325138L;
	private static Logger logger = LoggerFactory.getLogger(StateSaveInterceptor.class.getName());

 
	@Override
	public String intercept(ActionInvocation actionInvocation) throws Exception {
		 
		try {
			//logger.info("Final State Save Interceptor");
			Map<String, Object> sessionMap = actionInvocation
					.getInvocationContext().getSession();
			String lastActionName = ActionContext.getContext().getName();
			
			//checking action name from white List (CrmActions)
			CrmActions[] whiteActions = CrmActions.values();
			for(CrmActions action:whiteActions){
				if(action.getValue().equalsIgnoreCase(lastActionName)){
					// adding session attribute to save last activity state
					sessionMap.put(CrmFieldConstants.LAST_ACTION_NAME.getValue(), lastActionName);
					break;
				}
			}
			Object userObject = sessionMap.get(Constants.USER.getValue());
			//logger.info("Returning home "+lastActionName+" User Object ",userObject);
			if (null != userObject && (lastActionName.equalsIgnoreCase("merchantSignup") || lastActionName.equalsIgnoreCase("forgetPassword"))) {
			//	logger.info("Return Home");
				return "home";
			}
			
			return actionInvocation.invoke();
		} catch (Exception exception) {
			//logger.error("Exception", exception);
			return Action.ERROR;
		}
	}

	
	
}
