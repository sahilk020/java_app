package com.pay10.commons.util;

import org.bson.Document;

import com.pay10.commons.user.PayinPaymentS2S;

public class PayinPaymentS2SUtilities {
	
	public static PayinPaymentS2S getPaymentLinkFromDoc(Document doc) {
		PayinPaymentS2S paymentLink = new PayinPaymentS2S();
		paymentLink.setPayId(String.valueOf(doc.getString("PAY_ID")));
		paymentLink.setPayType(String.valueOf(doc.getString("PAY_TYPE")));
		paymentLink.setCustName(String.valueOf(doc.getString("CUST_NAME")));
		paymentLink.setCustFirstName(String.valueOf(doc.getString("CUST_FIRST_NAME")));
		paymentLink.setCustLastName(String.valueOf(doc.getString("CUST_LAST_NAME")));
		paymentLink.setCustStreeAddress1(String.valueOf(doc.getString("CUST_STREET_ADDRESS1")));
		paymentLink.setCustCity(String.valueOf(doc.getString("CUST_CITY")));
		paymentLink.setCustState(String.valueOf(doc.getString("CUST_STATE")));
		paymentLink.setCustCountry(String.valueOf(doc.getString("CUST_COUNTRY")));
		paymentLink.setCustZip(String.valueOf(doc.getString("CUST_ZIP")));
		paymentLink.setCustPhone(String.valueOf(doc.getString("CUST_PHONE")));
		paymentLink.setCustEmail(String.valueOf(doc.getString("CUST_EMAIL")));
		paymentLink.setAmount(String.valueOf(doc.getString("AMOUNT")));
		paymentLink.setTxntype(String.valueOf(doc.getString("TXNTYPE")));
		paymentLink.setCurrencyCode(String.valueOf(doc.getString("CURRENCY_CODE")));
		paymentLink.setProductDesc(String.valueOf(doc.getString("PRODUCT_DESC")));
		paymentLink.setOrderId(String.valueOf(doc.getString("ORDER_ID")));
		paymentLink.setReturnUrl(String.valueOf(doc.getString("RETURN_URL")));
		paymentLink.setHash(String.valueOf(doc.getString("HASH")));
		paymentLink.setPaymentType(String.valueOf(doc.getString("PAYMENT_TYPE")));
		paymentLink.setPaymentStatus(PayinPaymentS2SStatus.valueOf(doc.getString("STATUS")));
		paymentLink.setCreateDate(String.valueOf(doc.getString("CREATED_DATE")));
		paymentLink.setUpdateDate(String.valueOf(doc.getString("UPDATED_DATE")));
		paymentLink.setPaymentRedirectId(String.valueOf(doc.getString("PAYMENT_REDIRECT_ID")));
		paymentLink.setPaymentRedirectUrl(String.valueOf(doc.getString("PAYMENT_REDIRECT_URL")));

		return paymentLink;
	}
	
	public static Document getDocFromPaymentLink(PayinPaymentS2S paymentLink) {
		Document docBuilder = new Document();
		docBuilder.put("PAY_ID", paymentLink.getPayId());
		docBuilder.put("PAY_TYPE", paymentLink.getPayType());
		docBuilder.put("CUST_NAME", paymentLink.getCustName());
		docBuilder.put("CUST_FIRST_NAME", paymentLink.getCustFirstName());
		docBuilder.put("CUST_LAST_NAME", paymentLink.getCustLastName());
		docBuilder.put("CUST_STREET_ADDRESS1", paymentLink.getCustStreeAddress1());
		docBuilder.put("CUST_CITY", paymentLink.getCustCity());
		docBuilder.put("CUST_STATE", paymentLink.getCustState());
		docBuilder.put("CUST_COUNTRY", paymentLink.getCustCountry());
		docBuilder.put("CUST_ZIP", paymentLink.getCustZip());
		docBuilder.put("CUST_PHONE", paymentLink.getCustPhone());
		docBuilder.put("CUST_EMAIL", paymentLink.getCustEmail());
		docBuilder.put("AMOUNT", paymentLink.getAmount());
		docBuilder.put("TXNTYPE", paymentLink.getTxntype());
		docBuilder.put("CURRENCY_CODE", paymentLink.getCurrencyCode());
		docBuilder.put("PRODUCT_DESC", paymentLink.getUpdateDate());
		docBuilder.put("ORDER_ID", paymentLink.getOrderId());
		docBuilder.put("RETURN_URL", paymentLink.getReturnUrl());
		docBuilder.put("HASH", paymentLink.getHash());
		docBuilder.put("PAYMENT_TYPE", paymentLink.getPaymentType());
		docBuilder.put("STATUS", paymentLink.getPaymentStatus().toString());
		docBuilder.put("CREATED_DATE", paymentLink.getCreateDate());
		docBuilder.put("UPDATED_DATE", paymentLink.getUpdateDate());
		docBuilder.put("PAYMENT_REDIRECT_ID", paymentLink.getPaymentRedirectId());
		docBuilder.put("PAYMENT_REDIRECT_URL", paymentLink.getPaymentRedirectUrl());

		return docBuilder;
	}

}
