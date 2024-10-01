package com.pay10.commons.user;

import java.io.Serializable;

import com.pay10.commons.util.InvoiceStatus;

public class PaymentLink implements Serializable {

	private static final long serialVersionUID = 81100892505476449L;

	private Long id;
	private String paymentLinkId;
	private String payId;
	private String orderId;
	private String name;
	private String phone;
	private String email;
	private String address;
	private String amount;
	private String currencyCode="356";
	private String expiresDay;
	private String expiresHour;
	private String expiryTime;
	private String createDate;
	private String updateDate;
	private String returnUrl;
	private String shortUrl;
	private InvoiceStatus invoiceStatus;
	private String invoiceUrl;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getPaymentLinkId() {
		return paymentLinkId;
	}
	public void setPaymentLinkId(String paymentLinkId) {
		this.paymentLinkId = paymentLinkId;
	}
	public String getPayId() {
		return payId;
	}
	public void setPayId(String payId) {
		this.payId = payId;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
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
	public String getAmount() {
		return amount;
	}
	public void setAmount(String amount) {
		this.amount = amount;
	}
	
	public String getCurrencyCode() {
		return currencyCode;
	}
	public void setCurrencyCode(String currencyCode) {
		this.currencyCode = currencyCode;
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
	public String getReturnUrl() {
		return returnUrl;
	}
	public void setReturnUrl(String returnUrl) {
		this.returnUrl = returnUrl;
	}
	public String getShortUrl() {
		return shortUrl;
	}
	public void setShortUrl(String shortUrl) {
		this.shortUrl = shortUrl;
	}
	public InvoiceStatus getInvoiceStatus() {
		return invoiceStatus;
	}
	public void setInvoiceStatus(InvoiceStatus invoiceStatus) {
		this.invoiceStatus = invoiceStatus;
	}
	public String getInvoiceUrl() {
		return invoiceUrl;
	}
	public void setInvoiceUrl(String invoiceUrl) {
		this.invoiceUrl = invoiceUrl;
	}
	public String getOrderId() {
		return orderId;
	}
	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}
	public String getExpiresDay() {
		return expiresDay;
	}
	public void setExpiresDay(String expiresDay) {
		this.expiresDay = expiresDay;
	}
	public String getExpiresHour() {
		return expiresHour;
	}
	public void setExpiresHour(String expiresHour) {
		this.expiresHour = expiresHour;
	}
	public String getExpiryTime() {
		return expiryTime;
	}
	public void setExpiryTime(String expiryTime) {
		this.expiryTime = expiryTime;
	}
	@Override
	public String toString() {
		return "PaymentLink [id=" + id + ", paymentLinkId=" + paymentLinkId + ", payId=" + payId + ", orderId="
				+ orderId + ", name=" + name + ", phone=" + phone + ", email=" + email + ", address=" + address
				+ ", amount=" + amount + ", currencyCode=" + currencyCode + ", expiresDay=" + expiresDay
				+ ", expiresHour=" + expiresHour + ", expiryTime=" + expiryTime + ", createDate=" + createDate
				+ ", updateDate=" + updateDate + ", returnUrl=" + returnUrl + ", shortUrl=" + shortUrl
				+ ", invoiceStatus=" + invoiceStatus + ", invoiceUrl=" + invoiceUrl + "]";
	}
	
	
	
	
}
