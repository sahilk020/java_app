package com.crmws.dto;

public class TPAPTxnLimitDTO {

	private String id;
	private String dailyLimit;
	private String weeklyLimit;
	private String monthlyLimit;
	private String transactionLimit;
	private String MaxVPATxnLimit;
	private String date;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDailyLimit() {
		return dailyLimit;
	}

	public void setDailyLimit(String dailyLimit) {
		this.dailyLimit = dailyLimit;
	}

	public String getWeeklyLimit() {
		return weeklyLimit;
	}

	public void setWeeklyLimit(String weeklyLimit) {
		this.weeklyLimit = weeklyLimit;
	}

	public String getMonthlyLimit() {
		return monthlyLimit;
	}

	public void setMonthlyLimit(String monthlyLimit) {
		this.monthlyLimit = monthlyLimit;
	}

	public String getTransactionLimit() {
		return transactionLimit;
	}

	public void setTransactionLimit(String transactionLimit) {
		this.transactionLimit = transactionLimit;
	}

	public String getMaxVPATxnLimit() {
		return MaxVPATxnLimit;
	}

	public void setMaxVPATxnLimit(String maxVPATxnLimit) {
		MaxVPATxnLimit = maxVPATxnLimit;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	@Override
	public String toString() {
		return "TPAPTxnLimitDTO [id=" + id + ", dailyLimit=" + dailyLimit + ", weeklyLimit=" + weeklyLimit
				+ ", monthlyLimit=" + monthlyLimit + ", transactionLimit=" + transactionLimit + ", MaxVPATxnLimit="
				+ MaxVPATxnLimit + ", date=" + date + "]";
	}

}
