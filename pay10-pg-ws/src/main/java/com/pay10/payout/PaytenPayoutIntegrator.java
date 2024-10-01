package com.pay10.payout;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.exception.SystemException;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.StatusType;
import com.pay10.commons.util.StatusTypePayout;
import com.pay10.commons.util.TransactionType;

@Service
public class PaytenPayoutIntegrator {

	private static final Logger logger = LoggerFactory.getLogger(PaytenPayoutIntegrator.class.getName());
	@Autowired
	@Qualifier("PayoutTransactionConverter")
	private TransactionConverter converter;

	public void process(Fields fields) throws SystemException {

		send(fields);

	}//process
	

	

	public void send(Fields fields) throws SystemException {

		logger.info("Field in pay10 payout={}", fields.getFieldsAsString());

		String txnType = fields.get(FieldType.TXNTYPE.getName());
		if (txnType.equalsIgnoreCase(TransactionType.SALE.getName())) {
			fields.put(FieldType.ORIG_TXN_ID.getName(), fields.get(FieldType.TXN_ID.getName()));

			converter.perpareRequest(fields);
		}

	}

	public TransactionConverter getConverter() {
		return converter;
	}

	public void setConverter(TransactionConverter converter) {
		this.converter = converter;
	}

}