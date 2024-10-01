package com.pay10.crm.mongoReports;

public class PGPOReportDTO {
	private String pgRefNum;
	private String orderId;
	private String acquireType;
	private String Customerphone;
	private String txnType;
	private String mopType;
	private String payInAmount;
	private String payOutAmount;
	private String status;
	private String dateFrom;
	private String ipaddress;
	private String payId;
	private String merchantName;
	private String CustomerName;
	private String currencyCode;

	public String getPgRefNum() {
		return pgRefNum;
	}

	public void setPgRefNum(String pgRefNum) {
		this.pgRefNum = pgRefNum;
	}

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public String getAcquireType() {
		return acquireType;
	}

	public void setAcquireType(String acquireType) {
		this.acquireType = acquireType;
	}

	public String getCustomerphone() {
		return Customerphone;
	}

	public void setCustomerphone(String customerphone) {
		Customerphone = customerphone;
	}

	public String getTxnType() {
		return txnType;
	}

	public void setTxnType(String txnType) {
		this.txnType = txnType;
	}

	public String getMopType() {
		return mopType;
	}

	public void setMopType(String mopType) {
		this.mopType = mopType;
	}

	public String getPayInAmount() {
		return payInAmount;
	}

	public void setPayInAmount(String payInAmount) {
		this.payInAmount = payInAmount;
	}

	public String getPayOutAmount() {
		return payOutAmount;
	}

	public void setPayOutAmount(String payOutAmount) {
		this.payOutAmount = payOutAmount;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getDateFrom() {
		return dateFrom;
	}

	public void setDateFrom(String dateFrom) {
		this.dateFrom = dateFrom;
	}

	public String getIpaddress() {
		return ipaddress;
	}

	public void setIpaddress(String ipaddress) {
		this.ipaddress = ipaddress;
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

	public String getCustomerName() {
		return CustomerName;
	}

	public void setCustomerName(String customerName) {
		CustomerName = customerName;
	}

	public String getCurrencyCode() {
		return currencyCode;
	}

	public void setCurrencyCode(String currencyCode) {
		this.currencyCode = currencyCode;
	}

}
