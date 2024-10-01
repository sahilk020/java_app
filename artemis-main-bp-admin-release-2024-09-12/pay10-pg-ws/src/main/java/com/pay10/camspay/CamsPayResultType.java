package com.pay10.camspay;

/**
 * @author Jay
 *
 */
public enum CamsPayResultType {

	//@formatter:off
	RC111("RC111", "000", "Captured", "Request success"),
	RC000("RC000", "022", "Failed", "Request failure"),
	INVMER("INVMER", "007", "Rejected", "Invalid Merchant id"),
	INVCSUM("INVCSUM", "300", "Rejected", "Invalid Checksum"),
	RFMISS("RFMISS", "300", "Rejected", "Required Field missing"),
	VALINV("VALINV", "300", "Rejected", "Value Invalid"),
	RC222("RC222", "007", "Failed", "Encryption Decryption failure"),
	RC403("RC403", "004", "Failed", "Authentication failure"),
	MERAD("MERAD", "007", "Failed", "Merchant Access Denied"),
	SBAD("SBAD", "007", "Failed", "Sub Biller Access Denied"),
	TMINLNR("TMINLNR", "007", "Failed", "Transaction Minimum Limit not reached"),
	TMAXLEX("TMAXLEX", "007", "Failed", "Transaction Maximum Limit Exceeded"),
	PTNMM("PTNMM", "007", "Failed", "Payment type not mapped for the merchant id"),
	INVUPI("INVUPI", "300", "Rejected", "Invalid UPI id"),
	TRXNDUP("TRXNDUP", "008", "Rejected", "Transaction id duplicated"),
	UPIDEC("UPIDEC", "010", "Declined", "UPI transaction declined by user"),
	AMTEXPT("AMTEXPT", "007", "Rejected", "Amount exceeded for the payment type")
	; 
	//@formatter:on
	private CamsPayResultType(String bankCode, String code, String status, String message) {
		this.bankCode = bankCode;
		this.code = code;
		this.status = status;
		this.message = message;
	}

	public static CamsPayResultType getInstanceFromName(String code) {
		CamsPayResultType[] statusTypes = CamsPayResultType.values();
		for (CamsPayResultType statusType : statusTypes) {
			if (String.valueOf(statusType.getBankCode()).toUpperCase().equals(code)) {
				return statusType;
			}
		}
		return null;
	}

	private final String bankCode;
	private final String code;
	private final String status;
	private final String message;

	public String getBankCode() {
		return bankCode;
	}

	public String getStatus() {
		return status;
	}

	public String getMessage() {
		return message;
	}

	public String getCode() {
		return code;
	}
}