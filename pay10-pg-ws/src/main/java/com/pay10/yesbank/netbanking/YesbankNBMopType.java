package com.pay10.yesbank.netbanking;

import java.util.ArrayList;
import java.util.List;

import com.pay10.agreepay.AgreepayMopType;
import com.pay10.commons.util.Helper;
import com.pay10.commons.util.PropertiesManager;

public enum YesbankNBMopType {

    YES_BANK("Yes Bank", "1001", "YESN");

    private final String bankName;
    private final String code;
    private final String bankCode;

    private YesbankNBMopType(String bankName, String code, String bankCode) {
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

    public static YesbankNBMopType getInstance(String name) {
        YesbankNBMopType[] mopTypes = YesbankNBMopType.values();
        for (YesbankNBMopType mopType : mopTypes) {
            if (mopType.getBankName().equals(name)) {
                return mopType;
            }
        }
        return null;
    }

    public static List<YesbankNBMopType> getGetMopsFromSystemProp(String mopsList) {

        List<String> mopStringList = (List<String>) Helper.parseFields(PropertiesManager.propertiesMap.get(mopsList));

        List<YesbankNBMopType> mops = new ArrayList<YesbankNBMopType>();

        for (String mopCode : mopStringList) {
            YesbankNBMopType mop = getmop(mopCode);
            mops.add(mop);
        }
        return mops;
    }

    public static String getmopName(String mopCode) {
        YesbankNBMopType mopType = YesbankNBMopType.getmop(mopCode);
        if (mopType == null) {
            return "";
        }
        return mopType.getBankName();
    }

    public static String getBankCode(String code) {
        YesbankNBMopType mopType = YesbankNBMopType.getmop(code);
        if (mopType == null) {
            return "";
        }
        return mopType.getBankCode();
    }

    public static YesbankNBMopType getmop(String mopCode) {
        YesbankNBMopType mopObj = null;
        if (null != mopCode) {
            for (YesbankNBMopType mop : YesbankNBMopType.values()) {
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
            for (AgreepayMopType mop : AgreepayMopType.values()) {
                if (mopCode.equals(mop.getBankName().toString())) {
                    moptType = mop.getCode();
                    break;
                }
            }
        }
        return moptType;
    }

    public static YesbankNBMopType getInstanceIgnoreCase(String name) {
        YesbankNBMopType[] mopTypes = YesbankNBMopType.values();
        for (YesbankNBMopType mopType : mopTypes) {
            if (mopType.getBankName().equalsIgnoreCase(name)) {
                return mopType;
            }
        }
        return null;
    }
}
