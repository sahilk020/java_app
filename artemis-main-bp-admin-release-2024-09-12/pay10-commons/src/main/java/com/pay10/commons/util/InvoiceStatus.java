package com.pay10.commons.util;

public enum InvoiceStatus {
	
	PAID 			("PAID"),
	UNPAID			("UNPAID"),
	ATTEMPTED 		("ATTEMPTED"),
	IN_PROCESS		("IN_PROCESS"),
	EXPIRED			("EXPIRED");
	
	
	private final String name;
	
	private InvoiceStatus(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}	
	
}
