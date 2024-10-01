package com.pay10.pg.action;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.json.JSONUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

/**
 * 
 * @author shubhamchauhan
 *
 */
public class AxisUpiMandateAction extends AbstractSecureAction implements ServletRequestAware {
//	Refer to ResponseAction.java
	private static Logger logger = LoggerFactory.getLogger(AxisUpiMandateAction.class.getName());
	private static final long serialVersionUID = -234334014110013753L;
	
	private String response;
	private HttpServletRequest httpRequest;
	
	@Override
	public void setServletRequest(HttpServletRequest hReq) {
		this.httpRequest = hReq;
	}
	
	public String execute() {
		try {
		Object obj = JSONUtil.deserialize(httpRequest.getReader());
		// conveting object to json
		ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
		String json = ow.writeValueAsString(obj);
		logger.info(json);
		setResponse("Success");
		return SUCCESS;
		}catch (Exception e) {
			logger.error("Failed to get mandate callback response");
			logger.error(e.getMessage());
			e.printStackTrace();
			setResponse("Failed");
			return SUCCESS;
		}
	}


	public String getResponse() {
		return response;
	}


	public void setResponse(String response) {
		this.response = response;
	}
}
