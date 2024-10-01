package com.pay10.commons.user;

import org.springframework.stereotype.Service;

@Service
public class ResponseObject {

	private String responseCode;
	private String responseMessage;
	private String accountValidationID;
	private String email;
	private String name;

	private String payId;

	private String salt;

	private String merchantHostedEncryptionKey;

	private String requestUrl;

	public ResponseObject() {

	}

	public String getPayId() {
		return payId;
	}

	public void setPayId(String payId) {
		this.payId = payId;
	}

	public String getSalt() {
		return salt;
	}

	public void setSalt(String salt) {
		this.salt = salt;
	}

	public String getMerchantHostedEncryptionKey() {
		return merchantHostedEncryptionKey;
	}

	public void setMerchantHostedEncryptionKey(String merchantHostedEncryptionKey) {
		this.merchantHostedEncryptionKey = merchantHostedEncryptionKey;
	}

	public String getRequestUrl() {
		return requestUrl;
	}

	public void setRequestUrl(String requestUrl) {
		this.requestUrl = requestUrl;
	}

	public String getResponseCode() {
		return responseCode;
	}

	public void setResponseCode(String responseCode) {
		this.responseCode = responseCode;
	}

	public String getResponseMessage() {
		return responseMessage;
	}

	public void setResponseMessage(String responseMessage) {
		this.responseMessage = responseMessage;
	}

	public String getAccountValidationID() {
		return accountValidationID;
	}

	public void setAccountValidationID(String accountValidationID) {
		this.accountValidationID = accountValidationID;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "ResponseObject [responseCode=" + responseCode + ", responseMessage=" + responseMessage
				+ ", accountValidationID=" + accountValidationID + ", email=" + email + ", name=" + name + "]";
	}
}
