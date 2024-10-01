package com.pay10.commons.audittrail;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Proxy;

/**
 * @author Jay Gajera
 *
 */
@Entity
@Proxy(lazy = false)
@Table(name = "audit_trail")
public class AuditTrail implements Serializable {

	private static final long serialVersionUID = -8234159232619595827L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

	@Column
	private String emailId;

	@Column
	private String firstName;

	@Column
	private String payload;

	@Column
	private String previousValue;

	@Column
	private String diffValue;

	@Column
	private String browser;

	@Column
	private String ip;

	@Column
	private String os;

	@Column
	private String timestamp;

	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "actionMessageId")
	private ActionMessageByAction actionMessageByAction;

	public AuditTrail() {
	}

	public AuditTrail(String payload, String previousValue, ActionMessageByAction actionMessageByAction) {
		super();
		this.payload = payload;
		this.actionMessageByAction = actionMessageByAction;
		this.previousValue = previousValue;
	}

	public AuditTrail(String emailId, String firstName, String payload, String previousValue, String diffValue,
			String browser, String ip, String os, ActionMessageByAction actionMessageByAction) {
		super();
		this.emailId = emailId;
		this.firstName = firstName;
		this.payload = payload;
		this.previousValue = previousValue;
		this.diffValue = diffValue;
		this.browser = browser;
		this.ip = ip;
		this.os = os;
		this.actionMessageByAction = actionMessageByAction;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getEmailId() {
		return emailId;
	}

	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getPayload() {
		return payload;
	}

	public void setPayload(String payload) {
		this.payload = payload;
	}

	public String getPreviousValue() {
		return previousValue;
	}

	public void setPreviousValue(String previousValue) {
		this.previousValue = previousValue;
	}

	public String getDiffValue() {
		return diffValue;
	}

	public void setDiffValue(String diffValue) {
		this.diffValue = diffValue;
	}

	public String getBrowser() {
		return browser;
	}

	public void setBrowser(String browser) {
		this.browser = browser;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getOs() {
		return os;
	}

	public void setOs(String os) {
		this.os = os;
	}

	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	@Override
	public String toString() {
		return "AuditTrail [id=" + id + ", emailId=" + emailId + ", firstName=" + firstName + ", payload=" + payload
				+ ", previousValue=" + previousValue + ", diffValue=" + diffValue + ", browser=" + browser + ", ip="
				+ ip + ", os=" + os + ", timestamp=" + timestamp + ", actionMessageByAction=" + actionMessageByAction
				+ "]";
	}

	public ActionMessageByAction getActionMessageByAction() {
		return actionMessageByAction;
	}

	public void setActionMessageByAction(ActionMessageByAction actionMessageByAction) {
		this.actionMessageByAction = actionMessageByAction;
	}
}
