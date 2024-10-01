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

public class DashboardPreferenceSet implements Serializable {

	
	// DashboardPreferenceSet
	@Id
	@Column(nullable=true,unique=true)
	private Long id;
	private String emailId;
	private Date createdDate;
	private String preferenceSetConstant;
			
	public DashboardPreferenceSet(){
		
	}
	
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	
	public String getEmailId() {
		return emailId;
	}
	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}
	
	
	public String getPreferenceSetConstant() {
		return preferenceSetConstant;
	}

	public void setPreferenceSetConstant(String preferenceSetConstant) {
		this.preferenceSetConstant = preferenceSetConstant;
	}

	public Date getCreatedDate() {
		return createdDate;
	}
	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}
	
}
