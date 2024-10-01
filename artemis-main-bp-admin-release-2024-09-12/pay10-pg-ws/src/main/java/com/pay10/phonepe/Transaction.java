package com.pay10.phonepe;

import org.springframework.stereotype.Service;

@Service("phonepeTransaction")
public class Transaction {

	
	// SALE
	private String request;
	private String saltKey;
	private String saltIndex;
	private String merchantId;
	private String transactionId;
	private String merchantUserId;
	private String amount;
	private String merchantOrderId;
	private String mobileNumber;
	private String message;
	private String subMerchant;
	private String email;
	
	private String success;
	private String code;
	private String shortName;
	
	private String redirectUrl;
	private String redirectMode;
	
	//REFUND AND STATUS
	private String providerReferenceId;
	private String paymentState;
	private String payResponseCode;
	private String originalTransactionId;
	private String hashCodeAcquirer;
	private String hashCodePg;
	
	private String status;
	
	public String getRequest() {
		return request;
	}
	public void setRequest(String request) {
		this.request = request;
	}
	public String getSaltKey() {
		return saltKey;
	}
	public void setSaltKey(String saltKey) {
		this.saltKey = saltKey;
	}
	public String getSaltIndex() {
		return saltIndex;
	}
	public void setSaltIndex(String saltIndex) {
		this.saltIndex = saltIndex;
	}
	public String getMerchantId() {
		return merchantId;
	}
	public void setMerchantId(String merchantId) {
		this.merchantId = merchantId;
	}
	public String getTransactionId() {
		return transactionId;
	}
	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}
	public String getMerchantUserId() {
		return merchantUserId;
	}
	public void setMerchantUserId(String merchantUserId) {
		this.merchantUserId = merchantUserId;
	}
	public String getAmount() {
		return amount;
	}
	public void setAmount(String amount) {
		this.amount = amount;
	}
	public String getMerchantOrderId() {
		return merchantOrderId;
	}
	public void setMerchantOrderId(String merchantOrderId) {
		this.merchantOrderId = merchantOrderId;
	}
	public String getMobileNumber() {
		return mobileNumber;
	}
	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getSubMerchant() {
		return subMerchant;
	}
	public void setSubMerchant(String subMerchant) {
		this.subMerchant = subMerchant;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getSuccess() {
		return success;
	}
	public void setSuccess(String success) {
		this.success = success;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getShortName() {
		return shortName;
	}
	public void setShortName(String shortName) {
		this.shortName = shortName;
	}
	public String getProviderReferenceId() {
		return providerReferenceId;
	}
	public void setProviderReferenceId(String providerReferenceId) {
		this.providerReferenceId = providerReferenceId;
	}
	public String getPaymentState() {
		return paymentState;
	}
	public void setPaymentState(String paymentState) {
		this.paymentState = paymentState;
	}
	public String getPayResponseCode() {
		return payResponseCode;
	}
	public void setPayResponseCode(String payResponseCode) {
		this.payResponseCode = payResponseCode;
	}
	public String getOriginalTransactionId() {
		return originalTransactionId;
	}
	public void setOriginalTransactionId(String originalTransactionId) {
		this.originalTransactionId = originalTransactionId;
	}
	public String getRedirectUrl() {
		return redirectUrl;
	}
	public void setRedirectUrl(String redirectUrl) {
		this.redirectUrl = redirectUrl;
	}
	public String getRedirectMode() {
		return redirectMode;
	}
	public void setRedirectMode(String redirectMode) {
		this.redirectMode = redirectMode;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getHashCodeAcquirer() {
		return hashCodeAcquirer;
	}
	public void setHashCodeAcquirer(String hashCodeAcquirer) {
		this.hashCodeAcquirer = hashCodeAcquirer;
	}
	public String getHashCodePg() {
		return hashCodePg;
	}
	public void setHashCodePg(String hashCodePg) {
		this.hashCodePg = hashCodePg;
	}
	
}
