package com.pay10.commons.dto;

public class BulkUploadLiabilityHoldAndRelease {

	private String pgRefNum;
	private String status;
	private String errorMsg;
	private String liabilityType;
	private String liablityDateIndex;
	private String dateIndex;
	private String remarks;
	private String updatedby;
	private String updatedat;
	
	
	public String getUpdatedby() {
		return updatedby;
	}
	public void setUpdatedby(String updatedby) {
		this.updatedby = updatedby;
	}
	public String getUpdatedat() {
		return updatedat;
	}
	public void setUpdatedat(String updatedat) {
		this.updatedat = updatedat;
	}
	
	public String getRemarks() {
		return remarks;
	}
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
	public String getPgRefNum() {
		return pgRefNum;
	}
	public void setPgRefNum(String pgRefNum) {
		this.pgRefNum = pgRefNum;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getErrorMsg() {
		return errorMsg;
	}
	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}
	public String getLiabilityType() {
		return liabilityType;
	}
	public void setLiabilityType(String liabilityType) {
		this.liabilityType = liabilityType;
	}
	public String getLiablityDateIndex() {
		return liablityDateIndex;
	}
	public void setLiablityDateIndex(String liablityDateIndex) {
		this.liablityDateIndex = liablityDateIndex;
	}
	public String getDateIndex() {
		return dateIndex;
	}
	public void setDateIndex(String dateIndex) {
		this.dateIndex = dateIndex;
	}
	
	
	
}
