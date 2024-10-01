package com.pay10.yesbank.netbanking;

public enum YesbankNBResultType {

	YESBANKNB000("Merchant transaction successfull", "000", "Captured", "Transaction Successful"),
	YESBANKNB001("Error in processing request", "022", "Failed at Acquirer", "Transaction Failed"),
	YESBANKNB002("Insufficient funds in account", "006", "Processing", "Pending at Bank End"),
	YESBANKNB003("Account validation error:Invalid account status", "004", "Declined", "Transaction Declined"),
	YESBANKNB004("High level memo present on account", "006", "Processing", "Pending at Bank End"),
	YESBANKNB005("Merchant transaction is cancelled by user", "010", "Cancelled by user", "Transaction Cancelled"),
	YESBANKNB006("High level memo present on Customer", "006", "Processing", "Pending at Bank End");

	private YesbankNBResultType(String bankCode, String iPayCode, String statusName, String message) {
		this.bankCode = bankCode;
		this.iPayCode = iPayCode;
		this.statusName = statusName;
		this.message = message;
	}

	public static YesbankNBResultType getInstanceFromName(String code) {
		YesbankNBResultType[] statusTypes = YesbankNBResultType.values();
		for (YesbankNBResultType statusType : statusTypes) {
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
