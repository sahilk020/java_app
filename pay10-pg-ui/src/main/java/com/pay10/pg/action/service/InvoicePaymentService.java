package com.pay10.pg.action.service;

import java.util.Random;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pay10.commons.api.Hasher;
import com.pay10.commons.exception.SystemException;
import com.pay10.commons.user.Invoice;
import com.pay10.commons.util.Amount;
import com.pay10.commons.util.ConfigurationConstants;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.pg.core.util.ResponseCreator;

@Service
public class InvoicePaymentService {

	@Autowired
	private ResponseCreator responseCreater;

	public String prepareFields(Invoice invoice) throws SystemException {
		Random random = new Random();
		String requestUrl = ConfigurationConstants.INVOICE_PAYMENT_LINK.getValue();
		// implement sequential ID
		String orderId = invoice.getInvoiceNo() + (random.nextInt(10000) + 1);
		Fields fields = new Fields();
		fields.put(FieldType.ORDER_ID.getName(), orderId);
		fields.put(FieldType.INVOICE_ID.getName(), invoice.getInvoiceId());
		fields.put(FieldType.INVOICE_NO.getName(), invoice.getInvoiceNo());
		fields.put(FieldType.RETURN_URL.getName(), invoice.getReturnUrl());
		fields.put(FieldType.CURRENCY_CODE.getName(), invoice.getCurrencyCode());
		fields.put(FieldType.PAYMENTS_REGION.getName(), invoice.getRegion());
		fields.put(FieldType.PAY_ID.getName(), invoice.getPayId());
		fields.put(FieldType.AMOUNT.getName(),
				Amount.formatAmount(invoice.getTotalAmount(), invoice.getCurrencyCode()));
		fields.put("CUST_CONSENT",String.valueOf(invoice.isCustomerConsent()));
		//Optional fields
		if (StringUtils.isNotBlank(invoice.getAddress())) {
			fields.put(FieldType.CUST_STREET_ADDRESS1.getName(), invoice.getAddress());
		}
		
		if (StringUtils.isNotBlank(invoice.getState())) {
			fields.put(FieldType.CUST_STATE.getName(), invoice.getState());
		}
		
		if (StringUtils.isNotBlank(invoice.getCity())) {
			fields.put(FieldType.CUST_CITY.getName(), invoice.getCity());
		}
		if (StringUtils.isNotBlank(invoice.getCountry())) {
			fields.put(FieldType.CUST_COUNTRY.getName(), invoice.getCountry());
		}
		if (StringUtils.isNotBlank(invoice.getEmail())) {
			fields.put(FieldType.CUST_EMAIL.getName(), invoice.getEmail());
		}
		if (StringUtils.isNotBlank(invoice.getName())) {
			fields.put(FieldType.CUST_NAME.getName(), invoice.getName());
		}
		if (StringUtils.isNotBlank(invoice.getPhone())) {
			fields.put(FieldType.CUST_PHONE.getName(), invoice.getPhone());
		}
		if (StringUtils.isNotBlank(invoice.getProductDesc())) {
			fields.put(FieldType.PRODUCT_DESC.getName(), invoice.getProductDesc());
		}
		if (StringUtils.isNotBlank(invoice.getZip())) {
			fields.put(FieldType.CUST_ZIP.getName(), invoice.getZip());
		}
		String hash = Hasher.getHash(fields);
		fields.put(FieldType.HASH.getName(), hash);

		return responseCreater.createInvoiceRequest(fields, requestUrl);
	}
}
