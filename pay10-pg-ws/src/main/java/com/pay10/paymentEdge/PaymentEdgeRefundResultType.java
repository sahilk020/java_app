package com.pay10.paymentEdge;


public enum PaymentEdgeRefundResultType {

	PAYMENTEDGE000			("000" , "000" , "REFUND_INITIATED" , "Pending"),
	PAYMENTEDGE001			("007" , "007" , "Rejected" , "Refund Rejected"),
	PAYMENTEDGE002			("013" , "013" , "Declined" , "Refund Declined"),
	PAYMENTEDGE003			("014" , "014" , "Declined" , "Refund Amount should be less than today's Captured Amount"),
	PAYMENTEDGE004			("022" , "022" , "Failed at Acquirer" , "Failed at Acquirer"),
	PAYMENTEDGE005			("003" , "003" , "Timeout" , "Timeout"),
	PAYMENTEDGE007			("004" , "004" , "Declined" , "Refund Declined"),
	PAYMENTEDGE008			("016" , "016" , "Rejected" , "Refund Rejected");

	private PaymentEdgeRefundResultType(String bankCode, String iPayCode, String statusCode, String message) {
		this.bankCode = bankCode;
		this.iPayCode = iPayCode;
		this.statusCode = statusCode;
		this.message = message;
	}

	public static PaymentEdgeRefundResultType getInstanceFromName(String code) {
		PaymentEdgeRefundResultType[] statusTypes = PaymentEdgeRefundResultType.values();
		for (PaymentEdgeRefundResultType statusType : statusTypes) {
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