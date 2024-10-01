package com.pay10.pg.action;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.interceptor.ServletRequestAware;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pay10.commons.user.Token;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.FieldType;

public class DisplayUpiLoadingPage extends AbstractSecureAction implements ServletRequestAware {

	private static final long serialVersionUID = -2384590724876324293L;
	private static final Logger logger = LoggerFactory.getLogger(DisplayUpiLoadingPage.class.getName());

	private String orderId;
	private String txnType;
	private HttpServletRequest request;
	private String token ;

	@SuppressWarnings("unchecked")
	@Override
	public String execute() {
		logger.info("orderId={}, txnType={}, token={}", getOrderId(), getTxnType(),sessionMap.get(Constants.CUSTOM_TOKEN.getValue()));
		setToken((String)sessionMap.get(Constants.CUSTOM_TOKEN.getValue()));
		return SUCCESS;
	}

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public String getTxnType() {
		return txnType;
	}

	public void setTxnType(String txnType) {
		this.txnType = txnType;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
		
	}

	
	
}
