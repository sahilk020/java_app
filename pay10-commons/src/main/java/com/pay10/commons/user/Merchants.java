package com.pay10.commons.user;

import java.io.Serializable;

public class Merchants implements Serializable, Comparable<Merchants> {

	public Merchants() {

	}

	private static final long serialVersionUID = -5829924589073475754L;

	private String emailId;
	private String payId;
	private String businessName;
	private String firstName;
	private String lastName;
	private String mobile;
	private String resellerId;
	private Boolean isActive;
	private String registrationDate;
	private String status;
	private String resellerName;
	private String uuId;
	private String userType;
	 private String currency;

	public void setMerchant(User user) {
		setEmailId(user.getEmailId());
		setBusinessName(user.getBusinessName());
		setPayId(user.getPayId());
		setCurrency(user.getCurrency());

	}

	public String getEmailId() {
		return emailId;
	}

	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}

	public String getPayId() {
		return payId;
	}

	public void setPayId(String payId) {
		this.payId = payId;
	}

	public String getBusinessName() {
		return businessName;
	}

	public void setBusinessName(String businessName) {
		this.businessName = businessName;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public Boolean getIsActive() {
		return isActive;
	}

	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}

	public String getResellerId() {
		return resellerId;
	}

	public void setResellerId(String resellerId) {
		this.resellerId = resellerId;
	}

	public String getRegistrationDate() {
		return registrationDate;
	}

	public void setRegistrationDate(String registrationDate) {
		this.registrationDate = registrationDate;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getResellerName() {
		return resellerName;
	}

	public void setResellerName(String resellerName) {
		this.resellerName = resellerName;
	}

	@Override
	public int compareTo(Merchants o) {
		return getRegistrationDate().compareTo(o.getRegistrationDate());
	}

	public String getUuId() {
		return uuId;
	}

	public void setUuId(String uuId) {
		this.uuId = uuId;
	}

	public String getUserType() {
		return userType;
	}

	public void setUserType(String userType) {
		this.userType = userType;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}
}
