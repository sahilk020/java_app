package com.pay10.commons.util;

public enum PayinPaymentS2SStatus {
	
	PAID 			("PAID"),
	UNPAID			("UNPAID"),
	ATTEMPTED 		("ATTEMPTED"),
	IN_PROCESS		("IN_PROCESS"),
	EXPIRED			("EXPIRED");
	
	
	private final String name;
	
	private PayinPaymentS2SStatus(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}	
	
}
