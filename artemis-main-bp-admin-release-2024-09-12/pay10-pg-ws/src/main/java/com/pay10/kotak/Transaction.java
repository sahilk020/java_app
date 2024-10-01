package com.pay10.kotak;

import org.springframework.stereotype.Service;

import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.TransactionType;

@Service("kotakTransaction")
public class Transaction {

	private String cardNumber;
	private String expiryDate;
	private String cvv;
	private String txnType;
	private String responseCode;
	private String acqId;
	private String authCode;
	private String hash;
	private String terminalId;
	private String amount;
	private String maskedCardNo;
	private String txnRefNo;
	private String message;
	private String merchantId;
	private String orderInfo;
	private String cardType;
	private String bacthNo;
	private String status;

	private String messagCode;
	private String dateAndTime;
	// private String merchantId;
	private String merchantReference;
	// private String amount;
	private String authStatus;
	private String bankReferenceNumber;
	private String fUP1;
	private String fUP2;
	private String fUP3;
	private String Checksum;
	private String responseMessage;

	public String getResponseMessage() {
		return responseMessage;
	}

	public void setResponseMessage(String responseMessage) {
		this.responseMessage = responseMessage;
	}

	public String getMessagCode() {
		return messagCode;
	}

	public void setMessagCode(String messagCode) {
		this.messagCode = messagCode;
	}

	public String getDateAndTime() {
		return dateAndTime;
	}

	public void setDateAndTime(String dateAndTime) {
		this.dateAndTime = dateAndTime;
	}

	public String getMerchantReference() {
		return merchantReference;
	}

	public void setMerchantReference(String merchantReference) {
		this.merchantReference = merchantReference;
	}

	public String getAuthStatus() {
		return authStatus;
	}

	public void setAuthStatus(String authStatus) {
		this.authStatus = authStatus;
	}

	public String getBankReferenceNumber() {
		return bankReferenceNumber;
	}

	public void setBankReferenceNumber(String bankReferenceNumber) {
		this.bankReferenceNumber = bankReferenceNumber;
	}

	public String getfUP1() {
		return fUP1;
	}

	public void setfUP1(String fUP1) {
		this.fUP1 = fUP1;
	}

	public String getfUP2() {
		return fUP2;
	}

	public void setfUP2(String fUP2) {
		this.fUP2 = fUP2;
	}

	public String getfUP3() {
		return fUP3;
	}

	public void setfUP3(String fUP3) {
		this.fUP3 = fUP3;
	}

	public String getChecksum() {
		return Checksum;
	}

	public void setChecksum(String checksum) {
		Checksum = checksum;
	}

	public void setEnrollment(Fields fields) {
		setCardDetails(fields);
		setTxnType(fields);
	}

	public void setRefund(Fields fields) {
		setTxnType(fields);
	}

	private void setCardDetails(Fields fields) {
		setCardNumber(fields.get(FieldType.CARD_NUMBER.getName()));
		setCvv(fields.get(FieldType.CVV.getName()));
		String expDate = fields.get(FieldType.CARD_EXP_DT.getName());
		String expYear = (expDate.substring(4, 6));
		String expMonth = (expDate.substring(0, 2));
		setExpiryDate(expMonth.concat(expYear));
	}

	private void setTxnType(Fields fields) {
		String txnType = fields.get(FieldType.TXNTYPE.getName());
		if (txnType.equals(TransactionType.SALE.toString())) {
			setTxnType("01");
		} else if (txnType.equals(TransactionType.REFUND.toString())) {
			setTxnType("04");
		} else if (txnType.equals(TransactionType.ENQUIRY.toString())) {
			setTxnType("05");
		} else {

		}
	}

	public String getCardNumber() {
		return cardNumber;
	}

	public void setCardNumber(String cardNumber) {
		this.cardNumber = cardNumber;
	}

	public String getExpiryDate() {
		return expiryDate;
	}

	public void setExpiryDate(String expiryDate) {
		this.expiryDate = expiryDate;
	}

	public String getCvv() {
		return cvv;
	}

	public void setCvv(String cvv) {
		this.cvv = cvv;
	}

	public String getTxnType() {
		return txnType;
	}

	public void setTxnType(String txnType) {
		this.txnType = txnType;
	}

	public String getResponseCode() {
		return responseCode;
	}

	public void setResponseCode(String responseCode) {
		this.responseCode = responseCode;
	}

	public String getAuthCode() {
		return authCode;
	}

	public void setAuthCode(String authCode) {
		this.authCode = authCode;
	}

	public String getAcqId() {
		return acqId;
	}

	public void setAcqId(String acqId) {
		this.acqId = acqId;
	}

	public String getHash() {
		return hash;
	}

	public void setHash(String hash) {
		this.hash = hash;
	}

	public String getTerminalId() {
		return terminalId;
	}

	public void setTerminalId(String terminalId) {
		this.terminalId = terminalId;
	}

	public String getMaskedCardNo() {
		return maskedCardNo;
	}

	public void setMaskedCardNo(String maskedCardNo) {
		this.maskedCardNo = maskedCardNo;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getTxnRefNo() {
		return txnRefNo;
	}

	public void setTxnRefNo(String txnRefNo) {
		this.txnRefNo = txnRefNo;
	}

	public String getBacthNo() {
		return bacthNo;
	}

	public void setBacthNo(String bacthNo) {
		this.bacthNo = bacthNo;
	}

	public String getCardType() {
		return cardType;
	}

	public void setCardType(String cardType) {
		this.cardType = cardType;
	}

	public String getOrderInfo() {
		return orderInfo;
	}

	public void setOrderInfo(String orderInfo) {
		this.orderInfo = orderInfo;
	}

	public String getMerchantId() {
		return merchantId;
	}

	public void setMerchantId(String merchantId) {
		this.merchantId = merchantId;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	@Override
	public String toString() {
		return "Transaction [cardNumber=" + cardNumber + ", expiryDate=" + expiryDate + ", cvv=" + cvv + ", txnType="
				+ txnType + ", responseCode=" + responseCode + ", acqId=" + acqId + ", authCode=" + authCode + ", hash="
				+ hash + ", terminalId=" + terminalId + ", amount=" + amount + ", maskedCardNo=" + maskedCardNo
				+ ", txnRefNo=" + txnRefNo + ", message=" + message + ", merchantId=" + merchantId + ", orderInfo="
				+ orderInfo + ", cardType=" + cardType + ", bacthNo=" + bacthNo + ", status=" + status + ", messagCode="
				+ messagCode + ", dateAndTime=" + dateAndTime + ", merchantReference=" + merchantReference
				+ ", authStatus=" + authStatus + ", bankReferenceNumber=" + bankReferenceNumber + ", fUP1=" + fUP1
				+ ", fUP2=" + fUP2 + ", fUP3=" + fUP3 + ", Checksum=" + Checksum + ", responseMessage="
				+ responseMessage + "]";
	}



}
