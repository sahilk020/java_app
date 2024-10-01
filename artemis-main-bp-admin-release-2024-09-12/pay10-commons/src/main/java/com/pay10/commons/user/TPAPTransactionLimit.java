package com.pay10.commons.user;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Proxy;

@Entity
@Proxy(lazy = false)
@Table(name = "tpap_transaction_limit_info")
public class TPAPTransactionLimit {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

	@Column
	private String dailyLimit;

	@Column
	private String weeklyLimit;

	@Column
	private String monthlyLimit;

	@Column
	private String transactionLimit;

	@Column
	private String MaxVPATxnLimit;

	@Column
	private String status;

	@Column
	private String createdDate;

	@Column
	private String createdBy;

	@Column
	private String updateBy;

	@Column
	private String updatedDate;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getDailyLimit() {
		return dailyLimit;
	}

	public void setDailyLimit(String dailyLimit) {
		this.dailyLimit = dailyLimit;
	}

	public String getWeeklyLimit() {
		return weeklyLimit;
	}

	public void setWeeklyLimit(String weeklyLimit) {
		this.weeklyLimit = weeklyLimit;
	}

	public String getMonthlyLimit() {
		return monthlyLimit;
	}

	public void setMonthlyLimit(String monthlyLimit) {
		this.monthlyLimit = monthlyLimit;
	}

	public String getTransactionLimit() {
		return transactionLimit;
	}

	public void setTransactionLimit(String transactionLimit) {
		this.transactionLimit = transactionLimit;
	}

	public String getMaxVPATxnLimit() {
		return MaxVPATxnLimit;
	}

	public void setMaxVPATxnLimit(String maxVPATxnLimit) {
		MaxVPATxnLimit = maxVPATxnLimit;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(String createdDate) {
		this.createdDate = createdDate;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public String getUpdateBy() {
		return updateBy;
	}

	public void setUpdateBy(String updateBy) {
		this.updateBy = updateBy;
	}

	public String getUpdatedDate() {
		return updatedDate;
	}

	public void setUpdatedDate(String updatedDate) {
		this.updatedDate = updatedDate;
	}

	@Override
	public String toString() {
		return "TPAPTransactionLimit [id=" + id + ", dailyLimit=" + dailyLimit + ", weeklyLimit=" + weeklyLimit
				+ ", monthlyLimit=" + monthlyLimit + ", transactionLimit=" + transactionLimit + ", MaxVPATxnLimit="
				+ MaxVPATxnLimit + ", status=" + status + ", createdDate=" + createdDate + ", createdBy=" + createdBy
				+ ", updateBy=" + updateBy + ", updatedDate=" + updatedDate + "]";
	}

}
