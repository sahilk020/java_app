package com.pay10.commons.repository;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Proxy;

import com.pay10.commons.user.Status;
import com.pay10.commons.util.ModeType;

@Entity
@Proxy(lazy = false)
@Table(name = "disputeManagementTable")
public class DMSEntity implements Serializable {

	private static final long serialVersionUID = -2316060336671262154L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

	private String merchantPayId;
	private String cbCaseId;
	private String dtOfTxn;
	private String txnAmount;
	private String pgCaseId;
	private String merchantTxnId;
	private String orderId;
	private String pgRefNo;
	private String bankTxnId;
	private String cbAmount;
	private String cbReason;
	private String cbReasonCode;
	private String cbIntimationDate;
	private String cbDdlineDate;
	private String modeOfPayment;
	private String acqName;
	private String settlemtDate;
	private String customerName;
	private String customerPhone;
	private String email;
	private String nemail;
	private String filePaths;
	
	@Enumerated(EnumType.STRING)
	private Status status;

	private String createdBy;
	private String updatedBy;
	@ColumnDefault("0")
	private boolean merchantDocUploadFlag;
	private String cbClosedInFavorBank;
	private String cbClosedInFavorMerchant;//Bank/Acquire or merchant
//	private String merchantAmoountDR;
//	private String merchantAmoountCR;
	
	private String cbPodDate;
	private String cbInitiatedDate;
	private String cbAcceptDate;
	private String cbCloseDate;
	private String cbFavourDate;
	
	private String businessName;
	private String currencyName;
	private String dateWithTimeStamp;
	
	public DMSEntity() {

	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
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

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
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

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
	public String getBusinessName() {
		return businessName;
	}

	public void setBusinessName(String businessName) {
		this.businessName = businessName;
	}

	public String getCurrencyName() {
		return currencyName;
	}

	public void setCurrencyName(String currencyName) {
		this.currencyName = currencyName;
	}
	
	public String getDateWithTimeStamp() {
		return dateWithTimeStamp;
	}

	public void setDateWithTimeStamp(String dateWithTimeStamp) {
		this.dateWithTimeStamp = dateWithTimeStamp;
	}

	@Override
	public String toString() {
		return "DMSEntity [id=" + id + ", merchantPayId=" + merchantPayId + ", cbCaseId=" + cbCaseId + ", dtOfTxn="
				+ dtOfTxn + ", txnAmount=" + txnAmount + ", pgCaseId=" + pgCaseId + ", merchantTxnId=" + merchantTxnId
				+ ", orderId=" + orderId + ", pgRefNo=" + pgRefNo + ", bankTxnId=" + bankTxnId + ", cbAmount="
				+ cbAmount + ", cbReason=" + cbReason + ", cbReasonCode=" + cbReasonCode + ", cbIntimationDate="
				+ cbIntimationDate + ", cbDdlineDate=" + cbDdlineDate + ", modeOfPayment=" + modeOfPayment
				+ ", acqName=" + acqName + ", settlemtDate=" + settlemtDate + ", customerName=" + customerName
				+ ", customerPhone=" + customerPhone + ", email=" + email + ", nemail=" + nemail + ", filePaths="
				+ filePaths + ", status=" + status + ", createdBy=" + createdBy + ", updatedBy=" + updatedBy
				+ ", merchantDocUploadFlag=" + merchantDocUploadFlag + ", cbClosedInFavorBank=" + cbClosedInFavorBank
				+ ", cbClosedInFavorMerchant=" + cbClosedInFavorMerchant + ", cbPodDate=" + cbPodDate
				+ ", cbInitiatedDate=" + cbInitiatedDate + ", cbAcceptDate=" + cbAcceptDate + ", cbCloseDate="
				+ cbCloseDate + ", cbFavourDate=" + cbFavourDate + "]";
	}

	public DMSEntity(long id, String merchantPayId, String cbCaseId, String dtOfTxn, String txnAmount, String pgCaseId,
			String merchantTxnId, String orderId, String pgRefNo, String bankTxnId, String cbAmount, String cbReason,
			String cbReasonCode, String cbIntimationDate, String cbDdlineDate, String modeOfPayment, String acqName,
			String settlemtDate, String customerName, String customerPhone, String email, String nemail,
			String filePaths, Status status, String createdBy, String updatedBy, boolean merchantDocUploadFlag,
			String cbClosedInFavorBank, String cbClosedInFavorMerchant, String cbPodDate, String cbInitiatedDate,
			String cbAcceptDate, String cbCloseDate, String cbFavourDate) {
		super();
		this.id = id;
		this.merchantPayId = merchantPayId;
		this.cbCaseId = cbCaseId;
		this.dtOfTxn = dtOfTxn;
		this.txnAmount = txnAmount;
		this.pgCaseId = pgCaseId;
		this.merchantTxnId = merchantTxnId;
		this.orderId = orderId;
		this.pgRefNo = pgRefNo;
		this.bankTxnId = bankTxnId;
		this.cbAmount = cbAmount;
		this.cbReason = cbReason;
		this.cbReasonCode = cbReasonCode;
		this.cbIntimationDate = cbIntimationDate;
		this.cbDdlineDate = cbDdlineDate;
		this.modeOfPayment = modeOfPayment;
		this.acqName = acqName;
		this.settlemtDate = settlemtDate;
		this.customerName = customerName;
		this.customerPhone = customerPhone;
		this.email = email;
		this.nemail = nemail;
		this.filePaths = filePaths;
		this.status = status;
		this.createdBy = createdBy;
		this.updatedBy = updatedBy;
		this.merchantDocUploadFlag = merchantDocUploadFlag;
		this.cbClosedInFavorBank = cbClosedInFavorBank;
		this.cbClosedInFavorMerchant = cbClosedInFavorMerchant;
		this.cbPodDate = cbPodDate;
		this.cbInitiatedDate = cbInitiatedDate;
		this.cbAcceptDate = cbAcceptDate;
		this.cbCloseDate = cbCloseDate;
		this.cbFavourDate = cbFavourDate;
	}
		

}