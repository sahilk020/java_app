package com.crmws.dto;

public class AcquirerStatusDto {
	
	private String acquirer;
	private String paymentType;
	private String lastTxnTime;
	private String status;
	private String lastSuccessTime;
	
	public String getAcquirer() {
		return acquirer;
	}
	public void setAcquirer(String acquirer) {
		this.acquirer = acquirer;
	}
	
	public String getPaymentType() {
		return paymentType;
	}
	public void setPaymentType(String paymentType) {
		this.paymentType = paymentType;
	}
	public String getLastTxnTime() {
		return lastTxnTime;
	}
	public void setLastTxnTime(String lastTxnTime) {
		this.lastTxnTime = lastTxnTime;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getLastSuccessTime() {
		return lastSuccessTime;
	}
	public void setLastSuccessTime(String lastSuccessTime) {
		this.lastSuccessTime = lastSuccessTime;
	}
	
	
	

}
