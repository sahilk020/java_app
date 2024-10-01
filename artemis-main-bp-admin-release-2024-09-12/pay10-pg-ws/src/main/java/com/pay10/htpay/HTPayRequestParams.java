package com.pay10.htpay;

public class HTPayRequestParams {

	private String amount;
	private String clientip;
	private String currency;
	private String mhtorderno;
	private String mhtuserid;// =user001
	private String notifyurl;
	private String opmhtid;
	private String payername;
	private String paytype;
	private String random;
	private String returnurl;
	private String accno;
	private String payerphone;

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public String getClientip() {
		return clientip;
	}

	public void setClientip(String clientip) {
		this.clientip = clientip;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public String getMhtorderno() {
		return mhtorderno;
	}

	public void setMhtorderno(String mhtorderno) {
		this.mhtorderno = mhtorderno;
	}

	public String getMhtuserid() {
		return mhtuserid;
	}

	public void setMhtuserid(String mhtuserid) {
		this.mhtuserid = mhtuserid;
	}

	public String getNotifyurl() {
		return notifyurl;
	}

	public void setNotifyurl(String notifyurl) {
		this.notifyurl = notifyurl;
	}

	public String getPayername() {
		return payername;
	}

	public void setPayername(String payername) {
		this.payername = payername;
	}

	public String getPaytype() {
		return paytype;
	}

	public void setPaytype(String paytype) {
		this.paytype = paytype;
	}

	public String getRandom() {
		return random;
	}

	public void setRandom(String random) {
		this.random = random;
	}

	public String getReturnurl() {
		return returnurl;
	}

	public void setReturnurl(String returnurl) {
		this.returnurl = returnurl;
	}

	public String getOpmhtid() {
		return opmhtid;
	}

	public void setOpmhtid(String opmhtid) {
		this.opmhtid = opmhtid;
	}

	public String getAccno() {
		return accno;
	}

	public void setAccno(String accno) {
		this.accno = accno;
	}

	public String getPayerphone() {
		return payerphone;
	}

	public void setPayerphone(String payerphone) {
		this.payerphone = payerphone;
	}

	@Override
	public String toString() {
		return "HTPayRequestParams [amount=" + amount + ", clientip=" + clientip + ", currency=" + currency
				+ ", mhtorderno=" + mhtorderno + ", mhtuserid=" + mhtuserid + ", notifyurl=" + notifyurl + ", opmhtid="
				+ opmhtid + ", payername=" + payername + ", paytype=" + paytype + ", random=" + random + ", returnurl="
				+ returnurl + ", accno=" + accno + ", payerphone=" + payerphone + "]";
	}

}
