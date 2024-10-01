package com.pay10.payout.htpay;

import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonProperty;

public class HTPayBankListResponse {
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
	public ArrayList<BankListResult> getResult() {
		return this.result;
	}

	public void setResult(ArrayList<BankListResult> result) {
		this.result = result;
	}

	ArrayList<BankListResult> result;
}

class BankListResult {
	@JsonProperty("bankCode")
	public String getBankCode() {
		return this.bankCode;
	}

	public void setBankCode(String bankCode) {
		this.bankCode = bankCode;
	}

	String bankCode;

	@JsonProperty("bankName")
	public String getBankName() {
		return this.bankName;
	}

	public void setBankName(String bankName) {
		this.bankName = bankName;
	}

	String bankName;
}
