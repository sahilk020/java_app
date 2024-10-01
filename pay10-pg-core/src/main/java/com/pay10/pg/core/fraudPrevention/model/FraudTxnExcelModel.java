package com.pay10.pg.core.fraudPrevention.model;

public class FraudTxnExcelModel {

	private String txnId;
	private String pgRefNo;
	private String merchantName;
	private String date;
	private String orderId;
	private String refundOrderId;
	private String mopType;
	private String paymentType;
	private String txnType;
	private String status;
	private double baseAmount; // Transaction Amount
	private String customerEmail;
	private String customerPhone;
	private String ipAddress;
	private String cardMask;
	private String ruleType;

	public String getTxnId() {
		return txnId;
	}

	public void setTxnId(String txnId) {
		this.txnId = txnId;
	}

	public String getPgRefNo() {
		return pgRefNo;
	}

	public void setPgRefNo(String pgRefNo) {
		this.pgRefNo = pgRefNo;
	}

	public String getMerchantName() {
		return merchantName;
	}

	public void setMerchantName(String merchantName) {
		this.merchantName = merchantName;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public String getRefundOrderId() {
		return refundOrderId;
	}

	public void setRefundOrderId(String refundOrderId) {
		this.refundOrderId = refundOrderId;
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

	public String getTxnType() {
		return txnType;
	}

	public void setTxnType(String txnType) {
		this.txnType = txnType;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public double getBaseAmount() {
		return baseAmount;
	}

	public void setBaseAmount(double baseAmount) {
		this.baseAmount = baseAmount;
	}

	public String getCustomerEmail() {
		return customerEmail;
	}

	public void setCustomerEmail(String customerEmail) {
		this.customerEmail = customerEmail;
	}

	public String getCustomerPhone() {
		return customerPhone;
	}

	public void setCustomerPhone(String customerPhone) {
		this.customerPhone = customerPhone;
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public String getCardMask() {
		return cardMask;
	}

	public void setCardMask(String cardMask) {
		this.cardMask = cardMask;
	}

	public String getRuleType() {
		return ruleType;
	}

	public void setRuleType(String ruleType) {
		this.ruleType = ruleType;
	}

	public static String[] getHeaderName() {
		String[] objectArray = new String[15];
		objectArray[0] = "Txn Id";
		objectArray[1] = "PG Ref No";
		objectArray[2] = "Merchant Name";
		objectArray[3] = "Date";
		objectArray[4] = "Order Id";
		objectArray[5] = "Refund Order Id";
		objectArray[6] = "MOP type";
		objectArray[7] = "Payment Type";
		objectArray[8] = "Txn Type";
		objectArray[9] = "Status";
		objectArray[10] = "Amount";
		objectArray[11] = "Customer Email";
		objectArray[12] = "Customer Phone";
		objectArray[13] = "IP Address";
		objectArray[14] = "Card Mask";
		return objectArray;
	}

	public Object[] excelData() {
		Object[] objectArray = new Object[15];
		objectArray[0] = txnId;
		objectArray[1] = pgRefNo;
		objectArray[2] = merchantName;
		objectArray[3] = date;
		objectArray[4] = orderId;
		objectArray[5] = refundOrderId;
		objectArray[6] = mopType;
		objectArray[7] = paymentType;
		objectArray[8] = txnType;
		objectArray[9] = status;
		objectArray[10] = baseAmount;
		objectArray[11] = customerEmail;
		objectArray[12] = customerPhone;
		objectArray[13] = ipAddress;
		objectArray[14] = cardMask;
		return objectArray;
	}
}
