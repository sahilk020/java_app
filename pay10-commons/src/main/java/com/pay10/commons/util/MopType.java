package com.pay10.commons.util;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.SpringApplication;

public enum MopType {

	// Cards
	VISA("Visa", "VI", false), AMEX("Amex", "AX", true), DISCOVER("Discover", "DI", true), JCB("JCB", "JC", true),
	MASTERCARD("MasterCard", "MC", false), MAESTRO("Maestro", "MS", true), DINERS("Diners", "DN", true),
	RUPAY("Rupay", "RU", false), EZEECLICK("EzeeClick", "EZ", true), UPI("UPI", "UP", false),
	AUTODEBIT("AutoDebit", "AD", true), GOOGLEPAY("GooglePay", "GP", true),

	// DIRECPAY & CITRUSPAY
	
//DEVELOPMENT_CREDIT_BANK("Development Credit Bank", "1040", true),
	

	AXIS_BANK("Axis Bank", "1005", false), ABN_AMRO_BANK("ABN Amro Bank", "1029", true),
	BANK_OF_BAHRAIN_AND_KUWAIT("Bank Of Bahrain And Kuwait", "1043", true),
	BANK_OF_INDIA("Bank Of India", "1009", true), BANK_OF_PUNJAB("Bank of Punjab", "1007", true),
	BANK_OF_MAHARASHTRA("Bank Of Maharashtra", "1064", true), CANARA_BANK("Canara Bank", "1055", true),
	CENTRAL_BANK_OF_INDIA("Central Bank Of India", "1063", true),
	CENTURIAN_BANK_OF_PUNJAB("Centurion Bank of Punjab", "1008", true), CITIBANK("Citi Bank", "1010", true),
	CITY_UNION_BANK("City Union Bank", "1060", true), CORPORATION_BANK("Corporation Bank", "1034", true),
	DCB_BANK("DCB Bank", "1040", true), DCB_BANK_CORPORATE("DCB Bank Corporate", "1292", true),
	DEUTSCHE_BANK("Deutsche Bank", "1026", true), DHANLAXMI_BANK("Dhanlakshmi Bank", "1070", true),
	 FEDERAL_BANK("Federal Bank", "1027", false),
	HDFC_BANK("Hdfc Bank", "1004", false), ICICI_BANK("Icici Bank", "1013", false),
	INDIAN_BANK("Indian Bank", "1069", true), YESBANK_CB("YES BANK CB", "1022", true),
	IDFC_NETBANKING("IDFC NETBANKING", "1316", true),
	INDIAN_OVERSEAS_BANK("Indian Overseas Bank", "1049", true), INDUSIND_BANK("Indusind Bank", "1054", true),
	IDBI_BANK("IDBI Bank", "1003", true), ING_VYSYA_BANK("IngVysya Bank", "1062", true),
	JAMMU_AND_KASHMIR_BANK("Jammu And Kashmir Bank", "1041", true),
	KARNATAKA_BANK_LTD("Karnataka Bank", "1032", true), KARUR_VYSYA_BANK("Karur Vysya Bank", "1048", true),
	KOTAK_BANK("Kotak Mahindra Bank", "1012", false), ORIENTAL_BANK_OF_COMMERCE("Oriental Bank Of Commerce", "1042", true),
	PUNJAB_AND_SIND_BANK("Punjab & Sind Bank", "1296", true),
	PUNJAB_NATIONAL_BANK("Punjab National Bank", "1002", true), RBL_BANK("RBL Bank Limited", "1053", true),
	SOUTH_INDIAN_BANK("South Indian Bank", "1045", true),
	STATE_BANK_OF_BIKANER_AND_JAIPUR("State Bank Of Bikaner And Jaipur", "1050", true),
	STATE_BANK_OF_HYDERABAD("State Bank Of Hyderabad", "1039", true),
	STATE_BANK_OF_INDIA("State Bank Of India", "1030", false),
	STATE_BANK_OF_MYSORE("State Bank Of Mysore", "1037", true),
	STATE_BANK_OF_PATIALA("State Bank Of Patiala", "1068", true),
	
	STATE_BANK_OF_TRAVANCORE("State Bank Of Travancore", "1061", true),
	TAMILNAD_MERCANTILE_BANK("Tamilnad Mercantile Bank", "1065", true),
	TP_BANK_OF_RAJASTHAN("TP Bank of Rajasthan", "1021", true),
	ROYAL_BANK_OF_SCOTLAND("Royal Bank Of Scotland", "1145", true),
	TP_CORPORATION_BANK("TP Corporation Bank", "1018", true), TP_KARNATAKA_BANK("TP Karnataka Bank", "1017", true),
	TP_ORIENTAL_BANK_OF_COMMRCE("TP Oriental Bank of Commerce", "1019", true),
	TP_SOUTH_INDIAN_BANK("TP South Indian Bank", "1020", true),
	TP_THE_FEDERAL_BANK("TP The Federal Bank", "1031", true), TP_VIJAYA_BANK("TP Vijaya Bank", "1023", true),
	UNION_BANK_OF_INDIA("Union Bank Of India", "1038", true),
	UNITED_BANK_OF_INDIA("United Bank Of India", "1046", true), VIJAYA_BANK("Vijaya Bank", "1044", true),
	YES_BANK("Yes Bank ", "1001", false),

	AIRTEL_PAYMENTS_BANK("Airtel Payments Bank", "2002", true), BHARAT_CO_OP_BANK("Bharat Co-Op Bank", "2003", true),
	DENA_BANK("Dena Bank", "2004", true),

	KARUR_VYSYA_CORPORATE_NETBANKING("Karur Vysya - Corporate Netbanking", "2007", true),
	LAKSHMI_VILAS_BANK_CORPORATE_NETBANKING("Lakshmi Vilas Bank - Corporate Netbanking", "2008", true),
	NAINITAL_BANK("Nainital Bank", "2010", true),
	PUNJAB_AND_MAHARASHTRA_CO_OPERATIVE_BANK_LIMITED("Punjab And Maharashtra Co-operative Bank ", "2011", true),

	PUNJAB_NATIONAL_BANK_RETAIL_BANKING("Punjab National Bank - Retail Net Banking", "2013", true),

	SHAMRAO_VITHAL_CO_OPERATIVE_BANK_LTD("Shamrao Vithal Co-operative Bank ", "2015", true),

	UNION_BANK_CORPORATE_NETBANKING("Union Bank - Corporate", "2017", true),

	// Direcpay Only
	DEMO_BANK("Demo Bank", "1025", true),
	TMBNB_BANK("TMBNB Bank", "1065", true),//added by vijay
	TFP("TFP Bank", "1066", true),//added by vijay

	// CITRUSPAY ONLY
	ANDHRA_BANK("Andhra Bank", "1091", true),
	BANK_OF_BRAODA_CORPORATE_ACCOUNTS("Bank of Baroda - Corporate", "1092", true),
	BANK_OF_BARODA_RETAIL_ACCOUNTS("Bank of Baroda Retail Accounts", "1093", true),
	CATHOLIC_SYRIAN_BANK("Catholic Syrian Bank", "1094", true),
	LAKSHMI_VILAS_BANK_NETBANKING("Laxmi Vilas Bank - Retail Net Banking", "1095", true),
	PUNJAB_NATIONAL_BANK_CORPORATE_ACCOUNTS("Punjab National Bank Corporate", "1096", true),
	STANDARD_CHARTERED_BANK("Standard Chartered Bank", "1097", true), SYNDICATE_BANK("Syndicate Bank", "1098", true),
	AXIS_CORPORATE_BANK("Axis Bank Corporate ", "1099", true),
	ICICI_CORPORATE_BANK("ICICI  Bank Corporate", "1100", true), PNB_CORPORATE_BANK("PNB  Bank Corporate", "1101", true),
	HSBC_BANK("HSBC Bank", "1102", true), UCO_BANK("UCO Bank", "1103", true), COSMOS_BANK("COSMOS Bank", "1104", true),
	SARASWAT_BANK("SaraSwat Bank", "1056", true),

	// ATOM
	ATOM_BANK("ATOM Bank", "2001", true), EQUITAS_BANK("Equitas Small Finance Bank", "1106", true),
	IDFC_FIRST_BANK("IDFC FIRST Bank", "1107", true),
	JANATA_SAHKARI_BANK("Janata Sahakari Bank Pune", "1072", true),

	// Wallet
	PAYTM_WALLET("PaytmWallet", "PPL", true), MOBIKWIK_WALLET("MobikwikWallet", "MWL", true),
	OLAMONEY_WALLET("OlaMoneyWallet", "OLAWL", true), MATCHMOVE_WALLET("MatchMoveWallet", "MMWL", true),
	AMAZON_PAY_WALLET("AmazonPayWallet", "APWL", true),

	AIRTEL_PAY_WALLET("AirtelPayWallet", "AWL", true), FREECHARGE_WALLET("FreeChargeWallet", "FCWL", true),
	GOOGLE_PAY_WALLET("GooglePayWallet", "GPWL", true), ITZ_CASH_WALLET("ItzCashWallet", "ICWL", true),
	JIO_MONEY_WALLET("JioMoneyWallet", "JMWL", true), M_PESA_WALLET("MPesaWallet", "MPWL", true),
	OXYZEN_WALLET("OxyzenWallet", "OXWL", true), PHONE_PE_WALLET("PhonePeWallet", "PPWL", true),
	SBI_BUDDY_WALLET("SbiBuddyWallet", "SBWL", true), ZIP_CASH_WALLET("ZipCashWallet", "ZCWL", true),

	HDFC_PAYZAPP_WALLET("HdfcPayZappWallet", "HPWL", true),
	AMEX_EASY_CLICK_WALLET("AmexEasyClickWallet", "AECWL", true), YESBANK_WALLET("YesbankWallet", "YBWL", true),
	PAYCASH_WALLET("PaycashWallet", "PCWL", true), CITIBANK_WALLET("CitibankRewardPoints", "CBWL", true),
	ALLAHABAD_BANK("Allahabad Bank", "1110", true),
	QR_CODE("QR_CODE","QR",true),

	EMI("EMI", "EMI", false),

	// BILLDESK
		ANDHRA_BANK_CORPORATE("Andhra Bank Corporate", "1200", true),
		TN_STATE_COOP_BANK("Tamil Nadu State Co-operative Bank", "1201", true),
		NKGSB_COOP_BANK("NKGSB Co op Bank", "1202", true),
		TJSB_BANK("TJSB Bank", "1203", true),
		KALYAN_JANTA_SAHAKARI_BANK("Kalyan Janata Sahakari Bank", "1204", true),
		MEHSANA_URBAN_COOP_BANK ("Mehsana urban Co-op Bank", "1205", true),
		BANDHAN_BANK ("Bandhan Bank", "1206", true),
		DIGIBANK_BY_DBS ("Digibank by DBS", "1207", true),
		BASSIEN_CATH_COOP_BANK ("Bassien Catholic Coop Bank","1208", true),
		KALUPUR_COMM_COOP_BANK ("The Kalupur Commercial Co-Operative Bank","1209", true),
		THANE_BHARAT_SAH_BANK ("Thane Bharat Sahakari Bank Ltd", "1210", true),
		SURYODAY_BANK ("Suryoday Small Finance Bank", "1211", true),
		ESAF_BANK ("ESAF Small Finance Bank", "1212", true),
		VARACHHA_BANK ("Varachha Co-operative Bank Limited", "1213", true),
		NE_SMALL_FIN_BANK ("North East Small Finance Bank ", "1214", true),
		CORPORATION_BANK_CORP ("Corporation Bank Corporate", "1215", true),
		RBL_BANK_CORP ("RBL Bank Limited Corporate ", "1216", true),
		SHAMRAO_VITHAL_CORPORATE ("Shamrao Vithal Co op Bank - Corporate", "1217", true),
		BARCLAYS_CORPORATE ("Barclays Corporate  Corporate Banking", "1218", true),
		ZOROASTRIAN_BANK ("Zoroastrian Co-op Bank", "1219", true),
		AU_SMALL_FIN_BANK ("AU small finance bank", "1220", true),
		KARNATAKA_GRAMINA_BANK ("Karnataka Gramina Bank", "1221", true),
		FINCARE_BANK ("Fincare Bank", "1222", true),
		PAY_WORLD_MONEY ("Pay World Money", "PCH", true),
		DCB_SIPPY ("DCB Cippy", "DCW", true),
		ICC_CASH_CARD ("ICC Cash Card", "ICC", true),
		CASHFREE_DEMO_BANK("Cashfree Demo Bank", "3333", true),
	//CASHFREE- ADDED BY UMESH on 20thOCT2021j
		DBS_BANK("DBS Bank Ltd", "1300", true),
	    DCB_BANK_PERSONAL("DCB Bank - Personal", "1301", true),
		BANK_OF_INDIA_CORPORATE("Bank of India - Corporate", "1302", true),
		LAXMIVILAS_BANK_CORPORATE("Lakshmi Vilas Bank - Corporate", "1303", true),
	    PUNJAB_NATIONAL_BANK_CORPORATE("Punjab National Bank - Corporate", "1304", true),
	    STATE_BANK_OF_INDIA_CORPORATE("State Bank of India - Corporate", "1305", true),
	    UNION_BANK_OF_INDIA_CORPORATE("Union Bank of India - Corporate", "1306", true),
	    DHANLAXMI_BANK_CORPORATE("Dhanlaxmi Bank Corporate", "1307", true),
		ICICI_CORPORATE_NETBANKING("ICICI Corporate Netbanking", "1308", true),
		RATNAKAR_CORPORATE_BANKING("Ratnakar Corporate Banking", "1309", true),
	    SHIVALIK_BANK("Shivalik Bank", "1310", true),
	AU_SMALL_FINANCE("AU Small Finance", "1311", true),
	UTKARSH_SMALL_FINANCE_BANK("Utkarsh Small Finance bank", "1312", true),
	THE_SURAT_PEOPLE_COOPERATIVE_BANK("The Surat People’s Co-operative Bank", "1313", true),
	GUJRAT_STATE_COOPERATIVE_BANK_LTD("Gujarat State Co-operative Bank", "1314", true),
	HSBC_RETAIL_NETBANKING("HSBC Retail", "1315", true),
		AGREEPAY_DEMO_BANK("Agreepay Demo Bank", "3334", true),
		
		UCPBANK("UCPBank", "3006", true),
		MAY_BANK("May Bank", "3007", true),
		UNION_BANK_OF_PHILIPPINES("Union Bank of Philippines", "3008", true),
		RCBC_DIRECT_DEBIT("RCBC Direct Debit", "3009", true),
		PIA_BANK("PIA Bank", "3010", true),
		
		CIMB_BANK("CIMB Bank","3011", true),
		HONG_LEONG_BANK("Hong Leong Bank","3012", true),
		//MAY_BANK("MayBank","3013", true),
		PUBLIC_BANK("Public Bank","3014", true),
		RHB_BANK("RHB Bank","3015", true),
		//HSBC_BANK("HSBC Bank","3016", true),
		UOB_BANK("UOB Bank","3017", true),
		//STANDARD_CHARTERED_BANK("Standard Chartered Bank","3018", true),
		OCBC_BANK("OCBC Bank","3019", true),
		ALLIANCE_BANK("Alliance Bank","3020", true),
		AMBANK("AmBank","3021", true),
		BANK_SIMPANAN_NASIONAL("Bank Simpanan Nasional","3022", true),
		ASIA_COMMERCIAL_BANK("Asia Commercial Bank","3023", true),
		BIDV_BANK("BIDV Bank","3024", true),
		DONGA_BANK("DongA Bank","3025", true),
		EXIM_BANK("Exim Bank","3026", true),
		SACOM_BANK("Sacom Bank","3027", true),
		TECHCOM_BANK("Techcom Bank","3028", true),
		VIETCOM_BANK("Vietcom Bank","3029", true),
		VIETIN_BANK("Vietin Bank","3030", true),
		AGRI_BANK("Agri Bank","3031", true),
		
	// Recurring Payment
	RECURRING_AUTO_DEBIT("Recurring Auto Debit", "RAD", true), 
	RECURRING_INVOICE("Recurring Invoice", "RIN", true),
	VIETNAM_BANK_TRANSFER("Vietnam bank transfer","3032",true),
	GCASH_WALLET("GCashWallet", "GCASHWL", true),
	MAYA_WALLET("MayaWallet", "MAYAWL", true),
	
	//For THB Currency
	BANGKOK_BANK("Bangkok Bank","3033",true),	    
	KASIKORN_BANK("Kasikorn Bank","3034",true),	    
	KRUNG_THAI_BANK("Krung Thai Bank","3035",true),	    
	KRUNGSRI_BANK("Krungsri Bank","3036",true),	    
	SIAM_COMMERCIAL_BANK("Siam Commercial Bank","3037",true),
	THAI_MILITARY_BANK("Thai Military Bank","3038",true),
	
	//For VND Currecny
	VIETNAM_VTPAY("Vietnam VTPay","3039",true),
	VIETNAM_MOMOPAY("Vietnam MOMOPay","3040",true),
	VIETNAM_ZALOPAY("Vietnam ZaloPay","3041",true),
	
	//For THB Currecny
	INDONESIA_BANK_TRANSFER("Indonesia bank transfer","3042",true),
	INDONESIA_DANA("Indonesia DANA","3043",true),
	INDONESIA_OVO("Indonesia OVO","3044",true),
	INDONESIA_QRIS("Indonesia QRIS","3045",true),
	//
	THAILAND_BANK_TRANSFER("Thailand bank transfer","3046",true),
	ALIPAY("alipay","3047",true);

	private final String name;
	private final String code;
	private final boolean isOther;

	private MopType(String name, String code, boolean isOther) {
		this.name = name;
		this.code = code;
		this.isOther = isOther;
	}

	public String getName() {
		return name;
	}

	public String getCode() {
		return code;
	}

	public boolean isOther() {
		return isOther;
	}

	public static MopType getInstance(String name) {
		MopType[] mopTypes = MopType.values();
		for (MopType mopType : mopTypes) {
			if (mopType.getName().equals(name)) {
				return mopType;
			}
		}
		return null;
	}

	public static List<MopType> getTMBNBMops() {
		return getGetMopsFromSystemProp("TMBNBMOP");//added by vijay
	}


	public static List<MopType> getTFPCCMops() {
		return getGetMopsFromSystemProp("TFPCCMOP");//added by vijay
	}
	public static List<MopType> getDCMops() {
		return getGetMopsFromSystemProp("DCMOP");
	}

	public static List<MopType> getCCMops() {
		return getGetMopsFromSystemProp("CCMOP");
	}

	public static List<MopType> getNBMops() {
		return getGetMopsFromSystemProp("NBMOP");
	}

	public static List<MopType> getWLMops() {
		return getGetMopsFromSystemProp("WALLET");
	}

	public static List<MopType> getADMops() {
		return getGetMopsFromSystemProp("AUTODEBIT");
	}

	public static List<MopType> getUPIMops() {
		return getGetMopsFromSystemProp("UPI");
	}

	public static List<MopType> getFSSUPIMops() {
		return getGetMopsFromSystemProp("FSSUPIMOP");
	}

	public static List<MopType> getFSSCCMops() {
		return getGetMopsFromSystemProp("FSSCCMOP");
	}


	public static List<MopType> getFSSDCMops() {
		return getGetMopsFromSystemProp("FSSDCMOP");
	}
	public static List<MopType> getJammuAndKashmirNBMops() {
		return getGetMopsFromSystemProp("JAMMUANDKASHMIRNBMOP");
	}
	public static List<MopType> getShivalikNBMops() {
		return getGetMopsFromSystemProp("SHIVALIKNBBANKNBMOP");
	}
	
	public static List<MopType> getCityUnionBankNBMops() {
		return getGetMopsFromSystemProp("CITYUNIONBANKNBMOP");
	}
	public static List<MopType> getIDFCBANKNBMops() {
		return getGetMopsFromSystemProp("IDFCBANKNB");
	}

	public static List<MopType> getFSSPCMops() {
		return getGetMopsFromSystemProp("FSSPCMOP");
	}

	public static List<MopType> getFSSNBMops() {
		return getGetMopsFromSystemProp("FSSNBMOP");
	}

	public static List<MopType> getFSSWLMops() {
		return getGetMopsFromSystemProp("FSSWLMOP");
	}

	public static List<MopType> getIDFCUPIMops() {
		return getGetMopsFromSystemProp("IDFCUPIMOP");
	}
	public static List<MopType> getCOSMOSQRMops() {
		return getGetMopsFromSystemProp("COSMOSQRMOP");
	}

	public static List<MopType> getBOBCCMops() {
		return getGetMopsFromSystemProp("BOBCCMOP");
	}

	public static List<MopType> getBOBDCMops() {
		return getGetMopsFromSystemProp("BOBDCMOP");
	}

	public static List<MopType> getBOBPCMops() {
		return getGetMopsFromSystemProp("BOBPCMOP");
	}

	public static List<MopType> getBOBUPMops() {
		return getGetMopsFromSystemProp("BOBUPMOP");
	}

	public static List<MopType> getAXISCBCCMops() {
		return getGetMopsFromSystemProp("AXISCBCCMOP");
	}

	public static List<MopType> getAXISCBDCMops() {
		return getGetMopsFromSystemProp("AXISCBDCMOP");
	}

	public static List<MopType> getKOTAKCCMops() {
		return getGetMopsFromSystemProp("KOTAKCCMOP");
	}

	public static List<MopType> getKOTAKDCMops() {
		return getGetMopsFromSystemProp("KOTAKDCMOP");
	}

	/*
	 * public static List<MopType> getKOTAKPCMops() { return
	 * getGetMopsFromSystemProp("KOTAKPCMOP"); }
	 */

	public static List<MopType> getKOTAKUPIMops() {
		return getGetMopsFromSystemProp("KOTAKUPIMOP");
	}

	public static List<MopType> getICICIMPGSCCMops() {
		return getGetMopsFromSystemProp("ICICIMPGSCCMOP");
	}

	public static List<MopType> getICICIMPGSDCMops() {
		return getGetMopsFromSystemProp("ICICIMPGSDCMOP");
	}

	public static List<MopType> getICICIMPGSPCMops() {
		return getGetMopsFromSystemProp("ICICIMPGSPCMOP");
	}

	public static List<MopType> getATLMops() {
		return getGetMopsFromSystemProp("ATLMOP");
	}

	public static List<MopType> getFIRSTDATADCMops() {
		return getGetMopsFromSystemProp("FIRSTDATADCMOP");
	}

	public static List<MopType> getFIRSTDATAPCMops() {
		return getGetMopsFromSystemProp("FIRSTDATAPCMOP");
	}

	public static List<MopType> getFIRSTDATACCMops() {
		return getGetMopsFromSystemProp("FIRSTDATACCMOP");
	}

	public static List<MopType> getFEDERALDCMops() {
		return getGetMopsFromSystemProp("FEDERALDCMOP");
	}

	public static List<MopType> getFEDERALPCMops() {
		return getGetMopsFromSystemProp("FEDERALPCMOP");
	}

	public static List<MopType> getFEDERALUPIMops() {
		return getGetMopsFromSystemProp("FEDERALUPIMOP");
	}

	public static List<MopType> getYESBANKCBMops() {
		return getGetMopsFromSystemProp("YESBANKCBMOP");
	}

	public static List<MopType> getCYBERSOURCEDCMops() {
		return getGetMopsFromSystemProp("CYBERSOURCEDCMOP");
	}

	public static List<MopType> getCYBERSOURCEPCMops() {
		return getGetMopsFromSystemProp("CYBERSOURCEPCMOP");
	}

	public static List<MopType> getCYBERSOURCECCMops() {
		return getGetMopsFromSystemProp("CYBERSOURCECCMOP");
	}

	public static List<MopType> getFEDERALCCMops() {
		return getGetMopsFromSystemProp("FEDERALCCMOP");
	}

	public static List<MopType> getBARCLAYCCMops() {
		return getGetMopsFromSystemProp("BARCLAYCCMOP");
	}

	public static List<MopType> getCITRUSDCMops() {
		return getGetMopsFromSystemProp("CITRUSDCMOP");
	}

	public static List<MopType> getCITRUSCCMops() {
		return getGetMopsFromSystemProp("CITRUSCCMOP");
	}

	public static List<MopType> getAMEXCCMops() {
		return getGetMopsFromSystemProp("AMEXCCMOP");
	}

	public static List<MopType> getEZEECLICKCCMops() {
		return getGetMopsFromSystemProp("EZEECLICKCCMOP");
	}

	public static List<MopType> getPAYTMWLMops() {
		return getGetMopsFromSystemProp("PAYTMWLMOP");
	}

	public static List<MopType> getMOBIKWIKWLMops() {
		return getGetMopsFromSystemProp("MOBIKWIKWL");
	}

	public static List<MopType> getCITRUSNBMops() {
		return getGetMopsFromSystemProp("CITRUSNB");
	}

	public static List<MopType> getDIRECPAYNBMops() {
		return getGetMopsFromSystemProp("DIRECPAYNB");
	}

	public static List<MopType> getDIRECPAYCCMops() {
		return getGetMopsFromSystemProp("DIRECPAYCCMOP");
	}

	public static List<MopType> getDIRECPAYDCMops() {
		return getGetMopsFromSystemProp("DIRECPAYDCMOP");
	}

	public static List<MopType> getATOMNBMops() {
		return getGetMopsFromSystemProp("ATOMNB");
	}

	public static List<MopType> getATOMUPMops() {
		return getGetMopsFromSystemProp("ATOMUPIMOP");
	}
	
	public static List<MopType> getATOMWLMops() {
		return getGetMopsFromSystemProp("ATOMWLMOP");
	}

	public static List<MopType> getATOMEMIMops() {
		return getGetMopsFromSystemProp("ATOMEMIMOP");
	}
	public static List<MopType> getCOSMOSUPIMops() {
		return getGetMopsFromSystemProp("COSMOSUPIMOP");
	}
	public static List<MopType> getFreeChargeUPMops() {
		return getGetMopsFromSystemProp("FREECHARGEUPIMOP");
	}

	public static List<MopType> getIciciNBMops() {
		return getGetMopsFromSystemProp("ICICINBMOP");
	}
	public static List<MopType> getCANARANBBANKMops() {
		return getGetMopsFromSystemProp("CANARABANKNBMop");
	}

	public static List<MopType> getSBINBMops() {
		return getGetMopsFromSystemProp("SBINBMOP");
	}
	
	public static List<MopType> getSBIUPIMops() {
		return getGetMopsFromSystemProp("SBIUPIMOP");
	}

	public static List<MopType> getATOMCCMops() {
		return getGetMopsFromSystemProp("ATOMCCMOP");
	}

	public static List<MopType> getATOMDCMops() {
		return getGetMopsFromSystemProp("ATOMDCMOP");
	}

	public static List<MopType> getAPBLWLMops() {
		return getGetMopsFromSystemProp("APBLWLMOP");
	}

	public static List<MopType> getAPBLNBMops() {
		return getGetMopsFromSystemProp("APBLNBMOP");
	}

	public static List<MopType> getMobikwikWLMops() {
		return getGetMopsFromSystemProp("MOBIKWIKWLMOP");
	}

	public static List<MopType> getphonePeWLMops() {
		return getGetMopsFromSystemProp("PHONEPEWLMOP");
	}

	public static List<MopType> getKOTAKNBMops() {
		return getGetMopsFromSystemProp("KOTAKNBMOP");
	}

	public static List<MopType> getSBIBANKNBMops() {
		return getGetMopsFromSystemProp("SBINB");
	}

	public static List<MopType> getALLAHABADBANKNBMops() {
		return getGetMopsFromSystemProp("ALLAHABADBANKNB");
	}

	public static List<MopType> getVIJAYABANKNBMops() {
		return getGetMopsFromSystemProp("VIJAYABANKNB");
	}

	public static List<MopType> getAXISBANKNBMops() {
		return getGetMopsFromSystemProp("AXISBANKNB");
	}
	public static List<MopType> getFEDERALBANKNBMops() {
		return getGetMopsFromSystemProp("FEDERALBANKNB");
	}

	public static List<MopType> getYESBANKNBMop() {
		return getGetMopsFromSystemProp("YESBANKNBMOP");
	}

	public static List<MopType> getICICIBANKNBMops() {
		return getGetMopsFromSystemProp("ICICIBANKNB");
	}
	
	public static List<MopType> getICICIBANKUPIMops() {
		return getGetMopsFromSystemProp("ICICIBANKUP");
	}

	public static List<MopType> getCORPORATIONBANKNBMops() {
		return getGetMopsFromSystemProp("CORPORATIONBANKNB");
	}

	public static List<MopType> getSOUTHINDIANBANKNBMops() {
		return getGetMopsFromSystemProp("SOUTHINDIANBANKNB");
	}

	public static List<MopType> getKARURVYSYABANKNBMops() {
		return getGetMopsFromSystemProp("KARURVYSYABANKNB");
	}

	public static List<MopType> getKARNATAKABANKNBMops() {
		return getGetMopsFromSystemProp("KARNATAKABANKNB");
	}

	public static List<MopType> getOLAMONEYWLMops() {
		return getGetMopsFromSystemProp("OLAMONEYWL");
	}

	public static List<MopType> getMIGSDCMops() {
		return getGetMopsFromSystemProp("MIGSDCMOP");
	}

	public static List<MopType> getMIGSPCMops() {
		return getGetMopsFromSystemProp("MIGSPCMOP");
	}

	public static List<MopType> getMATCHMOVEWLMops() {
		return getGetMopsFromSystemProp("MATCHMOVEWL");
	}

	public static List<MopType> getMIGSCCMops() {
		return getGetMopsFromSystemProp("MIGSCCMOP");
	}

	public static List<MopType> getIDBIBANKDCMops() {
		return getGetMopsFromSystemProp("IDBIBANKDCMOP");
	}

	public static List<MopType> getIDBIBANKPCMops() {
		return getGetMopsFromSystemProp("IDBIBANKPCMOP");
	}

	public static List<MopType> getIDBIBANKCCMops() {
		return getGetMopsFromSystemProp("IDBIBANKCCMOP");
	}

	public static List<MopType> getIDBIBANKNB() {
		return getGetMopsFromSystemProp("IDBIBANKNB");
	}

	public static List<MopType> getISGPAYCCMops() {
		return getGetMopsFromSystemProp("ISGPAYCCMOP");
	}
	public static List<MopType> getISGPAYUPMops() {
		return getGetMopsFromSystemProp("ISGPAYUPMOP");
	}

	public static List<MopType> getISGPAYDCMops() {
		return getGetMopsFromSystemProp("ISGPAYDCMOP");
	}

	public static List<MopType> getISGPAYNBMops() {
		return getGetMopsFromSystemProp("ISGPAYNBMOP");
	}

	public static List<MopType> getAXISBANKCCMops() {
		return getGetMopsFromSystemProp("AXISBANKCCMOP");
	}

	public static List<MopType> getAXISBANKDCMops() {
		return getGetMopsFromSystemProp("AXISBANKDCMOP");
	}

	public static List<MopType> getAXISBANKUPMops() {
		return getGetMopsFromSystemProp("AXISBANKUPMOP");
	}

	public static List<MopType> getAXISBANKNBMop() {
		return getGetMopsFromSystemProp("AXISBANKNBMOP");
	}

	public static List<MopType> getINGENICOCCMops() {
		return getGetMopsFromSystemProp("INGENICOCCMOP");
	}

	public static List<MopType> getINGENICODCMops() {
		return getGetMopsFromSystemProp("INGENICODCMOP");
	}

	public static List<MopType> getPAYUCCMOPMops() {
		return getGetMopsFromSystemProp("PAYUCCMOP");
	}

	public static List<MopType> getPAYUDCMOPMops() {
		return getGetMopsFromSystemProp("PAYUDCMOP");
	}

	public static List<MopType> getPAYUNBMops() {
		return getGetMopsFromSystemProp("PAYUNBMOP");
	}

	public static List<MopType> getPAYUWLMops() {
		return getGetMopsFromSystemProp("PAYUWLMOP");
	}

	public static List<MopType> getPAYUUPMops() {
		return getGetMopsFromSystemProp("PAYUUPMOP");
	}

	public static List<MopType> getBILLDESKUPMops() {
		return getGetMopsFromSystemProp("BILLDESKUPMOP");
	}

	public static List<MopType> getBILLDESKWLMops() {
		return getGetMopsFromSystemProp("BILLDESKWLMOP");
	}

	public static List<MopType> getBILLDESKNBMops() {
		return getGetMopsFromSystemProp("BILLDESKNBMOP");
	}

	public static List<MopType> getLYRACCMOPS() {
		return getGetMopsFromSystemProp("LYRACCMOP");
	}

	public static List<MopType> getLYRADCMOPS() {
		return getGetMopsFromSystemProp("LYRADCMOP");
	}

	public static List<MopType> getLYRANBMOPS() {
		return getGetMopsFromSystemProp("LYRANBMOP");
	}

	public static List<MopType> getLYRAWLMOPS() {
		return getGetMopsFromSystemProp("LYRAWLMOP");
	}

	public static List<MopType> getLYRAUPMOPS() {
		return getGetMopsFromSystemProp("LYRAUPMOP");
	}

	public static List<MopType> getEASEBUZZCCMOPMops() {
		return getGetMopsFromSystemProp("EASEBUZZCCMOP");
	}

	public static List<MopType> getEASEBUZZDCMOPMops() {
		return getGetMopsFromSystemProp("EASEBUZZDCMOP");
	}

	public static List<MopType> getEASEBUZZNBMops() {
		return getGetMopsFromSystemProp("EASEBUZZNBMOP");
	}

	public static List<MopType> getEASEBUZZWLMops() {
		return getGetMopsFromSystemProp("EASEBUZZWLMOP");
	}

	public static List<MopType> getEASEBUZZUPMops() {
		return getGetMopsFromSystemProp("EASEBUZZUPMOP");
	}
	public static List<MopType> getCASHFREECCMOPMops() {
		return getGetMopsFromSystemProp("CASHFREECCMOP");
	}

	public static List<MopType> getCASHFREEDCMOPMops() {
		return getGetMopsFromSystemProp("CASHFREEDCMOP");
	}

	public static List<MopType> getCASHFREENBMops() {
		return getGetMopsFromSystemProp("CASHFREENBMOP");
	}

	public static List<MopType> getCASHFREEWLMops() {
		return getGetMopsFromSystemProp("CASHFREEWLMOP");
	}

	public static List<MopType> getCASHFREEUPMops() {
		return getGetMopsFromSystemProp("CASHFREEUPMOP");
	}
	
	public static List<MopType> getDEMOCCMOPMops() {
		return getGetMopsFromSystemProp("DEMOCCMOP");
	}

	public static List<MopType> getDEMODCMOPMops() {
		return getGetMopsFromSystemProp("DEMODCMOP");
	}

	public static List<MopType> getDEMONBMops() {
		return getGetMopsFromSystemProp("DEMONBMOP");
	}

	public static List<MopType> getDEMOWLMops() {
		return getGetMopsFromSystemProp("DEMOWLMOP");
	}

	public static List<MopType> getDEMOUPMops() {
		return getGetMopsFromSystemProp("DEMOUPMOP");
	}

	
	// Added By Sonu Chaudhari
	public static List<MopType> getSBICCMops() {
		return getGetMopsFromSystemProp("SBICCMOP");
	}

	// Added By Sonu Chaudhari
	public static List<MopType> getSBIDCMops() {
		return getGetMopsFromSystemProp("SBIDCMOP");
	}

	
	public static List<MopType> getAGREEPAYCCMOPMops() {
		return getGetMopsFromSystemProp("AGREEPAYCCMOP");
	}

	public static List<MopType> getAGREEPAYDCMOPMops() {
		return getGetMopsFromSystemProp("AGREEPAYDCMOP");
	}

	public static List<MopType> getAGREEPAYNBMops() {
		return getGetMopsFromSystemProp("AGREEPAYNBMOP");
	}
	public static List<MopType> getAGREEPAYWLMops() {
		return getGetMopsFromSystemProp("AGREEPAYWLMOP");
	}
	public static List<MopType> getAGREEPAYUPMops() {
		return getGetMopsFromSystemProp("AGREEPAYUPMOP");
	}
	public static List<MopType> getPINELABSCCMOPMops() {
		return getGetMopsFromSystemProp("PINELABSCCMOP");
	}

	public static List<MopType> getPINELABSDCMOPMops() {
		return getGetMopsFromSystemProp("PINELABSDCMOP");
	}

	public static List<MopType> getPINELABSNBMops() {
		return getGetMopsFromSystemProp("PINELABSNBMOP");
	}
	public static List<MopType> getPINELABSWLMops() {
		return getGetMopsFromSystemProp("PINELABSWLMOP");
	}
	public static List<MopType> getPINELABSUPMops() {
		return getGetMopsFromSystemProp("PINELABSUPMOP");
	}

	public static List<MopType> getCAMSPAYCCMOPMops() {
		return getGetMopsFromSystemProp("CAMSPAYCCMOP");
	}

	public static List<MopType> getCAMSPAYDCMOPMops() {
		return getGetMopsFromSystemProp("CAMSPAYDCMOP");
	}

	public static List<MopType> getCAMSPAYNBMops() {
		return getGetMopsFromSystemProp("CAMSPAYNBMOP");
	}

	public static List<MopType> getCAMSPAYUPMops() {
		return getGetMopsFromSystemProp("CAMSPAYUPMOP");
	}

	public static List<MopType> getGetMopsFromSystemProp(String mopsList) {

		List<String> mopStringList = (List<String>) Helper.parseFields(PropertiesManager.propertiesMap.get(mopsList));

		List<MopType> mops = new ArrayList<MopType>();

		for (String mopCode : mopStringList) {
			MopType mop = getmop(mopCode);
			mops.add(mop);
		}
		return mops;
	}

	public static String getmopName(String mopCode) {
		MopType mopType = MopType.getmop(mopCode);
		if (mopType == null) {
			return "";
		}
		return mopType.getName();
	}

	public static MopType getmop(String mopCode) {
		MopType mopObj = null;
		if (null != mopCode) {
			for (MopType mop : MopType.values()) {
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
			for (MopType mop : MopType.values()) {
				if (mopCode.equals(mop.getName().toString())) {
					moptType = mop.getCode();
					break;
				}
			}
		}
		return moptType;
	}
	
	public static MopType getInstanceUsingStringValue1(String value) {
		MopType[] mopTypes = MopType.values();
		for (MopType mopType : mopTypes) {
			if (mopType.toString().equals(value)) {
				return mopType;
			}
		}
		return null;
	}
	

	public static MopType getInstanceUsingCode(String value) {
		MopType[] mopTypes = MopType.values();
		for (MopType mopType : mopTypes) {
			if (mopType.getCode().equals(value)) {
				return mopType;
			}
		}
		return null;
	}
	public static MopType getInstanceIgnoreCaseForRuleEngine(String name) {
		MopType[] mopTypes = MopType.values();
		for (MopType mopType : mopTypes) {
			if (mopType.getName().equalsIgnoreCase(name)) {
				return mopType;
			}
		}
		return null;
	}
	public static MopType getInstanceIgnoreCase(String name) {
		MopType[] mopTypes = MopType.values();
		for (MopType mopType : mopTypes) {
			if (mopType.toString().equalsIgnoreCase(name)) {
				return mopType;
			}
		}
		return null;
	}

	public static String getOTherTypeCodes() {

		MopType[] mopTypes = MopType.values();
		StringBuilder mopTypeString = new StringBuilder();
		for (MopType mopType : mopTypes) {
			if (mopType.isOther()) {
				mopTypeString.append(mopType.getCode());
				mopTypeString.append(Constants.COMMA.getValue().trim());
			}
		}
		return mopTypeString.substring(0, mopTypeString.length() - 1);
	}
	public static String getCodeusingInstance(String name) {
		MopType[] mopTypes = MopType.values();
		for (MopType mopType : mopTypes) {
			if (mopType.toString().equalsIgnoreCase(name)) {
				return mopType.code;
			}
		}
		return null;
	}
	public static String getNameFromInstance(String instance) {
		MopType[] mopTypes = MopType.values();
		for (MopType mopType : mopTypes) {
			if (mopType.toString().equalsIgnoreCase(instance)) {
			
				return mopType.name;
			}
		}
		return null;
	}

	public static List<MopType> getQUOMOCCMops() {
		return getGetMopsFromSystemProp("QUOMOCCMOP");
	}

	public static List<MopType> getQUOMODCMops() {
		return getGetMopsFromSystemProp("QUOMODCMOP");
	}

	public static List<MopType> getQUOMONBMops() {
		return getGetMopsFromSystemProp("QUOMONB");
	}
	
	public static List<MopType> getQUOMOWLMops() {
		return getGetMopsFromSystemProp("QUOMOWLMOP");
	}

	public static  List<MopType> getHTPAYNBBANKMops() {
		// TODO Auto-generated method stub
		return getGetMopsFromSystemProp("HTPAYNB");
	}
}
