package com.pay10.payout.payten;

public class PaytenRequest {

	private String beneName;
	private String accountNo;
	private String merchantTransId;
	private String transferType;
	private String bankName;
	private String ifscCode;
	private String hash;
	private String aggregatorId;
	private double amount;
	private String batchId;
	private String fileName;
	
	private String acquirer;
	
	private String resellerId;
	
	private String transid;
	private String purpose;
	
	public String getBeneName() {
		return beneName;
	}
	public void setBeneName(String beneName) {
		this.beneName = beneName;
	}
	public String getAccountNo() {
		return accountNo;
	}
	public void setAccountNo(String accountNo) {
		this.accountNo = accountNo;
	}
	public String getMerchantTransId() {
		return merchantTransId;
	}
	public void setMerchantTransId(String merchantTransId) {
		this.merchantTransId = merchantTransId;
	}
	public String getTransferType() {
		return transferType;
	}
	public void setTransferType(String transferType) {
		this.transferType = transferType;
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
	public String getHash() {
		return hash;
	}
	public void setHash(String hash) {
		this.hash = hash;
	}
	public String getAggregatorId() {
		return aggregatorId;
	}
	public void setAggregatorId(String aggregatorId) {
		this.aggregatorId = aggregatorId;
	}
	public double getAmount() {
		return amount;
	}
	public void setAmount(double amount) {
		this.amount = amount;
	}
	
	
	
	public String getTransid() {
		return transid;
	}
	public void setTransid(String transid) {
		this.transid = transid;
	}
	
	
	
	
	public String getBatchId() {
		return batchId;
	}
	public void setBatchId(String batchId) {
		this.batchId = batchId;
	}
	
	
	public String getResellerId() {
		return resellerId;
	}
	public void setResellerId(String resellerId) {
		this.resellerId = resellerId;
	}
	
	
	
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	
	
	
	public String getAcquirer() {
		return acquirer;
	}
	public void setAcquirer(String acquirer) {
		this.acquirer = acquirer;
	}
	
	
	
	public String getPurpose() {
		return purpose;
	}
	public void setPurpose(String purpose) {
		this.purpose = purpose;
	}
	@Override
	public String toString() {
		return "PayoutNewRequest [beneName=" + beneName + ", accountNo=" + accountNo + ", merchantTransId="
				+ merchantTransId + ", transferType=" + transferType + ", bankName=" + bankName + ", ifscCode="
				+ ifscCode + ", hash=" + hash + ", aggregatorId=" + aggregatorId + ", amount=" + amount + "]";
	}
	
	
	
}
