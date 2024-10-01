package com.pay10.commons.user;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Lob;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Proxy;

/**
 * @ Rajendra
 */

@Entity
@Proxy(lazy= false)
@Table
public class CompanyProfile implements Serializable {
	private static final long serialVersionUID = 8476685267435231830L;


	
	// Company details
	@Id
	@Column(nullable=false,unique=true)
	private String emailId;
	
	private Long tenantId;
	private String tenantNumber;
	private String tanNumber;
	private String companyName;
	private String mobile;
	private String telephoneNo;
	private String fax;
	
	private String address;
	private String city;
	private String state;
	private String country;
	private String postalCode;
	private String hsnSacCode;
	private String cin;
	private String companyGstNo;
	private String companyUrl;
	private String pgUrl;
	
	// Company Bank details
	private String bankName;
	private String ifscCode;
	private String accHolderName;
	private String currency;
	private String branchName;
	private String panCard;
	private String panName;
	private String accountNo;
	
	private String tenantStatus;
		
	// for acquirer details saving 
	private String acquirerString;
	
	// Direcor details saving
	private String directorname;
	private String directorAadharNumber;
	private String directorpan;
	private String directorMobileNumber;
	private String directorEmail;
	
	// Company logos 
	private String pgLogo;
	private String headerTextLogo;
	private String headerIconLogo;
	private String footerLogo;
	private String logoutLogo;
	private String thankYouLogo;
	private String receiptIconLogo;
	private String receiptTextLogo;
	private String crmLogo;
	
	// Company documents
	private String companyPanImage;
	private String companyGstImage;
	private String companyTanImage;
	private String companyAoaImage;
	


	public String getCompanyUrl() {
		return companyUrl;
	}

	public void setCompanyUrl(String companyUrl) {
		this.companyUrl = companyUrl;
	}

	public String getPgLogo() {
		return pgLogo;
	}

	public void setPgLogo(String pgLogo) {
		this.pgLogo = pgLogo;
	}
	
	
	public String getHeaderTextLogo() {
		return headerTextLogo;
	}

	public void setHeaderTextLogo(String headerTextLogo) {
		this.headerTextLogo = headerTextLogo;
	}

	public String getHeaderIconLogo() {
		return headerIconLogo;
	}

	public void setHeaderIconLogo(String headerIconLogo) {
		this.headerIconLogo = headerIconLogo;
	}

	public String getFooterLogo() {
		return footerLogo;
	}

	public void setFooterLogo(String footerLogo) {
		this.footerLogo = footerLogo;
	}

	public String getLogoutLogo() {
		return logoutLogo;
	}

	public void setLogoutLogo(String logoutLogo) {
		this.logoutLogo = logoutLogo;
	}

	public String getThankYouLogo() {
		return thankYouLogo;
	}

	public void setThankYouLogo(String thankYouLogo) {
		this.thankYouLogo = thankYouLogo;
	}

	public String getReceiptIconLogo() {
		return receiptIconLogo;
	}

	public void setReceiptIconLogo(String receiptIconLogo) {
		this.receiptIconLogo = receiptIconLogo;
	}

	public String getReceiptTextLogo() {
		return receiptTextLogo;
	}

	public void setReceiptTextLogo(String receiptTextLogo) {
		this.receiptTextLogo = receiptTextLogo;
	}

	public String getCrmLogo() {
		return crmLogo;
	}

	public void setCrmLogo(String crmLogo) {
		this.crmLogo = crmLogo;
	}

	public String getPgUrl() {
		return pgUrl;
	}

	public void setPgUrl(String pgUrl) {
		this.pgUrl = pgUrl;
	}


	public Long getTenantId() {
		return tenantId;
	}
	
	public String getTenantStatus() {
		return tenantStatus;
	}

	public void setTenantStatus(String tenantStatus) {
		this.tenantStatus = tenantStatus;
	}

	public void setTenantId(Long tenantId) {
		this.tenantId = tenantId;
	}
	
	public String getTenantNumber() {
		return tenantNumber;
	}

	public void setTenantNumber(String tenantNumber) {
		this.tenantNumber = tenantNumber;
	}
	
	public String getEmailId() {
		return emailId;
	}

	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}
	
	public String getTanNumber() {
		return tanNumber;
	}

	public void setTanNumber(String tanNumber) {
		this.tanNumber = tanNumber;
	}
	
	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}
	
	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	
	public String getTelephoneNo() {
		return telephoneNo;
	}

	public void setTelephoneNo(String telephoneNo) {
		this.telephoneNo = telephoneNo;
	}

	public String getFax() {
		return fax;
	}

	public void setFax(String fax) {
		this.fax = fax;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getPostalCode() {
		return postalCode;
	}

	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}
	
	public String getHsnSacCode() {
		return hsnSacCode;
	}

	public void setHsnSacCode(String hsnSacCode) {
		this.hsnSacCode = hsnSacCode;
	}
	
	public String getCin() {
		return cin;
	}

	public void setCin(String cin) {
		this.cin = cin;
	}
	
	public String getCompanyGstNo() {
		return companyGstNo;
	}

	public void setCompanyGstNo(String companyGstNo) {
		this.companyGstNo = companyGstNo;
	}
	
	// Bank details getter and setter methods 
	public String getBankName() {
		return bankName;
	}

	public void setBankName(String bankName) {
		this.bankName = bankName;
	}

	public String getIfscCode() {
		return ifscCode;
	}

	public void setIfscCode(String ifscCode) {
		this.ifscCode = ifscCode;
	}

	public String getAccHolderName() {
		return accHolderName;
	}

	public void setAccHolderName(String accHolderName) {
		this.accHolderName = accHolderName;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public String getBranchName() {
		return branchName;
	}

	public void setBranchName(String branchName) {
		this.branchName = branchName;
	}

	public String getPanCard() {
		return panCard;
	}

	public void setPanCard(String panCard) {
		this.panCard = panCard;
	}

	public String getPanName() {
		return panName;
	}

	public void setPanName(String panName) {
		this.panName = panName;
	}
	public String getAccountNo() {
		return accountNo;
	}

	public void setAccountNo(String accountNo) {
		this.accountNo = accountNo;
	}

	public String getAcquirerString() {
		return acquirerString;
	}

	public void setAcquirerString(String acquirerString) {
		this.acquirerString = acquirerString;
	}

	public String getDirectorname() {
		return directorname;
	}

	public void setDirectorname(String directorname) {
		this.directorname = directorname;
	}

	public String getDirectorAadharNumber() {
		return directorAadharNumber;
	}

	public void setDirectorAadharNumber(String directorAadharNumber) {
		this.directorAadharNumber = directorAadharNumber;
	}

	public String getDirectorpan() {
		return directorpan;
	}

	public void setDirectorpan(String directorpan) {
		this.directorpan = directorpan;
	}

	public String getDirectorMobileNumber() {
		return directorMobileNumber;
	}

	public void setDirectorMobileNumber(String directorMobileNumber) {
		this.directorMobileNumber = directorMobileNumber;
	}

	public String getDirectorEmail() {
		return directorEmail;
	}

	public void setDirectorEmail(String directorEmail) {
		this.directorEmail = directorEmail;
	}
	
	public String getCompanyPanImage() {
		return companyPanImage;
	}

	public void setCompanyPanImage(String companyPanImage) {
		this.companyPanImage = companyPanImage;
	}

	public String getCompanyGstImage() {
		return companyGstImage;
	}

	public void setCompanyGstImage(String companyGstImage) {
		this.companyGstImage = companyGstImage;
	}

	public String getCompanyTanImage() {
		return companyTanImage;
	}

	public void setCompanyTanImage(String companyTanImage) {
		this.companyTanImage = companyTanImage;
	}

	public String getCompanyAoaImage() {
		return companyAoaImage;
	}

	public void setCompanyAoaImage(String companyAoaImage) {
		this.companyAoaImage = companyAoaImage;
	}
	
}
