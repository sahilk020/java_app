package com.pay10.atom;

import java.util.ArrayList;
import java.util.List;

import com.pay10.commons.util.Helper;
import com.pay10.commons.util.PropertiesManager;


public enum AtomMopType {


	//ATOM UAT
	ATOM_BANK ("Atom Bank","2001" ,"2001"),
	
	//ATOM LIVE
	
	ALLAHABAD_BANK("Allahabad Bank", "1110","1056"),
	ANDHRA_BANK ("Andhra Bank", "1091","1058"),
	AXIS_BANK ("Axis Bank", "1005","1003"),
	BANK_OF_BRAODA_CORPORATE_ACCOUNTS("Bank of Baroda Corporate Accounts", "1092", "1075"),
	BANK_OF_BARODA_RETAIL_ACCOUNTS("Bank of Baroda Retail Accounts", "1093", "1076"),
	BANK_OF_INDIA ("Bank Of India", "1009", "1012"),
	BANK_OF_MAHARASHTRA ("Bank Of Maharashtra", "1064", "1033"),
	CANARA_BANK ("Canara Bank","1055","1030"),
	CENTRAL_BANK_OF_INDIA ("Central Bank of India", "1063","1028"),
	CORPORATION_BANK ("Coporation Bank","1034","1004"),
	CATHOLIC_SYRIAN_BANK("Catholic Syrian Bank","1094", "1031"),
	DCB_BANK ("DCB Bank", "1040","1027"),
	DEUTSCHE_BANK ("Deutsche Bank", "1026","1024"),
	DHANLAXMI_BANK ("Dhanlaxmi Bank", "1070","1038"),
	FEDERAL_BANK ("Federal Bank", "1027","1019"),
	HDFC_BANK ("Hdfc Bank", "1004","1006"),
	ICICI_BANK ("Icici Bank", "1013","1002"),
	IDBI_BANK ("IDBI Bank", "1003","1007"),
	EQUITAS_BANK ("Equitas Bank", "1106","1063"),
	IDFC_FIRST_BANK("IDFC FIRST Bank Limited", "1107","1073"),
	INDIAN_BANK ("Indian Bank", "1069","1026"),
	INDIAN_OVERSEAS_BANK ("Indian Overseas Bank","1049", "1029"),
	INDUSIND_BANK ("Indusind Bank","1054", "1015"),
	JAMMU_AND_KASHMIR_BANK ("Jammu And Kashmir Bank","1041","1001"),
	KARNATAKA_BANK_LTD ("Karnatka Bank Ltd", "1032","1008"),
	KARUR_VYSYA_BANK ("KarurVysya Bank", "1048","1018"),
	KOTAK_BANK ("Kotak Bank", "1012","1013"),
	LAKSHMI_VILAS_BANK_NETBANKING("Lakshmi Vilas Bank NetBanking", "1095","1009"),
	ORIENTAL_BANK_OF_COMMERCE ("Oriental Bank Of Commerce", "1042","1035"),
	PUNJAB_AND_SIND_BANK ("Punjab and Sind Bank","1296","1077"),
	PNB_CORPORATE_BANK ("PNB Corporate Bank", "1101","1077"),
	PUNJAB_NATIONAL_BANK ("Punjab National Bank", "1002","1049"),
	RBL_BANK ("RBL Bank Limited","1053", "1066"),
	STATE_BANK_OF_INDIA ("State Bank Of India","1030", "1014"),
	SARASWAT_BANK ("SaraSwat Bank", "1056","1053"),
	TAMILNAD_MERCANTILE_BANK ("Tamilnad Mercantile Bank", "1065","1044"),
	UCO_BANK ("UCO Bank", "1103","1057"),
	UNION_BANK_OF_INDIA ("Union Bank Of India","1038","1016"),
	UNITED_BANK_OF_INDIA ("United Bank Of India", "1046","1041"),
	VIJAYA_BANK ("Vijaya Bank","1044", "1039"),
	YES_BANK ("Yes Bank","1001", "1005"),

	
	FREECHARGE_WALLET("FreeChargeWallet", "FCWL", "freecharge"),
	MOBIKWIK_WALLET("MobikwikWallet", "MWL", "mobikwik"),
	OLAMONEY_WALLET("OlaMoneyWallet", "OLAWL", "ola"),
	JIO_MONEY_WALLET("JioMoneyWallet", "JMWL", "jio"),
	AIRTEL_PAY_WALLET("AirtelPayWallet", "AWL", "airtel"),
	PAYTM_WALLET("PaytmWallet", "PPL", "paytm"),
	AMAZON_PAY_WALLET("AmazonPayWallet", "APWL", "amazon"),
	PHONE_PE_WALLET("PhonePeWallet", "PPWL", "phonepe"),
	GOOGLE_PAY_WALLET("GooglePayWallet", "GPWL", "gpay");
		

	private final String bankName;
	private final String code;
	private final String bankCode;
	
	private AtomMopType(String bankName, String code, String bankCode){
		this.bankName = bankName;
		this.code = code;
		this.bankCode = bankCode;
	}

	public String getBankName() {
		return bankName;
	}

	public String getCode(){
		return code;
	}

	public String getBankCode() {
		return bankCode;
	}

	public static AtomMopType getInstance(String name){
		AtomMopType[] mopTypes = AtomMopType.values();
		for(AtomMopType mopType : mopTypes){
			if(mopType.getBankName().equals(name)){
				return mopType;
			}
		}		
		return null;
	}

	
	public static List<AtomMopType> getGetMopsFromSystemProp(String mopsList){

		List<String> mopStringList= (List<String>) Helper.parseFields(PropertiesManager.propertiesMap.get(mopsList));

		List<AtomMopType> mops = new ArrayList<AtomMopType>();

		for(String mopCode:mopStringList){
			AtomMopType mop = getmop(mopCode);
			mops.add(mop);
		}
		return mops;
	}

	public static String getmopName(String mopCode){
		AtomMopType mopType = AtomMopType.getmop(mopCode);		
		if(mopType == null) {
			return "";
		}
		return mopType.getBankName();
	}
	
	
	public static String getBankCode(String code){
		AtomMopType mopType = AtomMopType.getmop(code);		
		if(mopType == null) {
			return "";
		}
		return mopType.getBankCode();
	}

	public static AtomMopType getmop(String mopCode){
		AtomMopType mopObj = null;
		if(null!=mopCode){
			for(AtomMopType mop:AtomMopType.values()){
				if(mopCode.equals(mop.getCode().toString())){
					mopObj=mop;
					break;
				}
			}
		}
		return mopObj;
	}	
	
	public static String getMopTypeName(String mopCode) {
		String moptType = null;
		if (null != mopCode) {
			for (AtomMopType mop : AtomMopType.values()) {
				if (mopCode.equals(mop.getBankName().toString())) {
					moptType = mop.getCode();
					break;
				}
			}
		}
		return moptType;
	}
	
	public static AtomMopType getInstanceIgnoreCase(String name){
		AtomMopType[] mopTypes = AtomMopType.values();
		for(AtomMopType mopType : mopTypes){
			if(mopType.getBankName().equalsIgnoreCase(name)){
				return mopType;
			}
		}		
		return null;
	}
}
