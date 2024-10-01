package com.pay10.sbi.upi;

import org.springframework.stereotype.Service;

@Service("sbiUpiTransaction")
public class Transaction {

	private String status;
	private String statusDesc;
	private String clientSecret;
	private String pspRespRefNo;
	private String addInfo1;
	private String safetyNetFlag;
	private String accessToken;
	private String tokenType;
	private String refreshToken;
	private String expireIn;
	private String upiTransRefNo;
	private String npciTransId;
	private String custRefNo;
	private String txnAuthDate;
	private String addInfo2;
	private String payerVPA;
	private String payeeVPA;
	private String amount;
	private String responseCode;
	private String txnType;
	private String errCode;
	private String txnNote;
	private String approvalNumber;
	private String clientId;
	private int accessTokenExpire;
	private int refreshTokenExpire;
	private String acquireName;
	private String refreshTokenError;
	private String payerName;

	///
	private String response;
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
	private String collectCode;

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

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getStatusDesc() {
		return statusDesc;
	}

	public void setStatusDesc(String statusDesc) {
		this.statusDesc = statusDesc;
	}

	public String getClientSecret() {
		return clientSecret;
	}

	public void setClientSecret(String clientSecret) {
		this.clientSecret = clientSecret;
	}

	public String getPspRespRefNo() {
		return pspRespRefNo;
	}

	public void setPspRespRefNo(String pspRespRefNo) {
		this.pspRespRefNo = pspRespRefNo;
	}

	public String getAddInfo1() {
		return addInfo1;
	}

	public void setAddInfo1(String addInfo1) {
		this.addInfo1 = addInfo1;
	}

	public String getSafetyNetFlag() {
		return safetyNetFlag;
	}

	public void setSafetyNetFlag(String safetyNetFlag) {
		this.safetyNetFlag = safetyNetFlag;
	}

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	public String getTokenType() {
		return tokenType;
	}

	public void setTokenType(String tokenType) {
		this.tokenType = tokenType;
	}

	public String getRefreshToken() {
		return refreshToken;
	}

	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}

	public String getExpireIn() {
		return expireIn;
	}

	public void setExpireIn(String expiresIn) {
		this.expireIn = expiresIn;
	}

	public String getUpiTransRefNo() {
		return upiTransRefNo;
	}

	public void setUpiTransRefNo(String upiTransRefNo) {
		this.upiTransRefNo = upiTransRefNo;
	}

	public String getNpciTransId() {
		return npciTransId;
	}

	public void setNpciTransId(String npciTransId) {
		this.npciTransId = npciTransId;
	}

	public String getCustRefNo() {
		return custRefNo;
	}

	public void setCustRefNo(String custRefNo) {
		this.custRefNo = custRefNo;
	}

	public String getTxnAuthDate() {
		return txnAuthDate;
	}

	public void setTxnAuthDate(String txnAuthDate) {
		this.txnAuthDate = txnAuthDate;
	}

	public String getAddInfo2() {
		return addInfo2;
	}

	public void setAddInfo2(String addInfo2) {
		this.addInfo2 = addInfo2;
	}

	public String getPayerVPA() {
		return payerVPA;
	}

	public void setPayerVPA(String payerVPA) {
		this.payerVPA = payerVPA;
	}

	public String getPayeeVPA() {
		return payeeVPA;
	}

	public void setPayeeVPA(String payeeVPA) {
		this.payeeVPA = payeeVPA;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public String getResponseCode() {
		return responseCode;
	}

	public void setResponseCode(String responseCode) {
		this.responseCode = responseCode;
	}

	public String getTxnType() {
		return txnType;
	}

	public void setTxnType(String txnType) {
		this.txnType = txnType;
	}

	public String getErrCode() {
		return errCode;
	}

	public void setErrCode(String errCode) {
		this.errCode = errCode;
	}

	public String getTxnNote() {
		return txnNote;
	}

	public void setTxnNote(String txnNote) {
		this.txnNote = txnNote;
	}

	public String getApprovalNumber() {
		return approvalNumber;
	}

	public void setApprovalNumber(String approvalNumber) {
		this.approvalNumber = approvalNumber;
	}

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public int getAccessTokenExpire() {
		return accessTokenExpire;
	}

	public void setAccessTokenExpire(int accessTokenExpire) {
		this.accessTokenExpire = accessTokenExpire;
	}

	public int getRefreshTokenExpire() {
		return refreshTokenExpire;
	}

	public void setRefreshTokenExpire(int refreshTokenExpire) {
		this.refreshTokenExpire = refreshTokenExpire;
	}

	public String getAcquireName() {
		return acquireName;
	}

	public void setAcquireName(String acquireName) {
		this.acquireName = acquireName;
	}

	public String getRefreshTokenError() {
		return refreshTokenError;
	}

	public void setRefreshTokenError(String refreshTokenError) {
		this.refreshTokenError = refreshTokenError;
	}

	public String getPayerName() {
		return payerName;
	}

	public void setPayerName(String payerName) {
		this.payerName = payerName;
	}

}
