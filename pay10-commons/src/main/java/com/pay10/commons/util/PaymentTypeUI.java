package com.pay10.commons.util;

import java.util.ArrayList;
import java.util.List;

public enum PaymentTypeUI {

	CREDIT_CARD("Credit Card", "CC", true), 
	DEBIT_CARD("Debit Card", "DC", true),
//	EMI("EMI", "EMI", true),
	NET_BANKING("Net Banking", "NB",true),
//	QRCODE("QR CODE","QR", true),
	UPI("UPI", "UP", true),
	WALLET("Wallet", "WL",true);

	private final String name;
	private final String code;
	private final boolean acquirerRouterFlag;

	
	private PaymentTypeUI(String name, String code, boolean acquirerRouterFlag) {
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
			for (PaymentTypeUI pay : PaymentTypeUI.values()) {
				if (paymentCode.equals(pay.getCode().toString())) {
					payment = pay.getName();
					break;
				}
			}
		}
		return payment;
	}

	public static PaymentTypeUI getInstance(String name) {
		PaymentTypeUI[] paymentTypes = PaymentTypeUI.values();
		for (PaymentTypeUI paymentType : paymentTypes) {
			if (paymentType.getName().toString().equals(name)) {
				return paymentType;
			}
		}
		return null;
	}

	public static PaymentTypeUI getInstanceUsingStringValue(String value) {
		PaymentTypeUI[] paymentTypes = PaymentTypeUI.values();
		for (PaymentTypeUI paymentType : paymentTypes) {
			if (paymentType.toString().equals(value)) {
				return paymentType;
			}
		}
		return null;
	}

	public static PaymentTypeUI getInstanceUsingCode(String paymentCode) {
		PaymentTypeUI payment = null;
		if (null != paymentCode) {
			for (PaymentTypeUI pay : PaymentTypeUI.values()) {
				if (paymentCode.equals(pay.getCode().toString())) {
					payment = pay;
					break;
				}
			}
		}
		return payment;
	}

	public static List<PaymentTypeUI> getGetPaymentsFromSystemProp(String acquirerCode) {

		List<String> paymentCodeStringList = (List<String>) Helper
				.parseFields(PropertiesManager.propertiesMap.get(acquirerCode));

		List<PaymentTypeUI> paymentTypes = new ArrayList<PaymentTypeUI>();

		for (String paymentCode : paymentCodeStringList) {
			PaymentTypeUI pay = getInstance(getpaymentName(paymentCode));
			paymentTypes.add(pay);
		}
		return paymentTypes;
	}

	public static List<PaymentTypeUI> getAcqRouterPaymentTypeList() {
		PaymentTypeUI[] paymentTypes = PaymentTypeUI.values();
		List<PaymentTypeUI> paymentTypeList = new ArrayList<>();
		for (PaymentTypeUI paymentType : paymentTypes) {
			if (paymentType.acquirerRouterFlag) {
				paymentTypeList.add(paymentType);
			}
		}
		return paymentTypeList;
	}

	public static PaymentTypeUI getInstanceIgnoreCase(String name) {

		PaymentTypeUI[] paymentTypes = PaymentTypeUI.values();
		for (PaymentTypeUI paymentType : paymentTypes) {
			if (paymentType.getName().equalsIgnoreCase(name.replace("_", " "))) {
				return paymentType;
			}
		}
		return null;
	}

	public boolean isAcquirerRouterFlag() {
		return acquirerRouterFlag;
	}

	public static String getInstanceUsingCode1(String paymentCode) {
		String payment = null;
		if (null != paymentCode) {
			for (PaymentTypeUI pay : PaymentTypeUI.values()) {
				if (paymentCode.equals(pay.getCode().toString())) {
					payment = pay.toString();
					break;
				}
			}
		}
		return payment;
	}
	
}
