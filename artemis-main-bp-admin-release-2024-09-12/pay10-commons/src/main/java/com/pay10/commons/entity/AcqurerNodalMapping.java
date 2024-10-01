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
import javax.ws.rs.DefaultValue;

@Entity
@Table(name = "Acquirer_Nodal_Mapping")
public class AcqurerNodalMapping {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long sno;
	private String acquirer;
	private String nodal;
	
	@Temporal(TemporalType.TIMESTAMP)
	private Date createAt;
	private String createBy;
	@Temporal(TemporalType.TIMESTAMP)
	private Date updatedAt;
	private String updatedBy;
	@Temporal(TemporalType.TIMESTAMP)
	private Date startAt;
	@Temporal(TemporalType.TIMESTAMP)
	private Date endDate;
	private String status;
	private boolean activeFlag;
	private String bankName;
	private String ifscCode;
	
	
	public String getBankName() {
		return bankName;
	}
	public void setBankName(String bankName) {
		this.bankName = bankName;
	}
	public String getIfscCode() {
		return ifscCode;
	}
	public void setIfscCode(String ifscCode) {
		this.ifscCode = ifscCode;
	}
	public boolean isActiveFlag() {
		return activeFlag;
	}
	public void setActiveFlag(boolean activeFlag) {
		this.activeFlag = activeFlag;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public Long getSno() {
		return sno;
	}
	public void setSno(Long sno) {
		this.sno = sno;
	}
	public String getAcquirer() {
		return acquirer;
	}
	public void setAcquirer(String acquirer) {
		this.acquirer = acquirer;
	}
	
	public String getNodal() {
		return nodal;
	}
	public void setNodal(String nodal) {
		this.nodal = nodal;
	}
	public Date getCreateAt() {
		return createAt;
	}
	public void setCreateAt(Date createAt) {
		this.createAt = createAt;
	}
	public String getCreateBy() {
		return createBy;
	}
	public void setCreateBy(String createBy) {
		this.createBy = createBy;
	}
	public Date getUpdatedAt() {
		return updatedAt;
	}
	public void setUpdatedAt(Date updatedAt) {
		this.updatedAt = updatedAt;
	}
	public String getUpdatedBy() {
		return updatedBy;
	}
	public void setUpdatedBy(String updatedBy) {
		this.updatedBy = updatedBy;
	}
	public Date getStartAt() {
		return startAt;
	}
	public void setStartAt(Date startAt) {
		this.startAt = startAt;
	}
	public Date getEndDate() {
		return endDate;
	}
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	

}
