package com.pay10.commons.user;

public class BSESMonthlyInvoiceReportDetails {
	
	private String pgRefNo;
	private String payId;
	private String merchantName;
	private String transactionDate;
	private String orderId;
	private String paymentType;
	private String txnType;
	private String mopType;
	private String udf9;
	private String udf10;
	private String udf11;
	private String amount;
	private String invoiceValue;
	private String invoiceGst;
	private String totalInvoiceValue;
	
	public String getPgRefNo() {
		return pgRefNo;
	}
	public void setPgRefNo(String pgRefNo) {
		this.pgRefNo = pgRefNo;
	}
	public String getPayId() {
		return payId;
	}
	public void setPayId(String payId) {
		this.payId = payId;
	}
	public String getMerchantName() {
		return merchantName;
	}
	public void setMerchantName(String merchantName) {
		this.merchantName = merchantName;
	}
	public String getTransactionDate() {
		return transactionDate;
	}
	public void setTransactionDate(String transactionDate) {
		this.transactionDate = transactionDate;
	}
	public String getOrderId() {
		return orderId;
	}
	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}
	public String getPaymentType() {
		return paymentType;
	}
	public void setPaymentType(String paymentType) {
		this.paymentType = paymentType;
	}
	public String getTxnType() {
		return txnType;
	}
	public void setTxnType(String txnType) {
		this.txnType = txnType;
	}
	public String getMopType() {
		return mopType;
	}
	public void setMopType(String mopType) {
		this.mopType = mopType;
	}
	public String getUdf9() {
		return udf9;
	}
	public void setUdf9(String udf9) {
		this.udf9 = udf9;
	}
	public String getUdf10() {
		return udf10;
	}
	public void setUdf10(String udf10) {
		this.udf10 = udf10;
	}
	public String getUdf11() {
		return udf11;
	}
	public void setUdf11(String udf11) {
		this.udf11 = udf11;
	}
	public String getAmount() {
		return amount;
	}
	public void setAmount(String amount) {
		this.amount = amount;
	}
	public String getInvoiceValue() {
		return invoiceValue;
	}
	public void setInvoiceValue(String invoiceValue) {
		this.invoiceValue = invoiceValue;
	}
	public String getInvoiceGst() {
		return invoiceGst;
	}
	public void setInvoiceGst(String gst) {
		this.invoiceGst = gst;
	}
	public String getTotalInvoiceValue() {
		return totalInvoiceValue;
	}
	public void setTotalInvoiceValue(String totalInvoiceValue) {
		this.totalInvoiceValue = totalInvoiceValue;
	}
	public Object[] myCsvMethodDownloadPaymentsReport() {
		Object[] objectArray = new Object[15];
		  
		objectArray[0] = pgRefNo;
		objectArray[1] = payId;
		objectArray[2] = merchantName;
		objectArray[3] = transactionDate;
		objectArray[4] = orderId;
		objectArray[5] = paymentType;
		objectArray[6] = txnType;
		objectArray[7] = mopType;
		objectArray[8] = udf9;
		objectArray[9] = udf10;
		objectArray[10] = udf11;
		objectArray[11] = amount;
		objectArray[12] = invoiceValue;
		objectArray[13] = invoiceGst;
		objectArray[14] = totalInvoiceValue;
		
		  return objectArray;
		
	}
}

