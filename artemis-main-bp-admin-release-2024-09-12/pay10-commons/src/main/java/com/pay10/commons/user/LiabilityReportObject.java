package com.pay10.commons.user;

public class LiabilityReportObject implements Cloneable{
	private String srNo;
	private String holdDate;
	private String releaseDate;
	private String mopType;
	private String totalAmount;
	private String pgRefNum;
	private String dateIndex;
	private String txnType;
	private String paymentType;
	private String aliasStaus;
	private String acquirerType;
	private String day;
	private String businessName;
	private String liabilityReleaseRemark;
	private String liabilityHoldRemarks;
	
	
	public String getLiabilityReleaseRemark() {
		return liabilityReleaseRemark;
	}


	public void setLiabilityReleaseRemark(String liabilityReleaseRemark) {
		this.liabilityReleaseRemark = liabilityReleaseRemark;
	}


	public String getLiabilityHoldRemarks() {
		return liabilityHoldRemarks;
	}


	public void setLiabilityHoldRemarks(String liabilityHoldRemarks) {
		this.liabilityHoldRemarks = liabilityHoldRemarks;
	}


	public String getSrNo() {
		return srNo;
	}


	public void setSrNo(String srNo) {
		this.srNo = srNo;
	}


	public String getHoldDate() {
		return holdDate;
	}


	public void setHoldDate(String holdDate) {
		this.holdDate = holdDate;
	}


	public String getReleaseDate() {
		return releaseDate;
	}


	public void setReleaseDate(String releaseDate) {
		this.releaseDate = releaseDate;
	}


	public String getMopType() {
		return mopType;
	}


	public void setMopType(String mopType) {
		this.mopType = mopType;
	}


	public String getTotalAmount() {
		return totalAmount;
	}


	public void setTotalAmount(String totalAmount) {
		this.totalAmount = totalAmount;
	}


	public String getPgRefNum() {
		return pgRefNum;
	}


	public void setPgRefNum(String pgRefNum) {
		this.pgRefNum = pgRefNum;
	}


	public String getDateIndex() {
		return dateIndex;
	}


	public void setDateIndex(String dateIndex) {
		this.dateIndex = dateIndex;
	}


	public String getTxnType() {
		return txnType;
	}


	public void setTxnType(String txnType) {
		this.txnType = txnType;
	}


	public String getPaymentType() {
		return paymentType;
	}


	public void setPaymentType(String paymentType) {
		this.paymentType = paymentType;
	}


	public String getAliasStaus() {
		return aliasStaus;
	}


	public void setAliasStaus(String aliasStaus) {
		this.aliasStaus = aliasStaus;
	}


	public String getAcquirerType() {
		return acquirerType;
	}


	public void setAcquirerType(String acquirerType) {
		this.acquirerType = acquirerType;
	}


	public String getDay() {
		return day;
	}


	public void setDay(String day) {
		this.day = day;
	}


	public String getBusinessName() {
		return businessName;
	}


	public void setBusinessName(String businessName) {
		this.businessName = businessName;
	}


	public Object[] objectForHold() {
		  Object[] objectArray = new Object[12];
		  
		  objectArray[0] =  srNo;
		  objectArray[1] = businessName;
		  objectArray[2] =  holdDate;  
		  objectArray[3] =  mopType;
		  objectArray[4] =  totalAmount;
		  objectArray[5] =  pgRefNum;
		  objectArray[6] =  dateIndex;
		  objectArray[7] =  txnType;
		  objectArray[8] =  paymentType;
		  objectArray[9] = aliasStaus;
		  objectArray[10] = acquirerType;
		  objectArray[11] = liabilityHoldRemarks;
		 
		  
		
		  return objectArray;
		}
	
	
	public Object[] objectForRelease() {
		  Object[] objectArray = new Object[11];
		  
		  objectArray[0] =  srNo;
		  objectArray[1] = businessName;
		  objectArray[2] =  releaseDate;
		  objectArray[3] =  mopType;
		  objectArray[4] =  totalAmount;
		  objectArray[5] =  pgRefNum;
		  objectArray[6] =  dateIndex;
		  objectArray[7] =  txnType;
		  objectArray[8] =  paymentType;
		  objectArray[9] = aliasStaus;
		  objectArray[10] = acquirerType;
		  
		
		  return objectArray;
		}
	
	
	public Object[] objectForAudit() {
		  Object[] objectArray = new Object[15];
		  
		  objectArray[0] =  srNo;
		  objectArray[1] = businessName;
		  objectArray[2] =  holdDate;
		  objectArray[3] =  releaseDate;
		  objectArray[4] =  mopType;
		  objectArray[5] =  totalAmount;
		  objectArray[6] =  pgRefNum;
		  objectArray[7] =  dateIndex;
		  objectArray[8] =  txnType;
		  objectArray[9] =  paymentType;
		  objectArray[10] = aliasStaus;
		  objectArray[11] = acquirerType;
		  objectArray[12] = day;
		  objectArray[13] = liabilityReleaseRemark;
		  objectArray[14] = liabilityHoldRemarks;
		 
		
		  return objectArray;
		}
}
