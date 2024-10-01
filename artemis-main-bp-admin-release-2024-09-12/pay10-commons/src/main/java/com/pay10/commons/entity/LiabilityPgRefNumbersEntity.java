package com.pay10.commons.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "liability_hold_and_realease")
public class LiabilityPgRefNumbersEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	private String pgRefnum;
	private String liabilityType;
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getPgRefnum() {
		return pgRefnum;
	}
	public void setPgRefnum(String pgRefnum) {
		this.pgRefnum = pgRefnum;
	}
	public String getLiabilityType() {
		return liabilityType;
	}
	public void setLiabilityType(String liabilityType) {
		this.liabilityType = liabilityType;
	}
	
	
}
