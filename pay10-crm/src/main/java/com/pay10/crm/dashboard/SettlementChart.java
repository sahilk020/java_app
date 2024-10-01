package com.pay10.crm.dashboard;

import java.io.Serializable;

public class SettlementChart implements Serializable {

/**
 * Rohit
 */
	private static final long serialVersionUID = -4691579307357279956L;
	private String createDate;
	private Double settledAmount;
	
	public String getCreateDate() {
		return createDate;
	}
	public void setCreateDate(String createDate) {
		this.createDate = createDate;
	}
	public Double getSettledAmount() {
		return settledAmount;
	}
	public void setSettledAmount(Double settledAmount) {
		this.settledAmount = settledAmount;
	}



}
