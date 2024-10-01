package com.crmws.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PayinPaymentS2SRequest {

	@JsonProperty("PAY_ID")
	private String payId;

	@JsonProperty("PAY_TYPE")
	private String payType;

	@JsonProperty("CUST_NAME")
	private String custName;

	@JsonProperty("CUST_FIRST_NAME")
	private String custFirstName;

	@JsonProperty("CUST_LAST_NAME")
	private String custLastName;

	@JsonProperty("CUST_STREET_ADDRESS1")
	private String custStreeAddress1;

	@JsonProperty("CUST_CITY")
	private String custCity;

	@JsonProperty("CUST_STATE")
	private String custState;

	@JsonProperty("CUST_COUNTRY")
	private String custCountry;

	@JsonProperty("CUST_ZIP")
	private String custZip;

	@JsonProperty("CUST_PHONE")
	private String custPhone;

	@JsonProperty("CUST_EMAIL")
	private String custEmail;

	@JsonProperty("AMOUNT")
	private String amount;

	@JsonProperty("TXNTYPE")
	private String txntype;

	@JsonProperty("CURRENCY_CODE")
	private String currencyCode;

	@JsonProperty("PRODUCT_DESC")
	private String productDesc;

	@JsonProperty("ORDER_ID")
	private String orderId;

	@JsonProperty("RETURN_URL")
	private String return_url;

	@JsonProperty("HASH")
	private String hash;

	@JsonProperty("PAYMENT_TYPE")
	private String paymentType;

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

	public String getReturn_url() {
		return return_url;
	}

	public void setReturn_url(String return_url) {
		this.return_url = return_url;
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

	@Override
	public String toString() {
		return "PayinPaymentS2SRequest [payId=" + payId + ", payType=" + payType + ", custName=" + custName
				+ ", custFirstName=" + custFirstName + ", custLastName=" + custLastName + ", custStreeAddress1="
				+ custStreeAddress1 + ", custCity=" + custCity + ", custState=" + custState + ", custCountry="
				+ custCountry + ", custZip=" + custZip + ", custPhone=" + custPhone + ", custEmail=" + custEmail
				+ ", amount=" + amount + ", txntype=" + txntype + ", currencyCode=" + currencyCode + ", productDesc="
				+ productDesc + ", orderId=" + orderId + ", return_url=" + return_url + ", hash=" + hash
				+ ", paymentType=" + paymentType + "]";
	}

}
