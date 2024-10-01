package com.pay10.commons.util;

public enum YesBankFT3ResultType {
	// TODO: Create result types for bene addition.
	NSE401				("401", "Authentication Required", "Authentication Required"),
	NSE400				("ns:E400", "Bad Request", "Bad Request"),
	NSE800				("800", "Beneficiary code cannot contain special character.", "Beneficiary code cannot contain special character."),
//	NSE800				("800", "Beneficiary name can contain alphabets, numbers & special characters i.e. hyphen, apostrophe & period only.", "Beneficiary name can contain alphabets, numbers & special characters i.e. hyphen, apostrophe & period only.")
	NSE102				("102", "Record does not exist", "Record does not exist or disabled"),
	NSE101				("101", "Record already exist but pending for approval", "Record already exist but pending for approval"),
	
		
	// Result Types for transaction.
	RECEIVED			("000", "Received", "Transaction has been Accepted"),
	DUPLICATE			("ns:E8000", "Duplicate", "Retry with a new UNR"),
	
	// TODO: Result types for Fund confirmation.
//	NSE2000				("ns:E2000", "Invalid details/FAILED", "Either customer does not exist or Customer/Account combination is invalid or Customer/Account Relationship is invalid"),
	NSE2000				("ns:E2000", "FAILED", "Either customer does not exist or Customer/Account combination is invalid or Customer/Account Relationship is invalid"),
	
	// TODO: Create result types for Status enquiry.
	NSE307					("ns:E307", "FAILED", "Transaction has failed at downstream system/transaction cannot be processed during this period"),
	NSE402					("ns:E402", "FAILED", "Insufficient Balance in debit account, payment required"),
	NSE403					("ns:E403", "FAILED", "Customer / Account Access Forbidden"),
	NSE404					("ns:E404", "FAILED", "Request Not Found"),
	NSE405					("ns:E405", "FAILED", "None of the transfer types allowed to the app id or customer can perform the requested transaction."),
	NSE406					("ns:E406", "FAILED", "Beneficiary not acceptable"),
	NSE410					("ns:E410", "FAILED", "None: The requested entity is no longer available"),
	NSE429					("ns:E429", "FAILED", "(Limit Daily/transaction/rate) exceeded"),
//	NSE502					("ns:E502", "FAILED/SettlementInProcess", "Bad Gateway"),
	NSE502					("ns:E502", "SettlementInProcess", "Bad Gateway"),
	NSE1001					("ns:E1001", "FAILED", "The transaction amount exceeds the maximum amount for IMPS."),
	NSE1002					("ns:E1002", "FAILED", "The transfer currency is not supported. Supported currency is INR. "),
	NSE1004					("ns:E1004", "FAILED", "Transfer Amount is less than minimum amount for RTGS"),
	NSE1028					("ns:E1028", "FAILED", "IMPS is not enabled for the beneficiary IFSC"),
	NSE1029					("ns:E1029", "FAILED", "FT Transfer with Non yes bank beneficiary"),
//	NSE2000					("ns:E2000", "FAILED", "Either customer does not exist or Customer/Account combination is invalid or Customer/Account Relationship is invalid"),
	NSE2001					("ns:E2001", "FAILED", "Account status is inactive"),
	NSE2005					("ns:E2005", "FAILED", "Beneficairy not found for code"),
	NSE6000					("ns:E6000", "FAILED", "Purpose Code not found:"),
	NSE6001					("ns:E6001", "FAILED", "Only registered beneficiaries are allowed for this purpose code."),
	NSE6002					("ns:E6002", "FAILED", "Purpose Code is required for this customer."),
	NSE6003					("ns:E6003", "FAILED", "Invalid Debit Account for Customer"),
	NSE6005					("ns:E6005", "FAILED", "Either Beneficiary Name or beneficiary IFSC code is not valid for GST payment."),
	NSE6006					("ns:E6006", "FAILED", "Beneficiary account no length is not valid for GST payment allowed length is 14"),
	NSE6007					("ns:E6007", "FAILED", "The specified purpose code is not allowed for the chosen transferType"),
	NSE6008					("ns:E6008", "FAILED", "Unique request no length is not valid for APBS transfer type, max allowed length is 13"),
	NSE8000					("ns:E8000", "FAILED", "A transaction with same reference number is already processed or under processing"),
	FLEXE11					("flex:E11", "FAILED", "Hold Funds Present - Refer to Drawer ( Account would Overdraw ) "),
	FLEXE307				("flex:E307", "Pending", "Rejected/Failed at upstream CBS Service"), // Retry after 30 minutes
	FLEXE404				("flex:E404", "FAILED", "No Relationship Exists with the debit Account {AccountNo} and partner"),
	FLEXE449				("flex:E449", "FAILED", "Rejected by upstream CBS Service for the request parameters passed"),
	FLEXE2435				("flex:E2435", "FAILED", "Invalid Input - To/Credit Account Has Invalid Account Status : 07-Dormant."),
	FLEXE2853				("flex:E2853", "FAILED", "Account not found."),
	FLEXE3403				("flex:E3403", "Pending", "Called function has had a Fatal Error Different reference number passed. Probably, multiple keys generated in same interaction "), // Retry after 30 minutes
	FLEXE6833				("flex:E6833", "FAILED", "Invalid input TransactionAmount."),
	FLEXE7585				("flex:E7585", "Pending", "Unhandled Message Received - This is not expected, turn on validation on HTTPRequest Node"), // Retry after 30 minutes
	FLEXE8035				("flex:E8035", "FAILED", "NEFT - Customer Mobile number is not valid."),
	FLEXE8036				("flex:E8036", "FAILED", "NEFT - Both Customer Mobile and Email is not valid."),
	FLEXE8037				("flex:E8037", "FAILED", "Transaction Amount is Greater Than Available Balance."),
	FLEXE8086				("flex:E8086", "FAILED", "From Account Number is Invalid."),
	FLEXE8087				("flex:E8087", "FAILED", "To Account Number is Invalid."),
	FLEXE9072				("flex:E9072", "FAILED", "Destination Bank and Branch could not be resolved."),
	FLEXE9074				("flex:E9074", "FAILED", "RemitOrg1 - Max length (35) exceeded"),
	FLEXE11017				("FLEX:E11017", "FAILED", "System Rejects due to format/functional issues"),
	NPCIE02					("npci:E02", "Pending", "Connectivity issue with NPCI"), // Retry after 30 minutes
	NPCIE08					("npci:E08", "Pending", "Acquiring Bank CBS or node offline"), // Retry after 30 minutes
	NPCIECE					("npci:ECE", "Pending", "Sorry, this service is currently unavailable. Please try again after sometime."), // Retry after 30 minutes
	NPCIEMP					("npci:EMP", "Pending", "Functionality not yet available for funds transfer based on account number through beneficiary bank / Transaction could not be processed"), // Retry after 30 minutes
	NPCIE96					("npci:E96", "Pending", "Transaction could not be processed / Your fund transfer could not be processed."), // Retry after 30 minutes
//	NPCIEMP					("npci:EMP", "FAILED", "Transaction could not be processed "),
	NPCIE20					("npci:E20", "Pending", "Transaction could not be processed / Your fund transfer could not be processed."), // Retry after 30 minutes
	NPCIE12					("npci:E12", "Pending", "Transaction could not be processed / Your fund transfer could not be processed. "), // Retry after 30 minutes
//	NPCIE96					("npci:E96", "FAILED", "Your fund transfer could not be processed."),
//	NPCIE20					("npci:E20", "FAILED", "Your fund transfer could not be processed."),
//	NPCIE12					("npci:E12", "FAILED", "Your fund transfer could not be processed."),
	NPCIE92					("npci:E92", "Pending", "Your fund transfer could not be processed."), // Retry after 30 minutes
	NPCIE22					("npci:E22", "Pending", "Your fund transfer could not be processed."), // Retry after 30 minutes
	NPCIE01					("npci:E01", "Pending", "Your fund transfer could not be processed."), // Retry after 30 minutes
	NPCIEM1					("npci:EM1", "FAILED", "Invalid Beneficiary MMID/Mobile Number"),
	NPCIEM2					("npci:EM2", "FAILED", "Amount limit exceeded"),
	NPCIEM3					("npci:EM3", "FAILED", "Account blocked/frozen"),
	NPCIEM4					("npci:EM4", "FAILED", "Beneficiary Bank is not enabled for Foreign Inward Remittance"),
	NPCIEM5					("npci:EM5", "FAILED", "Account closed"),
	NPCIE0					("npci:E0", "Pending", "No description found for NPCI response code : 0 in ATOM.ERROR_MASTER"), // Retry after 30 minutes
	NPCIE13					("npci:E13", "FAILED", "Invalid amount field"),
	NPCIE30					("npci:E30", "FAILED", "Invalid message format"),
	NPCIE52					("npci:E52", "FAILED", "INVALID ACCOUNT"),
	NPCIE80					("npci:E80", "Pending", "No description found for NPCI response code : 80 in ATOM.ERROR_MASTER"), // Retry after 30 minutes
	NPCIE307				("npci:E307", "Pending", "Rejected/Failed at beneficiary bank"), // Retry after 30 minutes
	NPCIE308				("npci:E308", "FAILED", "Rejected by beneficiary bank in reconciliation"),
	NPCIE449				("npci:E449", "FAILED", "Rejected by beneficiary bank for request parameters passed"),
	NPCIE450				("npci:E450", "FAILED", "Beneficiary bank rejection"),
	ATOME11					("atom:E11", "Pending", "CBS ISSUE"), // Retry after 30 minutes
	ATOME307				("atom:E307", "Pending", "Rejected/Failed at upstream IMPS Service"), // Retry after 30 minutes
	ATOME404				("atom:E404", "FAILED", "No Relationship Exists with the debit Account {AccountNo} and partner"),
	ATOME449				("atom:E449", "FAILED", "Rejected by upstream IMPS Service for the request parameters passed"),
	ATOME450				("ATOM:E450", "FAILED", "System Rejects due to format/functional issues"),
	ATOMEA19				("atom:EA19", "Pending", "Sorry, we are unable to process your request. Please try again later."), // Retry after 30 minutes
	SFMS99					("sfms:99", "Pending", "An Unhandled Exception Occurred In Processing"), // Retry after 30 minutes
	SFMSE18					("sfms:E18", "FAILED", "Rejected by SFMS"),
	SFMSE55					("sfms:E55", "Pending", "Authorisation Complete"), // Retry after 30 minutes
	SFMSE59					("sfms:E59", "Pending", "Message Encoded"), // Retry after 30 minutes
	SFMSE70					("sfms:E70", "FAILED", "Outward Transaction Rejected"),
	SFMSE99					("sfms:E99", "FAILED", "Manually Marked in Error"),
	SFMSEREV				("sfms:EREV", "FAILED", "The IMPS transaction has reversed back to remitter account"),
	SETTLEMENT_IN_PROCESS	("SettlementInProcess", "SettlementInProcess", "Response not received within set time limit."),
	NSE500					("ns:E500", "SettlementInProcess", "Internal Server Error"),
//	NSE502					("ns:E502", "SettlementInProcess", "Bad Gateway"),
	NSE504					("ns:E504", "SettlementInProcess", "Gateway Timeout"),
	NSE9001					("ns:E9001", "SettlementInProcess", "A request was found, but a corresponding entry for IMPS in CBS (transaction_master) for Ref"),
	NSE9002					("ns:E9002", "SettlementInProcess", "A request was found, but a corresponding entry for NEFT in CBS (pm_bulk_msg_details) for Ref"),
	NPCIENA					("npci:ENA", "SettlementInProcess", "No description found for NPCI response code : NA in ATOM.ERROR_MASTER 80"),
	NPCIE91					("npci:E91", "SettlementInProcess", "Kindly confirm with YES Bank before reinitiating"),
//	SFMSE55					("sfms:E55", "SettlementInProcess", "Authorisation Complete"),
	SETTLEMENT_REVERSED		("SettlementReversed", "SettlementReversed", "Transaction accepted by RBI but beneficiary bank rejected it."),
	SFMSE62					("sfms:E62", "SettlementReversed", "Multiple reasons why beneficiary account could not be credited"),
	PENDING					("Pending", "Pending", "Transaction is under process"),
	ACCEPTED				("Accepted", "Accepted", "Transaction Accepted"),
	SETTLEMENT_COMPLETED 	("SettlementCompleted", "SettlementCompleted", "Settled"),
	FAILED					("Failed", "FAILED", "Transaction Failed"),
	UNKNOWN					("Unknown", "Unknown", "Unknown error"),
	
	;
	
	
	private final String bankCode;
	private final String statusName;
	private final String message;
	
	
	
	private YesBankFT3ResultType(String bankCode, String statusName, String message) {
		this.bankCode = bankCode;
		this.statusName = statusName;
		this.message = message;
	}
	
	public static YesBankFT3ResultType getInstanceFromCode(String code) {
		YesBankFT3ResultType[] statusTypes = YesBankFT3ResultType.values();
		for (YesBankFT3ResultType statusType : statusTypes) {
			if (statusType.getBankCode().equalsIgnoreCase(code)) {
				return statusType;
			}
		}
		return null;
	}
	
	public String getBankCode() {
		return bankCode;
	}

	public String getMessage() {
		return message;
	}

	public String getStatusName() {
		return statusName;
	}
}
