package com.pay10.notification.sms.smsCreater;

import org.springframework.stereotype.Service;

@Service
public class SmsRequestObject {

	private String mobileNo;
	private String message;
	private String payId;
	private String duration;
	private String fromDate;
	private String toDate;
	
	public SmsRequestObject(){
		
	}

	
	
	public String getFromDate() {
		return fromDate;
	}



	public void setFromDate(String fromDate) {
		this.fromDate = fromDate;
	}



	public String getToDate() {
		return toDate;
	}



	public void setToDate(String toDate) {
		this.toDate = toDate;
	}



	public String getMobileNo() {
		return mobileNo;
	}

	public void setMobileNo(String mobileNo) {
		this.mobileNo = mobileNo;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getPayId() {
		return payId;
	}

	public void setPayId(String payId) {
		this.payId = payId;
	}

	public String getDuration() {
		return duration;
	}

	public void setDuration(String duration) {
		this.duration = duration;
	}
		
}
