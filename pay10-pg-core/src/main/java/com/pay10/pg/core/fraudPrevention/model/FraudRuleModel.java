package com.pay10.pg.core.fraudPrevention.model;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Harpreet, Rahul
 *
 */
public class FraudRuleModel {

	private String id;
	private String payId;
	private String email;
	private String ipAddress;
	private String subnetMask;
	private String status;
	private String negativeBin;
	private String negativeCard;
	private String minutesTxnLimit;
	private String currency;
	private String minTransactionAmount;
	private String maxTransactionAmount;
	private String issuerCountry;
	private String userCountry;

	private String fraudType;
	private String perCardTransactionAllowed;
	private String noOfTransactionAllowed;

	private String responseCode;
	private String responseMsg;

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
	private String dateActiveFrom;
	private String dateActiveTo;
	private String startTime;
	private String endTime;
	private String stateCode;
	// used for CRUD only no impact on transaction.
	private String country;
	// used for CRUD only no impact on transaction.
	private String state;
	private String city;
	private String repeatDays;
	private boolean alwaysOnFlag;
	private String phone;
	private String userIdentifier;
	private String createdBy;
	private String updatedBy;
	//Added By Sweety;
	private String vpaAddress;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
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

	public String getSubnetMask() {
		return subnetMask;
	}

	public void setSubnetMask(String subnetMask) {
		this.subnetMask = subnetMask;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
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

	public String getIssuerCountry() {
		return issuerCountry;
	}

	public void setIssuerCountry(String issuerCountry) {
		this.issuerCountry = issuerCountry;
	}

	public String getUserCountry() {
		return userCountry;
	}

	public void setUserCountry(String userCountry) {
		this.userCountry = userCountry;
	}

	public String getFraudType() {
		return fraudType;
	}

	public void setFraudType(String fraudType) {
		this.fraudType = fraudType;
	}

	public String getPerCardTransactionAllowed() {
		return perCardTransactionAllowed;
	}

	public void setPerCardTransactionAllowed(String perCardTransactionAllowed) {
		this.perCardTransactionAllowed = perCardTransactionAllowed;
	}

	public String getResponseCode() {
		return responseCode;
	}

	public void setResponseCode(String responseCode) {
		this.responseCode = responseCode;
	}

	public String getResponseMsg() {
		return responseMsg;
	}

	public void setResponseMsg(String responseMsg) {
		this.responseMsg = responseMsg;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
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

	public void setAlwaysOnFlag(boolean alwaysOnFlag) {
		this.alwaysOnFlag = alwaysOnFlag;
	}

	public String getMinutesTxnLimit() {
		return minutesTxnLimit;
	}

	public void setMinutesTxnLimit(String minutesTxnLimit) {
		this.minutesTxnLimit = minutesTxnLimit;
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

	public String getVpaAddress() {
		return vpaAddress;
	}

	public void setVpaAddress(String vpaAddress) {
		this.vpaAddress = vpaAddress;
	}
}