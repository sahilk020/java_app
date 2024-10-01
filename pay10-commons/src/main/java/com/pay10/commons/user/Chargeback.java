package com.pay10.commons.user;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Blob;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Proxy;

/**
 * @modified shubhamchauhan
 */

@Entity
@Proxy(lazy= false)
@Table
public class Chargeback implements Serializable{

	private static final long serialVersionUID = -6035765912522135772L;
	
	@Id
	@Column(nullable = false, unique = true)
	private String id; // use transaction manager class.
	private String caseId;
	private String targetDate;
	private Date createDate; // Bank assign date 
	private String custEmail;
	private String orderId;
	private BigDecimal amount;
	private String chargebackType;
	private String chargebackStatus; // To be set by admin
	private String payId;
	private String transactionId;
	
	@OneToMany(targetEntity = ChargebackComment.class, fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	private Set<ChargebackComment> chargebackComment = new HashSet<ChargebackComment>();
	
	
	@Column(length = 65535,columnDefinition="Text")
	private String comments;

	// New Fields
	private Date cbRegistrationDate; // Date when chargeback was created. For internal purpose.
	private String pg_ref_num;
	private String txn_date;
	private String cust_phone;
	private String acq_id;
	private String txnStatus;
	private String businessName;
	
	@Lob
	private byte[] chargebackChat;
	
	@Lob
	private byte[] chargebackTrail;
	
	
//  Other fields...
	private String cardNumber;
	private String mopType;
	private String paymentType;
	private String status;
	
	private String internalCustIP;
	private String internalCustCountryName;
	private String internalCardIssusserBank;
	private String internalCardIssusserCountry;
	
	private String currencyCode; // class currency.java (getAlplabetic code);
	private String currencyNameCode;
		
	//Refund service	
	private BigDecimal capturedAmount;	// Same as amount
	private BigDecimal authorizedAmount;	// Amount
	private BigDecimal fixedTxnFee;	//  Not req
	private BigDecimal tdr; // PG_TDR_SC + ACQUIRER_TDR_SC
	private BigDecimal serviceTax; // PG_GST + ACQUIRER_GST
	private BigDecimal chargebackAmount; // Amount
	private BigDecimal netAmount; // captured amount - (TDR + GST).
	private BigDecimal percentecServiceTax; // Not req
	private BigDecimal merchantTDR; // Not req
	
// Note : upload documents as PDF, JPG, JPEG, PNG. File size : 10MB or 10 files whichever earlier.
//	Validate files. Example: I changed extension of file from .exe to 
//	pdf then it should not upload.
	
	private Date updateDate;
	private String commentedBy;
	
	public String getComments() {
		return comments;
	}
	public void setComments(String comments) {
		this.comments = comments;
	}

	@Transient
	private String documentId;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getOrderId() {
		return orderId;
	}
	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public String getPayId() {
		return payId;
	}
	public void setPayId(String payId) {
		this.payId = payId;
	}
	public String getCardNumber() {
		return cardNumber;
	}
	public void setCardNumber(String cardNumber) {
		this.cardNumber = cardNumber;
	}
	public String getMopType() {
		return mopType;
	}
	public void setMopType(String mopType) {
		this.mopType = mopType;
	}
	public String getPaymentType() {
		return paymentType;
	}
	public void setPaymentType(String paymentType) {
		this.paymentType = paymentType;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getCustEmail() {
		return custEmail;
	}
	public void setCustEmail(String custEmail) {
		this.custEmail = custEmail;
	}
	public String getInternalCustIP() {
		return internalCustIP;
	}
	public void setInternalCustIP(String internalCustIP) {
		this.internalCustIP = internalCustIP;
	}
	public String getInternalCustCountryName() {
		return internalCustCountryName;
	}
	public void setInternalCustCountryName(String internalCustCountryName) {
		this.internalCustCountryName = internalCustCountryName;
	}
	public String getInternalCardIssusserBank() {
		return internalCardIssusserBank;
	}
	public void setInternalCardIssusserBank(String internalCardIssusserBank) {
		this.internalCardIssusserBank = internalCardIssusserBank;
	}
	public String getInternalCardIssusserCountry() {
		return internalCardIssusserCountry;
	}
	public void setInternalCardIssusserCountry(String internalCardIssusserCountry) {
		this.internalCardIssusserCountry = internalCardIssusserCountry;
	}
	public String getCurrencyCode() {
		return currencyCode;
	}
	public void setCurrencyCode(String currencyCode) {
		this.currencyCode = currencyCode;
	}
	public String getCurrencyNameCode() {
		return currencyNameCode;
	}
	public void setCurrencyNameCode(String currencyNameCode) {
		this.currencyNameCode = currencyNameCode;
	}
	public BigDecimal getAmount() {
		return amount;
	}
	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}
	public BigDecimal getCapturedAmount() {
		return capturedAmount;
	}
	public void setCapturedAmount(BigDecimal capturedAmount) {
		this.capturedAmount = capturedAmount;
	}
	public BigDecimal getAuthorizedAmount() {
		return authorizedAmount;
	}
	public void setAuthorizedAmount(BigDecimal authorizedAmount) {
		this.authorizedAmount = authorizedAmount;
	}
	public BigDecimal getFixedTxnFee() {
		return fixedTxnFee;
	}
	public void setFixedTxnFee(BigDecimal fixedTxnFee) {
		this.fixedTxnFee = fixedTxnFee;
	}
		public BigDecimal getTdr() {
		return tdr;
	}
	public void setTdr(BigDecimal tdr) {
		this.tdr = tdr;
	}
	public BigDecimal getServiceTax() {
		return serviceTax;
	}
	public void setServiceTax(BigDecimal serviceTax) {
		this.serviceTax = serviceTax;
	}
	public BigDecimal getChargebackAmount() {
		return chargebackAmount;
	}
	public void setChargebackAmount(BigDecimal chargebackAmount) {
		this.chargebackAmount = chargebackAmount;
	}
	public BigDecimal getNetAmount() {
		return netAmount;
	}
	public void setNetAmount(BigDecimal netAmount) {
		this.netAmount = netAmount;
	}
	public BigDecimal getPercentecServiceTax() {
		return percentecServiceTax;
	}
	public void setPercentecServiceTax(BigDecimal percentecServiceTax) {
		this.percentecServiceTax = percentecServiceTax;
	}
	public BigDecimal getMerchantTDR() {
		return merchantTDR;
	}
	public void setMerchantTDR(BigDecimal merchantTDR) {
		this.merchantTDR = merchantTDR;
	}
	public String getCaseId() {
		return caseId;
	}
	public void setCaseId(String caseId) {
		this.caseId = caseId;
	}
	
	public String getTargetDate() {
		return targetDate;
	}
	public void setTargetDate(String targetDate) {
		this.targetDate = targetDate;
	}

	public String getCommentedBy() {
		return commentedBy;
	}
	public void setCommentedBy(String commentedBy) {
		this.commentedBy = commentedBy;
	}
	public String getChargebackType() {
		return chargebackType;
	}
	public void setChargebackType(String chargebackType) {
		this.chargebackType = chargebackType;
	}
	public String getChargebackStatus() {
		return chargebackStatus;
	}
	public void setChargebackStatus(String chargebackStatus) {
		this.chargebackStatus = chargebackStatus;
	}
	public String getTransactionId() {
		return transactionId;
	}
	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}
	public Date getUpdateDate() {
		return updateDate;
	}
	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}
	public Date getCreateDate() {
		return createDate;
	}
	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}
	public String getDocumentId() {
		return documentId;
	}
	public void setDocumentId(String documentId) {
		this.documentId = documentId;
	}
/*	public String getComments() {
		try{
			return new String(comments.getBytes(1l, (int) comments.length()));
		}
		catch(Exception e){
			return "Error";
		}
		
	}
	public void setComments(java.sql.Blob blob) {
		this.comments = blob;
	}*/
	public Set<ChargebackComment> getChargebackComments() {
		return chargebackComment;
	}

	public void setChargebackComments(Set<ChargebackComment> chargebackComment) {
		this.chargebackComment = chargebackComment;
	}

	public void addChargebackComments(ChargebackComment chargebackComment) {
		this.chargebackComment.add(chargebackComment);
	}

	public void removeChargebackComments(ChargebackComment chargebackComment) {
		this.chargebackComment.remove(chargebackComment);
	}
	public String getPg_ref_num() {
		return pg_ref_num;
	}
	public void setPg_ref_num(String pg_ref_num) {
		this.pg_ref_num = pg_ref_num;
	}
	public String getTxn_date() {
		return txn_date;
	}
	public void setTxn_date(String txn_date) {
		this.txn_date = txn_date;
	}
	public String getCust_phone() {
		return cust_phone;
	}
	public void setCust_phone(String cust_phone) {
		this.cust_phone = cust_phone;
	}
	public String getAcq_id() {
		return acq_id;
	}
	public void setAcq_id(String acq_id) {
		this.acq_id = acq_id;
	}
	
	public byte[] getChargebackChat() {
		return chargebackChat;
	}
	public void setChargebackChat(byte[] chargebackChat) {
		this.chargebackChat = chargebackChat;
	}
	public byte[] getChargebackTrail() {
		return chargebackTrail;
	}
	public void setChargebackTrail(byte[] chargebackTrail) {
		this.chargebackTrail = chargebackTrail;
	}
	public String getTxnStatus() {
		return txnStatus;
	}
	public void setTxnStatus(String txnStatus) {
		this.txnStatus = txnStatus;
	}
	public String getBusinessName() {
		return businessName;
	}
	public void setBusinessName(String businessName) {
		this.businessName = businessName;
	}
	public Date getCbRegistrationDate() {
		return cbRegistrationDate;
	}
	public void setCbRegistrationDate(Date cbRegistrationDate) {
		this.cbRegistrationDate = cbRegistrationDate;
	}
	@Override
	public String toString() {
		return "Chargeback [id=" + id + ", caseId=" + caseId + ", targetDate=" + targetDate + ", createDate="
				+ createDate + ", custEmail=" + custEmail + ", orderId=" + orderId + ", amount=" + amount
				+ ", chargebackType=" + chargebackType + ", chargebackStatus=" + chargebackStatus + ", payId=" + payId
				+ ", transactionId=" + transactionId + ", chargebackComment=" + chargebackComment + ", comments="
				+ comments + ", cbRegistrationDate=" + cbRegistrationDate + ", pg_ref_num=" + pg_ref_num + ", txn_date="
				+ txn_date + ", cust_phone=" + cust_phone + ", acq_id=" + acq_id + ", txnStatus=" + txnStatus
				+ ", businessName=" + businessName + ", chargebackChat=" + Arrays.toString(chargebackChat)
				+ ", chargebackTrail=" + Arrays.toString(chargebackTrail) + ", cardNumber=" + cardNumber + ", mopType="
				+ mopType + ", paymentType=" + paymentType + ", status=" + status + ", internalCustIP=" + internalCustIP
				+ ", internalCustCountryName=" + internalCustCountryName + ", internalCardIssusserBank="
				+ internalCardIssusserBank + ", internalCardIssusserCountry=" + internalCardIssusserCountry
				+ ", currencyCode=" + currencyCode + ", currencyNameCode=" + currencyNameCode + ", capturedAmount="
				+ capturedAmount + ", authorizedAmount=" + authorizedAmount + ", fixedTxnFee=" + fixedTxnFee + ", tdr="
				+ tdr + ", serviceTax=" + serviceTax + ", chargebackAmount=" + chargebackAmount + ", netAmount="
				+ netAmount + ", percentecServiceTax=" + percentecServiceTax + ", merchantTDR=" + merchantTDR
				+ ", updateDate=" + updateDate + ", commentedBy=" + commentedBy + ", documentId=" + documentId + "]";
	}
	
		
}
