package com.crmws.service;





public interface TDRAndSurchargeCalculatorService {
	
	public String[] getTdrAndSurcharge(String payId, String paymentType,
			String acquirerType, String mopType, String transactionType,
			String currency, String amount,String date);

}
