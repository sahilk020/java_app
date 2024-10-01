package com.pay10.commons.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name = "pgWebHookPostConfigURL")
public class PGWebHookPostConfigURL {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	@Column
	private String payId;
	@Column
	private String webhookUrl;
	@Column
	private String webhookType;
	@Column
	private String status;
	@Column(name = "createdOn")
	@Temporal(TemporalType.TIMESTAMP)
	private Date createdOn;
	@Column(name = "updatedOn")
	@Temporal(TemporalType.TIMESTAMP)
	private Date updatedOn;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getPayId() {
		return payId;
	}

	public void setPayId(String payId) {
		this.payId = payId;
	}

	public String getWebhookUrl() {
		return webhookUrl;
	}

	public void setWebhookUrl(String webhookUrl) {
		this.webhookUrl = webhookUrl;
	}

	public String getWebhookType() {
		return webhookType;
	}

	public void setWebhookType(String webhookType) {
		this.webhookType = webhookType;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Date getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}

	public Date getUpdatedOn() {
		return updatedOn;
	}

	public void setUpdatedOn(Date updatedOn) {
		this.updatedOn = updatedOn;
	}

	public String toString() {
		return "PGWebHookPostConfigURL [id=" + id + ", payId=" + payId + ", webhookUrl=" + webhookUrl
				+ ", webhookType=" + webhookType + ", status=" + status + ", createdOn=" + createdOn
				+ ", updatedOn=" + updatedOn + "]";
	}
}
