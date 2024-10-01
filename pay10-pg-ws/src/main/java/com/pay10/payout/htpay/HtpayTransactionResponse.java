package com.pay10.payout.htpay;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;


public class HtpayTransactionResponse {
	@JsonProperty("rtCode")
	public int rtCode; // 0:Success; Other:Failure

	@JsonProperty("amount")
	public BigDecimal amount;
	@JsonProperty("msg")
	public String msg;

	@JsonProperty("result")
	public HTPayResult result;

	public int getRtCode() {
		return rtCode;
	}

	public void setRtCode(int rtCode) {
		this.rtCode = rtCode;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public HTPayResult getResult() {
		return result;
	}

	public void setResult(HTPayResult result) {
		this.result = result;
	}

	public HtpayTransactionResponse(int rtCode, BigDecimal amount, String msg, HTPayResult result) {
		super();
		this.rtCode = rtCode;
		this.amount = amount;
		this.msg = msg;
		this.result = result;
	}
	public HtpayTransactionResponse() {
		
	}
	
	

}

class HTPayResult {
	@JsonProperty("pforderno")
	public String pforderno;
	@JsonProperty("payurl")
	public String payurl;
	public String getPforderno() {
		return pforderno;
	}
	public void setPforderno(String pforderno) {
		this.pforderno = pforderno;
	}
	public String getPayurl() {
		return payurl;
	}
	public void setPayurl(String payurl) {
		this.payurl = payurl;
	}
	public HTPayResult(String pforderno, String payurl) {
		super();
		this.pforderno = pforderno;
		this.payurl = payurl;
	}
	
	
	public HTPayResult() {
		
	}
}
