package com.pay10.migs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pay10.commons.exception.SystemException;
import com.pay10.commons.util.AcquirerType;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.TransactionType;
import com.pay10.pg.core.util.Processor;
@Service("ezeeClickProcessor")
public class EzeeClickProcessor implements Processor{
	@Autowired
	private EzeeClickIntegrator ezeeClickIntegrator;
	@Override
	public void preProcess(Fields fields) throws SystemException {
	}

	@Override
	public void process(Fields fields) throws SystemException {
		if ((fields.get(FieldType.TXNTYPE.getName()).equals(TransactionType.NEWORDER.getName()) || fields.get(FieldType.INTERNAL_ORIG_TXN_TYPE.getName()).equals(TransactionType.STATUS.getName()) || fields.get(FieldType.INTERNAL_ORIG_TXN_TYPE.getName()).equals(TransactionType.VERIFY.getName())|| fields.get(FieldType.INTERNAL_ORIG_TXN_TYPE.getName()).equals(TransactionType.RECO.getName()) || fields.get(FieldType.INTERNAL_ORIG_TXN_TYPE.getName()).equals(TransactionType.REFUNDRECO.getName()))) {
			// New Order Transactions are not processed by Amex
			return;
		}

		if(!fields.get(FieldType.ACQUIRER_TYPE.getName()).equals(AcquirerType.EZEECLICK.getCode())){
			return;
		}
		
		ezeeClickIntegrator.process(fields);
	}

	@Override
	public void postProcess(Fields fields) throws SystemException {
	}
}
