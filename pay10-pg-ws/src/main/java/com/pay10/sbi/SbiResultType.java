package com.pay10.sbi;


public enum SbiResultType {

	SBI001			("SBI00001" , "000" , "Captured" , "Success"),
	SBI002			("SBI00002" , "022" , "Failed at Acquirer" , "Failure"),
	SBI003			("SBI00003" , "032" , "Pending" , "Pending");

	
	private SbiResultType(String bankCode, String PGCode, String statusCode, String message) {
		this.bankCode = bankCode;
		this.PGCode = PGCode;
		this.statusCode = statusCode;
		this.message = message;
	}

	public static SbiResultType getInstanceFromName(String code) {
		SbiResultType[] statusTypes = SbiResultType.values();
		for (SbiResultType statusType : statusTypes) {
			if (String.valueOf(statusType.getBankCode()).toUpperCase().equals(code)) {
				return statusType;
			}
		}
		return null;
	}

	private final String bankCode;
	private final String PGCode;
	private final String statusCode;
	private final String message;

	public String getBankCode() {
		return bankCode;
	}
	
	public String getStatusCode() {
		return statusCode;
	}

	public String getMessage() {
		return message;
	}

	public String getPGCode() {
		return PGCode;
	}
}