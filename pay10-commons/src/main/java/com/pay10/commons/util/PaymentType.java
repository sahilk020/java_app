package com.pay10.commons.util;

import java.util.ArrayList;
import java.util.List;

public enum PaymentType {

	CREDIT_CARD("Credit Card", "CC", true), 
	DEBIT_CARD("Debit Card", "DC", true), 
	NET_BANKING("Net Banking", "NB",true), 

	//EMI("EMI", "EM", false), 
	//WALLET("Wallet", "PPI",true), 
	WALLET("Wallet", "WL",true), 
	//RECURRING_PAYMENT("Recurring Payment", "RP", false), 
	EXPRESS_PAY("Express Pay", "EX", false),
	//DEBIT_CARD_WITH_PIN("Debit Card With Pin","DP",true),
	UPI("UPI", "UP", true),
	//AD("AutoDebit", "AD", true),
	PREPAID_CARD("Prepaid Card", "PC", true),
	QRCODE("QR CODE","QR", true),
	QR_CODE("QR CODE","QR", true),
	NB("netbank","NB", true),

	EMI("EMI", "EMI", false);

	private final String name;
	private final String code;
	private final boolean acquirerRouterFlag;

	private PaymentType(String name, String code, boolean acquirerRouterFlag) {
		this.name = name;
		this.code = code;
		this.acquirerRouterFlag = acquirerRouterFlag;
	}

	public String getName() {
		return name;
	}

	public String getCode() {
		return code;
	}

	public static String getpaymentName(String paymentCode) {
		String payment = null;
		if (null != paymentCode) {
			for (PaymentType pay : PaymentType.values()) {
				if (paymentCode.equals(pay.getCode().toString())) {
					payment = pay.getName();
					break;
				}
			}
		}
		return payment;
	}

	public static PaymentType getInstance(String name) {
		PaymentType[] paymentTypes = PaymentType.values();
		for (PaymentType paymentType : paymentTypes) {
			if (paymentType.getName().toString().equals(name)) {
				return paymentType;
			}
		}
		return null;
	}

	public static PaymentType getInstanceUsingStringValue(String value) {
		PaymentType[] paymentTypes = PaymentType.values();
		for (PaymentType paymentType : paymentTypes) {
			if (paymentType.toString().equals(value)) {
				return paymentType;
			}
		}
		return null;
	}

	public static PaymentType getInstanceUsingCode(String paymentCode) {
		PaymentType payment = null;
		if (null != paymentCode) {
			for (PaymentType pay : PaymentType.values()) {
				if (paymentCode.equals(pay.getCode().toString())) {
					payment = pay;
					break;
				}
			}
		}
		return payment;
	}

	public static List<PaymentType> getGetPaymentsFromSystemProp(String acquirerCode) {

		List<String> paymentCodeStringList = (List<String>) Helper
				.parseFields(PropertiesManager.propertiesMap.get(acquirerCode));

		List<PaymentType> paymentTypes = new ArrayList<PaymentType>();

		for (String paymentCode : paymentCodeStringList) {
			PaymentType pay = getInstance(getpaymentName(paymentCode));
			paymentTypes.add(pay);
		}
		return paymentTypes;
	}

	public static List<PaymentType> getAcqRouterPaymentTypeList() {
		PaymentType[] paymentTypes = PaymentType.values();
		List<PaymentType> paymentTypeList = new ArrayList<>();
		for (PaymentType paymentType : paymentTypes) {
			if (paymentType.acquirerRouterFlag) {
				paymentTypeList.add(paymentType);
			}
		}
		return paymentTypeList;
	}

	public static PaymentType getInstanceIgnoreCase(String name) {

		PaymentType[] paymentTypes = PaymentType.values();
		for (PaymentType paymentType : paymentTypes) {
			if (paymentType.getName().equalsIgnoreCase(name.replace("_", " "))) {
				return paymentType;
			}
		}
		return null;
	}

	public boolean isAcquirerRouterFlag() {
		return acquirerRouterFlag;
	}
	public static String getpaymentCode(String paymentName) {
		String payment = null;
		if (null != paymentName) {
			for (PaymentType pay : PaymentType.values()) {
				if (paymentName.equals(pay.getName().toString())) {
					payment = pay.getCode();
					break;
				}
			}
		}
		return payment;
	}


	public static String getPaymentCodeFromInstance(String nameOfPayment) {
		PaymentType[] paymentTypes = PaymentType.values();
		String paymentTypeCode=null;
		for (PaymentType paymentType : paymentTypes) {
			if (paymentType.equals(PaymentType.getInstanceUsingStringValue(nameOfPayment))) {
				paymentTypeCode=paymentType.getCode();
				break;
			}
		}
		return paymentTypeCode;
	}

}
