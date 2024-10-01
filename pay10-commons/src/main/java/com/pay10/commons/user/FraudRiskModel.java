package com.pay10.commons.user;

public class FraudRiskModel {

	private String id;
	private String payId;
	private String minTransactionAmount;
	private String maxTransactionAmount;
	private String fraudType;
	private String paymentType;
	private String dailyAmount;
	private String weeklyAmount;
	private String monthlyAmount;
	private String merchantProfile;
	private String blockDaily;
	private String blockWeekly;
	private String blockMonthly;

	private String displayDailyQuota;

	private String displayWeeklyQuota;

	private String displayMonthlyQuota;


	private String displayDailyVolumeQuota;

	private String displayWeeklyVolumeQuota;

	private String displayMonthlyVolumeQuota;

	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getPayId() {
		return payId;
	}
	public void setPayId(String payId) {
		this.payId = payId;
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
	public String getFraudType() {
		return fraudType;
	}
	public void setFraudType(String fraudType) {
		this.fraudType = fraudType;
	}
	public String getPaymentType() {
		return paymentType;
	}
	public void setPaymentType(String paymentType) {
		this.paymentType = paymentType;
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

	public String getDisplayDailyQuota() {
		return displayDailyQuota;
	}

	public void setDisplayDailyQuota(String displayDailyQuota) {
		this.displayDailyQuota = displayDailyQuota;
	}

	public String getDisplayWeeklyQuota() {
		return displayWeeklyQuota;
	}

	public void setDisplayWeeklyQuota(String displayWeeklyQuota) {
		this.displayWeeklyQuota = displayWeeklyQuota;
	}

	public String getDisplayMonthlyQuota() {
		return displayMonthlyQuota;
	}

	public void setDisplayMonthlyQuota(String displayMonthlyQuota) {
		this.displayMonthlyQuota = displayMonthlyQuota;
	}

	public String getDisplayDailyVolumeQuota() {
		return displayDailyVolumeQuota;
	}

	public void setDisplayDailyVolumeQuota(String displayDailyVolumeQuota) {
		this.displayDailyVolumeQuota = displayDailyVolumeQuota;
	}

	public String getDisplayWeeklyVolumeQuota() {
		return displayWeeklyVolumeQuota;
	}

	public void setDisplayWeeklyVolumeQuota(String displayWeeklyVolumeQuota) {
		this.displayWeeklyVolumeQuota = displayWeeklyVolumeQuota;
	}

	public String getDisplayMonthlyVolumeQuota() {
		return displayMonthlyVolumeQuota;
	}

	public void setDisplayMonthlyVolumeQuota(String displayMonthlyVolumeQuota) {
		this.displayMonthlyVolumeQuota = displayMonthlyVolumeQuota;
	}
}
