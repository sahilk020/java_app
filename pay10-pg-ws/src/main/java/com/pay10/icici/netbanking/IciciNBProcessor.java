package com.pay10.icici.netbanking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pay10.commons.exception.SystemException;
import com.pay10.commons.util.AcquirerType;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.MopType;
import com.pay10.commons.util.PaymentType;
import com.pay10.commons.util.TransactionType;
import com.pay10.pg.core.util.Processor;
import com.pay10.icici.upi.ICICIUpiIntegrator;

@Service("iciciBankNBProcessor")
public class IciciNBProcessor implements Processor {

	@Autowired
	private IciciNBIntegrator iciciNBIntegrator;
	
	@Autowired
	private ICICIUpiIntegrator iciciUpiIntegrator;

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
			// New Order Transactions are not processed by BOB
			return;
		}

		if (!fields.get(FieldType.ACQUIRER_TYPE.getName()).equals(AcquirerType.ICICIBANK.getCode())) {
			return;
		}
		// if the MOP Type is GPay
		String mopType = fields.get(FieldType.MOP_TYPE.getName());

		String paymentType = fields.get(FieldType.PAYMENT_TYPE.getName());
		if (mopType.equals(MopType.GOOGLEPAY.getCode())) {
			return;
		}
		if (paymentType.equals(PaymentType.UPI.getCode())) {
			iciciUpiIntegrator.process(fields);
		} else {
			iciciNBIntegrator.process(fields);
		}


		iciciNBIntegrator.process(fields);

	}

	@Override
	public void postProcess(Fields fields) throws SystemException {
	}
}