package com.pay10.batch.commons;

import java.io.Serializable;
import java.sql.Date;

public class TransactionSearch implements Serializable {
	private static final long serialVersionUID = -4691009307357010956L;

	
	private String pgRefNum;
	private String dateFrom;
	private Date dateTo;
	private String txnType;
	private String status;
	private String acqId;
	
	public String getAcqId() {
		return acqId;
	}
	public void setAcqId(String acqId) {
		this.acqId = acqId;
	}
	public String getPgRefNum() {
		return pgRefNum;
	}
	public void setPgRefNum(String pgRefNum) {
		this.pgRefNum = pgRefNum;
	}
	public String getDateFrom() {
		return dateFrom;
	}
	public void setDateFrom(String dateFrom) {
		this.dateFrom = dateFrom;
	}
	public Date getDateTo() {
		return dateTo;
	}
	public void setDateTo(Date dateTo) {
		this.dateTo = dateTo;
	}
	public String getTxnType() {
		return txnType;
	}
	public void setTxnType(String txnType) {
		this.txnType = txnType;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	
}
