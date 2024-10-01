package com.pay10.camspay;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.pay10.commons.util.Amount;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.TransactionType;
import com.pay10.pg.core.camspay.util.Constants;

import bsh.This;

@Service("camsPayFactory")
public class TransactionFactory {

	private static Logger logger = LoggerFactory.getLogger(This.class.getName());

	@SuppressWarnings("incomplete-switch")
	public Transaction getInstance(Fields fields) {

		Transaction transaction = new Transaction();
		switch (TransactionType.getInstance(fields.get(FieldType.TXNTYPE.getName()))) {
		case AUTHORISE:
			break;
		case ENROLL:
			fields.put(FieldType.TXNTYPE.getName(), TransactionType.SALE.getName());
			fields.put(FieldType.PG_REF_NUM.getName(), fields.get(FieldType.TXN_ID.getName()));
			fields.put(FieldType.ORIG_TXN_ID.getName(), fields.get(FieldType.TXN_ID.getName()));
			setEnrollment(fields, transaction);
			break;
		case REFUND:

			fields.put(FieldType.TOTAL_AMOUNT.getName(), fields.get(FieldType.AMOUNT.getName()));
			fields.put(FieldType.PG_REF_NUM.getName(), fields.get(FieldType.TXN_ID.getName()));
			logger.info("getInstance:: skipped as acquierer not provide.");
			break;
		case SALE:
			fields.put(FieldType.TXNTYPE.getName(), TransactionType.SALE.getName());
			fields.put(FieldType.PG_REF_NUM.getName(), fields.get(FieldType.TXN_ID.getName()));
			fields.put(FieldType.ORIG_TXN_ID.getName(), fields.get(FieldType.TXN_ID.getName()));
			setEnrollment(fields, transaction);
			break;
		case CAPTURE:
			fields.put(FieldType.PG_REF_NUM.getName(), fields.get(FieldType.ORIG_TXN_ID.getName()));
			break;
		case STATUS:
			setStatusEnquiry(fields, transaction);
			break;
		}

		return transaction;
	}

	public void setEnrollment(Fields fields, Transaction transaction) {
		try {
			String amount = Amount.toDecimal(fields.get(FieldType.TOTAL_AMOUNT.getName()),
					fields.get(FieldType.CURRENCY_CODE.getName()));
			String firstName = fields.get(FieldType.CUST_NAME.getName());
			transaction.setTxnId(fields.get(FieldType.PG_REF_NUM.getName()));
			transaction.setAmount(amount);
			if (StringUtils.isEmpty(firstName)) {
				fields.put(FieldType.CUST_NAME.getName(), Constants.PAY10);
			} else if (firstName.length() > 20) {
				firstName = firstName.substring(0, 20);
				fields.put(FieldType.CUST_NAME.getName(), firstName);
			}
			fields.put(FieldType.PASSWORD.getName(), fields.get(FieldType.ADF2.getName()));
		} catch (Exception e) {
			logger.error("setEnrollment:: CAMSPAY: failed.", e);
		}
	}

	public void setStatusEnquiry(Fields fields, Transaction transaction) {
	}
}
