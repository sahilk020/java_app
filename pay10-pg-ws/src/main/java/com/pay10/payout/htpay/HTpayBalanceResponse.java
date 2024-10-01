package com.pay10.payout.htpay;

import java.math.BigDecimal;
import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonProperty;

public class HTpayBalanceResponse {
	@JsonProperty("rtCode")
	public int getRtCode() {
		return this.rtCode;
	}

	public void setRtCode(int rtCode) {
		this.rtCode = rtCode;
	}

	int rtCode;

	@JsonProperty("msg")
	public String getMsg() {
		return this.msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	String msg;

	@JsonProperty("result")
	public ArrayList<Result> getResult() {
		return this.result;
	}

	public void setResult(ArrayList<Result> result) {
		this.result = result;
	}

	ArrayList<Result> result;
}

class Result {
	@JsonProperty("balanceavailable")
	public BigDecimal getBalanceavailable() {
		return this.balanceavailable;
	}

	public void setBalanceavailable(BigDecimal balanceavailable) {
		this.balanceavailable = balanceavailable;
	}

	BigDecimal balanceavailable;

	@JsonProperty("balancereal")
	public double getBalancereal() {
		return this.balancereal;
	}

	public void setBalancereal(double balancereal) {
		this.balancereal = balancereal;
	}

	double balancereal;

	@JsonProperty("currency")
	public String getCurrency() {
		return this.currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	String currency;
}
