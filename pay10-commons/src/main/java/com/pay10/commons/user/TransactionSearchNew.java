package com.pay10.commons.user;

import java.io.Serializable;
import java.math.BigInteger;

/**
 * @author MMAD
 *
 */
public class TransactionSearchNew implements Serializable {

	private static final long serialVersionUID = -4691009307357010956L;

	private BigInteger transactionId;
	private String transactionIdString;
	private String payId;
	private String customerName;
	private String cardHolderName;
	private String customerEmail;
	private String customerPhone;
	private String txnType;
	private String paymentMethods;
	private String status;
	private String amount;
	private String totalAmount;
	private String orderId;
	private String currency;
	private String mopType;
	private String businessName;
	private String pgRefNum;
	private String acqId;
	private String rrn;
	private String arn;
	private String tDate;
	private String acquirerType;
	private String pgTxnMessage;

	private String transactionRegion;
	private String cardHolderType;

	private String surchargeFlag;
	private String oId;
	private String origTxnType;
	private String transactionCaptureDate;
	private String postSettledFlag;
	private String refundOrderId;
	private String cardMask;
	private String createDate;
	private String	cardNumber;
	private String dateFrom;
	private String merchants;
	
	private String acquirerSurchargeAmount;
	private String pgSurchargeAmount;
	private String acquirerGst;
	private String pgGst;
	private String ipaddress;
	private String udf4;
	private String udf5;
	private String udf6;
	private String TransactRef;
	private String srNo;
	private String totalPayoutFromNodal;
	private String totalAmountPayableToMerchant;

	
	
	public String getTotalPayoutFromNodal() {
		return totalPayoutFromNodal;
	}

	public void setTotalPayoutFromNodal(String totalPayoutFromNodal) {
		this.totalPayoutFromNodal = totalPayoutFromNodal;
	}

	public String getTotalAmountPayableToMerchant() {
		return totalAmountPayableToMerchant;
	}

	public void setTotalAmountPayableToMerchant(String totalAmountPayableToMerchant) {
		this.totalAmountPayableToMerchant = totalAmountPayableToMerchant;
	}


	private String pspName;
	

	public String getPspName() {
		return pspName;
	}

	public void setPspName(String pspName) {
		this.pspName = pspName;
	}


	private String updatedBy;
	private String updatedAt;


	private String splitPayment;
	
	
	public String getSrNo() {
		return srNo;
	}

	public void setSrNo(String srNo) {
		this.srNo = srNo;
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

	public String getTransactRef() {
		return TransactRef;
	}

	public void setTransactRef(String transactRef) {
		TransactRef = transactRef;
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

	public String getSplitPayment() {
		return splitPayment;
	}

	public void setSplitPayment(String splitPayment) {
		this.splitPayment = splitPayment;
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
	public BigInteger getTransactionId() {
		return transactionId;
	}
	public void setTransactionId(BigInteger transactionId) {
		this.transactionId = transactionId;
	}
	public String getTransactionIdString() {
		return transactionIdString;
	}
	public void setTransactionIdString(String transactionIdString) {
		this.transactionIdString = transactionIdString;
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
	public String getCurrency() {
		return currency;
	}
	public void setCurrency(String currency) {
		this.currency = currency;
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
	public String getPgRefNum() {
		return pgRefNum;
	}
	public void setPgRefNum(String pgRefNum) {
		this.pgRefNum = pgRefNum;
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
	public String gettDate() {
		return tDate;
	}
	public void settDate(String tDate) {
		this.tDate = tDate;
	}
	public String getAcquirerType() {
		return acquirerType;
	}
	public void setAcquirerType(String acquirerType) {
		this.acquirerType = acquirerType;
	}
	public String getPgTxnMessage() {
		return pgTxnMessage;
	}
	public void setPgTxnMessage(String pgTxnMessage) {
		this.pgTxnMessage = pgTxnMessage;
	}
	public String getTransactionRegion() {
		return transactionRegion;
	}
	public void setTransactionRegion(String transactionRegion) {
		this.transactionRegion = transactionRegion;
	}
	public String getCardHolderType() {
		return cardHolderType;
	}
	public void setCardHolderType(String cardHolderType) {
		this.cardHolderType = cardHolderType;
	}
	public String getSurchargeFlag() {
		return surchargeFlag;
	}
	public void setSurchargeFlag(String surchargeFlag) {
		this.surchargeFlag = surchargeFlag;
	}
	public String getoId() {
		return oId;
	}
	public void setoId(String oId) {
		this.oId = oId;
	}
	public String getOrigTxnType() {
		return origTxnType;
	}
	public void setOrigTxnType(String origTxnType) {
		this.origTxnType = origTxnType;
	}
	public String getTransactionCaptureDate() {
		return transactionCaptureDate;
	}
	public void setTransactionCaptureDate(String transactionCaptureDate) {
		this.transactionCaptureDate = transactionCaptureDate;
	}
	public String getPostSettledFlag() {
		return postSettledFlag;
	}
	public void setPostSettledFlag(String postSettledFlag) {
		this.postSettledFlag = postSettledFlag;
	}
	public String getRefundOrderId() {
		return refundOrderId;
	}
	public void setRefundOrderId(String refundOrderId) {
		this.refundOrderId = refundOrderId;
	}
	public String getCardMask() {
		return cardMask;
	}
	public void setCardMask(String cardMask) {
		this.cardMask = cardMask;
	}
	public String getCreateDate() {
		return createDate;
	}
	public void setCreateDate(String createDate) {
		this.createDate = createDate;
	}
	public String getCardNumber() {
		return cardNumber;
	}
	public void setCardNumber(String cardNumber) {
		this.cardNumber = cardNumber;
	}
	public String getDateFrom() {
		return dateFrom;
	}
	public void setDateFrom(String dateFrom) {
		this.dateFrom = dateFrom;
	}
	public String getMerchants() {
		return merchants;
	}
	public void setMerchants(String merchants) {
		this.merchants = merchants;
	}
	public String getTotalAmount() {
		return totalAmount;
	}
	public void setTotalAmount(String totalAmount) {
		this.totalAmount = totalAmount;
	}

	public String getIpaddress() {
		return ipaddress;
	}

	public void setIpaddress(String ipaddress) {
		this.ipaddress = ipaddress;
	}

	
	public Object[] myCsvMethod() {
		  Object[] objectArray = new Object[21];
		  
		 
		  objectArray[0] = srNo;
		  objectArray[1] = transactionIdString;
		  objectArray[2] = pgRefNum;
		  objectArray[3] = merchants;
		  objectArray[4] = dateFrom;
		  objectArray[5] = orderId;
		  objectArray[6] = refundOrderId;
		  objectArray[7] = mopType;
		  objectArray[8] = paymentMethods;
		  objectArray[9] = txnType;
		  objectArray[10] = status;
		  objectArray[11] = amount;
		  objectArray[12] = totalAmount;
		  objectArray[13] = payId;
		  objectArray[14] = customerEmail;
		  objectArray[15] = customerPhone;
		  objectArray[16] = acquirerType;
		  objectArray[17] = ipaddress;
		  objectArray[18] = cardMask;
		  objectArray[19] = rrn;
		  objectArray[20] = splitPayment;		
		  return objectArray;
		}
	public Object[] myCsvMethod1() {
		  Object[] objectArray = new Object[27];
		  
		 
		  objectArray[0] = srNo;
		  objectArray[1] = transactionIdString;
		  objectArray[2] = payId;
		  objectArray[3] = pgRefNum;
		  objectArray[4] = merchants;
		  objectArray[5] = dateFrom;
		  objectArray[6] = orderId;
		  objectArray[7] = refundOrderId;
		  objectArray[8] = mopType;
		  objectArray[9] = paymentMethods;
		  objectArray[10] = txnType;
		  objectArray[11] = status;
		  objectArray[12] = totalAmount;
		  objectArray[13] = pgSurchargeAmount;
		  objectArray[14] = pgGst;
		  objectArray[15] = acquirerSurchargeAmount;
		  objectArray[16] = acquirerGst;
		  objectArray[17] = totalAmountPayableToMerchant;
		  objectArray[18] = totalPayoutFromNodal;
		  objectArray[19] = surchargeFlag;
		  objectArray[20] = customerEmail;
		  objectArray[21] = customerPhone;
		  objectArray[22] = acquirerType;
		  objectArray[23] = ipaddress;
		  objectArray[24] = cardMask;
		  objectArray[25] = rrn;
		  objectArray[26] = splitPayment;
		  return objectArray;
		}
	
	
	 
	
	@Override
	public String toString() {
		return "TransactionSearchNew [transactionId=" + transactionId + ", transactionIdString=" + transactionIdString
				+ ", payId=" + payId + ", customerName=" + customerName + ", cardHolderName=" + cardHolderName
				+ ", customerEmail=" + customerEmail + ", customerPhone=" + customerPhone + ", txnType=" + txnType
				+ ", paymentMethods=" + paymentMethods + ", status=" + status + ", amount=" + amount + ", totalAmount="
				+ totalAmount + ", orderId=" + orderId + ", currency=" + currency + ", mopType=" + mopType
				+ ", businessName=" + businessName + ", pgRefNum=" + pgRefNum + ", acqId=" + acqId + ", rrn=" + rrn
				+ ", arn=" + arn + ", tDate=" + tDate + ", acquirerType=" + acquirerType + ", pgTxnMessage="
				+ pgTxnMessage + ", transactionRegion=" + transactionRegion + ", cardHolderType=" + cardHolderType
				+ ", surchargeFlag=" + surchargeFlag + ", oId=" + oId + ", origTxnType=" + origTxnType
				+ ", transactionCaptureDate=" + transactionCaptureDate + ", postSettledFlag=" + postSettledFlag
				+ ", refundOrderId=" + refundOrderId + ", cardMask=" + cardMask + ", createDate=" + createDate
				+ ", cardNumber=" + cardNumber + ", dateFrom=" + dateFrom + ", merchants=" + merchants
				+ ", acquirerSurchargeAmount=" + acquirerSurchargeAmount + ", pgSurchargeAmount=" + pgSurchargeAmount
				+ ", acquirerGst=" + acquirerGst + ", pgGst=" + pgGst + ", ipaddress=" + ipaddress + "]";
	}
	
	

	
	
	
}
