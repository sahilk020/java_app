package com.pay10.yesbankcb;

import org.springframework.stereotype.Service;

import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.TransactionType;


/**
 * @author VJ
 *
 */
@Service("yesBankCbFactory")
public class TransactionFactory {
	
	@SuppressWarnings("incomplete-switch")
	public Transaction getInstance(Fields fields) {

		Transaction transaction = new Transaction();
		switch (TransactionType.getInstance(fields.get(FieldType.TXNTYPE.getName()))) {
		case REFUND:
			fields.put(FieldType.PG_REF_NUM.getName(), fields.get(FieldType.TXN_ID.getName()));
			break;
		case SALE:
			fields.put(FieldType.PG_REF_NUM.getName(), fields.get(FieldType.TXN_ID.getName()));
			fields.put(FieldType.ORIG_TXN_ID.getName(), fields.get(FieldType.TXN_ID.getName()));
			break;
		case STATUS:
			fields.put(FieldType.PG_REF_NUM.getName(), fields.get(FieldType.TXN_ID.getName()));
			// transaction.setStatusEnquiry(fields);
			break;
		}

		return transaction;
	}

}
