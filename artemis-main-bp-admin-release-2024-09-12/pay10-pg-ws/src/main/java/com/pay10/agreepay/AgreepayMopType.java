package com.pay10.agreepay;

import java.util.ArrayList;
import java.util.List;

import com.pay10.agreepay.AgreepayMopType;
import com.pay10.commons.util.Helper;
import com.pay10.commons.util.PropertiesManager;

public enum AgreepayMopType {

	
		// Cards CC
		VISA("Visa", "VI", "VIS"), 
		AMEX("Amex", "AX", "AMX"), 
		MASTERCARD("MasterCard", "MC", "MAS"), 
		DINERS("Diners", "DN", "DIN"),
		RUPAY("Rupay", "RU", "RUP"), 
		
				
		
	
	//KOTAK_BANK ("Kotak Bank", "1012","3032"),
	
	//AGREEPAY LIVE netbanking
	ALLAHABAD_BANK("Allahabad Bank", "1110","ALLN"),
	ANDHRA_BANK ("Andhra Bank", "1091","ADBN"),
	AXIS_BANK("Axis Bank", "1005", "AXIN"),
	BANK_OF_INDIA("Bank Of India", "1009", "BOIN"),
	CITY_UNION_BANK("City Union Bank", "1060", "CUBN"),
	BANK_OF_BRAODA_CORPORATE_ACCOUNTS ("Bank of Baroda Corporate Accounts", "1092","BBRM"),
	BANK_OF_BARODA_RETAIL_ACCOUNTS ("Bank of Baroda Retail Accounts","1093","BBRN"),
	BANK_OF_MAHARASHTRA ("Bank Of Maharashtra", "1064","BOMN"),
	CENTRAL_BANK_OF_INDIA("Central Bank Of India", "1063","CBIN"),
	CANARA_BANK("Canara Bank", "1055", "CANN"),
	DBS_BANK_LTD ("DBS Bank Ltd", "3017", "DBSN"),
	DBS_BANK_LTD_PERSONAL ("DCB Bank Personal", "3018", "DCBM"),
	CORPORATION_BANK("Corporation Bank", "1034","CRPN"),
	CATHOLIC_SYRIAN_BANK("Catholic Syrian Bank", "1094" ,"CSBN"),
	DEUTSCHE_BANK("Deutsche Bank", "1026","DSHN"),
	DENA_BANK("Dena Bank", "2004","DENN"),
	DHANLAXMI_BANK("Dhanlaxmi Bank", "1070","DHNN"),
	FEDERAL_BANK("Federal Bank", "1027","FEDN"),
	IDBI_BANK("IDBI Bank", "1003","IDBN"),
	INDUSIND_BANK("Indusind Bank", "1054", "INDN"),
	INDIAN_BANK("Indian Bank", "1069", "ININ"),
	INDIAN_OVERSEAS_BANK("Indian Overseas Bank", "1049", "IOBN"), 
	JAMMU_AND_KASHMIR_BANK("Jammu And Kashmir Bank", "1041", "JAKN"),
	KARNATAKA_BANK_LTD("Karnatka Bank Ltd", "1032", "KRKN"), 
	KARUR_VYSYA_BANK("KarurVysya Bank", "1048", "KRVN"),
	LAKSHMI_VILAS_BANK_NETBANKING("Lakshmi Vilas Bank NetBanking", "1095", "LVBN"),
	ORIENTAL_BANK_OF_COMMERCE("Oriental Bank Of Commerce", "1042", "OBCN"),
	PUNJAB_NATIONAL_BANK_RETAIL_BANKING("Punjab National Bank Retail Banking", "2013", "PNBN"),
	PUNJAB_AND_SIND_BANK("Punjab and Sind Bank", "1296", "PSBN"),
	STANDARD_CHARTERED_BANK("Standard Chartered Bank", "1097", "SCBN"),
	SOUTH_INDIAN_BANK("South Indian Bank", "1045", "SOIN"),
	SHAMRAO_VITHAL_CO_OPERATIVE_BANK_LTD("Shamrao Vithal Co operative Bank Ltd", "2015", "SVCN"),
	SARASWAT_BANK("SaraSwat Bank", "1056", "SRSN"),
	SYNDICATE_BANK("Syndicate Bank", "1098", "SYDN"),
	TAMILNAD_MERCANTILE_BANK("Tamilnad Mercantile Bank", "1065", "TMBN"),
	TN_STATE_COOP_BANK("Tamil Nadu State Co-operative Bank", "1201", "TSCN"),
	UNION_BANK_OF_INDIA("Union Bank Of India", "1038", "UBIN"),
	UCO_BANK("UCO Bank", "1103", "UCON"),
	UNITED_BANK_OF_INDIA("United Bank Of India", "1046", "UNIN"),
	VIJAYA_BANK("Vijaya Bank", "1044", "VIJN"),
	YES_BANK("Yes Bank", "1001", "YESN"),
	BANDHAN_BANK ("Bandhan Bank", "1206", "BDNN"),
	IDFC_FIRST_BANK("IDFC FIRST Bank Limited", "1107", "IDFN"),
	RBL_BANK("RBL Bank Limited", "1053", "RTNN"),
	EQUITAS_BANK("Equitas Bank", "1106", "ESFN"),
	STATE_BANK_OF_INDIA("State Bank Of India", "1030", "SBIN"),
	HDFC_BANK("Hdfc Bank", "1004", "HDFN"),
	ICICI_BANK("Icici Bank", "1013", "ICIN"),
	AGREEPAY_DEMO_BANK("Agreepay Demo Bank", "3334", "DEMN"),
	
	FREECHARGE_WALLET("FreeChargeWallet", "FCWL", "FRCW"),
	MOBIKWIK_WALLET("MobikwikWallet", "MWL", "MBKW"),
	OLAMONEY_WALLET("OlaMoneyWallet", "OLAWL", "OLAW"),
	JIO_MONEY_WALLET("JioMoneyWallet", "JMWL", "JIOW"),
	AIRTEL_PAY_WALLET("AirtelPayWallet", "AWL", "ATLW"),
	PAYTM_WALLET("PaytmWallet", "PPL", "PTMW"),
	AMAZON_PAY_WALLET("AmazonPayWallet", "APWL", "AMPW"),
	PHONE_PE_WALLET("PhonePeWallet", "PPWL", "PHPW"),
	EZEECLICK("EzeeClick", "EZ", "EZCW"),
	UPI("UPI", "UP", "UPIU");


private final String bankName;
private final String code;
private final String bankCode;

private AgreepayMopType(String bankName, String code, String bankCode){
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

public static AgreepayMopType getInstance(String name){
	AgreepayMopType[] mopTypes = AgreepayMopType.values();
	for(AgreepayMopType mopType : mopTypes){
		if(mopType.getBankName().equals(name)){
			return mopType;
		}
	}		
	return null;
}


public static List<AgreepayMopType> getGetMopsFromSystemProp(String mopsList){

	List<String> mopStringList= (List<String>) Helper.parseFields(PropertiesManager.propertiesMap.get(mopsList));

	List<AgreepayMopType> mops = new ArrayList<AgreepayMopType>();

	for(String mopCode:mopStringList){
		AgreepayMopType mop = getmop(mopCode);
		mops.add(mop);
	}
	return mops;
}

public static String getmopName(String mopCode){
	AgreepayMopType mopType = AgreepayMopType.getmop(mopCode);		
	if(mopType == null) {
		return "";
	}
	return mopType.getBankName();
}


public static String getBankCode(String code){
	AgreepayMopType mopType = AgreepayMopType.getmop(code);		
	if(mopType == null) {
		return "";
	}
	return mopType.getBankCode();
}

public static AgreepayMopType getmop(String mopCode){
	AgreepayMopType mopObj = null;
	if(null!=mopCode){
		for(AgreepayMopType mop:AgreepayMopType.values()){
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
		for (AgreepayMopType mop : AgreepayMopType.values()) {
			if (mopCode.equals(mop.getBankName().toString())) {
				moptType = mop.getCode();
				break;
			}
		}
	}
	return moptType;
}

public static AgreepayMopType getInstanceIgnoreCase(String name){
	AgreepayMopType[] mopTypes = AgreepayMopType.values();
	for(AgreepayMopType mopType : mopTypes){
		if(mopType.getBankName().equalsIgnoreCase(name)){
			return mopType;
		}
	}		
	return null;
}
}
