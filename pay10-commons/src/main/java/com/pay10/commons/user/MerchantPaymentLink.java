package com.pay10.commons.user;

import java.io.Serializable;
import java.util.Map;

import com.pay10.commons.util.InvoiceStatus;
import com.pay10.commons.util.InvoiceType;

public class MerchantPaymentLink implements Serializable {

	private static final long serialVersionUID = 81100892505476449L;

	private Long id;
	private String payId;
	private String businessName;
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
	private String currencyName;
	private String expiresDay;
	private String expiresHour;
	private String expiryTime;
	private String createDate;
	private String updateDate;
	private String gst;
	private String gstAmount;
	private  Map<String, String> currencies;

	
	private String saltKey; // Not required
	
	private String returnUrl;
	private String recipientMobile;
	private String messageBody;
	private String shortUrl;
	private String paymentNumber;
	
	private String paymentActionUrl;
	
	private String orderId;
	
	public MerchantPaymentLink(){}
	
	
	public  MerchantPaymentLink(String email, String phone) {
	this.email=email;
	this.phone=phone;

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

	public String getExpiryTime() {
		return expiryTime;
	}


	public void setExpiryTime(String expiryTime) {
		this.expiryTime = expiryTime;
	}


	public String getPaymentNumber() {
		return paymentNumber;
	}


	public void setPaymentNumber(String paymentNumber) {
		this.paymentNumber = paymentNumber;
	}


	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public String getPaymentActionUrl() {
		return paymentActionUrl;
	}

	public void setPaymentActionUrl(String paymentActionUrl) {
		this.paymentActionUrl = paymentActionUrl;
	}


	public String getCurrencyName() {
		return currencyName;
	}


	public void setCurrencyName(String currencyName) {
		this.currencyName = currencyName;
	}
	public Map<String, String> getCurrencies() {
		return currencies;
	}


	public void setCurrencies(Map<String, String> currencies) {
		this.currencies = currencies;
	}

		
}
