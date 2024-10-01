package com.pay10.commons.user;

import java.math.BigDecimal;

public class SummaryObject {

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
	private String mopType;
	private String currency;
	private String cardHolderType;
	private String captureDate;
	private String tdrScAcquirer;
	private String tdrScIpay;
	private String gstScAcquirer;
	private String gstScIpay;
	private String gstScMmad;
	private String tdrScMmad;
	private String merchantAmount;
	private String acqId;
	private String rrn;

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

	public Object[] myCsvMethodDownloadSummaryReport() {
		Object[] objectArray = new Object[26];

		BigDecimal toatlTdr = new BigDecimal(tdrScAcquirer).add(new BigDecimal(tdrScIpay));
		BigDecimal totalgst = new BigDecimal(gstScAcquirer).add(new BigDecimal(gstScIpay));
		BigDecimal totalDeductions = toatlTdr.add(totalgst);

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
		objectArray[14] = amount;
		objectArray[15] = totalAmount;
		objectArray[16] = tdrScAcquirer;
		objectArray[17] = gstScAcquirer;
		objectArray[18] = tdrScIpay;
		objectArray[19] = gstScIpay;
		objectArray[20] = String.valueOf(toatlTdr);
		objectArray[21] = String.valueOf(totalgst);
		objectArray[22] = String.valueOf(new BigDecimal(totalAmount).subtract(totalDeductions));
		objectArray[23] = acqId;
		objectArray[24] = rrn;
		objectArray[25] = postSettledFlag;

		return objectArray;
	}

	public Object[] merchantDownloadSummaryReport() {
		Object[] objectArray = new Object[21];

		BigDecimal toatlTdr = new BigDecimal(tdrScAcquirer).add(new BigDecimal(tdrScIpay));
		BigDecimal totalgst = new BigDecimal(gstScAcquirer).add(new BigDecimal(gstScIpay));
		BigDecimal totalDeductions = toatlTdr.add(totalgst);

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
		objectArray[13] = amount;
		objectArray[14] = totalAmount;
		objectArray[15] = String.valueOf(toatlTdr);
		objectArray[16] = String.valueOf(totalgst);
		objectArray[17] = String.valueOf(new BigDecimal(totalAmount).subtract(totalDeductions));
		objectArray[18] = acqId;
		objectArray[19] = rrn;
		objectArray[20] = postSettledFlag;

		return objectArray;
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
