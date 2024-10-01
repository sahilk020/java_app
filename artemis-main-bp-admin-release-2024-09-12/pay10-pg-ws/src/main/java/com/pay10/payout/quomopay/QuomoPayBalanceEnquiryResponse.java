package com.pay10.payout.quomopay;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;

public class QuomoPayBalanceEnquiryResponse {
	@JsonProperty("responseCode")
	public int responseCode;
	@JsonProperty("responseMessage")
	public String responseMessage;
	@JsonProperty("merchantId")
	public String merchantId;
	@JsonProperty("balanceAmount")
	public BigDecimal balanceAmount;
	@JsonProperty("UbalanceAmount")
	public BigDecimal UbalanceAmount;

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

	public BigDecimal getBalanceAmount() {
		return balanceAmount;
	}

	public void setBalanceAmount(BigDecimal balanceAmount) {
		this.balanceAmount = balanceAmount;
	}

	public BigDecimal getUbalanceAmount() {
		return UbalanceAmount;
	}

	public void setUbalanceAmount(BigDecimal ubalanceAmount) {
		UbalanceAmount = ubalanceAmount;
	}

	public QuomoPayBalanceEnquiryResponse(int responseCode, String responseMessage, String merchantId,
			BigDecimal balanceAmount, BigDecimal ubalanceAmount) {
		super();
		this.responseCode = responseCode;
		this.responseMessage = responseMessage;
		this.merchantId = merchantId;
		this.balanceAmount = balanceAmount;
		UbalanceAmount = ubalanceAmount;
	}
	public QuomoPayBalanceEnquiryResponse() {
		
	}
}
