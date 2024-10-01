package com.pay10.crm.actionBeans;

public class GateWayDashboardBean {
	private String paymentType="N/A";
	private long success;
	private long failed;
	private long inqueue;
	private double successRatio;
	private String lastTrnReceived="N/A";
	private String Status="N/A";
	public String getPaymentType() {
		return paymentType;
	}
	public void setPaymentType(String paymentType) {
		this.paymentType = paymentType;
	}
	public long getSuccess() {
		return success;
	}
	public void setSuccess(long success) {
		this.success = success;
	}
	public long getFailed() {
		return failed;
	}
	public void setFailed(long failed) {
		this.failed = failed;
	}
	public long getInqueue() {
		return inqueue;
	}
	public void setInqueue(long inqueue) {
		this.inqueue = inqueue;
	}
	public double getSuccessRatio() {
		return successRatio;
	}
	public void setSuccessRatio(double successRatio) {
		this.successRatio = successRatio;
	}
	public String getLastTrnReceived() {
		return lastTrnReceived;
	}
	public void setLastTrnReceived(String lastTrnReceived) {
		this.lastTrnReceived = lastTrnReceived;
	}
	public String getStatus() {
		return Status;
	}
	public void setStatus(String status) {
		Status = status;
	}
	
	public Object[] gatewayDashboard() {
		  Object[] objectArray = new Object[7];
		  
		 
		  objectArray[0] = paymentType;
		  objectArray[1] = success==0?"0":""+success;
		  objectArray[2] = failed==0?"0":""+failed;;
		  objectArray[3] = inqueue==0?"0":""+inqueue;;
		  objectArray[4] = successRatio==0?"0":""+successRatio;;
		  objectArray[5] = lastTrnReceived;
		  objectArray[6] = Status;
	
		  return objectArray;
		}
}
