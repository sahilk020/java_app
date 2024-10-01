package com.pay10.crm.audittrail.dto;

public class FinalAcquirerWiseDTO {
	private String acquirerName;
	private long captured;
	private double capturedPercentage;
	private long failed;
	private double failedPercentage;
	private long pending;
	private double pendingPercentage;
	private long grandTotal;
	private String payId;
	private String merchantName;

	public String getAcquirerName() {
		return acquirerName;
	}

	public void setAcquirerName(String acquirerName) {
		this.acquirerName = acquirerName;
	}

	public long getCaptured() {
		return captured;
	}

	public void setCaptured(long captured) {
		this.captured = captured;
	}

	public double getCapturedPercentage() {
		return capturedPercentage;
	}

	public void setCapturedPercentage(double capturedPercentage) {
		this.capturedPercentage = capturedPercentage;
	}

	public long getFailed() {
		return failed;
	}

	public void setFailed(long failed) {
		this.failed = failed;
	}

	public double getFailedPercentage() {
		return failedPercentage;
	}

	public void setFailedPercentage(double failedPercentage) {
		this.failedPercentage = failedPercentage;
	}

	public long getPending() {
		return pending;
	}

	public void setPending(long pending) {
		this.pending = pending;
	}

	public double getPendingPercentage() {
		return pendingPercentage;
	}

	public void setPendingPercentage(double pendingPercentage) {
		this.pendingPercentage = pendingPercentage;
	}

	public long getGrandTotal() {
		return grandTotal;
	}

	public void setGrandTotal(long grandTotal) {
		this.grandTotal = grandTotal;
	}

	public String getPayId() {
		return payId;
	}

	public void setPayId(String payId) {
		this.payId = payId;
	}

	public String getMerchantName() {
		return merchantName;
	}

	public void setMerchantName(String merchantName) {
		this.merchantName = merchantName;
	}
	
	public Object[] myCsvMethod() {
		  Object[] objectArray = new Object[8];
		  objectArray[0] = acquirerName;
		  objectArray[1] = captured;
		  objectArray[2] = capturedPercentage;
		  objectArray[3] = failed;
		  objectArray[4] = failedPercentage;
		  objectArray[5] = pending;
		  objectArray[6] = pendingPercentage;
		  objectArray[7] = grandTotal;
		  return objectArray;
	}
	
	public Object[] myCsvMethodMerchant() {
		  Object[] objectArray = new Object[8];
		  objectArray[0] = merchantName;
		  objectArray[1] = captured;
		  objectArray[2] = capturedPercentage;
		  objectArray[3] = failed;
		  objectArray[4] = failedPercentage;
		  objectArray[5] = pending;
		  objectArray[6] = pendingPercentage;
		  objectArray[7] = grandTotal;
		  return objectArray;
	}

}
