package com.pay10.icici.mpgs;


public enum MPGSResultType {
	
	MPGSResult001		("ABORTED","022","Failed at Acquirer","Transaction aborted by payer"),
	MPGSResult002		("ACQUIRER_SYSTEM_ERROR","022","Failed at Acquirer","Acquirer system error occurred processing the transaction"),
	MPGSResult006		("AUTHENTICATION_FAILED","004","Declined","3D Secure authentication failed"),		
	MPGSResult008		("BLOCKED","002","Denied by risk","Transaction blocked due to Risk or 3D Secure blocking rules"),
	MPGSResult009		("CANCELLED","010","Cancelled","Transaction cancelled by payer"),
	MPGSResult010		("DECLINED","004","Declined","Transaction declined by issuer"),
	MPGSResult011		("DECLINED_AVS","004","Declined","Transaction declined due to address verification"),
	MPGSResult012		("DECLINED_AVS_CSC","004","Declined","Transaction declined due to address verification and card security code"),
	MPGSResult013		("DECLINED_CSC","004","Declined","Transaction declined due to card security code"),
	MPGSResult014		("DECLINED_DO_NOT_CONTACT","004","Declined","Transaction declined - do not contact issuer"),
	MPGSResult015		("DECLINED_INVALID_PIN","004","Declined","Transaction declined due to invalid PIN"),
	MPGSResult016		("DECLINED_PAYMENT_PLAN","004","Declined","Transaction declined due to payment plan"),
	MPGSResult017		("DECLINED_PIN_REQUIRED","004","Declined","Transaction declined due to PIN required"),
	MPGSResult018		("DEFERRED_TRANSACTION_RECEIVED","022","Failed at Acquirer","Deferred transaction received and awaiting processing"),
	MPGSResult019		("DUPLICATE_BATCH","004","Declined","Transaction declined due to duplicate batch"),
	MPGSResult020		("EXCEEDED_RETRY_LIMIT","002","Denied by risk","Transaction retry limit exceeded"),
	MPGSResult021		("EXPIRED_CARD","004","Declined","Transaction declined due to expired card"),
	MPGSResult022		("INSUFFICIENT_FUNDS","004","Declined","Transaction declined due to insufficient funds"),
	MPGSResult023		("INVALID_CSC","004","Declined","Invalid card security code"),
	MPGSResult024		("LOCK_FAILURE","007","Rejected"," Order locked - another transaction is in progress for this order"),
	MPGSResult025		("NOT_ENROLLED_3D_SECURE","022","Failed at Acquirer","Card holder is not enrolled in 3D Secure"),
	MPGSResult026		("NOT_SUPPORTED","004","Declined","Transaction type not supported"),
	MPGSResult027		("NO_BALANCE","004","Declined","A balance amount is not available for the card."),
	MPGSResult029		("PENDING","003","Timed out at Acquirer","Transaction is pending"),
	MPGSResult030		("REFERRED","004","Declined","Transaction declined - refer to issuer"),
	MPGSResult032		("SYSTEM_ERROR","022","Failed at Acquirer"," Internal system error occurred processing the transaction"),
	MPGSResult033		("TIMED_OUT","003","Timed out at Acquirer","The gateway has timed out the request to the acquirer because it did not receive a response."),
	MPGSResult034		("UNKNOWN","003","Timed out at acquirer","The transaction has been submitted to the acquirer but the gateway was not able to find out about the success or otherwise of the payment. If the gateway subsequently finds out about the success of the payment it will update the response code."),
	MPGSResult035		("UNSPECIFIED_FAILURE","022","Failed at Acquirer","Transaction could not be processed");
		

	

	private MPGSResultType(String bankCode, String iPayCode, String statusCode, String message) {
		this.bankCode = bankCode;
		this.iPayCode = iPayCode;
		this.statusCode = statusCode;
		this.message = message;
	}

	public static MPGSResultType getInstanceFromName(String code) {
		MPGSResultType[] statusTypes = MPGSResultType.values();
		for (MPGSResultType statusType : statusTypes) {
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
