package com.pay10.payout.btsepay;

public class HeaderRequestPojo {
	String baseUrl;
	String unixTSInMS;
	String jsonData;
	String apikey;
	String nonce;
	String sign;

	public String getNonce() {
		return nonce;
	}

	public void setNonce(String nonce) {
		this.nonce = nonce;
	}

	public String getSign() {
		return sign;
	}

	public void setSign(String sign) {
		this.sign = sign;
	}

	public String getBaseUrl() {
		return baseUrl;
	}

	public void setBaseUrl(String baseUrl) {
		this.baseUrl = baseUrl;
	}

	public String getUnixTSInMS() {
		return unixTSInMS;
	}

	public void setUnixTSInMS(String unixTSInMS) {
		this.unixTSInMS = unixTSInMS;
	}

	public String getJsonData() {
		return jsonData;
	}

	public void setJsonData(String jsonData) {
		this.jsonData = jsonData;
	}

	public String getApikey() {
		return apikey;
	}

	public void setApikey(String apikey) {
		this.apikey = apikey;
	}

}
