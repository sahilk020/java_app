package com.pay10.cSource;

public enum CSourceResultType {

	CSOURCE001			("100" , "024" , "Failed at Acquirer" , "Card is not enrolled"),
	CSOURCE002			("101" , "021" , "Invalid" , "Declined   The request is missing one or more fields"),
	CSOURCE003			("102" , "021" , "Invalid" , "Declined   One or more fields in the request contains invalid data"),
	CSOURCE004			("104" , "021" , "Invalid" , "Declined ThemerchantReferenceCode sent with this authorization request matches the merchantReferenceCode of another authorization request that you sent in the last 15 minutes."),
	CSOURCE005			("110" , "007" , "Rejected" , "Partial amount was approved"),
	CSOURCE006			("150" , "022" , "Failed at Acquirer" , "Error   General system failure."),
	CSOURCE007			("151" , "003" , "Timed out at Acquirer" , "Error   The request was received but there was a server timeout."),
	CSOURCE008			("152" , "003" , "Timed out at Acquirer" , "Error The request was received, but a service did not finish running in time."),
	CSOURCE009			("200" , "004" , "Declined" , "Soft Decline   The authorization request was approved by the issuing bank but declined by CSOURCE."),
	CSOURCE010			("201" , "004" , "Declined" , "Decline   The issuing bank has questions about the request."),
	CSOURCE011			("202" , "004" , "Declined" , "Decline   Expired card."),
	CSOURCE012			("203" , "004" , "Declined" , "Decline   General decline of the card."),
	CSOURCE013			("204" , "004" , "Declined" , "Decline   Insufficient funds in the account."),
	CSOURCE014			("205" , "004" , "Declined" , "Decline   Stolen or lost card."),
	CSOURCE015			("207" , "004" , "Declined" , "Decline   Issuing bank unavailable."),
	CSOURCE016			("208" , "004" , "Declined" , "Decline   Inactive card or card not authorized for card not present transactions."),
	CSOURCE017			("209" , "004" , "Declined" , "Decline card verification number did not match."),
	CSOURCE018			("210" , "004" , "Declined" , "Decline   The card has reached the credit limit."),
	CSOURCE019			("211" , "004" , "Declined" , "Decline   Invalid Card Verification Number (CVN)."),
	CSOURCE020			("220" , "004" , "Declined" , "Decline   Generic Decline."),
	CSOURCE021			("221" , "004" , "Declined" , "Decline   The customer matched an entry on the processors negative file."),
	CSOURCE022			("222" , "004" , "Declined" , "Decline   customer's account is frozen"),
	CSOURCE023			("230" , "004" , "Declined" , "Soft Decline   The authorization request was approved by the issuing bank but declined by CSOURCE because it did not pass the card verification number (CVN) check."),
	CSOURCE024			("231" , "004" , "Declined" , "Decline   Invalid account number"),
	CSOURCE025			("232" , "004" , "Declined" , "Decline   The card type is not accepted by the payment processor."),
	CSOURCE026			("233" , "004" , "Declined" , "Decline   General decline by the processor."),
	CSOURCE027			("234" , "007" , "Rejected" , "Decline   There is a problem with your CSOURCE merchant configuration."),
	CSOURCE028			("235" , "007" , "Rejected" , "Decline   The requested amount exceeds the originally authorized amount."),
	CSOURCE029			("236" , "022" , "Failed at Acquirer" , "Decline   Processor failure."),
	CSOURCE030			("237" , "007" , "Rejected" , "Decline   The authorization has already been reversed."),
	CSOURCE031			("238" , "007" , "Rejected" , "Decline   The transaction has already been settled."),
	CSOURCE032			("239" , "007" , "Rejected" , "Decline   The requested transaction amount must match the previous transaction amount."),
	CSOURCE033			("240" , "021" , "Invalid" , "Decline   The card type sent is invalid or does not correlate with the credit card number."),
	CSOURCE034			("241" , "007" , "Rejected" , "Decline   The referenced request id is invalid for all follow on transactions."),
	CSOURCE035			("242" , "007" , "Rejected" , "Decline   The request ID is invalid."),
	CSOURCE036			("243" , "007" , "Rejected" , "Decline   The transaction has already been settled or reversed."),
	CSOURCE037			("246" , "007" , "Rejected" , "Decline   The capture or credit is not voidable because the capture or credit information has already been submitted to your processor. Or, you requested a void for a type of transaction that cannot be voided"),
	CSOURCE038			("247" , "007" , "Rejected" , "Decline   You requested a credit for a capture that was previously voided."),
	CSOURCE039			("248" , "004" , "Declined" , "Decline   The boleto request was declined by your processor."),
	CSOURCE040			("250" , "003" , "Timed out at Acquirer" , "Error   The request was received, but there was a timeout at the payment processor."),
	CSOURCE041			("251" , "004" , "Declined" , "Decline   The Pinless Debit card's use frequency or maximum amount per use has been exceeded."),
	CSOURCE042			("254" , "004" , "Declined" ,"Decline   Account is prohibited from processing stand alone refunds."),
	CSOURCE043			("400" , "012" , "Denied due to fraud" , "Soft Decline   Fraud score exceeds threshold."),
	CSOURCE044			("450" , "002" , "Denied by risk" , "Apartment number missing or not found."),
	CSOURCE045			("451" , "002" , "Denied by risk" , "Insufficient address information."),
	CSOURCE046			("452" , "002" , "Denied by risk" , "House/Box number not found on street."),
	CSOURCE047			("453" , "002" , "Denied by risk" , "Multiple address matches were found."),
	CSOURCE048			("454" , "002" , "Denied by risk" , "P.O. Box identifier not found or out of range."),
	CSOURCE049			("455" , "002" , "Denied by risk" , "Route service identifier not found or out of range."),
	CSOURCE050			("456" , "002" , "Denied by risk" , "Street name not found in Postal code."),
	CSOURCE051			("457" , "002" , "Denied by risk" , "Postal code not found in database."),
	CSOURCE052			("458" , "002" , "Denied by risk" , "Unable to verify or correct address."),
	CSOURCE053			("459" , "002" , "Denied by risk" , "Multiple addres matches were found (international)"),
	CSOURCE054			("460" , "002" , "Denied by risk" , "Address match not found (no reason given)"),
	CSOURCE055			("461" , "002" , "Denied by risk" , "Unsupported character set"),
	CSOURCE057			("476" , "010" , "Cancelled" , "Encountered a Payer Authentication problem."),
	CSOURCE058			("480" , "007" , "Rejected" , "The order is marked for review by Decision Manager"),
	CSOURCE059			("481" , "007" , "Rejected" , "The order has been rejected by Decision Manager"),
	CSOURCE060			("520" , "004" , "Declined" , "Soft Decline   The authorization request was approved by the issuing bank but declined by CSOURCE based on your Smart Authorization settings."),
	CSOURCE061			("700" , "002" , "Denied by risk" , "The customer matched the Denied Parties List"),
	CSOURCE062			("701" , "002" , "Denied by risk" , "Export bill_country/ship_country match"),
	CSOURCE063			("702" , "002" , "Denied by risk" , "Export email_country match"),
	CSOURCE064			("703" , "002" , "Denied By risk" , "Export hostname_country/ip_country match");
	
	
	
	

	private CSourceResultType(String bankCode, String iPayCode, String statusName, String message) {
		this.bankCode = bankCode;
		this.iPayCode = iPayCode;
		this.statusName = statusName;
		this.message = message;
	}

	public static CSourceResultType getInstanceFromName(String code) {
		CSourceResultType[] statusTypes = CSourceResultType.values();
		for (CSourceResultType statusType : statusTypes) {
			if (String.valueOf(statusType.getBankCode()).toUpperCase().equals(code)) {
				return statusType;
			}
		}
		return null;
	}

	private final String bankCode;
	private final String iPayCode;
	private final String message;
	private final String statusName;
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
