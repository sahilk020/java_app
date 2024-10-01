package com.pay10.commons.user;

import com.pay10.commons.util.AccountEntryType;
import com.pay10.commons.util.BeneficiaryTypes;
import com.pay10.commons.util.SettlementTransactionType;

public class NodalTransactions {

	private Long id;
	
	private String status;
	private String requestedBy;
	private String processedBy;
	private String acquirer;
	private String amount;
	private String txnId;
	private String rrn;
	private String oid;
	private String customerId;
	private String srcAccNo;
	private String beneAccNo;
	private String currencyCode;
	private String responseCode;
	private String createdDate;
	private String pgRespCode;
	private String paymentType;
	private String comments;
	private String beneficiaryName;
	private String beneficiaryCode;
	private String merchantBusinessName;
	private String orderId;
	private String mobile;
	private String email;
	private String payId;
	private String responseMessage;
	private String beneIfscCode;
	private String beneMerchantProvidedName;
	private String beneMerchantProvidedCode;
	private SettlementTransactionType txnType;
	private AccountEntryType accountEntryType;
	private BeneficiaryTypes beneType;
	
	public NodalTransactions(String beneficiaryCode, String beneAccountNumber, String paymentType, String amount, String comments) {
		this.beneficiaryCode = beneficiaryCode;
		this.beneAccNo = beneAccountNumber;
		this.paymentType = paymentType;
		this.amount = amount;
		this.comments = comments;
	}
	
	public NodalTransactions() {
	}


	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	public String getRequestedBy() {
		return requestedBy;
	}
	public void setRequestedBy(String requestedBy) {
		this.requestedBy = requestedBy;
	}
	public String getProcessedBy() {
		return processedBy;
	}
	public void setProcessedBy(String processedBy) {
		this.processedBy = processedBy;
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
	public String getTxnId() {
		return txnId;
	}
	public void setTxnId(String txnId) {
		this.txnId = txnId;
	}
	public String getRrn() {
		return rrn;
	}
	public void setRrn(String rrn) {
		this.rrn = rrn;
	}

	public String getOid() {
		return oid;
	}
	public void setOid(String oid) {
		this.oid = oid;
	}
	public String getCustomerId() {
		return customerId;
	}
	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}
	public String getSrcAccNo() {
		return srcAccNo;
	}
	public void setSrcAccNo(String srcAccNo) {
		this.srcAccNo = srcAccNo;
	}
	public String getBeneAccNo() {
		return beneAccNo;
	}
	public void setBeneAccNo(String beneAccNo) {
		this.beneAccNo = beneAccNo;
	}
	public String getCurrencyCode() {
		return currencyCode;
	}
	public void setCurrencyCode(String currencyCode) {
		this.currencyCode = currencyCode;
	}
	public String getResponseCode() {
		return responseCode;
	}
	public void setResponseCode(String responseCode) {
		this.responseCode = responseCode;
	}
	public String getPayId() {
		return payId;
	}
	public void setPayId(String payId) {
		this.payId = payId;
	}
	public String getCreatedDate() {
		return createdDate;
	}
	public String getOrderId() {
		return orderId;
	}
	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}
	public void setCreatedDate(String createdDate) {
		this.createdDate = createdDate;
	}
	public String getPaymentType() {
		return paymentType;
	}
	public void setPaymentType(String paymentType) {
		this.paymentType = paymentType;
	}
	public String getPgRespCode() {
		return pgRespCode;
	}
	public void setPgRespCode(String pgRespCode) {
		this.pgRespCode = pgRespCode;
	}
	public String getComments() {
		return comments;
	}
	public void setComments(String comments) {
		this.comments = comments;
	}
	public String getAcquirer() {
		return acquirer;
	}
	public void setAcquirer(String acquirer) {
		this.acquirer = acquirer;
	}
	public String getBeneficiaryName() {
		return beneficiaryName;
	}
	public void setBeneficiaryName(String beneficiaryName) {
		this.beneficiaryName = beneficiaryName;
	}
	public String getBeneficiaryCode() {
		return beneficiaryCode;
	}
	public void setBeneficiaryCode(String beneficiaryCode) {
		this.beneficiaryCode = beneficiaryCode;
	}
	public String getMerchantBusinessName() {
		return merchantBusinessName;
	}
	public void setMerchantBusinessName(String merchantBusinessName) {
		this.merchantBusinessName = merchantBusinessName;
	}
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getResponseMessage() {
		return responseMessage;
	}
	public void setResponseMessage(String responseMessage) {
		this.responseMessage = responseMessage;
	}

	public AccountEntryType getAccountEntryType() {
		return accountEntryType;
	}
	public void setAccountEntryType(AccountEntryType accountEntryType) {
		this.accountEntryType = accountEntryType;
	}
	public SettlementTransactionType getTxnType() {
		return txnType;
	}
	public void setTxnType(SettlementTransactionType txnType) {
		this.txnType = txnType;
	}

	public String getBeneIfscCode() {
		return beneIfscCode;
	}

	public void setBeneIfscCode(String beneIfscCode) {
		this.beneIfscCode = beneIfscCode;
	}

	public String getBeneMerchantProvidedName() {
		return beneMerchantProvidedName;
	}

	public void setBeneMerchantProvidedName(String beneMerchantProvidedName) {
		this.beneMerchantProvidedName = beneMerchantProvidedName;
	}

	public String getBeneMerchantProvidedCode() {
		return beneMerchantProvidedCode;
	}

	public void setBeneMerchantProvidedCode(String beneMerchantProvidedCode) {
		this.beneMerchantProvidedCode = beneMerchantProvidedCode;
	}

	public BeneficiaryTypes getBeneType() {
		return beneType;
	}

	public void setBeneType(BeneficiaryTypes beneType) {
		this.beneType = beneType;
	}

	@Override
	public String toString() {
		return "NodalTransactions [id=" + id + ", status=" + status + ", requestedBy=" + requestedBy + ", processedBy="
				+ processedBy + ", acquirer=" + acquirer + ", amount=" + amount + ", txnId=" + txnId + ", rrn=" + rrn
				+ ", oid=" + oid + ", customerId=" + customerId + ", srcAccNo=" + srcAccNo + ", beneAccNo=" + beneAccNo
				+ ", currencyCode=" + currencyCode + ", responseCode=" + responseCode + ", createdDate=" + createdDate
				+ ", pgRespCode=" + pgRespCode + ", paymentType=" + paymentType + ", comments=" + comments
				+ ", beneficiaryName=" + beneficiaryName + ", beneficiaryCode=" + beneficiaryCode
				+ ", merchantBusinessName=" + merchantBusinessName + ", orderId=" + orderId + ", mobile=" + mobile
				+ ", email=" + email + ", payId=" + payId + ", responseMessage=" + responseMessage + ", beneIfscCode="
				+ beneIfscCode + ", beneMerchantProvidedName=" + beneMerchantProvidedName
				+ ", beneMerchantProvidedCode=" + beneMerchantProvidedCode + ", txnType=" + txnType
				+ ", accountEntryType=" + accountEntryType + "]";
	}
	
}
