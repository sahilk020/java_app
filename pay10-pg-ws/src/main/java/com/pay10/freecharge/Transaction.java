package com.pay10.freecharge;

import org.springframework.stereotype.Service;

import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.TxnType;

@Service("freeChargeTransaction")
public class Transaction {

	private String amount;
	private String checksum;
	private String channel;
	private String errorCode;
	private String errorMessage;
	private String furl;
	private String merchantId;
	private String merchantTxnId;
	private String refundMerchantTxnId;
	private String refundTxnId;
	private String refundAmount;
	private String refundedAmount;
	private String surl;
	private String status;
	private String txnId;
	private String txnType;
	private String txnKey;

	public void setEnrollment(Fields fields) {
		setMerchantInformation(fields);
	}

	public void setSale(Fields fields) {
		setMerchantInformation(fields);
	}

	public void setRefund(Fields fields) {
		setMerchantInformation(fields);
	}

	public void setStatusEnquiry(Fields fields) {
		setMerchantInformation(fields);
		setMerchantTxnId(fields.get(FieldType.ORDER_ID.getName()));
		if (fields.get(FieldType.ORIG_TXNTYPE.getName()).equals(TxnType.SALE.getName())) {
			setTxnType("CUSTOMER_PAYMENT");
		}
		if (fields.get(FieldType.ORIG_TXNTYPE.getName()).equals(TxnType.REFUND.getName())) {
			setTxnType("CANCELLATION_REFUND");
		}

	}

	private void setMerchantInformation(Fields fields) {
		setMerchantId(fields.get(FieldType.MERCHANT_ID.getName()));
		setTxnKey(fields.get(FieldType.TXN_KEY.getName()));
	}

	public String getMerchantId() {
		return merchantId;
	}

	public void setMerchantId(String merchantId) {
		this.merchantId = merchantId;
	}

	public String getMerchantTxnId() {
		return merchantTxnId;
	}

	public void setMerchantTxnId(String merchantTxnId) {
		this.merchantTxnId = merchantTxnId;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public String getFurl() {
		return furl;
	}

	public void setFurl(String furl) {
		this.furl = furl;
	}

	public String getSurl() {
		return surl;
	}

	public void setSurl(String surl) {
		this.surl = surl;
	}

	public String getChecksum() {
		return checksum;
	}

	public void setChecksum(String checksum) {
		this.checksum = checksum;
	}

	public String getChannel() {
		return channel;
	}

	public void setChannel(String channel) {
		this.channel = channel;
	}

	public String getTxnId() {
		return txnId;
	}

	public void setTxnId(String txnId) {
		this.txnId = txnId;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public String getRefundMerchantTxnId() {
		return refundMerchantTxnId;
	}

	public void setRefundMerchantTxnId(String refundMerchantTxnId) {
		this.refundMerchantTxnId = refundMerchantTxnId;
	}

	public String getRefundTxnId() {
		return refundTxnId;
	}

	public void setRefundTxnId(String refundTxnId) {
		this.refundTxnId = refundTxnId;
	}

	public String getRefundedAmount() {
		return refundedAmount;
	}

	public void setRefundedAmount(String refundedAmount) {
		this.refundedAmount = refundedAmount;
	}

	public String getTxnType() {
		return txnType;
	}

	public void setTxnType(String txnType) {
		this.txnType = txnType;
	}

	public String getRefundAmount() {
		return refundAmount;
	}

	public void setRefundAmount(String refundAmount) {
		this.refundAmount = refundAmount;
	}

	public String getTxnKey() {
		return txnKey;
	}

	public void setTxnKey(String txnKey) {
		this.txnKey = txnKey;
	}

}
