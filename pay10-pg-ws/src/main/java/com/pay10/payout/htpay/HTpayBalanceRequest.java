package com.pay10.payout.htpay;

public class HTpayBalanceRequest {
	public String opmhtid;
	public String random;
	public String sign;
	public String getOpmhtid() {
		return opmhtid;
	}
	public void setOpmhtid(String opmhtid) {
		this.opmhtid = opmhtid;
	}
	public String getRandom() {
		return random;
	}
	public void setRandom(String random) {
		this.random = random;
	}
	public String getSign() {
		return sign;
	}
	public void setSign(String sign) {
		this.sign = sign;
	}
	public HTpayBalanceRequest(String opmhtid, String random, String sign) {
		super();
		this.opmhtid = opmhtid;
		this.random = random;
		this.sign = sign;
	}
	
	
	
	
}
