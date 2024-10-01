package com.pay10.icici.mpgs;

import org.springframework.stereotype.Service;

import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.TransactionManager;
import com.pay10.commons.util.TransactionType;


/**
 * @author Rahul
 *
 */
@Service("iciciMpgsFactory")
public class TransactionFactory {
	
	@SuppressWarnings("incomplete-switch")
	public Transaction getInstance(Fields fields) {

		Transaction transaction = new Transaction();
		switch (TransactionType.getInstance(fields.get(FieldType.TXNTYPE.getName()))) {
		case AUTHORISE:
			break;
		case ENROLL:
			transaction.setCardDetails(fields);
			fields.put(FieldType.PG_REF_NUM.getName(), fields.get(FieldType.TXN_ID.getName()));
			String transId = TransactionManager.getNewTransactionId();
			fields.put(FieldType.ORIG_TXN_ID.getName(), transId);
			fields.put(FieldType.TXN_ID.getName(), transId);
			break;
		case REFUND:
			//transaction.setRefund(fields);
			fields.put(FieldType.PG_REF_NUM.getName(), fields.get(FieldType.TXN_ID.getName()));
			break;
		case SALE:
			// Authorization and Sale messaging format is same, just action code
			// changes
			//transaction.setAuthorization(fields);
			transaction.setCardDetails(fields);
			fields.put(FieldType.PG_REF_NUM.getName(), fields.get(FieldType.TXN_ID.getName()));
			break;
		case CAPTURE:
			//transaction.setCapture(fields);
			fields.put(FieldType.PG_REF_NUM.getName(), fields.get(FieldType.ORIG_TXN_ID.getName()));
			break;
		case STATUS:
			//transaction.setStatusEnquiry(fields);
			break;
		}

		return transaction;
	}

}
