package com.pay10.crm.dashboard;

import java.io.Serializable;

public class PieChart implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -957731054268666487L;
	private String visa;
	private String mastercard;
	private String amex;
	private String net;
	private String maestro;
	private String ezeeClick;
	private String totalSuccess;
	private String totalCancelled;
	private String totalFailed;
	private String totalPending;
	private String totalErrors;
	private String totalTimeouts;
	private String totalCredit;
	private String totalDebit;
	private String txndate;
	private String totalRefunded;
	private String totalNewOder;
	private String totalEnrolled;
	private String inr;
	private String usd;
	private String aed;
	private String gbp;
	private String eur;
	private String aud;
	private String totalNetBankingTransaction;
	private String totalCreditCardsTransaction;
	private String totalDebitCardsTransaction;
	private String cc;
	private String dc;
	private String nb;
	private String totalWalletTransaction;
	private String other;
	private String wl;
	private double totalSuccessAmount;

	public String getTotalCancelled() {
		return totalCancelled;
	}

	public void setTotalCancelled(String totalCancelled) {
		this.totalCancelled = totalCancelled;
	}

	public String getVisa() {
		return visa;
	}

	public void setVisa(String visa) {
		this.visa = visa;
	}

	public String getMastercard() {
		return mastercard;
	}

	public void setMastercard(String mastercard) {
		this.mastercard = mastercard;
	}

	public String getAmex() {
		return amex;
	}

	public void setAmex(String amex) {
		this.amex = amex;
	}

	public String getNet() {
		return net;
	}

	public void setNet(String net) {
		this.net = net;
	}

	public String getTotalSuccess() {
		return totalSuccess;
	}

	public void setTotalSuccess(String totalSuccess) {
		this.totalSuccess = totalSuccess;
	}

	public String getTotalFailed() {
		return totalFailed;
	}

	public void setTotalFailed(String totalFailed) {
		this.totalFailed = totalFailed;
	}

	public String getTotalPending() {
		return totalPending;
	}

	public void setTotalPending(String totalPending) {
		this.totalPending = totalPending;
	}

	public String getTotalErrors() {
		return totalErrors;
	}

	public void setTotalErrors(String totalErrors) {
		this.totalErrors = totalErrors;
	}

	public String getTotalTimeouts() {
		return totalTimeouts;
	}

	public void setTotalTimeouts(String totalTimeouts) {
		this.totalTimeouts = totalTimeouts;
	}

	public String getMaestro() {
		return maestro;
	}

	public void setMaestro(String maestro) {
		this.maestro = maestro;
	}

	public String getEzeeClick() {
		return ezeeClick;
	}

	public void setEzeeClick(String ezeeClick) {
		this.ezeeClick = ezeeClick;
	}

	public String getTxndate() {
		return txndate;
	}

	public void setTxndate(String txndate) {
		this.txndate = txndate;
	}

	public String getTotalCredit() {
		return totalCredit;
	}

	public void setTotalCredit(String totalCredit) {
		this.totalCredit = totalCredit;
	}

	public String getTotalDebit() {
		return totalDebit;
	}

	public void setTotalDebit(String totalDebit) {
		this.totalDebit = totalDebit;
	}

	public String getTotalRefunded() {
		return totalRefunded;
	}

	public void setTotalRefunded(String totalRefunded) {
		this.totalRefunded = totalRefunded;
	}

	public String getTotalNewOder() {
		return totalNewOder;
	}

	public void setTotalNewOder(String totalNewOder) {
		this.totalNewOder = totalNewOder;
	}

	public String getTotalEnrolled() {
		return totalEnrolled;
	}

	public void setTotalEnrolled(String totalEnrolled) {
		this.totalEnrolled = totalEnrolled;
	}

	public String getInr() {
		return inr;
	}

	public void setInr(String inr) {
		this.inr = inr;
	}

	public String getUsd() {
		return usd;
	}

	public void setUsd(String usd) {
		this.usd = usd;
	}

	public String getAed() {
		return aed;
	}

	public void setAed(String aed) {
		this.aed = aed;
	}

	public String getGbp() {
		return gbp;
	}

	public void setGbp(String gbp) {
		this.gbp = gbp;
	}

	public String getEur() {
		return eur;
	}

	public void setEur(String eur) {
		this.eur = eur;
	}

	public String getAud() {
		return aud;
	}

	public void setAud(String aud) {
		this.aud = aud;
	}

	public String getCc() {
		return cc;
	}

	public void setCc(String cc) {
		this.cc = cc;
	}

	public String getDc() {
		return dc;
	}

	public void setDc(String dc) {
		this.dc = dc;
	}

	public String getNb() {
		return nb;
	}

	public void setNb(String nb) {
		this.nb = nb;
	}

	public String getTotalDebitCardsTransaction() {
		return totalDebitCardsTransaction;
	}

	public void setTotalDebitCardsTransaction(String totalDebitCardsTransaction) {
		this.totalDebitCardsTransaction = totalDebitCardsTransaction;
	}

	public String getOther() {
		return other;
	}

	public void setOther(String other) {
		this.other = other;
	}

	public String getTotalWalletTransaction() {
		return totalWalletTransaction;
	}

	public void setTotalWalletTransaction(String totalWalletTransaction) {
		this.totalWalletTransaction = totalWalletTransaction;
	}

	public String getTotalCreditCardsTransaction() {
		return totalCreditCardsTransaction;
	}

	public void setTotalCreditCardsTransaction(String totalCreditCardsTransaction) {
		this.totalCreditCardsTransaction = totalCreditCardsTransaction;
	}

	public String getTotalNetBankingTransaction() {
		return totalNetBankingTransaction;
	}

	public void setTotalNetBankingTransaction(String totalNetBankingTransaction) {
		this.totalNetBankingTransaction = totalNetBankingTransaction;
	}

	public String getWl() {
		return wl;
	}

	public void setWl(String wl) {
		this.wl = wl;
	}

	public double getTotalSuccessAmount() {
		return totalSuccessAmount;
	}

	public void setTotalSuccessAmount(double totalSuccessAmount) {
		this.totalSuccessAmount = totalSuccessAmount;
	}

}
