package com.pay10.crm.actionBeans;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pay10.commons.api.Hasher;
import com.pay10.commons.exception.SystemException;
import com.pay10.commons.user.Invoice;
import com.pay10.commons.util.Amount;
import com.pay10.commons.util.ConfigurationConstants;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.PropertiesManager;

@Service
public class InvoiceHasher {

	@Autowired
	Hasher hasher;
	public String createInvoiceHash(Invoice invoice) throws SystemException {

		StringBuilder allFields = new StringBuilder();
		Map<String, String> invoiceMap = new HashMap<String, String>();

		invoiceMap.put(FieldType.PAY_ID.getName(), invoice.getPayId());
		invoiceMap.put(FieldType.ORDER_ID.getName(), invoice.getInvoiceId());
		invoiceMap.put(FieldType.AMOUNT.getName(), Amount.formatAmount(invoice.getTotalAmount(),invoice.getCurrencyCode()));
		//invoiceMap.put(FieldType.TXNTYPE.getName(),"SALE");
		//invoiceMap.put(FieldType.CUST_NAME.getName(), invoice.getName());
		//invoiceMap.put(FieldType.CUST_STREET_ADDRESS1.getName(), invoice.getAddress());
		//invoiceMap.put(FieldType.CUST_ZIP.getName(), invoice.getZip());
		//invoiceMap.put(FieldType.CUST_PHONE.getName(), invoice.getPhone());
		//invoiceMap.put(FieldType.CUST_EMAIL.getName(), invoice.getEmail());
		//invoiceMap.put(FieldType.PRODUCT_DESC.getName(), invoice.getProductDesc());
		invoiceMap.put(FieldType.CURRENCY_CODE.getName(), invoice.getCurrencyCode());
		invoiceMap.put(FieldType.RETURN_URL.getName(), invoice.getReturnUrl());
		
		

		Map<String, String> sortedMap = new TreeMap<String, String>(invoiceMap);
		for (String key : sortedMap.keySet()) {
			if(StringUtils.isNotBlank(allFields.toString())) {
				allFields.append(ConfigurationConstants.FIELD_SEPARATOR.getValue());
			}
			
			allFields.append(key);
			allFields.append(ConfigurationConstants.FIELD_EQUATOR.getValue());
			allFields.append(sortedMap.get(key));
		}
		String salt = (new PropertiesManager()).getSalt(invoice.getPayId());
		allFields.append(salt);
		return Hasher.getHash(allFields.toString());
	}

}
