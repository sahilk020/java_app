package com.pay10.quomo;


public enum QuomoResultType {
	
	ATOM000			("Ok" , "000" , "SUCCESS" , "No Error."),
	ATOM001			("F" ,  "004" , "Declined" , "Transaction Cancelled or Declined"),
	ATOM002			("00" , "000" , "SUCCESS" , "Full Refund initiated successfully"),
	ATOM003			("01" , "000" , "SUCCESS" , "Partial Refund initiated successfully"),
	ATOM004			("M0" , "004" , "Declined" , "Invalid merchant"),
	ATOM005			("M1" , "004" , "Declined" , "Refund is not allowed"),
	ATOM006			("M2" , "004" , "Declined" , "Invalid Transaction ID"),
	ATOM007			("M3" , "004" , "Declined" , "Invalid amount"),
	ATOM008			("M4" , "013" , "Declined" , "Insufficient amount in the account to be refund"),
	ATOM009			("M5" , "004" , "Declined" , "Invalid transaction date"),
	ATOM010			("M6" , "022" , "Failed at Acquirer" , "Failed Transaction"),
	//ATOM011			("Initiated" , "006" , "Processing" , "Transaction in processing state"),
	ATOM011			("Initiated" , "006" , "Sent to Bank" , "Transaction in processing state"),
	ATOM012			("SUCCESS" , "000" , "SUCCESS" , "SUCCESS"),
	ATOM013			("FAILED" , "004" , "Declined" , "Invalid amount"),
	ATOM014			("NODATA" , "007" , "Rejected" , "No data found at Acquirer End"),
	//ATOM015			("Pending From bank" , "006" , "Processing" , "Pending at bank end"),
	ATOM015			("Pending From bank" , "006" , "Sent to Bank" , "Pending at bank end"),
	ATOM016			("Invalid date format" , "004" , "Declined" , "Invalid date format"),
	ATOM017			("EE" , "007" , "Rejected" , "No Data Found at acquirer end");
	
	
	private QuomoResultType(String bankCode, String iPayCode, String statusCode, String message) {
		this.bankCode = bankCode;
		this.iPayCode = iPayCode;
		this.statusCode = statusCode;
		this.message = message;
	}

	public static QuomoResultType getInstanceFromName(String code) {
		QuomoResultType[] statusTypes = QuomoResultType.values();
		for (QuomoResultType statusType : statusTypes) {
			if (String.valueOf(statusType.getBankCode()).toUpperCase().equalsIgnoreCase(code)) {
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