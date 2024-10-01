package com.pay10.commons.user;

public class AnalyticsData {

	private String overallTotalPercent;
	private String totalTxnCount;
	private String totalCapturedTxnAmount;
	private String totalRejectedTxnPercent;
	private String successTxnCount;
	private String successTxnPercent;
	private String failedTxnCount;
	private String avgTkt;
	private String CCTxnPercent;
	private String CCSuccessRate;
	private String DCTxnPercent;
	private String WLTxnPercent;
	private String NBTxnPercent;
	private String DCSuccessRate;
	private String UPTxnPercent;
	private String UPSuccessRate;
	private String WLSuccessRate;
	private String NBSuccessRate;

	private String totalCCTxn;
	private String totalCCSuccessTxnPercent;
	private String totalCCFailedTxnPercent;
	private String totalCCCancelledTxnPercent;
	private String totalCCInvalidTxnPercent;
	private String totalCCFraudTxnPercent;
	private String totalCCDroppedTxnPercent;
	private String totalCCRejectedTxnPercent;

	private String totalDCTxn;
	private String totalDCSuccessTxnPercent;
	private String totalDCFailedTxnPercent;
	private String totalDCCancelledTxnPercent;
	private String totalDCInvalidTxnPercent;
	private String totalDCFraudTxnPercent;
	private String totalDCDroppedTxnPercent;
	private String totalDCRejectedTxnPercent;

	private String ipayProfitCumm;
	private String ipayProfitInclGstCumm;
	private String ipayProfitExcGstCumm;
	private String ipayProfit;
	private String ipayProfitInclGst;
	private String ipayProfitExcGst;
	private String dateSettled;
	private String dateCaptured;
	private String ipayProfitAmount;

	private String totalUPTxn;
	private String totalUPSuccessTxnPercent;
	private String totalUPFailedTxnPercent;
	private String totalUPCancelledTxnPercent;
	private String totalUPInvalidTxnPercent;
	private String totalUPFraudTxnPercent;
	private String totalUPDroppedTxnPercent;
	private String totalUPRejectedTxnPercent;

	private String totalWLTxn;
	private String totalWLSuccessTxnPercent;
	private String totalWLFailedTxnPercent;
	private String totalWLCancelledTxnPercent;
	private String totalWLInvalidTxnPercent;
	private String totalWLFraudTxnPercent;
	private String totalWLDroppedTxnPercent;
	private String totalWLRejectedTxnPercent;

	private String totalNBTxn;
	private String totalNBSuccessTxnPercent;
	private String totalNBFailedTxnPercent;
	private String totalNBCancelledTxnPercent;
	private String totalNBInvalidTxnPercent;
	private String totalNBFraudTxnPercent;
	private String totalNBDroppedTxnPercent;
	private String totalNBRejectedTxnPercent;

	private String captured;
	private String failed;
	private String cancelled;
	private String invalid;
	private String fraud;
	private String dropped;
	private String rejected;
	private String gst;

	private String capturedPercent;
	private String failedPercent;
	private String cancelledPercent;
	private String invalidPercent;
	private String fraudPercent;
	private String droppedPercent;
	private String rejectedPercent;

	private String unknownTxnCount;

	private String totalCCTxnAmount;
	private String totalDCTxnAmount;
	private String totalUPTxnAmount;
	private String totalWLTxnAmount;
	private String totalNBTxnAmount;
	private String totalCCCapturedCount;
	private String totalDCCapturedCount;
	private String totalUPCapturedCount;
	private String totalWLCapturedCount;
	private String totalNBCapturedCount;
	private String merchantPgRatio;
	private String acquirerPgRatio;

	private String ccSettledPercentage;
	private String dcSettledPercentage;
	private String upSettledPercentage;
	private String wlSettledPercentage;
	private String nbSettledPercentage;
	private String avgSettlementAmount;
	private String merchantSaleSettledAmount;
	private String merchantRefundSettledAmount;

	private String totalProfit;
	private String postSettledTransactionCount;
	private String actualSettlementAmount;

	// For GST and Surcharge calculation
	private String surchargeFlag;
	private String payId;
	private String mopType;
	private String paymentType;
	private String txnType;
	private String paymentsRegion;
	private String cardHolderType;
	private String amount;
	private String paymentMethod;
	private String totalAmount;

	private String merchantName;
	private String saleCapturedAmount;
	private String saleCapturedCount;
	private String pgSaleSurcharge;
	private String acquirerSaleSurcharge;
	private String pgSaleGst;
	private String acquirerSaleGst;
	private String refundCapturedAmount;
	private String refundCapturedCount;
	private String pgRefundSurcharge;
	private String acquirerRefundSurcharge;
	private String pgRefundGst;
	private String acquirerRefundGst;
	private String totalMerchantAmount;
	private String acquirer;

	private String capturedTotalAmount;
	private String failedTotalAmount;
	private String cancelledTotalAmount;
	private String invalidTotalAmount;

	public String getTotalNBTxnAmount() {
		return totalNBTxnAmount;
	}

	public void setTotalNBTxnAmount(String totalNBTxnAmount) {
		this.totalNBTxnAmount = totalNBTxnAmount;
	}

	public String getTotalNBCapturedCount() {
		return totalNBCapturedCount;
	}

	public void setTotalNBCapturedCount(String totalNBCapturedCount) {
		this.totalNBCapturedCount = totalNBCapturedCount;
	}

	public String getNbSettledPercentage() {
		return nbSettledPercentage;
	}

	public void setNbSettledPercentage(String nbSettledPercentage) {
		this.nbSettledPercentage = nbSettledPercentage;
	}

	public String getNBTxnPercent() {
		return NBTxnPercent;
	}

	public void setNBTxnPercent(String nBTxnPercent) {
		NBTxnPercent = nBTxnPercent;
	}

	public String getTotalNBTxn() {
		return totalNBTxn;
	}

	public String getNBSuccessRate() {
		return NBSuccessRate;
	}

	public void setNBSuccessRate(String nBSuccessRate) {
		NBSuccessRate = nBSuccessRate;
	}

	public void setTotalNBTxn(String totalNBTxn) {
		this.totalNBTxn = totalNBTxn;
	}

	public String getTotalNBSuccessTxnPercent() {
		return totalNBSuccessTxnPercent;
	}

	public void setTotalNBSuccessTxnPercent(String totalNBSuccessTxnPercent) {
		this.totalNBSuccessTxnPercent = totalNBSuccessTxnPercent;
	}

	public String getTotalNBFailedTxnPercent() {
		return totalNBFailedTxnPercent;
	}

	public void setTotalNBFailedTxnPercent(String totalNBFailedTxnPercent) {
		this.totalNBFailedTxnPercent = totalNBFailedTxnPercent;
	}

	public String getTotalNBCancelledTxnPercent() {
		return totalNBCancelledTxnPercent;
	}

	public void setTotalNBCancelledTxnPercent(String totalNBCancelledTxnPercent) {
		this.totalNBCancelledTxnPercent = totalNBCancelledTxnPercent;
	}

	public String getTotalNBInvalidTxnPercent() {
		return totalNBInvalidTxnPercent;
	}

	public void setTotalNBInvalidTxnPercent(String totalNBInvalidTxnPercent) {
		this.totalNBInvalidTxnPercent = totalNBInvalidTxnPercent;
	}

	public String getTotalNBFraudTxnPercent() {
		return totalNBFraudTxnPercent;
	}

	public void setTotalNBFraudTxnPercent(String totalNBFraudTxnPercent) {
		this.totalNBFraudTxnPercent = totalNBFraudTxnPercent;
	}

	public String getTotalNBDroppedTxnPercent() {
		return totalNBDroppedTxnPercent;
	}

	public void setTotalNBDroppedTxnPercent(String totalNBDroppedTxnPercent) {
		this.totalNBDroppedTxnPercent = totalNBDroppedTxnPercent;
	}

	public String getTotalNBRejectedTxnPercent() {
		return totalNBRejectedTxnPercent;
	}

	public void setTotalNBRejectedTxnPercent(String totalNBRejectedTxnPercent) {
		this.totalNBRejectedTxnPercent = totalNBRejectedTxnPercent;
	}

	public String getOverallTotalPercent() {
		return overallTotalPercent;
	}

	public void setOverallTotalPercent(String overallTotalPercent) {
		this.overallTotalPercent = overallTotalPercent;
	}

	public String getGst() {
		return gst;
	}

	public void setGst(String gst) {
		this.gst = gst;
	}

	public String getWLTxnPercent() {
		return WLTxnPercent;
	}

	public void setWLTxnPercent(String wLTxnPercent) {
		WLTxnPercent = wLTxnPercent;
	}

	public String getWLSuccessRate() {
		return WLSuccessRate;
	}

	public void setWLSuccessRate(String wLSuccessRate) {
		WLSuccessRate = wLSuccessRate;
	}

	public String getTotalWLTxn() {
		return totalWLTxn;
	}

	public void setTotalWLTxn(String totalWLTxn) {
		this.totalWLTxn = totalWLTxn;
	}

	public String getTotalWLSuccessTxnPercent() {
		return totalWLSuccessTxnPercent;
	}

	public void setTotalWLSuccessTxnPercent(String totalWLSuccessTxnPercent) {
		this.totalWLSuccessTxnPercent = totalWLSuccessTxnPercent;
	}

	public String getTotalWLFailedTxnPercent() {
		return totalWLFailedTxnPercent;
	}

	public void setTotalWLFailedTxnPercent(String totalWLFailedTxnPercent) {
		this.totalWLFailedTxnPercent = totalWLFailedTxnPercent;
	}

	public String getTotalWLCancelledTxnPercent() {
		return totalWLCancelledTxnPercent;
	}

	public void setTotalWLCancelledTxnPercent(String totalWLCancelledTxnPercent) {
		this.totalWLCancelledTxnPercent = totalWLCancelledTxnPercent;
	}

	public String getTotalWLInvalidTxnPercent() {
		return totalWLInvalidTxnPercent;
	}

	public void setTotalWLInvalidTxnPercent(String totalWLInvalidTxnPercent) {
		this.totalWLInvalidTxnPercent = totalWLInvalidTxnPercent;
	}

	public String getTotalWLFraudTxnPercent() {
		return totalWLFraudTxnPercent;
	}

	public void setTotalWLFraudTxnPercent(String totalWLFraudTxnPercent) {
		this.totalWLFraudTxnPercent = totalWLFraudTxnPercent;
	}

	public String getTotalWLDroppedTxnPercent() {
		return totalWLDroppedTxnPercent;
	}

	public void setTotalWLDroppedTxnPercent(String totalWLDroppedTxnPercent) {
		this.totalWLDroppedTxnPercent = totalWLDroppedTxnPercent;
	}

	public String getTotalWLRejectedTxnPercent() {
		return totalWLRejectedTxnPercent;
	}

	public void setTotalWLRejectedTxnPercent(String totalWLRejectedTxnPercent) {
		this.totalWLRejectedTxnPercent = totalWLRejectedTxnPercent;
	}

	public String getTotalWLTxnAmount() {
		return totalWLTxnAmount;
	}

	public void setTotalWLTxnAmount(String totalWLTxnAmount) {
		this.totalWLTxnAmount = totalWLTxnAmount;
	}

	public String getTotalWLCapturedCount() {
		return totalWLCapturedCount;
	}

	public void setTotalWLCapturedCount(String totalWLCapturedCount) {
		this.totalWLCapturedCount = totalWLCapturedCount;
	}

	public String getWlSettledPercentage() {
		return wlSettledPercentage;
	}

	public void setWlSettledPercentage(String wlSettledPercentage) {
		this.wlSettledPercentage = wlSettledPercentage;
	}

	public String getSurchargeFlag() {
		return surchargeFlag;
	}

	public void setSurchargeFlag(String surchargeFlag) {
		this.surchargeFlag = surchargeFlag;
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

	public String getTxnType() {
		return txnType;
	}

	public void setTxnType(String txnType) {
		this.txnType = txnType;
	}

	public String getPaymentsRegion() {
		return paymentsRegion;
	}

	public void setPaymentsRegion(String paymentsRegion) {
		this.paymentsRegion = paymentsRegion;
	}

	public String getCardHolderType() {
		return cardHolderType;
	}

	public void setCardHolderType(String cardHolderType) {
		this.cardHolderType = cardHolderType;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public String getPaymentMethod() {
		return paymentMethod;
	}

	public void setPaymentMethod(String paymentMethod) {
		this.paymentMethod = paymentMethod;
	}

	public String getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(String totalAmount) {
		this.totalAmount = totalAmount;
	}

	public String getMerchantPgRatio() {
		return merchantPgRatio;
	}

	public void setMerchantPgRatio(String merchantPgRatio) {
		this.merchantPgRatio = merchantPgRatio;
	}

	public String getAcquirerPgRatio() {
		return acquirerPgRatio;
	}

	public void setAcquirerPgRatio(String acquirerPgRatio) {
		this.acquirerPgRatio = acquirerPgRatio;
	}

	public String getTotalCCCapturedCount() {
		return totalCCCapturedCount;
	}

	public void setTotalCCCapturedCount(String totalCCCapturedCount) {
		this.totalCCCapturedCount = totalCCCapturedCount;
	}

	public String getTotalDCCapturedCount() {
		return totalDCCapturedCount;
	}

	public void setTotalDCCapturedCount(String totalDCCapturedCount) {
		this.totalDCCapturedCount = totalDCCapturedCount;
	}

	public String getTotalUPCapturedCount() {
		return totalUPCapturedCount;
	}

	public void setTotalUPCapturedCount(String totalUPCapturedCount) {
		this.totalUPCapturedCount = totalUPCapturedCount;
	}

	public String getTotalCCTxnAmount() {
		return totalCCTxnAmount;
	}

	public void setTotalCCTxnAmount(String totalCCTxnAmount) {
		this.totalCCTxnAmount = totalCCTxnAmount;
	}

	public String getTotalDCTxnAmount() {
		return totalDCTxnAmount;
	}

	public void setTotalDCTxnAmount(String totalDCTxnAmount) {
		this.totalDCTxnAmount = totalDCTxnAmount;
	}

	public String getTotalUPTxnAmount() {
		return totalUPTxnAmount;
	}

	public void setTotalUPTxnAmount(String totalUPTxnAmount) {
		this.totalUPTxnAmount = totalUPTxnAmount;
	}

	public String getCapturedPercent() {
		return capturedPercent;
	}

	public void setCapturedPercent(String capturedPercent) {
		this.capturedPercent = capturedPercent;
	}

	public String getFailedPercent() {
		return failedPercent;
	}

	public void setFailedPercent(String failedPercent) {
		this.failedPercent = failedPercent;
	}

	public String getCancelledPercent() {
		return cancelledPercent;
	}

	public void setCancelledPercent(String cancelledPercent) {
		this.cancelledPercent = cancelledPercent;
	}

	public String getInvalidPercent() {
		return invalidPercent;
	}

	public void setInvalidPercent(String invalidPercent) {
		this.invalidPercent = invalidPercent;
	}

	public String getFraudPercent() {
		return fraudPercent;
	}

	public void setFraudPercent(String fraudPercent) {
		this.fraudPercent = fraudPercent;
	}

	public String getDroppedPercent() {
		return droppedPercent;
	}

	public void setDroppedPercent(String droppedPercent) {
		this.droppedPercent = droppedPercent;
	}

	public String getRejectedPercent() {
		return rejectedPercent;
	}

	public void setRejectedPercent(String rejectedPercent) {
		this.rejectedPercent = rejectedPercent;
	}

	public String getCaptured() {
		return captured;
	}

	public void setCaptured(String captured) {
		this.captured = captured;
	}

	public String getFailed() {
		return failed;
	}

	public void setFailed(String failed) {
		this.failed = failed;
	}

	public String getCancelled() {
		return cancelled;
	}

	public void setCancelled(String cancelled) {
		this.cancelled = cancelled;
	}

	public String getInvalid() {
		return invalid;
	}

	public void setInvalid(String invalid) {
		this.invalid = invalid;
	}

	public String getFraud() {
		return fraud;
	}

	public void setFraud(String fraud) {
		this.fraud = fraud;
	}

	public String getDropped() {
		return dropped;
	}

	public void setDropped(String dropped) {
		this.dropped = dropped;
	}

	public String getRejected() {
		return rejected;
	}

	public void setRejected(String rejected) {
		this.rejected = rejected;
	}

	public String getTotalTxnCount() {
		return totalTxnCount;
	}

	public void setTotalTxnCount(String totalTxnCount) {
		this.totalTxnCount = totalTxnCount;
	}

	public String getSuccessTxnCount() {
		return successTxnCount;
	}

	public void setSuccessTxnCount(String successTxnCount) {
		this.successTxnCount = successTxnCount;
	}

	public String getFailedTxnCount() {
		return failedTxnCount;
	}

	public void setFailedTxnCount(String failedTxnCount) {
		this.failedTxnCount = failedTxnCount;
	}

	public String getAvgTkt() {
		return avgTkt;
	}

	public void setAvgTkt(String avgTkt) {
		this.avgTkt = avgTkt;
	}

	public String getCCTxnPercent() {
		return CCTxnPercent;
	}

	public void setCCTxnPercent(String cCTxnPercent) {
		CCTxnPercent = cCTxnPercent;
	}

	public String getCCSuccessRate() {
		return CCSuccessRate;
	}

	public void setCCSuccessRate(String cCSuccessRate) {
		CCSuccessRate = cCSuccessRate;
	}

	public String getDCTxnPercent() {
		return DCTxnPercent;
	}

	public void setDCTxnPercent(String dCTxnPercent) {
		DCTxnPercent = dCTxnPercent;
	}

	public String getDCSuccessRate() {
		return DCSuccessRate;
	}

	public void setDCSuccessRate(String dCSuccessRate) {
		DCSuccessRate = dCSuccessRate;
	}

	public String getUPTxnPercent() {
		return UPTxnPercent;
	}

	public void setUPTxnPercent(String uPTxnPercent) {
		UPTxnPercent = uPTxnPercent;
	}

	public String getUPSuccessRate() {
		return UPSuccessRate;
	}

	public void setUPSuccessRate(String uPSuccessRate) {
		UPSuccessRate = uPSuccessRate;
	}

	public String getTotalCCTxn() {
		return totalCCTxn;
	}

	public void setTotalCCTxn(String totalCCTxn) {
		this.totalCCTxn = totalCCTxn;
	}

	public String getTotalCCSuccessTxnPercent() {
		return totalCCSuccessTxnPercent;
	}

	public void setTotalCCSuccessTxnPercent(String totalCCSuccessTxnPercent) {
		this.totalCCSuccessTxnPercent = totalCCSuccessTxnPercent;
	}

	public String getTotalCCFailedTxnPercent() {
		return totalCCFailedTxnPercent;
	}

	public void setTotalCCFailedTxnPercent(String totalCCFailedTxnPercent) {
		this.totalCCFailedTxnPercent = totalCCFailedTxnPercent;
	}

	public String getTotalCCCancelledTxnPercent() {
		return totalCCCancelledTxnPercent;
	}

	public void setTotalCCCancelledTxnPercent(String totalCCCancelledTxnPercent) {
		this.totalCCCancelledTxnPercent = totalCCCancelledTxnPercent;
	}

	public String getTotalCCInvalidTxnPercent() {
		return totalCCInvalidTxnPercent;
	}

	public void setTotalCCInvalidTxnPercent(String totalCCInvalidTxnPercent) {
		this.totalCCInvalidTxnPercent = totalCCInvalidTxnPercent;
	}

	public String getTotalCCFraudTxnPercent() {
		return totalCCFraudTxnPercent;
	}

	public void setTotalCCFraudTxnPercent(String totalCCFraudTxnPercent) {
		this.totalCCFraudTxnPercent = totalCCFraudTxnPercent;
	}

	public String getTotalCCDroppedTxnPercent() {
		return totalCCDroppedTxnPercent;
	}

	public void setTotalCCDroppedTxnPercent(String totalCCDroppedTxnPercent) {
		this.totalCCDroppedTxnPercent = totalCCDroppedTxnPercent;
	}

	public String getTotalCCRejectedTxnPercent() {
		return totalCCRejectedTxnPercent;
	}

	public void setTotalCCRejectedTxnPercent(String totalCCRejectedTxnPercent) {
		this.totalCCRejectedTxnPercent = totalCCRejectedTxnPercent;
	}

	public String getTotalDCTxn() {
		return totalDCTxn;
	}

	public void setTotalDCTxn(String totalDCTxn) {
		this.totalDCTxn = totalDCTxn;
	}

	public String getTotalDCSuccessTxnPercent() {
		return totalDCSuccessTxnPercent;
	}

	public void setTotalDCSuccessTxnPercent(String totalDCSuccessTxnPercent) {
		this.totalDCSuccessTxnPercent = totalDCSuccessTxnPercent;
	}

	public String getTotalDCFailedTxnPercent() {
		return totalDCFailedTxnPercent;
	}

	public void setTotalDCFailedTxnPercent(String totalDCFailedTxnPercent) {
		this.totalDCFailedTxnPercent = totalDCFailedTxnPercent;
	}

	public String getTotalDCCancelledTxnPercent() {
		return totalDCCancelledTxnPercent;
	}

	public void setTotalDCCancelledTxnPercent(String totalDCCancelledTxnPercent) {
		this.totalDCCancelledTxnPercent = totalDCCancelledTxnPercent;
	}

	public String getTotalDCInvalidTxnPercent() {
		return totalDCInvalidTxnPercent;
	}

	public void setTotalDCInvalidTxnPercent(String totalDCInvalidTxnPercent) {
		this.totalDCInvalidTxnPercent = totalDCInvalidTxnPercent;
	}

	public String getTotalDCFraudTxnPercent() {
		return totalDCFraudTxnPercent;
	}

	public void setTotalDCFraudTxnPercent(String totalDCFraudTxnPercent) {
		this.totalDCFraudTxnPercent = totalDCFraudTxnPercent;
	}

	public String getTotalDCDroppedTxnPercent() {
		return totalDCDroppedTxnPercent;
	}

	public void setTotalDCDroppedTxnPercent(String totalDCDroppedTxnPercent) {
		this.totalDCDroppedTxnPercent = totalDCDroppedTxnPercent;
	}

	public String getTotalDCRejectedTxnPercent() {
		return totalDCRejectedTxnPercent;
	}

	public void setTotalDCRejectedTxnPercent(String totalDCRejectedTxnPercent) {
		this.totalDCRejectedTxnPercent = totalDCRejectedTxnPercent;
	}

	public String getTotalUPTxn() {
		return totalUPTxn;
	}

	public void setTotalUPTxn(String totalUPTxn) {
		this.totalUPTxn = totalUPTxn;
	}

	public String getTotalUPSuccessTxnPercent() {
		return totalUPSuccessTxnPercent;
	}

	public void setTotalUPSuccessTxnPercent(String totalUPSuccessTxnPercent) {
		this.totalUPSuccessTxnPercent = totalUPSuccessTxnPercent;
	}

	public String getTotalUPFailedTxnPercent() {
		return totalUPFailedTxnPercent;
	}

	public void setTotalUPFailedTxnPercent(String totalUPFailedTxnPercent) {
		this.totalUPFailedTxnPercent = totalUPFailedTxnPercent;
	}

	public String getTotalUPCancelledTxnPercent() {
		return totalUPCancelledTxnPercent;
	}

	public void setTotalUPCancelledTxnPercent(String totalUPCancelledTxnPercent) {
		this.totalUPCancelledTxnPercent = totalUPCancelledTxnPercent;
	}

	public String getTotalUPInvalidTxnPercent() {
		return totalUPInvalidTxnPercent;
	}

	public void setTotalUPInvalidTxnPercent(String totalUPInvalidTxnPercent) {
		this.totalUPInvalidTxnPercent = totalUPInvalidTxnPercent;
	}

	public String getTotalUPFraudTxnPercent() {
		return totalUPFraudTxnPercent;
	}

	public void setTotalUPFraudTxnPercent(String totalUPFraudTxnPercent) {
		this.totalUPFraudTxnPercent = totalUPFraudTxnPercent;
	}

	public String getTotalUPDroppedTxnPercent() {
		return totalUPDroppedTxnPercent;
	}

	public void setTotalUPDroppedTxnPercent(String totalUPDroppedTxnPercent) {
		this.totalUPDroppedTxnPercent = totalUPDroppedTxnPercent;
	}

	public String getTotalUPRejectedTxnPercent() {
		return totalUPRejectedTxnPercent;
	}

	public void setTotalUPRejectedTxnPercent(String totalUPRejectedTxnPercent) {
		this.totalUPRejectedTxnPercent = totalUPRejectedTxnPercent;
	}

	public String getUnknownTxnCount() {
		return unknownTxnCount;
	}

	public void setUnknownTxnCount(String unknownTxnCount) {
		this.unknownTxnCount = unknownTxnCount;
	}

	public String getSuccessTxnPercent() {
		return successTxnPercent;
	}

	public void setSuccessTxnPercent(String successTxnPercent) {
		this.successTxnPercent = successTxnPercent;
	}

	public String getTotalCapturedTxnAmount() {
		return totalCapturedTxnAmount;
	}

	public void setTotalCapturedTxnAmount(String totalCapturedTxnAmount) {
		this.totalCapturedTxnAmount = totalCapturedTxnAmount;
	}

	public String getTotalRejectedTxnPercent() {
		return totalRejectedTxnPercent;
	}

	public void setTotalRejectedTxnPercent(String totalRejectedTxnPercent) {
		this.totalRejectedTxnPercent = totalRejectedTxnPercent;
	}

	public String getIpayProfitCumm() {
		return ipayProfitCumm;
	}

	public void setIpayProfitCumm(String ipayProfitCumm) {
		this.ipayProfitCumm = ipayProfitCumm;
	}

	public String getIpayProfitInclGstCumm() {
		return ipayProfitInclGstCumm;
	}

	public void setIpayProfitInclGstCumm(String ipayProfitInclGstCumm) {
		this.ipayProfitInclGstCumm = ipayProfitInclGstCumm;
	}

	public String getIpayProfitExcGstCumm() {
		return ipayProfitExcGstCumm;
	}

	public void setIpayProfitExcGstCumm(String ipayProfitExcGstCumm) {
		this.ipayProfitExcGstCumm = ipayProfitExcGstCumm;
	}

	public String getIpayProfit() {
		return ipayProfit;
	}

	public void setIpayProfit(String ipayProfit) {
		this.ipayProfit = ipayProfit;
	}

	public String getIpayProfitInclGst() {
		return ipayProfitInclGst;
	}

	public void setIpayProfitInclGst(String ipayProfitInclGst) {
		this.ipayProfitInclGst = ipayProfitInclGst;
	}

	public String getIpayProfitExcGst() {
		return ipayProfitExcGst;
	}

	public void setIpayProfitExcGst(String ipayProfitExcGst) {
		this.ipayProfitExcGst = ipayProfitExcGst;
	}

	public String getDateSettled() {
		return dateSettled;
	}

	public void setDateSettled(String dateSettled) {
		this.dateSettled = dateSettled;
	}

	public String getDateCaptured() {
		return dateCaptured;
	}

	public void setDateCaptured(String dateCaptured) {
		this.dateCaptured = dateCaptured;
	}

	public String getIpayProfitAmount() {
		return ipayProfitAmount;
	}

	public void setIpayProfitAmount(String ipayProfitAmount) {
		this.ipayProfitAmount = ipayProfitAmount;
	}

	public String getCcSettledPercentage() {
		return ccSettledPercentage;
	}

	public void setCcSettledPercentage(String ccSettledPercentage) {
		this.ccSettledPercentage = ccSettledPercentage;
	}

	public String getDcSettledPercentage() {
		return dcSettledPercentage;
	}

	public void setDcSettledPercentage(String dcSettledPercentage) {
		this.dcSettledPercentage = dcSettledPercentage;
	}

	public String getUpSettledPercentage() {
		return upSettledPercentage;
	}

	public void setUpSettledPercentage(String upSettledPercentage) {
		this.upSettledPercentage = upSettledPercentage;
	}

	public String getAvgSettlementAmount() {
		return avgSettlementAmount;
	}

	public void setAvgSettlementAmount(String avgSettlementAmount) {
		this.avgSettlementAmount = avgSettlementAmount;
	}

	public String getMerchantSaleSettledAmount() {
		return merchantSaleSettledAmount;
	}

	public void setMerchantSaleSettledAmount(String merchantSaleSettledAmount) {
		this.merchantSaleSettledAmount = merchantSaleSettledAmount;
	}

	public String getMerchantRefundSettledAmount() {
		return merchantRefundSettledAmount;
	}

	public void setMerchantRefundSettledAmount(String merchantRefundSettledAmount) {
		this.merchantRefundSettledAmount = merchantRefundSettledAmount;
	}

	public String getTotalProfit() {
		return totalProfit;
	}

	public void setTotalProfit(String totalProfit) {
		this.totalProfit = totalProfit;
	}

	public String getPostSettledTransactionCount() {
		return postSettledTransactionCount;
	}

	public void setPostSettledTransactionCount(String postSettledTransactionCount) {
		this.postSettledTransactionCount = postSettledTransactionCount;
	}

	public String getActualSettlementAmount() {
		return actualSettlementAmount;
	}

	public void setActualSettlementAmount(String actualSettlementAmount) {
		this.actualSettlementAmount = actualSettlementAmount;
	}

	public String getMerchantName() {
		return merchantName;
	}

	public void setMerchantName(String merchantName) {
		this.merchantName = merchantName;
	}

	public String getSaleCapturedAmount() {
		return saleCapturedAmount;
	}

	public void setSaleCapturedAmount(String saleCapturedAmount) {
		this.saleCapturedAmount = saleCapturedAmount;
	}

	public String getSaleCapturedCount() {
		return saleCapturedCount;
	}

	public void setSaleCapturedCount(String saleCapturedCount) {
		this.saleCapturedCount = saleCapturedCount;
	}

	public String getPgSaleSurcharge() {
		return pgSaleSurcharge;
	}

	public void setPgSaleSurcharge(String pgSaleSurcharge) {
		this.pgSaleSurcharge = pgSaleSurcharge;
	}

	public String getAcquirerSaleSurcharge() {
		return acquirerSaleSurcharge;
	}

	public void setAcquirerSaleSurcharge(String acquirerSaleSurcharge) {
		this.acquirerSaleSurcharge = acquirerSaleSurcharge;
	}

	public String getPgSaleGst() {
		return pgSaleGst;
	}

	public void setPgSaleGst(String pgSaleGst) {
		this.pgSaleGst = pgSaleGst;
	}

	public String getAcquirerSaleGst() {
		return acquirerSaleGst;
	}

	public void setAcquirerSaleGst(String acquirerSaleGst) {
		this.acquirerSaleGst = acquirerSaleGst;
	}

	public String getRefundCapturedAmount() {
		return refundCapturedAmount;
	}

	public void setRefundCapturedAmount(String refundCapturedAmount) {
		this.refundCapturedAmount = refundCapturedAmount;
	}

	public String getRefundCapturedCount() {
		return refundCapturedCount;
	}

	public void setRefundCapturedCount(String refundCapturedCount) {
		this.refundCapturedCount = refundCapturedCount;
	}

	public String getPgRefundSurcharge() {
		return pgRefundSurcharge;
	}

	public void setPgRefundSurcharge(String pgRefundSurcharge) {
		this.pgRefundSurcharge = pgRefundSurcharge;
	}

	public String getAcquirerRefundSurcharge() {
		return acquirerRefundSurcharge;
	}

	public void setAcquirerRefundSurcharge(String acquirerRefundSurcharge) {
		this.acquirerRefundSurcharge = acquirerRefundSurcharge;
	}

	public String getPgRefundGst() {
		return pgRefundGst;
	}

	public void setPgRefundGst(String pgRefundGst) {
		this.pgRefundGst = pgRefundGst;
	}

	public String getAcquirerRefundGst() {
		return acquirerRefundGst;
	}

	public void setAcquirerRefundGst(String acquirerRefundGst) {
		this.acquirerRefundGst = acquirerRefundGst;
	}

	public String getTotalMerchantAmount() {
		return totalMerchantAmount;
	}

	public void setTotalMerchantAmount(String totalMerchantAmount) {
		this.totalMerchantAmount = totalMerchantAmount;
	}

	public String getAcquirer() {
		return acquirer;
	}

	public void setAcquirer(String acquirer) {
		this.acquirer = acquirer;
	}

	public String getCapturedTotalAmount() {
		return capturedTotalAmount;
	}

	public void setCapturedTotalAmount(String capturedTotalAmount) {
		this.capturedTotalAmount = capturedTotalAmount;
	}

	public String getFailedTotalAmount() {
		return failedTotalAmount;
	}

	public void setFailedTotalAmount(String failedTotalAmount) {
		this.failedTotalAmount = failedTotalAmount;
	}

	public String getCancelledTotalAmount() {
		return cancelledTotalAmount;
	}

	public void setCancelledTotalAmount(String cancelledTotalAmount) {
		this.cancelledTotalAmount = cancelledTotalAmount;
	}

	public String getInvalidTotalAmount() {
		return invalidTotalAmount;
	}

	public void setInvalidTotalAmount(String invalidTotalAmount) {
		this.invalidTotalAmount = invalidTotalAmount;
	}

}
