package com.crmws.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;


@JsonIgnoreProperties(ignoreUnknown = true)
public class MerchantRequest {
	
	@JsonProperty("pay_id")
	private String payId;
	
	@JsonProperty("order_id")
	private String orderId;
	
	@JsonProperty("amount")
	private String amount;
	
	@JsonProperty("name")
	private String custName;
	
	@JsonProperty("email")
	private String custEmail;
	
	@JsonProperty("phone")
	private String custPhone;
	
	@JsonProperty("address")
	private String address;
	
	@JsonProperty("expires_day")
	private String expiresDay;
	
	@JsonProperty("expires_hour")
	private String expiresHour;
	
	@JsonProperty("return_url")
	private String returnUrl;

	public String getPayId() {
		return payId;
	}

	public void setPayId(String payId) {
		this.payId = payId;
	}

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public String getCustName() {
		return custName;
	}

	public void setCustName(String custName) {
		this.custName = custName;
	}

	public String getCustEmail() {
		return custEmail;
	}

	public void setCustEmail(String custEmail) {
		this.custEmail = custEmail;
	}

	public String getCustPhone() {
		return custPhone;
	}

	public void setCustPhone(String custPhone) {
		this.custPhone = custPhone;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getReturnUrl() {
		return returnUrl;
	}

	public void setReturnUrl(String returnUrl) {
		this.returnUrl = returnUrl;
	}

	public String getExpiresDay() {
		return expiresDay;
	}

	public void setExpiresDay(String expiresDay) {
		this.expiresDay = expiresDay;
	}

	public String getExpiresHour() {
		return expiresHour;
	}

	public void setExpiresHour(String expiresHour) {
		this.expiresHour = expiresHour;
	}

}
