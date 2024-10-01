package com.pay10.axis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pay10.axisbank.card.AxisBankCardIntegrator;
import com.pay10.axisbank.netbanking.AxisBankNBIntegrator;
import com.pay10.axisbank.upi.AxisBankUpiIntegrator;
import com.pay10.commons.exception.SystemException;
import com.pay10.commons.util.AcquirerType;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.MopType;
import com.pay10.commons.util.PaymentType;
import com.pay10.commons.util.TransactionType;
import com.pay10.pg.core.util.Processor;

@Service("axisBankProcessor")
public class AxisBankProcessor implements Processor {

	@Autowired
	private AxisBankCardIntegrator axisBankCardIntegrator;
	
	@Autowired
	private AxisBankUpiIntegrator axisBankUpiIntegrator;
	
	@Autowired
	private AxisBankNBIntegrator axisBankNBIntegrator;
	
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

		if (!fields.get(FieldType.ACQUIRER_TYPE.getName()).equals(AcquirerType.AXISBANK.getCode())) {
			return;
		}
		// if the MOP Type is GPay
		String mopType = fields.get(FieldType.MOP_TYPE.getName());
		String paymentType = fields.get(FieldType.PAYMENT_TYPE.getName());

		if (mopType.equals(MopType.GOOGLEPAY.getCode())) {
			return;
		}
		else if (paymentType.equals(PaymentType.UPI.getCode())) {
			axisBankUpiIntegrator.process(fields);
		}
		else if (paymentType.equals(PaymentType.NET_BANKING.getCode())) {
			axisBankNBIntegrator.process(fields);
		} 
		else {
			axisBankCardIntegrator.process(fields);
		}
	}

	@Override
	public void postProcess(Fields fields) throws SystemException {
	}
}