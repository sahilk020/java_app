package com.pay10.payout.quomopay;

import java.security.Key;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.pay10.commons.exception.SystemException;
import com.pay10.commons.util.AcquirerType;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.TransactionType;
import com.pay10.payout.PaytenPayoutIntegrator;
import com.pay10.payout.PaytenPayoutProcessor;
import com.pay10.pg.core.util.Processor;

@Service("quomoPayoutProcess")
public class QuomoPayoutProcess implements Processor {
	private static Logger logger = LoggerFactory.getLogger(PaytenPayoutProcessor.class.getName());
	static Key secretKey = null;

	@Autowired
	private QuomoPayoutIntegration quomoPayoutIntegration;

	public void preProcess(Fields fields) throws SystemException {

	}

	@Override
	public void process(Fields fields) throws SystemException {

		System.out.println("Enter payout processor : " + fields.getFieldsAsString());
		String pgRefNo = fields.get(FieldType.PG_REF_NUM.getName());
		logger.info("Pay10 pgRefNo++process+++++++++++++++++++++++++++++++++++++++++++" + pgRefNo);
		if ((StringUtils.isNoneBlank(fields.get(FieldType.INTERNAL_ORIG_TXN_TYPE.getName()))
				&& fields.get(FieldType.INTERNAL_ORIG_TXN_TYPE.getName()).equals("PO_STATUS"))) {
			logger.info("process:: return without initializing integrator.");
			return;
		}

		if (!fields.get(FieldType.ACQUIRER_TYPE.getName()).equalsIgnoreCase(AcquirerType.QUOMO.getName())) {
			return;
		}

		if (fields.get(FieldType.TXNTYPE.getName()).equalsIgnoreCase(TransactionType.ENROLL.getName())
				|| fields.get(FieldType.TXNTYPE.getName()).equalsIgnoreCase(TransactionType.SALE.getName())) {
			fields.put(FieldType.TXNTYPE.getName(), TransactionType.SALE.getName());

			quomoPayoutIntegration.process(fields);

		}

		if (fields.get(FieldType.TXNTYPE.getName()).equalsIgnoreCase(TransactionType.STATUS.getName())) {

			quomoPayoutIntegration.processStatus(fields);

		}

		System.out.println("Exit payout processor : " + fields.getFieldsAsString());

	}

	@Override
	public void postProcess(Fields fields) throws SystemException {
		// TODO Auto-generated method stub

	}

}
