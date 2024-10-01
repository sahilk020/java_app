package com.crmws.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PayinPaymentS2SResponse {

	@JsonProperty("ORDER_ID")
	private String orderId;

	@JsonProperty("PAYMENT_REDIRECT_URL")
	private String redirectUrl;

	@JsonProperty("RESP_MESSAGE")
	private String respMessage;

	@JsonProperty("RESP_CODE")
	private String respCode;

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

	public String getRespMessage() {
		return respMessage;
	}

	public void setRespMessage(String respMessage) {
		this.respMessage = respMessage;
	}

	public String getRespCode() {
		return respCode;
	}

	public void setRespCode(String respCode) {
		this.respCode = respCode;
	}

	public PayinPaymentS2SResponse(String orderId, String redirectUrl, String respMessage, String respCode) {
		super();
		this.orderId = orderId;
		this.redirectUrl = redirectUrl;
		this.respMessage = respMessage;
		this.respCode = respCode;
	}

	public PayinPaymentS2SResponse() {
		super();
		// TODO Auto-generated constructor stub
	}

	@Override
	public String toString() {
		return "PayinPaymentS2SResponse [orderId=" + orderId + ", redirectUrl=" + redirectUrl + ", respMessage=" + respMessage
				+ ", respCode=" + respCode + "]";
	}

}
