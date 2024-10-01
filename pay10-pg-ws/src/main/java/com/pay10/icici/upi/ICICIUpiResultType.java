package com.pay10.icici.upi;



public enum ICICIUpiResultType {
	
	
	ICICIUPI000			("92" , "000" , "SUCCESS" , "Accepted Collect Request."),
	ICICIUPI001			("0" , "000" , "SUCCESS" , "SUCCESS."),
	ICICIUPI002			("99" , "022" , "Failed at Acquirer" , "Internal Server Error."),
	ICICIUPI003			("444" , "004" , "Declined" , "CheckSum Error."),
	ICICIUPI004			("111" , "004" , "Declined" , "Missing or Empty Parameter."),
	ICICIUPI005			("125" , "004" , "Declined" , "Validation Error."),
	ICICIUPI006			("303" , "004" , "Declined" , "Duplicate Transaction Id."),
	ICICIUPI007			("ZH" , "004" , "Declined" , "Invalid VPA."),
	ICICIUPI009			("NA" , "007" , "Rejected" , "Acquirer Error."),
	ICICIUPI010			("A79" , "004" , "Declined" , "Refund Amount cannot be zero or negative."),
	ICICIUPI011			("A78" , "004" , "Declined" , "Duplicate Refund Id."),
	ICICIUPI012			("444" , "022" , "Failed at Acquirer" , "Internal Server Error."),
	ICICIUPI013			("404" , "004" , "Declined" , "Record Not Available."),
	ICICIUPI014			("U69" , "004" , "Declined" , "Request Expired."),
	ICICIUPI015			("ZA" , "010" , "Cancelled" , "Payment cancelled by User."),
	ICICIUPI016			("ZM" , "004" , "Declined" , "Declined due to invalid PIN."),
	ICICIUPI017			("96" , "004" , "Declined" , "Timeout.");
	
	
	private ICICIUpiResultType(String bankCode, String iPayCode, String statusCode, String message) {
		this.bankCode = bankCode;
		this.iPayCode = iPayCode;
		this.statusCode = statusCode;
		this.message = message;
	}

	public static ICICIUpiResultType getInstanceFromName(String code) {
		ICICIUpiResultType[] statusTypes = ICICIUpiResultType.values();
		for (ICICIUpiResultType statusType : statusTypes) {
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