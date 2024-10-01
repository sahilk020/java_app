package com.pay10.payout.btsepay;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SendRequestBodyPojo {

	@JsonProperty("coin")
	public String getCoin() {
		return this.coin;
	}

	public void setCoin(String coin) {
		this.coin = coin;
	}

	String coin;

	@JsonProperty("protocol")
	public String getProtocol() {
		return this.protocol;
	}

	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}

	String protocol;

	@JsonProperty("feeType")
	public int getFeeType() {
		return this.feeType;
	}

	public void setFeeType(int feeType) {
		this.feeType = feeType;
	}

	int feeType;

	@JsonProperty("amount")
	public double getAmount() {
		return this.amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	double amount;

	@JsonProperty("address")
	public String getAddress() {
		return this.address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	String address;

	@JsonProperty("clientOrderId")
	public String getClientOrderId() {
		return this.clientOrderId;
	}

	public void setClientOrderId(String clientOrderId) {
		this.clientOrderId = clientOrderId;
	}

	String clientOrderId;

	@JsonProperty("referenceId")
	public String getReferenceId() {
		return this.referenceId;
	}

	public void setReferenceId(String referenceId) {
		this.referenceId = referenceId;
	}

	String referenceId;

	@JsonProperty("extra")
	public String getExtra() {
		return this.extra;
	}

	public void setExtra(String extra) {
		this.extra = extra;
	}

	String extra;
}
