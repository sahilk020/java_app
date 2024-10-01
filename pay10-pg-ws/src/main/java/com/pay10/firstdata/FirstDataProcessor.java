package com.pay10.firstdata;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pay10.commons.exception.SystemException;
import com.pay10.commons.util.AcquirerType;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.MopType;
import com.pay10.commons.util.TransactionType;
import com.pay10.pg.core.util.Processor;

@Service("firstDataProcessor")
public class FirstDataProcessor implements Processor {

	@Autowired
	private FirstDataIntegrator firstDataIntegrator;

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
			// New Order Transactions are not processed by FirstData
			return;
		}

		if (!(fields.get(FieldType.ACQUIRER_TYPE.getName()).equals(AcquirerType.ICICI_FIRSTDATA.getCode())
				|| fields.get(FieldType.ACQUIRER_TYPE.getName()).equals(AcquirerType.IDFC_FIRSTDATA.getCode()))) {

			return;
		}
		// if the MOP Type is GPay
		String mopType = fields.get(FieldType.MOP_TYPE.getName());

		if (mopType.equals(MopType.GOOGLEPAY.getCode())) {
			return;
		}

		if (fields.get(FieldType.TXNTYPE.getName()).equals(TransactionType.ENROLL.getName())) {
			fields.put(FieldType.ORIG_TXN_ID.getName(), fields.get(FieldType.TXN_ID.getName()));
		} else if (fields.get(FieldType.TXNTYPE.getName()).equals(TransactionType.SALE.getName())) {
			fields.put(FieldType.ORIG_TXN_ID.getName(), fields.get(FieldType.INTERNAL_ORIG_TXN_ID.getName()));
		} else {
			fields.put(FieldType.ORIG_TXN_ID.getName(), fields.get(FieldType.ORIG_TXN_ID.getName()));
		}

		firstDataIntegrator.process(fields);
	}

	@Override
	public void postProcess(Fields fields) throws SystemException {
	}

}
