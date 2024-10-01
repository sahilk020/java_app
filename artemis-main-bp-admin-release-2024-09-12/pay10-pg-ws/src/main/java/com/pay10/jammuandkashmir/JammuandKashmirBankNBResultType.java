package com.pay10.jammuandkashmir;


	public enum JammuandKashmirBankNBResultType {
		
		JAMMUANDKISHMIRBANKNB000			("Y" , "000" , "SUCCESS" , "No Error."),
		JAMMUANDKISHMIRBANKNB001			("N" , "004" , "Declined", "Transaction Declined"),
		JAMMUANDKISHMIRBANKNB003			("S" , "004" , "Declined", "Suspect/Doubftul Transaction"),

		JAMMUANDKISHMIRBANKNB002			("P" , "006" , "Processing", "Pending at Bank End");
		
		
		private JammuandKashmirBankNBResultType(String bankCode, String iPayCode, String statusCode, String message) {
			this.bankCode = bankCode;
			this.iPayCode = iPayCode;
			this.statusCode = statusCode;
			this.message = message;
		}

		public static JammuandKashmirBankNBResultType getInstanceFromName(String code) {
			JammuandKashmirBankNBResultType[] statusTypes = JammuandKashmirBankNBResultType.values();
			for (JammuandKashmirBankNBResultType statusType : statusTypes) {
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
