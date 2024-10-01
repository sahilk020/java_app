package com.pay10.commons.user;

public class CapturedTransactionDataObject {

	private String _id;
	private String transactionId;
	private String pgRefNum;
	private String transactionRegion;
	private String merchants;
	private String postSettledFlag;
	private String txnType;
	private String acquirerType;
	private String paymentMethods;
	private String createDate;
	private String orderId;
	private String payId;
	private String mopType;
	private String currency;
	private String cardHolderType;
	private String acqId;
	private String rrn;
	private String deltaRefundFlag;
	private String surchargeFlag;
	private String refundFlag;
	private String arn;
	private String origTxnId;
	private String status;
	private Double totalAmount;
	private Double amount;
	private Double tdrScAcquirer;
	private Double tdrScIpay;
	private Double tdrScMmad;
	private Double gstScAcquirer;
	private Double gstScIpay;
	private Double gstScMmad;
	private String captureDate;
	private String settledDate;
	private String nodalCreditDate;
	private String nodalPayoutInitiatedDate;
	private String nodalPayoutDate;
	
	
	
	public String getSettledDate() {
		return settledDate;
	}
	public void setSettledDate(String settledDate) {
		this.settledDate = settledDate;
	}
	public String getNodalCreditDate() {
		return nodalCreditDate;
	}
	public void setNodalCreditDate(String nodalCreditDate) {
		this.nodalCreditDate = nodalCreditDate;
	}
	public String getNodalPayoutInitiatedDate() {
		return nodalPayoutInitiatedDate;
	}
	public void setNodalPayoutInitiatedDate(String nodalPayoutInitiatedDate) {
		this.nodalPayoutInitiatedDate = nodalPayoutInitiatedDate;
	}
	public String getNodalPayoutDate() {
		return nodalPayoutDate;
	}
	public void setNodalPayoutDate(String nodalPayoutDate) {
		this.nodalPayoutDate = nodalPayoutDate;
	}
	public String getTransactionId() {
		return transactionId;
	}
	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}
	public String getPgRefNum() {
		return pgRefNum;
	}
	public void setPgRefNum(String pgRefNum) {
		this.pgRefNum = pgRefNum;
	}
	public String getTransactionRegion() {
		return transactionRegion;
	}
	public void setTransactionRegion(String transactionRegion) {
		this.transactionRegion = transactionRegion;
	}
	public String getMerchants() {
		return merchants;
	}
	public void setMerchants(String merchants) {
		this.merchants = merchants;
	}
	public String getPostSettledFlag() {
		return postSettledFlag;
	}
	public void setPostSettledFlag(String postSettledFlag) {
		this.postSettledFlag = postSettledFlag;
	}
	public String getTxnType() {
		return txnType;
	}
	public void setTxnType(String txnType) {
		this.txnType = txnType;
	}
	public String getAcquirerType() {
		return acquirerType;
	}
	public void setAcquirerType(String acquirerType) {
		this.acquirerType = acquirerType;
	}
	public String getPaymentMethods() {
		return paymentMethods;
	}
	public void setPaymentMethods(String paymentMethods) {
		this.paymentMethods = paymentMethods;
	}
	public String getCreateDate() {
		return createDate;
	}
	public void setCreateDate(String createDate) {
		this.createDate = createDate;
	}
	public String getOrderId() {
		return orderId;
	}
	public void setOrderId(String orderId) {
		this.orderId = orderId;
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
	public String getCurrency() {
		return currency;
	}
	public void setCurrency(String currency) {
		this.currency = currency;
	}
	public String getCardHolderType() {
		return cardHolderType;
	}
	public void setCardHolderType(String cardHolderType) {
		this.cardHolderType = cardHolderType;
	}
	public String getCaptureDate() {
		return captureDate;
	}
	public void setCaptureDate(String captureDate) {
		this.captureDate = captureDate;
	}
	public String getAcqId() {
		return acqId;
	}
	public void setAcqId(String acqId) {
		this.acqId = acqId;
	}
	public String getRrn() {
		return rrn;
	}
	public void setRrn(String rrn) {
		this.rrn = rrn;
	}
	public String getDeltaRefundFlag() {
		return deltaRefundFlag;
	}
	public void setDeltaRefundFlag(String deltaRefundFlag) {
		this.deltaRefundFlag = deltaRefundFlag;
	}
	public String getSurchargeFlag() {
		return surchargeFlag;
	}
	public void setSurchargeFlag(String surchargeFlag) {
		this.surchargeFlag = surchargeFlag;
	}
	public String getRefundFlag() {
		return refundFlag;
	}
	public void setRefundFlag(String refundFlag) {
		this.refundFlag = refundFlag;
	}
	public Double getTotalAmount() {
		return totalAmount;
	}
	public void setTotalAmount(Double totalAmount) {
		this.totalAmount = totalAmount;
	}
	public Double getAmount() {
		return amount;
	}
	public void setAmount(Double amount) {
		this.amount = amount;
	}
	public Double getTdrScAcquirer() {
		return tdrScAcquirer;
	}
	public void setTdrScAcquirer(Double tdrScAcquirer) {
		this.tdrScAcquirer = tdrScAcquirer;
	}
	public Double getTdrScIpay() {
		return tdrScIpay;
	}
	public void setTdrScIpay(Double tdrScIpay) {
		this.tdrScIpay = tdrScIpay;
	}
	public Double getTdrScMmad() {
		return tdrScMmad;
	}
	public void setTdrScMmad(Double tdrScMmad) {
		this.tdrScMmad = tdrScMmad;
	}
	public Double getGstScAcquirer() {
		return gstScAcquirer;
	}
	public void setGstScAcquirer(Double gstScAcquirer) {
		this.gstScAcquirer = gstScAcquirer;
	}
	public Double getGstScIpay() {
		return gstScIpay;
	}
	public void setGstScIpay(Double gstScIpay) {
		this.gstScIpay = gstScIpay;
	}
	public Double getGstScMmad() {
		return gstScMmad;
	}
	public void setGstScMmad(Double gstScMmad) {
		this.gstScMmad = gstScMmad;
	}
	public String get_id() {
		return _id;
	}
	public void set_id(String _id) {
		this._id = _id;
	}
	public String getArn() {
		return arn;
	}
	public void setArn(String arn) {
		this.arn = arn;
	}
	public String getOrigTxnId() {
		return origTxnId;
	}
	public void setOrigTxnId(String origTxnId) {
		this.origTxnId = origTxnId;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	
	
	
	
}
