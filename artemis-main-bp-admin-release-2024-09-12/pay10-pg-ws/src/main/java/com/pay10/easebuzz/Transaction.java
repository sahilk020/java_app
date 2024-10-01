package com.pay10.easebuzz;

import org.springframework.stereotype.Service;

import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;

@Service("easebuzzTransaction")
public class Transaction {

	private String appId;
	private String orderId;
	private String orderAmount;
	private String orderCurrency;
	private String customerName;
	private String customerEmail;
	private String customerPhone;
	private String paymentOption;
	private String paymentCode;
	private String referenceId;
	private String txStatus;
	private String paymentMode;
	private String txMsg;
	private String signature;
	private String message;
	private String refundId;
	private String status;
	private String orderStatus;

	private String merchantKey;
	private String bankCode;
	private String encryptionKey;
	private String txnid;
	private String amount;
	private String productinfo;
	private String firstname;
	private String phone;
	private String email;
	private String surl;
	private String furl;
	private String hash;
	private String salt;
	private String checksum;

	private String cardNo;
	private String cvv;
	private String expMonth;
	private String expYear;
	private String cardHname;
	private String iv;
	private String cardExp;

	private String upiVA;
	private String upiQR;
	private String acqrefundid;
	private String error_Message;
	private String ACquirertxid;
	
	


	public String getAcqrefundid() {
		return acqrefundid;
	}

	public void setAcqrefundid(String acqrefundid) {
		this.acqrefundid = acqrefundid;
	}

	public String getError_Message() {
		return error_Message;
	}

	public void setError_Message(String error_Message) {
		this.error_Message = error_Message;
	}

	public String getACquirertxid() {
		return ACquirertxid;
	}

	public void setACquirertxid(String aCquirertxid) {
		ACquirertxid = aCquirertxid;
	}

	public String getUpiVA() {
		return upiVA;
	}

	public void setUpiVA(String upiVA) {
		this.upiVA = upiVA;
	}

	public String getUpiQR() {
		return upiQR;
	}

	public void setUpiQR(String upiQR) {
		this.upiQR = upiQR;
	}

	public String getChecksum() {
		return checksum;
	}

	public void setChecksum(String checksum) {
		this.checksum = checksum;
	}

	public String getCardExp() {
		return cardExp;
	}

	public void setCardExp(String cardExp) {
		this.cardExp = cardExp;
	}

	public String getIv() {
		return iv;
	}

	public void setIv(String iv) {
		this.iv = iv;
	}

	public String getEncryptionKey() {
		return encryptionKey;
	}

	public void setEncryptionKey(String encryptionKey) {
		this.encryptionKey = encryptionKey;
	}

	public String getMerchantKey() {
		return merchantKey;
	}

	public void setMerchantKey(String merchantKey) {
		this.merchantKey = merchantKey;
	}

	public String getCardHname() {
		return cardHname;
	}

	public void setCardHname(String cardHname) {
		this.cardHname = cardHname;
	}

	public String getCardNo() {
		return cardNo;
	}

	public void setCardNo(String cardNo) {
		this.cardNo = cardNo;
	}

	public String getCvv() {
		return cvv;
	}

	public void setCvv(String cvv) {
		this.cvv = cvv;
	}

	public String getExpMonth() {
		return expMonth;
	}

	public void setExpMonth(String expMonth) {
		this.expMonth = expMonth;
	}

	public String getExpYear() {
		return expYear;
	}

	public void setExpYear(String expYear) {
		this.expYear = expYear;
	}

	public String getBankCode() {
		return bankCode;
	}

	public void setBankCode(String bankCode) {
		this.bankCode = bankCode;
	}

	public String getSalt() {
		return salt;
	}

	public void setSalt(String salt) {
		this.salt = salt;
	}

	public String getTxnid() {
		return txnid;
	}

	public void setTxnid(String txnid) {
		this.txnid = txnid;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public String getProductinfo() {
		return productinfo;
	}

	public void setProductinfo(String productinfo) {
		this.productinfo = productinfo;
	}

	public String getFirstname() {
		return firstname;
	}

	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getSurl() {
		return surl;
	}

	public void setSurl(String surl) {
		this.surl = surl;
	}

	public String getFurl() {
		return furl;
	}

	public void setFurl(String furl) {
		this.furl = furl;
	}

	public String getHash() {
		return hash;
	}

	public void setHash(String hash) {
		this.hash = hash;
	}

	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public String getOrderAmount() {
		return orderAmount;
	}

	public void setOrderAmount(String orderAmount) {
		this.orderAmount = orderAmount;
	}

	public String getOrderCurrency() {
		return orderCurrency;
	}

	public void setOrderCurrency(String orderCurrency) {
		this.orderCurrency = orderCurrency;
	}

	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public String getCustomerEmail() {
		return customerEmail;
	}

	public void setCustomerEmail(String customerEmail) {
		this.customerEmail = customerEmail;
	}

	public String getCustomerPhone() {
		return customerPhone;
	}

	public void setCustomerPhone(String customerPhone) {
		this.customerPhone = customerPhone;
	}

	public String getPaymentOption() {
		return paymentOption;
	}

	public void setPaymentOption(String paymentOption) {
		this.paymentOption = paymentOption;
	}

	public String getPaymentCode() {
		return paymentCode;
	}

	public void setPaymentCode(String paymentCode) {
		this.paymentCode = paymentCode;
	}

	public String getReferenceId() {
		return referenceId;
	}

	public void setReferenceId(String referenceId) {
		this.referenceId = referenceId;
	}

	public String getTxStatus() {
		return txStatus;
	}

	public void setTxStatus(String txStatus) {
		this.txStatus = txStatus;
	}

	public String getPaymentMode() {
		return paymentMode;
	}

	public void setPaymentMode(String paymentMode) {
		this.paymentMode = paymentMode;
	}

	public String getTxMsg() {
		return txMsg;
	}

	public void setTxMsg(String txMsg) {
		this.txMsg = txMsg;
	}

	public String getSignature() {
		return signature;
	}

	public void setSignature(String signature) {
		this.signature = signature;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getRefundId() {
		return refundId;
	}

	public void setRefundId(String refundId) {
		this.refundId = refundId;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getOrderStatus() {
		return orderStatus;
	}

	public void setOrderStatus(String orderStatus) {
		this.orderStatus = orderStatus;
	}

	@Override
	public String toString() {
		return "Transaction [appId=" + appId + ", orderId=" + orderId + ", orderAmount=" + orderAmount
				+ ", orderCurrency=" + orderCurrency + ", customerName=" + customerName + ", customerEmail="
				+ customerEmail + ", customerPhone=" + customerPhone + ", paymentOption=" + paymentOption
				+ ", paymentCode=" + paymentCode + ", referenceId=" + referenceId + ", txStatus=" + txStatus
				+ ", paymentMode=" + paymentMode + ", txMsg=" + txMsg + ", signature=" + signature + ", message="
				+ message + ", refundId=" + refundId + ", status=" + status + ", orderStatus=" + orderStatus
				+ ", merchantKey=" + merchantKey + ", bankCode=" + bankCode + ", encryptionKey=" + encryptionKey
				+ ", txnid=" + txnid + ", amount=" + amount + ", productinfo=" + productinfo + ", firstname="
				+ firstname + ", phone=" + phone + ", email=" + email + ", surl=" + surl + ", furl=" + furl + ", hash="
				+ hash + ", salt=" + salt + ", checksum=" + checksum + ", cardNo=" + cardNo + ", cvv=" + cvv
				+ ", expMonth=" + expMonth + ", expYear=" + expYear + ", cardHname=" + cardHname + ", iv=" + iv
				+ ", cardExp=" + cardExp + ", upiVA=" + upiVA + ", upiQR=" + upiQR + ", acqrefundid=" + acqrefundid
				+ ", error_Message=" + error_Message + ", ACquirertxid=" + ACquirertxid + "]";
	}

	
//	@Override
//	public String toString() {
//		return "Transaction [appId=" + appId + ", orderId=" + orderId + ", orderAmount=" + orderAmount
//				+ ", orderCurrency=" + orderCurrency + ", customerName=" + customerName + ", customerEmail="
//				+ customerEmail + ", customerPhone=" + customerPhone + ", paymentOption=" + paymentOption
//				+ ", paymentCode=" + paymentCode + ", referenceId=" + referenceId + ", txStatus=" + txStatus
//				+ ", paymentMode=" + paymentMode + ", txMsg=" + txMsg + ", signature=" + signature + ", message="
//				+ message + ", refundId=" + refundId + ", status=" + status + ", orderStatus=" + orderStatus
//				+ ", merchantKey=" + merchantKey + ", bankCode=" + bankCode + ", encryptionKey=" + encryptionKey
//				+ ", txnid=" + txnid + ", amount=" + amount + ", productinfo=" + productinfo + ", firstname="
//				+ firstname + ", phone=" + phone + ", email=" + email + ", surl=" + surl + ", furl=" + furl + ", hash="
//				+ hash + ", salt=" + salt + ", checksum=" + checksum + ", cardNo=" + cardNo + ", cvv=" + cvv
//				+ ", expMonth=" + expMonth + ", expYear=" + expYear + ", cardHname=" + cardHname + ", iv=" + iv
//				+ ", cardExp=" + cardExp + ", upiVA=" + upiVA + ", upiQR=" + upiQR + "]";
//	}

}
