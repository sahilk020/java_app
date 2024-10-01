package com.pay10.icici.netbanking;

import org.springframework.stereotype.Service;

@Service("iciciNBTransaction")
public class Transaction {

	// Merchant Details

	private String pid;
	private String response;
	private String prn1;

	// Transaction Details Data

	private String paid;
	private String bid;
	private String prn;
	private String amt;
	private String md;
	private String crn;
	private String ru;
	private String itc;
	private String cg;
	private String statFlg;

	private String qr;
	private String qs;
	private String fedId;

	private String payeeId;
	private String date;
	private String chksum;
	private String enddata;

	public String getPid() {
		return pid;
	}

	public void setPid(String pid) {
		this.pid = pid;
	}

	public String getResponse() {
		return response;
	}

	public void setResponse(String response) {
		this.response = response;
	}

	public String getPrn1() {
		return prn1;
	}

	public void setPrn1(String prn1) {
		this.prn1 = prn1;
	}

	public String getPaid() {
		return paid;
	}

	public void setPaid(String paid) {
		this.paid = paid;
	}

	public String getBid() {
		return bid;
	}

	public void setBid(String bid) {
		this.bid = bid;
	}

	public String getPrn() {
		return prn;
	}

	public void setPrn(String prn) {
		this.prn = prn;
	}

	public String getAmt() {
		return amt;
	}

	public void setAmt(String amt) {
		this.amt = amt;
	}

	public String getMd() {
		return md;
	}

	public void setMd(String md) {
		this.md = md;
	}

	public String getCrn() {
		return crn;
	}

	public void setCrn(String crn) {
		this.crn = crn;
	}

	public String getRu() {
		return ru;
	}

	public void setRu(String ru) {
		this.ru = ru;
	}

	public String getItc() {
		return itc;
	}

	public void setItc(String itc) {
		this.itc = itc;
	}

	public String getCg() {
		return cg;
	}

	public void setCg(String cg) {
		this.cg = cg;
	}

	public String getStatFlg() {
		return statFlg;
	}

	public void setStatFlg(String statFlg) {
		this.statFlg = statFlg;
	}

	public String getQr() {
		return qr;
	}

	public void setQr(String qr) {
		this.qr = qr;
	}

	public String getQs() {
		return qs;
	}

	public void setQs(String qs) {
		this.qs = qs;
	}

	public String getPayeeId() {
		return payeeId;
	}

	public void setPayeeId(String payeeId) {
		this.payeeId = payeeId;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getChksum() {
		return chksum;
	}

	public void setChksum(String chksum) {
		this.chksum = chksum;
	}

	public String getEnddata() {
		return enddata;
	}

	public void setEnddata(String enddata) {
		this.enddata = enddata;
	}

	public String getFedId() {
		return fedId;
	}

	public void setFedId(String fedId) {
		this.fedId = fedId;
	}

}
