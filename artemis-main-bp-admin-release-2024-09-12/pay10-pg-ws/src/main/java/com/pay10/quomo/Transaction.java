package com.pay10.quomo;

import org.springframework.stereotype.Service;

@Service("quomoTransaction")
public class Transaction {

	private String txnId;
	private String amount;
	private String pgRefNum;
	private String responseMsg;
	private String bankRefNum;
	private String status;
	private String merchantId;
	private String currency;
	private String signData;

	public String getTxnId() {
		return txnId;
	}

	public void setTxnId(String txnId) {
		this.txnId = txnId;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public String getPgRefNum() {
		return pgRefNum;
	}

	public void setPgRefNum(String pgRefNum) {
		this.pgRefNum = pgRefNum;
	}

	public String getResponseMsg() {
		return responseMsg;
	}

	public void setResponseMsg(String responseMsg) {
		this.responseMsg = responseMsg;
	}

	public String getBankRefNum() {
		return bankRefNum;
	}

	public void setBankRefNum(String bankRefNum) {
		this.bankRefNum = bankRefNum;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getMerchantId() {
		return merchantId;
	}

	public void setMerchantId(String merchantId) {
		this.merchantId = merchantId;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public String getSignData() {
		return signData;
	}

	public void setSignData(String signData) {
		this.signData = signData;
	}

	@Override
	public String toString() {
		return "Transaction [txnId=" + txnId + ", amount=" + amount + ", pgRefNum=" + pgRefNum + ", responseMsg="
				+ responseMsg + ", bankRefNum=" + bankRefNum + ", status=" + status + ", merchantId=" + merchantId
				+ ", currency=" + currency + ", signData=" + signData + "]";
	}

}
