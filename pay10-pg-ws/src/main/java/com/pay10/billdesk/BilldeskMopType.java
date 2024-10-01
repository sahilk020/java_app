package com.pay10.billdesk;

import java.util.ArrayList;
import java.util.List;

import com.pay10.commons.util.Helper;
import com.pay10.commons.util.PropertiesManager;


public enum BilldeskMopType {

	
	//BILLDESK LIVE
		KOTAK_BANK ("Kotak Bank", "1012","162"),
		ANDHRA_BANK ("Andhra Bank", "1091","ADB"),
		ANDHRA_BANK_CORPORATE ("Andhra Bank Corporate", "1200","ADC"),
		BANK_OF_BRAODA_CORPORATE_ACCOUNTS ("Bank of Baroda Corporate Accounts", "1092","BBC"),
		BANK_OF_BAHRAIN_AND_KUWAIT ("Bank Of Bahrain And Kuwait","1043", "BBK"),
		BANK_OF_BARODA_RETAIL_ACCOUNTS ("Bank of Baroda Retail Accounts","1093","BBR"),
		BANK_OF_MAHARASHTRA ("Bank Of Maharashtra", "1064","BOM"),
		CENTRAL_BANK_OF_INDIA("Central Bank Of India", "1063","CBI"),
		CANARA_BANK("Canara Bank", "1055", "CNB"),
		COSMOS_BANK("COSMOS Bank", "1104","COB"),
		PUNJAB_NATIONAL_BANK_CORPORATE_ACCOUNTS("Punjab National Bank Corporate Accounts", "1096","CPN"),
		CORPORATION_BANK("Corporation Bank", "1034","CRP"),
		CATHOLIC_SYRIAN_BANK("Catholic Syrian Bank", "1094" ,"CSB"),
		DEUTSCHE_BANK("Deutsche Bank", "1026","DBK"),
		DEVELOPMENT_CREDIT_BANK("Development Credit Bank", "1040","DCB"),
		DENA_BANK("Dena Bank", "2004","DEN"),
		DHANLAXMI_BANK("Dhanlaxmi Bank", "1070","DLB"),
		FEDERAL_BANK("Federal Bank", "1027","FBK"),
		IDBI_BANK("IDBI Bank", "1003","IDB"),
		INDUSIND_BANK("Indusind Bank", "1054", "IDS"),
		INDIAN_BANK("Indian Bank", "1069", "INB"),
		INDIAN_OVERSEAS_BANK("Indian Overseas Bank", "1049", "IOB"), 
		JAMMU_AND_KASHMIR_BANK("Jammu And Kashmir Bank", "1041", "JKB"),
		KARNATAKA_BANK_LTD("Karnatka Bank Ltd", "1032", "KBL"), 
		KARUR_VYSYA_BANK("KarurVysya Bank", "1048", "KVB"),
		LAKSHMI_VILAS_BANK_NETBANKING("Lakshmi Vilas Bank NetBanking", "1095", "LVR"),
		ORIENTAL_BANK_OF_COMMERCE("Oriental Bank Of Commerce", "1042", "OBC"),
		PUNJAB_AND_MAHARASHTRA_CO_OPERATIVE_BANK_LIMITED("Punjab And Maharashtra Co operative Bank Limited", "2011", "PMC"),
		PUNJAB_NATIONAL_BANK_RETAIL_BANKING("Punjab National Bank Retail Banking", "2013", "PNB"),
		PUNJAB_AND_SIND_BANK("Punjab and Sind Bank", "1296", "PSB"),
		STANDARD_CHARTERED_BANK("Standard Chartered Bank", "1097", "SCB"),
		SOUTH_INDIAN_BANK("South Indian Bank", "1045", "SIB"),
		SHAMRAO_VITHAL_CO_OPERATIVE_BANK_LTD("Shamrao Vithal Co operative Bank Ltd", "2015", "SVC"),
		SARASWAT_BANK("SaraSwat Bank", "1056", "SWB"),
		SYNDICATE_BANK("Syndicate Bank", "1098", "SYD"),
		TAMILNAD_MERCANTILE_BANK("Tamilnad Mercantile Bank", "1065", "TMB"),
		TN_STATE_COOP_BANK("Tamil Nadu State Co-operative Bank", "1201", "TNC"),
		UNION_BANK_OF_INDIA("Union Bank Of India", "1038", "UBI"),
		UCO_BANK("UCO Bank", "1103", "UCO"),
		UNITED_BANK_OF_INDIA("United Bank Of India", "1046", "UNI"),
		VIJAYA_BANK("Vijaya Bank", "1044", "VJB"),
		YES_BANK("Yes Bank", "1001", "YBK"),
		JANATA_SAHKARI_BANK("Janata Sahakari Bank Pune", "1072", "JSB"),
		NKGSB_COOP_BANK("NKGSB Co op Bank", "1202", "NKB"),
		TJSB_BANK("TJSB Bank", "1203", "TJB"),
		KALYAN_JANTA_SAHAKARI_BANK("Kalyan Janata Sahakari Bank", "1204", "KJB"),
		MEHSANA_URBAN_COOP_BANK ("Mehsana urban Co-op Bank", "1205", "MSB"),
		BANDHAN_BANK ("Bandhan Bank", "1206", "BDN"),
		DIGIBANK_BY_DBS ("Digibank by DBS", "1207", "DBS"),
		IDFC_FIRST_BANK("IDFC FIRST Bank Limited", "1107", "IDN"),
		BASSIEN_CATH_COOP_BANK ("Bassien Catholic Coop Bank","1208", "BCB"),
		RBL_BANK("RBL Bank Limited", "1053", "RBL"),
		KALUPUR_COMM_COOP_BANK ("The Kalupur Commercial Co-Operative Bank","1209", "KLB"),
		EQUITAS_BANK("Equitas Bank", "1106", "EQB"),
		THANE_BHARAT_SAH_BANK ("Thane Bharat Sahakari Bank Ltd", "1210", "TBB"),
		SURYODAY_BANK ("Suryoday Small Finance Bank", "1211", "SRB"),
		ESAF_BANK ("ESAF Small Finance Bank", "1212", "ESF"),
		VARACHHA_BANK ("Varachha Co-operative Bank Limited", "1213", "VRB"),
		NE_SMALL_FIN_BANK ("North East Small Finance Bank Ltd", "1214", "NEB"),
		YESBANK_CB("YES BANK CB", "1022", "YBC"),
		CORPORATION_BANK_CORP ("Corporation Bank Corporate", "1215", "CR2"),
		RBL_BANK_CORP ("RBL Bank Limited Corporate Banking", "1216", "RTC"),
		SHAMRAO_VITHAL_CORPORATE ("Shamrao Vithal Co op Bank  Corporate", "1217", "SV2"),
		BARCLAYS_CORPORATE ("Barclays Corporate  Corporate Banking", "1218", "BRL"),
		ZOROASTRIAN_BANK ("Zoroastrian Co-op Bank", "1219", "ZOB"),
		AU_SMALL_FIN_BANK ("AU small finance bank", "1220", "AUB"),
		KARNATAKA_GRAMINA_BANK ("Karnataka Gramina Bank", "1221", "PKB"),
		STATE_BANK_OF_INDIA("State Bank Of India", "1030", "SBI"),
		HDFC_BANK("Hdfc Bank", "1004", "HDF"),
		ICICI_BANK("Icici Bank", "1013", "ICI"),
		FINCARE_BANK ("Fincare Bank", "1222", "FNC"),
		OXYZEN_WALLET("OxyzenWallet", "OXWL", "OXY"),
		YESBANK_WALLET("YesbankWallet", "YBWL", "YBW"),
		ICC_CASH_CARD("ICC Cash Card", "ICC", "ICC"),	
		PAY_WORLD_MONEY ("Pay World Money", "PCH", "PCH"),
		DCB_SIPPY ("DCB Cippy", "DCW", "DCW"),
		AXIS_BANK("Axis Bank", "1005", "UTI");
		

	private final String bankName;
	private final String code;
	private final String bankCode;
	
	private BilldeskMopType(String bankName, String code, String bankCode){
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

	public static BilldeskMopType getInstance(String name){
		BilldeskMopType[] mopTypes = BilldeskMopType.values();
		for(BilldeskMopType mopType : mopTypes){
			if(mopType.getBankName().equals(name)){
				return mopType;
			}
		}		
		return null;
	}

	
	public static List<BilldeskMopType> getGetMopsFromSystemProp(String mopsList){

		List<String> mopStringList= (List<String>) Helper.parseFields(PropertiesManager.propertiesMap.get(mopsList));

		List<BilldeskMopType> mops = new ArrayList<BilldeskMopType>();

		for(String mopCode:mopStringList){
			BilldeskMopType mop = getmop(mopCode);
			mops.add(mop);
		}
		return mops;
	}

	public static String getmopName(String mopCode){
		BilldeskMopType mopType = BilldeskMopType.getmop(mopCode);		
		if(mopType == null) {
			return "";
		}
		return mopType.getBankName();
	}
	
	
	public static String getBankCode(String code){
		BilldeskMopType mopType = BilldeskMopType.getmop(code);		
		if(mopType == null) {
			return "";
		}
		return mopType.getBankCode();
	}

	public static BilldeskMopType getmop(String mopCode){
		BilldeskMopType mopObj = null;
		if(null!=mopCode){
			for(BilldeskMopType mop:BilldeskMopType.values()){
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
			for (BilldeskMopType mop : BilldeskMopType.values()) {
				if (mopCode.equals(mop.getBankName().toString())) {
					moptType = mop.getCode();
					break;
				}
			}
		}
		return moptType;
	}
	
	public static BilldeskMopType getInstanceIgnoreCase(String name){
		BilldeskMopType[] mopTypes = BilldeskMopType.values();
		for(BilldeskMopType mopType : mopTypes){
			if(mopType.getBankName().equalsIgnoreCase(name)){
				return mopType;
			}
		}		
		return null;
	}
}
