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
@Table(name = "AcquirerDownConfiguration")
public class AcquirerDownTimeConfiguration {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

	@Column
	private String acquirerName;

	@Column
	private String paymentType;

	@Column
	private String mopType;

	@Column
	private String failedCount;

	@Column
	private String timeSlab;

	@Column
	private String status;

	@Column
	private String createdBy;

	@Column
	private String createdOn;

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

	public String getAcquirerName() {
		return acquirerName;
	}

	public void setAcquirerName(String acquirerName) {
		this.acquirerName = acquirerName;
	}

	public String getPaymentType() {
		return paymentType;
	}

	public void setPaymentType(String paymentType) {
		this.paymentType = paymentType;
	}

	public String getMopType() {
		return mopType;
	}

	public void setMopType(String mopType) {
		this.mopType = mopType;
	}

	public String getFailedCount() {
		return failedCount;
	}

	public void setFailedCount(String failedCount) {
		this.failedCount = failedCount;
	}

	public String getTimeSlab() {
		return timeSlab;
	}

	public void setTimeSlab(String timeSlab) {
		this.timeSlab = timeSlab;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public String getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(String createdOn) {
		this.createdOn = createdOn;
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
		return "AcquirerDownTimeConfiguration [id=" + id + ", acquirerName=" + acquirerName + ", paymentType="
				+ paymentType + ", mopType=" + mopType + ", failedCount=" + failedCount + ", timeSlab=" + timeSlab
				+ ", status=" + status + ", createdBy=" + createdBy + ", createdOn=" + createdOn + ", updateBy="
				+ updateBy + ", updatedDate=" + updatedDate + "]";
	}

}
