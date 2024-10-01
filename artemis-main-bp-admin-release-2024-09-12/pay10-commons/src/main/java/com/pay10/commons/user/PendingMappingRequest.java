package com.pay10.commons.user;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Proxy;

import com.pay10.commons.util.TDRStatus;

@Entity
@Proxy(lazy= false)
@Table
public class PendingMappingRequest implements Serializable{
	
	private static final long serialVersionUID = 6451344616066902378L;
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	
	@Enumerated(EnumType.STRING)
	private TDRStatus status;

	@Column(name="accountCurrencySet", columnDefinition="TEXT")
	private String accountCurrencySet;
	
	@Column(name="mapString", columnDefinition="TEXT")
	private String mapString;
	
	private String acquirer;
	private String merchantEmailId;
	
	private String requestedBy;
	private String processedBy;
	
	private Date createdDate;
	private Date updatedDate;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	public Date getCreatedDate() {
		return createdDate;
	}
	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}
	public Date getUpdatedDate() {
		return updatedDate;
	}
	public void setUpdatedDate(Date updatedDate) {
		this.updatedDate = updatedDate;
	}
	public TDRStatus getStatus() {
		return status;
	}
	public void setStatus(TDRStatus status) {
		this.status = status;
	}
	public String getAccountCurrencySet() {
		return accountCurrencySet;
	}
	public void setAccountCurrencySet(String accountCurrencySet) {
		this.accountCurrencySet = accountCurrencySet;
	}
	public String getMapString() {
		return mapString;
	}
	public void setMapString(String mapString) {
		this.mapString = mapString;
	}
	public String getAcquirer() {
		return acquirer;
	}
	public void setAcquirer(String acquirer) {
		this.acquirer = acquirer;
	}
	public String getMerchantEmailId() {
		return merchantEmailId;
	}
	public void setMerchantEmailId(String merchantEmailId) {
		this.merchantEmailId = merchantEmailId;
	}
	public String getRequestedBy() {
		return requestedBy;
	}
	public void setRequestedBy(String requestedBy) {
		this.requestedBy = requestedBy;
	}
	public String getProcessedBy() {
		return processedBy;
	}
	public void setProcessedBy(String processedBy) {
		this.processedBy = processedBy;
	}
}
