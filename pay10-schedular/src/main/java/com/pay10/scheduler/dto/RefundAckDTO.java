package com.pay10.scheduler.dto;

public class RefundAckDTO {

	private String merchantId;
	private String refundReqNo;
	private String transRefNo;
	private String custRefNo;
	private String orderNo;
	private String refundReqAmt;
	private String refundRemark;
	private String refundStatus;
	private String bankRefundTxnId;
	private String refundDate;
	private String bankRemark;

	public String getRefundRemark() {
		return refundRemark;
	}

	public void setRefundRemark(String refundRemark) {
		this.refundRemark = refundRemark;
	}

	public String getMerchantId() {
		return merchantId;
	}

	public void setMerchantId(String merchantId) {
		this.merchantId = merchantId;
	}

	public String getRefundReqNo() {
		return refundReqNo;
	}

	public void setRefundReqNo(String refundReqNo) {
		this.refundReqNo = refundReqNo;
	}

	public String getTransRefNo() {
		return transRefNo;
	}

	public void setTransRefNo(String transRefNo) {
		this.transRefNo = transRefNo;
	}

	public String getCustRefNo() {
		return custRefNo;
	}

	public void setCustRefNo(String custRefNo) {
		this.custRefNo = custRefNo;
	}

	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

	public String getRefundReqAmt() {
		return refundReqAmt;
	}

	public void setRefundReqAmt(String refundReqAmt) {
		this.refundReqAmt = refundReqAmt;
	}

	public String getRefundStatus() {
		return refundStatus;
	}

	public void setRefundStatus(String refundStatus) {
		this.refundStatus = refundStatus;
	}

	public String getBankRefundTxnId() {
		return bankRefundTxnId;
	}

	public void setBankRefundTxnId(String bankRefundTxnId) {
		this.bankRefundTxnId = bankRefundTxnId;
	}

	public String getRefundDate() {
		return refundDate;
	}

	public void setRefundDate(String refundDate) {
		this.refundDate = refundDate;
	}

	public String getBankRemark() {
		return bankRemark;
	}

	public void setBankRemark(String bankRemark) {
		this.bankRemark = bankRemark;
	}

	@Override
	public String toString() {
		return "RefundAckDTO [merchantId=" + merchantId + ", refundReqNo=" + refundReqNo + ", transRefNo=" + transRefNo
				+ ", custRefNo=" + custRefNo + ", orderNo=" + orderNo + ", refundReqAmt=" + refundReqAmt
				+ ", refundRemark=" + refundRemark + ", refundStatus=" + refundStatus + ", bankRefundTxnId="
				+ bankRefundTxnId + ", refundDate=" + refundDate + ", bankRemark=" + bankRemark + "]";
	}

}
