package com.pay10.batch.exception;

public enum ErrorType {
	SUCCESS 				("000", "RECO_SUCCESS"),
	DATABASE_ERROR			("902", "900", "Database Error", "Operation could not be completed, please try again later!"),
	UNKNOWN 				("999", "Unknown Error"),
	VALIDATION_FAILED		("300", "Invalid Request" ),
	
	//RECO REPORTING ERROR
	
	INVALID_RECO_TXN_ID     ("600", "PG_REF_NUM in RECO file is not matching the PG_REF_NUM in DB."),
	INVALID_RECO_AMOUNT     ("601", "Amount in RECO file is not matching the amount in DB for the same transaction."),
	INVALID_RECO_ACQ_ID     ("602", "Merchant RECO_ACQ_ID in RECO file is not matching the Merchant RECO_ACQ_ID in DB for the same transaction."),
	INVALID_REFUND_SALE     ("603", "Sale reference number for Refund in RECO file is not matching the sale reference number in DB for the same transaction."),
	INVALID_REFUND_FLAG     ("604", "Refund Flag in REFUND file is not matching the Refund Flag in DB for the same transaction."),
	INVALID_RECO_ORDER_ID   ("605", "Merchant Reconciled ORDER_Id in RECO file is not matching the Merchant Reconciled  Id in DB for the same transaction."),
	INVALID_REFUND_AMOUNT   ("606", "Amount in REFUND file should be the same amount in DB for the same transaction with R REFUND FLAG ."),
	MISSING_RECO_SETTLED    ("607", "There is no Reco/Settled transaction available in DB for the same PG_REF_NUM."),
	MISMATCH_ACQUIRER_TYPE  ("608", "RECO_ACQUIRER_TYPE in MPR file is not matching the RECO_ACQUIRER_TYPE in DB."),
	INTERNAL_SYSTEM_ERROR	("900", "900", "Internal System Error", "Operation could not be completed, please try again later!"),
	MISMATCH_STATUS         ("609", "Pay10 STATUS Mismatch");
	
	
	
	//Response code for user
	private final String responseCode;
	
	//This code contains more details about this error - it may be internal
	private final String code;
	
	//This message contains more details about the error - it may be internal
	private final String internalMessage;
	
	//message for displaying to user
	private final String responseMessage;
	
	private ErrorType(String code, String responseCode, String internalMessage, String userMessage){
		this.code = code;
		this.responseCode = responseCode;
		this.internalMessage = internalMessage;
		this.responseMessage = userMessage;
	}
	private ErrorType(String code, String userMessage){
		this.code = this.responseCode = code;
		this.internalMessage = this.responseMessage = userMessage;
	}
	private ErrorType(String code, String responseCode, String userMessage){
		this.code = code;
		this.responseCode = responseCode;
		this.internalMessage = this.responseMessage = userMessage;
	}

	public String getCode() {
		return code;
	}

	public String getInternalMessage() {
		return internalMessage;
	}

	public String getResponseMessage() {
		return responseMessage;
	}

	public String getResponseCode() {
		return responseCode;
	}
}
