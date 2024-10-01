package com.pay10.commons.util;

import org.bson.Document;

import com.pay10.commons.user.Invoice;
import com.pay10.commons.user.InvoiceTrailReport;
import com.pay10.commons.util.CrmFieldType;
import com.pay10.commons.util.InvoiceStatus;
import com.pay10.commons.util.InvoiceType;

public class InvoiceUtilities {

	private InvoiceUtilities() {
	}

	public static Invoice getInvoiceFromDoc(Document doc) {
		Invoice invoice = new Invoice();
		invoice.setAddress(doc.getString(CrmFieldType.INVOICE_ADDRESS.getName()));
		invoice.setAmount(doc.getString(CrmFieldType.INVOICE_AMOUNT.getName()));
		invoice.setBusinessName(doc.getString(CrmFieldType.INVOICE_BUSINESS_NAME.getName()));
		invoice.setCity(doc.getString(CrmFieldType.INVOICE_CITY.getName()));
		invoice.setCountry(doc.getString(CrmFieldType.INVOICE_COUNTRY.getName()));
		invoice.setCreateDate(doc.getString(CrmFieldType.INVOICE_CREATE_DATE.getName()));
		invoice.setCurrencyCode(doc.getString(CrmFieldType.INVOICE_CURRENCY_CODE.getName()));
		invoice.setRegion(doc.getString(CrmFieldType.INVOICE_REGION.getName()));
		invoice.setEmail(doc.getString(CrmFieldType.INVOICE_EMAIL.getName()));
		invoice.setExpiresDay(doc.getString(CrmFieldType.INVOICE_EXPIRES_DAY.getName()));
		invoice.setExpiresHour(doc.getString(CrmFieldType.INVOICE_EXPIRES_HOUR.getName()));
		invoice.setExpiryTime(doc.getString(CrmFieldType.INVOICE_EXPIRY_TIME.getName()));
		invoice.setInvoiceId(doc.getString(CrmFieldType.INVOICE_ID.getName()));
		invoice.setInvoiceNo(doc.getString(CrmFieldType.INVOICE_NUMBER.getName()));
		invoice.setInvoiceType(InvoiceType.valueOf(doc.getString(CrmFieldType.INVOICE_TYPE.getName())));
		invoice.setMessageBody(doc.getString(CrmFieldType.INVOICE_MESSAGE_BODY.getName()));
		invoice.setName(doc.getString(CrmFieldType.INVOICE_NAME.getName()));
		invoice.setPayId(doc.getString(CrmFieldType.INVOICE_PAY_ID.getName()));
		invoice.setPhone(doc.getString(CrmFieldType.INVOICE_PHONE.getName()));
		invoice.setProductDesc(doc.getString(CrmFieldType.INVOICE_PRODUCT_DESCRIPTION.getName()));
		invoice.setProductName(doc.getString(CrmFieldType.INVOICE_PRODUCT_NAME.getName()));
		invoice.setQuantity(doc.getString(CrmFieldType.INVOICE_QUANTITY.getName()));
		invoice.setReturnUrl(doc.getString(CrmFieldType.INVOICE_RETURN_URL.getName()));
		invoice.setShortUrl(doc.getString(CrmFieldType.INVOICE_SHORT_URL.getName()));
		invoice.setState(doc.getString(CrmFieldType.INVOICE_STATE.getName()));
		invoice.setTotalAmount(doc.getString(CrmFieldType.INVOICE_TOTAL_AMOUNT.getName()));
		invoice.setUpdateDate(doc.getString(CrmFieldType.INVOICE_UPDATE_DATE.getName()));
		invoice.setZip(doc.getString(CrmFieldType.INVOICE_ZIP.getName()));
		invoice.setGst(doc.getString(CrmFieldType.INVOICE_GST.getName()));
		invoice.setGstAmount(doc.getString(CrmFieldType.INVOICE_GST_AMOUNT.getName()));
		invoice.setInvoiceStatus(InvoiceStatus.valueOf(doc.getString(CrmFieldType.INVOICE_STATUS.getName())));
		invoice.setInvoiceUrl(doc.getString(CrmFieldType.INVOICE_URL.getName()));
//		invoice.setRecipientMobile(doc.getString(CrmFieldType.INVOICE_RECIPIENT_MOBILE.getName()));
//		invoice.setSaltKey(doc.getString(CrmFieldType.INVOICE_SALT_KEY.getName()));
//		invoice.setServiceCharge(doc.getString(CrmFieldType.INVOICE_SERVICE_CHARGE.getName()));
		//Added By Sweety
		//boolean merConsent=(doc.getBoolean(FieldType.MERCHANT_CONSENT.getName()));
		//System.out.println(merConsent);
		invoice.setMerchantConsent(doc.getBoolean(FieldType.MERCHANT_CONSENT.getName()));
	
		return invoice;
	}

	public static Document getDocFromInvoice(Invoice invoice) {
		Document docBuilder = new Document();
		docBuilder.put(CrmFieldType.INVOICE_ADDRESS.getName(), invoice.getAddress());
		docBuilder.put(CrmFieldType.INVOICE_AMOUNT.getName(), invoice.getAmount());
		docBuilder.put(CrmFieldType.INVOICE_BUSINESS_NAME.getName(), invoice.getBusinessName());
		docBuilder.put(CrmFieldType.INVOICE_CITY.getName(), invoice.getCity());
		docBuilder.put(CrmFieldType.INVOICE_COUNTRY.getName(), invoice.getCountry());
		docBuilder.put(CrmFieldType.INVOICE_CREATE_DATE.getName(), invoice.getCreateDate());
		docBuilder.put(CrmFieldType.INVOICE_CURRENCY_CODE.getName(), invoice.getCurrencyCode());
		docBuilder.put(CrmFieldType.INVOICE_REGION.getName(), invoice.getRegion());
		docBuilder.put(CrmFieldType.INVOICE_EMAIL.getName(), invoice.getEmail());
		docBuilder.put(CrmFieldType.INVOICE_EXPIRES_DAY.getName(), invoice.getExpiresDay());
		docBuilder.put(CrmFieldType.INVOICE_EXPIRES_HOUR.getName(), invoice.getExpiresHour());
		docBuilder.put(CrmFieldType.INVOICE_EXPIRY_TIME.getName(), invoice.getExpiryTime());
		docBuilder.put(CrmFieldType.INVOICE_ID.getName(), invoice.getInvoiceId());
		docBuilder.put(CrmFieldType.INVOICE_NUMBER.getName(), invoice.getInvoiceNo());
		docBuilder.put(CrmFieldType.INVOICE_TYPE.getName(), invoice.getInvoiceType().toString());
		docBuilder.put(CrmFieldType.INVOICE_MESSAGE_BODY.getName(), invoice.getMessageBody());
		docBuilder.put(CrmFieldType.INVOICE_NAME.getName(), invoice.getName());
		docBuilder.put(CrmFieldType.INVOICE_PAY_ID.getName(), invoice.getPayId());
		docBuilder.put(CrmFieldType.INVOICE_PHONE.getName(), invoice.getPhone());
		docBuilder.put(CrmFieldType.INVOICE_PRODUCT_DESCRIPTION.getName(), invoice.getProductDesc());
		docBuilder.put(CrmFieldType.INVOICE_PRODUCT_NAME.getName(), invoice.getProductName());
		docBuilder.put(CrmFieldType.INVOICE_QUANTITY.getName(), invoice.getQuantity());
		docBuilder.put(CrmFieldType.INVOICE_RETURN_URL.getName(), invoice.getReturnUrl());
		docBuilder.put(CrmFieldType.INVOICE_SHORT_URL.getName(), invoice.getShortUrl());
		docBuilder.put(CrmFieldType.INVOICE_STATE.getName(), invoice.getState());
		docBuilder.put(CrmFieldType.INVOICE_TOTAL_AMOUNT.getName(), invoice.getTotalAmount());
		docBuilder.put(CrmFieldType.INVOICE_UPDATE_DATE.getName(), invoice.getUpdateDate());
		docBuilder.put(CrmFieldType.INVOICE_ZIP.getName(), invoice.getZip());
		docBuilder.put(CrmFieldType.INVOICE_GST.getName(), invoice.getGst());
		docBuilder.put(CrmFieldType.INVOICE_GST_AMOUNT.getName(), invoice.getGstAmount());
		docBuilder.put(CrmFieldType.INVOICE_STATUS.getName(), invoice.getInvoiceStatus().toString());
		docBuilder.put(CrmFieldType.INVOICE_URL.getName(), invoice.getInvoiceUrl());
		docBuilder.put(FieldType.MERCHANT_CONSENT.getName(), invoice.isMerchantConsent());

//		docBuilder.put("SALT_KEY", invoice.getSaltKey());   										// not required.
//		docBuilder.put(CrmFieldType.INVOICE_SERVICE_CHARGE.getName(), invoice.getServiceCharge()); 	// not required.
//		docBuilder.put("RECIPIENT_MOBILE", invoice.getRecipientMobile()); 							// not required.

		return docBuilder;
	}

	//Added By Sweety
	public static Document getDocFromInvoiceTrailReport(InvoiceTrailReport invoiceTrailReport) {
		Document docBuilder = new Document();
		docBuilder.put("INVOICE_ID", invoiceTrailReport.getInvoiceId());
		docBuilder.put("INVOICE_NO", invoiceTrailReport.getInvoiceNo());
		docBuilder.put("BUSINESS_NAME", invoiceTrailReport.getBusinessName());
		docBuilder.put("MERCHANT_TNC", invoiceTrailReport.getMerchantTnc());
		docBuilder.put("CUSTOMER_TNC", invoiceTrailReport.getCustomerTnc());
		docBuilder.put("MERCHANT_TNC_TIMESTAMP", invoiceTrailReport.getMerchantTncTimeStamp());
		docBuilder.put("CUSTOMER_TNC_TIMESTAMP", invoiceTrailReport.getCustomerTncTimeStamp());
		return docBuilder;
	}

}
