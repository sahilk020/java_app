package com.pay10.federalNB;



public enum FederalBankNBResultType {

	FEDERALBANKNB001		("0","000","Captured","Transaction Successful"),
	FEDERALBANKNB002		("1000","022","Failed at Acquirer","Transaction Failed"),
	FEDERALBANKNB003		("1029","007","Rejected","Payment Processing Timed Out"),
	FEDERALBANKNB004		("1043","010" , "Cancelled by user","Transaction Cancelled"),
	FEDERALBANKNB005		("1030","022","Failed at Acquirer","Payment successful but kept on hold by risk system"),
	FEDERALBANKNB006		("1039","007" , "Rejected","INVALID-REFUND-AMOUNT"),
	FEDERALBANKNB007		("CANCELLED","010" , "Cancelled by user","No Payment Attempted"),
	FEDERALBANKNB008		("ERROR","007" , "Rejected","Refund Failed");

	
	private FederalBankNBResultType(String bankCode, String iPayCode, String statusCode, String message) {
		this.bankCode = bankCode;
		this.iPayCode = iPayCode;
		this.statusCode = statusCode;
		this.message = message;
	}

	public static FederalBankNBResultType getInstanceFromName(String code) {
		FederalBankNBResultType[] statusTypes = FederalBankNBResultType.values();
		for (FederalBankNBResultType statusType : statusTypes) {
			if (String.valueOf(statusType.getBankCode()).toUpperCase().equals(code)) {
				return statusType;
			}
		}
		return null;
	}

	private final String bankCode;
	private final String iPayCode;
	private final String statusCode;
	private final String message;

	public String getBankCode() {
		return bankCode;
	}

	public String getiPayCode() {
		return iPayCode;
	}

	public String getStatusCode() {
		return statusCode;
	}

	public String getMessage() {
		return message;
	}
}