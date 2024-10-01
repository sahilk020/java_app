package com.pay10.commons.util;

import java.util.ArrayList;
import java.util.List;

public enum StatusTypeUIGRS {

	CAPTURED				("Captured", "Captured","Success"),
	SETTLED					("Settled", "Settled","Success"),
	FAILED					("Failed", "Failed","Failed"),	
	CANCELLED				("Cancelled", "Cancelled","Cancelled"),
	PENDING					("Pending", "Pending","Pending"),
	RNS					    ("RNS", "RNS","RNS"),
	INVALID					("Invalid", "Invalid","Invalid"),
	REFUND_INITIATED ("REFUND_INITIATED","REFUND_INITIATED","REFUND_INITIATED"),

	FORCE_CAPTURED					("Force Captured", "Force Captured","Force Captured");
	
	private final String code;
	private final String name;
	private final String alias;
//	private final boolean isInternal;
	
	private StatusTypeUIGRS(String code, String name,String alias){
		this.code = code;
		this.name = name;
		this.alias = alias;
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
	

	
	public static List<StatusTypeUIGRS> getStatusType() {
		List<StatusTypeUIGRS> statusTypes = new ArrayList<StatusTypeUIGRS>();						
		for(StatusTypeUIGRS statusType:StatusTypeUIGRS.values()){
			
			statusTypes.add(statusType);
		}
	  return statusTypes;
	}
	
	public static StatusTypeUIGRS getInstanceFromName(String name){
		StatusTypeUIGRS[] statusTypes = StatusTypeUIGRS.values();
		for(StatusTypeUIGRS statusType : statusTypes){
			if(String.valueOf(statusType.getName()).toUpperCase().equals(name)){
				return statusType;
			}
		}		
		return null;
	}
	
	
}
