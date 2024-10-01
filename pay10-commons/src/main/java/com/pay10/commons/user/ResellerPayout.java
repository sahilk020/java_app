package com.pay10.commons.user;

import java.io.Serializable;
import java.text.DecimalFormat;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 
 */

@Entity
@Table
public class ResellerPayout implements Serializable {

	private static final long serialVersionUID = 6888067352650779333L;
	private static final DecimalFormat df = new DecimalFormat("0.00");

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	private String payoutId;
	private String resellerId;
	private String settlementDate;
	private String batchNo;
	private double totalCommission;
	private double totalamount;

	private String tds;
	private String utrNo;
	private String fromDate;
	private String toDate;
	private String status;
	private String resellerName;
	private double totalSMACommission;
	private double totalMACommission;
	private double totalAgentCommission;
	private String currency;
	private String creationDate;

	public ResellerPayout() {

	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getPayoutId() {
		return payoutId;
	}

	public void setPayoutId(String payoutId) {
		this.payoutId = payoutId;
	}

	public String getResellerId() {
		return resellerId;
	}

	public void setResellerId(String resellerId) {
		this.resellerId = resellerId;
	}

	public String getSettlementDate() {
		return settlementDate;
	}

	public void setSettlementDate(String settlementDate) {
		this.settlementDate = settlementDate;
	}

	public String getBatchNo() {
		return batchNo;
	}

	public void setBatchNo(String batchNo) {
		this.batchNo = batchNo;
	}

	public String getTds() {
		return tds;
	}

	public void setTds(String tds) {
		this.tds = tds;
	}

	public String getUtrNo() {
		return utrNo;
	}

	public void setUtrNo(String utrNo) {
		this.utrNo = utrNo;
	}

	public String getFromDate() {
		return fromDate;
	}

	public void setFromDate(String fromDate) {
		this.fromDate = fromDate;
	}

	public String getToDate() {
		return toDate;
	}

	public void setToDate(String toDate) {
		this.toDate = toDate;
	}

	public double getTotalCommission() {
		return Double.valueOf(df.format(totalCommission));
	}

	public void setTotalCommission(double totalCommission) {
		this.totalCommission = totalCommission;
	}

	public double getTotalamount() {
		return totalamount;
	}

	public void setTotalamount(double totalamount) {
		this.totalamount = totalamount;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getResellerName() {
		return resellerName;
	}

	public void setResellerName(String resellerName) {
		this.resellerName = resellerName;
	}

	public double getTotalSMACommission() {
		return totalSMACommission;
	}

	public void setTotalSMACommission(double totalSMACommission) {
		this.totalSMACommission = totalSMACommission;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public double getTotalMACommission() {
		return totalMACommission;
	}

	public void setTotalMACommission(double totalMACommission) {
		this.totalMACommission = totalMACommission;
	}

	public double getTotalAgentCommission() {
		return totalAgentCommission;
	}

	public void setTotalAgentCommission(double totalAgentCommission) {
		this.totalAgentCommission = totalAgentCommission;
	}

	public String getCreationDate() {
		return this.creationDate;
	}

	public void setCreationDate(String creationDate) {
		this.creationDate = creationDate;
	}


	@Override
	public String toString() {
		return "ResellerPayout [id=" + id + ", payoutId=" + payoutId + ", resellerId=" + resellerId
				+ ", settlementDate=" + settlementDate + ", batchNo=" + batchNo + ", totalCommission=" + totalCommission
				+ ", totalamount=" + totalamount + ", tds=" + tds + ", utrNo=" + utrNo + ", fromDate=" + fromDate
				+ ", toDate=" + toDate + ", status=" + status + ", resellerName=" + resellerName
				+ ", totalSMACommission=" + totalSMACommission + ", totalMACommission=" + totalMACommission
				+ ", totalAgentCommission=" + totalAgentCommission + "]";
	}
	
	
}
