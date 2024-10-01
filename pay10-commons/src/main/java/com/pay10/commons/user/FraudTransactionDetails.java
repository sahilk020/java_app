package com.pay10.commons.user;

public class FraudTransactionDetails {
	private String id;
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

	private String fraudType;
	private String blockType;
	private String isBlock;
	private String isIgnore;
	private String isNotify;
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

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

	public String getFraudType() {
		return fraudType;
	}

	public void setFraudType(String fraudType) {
		this.fraudType = fraudType;
	}

	public String getBlockType() {
		return blockType;
	}

	public void setBlockType(String blockType) {
		this.blockType = blockType;
	}

	public String getIsBlock() {
		return isBlock;
	}

	public void setIsBlock(String isBlock) {
		this.isBlock = isBlock;
	}

	public String getIsIgnore() {
		return isIgnore;
	}

	public void setIsIgnore(String isIgnore) {
		this.isIgnore = isIgnore;
	}

	public String getIsNotify() {
		return isNotify;
	}

	public void setIsNotify(String isNotify) {
		this.isNotify = isNotify;
	}

	
}
