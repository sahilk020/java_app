package com.pay10.commons.util;

/**
 * @author R
 *
 */
public enum CurrencyNumber {

	// Credit Card
	INR("356", "2","100"),
	MYR("458", "4","10000"),
	VND("704", "5","100000"),
	PHP("608", "3","1000"),
	THB("764", "4","10000"),
	CNY("156", "3","1000"),
	USD("840", "2","100"),
	EUR("978", "2","100"),
	AED("784", "2","100"),
	AUD("036", "2","100"),
	CAD("124", "2","100"),
	GBP("826", "2","100"),
	BDT("050", "2","100"),
	BHD("048", "3","1000"),
	CHF("756", "2","100"),
	JPY("392", "0","1"),
	KES("404", "2","100"),
	KWD("414", "3","1000"),
	LKR("144", "2","100"),
	NPR("524", "2","100"),
	QAR("634", "2","100"),
	ZAR("710", "2","100"),
	NGN("566", "2","100"),
	SAR("682", "2","100"),
	OMR("512", "3","1000"),
	SGD("702", "2","100"),
	EGP("818", "2","100"),
	ZMW("967", "2","100"),
	RUB("643", "2","100"),
	GHS("936", "2","100"),
	UGX("800", "0","1"),
	TND("788", "3","1000"),
	UAH("980", "2","100"),
	JOD("400", "3","1000"),
	MAD("504", "2","100"),
	IQD("368", "3","1000"),
	TRY("949", "2","100");

	private final String code;
	private final String decimal;
	private final String multiply;

	private CurrencyNumber(String code, String decimal,String multiply) {
		this.code = code;
		this.decimal = decimal;
		this.multiply = multiply;
		
	}

	public String getCode() {
		return code;
	}

	public String getDecimal() {
		return decimal;
	}
	
	public String getMultiply() {
		return multiply;
	}

	
	public static CurrencyNumber getInstancefromCode(String currencyNumber) {
		CurrencyNumber currencyNumberType = null;

		for (CurrencyNumber number : CurrencyNumber.values()) {

			if (number.getCode().equalsIgnoreCase(currencyNumber)) {
				currencyNumberType = number;
				break;
			}
		}

		return currencyNumberType;
	}
	
	public static String getDecimalfromCode(String currencyNumber) {
		//System.out.println("deep : "+currencyNumber);
		String currencyNumberType = null;

		for (CurrencyNumber number : CurrencyNumber.values()) {

			if (number.getCode().equalsIgnoreCase(currencyNumber)) {
				currencyNumberType = number.getDecimal();
				break;
			}
		}

		return currencyNumberType;
	}
	
	public static String getMultiplyerfromCode(String currencyNumber) {
		String currencyNumberType = null;

		for (CurrencyNumber number : CurrencyNumber.values()) {

			if (number.getCode().equalsIgnoreCase(currencyNumber)) {
				currencyNumberType = number.getMultiply();
				break;
			}
		}

		return currencyNumberType;
	}
	public static String getDecimalfromInstance(String instance) {
		String currencyNumberType = null;

		for (CurrencyNumber number : CurrencyNumber.values()) {

			if (number.toString().equalsIgnoreCase(instance)) {
				currencyNumberType = number.getDecimal();
				break;
			}
		}

		return currencyNumberType;
	}
	
	public static void main(String[] args) {

		int points=Integer.parseInt(CurrencyNumber.getDecimalfromInstance("MYR"));
		String s="0.";
		for(int i=0;i<points;i++){
				s=s+"0";
			}
				System.out.println(s);
	}

}
