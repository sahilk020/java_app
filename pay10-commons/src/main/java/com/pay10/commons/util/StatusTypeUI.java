package com.pay10.commons.util;

import java.util.ArrayList;
import java.util.List;

public enum StatusTypeUI {

	//	CANCELLED				("Cancelled", "Cancelled","Cancelled", "Cancelled"),
	CAPTURED				("Captured", "Captured","Success", "Captured"),
	CHARGEBACK_INITIATED    ("Chargeback Initiated","Chargeback Initiated","Chargeback Initiated", "Chargeback Initiated"),
	CHARGEBACK_REVERSAL     ("Chargeback Reversal","Chargeback Reversal","Chargeback Reversal", "Chargeback Reversal"),
	FAILED					("Failed", "Failed","Failed", "Failed"),
	//	FORCE_CAPTURED					("Force Captured", "Force Captured","Force Captured", "Force Captured"),
//	INVALID					("Invalid", "Invalid","Invalid", "Invalid"),
//	PENDING					("Pending", "Pending","Pending", "Pending"),
//	REFUND_INITIATED ("REFUND_INITIATED","REFUND_INITIATED","REFUND_INITIATED", "Refund Initiated"),
	REQUEST_ACCEPTED ("Request Accepted", "Request Accepted", "Request Accepted", "Request Accepted"),
	RNS					    ("RNS", "RNS","RNS", "RNS"),
	SETTLED					("Settled", "Settled","Success", "Settled");

	private final String code;
	private final String name;
	private final String alias;
	private final String uiName;
//	private final boolean isInternal;
	
	private StatusTypeUI(String code, String name,String alias, String uiName){
		this.code = code;
		this.name = name;
		this.alias = alias;
		this.uiName = uiName;
	}

	
	
	public String getCode() {
		return code;
	}

	public String getName() {
		return name;
	}
	
	public String getAlias() {
		return alias;
	}

	public String getUiName() {
		return uiName;
	}

	public static List<StatusTypeUI> getStatusType() {
		List<StatusTypeUI> statusTypes = new ArrayList<StatusTypeUI>();						
		for(StatusTypeUI statusType:StatusTypeUI.values()){
			
			statusTypes.add(statusType);
		}
	  return statusTypes;
	}
	
	public static StatusTypeUI getInstanceFromName(String name){
		StatusTypeUI[] statusTypes = StatusTypeUI.values();
		for(StatusTypeUI statusType : statusTypes){
			if(String.valueOf(statusType.getName()).toUpperCase().equals(name)){
				return statusType;
			}
		}		
		return null;
	}
	
	
}
