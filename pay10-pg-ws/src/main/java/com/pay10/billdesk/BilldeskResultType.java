package com.pay10.billdesk;


public enum BilldeskResultType {
	
	BILLDESK001		("0300","000","Captured","Success"),
	BILLDESK002		("0399","010", "Cancelled by user","CANCELLED"),
	BILLDESK003		("NA","007","Rejected","Error Condition  Txn not found or Invalid checksum or Invalid Request IP etc"),
	BILLDESK004		("0002","006" , "Processing","Pending or Abandoned"),
	BILLDESK005		("0001","022","Failed at Acquirer","Error at BillDesk");

	

	private BilldeskResultType(String bankCode, String iPayCode, String statusName, String message) {
		this.bankCode = bankCode;
		this.iPayCode = iPayCode;
		this.statusName = statusName;
		this.message = message;
	}

	public static BilldeskResultType getInstanceFromName(String code) {
		BilldeskResultType[] statusTypes = BilldeskResultType.values();
		for (BilldeskResultType statusType : statusTypes) {
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
}
