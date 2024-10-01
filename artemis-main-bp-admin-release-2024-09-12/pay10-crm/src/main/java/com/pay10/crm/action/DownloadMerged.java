package com.pay10.crm.action;

import org.apache.catalina.users.AbstractUser;

import com.pay10.commons.user.User;
import com.pay10.commons.util.Constants;

public class DownloadMerged extends AbstractSecureAction{
	private static final long serialVersionUID = 1L;
	private String payid;
	private User sessionUser = null;
	
	 public String execute() {try {
 		sessionUser = (User) sessionMap.get(Constants.USER.getValue());
 	}catch (Exception e) {
			// TODO: handle exception
 		
		}
 	
     return "success";}

	public String getPayid() {
		return payid;
	}

	public void setPayid(String payid) {
		this.payid = payid;
	}

	public User getSessionUser() {
		return sessionUser;
	}

	public void setSessionUser(User sessionUser) {
		this.sessionUser = sessionUser;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
}
