package com.pay10.payout.quomopay;

import com.fasterxml.jackson.annotation.JsonProperty;


public class QuomoPayTransferResponse {

	@JsonProperty("responseCode")

	public int responseCode;
	@JsonProperty("responseMessage")

	public String responseMessage;
	@JsonProperty("merchantId")
	public String merchantId;
	@JsonProperty("merchantTransactionId")
	public String merchantTransactionId;
	public int getResponseCode() {
		return responseCode;
	}
	public void setResponseCode(int responseCode) {
		this.responseCode = responseCode;
	}
	public String getResponseMessage() {
		return responseMessage;
	}
	public void setResponseMessage(String responseMessage) {
		this.responseMessage = responseMessage;
	}
	public String getMerchantId() {
		return merchantId;
	}
	public void setMerchantId(String merchantId) {
		this.merchantId = merchantId;
	}
	public String getMerchantTransactionId() {
		return merchantTransactionId;
	}
	public void setMerchantTransactionId(String merchantTransactionId) {
		this.merchantTransactionId = merchantTransactionId;
	}
	public QuomoPayTransferResponse(int responseCode, String responseMessage, String merchantId,
			String merchantTransactionId) {
		super();
		this.responseCode = responseCode;
		this.responseMessage = responseMessage;
		this.merchantId = merchantId;
		this.merchantTransactionId = merchantTransactionId;
	}
	
	public QuomoPayTransferResponse() {
		
	}

}
