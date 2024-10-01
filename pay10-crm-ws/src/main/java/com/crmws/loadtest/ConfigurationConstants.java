package com.crmws.loadtest;

public enum ConfigurationConstants {

	// Security Configurations
	FIELD_SEPARATOR("~"), FIELD_EQUATOR("="), DEFAULT_ENCODING_UTF_8("UTF-8");

	ConfigurationConstants(String value) {
		this.value = value;
	}

	private final String value;

	public String getValue() {
		return value;
	}

}
