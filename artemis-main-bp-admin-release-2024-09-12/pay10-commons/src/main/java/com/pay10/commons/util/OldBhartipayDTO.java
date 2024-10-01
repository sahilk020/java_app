package com.pay10.commons.util;

public class OldBhartipayDTO {
	private String txnId;
	private String oid;
	private String txnType;
	private String amount;
	private String orderId;
	private String customerName;
	private String payId;
	private String mopType;
	private String paymentType;
	private String currencyCode;
	private String status;
	private String createDate;
	private String pgrefnum;
	private String acquirerType;
	private String surchargeAmount;
	public String getTxnId() {
		return txnId;
	}
	public void setTxnId(String txnId) {
		this.txnId = txnId;
	}
	public String getOid() {
		return oid;
	}
	public void setOid(String oid) {
		this.oid = oid;
	}
	public String getTxnType() {
		return txnType;
	}
	public void setTxnType(String txnType) {
		this.txnType = txnType;
	}
	public String getAmount() {
		return amount;
	}
	public void setAmount(String amount) {
		this.amount = amount;
	}
	public String getOrderId() {
		return orderId;
	}
	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}
	public String getCustomerName() {
		return customerName;
	}
	public void setCustomerName(String customerName) {
		this.customerName = customerName;
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
	public String getCurrencyCode() {
		return currencyCode;
	}
	public void setCurrencyCode(String currencyCode) {
		this.currencyCode = currencyCode;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getCreateDate() {
		return createDate;
	}
	public void setCreateDate(String createDate) {
		this.createDate = createDate;
	}
	public String getPgrefnum() {
		return pgrefnum;
	}
	public void setPgrefnum(String pgrefnum) {
		this.pgrefnum = pgrefnum;
	}
	public String getAcquirerType() {
		return acquirerType;
	}
	public void setAcquirerType(String acquirerType) {
		this.acquirerType = acquirerType;
	}
	public String getSurchargeAmount() {
		return surchargeAmount;
	}
	public void setSurchargeAmount(String surchargeAmount) {
		this.surchargeAmount = surchargeAmount;
	}
	
	public Object[] myCsvMethodDownloadPaymentsReport() {
		Object[] objectArray = new Object[15];

		objectArray[0] = txnId;
		objectArray[1] = oid;
		objectArray[2] = txnType;
		objectArray[3] = amount;
		objectArray[4] = orderId;
		objectArray[5] = customerName;
		objectArray[6] = payId;
		objectArray[7] = mopType;
		objectArray[8] = paymentType;		
		objectArray[9] = currencyCode;
		objectArray[10] = status;
		objectArray[11] = createDate;
		objectArray[12] = pgrefnum;
		objectArray[13] = acquirerType;
		objectArray[14] = surchargeAmount;
		
		return objectArray;

	}

	
}
