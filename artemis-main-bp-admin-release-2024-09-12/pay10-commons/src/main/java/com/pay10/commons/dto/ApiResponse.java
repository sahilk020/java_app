package com.pay10.commons.dto;

public class ApiResponse {

	private boolean status;
	private String message;
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

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}

	public ApiResponse() {
		super();
	}

	public ApiResponse(boolean status, String message, Object data) {
		super();
		this.status = status;
		this.message = message;
		this.data = data;
	}

	@Override
	public String toString() {
		return "ApiResponse [status=" + status + ", message=" + message + ", data=" + data + "]";
	}

}
