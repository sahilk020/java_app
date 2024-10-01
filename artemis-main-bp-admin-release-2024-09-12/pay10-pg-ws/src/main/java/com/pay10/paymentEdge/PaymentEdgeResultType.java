package com.pay10.paymentEdge;


public enum PaymentEdgeResultType {
	
	//PAYMENTEDGE000			("000" , "000" , "Captured" , "Payment is successful"),
	PAYMENTEDGE001			("007" , "007" , "Rejected" , "Payment Rejected"),
	PAYMENTEDGE002			("013" , "013" , "Declined" , "Payment Declined"),
	PAYMENTEDGE003			("010" , "010" , "Cancelled" , "Payment Cancelled"),
	PAYMENTEDGE004			("022" , "022" , "Failed at Acquirer" , "Failed at Acquirer"),
	PAYMENTEDGE005			("003" , "003" , "Timeout" , "Timeout"),
	PAYMENTEDGE006			("008" , "008" , "Duplicate" , "Duplicate"),
	PAYMENTEDGE007			("1011" , "1011" , "Failed" , "E-Kyc need to reverify");
	
	private PaymentEdgeResultType(String bankCode, String iPayCode, String statusCode, String message) {
		this.bankCode = bankCode;
		this.iPayCode = iPayCode;
		this.statusCode = statusCode;
		this.message = message;
	}

	public static PaymentEdgeResultType getInstanceFromName(String code) {
		PaymentEdgeResultType[] statusTypes = PaymentEdgeResultType.values();
		for (PaymentEdgeResultType statusType : statusTypes) {
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