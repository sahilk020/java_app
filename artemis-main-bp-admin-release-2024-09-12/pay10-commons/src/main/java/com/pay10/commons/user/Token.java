package com.pay10.commons.user;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import com.pay10.commons.util.TokenStatus;

public class Token {
	
	private String id;
	private String payId;
	private String mopType;
	private String paymentType;
	
	@Enumerated(EnumType.STRING)
	private TokenStatus status;
	private String customerName;
	//private String email;
	private String cardMask;
	
	private String userCardMask;
	private String cardLabel;
	private String cardSaveParam;
	
	private String cardToken;
	private String networkToken;
	private String issuerToken;

	private String paymentsRegion;
	private String cardHolderType;

	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getPayId() {
		return payId;
	}

	public void setPayId(String payId) {
		this.payId = payId;
	}

	public String getMopType() {
		return mopType;
	}
	public void setMopType(String mopType) {
		this.mopType = mopType;
	}
	public String getPaymentType() {
		return paymentType;
	}
	public void setPaymentType(String paymentType) {
		this.paymentType = paymentType;
	}
	/*public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}*/
	public String getCardMask() {
		return cardMask;
	}
	public void setCardMask(String cardMask) {
		this.cardMask = cardMask;
	}


	public TokenStatus getStatus() {
		return status;
	}
	public void setStatus(TokenStatus status) {
		this.status = status;
	}
	public String getCustomerName() {
		return customerName;
	}
	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}
	
	public String getCardSaveParam() {
		return cardSaveParam;
	}
	public void setCardSaveParam(String cardSaveParam) {
		this.cardSaveParam = cardSaveParam;
	}
	public String getPaymentsRegion() {
		return paymentsRegion;
	}
	public void setPaymentsRegion(String paymentsRegion) {
		this.paymentsRegion = paymentsRegion;
	}
	public String getCardHolderType() {
		return cardHolderType;
	}
	public void setCardHolderType(String cardHolderType) {
		this.cardHolderType = cardHolderType;
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
	public String getUserCardMask() {
		return userCardMask;
	}
	public void setUserCardMask(String userCardMask) {
		this.userCardMask = userCardMask;
	}
	
	
	
	public String getCardLabel() {
		return cardLabel;
	}
	public void setCardLabel(String cardLabel) {
		this.cardLabel = cardLabel;
	}
	@Override
	public String toString() {
		return "Token [id=" + id + ", payId=" + payId + ", mopType=" + mopType + ", paymentType=" + paymentType
				+ ", status=" + status + ", customerName=" + customerName + ", cardMask=" + cardMask + ", userCardMask="
				+ userCardMask + ", cardLabel=" + cardLabel + ", cardSaveParam=" + cardSaveParam + ", cardToken="
				+ cardToken + ", networkToken=" + networkToken + ", issuerToken=" + issuerToken + ", paymentsRegion="
				+ paymentsRegion + ", cardHolderType=" + cardHolderType + "]";
	}
	
	
	
	
	
	
	
	
	
}// Token
