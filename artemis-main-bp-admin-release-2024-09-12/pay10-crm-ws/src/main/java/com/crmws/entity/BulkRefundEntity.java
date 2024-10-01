package com.crmws.entity;

import javax.persistence.Column;

import org.springframework.data.mongodb.core.mapping.Document;




@Document
public class BulkRefundEntity {

	@Column(name = "ORDER_ID")
	private String orderId;
	
	@Column(name="REFUND_FLAG")
	private String refund_Flag;
	
	@Column(name ="AMOUNT")
	private String amount;
	@Column(name="PG_REF_NUM")
	private String pgRefNO;
	@Column(name="CURRENCY_CODE")
	private String currencyCode;
	@Column(name="TXNTYPE")
	private String transactionType;
	@Column(name="PAY_ID")
	private String payId;
	@Column(name="HASH")
	private String Hash;
	@Column(name="CREATE_DATE")
	private String createDate;
	@Column(name="STATUS")
	private String status;
	private String fileName;
	private String RefundOrderId;
	private String ResponseCode;
	private String ResponseMessage; 
	
	private String CreateBy;
	
	public String getOrderId() {
		return orderId;
	}
	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}
	public String getRefund_Flag() {
		return refund_Flag;
	}
	public void setRefund_Flag(String refund_Flag) {
		this.refund_Flag = refund_Flag;
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
	@Override
	public String toString() {
		return "BulkRefundEntity [orderId=" + orderId + ", refund_Flag=" + refund_Flag + ", amount=" + amount
				+ ", pgRefNO=" + pgRefNO + ", currencyCode=" + currencyCode + ", transactionType=" + transactionType
				+ ", payId=" + payId + ", Hash=" + Hash + ", createDate=" + createDate + ", status=" + status
				+ ", fileName=" + fileName + ", RefundOrderId=" + RefundOrderId + ", ResponseCode=" + ResponseCode
				+ ", ResponseMessage=" + ResponseMessage + ", CreateBy=" + CreateBy + "]";
	}
	
	
	

	
	
	
}
