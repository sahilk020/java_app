package com.pay10.commons.user;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "acquirererrorCodePayout")
public class ErrorCodeMappingPayout {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	private String acquirer;
	private String pgCode;
	private String pgStatus;
	private String acqStatuscode;
	private String acqMessage;
	private String pgMessage;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getAcquirer() {
		return acquirer;
	}
	public void setAcquirer(String acquirer) {
		this.acquirer = acquirer;
	}
	public String getPgCode() {
		return pgCode;
	}
	public void setPgCode(String pgCode) {
		this.pgCode = pgCode;
	}
	public String getPgStatus() {
		return pgStatus;
	}
	public void setPgStatus(String pgStatus) {
		this.pgStatus = pgStatus;
	}
	public String getAcqStatuscode() {
		return acqStatuscode;
	}
	public void setAcqStatuscode(String acqStatuscode) {
		this.acqStatuscode = acqStatuscode;
	}
	public String getAcqMessage() {
		return acqMessage;
	}
	public void setAcqMessage(String acqMessage) {
		this.acqMessage = acqMessage;
	}
	public String getPgMessage() {
		return pgMessage;
	}
	public void setPgMessage(String pgMessage) {
		this.pgMessage = pgMessage;
	}

	

	
	
	
	
	
}
