package com.pay10.commons.util;

public enum NodalPaymentTypesYesBankFT3 {

	// NodalPaymentTypes
	NEFT("N", "NEFT", "NEFT", "999999999", "1", "999999999"), 
	RTGS("R", "RTGS", "RTGS", "999999999", "20000.1", "999999999"),
//	OTHR("O", "OTHR", "OTHR", "999999999"),
	FT("FT", "FT", "FT", "999999999", "1", "999999999"), 
	IMPS("IMPS", "IMPS", "IMPS", "999999999", "1", "200000"),
	
	;
	
	/*UPI("UPI", "UPI"), 
	*/

	private final String code;
	private final String name;
	private final String pgNodalPaymentTypes;
	private final String defaultTransactionLimit;
	private final String minimumAmount;
	private final String maximumAmount;

	private NodalPaymentTypesYesBankFT3(String code, String name, String pgNotalPaymentTypes, String defaultTransactionLimit, String minimumAmount, String maximumAmount) {
		this.code = code;
		this.name = name;
		this.pgNodalPaymentTypes = pgNotalPaymentTypes;
		this.defaultTransactionLimit = defaultTransactionLimit;
		this.minimumAmount = minimumAmount;
		this.maximumAmount = maximumAmount;
	}

	public String getCode() {
		return code;
	}

	public String getName() {
		return name;
	}
	
	public String getPgNodalPaymentType() {
		return pgNodalPaymentTypes;
	}
	
	public String getDefaultTransactionLimit() {
		return defaultTransactionLimit;
	}

	public static NodalPaymentTypesYesBankFT3 getInstancefromCode(String acquirerCode) {
		NodalPaymentTypesYesBankFT3 acquirerType = null;

		for (NodalPaymentTypesYesBankFT3 acquirer : NodalPaymentTypesYesBankFT3.values()) {

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
			for (NodalPaymentTypesYesBankFT3 acquirer : NodalPaymentTypesYesBankFT3.values()) {
				if (acquirerCode.equals(acquirer.getCode())) {
					acquirertype = acquirer.getName();
					break;
				}
			}
		}
		return acquirertype;
	}

	public static NodalPaymentTypesYesBankFT3 getInstancefromName(String acquirerName) {
		NodalPaymentTypesYesBankFT3 acquirerType = null;

		for (NodalPaymentTypesYesBankFT3 acquirer : NodalPaymentTypesYesBankFT3.values()) {

			if (acquirerName.equals(acquirer.getName())) {
				acquirerType = acquirer;
				break;
			}
		}

		return acquirerType;
	}
	
	public String getMinimumAmount() {
		return minimumAmount;
	}

	public String getMaximumAmount() {
		return maximumAmount;
	}

	public static NodalPaymentTypes getPgNodalInstance(String name ) {
		NodalPaymentTypes nodalPaymentTypes = null;
		NodalPaymentTypesYesBankFT3 nodalPaymentTypesYesBankFT3 = getInstancefromName(name);
		if(nodalPaymentTypesYesBankFT3 == null) {
			return null;
		}
		
		nodalPaymentTypes = NodalPaymentTypes.getInstancefromName(nodalPaymentTypesYesBankFT3.getPgNodalPaymentType());
		return nodalPaymentTypes;
	}

}
