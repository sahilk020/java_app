package com.pay10.camspay;

import org.springframework.stereotype.Service;

@Service("camsPayTransaction")
public class Transaction {

	private String txnId;
	private String amount;
	private String responseCode;
	private String responseMsg;
	private String bankRefNum;
	private String status;

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

	public String getResponseCode() {
		return responseCode;
	}

	public void setResponseCode(String responseCode) {
		this.responseCode = responseCode;
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

}
