package com.pay10.commons.util;

/**
 * 
 * @author shubhamchauhan
 *
 */
public enum AccountType {
	NODAL		("NODAL"),
	BENEFICIARY	("BENEFICIARY"),
	
	;
	
	private String name;
	
	private AccountType(String name) {
		this.setName(name);
	}
	
	public static AccountType getInstancefromName(String name) {

		for (AccountType accountType : AccountType.values()) {
			if (name.equals(accountType.getName())) {
				return accountType;
			}
		}
		return null;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
}
