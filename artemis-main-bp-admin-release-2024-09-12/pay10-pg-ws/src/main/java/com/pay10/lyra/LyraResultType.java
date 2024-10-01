package com.pay10.lyra;


public enum LyraResultType {

	LYRA001			("00" , "000" , "Captured" , "completed successfully"),
	LYRA002			("1" , "022" , "Failed at Acquirer" , "Refer to card issuer"),
	LYRA003			("2" , "022" , "Failed at Acquirer" , "Refer to card issuer, special condition"),
	LYRA004			("3" , "004" , "Declined" , "Invalid merchant"),
	LYRA005			("4" , "002" , "Denied by risk" , "Capture card"),
	LYRA006			("5" , "004" , "Declined" , "Do not honor"),
	LYRA007			("6" , "022" , "Failed at Acquirer" ,"Error"),
	LYRA008			("7" , "002" , "Denied by risk" , "Pickup card, special condition (other than lost/stolen card)"),
	LYRA009			("8" , "004" , "Declined" , "Honor with ID"),
	LYRA010			("10" , "004" , "Declined" , "Partial Approval"),
	LYRA011			("12" , "004" , "Declined" , "Invalid transaction"),
	LYRA012			("13" , "004" , "Declined" , "Invalid amount"),
	LYRA013			("14" , "004" , "Declined" , "Invalid card number"),
	LYRA014			("15" , "004" , "Declined" , "Invalid issuer"),
	LYRA015			("17" , "010" , "Cancelled" , "Customer cancellation."),
	//LYRA016			("19" , "022" , "Failed at Acquirer" , "Re-enter transaction"),
	LYRA017			("20" , "022" , "Failed at Acquirer" , "Invalid response."),
	LYRA018			("21" , "022" , "Failed at Acquirer" , "No action taken."),
	LYRA019			("22" , "002" , "Denied by risk" , "Suspected malfunction."),
	LYRA020			("25" , "004" , "Declined" , "Unable to locate record"),
	//LYRA021			("27" , "004" , "Declined" ,"File Update field edit error"),
	//LYRA022			("28" , "004" , "Declined" ,  "Record already exist in the file"),
	//LYRA023			("29" , "004" , "Declined" , "File Update not successful"),
	LYRA024			("30" , "004" , "Declined" , "Format error"),
	LYRA025			("31" , "022" , "Failed at Acquirer" , "Bank not supported by switch"),
	LYRA026			("32" , "004" , "Declined" , "Partial Reversal"),
	LYRA027			("33" , "004" , "Declined" , "Expired card, capture"),
	LYRA028			("34" , "002" , "Denied by risk" , "Suspected fraud, capture."),
	LYRA029			("36" , "004" , "Declined" , "Restricted card, capture"),
	LYRA030			("38" , "004" , "Declined" , "Allowable PIN tries exceeded, capture."),
	LYRA031			("39" , "004" , "Declined" , "No credit account."),
	LYRA032			("40" , "004" , "Declined" , "Requested function not supported."),
	
	LYRA033			("41" , "002" , "Denied by risk", "Lost card, capture."),
	LYRA034			("42" , "004" , "Declined" ,"No universal account."),
	LYRA035			("43" , "002" , "Denied by risk" , "Stolen card, capture."),
	LYRA036			("51" , "004" , "Declined" , "Insufficient funds/over credit limit"),
	LYRA037			("52" , "004" , "Declined" , "No checking account"),
	LYRA038			("53" , "004" , "Declined" , "No savings account."),
	LYRA039			("54" , "004" , "Declined" , "Expired card"),
	LYRA040			("55" , "004" , "Declined" ,  "Invalid PIN"),
	LYRA041			("57" , "004" , "Declined" , "Transaction not permitted to Cardholder"),
	LYRA042			("58" , "004" , "Declined" , "Transaction not permitted to terminal."),
	LYRA043			("59" , "002" , "Denied by risk" , "Suspected fraud"),
	LYRA044			("60" , "004" , "Declined" , "Card acceptor contact acquirer, decline."),
	LYRA045			("61" , "004" , "Declined" , "Exceeds withdrawal amount limit."),
	LYRA046			("62" , "004" , "Declined" , "Restricted card"),
	LYRA047			("63" , "004" , "Declined" , "Security violation."),
	LYRA048			("64" , "004" , "Declined" , "Transaction does not fulfill AML requirement (Stand-In Processing)"),
	LYRA049			("65" , "004" , "Declined" , "Exceeds withdrawal frequency limit."),
	LYRA050			("66" , "004" , "Declined" , "Card acceptor calls acquirer"),
	LYRA051			("67" , "004" , "Declined" , "Hard capture (requires that card be picked up at ATM)."),
	
	LYRA052			("68" , "003" , "Timed out at Acquirer" , "Acquirer time-out"),
	LYRA053			("69" , "004" , "Declined" , "Mobile number record not found/ miss-match"),
	LYRA054			("70" , "004" , "Declined" , "Contact Card Issuer"),
	LYRA055			("71" , "004" , "Declined" , "Deemed Acceptance / PIN Not Changed"),
	LYRA056			("74" , "002" , "Denied by risk" , "Transactions declined by Issuer based on Risk Score"),
	LYRA057			("75" , "004" , "Declined" , "Allowable number of PIN tries exceeded"),
	LYRA058			("79" , "004" , "Declined" , "Transaction already reversed"),
	LYRA059			("82" , "003" , "Timed out at Acquirer" , "Timeout at issuer"),
	LYRA060			("84" , "004" , "Declined" , "Invalid Authorization Life Cycle"),
	LYRA061			("85" , "004" , "Declined" , "Not declined, Valid for all zero amount transactions"),
	LYRA062			("86" , "004" , "Declined" , "PIN Validation not possible"),
	LYRA063			("87" , "004" , "Declined" , "Purchase Amount Only, No Cash Back Allowed"),
	LYRA064			("88" , "022" , "Failed at Acquirer" , "Cryptographic failure"),
	LYRA065			("89" , "004" , "Declined" , "Unacceptable PIN - Transaction Declined Retry"),
	LYRA066			("90" , "004" , "Declined" , "Cut-off is in process."),
	LYRA067			("91" , "004" , "Declined" , "Authorization System or issuer system inoperative"),
	LYRA068			("92" , "022" , "Failed at Acquirer" , "Unable to route transaction"),
	LYRA070			("93" , "004" , "Declined" , "Transaction cannot be completed; violation of law"),
	LYRA071			("94" , "004" , "Declined" , "Duplicate transmission."),
	LYRA075			("DUE" , "006" , "Processing" , "DUE"),
	LYRA076			("DROP" , "022" , "Failed at Acquirer" , "DROP"),
	LYRA072			("95" , "022" , "Failed at Acquirer" , "Reconcile error"),
	LYRA073			("96" , "022" , "Failed at Acquirer" , "System malfunction"),
	LYRA074			("N7" , "004" , "Declined" , "Decline for CVV2 failure");

	
	
	private LyraResultType(String bankCode, String PGCode, String statusCode, String message) {
		this.bankCode = bankCode;
		this.PGCode = PGCode;
		this.statusCode = statusCode;
		this.message = message;
	}

	public static LyraResultType getInstanceFromName(String code) {
		LyraResultType[] statusTypes = LyraResultType.values();
		for (LyraResultType statusType : statusTypes) {
			if (String.valueOf(statusType.getBankCode()).toUpperCase().equals(code)) {
				return statusType;
			}
		}
		return null;
	}

	private final String bankCode;
	private final String PGCode;
	private final String statusCode;
	private final String message;

	public String getBankCode() {
		return bankCode;
	}
	
	public String getStatusCode() {
		return statusCode;
	}

	public String getMessage() {
		return message;
	}

	public String getPGCode() {
		return PGCode;
	}
}