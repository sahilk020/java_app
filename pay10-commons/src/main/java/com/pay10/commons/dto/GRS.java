package com.pay10.commons.dto;

public class GRS {
	private String id;
	private String GrsDesc;
	private String GrsId;
	private String grsTittle;
	private String PgrefNum;
	private String amount;
	private String totalAmount;
	private String status;
	private String orderId;
	private String merchantName;
	private String createdDate;
	private String createdBy;
	private String updatedBy;
	private String updatedAt;
	private String txnDate;
	
	private String PaymentMethod;
	private String mopType;
	private String customerName;
	private String customerPhone;
	private String payId;
	private String fileName;
	
	
	
	
	
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getGrsTittle() {
		return grsTittle;
	}
	public void setGrsTittle(String grsTittle) {
		this.grsTittle = grsTittle;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getGrsDesc() {
		return GrsDesc;
	}
	public void setGrsDesc(String grsDesc) {
		GrsDesc = grsDesc;
	}
	public String getGrsId() {
		return GrsId;
	}
	public void setGrsId(String grsId) {
		GrsId = grsId;
	}
	public String getPgrefNum() {
		return PgrefNum;
	}
	public void setPgrefNum(String pgrefNum) {
		PgrefNum = pgrefNum;
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
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getOrderId() {
		return orderId;
	}
	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}
	public String getMerchantName() {
		return merchantName;
	}
	public void setMerchantName(String merchantName) {
		this.merchantName = merchantName;
	}
	public String getCreatedDate() {
		return createdDate;
	}
	public void setCreatedDate(String createdDate) {
		this.createdDate = createdDate;
	}
	public String getCreatedBy() {
		return createdBy;
	}
	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
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
	public String getTxnDate() {
		return txnDate;
	}
	public void setTxnDate(String txnDate) {
		this.txnDate = txnDate;
	}
	public String getPaymentMethod() {
		return PaymentMethod;
	}
	public void setPaymentMethod(String paymentMethod) {
		PaymentMethod = paymentMethod;
	}
	public String getMopType() {
		return mopType;
	}
	public void setMopType(String mopType) {
		this.mopType = mopType;
	}
	public String getCustomerName() {
		return customerName;
	}
	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}
	public String getCustomerPhone() {
		return customerPhone;
	}
	public void setCustomerPhone(String customerPhone) {
		this.customerPhone = customerPhone;
	}
	public String getPayId() {
		return payId;
	}
	public void setPayId(String payId) {
		this.payId = payId;
	}
	
	
	
	public Object[] myCsvMethodDownloadPaymentsReport() {
		Object[] objectArray = new Object[48];

		objectArray[0] = PgrefNum;
		objectArray[1] = GrsId;
		objectArray[2] = merchantName;
		objectArray[3] = grsTittle;
		objectArray[4] = amount;
		objectArray[5] = totalAmount;
		objectArray[6] = status;
		objectArray[7] = orderId;
		objectArray[8] = createdDate;		
		objectArray[9] = createdBy;
		objectArray[10] = txnDate;
		objectArray[11] = PaymentMethod;
		objectArray[12] = mopType;
		objectArray[13] = payId;
		objectArray[14] = customerName;
		objectArray[15] = customerPhone;
		objectArray[16] = updatedBy;
		objectArray[17] = updatedAt;
		
		return objectArray;

	}
	
	
}
