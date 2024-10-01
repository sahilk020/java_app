package com.pay10.apbl;

import org.springframework.stereotype.Service;

@Service("apblTransaction")
public class Transaction {

	private String MID;
	private String TXN_REF_NO;
	private String SU;
	private String FU;
	private String AMT;
	private String DATE;
	private String CUR;
	private String END_MID;
	private String CUST_MOBILE;
	private String MNAME;
	private String service;
	private String HASH;
	
	
	private String STATUS;
	private String CODE;
	private String MSG;
	private String TRAN_ID;
	private String TRAN_AMT;
	private String TRAN_CUR;
	private String TRAN_DATE;
	
	private String feSessionId;
	private String txnId;
	private String request;
	
	private String new_txn_id;
	private String messageText;
	private String errorCode;
	
	
	public String getMID() {
		return MID;
	}
	public void setMID(String mID) {
		MID = mID;
	}
	public String getTXN_REF_NO() {
		return TXN_REF_NO;
	}
	public void setTXN_REF_NO(String tXN_REF_NO) {
		TXN_REF_NO = tXN_REF_NO;
	}
	public String getSU() {
		return SU;
	}
	public void setSU(String sU) {
		SU = sU;
	}
	public String getFU() {
		return FU;
	}
	public void setFU(String fU) {
		FU = fU;
	}
	public String getAMT() {
		return AMT;
	}
	public void setAMT(String aMT) {
		AMT = aMT;
	}
	public String getDATE() {
		return DATE;
	}
	public void setDATE(String dATE) {
		DATE = dATE;
	}
	public String getCUR() {
		return CUR;
	}
	public void setCUR(String cUR) {
		CUR = cUR;
	}
	public String getEND_MID() {
		return END_MID;
	}
	public void setEND_MID(String eND_MID) {
		END_MID = eND_MID;
	}
	public String getCUST_MOBILE() {
		return CUST_MOBILE;
	}
	public void setCUST_MOBILE(String cUST_MOBILE) {
		CUST_MOBILE = cUST_MOBILE;
	}
	public String getMNAME() {
		return MNAME;
	}
	public void setMNAME(String mNAME) {
		MNAME = mNAME;
	}
	public String getService() {
		return service;
	}
	public void setService(String service) {
		this.service = service;
	}
	public String getHASH() {
		return HASH;
	}
	public void setHASH(String hASH) {
		HASH = hASH;
	}
	public String getSTATUS() {
		return STATUS;
	}
	public void setSTATUS(String sTATUS) {
		STATUS = sTATUS;
	}
	public String getCODE() {
		return CODE;
	}
	public void setCODE(String cODE) {
		CODE = cODE;
	}
	public String getMSG() {
		return MSG;
	}
	public void setMSG(String mSG) {
		MSG = mSG;
	}
	public String getTRAN_ID() {
		return TRAN_ID;
	}
	public void setTRAN_ID(String tRAN_ID) {
		TRAN_ID = tRAN_ID;
	}
	public String getTRAN_AMT() {
		return TRAN_AMT;
	}
	public void setTRAN_AMT(String tRAN_AMT) {
		TRAN_AMT = tRAN_AMT;
	}
	public String getTRAN_CUR() {
		return TRAN_CUR;
	}
	public void setTRAN_CUR(String tRAN_CUR) {
		TRAN_CUR = tRAN_CUR;
	}
	public String getTRAN_DATE() {
		return TRAN_DATE;
	}
	public void setTRAN_DATE(String tRAN_DATE) {
		TRAN_DATE = tRAN_DATE;
	}
	public String getFeSessionId() {
		return feSessionId;
	}
	public void setFeSessionId(String feSessionId) {
		this.feSessionId = feSessionId;
	}
	public String getTxnId() {
		return txnId;
	}
	public void setTxnId(String txnId) {
		this.txnId = txnId;
	}
	public String getRequest() {
		return request;
	}
	public void setRequest(String request) {
		this.request = request;
	}
	public String getNew_txn_id() {
		return new_txn_id;
	}
	public void setNew_txn_id(String new_txn_id) {
		this.new_txn_id = new_txn_id;
	}
	public String getMessageText() {
		return messageText;
	}
	public void setMessageText(String messageText) {
		this.messageText = messageText;
	}
	public String getErrorCode() {
		return errorCode;
	}
	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	
	
}
