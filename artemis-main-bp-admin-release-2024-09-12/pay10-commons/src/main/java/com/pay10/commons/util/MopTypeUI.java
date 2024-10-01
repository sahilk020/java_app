package com.pay10.commons.util;

import java.util.ArrayList;
import java.util.List;

public enum MopTypeUI {

	// Cards
	VISA("Visa", "VI", "Visa"), MASTERCARD("MasterCard", "MC", "MasterCard"),
	RUPAY("Rupay", "RU", "Rupay"), UPI("UPI", "UP", "UPI"),
	STATE_BANK_OF_INDIA("State Bank Of India", "1030", "SBI"), HDFC_BANK("Hdfc Bank", "1004", "Hdfc"),
	ICICI_BANK("Icici Bank", "1013", "Icici"), KOTAK_BANK("Kotak Bank", "1012", "Kotak"),
	YES_BANK("Yes Bank", "1001", "Yes"), AXIS_BANK("Axis Bank", "1005", "Axis"),
	OTHERS("Others", "OT", "Others"),
	EMI("EMI", "EMI", "EMI");

	private final String name;
	private final String code;
	private final String uiName;
	
	private MopTypeUI(String name, String code) {
		this.name = this.uiName = name;
		this.code = code;
	}

	private MopTypeUI(String name, String code, String uiName) {
		this.name = name;
		this.uiName = uiName;
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public String getCode() {
		return code;
	}

	public String getUiName() {
		return uiName;
	}

	public static MopTypeUI getInstance(String name) {
		MopTypeUI[] mopTypes = MopTypeUI.values();
		for (MopTypeUI mopType : mopTypes) {
			if (mopType.getName().equals(name)) {
				return mopType;
			}
		}
		return null;
	}

	public static List<MopTypeUI> getDCMops() {
		return getGetMopsFromSystemProp("DCMOP");
	}

	public static List<MopTypeUI> getCCMops() {
		return getGetMopsFromSystemProp("CCMOP");
	}

	public static List<MopTypeUI> getNBMops() {
		return getGetMopsFromSystemProp("NBMOP");
	}

	public static List<MopTypeUI> getWLMops() {
		return getGetMopsFromSystemProp("WALLET");
	}

	public static List<MopTypeUI> getADMops() {
		return getGetMopsFromSystemProp("AUTODEBIT");
	}

	public static List<MopTypeUI> getUPIMops() {
		return getGetMopsFromSystemProp("UPI");
	}

	public static List<MopTypeUI> getFSSUPIMops() {
		return getGetMopsFromSystemProp("FSSUPIMOP");
	}

	public static List<MopTypeUI> getFSSCCMops() {
		return getGetMopsFromSystemProp("FSSCCMOP");
	}

	public static List<MopTypeUI> getFSSDCMops() {
		return getGetMopsFromSystemProp("FSSDCMOP");
	}

	public static List<MopTypeUI> getFSSPCMops() {
		return getGetMopsFromSystemProp("FSSPCMOP");
	}

	public static List<MopTypeUI> getFSSNBMops() {
		return getGetMopsFromSystemProp("FSSNBMOP");
	}

	public static List<MopTypeUI> getFSSWLMops() {
		return getGetMopsFromSystemProp("FSSWLMOP");
	}

	public static List<MopTypeUI> getIDFCUPIMops() {
		return getGetMopsFromSystemProp("IDFCUPIMOP");
	}

	public static List<MopTypeUI> getBOBCCMops() {
		return getGetMopsFromSystemProp("BOBCCMOP");
	}

	public static List<MopTypeUI> getBOBDCMops() {
		return getGetMopsFromSystemProp("BOBDCMOP");
	}

	public static List<MopTypeUI> getAXISCBCCMops() {
		return getGetMopsFromSystemProp("AXISCBCCMOP");
	}

	public static List<MopTypeUI> getAXISCBDCMops() {
		return getGetMopsFromSystemProp("AXISCBDCMOP");
	}

	public static List<MopTypeUI> getKOTAKCCMops() {
		return getGetMopsFromSystemProp("KOTAKCCMOP");
	}

	public static List<MopTypeUI> getKOTAKDCMops() {
		return getGetMopsFromSystemProp("KOTAKDCMOP");
	}

	public static List<MopTypeUI> getATLMops() {
		return getGetMopsFromSystemProp("ATLMOP");
	}

	public static List<MopTypeUI> getFIRSTDATADCMops() {
		return getGetMopsFromSystemProp("FIRSTDATADCMOP");
	}

	public static List<MopTypeUI> getFIRSTDATACCMops() {
		return getGetMopsFromSystemProp("FIRSTDATACCMOP");
	}

	public static List<MopTypeUI> getFEDERALDCMops() {
		return getGetMopsFromSystemProp("FEDERALDCMOP");
	}

	public static List<MopTypeUI> getFEDERALUPIMops() {
		return getGetMopsFromSystemProp("FEDERALUPIMOP");
	}

	public static List<MopTypeUI> getSBINBMops() {
		return getGetMopsFromSystemProp("SBINBMOP");
	}

	public static List<MopTypeUI> getKOTAKUPIMops() {
		return getGetMopsFromSystemProp("KOTAKUPIMOP");
	}

	public static List<MopTypeUI> getYESBANKCBMops() {
		return getGetMopsFromSystemProp("YESBANKCBMOP");
	}

	public static List<MopTypeUI> getCYBERSOURCEDCMops() {
		return getGetMopsFromSystemProp("CYBERSOURCEDCMOP");
	}

	public static List<MopTypeUI> getCYBERSOURCECCMops() {
		return getGetMopsFromSystemProp("CYBERSOURCECCMOP");
	}

	public static List<MopTypeUI> getFEDERALCCMops() {
		return getGetMopsFromSystemProp("FEDERALCCMOP");
	}

	public static List<MopTypeUI> getBARCLAYCCMops() {
		return getGetMopsFromSystemProp("BARCLAYCCMOP");
	}

	public static List<MopTypeUI> getCITRUSDCMops() {
		return getGetMopsFromSystemProp("CITRUSDCMOP");
	}

	public static List<MopTypeUI> getCITRUSCCMops() {
		return getGetMopsFromSystemProp("CITRUSCCMOP");
	}

	public static List<MopTypeUI> getFreeChargeUPMops() {
		return getGetMopsFromSystemProp("FREECHARGEUPIMOP");
	}

	public static List<MopTypeUI> getAMEXCCMops() {
		return getGetMopsFromSystemProp("AMEXCCMOP");
	}

	public static List<MopTypeUI> getEZEECLICKCCMops() {
		return getGetMopsFromSystemProp("EZEECLICKCCMOP");
	}

	public static List<MopTypeUI> getPAYTMWLMops() {
		return getGetMopsFromSystemProp("PAYTMWL");
	}

	public static List<MopTypeUI> getPAYUWLMops() {
		return getGetMopsFromSystemProp("PAYUWL");
	}

	public static List<MopTypeUI> getPAYUNBMops() {
		return getGetMopsFromSystemProp("PAYUNB");
	}

	public static List<MopTypeUI> getPAYUCCMops() {
		return getGetMopsFromSystemProp("PAYUCC");
	}

	public static List<MopTypeUI> getPAYUDCMops() {
		return getGetMopsFromSystemProp("PAYUDC");
	}

	public static List<MopTypeUI> getMOBIKWIKWLMops() {
		return getGetMopsFromSystemProp("MOBIKWIKWL");
	}

	public static List<MopTypeUI> getCITRUSNBMops() {
		return getGetMopsFromSystemProp("CITRUSNB");
	}

	public static List<MopTypeUI> getDIRECPAYNBMops() {
		return getGetMopsFromSystemProp("DIRECPAYNB");
	}

	public static List<MopTypeUI> getKOTAKNBMops() {
		return getGetMopsFromSystemProp("KOTAKNB");
	}

	public static List<MopTypeUI> getSBIBANKNBMops() {
		return getGetMopsFromSystemProp("SBINB");
	}

	public static List<MopTypeUI> getALLAHABADBANKNBMops() {
		return getGetMopsFromSystemProp("ALLAHABADBANKNB");
	}

	public static List<MopTypeUI> getVIJAYABANKNBMops() {
		return getGetMopsFromSystemProp("VIJAYABANKNB");
	}

	public static List<MopTypeUI> getAXISBANKNBMops() {
		return getGetMopsFromSystemProp("AXISBANKNB");
	}

	public static List<MopTypeUI> getICICIBANKNBMops() {
		return getGetMopsFromSystemProp("ICICIBANKNB");
	}

	public static List<MopTypeUI> getCORPORATIONBANKNBMops() {
		return getGetMopsFromSystemProp("CORPORATIONBANKNB");
	}

	public static List<MopTypeUI> getSOUTHINDIANBANKNBMops() {
		return getGetMopsFromSystemProp("SOUTHINDIANBANKNB");
	}

	public static List<MopTypeUI> getKARURVYSYABANKNBMops() {
		return getGetMopsFromSystemProp("KARURVYSYABANKNB");
	}

	public static List<MopTypeUI> getKARNATAKABANKNBMops() {
		return getGetMopsFromSystemProp("KARNATAKABANKNB");
	}

	public static List<MopTypeUI> getOLAMONEYWLMops() {
		return getGetMopsFromSystemProp("OLAMONEYWL");
	}

	public static List<MopTypeUI> getMIGSDCMops() {
		return getGetMopsFromSystemProp("MIGSDCMOP");
	}

	public static List<MopTypeUI> getMIGSCCMops() {
		return getGetMopsFromSystemProp("MIGSCCMOP");
	}

	public static List<MopTypeUI> getIDBIBANKDCMops() {
		return getGetMopsFromSystemProp("IDBIBANKDCMOP");
	}

	public static List<MopTypeUI> getIDBIBANKCCMops() {
		return getGetMopsFromSystemProp("IDBIBANKCCMOP");
	}

	public static List<MopTypeUI> getCAMSPAYCCMOPMops() {
		return getGetMopsFromSystemProp("CAMSPAYCCMOP");
	}

	public static List<MopTypeUI> getCAMSPAYDCMOPMops() {
		return getGetMopsFromSystemProp("CAMSPAYDCMOP");
	}

	public static List<MopTypeUI> getCAMSPAYNBMops() {
		return getGetMopsFromSystemProp("CAMSPAYNBMOP");
	}

	public static List<MopTypeUI> getCAMSPAYUPMops() {
		return getGetMopsFromSystemProp("CAMSPAYUPMOP");
	}

	public static List<MopTypeUI> getMATCHMOVEWLMops() {
		return getGetMopsFromSystemProp("MATCHMOVEWL");
	}

	public static List<MopTypeUI> getGetMopsFromSystemProp(String mopsList) {

		List<String> mopStringList = (List<String>) Helper.parseFields(PropertiesManager.propertiesMap.get(mopsList));

		List<MopTypeUI> mops = new ArrayList<MopTypeUI>();

		for (String mopCode : mopStringList) {
			MopTypeUI mop = getmop(mopCode);
			mops.add(mop);
		}
		return mops;
	}

	public static String getmopName(String mopCode) {
		MopTypeUI mopType = MopTypeUI.getmop(mopCode);
		if (mopType == null) {
			return "";
		}
		return mopType.getName();
	}

	public static String getmopCodeByUiName(String uiName) {
		MopTypeUI[] mopTypes = MopTypeUI.values();
		for (MopTypeUI mopType : mopTypes) {
			if (mopType.getUiName().equals(uiName)) {
				return mopType.getCode().toString();
			}
		}
		return null;
	}

	public static MopTypeUI getmop(String mopCode) {
		MopTypeUI mopObj = null;
		if (null != mopCode) {
			for (MopTypeUI mop : MopTypeUI.values()) {
				if (mopCode.equals(mop.getCode().toString())) {
					mopObj = mop;
					break;
				}
			}
		}
		return mopObj;
	}

	public static MopTypeUI getInstanceIgnoreCase(String name) {
		MopTypeUI[] mopTypes = MopTypeUI.values();
		for (MopTypeUI mopType : mopTypes) {
			if (mopType.getName().equalsIgnoreCase(name)) {
				return mopType;
			}
		}
		return null;
	}

	public static MopTypeUI getInstanceUsingStringValue(String value) {
		MopTypeUI[] mopTypes = MopTypeUI.values();
		for (MopTypeUI mopType : mopTypes) {
			if (mopType.toString().equals(value)) {
				return mopType;
			}

		}
		return null;
	}
}
