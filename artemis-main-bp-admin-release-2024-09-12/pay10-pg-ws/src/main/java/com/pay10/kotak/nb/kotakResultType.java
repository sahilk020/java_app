package com.pay10.kotak.nb;


public enum kotakResultType {

	Kotaknb001		("Y","000","Captured","Successful Payment"),
	KotakNb002		("N","022","Failed at Acquirer","Payment Failed");
	

	

	private kotakResultType(String bankCode, String iPayCode, String statusName, String message) {
		this.bankCode = bankCode;
		this.iPayCode = iPayCode;
		this.statusName = statusName;
		this.message = message;
	}

	public static kotakResultType getInstanceFromName(String code) {
		kotakResultType[] statusTypes = kotakResultType.values();
		for (kotakResultType statusType : statusTypes) {
			if (String.valueOf(statusType.getBankCode()).toUpperCase().equals(code)) {
				return statusType;
			}
		}
		return null;
	}

	private final String bankCode;
	private final String iPayCode;
	private final String message;
	private final String statusName;
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
	public static void main(String[] args) {
	kotakResultType result=getInstanceFromName("ERROR");	
	System.out.println("ss"+result);

}

}


