package com.pay10.crm.audittrail.dto;

import java.util.ArrayList;
import java.util.List;

public class ResponseData {
	private String reportType;
	private int statusCode;
	private List<TransactionMonitoringSummaryDTO> summaryDTOs = new ArrayList<TransactionMonitoringSummaryDTO>();
	public String getReportType() {
		return reportType;
	}
	public void setReportType(String reportType) {
		this.reportType = reportType;
	}
	public int getStatusCode() {
		return statusCode;
	}
	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}
	public List<TransactionMonitoringSummaryDTO> getSummaryDTOs() {
		return summaryDTOs;
	}
	public void setSummaryDTOs(List<TransactionMonitoringSummaryDTO> summaryDTOs) {
		this.summaryDTOs = summaryDTOs;
	}
	
}
