package com.pay10.nodal.payout;

import org.springframework.stereotype.Service;

@Service("settlementTransaction")
public class Transaction {

	private String uniqueResponseNo;
	private String attemptNo;
	private String statusCode;
	private String subStatusCode;
	private String responeMessage;
	private String bankReferenceNo;
	private String beneficiaryReferenceNo;
	private String transactionDate;
	private String transferAmount;
	private String status;
	private String rrn;

	// kotak nodal payout fields response
	private String kotakMessageId;
	private String kotakStatusCd;
	private String kotakStatusRem;

	// kotak nodal payout status enquiry response
	private String enquiryReqId;
	private String enquiryDatePost;
	private String enquiryMsgId;

	public String getenquiryReqId() {
		return enquiryReqId;
	}

	public void setenquiryReqId(String enquiryReqId) {
		this.enquiryReqId = enquiryReqId;
	}

	public String getenquiryDatePost() {
		return enquiryDatePost;
	}

	public void setenquiryDatePost(String enquiryDatePost) {
		this.enquiryDatePost = enquiryDatePost;
	}

	public String getenquiryMsgId() {
		return enquiryMsgId;
	}

	public void setenquiryMsgId(String enquiryMsgId) {
		this.enquiryMsgId = enquiryMsgId;
	}
	
	public String getKotakMessageId() {
		return kotakMessageId;
	}

	public void setKotakMessageId(String kotakMessageId) {
		this.kotakMessageId = kotakMessageId;
	}

	public String getKotakStatusCd() {
		return kotakStatusCd;
	}

	public void setKotakStatusCd(String kotakStatusCd) {
		this.kotakStatusCd = kotakStatusCd;
	}

	public String getKotakStatusRem() {
		return kotakStatusRem;
	}

	public void setKotakStatusRem(String kotakStatusRem) {
		this.kotakStatusRem = kotakStatusRem;
	}

	public String getUniqueResponseNo() {
		return uniqueResponseNo;
	}

	public void setUniqueResponseNo(String uniqueResponseNo) {
		this.uniqueResponseNo = uniqueResponseNo;
	}

	public String getAttemptNo() {
		return attemptNo;
	}

	public void setAttemptNo(String attemptNo) {
		this.attemptNo = attemptNo;
	}

	public String getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(String statusCode) {
		this.statusCode = statusCode;
	}

	public String getSubStatusCode() {
		return subStatusCode;
	}

	public void setSubStatusCode(String subStatusCode) {
		this.subStatusCode = subStatusCode;
	}

	public String getResponeMessage() {
		return responeMessage;
	}

	public void setResponeMessage(String responeMessage) {
		this.responeMessage = responeMessage;
	}

	public String getBankReferenceNo() {
		return bankReferenceNo;
	}

	public void setBankReferenceNo(String bankReferenceNo) {
		this.bankReferenceNo = bankReferenceNo;
	}

	public String getBeneficiaryReferenceNo() {
		return beneficiaryReferenceNo;
	}

	public void setBeneficiaryReferenceNo(String beneficiaryReferenceNo) {
		this.beneficiaryReferenceNo = beneficiaryReferenceNo;
	}

	public String getTransactionDate() {
		return transactionDate;
	}

	public void setTransactionDate(String transactionDate) {
		this.transactionDate = transactionDate;
	}

	public String getTransferAmount() {
		return transferAmount;
	}

	public void setTransferAmount(String transferAmount) {
		this.transferAmount = transferAmount;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getRrn() {
		return rrn;
	}

	public void setRrn(String rrn) {
		this.rrn = rrn;
	}

}
