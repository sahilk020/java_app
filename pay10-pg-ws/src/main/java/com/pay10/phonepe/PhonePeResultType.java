package com.pay10.phonepe;


public enum PhonePeResultType {
	
	PHONEPE000			("PAYMENT_SUCCESS" , "000" , "SUCCESS" , "Payment is successful"),
	PHONEPE001			("TRANSACTION_NOT_FOUND" , "007" , "Rejected" , "Payment not initiated inside PhonePe"),
	PHONEPE002			("BAD_REQUEST" , "007" , "Rejected" , "Invalid request"),
	PHONEPE003			("AUTHORIZATION_FAILED" , "007" , "Rejected" , "X VERIFY header is incorrect"),
	PHONEPE004			("INTERNAL_SERVER_ERROR" , "022" , "Failed at Acquirer" , "Something went wrong. Merchant needs to call Check Transaction Status to verify the transaction status."),
	PHONEPE005			("PAYMENT_ERROR" , "022" , "Failed at Acquirer" , "Payment failed"),
	PHONEPE006			("PAYMENT_PENDING" , "028" , "Pending at Acquirer" , "Payment is pending. It does not indicate failed payment. Merchant needs to call Check Transaction Status to verify the transaction status"),
	PHONEPE007			("PAYMENT_DECLINED" , "010","Cancelled" , "Payment declined by user"),
	PHONEPE008			("PAYMENT_CANCELLED" , "010","Cancelled" , "Payment cancelled by the merchant using Cancel API"),
	PHONEPE009			("TIMED_OUT" , "022" , "Failed at acquirer" , "Refund timed out."),
	PHONEPE010			("EXCESS_REFUND_AMOUNT" , "013" , "Declined" , "Refund Amount exceeding sale amount");
	
	private PhonePeResultType(String bankCode, String iPayCode, String statusCode, String message) {
		this.bankCode = bankCode;
		this.iPayCode = iPayCode;
		this.statusCode = statusCode;
		this.message = message;
	}

	public static PhonePeResultType getInstanceFromName(String code) {
		PhonePeResultType[] statusTypes = PhonePeResultType.values();
		for (PhonePeResultType statusType : statusTypes) {
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