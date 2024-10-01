package com.pay10.commons.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class TdrWaveOffSettingEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	@Column(nullable = false)
	private long payId;
	@Column(nullable = false)
	private String paymentType;
	@Column(nullable = false)
	private double minAmount;
	@Column(nullable = false)
	private double maxAmount;
	@Column(nullable = false)
	private double pgPercentage;
	@Column(nullable = false)
	private double bankPercentage;
	@Column(nullable = false)
	private double merchantPercentage;
	@Column(nullable = false)
	private double gst;
	@Column(nullable = false,length = 1)
	private String isExampted;
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public long getPayId() {
		return payId;
	}
	public void setPayId(long payId) {
		this.payId = payId;
	}
	public String getPaymentType() {
		return paymentType;
	}
	public void setPaymentType(String paymentType) {
		this.paymentType = paymentType;
	}
	public double getMinAmount() {
		return minAmount;
	}
	public void setMinAmount(double minAmount) {
		this.minAmount = minAmount;
	}
	public double getMaxAmount() {
		return maxAmount;
	}
	public void setMaxAmount(double maxAmount) {
		this.maxAmount = maxAmount;
	}
	public double getPgPercentage() {
		return pgPercentage;
	}
	public void setPgPercentage(double pgPercentage) {
		this.pgPercentage = pgPercentage;
	}
	public double getBankPercentage() {
		return bankPercentage;
	}
	public void setBankPercentage(double bankPercentage) {
		this.bankPercentage = bankPercentage;
	}
	public double getMerchantPercentage() {
		return merchantPercentage;
	}
	public void setMerchantPercentage(double merchantPercentage) {
		this.merchantPercentage = merchantPercentage;
	}
	public double getGst() {
		return gst;
	}
	public void setGst(double gst) {
		this.gst = gst;
	}
	public String getIsExampted() {
		return isExampted;
	}
	public void setIsExampted(String isExampted) {
		this.isExampted = isExampted;
	}
	
	
	
}
