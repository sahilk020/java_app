package com.pay10.commons.util;

/**
 * @author puneet
 * For payment types provided to merchant and PG internal payment types
 */
public enum MerchantPaymentType {
	
	CARD("Card", "CARD"),
	NET_BANKING("Net Banking", "NB"),
	WALLET("Wallet", "WL"),
	UPI("UPI", "UP"), 
	QRCODE("QR CODE","QR");


	private final String name;
	private final String code;

	private MerchantPaymentType(String name, String code) {
		this.name = name;
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public String getCode() {
		return code;
	}
	
	public static MerchantPaymentType getInstanceFromCode(String code) {
		MerchantPaymentType[] paymentTypes = MerchantPaymentType.values();
		for(MerchantPaymentType paymentType:paymentTypes) {
			if(paymentType.code.equals(code)) {
				return paymentType;
			}
		}
		return null;
	}

}
