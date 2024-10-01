package com.pay10.commons.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class WebhookRequest {

	@JsonProperty("pay_id")
	private String payId;

	@JsonProperty("pay_in_url")
	private String payInUrl;

	@JsonProperty("pay_out_url")
	private String payOutUrl;

	@JsonProperty("pay_in_active")
	private boolean payInActive;

	@JsonProperty("pay_out_active")
	private boolean payOutActive;

	@JsonProperty("id")
	private String id;


	@JsonProperty("pay_in_id")
	private String payInId;

	@JsonProperty("pay_out_id")
	private String payOutId;
	
	@JsonProperty("active")
	private boolean active;
	
	

	public String getPayInId() {
		return payInId;
	}

	public void setPayInId(String payInId) {
		this.payInId = payInId;
	}

	public String getPayOutId() {
		return payOutId;
	}

	public void setPayOutId(String payOutId) {
		this.payOutId = payOutId;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getPayId() {
		return payId;
	}

	public void setPayId(String payId) {
		this.payId = payId;
	}

	public String getPayInUrl() {
		return payInUrl;
	}

	public void setPayInUrl(String payInUrl) {
		this.payInUrl = payInUrl;
	}

	public String getPayOutUrl() {
		return payOutUrl;
	}

	public void setPayOutUrl(String payOutUrl) {
		this.payOutUrl = payOutUrl;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public WebhookRequest() {
	}

	public boolean isPayInActive() {
		return payInActive;
	}

	public void setPayInActive(boolean payInActive) {
		this.payInActive = payInActive;
	}

	public boolean isPayOutActive() {
		return payOutActive;
	}

	public void setPayOutActive(boolean payOutActive) {
		this.payOutActive = payOutActive;
	}

	public WebhookRequest(String payId, String payInUrl, String payOutUrl, boolean payInActive, boolean payOutActive,
			boolean active) {
		super();
		this.payId = payId;
		this.payInUrl = payInUrl;
		this.payOutUrl = payOutUrl;
		this.payInActive = payInActive;
		this.payOutActive = payOutActive;
		this.active = active;
	}

	@Override
	public String toString() {
		return "WebhookRequest [payId=" + payId + ", payInUrl=" + payInUrl + ", payOutUrl=" + payOutUrl
				+ ", payInActive=" + payInActive + ", payOutActive=" + payOutActive + ", active=" + active + "]";
	}

}
