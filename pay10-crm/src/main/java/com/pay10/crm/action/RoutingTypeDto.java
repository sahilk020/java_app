package com.pay10.crm.action;

public class RoutingTypeDto {
	
	String name;
	String value;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public RoutingTypeDto(String name, String value) {
		super();
		this.name = name;
		this.value = value;
	}
	
	
	

	
}
