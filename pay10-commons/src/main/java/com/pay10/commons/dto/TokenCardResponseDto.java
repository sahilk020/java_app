package com.pay10.commons.dto;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TokenCardResponseDto {
	
	@JsonProperty("status")
	private int status;
	
	@JsonProperty("msg")
	private String msg;
	
	@JsonProperty("user_cards")
	private Map<String,TokenCardDetailsDto> userCards;

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

	public Map<String, TokenCardDetailsDto> getUserCards() {
		return userCards;
	}

	public void setUserCards(Map<String, TokenCardDetailsDto> userCards) {
		this.userCards = userCards;
	}
	
	

}
