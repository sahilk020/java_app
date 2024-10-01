package com.pay10.payten;


public enum PaytenResultType {
	
	//PAYTEN000			("000" , "000" , "Captured" , "Payment is successful"),
	PAYTEN001			("007" , "007" , "Rejected" , "Payment Rejected"),
	PAYTEN002			("013" , "013" , "Declined" , "Payment Declined"),
	PAYTEN003			("010" , "010" , "Cancelled" , "Payment Cancelled"),
	PAYTEN004			("022" , "022" , "Failed at Acquirer" , "Failed at Acquirer"),
	PAYTEN005			("003" , "003" , "Timeout" , "Timeout"),
	PAYTEN006			("008" , "008" , "Duplicate" , "Duplicate"),
	PAYTEN007			("1011" , "1011" , "Failed" , "E-Kyc need to reverify");
	
	private PaytenResultType(String bankCode, String iPayCode, String statusCode, String message) {
		this.bankCode = bankCode;
		this.iPayCode = iPayCode;
		this.statusCode = statusCode;
		this.message = message;
	}

	public static PaytenResultType getInstanceFromName(String code) {
		PaytenResultType[] statusTypes = PaytenResultType.values();
		for (PaytenResultType statusType : statusTypes) {
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