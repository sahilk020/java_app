package com.pay10.commons.util;

public enum AccountEntryType {

	CREDIT ("CREDIT"),
	DEBIT ("DEBIT"),
	FAILED			("FAILED"),
	
	;
	
	private AccountEntryType(String name) {
		this.setName(name);
	}

	private String name;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
}
