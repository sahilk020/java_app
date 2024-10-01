package com.pay10.commons.util;

import java.util.ArrayList;
import java.util.List;

public enum NodalStatusType {

//	APPROVED				(0, "Approved",       false, "Captured"),
//	DECLINED				(1, "Declined",       false, "Failed"),
	REJECTED				(2, "Rejected",       false, "Failed"),
	PENDING					(3, "Pending",        false,"Pending"),
//	CAPTURED				(4, "Captured",       false,"Captured"),
//	ERROR					(5, "Error",          false,"Failed"),	
//	TIMEOUT					(6, "Timeout",        false,"Failed"),
	SETTLED					(7, "Settled",        false,"Settled"),
//	BROWSER_CLOSED			(8, "Browser Closed", false,"Cancelled"),
//	CANCELLED				(9, "Cancelled",      false,"Cancelled"),
//	DENIED					(10, "Denied by risk",false,"Failed"),
//	ENROLLED				(11, "Enrolled",      false,"Pending"),	
	DUPLICATE				(12, "Duplicate",     false,"Failed"),
	FAILED					(13, "Failed",        false,"Failed"),
//	INVALID					(14, "Invalid",       false,"Invalid"),
//	AUTHENTICATION_FAILED	(15, "Authentication Failed", false,"Failed"),
//	SENT_TO_BANK           	(16, "Sent to Bank", false,"Pending"),
//	DENIED_BY_FRAUD		   	(17, "Denied due to fraud", false,"Failed"),
//	RECONCILED		   		(18, "Reconciled", false),
//	ACQUIRER_DOWN			(19, "Acquirer down", false,"Failed"),
	PROCESSING		        (20, "Processing", false,"Pending"),
//	FAILED_AT_ACQUIRER		(21, "Failed at Acquirer", false,"Failed"),
//	ACQUIRER_TIMEOUT		(22, "Timed out at Acquirer", false,"Failed"),
//	NODAL_CREDIT			(23, "Credit in Nodal", false),
//	NODAL_PAYOUT			(24, "Payout from Nodal", false),
//	PROCESSED				(25, "Processed", true,"Pending"),
//	PENDING_AT_ACQUIRER		(26, "Pending at Acquirer", false,"Pending"),
//	USER_INACTIVE			(27, "User Inactive",false,"Cancelled"),
//	SUCCESS					(28, "Success",false,"Success"),
	SETTLEMENT_REVERSED		(29, "Settlement Revesed", false, "Failed"),
	
	;

	private final int code;
	private final String name;
	private final boolean isInternal;
	private final String uiName;

	private NodalStatusType(int code, String name, boolean isInternal, String uiName) {
		this.code = code;
		this.name = name;
		this.isInternal = isInternal;
		this.uiName = uiName;
	}

	private NodalStatusType(int code, String name, boolean isInternal) {
		this.code = code;
		this.name = this.uiName = name;
		this.isInternal = isInternal;
	}

	public static String getInternalStatus(String uiName) {

		NodalStatusType[] nodalStatusTypes = NodalStatusType.values();
		StringBuilder statusString = new StringBuilder();
		for (NodalStatusType nodalStatusType : nodalStatusTypes) {
			if (nodalStatusType.getUiName().equals(uiName)) {
				statusString.append(nodalStatusType.getName());
				statusString.append(Constants.COMMA.getValue().trim());
			}
		}
		return statusString.substring(0, statusString.length() - 1);
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

	public static List<NodalStatusType> getNodalStatusType() {
		List<NodalStatusType> nodalStatusTypes = new ArrayList<NodalStatusType>();
		for (NodalStatusType nodalStatusType : NodalStatusType.values()) {
			if (!nodalStatusType.isInternal())
				nodalStatusTypes.add(nodalStatusType);
		}
		return nodalStatusTypes;
	}

	public static NodalStatusType getInstanceFromName(String name) {
		NodalStatusType[] nodalStatusTypes = NodalStatusType.values();
		for (NodalStatusType nodalStatusType : nodalStatusTypes) {
			if (nodalStatusType.getName().equalsIgnoreCase(name)) {
				return nodalStatusType;
			}
		}
		return null;
	}

	public static List<String> getFailedStatusFromInternalStatus() {
		List<String> failedNodalStatusTypes = new ArrayList<String>();
		NodalStatusType[] nodalStatusTypes = NodalStatusType.values();
		for (NodalStatusType nodalStatusType : nodalStatusTypes) {
			if (String.valueOf(nodalStatusType.getUiName()).equalsIgnoreCase("Failed")) {
				failedNodalStatusTypes.add(nodalStatusType.getName());
			}
		}
		return failedNodalStatusTypes;
	}

	public String getUiName() {
		return uiName;
	}

}
