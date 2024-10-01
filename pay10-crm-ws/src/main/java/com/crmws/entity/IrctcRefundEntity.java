package com.crmws.entity;

import javax.persistence.Column;

import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class IrctcRefundEntity {

	@Column(name = "ORDER_ID")
	private String orderId;

	@Column(name = "IRCTC_REFUND_TYPE")
	private String irctcRefundType;

	@Column(name = "Bank_Ref_Num")
	private String bankRefNum;

	@Column(name = "IRCTC_REFUND_CANCELLED_DATE")
	private String irctcRefundCancelledDate;

	@Column(name = "IRCTC_REFUND_CANCELLED_ID")
	private String irctcRefundCancelledId;

	@Column(name = "REFUND_FLAG")
	private String refundFlag;

	@Column(name = "AMOUNT")
	private String amount;

	@Column(name = "TOTAL_AMOUNT")
	private String totalAmount;

	@Column(name = "PG_REF_NUM")
	private String pgRefNO;

	@Column(name = "CURRENCY_CODE")
	private String currencyCode;

	@Column(name = "TXNTYPE")
	private String transactionType;
	@Column(name = "PAY_ID")

	private String payId;
	@Column(name = "HASH")

	private String Hash;
	@Column(name = "CREATE_DATE")

	private String createDate;
	@Column(name = "STATUS")

	private String status;
	private String fileName;
	private String RefundOrderId;
	private String ResponseCode;
	private String ResponseMessage;
	private String CreateBy;
	private String refundFileType;
	private String fileDate;

	public String getFileDate() {
		return fileDate;
	}

	public void setFileDate(String fileDate) {
		this.fileDate = fileDate;
	}

	public String getRefundFileType() {
		return refundFileType;
	}

	public void setRefundFileType(String refundFileType) {
		this.refundFileType = refundFileType;
	}

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public String getPgRefNO() {
		return pgRefNO;
	}

	public void setPgRefNO(String pgRefNO) {
		this.pgRefNO = pgRefNO;
	}

	public String getCurrencyCode() {
		return currencyCode;
	}

	public void setCurrencyCode(String currencyCode) {
		this.currencyCode = currencyCode;
	}

	public String getTransactionType() {
		return transactionType;
	}

	public void setTransactionType(String transactionType) {
		this.transactionType = transactionType;
	}

	public String getPayId() {
		return payId;
	}

	public void setPayId(String payId) {
		this.payId = payId;
	}

	public String getHash() {
		return Hash;
	}

	public void setHash(String hash) {
		Hash = hash;
	}

	public String getCreateDate() {
		return createDate;
	}

	public void setCreateDate(String createDate) {
		this.createDate = createDate;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getRefundOrderId() {
		return RefundOrderId;
	}

	public void setRefundOrderId(String refundOrderId) {
		RefundOrderId = refundOrderId;
	}

	public String getResponseCode() {
		return ResponseCode;
	}

	public void setResponseCode(String responseCode) {
		ResponseCode = responseCode;
	}

	public String getResponseMessage() {
		return ResponseMessage;
	}

	public void setResponseMessage(String responseMessage) {
		ResponseMessage = responseMessage;
	}

	public String getCreateBy() {
		return CreateBy;
	}

	public void setCreateBy(String createBy) {
		CreateBy = createBy;
	}

	public String getIrctcRefundType() {
		return irctcRefundType;
	}

	public void setIrctcRefundType(String irctcRefundType) {
		this.irctcRefundType = irctcRefundType;
	}

	public String getBankRefNum() {
		return bankRefNum;
	}

	public void setBankRefNum(String bankRefNum) {
		this.bankRefNum = bankRefNum;
	}

	public String getIrctcRefundCancelledDate() {
		return irctcRefundCancelledDate;
	}

	public void setIrctcRefundCancelledDate(String irctcRefundCancelledDate) {
		this.irctcRefundCancelledDate = irctcRefundCancelledDate;
	}

	public String getIrctcRefundCancelledId() {
		return irctcRefundCancelledId;
	}

	public void setIrctcRefundCancelledId(String irctcRefundCancelledId) {
		this.irctcRefundCancelledId = irctcRefundCancelledId;
	}

	public String getRefundFlag() {
		return refundFlag;
	}

	public void setRefundFlag(String refundFlag) {
		this.refundFlag = refundFlag;
	}

	public String getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(String totalAmount) {
		this.totalAmount = totalAmount;
	}

}
