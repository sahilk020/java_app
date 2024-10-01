package com.pay10.matchmove;

public enum MatchMoveResultType {
	
	MATCHMOVE001			("CP0200" , "000" , "Captured" , "Success."),
	MATCHMOVE002			("CPC200" , "004" , "Declined" , "Declined."),
	MATCHMOVE003			("CPR200" , "004" , "Declined" , "Declined."),
	MATCHMOVE004			("CP0400" , "004" , "Declined" , "Canceled by user."),
	MATCHMOVE005			("CPVE00" , "004" , "Declined" , "Invalid Request"),
	MATCHMOVE006			("CPVE01" , "004" , "Declined" , "Invalid Merchant ID."),
	MATCHMOVE007			("CP0401" , "004" , "Declined" , "Invalid Hash"),
	MATCHMOVE008			("CPE401" , "004" , "Declined" , "Invalid User"),
	MATCHMOVE009			("CPVE02" , "004" , "Declined" , "Invalid Currency"),
	MATCHMOVE010			("CPVE03" , "004" , "Declined" , "Duplicate txn_ref_id"),
	MATCHMOVE011			("CPVE04" , "004" , "Declined" , "Invalid Mobile Number format"),
	MATCHMOVE012			("CPVE05" , "004" , "Declined" , "Invalid Email format."),
	MATCHMOVE013			("CPVE06" , "004" , "Declined" , "Invalid Order ID"),
	MATCHMOVE014			("CPVE07" , "004" , "Declined" , "Invalid Checkout ref ID transaction already been processed."),
	MATCHMOVE015			("CPVE08" , "004" , "Declined" , "Invalid transaction type."),
	MATCHMOVE016			("CPVE09" , "004" , "Declined" , "Invalid Product Description"),
	MATCHMOVE017			("CPVE10" , "004" , "Declined" , "Invalid Transaction Ref ID."),
	MATCHMOVE018			("CPVE11" , "004" , "Declined" , "Purchase amount below minimum"),
	MATCHMOVE019			("CPVE12" , "004" , "Declined" , "Current transaction status not allowed."),
	MATCHMOVE020			("CPVE13" , "004" , "Declined" , "Invalid amount not meeting right money format for purchase amount, fee, and other amount"),
	MATCHMOVE022			("CPVE14" , "022" , "Failed at Acquirer" , "Invalid transaction found."),
	MATCHMOVE023			("CP0403" , "004" , "Declined" , "Purchase amount greater than Balance."),
	MATCHMOVE024			("CPE403" , "022" , "Failed at Acquirer" , "Transaction already processed."),
	MATCHMOVE025			("CPE403" , "022" , "Failed at Acquirer" , "Internal Server Error.");

	
	private MatchMoveResultType(String bankCode, String iPayCode, String statusCode, String message) {
		this.bankCode = bankCode;
		this.iPayCode = iPayCode;
		this.statusCode = statusCode;
		this.message = message;
	}

	public static MatchMoveResultType getInstanceFromName(String code) {
		MatchMoveResultType[] statusTypes = MatchMoveResultType.values();
		for (MatchMoveResultType statusType : statusTypes) {
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
