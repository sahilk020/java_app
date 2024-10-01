package com.pay10.pinelabs;

import org.springframework.stereotype.Service;

@Service("pinelabsTransaction")
public class Transaction {

	String token;
	String responseCode;
	String responseStatus;
	String responseMsg;
	String merchantTxnId;
	String captureAmt;
	String txnAmt;
	String pineTxnId;
	String responseRefundAmt;
	String parentTxnStatus;
	String pineTxnStatus;
	String txnCompeletionDT;
	String acquirerName;
	String txnResponseMsg;
	String parentTxnMsg;
	String parentTxnCode;
	String paymentMode;
	String refundId;
	String ppcPinePGTxnStatus;
	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getResponseCode() {
		return responseCode;
	}

	public void setResponseCode(String responseCode) {
		this.responseCode = responseCode;
	}

	public String getResponseStatus() {
		return responseStatus;
	}

	public void setResponseStatus(String responseStatus) {
		this.responseStatus = responseStatus;
	}

	public String getResponseMsg() {
		return responseMsg;
	}

	public void setResponseMsg(String responseMsg) {
		this.responseMsg = responseMsg;
	}

	public String getMerchantTxnId() {
		return merchantTxnId;
	}

	public void setMerchantTxnId(String merchantTxnId) {
		this.merchantTxnId = merchantTxnId;
	}

	public String getCaptureAmt() {
		return captureAmt;
	}

	public void setCaptureAmt(String captureAmt) {
		this.captureAmt = captureAmt;
	}

	public String getPineTxnId() {
		return pineTxnId;
	}

	public void setPineTxnId(String pineTxnId) {
		this.pineTxnId = pineTxnId;
	}

	public String getTxnAmt() {
		return txnAmt;
	}

	public void setTxnAmt(String txnAmt) {
		this.txnAmt = txnAmt;
	}

	public String getAcquirerName() {
		return acquirerName;
	}

	public void setAcquirerName(String acquirerName) {
		this.acquirerName = acquirerName;
	}

	public String getResponseRefundAmt() {
		return responseRefundAmt;
	}

	public void setResponseRefundAmt(String responseRefundAmt) {
		this.responseRefundAmt = responseRefundAmt;
	}

	public String getParentTxnStatus() {
		return parentTxnStatus;
	}

	public void setParentTxnStatus(String parentTxnStatus) {
		this.parentTxnStatus = parentTxnStatus;
	}

	public String getPineTxnStatus() {
		return pineTxnStatus;
	}

	public void setPineTxnStatus(String pineTxnStatus) {
		this.pineTxnStatus = pineTxnStatus;
	}

	public String getTxnCompeletionDT() {
		return txnCompeletionDT;
	}

	public void setTxnCompeletionDT(String txnCompeletionDT) {
		this.txnCompeletionDT = txnCompeletionDT;
	}

	public String getTxnResponseMsg() {
		return txnResponseMsg;
	}

	public void setTxnResponseMsg(String txnResponseMsg) {
		this.txnResponseMsg = txnResponseMsg;
	}

	public String getParentTxnMsg() {
		return parentTxnMsg;
	}

	public void setParentTxnMsg(String parentTxnMsg) {
		this.parentTxnMsg = parentTxnMsg;
	}

	public String getParentTxnCode() {
		return parentTxnCode;
	}

	public void setParentTxnCode(String parentTxnCode) {
		this.parentTxnCode = parentTxnCode;
	}

	public String getPaymentMode() {
		return paymentMode;
	}

	public void setPaymentMode(String paymentMode) {
		this.paymentMode = paymentMode;
	}

	public String getRefundId() {
		return refundId;
	}

	public void setRefundId(String refundId) {
		this.refundId = refundId;
	}

	public String getPpcPinePGTxnStatus() {
		return ppcPinePGTxnStatus;
	}

	public void setPpcPinePGTxnStatus(String ppcPinePGTxnStatus) {
		this.ppcPinePGTxnStatus = ppcPinePGTxnStatus;
	}

}
