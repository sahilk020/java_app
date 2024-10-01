package com.pay10.commons.user;

public class DailyReportObject {
	
	private String pgRefNum;
	private String paymentMethod;
	private String mopType;
	private String orderId;
	private String businessName;
	private String currency;
	private String txnType;
	private String captureDate;
	private String transactionRegion;
	private String cardHolderType;
	private String acquirer;
	
	private String totalAmount;
	private String surchargeAcquirer;
	private String surchargeIpay;
	private String gstAcq;
	private String gstIpay;
	private String merchantAmount;
	private String acqId;
	private String rrn;
	private String postSettledFlag;
	private String refundFlag;
	private String refundOrderId;
	
	
	public String getPgRefNum() {
		return pgRefNum;
	}
	public void setPgRefNum(String pgRefNum) {
		this.pgRefNum = pgRefNum;
	}
	public String getPaymentMethod() {
		return paymentMethod;
	}
	public void setPaymentMethod(String paymentMethod) {
		this.paymentMethod = paymentMethod;
	}
	public String getMopType() {
		return mopType;
	}
	public void setMopType(String mopType) {
		this.mopType = mopType;
	}
	public String getOrderId() {
		return orderId;
	}
	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}
	public String getBusinessName() {
		return businessName;
	}
	public void setBusinessName(String businessName) {
		this.businessName = businessName;
	}
	public String getCurrency() {
		return currency;
	}
	public void setCurrency(String currency) {
		this.currency = currency;
	}
	public String getTxnType() {
		return txnType;
	}
	public void setTxnType(String txnType) {
		this.txnType = txnType;
	}
	public String getCaptureDate() {
		return captureDate;
	}
	public void setCaptureDate(String captureDate) {
		this.captureDate = captureDate;
	}
	public String getTransactionRegion() {
		return transactionRegion;
	}
	public void setTransactionRegion(String transactionRegion) {
		this.transactionRegion = transactionRegion;
	}
	public String getCardHolderType() {
		return cardHolderType;
	}
	public void setCardHolderType(String cardHolderType) {
		this.cardHolderType = cardHolderType;
	}
	public String getAcquirer() {
		return acquirer;
	}
	public void setAcquirer(String acquirer) {
		this.acquirer = acquirer;
	}
	public String getTotalAmount() {
		return totalAmount;
	}
	public void setTotalAmount(String totalAmount) {
		this.totalAmount = totalAmount;
	}
	public String getSurchargeAcquirer() {
		return surchargeAcquirer;
	}
	public void setSurchargeAcquirer(String surchargeAcquirer) {
		this.surchargeAcquirer = surchargeAcquirer;
	}
	public String getSurchargeIpay() {
		return surchargeIpay;
	}
	public void setSurchargeIpay(String surchargeIpay) {
		this.surchargeIpay = surchargeIpay;
	}
	public String getGstAcq() {
		return gstAcq;
	}
	public void setGstAcq(String gstAcq) {
		this.gstAcq = gstAcq;
	}
	public String getGstIpay() {
		return gstIpay;
	}
	public void setGstIpay(String gstIpay) {
		this.gstIpay = gstIpay;
	}
	public String getMerchantAmount() {
		return merchantAmount;
	}
	public void setMerchantAmount(String merchantAmount) {
		this.merchantAmount = merchantAmount;
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
	public String getPostSettledFlag() {
		return postSettledFlag;
	}
	public void setPostSettledFlag(String postSettledFlag) {
		this.postSettledFlag = postSettledFlag;
	}
	public String getRefundFlag() {
		return refundFlag;
	}
	public void setRefundFlag(String refundFlag) {
		this.refundFlag = refundFlag;
	}
	public String getRefundOrderId() {
		return refundOrderId;
	}
	public void setRefundOrderId(String refundOrderId) {
		this.refundOrderId = refundOrderId;
	}
	
	
	
	public Object[] myCsvMethodDownloadSummaryReport() {
		  Object[] objectArray = new Object[21];
		  objectArray[0] = pgRefNum;
		  objectArray[1] = paymentMethod;
		  objectArray[2] = mopType;
		  objectArray[3] = orderId;
		  objectArray[4] = businessName;
		  objectArray[5] = currency;
		  objectArray[6] = txnType;
		  objectArray[7] = captureDate;
		  objectArray[8] = transactionRegion;
		  objectArray[9] = cardHolderType;
		  objectArray[10] = acquirer;
		  objectArray[11] = totalAmount;
		  objectArray[12] = surchargeAcquirer;
		  objectArray[13] = surchargeIpay;
		  objectArray[14] = gstIpay;
		  objectArray[15] = gstAcq;
		  objectArray[16] = merchantAmount;
		  objectArray[17] = acqId;
		  objectArray[18] = rrn;
		  objectArray[19] = postSettledFlag;
		  objectArray[20] = refundFlag;
		  return objectArray;
		}
	
	
	
	
	

}
