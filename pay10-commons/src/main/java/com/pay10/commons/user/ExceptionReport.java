package com.pay10.commons.user;

import java.io.Serializable;
import java.math.BigInteger;

public class ExceptionReport implements Serializable {
	
	private static final long serialVersionUID = -469100930735471956L;

	private String pgRefNo;
	private BigInteger txnId;
	private String orderId;
	private String acqId;
	private String createdDate;
	private String status;
	private String exception;
	
	
	public String getPgRefNo() {
		return pgRefNo;
	}
	public void setPgRefNo(String pgRefNo) {
		this.pgRefNo = pgRefNo;
	}
	public BigInteger getTxnId() {
		return txnId;
	}
	public void setTxnId(BigInteger txnId) {
		this.txnId = txnId;
	}
	public String getOrderId() {
		return orderId;
	}
	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}
	
	public String getAcqId() {
		return acqId;
	}
	public void setAcqId(String acqId) {
		this.acqId = acqId;
	}
	public String getCreatedDate() {
		return createdDate;
	}
	public void setCreatedDate(String createdDate) {
		this.createdDate = createdDate;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getException() {
		return exception;
	}
	public void setException(String exception) {
		this.exception = exception;
	}
}
