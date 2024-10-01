package com.pay10.pinelabs;

import com.pay10.pinelabs.PinelabsResultType;

public enum PinelabsResultType {

	PINELABS001		("4","000","Captured","Transaction Successful"),
	PINELABS002		("-10","010" , "Cancelled by user","Transaction Cancelled"),
	PINELABS003		("-9","022","Auth Cancelled","Authorisation transaction has cancelled"),
	PINELABS004		("-8","022","Velocity Check Failed","Velocity Check Failed For EMI"),
	PINELABS005		("-7","022","Failed at Acquirer","Transaction Failed"),
	PINELABS006		("-6","007","Rejected","Transaction has been rejected"),
	PINELABS007		("1","026" , "Sent to Bank"," Response has not received from Payment Provider/Bank"),
	PINELABS008		("3","022" , "Auth Complete","Transaction is now eligible for 'Capture'"),
	PINELABS009		("6","000" , "Refunded","Refund of the transaction is successful"),
	PINELABS0010		("7","000" , "Query Complete","Query Complete"),
	PINELABS0011		("8","000" , "Partially Captured","Auth transaction is partially Capture"),
	PINELABS0012		("9","000" , "Partially Refunded","Transaction is partially refuned"),
	PINELABS0013		("10","026" , "Refund Initiated","When refund of aggregator transaction is initiated");
	
	
	

	private PinelabsResultType(String bankCode, String iPayCode, String statusName, String message) {
		this.bankCode = bankCode;
		this.iPayCode = iPayCode;
		this.statusName = statusName;
		this.message = message;
	}

	public static PinelabsResultType getInstanceFromName(String code) {
		PinelabsResultType[] statusTypes = PinelabsResultType.values();
		for (PinelabsResultType statusType : statusTypes) {
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

