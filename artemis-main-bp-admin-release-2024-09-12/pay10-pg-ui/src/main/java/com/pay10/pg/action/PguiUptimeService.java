package com.pay10.pg.action;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.interceptor.ServletRequestAware;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.pay10.commons.api.TransactionControllerServiceProvider;
import com.pay10.commons.exception.SystemException;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.PropertiesManager;

/**
 * 
 * @author shubhamchauhan
 *
 */
public class PguiUptimeService extends AbstractSecureAction  implements ServletRequestAware {
	private static Logger logger = LoggerFactory.getLogger(PguiUptimeService.class.getName());
	private static final long serialVersionUID = -8994652842808087554L;
	private HttpServletRequest httpRequest;
	private String response;
	
	@Autowired
	TransactionControllerServiceProvider tcsp;
	
	public String execute() {
		setResponse("Success");
		return SUCCESS;
	}
	
	public String statusCake() {
		try {
			String reqToken = httpRequest.getHeader("Authorization");
			String uptimeToken = PropertiesManager.propertiesMap.get(Constants.UPTIME_TOKEN.getValue());
			if(uptimeToken == null || !uptimeToken.equals(reqToken)) {
				logger.info("Request Token : " + reqToken);
				logger.error("Token mismatch");
				setResponse("Token mismatch");
				return "400";
			}
			String responseCode = String.valueOf(tcsp.uptimeService());
			setResponse(responseCode);
			return responseCode;
		} catch (Exception e) {
			logger.error("Failed to connect to pgws.");
		}
		setResponse("Failed to connect to WS");
		return "500";
	}

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.httpRequest = request;
	}

	public String getResponse() {
		return response;
	}

	public void setResponse(String response) {
		this.response = response;
	}
}
