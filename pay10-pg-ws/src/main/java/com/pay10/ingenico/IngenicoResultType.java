package com.pay10.ingenico;


public enum IngenicoResultType {
	
	INGENICO000			("0300" , "000" , "SUCCESS" , "Transaction Success In Db."),
	INGENICO001			("0392" ,  "004" , "Declined" , "Transaction Cancelled or Declined"),
	INGENICO002			("00" , "000" , "SUCCESS" , "Full Refund initiated successfully"),
	INGENICO003			("01" , "000" , "SUCCESS" , "Partial Refund initiated successfully"),
	INGENICO004			("M0" , "004" , "Declined" , "Invalid merchant"),
	INGENICO005			("M1" , "004" , "Declined" , "Refund is not allowed"),
	INGENICO006			("M2" , "004" , "Declined" , "Invalid Transaction ID"),
	INGENICO007			("M3" , "004" , "Declined" , "Invalid amount"),
	INGENICO008			("M4" , "013" , "Declined" , "Insufficient amount in the account to be refund"),
	INGENICO009			("M5" , "004" , "Declined" , "Invalid transaction date"),
	INGENICO010			("M6" , "022" , "Failed at Acquirer" , "Failed Transaction"),
	INGENICO011			("Initiated" , "006" , "Processing" , "Transaction in processing state"),
	INGENICO012			("SUCCESS" , "000" , "SUCCESS" , "SUCCESS"),
	INGENICO013			("FAILED" , "004" , "Declined" , "Invalid amount"),
	INGENICO014			("NODATA" , "007" , "Rejected" , "No data found at Acquirer End"),
	INGENICO015			("Pending From bank" , "006" , "Processing" , "Pending at bank end"),
	INGENICO016			("Invalid date format" , "004" , "Declined" , "Invalid date format"),
	INGENICO017			("EE" , "007" , "Rejected" , "No Data Found at acquirer end");
	
	
	private IngenicoResultType(String bankCode, String iPayCode, String statusCode, String message) {
		this.bankCode = bankCode;
		this.iPayCode = iPayCode;
		this.statusCode = statusCode;
		this.message = message;
	}

	public static IngenicoResultType getInstanceFromName(String code) {
		IngenicoResultType[] statusTypes = IngenicoResultType.values();
		for (IngenicoResultType statusType : statusTypes) {
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