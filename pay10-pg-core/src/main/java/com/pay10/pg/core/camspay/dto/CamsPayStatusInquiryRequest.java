package com.pay10.pg.core.camspay.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class CamsPayStatusInquiryRequest {

	private String trxnno;
	private String orderno;
	private String merchantid;
	private String requeryKey;
	private String subbillerid;

	public String getTrxnno() {
		return trxnno;
	}

	public void setTrxnno(String trxnno) {
		this.trxnno = trxnno;
	}

	public String getOrderno() {
		return orderno;
	}

	public void setOrderno(String orderno) {
		this.orderno = orderno;
	}

	public String getMerchantid() {
		return merchantid;
	}

	public void setMerchantid(String merchantid) {
		this.merchantid = merchantid;
	}

	public String getRequeryKey() {
		return requeryKey;
	}

	public void setRequeryKey(String requeryKey) {
		this.requeryKey = requeryKey;
	}

	public String getSubbillerid() {
		return subbillerid;
	}

	public void setSubbillerid(String subbillerid) {
		this.subbillerid = subbillerid;
	}
}
