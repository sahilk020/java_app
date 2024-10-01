package com.pay10.commons.util;

import java.util.ArrayList;
import java.util.List;


public enum StatusType {

	APPROVED				(0, "Approved",       false, "Captured"),
	DECLINED				(1, "Declined",       false, "Failed"),
	REJECTED				(2, "Rejected",       false, "Failed"),
	PENDING					(3, "Pending",        false,"Pending"),
	CAPTURED				(4, "Captured",       false,"Captured"),
	ERROR					(5, "Error",          false,"Failed"),	
	TIMEOUT					(6, "Timeout",        false,"Failed"),
	SETTLED_SETTLE			(7, "Settled",        false,"Settled"),
	SETTLED_RECONCILLED		(30, "RNS",        false,"Reconcilled But Not Settled"),
	BROWSER_CLOSED			(8, "Browser Closed", false,"Cancelled"),
	CANCELLED				(9, "Cancelled",      false,"Cancelled"),
	DENIED					(10, "Denied by risk",false,"Failed"),
	ENROLLED				(11, "Enrolled",      false,"Pending"),	
	DUPLICATE				(12, "Duplicate",     false,"Failed"),
	FAILED					(13, "Failed",        false,"Failed"),
	INVALID					(14, "Invalid",       false,"Invalid"),
	AUTHENTICATION_FAILED	(15, "Authentication Failed", false,"Failed"),
	SENT_TO_BANK           	(16, "Sent to Bank", false,"Pending"),
	DENIED_BY_FRAUD		   	(17, "Denied due to fraud", false,"Failed"),
	RECONCILED		   		(18, "Reconciled", false),
	ACQUIRER_DOWN			(19, "Acquirer down", false,"Failed"),
	PROCESSING		        (20, "Processing", false,"Pending"),
	FAILED_AT_ACQUIRER		(21, "Failed at Acquirer", false,"Failed"),
	ACQUIRER_TIMEOUT		(22, "Timed out at Acquirer", false,"Failed"),
	NODAL_CREDIT			(23, "Credit in Nodal", false),
	NODAL_PAYOUT			(24, "Payout from Nodal", false),
	PROCESSED				(25, "Processed", true,"Pending"),
	PENDING_AT_ACQUIRER		(26, "Pending at Acquirer", false,"Pending"),
	USER_INACTIVE			(27, "User Inactive",false,"Cancelled"),
	SUCCESS					(28, "Success",false,"Success"),
	ONHOLD					(29, "Onhold", false, "Onhold"),
	RNC                      (30,"Reconcilled",false,"Reconcilled But Not Settled"),
	CHARGEBACK_INITIATED    (31,"Chargeback Initiated",false,"Chargeback Initiated"),
	CHARGEBACK_REVERSAL     (32,"Chargeback Reversal",false,"Chargeback Reversal"),
	FORCE_CAPTURED          (33,"Force Captured",false,"Force Captured"),
	CANCELLED_BY_USER		(31, "Cancelled by user",      false,"Cancelled");
	private final int code;
	private final String name;
	private final boolean isInternal;
	private final String uiName;
	
	private StatusType(int code, String name,boolean isInternal, String uiName){
		this.code = code;
		this.name = name;
		this.isInternal = isInternal;
		this.uiName = uiName;
	}
	private StatusType(int code, String name,boolean isInternal){
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
		
		StatusType[] statusTypes = StatusType.values();
		StringBuilder statusString = new StringBuilder();
		for(StatusType statusType:statusTypes) {
			if(statusType.getUiName().equals(uiName)) {
				statusString.append(statusType.getName());
				statusString.append(Constants.COMMA.getValue().trim());
			}
		}
		return statusString.substring(0,statusString.length()-1);
	}

	public static List<String> getInternalStatusList(String uiName) {

		StatusType[] statusTypes = StatusType.values();
		List<String> status = new ArrayList<>();
		for(StatusType statusType:statusTypes) {
			if(statusType.getUiName().equals(uiName)) {
				status.add(statusType.getName());
			}
		}
		return status;
	}

public static String getInternalStatusForFailedAndCancelled(String... uiName) {

		StatusType[] statusTypes = StatusType.values();
		StringBuilder statusString = new StringBuilder();

		for(StatusType statusType:statusTypes) {
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
	
	public static List<StatusType> getStatusType() {
		List<StatusType> statusTypes = new ArrayList<StatusType>();						
		for(StatusType statusType:StatusType.values()){
			if(!statusType.isInternal())
			statusTypes.add(statusType);
		}
	  return statusTypes;
	}
	

	public static StatusType getInstanceFromName(String name){
		StatusType[] statusTypes = StatusType.values();
		for(StatusType statusType : statusTypes){
			if(String.valueOf(statusType.getName()).toUpperCase().equals(name)){
				return statusType;
			}
		}		
		return null;
	}
	
	public static List<String> getFailedStatusFromInternalStatus(){
		List<String> failedStatusTypes = new ArrayList<String>();	
		StatusType[] statusTypes = StatusType.values();
		for(StatusType statusType : statusTypes){
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
