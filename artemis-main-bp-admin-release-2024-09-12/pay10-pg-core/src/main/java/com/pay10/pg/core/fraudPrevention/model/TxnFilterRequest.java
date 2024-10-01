package com.pay10.pg.core.fraudPrevention.model;

public class TxnFilterRequest {

	private String payId;
	private String fromDate;
	private String toDate;
	private String currency;
	private String ipAddress;
	private String emailId;
	private String mobileNo;
	private String txnStatus;
	private String vpa;
	private double amount;
	private String mop;
	private String paymentType;

	public TxnFilterRequest() {
	}

	public TxnFilterRequest(String payId, String fromDate, String toDate, String currency, String ipAddress,
			String emailId, String mobileNo, String txnStatus, String vpa, double amount, String mop,
			String paymentType) {
		this.payId = payId;
		this.fromDate = fromDate;
		this.toDate = toDate;
		this.currency = currency;
		this.ipAddress = ipAddress;
		this.emailId = emailId;
		this.mobileNo = mobileNo;
		this.txnStatus = txnStatus;
		this.vpa = vpa;
		this.amount = amount;
		this.mop = mop;
		this.paymentType = paymentType;
	}

	public String getPayId() {
		return payId;
	}

	public void setPayId(String payId) {
		this.payId = payId;
	}

	public String getFromDate() {
		return fromDate;
	}

	public void setFromDate(String fromDate) {
		this.fromDate = fromDate;
	}

	public String getToDate() {
		return toDate;
	}

	public void setToDate(String toDate) {
		this.toDate = toDate;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public String getEmailId() {
		return emailId;
	}

	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}

	public String getMobileNo() {
		return mobileNo;
	}

	public void setMobileNo(String mobileNo) {
		this.mobileNo = mobileNo;
	}

	public String getTxnStatus() {
		return txnStatus;
	}

	public void setTxnStatus(String txnStatus) {
		this.txnStatus = txnStatus;
	}

	public String getVpa() {
		return vpa;
	}

	public void setVpa(String vpa) {
		this.vpa = vpa;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public String getMop() {
		return mop;
	}

	public void setMop(String mop) {
		this.mop = mop;
	}
	
	public String getPaymentType() {
		return paymentType;
	}

	public void setPaymentType(String paymentType) {
		this.paymentType = paymentType;
	}
}
