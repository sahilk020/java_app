package com.pay10.commons.repository;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Proxy;

import com.pay10.commons.user.Status;
import com.pay10.commons.util.ModeType;

@Entity
@Proxy(lazy = false)
@Table(name = "disputeManagementTableFileData")
public class DMSFileData implements Serializable {

	private static final long serialVersionUID = -2316060336671262154L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	private String cbCaseId;
	private String createDate;
	private String createdBy;
	private String updatedBy;
	private String updatedDate;
	private String filePaths;
	private boolean activeFlag;
	private String docType;
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getCbCaseId() {
		return cbCaseId;
	}
	public void setCbCaseId(String cbCaseId) {
		this.cbCaseId = cbCaseId;
	}
	public String getCreateDate() {
		return createDate;
	}
	public void setCreateDate(String createDate) {
		this.createDate = createDate;
	}
	public String getCreatedBy() {
		return createdBy;
	}
	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}
	public String getUpdatedBy() {
		return updatedBy;
	}
	public void setUpdatedBy(String updatedBy) {
		this.updatedBy = updatedBy;
	}
	public String getUpdatedDate() {
		return updatedDate;
	}
	public void setUpdatedDate(String updatedDate) {
		this.updatedDate = updatedDate;
	}
	public String getFilePaths() {
		return filePaths;
	}
	public void setFilePaths(String filePaths) {
		this.filePaths = filePaths;
	}
	public boolean isActiveFlag() {
		return activeFlag;
	}
	public void setActiveFlag(boolean activeFlag) {
		this.activeFlag = activeFlag;
	}
	public String getDocType() {
		return docType;
	}
	public void setDocType(String docType) {
		this.docType = docType;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
	
	@Override
	public String toString() {
		return "DMSFileData [id=" + id + ", cbCaseId=" + cbCaseId + ", createDate=" + createDate + ", createdBy="
				+ createdBy + ", updatedBy=" + updatedBy + ", updatedDate=" + updatedDate + ", filePaths=" + filePaths
				+ ", activeFlag=" + activeFlag + ", docType=" + docType + "]";
	}
	
	public DMSFileData() {}
	public DMSFileData(long id, String cbCaseId, String createDate, String createdBy, String updatedBy,
			String updatedDate, String filePaths, boolean activeFlag, String docType) {
		super();
		this.id = id;
		this.cbCaseId = cbCaseId;
		this.createDate = createDate;
		this.createdBy = createdBy;
		this.updatedBy = updatedBy;
		this.updatedDate = updatedDate;
		this.filePaths = filePaths;
		this.activeFlag = activeFlag;
		this.docType = docType;
	}
	
	
	
	
}