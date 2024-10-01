package com.pay10.commons.util;

public enum SBIUpiResultType {
	
	DEEMED_SUCCESS                           	 ("T", "022",  "Sent to Bank",  "Deemed Successful"),
	PENDING_TXN                           	 ("P", "022",  "Sent to Bank",  "Transaction Pending waiting for response"),
	PAYMENT_FAIL                           	 ("F", "022",  "Failed at Acquirer",  "Payment failed."),
	REQUEST_REJECT                           	 ("R", "022",  "Failed at Acquirer",  "Collect request rejected by customer"),
	REQUEST_EXPIRED                           	 ("X", "022",  "Failed at Acquirer",  "Collect request expired.	"),
	VALIDATION_ERROR                           	 ("V", "022",  "Failed at Acquirer",  "Request Validation Error");

	private final String bankCode;
	private final String iPayCode;
	private final String message;
	private final String statusName;
	
	
	private SBIUpiResultType(String bankCode, String iPayCode, String statusName, String message) {
		this.bankCode = bankCode;
		this.iPayCode = iPayCode;
		this.statusName = statusName;
		this.message = message;
	}
	
	public static SBIUpiResultType getInstanceFromName(String code) {
		SBIUpiResultType[] statusTypes = SBIUpiResultType.values();
		for (SBIUpiResultType statusType : statusTypes) {
			if (String.valueOf(statusType.getBankCode()).toUpperCase().equals(code)) {
				return statusType;
			}
		}
		return null;
	}

	
	
	
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
