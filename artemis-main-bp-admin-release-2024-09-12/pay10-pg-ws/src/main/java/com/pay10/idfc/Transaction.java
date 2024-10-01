package com.pay10.idfc;

import org.springframework.stereotype.Service;

@Service("idfcTransaction")
public class Transaction {

	// Merchant Details
	//MID|PID|AMT|TxnType|ACCNO|NAR|BID|ResponseCode|ResponseMsg|PAID

	private String MID;
	private String PID;
	private String AMT;
private String REFUNDAMT;
private String TXNAMT;
	// Transaction Details Data

	private String TxnType;
	private String ACCNO;
	private String NAR;
	private String BID;
	private String ResponseCode;
	private String ResponseMsg;
	private String PAID;
	private String MCC;
	public String getMID() {
		return MID;
	}
	public void setMID(String mID) {
		MID = mID;
	}
	public String getPID() {
		return PID;
	}
	public void setPID(String pID) {
		PID = pID;
	}
	public String getAMT() {
		return AMT;
	}
	public void setAMT(String aMT) {
		AMT = aMT;
	}
	public String getTxnType() {
		return TxnType;
	}
	public void setTxnType(String txnType) {
		TxnType = txnType;
	}
	public String getACCNO() {
		return ACCNO;
	}
	public void setACCNO(String aCCNO) {
		ACCNO = aCCNO;
	}
	public String getNAR() {
		return NAR;
	}
	public void setNAR(String nAR) {
		NAR = nAR;
	}
	public String getBID() {
		return BID;
	}
	public void setBID(String bID) {
		BID = bID;
	}
	public String getResponseCode() {
		return ResponseCode;
	}
	public void setResponseCode(String responseCode) {
		ResponseCode = responseCode;
	}
	public String getResponseMsg() {
		return ResponseMsg;
	}
	public void setResponseMsg(String responseMsg) {
		ResponseMsg = responseMsg;
	}
	public String getPAID() {
		return PAID;
	}
	public void setPAID(String pAID) {
		PAID = pAID;
	}
	public String getMCC() {
		return MCC;
	}
	public void setMCC(String mCC) {
		MCC = mCC;
	}
	public String getREFUNDAMT() {
		return REFUNDAMT;
	}
	public void setREFUNDAMT(String rEFUNDAMT) {
		REFUNDAMT = rEFUNDAMT;
	}
	public String getTXNAMT() {
		return TXNAMT;
	}
	public void setTXNAMT(String tXNAMT) {
		TXNAMT = tXNAMT;
	}
	@Override
	public String toString() {
		return "Transaction [MID=" + MID + ", PID=" + PID + ", AMT=" + AMT + ", REFUNDAMT=" + REFUNDAMT + ", TXNAMT="
				+ TXNAMT + ", TxnType=" + TxnType + ", ACCNO=" + ACCNO + ", NAR=" + NAR + ", BID=" + BID
				+ ", ResponseCode=" + ResponseCode + ", ResponseMsg=" + ResponseMsg + ", PAID=" + PAID + ", MCC=" + MCC
				+ "]";
	}
	
	
}
