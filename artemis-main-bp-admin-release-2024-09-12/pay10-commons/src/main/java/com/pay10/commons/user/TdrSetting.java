package com.pay10.commons.user;

import com.pay10.commons.util.TransactionType;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Proxy;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Proxy(lazy = false)
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class TdrSetting implements Serializable, Cloneable ,Comparable<TdrSetting>{
	private static final long serialVersionUID = 4010671786498012786L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	private String payId;
	private String acquirerName;
	private String paymentType;
	private String mopType;
	
	private double minTransactionAmount;
	
	private double maxTransactionAmount;
	@Temporal(TemporalType.TIMESTAMP)
	private Date fromDate;
	
	private String bankPreference;
	private String merchantPreference;
	
	private double bankTdr;
	
	private double bankMinTdrAmt;
	
	private double bankMaxTdrAmt;
	
	private double merchantTdr;
	
	private double merchantMinTdrAmt;
	
	private double merchantMaxTdrAmt;
	private boolean enableSurcharge;
	
	private double igst; //gst
	private String status;
	private String tdrStatus;
	private String paymentRegion;
	private String type;
	private String transactionType;
	private String currency;
	private String updatedBy;
	@Temporal(TemporalType.TIMESTAMP)
	private Date updatedAt;
	@Transient
	private String fDate;
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getPayId() {
		return payId;
	}

	public void setPayId(String payId) {
		this.payId = payId.toUpperCase();
	}

	public String getAcquirerName() {
		return acquirerName;
	}

	public void setAcquirerName(String acquirerName) {
		this.acquirerName = acquirerName.toUpperCase();
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

	public double getMinTransactionAmount() {
		return minTransactionAmount;
	}

	public void setMinTransactionAmount(double minTransactionAmount) {
		this.minTransactionAmount = minTransactionAmount;
	}

	public double getMaxTransactionAmount() {
		return maxTransactionAmount;
	}

	public void setMaxTransactionAmount(double maxTransactionAmount) {
		this.maxTransactionAmount = maxTransactionAmount;
	}

	

	

	public String getBankPreference() {
		return bankPreference;
	}

	public void setBankPreference(String bankPreference) {
		this.bankPreference = bankPreference;
	}



	public String getMerchantPreference() {
		return merchantPreference;
	}

	public void setMerchantPreference(String merchantPreference) {
		this.merchantPreference = merchantPreference;
	}

	public double getBankTdr() {
		return bankTdr;
	}

	public void setBankTdr(double bankTdr) {
		this.bankTdr = bankTdr;
	}

	public double getBankMinTdrAmt() {
		return bankMinTdrAmt;
	}

	public void setBankMinTdrAmt(double bankMinTdrAmt) {
		this.bankMinTdrAmt = bankMinTdrAmt;
	}

	public double getBankMaxTdrAmt() {
		return bankMaxTdrAmt;
	}

	public void setBankMaxTdrAmt(double bankMaxTdrAmt) {
		this.bankMaxTdrAmt = bankMaxTdrAmt;
	}

	public double getMerchantTdr() {
		return merchantTdr;
	}

	public void setMerchantTdr(double merchantTdr) {
		this.merchantTdr = merchantTdr;
	}

	public double getMerchantMinTdrAmt() {
		return merchantMinTdrAmt;
	}

	public void setMerchantMinTdrAmt(double merchantMinTdrAmt) {
		this.merchantMinTdrAmt = merchantMinTdrAmt;
	}

	public double getMerchantMaxTdrAmt() {
		return merchantMaxTdrAmt;
	}

	public void setMerchantMaxTdrAmt(double merchantMaxTdrAmt) {
		this.merchantMaxTdrAmt = merchantMaxTdrAmt;
	}

	public Boolean getEnableSurcharge() {
		return enableSurcharge;
	}

	public void setEnableSurcharge(Boolean enableSurcharge) {
		this.enableSurcharge = enableSurcharge;
	}

	public double getIgst() {
		return igst;
	}

	public void setIgst(double igst) {
		this.igst = igst;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status.toUpperCase();
	}

	public String getTdrStatus() {
		return tdrStatus;
	}

	public void setTdrStatus(String tdrStatus) {
		this.tdrStatus = tdrStatus.toUpperCase();
	}

	public String getPaymentRegion() {
		return paymentRegion;
	}

	public void setPaymentRegion(String paymentRegion) {
		this.paymentRegion = paymentRegion.toUpperCase();
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type.toUpperCase();
	}

	public String getTransactionType() {
		return transactionType;
	}

	public void setTransactionType(String transactionType) {
		this.transactionType = transactionType;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}
	public String getUpdatedBy() {
		return updatedBy;
	}

	public void setUpdatedBy(String updatedBy) {
		this.updatedBy = updatedBy;
	}

	public void setEnableSurcharge(boolean enableSurcharge) {
		this.enableSurcharge = enableSurcharge;
	}
	
	



	public Date getFromDate() {
		return fromDate;
	}

	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}

	public String getfDate() {
		return fDate;
	}

	public void setfDate(String fDate) {
		this.fDate = fDate;
	}

	
	
	public Date getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(Date updatedAt) {
		this.updatedAt = updatedAt;
	}

	@Override
	public int compareTo(TdrSetting o) {
		if (transactionType == null) {
			String compareString = o.getCurrency() + (getMopType().toString());
			return ((this.currency + this.mopType.toString()).compareToIgnoreCase(compareString));
		}
		StringBuilder compareString = new StringBuilder();
		compareString.append(o.getCurrency());
		compareString.append(o.getMopType());
		if (o.getTransactionType() != null) {
			compareString.append(o.getTransactionType());
		} else {
			compareString.append(TransactionType.SALE.getName());
		}

		return (this.currency + this.mopType + this.transactionType)
				.compareToIgnoreCase(compareString.toString());
	}

	@Override
	public String toString() {
		return "TdrSetting{" +
				"id=" + id +
				", payId='" + payId + '\'' +
				", acquirerName='" + acquirerName + '\'' +
				", paymentType='" + paymentType + '\'' +
				", mopType='" + mopType + '\'' +
				", minTransactionAmount=" + minTransactionAmount +
				", maxTransactionAmount=" + maxTransactionAmount +
				", fromDate=" + fromDate +
				", bankPreference='" + bankPreference + '\'' +
				", merchantPreference='" + merchantPreference + '\'' +
				", bankTdr=" + bankTdr +
				", bankMinTdrAmt=" + bankMinTdrAmt +
				", bankMaxTdrAmt=" + bankMaxTdrAmt +
				", merchantTdr=" + merchantTdr +
				", merchantMinTdrAmt=" + merchantMinTdrAmt +
				", merchantMaxTdrAmt=" + merchantMaxTdrAmt +
				", enableSurcharge=" + enableSurcharge +
				", igst=" + igst +
				", status='" + status + '\'' +
				", tdrStatus='" + tdrStatus + '\'' +
				", paymentRegion='" + paymentRegion + '\'' +
				", type='" + type + '\'' +
				", transactionType='" + transactionType + '\'' +
				", currency='" + currency + '\'' +
				", updatedBy='" + updatedBy + '\'' +
				", updatedAt=" + updatedAt +
				", fDate='" + fDate + '\'' +
				'}';
	}
}
