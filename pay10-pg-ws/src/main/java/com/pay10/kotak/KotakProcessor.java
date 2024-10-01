package com.pay10.kotak;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pay10.commons.exception.SystemException;
import com.pay10.commons.util.AcquirerType;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.MopType;
import com.pay10.commons.util.PaymentType;
import com.pay10.commons.util.TransactionType;
import com.pay10.kotak.nb.KotakNBIntegrator;
import com.pay10.kotak.upi.KotakUpiIntegrator;
import com.pay10.pg.core.util.Processor;

@Service("kotakProcessor")
public class KotakProcessor implements Processor {

	@Autowired
	private KotakIntegrator kotakIntegrator;

	@Autowired
	private KotakUpiIntegrator kotakUpiIntegrator;
	
	@Autowired
	private KotakNBIntegrator kotakNBIntegrator;

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
			// New Order Transactions are not processed by Kotak
			return;
		}

		if (!fields.get(FieldType.ACQUIRER_TYPE.getName()).equals(AcquirerType.KOTAK.getCode())) {
			return;
		}
		// if the MOP Type is GPay
		String paymentType = fields.get(FieldType.PAYMENT_TYPE.getName());
		String mopType = fields.get(FieldType.MOP_TYPE.getName());

		if (paymentType.equals(PaymentType.UPI.getCode()) && mopType.equals(MopType.GOOGLEPAY.getCode())) {
			return;
		}
		// IF PAYMENT TYPE IS UPI
		if (paymentType.equals(PaymentType.UPI.getCode())) {

			kotakUpiIntegrator.process(fields);

		} else if (paymentType.equals(PaymentType.NET_BANKING.getCode())) {
			
			kotakNBIntegrator.process(fields);
		} else {

			kotakIntegrator.process(fields);
		}
	}

	@Override
	public void postProcess(Fields fields) throws SystemException {
	}
}
