package com.pay10.commons.user;

public class MISReportObject  implements Cloneable {

	private String transactionId;
	private String pgRefNum;
	private String merchants;
	private String txnType;
	private String acquirerType;
	private String paymentMethods;
	private String dateFrom;
	private String orderId;
	private String srNo;
	private String payId;
	private String mopType;
	private String transactionDate;
	private String deltaRefundFlag;
	private String surchargeFlag;
	private String netMerchantPayableAmount;
	private String refundFlag;
	private String totalAmtPayable;
	private String accountNo;
	private String totalPayoutNodalAccount;
	private String aggregatorCommissionAMT;
	private String acquirerCommissionAMT;
	private String grossTransactionAmt;
	private String beneficiaryBankName;
	private String transactionRegion;
	private String cardHolderType;
	private String amount;
	private String totalAmount;
	private String nodalCreditDate;
	private String nodalPayoutInitiationDate;
	private String remarks;
	private String acqId;
	private String arn;
	private String rrn;
	private String postSettledFlag;
	private String accountHolderName;
	private String accountNumber;
	private String ifsc;
	private String merchantAmount;
	private String settlementCycle;
	private String utrNo;
	private String tdrAmount;
	private String gstOnTdrAmount;
	private String reseller_commission;
	
	public MISReportObject() {
		
	}
	public MISReportObject(MISReportObject misReportObject) {
		super();
		
	}

	public Object clone() throws CloneNotSupportedException
    {
        return super.clone();
    }
	
	public Object[] myCsvMethod() {
		  Object[] objectArray = new Object[24];
		  
		 
		  objectArray[0] = srNo;
		  objectArray[1] = merchants;
		  objectArray[2] = payId;
		  objectArray[3] = pgRefNum;
		  objectArray[4] = orderId;
		  objectArray[5] = transactionDate;
		  objectArray[6] = dateFrom;
		  objectArray[7] = txnType;
		  objectArray[8] = grossTransactionAmt;
		  objectArray[9] = aggregatorCommissionAMT;
		  objectArray[10] = acquirerCommissionAMT;
		  objectArray[11] = totalAmtPayable;
		  objectArray[12] = totalPayoutNodalAccount;
		  objectArray[13] = acquirerType;
		  objectArray[14] = "";
		  objectArray[15] = "Pay10";
		  objectArray[16] = acquirerType;
		  objectArray[17] = refundFlag;
		  objectArray[18] = paymentMethods;
		  objectArray[19] = mopType;
		  objectArray[20] = surchargeFlag;
		  objectArray[21] = settlementCycle;
		  objectArray[22]=accountNo;
		  objectArray[23]= reseller_commission;
		
		  return objectArray;
		}
	
	public Object[] preMisCsvMethod() {
		  Object[] objectArray = new Object[22];
		  
		 
		  objectArray[0] = srNo;
		  objectArray[1] = merchants;
		  objectArray[2] = payId;
		  objectArray[3] = pgRefNum;
		  objectArray[4] = orderId;
		  objectArray[5] = transactionDate;
//		  objectArray[6] = dateFrom;
		  objectArray[6] = txnType;
		  objectArray[7] = grossTransactionAmt;
		  objectArray[8] = aggregatorCommissionAMT;
		  objectArray[9] = acquirerCommissionAMT;
		  objectArray[10] = totalAmtPayable;
		  objectArray[11] = totalPayoutNodalAccount;
		  objectArray[12] = acquirerType;
		  objectArray[13] = "";
		  objectArray[14] = "Pay10";
		  objectArray[15] = acquirerType;
		  objectArray[16] = refundFlag;
		  objectArray[17] = paymentMethods;
		  objectArray[18] = mopType;
		  objectArray[19] = surchargeFlag;
		  objectArray[20] = settlementCycle;
		  objectArray[21]=accountNo;
		  return objectArray;
		}
	
	public Object[] preMisCsvMethodSplit() {
		  Object[] objectArray = new Object[24];
		  
		  objectArray[0] = srNo;
		  objectArray[1] = merchants;
		  objectArray[2] = payId;
		  objectArray[3] = pgRefNum;
		  objectArray[4] = orderId;
		  objectArray[5] = transactionDate;
//		  objectArray[6] = dateFrom;
		  objectArray[6] = txnType;
		  objectArray[7] = grossTransactionAmt;
		  objectArray[8] = aggregatorCommissionAMT;
		  objectArray[9] = acquirerCommissionAMT;
		  objectArray[10] = totalAmtPayable;
		  objectArray[11] = totalPayoutNodalAccount;
		  objectArray[12] = acquirerType;
		  objectArray[13] = "";
		  objectArray[14] = "Pay10";
		  objectArray[15] = acquirerType;
		  objectArray[16] = refundFlag;
		  objectArray[17] = paymentMethods;
		  objectArray[18] = mopType;
		  objectArray[19] = accountHolderName;
		  objectArray[20] = accountNumber;
		  objectArray[21] = ifsc;
		  objectArray[22] = merchantAmount;
		  objectArray[23] = settlementCycle;
		
		  return objectArray;
		}
	
	public Object[] misNodalReport() {
		  Object[] objectArray = new Object[22];
		  
		 
		  objectArray[0] = srNo;
		  objectArray[1] = merchants;
		  objectArray[2] = payId;
		  objectArray[3] = pgRefNum;
		  objectArray[4] = orderId;
		  objectArray[5] = transactionDate;
		  objectArray[6] = dateFrom;
		  objectArray[7] = nodalCreditDate;
		  objectArray[8] = nodalPayoutInitiationDate;
		  objectArray[9] = txnType;
		  objectArray[10] = grossTransactionAmt;
		  objectArray[11] = aggregatorCommissionAMT;
		  objectArray[12] = acquirerCommissionAMT;
		  objectArray[13] = totalAmtPayable;
		  objectArray[14] = totalPayoutNodalAccount;
		  objectArray[15] = acquirerType;
		  objectArray[16] = "";
		  objectArray[17] = "IRCTC iPay";
		  objectArray[18] = acquirerType;
		  objectArray[19] = refundFlag;
		  objectArray[20] = paymentMethods;
		  objectArray[21] = mopType;
		
		  return objectArray;
		}
	
	public Object[] exceptionsNodalReport() {
		  Object[] objectArray = new Object[17];
		  
		 
		  objectArray[0] = srNo;
		  objectArray[1] = merchants;
		  objectArray[2] = pgRefNum;
		  objectArray[3] = orderId;
		  objectArray[4] = transactionDate;
		  objectArray[5] = dateFrom;
		  objectArray[6] = txnType;
		  objectArray[7] = amount;
		  objectArray[8] = totalAmount;
		  objectArray[9] = acqId;
		  objectArray[10] = rrn;
		  objectArray[11] = arn;
		  objectArray[12] = acquirerType;
		  objectArray[13] = postSettledFlag;
		  objectArray[14] = paymentMethods;
		  objectArray[15] = mopType;
		  objectArray[16] = remarks;
		
		  return objectArray;
		}
	
	//Added By Sweety
	public Object[] myCsvMethodSplit() {
		  Object[] objectArray = new Object[24];
		  
		 
		  objectArray[0] = srNo;
		  objectArray[1] = merchants;
		  objectArray[2] = payId;
		  objectArray[3] = pgRefNum;
		  objectArray[4] = orderId;
		  objectArray[5] = transactionDate;
		  objectArray[6] = dateFrom;
		  objectArray[7] = txnType;
		  objectArray[8] = grossTransactionAmt;
		  objectArray[9] = aggregatorCommissionAMT;
		  objectArray[10] = acquirerCommissionAMT;
		  objectArray[11] = totalAmtPayable;
		  objectArray[12] = totalPayoutNodalAccount;
		  objectArray[13] = acquirerType;
		  objectArray[14] = "";
		  objectArray[15] = "Pay10";
		  objectArray[16] = acquirerType;
		  objectArray[17] = refundFlag;
		  objectArray[18] = paymentMethods;
		  objectArray[19] = mopType;
		  objectArray[20] = accountNumber;
		  objectArray[21] = ifsc;
		  objectArray[22] = accountHolderName;
		  objectArray[23] = settlementCycle;
		  
		
		  return objectArray;
		}
	public Object[] myCsvMethodSplitSettled() {
		  Object[] objectArray = new Object[16];
		  
			 
		  objectArray[0] = srNo;
		  objectArray[1] = merchants;
		  objectArray[2] = payId;
		  objectArray[3] = pgRefNum;
		  objectArray[4] = orderId;
		  objectArray[5] = transactionDate;
		  objectArray[6] = dateFrom;
		  objectArray[7] = txnType;
		  objectArray[8] = grossTransactionAmt;//here ok after this we need to change some column data
		  objectArray[9] = tdrAmount;//tdr
		  objectArray[10] = gstOnTdrAmount;
		  //objectArray[9] = aggregatorCommissionAMT;
		  //objectArray[10] = acquirerCommissionAMT;
		  objectArray[11] = totalAmtPayable;
		  //objectArray[12] = totalPayoutNodalAccount;
		  //objectArray[13] = acquirerType;
		  //objectArray[14] = "";
		  //objectArray[15] = "Pay10";
		  //objectArray[16] = acquirerType;
		  //objectArray[17] = refundFlag;
		  objectArray[12] = paymentMethods;
		  objectArray[13] = mopType;
		  //objectArray[20] = accountNumber;
		  //objectArray[21] = ifsc;
		  //objectArray[22] = accountHolderName;
		  //objectArray[23] = settlementCycle;
		  objectArray[14] = utrNo;
		  objectArray[15] = accountNumber;
		
		  return objectArray;
		}
	public String getAccountHolderName() {
		return accountHolderName;
	}

	public void setAccountHolderName(String accountHolderName) {
		this.accountHolderName = accountHolderName;
	}

	public String getAccountNumber() {
		return accountNumber;
	}

	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}

	public String getIfsc() {
		return ifsc;
	}

	public void setIfsc(String ifsc) {
		this.ifsc = ifsc;
	}
	
	public String getMerchantAmount() {
		return merchantAmount;
	}
	public void setMerchantAmount(String merchantAmount) {
		this.merchantAmount = merchantAmount;
	}
	
	public String getNetMerchantPayableAmount() {
		return netMerchantPayableAmount;
	}
	public void setNetMerchantPayableAmount(String netMerchantPayableAmount) {
		this.netMerchantPayableAmount = netMerchantPayableAmount;
	}
	public String getTransactionId() {
		return transactionId;
	}
	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}
	public String getPgRefNum() {
		return pgRefNum;
	}
	public void setPgRefNum(String pgRefNum) {
		this.pgRefNum = pgRefNum;
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
	public String getAcquirerType() {
		return acquirerType;
	}
	public void setAcquirerType(String acquirerType) {
		this.acquirerType = acquirerType;
	}
	public String getPaymentMethods() {
		return paymentMethods;
	}
	public void setPaymentMethods(String paymentMethods) {
		this.paymentMethods = paymentMethods;
	}
	public String getDateFrom() {
		return dateFrom;
	}
	public void setDateFrom(String dateFrom) {
		this.dateFrom = dateFrom;
	}
	public String getOrderId() {
		return orderId;
	}
	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}
	public String getSrNo() {
		return srNo;
	}
	public void setSrNo(String srNo) {
		this.srNo = srNo;
	}
	public String getMopType() {
		return mopType;
	}
	public void setMopType(String mopType) {
		this.mopType = mopType;
	}
	public String getDeltaRefundFlag() {
		return deltaRefundFlag;
	}
	public void setDeltaRefundFlag(String deltaRefundFlag) {
		this.deltaRefundFlag = deltaRefundFlag;
	}
	public String getPayId() {
		return payId;
	}
	public void setPayId(String payId) {
		this.payId = payId;
	}
	public String getSurchargeFlag() {
		return surchargeFlag;
	}
	public void setSurchargeFlag(String surchargeFlag) {
		this.surchargeFlag = surchargeFlag;
	}
	
	public String getTransactionDate() {
		return transactionDate;
	}
	public void setTransactionDate(String transactionDate) {
		this.transactionDate = transactionDate;
	}
	public String getRefundFlag() {
		return refundFlag;
	}
	public void setRefundFlag(String refundFlag) {
		this.refundFlag = refundFlag;
	}
	public String getTotalAmtPayable() {
		return totalAmtPayable;
	}
	public void setTotalAmtPayable(String totalAmtPayable) {
		this.totalAmtPayable = totalAmtPayable;
	}
	public String getAccountNo() {
		return accountNo;
	}
	public void setAccountNo(String accountNo) {
		this.accountNo = accountNo;
	}
	public String getTotalPayoutNodalAccount() {
		return totalPayoutNodalAccount;
	}
	public void setTotalPayoutNodalAccount(String totalPayoutNodalAccount) {
		this.totalPayoutNodalAccount = totalPayoutNodalAccount;
	}
	public String getAggregatorCommissionAMT() {
		return aggregatorCommissionAMT;
	}
	public void setAggregatorCommissionAMT(String aggregatorCommissionAMT) {
		this.aggregatorCommissionAMT = aggregatorCommissionAMT;
	}
	public String getAcquirerCommissionAMT() {
		return acquirerCommissionAMT;
	}
	public void setAcquirerCommissionAMT(String acquirerCommissionAMT) {
		this.acquirerCommissionAMT = acquirerCommissionAMT;
	}
	public String getGrossTransactionAmt() {
		return grossTransactionAmt;
	}
	public void setGrossTransactionAmt(String grossTransactionAmt) {
		this.grossTransactionAmt = grossTransactionAmt;
	}
	public String getBeneficiaryBankName() {
		return beneficiaryBankName;
	}
	public void setBeneficiaryBankName(String beneficiaryBankName) {
		this.beneficiaryBankName = beneficiaryBankName;
	}

	public String getCardHolderType() {
		return cardHolderType;
	}

	public void setCardHolderType(String cardHolderType) {
		this.cardHolderType = cardHolderType;
	}

	public String getTransactionRegion() {
		return transactionRegion;
	}

	public void setTransactionRegion(String transactionRegion) {
		this.transactionRegion = transactionRegion;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public String getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(String totalAmount) {
		this.totalAmount = totalAmount;
	}

	public String getNodalCreditDate() {
		return nodalCreditDate;
	}

	public void setNodalCreditDate(String nodalCreditDate) {
		this.nodalCreditDate = nodalCreditDate;
	}

	public String getNodalPayoutInitiationDate() {
		return nodalPayoutInitiationDate;
	}

	public void setNodalPayoutInitiationDate(String nodalPayoutInitiationDate) {
		this.nodalPayoutInitiationDate = nodalPayoutInitiationDate;
	}



	public String getRemarks() {
		return remarks;
	}



	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}



	public String getAcqId() {
		return acqId;
	}



	public void setAcqId(String acqId) {
		this.acqId = acqId;
	}



	public String getArn() {
		return arn;
	}



	public void setArn(String arn) {
		this.arn = arn;
	}



	public String getRrn() {
		return rrn;
	}



	public String getPostSettledFlag() {
		return postSettledFlag;
	}



	public void setPostSettledFlag(String postSettledFlag) {
		this.postSettledFlag = postSettledFlag;
	}



	public void setRrn(String rrn) {
		this.rrn = rrn;
	}
	public String getSettlementCycle() {
		return settlementCycle;
	}
	public void setSettlementCycle(String settlementCycle) {
		this.settlementCycle = settlementCycle;
	}
	public String getUtrNo() {
		return utrNo;
	}
	public void setUtrNo(String utrNo) {
		this.utrNo = utrNo;
	}
	public String getTdrAmount() {
		return tdrAmount;
	}
	public void setTdrAmount(String tdrAmount) {
		this.tdrAmount = tdrAmount;
	}
	public String getGstOnTdrAmount() {
		return gstOnTdrAmount;
	}
	public void setGstOnTdrAmount(String gstOnTdrAmount) {
		this.gstOnTdrAmount = gstOnTdrAmount;
	}
	public String getReseller_commission() {
		return reseller_commission;
	}
	public void setReseller_commission(String reseller_commission) {
		this.reseller_commission = reseller_commission;
	}
	
}
