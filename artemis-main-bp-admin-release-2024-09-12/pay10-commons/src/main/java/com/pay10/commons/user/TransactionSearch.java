package com.pay10.commons.user;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Date;

/**
 * @author MMAD
 *
 */
public class TransactionSearch implements Serializable {

	private static final long serialVersionUID = -4691009307357010956L;

	private BigInteger transactionId;
	private String transactionIdString;
	private String payId;
	private String customerName;
	private String cardHolderName;
	private String customerEmail;
	private String customerPhone;
	private String merchants;
	private String txnType;
	private String paymentMethods;
	private String cardNumber;
	private String status;
	private String dateFrom;
	private Date dateTo;
	private String amount;
	private String totalAmount;
	private String settledAmount;
	private String orderId;
	private String productDesc;
	private String currency;
	private String mopType;
	private String internalCardIssusserBank;
	private String internalCardIssusserCountry;
	private String refundableAmount;
	private String approvedAmount;
	private String businessName;
	private String pgRefNum;
	private String acqId;
	private String rrn;
	private String arn;
	private String internalCustIP;
	private String subMerchantAccountNo;
	private String BeneficiaryBankName;
	private String BeneficiaryIfscCode;
	private String fundsReceived;
	private String grossTransactionAmt;
	private String aggregatorCommissionAMT;
	private String totalAmtPayable;
	private String totalPayoutNodalAccount;
	private String tDate;
	private String accountNo;
	private String nodalAccountNo;
	private String acquirerType;
	private String netAmountPayout;
	private String pgTxnMessage;

	private String transactionRegion;
	private String cardHolderType;

	private String totalGstOnMerchant;
	private String totalGstOnAcquirer;
	private String netMerchantPayableAmount;
	private String merchantTdrCalculate;
	private String acquirerTdrCalculate;
	private String surchargeFlag;
	private String acquirerCommissionAMT;
	private String updatedBy;
	private String updatedAt;


	// Refund Report
	private String origTxnId;
	private String refundFlag;
	private String origTxnDate;
	private BigInteger refundTxnId;
	private String refundDate;
	private String origAmount;
	private String refundAmount;
	private String refundStatus;
	private String oId;
	private String origTxnType;
	private int srNo;
	private int totalCount;

	// SUmmary Report
	private String acquirerSurchargeAmount;
	private String pgSurchargeAmount;
	private String acquirerGst;
	private String pgGst;
	private String iPaySurchargeAmount;
	private String mmadSurchargeAmount;
	private String totalGstOniPay;
	private String totalGstOnMMAD;
	private String deltaRefundFlag;
	private String transactionCaptureDate;
	private String postSettledFlag;
	private String refundOrderId;
	private String refundButtonName;
	

	private String cardMask;
	private String createDate;
	
//	new fields : shubhamchauhan
	private String pgTdrSc;
	private BigDecimal totalTdr;
	private BigDecimal serviceTax;
	private String caseId;
	private String customerCountry;
	
//	Fields to check whether chargeback exists for transactions or not.
	private String responseCode;	
	private String responseMessage;

	private String currentStatus;
	private String udf4;
	private String udf5;
	private String udf6;
	private String pspName;
	

	public String getPspName() {
		return pspName;
	}

	public void setPspName(String pspName) {
		this.pspName = pspName;
	}
	public String getUdf4() {
		return udf4;
	}

	public void setUdf4(String udf4) {
		this.udf4 = udf4;
	}

	public String getUdf5() {
		return udf5;
	}

	public void setUdf5(String udf5) {
		this.udf5 = udf5;
	}

	public String getUdf6() {
		return udf6;
	}

	public void setUdf6(String udf6) {
		this.udf6 = udf6;
	}

	public String getRefundButtonName() {
		return refundButtonName;
	}

	public void setRefundButtonName(String refundButtonName) {
		this.refundButtonName = refundButtonName;
	}
	
	public String getCreateDate() {
		return createDate;
	}

	public void setCreateDate(String createDate) {
		this.createDate = createDate;
	}
	
	
	public String getUpdatedBy() {
		return updatedBy;
	}

	public void setUpdatedBy(String updatedBy) {
		this.updatedBy = updatedBy;
	}
	
	public String getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(String updatedAt) {
		this.updatedAt = updatedAt;
	}
	

	public String getCardMask() {
		return cardMask;
	}

	public void setCardMask(String cardMask) {
		this.cardMask = cardMask;
	}

	public String getPostSettledFlag() {
		return postSettledFlag;
	}

	public void setPostSettledFlag(String postSettledFlag) {
		this.postSettledFlag = postSettledFlag;
	}

	public String getAcquirerSurchargeAmount() {
		return acquirerSurchargeAmount;
	}

	public void setAcquirerSurchargeAmount(String acquirerSurchargeAmount) {
		this.acquirerSurchargeAmount = acquirerSurchargeAmount;
	}

	public String getPgSurchargeAmount() {
		return pgSurchargeAmount;
	}

	public void setPgSurchargeAmount(String pgSurchargeAmount) {
		this.pgSurchargeAmount = pgSurchargeAmount;
	}

	public String getiPaySurchargeAmount() {
		return iPaySurchargeAmount;
	}

	public void setiPaySurchargeAmount(String iPaySurchargeAmount) {
		this.iPaySurchargeAmount = iPaySurchargeAmount;
	}

	public String getMmadSurchargeAmount() {
		return mmadSurchargeAmount;
	}

	public void setMmadSurchargeAmount(String mmadSurchargeAmount) {
		this.mmadSurchargeAmount = mmadSurchargeAmount;
	}

	public String getTotalGstOniPay() {
		return totalGstOniPay;
	}

	public void setTotalGstOniPay(String totalGstOniPay) {
		this.totalGstOniPay = totalGstOniPay;
	}

	public String getTotalGstOnMMAD() {
		return totalGstOnMMAD;
	}

	public void setTotalGstOnMMAD(String totalGstOnMMAD) {
		this.totalGstOnMMAD = totalGstOnMMAD;
	}

	public String getTotalGstOnMerchant() {
		return totalGstOnMerchant;
	}

	public String getRefundFlag() {
		return refundFlag;
	}

	public void setRefundFlag(String refundFlag) {
		this.refundFlag = refundFlag;
	}

	public String getSurchargeFlag() {
		return surchargeFlag;
	}

	public void setSurchargeFlag(String surchargeFlag) {
		this.surchargeFlag = surchargeFlag;
	}

	public void setTotalGstOnMerchant(String totalGstOnMerchant) {
		this.totalGstOnMerchant = totalGstOnMerchant;
	}

	public String getNetMerchantPayableAmount() {
		return netMerchantPayableAmount;
	}

	public void setNetMerchantPayableAmount(String netMerchantPayableAmount) {
		this.netMerchantPayableAmount = netMerchantPayableAmount;
	}

	public String getMerchantTdrCalculate() {
		return merchantTdrCalculate;
	}

	public String getOrigTxnType() {
		return origTxnType;
	}

	public void setOrigTxnType(String origTxnType) {
		this.origTxnType = origTxnType;
	}

	public void setMerchantTdrCalculate(String merchantTdrCalculate) {
		this.merchantTdrCalculate = merchantTdrCalculate;
	}

	public String getSubMerchantAccountNo() {
		return subMerchantAccountNo;
	}

	public String getNetAmountPayout() {
		return netAmountPayout;
	}

	public void setNetAmountPayout(String netAmountPayout) {
		this.netAmountPayout = netAmountPayout;
	}

	public void setSubMerchantAccountNo(String subMerchantAccountNo) {
		this.subMerchantAccountNo = subMerchantAccountNo;
	}

	public String getBeneficiaryBankName() {
		return BeneficiaryBankName;
	}

	public String getAcquirerType() {
		return acquirerType;
	}

	public void setAcquirerType(String acquirerType) {
		this.acquirerType = acquirerType;
	}

	public void setBeneficiaryBankName(String beneficiaryBankName) {
		BeneficiaryBankName = beneficiaryBankName;
	}

	public String getBeneficiaryIfscCode() {
		return BeneficiaryIfscCode;
	}

	public void setBeneficiaryIfscCode(String beneficiaryIfscCode) {
		BeneficiaryIfscCode = beneficiaryIfscCode;
	}

	public String getFundsReceived() {
		return fundsReceived;
	}

	public int getSrNo() {
		return srNo;
	}

	public void setSrNo(int srNo) {
		this.srNo = srNo;
	}

	public void setFundsReceived(String fundsReceived) {
		this.fundsReceived = fundsReceived;
	}

	public String getGrossTransactionAmt() {
		return grossTransactionAmt;
	}

	public void setGrossTransactionAmt(String grossTransactionAmt) {
		this.grossTransactionAmt = grossTransactionAmt;
	}

	public String getNodalAccountNo() {
		return nodalAccountNo;
	}

	public void setNodalAccountNo(String nodalAccountNo) {
		this.nodalAccountNo = nodalAccountNo;
	}

	public String getAggregatorCommissionAMT() {
		return aggregatorCommissionAMT;
	}

	public void setAggregatorCommissionAMT(String aggregatorCommissionAMT) {
		this.aggregatorCommissionAMT = aggregatorCommissionAMT;
	}

	public String getTotalAmtPayable() {
		return totalAmtPayable;
	}

	public void setTotalAmtPayable(String totalAmtPayable) {
		this.totalAmtPayable = totalAmtPayable;
	}

	public String getTotalPayoutNodalAccount() {
		return totalPayoutNodalAccount;
	}

	public void setTotalPayoutNodalAccount(String totalPayoutNodalAccount) {
		this.totalPayoutNodalAccount = totalPayoutNodalAccount;
	}

	public String gettDate() {
		return tDate;
	}

	public void settDate(String tDate) {
		this.tDate = tDate;
	}

	public String getRefundableAmount() {
		return refundableAmount;
	}

	public void setRefundableAmount(String refundableAmount) {
		this.refundableAmount = refundableAmount;
	}

	public TransactionSearch() {

	}

	public BigInteger getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(BigInteger transactionId) {
		this.transactionId = transactionId;
	}

	public String getPayId() {
		return payId;
	}

	public void setPayId(String payId) {
		this.payId = payId;
	}

	
	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}
	
	public String getCardHolderName() {
		return cardHolderName;
	}

	public void setCardHolderName(String cardHolderName) {
		this.cardHolderName = cardHolderName;
	}
	
	public String getCustomerEmail() {
		return customerEmail;
	}

	public void setCustomerEmail(String customerEmail) {
		this.customerEmail = customerEmail;
	}
	
	public String getCustomerPhone() {
		return customerPhone;
	}

	public void setCustomerPhone(String customerPhone) {
		this.customerPhone = customerPhone;
	}

	public String getMerchants() {
		return merchants;
	}

	public void setMerchants(String merchants) {
		this.merchants = merchants;
	}

	public String getTxnType() {
		return txnType;
	}

	public void setTxnType(String txnType) {
		this.txnType = txnType;
	}

	public String getPaymentMethods() {
		return paymentMethods;
	}

	public void setPaymentMethods(String paymentMethods) {
		this.paymentMethods = paymentMethods;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getDateFrom() {
		return dateFrom;
	}

	public void setDateFrom(String dateFrom) {
		this.dateFrom = dateFrom;
	}

	public Date getDateTo() {
		return dateTo;
	}

	public void setDateTo(Date dateTo) {
		this.dateTo = dateTo;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public String getProductDesc() {
		return productDesc;
	}

	public void setProductDesc(String productDesc) {
		this.productDesc = productDesc;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
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

	public String getBusinessName() {
		return businessName;
	}

	public void setBusinessName(String businessName) {
		this.businessName = businessName;
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

	public String getApprovedAmount() {
		return approvedAmount;
	}

	public void setApprovedAmount(String approvedAmount) {
		this.approvedAmount = approvedAmount;
	}

	public String getPgRefNum() {
		return pgRefNum;
	}

	public void setPgRefNum(String pgRefNum) {
		this.pgRefNum = pgRefNum;
	}

	public String getAccountNo() {
		return accountNo;
	}

	public void setAccountNo(String accountNo) {
		this.accountNo = accountNo;
	}

	public String getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(String totalAmount) {
		this.totalAmount = totalAmount;
	}
	
	public String getSettledAmount() {
		return settledAmount;
	}

	public void setSettledAmount(String settledAmount) {
		this.settledAmount = settledAmount;
	}
	

	public String getOrigTxnDate() {
		return origTxnDate;
	}

	public void setOrigTxnDate(String origTxnDate) {
		this.origTxnDate = origTxnDate;
	}

	public String getOrigTxnId() {
		return origTxnId;
	}

	public void setOrigTxnId(String origTxnId) {
		this.origTxnId = origTxnId;
	}

	public BigInteger getRefundTxnId() {
		return refundTxnId;
	}

	public void setRefundTxnId(BigInteger refundTxnId) {
		this.refundTxnId = refundTxnId;
	}

	public String getRefundDate() {
		return refundDate;
	}

	public void setRefundDate(String refundDate) {
		this.refundDate = refundDate;
	}

	public String getOrigAmount() {
		return origAmount;
	}

	public void setOrigAmount(String origAmount) {
		this.origAmount = origAmount;
	}

	public String getRefundAmount() {
		return refundAmount;
	}

	public void setRefundAmount(String refundAmount) {
		this.refundAmount = refundAmount;
	}

	public String getRefundStatus() {
		return refundStatus;
	}

	public void setRefundStatus(String refundStatus) {
		this.refundStatus = refundStatus;
	}

	public String getoId() {
		return oId;
	}

	public void setoId(String oId) {
		this.oId = oId;
	}

	public Object[] myCsvMethod() {
		Object[] objectArray = new Object[20];

		objectArray[0] = srNo;
		objectArray[1] = businessName;
		objectArray[2] = payId;
		objectArray[3] = pgRefNum;
		objectArray[4] = orderId;
		objectArray[5] = tDate;
		objectArray[6] = dateFrom;
		objectArray[7] = txnType;
		objectArray[8] = grossTransactionAmt;
		objectArray[9] = aggregatorCommissionAMT;
		objectArray[10] = acquirerCommissionAMT;
		objectArray[11] = totalAmtPayable;
		objectArray[12] = totalPayoutNodalAccount;
		objectArray[13] = acquirerType;
		objectArray[14] = "";
		objectArray[15] = "IRCTC iPay";
		objectArray[16] = acquirerType;
		objectArray[17] = refundFlag;
		objectArray[18] = paymentMethods;
		objectArray[19] = mopType;

		return objectArray;
	}

	public Object[] myCsvMethodDownloadPaymentsReport(UserType sessionUserType) {
		Object[] objectArray = new Object[17];
		if (sessionUserType.equals(UserType.MERCHANT)) {
			objectArray = new Object[14];
			objectArray[0] = srNo;
			objectArray[1] = transactionIdString;
			objectArray[2] = pgRefNum;
			objectArray[3] = dateFrom;
			objectArray[4] = orderId;
			objectArray[5] = paymentMethods;
			objectArray[6] = txnType;
			objectArray[7] = status;
			objectArray[8] = transactionRegion;
			objectArray[9] = amount;
			objectArray[10] = totalAmount;
			objectArray[11] = deltaRefundFlag;
			objectArray[12] = rrn;
			objectArray[13] = postSettledFlag;
		} else {
			objectArray[0] = srNo;
			objectArray[1] = transactionIdString;
			objectArray[2] = pgRefNum;
			objectArray[3] = merchants;
			objectArray[4] = acquirerType;
			objectArray[5] = dateFrom;
			objectArray[6] = orderId;
			objectArray[7] = paymentMethods;
			objectArray[8] = txnType;
			objectArray[9] = status;
			objectArray[10] = transactionRegion;
			objectArray[11] = amount;
			objectArray[12] = totalAmount;
			objectArray[13] = deltaRefundFlag;
			objectArray[14] = acqId;
			objectArray[15] = rrn;
			objectArray[16] = postSettledFlag;
		}

		return objectArray;
	}

	public Object[] myCsvMethodDownloadPaymentsReportByView() {
		Object[] objectArray = new Object[13];

		objectArray[0] = srNo;
		objectArray[1] = transactionIdString;
		objectArray[2] = pgRefNum;
		objectArray[3] = merchants;
		objectArray[4] = dateFrom;
		objectArray[5] = orderId;
		objectArray[6] = paymentMethods;
		objectArray[7] = txnType;
		objectArray[8] = status;
		objectArray[9] = transactionRegion;
		objectArray[10] = amount;
		objectArray[11] = totalAmount;
		objectArray[12] = postSettledFlag;
		return objectArray;
	}

	public Object[] myCsvMethodDownloadSummaryReport() {
		Object[] objectArray = new Object[26];

		objectArray[0] = srNo;
		objectArray[1] = transactionIdString;
		objectArray[2] = pgRefNum;
		objectArray[3] = paymentMethods;
		objectArray[4] = mopType;
		objectArray[5] = orderId;
		objectArray[6] = businessName;
		objectArray[7] = currency;
		objectArray[8] = txnType;
		objectArray[9] = transactionCaptureDate;
		objectArray[10] = dateFrom;
		objectArray[11] = transactionRegion;
		objectArray[12] = cardHolderType;
		objectArray[13] = acquirerType;
		objectArray[14] = totalAmount;
		objectArray[15] = acquirerSurchargeAmount;
		objectArray[16] = iPaySurchargeAmount;
		objectArray[17] = mmadSurchargeAmount;
		objectArray[18] = totalGstOnAcquirer;
		objectArray[19] = totalGstOniPay;
		objectArray[20] = totalGstOnMMAD;
		objectArray[21] = netMerchantPayableAmount;
		objectArray[22] = acqId;
		objectArray[23] = rrn;
		objectArray[24] = postSettledFlag;
		objectArray[25] = deltaRefundFlag;

		return objectArray;
	}

	public Object[] summaryPayoutReportCsv(String dataArray) {

		int arraySize = dataArray.split(";").length;
		String[] objectArrayString = dataArray.split(";");

		Object[] objectArray = new Object[arraySize];

		for (int i = 0; i < arraySize; i++) {
			objectArray[i] = objectArrayString[i];
		}

		return objectArray;
	}

	public int getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}

	public String getTransactionRegion() {
		return transactionRegion;
	}

	public void setTransactionRegion(String transactionRegion) {
		this.transactionRegion = transactionRegion;
	}

	public String getTransactionIdString() {
		return transactionIdString;
	}

	public void setTransactionIdString(String transactionIdString) {
		this.transactionIdString = transactionIdString;
	}

	public String getAcquirerTdrCalculate() {
		return acquirerTdrCalculate;
	}

	public void setAcquirerTdrCalculate(String acquirerTdrCalculate) {
		this.acquirerTdrCalculate = acquirerTdrCalculate;
	}

	public String getCardHolderType() {
		return cardHolderType;
	}

	public void setCardHolderType(String cardHolderType) {
		this.cardHolderType = cardHolderType;
	}

	public String getDeltaRefundFlag() {
		return deltaRefundFlag;
	}

	public void setDeltaRefundFlag(String deltaRefundFlag) {
		this.deltaRefundFlag = deltaRefundFlag;
	}

	public String getAcquirerCommissionAMT() {
		return acquirerCommissionAMT;
	}

	public void setAcquirerCommissionAMT(String acquirerCommissionAMT) {
		this.acquirerCommissionAMT = acquirerCommissionAMT;
	}

	public String getAcqId() {
		return acqId;
	}

	public void setAcqId(String acqId) {
		this.acqId = acqId;
	}

	public String getRrn() {
		return rrn;
	}

	public void setRrn(String rrn) {
		this.rrn = rrn;
	}
	
	public String getArn() {
		return arn;
	}

	public void setArn(String arn) {
		this.arn = arn;
	}
	
	public String getInternalCustIP() {
		return internalCustIP;
	}

	public void setInternalCustIP(String internalCustIP) {
		this.internalCustIP = internalCustIP;
	}
	

	public String getTotalGstOnAcquirer() {
		return totalGstOnAcquirer;
	}

	public void setTotalGstOnAcquirer(String totalGstOnAcquirer) {
		this.totalGstOnAcquirer = totalGstOnAcquirer;
	}

	public String getTransactionCaptureDate() {
		return transactionCaptureDate;
	}

	public void setTransactionCaptureDate(String transactionCaptureDate) {
		this.transactionCaptureDate = transactionCaptureDate;
	}

	public String getRefundOrderId() {
		return refundOrderId;
	}

	public void setRefundOrderId(String refundOrderId) {
		this.refundOrderId = refundOrderId;
	}

	public String getPgTxnMessage() {
		return pgTxnMessage;
	}

	public void setPgTxnMessage(String pgTxnMessage) {
		this.pgTxnMessage = pgTxnMessage;
	}

	public String getAcquirerGst() {
		return acquirerGst;
	}

	public void setAcquirerGst(String acquirerGst) {
		this.acquirerGst = acquirerGst;
	}

	public String getPgGst() {
		return pgGst;
	}

	public void setPgGst(String pgGst) {
		this.pgGst = pgGst;
	}

	public String getPgTdrSc() {
		return pgTdrSc;
	}

	public void setPgTdrSc(String pgTdrSc) {
		this.pgTdrSc = pgTdrSc;
	}

	public BigDecimal getTotalTdr() {
		return totalTdr;
	}

	public void setTotalTdr(BigDecimal totalTdr) {
		this.totalTdr = totalTdr;
	}

	public BigDecimal getServiceTax() {
		return serviceTax;
	}

	public void setServiceTax(BigDecimal serviceTax) {
		this.serviceTax = serviceTax;
	}

	public String getCaseId() {
		return caseId;
	}

	public void setCaseId(String caseId) {
		this.caseId = caseId;
	}

	public String getResponseCode() {
		return responseCode;
	}

	public void setResponseCode(String responseCode) {
		this.responseCode = responseCode;
	}

	public String getResponseMessage() {
		return responseMessage;
	}

	public void setResponseMessage(String responseMessage) {
		this.responseMessage = responseMessage;
	}

	public String getCustomerCountry() {
		return customerCountry;
	}

	public void setCustomerCountry(String customerCountry) {
		this.customerCountry = customerCountry;
	}

	public String getCurrentStatus() {
		return currentStatus;
	}

	public void setCurrentStatus(String currentStatus) {
		this.currentStatus = currentStatus;
	}

	@Override
	public String toString() {
		return "TransactionSearch [transactionId=" + transactionId + ", transactionIdString=" + transactionIdString
				+ ", payId=" + payId + ", customerName=" + customerName + ", cardHolderName=" + cardHolderName
				+ ", customerEmail=" + customerEmail + ", customerPhone=" + customerPhone + ", merchants=" + merchants
				+ ", txnType=" + txnType + ", paymentMethods=" + paymentMethods + ", cardNumber=" + cardNumber
				+ ", status=" + status + ", dateFrom=" + dateFrom + ", dateTo=" + dateTo + ", amount=" + amount
				+ ", totalAmount=" + totalAmount + ", settledAmount=" + settledAmount + ", orderId=" + orderId
				+ ", productDesc=" + productDesc + ", currency=" + currency + ", mopType=" + mopType
				+ ", internalCardIssusserBank=" + internalCardIssusserBank + ", internalCardIssusserCountry="
				+ internalCardIssusserCountry + ", refundableAmount=" + refundableAmount + ", approvedAmount="
				+ approvedAmount + ", businessName=" + businessName + ", pgRefNum=" + pgRefNum + ", acqId=" + acqId
				+ ", rrn=" + rrn + ", arn=" + arn + ", internalCustIP=" + internalCustIP + ", subMerchantAccountNo="
				+ subMerchantAccountNo + ", BeneficiaryBankName=" + BeneficiaryBankName + ", BeneficiaryIfscCode="
				+ BeneficiaryIfscCode + ", fundsReceived=" + fundsReceived + ", grossTransactionAmt="
				+ grossTransactionAmt + ", aggregatorCommissionAMT=" + aggregatorCommissionAMT + ", totalAmtPayable="
				+ totalAmtPayable + ", totalPayoutNodalAccount=" + totalPayoutNodalAccount + ", tDate=" + tDate
				+ ", accountNo=" + accountNo + ", nodalAccountNo=" + nodalAccountNo + ", acquirerType=" + acquirerType
				+ ", netAmountPayout=" + netAmountPayout + ", pgTxnMessage=" + pgTxnMessage + ", transactionRegion="
				+ transactionRegion + ", cardHolderType=" + cardHolderType + ", totalGstOnMerchant="
				+ totalGstOnMerchant + ", totalGstOnAcquirer=" + totalGstOnAcquirer + ", netMerchantPayableAmount="
				+ netMerchantPayableAmount + ", merchantTdrCalculate=" + merchantTdrCalculate
				+ ", acquirerTdrCalculate=" + acquirerTdrCalculate + ", surchargeFlag=" + surchargeFlag
				+ ", acquirerCommissionAMT=" + acquirerCommissionAMT + ", origTxnId=" + origTxnId + ", refundFlag="
				+ refundFlag + ", origTxnDate=" + origTxnDate + ", refundTxnId=" + refundTxnId + ", refundDate="
				+ refundDate + ", origAmount=" + origAmount + ", refundAmount=" + refundAmount + ", refundStatus="
				+ refundStatus + ", oId=" + oId + ", origTxnType=" + origTxnType + ", srNo=" + srNo + ", totalCount="
				+ totalCount + ", acquirerSurchargeAmount=" + acquirerSurchargeAmount + ", pgSurchargeAmount="
				+ pgSurchargeAmount + ", acquirerGst=" + acquirerGst + ", pgGst=" + pgGst + ", iPaySurchargeAmount="
				+ iPaySurchargeAmount + ", mmadSurchargeAmount=" + mmadSurchargeAmount + ", totalGstOniPay="
				+ totalGstOniPay + ", totalGstOnMMAD=" + totalGstOnMMAD + ", deltaRefundFlag=" + deltaRefundFlag
				+ ", transactionCaptureDate=" + transactionCaptureDate + ", postSettledFlag=" + postSettledFlag
				+ ", refundOrderId=" + refundOrderId + ", refundButtonName=" + refundButtonName + ", cardMask="
				+ cardMask + ", createDate=" + createDate + ", pgTdrSc=" + pgTdrSc + ", totalTdr=" + totalTdr
				+ ", serviceTax=" + serviceTax + ", caseId=" + caseId + ", customerCountry=" + customerCountry
				+ ", responseCode=" + responseCode + ", responseMessage=" + responseMessage + ", currentStatus="
				+ currentStatus + "]";
	}

	
}
