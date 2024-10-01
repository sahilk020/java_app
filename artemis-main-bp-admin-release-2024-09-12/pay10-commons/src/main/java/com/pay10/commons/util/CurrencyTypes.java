package com.pay10.commons.util;

/**
 * @author Shaiwal
 *
 */
public enum CurrencyTypes {

	// NodalPaymentTypes
	INR("INR", "356");
	/*UPI("UPI", "UPI"), 
	FT("FT", "FT"), 
	IMPS("IMPS", "IMPS")*/

	private final String code;
	private final String name;

	private CurrencyTypes(String code, String name) {
		this.code = code;
		this.name = name;
	}

	public String getCode() {
		return code;
	}

	public String getName() {
		return name;
	}

	public static CurrencyTypes getInstancefromCode(String acquirerCode) {
		CurrencyTypes acquirerType = null;

		for (CurrencyTypes acquirer : CurrencyTypes.values()) {

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
			for (CurrencyTypes acquirer : CurrencyTypes.values()) {
				if (acquirerCode.equals(acquirer.getCode())) {
					acquirertype = acquirer.getName();
					break;
				}
			}
		}
		return acquirertype;
	}

	public static CurrencyTypes getInstancefromName(String acquirerName) {
		CurrencyTypes acquirerType = null;

		for (CurrencyTypes acquirer : CurrencyTypes.values()) {

			if (acquirerName.equals(acquirer.getName())) {
				acquirerType = acquirer;
				break;
			}
		}

		return acquirerType;
	}

}
