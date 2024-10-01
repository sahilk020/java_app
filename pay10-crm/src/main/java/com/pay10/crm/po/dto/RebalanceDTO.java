package com.pay10.crm.po.dto;

import java.io.Serializable;

@SuppressWarnings("serial")
public class RebalanceDTO implements Serializable {
	private int sNo;
	private String acquireFrom;
	private String acquireTo;
	private String amount;
	private String date;
	private String currency;

	public String getAcquireFrom() {
		return acquireFrom;
	}

	public void setAcquireFrom(String acquireFrom) {
		this.acquireFrom = acquireFrom;
	}

	public String getAcquireTo() {
		return acquireTo;
	}

	public void setAcquireTo(String acquireTo) {
		this.acquireTo = acquireTo;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public int getsNo() {
		return sNo;
	}

	public void setsNo(int sNo) {
		this.sNo = sNo;
	}

}
