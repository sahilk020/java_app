package com.pay10.ipay;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pay10.commons.exception.SystemException;
import com.pay10.commons.util.AcquirerType;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.MopType;
import com.pay10.commons.util.TransactionType;
import com.pay10.pg.core.util.Processor;

@Service("ipayProcessor")
public class IPayProcessor implements Processor {

	@Autowired
	private IPayIntegrator ipayIntegrator;

	@Override
	public void preProcess(Fields fields) throws SystemException {

	}

	@Override
	public void process(Fields fields) throws SystemException {
		String acquirerName = fields.get(FieldType.ACQUIRER_TYPE.getName());
		if (TransactionType.NEWORDER.getName().equals(fields.get(FieldType.TXNTYPE.getName()))
				|| TransactionType.STATUS.getName().equals(fields.get(FieldType.INTERNAL_ORIG_TXN_TYPE.getName()))
				|| fields.get(FieldType.INTERNAL_ORIG_TXN_TYPE.getName()).equals(TransactionType.VERIFY.getName())
				|| TransactionType.RECO.getName().equals(fields.get(FieldType.INTERNAL_ORIG_TXN_TYPE.getName()))
				|| TransactionType.REFUNDRECO.getName().equals(fields.get(FieldType.INTERNAL_ORIG_TXN_TYPE.getName()))
				|| !((AcquirerType.NB_SBI.getCode().equals(acquirerName))
						|| (AcquirerType.NB_SOUTH_INDIAN_BANK.getCode().equals(acquirerName))
						|| (AcquirerType.NB_CORPORATION_BANK.getCode().equals(acquirerName))
						|| (AcquirerType.NB_ICICI_BANK.getCode().equals(acquirerName))
						|| (AcquirerType.NB_KARUR_VYSYA_BANK.getCode().equals(acquirerName))
						|| (AcquirerType.NB_VIJAYA_BANK.getCode().equals(acquirerName))
						|| (AcquirerType.NB_ALLAHABAD_BANK.getCode().equals(acquirerName))
						|| (AcquirerType.NB_AXIS_BANK.getCode().equals(acquirerName))
						|| (AcquirerType.NB_KARNATAKA_BANK.getCode().equals(acquirerName))
						|| (AcquirerType.WL_MOBIKWIK.getCode().equals(acquirerName))
						|| (AcquirerType.WL_OLAMONEY.getCode().equals(acquirerName)))) {
			// New Order Transactions and Non Net Banking and Wallet transactions are not
			// processed by IPAY processor (this)
			return;
		}

		// if the MOP Type is GPay
		String mopType = fields.get(FieldType.MOP_TYPE.getName());

		if (mopType.equals(MopType.GOOGLEPAY.getCode())) {
			return;
		}

		ipayIntegrator.process(fields);

	}

	@Override
	public void postProcess(Fields fields) throws SystemException {

	}

}
