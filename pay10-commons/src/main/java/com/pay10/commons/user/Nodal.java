package com.pay10.commons.user;

import java.io.Serializable;

import com.pay10.commons.util.AcquirerTypeNodal;
import com.pay10.commons.util.BeneficiaryTypes;
import com.pay10.commons.util.CurrencyTypes;
import com.pay10.commons.util.NodalPaymentTypes;

public class Nodal implements Serializable{

	private static final long serialVersionUID = -4085790886077932758L;
	
	private String nodalAcquirer; // AcquirerTypeNodal
	private String custId;
	private String srcAccountNumber;
	private String beneficiaryCode;
	private String paymentType;
	private String beneName;
	private String beneType;
	private String currencyCode;
	private String transactionLimit;
	private String bankName;
	private String ifscCode;
	private String beneAccountNumber;
	private String action;
	private String payId;
	
	public Nodal() {
		setDefault();
	}
	
	private void setDefault() {
//		setCustId("124");
//		setSrcAccountNumber("016190100002195");
//		setBeneficiaryCode("PUNEETs");
//		setBeneName("PUNEETs");
//		setBeneAccountNumber("886075373636");
//		setIfscCode("YESB0000262");
//		setBankName("Yes Banks");
	}

	public String getNodalAcquirer() {
		return nodalAcquirer;
	}
	public void setNodalAcquirer(String nodalAcquirer) {
		this.nodalAcquirer = nodalAcquirer;
	}
	public String getCustId() {
		return custId;
	}
	public void setCustId(String custId) {
		this.custId = custId;
	}
	public String getBeneficiaryCode() {
		return beneficiaryCode;
	}
	public void setBeneficiaryCode(String beneficiaryCode) {
		this.beneficiaryCode = beneficiaryCode;
	}
	public String getPaymentType() {
		return paymentType;
	}
	public void setPaymentType(String paymentType) {
		this.paymentType = paymentType;
	}
	public String getBeneName() {
		return beneName;
	}
	public void setBeneName(String beneName) {
		this.beneName = beneName;
	}
	public String getBeneType() {
		return beneType;
	}
	public void setBeneType(String beneType) {
		this.beneType = beneType;
	}
	public String getCurrencyCode() {
		return currencyCode;
	}
	public void setCurrencyCode(String currencyCode) {
		this.currencyCode = currencyCode;
	}
	public String getTransactionLimit() {
		return transactionLimit;
	}
	public void setTransactionLimit(String transactionLimit) {
		this.transactionLimit = transactionLimit;
	}
	public String getBankName() {
		return bankName;
	}
	public void setBankName(String bankName) {
		this.bankName = bankName;
	}
	public String getIfscCode() {
		return ifscCode;
	}
	public void setIfscCode(String ifscCode) {
		this.ifscCode = ifscCode;
	}
	public String getBeneAccountNumber() {
		return beneAccountNumber;
	}
	public void setBeneAccountNumber(String beneAccountNumber) {
		this.beneAccountNumber = beneAccountNumber;
	}
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	public String getPayId() {
		return payId;
	}
	public void setPayId(String payId) {
		this.payId = payId;
	}

	public String getSrcAccountNumber() {
		return srcAccountNumber;
	}

	public void setSrcAccountNumber(String srcAccountNumber) {
		this.srcAccountNumber = srcAccountNumber;
	}

	@Override
	public String toString() {
		return "Nodal [nodalAcquirer=" + nodalAcquirer + ", custId=" + custId + ", srcAccountNumber=" + srcAccountNumber
				+ ", beneficiaryCode=" + beneficiaryCode + ", paymentType=" + paymentType + ", beneName=" + beneName
				+ ", beneType=" + beneType + ", currencyCode=" + currencyCode + ", transactionLimit=" + transactionLimit
				+ ", bankName=" + bankName + ", ifscCode=" + ifscCode + ", beneAccountNumber=" + beneAccountNumber
				+ ", action=" + action + ", payId=" + payId + "]";
	}
}
