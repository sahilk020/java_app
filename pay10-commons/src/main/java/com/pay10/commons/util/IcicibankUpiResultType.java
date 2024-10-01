package com.pay10.commons.util;

public enum IcicibankUpiResultType {

	TXN_INITIATED("92", "000", "SUCCESS", "Transaction Initiated"),
	TXN_sUCCESS("0", "000", "SUCCESS", "Transaction successful"),
	USER_PROFILE_NOT_FOUND("1", "022", "Failed at Acquirer", "User profile not found"),
	RES_PARSING_ERROR("4", "022", "Failed at Acquirer", "Response parsing error"),
	TXN_REJECTED("9", "004", "Rejected", "Transaction rejected"),
	INSUFFICIENT_DATA("10", "022", "Failed at Acquirer", "Insufficient data"),
	TXN_NOT_PROCESSED("99", "022", "Failed at Acquirer", "Transaction cannot be processed"),
	INVALID_REQUEST("5000", "022", "Failed at Acquirer", "Invalid Request"),
	INVALID_MID("5001", "022", "Failed at Acquirer", "Invalid MerchantID"),
	DUPLICATE_MERCHANT_TXN_ID("5002", "022", "Failed at Acquirer", "Duplicate MerchantTranId"),
	MERCHANT_TXN_MANDATORY("5003", "022", "Failed at Acquirer", "Merchant Transaction Id is mandatory"),
	INVALIDATE_DATA("5004", "022", "Failed at Acquirer", "Invalid Data"),
	DATE_GT("5005", "022", "Failed at Acquirer", "Collect By date should be greater than or equal to Current date"),
	MERCHANT_TXN_ID_BLANK("5006", "022", "Failed at Acquirer", "Merchant TranId is not available"),
	VPA_NOT_PRESENT("5007", "022", "Failed at Acquirer", "Virtual address not present"),
	PSP_NOT_REGISTER("5008", "022", "Failed at Acquirer", "PSP is not registered"),
	SERVICE_UNAVAILABLE("5009", "022", "Failed at Acquirer", "Service unavailable. Please try later."),
	DUPLICATE_TRX("5011", "022", "Failed at Acquirer",
			"This transaction is already processed (Online duplicate transaction)"),
	ALRERADY_INITIATED("5012", "022", "Failed at Acquirer",
			"Request has already been initiated for this transaction (Offline duplicate transaction)"),
	INVALID_VPA("5013", "022", "Failed at Acquirer", "Invalid VPA"),
	INSUFFICIENT_AMT("5014", "022", "Failed at Acquirer", "Insufficient Amount"),
	INVALID_ENCRYPT_REQ("8000", "022", "Failed at Acquirer", "Invalid Encrypted Request"),
	JSON_EMPTY("8001", "022", "Failed at Acquirer", "JSON IS EMPTY"),
	INVALID_JSON("8002", "022", "Failed at Acquirer", "INVALID_JSON"),
	INVALID_FORMAT("8003", "022", "Failed at Acquirer", "INVALID_FIELD FORMAT OR LENGTH");

	private final String bankCode;
	private final String iPayCode;
	private final String message;
	private final String statusName;

	private IcicibankUpiResultType(String bankCode, String iPayCode, String statusName, String message) {
		this.bankCode = bankCode;
		this.iPayCode = iPayCode;
		this.statusName = statusName;
		this.message = message;
	}

	public static IcicibankUpiResultType getInstanceFromName(String code) {
		IcicibankUpiResultType[] statusTypes = IcicibankUpiResultType.values();
		for (IcicibankUpiResultType statusType : statusTypes) {
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
