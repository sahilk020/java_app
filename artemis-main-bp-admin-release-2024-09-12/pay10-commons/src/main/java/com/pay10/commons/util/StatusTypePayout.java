package com.pay10.commons.util;

import java.util.ArrayList;
import java.util.List;


public enum StatusTypePayout {

	REQUEST_ACCEPTED				(0, "Request Accepted",false, "Request Accepted"),
//	SUCCESS				(0, "SUCCESS",false, "SUCCESS"),
//	FAIL				(0, "FAIL",false, "FAIL"),
//	PENDING				(0, "PENDING",false, "PENDING");
	SUCCESS				(1, "SUCCESS",false, "SUCCESS"),
	FAIL				(2, "Failed",false, "Failed"),
	PENDING				(3, "Pending",false, "Pending");
	
	private final int code;
	private final String name;
	private final boolean isInternal;
	private final String uiName;
	
	private StatusTypePayout(int code, String name,boolean isInternal, String uiName){
		this.code = code;
		this.name = name;
		this.isInternal = isInternal;
		this.uiName = uiName;
	}
	private StatusTypePayout(int code, String name,boolean isInternal){
		this.code = code;
		this.name = this.uiName = name;
		this.isInternal = isInternal;
	}
	

//	public static void main(String[] args) {
//		System.out.println(getInternalStatusForFailedAndCancelled("Failed","Cancelled"));
////		System.out.println(getInternalStatus());
////		System.out.println(getInternalStatus());
//
//	}
	public static String getInternalStatus(String uiName) {
		
		StatusTypePayout[] statusTypes = StatusTypePayout.values();
		StringBuilder statusString = new StringBuilder();
		for(StatusTypePayout statusType:statusTypes) {
			if(statusType.getUiName().equals(uiName)) {
				statusString.append(statusType.getName());
				statusString.append(Constants.COMMA.getValue().trim());
			}
		}
		return statusString.substring(0,statusString.length()-1);
	}

public static String getInternalStatusForFailedAndCancelled(String... uiName) {

		StatusTypePayout[] statusTypes = StatusTypePayout.values();
		StringBuilder statusString = new StringBuilder();

		for(StatusTypePayout statusType:statusTypes) {
			for(String name:uiName) {
				if(statusType.getUiName().equals(name)) {
					statusString.append(statusType.getName());
					statusString.append(Constants.COMMA.getValue().trim());
				}
			}

		}
		return statusString.substring(0,statusString.length()-1);
	}

	public int getCode() {
		return code;
	}

	public String getName() {
		return name;
	}
	
	public boolean isInternal() {
		return isInternal;
	}	
	
	public static List<StatusTypePayout> getStatusType() {
		List<StatusTypePayout> statusTypes = new ArrayList<StatusTypePayout>();						
		for(StatusTypePayout statusType:StatusTypePayout.values()){
			if(!statusType.isInternal())
			statusTypes.add(statusType);
		}
	  return statusTypes;
	}
	

	public static StatusTypePayout getInstanceFromName(String name){
		StatusTypePayout[] statusTypes = StatusTypePayout.values();
		for(StatusTypePayout statusType : statusTypes){
			if(String.valueOf(statusType.getName()).toUpperCase().equals(name)){
				return statusType;
			}
		}		
		return null;
	}
	
	public static List<String> getFailedStatusFromInternalStatus(){
		List<String> failedStatusTypes = new ArrayList<String>();	
		StatusTypePayout[] statusTypes = StatusTypePayout.values();
		for(StatusTypePayout statusType : statusTypes){
			if(String.valueOf(statusType.getUiName()).equalsIgnoreCase("Failed")){
				failedStatusTypes.add(statusType.getName());
			}
		}		
		return failedStatusTypes;
	}
	
	public String getUiName() {
		return uiName;
	}
	
}
