package com.pay10.crm.interceptor;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts2.ServletActionContext;
import org.apache.struts2.interceptor.TokenInterceptor;
import org.apache.struts2.util.InvocationSessionStore;
import org.apache.struts2.util.TokenHelper;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.Result;
import com.opensymphony.xwork2.util.ValueStack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestTokenValidationInterceptor  extends TokenInterceptor {



	private static final long serialVersionUID = -2472738956231297786L;
    Logger logger= LoggerFactory.getLogger(RequestTokenValidationInterceptor.class.getName());

	@Override
    protected String handleToken(ActionInvocation invocation) throws Exception {
        //see WW-2902: we need to use the real HttpSession here, as opposed to the map
        //that wraps the session, because a new wrap is created on every request
        HttpSession session = ServletActionContext.getRequest().getSession(true);
       logger.info("Request Token validation interceptor",session);
        synchronized (session.getId().intern()) {
            logger.info("Request Token Validation interceptor{} ",session);

            if (!TokenHelper.validToken()) {
                logger.info("Token Helper validToken return null");
                return handleInvalidToken(invocation); 
            }
            return handleValidToken(invocation);
        }
    }

    @Override
    protected String handleInvalidToken(ActionInvocation invocation) throws Exception {
        ActionContext ac = invocation.getInvocationContext();

        HttpServletRequest request = (HttpServletRequest) ac.get(ServletActionContext.HTTP_REQUEST);
        HttpServletResponse response = (HttpServletResponse) ac.get(ServletActionContext.HTTP_RESPONSE);
        String tokenName = TokenHelper.getTokenName();
        logger.info("HTTP Request Token name "+tokenName);

        String token = TokenHelper.getToken(tokenName);
        logger.info("Token Helper Token Name "+token);
        if ((tokenName != null) && (token != null)) {
            Map params = ac.getParameters();
            params.remove(tokenName);
            params.remove(TokenHelper.TOKEN_NAME_FIELD);

			String sessionTokenName = TokenHelper.buildTokenSessionAttributeName(tokenName);
            ActionInvocation savedInvocation = InvocationSessionStore.loadInvocation(sessionTokenName, token);

            if (savedInvocation != null) {
                // set the valuestack to the request scope
                ValueStack stack = savedInvocation.getStack();
                request.setAttribute(ServletActionContext.STRUTS_VALUESTACK_KEY, stack);

                ActionContext savedContext = savedInvocation.getInvocationContext();
                savedContext.getContextMap().put(ServletActionContext.HTTP_REQUEST, request);
                savedContext.getContextMap().put(ServletActionContext.HTTP_RESPONSE, response);
                Result result = savedInvocation.getResult();

                if ((result != null) && (savedInvocation.getProxy().getExecuteResult())) {
                    result.execute(savedInvocation);
                }

                // turn off execution of this invocations result
                invocation.getProxy().setExecuteResult(false);

                return savedInvocation.getResultCode();
            }
        }
        logger.info("Tokken is "+token+" In Request Token Validation Interceptor");
        return INVALID_TOKEN_CODE;
    }

    @Override
    protected String handleValidToken(ActionInvocation invocation) throws Exception {
        // we know the token name and token must be there
        String key = TokenHelper.getTokenName();
        String token = TokenHelper.getToken(key);
		String sessionTokenName = TokenHelper.buildTokenSessionAttributeName(key);
		InvocationSessionStore.storeInvocation(sessionTokenName, token, invocation);
        logger.info("Handle Valid Token Request Token Validation interceptor Key "+key+"token "+token+" Session token Name "+sessionTokenName);

        return invocation.invoke();
    }

}
