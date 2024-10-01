package com.pay10.commons.util;

/**
 * @author puneet
 * For payment types provided to merchant and PG internal payment types
 */
public enum MerchantPaymentTypeForPO {
	
	CARD("Card", "CARD"),
	NET_BANKING("Net Banking", "NB"),
	WALLET("Wallet", "WL"),
	UPI("UPI", "UP"), 
	QRCODE("QR CODE","QR");


	private final String name;
	private final String code;

	private MerchantPaymentTypeForPO(String name, String code) {
		this.name = name;
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public String getCode() {
		return code;
	}
	
	public static MerchantPaymentTypeForPO getInstanceFromCode(String code) {
		MerchantPaymentTypeForPO[] paymentTypes = MerchantPaymentTypeForPO.values();
		for(MerchantPaymentTypeForPO paymentType:paymentTypes) {
			if(paymentType.code.equals(code)) {
				return paymentType;
			}
		}
		return null;
	}

}
