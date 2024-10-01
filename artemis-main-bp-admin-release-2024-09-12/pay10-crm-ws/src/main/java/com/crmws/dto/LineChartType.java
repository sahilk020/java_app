package com.crmws.dto;


public enum LineChartType {

	STATUS_CHART("STATUS_CHART", "STATUS"),
	ACQUIRER_CHART("ACQUIRER_CHART", "ACQUIRER_TYPE"),
	PAYMENT_TYPE_CHART("PAYMENT_TYPE_CHART", "PAYMENT_TYPE"),
	MOP_TYPE_CHART("MOP_TYPE_CHART", "MOP_TYPE");
	
	private final String name;
	private final String field;

	LineChartType(String name, String field) {
		this.name = name;
		this.field = field;
	}

	public String getName() {
		return name;
	}

	public String getField() {
		return field;
	}
	
	
	
}
