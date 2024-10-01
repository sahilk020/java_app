package com.pay10.direcpay;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pay10.commons.util.ConfigurationConstants;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.TransactionType;
import com.pay10.pg.core.whitelabel.ReturnUrlCustomizer;

@Service("direcpayFactory")
public class TransactionFactory {
	@Autowired
	private ReturnUrlCustomizer returnUrlCustomizer;
	
	@SuppressWarnings("incomplete-switch")
	public Transaction getInstance(Fields fields) {
		String returnUrl = "";
		Transaction transaction = new Transaction();
		switch (TransactionType.getInstance(fields.get(FieldType.TXNTYPE.getName()))) {
		case AUTHORISE:
			break;
		case ENROLL:
			fields.put(FieldType.TXNTYPE.getName(), TransactionType.SALE.getName());
			fields.put(FieldType.PG_REF_NUM.getName(), fields.get(FieldType.TXN_ID.getName()));
			fields.put(FieldType.ORIG_TXN_ID.getName(), fields.get(FieldType.TXN_ID.getName()));
			transaction.setEnrollment(fields);
			returnUrl = returnUrlCustomizer.customizeReturnUrl(fields, ConfigurationConstants.DIRECPAY_RETURN_URL.getValue());
			transaction.setSuccessURL(returnUrl);
			transaction.setFailureURL(returnUrl);
			break;
		case REFUND:
			fields.put(FieldType.TOTAL_AMOUNT.getName(), fields.get(FieldType.AMOUNT.getName()));
			fields.put(FieldType.PG_REF_NUM.getName(), fields.get(FieldType.TXN_ID.getName()));
			transaction.setRefund(fields);
			break;
		case SALE:
			fields.put(FieldType.TXNTYPE.getName(), TransactionType.SALE.getName());
			fields.put(FieldType.PG_REF_NUM.getName(), fields.get(FieldType.TXN_ID.getName()));
			fields.put(FieldType.ORIG_TXN_ID.getName(), fields.get(FieldType.TXN_ID.getName()));
			transaction.setEnrollment(fields);
			transaction.setEnrollment(fields);
			returnUrl = returnUrlCustomizer.customizeReturnUrl(fields, ConfigurationConstants.DIRECPAY_RETURN_URL.getValue());
			transaction.setSuccessURL(returnUrl);
			transaction.setFailureURL(returnUrl);
			break;
		case CAPTURE:
			fields.put(FieldType.PG_REF_NUM.getName(), fields.get(FieldType.ORIG_TXN_ID.getName()));
			break;
		case STATUS:
			transaction.setStatusEnquiry(fields);
			break;
		}

		return transaction;
	}

}
