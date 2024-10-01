package com.crmws.service;

import org.json.JSONObject;

public interface ResellerService {
	
	public boolean isMappedReseller(String payId);
	
	public JSONObject getTotalCount(String dateFrom, String dateTo, String resellerId, String merchantId,
			String paymentType);

	public boolean isMappedUser(String payId);


}
