package com.pay10.commons.user;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Proxy;

@Entity
@Table(name = "BankSettle")
@Proxy(lazy = false)
public class BankSettle implements Serializable{

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	private String productId;
	private boolean status;
	private String bankName;
	private String ifscCode;
	private String accountHolderName;
	private String bankCurrency;
	private String branchName;
	private String accountNumber;
	private String email;
	private String panCard;
	private String createdBy;
	private Date createdDate;
	private String updatedBy;
	private Date lastUpdatedDate;
	private boolean isDeletedFlag;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getProductId() {
		return productId;
	}

	public void setProductId(String productId) {
		this.productId = productId;
	}

	public boolean isStatus() {
		return status;
	}

	public void setStatus(boolean status) {
		this.status = status;
	}

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

	public String getAccountHolderName() {
		return accountHolderName;
	}

	public void setAccountHolderName(String accountHolderName) {
		this.accountHolderName = accountHolderName;
	}

	public String getBankCurrency() {
		return bankCurrency;
	}

	public void setBankCurrency(String bankCurrency) {
		this.bankCurrency = bankCurrency;
	}

	public String getBranchName() {
		return branchName;
	}

	public void setBranchName(String branchName) {
		this.branchName = branchName;
	}

	public String getAccountNumber() {
		return accountNumber;
	}

	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPanCard() {
		return panCard;
	}

	public void setPanCard(String panCard) {
		this.panCard = panCard;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public String getUpdatedBy() {
		return updatedBy;
	}

	public void setUpdatedBy(String updatedBy) {
		this.updatedBy = updatedBy;
	}

	public Date getLastUpdatedDate() {
		return lastUpdatedDate;
	}

	public void setLastUpdatedDate(Date lastUpdatedDate) {
		this.lastUpdatedDate = lastUpdatedDate;
	}

	public boolean isDeletedFlag() {
		return isDeletedFlag;
	}

	public void setDeletedFlag(boolean isDeletedFlag) {
		this.isDeletedFlag = isDeletedFlag;
	}

	@Override
	public String toString() {
		return "BankSettle [id=" + id + ", productId=" + productId + ", status=" + status + ", bankName=" + bankName
				+ ", ifscCode=" + ifscCode + ", accountHolderName=" + accountHolderName + ", bankCurrency="
				+ bankCurrency + ", branchName=" + branchName + ", accountNumber=" + accountNumber + ", email=" + email
				+ ", createdBy=" + createdBy + ", createdDate=" + createdDate + ", updatedBy=" + updatedBy
				+ ", lastUpdatedDate=" + lastUpdatedDate + ", isDeletedFlag=" + isDeletedFlag + "]";
	}

}
