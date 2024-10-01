package com.pay10.commons.user;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Proxy;

@Entity
@Proxy(lazy= false)
@Table
public class NodalCreditDebitHistory implements Serializable{
	
	private static final long serialVersionUID = 8296848264460730044L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	
	private Date createdDate;
	private String requestedBy;
	private String status;
	private String payId;
	private String paymentMethod;
	private String type;
	private String acquirer;
	private Date nodalDate;
	private String captureDate;
	private String saleCount;
	private String creditAmount;
	private String refundCount;
	private String debitAmount;
	private String netAmount;
	private String merchantName;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	public String getRequestedBy() {
		return requestedBy;
	}
	public void setRequestedBy(String requestedBy) {
		this.requestedBy = requestedBy;
	}
	public Date getCreatedDate() {
		return createdDate;
	}
	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getPayId() {
		return payId;
	}
	public void setPayId(String payId) {
		this.payId = payId;
	}
	public String getPaymentMethod() {
		return paymentMethod;
	}
	public void setPaymentMethod(String paymentMethod) {
		this.paymentMethod = paymentMethod;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getAcquirer() {
		return acquirer;
	}
	public void setAcquirer(String acquirer) {
		this.acquirer = acquirer;
	}
	public Date getNodalDate() {
		return nodalDate;
	}
	public void setNodalDate(Date nodalDate) {
		this.nodalDate = nodalDate;
	}
	public String getSaleCount() {
		return saleCount;
	}
	public void setSaleCount(String saleCount) {
		this.saleCount = saleCount;
	}
	public String getRefundCount() {
		return refundCount;
	}
	public void setRefundCount(String refundCount) {
		this.refundCount = refundCount;
	}
	public String getNetAmount() {
		return netAmount;
	}
	public void setNetAmount(String netAmount) {
		this.netAmount = netAmount;
	}
	public String getCaptureDate() {
		return captureDate;
	}
	public void setCaptureDate(String captureDate) {
		this.captureDate = captureDate;
	}
	public String getMerchantName() {
		return merchantName;
	}
	public void setMerchantName(String merchantName) {
		this.merchantName = merchantName;
	}
	public String getCreditAmount() {
		return creditAmount;
	}
	public void setCreditAmount(String creditAmount) {
		this.creditAmount = creditAmount;
	}
	public String getDebitAmount() {
		return debitAmount;
	}
	public void setDebitAmount(String debitAmount) {
		this.debitAmount = debitAmount;
	}
	
}
