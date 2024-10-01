package com.pay10.easebuzz;

import java.util.ArrayList;
import java.util.List;

import com.pay10.commons.util.Helper;
import com.pay10.commons.util.PropertiesManager;


public enum EasebuzzMopType {

	// add  NB BY   abhishek 
	BANK_OF_BARODA_RETAIL_ACCOUNTS("Bank of Baroda Retail Accounts","1093", "BOB"),
	FEDERAL_BANK("Federal Bank", "1027","FEDB"),
	JAMMU_AND_KASHMIR_BANK("Jammu And Kashmir Bank", "1041","JKB"),
	LAKSHMI_VILAS_BANK_NETBANKING("Laxmi Vilas Bank-Retail Net Banking", "1095","LVBRNB"),
	RBL_BANK("RBL Bank Limited", "1053", "RBLBL"),
	STANDARD_CHARTERED_BANK("Standard Chartered Bank", "1097","SCB"),
	SOUTH_INDIAN_BANK("South Indian Bank", "1045", "SIB"),
	TN_STATE_COOP_BANK("Tamil Nadu State Co-operative Bank", "1201", "TNSCB"),
	BANDHAN_BANK ("Bandhan Bank", "1206","BAN"),
	KOTAK_BANK("Kotak Mahindra Bank", "1012", "KTB"),
	ALLAHABAD_BANK("Allahabad Bank", "1110", "ALB"),
	CITY_UNION_BANK("City Union Bank", "1060", "CUB"),
	AXIS_BANK("Axis Bank", "1005", "AXB"),
	ANDHRA_BANK("Andhra Bank", "1091", "ANB"),
	ANDHRA_BANK_CORPORATE("Andhra Bank Corporate", "1200", "ANBC"),
	BANK_OF_BRAODA_CORPORATE_ACCOUNTS("Bank of Baroda - Corporate", "1092", "BOBCB"),
	BANK_OF_BAHRAIN_AND_KUWAIT("Bank Of Bahrain And Kuwait", "1043", "BOBAK"),
	BANK_OF_MAHARASHTRA("Bank Of Maharashtra", "1064", "BOMH"),
	CENTRAL_BANK_OF_INDIA("Central Bank Of India", "1063", "CBOI"),
	 CANARA_BANK("Canara Bank", "1055", "CANB"),
	 COSMOS_BANK("COSMOS Bank", "1104", "COSB"),
		PUNJAB_NATIONAL_BANK_CORPORATE_ACCOUNTS("Punjab National Bank Corporate Accounts", "1096", "PNBCB"),
		CORPORATION_BANK("Corporation Bank", "1034", "CORPB"),
		CATHOLIC_SYRIAN_BANK("Catholic Syrian Bank", "1094", "CSB"),
		DEUTSCHE_BANK("Deutsche Bank", "1026", "DEUTB"),
		DEVELOPMENT_CREDIT_BANK("Development Credit Bank", "1040", "DCB"),
		DENA_BANK("Dena Bank", "2004","DENAB"),
		DHANLAXMI_BANK("Dhanlakshmi Bank", "1070","DHANB"),
		IDBI_BANK("IDBI Bank", "1003", "IDBIB"),
		INDUSIND_BANK("Indusind Bank", "1054", "INDUSB"),
		INDIAN_BANK("Indian Bank", "1069", "INB"),
		INDIAN_OVERSEAS_BANK("Indian Overseas Bank", "1049", "IOB"),
		KARNATAKA_BANK_LTD("Karnataka Bank Ltd", "1032", "KBL"),
		KARUR_VYSYA_BANK("Karur Vysya Bank", "1048", "KVB"),
		LAXMIVILAS_BANK_CORPORATE("Lakshmi Vilas Bank - Corporate", "1303", "LVBCNB"),
		ORIENTAL_BANK_OF_COMMERCE("Oriental Bank Of Commerce", "1042", "OBOC"),
		PUNJAB_NATIONAL_BANK_RETAIL_BANKING("Punjab National Bank - Retail Net Banking", "2013", "PNBRB"),
		PUNJAB_AND_SIND_BANK("Punjab & Sind Bank", "1296", "PASB"),
		SHAMRAO_VITHAL_CORPORATE ("Shamrao Vithal Co op Bank - Corporate", "1217", "SVCB"),
		ICICI_BANK("Icici Bank", "1013", "ICICIB"),
		SARASWAT_BANK("SaraSwat Bank", "1056", "SARB"),
		 SYNDICATE_BANK("Syndicate Bank", "1098", "SYNB"),
			TAMILNAD_MERCANTILE_BANK("Tamilnad Mercantile Bank Ltd", "1065", "TMBL"),
			UNION_BANK_OF_INDIA("Union Bank Of India", "1038", "UBOI"),
			UCO_BANK("UCO Bank", "1103", "UCOB"),
			UNITED_BANK_OF_INDIA("United Bank Of India", "1046", "UNBOI"),
			VIJAYA_BANK("Vijaya Bank", "1044", "VIJB"),
			YES_BANK("Yes Bank Ltd", "1001", "YESBL"),
			JANATA_SAHKARI_BANK("Janata Sahakari Bank Pune", "1072", "JSBLP"),
			NKGSB_COOP_BANK("NKGSB Co op Bank", "1202", "NKGSBC"),
			TJSB_BANK("TJSB Bank", "1203", "TJSBB"),
			MEHSANA_URBAN_COOP_BANK ("Mehsana urban Co-op Bank", "1205", "MUCB"),
			AXIS_CORPORATE_BANK("Axis Corporate Bank", "1099","ACNB"),
			HDFC_BANK("Hdfc Bank", "1004", "HDFCB"),
			STATE_BANK_OF_HYDERABAD("State Bank Of Hyderabad", "1039", "SBOH"),
			STATE_BANK_OF_INDIA("State Bank Of India", "1030", "SBOI"),
			STATE_BANK_OF_BIKANER_AND_JAIPUR("State Bank Of Bikaner And Jaipur", "1050", "SBOBJ"),
			STATE_BANK_OF_MYSORE("State Bank Of Mysore", "1037", "SBOM"),
			STATE_BANK_OF_PATIALA("State Bank Of Patiala", "1068", "SBOP"),
			IDFC_FIRST_BANK("IDFC FIRST Bank Limited", "1107", "IDFCFB"),
			BASSIEN_CATH_COOP_BANK ("Bassien Catholic Coop Bank","1208", "BCCB"),
			STATE_BANK_OF_TRAVANCORE("State Bank Of Travancore", "1061", "SBOT"),
			KALUPUR_COMM_COOP_BANK ("The Kalupur Commercial Co-Operative Bank","1209", "TKCCBL"),
			DCB_BANK("DCB Bank", "1040", "DCBB"),
			BANK_OF_INDIA("Bank Of India", "1009", "BOIND"),
			EQUITAS_BANK("Equitas Small Finance Bank", "1106", "EQSFB"),
			DBS_BANK("DBS Bank Ltd", "1300", "DBSB"),
			ROYAL_BANK_OF_SCOTLAND("Royal Bank Of Scotland", "1145", "RBO"),
			KARNATAKA_GRAMINA_BANK ("Karnataka Gramina Bank", "1221", "KAGB"),
			FINCARE_BANK ("Fincare Bank", "1222", "FIB"),
			HSBC_RETAIL_NETBANKING("HSBC Retail Netbanking", "1315", "HSBC"),
			ICICI_CORPORATE_BANK("ICICI Corporate Bank", "1100", "ICICICO"),


		// ADD WALLET BY ABHISHEK
	
	JIO_MONEY_WALLET("JioMoneyWallet", "JMWL", "JIOM"),
	OLAMONEY_WALLET("OlaMoneyWallet", "OLAWL", "OLAM"),
	OXYZEN_WALLET("OxyzenWallet", "OXWL", "OXW"),
	PAYCASH_WALLET("PaycashWallet", "PCWL", "PAYC"),
	ICC_CASH_CARD ("ICC Cash Card", "ICC", "ICCCC"),
	PAYTM_WALLET("PaytmWallet", "PPL", "PAYTM"),
	MOBIKWIK_WALLET("MobikwikWallet", "MWL", "MOBKWK"),
	AIRTEL_PAY_WALLET("AirtelPayWallet", "AWL", "AIRTLM"),
	ITZ_CASH_WALLET("ItzCashWallet", "ICWL", "ITZCC");
	
	private final String bankName;
	private final String code;
	private final String bankCode;
	
	private EasebuzzMopType(String bankName, String code, String bankCode){
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

	public static EasebuzzMopType getInstance(String name){
		EasebuzzMopType[] mopTypes = EasebuzzMopType.values();
		for(EasebuzzMopType mopType : mopTypes){
			if(mopType.getBankName().equals(name)){
				return mopType;
			}
		}		
		return null;
	}

	
	public static List<EasebuzzMopType> getGetMopsFromSystemProp(String mopsList){

		List<String> mopStringList= (List<String>) Helper.parseFields(PropertiesManager.propertiesMap.get(mopsList));

		List<EasebuzzMopType> mops = new ArrayList<EasebuzzMopType>();

		for(String mopCode:mopStringList){
			EasebuzzMopType mop = getmop(mopCode);
			mops.add(mop);
		}
		return mops;
	}

	public static String getmopName(String mopCode){
		EasebuzzMopType mopType = EasebuzzMopType.getmop(mopCode);		
		if(mopType == null) {
			return "";
		}
		return mopType.getBankName();
	}
	
	
	public static String getBankCode(String code){
		EasebuzzMopType mopType = EasebuzzMopType.getmop(code);		
		if(mopType == null) {
			return "";
		}
		return mopType.getBankCode();
	}

	public static EasebuzzMopType getmop(String mopCode){
		EasebuzzMopType mopObj = null;
		if(null!=mopCode){
			for(EasebuzzMopType mop:EasebuzzMopType.values()){
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
			for (EasebuzzMopType mop : EasebuzzMopType.values()) {
				if (mopCode.equals(mop.getBankName().toString())) {
					moptType = mop.getCode();
					break;
				}
			}
		}
		return moptType;
	}
	
	public static EasebuzzMopType getInstanceIgnoreCase(String name){
		EasebuzzMopType[] mopTypes = EasebuzzMopType.values();
		for(EasebuzzMopType mopType : mopTypes){
			if(mopType.getBankName().equalsIgnoreCase(name)){
				return mopType;
			}
		}		
		return null;
	}
}
