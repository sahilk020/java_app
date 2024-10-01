package com.pay10.commons.user;

public class SummaryReportObject {

	private String transactionId;
	private String pgRefNum;
	private String transactionRegion;
	private String merchants;
	private String postSettledFlag;
	private String txnType;
	private String acquirerType;
	private String paymentMethods;
	private String dateFrom;
	private String amount;
	private String orderId;
	private String totalAmount;
	private String srNo;
	private String payId;
	private String mopType;
	private String currency;
	private String cardHolderType;
	private String captureDate;
	private String settlementDate;
	private String nodalCreditDate;
	private String nodalPayoutInitiationDate;
	private String nodalPayoutDate;
	private String tdrScAcquirer;
	private String tdrScIpay;
	private String gstScAcquirer;
	private String gstScIpay;
	private String gstScMmad;
	private String tdrScMmad;
	private String merchantAmount;
	private String acqId;
	private String rrn;
	private String deltaRefundFlag;
	private String surchargeFlag;
	private String netMerchantPayableAmount;
	private String status;
	private String nodalDate;
	
	public String getNetMerchantPayableAmount() {
		return netMerchantPayableAmount;
	}
	public void setNetMerchantPayableAmount(String netMerchantPayableAmount) {
		this.netMerchantPayableAmount = netMerchantPayableAmount;
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
	public String getDateFrom() {
		return dateFrom;
	}
	public void setDateFrom(String dateFrom) {
		this.dateFrom = dateFrom;
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
	public String getTotalAmount() {
		return totalAmount;
	}
	public void setTotalAmount(String totalAmount) {
		this.totalAmount = totalAmount;
	}

	public String getSrNo() {
		return srNo;
	}
	public void setSrNo(String srNo) {
		this.srNo = srNo;
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
	public String getSettlementDate() {
		return settlementDate;
	}
	public void setSettlementDate(String settlementDate) {
		this.settlementDate = settlementDate;
	}
	public String getTdrScAcquirer() {
		return tdrScAcquirer;
	}
	public void setTdrScAcquirer(String tdrScAcquirer) {
		this.tdrScAcquirer = tdrScAcquirer;
	}
	public String getTdrScIpay() {
		return tdrScIpay;
	}
	public void setTdrScIpay(String tdrScIpay) {
		this.tdrScIpay = tdrScIpay;
	}
	public String getGstScAcquirer() {
		return gstScAcquirer;
	}
	public void setGstScAcquirer(String gstScAcquirer) {
		this.gstScAcquirer = gstScAcquirer;
	}
	public String getGstScIpay() {
		return gstScIpay;
	}
	public void setGstScIpay(String gstScIpay) {
		this.gstScIpay = gstScIpay;
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
	public String getDeltaRefundFlag() {
		return deltaRefundFlag;
	}
	public void setDeltaRefundFlag(String deltaRefundFlag) {
		this.deltaRefundFlag = deltaRefundFlag;
	}
	public String getPayId() {
		return payId;
	}
	public void setPayId(String payId) {
		this.payId = payId;
	}
	public String getSurchargeFlag() {
		return surchargeFlag;
	}
	public void setSurchargeFlag(String surchargeFlag) {
		this.surchargeFlag = surchargeFlag;
	}
	
	public Object[] myCsvMethodDownloadSummaryReport() {
		  Object[] objectArray = new Object[25];
		  objectArray[0] = srNo;
		  objectArray[1] = transactionId;
		  objectArray[2] = pgRefNum;
		  objectArray[3] = paymentMethods;
		  objectArray[4] = mopType;
		  objectArray[5] = orderId;
		  objectArray[6] = merchants;
		  objectArray[7] = currency;
		  objectArray[8] = txnType;
		  objectArray[9] = captureDate;
		  objectArray[10] = dateFrom;
		  objectArray[11] = transactionRegion;
		  objectArray[12] = cardHolderType;
		  objectArray[13] = acquirerType;
		  objectArray[14] = totalAmount;
		  objectArray[15] = tdrScAcquirer;
		  objectArray[16] = tdrScIpay;
		  objectArray[17] = tdrScMmad;
		  objectArray[18] = gstScAcquirer;
		  objectArray[19] = gstScIpay;
		  objectArray[20] = gstScMmad;
		  objectArray[21] = amount;
		  objectArray[22] = acqId;
		  objectArray[23] = rrn;
		  objectArray[24] = postSettledFlag;
		
		  return objectArray;
		}
	
	
	
	public Object[] myCsvMethodNodalSummaryReport() {
		  Object[] objectArray = new Object[27];
		  objectArray[0] = srNo;
		  objectArray[1] = transactionId;
		  objectArray[2] = pgRefNum;
		  objectArray[3] = paymentMethods;
		  objectArray[4] = mopType;
		  objectArray[5] = orderId;
		  objectArray[6] = merchants;
		  objectArray[7] = currency;
		  objectArray[8] = txnType;
		  objectArray[9] = captureDate;
		  objectArray[10] = settlementDate;
		  objectArray[11] = nodalCreditDate;
		  objectArray[12] = nodalPayoutInitiationDate;
		  objectArray[13] = nodalPayoutDate;
		  objectArray[14] = transactionRegion;
		  objectArray[15] = cardHolderType;
		  objectArray[16] = acquirerType;
		  objectArray[17] = totalAmount;
		  objectArray[18] = tdrScAcquirer;
		  objectArray[19] = tdrScIpay;
		  objectArray[20] = gstScAcquirer;
		  objectArray[21] = gstScIpay;
		  objectArray[22] = amount;
		  objectArray[23] = acqId;
		  objectArray[24] = rrn;
		  objectArray[25] = postSettledFlag;
		  objectArray[26] = deltaRefundFlag;
		
		  return objectArray;
		}
	
	public String getNodalCreditDate() {
		return nodalCreditDate;
	}
	public void setNodalCreditDate(String nodalCreditDate) {
		this.nodalCreditDate = nodalCreditDate;
	}
	public String getNodalPayoutInitiationDate() {
		return nodalPayoutInitiationDate;
	}
	public void setNodalPayoutInitiationDate(String nodalPayoutInitiationDate) {
		this.nodalPayoutInitiationDate = nodalPayoutInitiationDate;
	}
	public String getNodalPayoutDate() {
		return nodalPayoutDate;
	}
	public void setNodalPayoutDate(String nodalPayoutDate) {
		this.nodalPayoutDate = nodalPayoutDate;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getNodalDate() {
		return nodalDate;
	}
	public void setNodalDate(String nodalDate) {
		this.nodalDate = nodalDate;
	}
	public String getGstScMmad() {
		return gstScMmad;
	}
	public void setGstScMmad(String gstScMmad) {
		this.gstScMmad = gstScMmad;
	}
	public String getTdrScMmad() {
		return tdrScMmad;
	}
	public void setTdrScMmad(String tdrScMmad) {
		this.tdrScMmad = tdrScMmad;
	}
	
}
