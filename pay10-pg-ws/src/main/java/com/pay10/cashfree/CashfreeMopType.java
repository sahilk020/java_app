package com.pay10.cashfree;

import java.util.ArrayList;
import java.util.List;

import com.pay10.commons.util.Helper;
import com.pay10.commons.util.PropertiesManager;


public enum CashfreeMopType {

	
	//CASHFREE LIVE
		ALLAHABAD_BANK("Allahabad Bank", "1110","3001"),
		KOTAK_BANK ("Kotak Mahindra Bank", "1012","3032"),
		ANDHRA_BANK ("Andhra Bank", "1091","3002"),
		AXIS_BANK("Axis Bank", "1005", "3003"),
		BANK_OF_INDIA("Bank Of India", "1009", "3006"),
		CITY_UNION_BANK("City Union Bank", "1060", "3012"),
		BANK_OF_BRAODA_CORPORATE_ACCOUNTS ("Bank of Baroda - Corporate", "1092","3060"),
		BANK_OF_BARODA_RETAIL_ACCOUNTS ("Bank of Baroda Retail Accounts","1093","3005"),
		BANK_OF_MAHARASHTRA ("Bank Of Maharashtra", "1064","3007"),
		CENTRAL_BANK_OF_INDIA("Central Bank Of India", "1063","3011"),
		CANARA_BANK("Canara Bank", "1055", "3009"),
		//DBS_BANK_LTD ("DBS Bank Ltd", "3017", "3017"),
		//DBS_BANK_LTD_PERSONAL ("DCB Bank Personal", "3018", "3018"),
		CORPORATION_BANK("Corporation Bank", "1034","3013"),
		CATHOLIC_SYRIAN_BANK("Catholic Syrian Bank", "1094" ,"3010"),
		DEUTSCHE_BANK("Deutsche Bank", "1026","3016"),
		DENA_BANK("Dena Bank", "2004","3015"),
		DHANLAXMI_BANK("Dhanlakshmi Bank", "1070","3019"),
		FEDERAL_BANK("Federal Bank", "1027","3020"),
		IDBI_BANK("IDBI Bank", "1003","3023"),
		INDUSIND_BANK("Indusind Bank", "1054", "3028"),
		INDIAN_BANK("Indian Bank", "1069", "3026"),
		INDIAN_OVERSEAS_BANK("Indian Overseas Bank", "1049", "3027"), 
		JAMMU_AND_KASHMIR_BANK("Jammu And Kashmir Bank", "1041", "3029"),
		KARNATAKA_BANK_LTD("Karnataka Bank Ltd", "1032", "3030"),
		KARUR_VYSYA_BANK("Karur Vysya Bank", "1048", "3031"),
		LAKSHMI_VILAS_BANK_NETBANKING("Laxmi Vilas Bank - Retail Net Banking", "1095", "3033"),
		ORIENTAL_BANK_OF_COMMERCE("Oriental Bank Of Commerce", "1042", "3035"),
		PUNJAB_NATIONAL_BANK_RETAIL_BANKING("Punjab National Bank - Retail Net Banking", "2013", "3038"),
		PUNJAB_AND_SIND_BANK("Punjab & Sind Bank", "1296", "3037"),
		STANDARD_CHARTERED_BANK("Standard Chartered Bank", "1097", "3043"),
		SOUTH_INDIAN_BANK("South Indian Bank", "1045", "3042"),
		SHAMRAO_VITHAL_CO_OPERATIVE_BANK_LTD("Shamrao Vithal Co operative Bank Ltd", "2015", "3075"),
		SARASWAT_BANK("SaraSwat Bank", "1056", "3040"),
		SYNDICATE_BANK("Syndicate Bank", "1098", "3050"),
		TAMILNAD_MERCANTILE_BANK("Tamilnad Mercantile Bank Ltd", "1065", "3052"),
		TN_STATE_COOP_BANK("Tamil Nadu State Co-operative Bank", "1201", "3051"),
		UNION_BANK_OF_INDIA("Union Bank Of India", "1038", "3055"),
		UCO_BANK("UCO Bank", "1103", "3054"),
		UNITED_BANK_OF_INDIA("United Bank Of India", "1046", "3056"),
		VIJAYA_BANK("Vijaya Bank", "1044", "3057"),
		YES_BANK("Yes Bank Ltd", "1001", "3058"),
		BANDHAN_BANK ("Bandhan Bank", "1206", "3079"),
		IDFC_FIRST_BANK("IDFC FIRST Bank Limited", "1107", "3024"),
		RBL_BANK("RBL Bank Limited", "1053", "3039"),
		EQUITAS_BANK("Equitas Small Finance Bank", "1106", "3076"),
		STATE_BANK_OF_INDIA("State Bank Of India", "1030", "3044"),
		HDFC_BANK("Hdfc Bank", "1004", "3021"),
		ICICI_BANK("Icici Bank", "1013", "3022"),
		CASHFREE_DEMO_BANK("Cashfree Demo Bank", "3333", "3333"),

	//ADDED NEW BANK LIST- BY UMESH on 20Oct2021
	DBS_BANK("DBS Bank Ltd", "1300", "3017"),
	DCB_BANK_PERSONAL("DCB Bank - Personal", "1301", "3018"),
	BANK_OF_INDIA_CORPORATE("Bank of India - Corporate", "1302", "3061"),
	DCB_BANK_CORPORATE("DCB Bank - Corporate", "1292", "3062"),
	LAXMIVILAS_BANK_CORPORATE("Lakshmi Vilas Bank - Corporate", "1303", "3064"),
	PUNJAB_NATIONAL_BANK_CORPORATE("Punjab National Bank - Corporate", "1304", "3065"),
	STATE_BANK_OF_INDIA_CORPORATE("State Bank of India - Corporate", "1305", "3066"),
	UNION_BANK_OF_INDIA_CORPORATE("Union Bank of India - Corporate", "1306", "3067"),
	DHANLAXMI_BANK_CORPORATE("Dhanlaxmi Bank Corporate", "1307", "3072"),
	ICICI_CORPORATE_NETBANKING("ICICI Corporate Netbanking", "1308", "3073"),
	RATNAKAR_CORPORATE_BANKING("Ratnakar Corporate Banking", "1309", "3074"),
	SHIVALIK_BANK("Shivalik Bank", "1310", "3086"),
	AU_SMALL_FINANCE("AU Small Finance", "1311", "3087"),
	UTKARSH_SMALL_FINANCE_BANK("Utkarsh Small Finance bank", "1312", "3089"),
	THE_SURAT_PEOPLE_COOPERATIVE_BANK("The Surat People’s Co-operative Bank Limited", "1313", "3090"),
	GUJRAT_STATE_COOPERATIVE_BANK_LTD("Gujarat State Co-operative Bank Limited", "1314", "3091"),
	HSBC_RETAIL_NETBANKING("HSBC Retail Netbanking", "1315", "3092"),

		
	/*
	 * FREECHARGE_WALLET("FreeChargeWallet", "FCWL", "4001"),
	 * MOBIKWIK_WALLET("MobikwikWallet", "MWL", "4002"),
	 * OLAMONEY_WALLET("OlaMoneyWallet", "OLAWL", "4003"),
	 * JIO_MONEY_WALLET("JioMoneyWallet", "JMWL", "4004"),
	 * AIRTEL_PAY_WALLET("AirtelPayWallet", "AWL", "4006"),
	 * PAYTM_WALLET("PaytmWallet", "PPL", "4007"),
	 * AMAZON_PAY_WALLET("AmazonPayWallet", "APWL", "4008"),
	 * PHONE_PE_WALLET("PhonePeWallet", "PPWL", "4009");
	 */
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
	
	private CashfreeMopType(String bankName, String code, String bankCode){
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

	public static CashfreeMopType getInstance(String name){
		CashfreeMopType[] mopTypes = CashfreeMopType.values();
		for(CashfreeMopType mopType : mopTypes){
			if(mopType.getBankName().equals(name)){
				return mopType;
			}
		}		
		return null;
	}

	
	public static List<CashfreeMopType> getGetMopsFromSystemProp(String mopsList){

		List<String> mopStringList= (List<String>) Helper.parseFields(PropertiesManager.propertiesMap.get(mopsList));

		List<CashfreeMopType> mops = new ArrayList<CashfreeMopType>();

		for(String mopCode:mopStringList){
			CashfreeMopType mop = getmop(mopCode);
			mops.add(mop);
		}
		return mops;
	}

	public static String getmopName(String mopCode){
		CashfreeMopType mopType = CashfreeMopType.getmop(mopCode);		
		if(mopType == null) {
			return "";
		}
		return mopType.getBankName();
	}
	
	
	public static String getBankCode(String code){
		CashfreeMopType mopType = CashfreeMopType.getmop(code);		
		if(mopType == null) {
			return "";
		}
		return mopType.getBankCode();
	}

	public static CashfreeMopType getmop(String mopCode){
		CashfreeMopType mopObj = null;
		if(null!=mopCode){
			for(CashfreeMopType mop:CashfreeMopType.values()){
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
			for (CashfreeMopType mop : CashfreeMopType.values()) {
				if (mopCode.equals(mop.getBankName().toString())) {
					moptType = mop.getCode();
					break;
				}
			}
		}
		return moptType;
	}
	
	public static CashfreeMopType getInstanceIgnoreCase(String name){
		CashfreeMopType[] mopTypes = CashfreeMopType.values();
		for(CashfreeMopType mopType : mopTypes){
			if(mopType.getBankName().equalsIgnoreCase(name)){
				return mopType;
			}
		}		
		return null;
	}
}
