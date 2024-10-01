package com.pay10.commons.user;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Proxy;

import com.pay10.commons.util.TDRStatus;

@Entity
@Proxy(lazy= false)
@Table
public class SurchargeDetails implements Serializable{

	private static final long serialVersionUID = 6451344616066902378L;
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	private String paymentType;
	private String payId;
	private BigDecimal surchargeAmount;
	private BigDecimal surchargePercentage;
	private BigDecimal minTransactionAmount;
	
	@Transient
	private BigDecimal serviceTax;
	
	@Enumerated(EnumType.STRING)
	private TDRStatus status;
	
	@Enumerated(EnumType.STRING)
	private AccountCurrencyRegion paymentsRegion;
	
	
	private Date createdDate;
	private Date updatedDate;
	
	private String requestedBy;
	private String processedBy;
	private String otp;
	private Date otpExpiryTime;
	
	@Transient
	private String code;
	
	private transient String merchantName;
	private Boolean isHigher;

	public Boolean getIsHigher() {
		return isHigher;
	}
	public void setIsHigher(Boolean isHigher) {
		this.isHigher = isHigher;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
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
	public BigDecimal getServiceTax() {
		return serviceTax;
	}
	public void setServiceTax(BigDecimal serviceTax) {
		this.serviceTax = serviceTax;
	}
	public BigDecimal getSurchargeAmount() {
		return surchargeAmount;
	}
	public void setSurchargeAmount(BigDecimal surchargeAmount) {
		this.surchargeAmount = surchargeAmount;
	}
	public String getPaymentType() {
		return paymentType;
	}
	public void setPaymentType(String paymentType) {
		this.paymentType = paymentType;
	}
	public BigDecimal getSurchargePercentage() {
		return surchargePercentage;
	}
	public void setSurchargePercentage(BigDecimal surchargePercentage) {
		this.surchargePercentage = surchargePercentage;
	}
	public String getPayId() {
		return payId;
	}
	public void setPayId(String payId) {
		this.payId = payId;
	}
	public TDRStatus getStatus() {
		return status;
	}
	public void setStatus(TDRStatus status) {
		this.status = status;
	}
	public String getMerchantName() {
		return merchantName;
	}
	public void setMerchantName(String merchantName) {
		this.merchantName = merchantName;
	}
	public String getRequestedBy() {
		return requestedBy;
	}
	public void setRequestedBy(String requestedBy) {
		this.requestedBy = requestedBy;
	}
	public String getProcessedBy() {
		return processedBy;
	}
	public void setProcessedBy(String processedBy) {
		this.processedBy = processedBy;
	}
	public BigDecimal getMinTransactionAmount() {
		return minTransactionAmount;
	}
	public void setMinTransactionAmount(BigDecimal minTransactionAmount) {
		this.minTransactionAmount = minTransactionAmount;
	}
	public AccountCurrencyRegion getPaymentsRegion() {
		return paymentsRegion;
	}
	public void setPaymentsRegion(AccountCurrencyRegion paymentsRegion) {
		this.paymentsRegion = paymentsRegion;
	}
	public String getOtp() {
		return otp;
	}
	public void setOtp(String otp) {
		this.otp = otp;
	}
	public Date getOtpExpiryTime() {
		return otpExpiryTime;
	}
	public void setOtpExpiryTime(Date otpExpiryTime) {
		this.otpExpiryTime = otpExpiryTime;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}

}
