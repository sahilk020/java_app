package com.crmws.worker.dto;

public class BsesTransactionWiseDTO {
	private String srNo;
	private String paymentFileDate;
	private String agencyName;
	private String payMode;
	private String productCode;
	private String transactionRefNo;
	private String   caNumber;
	private String transationDate;
	private String grossAmount;
	private String mdr;
	private String gst;
	private String convenienceFee;
	private String netAmount;
	
	
	
	public String getSrNo() {
		return srNo;
	}



	public void setSrNo(String srNo) {
		this.srNo = srNo;
	}



	public String getPaymentFileDate() {
		return paymentFileDate;
	}



	public void setPaymentFileDate(String paymentFileDate) {
		this.paymentFileDate = paymentFileDate;
	}



	public String getAgencyName() {
		return agencyName;
	}



	public void setAgencyName(String agencyName) {
		this.agencyName = agencyName;
	}



	public String getPayMode() {
		return payMode;
	}



	public void setPayMode(String payMode) {
		this.payMode = payMode;
	}



	public String getProductCode() {
		return productCode;
	}



	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}



	public String getTransactionRefNo() {
		return transactionRefNo;
	}



	public void setTransactionRefNo(String transactionRefNo) {
		this.transactionRefNo = transactionRefNo;
	}



	public String getCaNumber() {
		return caNumber;
	}



	public void setCaNumber(String caNumber) {
		this.caNumber = caNumber;
	}



	public String getTransationDate() {
		return transationDate;
	}



	public void setTransationDate(String transationDate) {
		this.transationDate = transationDate;
	}



	public String getGrossAmount() {
		return grossAmount;
	}



	public void setGrossAmount(String grossAmount) {
		this.grossAmount = grossAmount;
	}



	public String getMdr() {
		return mdr;
	}



	public void setMdr(String mdr) {
		this.mdr = mdr;
	}



	public String getGst() {
		return gst;
	}



	public void setGst(String gst) {
		this.gst = gst;
	}



	public String getConvenienceFee() {
		return convenienceFee;
	}



	public void setConvenienceFee(String convenienceFee) {
		this.convenienceFee = convenienceFee;
	}



	public String getNetAmount() {
		return netAmount;
	}



	public void setNetAmount(String netAmount) {
		this.netAmount = netAmount;
	}



	public Object[] getTransactionObject() {
		  Object[] objectArray = new Object[12];
		  
		 
		
		  objectArray[0] = paymentFileDate;
		  objectArray[1] = agencyName;
		  objectArray[2] = payMode;
		  objectArray[3] = productCode;
		  objectArray[4] = transactionRefNo;
		  objectArray[5] = caNumber;
		  objectArray[6] = transationDate;
		  objectArray[7] = grossAmount;
		  objectArray[8] = mdr;
		  objectArray[9] = gst;
		  objectArray[10] = convenienceFee;
		  objectArray[11] = netAmount;
		  
		
		  return objectArray;
		}
}
