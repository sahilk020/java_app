package com.pay10.icici.netbanking;


public enum IciciNBResultType {
	
	ICICINB000			("S" , "000" , "SUCCESS" , "No Error."),
	ICICINB001			("F" , "004" , "Declined", "Transaction Declined"),
	iciciNB002			("P" , "006" , "Processing", "Pending at Bank End");
	
	
	private IciciNBResultType(String bankCode, String iPayCode, String statusCode, String message) {
		this.bankCode = bankCode;
		this.iPayCode = iPayCode;
		this.statusCode = statusCode;
		this.message = message;
	}

	public static IciciNBResultType getInstanceFromName(String code) {
		IciciNBResultType[] statusTypes = IciciNBResultType.values();
		for (IciciNBResultType statusType : statusTypes) {
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