package com.pay10.commons.user;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;

import com.pay10.commons.dao.NodalAccountDetailsDao;
import com.pay10.commons.util.AccountStatus;
import com.pay10.commons.util.CurrencyTypes;
import com.pay10.commons.util.DateCreater;
import com.pay10.commons.util.NodalPaymentTypes;
import com.pay10.commons.util.SettlementTransactionType;
import com.pay10.commons.util.TransactionManager;

public class BeneficiaryAccounts {
	private String id;
	private String merchantProvidedId;
	private String merchantProvidedName;
	private String merchantPayId;
	private String merchantBusinessName;
	

	private AccountStatus status;
	private String createdDate;
	private String updatedDate;
	private String requestedBy;
	private String processedBy;

	private String custId;
	private String beneficiaryCd;
	private String srcAccountNo;
	private NodalPaymentTypes paymentType;
	private String beneName;
	private String beneType;
	private String beneExpiryDate;
	private CurrencyTypes currencyCd;
	private String transactionLimit;
	private String bankName;
	private String ifscCode;
	private String beneAccountNo;
	private String upiHandle;
	private String mobileNo;
	private String emailId;
	private String address1;
	private String address2;
	private String swiftCode;
	private String actions;
	private String acquirer;
	private String rrn;
	private String responseMessage;
	private String aadharNo;
	private String pgTxnMessage;
	private String pgRespCode;
	private String bankRequest;
	private String bankResponse;
	private String requestChannel;

	// TODO review
	public BeneficiaryAccounts(String custId, String srcAccountNo ,String merchantPayId, 
			String requestedByUser, String businessName, 
			String beneficiaryCd, String beneName,
			String bankName,String beneAccountNo, String beneExpiryDate,
			String mobileNo, String emailId, String address1, String address2, 
			String aadharNo, String ifscCode, String currencyCode) {
		
		this.custId = custId;
		this.srcAccountNo = srcAccountNo;
		this.id = TransactionManager.getNewTransactionId(); // Not required
		this.createdDate = DateCreater.formatDateForDb(new Date());
		this.updatedDate = DateCreater.formatDateForDb(new Date());
		this.status = AccountStatus.PENDING;
		this.merchantPayId = merchantPayId;
		currencyCode = currencyCode.toUpperCase();
		this.currencyCd = CurrencyTypes.getInstancefromCode(currencyCode);
		this.actions = SettlementTransactionType.ADD_BENEFICIARY.getName();
		
		this.requestedBy = requestedByUser;
		this.processedBy = requestedByUser;
		this.merchantProvidedId = beneficiaryCd;
		this.beneficiaryCd = beneficiaryCd;
		// this.paymentType = NodalPaymentTypes.getInstancefromCode(paymentType);
		this.beneName = beneName;
		this.merchantProvidedName = beneName;
		//this.beneType = beneType;
		this.bankName = bankName;
		this.ifscCode = ifscCode.toUpperCase();
		this.beneAccountNo = beneAccountNo;
		this.beneExpiryDate = beneExpiryDate;
		this.mobileNo = mobileNo;
		this.emailId = emailId;
		this.address1 = address1;
		this.address2 = address2;
		this.aadharNo = aadharNo;
		this.merchantBusinessName = businessName;
	}
	
	

	public BeneficiaryAccounts() {
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(String createdDate) {
		this.createdDate = createdDate;
	}

	public String getUpdatedDate() {
		return updatedDate;
	}

	public void setUpdatedDate(String updatedDate) {
		this.updatedDate = updatedDate;
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

	public AccountStatus getStatus() {
		return status;
	}

	public void setStatus(AccountStatus status) {
		this.status = status;
	}

	public String getCustId() {
		return custId;
	}

	public void setCustId(String custId) {
		this.custId = custId;
	}

	public String getBeneficiaryCd() {
		return beneficiaryCd;
	}

	public void setBeneficiaryCd(String beneficiaryCd) {
		this.beneficiaryCd = beneficiaryCd;
	}

	public String getSrcAccountNo() {
		return srcAccountNo;
	}

	public void setSrcAccountNo(String srcAccountNo) {
		this.srcAccountNo = srcAccountNo;
	}

	public NodalPaymentTypes getPaymentType() {
		return paymentType;
	}

	public void setPaymentType(NodalPaymentTypes paymentType) {
		this.paymentType = paymentType;
	}

	public String getBeneName() {
		return beneName;
	}

	public void setBeneName(String beneName) {
		this.beneName = beneName;
	}

	public String getBeneType() {
		return beneType;
	}

	public void setBeneType(String beneType) {
		this.beneType = beneType;
	}

	public String getBeneExpiryDate() {
		return beneExpiryDate;
	}

	public void setBeneExpiryDate(String beneExpiryDate) {
		this.beneExpiryDate = beneExpiryDate;
	}

	public CurrencyTypes getCurrencyCd() {
		return currencyCd;
	}

	public void setCurrencyCd(CurrencyTypes currencyCd) {
		this.currencyCd = currencyCd;
	}

	public String getTransactionLimit() {
		return transactionLimit;
	}

	public void setTransactionLimit(String transactionLimit) {
		this.transactionLimit = transactionLimit;
	}

	public String getBankName() {
		return bankName;
	}

	public void setBankName(String bankName) {
		this.bankName = bankName;
	}

	public String getIfscCode() {
		return ifscCode;
	}

	public void setIfscCode(String ifscCode) {
		this.ifscCode = ifscCode;
	}

	public String getBeneAccountNo() {
		return beneAccountNo;
	}

	public void setBeneAccountNo(String beneAccountNo) {
		this.beneAccountNo = beneAccountNo;
	}

	public String getUpiHandle() {
		return upiHandle;
	}

	public void setUpiHandle(String upiHandle) {
		this.upiHandle = upiHandle;
	}

	public String getMobileNo() {
		return mobileNo;
	}

	public void setMobileNo(String mobileNo) {
		this.mobileNo = mobileNo;
	}

	public String getEmailId() {
		return emailId;
	}

	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}

	public String getAddress1() {
		return address1;
	}

	public void setAddress1(String address1) {
		this.address1 = address1;
	}

	public String getAddress2() {
		return address2;
	}

	public void setAddress2(String address2) {
		this.address2 = address2;
	}

	public String getSwiftCode() {
		return swiftCode;
	}

	public void setSwiftCode(String swiftCode) {
		this.swiftCode = swiftCode;
	}

	public String getAcquirer() {
		return acquirer;
	}

	public void setAcquirer(String acquirer) {
		this.acquirer = acquirer;
	}

	public String getActions() {
		return actions;
	}

	public void setActions(String actions) {
		this.actions = actions;
	}

	public String getRrn() {
		return rrn;
	}

	public void setRrn(String rrn) {
		this.rrn = rrn;
	}

	public String getResponseMessage() {
		return responseMessage;
	}

	public void setResponseMessage(String responseMessage) {
		this.responseMessage = responseMessage;
	}

	public String getMerchantProvidedId() {
		return merchantProvidedId;
	}

	public void setMerchantProvidedId(String merchantProvidedId) {
		this.merchantProvidedId = merchantProvidedId;
	}

	public String getMerchantPayId() {
		return merchantPayId;
	}

	public void setMerchantPayId(String merchantPayId) {
		this.merchantPayId = merchantPayId;
	}

	public String getMerchantProvidedName() {
		return merchantProvidedName;
	}

	public void setMerchantProvidedName(String merchantProvidedName) {
		this.merchantProvidedName = merchantProvidedName;
	}

	
	public String getMerchantBusinessName() {
		return merchantBusinessName;
	}

	public void setMerchantBusinessName(String merchantBusinessName) {
		this.merchantBusinessName = merchantBusinessName;
	}

	public String getAadharNo() {
		return aadharNo;
	}

	public void setAadharNo(String aadharNo) {
		this.aadharNo = aadharNo;
	}

	public String getPgRespCode() {
		return pgRespCode;
	}

	public void setPgRespCode(String pgRespCode) {
		this.pgRespCode = pgRespCode;
	}

	public String getPgTxnMessage() {
		return pgTxnMessage;
	}

	public void setPgTxnMessage(String pgTxnMessage) {
		this.pgTxnMessage = pgTxnMessage;
	}

	public String getBankRequest() {
		return bankRequest;
	}

	public void setBankRequest(String bankRequest) {
		this.bankRequest = bankRequest;
	}

	public String getBankResponse() {
		return bankResponse;
	}

	public void setBankResponse(String bankResponse) {
		this.bankResponse = bankResponse;
	}

	public String getRequestChannel() {
		return requestChannel;
	}

	public void setRequestChannel(String requestChannel) {
		this.requestChannel = requestChannel;
	}

	@Override
	public String toString() {
		return "BeneficiaryAccounts [id=" + id + ", merchantProvidedId=" + merchantProvidedId
				+ ", merchantProvidedName=" + merchantProvidedName + ", merchantPayId=" + merchantPayId
				+ ", merchantBusinessName=" + merchantBusinessName + ", status=" + status + ", createdDate="
				+ createdDate + ", updatedDate=" + updatedDate + ", requestedBy=" + requestedBy + ", processedBy="
				+ processedBy + ", custId=" + custId + ", beneficiaryCd=" + beneficiaryCd + ", srcAccountNo="
				+ srcAccountNo + ", paymentType=" + paymentType + ", beneName=" + beneName + ", beneType=" + beneType
				+ ", beneExpiryDate=" + beneExpiryDate + ", currencyCd=" + currencyCd + ", transactionLimit="
				+ transactionLimit + ", bankName=" + bankName + ", ifscCode=" + ifscCode + ", beneAccountNo="
				+ beneAccountNo + ", upiHandle=" + upiHandle + ", mobileNo=" + mobileNo + ", emailId=" + emailId
				+ ", address1=" + address1 + ", address2=" + address2 + ", swiftCode=" + swiftCode + ", actions="
				+ actions + ", acquirer=" + acquirer + ", rrn=" + rrn + ", responseMessage=" + responseMessage
				+ ", aadharNo=" + aadharNo + ", pgTxnMessage=" + pgTxnMessage + ", pgRespCode=" + pgRespCode
				+ ", bankRequest=" + bankRequest + ", bankResponse=" + bankResponse + ", requestChannel="
				+ requestChannel + "]";
	}

}
