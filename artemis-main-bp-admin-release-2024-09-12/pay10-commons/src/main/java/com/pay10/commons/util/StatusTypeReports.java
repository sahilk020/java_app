package com.pay10.commons.util;

public enum StatusTypeReports {
	
	SUCCESS("Success"),
	FAILED("Failed");
	
	private final String code;
	
	private StatusTypeReports(String code){
		this.code = code;
	}

	public String getCode() {
		return code;
	}
	

}
