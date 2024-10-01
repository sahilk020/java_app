package com.pay10.commons.util;

/**
 * @author VJ
 *
 */

public enum YesbankCbUpiResultType {
	
	MERCHANT_NOT_REACHABLE                               ("X7", "022", "Failed at Acquirer",  "MERCHANT NOT REACHABLE"),
	ACCOUNT_DOES_NOT_EXIST_REMITTER                      ("XH", "004",  "Declined",           "ACCOUNT DOES NOT EXIST REMITTER"),
	ACCOUNT_DOES_NOT_EXIST_BENEFICIARY                   ("XI", "004",  "Declined",           "ACCOUNT DOES NOT EXIST BENEFICIARY"),
	LOST_OR_STOLEN_CARD_BENEFICIARY                      ("YB", "022",  "Failed at Acquirer", "LOST OR STOLEN CARD  BENEFICIARY"),
	DO_NOT_HONOUR_REMITTER                               ("YC", "004",  "Declined",           "DO NOT HONOUR  REMITTER "),
	DO_NOT_HONOUR_BENEFICIARY                            ("YD", "004",  "Declined",           "DO NOT HONOUR  BENEFICIARY"),
    INVALID_BENEFICIARY_CREDENTIALS                      ("Z5", "004",   "Declined",          "INVALID BENEFICIARY CREDENTIALS"),
    INSUFFICIENT_FUNDS_IN_CUSTOMER_REMITTER_ACCOUNT      ("Z9", "004",   "Declined",          "INSUFFICIENT FUNDS IN CUSTOMER  REMITTER ACCOUNT"),
    INVALID_MPIN                                         ("ZM", "004",   "Declined",          "INVALID MPIN"),
    REVERSAL_FAILURE                                     ("96", "022",   "Failed at Acquirer", "REVERSAL FAILURE"),
    ORIGINAL_CREDIT_NOT_FOUND                            ("OC", "022",   "Failed at Acquirer", "ORIGINAL CREDIT NOT FOUND"),
    ORIGINAL_DEBIT_NOT_FOUND                             ("OD", "022",   "Failed at Acquirer", "ORIGINAL DEBIT NOT FOUND"),
    CREDIT_NOT_DONE                                      ("NC", "022",   "Failed at Acquirer", "CREDIT NOT DONE"),
    DEBIT_NOT_DONE                                       ("ND", "022",   "Failed at Acquirer",  "DEBIT NOT DONE"),
    EXPIRED_VIRTUAL_ADDRESS                              ("UX", "004",    "Declined",           "EXPIRED VIRTUAL ADDRESS"),
    TRANSACTION_DECLINED_BY_CUSTOMER                     ("ZA", "010",   "Cancelled",           "TRANSACTION DECLINED BY CUSTOMER"),
    REQUEST_AUTHORISATION_IS_DECLINED                    ("U19","004",   "Declined",            "REQUEST AUTHORISATION IS DECLINED"),
    DEBIT_HAS_BEEN_FAILED                                ("U30", "022",  "Failed at Acquirer",  "DEBIT HAS BEEN FAILED"),
    YES_UPI_ERROR_U31                                    ("U31", "022",  "Failed at Acquirer",  "REQUEST NOT RECEIVED BY SERVER"),
    BENEFICIARY_BANK_OFFLINE                             ("U78", "022",  "Failed at Acquirer",  "BENEFICIARY BANK OFFLINE"),
    UPI_TRANSACION_FAILED                           	 ("U69", "022",  "Failed at Acquirer",  "Transaction fail"),
	BANK_INTERNAL_ERROR                                  ("MC06","003" , "Timed out at Acquirer","REQUEST NOT RECEIVED BY SERVER"),
	MERCHANT_NOT_FOUND                                   ("MC02","004",  "Rejected",             "Merchant not found"),
    YESUPI_FAILED_RES                                    ("F",  "022",    "Failed",              "FAILED"),
    YESUPI_FAILED_RES_XB                                 ("XB",  "022",   "Failed",              "Transaction fail"),
	YESUPI_RESP_MCO4                                     ("MC04","022",  "Failed at Acquirer",   "Invalid Transaction Request parameter size"),
	YESUPI_RESP_P                                        ("P",   "026",   "Pending",              "Refund status is pending");
	
	
	private final String bankCode;
	private final String iPayCode;
	private final String message;
	private final String statusName;
	
	
	private YesbankCbUpiResultType(String bankCode, String iPayCode, String statusName, String message) {
		this.bankCode = bankCode;
		this.iPayCode = iPayCode;
		this.statusName = statusName;
		this.message = message;
	}
	
	public static YesbankCbUpiResultType getInstanceFromName(String code) {
		YesbankCbUpiResultType[] statusTypes = YesbankCbUpiResultType.values();
		for (YesbankCbUpiResultType statusType : statusTypes) {
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
