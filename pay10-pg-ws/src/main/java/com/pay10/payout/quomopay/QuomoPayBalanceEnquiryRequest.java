package com.pay10.payout.quomopay;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class QuomoPayBalanceEnquiryRequest {

	public String merchantId;
	public String requestTime;
	public String signData;

	
	
	public String getMerchantId() {
		return merchantId;
	}

	public void setMerchantId(String merchantId) {
		this.merchantId = merchantId;
	}

	public String getRequestTime() {
		setRequestTime("");
		return requestTime;
	}

	public void setRequestTime(String requestTime) {

		LocalDateTime currentDateTime = LocalDateTime.now();
		// Define the desired date and time format
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		// Format the current date and time
		String formattedDateTime = currentDateTime.format(formatter);

		this.requestTime = formattedDateTime;
	}

	public String getSignData() {
		return signData;
	}

	public void setSignData(String signData) {
		this.signData = signData;
	}

	public QuomoPayBalanceEnquiryRequest(String merchantId, String requestTime, String signData) {
		super();
		this.merchantId = merchantId;
		this.requestTime = requestTime;
		this.signData = signData;
	}
	
	public QuomoPayBalanceEnquiryRequest() {
		
	}
	
	
	

}
