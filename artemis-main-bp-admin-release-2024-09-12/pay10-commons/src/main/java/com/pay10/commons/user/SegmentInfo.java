package com.pay10.commons.user;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Proxy;

@Entity
@Proxy(lazy = false)
@Table(name = "User")
public class SegmentInfo implements Serializable {

	private static final long serialVersionUID = -8794117484789299407L;

	@Id
	@Column(nullable = false, unique = true)
	private String emailId;

	@Column
	private String businessName;

	@Column
	private String payId;

	@Column
	private String userStatus;

	@Column
	private String mobile;

	@Column
	private String registrationDate;

	@Column
	private String segment;

	@Column
	private String userType;

	public String getEmailId() {
		return emailId;
	}

	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}

	public String getBusinessName() {
		return businessName;
	}

	public void setBusinessName(String businessName) {
		this.businessName = businessName;
	}

	public String getPayId() {
		return payId;
	}

	public void setPayId(String payId) {
		this.payId = payId;
	}

	public String getUserStatus() {
		return userStatus;
	}

	public void setUserStatus(String userStatus) {
		this.userStatus = userStatus;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getRegistrationDate() {
		return registrationDate;
	}

	public void setRegistrationDate(String registrationDate) {
		this.registrationDate = registrationDate;
	}

	public String getSegment() {
		return segment;
	}

	public void setSegment(String segment) {
		this.segment = segment;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public String getUserType() {
		return userType;
	}

	public void setUserType(String userType) {
		this.userType = userType;
	}

	@Override
	public String toString() {
		return "SegmentInfo [emailId=" + emailId + ", businessName=" + businessName + ", payId=" + payId
				+ ", userStatus=" + userStatus + ", mobile=" + mobile + ", registrationDate=" + registrationDate
				+ ", segment=" + segment + ", userType=" + userType + "]";
	}

}