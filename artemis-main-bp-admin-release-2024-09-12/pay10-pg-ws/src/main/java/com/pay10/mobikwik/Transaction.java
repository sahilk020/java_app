package com.pay10.mobikwik;

import org.springframework.stereotype.Service;

@Service("mobikwikTransaction")
public class Transaction {

	private String merchantIdentifier;
	private String amount;
	private String buyerEmail;
	private String currency;
	private String mode;
	private String orderId;
	private String returnUrl;
	private String zpPayOption;
	private String txnType;
	private String checksum;
	
	private String bank;
	private String bankid;
	private String doRedirect;
	private String responseCode;
	private String responseDescription;
	private String pgTransId; 
	
	private String status; 
	private String statuscode; 
	private String statusmessage; 
	private String refId;
	private String txid; 
	private String ordertype; 
	
	public String getMerchantIdentifier() {
		return merchantIdentifier;
	}
	public void setMerchantIdentifier(String merchantIdentifier) {
		this.merchantIdentifier = merchantIdentifier;
	}
	public String getAmount() {
		return amount;
	}
	public void setAmount(String amount) {
		this.amount = amount;
	}
	public String getBuyerEmail() {
		return buyerEmail;
	}
	public void setBuyerEmail(String buyerEmail) {
		this.buyerEmail = buyerEmail;
	}
	public String getCurrency() {
		return currency;
	}
	public void setCurrency(String currency) {
		this.currency = currency;
	}
	public String getMode() {
		return mode;
	}
	public void setMode(String mode) {
		this.mode = mode;
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
	public String getZpPayOption() {
		return zpPayOption;
	}
	public void setZpPayOption(String zpPayOption) {
		this.zpPayOption = zpPayOption;
	}
	public String getTxnType() {
		return txnType;
	}
	public void setTxnType(String txnType) {
		this.txnType = txnType;
	}
	public String getChecksum() {
		return checksum;
	}
	public void setChecksum(String checksum) {
		this.checksum = checksum;
	}
	public String getBank() {
		return bank;
	}
	public void setBank(String bank) {
		this.bank = bank;
	}
	public String getBankid() {
		return bankid;
	}
	public void setBankid(String bankid) {
		this.bankid = bankid;
	}
	public String getDoRedirect() {
		return doRedirect;
	}
	public void setDoRedirect(String doRedirect) {
		this.doRedirect = doRedirect;
	}
	public String getResponseDescription() {
		return responseDescription;
	}
	public void setResponseDescription(String responseDescription) {
		this.responseDescription = responseDescription;
	}
	public String getPgTransId() {
		return pgTransId;
	}
	public void setPgTransId(String pgTransId) {
		this.pgTransId = pgTransId;
	}
	public String getResponseCode() {
		return responseCode;
	}
	public void setResponseCode(String responseCode) {
		this.responseCode = responseCode;
	}
	public String getTxid() {
		return txid;
	}
	public void setTxid(String txid) {
		this.txid = txid;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getStatuscode() {
		return statuscode;
	}
	public void setStatuscode(String statuscode) {
		this.statuscode = statuscode;
	}
	public String getStatusmessage() {
		return statusmessage;
	}
	public void setStatusmessage(String statusmessage) {
		this.statusmessage = statusmessage;
	}
	public String getRefId() {
		return refId;
	}
	public void setRefId(String refId) {
		this.refId = refId;
	}
	public String getOrdertype() {
		return ordertype;
	}
	public void setOrdertype(String ordertype) {
		this.ordertype = ordertype;
	}
	
	
}
