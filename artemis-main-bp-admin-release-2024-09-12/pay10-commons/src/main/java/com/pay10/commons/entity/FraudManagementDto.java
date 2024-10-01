package com.pay10.commons.entity;

public class FraudManagementDto {
	
	private String payId;
	private String dateIndex;
	private String Volume;
	private String count;
	private String businessName;
	private String merchantByVolume;
	private String merchantByCount;
	private String frmBreachValue;
	private String leastPerformerTSR;
	//private String payId;
	
	public String getMerchantByVolume() {
		return merchantByVolume;
	}
	public String getFrmBreachValue() {
		return frmBreachValue;
	}
	public void setFrmBreachValue(String frmBreachValue) {
		this.frmBreachValue = frmBreachValue;
	}
	public String getLeastPerformerTSR() {
		return leastPerformerTSR;
	}
	public void setLeastPerformerTSR(String leastPerformerTSR) {
		this.leastPerformerTSR = leastPerformerTSR;
	}
	public void setMerchantByVolume(String merchantByVolume) {
		this.merchantByVolume = merchantByVolume;
	}
	public String getMerchantByCount() {
		return merchantByCount;
	}
	public void setMerchantByCount(String merchantByCount) {
		this.merchantByCount = merchantByCount;
	}
	public String getPayId() {
		return payId;
	}
	public void setPayId(String payId) {
		this.payId = payId;
	}
	public String getDateIndex() {
		return dateIndex;
	}
	public void setDateIndex(String dateIndex) {
		this.dateIndex = dateIndex;
	}
	public String getVolume() {
		return Volume;
	}
	public void setVolume(String volume) {
		Volume = volume;
	}
	public String getCount() {
		return count;
	}
	public void setCount(String count) {
		this.count = count;
	}
	public String getBusinessName() {
		return businessName;
	}
	public void setBusinessName(String businessName) {
		this.businessName = businessName;
	}

}
