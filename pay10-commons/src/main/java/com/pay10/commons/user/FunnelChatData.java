package com.pay10.commons.user;

public class FunnelChatData {
	
	private String totalTxnCount;
	//private String SentToBankCount;
	private String successTxnCount;
	private String failedTxnCount;
	private String invalidTxnCount;
	private String cancelledTxnCount;
	private String timeoutTxnCount;

	public String getTotalTxnCount() {
		return totalTxnCount;
	}
	public void setTotalTxnCount(String totalTxnCount) {
		this.totalTxnCount = totalTxnCount;
	}
	public String getSuccessTxnCount() {
		return successTxnCount;
	}
	public void setSuccessTxnCount(String successTxnCount) {
		this.successTxnCount = successTxnCount;
	}
	
	public String getFailedTxnCount() {
		return failedTxnCount;
	}
	public void setFailedTxnCount(String failedTxnCount) {
		this.failedTxnCount = failedTxnCount;
	}
	
	public String getInvalidTxnCount() {
		return invalidTxnCount;
	}
	public void setInvalidTxnCount(String invalidTxnCount) {
		this.invalidTxnCount = invalidTxnCount;
	}
	
	public String getCancelledTxnCount() {
		return cancelledTxnCount;
	}
	public void setCancelledTxnCount(String cancelledTxnCount) {
		this.cancelledTxnCount = cancelledTxnCount;
	}
	
	public String getTimeoutTxnCount() {
		return timeoutTxnCount;
	}
	public void setTimeoutTxnCount(String timeoutTxnCount) {
		this.timeoutTxnCount = timeoutTxnCount;
	}
	
}
