package com.pay10.commons.user;

public class TDRBifurcationReportDetails {
	
	private String txnId;
	private String pgRefNo;
	private String merchantName;
	private String acquirer;
	private String date;
	private String orderId;
	private String paymentMethod;
	private String txnType;
	private String status;
	private String transactionRegion;
	private String baseAmount; // Transaction Amount
	private String totalAmount;
	private String deltaRefundFlag;
	private String acqId;
	private String RRN;
	private String postSettledFlag;
	private String refundOrderId;
	private String refundFlag;
	private String mopType;
	private String acquirerTDRFixed; 
	private String acquirerTDRPercentage; 
	private String acquirerTDRAmontTotal; 
	private String gstOnAcquirerTDR; 
	private String pgTDRFixed; 
	private String pgTDRPercentage; 
	private String pgTDRAmontTotal; 
	private String gstOnPgTDR; 
	private String merchantTDRFixed; 
	private String merchantTDRPercentage; 
	private String tdr;
	private String igst18; 
	private String cgst9; 
	private String sgst9; 
	private String amountPaybleToMerchant; 
	private String refund;//na
	private String settlementDate;
	private String amountreceivedInNodal;
	private String amountReceivedNodalBank;
	private String settlementTat;
	private String accountHolderName;
	private String accountNumber;
	private String ifscCode;
	private String transactionIdentifer;
	private String liabilityHoldRemakrs;
	private String liabilityReleaseRemakrs;
	private String utrNumber;
	private String settlementPeriod;
	private String surchargeFlag;
	
	private String merchantPreference;
	private String merchantTDR;
	private String merchantMinTdramount;
	private String merchantMaxTdramount;
	private String bankPreference;
	private String bankTDR;
	private String bankMinTdrAmount;
	private String bankMaxTdrAmount;
	private String bankTdrInAMOUNT;
	private String bankGstInAmount;
	private String pgTdrInAmount;
	private String pgGstInAmount;
	
	
	
	
	
	public String getMerchantPreference() {
		return merchantPreference;
	}
	public void setMerchantPreference(String merchantPreference) {
		this.merchantPreference = merchantPreference;
	}
	public String getMerchantTDR() {
		return merchantTDR;
	}
	public void setMerchantTDR(String merchantTDR) {
		this.merchantTDR = merchantTDR;
	}
	public String getMerchantMinTdramount() {
		return merchantMinTdramount;
	}
	public void setMerchantMinTdramount(String merchantMinTdramount) {
		this.merchantMinTdramount = merchantMinTdramount;
	}
	public String getMerchantMaxTdramount() {
		return merchantMaxTdramount;
	}
	public void setMerchantMaxTdramount(String merchantMaxTdramount) {
		this.merchantMaxTdramount = merchantMaxTdramount;
	}
	public String getBankPreference() {
		return bankPreference;
	}
	public void setBankPreference(String bankPreference) {
		this.bankPreference = bankPreference;
	}
	public String getBankTDR() {
		return bankTDR;
	}
	public void setBankTDR(String bankTDR) {
		this.bankTDR = bankTDR;
	}
	public String getBankMinTdrAmount() {
		return bankMinTdrAmount;
	}
	public void setBankMinTdrAmount(String bankMinTdrAmount) {
		this.bankMinTdrAmount = bankMinTdrAmount;
	}
	public String getBankMaxTdrAmount() {
		return bankMaxTdrAmount;
	}
	public void setBankMaxTdrAmount(String bankMaxTdrAmount) {
		this.bankMaxTdrAmount = bankMaxTdrAmount;
	}
	public String getBankTdrInAMOUNT() {
		return bankTdrInAMOUNT;
	}
	public void setBankTdrInAMOUNT(String bankTdrInAMOUNT) {
		this.bankTdrInAMOUNT = bankTdrInAMOUNT;
	}
	public String getBankGstInAmount() {
		return bankGstInAmount;
	}
	public void setBankGstInAmount(String bankGstInAmount) {
		this.bankGstInAmount = bankGstInAmount;
	}
	public String getPgTdrInAmount() {
		return pgTdrInAmount;
	}
	public void setPgTdrInAmount(String pgTdrInAmount) {
		this.pgTdrInAmount = pgTdrInAmount;
	}
	public String getPgGstInAmount() {
		return pgGstInAmount;
	}
	public void setPgGstInAmount(String pgGstInAmount) {
		this.pgGstInAmount = pgGstInAmount;
	}
	public String getSurchargeFlag() {
		return surchargeFlag;
	}
	public void setSurchargeFlag(String surchargeFlag) {
		this.surchargeFlag = surchargeFlag;
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
	public String getAcquirer() {
		return acquirer;
	}
	public void setAcquirer(String acquirer) {
		this.acquirer = acquirer;
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
	public String getPaymentMethod() {
		return paymentMethod;
	}
	public void setPaymentMethod(String paymentMethod) {
		this.paymentMethod = paymentMethod;
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
	public String getTransactionRegion() {
		return transactionRegion;
	}
	public void setTransactionRegion(String transactionRegion) {
		this.transactionRegion = transactionRegion;
	}
	public String getBaseAmount() {
		return baseAmount;
	}
	public void setBaseAmount(String baseAmount) {
		this.baseAmount = baseAmount;
	}
	public String getTotalAmount() {
		return totalAmount;
	}
	public void setTotalAmount(String totalAmount) {
		this.totalAmount = totalAmount;
	}
	public String getDeltaRefundFlag() {
		return deltaRefundFlag;
	}
	public void setDeltaRefundFlag(String deltaRefundFlag) {
		this.deltaRefundFlag = deltaRefundFlag;
	}
	public String getAcqId() {
		return acqId;
	}
	public void setAcqId(String acqId) {
		this.acqId = acqId;
	}
	public String getRRN() {
		return RRN;
	}
	public void setRRN(String rRN) {
		RRN = rRN;
	}
	public String getPostSettledFlag() {
		return postSettledFlag;
	}
	public void setPostSettledFlag(String postSettledFlag) {
		this.postSettledFlag = postSettledFlag;
	}
	public String getRefundOrderId() {
		return refundOrderId;
	}
	public void setRefundOrderId(String refundOrderId) {
		this.refundOrderId = refundOrderId;
	}
	public String getRefundFlag() {
		return refundFlag;
	}
	public void setRefundFlag(String refundFlag) {
		this.refundFlag = refundFlag;
	}
	public String getMopType() {
		return mopType;
	}
	public void setMopType(String mopType) {
		this.mopType = mopType;
	}
	public String getAcquirerTDRFixed() {
		return acquirerTDRFixed;
	}
	public void setAcquirerTDRFixed(String acquirerTDRFixed) {
		this.acquirerTDRFixed = acquirerTDRFixed;
	}
	public String getAcquirerTDRPercentage() {
		return acquirerTDRPercentage;
	}
	public void setAcquirerTDRPercentage(String acquirerTDRPercentage) {
		this.acquirerTDRPercentage = acquirerTDRPercentage;
	}
	public String getAcquirerTDRAmontTotal() {
		return acquirerTDRAmontTotal;
	}
	public void setAcquirerTDRAmontTotal(String acquirerTDRAmontTotal) {
		this.acquirerTDRAmontTotal = acquirerTDRAmontTotal;
	}
	public String getGstOnAcquirerTDR() {
		return gstOnAcquirerTDR;
	}
	public void setGstOnAcquirerTDR(String gstOnAcquirerTDR) {
		this.gstOnAcquirerTDR = gstOnAcquirerTDR;
	}
	public String getPgTDRFixed() {
		return pgTDRFixed;
	}
	public void setPgTDRFixed(String pgTDRFixed) {
		this.pgTDRFixed = pgTDRFixed;
	}
	public String getPgTDRPercentage() {
		return pgTDRPercentage;
	}
	public void setPgTDRPercentage(String pgTDRPercentage) {
		this.pgTDRPercentage = pgTDRPercentage;
	}
	public String getPgTDRAmontTotal() {
		return pgTDRAmontTotal;
	}
	public void setPgTDRAmontTotal(String pgTDRAmontTotal) {
		this.pgTDRAmontTotal = pgTDRAmontTotal;
	}
	public String getGstOnPgTDR() {
		return gstOnPgTDR;
	}
	public void setGstOnPgTDR(String gstOnPgTDR) {
		this.gstOnPgTDR = gstOnPgTDR;
	}
	public String getMerchantTDRFixed() {
		return merchantTDRFixed;
	}
	public void setMerchantTDRFixed(String merchantTDRFixed) {
		this.merchantTDRFixed = merchantTDRFixed;
	}
	public String getMerchantTDRPercentage() {
		return merchantTDRPercentage;
	}
	public void setMerchantTDRPercentage(String merchantTDRPercentage) {
		this.merchantTDRPercentage = merchantTDRPercentage;
	}

	public String getTdr() {
		return tdr;
	}
	public void setTdr(String tdr) {
		this.tdr = tdr;
	}
	public String getIgst18() {
		return igst18;
	}
	public void setIgst18(String igst18) {
		this.igst18 = igst18;
	}
	public String getCgst9() {
		return cgst9;
	}
	public void setCgst9(String cgst9) {
		this.cgst9 = cgst9;
	}
	public String getSgst9() {
		return sgst9;
	}
	public void setSgst9(String sgst9) {
		this.sgst9 = sgst9;
	}
	
	public String getAmountPaybleToMerchant() {
		return amountPaybleToMerchant;
	}
	public void setAmountPaybleToMerchant(String amountPaybleToMerchant) {
		this.amountPaybleToMerchant = amountPaybleToMerchant;
	}
	public String getRefund() {
		return refund;
	}
	public void setRefund(String refund) {
		this.refund = refund;
	}
	public String getSettlementDate() {
		return settlementDate;
	}
	public void setSettlementDate(String settlementDate) {
		this.settlementDate = settlementDate;
	}
	public String getAmountreceivedInNodal() {
		return amountreceivedInNodal;
	}
	public void setAmountreceivedInNodal(String amountreceivedInNodal) {
		this.amountreceivedInNodal = amountreceivedInNodal;
	}
	public String getAmountReceivedNodalBank() {
		return amountReceivedNodalBank;
	}
	public void setAmountReceivedNodalBank(String amountReceivedNodalBank) {
		this.amountReceivedNodalBank = amountReceivedNodalBank;
	}
	public String getSettlementTat() {
		return settlementTat;
	}
	public void setSettlementTat(String settlementTat) {
		this.settlementTat = settlementTat;
	}
	public String getAccountHolderName() {
		return accountHolderName;
	}
	public void setAccountHolderName(String accountHolderName) {
		this.accountHolderName = accountHolderName;
	}
	public String getIfscCode() {
		return ifscCode;
	}
	public void setIfscCode(String ifscCode) {
		this.ifscCode = ifscCode;
	}
	public String getTransactionIdentifer() {
		return transactionIdentifer;
	}
	public void setTransactionIdentifer(String transactionIdentifer) {
		this.transactionIdentifer = transactionIdentifer;
	}
	public String getLiabilityHoldRemakrs() {
		return liabilityHoldRemakrs;
	}
	public void setLiabilityHoldRemakrs(String liabilityHoldRemakrs) {
		this.liabilityHoldRemakrs = liabilityHoldRemakrs;
	}
	public String getLiabilityReleaseRemakrs() {
		return liabilityReleaseRemakrs;
	}
	public void setLiabilityReleaseRemakrs(String liabilityReleaseRemakrs) {
		this.liabilityReleaseRemakrs = liabilityReleaseRemakrs;
	}
	public String getUtrNumber() {
		return utrNumber;
	}
	public void setUtrNumber(String utrNumber) {
		this.utrNumber = utrNumber;
	}
	public String getSettlementPeriod() {
		return settlementPeriod;
	}
	public void setSettlementPeriod(String settlementPeriod) {
		this.settlementPeriod = settlementPeriod;
	}
	public String getAccountNumber() {
		return accountNumber;
	}
	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}
	
	
}
