package com.pay10.axisbank.netbanking;

import org.springframework.stereotype.Service;

import com.pay10.commons.util.Amount;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.TransactionType;

@Service("axisBankNBTransactionFactory")
public class TransactionFactory {

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
			transaction = setEnrollment(fields);
			break;
		case REFUND:
			fields.put(FieldType.PG_REF_NUM.getName(), fields.get(FieldType.TXN_ID.getName()));
			transaction = setRefund(fields);
			break;
		case SALE:
			fields.put(FieldType.TXNTYPE.getName(), TransactionType.SALE.getName());
			fields.put(FieldType.PG_REF_NUM.getName(), fields.get(FieldType.TXN_ID.getName()));
			fields.put(FieldType.ORIG_TXN_ID.getName(), fields.get(FieldType.TXN_ID.getName()));
			transaction = setEnrollment(fields);
			break;
		case CAPTURE:
			fields.put(FieldType.PG_REF_NUM.getName(), fields.get(FieldType.ORIG_TXN_ID.getName()));
			break;
		case STATUS:
			transaction = setStatusEnquiry(fields);
			break;
		}

		return transaction;
	}

	public Transaction setEnrollment(Fields fields) {

		Transaction transaction = new Transaction();
		
		transaction.setPrn(fields.get(FieldType.PG_REF_NUM.getName()));
		transaction.setPid(fields.get(FieldType.ADF10.getName()));
		transaction.setMd("P");
		transaction.setAmt(Amount.toDecimal(fields.get(FieldType.TOTAL_AMOUNT.getName()),fields.get(FieldType.CURRENCY_CODE.getName())));
		transaction.setCrn("INR");
		transaction.setRu(fields.get(FieldType.RETURN_URL.getName()));
		transaction.setItc("PAY10");
		transaction.setCg("Y");
		transaction.setResponse("AUTO");
		
		return transaction;

	}

	public Transaction setRefund(Fields fields) {

		Transaction transaction = new Transaction();
		return transaction;

	}

	public Transaction setStatusEnquiry(Fields fields) {

		Transaction transaction = new Transaction();
		
		transaction.setPrn(fields.get(FieldType.PG_REF_NUM.getName()));
		transaction.setPid(fields.get(FieldType.ADF10.getName()));
		transaction.setAmt(Amount.toDecimal(fields.get(FieldType.TOTAL_AMOUNT.getName()),fields.get(FieldType.CURRENCY_CODE.getName())));
		transaction.setItc(fields.get(FieldType.AUTH_CODE.getName()));
		transaction.setDate(fields.get(FieldType.CREATE_DATE.getName()).substring(0,10));
		
		return transaction;

	}

}
