package com.pay10.commons.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class BSESMonthlyInvoiceReportEntity {
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

	@Column(nullable = false, length = 1)
	private String monthlyInvoicePreference;

	@Column(nullable = false)
	private double monthlyInvoiceRate;

	@Column(nullable = false, length = 1)
	private String ismonthlyInvoice;

	@Column(nullable = false)
	private double gst;

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

	public String getMonthlyInvoicePreference() {
		return monthlyInvoicePreference;
	}

	public void setMonthlyInvoicePreference(String monthlyInvoicePreference) {
		this.monthlyInvoicePreference = monthlyInvoicePreference;
	}

	public double getMonthlyInvoiceRate() {
		return monthlyInvoiceRate;
	}

	public void setMonthlyInvoiceRate(double monthlyInvoiceRate) {
		this.monthlyInvoiceRate = monthlyInvoiceRate;
	}

	public String getIsmonthlyInvoice() {
		return ismonthlyInvoice;
	}

	public void setIsmonthlyInvoice(String ismonthlyInvoice) {
		this.ismonthlyInvoice = ismonthlyInvoice;
	}

	public double getGst() {
		return gst;
	}

	public void setGst(double gst) {
		this.gst = gst;
	}

}
