package com.pay10.commons.payoutEntity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class PayoutAcquirer {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	private long acquirerId;
	private String acquirerName;
	private String channel;
	private String currencyCode;
	private String allowedMop;
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public long getAcquirerId() {
		return acquirerId;
	}
	public void setAcquirerId(long acquirerId) {
		this.acquirerId = acquirerId;
	}
	public String getAcquirerName() {
		return acquirerName;
	}
	public void setAcquirerName(String acquirerName) {
		this.acquirerName = acquirerName;
	}
	public String getChannel() {
		return channel;
	}
	public void setChannel(String channel) {
		this.channel = channel;
	}
	public String getCurrencyCode() {
		return currencyCode;
	}
	public void setCurrencyCode(String currencyCode) {
		this.currencyCode = currencyCode;
	}
	public String getAllowedMop() {
		return allowedMop;
	}
	public void setAllowedMap(String allowedMop) {
		this.allowedMop = allowedMop;
	}
	
	
	
	
}
