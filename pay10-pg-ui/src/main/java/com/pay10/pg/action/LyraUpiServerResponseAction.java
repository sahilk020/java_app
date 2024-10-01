package com.pay10.pg.action;

import java.io.BufferedReader;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.interceptor.ServletRequestAware;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.opensymphony.xwork2.Action;

public class LyraUpiServerResponseAction extends AbstractSecureAction implements ServletRequestAware {

	private static final long serialVersionUID = 1741520724921491462L;

	private HttpServletRequest httpRequest;

	private static Logger logger = LoggerFactory.getLogger(LyraUpiServerResponseAction.class.getName());

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.httpRequest = request;
	}

	public String execute() {

		
		// This will just capture the S2S response for logging purposes
		try {
			BufferedReader inputBuffered = httpRequest.getReader();
			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = inputBuffered.readLine()) != null) {
				response.append(inputLine);
			}
			inputBuffered.close();
			logger.info("Lyra UPI Callback Response >>> " + response.toString());
		} catch (Exception e) {
			logger.error("Error in Lyra UPI callback = ", e);
		}

		return Action.NONE;
	}
}
