package com.pay10.agreepay;

import com.pay10.agreepay.AgreepayResultType;

public enum AgreepayResultType {

	AGREEPAY001		("0","000","Captured","Transaction Successful"),
	AGREEPAY002		("1000","022","Failed at Acquirer","Transaction Failed"),
	AGREEPAY003		("1081","007","Rejected","Payment Processing Timed Out"),
	AGREEPAY004		("1043","010" , "Cancelled by user","Transaction Cancelled"),
	AGREEPAY005		("1030","022","Failed at Acquirer","Payment successful but kept on hold by risk system"),
	AGREEPAY006		("1039","007" , "Rejected","INVALID-REFUND-AMOUNT"),
	AGREEPAY007		("CANCELLED","010" , "Cancelled by user","No Payment Attempted"),
	AGREEPAY008		("ERROR","007" , "Rejected","Refund Failed");

	

	private AgreepayResultType(String bankCode, String iPayCode, String statusName, String message) {
		this.bankCode = bankCode;
		this.iPayCode = iPayCode;
		this.statusName = statusName;
		this.message = message;
	}

	public static AgreepayResultType getInstanceFromName(String code) {
		AgreepayResultType[] statusTypes = AgreepayResultType.values();
		for (AgreepayResultType statusType : statusTypes) {
			if (String.valueOf(statusType.getBankCode()).toUpperCase().equals(code)) {
				return statusType;
			}
		}
		return null;
	}

	private final String bankCode;
	private final String iPayCode;
	private final String message;
	private final String statusName;
	public String getBankCode() {
		return bankCode;
	}

	public String getiPayCode() {
		return iPayCode;
	}


	public String getMessage() {
		return message;
	}

	public String getStatusName() {
		return statusName;
	}
}

