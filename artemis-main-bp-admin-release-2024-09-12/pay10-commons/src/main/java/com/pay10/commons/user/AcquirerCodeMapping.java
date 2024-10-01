package com.pay10.commons.user;

import org.hibernate.annotations.CacheConcurrencyStrategy;  

import org.hibernate.annotations.Cache;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "acquirer_code_mapping")
@Cacheable
@Cache(usage=CacheConcurrencyStrategy.READ_ONLY)  
public class AcquirerCodeMapping {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	@Column(name = "acquirer")
	private String acquirer;
	@Column(name = "acq_status_code")
	private String acq_status_code;
	@Column(name = "status_msg")
	private String status_msg;
	@Column(name = "pg_code")
	private String pg_code;
	public String getAcquirer() {
		return acquirer;
	}
	public void setAcquirer(String acquirer) {
		this.acquirer = acquirer;
	}
	public String getAcq_status_code() {
		return acq_status_code;
	}
	public void setAcq_status_code(String acq_status_code) {
		this.acq_status_code = acq_status_code;
	}
	public String getStatus_msg() {
		return status_msg;
	}
	public void setStatus_msg(String status_msg) {
		this.status_msg = status_msg;
	}
	public String getPg_code() {
		return pg_code;
	}
	public void setPg_code(String pg_code) {
		this.pg_code = pg_code;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	
	
}
