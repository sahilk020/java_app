package com.pay10.kotak.nb;

import org.springframework.stereotype.Service;

/**
 * @author VJ
 *
 */
@Service("kotakNBTransaction")
public class Transaction {

	private String response;
	private String status;
	private String result;
	private String acq_id;
	private String rrn;
	private String auth_code;
	private String responseMessage;
	private String dateTime;
	private String merchantVpa;
	private String transactionMessage;
	private String payeeApprovalNum;
	private String ReferenceId;
	private String customerReference;
	private String transactionId;
	private String baseEncytData;

	private String merchantVPA;
	private String collectCode;
	private String hashValue;
	private String encryptedValue;

	private String messagCode;
	private String dateAndTime;
	private String merchantId;
	private String merchantReference;
	private String amount;
	private String authStatus;
	private String bankReferenceNumber;
	private String fUP1;
	private String fUP2;
	private String fUP3;
	private String Checksum;
	


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

	public String getMerchantId() {
		return merchantId;
	}

	public void setMerchantId(String merchantId) {
		this.merchantId = merchantId;
	}

	public String getMerchantReference() {
		return merchantReference;
	}

	public void setMerchantReference(String merchantReference) {
		this.merchantReference = merchantReference;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
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

	public String getCollectCode() {
		return collectCode;
	}

	public void setCollectCode(String collectCode) {
		this.collectCode = collectCode;
	}

	public String getMerchantVPA() {
		return merchantVPA;
	}

	public void setMerchantVPA(String merchantVPA) {
		this.merchantVPA = merchantVPA;
	}

	public String getCustomerReference() {
		return customerReference;
	}

	public void setCustomerReference(String customerReference) {
		this.customerReference = customerReference;
	}

	public String getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	private String address;

	public String getReferenceId() {
		return ReferenceId;
	}

	public void setReferenceId(String referenceId) {
		ReferenceId = referenceId;
	}

	public String getPayeeApprovalNum() {
		return payeeApprovalNum;
	}

	public void setPayeeApprovalNum(String payeeApprovalNum) {
		this.payeeApprovalNum = payeeApprovalNum;
	}

	public String getTransactionMessage() {
		return transactionMessage;
	}

	public void setTransactionMessage(String transactionMessage) {
		this.transactionMessage = transactionMessage;
	}

	public String getAcq_id() {
		return acq_id;
	}

	public void setAcq_id(String acq_id) {
		this.acq_id = acq_id;
	}

	public String getRrn() {
		return rrn;
	}

	public void setRrn(String rrn) {
		this.rrn = rrn;
	}

	public String getAuth_code() {
		return auth_code;
	}

	public void setAuth_code(String auth_code) {
		this.auth_code = auth_code;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public String getResponse() {
		return response;
	}

	public void setResponse(String response) {
		this.response = response;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getResponseMessage() {
		return responseMessage;
	}

	public void setResponseMessage(String responseMessage) {
		this.responseMessage = responseMessage;
	}

	public String getDateTime() {
		return dateTime;
	}

	public void setDateTime(String dateTime) {
		this.dateTime = dateTime;
	}

	public String getMerchantVpa() {
		return merchantVpa;
	}

	public void setMerchantVpa(String merchantVpa) {
		this.merchantVpa = merchantVpa;
	}

	public String getHashValue() {
		return hashValue;
	}

	public void setHashValue(String hashValue) {
		this.hashValue = hashValue;
	}

	public String getEncryptedValue() {
		return encryptedValue;
	}

	public void setEncryptedValue(String encryptedValue) {
		this.encryptedValue = encryptedValue;
	}

	public String getBaseEncytData() {
		return baseEncytData;
	}

	public void setBaseEncytData(String baseEncytData) {
		this.baseEncytData = baseEncytData;
	}

	@Override
	public String toString() {
		return "Transaction [response=" + response + ", status=" + status + ", result=" + result + ", acq_id=" + acq_id
				+ ", rrn=" + rrn + ", auth_code=" + auth_code + ", responseMessage=" + responseMessage + ", dateTime="
				+ dateTime + ", merchantVpa=" + merchantVpa + ", transactionMessage=" + transactionMessage
				+ ", payeeApprovalNum=" + payeeApprovalNum + ", ReferenceId=" + ReferenceId + ", customerReference="
				+ customerReference + ", transactionId=" + transactionId + ", baseEncytData=" + baseEncytData
				+ ", merchantVPA=" + merchantVPA + ", collectCode=" + collectCode + ", hashValue=" + hashValue
				+ ", encryptedValue=" + encryptedValue + ", messagCode=" + messagCode + ", dateAndTime=" + dateAndTime
				+ ", merchantId=" + merchantId + ", merchantReference=" + merchantReference + ", amount=" + amount
				+ ", authStatus=" + authStatus + ", bankReferenceNumber=" + bankReferenceNumber + ", fUP1=" + fUP1
				+ ", fUP2=" + fUP2 + ", fUP3=" + fUP3 + ", Checksum=" + Checksum + ", address=" + address + "]";
	}

}
