package com.pay10.commons.user;

import java.io.Serializable;
import java.math.BigDecimal;

import com.pay10.commons.util.CrmFieldConstants;
import com.pay10.commons.util.Currency;
import com.pay10.commons.util.StatusType;

/**
 * @author Chandan
 *
 */
public class TransactionHistory implements Serializable{
	
	private static final long serialVersionUID = -4199245590439341775L;

	private  String orderId;
	private String txnId;
	private String createDate;
	private String payId;	
	private String cardNumber;
	private String mopType;
	private String paymentType;
	private String status;
	private String txnType;
	private String custEmail;
	private String internalCustIP;
	private String internalCustCountryName;
	private String internalCardIssusserBank;
	private String internalCardIssusserCountry;
	private String acqId;
	
	private String currencyCode;
	private String currencyNameCode;
	private BigDecimal amount;	
	private String origTxnId;	
	private String acquirerCode;
	
	
	
	//Refund service	
	private BigDecimal capturedAmount;	
	private BigDecimal authorizedAmount;	
	private BigDecimal fixedTxnFee;	
	private BigDecimal availableRefundAmount;	
	private BigDecimal refundedAmount;
	private BigDecimal tdr;
	private BigDecimal serviceTax;
	private BigDecimal chargebackAmount;
	private BigDecimal netAmount;
	private BigDecimal percentecServiceTax;
	private BigDecimal merchantTDR;
	public String getOrderId() {
		return orderId;
	}
	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}
	public String getTxnId() {
		return txnId;
	}
	public void setTxnId(String txnId) {
		this.txnId = txnId;
	}
	public String getCreateDate() {
		return createDate;
	}
	public void setCreateDate(String createDate) {
		this.createDate = createDate;
	}
	public String getPayId() {
		return payId;
	}
	public void setPayId(String payId) {
		this.payId = payId;
	}
	public String getCardNumber() {
		return cardNumber;
	}
	public void setCardNumber(String cardNumber) {
		this.cardNumber = cardNumber;
	}
	public String getMopType() {
		return mopType;
	}
	public void setMopType(String mopType) {
		this.mopType = mopType;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getTxnType() {
		return txnType;
	}
	public void setTxnType(String txnType) {
		this.txnType = txnType;
	}
	public String getCustEmail() {
		return custEmail;
	}
	public void setCustEmail(String custEmail) {
		this.custEmail = custEmail;
	}
	public String getInternalCustIP() {
		return internalCustIP;
	}
	public void setInternalCustIP(String internalCustIP) {
		this.internalCustIP = internalCustIP;
	}
	public String getInternalCustCountryName() {
		return internalCustCountryName;
	}
	public void setInternalCustCountryName(String internalCustCountryName) {
		this.internalCustCountryName = internalCustCountryName;
	}
	public String getAcqId() {
		return acqId;
	}
	public void setAcqId(String acqId) {
		this.acqId = acqId;
	}
	public String getCurrencyCode() {
		return currencyCode;
	}
	public void setCurrencyCode(String currencyCode) {
		this.currencyCode = currencyCode;
	}
	public BigDecimal getAmount() {
		return amount;
	}
	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}
	public String getOrigTxnId() {
		return origTxnId;
	}
	public void setOrigTxnId(String origTxnId) {
		this.origTxnId = origTxnId;
	}
	public String getCurrencyNameCode() {
		return currencyNameCode;
	}
	public void setCurrencyNameCode(String currencyNameCode) {
		this.currencyNameCode = currencyNameCode;
	}
	public String getAcquirerCode() {
		return acquirerCode;
	}
	public void setAcquirerCode(String acquirerCode) {
		this.acquirerCode = acquirerCode;
	}
	public String getPaymentType() {
		return paymentType;
	}
	public void setPaymentType(String paymentType) {
		this.paymentType = paymentType;
	}
	public BigDecimal getCapturedAmount() {
		return capturedAmount;
	}
	public void setCapturedAmount(BigDecimal capturedAmount) {
		this.capturedAmount = capturedAmount;
	}
	public BigDecimal getFixedTxnFee() {
		return fixedTxnFee;
	}
	public void setFixedTxnFee(BigDecimal fixedTxnFee) {
		this.fixedTxnFee = fixedTxnFee;
	}
	public BigDecimal getAvailableRefundAmount() {
		return availableRefundAmount;
	}
	public void setAvailableRefundAmount(BigDecimal availableRefundAmount) {
		this.availableRefundAmount = availableRefundAmount;
	}
	public BigDecimal getAuthorizedAmount() {
		return authorizedAmount;
	}
	public void setAuthorizedAmount(BigDecimal authorizedAmount) {
		this.authorizedAmount = authorizedAmount;
	}
	public BigDecimal getRefundedAmount() {
		return refundedAmount;
	}
	public void setRefundedAmount(BigDecimal refundedAmount) {
		this.refundedAmount = refundedAmount;
	}
	public BigDecimal getTdr() {
		return tdr;
	}
	public void setTdr(BigDecimal tdr) {
		this.tdr = tdr;
	}
	public BigDecimal getServiceTax() {
		return serviceTax;
	}
	public void setServiceTax(BigDecimal serviceTax) {
		this.serviceTax = serviceTax;
	}
	public BigDecimal getChargebackAmount() {
		return chargebackAmount;
	}
	public void setChargebackAmount(BigDecimal chargebackAmount) {
		this.chargebackAmount = chargebackAmount;
	}
	public BigDecimal getNetAmount() {
		return netAmount;
	}
	public void setNetAmount(BigDecimal netAmount) {
		this.netAmount = netAmount;
	}
	public BigDecimal getPercentecServiceTax() {
		return percentecServiceTax;
	}
	public void setPercentecServiceTax(BigDecimal percentecServiceTax) {
		this.percentecServiceTax = percentecServiceTax;
	}
	public BigDecimal getMerchantTDR() {
		return merchantTDR;
	}
	public void setMerchantTDR(BigDecimal merchantTDR) {
		this.merchantTDR = merchantTDR;
	}
	

	public String getInternalCardIssusserBank() {
		return internalCardIssusserBank;
	}
	public void setInternalCardIssusserBank(String internalCardIssusserBank) {
		this.internalCardIssusserBank = internalCardIssusserBank;
	}
	public String getInternalCardIssusserCountry() {
		return internalCardIssusserCountry;
	}
	public void setInternalCardIssusserCountry(String internalCardIssusserCountry) {
		this.internalCardIssusserCountry = internalCardIssusserCountry;
	}
	public void setOriginalTransactionDetails(TransactionHistory transaction) {				

		setTxnType(transaction.getTxnType());
		setCreateDate(transaction.getCreateDate());
		setCurrencyCode(transaction.getCurrencyCode());
		setCurrencyNameCode(Currency.getAlphabaticCode(transaction.getCurrencyCode()));
		setAuthorizedAmount(transaction.getAmount());
		setOrigTxnId(transaction.getTxnId());
		setAcquirerCode(transaction.getAcquirerCode());
		setCapturedAmount(transaction.getAmount());
		setRefundedAmount(BigDecimal.valueOf(0.00));
		setCardNumber(transaction.getCardNumber());
		setOrderId(transaction.getOrderId());
		setPayId(transaction.getPayId());
		setMopType(transaction.getMopType());
		setPaymentType(transaction.getPaymentType());
		setCustEmail(transaction.getCustEmail());	
		setInternalCardIssusserBank(transaction.getInternalCardIssusserBank());
		setInternalCardIssusserCountry(transaction.getInternalCardIssusserCountry());
	}
	
	public void setNewOrderTransactionDetails(TransactionHistory transaction) {				
		setInternalCustIP(transaction.getInternalCustIP());	
		setInternalCustCountryName(transaction.getInternalCustCountryName());
	}
	
	public void setCaptureTransactionDetails(TransactionHistory transaction) {
		setCapturedAmount(transaction.getAmount());
		setAmount(transaction.getAmount());
	}
	
	public void setRefundedTransactionDetails(TransactionHistory transaction, int decimalPlaces) {
		if (transaction.getStatus().equals(StatusType.ERROR.getName())) {
			return;
		}
		if (transaction.getStatus().equals(StatusType.CAPTURED.getName())) {
			transaction
					.setStatus(CrmFieldConstants.REFUNDED.getValue());
			setRefundedAmount(getRefundedAmount()
					.add(transaction.getAmount()).setScale(decimalPlaces));
		}
		if (transaction.getStatus().equals(StatusType.SETTLED_SETTLE.getName())) {
			transaction.setStatus(CrmFieldConstants.REFUNDED.getValue());
		}
		if (transaction.getStatus().equals(StatusType.SENT_TO_BANK.getName())) {
			transaction.setStatus(CrmFieldConstants.INITIATED.getValue());
			setRefundedAmount(getRefundedAmount()
					.add(transaction.getAmount()).setScale(decimalPlaces));
		}
	}
}
