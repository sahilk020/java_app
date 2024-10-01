package com.pay10.pg.action;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KotakOptAction extends AbstractSecureAction{

	private static Logger logger = LoggerFactory.getLogger(MerchantPaymentLinkAction.class.getName());

	String orderId;
	String PostUrl;

	
	
	@Override
	public String execute() {

		try {
			logger.info("kotak opt page"+orderId);
			return "OptPage";
			
		} catch (Exception exception) {
			logger.error("Error kotak  opt page", exception);
			return ERROR;
		}}



	public String getOrderId() {
		return orderId;
	}



	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}



	public String getPostUrl() {
		return PostUrl;
	}



	public void setPostUrl(String postUrl) {
		PostUrl = postUrl;
	}
	
	
	

}
