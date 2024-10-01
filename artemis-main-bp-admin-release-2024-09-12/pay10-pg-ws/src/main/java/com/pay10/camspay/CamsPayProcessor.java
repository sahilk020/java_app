package com.pay10.camspay;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pay10.commons.exception.SystemException;
import com.pay10.commons.util.AcquirerType;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.TransactionType;
import com.pay10.pg.core.util.Processor;

import bsh.This;

@Service("camsPayProcessor")
public class CamsPayProcessor implements Processor {

	private static final Logger logger = LoggerFactory.getLogger(This.class.getName());

	@Autowired
	private CamsPayIntegrator camsPayIntegrator;

	@Override
	public void preProcess(Fields fields) throws SystemException {
	}

	@Override
	public void process(Fields fields) throws SystemException {

		logger.info("process:: txnType={}", fields.get(FieldType.TXNTYPE.getName()));
		if ((fields.get(FieldType.TXNTYPE.getName()).equals(TransactionType.NEWORDER.getName())
				|| fields.get(FieldType.INTERNAL_ORIG_TXN_TYPE.getName()).equals(TransactionType.STATUS.getName())
				|| fields.get(FieldType.INTERNAL_ORIG_TXN_TYPE.getName()).equals(TransactionType.VERIFY.getName())
				|| fields.get(FieldType.INTERNAL_ORIG_TXN_TYPE.getName()).equals(TransactionType.RECO.getName())
				|| fields.get(FieldType.INTERNAL_ORIG_TXN_TYPE.getName())
						.equals(TransactionType.REFUNDRECO.getName()))) {
			logger.info("process:: return without initializing integrator.");
			return;
		}

		if (!fields.get(FieldType.ACQUIRER_TYPE.getName()).equals(AcquirerType.CAMSPAY.getCode())) {
			return;
		}
		camsPayIntegrator.process(fields);
	}

	@Override
	public void postProcess(Fields fields) throws SystemException {
	}
}