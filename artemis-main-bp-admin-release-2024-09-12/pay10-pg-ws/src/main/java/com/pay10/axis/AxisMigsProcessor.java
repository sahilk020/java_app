package com.pay10.axis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pay10.cSource.CSourceIntegrator;
import com.pay10.commons.exception.SystemException;
import com.pay10.commons.util.AcquirerType;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.MopType;
import com.pay10.commons.util.TransactionType;
import com.pay10.pg.core.util.Processor;

@Service("axisProcessor")
public class AxisMigsProcessor implements Processor {

	@Autowired
	private AxisMigsIntegrator axisMigsIntegrator;

	@Autowired
	private CSourceIntegrator cSourceIntegrator;

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
			// New Order Transactions are not processed by Migs
			return;
		}
		// if the MOP Type is GPay
		String mopType = fields.get(FieldType.MOP_TYPE.getName());

		if (mopType.equals(MopType.GOOGLEPAY.getCode())) {
			return;
		}
		if (fields.get(FieldType.ACQUIRER_TYPE.getName()).equals(AcquirerType.AXIS_CB.getCode())) {
			cSourceIntegrator.process(fields);
		} else if (fields.get(FieldType.ACQUIRER_TYPE.getName()).equals(AcquirerType.AXISMIGS.getCode())) {
			axisMigsIntegrator.process(fields);
		} else {
			return;
		}

	}

	@Override
	public void postProcess(Fields fields) throws SystemException {
	}
}