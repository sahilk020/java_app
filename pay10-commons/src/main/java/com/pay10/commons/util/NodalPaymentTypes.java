package com.pay10.commons.util;

/**
 * @author Shaiwal
 *
 */
public enum NodalPaymentTypes {

	// NodalPaymentTypes
	NEFT("N", "NEFT"), 
	RTGS("R", "RTGS"),
	IFT("I", "IFT"),
	OTHR("O", "OTHR"),
	FT("FT", "FT"), 
	IMPS("IMPS", "IMPS"),
	
	;
	
	/*UPI("UPI", "UPI"), 
	*/

	private final String code;
	private final String name;

	private NodalPaymentTypes(String code, String name) {
		this.code = code;
		this.name = name;
	}

	public String getCode() {
		return code;
	}

	public String getName() {
		return name;
	}

	public static NodalPaymentTypes getInstancefromCode(String acquirerCode) {
		NodalPaymentTypes acquirerType = null;

		for (NodalPaymentTypes acquirer : NodalPaymentTypes.values()) {

			if (acquirerCode.equals(acquirer.getCode())) {
				acquirerType = acquirer;
				break;
			}
		}

		return acquirerType;
	}

	public static String getAcquirerName(String acquirerCode) {
		String acquirertype = null;
		if (null != acquirerCode) {
			for (NodalPaymentTypes acquirer : NodalPaymentTypes.values()) {
				if (acquirerCode.equals(acquirer.getCode())) {
					acquirertype = acquirer.getName();
					break;
				}
			}
		}
		return acquirertype;
	}

	public static NodalPaymentTypes getInstancefromName(String acquirerName) {
		NodalPaymentTypes acquirerType = null;

		for (NodalPaymentTypes acquirer : NodalPaymentTypes.values()) {

			if (acquirerName.equals(acquirer.getName())) {
				acquirerType = acquirer;
				break;
			}
		}

		return acquirerType;
	}

}
