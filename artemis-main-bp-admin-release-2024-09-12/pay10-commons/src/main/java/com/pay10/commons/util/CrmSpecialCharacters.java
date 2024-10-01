package com.pay10.commons.util;

public enum CrmSpecialCharacters {

	IP              ((new String(".:")).toCharArray()),
	PASSWORD        ((new String("!@,_+/=")).toCharArray()),
	ADDRESS         ((new String("-/() .,@;:# \r\n")).toCharArray()),
	OPERATINGSYSTEM ((new String(" ,.")).toCharArray()),
	WEBSITE			((new String(":./-")).toCharArray()),
	BUSINESSMODEL	((new String(":./ -")).toCharArray()),
	BROWSER			((new String("./ -")).toCharArray()),
	BUSINESS_NAME	((new String(". @_&")).toCharArray()),
	DATE            ((new String("/ -:")).toCharArray()),
	CARD_MASK       ((new String("*-X")).toCharArray()),
	SPACE           ((new String(" ")).toCharArray()),
	COMPANY         ((new String("-/() .,@;:# \r\n")).toCharArray()),
	ORDER_ID        ((new String(" -_+.")).toCharArray()),
	INVOICE_NUMBER				((new String(" -_+.")).toCharArray()),
	TELEPHONE     				((new String("+ -")).toCharArray()),
	AMEX_SELLER_ID_MCC			((new String("/")).toCharArray()),
	ALL_SPEICIAL_CHARACTERS     ((new String("!@,-_+/=*.:;()#~ \r\n")).toCharArray()),
	ALL_SPEICIAL_CHARACTERS_MAPPING     ((new String("!@$,-_+/=*.:;()#~ \r\n")).toCharArray()),
	SPEICIAL_CHARACTERS_FOR_TICKET((new String("~`!@#$%^&*()_+-={}[]:;'<>,.?/||&&\r\n| /")).toCharArray()),
	FRAUD_IP             ((new String(".:/")).toCharArray()),
	INDUSTRYCATEGORY            ((new String("_")).toCharArray()),
	INDUSTRYSUBCATEGORY         ((new String(" _")).toCharArray()),
	CARD_TYPE                   ((new String("_")).toCharArray()),
	SETTLEMENT_NAMING_CONVENTION                   ((new String("_")).toCharArray()),
	REFUNDVALIDATION_NAMING_CONVENTION             ((new String("_")).toCharArray()),
	COMMA             				((new String(",")).toCharArray()),
	CHARGEBACK_CHAT_SPECIAL_CHAR 	((new String("~`!@#$%^&*()_+-={}[]:;'<>,.?/||&&\r\n| /\"\\")).toCharArray()),
	WHITE_LABEL_URL 				((new String("-.")).toCharArray()),
	UNDERSCORE                   ((new String("_")).toCharArray()),
	BENE_EXPIRY_DATE				((new String("-")).toCharArray()),
	BENE_ADDRESS				((new String(",-_'/' ")).toCharArray()),
	BENE_BANK_NAME				((new String(",.-_\\D[^0-9] ")).toCharArray()),
	BENE_COMMENTS				((new String("@,.:-+ ")).toCharArray()),
	
	;
	
	private final char[] permittedSpecialChars;

	private CrmSpecialCharacters(char[] permittedSpecialChars)
	{
		this.permittedSpecialChars = permittedSpecialChars;
	}

	public char[] getPermittedSpecialChars() {
		return permittedSpecialChars;
	}
}
