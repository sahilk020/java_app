package com.pay10.commons.user;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Proxy;

/**
 * @ Rajendra
 */

@Entity
@Proxy(lazy= false)
@Table

public class MerchantFpsAccess  implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4880233280049930082L;
	@Id
	@Column(nullable=true,unique=true)
	private Long id;
	private String payId;
	private String createdBy;
	private String updatedBy;
	private Date createdDate;
	private Date updateDate;
	private String preferenceSet;
	private boolean activateMerchantMgmt;
	



	public MerchantFpsAccess(){
		
	}

	public boolean isActivateMerchantMgmt() {
		return activateMerchantMgmt;
	}


	public void setActivateMerchantMgmt(boolean activateMerchantMgmt) {
		this.activateMerchantMgmt = activateMerchantMgmt;
	}
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	
	public String getPayId() {
		return payId;
	}
	public void setPayId(String payId) {
		this.payId = payId;
	}
	
	
	public String getPreferenceSet() {
		return preferenceSet;
	}

	public void setPreferenceSet(String preferenceSet) {
		this.preferenceSet = preferenceSet;
	}

	public Date getCreatedDate() {
		return createdDate;
	}
	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}
	
	public Date getUpdateDate() {
		return updateDate;
	}
	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}

	public String getUpdatedBy() {
		return updatedBy;
	}
	
	public void setUpdatedBy(String updatedBy) {
		this.updatedBy = updatedBy;
	}
	
	public String getCreatedBy() {
		return createdBy;
	}


	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}
}
