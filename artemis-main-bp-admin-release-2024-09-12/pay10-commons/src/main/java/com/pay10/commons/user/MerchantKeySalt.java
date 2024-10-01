package com.pay10.commons.user;

import java.util.Date;

import javax.persistence.*;

import org.hibernate.annotations.Proxy;

@Entity
@Proxy(lazy = false)
@Table(name = "MerchantKeySalt")
public class MerchantKeySalt {

	@Id
	@Column(nullable = false, unique = true)
//	@GeneratedValue(strategy = GenerationType.AUTO)
	private String payId;
	@Column
	private String salt;
	@Column
	private String keySalt;
	@Column
	private String encryptionKey;
	@Column
	private String createdBy;
	@Column
	private Date createdOn;
	@Column
	private String updatedBy;
	@Column
	private Date updatedOn;

	public String getPayId() {
		return payId;
	}

	public void setPayId(String payId) {
		this.payId = payId;
	}

	public String getSalt() {
		return salt;
	}

	public void setSalt(String salt) {
		this.salt = salt;
	}

	public String getEncryptionKey() {
		return encryptionKey;
	}

	public void setEncryptionKey(String encryptionKey) {
		this.encryptionKey = encryptionKey;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public Date getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}

	public String getUpdatedBy() {
		return updatedBy;
	}

	public void setUpdatedBy(String updatedBy) {
		this.updatedBy = updatedBy;
	}

	public Date getUpdatedOn() {
		return updatedOn;
	}

	public void setUpdatedOn(Date updatedOn) {
		this.updatedOn = updatedOn;
	}

	public String getKeySalt() {
		return keySalt;
	}

	public void setKeySalt(String keySalt) {
		this.keySalt = keySalt;
	}

	@Override
	public String toString() {
		return "MerchantKeySalt [payId=" + payId + ", salt=" + salt + ", keySalt=" + keySalt + ", encryptionKey="
				+ encryptionKey + ", createdBy=" + createdBy + ", createdOn=" + createdOn + ", updatedBy=" + updatedBy
				+ ", updatedOn=" + updatedOn + "]";
	}

}
