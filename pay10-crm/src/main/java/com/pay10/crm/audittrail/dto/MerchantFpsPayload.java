package com.pay10.crm.audittrail.dto;

public class MerchantFpsPayload {

	private String payId;
	private String preferenceSet;
	private boolean activateMerchantMgmt;

	public MerchantFpsPayload() {
	}

	public MerchantFpsPayload(String payId, String preferenceSet, boolean activateMerchantMgmt) {
		super();
		this.payId = payId;
		this.preferenceSet = preferenceSet;
		this.activateMerchantMgmt = activateMerchantMgmt;
	}

	public String getPayId() {
		return payId;
	}

	public void setPayId(String payId) {
		this.payId = payId;
	}

	public String getPreferenceSet() {
		return preferenceSet;
	}

	public void setPreferenceSet(String preferenceSet) {
		this.preferenceSet = preferenceSet;
	}

	public boolean isActivateMerchantMgmt() {
		return activateMerchantMgmt;
	}

	public void setActivateMerchantMgmt(boolean activateMerchantMgmt) {
		this.activateMerchantMgmt = activateMerchantMgmt;
	}
}
