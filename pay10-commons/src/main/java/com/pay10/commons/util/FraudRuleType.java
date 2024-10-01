package com.pay10.commons.util;

import org.apache.commons.lang3.StringUtils;

/**
 * @author Harpreet, Rajendra
 *
 */
public enum FraudRuleType {

	// These ids will directly help to identify the nature/type/behaviour of fraud
	// rules
	BLOCK_NO_OF_TXNS("5", "BLOCK_NO_OF_TXNS"), 
	
	BLOCK_TXN_AMOUNT("6", "BLOCK_TXN_AMOUNT", FieldType.AMOUNT.getName()),

	BLOCK_IP_ADDRESS("1", "BLOCK_IP_ADDRESS", FieldType.INTERNAL_CUST_IP.getName()),

	BLOCK_EMAIL_ID("4", "BLOCK_EMAIL_ID", FieldType.CUST_EMAIL.getName()),

	BLOCK_USER_COUNTRY("2", "BLOCK_USER_COUNTRY", FieldType.INTERNAL_CUST_COUNTRY_NAME.getName()),

	BLOCK_CARD_ISSUER_COUNTRY("7", "BLOCK_CARD_ISSUER_COUNTRY", FieldType.INTERNAL_CARD_ISSUER_COUNTRY.getName()),

	BLOCK_CARD_BIN("8", "BLOCK_CARD_BIN", FieldType.CARD_NUMBER.getName()),

	BLOCK_CARD_NO("9", "BLOCK_CARD_NO", FieldType.CARD_NUMBER.getName()),

	BLOCK_CARD_TXN_THRESHOLD("10", "BLOCK_CARD_TXN_THRESHOLD", FieldType.CARD_NUMBER.getName()),

	BLOCK_PHONE_NUMBER("12", "BLOCK_PHONE_NUMBER", FieldType.CUST_PHONE.getName()),

	BLOCK_TXN_AMOUNT_VELOCITY("13", "BLOCK_TXN_AMOUNT_VELOCITY", FieldType.AMOUNT.getName()),

	BLOCK_MACK_ADDRESS("15", "BLOCK_MACK_ADDRESS", FieldType.INTERNAL_CUST_MAC.getName()),

	REPEATED_MOP_TYPES("16", "REPEATED_MOP_TYPES"), 
	
	FIRST_TRANSACTIONS_ALERT("17", "FIRST_TRANSACTIONS_ALERT"),

	BLOCK_USER_STATE("18", "BLOCK_USER_STATE"), 
	
	BLOCK_USER_CITY("19", "BLOCK_USER_CITY"),

	BLOCK_REPEATED_MOP_TYPE_FOR_SAME_DETAIL("20", "BLOCK_REPEATED_MOP_TYPE_FOR_SAME_DETAIL"),
	
	BLOCK_MOP_LIMIT("21", "BLOCK_MOP_LIMIT"),
	BLOCK_TICKET_LIMIT("22", "BLOCK_TICKET_LIMIT"),
	BLOCK_TRANSACTION_LIMIT("23", "BLOCK_TRANSACTION_LIMIT"),
	BLOCK_VPA_ADDRESS("24", "BLOCK_VPA_ADDRESS", "CARD_MASK");

	private final String code;
	private final String value;
	private final String fieldName;

	private FraudRuleType(String code, String value) {
		this.code = code;
		this.value = value;
		this.fieldName = null;
	}

	private FraudRuleType(String code, String value, String fieldName) {
		this.code = code;
		this.value = value;
		this.fieldName = fieldName;
	}

	public String getCode() {
		return code;
	}

	public String getValue() {
		return value;
	}

	public String getFieldName() {
		return fieldName;
	}

	public static FraudRuleType getInstance(String fraudRule) {
		FraudRuleType fraudType = null;
		if (!StringUtils.isBlank(fraudRule)) {
			for (FraudRuleType fraudObj : FraudRuleType.values()) {
				if (fraudRule.equals(fraudObj.getValue())) {
					fraudType = fraudObj;
					break;
				}
			}
		}
		return fraudType;
	}
}