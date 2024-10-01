package com.pay10.commons.user;
/** 
@author Deepak Bind */

public class PennyDto {
	

	private String name;
	private String bankName;
	private String accountNo;
	private String ifscCode;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getBankName() {
		return bankName;
	}
	public void setBankName(String bankName) {
		this.bankName = bankName;
	}
	public String getAccountNo() {
		return accountNo;
	}
	public void setAccountNo(String accountNo) {
		this.accountNo = accountNo;
	}
	public String getIfscCode() {
		return ifscCode;
	}
	public void setIfscCode(String ifscCode) {
		this.ifscCode = ifscCode;
	}
	public PennyDto() {
		super();
	
	}
	public PennyDto(String name, String bankName, String accountNo, String ifscCode) {
		super();
		this.name = name;
		this.bankName = bankName;
		this.accountNo = accountNo;
		this.ifscCode = ifscCode;
	}

	@Override
	public String toString() {
		return "PennyDto [name=" + name + ", bankName=" + bankName + ", accountNo=" + accountNo + ", ifscCode="
				+ ifscCode + "]";
	}


}
