package com.pay10.commons.user;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Proxy;

@Entity
@Proxy(lazy = false)
@Table(name = "vpamaster")
public class VpaMaster {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

	@Column
	private String pspName;

	@Column
	private String domainName;

	@Column
	private String createdDate;

	@Column
	private String createdBy;


	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getPspName() {
		return pspName;
	}

	public void setPspName(String pspName) {
		this.pspName = pspName;
	}

	public String getDomainName() {
		return domainName;
	}

	public void setDomainName(String domainName) {
		this.domainName = domainName;
	}

	public String getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(String createdDate) {
		this.createdDate = createdDate;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}



	@Override
	public String toString() {
		return "VpaMaster [id=" + id + ", pspName=" + pspName + ", domainName=" + domainName + ", createdDate="
				+ createdDate + ", createdBy=" + createdBy + "]";
	}




}
