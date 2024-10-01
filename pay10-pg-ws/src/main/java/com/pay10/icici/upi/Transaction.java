package com.pay10.icici.upi;

import org.springframework.stereotype.Service;

@Service("iciciUpiTransaction")
public class Transaction {

	private String responseCode;
	private String responseStatus;
	private String responseSuccess;
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
	private String merchantVPA;

	private String merchantId;
	private String subMerchantId;
	private String terminalId;
	private String merchantTranId;
	private String enquiryStatus;

	public String getResponseSuccess() {
		return responseSuccess;
	}

	public void setResponseSuccess(String responseSuccess) {
		this.responseSuccess = responseSuccess;
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

	public String getMerchantId() {
		return merchantId;
	}

	public void setMerchantId(String merchantId) {
		this.merchantId = merchantId;
	}

	public String getSubMerchantId() {
		return subMerchantId;
	}

	public void setSubMerchantId(String subMerchantId) {
		this.subMerchantId = subMerchantId;
	}

	public String getTerminalId() {
		return terminalId;
	}

	public void setTerminalId(String terminalId) {
		this.terminalId = terminalId;
	}

	public String getMerchantTranId() {
		return merchantTranId;
	}

	public void setMerchantTranId(String merchantTranId) {
		this.merchantTranId = merchantTranId;
	}

	public String getEnquiryStatus() {
		return enquiryStatus;
	}

	public void setEnquiryStatus(String enquiryStatus) {
		this.enquiryStatus = enquiryStatus;
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

}
