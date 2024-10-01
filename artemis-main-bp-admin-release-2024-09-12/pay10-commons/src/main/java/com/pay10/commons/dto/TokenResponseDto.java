package com.pay10.commons.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TokenResponseDto {
	
	@JsonProperty("status")
	private int status;
	
	@JsonProperty("msg")
	private String msg;
	
	@JsonProperty("card_number")
	private String cardNumber;
	
	@JsonProperty("cardToken")
	private String cardToken;
	
	@JsonProperty("network_token")
	private String networkToken;
	
	@JsonProperty("issuer_token")
	private String issuerToken;

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public String getCardToken() {
		return cardToken;
	}

	public void setCardToken(String cardToken) {
		this.cardToken = cardToken;
	}

	public String getNetworkToken() {
		return networkToken;
	}

	public void setNetworkToken(String networkToken) {
		this.networkToken = networkToken;
	}

	public String getIssuerToken() {
		return issuerToken;
	}

	public void setIssuerToken(String issuerToken) {
		this.issuerToken = issuerToken;
	}

	public String getCardNumber() {
		return cardNumber;
	}

	public void setCardNumber(String cardNumber) {
		this.cardNumber = cardNumber;
	}

	@Override
	public String toString() {
		return "TokenResponseDto [status=" + status + ", msg=" + msg + ", cardNumber=" + cardNumber + ", cardToken="
				+ cardToken + ", networkToken=" + networkToken + ", issuerToken=" + issuerToken + "]";
	}

	public TokenResponseDto(int status, String msg, String cardNumber, String cardToken, String networkToken,
			String issuerToken) {
		this.status = status;
		this.msg = msg;
		this.cardNumber = cardNumber;
		this.cardToken = cardToken;
		this.networkToken = networkToken;
		this.issuerToken = issuerToken;
	}

	public TokenResponseDto() {
	}

	
	
	
	
	
	
	
	
	
}
