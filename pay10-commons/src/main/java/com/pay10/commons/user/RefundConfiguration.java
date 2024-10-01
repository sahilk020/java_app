package com.pay10.commons.user;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Proxy;

@Entity
@Proxy(lazy = false)
@Table(name = "RefundConfiguration")
public class RefundConfiguration implements Serializable {

	private static final long serialVersionUID = -8794117484789299407L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

	@Column
	private String acquirerName;

	@Column
	private String payId;

	@Column
	private String paymentType;

	@Column
	private String mopType;

	@Column
	private String refundMode;

	@Column
	private String createdBy;

	@Column
	private String createdDate;

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

	public String getPayId() {
		return payId;
	}

	public void setPayId(String payId) {
		this.payId = payId;
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

	public String getRefundMode() {
		return refundMode;
	}

	public void setRefundMode(String refundMode) {
		this.refundMode = refundMode;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public String getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(String createdDate) {
		this.createdDate = createdDate;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	@Override
	public String toString() {
		return "RefundConfiguration [id=" + id + ", acquirerName=" + acquirerName + ", payId=" + payId
				+ ", paymentType=" + paymentType + ", mopType=" + mopType + ", refundMode=" + refundMode + ", createdBy="
				+ createdBy + ", createdDate=" + createdDate + "]";
	}

}
