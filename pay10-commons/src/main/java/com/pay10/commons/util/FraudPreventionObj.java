package com.pay10.commons.util;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Transient;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Rajendra
 *
 */

public class FraudPreventionObj {

	private String id;
	private String payId;
	private String email;
	private String ipAddress;
	private String subnetMask;
	private String negativeBin;
	private String negativeCard;
	private String minutesTxnLimit;
	private String currency;
	private String minTransactionAmount;
	private String maxTransactionAmount;
	private String issuerCountry;
	private String userCountry;
	private String createDate;
	private String updateDate;
	@Enumerated(EnumType.STRING)
	private TDRStatus status;
	@Enumerated(EnumType.STRING)
	private FraudRuleType fraudType;
	private String perCardTransactionAllowed;
	private String noOfTransactionAllowed;
	@Transient
	private String ruleGroupId;
	@Transient
	private String currencyName;

	private String dateActiveFrom;
	private String dateActiveTo;
	private String startTime;

	private String endTime;
	private String repeatDays;
	private boolean alwaysOnFlag;
	private String phone;
	private String userIdentifier;
	private String createdBy;

	private String updatedBy;

	private String mackAddress;
	private int repetationCount;
	private String blockTimeUnits; // HOURLY, DAILY, WEEKLY, MONTHLY, YEARLY
	private double transactionAmount;
	private boolean fixedAmountFlag;
	private String emailToNotify;
	private String paymentType;
	private double percentageOfRepeatedMop;
	private String vpa;
	private String statusVelocity;
	private String monitoringType;
	private boolean notified;
	private String stateCode;
	private String city;
	private String parentId;
	// used for CRUD only no impact on transaction.
	private String country;
	// used for CRUD only no impact on transaction.
	private String state;
	
	
	private String dailyAmount;
	private String weeklyAmount;
	private String monthlyAmount;
	private String merchantProfile;
	private String blockDaily;
	private String blockWeekly;
	private String blockMonthly;

	private String blockDailyType;
	private String blockWeeklyType;
	private String blockMonthlyType;
	private String vpaAddress;
	
	public String getVpaAddress() {
		return vpaAddress;
	}

	public void setVpaAddress(String vpaAddress) {
		this.vpaAddress = vpaAddress;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public String getUpdatedBy() {
		return updatedBy;
	}

	public void setUpdatedBy(String updatedBy) {
		this.updatedBy = updatedBy;
	}

	public FraudPreventionObj() {

	}

	public FraudPreventionObj(String payId) {
		this.payId = payId;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getUserIdentifier() {
		return userIdentifier;
	}

	public void setUserIdentifier(String userIdentifier) {
		this.userIdentifier = userIdentifier;
	}

	public String getPayId() {
		return payId;
	}

	public void setPayId(String payId) {
		this.payId = payId;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public String getNegativeBin() {
		return negativeBin;
	}

	public void setNegativeBin(String negativeBin) {
		this.negativeBin = negativeBin;
	}

	public String getNegativeCard() {
		return negativeCard;
	}

	public void setNegativeCard(String negativeCard) {
		this.negativeCard = negativeCard;
	}

	public String getMinTransactionAmount() {
		return minTransactionAmount;
	}

	public void setMinTransactionAmount(String minTransactionAmount) {
		this.minTransactionAmount = minTransactionAmount;
	}

	public String getMaxTransactionAmount() {
		return maxTransactionAmount;
	}

	public void setMaxTransactionAmount(String maxTransactionAmount) {
		this.maxTransactionAmount = maxTransactionAmount;
	}

	public String getSubnetMask() {
		return subnetMask;
	}

	public void setSubnetMask(String subnetMask) {
		this.subnetMask = subnetMask;
	}

	public String getUserCountry() {
		return userCountry;
	}

	public void setUserCountry(String userCountry) {
		this.userCountry = userCountry;
	}

	public String getIssuerCountry() {
		return issuerCountry;
	}

	public FraudRuleType getFraudType() {
		return fraudType;
	}

	public void setFraudType(FraudRuleType fraudType) {
		this.fraudType = fraudType;
	}

	public String getCreateDate() {
		return createDate;
	}

	public void setCreateDate(String createDate) {
		this.createDate = createDate;
	}

	public String getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(String updateDate) {
		this.updateDate = updateDate;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getPerCardTransactionAllowed() {
		return perCardTransactionAllowed;
	}

	public void setPerCardTransactionAllowed(String perCardTransactionAllowed) {
		this.perCardTransactionAllowed = perCardTransactionAllowed;
	}

	public void setIssuerCountry(String issuerCountry) {
		this.issuerCountry = issuerCountry;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	// returning ruleGroupId, from FraudRuleType Enum
	public String getRuleGroupId() {
		return fraudType.getCode();
	}

	// to return alpha code of currency for front-end
	public String getCurrencyName() {
		return Currency.getAlphabaticCode(this.currency);
	}

	public TDRStatus getStatus() {
		return status;
	}

	public void setStatus(TDRStatus status) {
		this.status = status;
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

	public String getRepeatDays() {
		return repeatDays;
	}

	public void setRepeatDays(String repeatDays) {
		this.repeatDays = repeatDays;
	}

	public String getDateActiveFrom() {
		return dateActiveFrom;
	}

	public void setDateActiveFrom(String dateActiveFrom) {
		this.dateActiveFrom = dateActiveFrom;
	}

	public String getDateActiveTo() {
		return dateActiveTo;
	}

	public void setDateActiveTo(String dateActiveTo) {
		this.dateActiveTo = dateActiveTo;
	}

	public boolean isAlwaysOnFlag() {
		return alwaysOnFlag;
	}

	public String getMinutesTxnLimit() {
		return minutesTxnLimit;
	}

	public void setMinutesTxnLimit(String minutesTxnLimit) {
		this.minutesTxnLimit = minutesTxnLimit;
	}

	public void setAlwaysOnFlag(boolean alwaysOnFlag) {
		this.alwaysOnFlag = alwaysOnFlag;
	}

	public String getNoOfTransactionAllowed() {
		return noOfTransactionAllowed;
	}

	public void setNoOfTransactionAllowed(String noOfTransactionAllowed) {
		this.noOfTransactionAllowed = noOfTransactionAllowed;
	}

	public String getMackAddress() {
		return mackAddress;
	}

	public void setMackAddress(String mackAddress) {
		this.mackAddress = mackAddress;
	}

	public int getRepetationCount() {
		return repetationCount;
	}

	public void setRepetationCount(int repetationCount) {
		this.repetationCount = repetationCount;
	}

	public String getBlockTimeUnits() {
		return blockTimeUnits;
	}

	public void setBlockTimeUnits(String blockTimeUnits) {
		this.blockTimeUnits = blockTimeUnits;
	}

	public double getTransactionAmount() {
		return transactionAmount;
	}

	public void setTransactionAmount(double transactionAmount) {
		this.transactionAmount = transactionAmount;
	}

	public boolean isFixedAmountFlag() {
		return fixedAmountFlag;
	}

	public void setFixedAmountFlag(boolean fixedAmountFlag) {
		this.fixedAmountFlag = fixedAmountFlag;
	}

	public String getEmailToNotify() {
		return emailToNotify;
	}

	public void setEmailToNotify(String emailToNotify) {
		this.emailToNotify = emailToNotify;
	}

	public String getPaymentType() {
		return paymentType;
	}

	public void setPaymentType(String paymentType) {
		this.paymentType = paymentType;
	}

	public double getPercentageOfRepeatedMop() {
		return percentageOfRepeatedMop;
	}

	public void setPercentageOfRepeatedMop(double percentageOfRepeatedMop) {
		this.percentageOfRepeatedMop = percentageOfRepeatedMop;
	}

	public String getVpa() {
		return vpa;
	}

	public void setVpa(String vpa) {
		this.vpa = vpa;
	}

	public String getStatusVelocity() {
		return statusVelocity;
	}

	public void setStatusVelocity(String statusVelocity) {
		this.statusVelocity = statusVelocity;
	}

	public String getMonitoringType() {
		return monitoringType;
	}

	public void setMonitoringType(String monitoringType) {
		this.monitoringType = monitoringType;
	}

	public boolean isNotified() {
		return notified;
	}

	public void setNotified(boolean notified) {
		this.notified = notified;
	}

	public String getStateCode() {
		return stateCode;
	}

	public void setStateCode(String stateCode) {
		this.stateCode = stateCode;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getParentId() {
		return parentId;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getDailyAmount() {
		return dailyAmount;
	}

	public void setDailyAmount(String dailyAmount) {
		this.dailyAmount = dailyAmount;
	}

	public String getWeeklyAmount() {
		return weeklyAmount;
	}

	public void setWeeklyAmount(String weeklyAmount) {
		this.weeklyAmount = weeklyAmount;
	}

	public String getMonthlyAmount() {
		return monthlyAmount;
	}

	public void setMonthlyAmount(String monthlyAmount) {
		this.monthlyAmount = monthlyAmount;
	}

	public String getMerchantProfile() {
		return merchantProfile;
	}

	public void setMerchantProfile(String merchantProfile) {
		this.merchantProfile = merchantProfile;
	}

	public String getBlockDaily() {
		return blockDaily;
	}

	public void setBlockDaily(String blockDaily) {
		this.blockDaily = blockDaily;
	}

	public String getBlockWeekly() {
		return blockWeekly;
	}

	public void setBlockWeekly(String blockWeekly) {
		this.blockWeekly = blockWeekly;
	}

	public String getBlockMonthly() {
		return blockMonthly;
	}

	public void setBlockMonthly(String blockMonthly) {
		this.blockMonthly = blockMonthly;
	}

	public String getBlockDailyType() {
		return blockDailyType;
	}

	public void setBlockDailyType(String blockDailyType) {
		this.blockDailyType = blockDailyType;
	}

	public String getBlockWeeklyType() {
		return blockWeeklyType;
	}

	public void setBlockWeeklyType(String blockWeeklyType) {
		this.blockWeeklyType = blockWeeklyType;
	}

	public String getBlockMonthlyType() {
		return blockMonthlyType;
	}

	public void setBlockMonthlyType(String blockMonthlyType) {
		this.blockMonthlyType = blockMonthlyType;
	}

	public void setRuleGroupId(String ruleGroupId) {
		this.ruleGroupId = ruleGroupId;
	}

	public void setCurrencyName(String currencyName) {
		this.currencyName = currencyName;
	}

}
