package com.pay10.migs;

import org.springframework.stereotype.Service;

import com.pay10.commons.util.Fields;

@Service("migsTransaction")

public class Transaction {

	private String responseCode;
	private String acqResponseCode;
	private String RRN;
	private String pgTransactionNo;
	private String authorizeId;
	private String merchTxnRef; // txnId for every transaction
	private String orderInfo;
	// a unique identifier for a set of txns ie auth,
	

	// cap, refund etc
	private String message;
	private String amount;
	private String command;
	private String secureHash;
	private String secureHashType;
	private String merchantId;
	private String eciFlag;
	private String threeDSXID;
	private String verToken;
	private String verStatus;
	private String verType;
	private String verSecurityLevel;
	private String threeDSenrolled;
	private String threeDSstatus;
	private String batchNo;
    private String drExists;
    
	public String getDrExists() {
		return drExists;
	}

	public void setDrExists(String drExists) {
		this.drExists = drExists;
	}

	public void setEnrollment(Fields fields) {
		setCardDetails(fields);
		setAction(fields);
		setMerchantInformation(fields);
		setOrderInformation(fields);
	}

	private void setCardDetails(Fields fields) {

	}

	private void setAction(Fields fields) {

	}

	private void setMerchantInformation(Fields fields) {

	}

	private void setOrderInformation(Fields fields) {

	}

	public String getAcqResponseCode() {
		return acqResponseCode;
	}

	public void setAcqResponseCode(String acqResponseCode) {
		this.acqResponseCode = acqResponseCode;
	}

	public String getRRN() {
		return RRN;
	}

	public void setRRN(String rRN) {
		RRN = rRN;
	}

	public String getAuthorizeId() {
		return authorizeId;
	}

	public void setAuthorizeId(String authorizeId) {
		this.authorizeId = authorizeId;
	}

	public String getMerchTxnRef() {
		return merchTxnRef;
	}

	public void setMerchTxnRef(String merchTxnRef) {
		this.merchTxnRef = merchTxnRef;
	}

	public String getOrderInfo() {
		return orderInfo;
	}

	public void setOrderInfo(String orderInfo) {
		this.orderInfo = orderInfo;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public String getCommand() {
		return command;
	}

	public void setCommand(String command) {
		this.command = command;
	}

	public String getSecureHash() {
		return secureHash;
	}

	public void setSecureHash(String secureHash) {
		this.secureHash = secureHash;
	}

	public String getMerchantId() {
		return merchantId;
	}

	public void setMerchantId(String merchantId) {
		this.merchantId = merchantId;
	}

	public String getSecureHashType() {
		return secureHashType;
	}

	public void setSecureHashType(String secureHashType) {
		this.secureHashType = secureHashType;
	}

	public String getPgTransactionNo() {
		return pgTransactionNo;
	}

	public void setPgTransactionNo(String pgTransactionNo) {
		this.pgTransactionNo = pgTransactionNo;
	}

	public String getEciFlag() {
		return eciFlag;
	}

	public void setEciFlag(String eciFlag) {
		this.eciFlag = eciFlag;
	}

	public String getThreeDSXID() {
		return threeDSXID;
	}

	public void setThreeDSXID(String threeDSXID) {
		this.threeDSXID = threeDSXID;
	}

	public String getVerToken() {
		return verToken;
	}

	public void setVerToken(String verToken) {
		this.verToken = verToken;
	}

	public String getVerStatus() {
		return verStatus;
	}

	public void setVerStatus(String verStatus) {
		this.verStatus = verStatus;
	}

	public String getVerType() {
		return verType;
	}

	public void setVerType(String verType) {
		this.verType = verType;
	}

	public String getVerSecurityLevel() {
		return verSecurityLevel;
	}

	public void setVerSecurityLevel(String verSecurityLevel) {
		this.verSecurityLevel = verSecurityLevel;
	}

	public String getThreeDSenrolled() {
		return threeDSenrolled;
	}

	public void setThreeDSenrolled(String threeDSenrolled) {
		this.threeDSenrolled = threeDSenrolled;
	}

	public String getThreeDSstatus() {
		return threeDSstatus;
	}

	public void setThreeDSstatus(String threeDSstatus) {
		this.threeDSstatus = threeDSstatus;
	}

	public String getBatchNo() {
		return batchNo;
	}

	public void setBatchNo(String batchNo) {
		this.batchNo = batchNo;
	}

	public String getResponseCode() {
		return responseCode;
	}

	public void setResponseCode(String responseCode) {
		this.responseCode = responseCode;
	}
	
}
