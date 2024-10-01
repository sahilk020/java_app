package com.pay10.commons.dto;


public class WebhookResp {
	
	private boolean status = false;
    private String message;
    private String responseCode;
    private Object data;
	public boolean isStatus() {
		return status;
	}
	public void setStatus(boolean status) {
		this.status = status;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getResponseCode() {
		return responseCode;
	}
	public void setResponseCode(String responseCode) {
		this.responseCode = responseCode;
	}
	public Object getData() {
		return data;
	}
	public void setData(Object data) {
		this.data = data;
	}
	public WebhookResp() {
		
	}
	public WebhookResp(boolean status, String message, String responseCode, Object data) {
		
		this.status = status;
		this.message = message;
		this.responseCode = responseCode;
		this.data = data;
	}
    
    

}
