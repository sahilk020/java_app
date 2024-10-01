package com.pay10.billdesk;

import org.springframework.stereotype.Service;

import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.TransactionType;

@Service("billdeskFactory")
public class TransactionFactory {
	
	@SuppressWarnings("incomplete-switch")
	public Transaction getInstance(Fields fields) {

		Transaction transaction = new Transaction();
		switch (TransactionType.getInstance(fields.get(FieldType.TXNTYPE.getName()))) {
		case AUTHORISE:
			break;
		case REFUND:
			transaction.setRefund(fields);
			fields.put(FieldType.PG_REF_NUM.getName(), fields.get(FieldType.TXN_ID.getName()));
			break;
		case SALE:
			fields.put(FieldType.TXNTYPE.getName(), TransactionType.SALE.getName());
			fields.put(FieldType.PG_REF_NUM.getName(), fields.get(FieldType.TXN_ID.getName()));
			fields.put(FieldType.ORIG_TXN_ID.getName(), fields.get(FieldType.TXN_ID.getName()));
			break;
		case STATUS:
			fields.put(FieldType.TXNTYPE.getName(), TransactionType.STATUS.getName());
			break;
		}

		return transaction;
	}

}
