package com.pay10.commons.util;

public enum AccountStatus {
	
	ACTIVE		("Active","Active"),
	INACTIVE	("InActive","InActive"),
	CANCELLED	("Cancelled","Cancelled"),
	REJECTED	("Rejected","Rejected"),
	PENDING		("Pending","Pending"),
	ACCEPTED	("Accepted","Accepted"),
	SETTLED		("Settled", "Settled"),
	SENT_TO_BENEFICIARY ("Sent to beneficiary", "Sent to beneficiary"),
	IN_PROCESS		("IN_PROCESS", "IN_PROCESS"),
	DECLINED	("Declined", "Declined"),
	FAILED		("Failed", "failed"),
	EXPIRE 		("Expire","Expire");
	
	private final String name;
	private final String code;

	
	private AccountStatus(String name, String code){
		this.name = name;
		this.code = code;
	}

	public String getName() {
		return name;
	}
	
	public String getCode(){
		return code;
	}

	public static AccountStatus getInstancefromCode(String code) {
		AccountStatus accountStatus = null;

		for (AccountStatus account : AccountStatus.values()) {

			if (code.equals(account.getCode())) {
				accountStatus = account;
				break;
			}
		}

		return accountStatus;
	}
}
