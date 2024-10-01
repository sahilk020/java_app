package com.pay10.axisbank.netbanking;


public enum AxisBankNBResultType {
	
	AXISBANKNB000			("S" , "000" , "SUCCESS" , "No Error."),
	AXISBANKNB001			("F" , "004" , "Declined", "Transaction Declined"),
	AXISBANKNB002			("P" , "006" , "Processing", "Pending at Bank End");
	
	
	private AxisBankNBResultType(String bankCode, String iPayCode, String statusCode, String message) {
		this.bankCode = bankCode;
		this.iPayCode = iPayCode;
		this.statusCode = statusCode;
		this.message = message;
	}

	public static AxisBankNBResultType getInstanceFromName(String code) {
		AxisBankNBResultType[] statusTypes = AxisBankNBResultType.values();
		for (AxisBankNBResultType statusType : statusTypes) {
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