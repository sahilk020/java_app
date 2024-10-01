package com.pay10.commons.exception;

public enum ErrorType {
	//check 021, 022, 023
	SUCCESS 						("000", "SUCCESS"),
	ACQUIRER_ERROR					("001", "Acquirer Error"),
	DENIED							("002", "Denied"),
	TIMEOUT							("003", "Timeout"),
	DECLINED						("004", "Declined"),
	AUTHENTICATION_UNAVAILABLE 		("005", "Authentication not available"),
	PROCESSING						("006", "Transaction processing"),
	REJECTED						("007", "Rejected by acquirer"),
	DUPLICATE						("008", "Duplicate"),
	SIGNATURE_MISMATCH				("009","Response signature did not match"),
	DUPLICATE_RESPONSE				("710","Duplicate response from acquirer for same pgref no "),
	CANCELLED						("010", "Cancelled by user"),	
	RECURRING_PAYMENT_UNSUCCESSFULL	("011", "Authorization success but error processing recurring payment"),
	DENIED_BY_FRAUD					("012", "Denied due to fraud detection"),
	REFUND_REJECTED					("013","Total refund amount greater than sale amount"),
	REFUND_DENIED 					("014","Refund Amount should be less than today's Captured Amount "),
	TRANSACTION_NOT_FOUND			("015", "Transaction not found"),
	REFUND_FLAG_AMOUNT_NOT_MATCH 	("016", "In case of Full Refund, Refund Amount shall be equal to the Sale Amount"),
	GOOGLEPAY_SERVER_DOWN 	        ("017", "In case if token is not generated from GPay server"),
	DUPLICATE_ORDER_ID				("018", "Duplicate order Id"),	
	DUPLICATE_REFUND_ORDER_ID		("019", "Duplicate refund order Id"),
	DECLINED_BY_INSUFFICIENT_BALANCE("020", "Declined due to insufficient balance"),
	
	
	
	INVALID_ACTIVITY				("021", "Invalid at acquirer"),
	FAILED							("022", "Failed at acquirer"),
	ENROLLED						("023", "The cardholder is enrolled in Payer Authentication"),
	CARD_NOT_ENROLLED				("024", "Card is not enrolled"),
	SURCHARGE_NOT_SET 				("025",	"Unable to fetch surcharge details"),
	PENDING 				        ("026",	"Pending"),
	REQUEST_ACCEPTED 				        ("226",	"Request Accepted"),

	DUPLICATE_SUBMISSION			("027", "Duplicate submission on same order ID"),	
	PENDING_AT_ACQUIRER				("028", "Pending at Acquirer"),
	
	//added by sonu 
	DECLINED_BY_LIMIT_EXCEEDED		("029", "Declined due to merchants limit exceeded"),
	ONHOLD 							("030", "Due to merchant insufficient balance"),
	REFUND_HOLD 					("031", "Refund not initiated due to transaction in hold state"),
	DECLINED_BY_TXN_LIMIT_NOT_AVAILABLE		("031", "Declined due to merchants transaction limit not available"),

	USER_NOT_FOUND 					("100", "100", "User not found", "User not found!"),
	USER_PASSWORD_INCORRECT 		("101", "100", "Password incorrect", "Incorrect User ID or Password!" ),
	USER_INACTIVE 					("102", "100", "User inactive", "User is inactive!" ),
	INVALID_INPUT 					("103", "100", "Validation failed", "Incorrect User ID or Password!" ),
	NOT_APPROVED_FROM_ACQUIRER		("104", "105", "User not approved from any acquirer", "User not approved from any acquirer" ),
	ACQUIRER_NOT_FOUND				("105","106","Account not present for this acquirer","Account not present for this acquirer"),
	MAPPING_NOT_FETCHED				("105","107","Unable to fetch mapping from database","Unable to fetch mapping from database"),	
	MAPPING_NOT_SAVED	    		("106","108","Unable to save merchant mapping.","Unable to save merchant mapping."),	
	MAPPING_SAVED		   	 		("107","109","Mapping saved successfully!!","Mapping saved successfully!!"),	
	CHARGINGDETAIL_NOT_FETCHED		("108","110","Merchant detail not present for this acquirer","Mapping detail not present for this acquirer"),
	CHARGINGDETAIL_NOT_SAVED		("109","111","Charging detail not saved to database","Charging detail not saved to database"),
	CURRENCY_NOT_MAPPED	    		("110","112","Merchant not mapped for this currency","Merchant not mapped for this currency"),
	SUCCESSFULLY_SAVED     			("111","Details Successfully Saved"),
	USER_PASSWORD_NOT_SET   		("112", "100", "Password not set", "Incorrect User ID or Password!" ),
	OTP_NOT_SET   		            ("797", "797", "OTP not set", "Invalid OTP!" ),
	PAYMENT_OPTION_NOT_SUPPORTED    ("113", "Payment option not supported!!"),
	LOCAL_TAX_RATES_NOT_AVAILABLE("114", "Local Tax Details Not Set!!"),
	MAPPING_SAVED_FOR_APPROVAL		("115", "Mapping Request saved for approval"),
	MAPPING_REQUEST_ALREADY_PENDING	("116", "Mapping Request already pending for this merchant"),
	MAPPING_REQUEST_APPROVAL_DENIED	("117", "Mapping Request cannot be updated as user is not Admin User"),
	MAPPING_REQUEST_NOT_FOUND		("118", "Mapping Request not found"),
	MAPPING_REQUEST_REJECTED		("119", "Mapping Request has been rejected"),
	SERVICE_TAX_DETAILS_SAVED		("120", "Local Tax Rate Details Saved!!"),
	SERVICE_TAX_REQUEST_ALREADY_PENDING		("121", "Service Tax Request already pending for this business type!!"),
	SERVICE_TAX_REQUEST_SAVED_FOR_APPROVAL		("122", "Service Tax Details Saved for approval!!"),
	MERCHANT_SURCHARGE_REQUEST_ALREADY_PENDING ("123", "Merchant Surcharge update request already pending !!"),
	MERCHANT_SURCHARGE_UPDATED ("124", "Merchant Surcharge updated !!"),
	MERCHANT_SURCHARGE_REQUEST_SENT_FOR_APPROVAL ("125", "Merchant Surcharge request saved for approval !!"),
	BANK_SURCHARGE_REQUEST_SENT_FOR_APPROVAL ("126", "Bank Surcharge request saved for approval !!"),
	BANK_SURCHARGE_UPDATED ("127", "Bank Surcharge updated !!"),
	BANK_SURCHARGE_REQUEST_ALREADY_PENDING ("128", "Bank Surcharge update request already pending !!"),
	BANK_SURCHARGE_REJECTED ("128", "Bank Surcharge request rejected !!"),
	BANK_SURCHARGE_UPDATE_DENIED ("133", "Bank Surcharge update request denied !!"),
	TDR_REQUEST_ALREADY_PENDING ("129", "TDR request is already pending for this merchant !!"),
	CURRENCY_NOT_SUPPORTED    ("130", "Currency not supported!!"),
	CARD_NUMBER_NOT_SUPPORTED    ("131", "Unsupported card number"),
	USER_ACCOUNT_LOCK 		("132", "100", "Account Lock", "Account Locked" ),
	ATTEMPT_MESSAGE 		("133", "100", "Attempt Message", "Incorrect Username or Password! You have 2 chances left" ),

	//added by sonu 
	TDR_SETTING_PENDING ("134", "TDR Setting is pending for this merchant !!"),
	MERCHANT_RETURN_URL_NOT_MAPPED ("135", "merchant return url not mapped in db for the same payId !!"),
	
	//User transaction operations
	ALREADY_CAPTURED_TRANSACTION 	("200","This transaction is already settled with Transaction Id: "),
	CAPTURE_SUCCESSFULL 			("201","Capture processed successfully order ID: "),
	CAPTURE_NOT_SUCCESSFULL 		("202","Capture not processed successfully for order ID: "),
	REFUND_SUCCESSFULL 				("203","Refund processed successfully order ID: "),
	REFUND_NOT_SUCCESSFULL 			("204","Refund not processed successfully order ID: "),
	VOID_NOT_SUCCESSFULL 			("205","Void not processed successfully order ID: "),
	VOID_SUCCESSFULL 				("206","Transaction void processed successfully order ID: "),
	REFUND_FAILED 					("207","Refund Amount should be less than today's Captured Amount "),
	REFUND_AMOUNT_MISMATCH 			("208","Refund request amount should be equal to sale amount for total refund"),
	AMOUNT_MISMATCH 				("209","Amount in response should be equal to sale amount "),
	SETTLEMENT_SUCCESSFULL 			("351","Settlement processed successfully order ID: "),
	SETTLEMENT_NOT_SUCCESSFULL 		("352","Settlement not processed successfully order ID: "),
	SETTLEMENT_REVERSED				("355", "Settlement Reversed"),
	
	VALIDATION_FAILED		("300", "Invalid Request" ),
	BLACKLISTED_IP			("301", "300", "Blacklisted IP address", "Invalid Request"),
	NO_SUCH_TRANSACTION		("302", "No Such Transaction Found"),
	EMPTY_FIELD             ("303", "This field is mandatory"),
	EMPTY_FIELDS            ("304", "Please provide a valid value for mandatory fields"),
	INVALID_FIELD_VALUE     ("305", "Enter valid value"),
	USER_UNAVAILABLE        ("306", "This Email id is already existing"),
	USER_AVAILABLE          ("307", "USER ID is available proceed to signup!!"),
	PASSWORD_MISMATCH       ("308", "Old Password does not Match"),
	OLD_PASSWORD_MATCH      ("309", "Please use a password that is not among last four passwords"),
	PASSWORD_CHANGED 	    ("310", "Password has been successfully changed, login to continue"),
	MERCHANT_RESET_PASSWORD_CHANGED 	    ("310", "Merchant Password has been successfully changed"),
	EMAIL_ERROR 	    	("311", "Error!! Unable to send email Emailer fail"),
	NEW_PASSWORD            ("312", "New password should not be blank!"),
	CONFIRM_NEW_PASSWORD    ("313", "Confirm new password should not be blank!"),
	PASSWORD_RESET 	        ("314", "Your password has been reset successfully, login to continue"),
	PASSWORD_SET 	        ("314", "Your password has been set successfully, login to continue"),
	
	TENANT_UNAVAILABLE        ("271", "Tenant Email ID is Not Exist"),
	TENANT_AVAILABLE          ("272", "Tenant Email ID is already Exist, Please Try another one"),
	
	ALREADY_PASSWORD_RESET 	("315", "You have already used this link, login to continue"),
	RESET_LINK_SENT         ("316", "Reset password link sent to your email id"),
	ALREADY_VALIDATE_EMAIL 	("317", "You have already validated using this link, login to continue"),
	INVALID_EMAIL 			("318", "Please provide valid email to reset your account's password"),
	INVALID_CURRENCY_CODE   ("319","301","Invalid currency code","Invalid Request" ),
	INVALID_AMOUNT          ("320","Invalid Request"),
	INVALID_ORDER_ID        ("321","Invalid Request"),
	INVALID_TXN_TYPE        ("322","Invalid Request"),
	INVALID_HASH            ("323","Invalid Hash"),
	INVALID_PAYID_ATTEMPT   ("324","Request with invalid payId"),
	INVALID_RETURN_URL      ("325","Invalid Request"),
	INVALID_FIELD           ("326","Invalid value"),
	USER_STATUS 	        ("555", "User is not Active"),
	INVALID_REQUEST_URL 	("327", "325", "User not allowed to use this reuest URL", "Invalid Request"),

	//Remittance 
	ACC_HOLDER_NAME           ("327","Invalid Account Holder Name"),
	ACCOUNT_NO                ("328","Invalid Account Number"),
	BANK_NAME                 ("329","Invalid Bank Name"),
	CURRENCY                  ("330","Invalid Currency"),
	DATE_FROM                 ("331","Invalid Transaction Date"),
	IFSC_CODE                 ("332","Invalid IFSC Code"),
	MERCHANT                  ("333","Invalid Merchant"),
	NET_AMOUNT                ("334","Invalid Net amount"),
	PAY_ID                    ("335","Invalid PayId"),
	STATUS                    ("336","Invalid Status"),
	UTR                       ("337","Invalid UTR"),

	
	//defaultCurrency Error
	INVALID_DEFAULT_CURRENCY	("338","Operation not successful, please try again later!!"),	
	UNMAPPED_CURRENCY_ERROR		("339","No currency mapped !!"),
	
	//fraud prevention sys Errors
			
	FRAUD_RULE_ALREADY_EXISTS	("340", "Fraud rule already exist"),
	FRAUD_RULE_NOT_EXIST		("341", "Fraud rule doesn't exist"),
	FRAUD_RULE_SUCCESS			("342", "Fraud Rule added successfully"),
	FRAUD_RULE_UPDATE_SUCCESS	("348", "Fraud Rule updated successfully"),
	FRAUD_RULE_SINGLE_ENTRY_ERROR	("343", "You can add only one rule of this type"),
	COMMON_ERROR				("344", "Something went wrong."),
	
	//ticketing System
	TICKET_SUCCESSFULLY_SAVED	("345","Ticket create successfully!!"),
	COMMENT_SUCCESSFULLY_ADDED	("346","Comment successfully added!!"),
	
	TRANSACTION_FAILED				("347", "Transaction Failed."),
	
	//Surcharge Module
	SURCHARGEDETAIL_NOT_FETCHED	("351","351","Surcharge detail not present for this payment type","Surcharge detail not present for this payment type"),
	SURCHARGEDETAIL_NOT_SAVED	("352","352","Surcharge details not saved to database","Surcharge details not saved to database"),
	SERVICETAX_NOT_SET 			("353","353","Service tax not set for this industry type","Service tax not set for this industry type"),
	
	PERMISSION_DENIED		("400", "Permission Denied"),
	INTERNAL_SYSTEM_ERROR	("900", "900", "Internal System Error", "Operation could not be completed, please try again later!"),
	CRYPTO_ERROR 			("901", "900", "Crypto Issue", "Operation could not be completed, please try again later!" ),
	DATABASE_ERROR			("902", "900", "Database Error", "Operation could not be completed, please try again later!"),
	UNKNOWN 				("999", "Unknown Error"),
	
	JMS_EMAIL_ERROR 		("0001", "Jms Email error"),
	
	CSV_NOT_SUCCESSFULLY_UPLOAD	("354","entry not  saved successfully!!"),
	INVALID_EMAIL_ID  			("356", "Enter valid EmailId"),
	VERIFY_EMAIL_ID				("357","We now need to verify your email address"),
	ROUTER_RULES_NOT_UPDATED 	("358", "Rule not updated!!"),
	INVALID_CAPTCHA				("359","Invalid captcha code!!"),
	
	//Smart router 
	ROUTER_RULE_CREATED				("360", "Router rule successfully created"),
	ROUTER_RULE_CREATE_REQUEST_SENT	("370", "Router rule create request successfully sent to Admin"),
	ROUTER_RULE_CREATE_REQUEST_PENDING	("371", "Router rule create request already pending with Admin"),
	ROUTER_CONFIGURATION_ALREADYPENDING	("372", "Router configuration status is pending for this router rule."),
	ROUTER_RULE_UPDATE_DENIED		("373", "Router Rule update denied! No Permissions Found."),
	ROUTER_RULE_UPDATED				("361", "Router rule successfully updated"),
	ROUTER_RULE_UPDATED_PENDING			("373", "Router rule update request sent to Admin"),
	ROUTER_RULE_ALREADY_EXIST		("362", "This rule is already exist"),
	SOME_RULE_ALREADY_EXIST			("363", "Duplicate rule are not created"),
	ROUTER_RULE_NOT_FOUND			("364", "Router rule not available"),
	
	INVALID_REQUEST_FIELD			("365","Invalid Request fields"),
	INVALID_VPA						("366", "Invalid VPA address"),
	INVALID_KOTAKUPI_VPA            ("U17", "Invalid VPA address"),
	REDIRECT_UPI                    ("398", "Redirect for UPI"),
	BILLDESK_REDIRECT_UPI           ("399", "Billdesk Redirect for UPI"),
	//Reco Refund Exeption User Type
	MERCHANT_EXCEPTION       		("367", "Merchant"),
	BANK_EXCEPTION       			("367", "Bank"),

	INACTIVE_OTP 		            ("697", "697", "Invalid OTP", "Invalid OTP!" ),
	EXPIRED_OTP 		            ("797", "797", "OTP has been expired!", "OTP has been expired!" ),
	INVALID_EMAILID 		        ("897", "897", "Invalid EmailId", "Invalid EmailId!"),
	
	ACUIRER_DOWN					("777", "Acquirer down"),
	
	// Direcpay
	TASK_SUCCESSFUL					("369", "Task Completed Successfully"),
	MERCHANT_ID_NOT_FOUND                  ("368","Merchant ID Not Available for transaction"),
	
	//Customer invoicing
	INVOICE_EXPIRED					("601", "The payment link has expired"),
	INVOICE_PAID					("602", "The payment has already been completed for this invoice"),
	INVOICE_IN_PROCESS				("603","A payment is already in process, please try again if the payment is not successful"),

	//Reco
	INVALID_RECO_TXN_ID     ("600", "PG_REF_NUM in RECO file is not matching the PG_REF_NUM in DB."),
	INVALID_RECO_AMOUNT     ("601", "Amount in RECO file is not matching the amount in DB for the same transaction."),
	INVALID_RECO_ACQ_ID     ("602", "Merchant ACQ_ID in RECO file is not matching the Merchant ACQ_ID in DB for the same transaction."),
	INVALID_REFUND_SALE     ("603", "Sale reference number for Refund in RECO file is not matching the sale reference number in DB for the same transaction."),
	INVALID_REFUND_FLAG     ("604", "Refund Flag in REFUND file is not matching the Refund Flag in DB for the same transaction."),
	INVALID_RECO_ORDER_ID   ("605", "Merchant Reconciled ORDER_Id in RECO file is not matching the Merchant Reconciled  Id in DB for the same transaction."),
	INVALID_REFUND_AMOUNT   ("606", "Amount in REFUND file should be the same amount in DB for the same transaction with R REFUND FLAG ."),
	MISSING_RECO_SETTLED    ("607", "There is no Reco/Settled transaction available in DB for the same PG_REF_NUM."),
	MISMATCH_ACQUIRER_TYPE  ("608", "ACQUIRER_TYPE in MPR file is not matching the ACQUIRER_TYPE in DB."),
	
	//change Password
	EMAIL_NEWPASSWORD_MATCH      ("609", "Email and password must not be same"),
	PAYID_NEWPASSWORD_MATCH      ("610", "Don't Use PayId in password"),
	SALT_NEWPASSWORD_MATCH      ("611", "Don't Use SALT in password"),
	INVALID_PASSWORDCOMPLEXITY      ("612", "This password does not meet the complexity requirements."), 
	REPEATOLD_PASSWORD_MATCH      ("613", "Password should be different from last 4 passwords"),
	INVALID_TOKEN                 ("617", "Invalid Token"),
	Acq_NotMap_with_paytype     ("786", " Operation fail ! Acquirer-2 is not Map With PaymentType and Mop  "),//add by abhishek
	Acquirer_record_save     ("1001", " Acquirer Scheduler  record has been  saved   Successfully"),//add by abhishek
	duplicated_record_cant_be_save     ("1000", " ! Duplicate Acquirer_scheduler Record are not created "),//add by abhishek
	ReMove_successful     ("1010", " ! Delete Successfull  "),//add by abhishek-
	
	VALID_VPA					("787", "Virtual Address already exists"),
	EKYC_VERIFICATION("1011","Ekyc Need to Reverify"),
	EKYC_DETAILS_NOT_FOUND("1014","KYC Details Not Found"),
	EKYC_SERVICE_DOWN("1015","Transaction not completed, Please Try Again Later"),
	MERCHANT_HOSTED_S2S("1013","S2S flag is not enabled"),
	TDR_NOT_SET_FOR_THIS_AMOUNT					("1012","022", "Tdr Not Set For This Amount"),
	PO					("2023","023", "Payout Not Enabled","Payout Not Enabled"),
	PO_FRM					("2023","024", "FRM IS NOT SET","FRM IS NOT SET"),
	PO_FRM_MIN_TICKET_SIZE					("2023","025", "PAYOUT MIN TICKET SIZE BREACH","PAYOUT MIN TICKET SIZE BREACH"),
	PO_FRM_MAX_TICKET_SIZE					("2023","026", "PAYOUT MAX TICKET SIZE BREACH","PAYOUT MAX TICKET SIZE BREACH"),
	
	PO_FRM_DAILY_LIMIT					("2023","027", "PAYOUT DAILY_LIMIT BREACH","PAYOUT DAILY_LIMIT BREACH"),
	PO_FRM_DAILY_VOLUME					("2023","028", "PAYOUT DAILY_VOLUME BREACH","PAYOUT DAILY_VOLUME BREACH"),
	
	PO_FRM_WEEKLY_LIMIT					("2023","029", "PAYOUT WEEKLY_LIMIT BREACH","PAYOUT WEEKLY_LIMIT BREACH"),
	PO_FRM_WEEKLY_VOLUME					("2023","030", "PAYOUT WEEKLY_VOLUME BREACH","PAYOUT WEEKLY_VOLUME BREACH"),
	
	PO_FRM_MONTHLY_LIMIT					("2023","031", "PAYOUT MONTHLY_LIMIT BREACH","PAYOUT MONTHLY_LIMIT BREACH"),
	PO_FRM_MONTHLY_VOLUME					("2023","032", "PAYOUT MONTHLY_VOLUME BREACH","PAYOUT MONTHLY_VOLUME BREACH"),
	BANK_CODE_MISSING	("701", "Bank Code Missing"),
	CURRENCY_CODE_MISSING	("702", "Currency Code Missing"),
	INVAILD_BANKCODE_OR_CURRENCY	("703", "Invalid Bank Code and Currency Code"),
	PO_WALLET_IS_NOT_FOUND	("704", "PAYOUT wallet not found "),
	INSUFFICIENT_BALANCE    ("705","Due to merchant insufficient balance"),


	;
	
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
	
	public static ErrorType getInstanceFromCode(String code){
		ErrorType[] errorTypes = ErrorType.values();
		for(ErrorType errorType : errorTypes){
			if(String.valueOf(errorType.getCode()).toUpperCase().equals(code)){
				return errorType;
			}
		}		
		return null;
	}
}
