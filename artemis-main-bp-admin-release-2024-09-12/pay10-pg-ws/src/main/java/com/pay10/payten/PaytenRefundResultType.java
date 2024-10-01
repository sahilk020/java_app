package com.pay10.payten;


public enum PaytenRefundResultType {

	PAYTEN000			("000" , "000" , "REFUND_INITIATED" , "Pending"),
	PAYTEN001			("007" , "007" , "Rejected" , "Refund Rejected"),
	PAYTEN002			("013" , "013" , "Declined" , "Refund Declined"),
	PAYTEN003			("014" , "014" , "Declined" , "Refund Amount should be less than today's Captured Amount"),
	PAYTEN004			("022" , "022" , "Failed at Acquirer" , "Failed at Acquirer"),
	PAYTEN005			("003" , "003" , "Timeout" , "Timeout"),
	PAYTEN007			("004" , "004" , "Declined" , "Refund Declined"),
	PAYTEN008			("016" , "016" , "Rejected" , "Refund Rejected");

	private PaytenRefundResultType(String bankCode, String iPayCode, String statusCode, String message) {
		this.bankCode = bankCode;
		this.iPayCode = iPayCode;
		this.statusCode = statusCode;
		this.message = message;
	}

	public static PaytenRefundResultType getInstanceFromName(String code) {
		PaytenRefundResultType[] statusTypes = PaytenRefundResultType.values();
		for (PaytenRefundResultType statusType : statusTypes) {
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