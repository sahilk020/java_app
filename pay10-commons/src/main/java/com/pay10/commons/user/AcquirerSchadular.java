package com.pay10.commons.user;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "acq_sched_config")
public class AcquirerSchadular implements Serializable {

	private static final long serialVersionUID = 6373356150889155995L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	@Column(name = "acquirer_name")
	private String acquirer;
	@Column(name = "instrument")
	private String paymentType;
	@Column(name = "start_time")
	private String startTime;
	@Column(name = "max_time")
	private String endTime;
	@Column(name = "status")
	private String status;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getAcquirer() {
		return acquirer;
	}

	public void setAcquirer(String acquirer) {
		this.acquirer = acquirer;
	}

	public String getPaymentType() {
		return paymentType;
	}

	public void setPaymentType(String paymentType) {
		this.paymentType = paymentType;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	@Override
	public String toString() {
		return "AcquirerSchadular [id=" + id + ", acquirer=" + acquirer + ", paymentType=" + paymentType
				+ ", startTime=" + startTime + ", endTime=" + endTime + ", status=" + status + "]";
	}

}
