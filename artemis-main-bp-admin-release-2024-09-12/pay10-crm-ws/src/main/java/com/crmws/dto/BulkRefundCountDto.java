package com.crmws.dto;



public class BulkRefundCountDto {
	
	String fileName;
	String successTXN;
	String failedTXN;
	String pending;
	String totalTxn;
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getSuccessTXN() {
		return successTXN;
	}
	public void setSuccessTXN(String successTXN) {
		this.successTXN = successTXN;
	}
	public String getFailedTXN() {
		return failedTXN;
	}
	public void setFailedTXN(String failedTXN) {
		this.failedTXN = failedTXN;
	}
	public String getPending() {
		return pending;
	}
	public void setPending(String pending) {
		this.pending = pending;
	}
	public String getTotalTxn() {
		return totalTxn;
	}
	public void setTotalTxn(String totalTxn) {
		this.totalTxn = totalTxn;
	}
	
	
	

}
