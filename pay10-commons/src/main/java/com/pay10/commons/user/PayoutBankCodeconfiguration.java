package com.pay10.commons.user;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "payout_BankCode_Configuration")
public class PayoutBankCodeconfiguration implements Serializable, Cloneable{
	private static final long serialVersionUID = 4010671786498012786L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	private String acqBankcode;
	private String pgbankcode;
	private String bankName;
	private String  currency;
	private String updateBy;
	private String updateOn;
	private String createBy ;
	private String createon;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getAcqBankcode() {
		return acqBankcode;
	}
	public void setAcqBankcode(String acqBankcode) {
		this.acqBankcode = acqBankcode;
	}
	public String getPgbankcode() {
		return pgbankcode;
	}
	public void setPgbankcode(String pgbankcode) {
		this.pgbankcode = pgbankcode;
	}
	public String getBankName() {
		return bankName;
	}
	public void setBankName(String bankName) {
		this.bankName = bankName;
	}
	public String getCurrency() {
		return currency;
	}
	public void setCurrency(String currency) {
		this.currency = currency;
	}
	public String getUpdateBy() {
		return updateBy;
	}
	public void setUpdateBy(String updateBy) {
		this.updateBy = updateBy;
	}
	public String getUpdateOn() {
		return updateOn;
	}
	public void setUpdateOn(String updateOn) {
		this.updateOn = updateOn;
	}
	public String getCreateBy() {
		return createBy;
	}
	public void setCreateBy(String createBy) {
		this.createBy = createBy;
	}
	public String getCreateon() {
		return createon;
	}
	public void setCreateon(String createon) {
		this.createon = createon;
	}
	@Override
	public String toString() {
		return "PayoutBankCodeconfiguration [id=" + id + ", acqBankcode=" + acqBankcode + ", pgbankcode=" + pgbankcode
				+ ", bankName=" + bankName + ", currency=" + currency + ", updateBy=" + updateBy + ", updateOn="
				+ updateOn + ", createBy=" + createBy + ", createon=" + createon + "]";
	}
	
	
	
	
	
	

}
