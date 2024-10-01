package com.crmws.dto;

public class AutorefundDto {
	
	private String Payid;
	private String CreateDate;
	public String getPayid() {
		return Payid;
	}
	public void setPayid(String payid) {
		Payid = payid;
	}
	public String getCreateDate() {
		return CreateDate;
	}
	public void setCreateDate(String createDate) {
		CreateDate = createDate;
	}
	@Override
	public String toString() {
		return "AutorefundDto [Payid=" + Payid + ", CreateDate=" + CreateDate + "]";
	}
	

}
