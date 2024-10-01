package com.pay10.commons.dto;

public class PassbookPODTO {

	private String txnId;
	private String payId;
	private String businessName;
	private String createDate;
	private String amount;
	private String type;
	private String narration;
	private String debitAmt;
	private String creditAmt;
	private String respTxn;
	private String currency;
	private String insertDate;

	private String openingBalance;
	private String closingBalance;

	public String getRespTxn() {
		return respTxn;
	}

	public void setRespTxn(String respTxn) {
		this.respTxn = respTxn;
	}

	public String getCreditAmt() {
		return creditAmt;
	}

	public void setCreditAmt(String creditAmt) {
		this.creditAmt = creditAmt;
	}

	public String getDebitAmt() {
		return debitAmt;
	}

	public void setDebitAmt(String debitAmt) {
		this.debitAmt = debitAmt;
	}

	public String getTxnId() {
		return txnId;
	}

	public void setTxnId(String txnId) {
		this.txnId = txnId;
	}

	public String getPayId() {
		return payId;
	}

	public void setPayId(String payId) {
		this.payId = payId;
	}

	public String getBusinessName() {
		return businessName;
	}

	public void setBusinessName(String businessName) {
		this.businessName = businessName;
	}

	public String getCreateDate() {
		return createDate;
	}

	public void setCreateDate(String createDate) {
		this.createDate = createDate;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getNarration() {
		return narration;
	}

	public void setNarration(String narration) {
		this.narration = narration;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public String getOpeningBalance() {
		return openingBalance;
	}

	public void setOpeningBalance(String openingBalance) {
		this.openingBalance = openingBalance;
	}

	public String getClosingBalance() {
		return closingBalance;
	}

	public void setClosingBalance(String closingBalance) {
		this.closingBalance = closingBalance;
	}

	public String getInsertDate() {
		return insertDate;
	}

	public void setInsertDate(String insertDate) {
		this.insertDate = insertDate;
	}

	@Override
	public String toString() {
		return "PassbookPODTO [txnId=" + txnId + ", payId=" + payId + ", businessName=" + businessName + ", createDate="
				+ createDate + ", amount=" + amount + ", type=" + type + ", narration=" + narration + ", debitAmt="
				+ debitAmt + ", creditAmt=" + creditAmt + ", respTxn=" + respTxn + ", currency=" + currency + "]";
	}

}
