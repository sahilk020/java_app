package com.pay10.commons.user;

import java.io.Serializable;

import com.pay10.commons.util.InvoiceStatus;
import com.pay10.commons.util.InvoiceType;

public class Invoice implements Serializable {

	private static final long serialVersionUID = 81100892505476449L;

	private Long id;
	private String invoiceId;
	private String payId;
	private String businessName;
	private String invoiceNo;
	private String name;
	private String city;
	private String country;
	private String state;
	private String zip;
	private String phone;
	private String email;
	private String address;
	private String productName;
	private String productDesc;
	private String quantity;
	private String amount;
	private String serviceCharge; // Not required
	private String totalAmount;
	private String currencyCode;
	private String region;
	private String expiresDay;
	private String expiresHour;
	private String expiryTime;
	private String createDate;
	private String updateDate;
	private String gst;
	private String gstAmount;

	private String saltKey; // Not required

	private String returnUrl;
	private String recipientMobile;
	private String messageBody;
	private String shortUrl;
	private InvoiceType invoiceType;
	private InvoiceStatus invoiceStatus;
	private String invoiceUrl;
	private boolean tncStatus;
	private String payText;
	private String invoiceText;
	// Added By Sweety
	private boolean merchantConsent;
	private boolean customerConsent;

	private String status;

	public Invoice() {
	}

	public Invoice(String email, String phone) {
		this.email = email;
		this.phone = phone;

	}

	public String getPayId() {
		return payId;
	}

	public void setPayId(String payId) {
		this.payId = payId;
	}
	public String getBusinessName() {
		return businessName;
	}
	public void setBusinessName(String businessName) {
		this.businessName = businessName;
	}
	public String getInvoiceNo() {
		return invoiceNo;
	}
	public void setInvoiceNo(String invoiceNo) {
		this.invoiceNo = invoiceNo;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getZip() {
		return zip;
	}
	public void setZip(String zip) {
		this.zip = zip;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getProductName() {
		return productName;
	}
	public void setProductName(String productName) {
		this.productName = productName;
	}
	public String getProductDesc() {
		return productDesc;
	}
	public void setProductDesc(String productDesc) {
		this.productDesc = productDesc;
	}
	public String getQuantity() {
		return quantity;
	}
	public void setQuantity(String quantity) {
		this.quantity = quantity;
	}
	public String getAmount() {
		return amount;
	}
	public void setAmount(String amount) {
		this.amount = amount;
	}
	public String getServiceCharge() {
		return serviceCharge;
	}
	public void setServiceCharge(String serviceCharge) {
		this.serviceCharge = serviceCharge;
	}
	public String getTotalAmount() {
		return totalAmount;
	}
	public void setTotalAmount(String totalAmount) {
		this.totalAmount = totalAmount;
	}
	public String getCurrencyCode() {
		return currencyCode;
	}
	public void setCurrencyCode(String currencyCode) {
		this.currencyCode = currencyCode;
	}

	public String getRegion() {
		return region;
	}

	public void setRegion(String region) {
		this.region = region;
	}

	public String getExpiresDay() {
		return expiresDay;
	}
	public String getExpiresHour() {
		return expiresHour;
	}
	public void setExpiresHour(String expiresHour) {
		this.expiresHour = expiresHour;
	}
	public void setExpiresDay(String expiresDay) {
		this.expiresDay = expiresDay;
	}
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getCreateDate() {
		return createDate;
	}
	public void setCreateDate(String createDate) {
		this.createDate = createDate;
	}
	public String getUpdateDate() {
		return updateDate;
	}
	public void setUpdateDate(String updateDate) {
		this.updateDate = updateDate;
	}
	public String getSaltKey() {
		return saltKey;
	}
	public void setSaltKey(String saltKey) {
		this.saltKey = saltKey;
	}
	public String getInvoiceId() {
		return invoiceId;
	}
	public void setInvoiceId(String invoiceId) {
		this.invoiceId = invoiceId;
	}
	public String getReturnUrl() {
		return returnUrl;
	}
	public void setReturnUrl(String returnUrl) {
		this.returnUrl = returnUrl;
	}
	public String getRecipientMobile() {
		return recipientMobile;
	}
	public void setRecipientMobile(String recipientMobile) {
		this.recipientMobile = recipientMobile;
	}
	public String getMessageBody() {
		return messageBody;
	}
	public void setMessageBody(String messageBody) {
		this.messageBody = messageBody;
	}

	public String getShortUrl() {
		return shortUrl;
	}
	public void setShortUrl(String shortUrl) {
		this.shortUrl = shortUrl;
	}

	public String getGst() {
		return gst;
	}
	public void setGst(String gst) {
		this.gst = gst;
	}


	public String getGstAmount() {
		return gstAmount;
	}


	public void setGstAmount(String gstAmount) {
		this.gstAmount = gstAmount;
	}


	public String getInvoiceUrl() {
		return invoiceUrl;
	}


	public void setInvoiceUrl(String invoiceUrl) {
		this.invoiceUrl = invoiceUrl;
	}


	public String getExpiryTime() {
		return expiryTime;
	}


	public void setExpiryTime(String expiryTime) {
		this.expiryTime = expiryTime;
	}


	public InvoiceType getInvoiceType() {
		return invoiceType;
	}


	public void setInvoiceType(InvoiceType invoiceType) {
		this.invoiceType = invoiceType;
	}


	public InvoiceStatus getInvoiceStatus() {
		return invoiceStatus;
	}


	public void setInvoiceStatus(InvoiceStatus invoiceStatus) {
		this.invoiceStatus = invoiceStatus;
	}

	public boolean isTncStatus() {
		return tncStatus;
	}

	public void setTncStatus(boolean tncStatus) {
		this.tncStatus = tncStatus;
	}

	public String getPayText() {
		return payText;
	}

	public void setPayText(String payText) {
		this.payText = payText;
	}

	public String getInvoiceText() {
		return invoiceText;
	}

	public void setInvoiceText(String invoiceText) {
		this.invoiceText = invoiceText;
	}

	public boolean isMerchantConsent() {
		return merchantConsent;
	}

	public void setMerchantConsent(boolean merchantConsent) {
		this.merchantConsent = merchantConsent;
	}

	public boolean isCustomerConsent() {
		return customerConsent;
	}

	public void setCustomerConsent(boolean customerConsent) {
		this.customerConsent = customerConsent;
	}
	
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

}
