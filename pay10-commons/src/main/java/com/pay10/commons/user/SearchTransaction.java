package com.pay10.commons.user;

import java.io.Serializable;
import java.math.BigInteger;

/**
 * @author MMAD
 *
 */
public class SearchTransaction implements Serializable {

	private static final long serialVersionUID = 5899705456765089877L;

	private BigInteger transactionId;
	private String payId;
	private String merchant;
	private String customerEmail;
	private String customerPhone;
	private String txnType;
	private String paymentType;
	private String status;
	private String amount;
	private String totalAmount;
	private String orderId;
	private String mopType;
	private String pgRefNum;
	private String tDate;
	private String custName;
	private String rrn;
	private String acqId;
	private String ipayResponseMessage;
	private String acquirerTxnMessage;
	private String responseCode;
	private String cardNum;
	private String cardMask;
	private String refund_txn_id;
	private String acquirer;
	private String arn;
	private String issuerBank;
	private String requestDate;

	
	public String getAcquirer() {
		return acquirer;
	}

	public void setAcquirer(String acquirer) {
		this.acquirer = acquirer;
	}

	public String getArn() {
		return arn;
	}

	public void setArn(String arn) {
		this.arn = arn;
	}

	public String getIssuerBank() {
		return issuerBank;
	}

	public void setIssuerBank(String issuerBank) {
		this.issuerBank = issuerBank;
	}

	public String getCustName() {
		return custName;
	}

	public void setCustName(String custName) {
		this.custName = custName;
	}

	public String getCardMask() {
		return cardMask;
	}

	public void setCardMask(String cardMask) {
		this.cardMask = cardMask;
	}

	public String gettDate() {
		return tDate;
	}

	public void settDate(String tDate) {
		this.tDate = tDate;
	}

	public SearchTransaction() {

	}

	public BigInteger getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(BigInteger transactionId) {
		this.transactionId = transactionId;
	}

	public String getPayId() {
		return payId;
	}

	public void setPayId(String payId) {
		this.payId = payId;
	}

	public String getMerchant() {
		return merchant;
	}

	public void setMerchant(String merchant) {
		this.merchant = merchant;
	}

	public String getTxnType() {
		return txnType;
	}

	public void setTxnType(String txnType) {
		this.txnType = txnType;
	}



	public String getPaymentType() {
		return paymentType;
	}

	public void setPaymentType(String paymentType) {
		this.paymentType = paymentType;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public String getMopType() {
		return mopType;
	}

	public void setMopType(String mopType) {
		this.mopType = mopType;
	}

	public String getPgRefNum() {
		return pgRefNum;
	}

	public void setPgRefNum(String pgRefNum) {
		this.pgRefNum = pgRefNum;
	}

	public String getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(String totalAmount) {
		this.totalAmount = totalAmount;
	}

	public String getRrn() {
		return rrn;
	}

	public void setRrn(String rrn) {
		this.rrn = rrn;
	}

	public String getAcqId() {
		return acqId;
	}

	public void setAcqId(String acqId) {
		this.acqId = acqId;
	}

	public String getIpayResponseMessage() {
		return ipayResponseMessage;
	}

	public void setIpayResponseMessage(String ipayResponseMessage) {
		this.ipayResponseMessage = ipayResponseMessage;
	}

	

	public String getAcquirerTxnMessage() {
		return acquirerTxnMessage;
	}

	public void setAcquirerTxnMessage(String acquirerTxnMessage) {
		this.acquirerTxnMessage = acquirerTxnMessage;
	}

	public String getResponseCode() {
		return responseCode;
	}

	public void setResponseCode(String responseCode) {
		this.responseCode = responseCode;
	}

	public String getCardNum() {
		return cardNum;
	}

	public void setCardNum(String cardNum) {
		this.cardNum = cardNum;
	}

	public String getRefund_txn_id() {
		return refund_txn_id;
	}

	public void setRefund_txn_id(String refund_txn_id) {
		this.refund_txn_id = refund_txn_id;
	}

	public String getRequestDate() {
		return requestDate;
	}

	public void setRequestDate(String requestDate) {
		this.requestDate = requestDate;
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
	

}
