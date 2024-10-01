package com.pay10.commons.user;

public class CashfreeTxnRespDTO {

	private String payId;
	private String orderId;
	private String pgRefNo;
	private String amount;
	private String totalAmount;
	private String custPhone;
	private String mopType;
	private String status;
	private String responseCode;
	private String responseMessage;
	private String custEmail;
	private String paymentType;
	private String autoCode;
	// private String requestTime;
	private String responseTime;
	// private String refundOrderId;
	private String txnType;

	public String getPayId() {
		return payId;
	}

	public void setPayId(String payId) {
		this.payId = payId;
	}

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public String getPgRefNo() {
		return pgRefNo;
	}

	public void setPgRefNo(String pgRefNo) {
		this.pgRefNo = pgRefNo;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public String getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(String totalAmount) {
		this.totalAmount = totalAmount;
	}

	public String getCustPhone() {
		return custPhone;
	}

	public void setCustPhone(String custPhone) {
		this.custPhone = custPhone;
	}

	public String getMopType() {
		return mopType;
	}

	public void setMopType(String mopType) {
		this.mopType = mopType;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getResponseCode() {
		return responseCode;
	}

	public void setResponseCode(String responseCode) {
		this.responseCode = responseCode;
	}

	public String getResponseMessage() {
		return responseMessage;
	}

	public void setResponseMessage(String responseMessage) {
		this.responseMessage = responseMessage;
	}

	public String getCustEmail() {
		return custEmail;
	}

	public void setCustEmail(String custEmail) {
		this.custEmail = custEmail;
	}

	public String getPaymentType() {
		return paymentType;
	}

	public void setPaymentType(String paymentType) {
		this.paymentType = paymentType;
	}

	public String getAutoCode() {
		return autoCode;
	}

	public void setAutoCode(String autoCode) {
		this.autoCode = autoCode;
	}

	public String getResponseTime() {
		return responseTime;
	}

	public void setResponseTime(String responseTime) {
		this.responseTime = responseTime;
	}

	public String getTxnType() {
		return txnType;
	}

	public void setTxnType(String txnType) {
		this.txnType = txnType;
	}

	@Override
	public String toString() {
		return "CashfreeTxnRespDTO [payId=" + payId + ", orderId=" + orderId + ", pgRefNo=" + pgRefNo + ", amount="
				+ amount + ", totalAmount=" + totalAmount + ", custPhone=" + custPhone + ", mopType=" + mopType
				+ ", status=" + status + ", responseCode=" + responseCode + ", responseMessage=" + responseMessage
				+ ", custEmail=" + custEmail + ", paymentType=" + paymentType + ", autoCode=" + autoCode
				+ ", responseTime=" + responseTime + ", txnType=" + txnType + "]";
	}

}
