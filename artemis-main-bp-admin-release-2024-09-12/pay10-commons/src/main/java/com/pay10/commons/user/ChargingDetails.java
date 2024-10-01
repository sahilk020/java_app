package com.pay10.commons.user;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Proxy;

import com.pay10.commons.util.MopType;
import com.pay10.commons.util.PaymentType;
import com.pay10.commons.util.TDRStatus;
import com.pay10.commons.util.TransactionType;

@Entity
@Proxy(lazy = false)
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class ChargingDetails implements Serializable, Comparable<ChargingDetails> {

	private static final long serialVersionUID = 3440046069273849470L;

	public ChargingDetails() {

	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Enumerated(EnumType.STRING)
	private MopType mopType;

	@Enumerated(EnumType.STRING)
	private PaymentType paymentType;

	@Enumerated(EnumType.STRING)
	private TransactionType transactionType;

	// Ceiling for fix charge
	private double fixChargeLimit;
	private boolean allowFixCharge;

	// SERVICE TAX
	private double bankServiceTax;
	private double merchantServiceTax;
	private double pgServiceTax;

	// Bank charges DOMESTIC CONSUMER
	private double bankTDR;
	private double bankFixCharge;
	private double bankFixChargeAFC;
	private double bankTDRAFC;

	// Total charges taken from merchant DOMESTIC CONSUMER
	private double merchantTDR;
	private double merchantFixCharge;
	private double merchantFixChargeAFC;
	private double merchantTDRAFC;

	// Charges by payment gateway DOMESTIC CONSUMER
	private double pgTDR;
	private double pgFixCharge;
	private double pgFixChargeAFC;
	private double pgTDRAFC;

	// Bank charges DOMESTIC COMMERCIAL
	@Column(columnDefinition = "double default 0")
	private double bankTDRDomComm;

	@Column(columnDefinition = "double default 0")
	private double bankFixChargeDomComm;

	@Column(columnDefinition = "double default 0")
	private double bankFixChargeAFCDomComm;

	@Column(columnDefinition = "double default 0")
	private double bankTDRAFCDomComm;

	// Total charges taken from merchant DOMESTIC COMMERCIAL

	@Column(columnDefinition = "double default 0")
	private double merchantTDRDomComm;

	@Column(columnDefinition = "double default 0")
	private double merchantFixChargeDomComm;

	@Column(columnDefinition = "double default 0")
	private double merchantFixChargeAFCDomComm;

	@Column(columnDefinition = "double default 0")
	private double merchantTDRAFCDomComm;

	// Charges by payment gateway DOMESTIC COMMERCIAL
	@Column(columnDefinition = "double default 0")
	private double pgTDRDomComm;

	@Column(columnDefinition = "double default 0")
	private double pgFixChargeDomComm;

	@Column(columnDefinition = "double default 0")
	private double pgFixChargeAFCDomComm;

	@Column(columnDefinition = "double default 0")
	private double pgTDRAFCDomComm;

	// Bank charges INTERNATIONAL CONSUMER
	@Column(columnDefinition = "double default 0")
	private double bankTDRIntCons;

	@Column(columnDefinition = "double default 0")
	private double bankFixChargeIntCons;

	@Column(columnDefinition = "double default 0")
	private double bankFixChargeAFCIntCons;

	@Column(columnDefinition = "double default 0")
	private double bankTDRAFCIntCons;

	// Total charges taken from merchant INTERNATIONAL CONSUMER
	@Column(columnDefinition = "double default 0")
	private double merchantTDRIntCons;

	@Column(columnDefinition = "double default 0")
	private double merchantFixChargeIntCons;

	@Column(columnDefinition = "double default 0")
	private double merchantFixChargeAFCIntCons;

	@Column(columnDefinition = "double default 0")
	private double merchantTDRAFCIntCons;

	// Charges by payment gateway INTERNATIONAL CONSUMER
	@Column(columnDefinition = "double default 0")
	private double pgTDRIntCons;

	@Column(columnDefinition = "double default 0")
	private double pgFixChargeIntCons;

	@Column(columnDefinition = "double default 0")
	private double pgFixChargeAFCIntCons;

	@Column(columnDefinition = "double default 0")
	private double pgTDRAFCIntCons;

	// Bank charges INTERNATIONAL COMMERCIAL
	@Column(columnDefinition = "double default 0")
	private double bankTDRIntComm;

	@Column(columnDefinition = "double default 0")
	private double bankFixChargeIntComm;

	@Column(columnDefinition = "double default 0")
	private double bankFixChargeAFCIntComm;

	@Column(columnDefinition = "double default 0")
	private double bankTDRAFCIntComm;

	// Total charges taken from merchant INTERNATIONAL COMMERCIAL
	@Column(columnDefinition = "double default 0")
	private double merchantTDRIntComm;

	@Column(columnDefinition = "double default 0")
	private double merchantFixChargeIntComm;

	@Column(columnDefinition = "double default 0")
	private double merchantFixChargeAFCIntComm;

	@Column(columnDefinition = "double default 0")
	private double merchantTDRAFCIntComm;

	// Charges by payment gateway INTERNATIONAL COMMERCIAL
	@Column(columnDefinition = "double default 0")
	private double pgTDRIntComm;

	@Column(columnDefinition = "double default 0")
	private double pgFixChargeIntComm;

	@Column(columnDefinition = "double default 0")
	private double pgFixChargeAFCIntComm;

	@Column(columnDefinition = "double default 0")
	private double pgTDRAFCIntComm;

	// User details
	private String acquirerName;
	private String payId;

	// relevent currency
	private String currency;

	@Transient
	private String response;

	@Transient
	private String merchantName;

	@Transient
	private String businessName;

	private Date createdDate;
	private Date updatedDate;
	private String updateBy;
	private String requestedBy;

	@Enumerated(EnumType.STRING)
	private TDRStatus status;

	// added by sonu for TDR Restriction merchant activation
	@Enumerated(EnumType.STRING)
	private TDRStatus tdrStatus;

	public TDRStatus getTdrStatus() {
		return tdrStatus;
	}

	public void setTdrStatus(TDRStatus tdrStatus) {
		this.tdrStatus = tdrStatus;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public Date getUpdatedDate() {
		return updatedDate;
	}

	public void setUpdatedDate(Date updatedDate) {
		this.updatedDate = updatedDate;
	}

	public TDRStatus getStatus() {
		return status;
	}

	public void setStatus(TDRStatus status) {
		this.status = status;
	}

	public PaymentType getPaymentType() {
		return paymentType;
	}

	public void setPaymentType(PaymentType paymentType) {
		this.paymentType = paymentType;
	}

	public String getMerchantName() {
		return merchantName;
	}

	public void setMerchantName(String merchantName) {
		this.merchantName = merchantName;
	}

	public MopType getMopType() {
		return mopType;
	}

	public void setMopType(MopType mopType) {
		this.mopType = mopType;
	}

	public TransactionType getTransactionType() {
		return transactionType;
	}

	public void setTransactionType(TransactionType transactionType) {
		this.transactionType = transactionType;
	}

	public String getPayId() {
		return payId;
	}

	public void setPayId(String payId) {
		this.payId = payId;
	}

	public double getMerchantFixCharge() {
		return merchantFixCharge;
	}

	public void setMerchantFixCharge(double merchantFixCharge) {
		this.merchantFixCharge = merchantFixCharge;
	}

	public double getBankTDR() {
		return bankTDR;
	}

	public void setBankTDR(double bankTDR) {
		this.bankTDR = bankTDR;
	}

	public double getBankFixCharge() {
		return bankFixCharge;
	}

	public void setBankFixCharge(double bankFixCharge) {
		this.bankFixCharge = bankFixCharge;
	}

	public double getBankServiceTax() {
		return bankServiceTax;
	}

	public void setBankServiceTax(double bankServiceTax) {
		this.bankServiceTax = bankServiceTax;
	}

	public double getMerchantTDR() {
		return merchantTDR;
	}

	public void setMerchantTDR(double merchantTDR) {
		this.merchantTDR = merchantTDR;
	}

	public double getMerchantServiceTax() {
		return merchantServiceTax;
	}

	public void setMerchantServiceTax(double merchantServiceTax) {
		this.merchantServiceTax = merchantServiceTax;
	}

	public double getPgTDR() {
		return pgTDR;
	}

	public void setPgTDR(double pgTDR) {
		this.pgTDR = pgTDR;
	}

	public double getPgFixCharge() {
		return pgFixCharge;
	}

	public double getPgFixChargeAFC() {
		return pgFixChargeAFC;
	}

	public void setPgFixCharge(double pgFixCharge) {
		this.pgFixCharge = pgFixCharge;
	}

	public double getPgServiceTax() {
		return pgServiceTax;
	}

	public void setPgServiceTax(double pgServiceTax) {
		this.pgServiceTax = pgServiceTax;
	}

	public String getAcquirerName() {
		return acquirerName;
	}

	public void setAcquirerName(String acquirerName) {
		this.acquirerName = acquirerName;
	}

	public double getFixChargeLimit() {
		return fixChargeLimit;
	}

	public void setFixChargeLimit(double fixChargeLimit) {
		this.fixChargeLimit = fixChargeLimit;
	}

	public boolean isAllowFixCharge() {
		return allowFixCharge;
	}

	public void setAllowFixCharge(boolean allowFixCharge) {
		this.allowFixCharge = allowFixCharge;
	}

	@Override
	public int compareTo(ChargingDetails ChargingDetails) {
		if (transactionType == null) {
			String compareString = ChargingDetails.getCurrency() + (getMopType().toString());
			return ((this.currency + this.mopType.toString()).compareToIgnoreCase(compareString));
		}
		StringBuilder compareString = new StringBuilder();
		compareString.append(ChargingDetails.getCurrency());
		compareString.append(ChargingDetails.getMopType().getName());
		if (ChargingDetails.getTransactionType() != null) {
			compareString.append(ChargingDetails.getTransactionType().getName());
		} else {
			compareString.append(TransactionType.SALE.getName());
		}

		return (this.currency + this.mopType.getName() + this.transactionType.getName())
				.compareToIgnoreCase(compareString.toString());
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public double getBankFixChargeAFC() {
		return bankFixChargeAFC;
	}

	public void setBankFixChargeAFC(double bankFixChargeAFC) {
		this.bankFixChargeAFC = bankFixChargeAFC;
	}

	public double getBankTDRAFC() {
		return bankTDRAFC;
	}

	public void setBankTDRAFC(double bankTDRAFC) {
		this.bankTDRAFC = bankTDRAFC;
	}

	public double getMerchantFixChargeAFC() {
		return merchantFixChargeAFC;
	}

	public void setMerchantFixChargeAFC(double merchantFixChargeAFC) {
		this.merchantFixChargeAFC = merchantFixChargeAFC;
	}

	public double getMerchantTDRAFC() {
		return merchantTDRAFC;
	}

	public void setMerchantTDRAFC(double merchantTDRAFC) {
		this.merchantTDRAFC = merchantTDRAFC;
	}

	public double getPgChargeAFC() {
		return pgFixChargeAFC;
	}

	public void setPgFixChargeAFC(double pgFixChargeAFC) {
		this.pgFixChargeAFC = pgFixChargeAFC;
	}

	public double getPgTDRAFC() {
		return pgTDRAFC;
	}

	public void setPgTDRAFC(double pgTDRAFC) {
		this.pgTDRAFC = pgTDRAFC;
	}

	public String getResponse() {
		return response;
	}

	public void setResponse(String response) {
		this.response = response;
	}

	public String getUpdateBy() {
		return updateBy;
	}

	public void setUpdateBy(String updateBy) {
		this.updateBy = updateBy;
	}

	public String getBusinessName() {
		return businessName;
	}

	public void setBusinessName(String businessName) {
		this.businessName = businessName;
	}

	public String getRequestedBy() {
		return requestedBy;
	}

	public void setRequestedBy(String requestedBy) {
		this.requestedBy = requestedBy;
	}

	public double getBankTDRDomComm() {
		return bankTDRDomComm;
	}

	public void setBankTDRDomComm(double bankTDRDomComm) {
		this.bankTDRDomComm = bankTDRDomComm;
	}

	public double getBankFixChargeDomComm() {
		return bankFixChargeDomComm;
	}

	public void setBankFixChargeDomComm(double bankFixChargeDomComm) {
		this.bankFixChargeDomComm = bankFixChargeDomComm;
	}

	public double getBankFixChargeAFCDomComm() {
		return bankFixChargeAFCDomComm;
	}

	public void setBankFixChargeAFCDomComm(double bankFixChargeAFCDomComm) {
		this.bankFixChargeAFCDomComm = bankFixChargeAFCDomComm;
	}

	public double getBankTDRAFCDomComm() {
		return bankTDRAFCDomComm;
	}

	public void setBankTDRAFCDomComm(double bankTDRAFCDomComm) {
		this.bankTDRAFCDomComm = bankTDRAFCDomComm;
	}

	public double getMerchantTDRDomComm() {
		return merchantTDRDomComm;
	}

	public void setMerchantTDRDomComm(double merchantTDRDomComm) {
		this.merchantTDRDomComm = merchantTDRDomComm;
	}

	public double getMerchantFixChargeDomComm() {
		return merchantFixChargeDomComm;
	}

	public void setMerchantFixChargeDomComm(double merchantFixChargeDomComm) {
		this.merchantFixChargeDomComm = merchantFixChargeDomComm;
	}

	public double getMerchantFixChargeAFCDomComm() {
		return merchantFixChargeAFCDomComm;
	}

	public void setMerchantFixChargeAFCDomComm(double merchantFixChargeAFCDomComm) {
		this.merchantFixChargeAFCDomComm = merchantFixChargeAFCDomComm;
	}

	public double getMerchantTDRAFCDomComm() {
		return merchantTDRAFCDomComm;
	}

	public void setMerchantTDRAFCDomComm(double merchantTDRAFCDomComm) {
		this.merchantTDRAFCDomComm = merchantTDRAFCDomComm;
	}

	public double getPgTDRDomComm() {
		return pgTDRDomComm;
	}

	public void setPgTDRDomComm(double pgTDRDomComm) {
		this.pgTDRDomComm = pgTDRDomComm;
	}

	public double getPgFixChargeDomComm() {
		return pgFixChargeDomComm;
	}

	public void setPgFixChargeDomComm(double pgFixChargeDomComm) {
		this.pgFixChargeDomComm = pgFixChargeDomComm;
	}

	public double getPgFixChargeAFCDomComm() {
		return pgFixChargeAFCDomComm;
	}

	public void setPgFixChargeAFCDomComm(double pgFixChargeAFCDomComm) {
		this.pgFixChargeAFCDomComm = pgFixChargeAFCDomComm;
	}

	public double getPgTDRAFCDomComm() {
		return pgTDRAFCDomComm;
	}

	public void setPgTDRAFCDomComm(double pgTDRAFCDomComm) {
		this.pgTDRAFCDomComm = pgTDRAFCDomComm;
	}

	public double getBankTDRIntCons() {
		return bankTDRIntCons;
	}

	public void setBankTDRIntCons(double bankTDRIntCons) {
		this.bankTDRIntCons = bankTDRIntCons;
	}

	public double getBankFixChargeIntCons() {
		return bankFixChargeIntCons;
	}

	public void setBankFixChargeIntCons(double bankFixChargeIntCons) {
		this.bankFixChargeIntCons = bankFixChargeIntCons;
	}

	public double getBankFixChargeAFCIntCons() {
		return bankFixChargeAFCIntCons;
	}

	public void setBankFixChargeAFCIntCons(double bankFixChargeAFCIntCons) {
		this.bankFixChargeAFCIntCons = bankFixChargeAFCIntCons;
	}

	public double getBankTDRAFCIntCons() {
		return bankTDRAFCIntCons;
	}

	public void setBankTDRAFCIntCons(double bankTDRAFCIntCons) {
		this.bankTDRAFCIntCons = bankTDRAFCIntCons;
	}

	public double getMerchantTDRIntCons() {
		return merchantTDRIntCons;
	}

	public void setMerchantTDRIntCons(double merchantTDRIntCons) {
		this.merchantTDRIntCons = merchantTDRIntCons;
	}

	public double getMerchantFixChargeIntCons() {
		return merchantFixChargeIntCons;
	}

	public void setMerchantFixChargeIntCons(double merchantFixChargeIntCons) {
		this.merchantFixChargeIntCons = merchantFixChargeIntCons;
	}

	public double getMerchantFixChargeAFCIntCons() {
		return merchantFixChargeAFCIntCons;
	}

	public void setMerchantFixChargeAFCIntCons(double merchantFixChargeAFCIntCons) {
		this.merchantFixChargeAFCIntCons = merchantFixChargeAFCIntCons;
	}

	public double getMerchantTDRAFCIntCons() {
		return merchantTDRAFCIntCons;
	}

	public void setMerchantTDRAFCIntCons(double merchantTDRAFCIntCons) {
		this.merchantTDRAFCIntCons = merchantTDRAFCIntCons;
	}

	public double getPgTDRIntCons() {
		return pgTDRIntCons;
	}

	public void setPgTDRIntCons(double pgTDRIntCons) {
		this.pgTDRIntCons = pgTDRIntCons;
	}

	public double getPgFixChargeIntCons() {
		return pgFixChargeIntCons;
	}

	public void setPgFixChargeIntCons(double pgFixChargeIntCons) {
		this.pgFixChargeIntCons = pgFixChargeIntCons;
	}

	public double getPgFixChargeAFCIntCons() {
		return pgFixChargeAFCIntCons;
	}

	public void setPgFixChargeAFCIntCons(double pgFixChargeAFCIntCons) {
		this.pgFixChargeAFCIntCons = pgFixChargeAFCIntCons;
	}

	public double getPgTDRAFCIntCons() {
		return pgTDRAFCIntCons;
	}

	public void setPgTDRAFCIntCons(double pgTDRAFCIntCons) {
		this.pgTDRAFCIntCons = pgTDRAFCIntCons;
	}

	public double getBankTDRIntComm() {
		return bankTDRIntComm;
	}

	public void setBankTDRIntComm(double bankTDRIntComm) {
		this.bankTDRIntComm = bankTDRIntComm;
	}

	public double getBankFixChargeIntComm() {
		return bankFixChargeIntComm;
	}

	public void setBankFixChargeIntComm(double bankFixChargeIntComm) {
		this.bankFixChargeIntComm = bankFixChargeIntComm;
	}

	public double getBankFixChargeAFCIntComm() {
		return bankFixChargeAFCIntComm;
	}

	public void setBankFixChargeAFCIntComm(double bankFixChargeAFCIntComm) {
		this.bankFixChargeAFCIntComm = bankFixChargeAFCIntComm;
	}

	public double getBankTDRAFCIntComm() {
		return bankTDRAFCIntComm;
	}

	public void setBankTDRAFCIntComm(double bankTDRAFCIntComm) {
		this.bankTDRAFCIntComm = bankTDRAFCIntComm;
	}

	public double getMerchantTDRIntComm() {
		return merchantTDRIntComm;
	}

	public void setMerchantTDRIntComm(double merchantTDRIntComm) {
		this.merchantTDRIntComm = merchantTDRIntComm;
	}

	public double getMerchantFixChargeIntComm() {
		return merchantFixChargeIntComm;
	}

	public void setMerchantFixChargeIntComm(double merchantFixChargeIntComm) {
		this.merchantFixChargeIntComm = merchantFixChargeIntComm;
	}

	public double getMerchantFixChargeAFCIntComm() {
		return merchantFixChargeAFCIntComm;
	}

	public void setMerchantFixChargeAFCIntComm(double merchantFixChargeAFCIntComm) {
		this.merchantFixChargeAFCIntComm = merchantFixChargeAFCIntComm;
	}

	public double getMerchantTDRAFCIntComm() {
		return merchantTDRAFCIntComm;
	}

	public void setMerchantTDRAFCIntComm(double merchantTDRAFCIntComm) {
		this.merchantTDRAFCIntComm = merchantTDRAFCIntComm;
	}

	public double getPgTDRIntComm() {
		return pgTDRIntComm;
	}

	public void setPgTDRIntComm(double pgTDRIntComm) {
		this.pgTDRIntComm = pgTDRIntComm;
	}

	public double getPgFixChargeIntComm() {
		return pgFixChargeIntComm;
	}

	public void setPgFixChargeIntComm(double pgFixChargeIntComm) {
		this.pgFixChargeIntComm = pgFixChargeIntComm;
	}

	public double getPgFixChargeAFCIntComm() {
		return pgFixChargeAFCIntComm;
	}

	public void setPgFixChargeAFCIntComm(double pgFixChargeAFCIntComm) {
		this.pgFixChargeAFCIntComm = pgFixChargeAFCIntComm;
	}

	public double getPgTDRAFCIntComm() {
		return pgTDRAFCIntComm;
	}

	public void setPgTDRAFCIntComm(double pgTDRAFCIntComm) {
		this.pgTDRAFCIntComm = pgTDRAFCIntComm;
	}

	@Override
	public String toString() {
		return "ChargingDetails [id=" + id + ", mopType=" + mopType + ", paymentType=" + paymentType
				+ ", transactionType=" + transactionType + ", fixChargeLimit=" + fixChargeLimit + ", allowFixCharge="
				+ allowFixCharge + ", bankServiceTax=" + bankServiceTax + ", merchantServiceTax=" + merchantServiceTax
				+ ", pgServiceTax=" + pgServiceTax + ", bankTDR=" + bankTDR + ", bankFixCharge=" + bankFixCharge
				+ ", bankFixChargeAFC=" + bankFixChargeAFC + ", bankTDRAFC=" + bankTDRAFC + ", merchantTDR="
				+ merchantTDR + ", merchantFixCharge=" + merchantFixCharge + ", merchantFixChargeAFC="
				+ merchantFixChargeAFC + ", merchantTDRAFC=" + merchantTDRAFC + ", pgTDR=" + pgTDR + ", pgFixCharge="
				+ pgFixCharge + ", pgFixChargeAFC=" + pgFixChargeAFC + ", pgTDRAFC=" + pgTDRAFC + ", bankTDRDomComm="
				+ bankTDRDomComm + ", bankFixChargeDomComm=" + bankFixChargeDomComm + ", bankFixChargeAFCDomComm="
				+ bankFixChargeAFCDomComm + ", bankTDRAFCDomComm=" + bankTDRAFCDomComm + ", merchantTDRDomComm="
				+ merchantTDRDomComm + ", merchantFixChargeDomComm=" + merchantFixChargeDomComm
				+ ", merchantFixChargeAFCDomComm=" + merchantFixChargeAFCDomComm + ", merchantTDRAFCDomComm="
				+ merchantTDRAFCDomComm + ", pgTDRDomComm=" + pgTDRDomComm + ", pgFixChargeDomComm="
				+ pgFixChargeDomComm + ", pgFixChargeAFCDomComm=" + pgFixChargeAFCDomComm + ", pgTDRAFCDomComm="
				+ pgTDRAFCDomComm + ", bankTDRIntCons=" + bankTDRIntCons + ", bankFixChargeIntCons="
				+ bankFixChargeIntCons + ", bankFixChargeAFCIntCons=" + bankFixChargeAFCIntCons + ", bankTDRAFCIntCons="
				+ bankTDRAFCIntCons + ", merchantTDRIntCons=" + merchantTDRIntCons + ", merchantFixChargeIntCons="
				+ merchantFixChargeIntCons + ", merchantFixChargeAFCIntCons=" + merchantFixChargeAFCIntCons
				+ ", merchantTDRAFCIntCons=" + merchantTDRAFCIntCons + ", pgTDRIntCons=" + pgTDRIntCons
				+ ", pgFixChargeIntCons=" + pgFixChargeIntCons + ", pgFixChargeAFCIntCons=" + pgFixChargeAFCIntCons
				+ ", pgTDRAFCIntCons=" + pgTDRAFCIntCons + ", bankTDRIntComm=" + bankTDRIntComm
				+ ", bankFixChargeIntComm=" + bankFixChargeIntComm + ", bankFixChargeAFCIntComm="
				+ bankFixChargeAFCIntComm + ", bankTDRAFCIntComm=" + bankTDRAFCIntComm + ", merchantTDRIntComm="
				+ merchantTDRIntComm + ", merchantFixChargeIntComm=" + merchantFixChargeIntComm
				+ ", merchantFixChargeAFCIntComm=" + merchantFixChargeAFCIntComm + ", merchantTDRAFCIntComm="
				+ merchantTDRAFCIntComm + ", pgTDRIntComm=" + pgTDRIntComm + ", pgFixChargeIntComm="
				+ pgFixChargeIntComm + ", pgFixChargeAFCIntComm=" + pgFixChargeAFCIntComm + ", pgTDRAFCIntComm="
				+ pgTDRAFCIntComm + ", acquirerName=" + acquirerName + ", payId=" + payId + ", currency=" + currency
				+ ", response=" + response + ", merchantName=" + merchantName + ", businessName=" + businessName
				+ ", createdDate=" + createdDate + ", updatedDate=" + updatedDate + ", updateBy=" + updateBy
				+ ", requestedBy=" + requestedBy + ", status=" + status + ", tdrStatus=" + tdrStatus + "]";
	}

}
