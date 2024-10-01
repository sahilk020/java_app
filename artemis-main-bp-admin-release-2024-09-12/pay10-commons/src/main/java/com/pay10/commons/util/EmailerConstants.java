package com.pay10.commons.util;

public enum EmailerConstants {

	CONTACT_US_EMAIL ("no-reply@bpgate.com"),
	GATEWAY ("BestPay"),
	COMPANY ("BestPay."),
	WEBSITE ("api.bpgate.net"),
	PHONE_NO ("");

	private final String value;

	public String getValue() {
		return value;
	}

	private EmailerConstants(String value){
		this.value = value;
	}
}
