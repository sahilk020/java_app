package com.crmws.dto;

public class AcquirerSwitchDto {
private String payId;
private  String merchantName;
private String mopType;
private String upDateBy;
private String status;
private String upDateOn;
private String acquirer;
private String paymentType;



public String getAcquirer() {
	return acquirer;
}
public void setAcquirer(String acquirer) {
	this.acquirer = acquirer;
}
public String getPaymentType() {
	return paymentType;
}
public void setPaymentType(String paymentType) {
	this.paymentType = paymentType;
}
public String getUpDateBy() {
	return upDateBy;
}
public void setUpDateBy(String upDateBy) {
	this.upDateBy = upDateBy;
}
public String getStatus() {
	return status;
}
public void setStatus(String status) {
	this.status = status;
}
public String getUpDateOn() {
	return upDateOn;
}
public void setUpDateOn(String upDateOn) {
	this.upDateOn = upDateOn;
}
public String getPayId() {
	return payId;
}
public void setPayId(String payId) {
	this.payId = payId;
}
public String getMerchantName() {
	return merchantName;
}
public void setMerchantName(String merchantName) {
	this.merchantName = merchantName;
}
public String getMopType() {
	return mopType;
}
public void setMopType(String mopType) {
	this.mopType = mopType;
}







}
