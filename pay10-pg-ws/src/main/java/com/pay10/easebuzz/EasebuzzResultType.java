package com.pay10.easebuzz;


public enum EasebuzzResultType {
	
	EASEBUZZ001		("SUCCESS","000","Captured","Successful Payment"),
	EASEBUZZ002		("FAILED","022","Failed at Acquirer","Payment Failed"),
	EASEBUZZ003		("PENDING","007","Rejected","Payment Processing Timed Out"),
	EASEBUZZ004		("CANCELLED","010" , "Cancelled by user","Payment cancelled by user"),
	EASEBUZZ005		("FLAGGED","022","Failed at Acquirer","Payment successful but kept on hold by risk system"),
	EASEBUZZ006		("Invalid inputs","007" , "Rejected","VALIDATION ERROR"),
	EASEBUZZ007		("CANCELLED","010" , "Cancelled by user","No Payment Attempted"),
EASEBUZZ008		("ERROR","007" , "Rejected","Refund Failed");

	

	private EasebuzzResultType(String bankCode, String iPayCode, String statusName, String message) {
		this.bankCode = bankCode;
		this.iPayCode = iPayCode;
		this.statusName = statusName;
		this.message = message;
	}

	public static EasebuzzResultType getInstanceFromName(String code) {
		EasebuzzResultType[] statusTypes = EasebuzzResultType.values();
		for (EasebuzzResultType statusType : statusTypes) {
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
