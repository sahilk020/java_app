package com.pay10.commons.dto;

public class PoFRM {

	private String id;
	private String payId;
	private String merchantName;
	private String channel;
	private String currencyName;
	private String currencyCode;
	private double minTicketSize;
	private double maxTicketSize;
	private double dailyLimit;
	private double weeklyLimit;
	private double monthlyLimit;
	private double dailyVolume;
	private double weeklyVolume;
	private double monthlyVolume;

	private String displayDailyQuota;

	private String displayWeeklyQuota;

	private String displayMonthlyQuota;


	private String displayDailyVolumeQuota;

	private String displayWeeklyVolumeQuota;

	private String displayMonthlyVolumeQuota;

	private String displaySymbol;
	
	public String getPayId() {
		return payId;
	}
	public void setPayId(String payId) {
		this.payId = payId;
	}
	public String getMerchantName() {
		return merchantName;
	}
	public void setMerchantName(String merchantName) {
		this.merchantName = merchantName;
	}
	public String getChannel() {
		return channel;
	}
	public void setChannel(String channel) {
		this.channel = channel;
	}
	public String getCurrencyName() {
		return currencyName;
	}
	public void setCurrencyName(String currencyName) {
		this.currencyName = currencyName;
	}
	public String getCurrencyCode() {
		return currencyCode;
	}
	public void setCurrencyCode(String currencyCode) {
		this.currencyCode = currencyCode;
	}
	public double getMinTicketSize() {
		return minTicketSize;
	}
	public void setMinTicketSize(double minTicketSize) {
		this.minTicketSize = minTicketSize;
	}
	public double getMaxTicketSize() {
		return maxTicketSize;
	}
	public void setMaxTicketSize(double maxTicketSize) {
		this.maxTicketSize = maxTicketSize;
	}
	public double getDailyLimit() {
		return dailyLimit;
	}
	public void setDailyLimit(double dailyLimit) {
		this.dailyLimit = dailyLimit;
	}
	public double getWeeklyLimit() {
		return weeklyLimit;
	}
	public void setWeeklyLimit(double weeklyLimit) {
		this.weeklyLimit = weeklyLimit;
	}
	public double getMonthlyLimit() {
		return monthlyLimit;
	}
	public void setMonthlyLimit(double monthlyLimit) {
		this.monthlyLimit = monthlyLimit;
	}
	public double getDailyVolume() {
		return dailyVolume;
	}
	public void setDailyVolume(double dailyVolume) {
		this.dailyVolume = dailyVolume;
	}
	public double getWeeklyVolume() {
		return weeklyVolume;
	}
	public void setWeeklyVolume(double weeklyVolume) {
		this.weeklyVolume = weeklyVolume;
	}
	public double getMonthlyVolume() {
		return monthlyVolume;
	}
	public void setMonthlyVolume(double monthlyVolume) {
		this.monthlyVolume = monthlyVolume;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
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

	public String getDisplaySymbol() {
		return displaySymbol;
	}

	public void setDisplaySymbol(String displaySymbol) {
		this.displaySymbol = displaySymbol;
	}
}
