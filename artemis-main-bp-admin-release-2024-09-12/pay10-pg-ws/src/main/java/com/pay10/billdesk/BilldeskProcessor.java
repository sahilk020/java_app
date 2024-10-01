package com.pay10.billdesk;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pay10.commons.exception.SystemException;
import com.pay10.commons.util.AcquirerType;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.PaymentType;
import com.pay10.commons.util.TransactionType;
import com.pay10.pg.core.util.Processor;

@Service("billdeskProcessor")
public class BilldeskProcessor implements Processor {

	@Autowired
	private BilldeskIntegrator billdeskIntegrator;

	@Autowired
	private BilldeskUpiIntegrator billdeskUpiIntegrator;

	@Override
	public void preProcess(Fields fields) throws SystemException {
	}

	@Override
	public void process(Fields fields) throws SystemException {

		if ((fields.get(FieldType.TXNTYPE.getName()).equals(TransactionType.NEWORDER.getName())
				|| fields.get(FieldType.INTERNAL_ORIG_TXN_TYPE.getName()).equals(TransactionType.STATUS.getName())
				|| fields.get(FieldType.INTERNAL_ORIG_TXN_TYPE.getName()).equals(TransactionType.VERIFY.getName())
				|| fields.get(FieldType.INTERNAL_ORIG_TXN_TYPE.getName()).equals(TransactionType.RECO.getName())
				|| fields.get(FieldType.INTERNAL_ORIG_TXN_TYPE.getName())
						.equals(TransactionType.REFUNDRECO.getName()))) {
			return;
		}

		if (!fields.get(FieldType.ACQUIRER_TYPE.getName()).equalsIgnoreCase(AcquirerType.BILLDESK.getCode())) {
			return;
		}
		String paymentType = fields.get(FieldType.PAYMENT_TYPE.getName());
		String txnType = fields.get(FieldType.TXNTYPE.getName());
		
		if (txnType.equalsIgnoreCase(TransactionType.ENROLL.getName())) {
			txnType = TransactionType.SALE.getName();
			fields.put(FieldType.TXNTYPE.getName(),txnType);
		}
		
		if (paymentType.equalsIgnoreCase(PaymentType.UPI.getCode()) && txnType.equalsIgnoreCase(TransactionType.SALE.getCode())) {
			billdeskUpiIntegrator.process(fields);
		}
		else if (paymentType.equalsIgnoreCase(PaymentType.UPI.getCode()) && txnType.equalsIgnoreCase(TransactionType.ENROLL.getName())) {
			
			fields.put(FieldType.TXNTYPE.getName(),TransactionType.SALE.getName());
			billdeskUpiIntegrator.process(fields);
			
		}
		
		else {
			billdeskIntegrator.process(fields);
		}
		
	}

	@Override
	public void postProcess(Fields fields) throws SystemException {
	}
}
