package com.pay10.commons.audittrail;

public class PayoutAuditTrailDTO {

	private String auditTxnId;
	private String actionName;
	private String actionBy;
	private String actionDate;
	private String updatedDate;
	private String baseJson;
	private String updatedJson;
	private String currency;

	public String getActionBy() {
		return actionBy;
	}

	public void setActionBy(String actionBy) {
		this.actionBy = actionBy;
	}

	public String getActionDate() {
		return actionDate;
	}

	public void setActionDate(String actionDate) {
		this.actionDate = actionDate;
	}

	
		public String getAuditTxnId() {
		return auditTxnId;
	}

	public void setAuditTxnId(String auditTxnId) {
		this.auditTxnId = auditTxnId;
	}

	public String getActionName() {
		return actionName;
	}

	public void setActionName(String actionName) {
		this.actionName = actionName;
	}

	public String getBaseJson() {
		return baseJson;
	}

	public void setBaseJson(String baseJson) {
		this.baseJson = baseJson;
	}

	public String getUpdatedJson() {
		return updatedJson;
	}

	public void setUpdatedJson(String updatedJson) {
		this.updatedJson = updatedJson;
	}

	public String getUpdatedDate() {
		return updatedDate;
	}

	public void setUpdatedDate(String updatedDate) {
		this.updatedDate = updatedDate;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

}
