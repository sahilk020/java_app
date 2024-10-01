package com.pay10.commons.util;

import java.util.ArrayList;
import java.util.List;

public enum StatusTypeCustom {

	CAPTURED           ("Captured", "Captured", "Success"), 
	SETTLED			   ("Settled", "Settled", "Success"), 
	RNS                ("RNS", "RNS", "RNS"),
	RISKHOLD      ("RISK HOLD", "1", "1"), 
	RISKRELEASE   ("RISK RELEASE", "0", "0");
//	FORCE_CAPTURED     ("Force Captured", "Force Captured", "Force Captured");

	private final String code;
	private final String name;
	private final String alias;
//	private final boolean isInternal;

	private StatusTypeCustom(String code, String name, String alias) {
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

	public static List<StatusTypeCustom> getStatusType() {
		List<StatusTypeCustom> statusTypes = new ArrayList<StatusTypeCustom>();
		for (StatusTypeCustom statusType : StatusTypeCustom.values()) {

			statusTypes.add(statusType);
		}
		return statusTypes;
	}

	public static StatusTypeCustom getInstanceFromName(String name) {
		StatusTypeCustom[] statusTypes = StatusTypeCustom.values();
		for (StatusTypeCustom statusType : statusTypes) {
			if (String.valueOf(statusType.getName()).toUpperCase().equals(name)) {
				return statusType;
			}
		}
		return null;
	}

}
