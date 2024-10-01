package com.pay10.commons.user;

import java.io.Serializable;

import com.pay10.commons.util.InvoiceStatus;
import com.pay10.commons.util.PayinPaymentS2SStatus;

public class PayinPaymentS2S implements Serializable {

	private static final long serialVersionUID = 81100892505476449L;

	private Long id;
	private String paymentRedirectId;
	private String paymentRedirectUrl;
	private String payId;
	private String payType;
	private String custName;
	private String custFirstName;
	private String custLastName;
	private String custStreeAddress1;
	private String custCity;
	private String custState;
	private String custCountry;
	private String custZip;
	private String custPhone;
	private String custEmail;
	private String amount;
	private String txntype;
	private String currencyCode;
	private String productDesc;
	private String orderId;
	private String returnUrl;
	private String hash;
	private String paymentType;
	private String createDate;
	private String updateDate;
	private PayinPaymentS2SStatus paymentStatus;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getPaymentRedirectId() {
		return paymentRedirectId;
	}

	public void setPaymentRedirectId(String paymentRedirectId) {
		this.paymentRedirectId = paymentRedirectId;
	}

	public String getPaymentRedirectUrl() {
		return paymentRedirectUrl;
	}

	public void setPaymentRedirectUrl(String paymentRedirectUrl) {
		this.paymentRedirectUrl = paymentRedirectUrl;
	}

	public String getPayId() {
		return payId;
	}

	public void setPayId(String payId) {
		this.payId = payId;
	}

	public String getPayType() {
		return payType;
	}

	public void setPayType(String payType) {
		this.payType = payType;
	}

	public String getCustName() {
		return custName;
	}

	public void setCustName(String custName) {
		this.custName = custName;
	}

	public String getCustFirstName() {
		return custFirstName;
	}

	public void setCustFirstName(String custFirstName) {
		this.custFirstName = custFirstName;
	}

	public String getCustLastName() {
		return custLastName;
	}

	public void setCustLastName(String custLastName) {
		this.custLastName = custLastName;
	}

	public String getCustStreeAddress1() {
		return custStreeAddress1;
	}

	public void setCustStreeAddress1(String custStreeAddress1) {
		this.custStreeAddress1 = custStreeAddress1;
	}

	public String getCustCity() {
		return custCity;
	}

	public void setCustCity(String custCity) {
		this.custCity = custCity;
	}

	public String getCustState() {
		return custState;
	}

	public void setCustState(String custState) {
		this.custState = custState;
	}

	public String getCustCountry() {
		return custCountry;
	}

	public void setCustCountry(String custCountry) {
		this.custCountry = custCountry;
	}

	public String getCustZip() {
		return custZip;
	}

	public void setCustZip(String custZip) {
		this.custZip = custZip;
	}

	public String getCustPhone() {
		return custPhone;
	}

	public void setCustPhone(String custPhone) {
		this.custPhone = custPhone;
	}

	public String getCustEmail() {
		return custEmail;
	}

	public void setCustEmail(String custEmail) {
		this.custEmail = custEmail;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public String getTxntype() {
		return txntype;
	}

	public void setTxntype(String txntype) {
		this.txntype = txntype;
	}

	public String getCurrencyCode() {
		return currencyCode;
	}

	public void setCurrencyCode(String currencyCode) {
		this.currencyCode = currencyCode;
	}

	public String getProductDesc() {
		return productDesc;
	}

	public void setProductDesc(String productDesc) {
		this.productDesc = productDesc;
	}

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public String getReturnUrl() {
		return returnUrl;
	}

	public void setReturnUrl(String returnUrl) {
		this.returnUrl = returnUrl;
	}

	public String getHash() {
		return hash;
	}

	public void setHash(String hash) {
		this.hash = hash;
	}

	public String getPaymentType() {
		return paymentType;
	}

	public void setPaymentType(String paymentType) {
		this.paymentType = paymentType;
	}

	public String getCreateDate() {
		return createDate;
	}

	public void setCreateDate(String createDate) {
		this.createDate = createDate;
	}

	public String getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(String updateDate) {
		this.updateDate = updateDate;
	}

	public PayinPaymentS2SStatus getPaymentStatus() {
		return paymentStatus;
	}

	public void setPaymentStatus(PayinPaymentS2SStatus paymentStatus) {
		this.paymentStatus = paymentStatus;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	@Override
	public String toString() {
		return "PayinPaymentS2S [id=" + id + ", paymentRedirectId=" + paymentRedirectId + ", paymentRedirectUrl="
				+ paymentRedirectUrl + ", payId=" + payId + ", payType=" + payType + ", custName=" + custName
				+ ", custFirstName=" + custFirstName + ", custLastName=" + custLastName + ", custStreeAddress1="
				+ custStreeAddress1 + ", custCity=" + custCity + ", custState=" + custState + ", custCountry="
				+ custCountry + ", custZip=" + custZip + ", custPhone=" + custPhone + ", custEmail=" + custEmail
				+ ", amount=" + amount + ", txntype=" + txntype + ", currencyCode=" + currencyCode + ", productDesc="
				+ productDesc + ", orderId=" + orderId + ", returnUrl=" + returnUrl + ", hash=" + hash
				+ ", paymentType=" + paymentType + ", createDate=" + createDate + ", updateDate=" + updateDate
				+ ", paymentStatus=" + paymentStatus + "]";
	}

}
