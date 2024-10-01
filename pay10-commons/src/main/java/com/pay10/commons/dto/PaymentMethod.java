package com.pay10.commons.dto;

import java.util.Map;

import org.springframework.http.HttpStatus;

public class PaymentMethod {
	private String respmessage;
	private HttpStatus httpStatus;
	public Map<String, String> multipleResponse;
	
	public String getRespmessage() {
		return respmessage;
	}
	public void setRespmessage(String respmessage) {
		this.respmessage = respmessage;
	}
	public HttpStatus getHttpStatus() {
		return httpStatus;
	}
	public void setHttpStatus(HttpStatus httpStatus) {
		this.httpStatus = httpStatus;
	}
	public Map<String, String> getMultipleResponse() {
		return multipleResponse;
	}
	public void setMultipleResponse(Map<String, String> multipleResponse) {
		this.multipleResponse = multipleResponse;
	}
	
	
}
