package com.pay10.pinelabs;

import java.util.ArrayList;
import java.util.List;

import com.pay10.commons.util.Helper;
import com.pay10.commons.util.PropertiesManager;

public enum PinelabsMopType {

	// Cards CC
	VISA("Visa", "VI", "VIS"), AMEX("Amex", "AX", "AMX"), MASTERCARD("MasterCard", "MC", "MAS"),
	DINERS("Diners", "DN", "DIN"), RUPAY("Rupay", "RU", "RUP"),

	AXIS_BANK("Axis Bank", "1005", "NB1004"), HDFC_BANK("Hdfc Bank", "1004", "NB1007"),
	JAMMU_AND_KASHMIR_BANK("Jammu And Kashmir Bank", "1041", "NB1015"), ICICI_BANK("Icici Bank", "1013", "NB1016"),
	FEDERAL_BANK("Federal Bank", "1027", "NB1029"), 
	KARNATAKA_BANK_LTD("Karnatka Bank Ltd", "1032", "NB1133"), CORPORATION_BANK("Corporation Bank", "1034", "NB1135"),
	INDIAN_BANK("Indian Bank", "1069", "NB1143"), YES_BANK("Yes Bank", "1001", "NB1146"),
	CENTRAL_BANK_OF_INDIA("Central Bank Of India", "1063", "NB1147"),
	KOTAK_BANK("Kotak Mahindra Bank", "1012", "NB1148"),
	ORIENTAL_BANK_OF_COMMERCE("Oriental Bank Of Commerce", "1042", "NB1154"),
	UNITED_BANK_OF_INDIA("United Bank Of India", "1046", "NB1212"),
	INDIAN_OVERSEAS_BANK("Indian Overseas Bank", "1049", "NB1213"),
	CITY_UNION_BANK("City Union Bank", "1060", "NB1215"), 
	UNION_BANK_OF_INDIA("Union Bank Of India", "1038", "NB1216"),
	CANARA_BANK("Canara Bank", "1055", "NB1224"), BANK_OF_MAHARASHTRA("Bank Of Maharashtra", "1064", "NB1229"),
	CATHOLIC_SYRIAN_BANK("Catholic Syrian Bank", "1094", "NB1272"), DHANLAXMI_BANK("Dhanlaxmi Bank", "1070", "NB1373"),
	ANDHRA_BANK("Andhra Bank", "1091", "NB1378"), VIJAYA_BANK("Vijaya Bank", "1044", "NB1379"),
	SARASWAT_BANK("SaraSwat Bank", "1056", "NB1380"),
	PUNJAB_NATIONAL_BANK("Punjab National Bank", "1002", "NB1381"),
	PUNJAB_AND_SIND_BANK("Punjab & Sind Bank", "1296", "NB1421"),
	INDUSIND_BANK("Indusind Bank", "1054", "NB1431"),
	TAMILNAD_MERCANTILE_BANK("Tamilnad Mercantile Bank", "1065", "NB1439"),UCO_BANK("UCO Bank", "1103", "NB1483"),
	
	 
	
	ANDHRA_BANK_CORPORATE("Andhra Bank Corporate", "1200", "NB1484"),
	BANK_OF_BRAODA_CORPORATE_ACCOUNTS("Bank of Baroda - Corporate", "1092", "NB1485"),
	BANK_OF_BAHRAIN_AND_KUWAIT("Bank Of Bahrain And Kuwait", "1043", "NB1486"),
	BANK_OF_BARODA_RETAIL_ACCOUNTS("Bank of Baroda Retail Accounts", "1093", "NB1487"),
	COSMOS_BANK("COSMOS Bank", "1104", "NB1488"),
	PUNJAB_NATIONAL_BANK_CORPORATE_ACCOUNTS("Punjab National Bank Corporate", "1096", "NB1489"),
	DEUTSCHE_BANK("Deutsche Bank", "1026", "NB1490"),
	DENA_BANK("Dena Bank", "2004", "NB1492"),
	IDBI_BANK("IDBI Bank", "1003", "NB1493"),
	KARUR_VYSYA_BANK("Karur Vysya Bank", "1048", "NB1494"),
	LAXMIVILAS_BANK_CORPORATE("Lakshmi Vilas Bank - Corporate", "1303", "NB1495"),
	LAKSHMI_VILAS_BANK_NETBANKING("Laxmi Vilas Bank - Retail Net Banking", "1095", "NB1496"),
	PUNJAB_AND_MAHARASHTRA_CO_OPERATIVE_BANK_LIMITED("Punjab And Maharashtra Co-operative Bank ", "2011", "NB1497"),
	STANDARD_CHARTERED_BANK("Standard Chartered Bank", "1097", "NB1498"),
	SOUTH_INDIAN_BANK("South Indian Bank", "1045", "NB1499"),
	SHAMRAO_VITHAL_CO_OPERATIVE_BANK_LTD("Shamrao Vithal Co-operative Bank ", "2015", "NB1500"),
	SYNDICATE_BANK("Syndicate Bank", "1098", "NB1501"),
	TN_STATE_COOP_BANK("Tamil Nadu State Co-operative Bank", "1201", "NB1502"),
	JANATA_SAHKARI_BANK("Janata Sahakari Bank Pune", "1072", "NB1503"),
	NKGSB_COOP_BANK("NKGSB Co op Bank", "1202", "NB1504"),
	TJSB_BANK("TJSB Bank", "1203", "NB1505"),
	KALYAN_JANTA_SAHAKARI_BANK("Kalyan Janata Sahakari Bank", "1204", "NB1506"),
	MEHSANA_URBAN_COOP_BANK ("Mehsana urban Co-op Bank", "1205", "NB1507"),
	BANDHAN_BANK ("Bandhan Bank", "1206", "NB1508"),
	DIGIBANK_BY_DBS ("Digibank by DBS", "1207", "NB1509"),
	IDFC_FIRST_BANK("IDFC FIRST Bank", "1107", "NB1510"),
	BASSIEN_CATH_COOP_BANK ("Bassien Catholic Coop Bank","1208", "NB1511"),
	RBL_BANK("RBL Bank Limited", "1053", "NB1513"),
	KALUPUR_COMM_COOP_BANK ("The Kalupur Commercial Co-Operative Bank","1209", "NB1514"),
	EQUITAS_BANK("Equitas Small Finance Bank", "1106", "NB1515"),
	THANE_BHARAT_SAH_BANK ("Thane Bharat Sahakari Bank Ltd", "1210", "NB1516"),
	SURYODAY_BANK ("Suryoday Small Finance Bank", "1211", "NB1517"),
	ESAF_BANK ("ESAF Small Finance Bank", "1212", "NB1518"),
	VARACHHA_BANK ("Varachha Co-operative Bank Limited", "1213", "NB1519"),
	NE_SMALL_FIN_BANK ("North East Small Finance Bank ", "1214", "NB1520"),
	YESBANK_CB("YES BANK CB", "1022", "NB1522"),
	CORPORATION_BANK_CORP ("Corporation Bank Corporate", "1215", "NB1523"),
	RBL_BANK_CORP("RBL Bank Limited Corporate ", "1216", "NB1524"),
	SHAMRAO_VITHAL_CORPORATE("Shamrao Vithal Co op Bank - Corporate", "1217", "NB1525"),
	DHANLAXMI_BANK_CORPORATE("Dhanlaxmi Bank Corporate", "1307", "NB1526"),
	BARCLAYS_CORPORATE ("Barclays Corporate  Corporate Banking", "1218", "NB1527"),
	ZOROASTRIAN_BANK ("Zoroastrian Co-op Bank", "1219", "NB1528"),
	AU_SMALL_FIN_BANK ("AU small finance bank", "1220", "NB1529"),
	ALLAHABAD_BANK("Allahabad Bank", "1110", "NB1530"),
	STATE_BANK_OF_INDIA("State Bank Of India", "1030", "NB1531"),
	FINCARE_BANK ("Fincare Bank", "1222", "NB1532"),
	KARNATAKA_GRAMINA_BANK ("Karnataka Gramina Bank", "1221", "NB1534"),
	
	//Bandhan Bank -NB1533  - Corporate --- not in mop type
	//IDBI Corporate   NB1521 - not in mop type
	//PNB Yuva Netbanking  NB1512--not in mop type
	//DEVELOPMENT_CREDIT_BANK("Development Credit Bank", "1040", true), NB1491  -- not exist in mop type
	OXYZEN_WALLET("OxyzenWallet", "OXWL", "OXY"), PAYTM_WALLET("PaytmWallet", "PPL", "PAYTM"),
	HDFC_PAYZAPP_WALLET("HdfcPayZappWallet", "HPWL", "PAYZAPP"), PHONE_PE_WALLET("PhonePeWallet", "PPWL", "PhonePe"),

	UPI("UPI", "UP", "10"),
	//payment type
	
	CREDIT_CARD("Credit Card", "CC", "1"), 
	DEBIT_CARD("Debit Card", "DC", "1"), 
	NET_BANKING("Net Banking", "NB","3"), 
	EMI("EMI", "EM", "4"), 
	WALLET("Wallet", "WL","11"), 
	DEBIT_CARD_WITH_PIN("Debit Card With Pin","DP","13");
	
	

	private final String bankName;
	private final String code;
	private final String bankCode;

	private PinelabsMopType(String bankName, String code, String bankCode) {
		this.bankName = bankName;
		this.code = code;
		this.bankCode = bankCode;
	}

	public String getBankName() {
		return bankName;
	}

	public String getCode() {
		return code;
	}

	public String getBankCode() {
		return bankCode;
	}

	public static PinelabsMopType getInstance(String name) {
		PinelabsMopType[] mopTypes = PinelabsMopType.values();
		for (PinelabsMopType mopType : mopTypes) {
			if (mopType.getBankName().equals(name)) {
				return mopType;
			}
		}
		return null;
	}

	public static List<PinelabsMopType> getGetMopsFromSystemProp(String mopsList) {

		List<String> mopStringList = (List<String>) Helper.parseFields(PropertiesManager.propertiesMap.get(mopsList));

		List<PinelabsMopType> mops = new ArrayList<PinelabsMopType>();

		for (String mopCode : mopStringList) {
			PinelabsMopType mop = getmop(mopCode);
			mops.add(mop);
		}
		return mops;
	}

	public static String getmopName(String mopCode) {
		PinelabsMopType mopType = PinelabsMopType.getmop(mopCode);
		if (mopType == null) {
			return "";
		}
		return mopType.getBankName();
	}

	public static String getBankCode(String code) {
		PinelabsMopType mopType = PinelabsMopType.getmop(code);
		if (mopType == null) {
			return "";
		}
		return mopType.getBankCode();
	}

	public static PinelabsMopType getmop(String mopCode) {
		PinelabsMopType mopObj = null;
		if (null != mopCode) {
			for (PinelabsMopType mop : PinelabsMopType.values()) {
				if (mopCode.equals(mop.getCode().toString())) {
					mopObj = mop;
					break;
				}
			}
		}
		return mopObj;
	}

	public static String getMopTypeName(String mopCode) {
		String moptType = null;
		if (null != mopCode) {
			for (PinelabsMopType mop : PinelabsMopType.values()) {
				if (mopCode.equals(mop.getBankName().toString())) {
					moptType = mop.getCode();
					break;
				}
			}
		}
		return moptType;
	}

	public static PinelabsMopType getInstanceIgnoreCase(String name) {
		PinelabsMopType[] mopTypes = PinelabsMopType.values();
		for (PinelabsMopType mopType : mopTypes) {
			if (mopType.getBankName().equalsIgnoreCase(name)) {
				return mopType;
			}
		}
		return null;
	}
}
