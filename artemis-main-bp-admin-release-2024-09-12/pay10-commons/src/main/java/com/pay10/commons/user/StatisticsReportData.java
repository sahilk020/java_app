package com.pay10.commons.user;

import java.io.Serializable;
import java.util.Map;

/**
 * @author Chandan
 *
 */

public class StatisticsReportData implements Serializable {

	private static final long serialVersionUID = 1822499142308976738L;

	private String merchant;
	private String capturedTxnDate;
	private int capturedTotalTxn;
	private double capturedTotalAmount;
	private String settledTotalTxn;
	private String settledTotalAmount;
	private String settledTxnDate;
	private String nodalTotalTxn;
	private String nodalTotalAmount;
	private String nodalTxnDate;
	private Map<String, Double> mapAmount;
	private Map<String, Integer> mapCount;
	private Map<String, Double> nodalMapAmount;
	private Map<String, Integer> nodalMapCount;
	private String pendingTotalTxn;
	private String pendingTotalAmount;

	public String getCapturedTxnDate() {
		return capturedTxnDate;
	}

	public void setCapturedTxnDate(String capturedTxnDate) {
		this.capturedTxnDate = capturedTxnDate;
	}
	
	public int getCapturedTotalTxn() {
		return capturedTotalTxn;
	}

	public void setCapturedTotalTxn(int capturedTotalTxn) {
		this.capturedTotalTxn = capturedTotalTxn;
	}

	public double getCapturedTotalAmount() {
		return capturedTotalAmount;
	}

	public void setCapturedTotalAmount(double capturedTotalAmount) {
		this.capturedTotalAmount = capturedTotalAmount;
	}

	public String getSettledTotalTxn() {
		return settledTotalTxn;
	}

	public void setSettledTotalTxn(String settledTotalTxn) {
		this.settledTotalTxn = settledTotalTxn;
	}

	public String getSettledTotalAmount() {
		return settledTotalAmount;
	}

	public void setSettledTotalAmount(String settledTotalAmount) {
		this.settledTotalAmount = settledTotalAmount;
	}

	public String getSettledTxnDate() {
		return settledTxnDate;
	}

	public void setSettledTxnDate(String settledTxnDate) {
		this.settledTxnDate = settledTxnDate;
	}
	
	public String getNodalTotalTxn() {
		return nodalTotalTxn;
	}

	public void setNodalTotalTxn(String nodalTotalTxn) {
		this.nodalTotalTxn = nodalTotalTxn;
	}

	public String getNodalTotalAmount() {
		return nodalTotalAmount;
	}

	public void setNodalTotalAmount(String nodalTotalAmount) {
		this.nodalTotalAmount = nodalTotalAmount;
	}

	public String getNodalTxnDate() {
		return nodalTxnDate;
	}

	public void setNodalTxnDate(String nodalTxnDate) {
		this.nodalTxnDate = nodalTxnDate;
	}

	public String getPendingTotalTxn() {
		return pendingTotalTxn;
	}

	public void setPendingTotalTxn(String pendingTotalTxn) {
		this.pendingTotalTxn = pendingTotalTxn;
	}

	public String getPendingTotalAmount() {
		return pendingTotalAmount;
	}

	public void setPendingTotalAmount(String pendingTotalAmount) {
		this.pendingTotalAmount = pendingTotalAmount;
	}

	public String getMerchant() {
		return merchant;
	}

	public void setMerchant(String merchant) {
		this.merchant = merchant;
	}
	
	public Map<String, Double> getMapAmount() {
		return mapAmount;
	}

	public void setMapAmount(Map<String, Double> mapAmount) {
		this.mapAmount = mapAmount;
	}

	public Map<String, Integer> getMapCount() {
		return mapCount;
	}

	public void setMapCount(Map<String, Integer> mapCount) {
		this.mapCount = mapCount;
	}
	
	public Map<String, Double> getNodalMapAmount() {
		return nodalMapAmount;
	}

	public void setNodalMapAmount(Map<String, Double> nodalMapAmount) {
		this.nodalMapAmount = nodalMapAmount;
	}

	public Map<String, Integer> getNodalMapCount() {
		return nodalMapCount;
	}

	public void setNodalMapCount(Map<String, Integer> nodalMapCount) {
		this.nodalMapCount = nodalMapCount;
	}

}
