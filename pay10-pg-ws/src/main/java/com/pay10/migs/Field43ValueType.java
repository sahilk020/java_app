package com.pay10.migs;

import com.pay10.commons.util.EmailerConstants;

public enum Field43ValueType {
	TRADING_NAME			("businessName", "vpc_SubMerchant_TradingName", Constants.FIELD_43_TRADING_NAME),
	REGISTERED_NAME			("companyName", "vpc_SubMerchant_RegisteredName", Constants.FIELD_43_REGISTERED_NAME),
	SUB_MERCHANT_ID     	("amexSellerId","vpc_SubMerchant_ID", Constants.FIELD_43_SUB_MERCHANT_ID),
	STREET					("address","vpc_SubMerchant_Street", Constants.FIELD_43_STREET),
	POSTAL_CODE				("postalCode","vpc_SubMerchant_PostCode", Constants.FIELD_43_POSTAL_CODE),
	CITY					("city","vpc_SubMerchant_City", Constants.FIELD_43_CITY),
	STATE					("state","vpc_SubMerchant_StateProvince", Constants.FIELD_43_STATE),
	COUNTRY					("country","vpc_SubMerchant_Country", Constants.FIELD_43_COUNTRY),
	PHONE					("mobile","vpc_SubMerchant_Phone",EmailerConstants.PHONE_NO.getValue()),
	EMAIL_ID				("emailId","vpc_SubMerchant_Email",EmailerConstants.CONTACT_US_EMAIL.getValue()),
	MCC						("mCC","vpc_SubMerchant_MerchantCategoryCode","1111");

	private final String fieldName;
	private final String vpcName;
	private final String dummyValue;

	private Field43ValueType(String fieldName, String vpcName, String dummyValue){
		this.fieldName = fieldName;
		this.vpcName = vpcName;
		this.dummyValue = dummyValue;
	}

	public static Field43ValueType getInstanceFieldName(String fieldName){
		for(Field43ValueType value:Field43ValueType.values()){
			if(value.getFieldName().equals(fieldName)){
				return value;
			}
		}
		return null;
	}

	public String getFieldName() {
		return fieldName;
	}

	public String getVpcName() {
		return vpcName;
	}

	public String getDummyValue() {
		return dummyValue;
	}
}
