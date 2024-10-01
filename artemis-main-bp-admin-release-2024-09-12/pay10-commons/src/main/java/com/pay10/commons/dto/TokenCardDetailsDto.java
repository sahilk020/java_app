package com.pay10.commons.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TokenCardDetailsDto {
	
	@JsonProperty("one_click_status")
	private String oneClickStatus;
	
	@JsonProperty("one_click_flow")
	private String oneClickFlow;
	
	@JsonProperty("card_type")
	private String cardType;
	
	@JsonProperty("expiry_year")
	private String expYear;
	
	@JsonProperty("network_token")
	private TokenDto networkToken;
	
	@JsonProperty("expiry_month")
	private String expMonth;
	
	@JsonProperty("card_mode")
	private String cardMode;
	
	@JsonProperty("is_expired")
	private int expired;
	
	@JsonProperty("card_no")
	private String cardNo;
	
	@JsonProperty("one_click_card_alias")
	private String oneClickCardAlias;
	
	@JsonProperty("card_token")
	private String cardToken;
	
	@JsonProperty("card_name")
	private String cardName;
	
	@JsonProperty("name_on_card")
	private String nameOnCard;
	
	@JsonProperty("card_brand")
	private String cardBrand;
	
	@JsonProperty("card_bin")
	private String cardBin;
	
	@JsonProperty("isDomestic")
	private String domestic;
	
	@JsonProperty("card_cvv")
	private String cvv;
	
	@JsonProperty("PAR")
	private String par;
	
	
	
	@JsonProperty("issuer_token")
	private TokenDto issueToken;



	public String getOneClickStatus() {
		return oneClickStatus;
	}



	public void setOneClickStatus(String oneClickStatus) {
		this.oneClickStatus = oneClickStatus;
	}



	public String getOneClickFlow() {
		return oneClickFlow;
	}



	public void setOneClickFlow(String oneClickFlow) {
		this.oneClickFlow = oneClickFlow;
	}



	public String getCardType() {
		return cardType;
	}



	public void setCardType(String cardType) {
		this.cardType = cardType;
	}



	public String getExpYear() {
		return expYear;
	}



	public void setExpYear(String expYear) {
		this.expYear = expYear;
	}



	public TokenDto getNetworkToken() {
		return networkToken;
	}



	public void setNetworkToken(TokenDto networkToken) {
		this.networkToken = networkToken;
	}



	public String getExpMonth() {
		return expMonth;
	}



	public void setExpMonth(String expMonth) {
		this.expMonth = expMonth;
	}



	public String getCardMode() {
		return cardMode;
	}



	public void setCardMode(String cardMode) {
		this.cardMode = cardMode;
	}



	public int getExpired() {
		return expired;
	}



	public void setExpired(int expired) {
		this.expired = expired;
	}



	public String getCardNo() {
		return cardNo;
	}



	public void setCardNo(String cardNo) {
		this.cardNo = cardNo;
	}



	public String getOneClickCardAlias() {
		return oneClickCardAlias;
	}



	public void setOneClickCardAlias(String oneClickCardAlias) {
		this.oneClickCardAlias = oneClickCardAlias;
	}



	public String getCardToken() {
		return cardToken;
	}



	public void setCardToken(String cardToken) {
		this.cardToken = cardToken;
	}



	public String getCardName() {
		return cardName;
	}



	public void setCardName(String cardName) {
		this.cardName = cardName;
	}



	public String getNameOnCard() {
		return nameOnCard;
	}



	public void setNameOnCard(String nameOnCard) {
		this.nameOnCard = nameOnCard;
	}



	public String getCardBrand() {
		return cardBrand;
	}



	public void setCardBrand(String cardBrand) {
		this.cardBrand = cardBrand;
	}



	public String getCardBin() {
		return cardBin;
	}



	public void setCardBin(String cardBin) {
		this.cardBin = cardBin;
	}



	public String getDomestic() {
		return domestic;
	}


	public void setDomestic(String domestic) {
		this.domestic = domestic;
	}


	public String getCvv() {
		return cvv;
	}


	public void setCvv(String cvv) {
		this.cvv = cvv;
	}


	public String getPar() {
		return par;
	}


	public void setPar(String par) {
		this.par = par;
	}


	public TokenDto getIssueToken() {
		return issueToken;
	}


	public void setIssueToken(TokenDto issueToken) {
		this.issueToken = issueToken;
	}
	
	
	
	
	
	
	
}
