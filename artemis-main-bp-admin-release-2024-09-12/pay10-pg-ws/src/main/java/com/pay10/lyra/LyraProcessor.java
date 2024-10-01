package com.pay10.lyra;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pay10.commons.exception.SystemException;
import com.pay10.commons.util.AcquirerType;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.TransactionType;
import com.pay10.pg.core.util.Processor;

@Service("lyraProcessor")
public class LyraProcessor implements Processor {

	@Autowired
	private LyraNBIntegrator lyraNBIntegrator;

	public void preProcess(Fields fields) throws SystemException {
	}

	public void process(Fields fields) throws SystemException {

		if ((fields.get(FieldType.TXNTYPE.getName()).equals(TransactionType.NEWORDER.getName())
				|| fields.get(FieldType.INTERNAL_ORIG_TXN_TYPE.getName()).equals(TransactionType.STATUS.getName())
				|| fields.get(FieldType.INTERNAL_ORIG_TXN_TYPE.getName()).equals(TransactionType.VERIFY.getName())
				|| fields.get(FieldType.INTERNAL_ORIG_TXN_TYPE.getName()).equals(TransactionType.RECO.getName())
				|| fields.get(FieldType.INTERNAL_ORIG_TXN_TYPE.getName())
						.equals(TransactionType.REFUNDRECO.getName()))) {
			// New Order Transactions are not processed by LYRA
			return;
		}

		if (!fields.get(FieldType.ACQUIRER_TYPE.getName()).equals(AcquirerType.LYRA.getCode())) {
			return;
		}

		fields.put(FieldType.TXNTYPE.getName(), TransactionType.SALE.getCode());
		lyraNBIntegrator.process(fields);

	}

	public void postProcess(Fields fields) throws SystemException {
	}

}
