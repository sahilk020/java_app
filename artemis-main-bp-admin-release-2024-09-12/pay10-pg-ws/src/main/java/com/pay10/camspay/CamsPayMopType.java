package com.pay10.camspay;

import java.util.ArrayList;
import java.util.List;

import com.pay10.commons.util.Helper;
import com.pay10.commons.util.PropertiesManager;

public enum CamsPayMopType {

	INDIAN_OVERSEAS_BANK("Indian Overseas Bank", "1049", "IOBA"),
	TAMILNAD_MERCANTILE_BANK_LTD("Tamilnad Mercantile Bank Ltd", "1065", "TMBL"),
	INDIAN_BANK("Indian Bank", "1069", "IDIB"), CENTRAL_BANK_OF_INDIA("Central Bank of India", "1063", "CBIN"),
	CANARA_BANK("Canara Bank", "1055", "CNRB"), INDUSIND_BANK("Indusind Bank", "1054", "INDB"),
	ALLAHABAD_BANK("Allahabad Bank", "1110", "ALLA"), BANK_OF_INDIA("Bank Of India", "1009", "BKID"),
	CORPORATION_BANK("Corporation Bank", "1034", "CORP"), VIJAYA_BANK("Vijaya Bank", "1044", "VIJB"),
	SYNDICATE_BANK("Syndicate bank", "1098", "SYNB"), PUNJAB_NATIONAL_BANK("Punjab National Bank", "1002", "PUNB"),
	BANK_OF_MAHARASHTRA("Bank of Maharashtra", "1064", "MAHB"), UCO_BANK("UCO Bank", "1103", "UCBA"),

	THE_MEHSANA_URBAN_CO_OPERATIVE_BANK("The Mehsana Urban Co-operative Bank", "1205", "MSNU"),
	FEDERAL_BANK("Federal Bank", "1027", "FDRL"),
	ORIENTAL_BANK_OF_COMMERCE("Oriental Bank of Commerce", "1042", "ORBC"), ANDHRA_BANK("Andhra Bank", "1091", "ANDB"),
	DENA_BANK("Dena Bank", "2004", "BKDN"), KARUR_VYSA_BANK("Karur Vysa Bank", "1048", "KVBL"),
	THE_VARACHHA_CO_OP_BANK_LTD("THE VARACHHA CO-OP BANK LTD", "1213", "VARA"),
	KAVERI_GRAMEENA_BANK("Kaveri Grameena Bank", "", "KGBX"),
	CITY_UNION_BANK_LIMITED("City Union Bank Limited", "1060", "CIUB"), DHANALAXMI_BANK("Dhanalaxmi Bank", "", "DLXB"),
	THE_LAKSHMI_VILAS_BANK_LTD("The Lakshmi Vilas Bank Ltd.", "1303", "LAVB"),
	KARNATAKA_VIKAS_GRAMEENA_BANK("Karnataka Vikas Grameena Bank", "1221", "KVGB"),
	THE_MUGBERIA_CENTRAL_CO_OPERATIVE_BANK_LTD("The Mugberia Central Co-operative Bank Ltd", "", "MBCX"),
	THE_SURAT_PEOPLES_CO_OP_BANK_LTD("The Surat Peoples Co-op. Bank Ltd.", "1313", "SPCB"),
	THE_AKOLA_DISTRICT_CENTRAL_COOPERATIVE_BANK_LTD("The Akola District Central Cooperative Bank Ltd", "", "ADCC"),
	PRAGATHI_GRAMIN_BANKPGBX35SHREYAS_GRAMIN_BANK("Pragathi Gramin BankPGBX35Shreyas Gramin Bank", "", "SGBX"),
	THE_SURAT_DISTRICT_CO_OP_BANK("The Surat District Co-Op Bank", "", "SDCB"),
	PANDYAN_GRAMA_BANK("Pandyan Grama Bank", "", "PDNX"), SARASWAT_BANK("Saraswat Bank", "", "SRCB"),
	SOUTH_INDIAN_BANK("South Indian Bank", "1045", "SIBL"),
	THE_KALUPUR_COMMERCIAL_CO_OPERATIVE_BANK("The Kalupur Commercial Co-Operative Bank", "1209", "KCCB"),
	SUCO_SOUHARDA_SAHAKARI_BANK("SUCO SOUHARDA SAHAKARI BANK", "", "SSDX"), IDBI_BANK("IDBI Bank", "1003", "IBKL"),
	UNITED_BANK_OF_INDIA("United Bank of India", "1046", "UTBI"),
	UNION_BANK_OF_INDIA("Union Bank of India", "1038", "UBIN"),
	MALDA_DISTRICT_CENTRAL_COOPERATIVE_BANK_LTD("MALDA DISTRICT CENTRAL COOPERATIVE BANK LTD", "", "MLDX"),
	ODISHA_GRAMYA_BANK("Odisha Gramya Bank", "", "ODGB"),
	BANGIYA_GRAMIN_VIKASH_BANK("Bangiya Gramin Vikash Bank", "", "BGVX"),
	MANIPUR_RURAL_BANK("MANIPUR RURAL BANK", "", "MRBX"),
	ASSAM_GRAMIN_VIKASH_BANK("Assam Gramin VIkash Bank", "", "AGVX"),
	TRIPURA_GRAMIN_BANK("Tripura Gramin Bank", "", "TGBX"),
	TJSB_SAHAKARI_BANK_LTD("TJSB Sahakari Bank Ltd", "", "TJSB"),
	THE_JAMMU_AND_KASHMIR_BANK_LTD("The Jammu And Kashmir Bank Ltd", "1041", "JAKA"),
	STATE_BANK_OF_HYDERABAD("State Bank of Hyderabad", "1039", "SBHY"),
	TAMLUK_GHATAL_CENTRAL_CO_OPERATIVE_BANK_LTD("Tamluk-Ghatal Central Co-operative Bank Ltd", "", "TGCX"),
	STATE_BANK_OF_TRAVANCORE("State bank of Travancore", "1061", "SBTR"),
	THE_GUJARAT_STATE_CO_OP_BANK_LTD("The Gujarat State Co-op Bank Ltd", "1314", "GSCB"),
	THE_WASHIM_URBAN_CO_OPERATIVE_BANK_LTD("The Washim Urban Co-operative Bank Ltd", "", "WUCX"),
	NUTAN_NAGARIK_SAHAKARI_BANK_LTD("Nutan Nagarik Sahakari Bank Ltd", "", "NNSB"),
	THE_AHMEDABAD_MERCANTILE_CO_OP_BANK_LTD("THE AHMEDABAD MERCANTILE CO-OP BANK LTD", "", "AMCB"),
	PRIME_CO_OPERATIVE_BANK_LTD("Prime Co-Operative Bank Ltd.", "", "PMEC"),
	PALLAVAN_GRAMA_BANKPABX64HARYANA_GRAMIN_BANK("Pallavan Grama BankPABX64Haryana Gramin Bank", "", "HGBX"),
	THE_UDAIPUR_URBAN_CO_OP_BANK_LTD("The Udaipur Urban Co-op Bank Ltd", "", "UUCX"),
	THE_KASARAGOD_DISTRICT_CO_OPERATIVE_BANK_LTD("The Kasaragod District Co-operative bank Ltd", "", "KADX"),
	RAIGANJ_CENTRAL_CO_OP_BANK_LTD("Raiganj Central Co op Bank Ltd", "", "RJCX"),
	THE_COSMOS_CO_OPERATIVE_BANK_LTD("THE COSMOS CO-OPERATIVE BANK LTD", "1104", "COSB"),
	THE_PUNJAB_STATE_COOPERATIVE_BANK_LTD("THE PUNJAB STATE COOPERATIVE BANK LTD", "", "PSCX"),
	JODHPUR_NAGRIK_SAHAKARI_BANK_LIMITED("Jodhpur Nagrik Sahakari Bank Limited", "", "JONX"),
	THE_FARIDKOT_CENTRAL_CO_OPERATIVE_BANK_LTD("THE FARIDKOT CENTRAL CO-OPERATIVE BANK LTD.", "", "FCCX"),
	INDRAPRASTHA_SEHKARI_BANK_LTD("Indraprastha Sehkari Bank Ltd", "", "ISBX"),
	THE_MALAPPURAM_DISTRICT_CO_OPERATIVE_BANK_LIMITED("The Malappuram District Co-operative Bank Limited", "", "MPDX"),
	PUNJAB_AND_SIND_BANK("Punjab & Sind Bank", "1296", "PSIB"),
	SMRITI_NAGRIK_SAHAKARI_BANK("Smriti Nagrik Sahakari Bank", "", "SNSX"),
	THE_JALANDHAR_CENTRAL_COOPERATIVE_BANK_LIMITED("THE JALANDHAR CENTRAL COOPERATIVE BANK LIMITED", "", "JCCX"),
	THE_LUDHIANA_CENTRAL_COOPERATIVE_BANK_LTD("THE LUDHIANA CENTRAL COOPERATIVE BANK LTD", "", "LCCX"),
	NARMADA_JHABUA_GRAMIN_BANK("Narmada Jhabua Gramin Bank", "", "NJGX"),
	SHREE_MAHALAXMI_URBAN_CO_OP_CREDIT_BANK_LTD("Shree Mahalaxmi Urban Co-op Credit Bank Ltd", "", "MAHX"),
	THE_JUNAGADH_COMMERCIAL_CO_OP_BANK_LTD("The Junagadh Commercial Co-op Bank Ltd", "", "JUCX"),
	THE_SATARA_DISTRICT_CENTRAL_CO_OPERATIVE_BANK_LTD("THE SATARA DISTRICT CENTRAL CO-OPERATIVE BANK LTD", "", "SDSX"),
	THRISSUR_DISTRICT_COOPERATIVE_BANK_LTD("Thrissur District Cooperative Bank Ltd", "", "TDCX"),
	THE_FAZILKA_CENTRAL_COOP_BANK_LTD("THE FAZILKA CENTRAL COOP. BANK LTD", "", "FCBX"),

	MUMBAI_DISTRICT_CENTRAL_CO_OP_BANK_LTD("Mumbai District Central Co-op Bank Ltd", "", "MDCB"),
	CATHOLIC_SYRIAN_BANK("Catholic Syrian Bank", "1094", "CSBK"), CITI_BANK("CITIBANK", "1010", "CITI"),
	DBS_BANK_LTD("DBS Bank Ltd", "1300", "DBSS"), DEUTSCHE_BANK("Deutsche Bank", "1026", "DEUT"),
	KARNATAKA_BANK("Karnataka Bank", "1032", "KARB"), ROYAL_BANK_OF_SCOTLAND("Royal Bank Of Scotland", "1145", "ABNA"),
	STANDARD_CHARTERED_BANK("Standard Chartered Bank", "1097", "SCBL"),
	STATE_BANK_OF_BIKANER_AND_JAIPUR("State Bank of Bikaner and jaipur", "1050", "SBBJ"),
	STATE_BANK_OF_MYSORE("State Bank of Mysore", "1037", "SBMY"),
	STATE_BANK_OF_PATIALA("State Bank of Patiala", "1068", "STBP"), BANK_OF_BARODA("Bank of Baroda", "", "BARB"),
	KOTAK_MAHINDRA_BANK_LTD("Kotak Mahindra Bank Ltd", "1012", "KKBK"),
	ICICI_BANK_LTD("ICICI Bank Ltd", "1013", "ICIC"), HDFC_BANK_LTD("HDFC Bank Ltd", "1004", "HDFC"),
	AXIS_BANK("Axis Bank", "1005", "UTIB"), STATE_BANK_OF_INDIA("State bank of India", "1030", "SBIN"),
	IDFC_FIRST_BANK("IDFC FIRST BANK", "1107", "IDFC"),
	BANK_OF_BARODA_NET_BANKING_CORPORATE("Bank of Baroda Net Banking Corporate", "1092", "BBNC"),
	BANK_OF_BARODA_NET_BANKING_RETAIL("Bank of Baroda Net Banking Retail", "1093", "BBNR"),
	PUNJAB_NATIONAL_BANK_CORPORATE("Punjab National Bank - Corporate", "1304", "PNBC"),
	BANDHAN_BANK("Bandhan Bank", "1206", "BANB"), ING_VYSYA_BANK("ING Vysya Bank", "", "INGB"),
	NKGSB_BANK("NKGSB Bank", "1202", "NKGB"), RBL_BANK_LIMITED("RBL Bank Limited", "1053", "RBLB"),
	SHAMRAO_VITTHAL_CO_OPERATIVE_BANK("Shamrao Vitthal Co-operative Bank", "2015", "SVCB"),
	JANTA_SAHAKARI_BANK("Janta Sahakari Bank", "", "JASB"), DCB_BANK("DCB Bank", "1040", "DCBB"),
	YES_BANK("Yes Bank", "1001", "YESB");

	private final String bankName;
	private final String code;
	private final String bankCode;

	private CamsPayMopType(String bankName, String code, String bankCode) {
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

	public static CamsPayMopType getInstance(String name) {
		CamsPayMopType[] mopTypes = CamsPayMopType.values();
		for (CamsPayMopType mopType : mopTypes) {
			if (mopType.getBankName().equals(name)) {
				return mopType;
			}
		}
		return null;
	}

	public static List<CamsPayMopType> getGetMopsFromSystemProp(String mopsList) {

		List<String> mopStringList = (List<String>) Helper.parseFields(PropertiesManager.propertiesMap.get(mopsList));

		List<CamsPayMopType> mops = new ArrayList<CamsPayMopType>();

		for (String mopCode : mopStringList) {
			CamsPayMopType mop = getmop(mopCode);
			mops.add(mop);
		}
		return mops;
	}

	public static String getmopName(String mopCode) {
		CamsPayMopType mopType = CamsPayMopType.getmop(mopCode);
		if (mopType == null) {
			return "";
		}
		return mopType.getBankName();
	}

	public static String getBankCode(String code) {
		CamsPayMopType mopType = CamsPayMopType.getmop(code);
		if (mopType == null) {
			return "";
		}
		return mopType.getBankCode();
	}

	public static CamsPayMopType getmop(String mopCode) {
		CamsPayMopType mopObj = null;
		if (null != mopCode) {
			for (CamsPayMopType mop : CamsPayMopType.values()) {
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
			for (CamsPayMopType mop : CamsPayMopType.values()) {
				if (mopCode.equals(mop.getBankName().toString())) {
					moptType = mop.getCode();
					break;
				}
			}
		}
		return moptType;
	}

	public static CamsPayMopType getInstanceIgnoreCase(String name) {
		CamsPayMopType[] mopTypes = CamsPayMopType.values();
		for (CamsPayMopType mopType : mopTypes) {
			if (mopType.getBankName().equalsIgnoreCase(name)) {
				return mopType;
			}
		}
		return null;
	}
}
