package com.pay10.crm.chargeback_new.util;

public enum ChargebackStatus {

	NEW							("New", "New"),
	ACCEPTED_BY_MERCHANT		("Accepted by merchant", "Accepted by merchant"),
	DISPUTES					("Disputes", "Disputes"),
	CONTESTED					("Contested","Contested");
		
	private final String name;
	private final String code;
	private ChargebackStatus(String name, String code){
		this.name = name;
		this.code = code;
		
	}

	public String getName() {
		return name;
	}

	public String getCode(){
		return code;
	}


}
