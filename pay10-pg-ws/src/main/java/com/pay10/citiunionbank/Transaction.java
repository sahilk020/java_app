package com.pay10.citiunionbank;

import org.springframework.stereotype.Service;

@Service("cityUnionTransaction")
public class Transaction {

	
	private String txnType;
	private String responseCode;
	private String acqId;
	private String authCode;
	private String terminalId;
	private String amount;
	private String maskedCardNo;
	private String txnRefNo;
	private String message;
	private String merchantId;
	private String orderInfo;
	private String cardType;
	private String bacthNo;
	private String status;

	private String messagCode;
	private String dateAndTime;
	private String merchantReference;
	private String authStatus;
	private String bankReferenceNumber;
	private String fUP1;
	private String fUP2;
	private String fUP3;
	private String pid;

	private String paid;
	private String bid;
	private String prn;
	private String amt;
	private String crn;
	private String itc;
	public String getTxnType() {
		return txnType;
	}
	public void setTxnType(String txnType) {
		this.txnType = txnType;
	}
	public String getResponseCode() {
		return responseCode;
	}
	public void setResponseCode(String responseCode) {
		this.responseCode = responseCode;
	}
	public String getAcqId() {
		return acqId;
	}
	public void setAcqId(String acqId) {
		this.acqId = acqId;
	}
	public String getAuthCode() {
		return authCode;
	}
	public void setAuthCode(String authCode) {
		this.authCode = authCode;
	}
	public String getTerminalId() {
		return terminalId;
	}
	public void setTerminalId(String terminalId) {
		this.terminalId = terminalId;
	}
	public String getAmount() {
		return amount;
	}
	public void setAmount(String amount) {
		this.amount = amount;
	}
	public String getMaskedCardNo() {
		return maskedCardNo;
	}
	public void setMaskedCardNo(String maskedCardNo) {
		this.maskedCardNo = maskedCardNo;
	}
	public String getTxnRefNo() {
		return txnRefNo;
	}
	public void setTxnRefNo(String txnRefNo) {
		this.txnRefNo = txnRefNo;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getMerchantId() {
		return merchantId;
	}
	public void setMerchantId(String merchantId) {
		this.merchantId = merchantId;
	}
	public String getOrderInfo() {
		return orderInfo;
	}
	public void setOrderInfo(String orderInfo) {
		this.orderInfo = orderInfo;
	}
	public String getCardType() {
		return cardType;
	}
	public void setCardType(String cardType) {
		this.cardType = cardType;
	}
	public String getBacthNo() {
		return bacthNo;
	}
	public void setBacthNo(String bacthNo) {
		this.bacthNo = bacthNo;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getMessagCode() {
		return messagCode;
	}
	public void setMessagCode(String messagCode) {
		this.messagCode = messagCode;
	}
	public String getDateAndTime() {
		return dateAndTime;
	}
	public void setDateAndTime(String dateAndTime) {
		this.dateAndTime = dateAndTime;
	}
	public String getMerchantReference() {
		return merchantReference;
	}
	public void setMerchantReference(String merchantReference) {
		this.merchantReference = merchantReference;
	}
	public String getAuthStatus() {
		return authStatus;
	}
	public void setAuthStatus(String authStatus) {
		this.authStatus = authStatus;
	}
	public String getBankReferenceNumber() {
		return bankReferenceNumber;
	}
	public void setBankReferenceNumber(String bankReferenceNumber) {
		this.bankReferenceNumber = bankReferenceNumber;
	}
	public String getfUP1() {
		return fUP1;
	}
	public void setfUP1(String fUP1) {
		this.fUP1 = fUP1;
	}
	public String getfUP2() {
		return fUP2;
	}
	public void setfUP2(String fUP2) {
		this.fUP2 = fUP2;
	}
	public String getfUP3() {
		return fUP3;
	}
	public void setfUP3(String fUP3) {
		this.fUP3 = fUP3;
	}
	public String getPid() {
		return pid;
	}
	public void setPid(String pid) {
		this.pid = pid;
	}
	public String getPaid() {
		return paid;
	}
	public void setPaid(String paid) {
		this.paid = paid;
	}
	public String getBid() {
		return bid;
	}
	public void setBid(String bid) {
		this.bid = bid;
	}
	public String getPrn() {
		return prn;
	}
	public void setPrn(String prn) {
		this.prn = prn;
	}
	public String getAmt() {
		return amt;
	}
	public void setAmt(String amt) {
		this.amt = amt;
	}
	public String getCrn() {
		return crn;
	}
	public void setCrn(String crn) {
		this.crn = crn;
	}
	public String getItc() {
		return itc;
	}
	public void setItc(String itc) {
		this.itc = itc;
	}
	@Override
	public String toString() {
		return "Transaction [txnType=" + txnType + ", responseCode=" + responseCode + ", acqId=" + acqId + ", authCode="
				+ authCode + ", terminalId=" + terminalId + ", amount=" + amount + ", maskedCardNo=" + maskedCardNo
				+ ", txnRefNo=" + txnRefNo + ", message=" + message + ", merchantId=" + merchantId + ", orderInfo="
				+ orderInfo + ", cardType=" + cardType + ", bacthNo=" + bacthNo + ", status=" + status + ", messagCode="
				+ messagCode + ", dateAndTime=" + dateAndTime + ", merchantReference=" + merchantReference
				+ ", authStatus=" + authStatus + ", bankReferenceNumber=" + bankReferenceNumber + ", fUP1=" + fUP1
				+ ", fUP2=" + fUP2 + ", fUP3=" + fUP3 + ", pid=" + pid + ", paid=" + paid + ", bid=" + bid + ", prn="
				+ prn + ", amt=" + amt + ", crn=" + crn + ", itc=" + itc + "]";
	}

	
}
