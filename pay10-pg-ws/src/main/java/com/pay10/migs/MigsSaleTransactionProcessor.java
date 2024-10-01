package com.pay10.migs;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.exception.SystemException;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.StatusType;
import com.pay10.pg.core.util.TransactionProcessor;

@Component("migsSale")
public class MigsSaleTransactionProcessor implements TransactionProcessor {
	private static Logger logger = LoggerFactory.getLogger( MigsSaleTransactionProcessor.class.getName());

	@Autowired
	@Qualifier("migsTransactionConverter")
	private TransactionConverter transactionConverter;

	@Autowired
	@Qualifier("migsTransactionCommunicator")
	private TransactionCommunicator communicator;

	@Override
	public void transact(Fields fields) throws SystemException {
		logger.info("Generating saleTransactionProcessor for Migs.");

		// to put OID for vpc_orderInfo field
		String oid = fields.get(FieldType.OID.getName());
		if (StringUtils.isBlank(oid)) {
			fields.put(FieldType.OID.getName(), fields.get(FieldType.INTERNAL_ORIG_TXN_ID.getName()));
		}

		String request = transactionConverter.getRequest(fields);

		communicator.getResponseString(request, fields);
		fields.put(FieldType.STATUS.getName(), StatusType.ENROLLED.getName());
		fields.put(FieldType.RESPONSE_CODE.getName(), ErrorType.SUCCESS.getResponseCode());
		fields.put(FieldType.RESPONSE_MESSAGE.getName(), ErrorType.SUCCESS.getResponseMessage());

	}
}