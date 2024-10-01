package com.pay10.commons.user;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import org.hibernate.annotations.ColumnDefault;

public class CbReportObject  implements Cloneable {
	
	private String merchantPayId;
	private String marchantName;//
	private String cbCaseId;//
	private String dtOfTxn;//
	private String txnAmount;//
	private String pgCaseId;//
	private String merchantTxnId;//
	private String orderId;//
	private String pgRefNo;//
	private String bankTxnId;///
	private String cbAmount;//
	private String cbReason;//
	private String cbReasonCode;//
	private String cbIntimationDate;//
	private String cbDdlineDate;//
	private String modeOfPayment;//
	private String acqName;//
	private String settlemtDate;//
	private String customerName;//
	private String customerPhone;//
	private String email;//
	private String nemail;//
	private String filePaths;
	private String status;//
	private String createdBy;
	private String updatedBy;
	private boolean merchantDocUploadFlag;
	private String cbClosedInFavorBank;
	private String cbClosedInFavorMerchant;
	private String cbPodDate;
	private String cbInitiatedDate;
	private String cbAcceptDate;
	private String cbCloseDate;
	private String cbFavourDate;
	private String date;//
	
	
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}	
	public String getMarchantName() {
		return marchantName;
	}
	public void setMarchantName(String marchantName) {
		this.marchantName = marchantName;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getMerchantPayId() {
		return merchantPayId;
	}
	public void setMerchantPayId(String merchantPayId) {
		this.merchantPayId = merchantPayId;
	}
	public String getCbCaseId() {
		return cbCaseId;
	}
	public void setCbCaseId(String cbCaseId) {
		this.cbCaseId = cbCaseId;
	}
	public String getDtOfTxn() {
		return dtOfTxn;
	}
	public void setDtOfTxn(String dtOfTxn) {
		this.dtOfTxn = dtOfTxn;
	}
	public String getTxnAmount() {
		return txnAmount;
	}
	public void setTxnAmount(String txnAmount) {
		this.txnAmount = txnAmount;
	}
	public String getPgCaseId() {
		return pgCaseId;
	}
	public void setPgCaseId(String pgCaseId) {
		this.pgCaseId = pgCaseId;
	}
	public String getMerchantTxnId() {
		return merchantTxnId;
	}
	public void setMerchantTxnId(String merchantTxnId) {
		this.merchantTxnId = merchantTxnId;
	}
	public String getOrderId() {
		return orderId;
	}
	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}
	public String getPgRefNo() {
		return pgRefNo;
	}
	public void setPgRefNo(String pgRefNo) {
		this.pgRefNo = pgRefNo;
	}
	public String getBankTxnId() {
		return bankTxnId;
	}
	public void setBankTxnId(String bankTxnId) {
		this.bankTxnId = bankTxnId;
	}
	public String getCbAmount() {
		return cbAmount;
	}
	public void setCbAmount(String cbAmount) {
		this.cbAmount = cbAmount;
	}
	public String getCbReason() {
		return cbReason;
	}
	public void setCbReason(String cbReason) {
		this.cbReason = cbReason;
	}
	public String getCbReasonCode() {
		return cbReasonCode;
	}
	public void setCbReasonCode(String cbReasonCode) {
		this.cbReasonCode = cbReasonCode;
	}
	public String getCbIntimationDate() {
		return cbIntimationDate;
	}
	public void setCbIntimationDate(String cbIntimationDate) {
		this.cbIntimationDate = cbIntimationDate;
	}
	public String getCbDdlineDate() {
		return cbDdlineDate;
	}
	public void setCbDdlineDate(String cbDdlineDate) {
		this.cbDdlineDate = cbDdlineDate;
	}
	public String getModeOfPayment() {
		return modeOfPayment;
	}
	public void setModeOfPayment(String modeOfPayment) {
		this.modeOfPayment = modeOfPayment;
	}
	public String getAcqName() {
		return acqName;
	}
	public void setAcqName(String acqName) {
		this.acqName = acqName;
	}
	public String getSettlemtDate() {
		return settlemtDate;
	}
	public void setSettlemtDate(String settlemtDate) {
		this.settlemtDate = settlemtDate;
	}
	public String getCustomerName() {
		return customerName;
	}
	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}
	public String getCustomerPhone() {
		return customerPhone;
	}
	public void setCustomerPhone(String customerPhone) {
		this.customerPhone = customerPhone;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getNemail() {
		return nemail;
	}
	public void setNemail(String nemail) {
		this.nemail = nemail;
	}
	public String getFilePaths() {
		return filePaths;
	}
	public void setFilePaths(String filePaths) {
		this.filePaths = filePaths;
	}
	
	public String getCreatedBy() {
		return createdBy;
	}
	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}
	public String getUpdatedBy() {
		return updatedBy;
	}
	public void setUpdatedBy(String updatedBy) {
		this.updatedBy = updatedBy;
	}
	public boolean isMerchantDocUploadFlag() {
		return merchantDocUploadFlag;
	}
	public void setMerchantDocUploadFlag(boolean merchantDocUploadFlag) {
		this.merchantDocUploadFlag = merchantDocUploadFlag;
	}
	public String getCbClosedInFavorBank() {
		return cbClosedInFavorBank;
	}
	public void setCbClosedInFavorBank(String cbClosedInFavorBank) {
		this.cbClosedInFavorBank = cbClosedInFavorBank;
	}
	public String getCbClosedInFavorMerchant() {
		return cbClosedInFavorMerchant;
	}
	public void setCbClosedInFavorMerchant(String cbClosedInFavorMerchant) {
		this.cbClosedInFavorMerchant = cbClosedInFavorMerchant;
	}
	public String getCbPodDate() {
		return cbPodDate;
	}
	public void setCbPodDate(String cbPodDate) {
		this.cbPodDate = cbPodDate;
	}
	public String getCbInitiatedDate() {
		return cbInitiatedDate;
	}
	public void setCbInitiatedDate(String cbInitiatedDate) {
		this.cbInitiatedDate = cbInitiatedDate;
	}
	public String getCbAcceptDate() {
		return cbAcceptDate;
	}
	public void setCbAcceptDate(String cbAcceptDate) {
		this.cbAcceptDate = cbAcceptDate;
	}
	public String getCbCloseDate() {
		return cbCloseDate;
	}
	public void setCbCloseDate(String cbCloseDate) {
		this.cbCloseDate = cbCloseDate;
	}
	public String getCbFavourDate() {
		return cbFavourDate;
	}
	public void setCbFavourDate(String cbFavourDate) {
		this.cbFavourDate = cbFavourDate;
	}
	
	
	
	 public Object[] myCsvMethod() {
		  Object[] objectArray = new Object[23];
		  
		  objectArray[0]=marchantName;
		  objectArray[1]=cbCaseId;
		  objectArray[2]=dtOfTxn;
		  objectArray[3]=txnAmount;
		  objectArray[4]=pgCaseId;
		  objectArray[5]=merchantTxnId;
		  objectArray[6]=orderId;
		  objectArray[7]=pgRefNo;
		  objectArray[8]=bankTxnId;
		  objectArray[9]=cbAmount;
		  objectArray[10]=cbReason;
		  objectArray[11]=cbReasonCode;
		  objectArray[12]=cbIntimationDate;
		  objectArray[13]=cbDdlineDate;
		  objectArray[14]=modeOfPayment;
		  objectArray[15]=acqName;
		  objectArray[16]=settlemtDate;
		  objectArray[17]=customerName;
		  objectArray[18]=customerPhone;
		  objectArray[19]=email;
		  objectArray[20]=nemail;
		  objectArray[21]=status;
		  objectArray[22]=date;
		  return objectArray;
		}
	
	 public Object clone() throws CloneNotSupportedException
	    {
	        return super.clone();
	    }
	 
	 
	 
	 
	
	 
	 
	 
}
