package com.pay10.commons.user;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Proxy;

@Entity
@Proxy(lazy = false)
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class TdrSettingPayout implements Serializable, Cloneable, Comparable<TdrSetting> {
	private static final long serialVersionUID = 4010671786498012786L;
	

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String payId;
	private String acquirerName;
	private String channel;

	private double minTransactionAmount;

	private double maxTransactionAmount;
//	@Temporal(TemporalType.TIMESTAMP)
//	private Date fromDate;
//	@Temporal(TemporalType.TIMESTAMP)
//	private Date toDate;
	private String fromDate;

	private String bankPreference;
	private String merchantPreference;

	private double bankTdr;

	private double bankMinTdrAmt;

	private double bankMaxTdrAmt;

	private double merchantTdr;

	private double merchantMinTdrAmt;

	private double merchantMaxTdrAmt;
	private boolean enableSurcharge;

	private double igst; 
	private String status;
	private String tdrStatus;
	private String paymentRegion;
	private String type;
	private String transactionType;
	private String currency;
	private String updatedBy;
//	@Temporal(TemporalType.TIMESTAMP)
//	private Date updatedAt;
	private String updatedAt;
	@Transient
	private String fDate;

	
	
	
	public TdrSettingPayout() {
		super();
		// TODO Auto-generated constructor stub
	}

	public TdrSettingPayout(Map<String, String> reqInfo) {
		if(reqInfo.containsKey("acquirerName")) this.acquirerName=reqInfo.get("acquirerName");
		if(reqInfo.containsKey("id")) this.id=new Long(reqInfo.get("id"));
		if(reqInfo.containsKey("payId")) this.payId=reqInfo.get("payId");
		if(reqInfo.containsKey("channel")) this.channel=reqInfo.get("channel");
		if(reqInfo.containsKey("minTransactionAmount")) this.minTransactionAmount=new Double(reqInfo.get("minTransactionAmount"));
		if(reqInfo.containsKey("maxTransactionAmount")) this.maxTransactionAmount=new Double(reqInfo.get("maxTransactionAmount"));
		if(reqInfo.containsKey("fromDate")) this.fromDate=reqInfo.get("fromDate");
		if(reqInfo.containsKey("bankPreference")) this.bankPreference=reqInfo.get("bankPreference");
		if(reqInfo.containsKey("merchantPreference")) this.merchantPreference=reqInfo.get("merchantPreference");
		if(reqInfo.containsKey("bankTdr")) this.bankTdr=new Double(reqInfo.get("bankTdr"));
		if(reqInfo.containsKey("bankMinTdrAmt")) this.bankMinTdrAmt=new Double(reqInfo.get("bankMinTdrAmt"));
		if(reqInfo.containsKey("acquirerName")) this.acquirerName=reqInfo.get("acquirerName");
		if(reqInfo.containsKey("bankMaxTdrAmt")) this.bankMaxTdrAmt=new Double(reqInfo.get("bankMaxTdrAmt"));
		if(reqInfo.containsKey("merchantTdr")) this.merchantTdr=new Double(reqInfo.get("merchantTdr"));
		if(reqInfo.containsKey("merchantMinTdrAmt")) this.merchantMinTdrAmt=new Double(reqInfo.get("merchantMinTdrAmt"));
		if(reqInfo.containsKey("merchantMaxTdrAmt")) this.merchantMaxTdrAmt=new Double(reqInfo.get("merchantMaxTdrAmt"));
		if(reqInfo.containsKey("enableSurcharge")) this.enableSurcharge=new Boolean(reqInfo.get("enableSurcharge"));
		if(reqInfo.containsKey("igst")) this.igst= new Double(reqInfo.get("igst"));
		if(reqInfo.containsKey("status")) this.status=reqInfo.get("status");
		if(reqInfo.containsKey("currency")) this.currency=reqInfo.get("currency");
		if(reqInfo.containsKey("updatedBy")) this.updatedBy=reqInfo.get("updatedBy");
		if(reqInfo.containsKey("fDate")) this.fDate=reqInfo.get("fDate");

	    SimpleDateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		this.tdrStatus="ACTIVE";
		this.paymentRegion="DOMESTIC";
		this.type="CONSUMER";
		this.updatedAt= dateFormat1.format(new Date()) ;
		this.transactionType="SALE";
		
	}

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

	public String getChannel() {
		return channel;
	}

	public void setChannel(String channel) {
		this.channel = channel;
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

	public String getFromDate() {
		return fromDate;
	}

	public void setFromDate(String fromDate) {
		this.fromDate = fromDate;
	}


	public String getfDate() {
		return fDate;
	}

	public void setfDate(String fDate) {
		this.fDate = fDate;
	}

	public String getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(String updatedAt) {
		this.updatedAt = updatedAt;
	}

	@Override
	public int compareTo(TdrSetting o) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String toString() {
		return "TdrSettingPayout [id=" + id + ", payId=" + payId + ", acquirerName=" + acquirerName + ", channel="
				+ channel + ", minTransactionAmount=" + minTransactionAmount + ", maxTransactionAmount="
				+ maxTransactionAmount + ", fromDate=" + fromDate + ", bankPreference="
				+ bankPreference + ", merchantPreference=" + merchantPreference + ", bankTdr=" + bankTdr
				+ ", bankMinTdrAmt=" + bankMinTdrAmt + ", bankMaxTdrAmt=" + bankMaxTdrAmt + ", merchantTdr="
				+ merchantTdr + ", merchantMinTdrAmt=" + merchantMinTdrAmt + ", merchantMaxTdrAmt=" + merchantMaxTdrAmt
				+ ", enableSurcharge=" + enableSurcharge + ", igst=" + igst + ", status=" + status + ", tdrStatus="
				+ tdrStatus + ", paymentRegion=" + paymentRegion + ", type=" + type + ", transactionType="
				+ transactionType + ", currency=" + currency + ", updatedBy=" + updatedBy + ", updatedAt=" + updatedAt
				+ ", fDate=" + fDate + "]";
	}

}
