package com.pay10.commons.dto;

import java.io.Serializable;

public class DashboardReportCount implements Serializable{
	private static final long serialVersionUID = -6835265484151916601L;
	private double rnsCount;
	private double forceCaptureCount;
	private double exceptionCount;
	
	public double getRnsCount() {
		return rnsCount;
	}
	public void setRnsCount(double rnsCount) {
		this.rnsCount = rnsCount;
	}
	public double getForceCaptureCount() {
		return forceCaptureCount;
	}
	public void setForceCaptureCount(double forceCaptureCount) {
		this.forceCaptureCount = forceCaptureCount;
	}
	public double getExceptionCount() {
		return exceptionCount;
	}
	public void setExceptionCount(double exceptionCount) {
		this.exceptionCount = exceptionCount;
	}
	
	
	
}
