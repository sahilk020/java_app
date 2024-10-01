package com.pay10.commons.user;

import com.pay10.commons.util.UserStatusType;

public class MerchantDetails {
	
	public MerchantDetails(){
		
	}
	
	public MerchantDetails(String payId,String resellerId,String businessName, String emailId, UserStatusType status,String mobile, String registrationDate,String userType,String businessType,String tenantId){
		this.payId = payId;
		this.resellerId = resellerId;
		this.setBusinessName(businessName);
		this.emailId = emailId;
		this.mobile = mobile;
		this.registrationDate = registrationDate;
		this.status = status;
		this.userType = userType;
		this.businessType = businessType;
		this.tenantId = tenantId;
	}
	
	private String payId;
	private String resellerId;
	private String businessName;
	private String emailId;
	private String mobile;
	private String registrationDate;
	private UserStatusType status;
	private String userType;
	private String businessType;
	private String tenantId;
	private String tenantNumber;
	private String companyName;

	public String getAccountNo() {
		return accountNo;
	}

	public void setAccountNo(String accountNo) {
		this.accountNo = accountNo;
	}

	public String getMerchantGstNo() {
		return merchantGstNo;
	}

	public void setMerchantGstNo(String merchantGstNo) {
		this.merchantGstNo = merchantGstNo;
	}

	public String getCin() {
		return cin;
	}

	public void setCin(String cin) {
		this.cin = cin;
	}

	public String getPanCard() {
		return panCard;
	}

	public void setPanCard(String panCard) {
		this.panCard = panCard;
	}

	public String getEncKey() {
		return encKey;
	}

	public void setEncKey(String encKey) {
		this.encKey = encKey;
	}

	//added feild by vijaylakshmi
	private String accountNo;
	private String merchantGstNo;
	private String cin;
	private String panCard;
	private String encKey;
	public String getTenantNumber() {
		return tenantNumber;
	}

	public void setTenantNumber(String tenantNumber) {
		this.tenantNumber = tenantNumber;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}
	
	public String getTenantId() {
		return tenantId;
	}
	public void setTenantId(String tenantId) {
		this.tenantId = tenantId;
	}
	
	public UserStatusType getStatus() {
		return status;
	}
	public void setStatus(UserStatusType status) {
		this.status = status;
	}
	public String getPayId() {
		return payId;
	}
	public String getBusinessName() {
		return businessName;
	}
	public void setBusinessName(String businessName) {
		this.businessName = businessName;
	}
	public void setPayId(String payId) {
		this.payId = payId;
	}
	
	public String getEmailId() {
		return emailId;
	}
	public void setEmailId(String emailId) {
		this.emailId = emailId;
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
	public String getUserType() {
		return userType;
	}
	public void setUserType(String userType) {
		this.userType = userType;
	}
	public String getResellerId() {
		return resellerId;
	}
	public void setResellerId(String resellerId) {
		this.resellerId = resellerId;
	}
	public String getBusinessType() {
		return businessType;
	}
	public void setBusinessType(String businessType) {
		this.businessType = businessType;
	}
	

}
