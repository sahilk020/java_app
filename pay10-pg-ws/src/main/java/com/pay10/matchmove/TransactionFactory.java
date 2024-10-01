package com.pay10.matchmove;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.TransactionType;

@Service("matchMoveFactory")
public class TransactionFactory {
	
	private static Logger logger = LoggerFactory.getLogger(TransactionFactory.class.getName());
	
	@SuppressWarnings("incomplete-switch")
	public Transaction getInstance(Fields fields) {

		logger.info("inside the TransactionFactory for get the instance value");
		Transaction transaction = new Transaction();
		switch (TransactionType.getInstance(fields.get(FieldType.TXNTYPE.getName()))) {
		case AUTHORISE:
			break;
		case ENROLL:
			transaction.setEnrollment(fields);
			fields.put(FieldType.TXNTYPE.getName(), TransactionType.SALE.getName());
			fields.put(FieldType.PG_REF_NUM.getName(), fields.get(FieldType.TXN_ID.getName()));
			fields.put(FieldType.ORIG_TXN_ID.getName(), fields.get(FieldType.TXN_ID.getName()));
			break;
		case REFUND:
			fields.put(FieldType.PG_REF_NUM.getName(), fields.get(FieldType.TXN_ID.getName()));
			break;
		case SALE:
//			transaction.setEnrollment(fields);
//			fields.put(FieldType.TXNTYPE.getName(), TransactionType.SALE.getName());
//			fields.put(FieldType.PG_REF_NUM.getName(), fields.get(FieldType.TXN_ID.getName()));
//			fields.put(FieldType.ORIG_TXN_ID.getName(), fields.get(FieldType.TXN_ID.getName()));
			break;
		case CAPTURE:
			fields.put(FieldType.PG_REF_NUM.getName(), fields.get(FieldType.ORIG_TXN_ID.getName()));
			break;
		case STATUS:
			//transaction.setStatusEnquiry(fields);
			break;
		}

		return transaction;
	}

	

}
