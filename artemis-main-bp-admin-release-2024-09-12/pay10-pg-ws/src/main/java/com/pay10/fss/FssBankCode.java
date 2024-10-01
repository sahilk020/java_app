package com.pay10.fss;

public enum FssBankCode {
	//Netbanking
	AXIS_BANK		("Axis bank", "1005" , "201"),
	ICICI_BANK 		("ICICI bank", "1007" , "411" ),
	
	//Wallet
	AMAZON_PAY		("Amazon pay", "APWL" , "3001"),
	AIRTEL_PAY		("Airtel pay", "AWL" , "3008"),
	FREE_CHARGE		("Free Charge", "FCWL" , "3002"),
	GOOGLE_PAY		("Googlepay", "GPWL" , ""),
	ITZ_CASH		("Itz Cash", "ICWL" , ""),
	JIO_MONEY		("Jio Money", "JMWL" , "3004"),
	MOBIKWIK		("MobiKwik", "MWL" , ""),
	M_PESA			("Mpesa", "MPWL" , ""),
	OLA_MONEY		("Ola money", "OLAWL" , "3005"),
	OXYZEN_WALLET	("Oxyzen Wallet", "OXWL" , ""),
	PAYTM			("Paytm", "", "PPI"),
	PHONE_PE		("Phonepe", "PPWL" , ""),
	SBI_BUDDY		("SBI Buddy ", "SBWL" , ""),
	ZIP_CASH		("Zipcash", "ZCWL" , "");
	
	
	private FssBankCode(String bankName, String code, String bankCode) {
		this.code = code;
		this.bankCode = bankCode;
		this.bankName = bankName;
		
	}

	public static FssBankCode getInstanceFromCode(String code) {
		FssBankCode[] statusTypes = FssBankCode.values();
		for (FssBankCode statusType : statusTypes) {
			if (String.valueOf(statusType.getCode()).toUpperCase().equals(code)) {
				return statusType;
			}
		}
		return null;
	}
	
	public static String getBankCode(String code) {
		String bankCode = null;
		if (null != code) {
			for (FssBankCode bank : FssBankCode.values()) {
				if (code.equals(bank.getCode().toString())) {
					bankCode = bank.getBankCode();
					break;
				}
			}
		}
		return bankCode;
	}

	private final String code;
	private final String bankCode;
	private final String bankName;
	
	
	public String getBankCode() {
		return bankCode;
	}

	public String getBankName() {
		return bankName;
	}
	
	public String getCode() {
		return code;
	}
}
