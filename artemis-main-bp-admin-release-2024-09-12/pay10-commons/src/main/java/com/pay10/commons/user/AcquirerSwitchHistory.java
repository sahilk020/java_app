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
@Table(name = "AcquirerSwitchHistory")
public class AcquirerSwitchHistory {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

	@Column
	private String acquirerName;

	@Column
	private String paymentType;

	

	@Column
	private String status;



	@Column
	private String UpdateOn;
	
	@Column
	private String upDateBy;

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

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getUpdateOn() {
		return UpdateOn;
	}

	public void setUpdateOn(String updateOn) {
		UpdateOn = updateOn;
	}

	public String getUpDateBy() {
		return upDateBy;
	}

	public void setUpDateBy(String upDateBy) {
		this.upDateBy = upDateBy;
	}

	@Override
	public String toString() {
		return "AcquirerSwitchHistory [id=" + id + ", acquirerName=" + acquirerName + ", paymentType=" + paymentType
				+ ", status=" + status + ", UpdateOn=" + UpdateOn + ", upDateBy=" + upDateBy + "]";
	}

	
	
	
}
