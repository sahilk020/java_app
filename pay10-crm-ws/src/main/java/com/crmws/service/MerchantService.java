package com.crmws.service;

import org.json.JSONObject;

import com.pay10.commons.exception.SystemException;

import java.text.ParseException;
import java.util.Map;

public interface MerchantService {
    //void createMerchant(JSONObject getData);

    void createMerchant(Map<String, Object> getData) throws ParseException,SystemException;

	String createMerchantMdrDetails(Map<String, Object> getData);
}
