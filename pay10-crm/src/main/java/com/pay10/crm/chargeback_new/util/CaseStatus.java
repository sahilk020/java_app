package com.pay10.crm.chargeback_new.util;

public enum CaseStatus {

	OPEN						("Open","Open"),
	INPROGRESS					("In Progress", "In Progress"),
	ACCEPTED					("Accepted", "Accepted"),
	CLOSE						("Closed","Closed");
	
	private final String name;
	private final String code;
	private CaseStatus(String name, String code){
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
