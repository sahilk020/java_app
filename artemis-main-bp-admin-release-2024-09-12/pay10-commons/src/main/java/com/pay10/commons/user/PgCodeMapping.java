package com.pay10.commons.user;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity
@Table(name = "pg_code_mapping")
@Cacheable  
@Cache(usage=CacheConcurrencyStrategy.READ_ONLY) 
public class PgCodeMapping {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	@Column(name = "pg_code")
	private String pg_code;
	@Column(name = "pg_msg")
	private String pg_msg;
	@Column(name = "pg_status")
	private String pg_status;
	public String getPg_code() {
		return pg_code;
	}
	public void setPg_code(String pg_code) {
		this.pg_code = pg_code;
	}
	public String getPg_msg() {
		return pg_msg;
	}
	public void setPg_msg(String pg_msg) {
		this.pg_msg = pg_msg;
	}
	public String getPg_status() {
		return pg_status;
	}
	public void setPg_status(String pg_status) {
		this.pg_status = pg_status;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}

}
