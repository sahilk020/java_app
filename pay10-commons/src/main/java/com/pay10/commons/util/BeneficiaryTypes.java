package com.pay10.commons.util;

/**
 * @author Shaiwal
 *
 */
public enum BeneficiaryTypes {

	// NodalPaymentTypes
	Dealer			("D", "Dealer", "D"), 
	Vendor			("V", "Vendor", "V"),
	Other			("O", "Other", "O") ,
	Merchant		("M", "Merchant", "D"),
	PG				("PG", "Payment Gateway", "O")
	
	;
	
	private final String code;
	private final String name;
	private final String bankCode;

	private BeneficiaryTypes(String code, String name, String bankCode) {
		this.code = code;
		this.name = name;
		this.bankCode = bankCode;
	}

	public String getCode() {
		return code;
	}

	public String getName() {
		return name;
	}

	public String getBankCode() {
		return bankCode;
	}

	public static BeneficiaryTypes getInstancefromCode(String acquirerCode) {
		BeneficiaryTypes acquirerType = null;

		for (BeneficiaryTypes acquirer : BeneficiaryTypes.values()) {

			if (acquirerCode.equals(acquirer.getCode().toString())) {
				acquirerType = acquirer;
				break;
			}
		}

		return acquirerType;
	}


	public static BeneficiaryTypes getInstancefromName(String acquirerName) {
		BeneficiaryTypes acquirerType = null;

		for (BeneficiaryTypes acquirer : BeneficiaryTypes.values()) {

			if (acquirerName.equals(acquirer.getName())) {
				acquirerType = acquirer;
				break;
			}
		}

		return acquirerType;
	}

}
