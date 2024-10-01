package com.crmws.dto;

public class AcquirerRebalanceTxnDTO {
	private String id;
	private String fromPayId;
	private String fromAcquirerName;
	private String toPayId;
	private String toAcquirerName;
	private String amount;
	private String orderId;
	private String txnType;
	private String narration;
	private String status;
	private String createdBy;
	private String createdDate;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getFromPayId() {
		return fromPayId;
	}

	public void setFromPayId(String fromPayId) {
		this.fromPayId = fromPayId;
	}

	public String getFromAcquirerName() {
		return fromAcquirerName;
	}

	public void setFromAcquirerName(String fromAcquirerName) {
		this.fromAcquirerName = fromAcquirerName;
	}

	public String getToPayId() {
		return toPayId;
	}

	public void setToPayId(String toPayId) {
		this.toPayId = toPayId;
	}

	public String getToAcquirerName() {
		return toAcquirerName;
	}

	public void setToAcquirerName(String toAcquirerName) {
		this.toAcquirerName = toAcquirerName;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public String getTxnType() {
		return txnType;
	}

	public void setTxnType(String txnType) {
		this.txnType = txnType;
	}

	public String getNarration() {
		return narration;
	}

	public void setNarration(String narration) {
		this.narration = narration;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public String getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(String createdDate) {
		this.createdDate = createdDate;
	}

	@Override
	public String toString() {
		return "AcquirerRebalanceTxnDTO [id=" + id + ", fromPayId=" + fromPayId + ", fromAcquirerName="
				+ fromAcquirerName + ", toPayId=" + toPayId + ", toAcquirerName=" + toAcquirerName + ", amount="
				+ amount + ", orderId=" + orderId + ", txnType=" + txnType + ", narration=" + narration + ", status="
				+ status + ", createdBy=" + createdBy + ", createdDate=" + createdDate + "]";
	}

}
