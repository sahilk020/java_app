package com.pay10.commons.dto;

public class RestMerchantResponse {
	private String orderId;
	private String redirectUrl;
	private String responseCode;
	private String responseMessage;

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public String getRedirectUrl() {
		return redirectUrl;
	}

	public void setRedirectUrl(String redirectUrl) {
		this.redirectUrl = redirectUrl;
	}

	public String getResponseCode() {
		return responseCode;
	}

	public void setResponseCode(String responseCode) {
		this.responseCode = responseCode;
	}

	public String getResponseMessage() {
		return responseMessage;
	}

	public void setResponseMessage(String responseMessage) {
		this.responseMessage = responseMessage;
	}

	public RestMerchantResponse() {
		super();
	}

	public RestMerchantResponse(String orderId, String redirectUrl, String responseCode, String responseMessage) {
		super();
		this.orderId = orderId;
		this.redirectUrl = redirectUrl;
		this.responseCode = responseCode;
		this.responseMessage = responseMessage;
	}

	@Override
	public String toString() {
		return "RestMerchantResponse [orderId=" + orderId + ", redirectUrl=" + redirectUrl + ", responseCode="
				+ responseCode + ", responseMessage=" + responseMessage + "]";
	}

}
