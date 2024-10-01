package com.pay10.idfc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.exception.SystemException;
import com.pay10.commons.util.Amount;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.StatusType;
import com.pay10.commons.util.TransactionType;

@Service
public class idfcIntegrator {

	private static Logger logger = LoggerFactory.getLogger(idfcIntegrator.class.getName());

	@Autowired
	@Qualifier("idfcTransactionConverter")
	private TransactionConverter converter;

	
	public void process(Fields fields) throws SystemException {

		send(fields);

	}// process

	public void send(Fields fields) throws SystemException {

		Transaction transactionRequest = new Transaction();
		Transaction transactionResponse = new Transaction();

		if (fields.get(FieldType.TXNTYPE.getName()).equalsIgnoreCase(TransactionType.ENROLL.getName())) {
			fields.put(FieldType.TXNTYPE.getName(),TransactionType.SALE.getName());
		}
		

		String request = converter.perpareRequest(fields);

		String txnType = fields.get(FieldType.TXNTYPE.getName());
		if (txnType.equals(TransactionType.SALE.getName())) {
			logger.info("feilds for idfc"+fields.getFieldsAsString());

			fields.put(FieldType.IDFC_FINAL_REQUEST.getName(), request);
			fields.put(FieldType.STATUS.getName(), StatusType.SENT_TO_BANK.getName());
			fields.put(FieldType.RESPONSE_CODE.getName(), ErrorType.SUCCESS.getResponseCode());
			fields.put(FieldType.RESPONSE_MESSAGE.getName(), ErrorType.SUCCESS.getResponseMessage());
			logger.info("request"+request);

			logger.info("feilds in idfc itegrator"+fields.getFieldsAsString());

		} else {
			String response = converter.RestApiCall(fields,request);
			String toamount = Amount.toDecimal(fields.get(FieldType.AMOUNT.getName()),
					fields.get(FieldType.CURRENCY_CODE.getName()));
			 transactionResponse = converter.toTransaction(response,transactionResponse);
			 if (fields.get(FieldType.PG_REF_NUM.getName()).equalsIgnoreCase(transactionResponse.getPID())
					 &&toamount.equalsIgnoreCase(transactionResponse.getREFUNDAMT())) {

					idfcTransformer idfctransformer = new idfcTransformer(transactionResponse);
					idfctransformer.updateResponse(fields);
				} else {
					fields.put(FieldType.STATUS.getName(), StatusType.DENIED.getName());
					fields.put(FieldType.RESPONSE_CODE.getName(), ErrorType.SIGNATURE_MISMATCH.getCode());
					fields.put(FieldType.RESPONSE_MESSAGE.getName(), ErrorType.SIGNATURE_MISMATCH.getResponseMessage());
				}

			
		
		}

	}

}
