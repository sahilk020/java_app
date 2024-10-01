package com.pay10.pg.core.camspay.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class CamsPayId {

	private String merchantid;
	private String subbillerid;
	private String paytype;
	private String addinfo1;
	private String addinfo2;
	private String addinfo3;
	private String addinfo4;
	private String addinfo5;

	public String getMerchantid() {
		return merchantid;
	}

	public void setMerchantid(String merchantid) {
		this.merchantid = merchantid;
	}

	public String getSubbillerid() {
		return subbillerid;
	}

	public void setSubbillerid(String subbillerid) {
		this.subbillerid = subbillerid;
	}

	public String getPaytype() {
		return paytype;
	}

	public void setPaytype(String paytype) {
		this.paytype = paytype;
	}

	public String getAddinfo1() {
		return addinfo1;
	}

	public void setAddinfo1(String addinfo1) {
		this.addinfo1 = addinfo1;
	}

	public String getAddinfo2() {
		return addinfo2;
	}

	public void setAddinfo2(String addinfo2) {
		this.addinfo2 = addinfo2;
	}

	public String getAddinfo3() {
		return addinfo3;
	}

	public void setAddinfo3(String addinfo3) {
		this.addinfo3 = addinfo3;
	}

	public String getAddinfo4() {
		return addinfo4;
	}

	public void setAddinfo4(String addinfo4) {
		this.addinfo4 = addinfo4;
	}

	public String getAddinfo5() {
		return addinfo5;
	}

	public void setAddinfo5(String addinfo5) {
		this.addinfo5 = addinfo5;
	}
}
