package com.pay10.commons.user;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Proxy;


@Entity
@Proxy(lazy= true)@Cache(usage=CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class AccountCurrencyPayout implements Serializable {

	private static final long serialVersionUID = 5796272112495882669L;
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getAcquirer() {
		return Acquirer;
	}

	public void setAcquirer(String acquirer) {
		Acquirer = acquirer;
	}

	private String Acquirer;
	private String currencyCode;
	private String merchantId;

	private String txnKey;

	@Column(columnDefinition = "TEXT", length = 65535)
	private String adf1;

	@Column(columnDefinition = "TEXT", length = 65535)
	private String adf2;

	@Column(columnDefinition = "TEXT", length = 65535)
	private String adf3;

	@Column(columnDefinition = "TEXT", length = 65535)
	private String adf4;

	@Column(columnDefinition = "TEXT", length = 65535)
	private String adf5;

	@Column(columnDefinition = "TEXT", length = 65535)
	private String adf6;

	@Column(columnDefinition = "TEXT", length = 65535)
	private String adf7;

	@Column(columnDefinition = "TEXT", length = 65535)
	private String adf8;

	@Column(columnDefinition = "TEXT", length = 65535)
	private String adf9;

	@Column(columnDefinition = "TEXT", length = 65535)
	private String adf10;

	@Column(columnDefinition = "TEXT", length = 65535)
	private String adf11;

	public String getCurrencyCode() {
		return currencyCode;
	}

	public void setCurrencyCode(String currencyCode) {
		this.currencyCode = currencyCode;
	}

	public String getMerchantId() {
		return merchantId;
	}

	public void setMerchantId(String merchantId) {
		this.merchantId = merchantId;
	}

	public String getTxnKey() {
		return txnKey;
	}

	public void setTxnKey(String txnKey) {
		this.txnKey = txnKey;
	}

	public String getAdf1() {
		return adf1;
	}

	public void setAdf1(String adf1) {
		this.adf1 = adf1;
	}

	public String getAdf2() {
		return adf2;
	}

	public void setAdf2(String adf2) {
		this.adf2 = adf2;
	}

	public String getAdf3() {
		return adf3;
	}

	public void setAdf3(String adf3) {
		this.adf3 = adf3;
	}

	public String getAdf4() {
		return adf4;
	}

	public void setAdf4(String adf4) {
		this.adf4 = adf4;
	}

	public String getAdf5() {
		return adf5;
	}

	public void setAdf5(String adf5) {
		this.adf5 = adf5;
	}

	public String getAdf6() {
		return adf6;
	}

	public void setAdf6(String adf6) {
		this.adf6 = adf6;
	}

	public String getAdf7() {
		return adf7;
	}

	public void setAdf7(String adf7) {
		this.adf7 = adf7;
	}

	public String getAdf8() {
		return adf8;
	}

	public void setAdf8(String adf8) {
		this.adf8 = adf8;
	}

	public String getAdf9() {
		return adf9;
	}

	public void setAdf9(String adf9) {
		this.adf9 = adf9;
	}

	public String getAdf10() {
		return adf10;
	}

	public void setAdf10(String adf10) {
		this.adf10 = adf10;
	}

	public String getAdf11() {
		return adf11;
	}

	public void setAdf11(String adf11) {
		this.adf11 = adf11;
	}

	@Override
	public String toString() {
		return "AccountCurrencyPayout [id=" + id + ", Acquirer=" + Acquirer + ", currencyCode=" + currencyCode
				+ ", merchantId=" + merchantId + ", txnKey=" + txnKey + ", adf1=" + adf1 + ", adf2=" + adf2 + ", adf3="
				+ adf3 + ", adf4=" + adf4 + ", adf5=" + adf5 + ", adf6=" + adf6 + ", adf7=" + adf7 + ", adf8=" + adf8
				+ ", adf9=" + adf9 + ", adf10=" + adf10 + ", adf11=" + adf11 + "]";
	}

	

}