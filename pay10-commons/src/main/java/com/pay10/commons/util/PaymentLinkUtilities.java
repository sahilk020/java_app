package com.pay10.commons.util;

import org.bson.Document;

import com.pay10.commons.user.PaymentLink;

public class PaymentLinkUtilities {
	
	public static PaymentLink getPaymentLinkFromDoc(Document doc) {
		PaymentLink paymentLink = new PaymentLink();
		paymentLink.setAddress(doc.getString(CrmFieldType.INVOICE_ADDRESS.getName()));
		paymentLink.setAmount(doc.getString(CrmFieldType.INVOICE_AMOUNT.getName()));
		paymentLink.setCreateDate(doc.getString(CrmFieldType.INVOICE_CREATE_DATE.getName()));
		paymentLink.setCurrencyCode(doc.getString(CrmFieldType.INVOICE_CURRENCY_CODE.getName()));
		paymentLink.setEmail(doc.getString(CrmFieldType.INVOICE_EMAIL.getName()));
		paymentLink.setPaymentLinkId(doc.getString(CrmFieldType.PAYMENT_LINK_ID.getName()));
		paymentLink.setOrderId(doc.getString(CrmFieldType.ORDER_ID.getName()));
		paymentLink.setName(doc.getString(CrmFieldType.INVOICE_NAME.getName()));
		paymentLink.setPayId(doc.getString(CrmFieldType.INVOICE_PAY_ID.getName()));
		paymentLink.setPhone(doc.getString(CrmFieldType.INVOICE_PHONE.getName()));
		paymentLink.setReturnUrl(doc.getString(CrmFieldType.INVOICE_RETURN_URL.getName()));
		paymentLink.setShortUrl(doc.getString(CrmFieldType.INVOICE_SHORT_URL.getName()));
		paymentLink.setExpiresDay(doc.getString(CrmFieldType.INVOICE_EXPIRES_DAY.getName()));
		paymentLink.setExpiresHour(doc.getString(CrmFieldType.INVOICE_EXPIRES_HOUR.getName()));
		paymentLink.setExpiryTime(doc.getString(CrmFieldType.INVOICE_EXPIRY_TIME.getName()));
		paymentLink.setUpdateDate(doc.getString(CrmFieldType.INVOICE_UPDATE_DATE.getName()));
		paymentLink.setInvoiceStatus(InvoiceStatus.valueOf(doc.getString(CrmFieldType.INVOICE_STATUS.getName())));
		paymentLink.setInvoiceUrl(doc.getString(CrmFieldType.INVOICE_URL.getName()));

		return paymentLink;
	}
	
	public static Document getDocFromPaymentLink(PaymentLink paymentLink) {
		Document docBuilder = new Document();
		docBuilder.put(CrmFieldType.INVOICE_ADDRESS.getName(), paymentLink.getAddress());
		docBuilder.put(CrmFieldType.INVOICE_AMOUNT.getName(), paymentLink.getAmount());
		docBuilder.put(CrmFieldType.INVOICE_CREATE_DATE.getName(), paymentLink.getCreateDate());
		docBuilder.put(CrmFieldType.INVOICE_CURRENCY_CODE.getName(), paymentLink.getCurrencyCode());
		docBuilder.put(CrmFieldType.INVOICE_EMAIL.getName(), paymentLink.getEmail());
		docBuilder.put(CrmFieldType.PAYMENT_LINK_ID.getName(), paymentLink.getPaymentLinkId());
		docBuilder.put(CrmFieldType.INVOICE_NAME.getName(), paymentLink.getName());
		docBuilder.put(CrmFieldType.INVOICE_PAY_ID.getName(), paymentLink.getPayId());
		docBuilder.put(CrmFieldType.INVOICE_PHONE.getName(), paymentLink.getPhone());
		docBuilder.put(CrmFieldType.INVOICE_RETURN_URL.getName(), paymentLink.getReturnUrl());
		docBuilder.put(CrmFieldType.INVOICE_SHORT_URL.getName(), paymentLink.getShortUrl());
		docBuilder.put(CrmFieldType.INVOICE_EXPIRES_DAY.getName(), paymentLink.getExpiresDay());
		docBuilder.put(CrmFieldType.INVOICE_EXPIRES_HOUR.getName(), paymentLink.getExpiresHour());
		docBuilder.put(CrmFieldType.INVOICE_EXPIRY_TIME.getName(), paymentLink.getExpiryTime());
		docBuilder.put(CrmFieldType.ORDER_ID.getName(), paymentLink.getOrderId());
		docBuilder.put(CrmFieldType.INVOICE_UPDATE_DATE.getName(), paymentLink.getUpdateDate());
		docBuilder.put(CrmFieldType.INVOICE_STATUS.getName(), paymentLink.getInvoiceStatus().toString());
		docBuilder.put(CrmFieldType.INVOICE_URL.getName(), paymentLink.getInvoiceUrl());

		return docBuilder;
	}

}
