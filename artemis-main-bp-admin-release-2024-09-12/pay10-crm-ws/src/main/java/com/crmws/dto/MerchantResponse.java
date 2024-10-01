package com.crmws.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;


@JsonIgnoreProperties(ignoreUnknown = true)
public class MerchantResponse {
	
	@JsonProperty("order_id")
	private String orderId;
	
	@JsonProperty("short_url")
	private String shortUrl;
	
	@JsonProperty("expiry")
	private String expiry;

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public String getShortUrl() {
		return shortUrl;
	}

	public void setShortUrl(String shortUrl) {
		this.shortUrl = shortUrl;
	}
	
	public String getExpiry() {
		return expiry;
	}

	public void setExpiry(String expiry) {
		this.expiry = expiry;
	}

	public MerchantResponse() {
		super();
	}

	public MerchantResponse(String orderId, String shortUrl,String expiry) {
		super();
		this.orderId = orderId;
		this.shortUrl = shortUrl;
		this.expiry = expiry;
	}
	
	

}
