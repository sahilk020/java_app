package com.pay10.federalNB;

import java.util.ArrayList;
import java.util.List;


import com.pay10.commons.util.Helper;
import com.pay10.commons.util.PropertiesManager;

public enum FederalBankNBMopType
{
	FEDERAL_BANK("Federal Bank", "1027", "FEDN");
	
	private final String bankName;
	private final String code;
	private final String bankCode;
	
	private FederalBankNBMopType(String bankName, String code, String bankCode)
	{
		this.bankName = bankName;
		this.code = code;
		this.bankCode = bankCode;
	}

	public String getBankName()
	{
		return bankName;
	}

	public String getCode()
	{
		return code;
	}

	public String getBankCode()
	{
		return bankCode;
	}

	public static FederalBankNBMopType getInstance(String name)
	{
		FederalBankNBMopType[] mopTypes = FederalBankNBMopType.values();
		for(FederalBankNBMopType mopType : mopTypes){
			if(mopType.getBankName().equals(name)){
				return mopType;
			}
		}		
		return null;
	}
	
	public static List<FederalBankNBMopType> getGetMopsFromSystemProp(String mopsList)
	{
		List<String> mopStringList= (List<String>) Helper.parseFields(PropertiesManager.propertiesMap.get(mopsList));
		List<FederalBankNBMopType> mops = new ArrayList<FederalBankNBMopType>();

		for(String mopCode:mopStringList){
			FederalBankNBMopType mop = getmop(mopCode);
			mops.add(mop);
		}

		return mops;
	}

	public static String getmopName(String mopCode)
	{
		FederalBankNBMopType mopType = FederalBankNBMopType.getmop(mopCode);		
		if(mopType == null) {
			return "";
		}
		return mopType.getBankName();
	}

	public static String getBankCode(String code)
	{
		FederalBankNBMopType mopType = FederalBankNBMopType.getmop(code);		
		if(mopType == null) {
			return "";
		}

		return mopType.getBankCode();
	}

	public static FederalBankNBMopType getmop(String mopCode)
	{
		FederalBankNBMopType mopObj = null;
		if(null!=mopCode){
			for(FederalBankNBMopType mop:FederalBankNBMopType.values()){
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
			for (FederalBankNBMopType mop : FederalBankNBMopType.values()) {
				if (mopCode.equals(mop.getBankName().toString())) {
					moptType = mop.getCode();
					break;
				}
			}
		}
		return moptType;
	}
	
	public static FederalBankNBMopType getInstanceIgnoreCase(String name){
		FederalBankNBMopType[] mopTypes = FederalBankNBMopType.values();
		for(FederalBankNBMopType mopType : mopTypes){
			if(mopType.getBankName().equalsIgnoreCase(name)){
				return mopType;
			}
		}		
		return null;
	}

}
