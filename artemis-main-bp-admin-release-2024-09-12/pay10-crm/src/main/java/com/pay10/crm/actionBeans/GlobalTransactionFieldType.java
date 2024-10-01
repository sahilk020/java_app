package com.pay10.crm.actionBeans;

public class GlobalTransactionFieldType {

	private String fieldName;
	private String fieldValue;
	
	public String getFieldName() {
		return fieldName;
	}
	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}
	public String getFieldValue() {
		return fieldValue;
	}
	public void setFieldValue(String fieldValue) {
		this.fieldValue = fieldValue;
	}
	public GlobalTransactionFieldType(String fieldName, String fieldValue) {
		super();
		this.fieldName = fieldName;
		this.fieldValue = fieldValue;
	}
	
	
	
}
