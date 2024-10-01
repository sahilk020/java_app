package com.pay10.notification.txnpush.pushtxnemailbuilder;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.Proxy;

/**
 * @author Rajendra
 */

@Entity
@Proxy(lazy= false)
@Table
public class NotificationDetail  implements Serializable {

	private static final long serialVersionUID = 8476685269435231830L;
	
	private String subject;
	private String message;
	private String notifierEmailId;
	private Date createDate;
	private String submittedBy;
	private String status;
	
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	
	public String getNotifierEmailId() {
		return notifierEmailId;
	}
	public void setNotifierEmailId(String notifierEmailId) {
		this.notifierEmailId = notifierEmailId;
	}
	
	public Date getCreateDate() {
		return createDate;
	}
	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}
	public String getSubmittedBy() {
		return submittedBy;
	}
	public void setSubmittedBy(String submittedBy) {
		this.submittedBy = submittedBy;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	
	
	
}
