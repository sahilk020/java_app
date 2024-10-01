package com.pay10.commons.user;

public class MasterReportDto {	
	private String transactionStatusDate;
	private String acquirer;
	private String txnType;
	private String status;
	private String countOftransaction;
	private String baseAmount;
	private String totalAmount;
	private String acquirerTDRAmontTotal;
	private String gSTOnAcquirerTDR;							
	private String pgTDRAmountTotal;
	private String gSTOnPgTDR;
	private String amountPayableToMerchant;
	private String amountReceivedInNodal;
	
	
	public String getTransactionStatusDate() {
		return transactionStatusDate;
	}
	public void setTransactionStatusDate(String transactionStatusDate) {
		this.transactionStatusDate = transactionStatusDate;
	}
	public String getAcquirer() {
		return acquirer;
	}
	public void setAcquirer(String acquirer) {
		this.acquirer = acquirer;
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
	public String getCountOftransaction() {
		return countOftransaction;
	}
	public void setCountOftransaction(String countOftransaction) {
		this.countOftransaction = countOftransaction;
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
	public String getAcquirerTDRAmontTotal() {
		return acquirerTDRAmontTotal;
	}
	public void setAcquirerTDRAmontTotal(String acquirerTDRAmontTotal) {
		this.acquirerTDRAmontTotal = acquirerTDRAmontTotal;
	}
	public String getgSTOnAcquirerTDR() {
		return gSTOnAcquirerTDR;
	}
	public void setgSTOnAcquirerTDR(String gSTOnAcquirerTDR) {
		this.gSTOnAcquirerTDR = gSTOnAcquirerTDR;
	}
	public String getPgTDRAmountTotal() {
		return pgTDRAmountTotal;
	}
	public void setPgTDRAmountTotal(String pgTDRAmountTotal) {
		this.pgTDRAmountTotal = pgTDRAmountTotal;
	}
	public String getgSTOnPgTDR() {
		return gSTOnPgTDR;
	}
	public void setgSTOnPgTDR(String gSTOnPgTDR) {
		this.gSTOnPgTDR = gSTOnPgTDR;
	}
	public String getAmountPayableToMerchant() {
		return amountPayableToMerchant;
	}
	public void setAmountPayableToMerchant(String amountPayableToMerchant) {
		this.amountPayableToMerchant = amountPayableToMerchant;
	}
	public String getAmountReceivedInNodal() {
		return amountReceivedInNodal;
	}
	public void setAmountReceivedInNodal(String amountReceivedInNodal) {
		this.amountReceivedInNodal = amountReceivedInNodal;
	}
	
	
}
