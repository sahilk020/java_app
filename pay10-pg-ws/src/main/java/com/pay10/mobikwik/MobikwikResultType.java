package com.pay10.mobikwik;


public enum MobikwikResultType {
	
	MOBIKWIK000			("0" , "000" , "SUCCESS" , "Transaction completed successfully."),
	MOBIKWIK001			("10" , "007" , "Rejected", "Merchant secret key does not exist"),
	MOBIKWIK002			("20" , "004" , "Declined" , "User Blocked"),
	MOBIKWIK003			("21" , "007" , "Rejected" , "Merchant Blocked"),
	MOBIKWIK004			("22" , "007" , "Rejected" , "Merchant does not Exist"),
	MOBIKWIK005			("23" , "007" , "Rejected" , "Merchant not registered on MobiKwik"),
	MOBIKWIK006			("24" , "004" , "Declined" , "Orderid is Blank or Null"),
	MOBIKWIK007			("30" , "004" , "Declined" , "Wallet TopUp Failed"),
	MOBIKWIK008			("31" , "004" , "Declined" , "Wallet Debit Failed"),
	MOBIKWIK009			("32" , "007" , "Rejected" , "Wallet Credit Failed"),
	MOBIKWIK010			("33" , "004" , "Declined" , "User does not have sufficient balance in his wallet"),
	MOBIKWIK011			("40" , "010",  "Cancelled" , "User canceled transaction at Login page"),
	MOBIKWIK012			("41" , "010","Cancelled" , "User canceled transaction at Wallet Top Up page"),
	MOBIKWIK013			("42" , "010","Cancelled" , "User canceled transaction at Wallet Debit page"),
	MOBIKWIK014			("50" , "004" , "Declined"  , "Order Id already processed with this merchant"),
	MOBIKWIK015			("51" , "004" , "Declined", "Length of parameter orderid must be between 8 to 30 characters"),
	MOBIKWIK016			("52" , "004" , "Declined" , "Parameter orderid must be alphanumeric only"),
	MOBIKWIK017			("53" , "004" , "Declined" , "Parameter email is invalid"),
	MOBIKWIK018			("54" , "007" , "Rejected" , "Parameter amount must be integer only"),
	MOBIKWIK019			("55" , "007" , "Rejected" , "Parameter cell is invalid. It must be numeric, have 10 digits and start with 7,8,9"),
	MOBIKWIK020			("56" , "007" , "Rejected" , "Parameter merchantname is invalid"),
	MOBIKWIK021			("57" , "007" , "Rejected" , "Parameter redirecturl is invalid"),
	MOBIKWIK022			("60" , "004" , "Declined" , "User Authentication failed"),
	MOBIKWIK023			("70" , "004" , "Declined" , "Monthly Wallet Top up limit crossed"),
	MOBIKWIK024			("71" , "004" , "Declined" , "Monthly transaction limit for this user crossed"),
	MOBIKWIK025			("72" , "007" , "Rejected" , "Maximum amount per transaction limit for this merchant crossed"),
	MOBIKWIK026			("73" , "007" , "Rejected" , "Merchant is not allowed to perform transactions on himself"),
	MOBIKWIK027			("74" , "007" , "Rejected" , "KYC Transactions is not allowed"),
	MOBIKWIK028			("80" , "007" , "Rejected" , "Checksum Mismatch"),
	MOBIKWIK029			("99" , "022" , "Failed at acquirer" , "Unexpected Error"),
	MOBIKWIK030			("1" , "007" , "Rejected" , "Failed."),
	MOBIKWIK031			("6" , "004" , "Declined" , "No refund on same transaction id within 5 minutes"),
	MOBIKWIK032			("7" , "007" , "Rejected" , "Failed to debit from merchant."),
	MOBIKWIK033			("101" , "004" , "Declined" , "No orderid found.");
	
	
	
	private MobikwikResultType(String bankCode, String iPayCode, String statusCode, String message) {
		this.bankCode = bankCode;
		this.iPayCode = iPayCode;
		this.statusCode = statusCode;
		this.message = message;
	}

	public static MobikwikResultType getInstanceFromName(String code) {
		MobikwikResultType[] statusTypes = MobikwikResultType.values();
		for (MobikwikResultType statusType : statusTypes) {
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