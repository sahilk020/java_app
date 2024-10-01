package com.pay10.yesbank.netbanking;

import org.springframework.stereotype.Service;

import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.TransactionType;

@Service("yesbankNBTransactionFactory")
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

        return transaction;

    }

    public Transaction setRefund(Fields fields) {

        Transaction transaction = new Transaction();
        return transaction;

    }

    public Transaction setStatusEnquiry(Fields fields) {

        Transaction transaction = new Transaction();

        return transaction;

    }

}
