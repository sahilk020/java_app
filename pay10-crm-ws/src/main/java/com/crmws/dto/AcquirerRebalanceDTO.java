package com.crmws.dto;

public class AcquirerRebalanceDTO {
	private String id;
	private String payId;
	private String acquirerName;
	private String acquirerCode;
	private String finalBalance;
	private String currency;

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

	public String getAcquirerName() {
		return acquirerName;
	}

	public void setAcquirerName(String acquirerName) {
		this.acquirerName = acquirerName;
	}

	public String getAcquirerCode() {
		return acquirerCode;
	}

	public void setAcquirerCode(String acquirerCode) {
		this.acquirerCode = acquirerCode;
	}

	public String getFinalBalance() {
		return finalBalance;
	}

	public void setFinalBalance(String finalBalance) {
		this.finalBalance = finalBalance;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	@Override
	public String toString() {
		return "AcquirerRebalanceDTO [id=" + id + ", payId=" + payId + ", acquirerName=" + acquirerName
				+ ", acquirerCode=" + acquirerCode + ", finalBalance=" + finalBalance + ", currency=" + currency + "]";
	}

}
