package com.pay10.commons.user;

import java.io.Serializable;

public class InvoiceTrailReport implements Serializable{

	/**
	 * Sweety
	 */
	private static final long serialVersionUID = 1516005532915547447L;

	private Long id;
	private String invoiceId;
	private String businessName;
	private String invoiceNo;
	private String merchantTnc;
	private String customerTnc;
	private String merchantTncTimeStamp;
	private String customerTncTimeStamp;
	
	
	public InvoiceTrailReport() {
	
	}
	public String getInvoiceId() {
		return invoiceId;
	}
	public void setInvoiceId(String invoiceId) {
		this.invoiceId = invoiceId;
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
	public String getMerchantTnc() {
		return merchantTnc;
	}
	public void setMerchantTnc(String merchantTnc) {
		this.merchantTnc = merchantTnc;
	}
	public String getCustomerTnc() {
		return customerTnc;
	}
	public void setCustomerTnc(String customerTnc) {
		this.customerTnc = customerTnc;
	}
	public String getMerchantTncTimeStamp() {
		return merchantTncTimeStamp;
	}
	public void setMerchantTncTimeStamp(String merchantTncTimeStamp) {
		this.merchantTncTimeStamp = merchantTncTimeStamp;
	}
	public String getCustomerTncTimeStamp() {
		return customerTncTimeStamp;
	}
	public void setCustomerTncTimeStamp(String customerTncTimeStamp) {
		this.customerTncTimeStamp = customerTncTimeStamp;
	}
	
	
}
