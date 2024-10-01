package com.pay10.apbl;


public enum APBLResultType {
	
	APBL000			("000" , "000" , "SUCCESS" , "SUC."),
	APBL001			("902" ,  "004" , "Declined" , "Invalid MID"),
	APBL002			("905" , "007" , "Rejected" , "Improper URL"),
	APBL003			("909" , "004" , "Declined" , "Only INR currency supported"),
	APBL004			("912" , "007" , "Rejected" , "Invalid Amount"),
	APBL005			("913" , "007" , "Rejected" , "Invalid Amount"),
	APBL006			("920" , "004" , "Declined" , "Invalid Date Format"),
	APBL007			("9002" , "007" , "Rejected" , "Invalid Date Format"),
	APBL008			("910" , "007" , "Rejected" , "Transaction not present"),
	APBL009			("930" , "007" , "Rejected" , "Invalid Merchant Id"),
	APBL0010		("931" , "007" , "Rejected" , "Invalid Date Format"),
	APBL0011		("1114" , "007" , "Rejected" , "Invalid Request Format"),
	APBL012			("999999" , "022" , "Failed at acquirer" , "Something went wrong.");
	
	
	
	private APBLResultType(String bankCode, String iPayCode, String statusCode, String message) {
		this.bankCode = bankCode;
		this.iPayCode = iPayCode;
		this.statusCode = statusCode;
		this.message = message;
	}

	public static APBLResultType getInstanceFromName(String code) {
		APBLResultType[] statusTypes = APBLResultType.values();
		for (APBLResultType statusType : statusTypes) {
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