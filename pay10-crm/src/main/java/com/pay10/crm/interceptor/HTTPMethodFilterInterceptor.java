package com.pay10.crm.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.ServletActionContext;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;
import com.pay10.commons.util.CrmFieldConstants;
import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HTTPMethodFilterInterceptor  extends AbstractInterceptor {

	private static final long serialVersionUID = 8474751470406698684L;
	Logger logger= LoggerFactory.getLogger(HTTPMethodFilterInterceptor.class.getName());

	@Override
	public String intercept(ActionInvocation actionInvocation) throws Exception {
		HttpServletRequest	request = ServletActionContext.getRequest();
		if(request.getMethod().equals(CrmFieldConstants.HTTP_POST_METHOD.getValue())){
	 		return actionInvocation.invoke();
		}
		logger.info(request.getMethod());
		logger.info("Invalid HTTP Method return login");
		HttpServletResponse response = ServletActionContext.getResponse();
        response.setStatus(400);
		return "login";	
	}
}
