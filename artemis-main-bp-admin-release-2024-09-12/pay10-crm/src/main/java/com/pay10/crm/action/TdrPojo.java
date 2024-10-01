package com.pay10.crm.action;

import java.io.Serializable;

public class TdrPojo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3235603446807671871L;

	private String bankTotalTdr;

	private String totalAggregatorCommisn;
	private String totalAmtPaytoMerchant;
	private String totalPayoutfrmNodal;
	private String totalAmount;
	private String grossAmount;
	private String merchantTdrCalculate;
	private String totalGstOnMerchant;
	private String netMerchantPayableAmount;
	
	private String acquirerSurchargeAmount;
	private String pgSurchargeAmount;
	private String iPaySurchargeAmount;
	private String mmadSurchargeAmount;

	
	
	public String getAcquirerSurchargeAmount() {
		return acquirerSurchargeAmount;
	}

	public void setAcquirerSurchargeAmount(String acquirerSurchargeAmount) {
		this.acquirerSurchargeAmount = acquirerSurchargeAmount;
	}

	public String getPgSurchargeAmount() {
		return pgSurchargeAmount;
	}

	public void setPgSurchargeAmount(String pgSurchargeAmount) {
		this.pgSurchargeAmount = pgSurchargeAmount;
	}

	public String getiPaySurchargeAmount() {
		return iPaySurchargeAmount;
	}

	public void setiPaySurchargeAmount(String iPaySurchargeAmount) {
		this.iPaySurchargeAmount = iPaySurchargeAmount;
	}

	public String getMmadSurchargeAmount() {
		return mmadSurchargeAmount;
	}

	public void setMmadSurchargeAmount(String mmadSurchargeAmount) {
		this.mmadSurchargeAmount = mmadSurchargeAmount;
	}

	public String getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(String totalAmount) {
		this.totalAmount = totalAmount;
	}

	public String getNetMerchantPayableAmount() {
		return netMerchantPayableAmount;
	}

	public String getGrossAmount() {
		return grossAmount;
	}

	public void setGrossAmount(String grossAmount) {
		this.grossAmount = grossAmount;
	}

	public void setNetMerchantPayableAmount(String netMerchantPayableAmount) {
		this.netMerchantPayableAmount = netMerchantPayableAmount;
	}

	public String getMerchantTdrCalculate() {
		return merchantTdrCalculate;
	}

	public void setMerchantTdrCalculate(String merchantTdrCalculate) {
		this.merchantTdrCalculate = merchantTdrCalculate;
	}

	public String getTotalGstOnMerchant() {
		return totalGstOnMerchant;
	}

	public void setTotalGstOnMerchant(String totalGstOnMerchant) {
		this.totalGstOnMerchant = totalGstOnMerchant;
	}

	public String getBankTotalTdr() {
		return bankTotalTdr;
	}

	public void setBankTotalTdr(String bankTotalTdr) {
		this.bankTotalTdr = bankTotalTdr;
	}

	public String getTotalAggregatorCommisn() {
		return totalAggregatorCommisn;
	}

	public void setTotalAggregatorCommisn(String totalAggregatorCommisn) {
		this.totalAggregatorCommisn = totalAggregatorCommisn;
	}

	public String getTotalAmtPaytoMerchant() {
		return totalAmtPaytoMerchant;
	}

	public void setTotalAmtPaytoMerchant(String totalAmtPaytoMerchant) {
		this.totalAmtPaytoMerchant = totalAmtPaytoMerchant;
	}

	public String getTotalPayoutfrmNodal() {
		return totalPayoutfrmNodal;
	}

	public void setTotalPayoutfrmNodal(String totalPayoutfrmNodal) {
		this.totalPayoutfrmNodal = totalPayoutfrmNodal;
	}

}
