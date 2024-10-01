package com.pay10.payout;

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
import com.pay10.pg.core.util.Processor;

@Service("paytenPayoutProcessor")
public class PaytenPayoutProcessor implements Processor {
	private static Logger logger = LoggerFactory.getLogger(PaytenPayoutProcessor.class.getName());
	static Key secretKey = null;

	@Autowired
	private PaytenPayoutIntegrator paytenPayoutIntegrator;

	public void preProcess(Fields fields) throws SystemException {

	}

	@Override
	public void process(Fields fields) throws SystemException {
		
		
		if (!fields.get(FieldType.ACQUIRER_TYPE.getName()).equalsIgnoreCase(AcquirerType.PAY10.getCode())) {
			return;
		}
		
		
		if (fields.get(FieldType.TXNTYPE.getName()).equalsIgnoreCase(TransactionType.ENROLL.getName())) {
            fields.put(FieldType.TXNTYPE.getName(),TransactionType.SALE.getName());
            
        }

		if (fields.get(FieldType.TXNTYPE.getName()).equalsIgnoreCase(TransactionType.ENROLL.getName())||fields.get(FieldType.TXNTYPE.getName()).equalsIgnoreCase(TransactionType.SALE.getName())) {
            fields.put(FieldType.TXNTYPE.getName(),TransactionType.SALE.getName());
            
    		paytenPayoutIntegrator.process(fields);

            
        }
		System.out.println("Exit payout processor : "+fields.getFieldsAsString());

	}

	@Override
	public void postProcess(Fields fields) throws SystemException {

	}

}