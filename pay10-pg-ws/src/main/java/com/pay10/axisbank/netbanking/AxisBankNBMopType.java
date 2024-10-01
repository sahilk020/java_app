package com.pay10.axisbank.netbanking;

import java.util.ArrayList;
import java.util.List;

import com.pay10.commons.util.Helper;
import com.pay10.commons.util.PropertiesManager;


public enum AxisBankNBMopType {


	//DIRECPAY UAT
	/*
	 * ANDHRA_BANK ("Andhra Bank", "1106"), ANDHRA_BANK_CORPORATE
	 * ("Andhra Bank Corporate", "1191"), AXIS_BANK ("Axis Bank", "1017"),
	 * BANK_OF_BAHRAIN_KUWAIT ("Bank of Bahrain and Kuwait", "1064"), BANK_OF_INDIA
	 * ("Bank of India", "1020"), BANK_OF_MAHARASHTRA ("Bank of Maharashtra",
	 * "1091"), CANARA_BANK ("Canara Bank", "1079"), CANARA_BANK_ATM_PIN
	 * ("Canara Bank ATM PIN", "1081"), CATHOLIC_SYRIAN_BANK
	 * ("Catholic Syrian Bank", "1102"), CENTRAL_BANK_OF_INDIA
	 * ("Central Bank of India", "1090"), CITIBANK ("Citibank", "1006"),
	 * CITY_UNION_BANK ("City Union Bank", "1084"), CORPORATION_BANK
	 * ("Corporation Bank", "1055"), DCB_BANK ("DCB Bank", "1059"),
	 * DCB_BANK_CORPORATE ("DCB Bank Corporate", "1111"), DEMO_BANK ("Demo Bank",
	 * "1025"), DEUTSCHE_BANK ("Deutsche Bank", "1046"), DHANLAXMI_BANK
	 * ("Dhanlaxmi Bank", "1105"), FEDERAL_BANK ("Federal Bank", "1049"), HDFC_BANK
	 * ("HDFC Bank", "1011"), ICICI_CORPORATE ("ICICI Corporate", "1082"),
	 * ICICI_BANK ("ICICI Bank", "1028"), INDIAN_BANK ("Indian Bank", "1101"),
	 * INDIAN_OVERSEAS_BANK ("Indian Overseas Bank", "1067"), INDUSIND_BANK
	 * ("IndusInd Bank", "1071"), IDBI_BANK ("IDBI Bank", "1004"), ING_VYSYA_BANK
	 * ("ING Vysya Bank", "1088"), JAMMU_AND_KASHMIR_BANK ("Jammu and Kashmir Bank",
	 * "1058"), KARNATAKA_BANK ("Karnataka Bank Ltd", "1053"), KARUR_VYSYA_BANK
	 * ("Karur Vysya Bank", "1066"), KOTAK_BANK ("Kotak Bank", "1024"),
	 * ORIENTAL_BANK_OF_COMMERCE ("Oriental Bank of Commerce", "1062"),
	 * PUNJAB_AND_SIND_BANK ("Punjab and Sind Bank", "1181"), PUNJAB_NATIONAL_BANK
	 * ("Punjab National Bank", "1016"), RBL_BANK ("RBL Bank Limited", "1075"),
	 * SOUTH_INDIAN_BANK ("South Indian Bank", "1039"), SBBJ_BANK
	 * ("State Bank of Bikaner and Jaipur", "1069"), STATE_BANK_OF_HYDERABAD
	 * ("State Bank of Hyderabad", "1060"), STATE_BANK_OF_INDIA
	 * ("State Bank of India", "1051"), STATE_BANK_OF_MYSORE
	 * ("State Bank of Mysore", "1056"), STATE_BANK_OF_PATIALA
	 * ("State Bank of Patiala", "1097"), STATE_BANK_OF_TRAVANCORE
	 * ("State Bank of Travancore", "1089"), TAMILNAD_MERCANTILE_BANK
	 * ("Tamilnad Mercantile Bank", "1092"), UCO_BANK ("UCO Bank", "1171"),
	 * UNION_BANK_OF_INDIA ("Union Bank of India", "1057"), UNITED_BANK_OF_INDIA
	 * ("United Bank of India", "1068"), VIJAYA_BANK ("Vijaya bank", "1061"),
	 * YES_BANK ("YES Bank", "1009");
	 */
	
	//DIRECPAY UAT
	DEMO_BANK ("Demo Bank","1025" ,"1025"),
	
	//DIRECPAY LIVE
		ABN_AMRO_BANK ("ABN Amro Bank", "1029","1029"),
		ANDHRA_BANK ("Andhra Bank", "1091","1293"),
		AXIS_BANK ("Axis Bank", "1005","1005"),
		BANK_OF_BAHRAIN_AND_KUWAIT ("Bank Of Bahrain And Kuwait", "1043","1043"),
		BANK_OF_INDIA ("Bank Of India","1009", "1009"),
		BANK_OF_MAHARASHTRA ("Bank Of Maharashtra","1064","1064"),
		BANK_OF_PUNJAB ("Bank of Punjab","1007","1007"),
		CANARA_BANK ("Canara Bank","1055","1055"),
		CATHOLIC_SYRIAN_BANK("Catholic Syrian Bank","1094", "1072"),
		CENTRAL_BANK_OF_INDIA ("Central Bank of India", "1063","1063"),
		CENTURIAN_BANK_OF_PUNJAB ("Centurion Bank of Punjab", "1008","1006"),
		CITIBANK ("CitiBank", "1010","1010"),
		CITY_UNION_BANK ("City Union Bank","1060" ,"1060"),
		CORPORATION_BANK ("Coporation Bank","1034","1034"),
		DCB_BANK ("DCB Bank", "1041","1040"),
		DCB_BANK_CORPORATE ("DCB Bank Corporate","1292","1292"),
		DEUTSCHE_BANK ("Deutsche Bank", "1026","1026"),
		DHANLAXMI_BANK ("Dhanlaxmi Bank", "1070","1070"),
		FEDERAL_BANK ("Federal Bank", "1027","1027"),
		HDFC_BANK ("Hdfc Bank", "1004","1004"),
		ICICI_CORPORATE_BANK ("ICICI Corporate Bank", "1100","1057"),
		ICICI_BANK ("Icici Bank", "1013","1013"),
		INDIAN_BANK ("Indian Bank", "1069","1069"),
		INDIAN_OVERSEAS_BANK ("Indian Overseas Bank","1049", "1049"),
		INDUSIND_BANK ("Indusind Bank","1054", "1054"),
		IDBI_BANK ("IDBI Bank", "1003","1003"),
		ING_VYSYA_BANK ("IngVysya Bank", "1062","1062"),
		JAMMU_AND_KASHMIR_BANK ("Jammu And Kashmir Bank","1041","1041"),
		KARNATAKA_BANK_LTD ("Karnatka Bank Ltd","1032", "1032"),
		KARUR_VYSYA_BANK ("KarurVysya Bank", "1048","1048"),
		KOTAK_BANK ("Kotak Bank", "1012","1012"),
		ORIENTAL_BANK_OF_COMMERCE ("Oriental Bank Of Commerce", "1042","1042"),
		PUNJAB_AND_SIND_BANK ("Punjab and Sind Bank","1296","1296"),
		PUNJAB_NATIONAL_BANK ("Punjab National Bank", "1002","1002"),
		RBL_BANK ("RBL Bank Limited","1053", "1053"),
		SOUTH_INDIAN_BANK ("South Indian Bank", "1045","1045"),
		STATE_BANK_OF_BIKANER_AND_JAIPUR ("State Bank Of Bikaner And Jaipur","1050", "1050"),
		STATE_BANK_OF_HYDERABAD("State Bank Of Hyderabad","1039", "1039"),
		STATE_BANK_OF_INDIA ("State Bank Of India","1030", "1030"),
		STATE_BANK_OF_MYSORE ("State Bank Of Mysore", "1037","1037"),
		STATE_BANK_OF_PATIALA ("State Bank Of Patiala", "1068","1068"),
		STATE_BANK_OF_TRAVANCORE ("State Bank Of Travancore", "1061","1061"),
		TAMILNAD_MERCANTILE_BANK ("Tamilnad Mercantile Bank", "1065","1065"),
		TP_BANK_OF_RAJASTHAN ("TP Bank of Rajasthan", "1021","1021"),
		TP_CORPORATION_BANK ("TP Corporation Bank","1018", "1018"),
		TP_KARNATAKA_BANK ("TP Karnataka Bank", "1017","1017"),
		TP_ORIENTAL_BANK_OF_COMMERCE ("TP Oriental Bank of Commerce","1019" ,"1019"),
		TP_SOUTH_INDIAN_BANK ("TP South Indian Bank","1020", "1020"),
		TP_THE_FEDERAL_BANK ("TP The Federal Bank","1031", "1031"),
		TP_VIJAYA_BANK ("TP Vijaya Bank","1023" ,"1022"),
		UCO_BANK ("UCO Bank", "1103","1294"),
		UNION_BANK_OF_INDIA ("Union Bank Of India","1038","1038"),
		UNITED_BANK_OF_INDIA ("United Bank Of India", "1046","1046"),
		VIJAYA_BANK ("Vijaya Bank","1044", "1044"),
		YES_BANK ("Yes Bank","1001", "1001");
		

	private final String bankName;
	private final String code;
	private final String bankCode;
	
	private AxisBankNBMopType(String bankName, String code, String bankCode){
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

	public static AxisBankNBMopType getInstance(String name){
		AxisBankNBMopType[] mopTypes = AxisBankNBMopType.values();
		for(AxisBankNBMopType mopType : mopTypes){
			if(mopType.getBankName().equals(name)){
				return mopType;
			}
		}		
		return null;
	}

	
	public static List<AxisBankNBMopType> getGetMopsFromSystemProp(String mopsList){

		List<String> mopStringList= (List<String>) Helper.parseFields(PropertiesManager.propertiesMap.get(mopsList));

		List<AxisBankNBMopType> mops = new ArrayList<AxisBankNBMopType>();

		for(String mopCode:mopStringList){
			AxisBankNBMopType mop = getmop(mopCode);
			mops.add(mop);
		}
		return mops;
	}

	public static String getmopName(String mopCode){
		AxisBankNBMopType mopType = AxisBankNBMopType.getmop(mopCode);		
		if(mopType == null) {
			return "";
		}
		return mopType.getBankName();
	}
	
	
	public static String getBankCode(String code){
		AxisBankNBMopType mopType = AxisBankNBMopType.getmop(code);		
		if(mopType == null) {
			return "";
		}
		return mopType.getBankCode();
	}

	public static AxisBankNBMopType getmop(String mopCode){
		AxisBankNBMopType mopObj = null;
		if(null!=mopCode){
			for(AxisBankNBMopType mop:AxisBankNBMopType.values()){
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
			for (AxisBankNBMopType mop : AxisBankNBMopType.values()) {
				if (mopCode.equals(mop.getBankName().toString())) {
					moptType = mop.getCode();
					break;
				}
			}
		}
		return moptType;
	}
	
	public static AxisBankNBMopType getInstanceIgnoreCase(String name){
		AxisBankNBMopType[] mopTypes = AxisBankNBMopType.values();
		for(AxisBankNBMopType mopType : mopTypes){
			if(mopType.getBankName().equalsIgnoreCase(name)){
				return mopType;
			}
		}		
		return null;
	}
}
