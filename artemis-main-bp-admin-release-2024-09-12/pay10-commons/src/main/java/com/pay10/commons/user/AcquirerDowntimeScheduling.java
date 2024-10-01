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
@Table(name = "AcquirerDowntimeScheduling")
public class AcquirerDowntimeScheduling {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

	@Column
	private String acquirerName;

	@Column
	private String paymentType;

	@Column
	private String fromDate;

	@Column
	private String toDate;


	@Column
	private String status;

	@Column
	private String createdBy;

	@Column
	private String createdOn;
	
	@Column
	private String upDateBy;

	
	
	public String getUpDateBy() {
		return upDateBy;
	}

	public void setUpDateBy(String upDateBy) {
		this.upDateBy = upDateBy;
	}

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

	public String getFromDate() {
		return fromDate;
	}

	public void setFromDate(String fromDate) {
		this.fromDate = fromDate;
	}

	public String getToDate() {
		return toDate;
	}

	public void setToDate(String toDate) {
		this.toDate = toDate;
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

	@Override
	public String toString() {
		return "AcquirerDowntimeScheduling [id=" + id + ", acquirerName=" + acquirerName + ", paymentType="
				+ paymentType + ", fromDate=" + fromDate + ", toDate=" + toDate + ", status=" + status + ", createdBy="
				+ createdBy + ", createdOn=" + createdOn + "]";
	}



	
}
