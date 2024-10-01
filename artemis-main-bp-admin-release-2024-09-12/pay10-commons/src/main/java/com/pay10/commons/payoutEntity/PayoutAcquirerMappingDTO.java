package com.pay10.commons.payoutEntity;

import java.util.ArrayList;
import java.util.List;

public class PayoutAcquirerMappingDTO {

	private String currencyName;
	private List<String>allowedMop=new ArrayList<>();
	public String getCurrencyName() {
		return currencyName;
	}
	public void setCurrencyName(String currencyName) {
		this.currencyName = currencyName;
	}
	public List<String> getAllowedMop() {
		return allowedMop;
	}
	public void setAllowedMop(List<String> allowedMop) {
		this.allowedMop = allowedMop;
	}
	
	
}
