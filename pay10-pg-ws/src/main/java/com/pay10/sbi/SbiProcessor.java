package com.pay10.sbi;

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
import com.pay10.sbi.upi.SbiUpiIntegrator;

@Service("sbiProcessor")
public class SbiProcessor implements Processor {

	@Autowired
	private SbiIntegrator sbiIntegrator;

	@Autowired
	private SbiUpiIntegrator sbiUpiIntegrator;

	public void preProcess(Fields fields) throws SystemException {
	}

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

		if (!fields.get(FieldType.ACQUIRER_TYPE.getName()).equals(AcquirerType.SBI.getCode())) {
			return;
		}
		// if the MOP Type is GPay
		String mopType = fields.get(FieldType.MOP_TYPE.getName());
		String paymentType = fields.get(FieldType.PAYMENT_TYPE.getName());
		if (mopType.equals(MopType.GOOGLEPAY.getCode())) {
			return;
		} else if (paymentType.equals(PaymentType.UPI.getCode())) {
			sbiUpiIntegrator.process(fields);
		}else {
			sbiIntegrator.process(fields);
		}

//		sbiIntegrator.process(fields);

	}

	public void postProcess(Fields fields) throws SystemException {
	}
}