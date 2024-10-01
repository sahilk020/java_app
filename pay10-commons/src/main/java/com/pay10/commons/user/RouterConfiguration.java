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
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Proxy;

import com.pay10.commons.util.TDRStatus;

@Entity
@Proxy(lazy = false)
@Table
public class RouterConfiguration implements Serializable {

	private static final long serialVersionUID = 5792324109884787378L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	private String currency;
	private String paymentType;
	private String mopType;
	private String transactionType;

	@Enumerated(EnumType.STRING)
	private TDRStatus status;

	private Date createdDate;
	private Date updatedDate;

	private String merchant;
	private int failureCount;
	private String identifier;
	private Date startTime;
	private Date endTime;
	private Date stopTime;
	private Date failoverStartTime;
	private Date failoverEndTime;
	private int allowedFailureCount;
	private boolean onUsoffUs;
	private boolean allowAmountBasedRouting;
	private boolean currentlyActive;
	private boolean alwaysOn;
	private boolean switchOnFail;
	private int loadPercentage;
	private boolean isDown;
	private String mode;
	private String priority;
	private double minAmount;
	private double maxAmount;
	private String requestedBy;
	private String updatedBy;
	private String retryMinutes;
	private String slabId;
	private String vpaCount;

	@Enumerated(EnumType.STRING)
	private AccountCurrencyRegion paymentsRegion;

	@Enumerated(EnumType.STRING)
	private CardHolderType cardHolderType;

	@Transient
	private String payId;

	@Transient
	private String paymentTypeName;

	@Transient
	private String mopTypeName;

	@Transient
	private String statusName;

	@Transient
	private String onUsoffUsName;

	@Transient
	private String surcharge;

	@Transient
	private String rulePriority;

	@Transient
	private String loadPercent;

	@Transient
	private String merchantName;
	
	@Transient
	private String currencyName;

	
	private boolean AcquirerSwitch;
	private String acUpdateBy;
	private String acUpdateOn;

	private String otp;
	private Date otpExpiryTime;

	private String routingType;
	private String routerMinAmount;
	private String routerMixAmount;

	// Added by Deep
	@Column(name = "totalTxn", columnDefinition = "BIGINT default 5000000")
	private long totalTxn; // Default 1 cr.

	@Column(name = "failedCount", columnDefinition = "INT default 51")
	private int failedCount;// set by default 15

	public String getRoutingType() {
		return routingType;
	}

	public void setRoutingType(String routingType) {
		this.routingType = routingType;
	}

	public String getRouterMinAmount() {
		return routerMinAmount;
	}

	public void setRouterMinAmount(String routerMinAmount) {
		this.routerMinAmount = routerMinAmount;
	}

	public String getRouterMixAmount() {
		return routerMixAmount;
	}

	public void setRouterMixAmount(String routerMixAmount) {
		this.routerMixAmount = routerMixAmount;
	}

	public int getFailedCount() {
		return failedCount;
	}

	public void setFailedCount(int failedCount) {
		this.failedCount = failedCount;
	}

	
	public boolean isAcquirerSwitch() {
		return AcquirerSwitch;
	}

	public void setAcquirerSwitch(boolean acquirerSwitch) {
		AcquirerSwitch = acquirerSwitch;
	}

	public String getAcUpdateBy() {
		return acUpdateBy;
	}

	public void setAcUpdateBy(String acUpdateBy) {
		this.acUpdateBy = acUpdateBy;
	}

	public String getAcUpdateOn() {
		return acUpdateOn;
	}

	public void setAcUpdateOn(String acUpdateOn) {
		this.acUpdateOn = acUpdateOn;
	}

	public long getTotalTxn() {
		return totalTxn;
	}

	public void setTotalTxn(long totalTxn) {
		this.totalTxn = totalTxn;
	}

	public String getPayId() {
		return payId;
	}

	public void setPayId(String payId) {
		this.payId = payId;
	}

	public int getLoadPercentage() {
		return loadPercentage;
	}

	public void setLoadPercentage(int loadPercentage) {
		this.loadPercentage = loadPercentage;
	}

	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}

	public String getPriority() {
		return priority;
	}

	public void setPriority(String priority) {
		this.priority = priority;
	}

	private String acquirer;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
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

	public String getTransactionType() {
		return transactionType;
	}

	public void setTransactionType(String transactionType) {
		this.transactionType = transactionType;
	}

	public TDRStatus getStatus() {
		return status;
	}

	public void setStatus(TDRStatus status) {
		this.status = status;
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

	public String getMerchant() {
		return merchant;
	}

	public void setMerchant(String merchant) {
		this.merchant = merchant;
	}

	public String getAcquirer() {
		return acquirer;
	}

	public void setAcquirer(String acquirer) {
		this.acquirer = acquirer;
	}

	public int getFailureCount() {
		return failureCount;
	}

	public void setFailureCount(int failureCount) {
		this.failureCount = failureCount;
	}
	
	public String getCurrencyName() {
		return currencyName;
	}

	public void setCurrencyName(String currencyName) {
		this.currencyName = currencyName;
	}


	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public boolean isCurrentlyActive() {
		return currentlyActive;
	}

	public void setCurrentlyActive(boolean currentlyActive) {
		this.currentlyActive = currentlyActive;
	}

	public boolean isAlwaysOn() {
		return alwaysOn;
	}

	public void setAlwaysOn(boolean alwaysOn) {
		this.alwaysOn = alwaysOn;
	}

	public boolean isSwitchOnFail() {
		return switchOnFail;
	}

	public void setSwitchOnFail(boolean switchOnFail) {
		this.switchOnFail = switchOnFail;
	}

	public String getPaymentTypeName() {
		return paymentTypeName;
	}

	public void setPaymentTypeName(String paymentTypeName) {
		this.paymentTypeName = paymentTypeName;
	}

	public String getMopTypeName() {
		return mopTypeName;
	}

	public void setMopTypeName(String mopTypeName) {
		this.mopTypeName = mopTypeName;
	}

	public String getStatusName() {
		return statusName;
	}

	public void setStatusName(String statusName) {
		this.statusName = statusName;
	}

	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	public boolean isOnUsoffUs() {
		return onUsoffUs;
	}

	public void setOnUsoffUs(boolean onUsoffUs) {
		this.onUsoffUs = onUsoffUs;
	}

	public String getOnUsoffUsName() {
		return onUsoffUsName;
	}

	public void setOnUsoffUsName(String onUsoffUsName) {
		this.onUsoffUsName = onUsoffUsName;
	}

	public Date getFailoverStartTime() {
		return failoverStartTime;
	}

	public void setFailoverStartTime(Date failoverStartTime) {
		this.failoverStartTime = failoverStartTime;
	}

	public Date getFailoverEndTime() {
		return failoverEndTime;
	}

	public void setFailoverEndTime(Date failoverEndTime) {
		this.failoverEndTime = failoverEndTime;
	}

	public Date getStopTime() {
		return stopTime;
	}

	public void setStopTime(Date stopTime) {
		this.stopTime = stopTime;
	}

	public int getAllowedFailureCount() {
		return allowedFailureCount;
	}

	public void setAllowedFailureCount(int allowedFailureCount) {
		this.allowedFailureCount = allowedFailureCount;
	}

	public boolean isDown() {
		return isDown;
	}

	public void setDown(boolean isDown) {
		this.isDown = isDown;
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

	public boolean isAllowAmountBasedRouting() {
		return allowAmountBasedRouting;
	}

	public void setAllowAmountBasedRouting(boolean allowAmountBasedRouting) {
		this.allowAmountBasedRouting = allowAmountBasedRouting;
	}

	public String getSurcharge() {
		return surcharge;
	}

	public void setSurcharge(String surcharge) {
		this.surcharge = surcharge;
	}

	public String getRulePriority() {
		return rulePriority;
	}

	public void setRulePriority(String rulePriority) {
		this.rulePriority = rulePriority;
	}

	public String getRequestedBy() {
		return requestedBy;
	}

	public void setRequestedBy(String requestedBy) {
		this.requestedBy = requestedBy;
	}

	public String getUpdatedBy() {
		return updatedBy;
	}

	public void setUpdatedBy(String updatedBy) {
		this.updatedBy = updatedBy;
	}

	public String getRetryMinutes() {
		return retryMinutes;
	}

	public void setRetryMinutes(String retryMinutes) {
		this.retryMinutes = retryMinutes;
	}

	public AccountCurrencyRegion getPaymentsRegion() {
		return paymentsRegion;
	}

	public void setPaymentsRegion(AccountCurrencyRegion paymentsRegion) {
		this.paymentsRegion = paymentsRegion;
	}

	public CardHolderType getCardHolderType() {
		return cardHolderType;
	}

	public void setCardHolderType(CardHolderType cardHolderType) {
		this.cardHolderType = cardHolderType;
	}

	public String getSlabId() {
		return slabId;
	}

	public void setSlabId(String slabId) {
		this.slabId = slabId;
	}

	public String getOtp() {
		return otp;
	}

	public void setOtp(String otp) {
		this.otp = otp;
	}

	public String getMerchantName() {
		return merchantName;
	}

	public void setMerchantName(String merchantName) {
		this.merchantName = merchantName;
	}

	public Date getOtpExpiryTime() {
		return otpExpiryTime;
	}

	public void setOtpExpiryTime(Date otpExpiryTime) {
		this.otpExpiryTime = otpExpiryTime;
	}

	public String getLoadPercent() {
		return loadPercent;
	}

	public void setLoadPercent(String loadPercent) {
		this.loadPercent = loadPercent;
	}

	public String getVpaCount() {
		return vpaCount;
	}

	public void setVpaCount(String vpaCount) {
		this.vpaCount = vpaCount;
	}

	@Override
	public String toString() {
		return "RouterConfiguration [id=" + id + ", currency=" + currency + ", paymentType=" + paymentType
				+ ", mopType=" + mopType + ", transactionType=" + transactionType + ", status=" + status
				+ ", createdDate=" + createdDate + ", updatedDate=" + updatedDate + ", merchant=" + merchant
				+ ", failureCount=" + failureCount + ", identifier=" + identifier + ", startTime=" + startTime
				+ ", endTime=" + endTime + ", stopTime=" + stopTime + ", failoverStartTime=" + failoverStartTime
				+ ", failoverEndTime=" + failoverEndTime + ", allowedFailureCount=" + allowedFailureCount
				+ ", onUsoffUs=" + onUsoffUs + ", allowAmountBasedRouting=" + allowAmountBasedRouting
				+ ", currentlyActive=" + currentlyActive + ", alwaysOn=" + alwaysOn + ", switchOnFail=" + switchOnFail
				+ ", loadPercentage=" + loadPercentage + ", isDown=" + isDown + ", mode=" + mode + ", priority="
				+ priority + ", minAmount=" + minAmount + ", maxAmount=" + maxAmount + ", requestedBy=" + requestedBy
				+ ", updatedBy=" + updatedBy + ", retryMinutes=" + retryMinutes + ", slabId=" + slabId + ", vpaCount="
				+ vpaCount + ", paymentsRegion=" + paymentsRegion + ", cardHolderType=" + cardHolderType + ", payId="
				+ payId + ", paymentTypeName=" + paymentTypeName + ", mopTypeName=" + mopTypeName + ", statusName="
				+ statusName + ", onUsoffUsName=" + onUsoffUsName + ", surcharge=" + surcharge + ", rulePriority="
				+ rulePriority + ", loadPercent=" + loadPercent + ", merchantName=" + merchantName + ", otp=" + otp
				+ ", otpExpiryTime=" + otpExpiryTime + ", routingType=" + routingType + ", routerMinAmount="
				+ routerMinAmount + ", routerMixAmount=" + routerMixAmount + ", totalTxn=" + totalTxn + ", failedCount="
				+ failedCount + ", acquirer=" + acquirer + "]";
	}

}
