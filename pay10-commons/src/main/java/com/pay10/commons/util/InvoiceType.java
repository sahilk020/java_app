package com.pay10.commons.util;

public enum InvoiceType {
	
	SINGLE_PAYMENT 			("SINGLE_PAYMENT"),
	PROMOTIONAL_PAYMENT 	("PROMOTIONAL_PAYMENT");
	
	
	
private final String name; 
	
	private InvoiceType(String name){
		this.name = name;
	}

	public String getName() {
		return name;
	}	
}

