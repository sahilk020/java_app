package com.pay10.commons.user;

import java.util.ArrayList;
import java.util.List;

public class TransactionSearchDownloadObject {
	private String pspName;
	private String  currency;

	private String payId;
	private String transactionId;
	private String pgRefNum;
	private String transactionRegion;
	private String merchants;
	private String postSettledFlag;
	private String deltaRefundFlag;
	private String txnType;
	private String acquirerType;
	private String paymentMethods;
	private String status;
	private String dateFrom;
	private String amount;
	private String orderId;
	private String totalAmount;
	private String acqId;
	private String rrn;
	private String srNo;
	private String refundOrderId;
	private String refundFlag;
	private String mopType;
	private String acquirerSurchargeAmount;
	private String pgSurchargeAmount;
	private String acquirerGst;
	private String pgGst;
	private String ipAddress;
	private String cardMask;
	private String customerEmail;
	private String customerPhone;
	private String acquirerResponseMessage;
	private String udf4;
	private String udf5;
	private String udf6;
	private String utrNo;
	private String cardHolderType;
	private String bankrefno;
	private String acquirerTdrSC;
	private String pgTdrSC;
	private String dateindex;
	private String grossAmount;
	private String surchargeFlag;
	private String totatTransaction;
	private String totatSurchargeTransactionCount;
	private String totatMdrTransaction;
	
	private String totatTransactionAmount;
	private String totatSurchargeTransactionAmount;
	private String totatMdrTransactionAmount;
	private String mdrCharge;
	private String gst;
	private String counterNumber;
	
	
	public String getPspName() {
		return pspName;
	}

	public void setPspName(String pspName) {
		this.pspName = pspName;
	}
	
	
	private String total="total";
	private String totalTransactionCount;
	private String totalTransactionAmount;
	private String totalSurchargeTransactionCount;
	private String totalSurchargeTransactionAmount;
	private String totalMdrTransactionCount;
	private String totalMdrTransactionAmount;
	private String totalMdrCharge;
	private String totalGst;
	private String totalNetAmount;
	
	
	
	public String getTotal() {
		return total;
	}

	public void setTotal(String total) {
		this.total = total;
	}

	public String getTotalTransactionCount() {
		return totalTransactionCount;
	}

	public void setTotalTransactionCount(String totalTransactionCount) {
		this.totalTransactionCount = totalTransactionCount;
	}

	public String getTotalTransactionAmount() {
		return totalTransactionAmount;
	}

	public void setTotalTransactionAmount(String totalTransactionAmount) {
		this.totalTransactionAmount = totalTransactionAmount;
	}

	public String getTotalSurchargeTransactionCount() {
		return totalSurchargeTransactionCount;
	}

	public void setTotalSurchargeTransactionCount(String totalSurchargeTransactionCount) {
		this.totalSurchargeTransactionCount = totalSurchargeTransactionCount;
	}

	public String getTotalSurchargeTransactionAmount() {
		return totalSurchargeTransactionAmount;
	}

	public void setTotalSurchargeTransactionAmount(String totalSurchargeTransactionAmount) {
		this.totalSurchargeTransactionAmount = totalSurchargeTransactionAmount;
	}

	public String getTotalMdrTransactionCount() {
		return totalMdrTransactionCount;
	}

	public void setTotalMdrTransactionCount(String totalMdrTransactionCount) {
		this.totalMdrTransactionCount = totalMdrTransactionCount;
	}

	public String getTotalMdrTransactionAmount() {
		return totalMdrTransactionAmount;
	}

	public void setTotalMdrTransactionAmount(String totalMdrTransactionAmount) {
		this.totalMdrTransactionAmount = totalMdrTransactionAmount;
	}

	public String getTotalMdrCharge() {
		return totalMdrCharge;
	}

	public void setTotalMdrCharge(String totalMdrCharge) {
		this.totalMdrCharge = totalMdrCharge;
	}

	public String getTotalGst() {
		return totalGst;
	}

	public void setTotalGst(String totalGst) {
		this.totalGst = totalGst;
	}

	public String getTotalNetAmount() {
		return totalNetAmount;
	}

	public void setTotalNetAmount(String totalNetAmount) {
		this.totalNetAmount = totalNetAmount;
	}

	public String getCounterNumber() {
		return counterNumber;
	}

	public void setCounterNumber(String counterNumber) {
		this.counterNumber = counterNumber;
	}

	public String getMdrCharge() {
		return mdrCharge;
	}

	public void setMdrCharge(String mdrCharge) {
		this.mdrCharge = mdrCharge;
	}

	public String getGst() {
		return gst;
	}

	public void setGst(String gst) {
		this.gst = gst;
	}

	public String getTotatTransaction() {
		return totatTransaction;
	}

	public void setTotatTransaction(String totatTransaction) {
		this.totatTransaction = totatTransaction;
	}

	

	public String getTotatSurchargeTransactionCount() {
		return totatSurchargeTransactionCount;
	}

	public void setTotatSurchargeTransactionCount(String totatSurchargeTransactionCount) {
		this.totatSurchargeTransactionCount = totatSurchargeTransactionCount;
	}

	public String getTotatMdrTransaction() {
		return totatMdrTransaction;
	}

	public void setTotatMdrTransaction(String totatMdrTransaction) {
		this.totatMdrTransaction = totatMdrTransaction;
	}

	public String getTotatTransactionAmount() {
		return totatTransactionAmount;
	}

	public void setTotatTransactionAmount(String totatTransactionAmount) {
		this.totatTransactionAmount = totatTransactionAmount;
	}

	public String getTotatSurchargeTransactionAmount() {
		return totatSurchargeTransactionAmount;
	}

	public void setTotatSurchargeTransactionAmount(String totatSurchargeTransactionAmount) {
		this.totatSurchargeTransactionAmount = totatSurchargeTransactionAmount;
	}

	public String getTotatMdrTransactionAmount() {
		return totatMdrTransactionAmount;
	}

	public void setTotatMdrTransactionAmount(String totatMdrTransactionAmount) {
		this.totatMdrTransactionAmount = totatMdrTransactionAmount;
	}

	public String getSurchargeFlag() {
		return surchargeFlag;
	}

	public void setSurchargeFlag(String surchargeFlag) {
		this.surchargeFlag = surchargeFlag;
	}

	public String getGrossAmount() {
		return grossAmount;
	}

	public void setGrossAmount(String grossAmount) {
		this.grossAmount = grossAmount;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public String getDateindex() {
		return dateindex;
	}

	public void setDateindex(String dateindex) {
		this.dateindex = dateindex;
	}

	public String getAcquirerTdrSC() {
		return acquirerTdrSC;
	}

	public void setAcquirerTdrSC(String acquirerTdrSC) {
		this.acquirerTdrSC = acquirerTdrSC;
	}

	public String getPgTdrSC() {
		return pgTdrSC;
	}

	public void setPgTdrSC(String pgTdrSC) {
		this.pgTdrSC = pgTdrSC;
	}

	public String getCardHolderType() {
		return cardHolderType;
	}

	public void setCardHolderType(String cardHolderType) {
		this.cardHolderType = cardHolderType;
	}

	public String getUtrNo() {
		return utrNo;
	}

	public void setUtrNo(String utrNo) {
		this.utrNo = utrNo;
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


	public String getBankrefno() {
		return bankrefno;
	}

	public void setBankrefno(String bankrefno) {
		this.bankrefno = bankrefno;
	}

	public String getUdf6() {
		return udf6;
	}

	public void setUdf6(String udf6) {
		this.udf6 = udf6;
	}

	public String getAcquirerResponseMessage() {
		return acquirerResponseMessage;
	}

	public void setAcquirerResponseMessage(String acquirerResponseMessage) {
		this.acquirerResponseMessage = acquirerResponseMessage;
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

	public String getMopType() {
		return mopType;
	}

	public void setMopType(String mopType) {
		this.mopType = mopType;
	}

	public String getRefundOrderId() {
		return refundOrderId;
	}

	public void setRefundOrderId(String refundOrderId) {
		this.refundOrderId = refundOrderId;
	}

	public String getRefundFlag() {
		return refundFlag;
	}

	public void setRefundFlag(String refundFlag) {
		this.refundFlag = refundFlag;
	}

	public String getPayId() {
		return payId;
	}

	public void setPayId(String payId) {
		this.payId = payId;
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

	public String getTransactionRegion() {
		return transactionRegion;
	}

	public void setTransactionRegion(String transactionRegion) {
		this.transactionRegion = transactionRegion;
	}

	public String getMerchants() {
		return merchants;
	}

	public void setMerchants(String merchants) {
		this.merchants = merchants;
	}

	public String getPostSettledFlag() {
		return postSettledFlag;
	}

	public void setPostSettledFlag(String postSettledFlag) {
		this.postSettledFlag = postSettledFlag;
	}

	public String getDeltaRefundFlag() {
		return deltaRefundFlag;
	}

	public void setDeltaRefundFlag(String deltaRefundFlag) {
		this.deltaRefundFlag = deltaRefundFlag;
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

	public String getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(String totalAmount) {
		this.totalAmount = totalAmount;
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

	public String getSrNo() {
		return srNo;
	}

	public void setSrNo(String srNo) {
		this.srNo = srNo;
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public String getCardMask() {
		return cardMask;
	}

	public void setCardMask(String cardMask) {
		this.cardMask = cardMask;
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

	public List<Object> myCsvMethodDownloadPaymentsReport(boolean splitSegment, boolean needToShowAcqFields) {
		List<Object> objArray = new ArrayList<>();
		objArray.add(srNo);
		objArray.add(transactionId);
		objArray.add(pgRefNum);
		objArray.add(merchants);
		if (needToShowAcqFields) {
			objArray.add(acquirerType);
		}
		objArray.add(dateFrom);
		objArray.add(orderId);
		if (needToShowAcqFields) {
			objArray.add(paymentMethods);
		}
		objArray.add(txnType);
		objArray.add(status);
		objArray.add(transactionRegion);
		objArray.add(amount);
		objArray.add(totalAmount);
		//if (!splitSegment) {
			//objArray.add(deltaRefundFlag);
			//objArray.add(acqId);
		//}
		objArray.add(rrn);
		//if (!splitSegment) {
		//	objArray.add(postSettledFlag);
		//}
		//objArray.add(refundOrderId);
//		if (!splitSegment) {
//			objArray.add(refundFlag);
//		}
		if (needToShowAcqFields) {
			objArray.add(mopType);
		}
		if (!splitSegment) {
			objArray.add(ipAddress);
		}
		objArray.add(cardMask);
		objArray.add(customerEmail);
		objArray.add(customerPhone);
		if (!splitSegment) {
			objArray.add(acquirerResponseMessage);
		}

		if (!splitSegment) {
			objArray.add(utrNo);
		}
		objArray.add(cardHolderType);
		objArray.add(bankrefno);
		objArray.add(pspName);
		objArray.add(currency);


		return objArray;
	}

	public Object[] myCsvMethodDownloadPaymentsReport() {
		Object[] objectArray = new Object[33];

		objectArray[0] = srNo;
		objectArray[1] = transactionId;
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
		objectArray[17] = refundOrderId;
		objectArray[18] = refundFlag;
		objectArray[19] = mopType;
		objectArray[20] = ipAddress;
		objectArray[21] = cardMask;
		objectArray[22] = customerEmail;
		objectArray[23] = customerPhone;
		objectArray[24] = acquirerResponseMessage;
		objectArray[25] = udf4;
		objectArray[26] = udf5;
		objectArray[27] = udf6;
		objectArray[28] = utrNo;
		objectArray[29] = cardHolderType;
		objectArray[30] = bankrefno;
		objectArray[31] = pspName;
		objectArray[32]= currency;



		return objectArray;

	}

	public Object[] getBsesMisReport() {
		Object[] objectArray = new Object[11];

		objectArray[0] = counterNumber;
		objectArray[1] = paymentMethods;
		objectArray[2] = totalTransactionCount;
		objectArray[3] = totalTransactionAmount;
		objectArray[4] = totalSurchargeTransactionCount;
		objectArray[5] = totalSurchargeTransactionAmount;
		objectArray[6] = totalMdrTransactionCount;
		objectArray[7] = totalMdrTransactionAmount;
		objectArray[8] = totalMdrCharge;
		objectArray[9] = totalGst;
		objectArray[10] = totalNetAmount;
	
		return objectArray;

	}
	
	public Object[] getBsesMisReportTotal() {
		Object[] objectArray = new Object[10];

		objectArray[0] = total;
		objectArray[1] = totalTransactionCount;
		objectArray[2] = totalTransactionAmount;
		objectArray[3] = totalSurchargeTransactionCount;
		objectArray[4] = totalSurchargeTransactionAmount;
		objectArray[5] = totalMdrTransactionCount;
		objectArray[6] = totalMdrTransactionAmount;
		objectArray[7] = totalMdrCharge;
		objectArray[8] = totalGst;
		objectArray[9] = totalNetAmount;
		
	
		return objectArray;

	}
	
	public Object[] getBsesMisReportTotalSummary() {
		Object[] objectArray = new Object[10];

		objectArray[0] = total;
		objectArray[1] = totalTransactionCount;
		
		return objectArray;

	}

	public Object[] myCsvMethodDownloadPaymentsReportSuperAdmin() {
		Object[] objectArray = new Object[28];

		objectArray[0] = srNo;
		objectArray[1] = transactionId;
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
		objectArray[17] = refundOrderId;
		objectArray[18] = refundFlag;
		objectArray[19] = mopType;
		objectArray[20] = postSettledFlag;
		objectArray[21] = refundOrderId;
		objectArray[22] = refundFlag;
		objectArray[23] = mopType;
		objectArray[24] = ipAddress;
		objectArray[25] = cardMask;
		objectArray[26] = customerEmail;
		objectArray[27] = customerPhone;
		return objectArray;

	}
	
	public List<Object> myCsvMethodDownloadSearchPaymentsReport(boolean splitSegment, boolean needToShowAcqFields) {
		List<Object> objArray = new ArrayList<>();
		objArray.add(srNo);
		objArray.add(transactionId);
		objArray.add(pgRefNum);
		objArray.add(merchants);
//		if (needToShowAcqFields) {
//			objArray.add(acquirerType);
//		}
		objArray.add(dateFrom);
		objArray.add(orderId);
		
			objArray.add(paymentMethods);
		
		objArray.add(txnType);
		objArray.add(status);
		objArray.add(transactionRegion);
		objArray.add(amount);
		objArray.add(totalAmount);
		if (!splitSegment) {
			objArray.add(deltaRefundFlag);
			objArray.add(acqId);
		}
		objArray.add(rrn);
		if (!splitSegment) {
			objArray.add(postSettledFlag);
		}
		objArray.add(refundOrderId);
		if (!splitSegment) {
			objArray.add(refundFlag);
		}
		
			objArray.add(mopType);
		
		if (!splitSegment) {
			objArray.add(ipAddress);
		}
		objArray.add(cardMask);
		objArray.add(customerEmail);
		objArray.add(customerPhone);
		if (!splitSegment) {
			objArray.add(acquirerResponseMessage);
		}
	//	objArray.add(udf4);
	//	objArray.add(udf5);
	//	objArray.add(udf6);
		if (!splitSegment) {
			objArray.add(utrNo);
		}
		objArray.add(cardHolderType);
		objArray.add(bankrefno);
		objArray.add(pspName);

		return objArray;
	}

	@Override
	public String toString() {
		return "TransactionSearchDownloadObject{" +
				"pspName='" + pspName + '\'' +
				", currency='" + currency + '\'' +
				", payId='" + payId + '\'' +
				", transactionId='" + transactionId + '\'' +
				", pgRefNum='" + pgRefNum + '\'' +
				", transactionRegion='" + transactionRegion + '\'' +
				", merchants='" + merchants + '\'' +
				", postSettledFlag='" + postSettledFlag + '\'' +
				", deltaRefundFlag='" + deltaRefundFlag + '\'' +
				", txnType='" + txnType + '\'' +
				", acquirerType='" + acquirerType + '\'' +
				", paymentMethods='" + paymentMethods + '\'' +
				", status='" + status + '\'' +
				", dateFrom='" + dateFrom + '\'' +
				", amount='" + amount + '\'' +
				", orderId='" + orderId + '\'' +
				", totalAmount='" + totalAmount + '\'' +
				", acqId='" + acqId + '\'' +
				", rrn='" + rrn + '\'' +
				", srNo='" + srNo + '\'' +
				", refundOrderId='" + refundOrderId + '\'' +
				", refundFlag='" + refundFlag + '\'' +
				", mopType='" + mopType + '\'' +
				", acquirerSurchargeAmount='" + acquirerSurchargeAmount + '\'' +
				", pgSurchargeAmount='" + pgSurchargeAmount + '\'' +
				", acquirerGst='" + acquirerGst + '\'' +
				", pgGst='" + pgGst + '\'' +
				", ipAddress='" + ipAddress + '\'' +
				", cardMask='" + cardMask + '\'' +
				", customerEmail='" + customerEmail + '\'' +
				", customerPhone='" + customerPhone + '\'' +
				", acquirerResponseMessage='" + acquirerResponseMessage + '\'' +
				", udf4='" + udf4 + '\'' +
				", udf5='" + udf5 + '\'' +
				", udf6='" + udf6 + '\'' +
				", utrNo='" + utrNo + '\'' +
				", cardHolderType='" + cardHolderType + '\'' +
				", bankrefno='" + bankrefno + '\'' +
				", acquirerTdrSC='" + acquirerTdrSC + '\'' +
				", pgTdrSC='" + pgTdrSC + '\'' +
				", dateindex='" + dateindex + '\'' +
				", grossAmount='" + grossAmount + '\'' +
				", surchargeFlag='" + surchargeFlag + '\'' +
				", totatTransaction='" + totatTransaction + '\'' +
				", totatSurchargeTransactionCount='" + totatSurchargeTransactionCount + '\'' +
				", totatMdrTransaction='" + totatMdrTransaction + '\'' +
				", totatTransactionAmount='" + totatTransactionAmount + '\'' +
				", totatSurchargeTransactionAmount='" + totatSurchargeTransactionAmount + '\'' +
				", totatMdrTransactionAmount='" + totatMdrTransactionAmount + '\'' +
				", mdrCharge='" + mdrCharge + '\'' +
				", gst='" + gst + '\'' +
				", counterNumber='" + counterNumber + '\'' +
				", total='" + total + '\'' +
				", totalTransactionCount='" + totalTransactionCount + '\'' +
				", totalTransactionAmount='" + totalTransactionAmount + '\'' +
				", totalSurchargeTransactionCount='" + totalSurchargeTransactionCount + '\'' +
				", totalSurchargeTransactionAmount='" + totalSurchargeTransactionAmount + '\'' +
				", totalMdrTransactionCount='" + totalMdrTransactionCount + '\'' +
				", totalMdrTransactionAmount='" + totalMdrTransactionAmount + '\'' +
				", totalMdrCharge='" + totalMdrCharge + '\'' +
				", totalGst='" + totalGst + '\'' +
				", totalNetAmount='" + totalNetAmount + '\'' +
				'}';
	}
}
