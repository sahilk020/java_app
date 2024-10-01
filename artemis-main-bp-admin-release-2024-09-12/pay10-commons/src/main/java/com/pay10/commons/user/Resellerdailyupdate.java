package com.pay10.commons.user;

public class Resellerdailyupdate {

	private Long id;
	private String Reseller_payId;
	private String Merchant_payId;

	private String MOP;
	private String TransType;
	private String TransDate; // date of transaction it's always AddedOn - 1 day.
	private double TotalRefund; // total refund amount
	private Double TotalChargeback;
	private double amount; // sale - refund. it will be amount
	private double commisionamount; // commission amount as per formula
	private String Added_By;
	private String AddedOn; // date of add entry in collection
	private double saleamount; // total sale amount
	private String IsDeleted;
	private String SMA_payId;
	private String MA_payId;
	private String Agent_payId;
	private double smacommisionamount;
	private double macommisionamount;
	private double agentcommisionamount;
	private String currency;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getReseller_payId() {
		return Reseller_payId;
	}

	public void setReseller_payId(String reseller_payId) {
		Reseller_payId = reseller_payId;
	}

	public String getMOP() {
		return MOP;
	}

	public void setMOP(String mOP) {
		MOP = mOP;
	}

	public String getTransType() {
		return TransType;
	}

	public void setTransType(String transType) {
		TransType = transType;
	}

	public String getTransDate() {
		return TransDate;
	}

	public void setTransDate(String transDate) {
		TransDate = transDate;
	}

	public double getTotalRefund() {
		return TotalRefund;
	}

	public void setTotalRefund(double totalrefundamount) {
		TotalRefund = totalrefundamount;
	}

	public Double getTotalChargeback() {
		return TotalChargeback;
	}

	public void setTotalChargeback(Double totalChargeback) {
		TotalChargeback = totalChargeback;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public String getAdded_By() {
		return Added_By;
	}

	public void setAdded_By(String added_By) {
		Added_By = added_By;
	}

	public String getAddedOn() {
		return AddedOn;
	}

	public void setAddedOn(String addedOn) {
		AddedOn = addedOn;
	}

	public String getIsDeleted() {
		return IsDeleted;
	}

	public void setIsDeleted(String isDeleted) {
		IsDeleted = isDeleted;
	}

	public double getSaleamount() {
		return saleamount;
	}

	public void setSaleamount(double saleamount) {
		this.saleamount = saleamount;
	}

	public String getMerchant_payId() {
		return Merchant_payId;
	}

	public void setMerchant_payId(String merchant_payId) {
		Merchant_payId = merchant_payId;
	}

	public double getCommisionamount() {
		return commisionamount;
	}

	public void setCommisionamount(double commisionamount) {
		this.commisionamount = commisionamount;
	}

	public String getSMA_payId() {
		return SMA_payId;
	}

	public void setSMA_payId(String sMA_payId) {
		SMA_payId = sMA_payId;
	}

	public String getMA_payId() {
		return MA_payId;
	}

	public void setMA_payId(String mA_payId) {
		MA_payId = mA_payId;
	}

	public String getAgent_payId() {
		return Agent_payId;
	}

	public void setAgent_payId(String agent_payId) {
		Agent_payId = agent_payId;
	}

	public double getSmacommisionamount() {
		return smacommisionamount;
	}

	public void setSmacommisionamount(double smacommisionamount) {
		this.smacommisionamount = smacommisionamount;
	}

	public double getMacommisionamount() {
		return macommisionamount;
	}

	public void setMacommisionamount(double macommisionamount) {
		this.macommisionamount = macommisionamount;
	}

	public double getAgentcommisionamount() {
		return agentcommisionamount;
	}

	public void setAgentcommisionamount(double agentcommisionamount) {
		this.agentcommisionamount = agentcommisionamount;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	@Override
	public String toString() {
		return "Resellerdailyupdate [id=" + id + ", Reseller_payId=" + Reseller_payId + ", Merchant_payId="
				+ Merchant_payId + ", MOP=" + MOP + ", TransType=" + TransType + ", TransDate=" + TransDate
				+ ", TotalRefund=" + TotalRefund + ", TotalChargeback=" + TotalChargeback + ", amount=" + amount
				+ ", commisionamount=" + commisionamount + ", Added_By=" + Added_By + ", AddedOn=" + AddedOn
				+ ", saleamount=" + saleamount + ", IsDeleted=" + IsDeleted + ", SMA_payId=" + SMA_payId + ", MA_payId="
				+ MA_payId + ", Agent_payId=" + Agent_payId + "]";
	}


}
