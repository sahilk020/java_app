package com.pay10.paymentEdge;

import java.util.Map;

import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.util.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.pay10.commons.exception.SystemException;
import com.pay10.pg.core.util.ProcessManager;
import com.pay10.pg.core.util.Processor;

@Service
public class PaymentEdgeSaleResponseHandler {

	private static Logger logger = LoggerFactory.getLogger(PaymentEdgeSaleResponseHandler.class.getName());

	@Autowired
	private Validator generalValidator;

	@Autowired
	@Qualifier("updateProcessor")
	private Processor updateProcessor;

	public Map<String, String> process(Fields fields) throws SystemException {

		String newTxnId = TransactionManager.getNewTransactionId();
		fields.put(FieldType.TXN_ID.getName(), newTxnId);
		generalValidator.validate(fields);
		Transaction transactionResponse = new Transaction();
		String response = fields.get(FieldType.PAYMENTAGE_FINAL_RESPONSE.getName());
		transactionResponse = toTransaction(response,fields);
		logger.info("Transaction Response For PaymentEdge : OrderId :" + fields.get(FieldType.ORDER_ID.getName())
				+ " Txn Response : " + transactionResponse.toString());
		if(StringUtils.equalsIgnoreCase(transactionResponse.getResponseCode(), ErrorType.PENDING.getResponseCode())
			||	StringUtils.equalsIgnoreCase(transactionResponse.getStatus(), StatusType.SENT_TO_BANK.getName())
				||	StringUtils.equalsIgnoreCase(transactionResponse.getStatus(), StatusType.DENIED.getName())) {
			logger.info("PaymentEdge Sale Response Received For Pending ::  pgRefNo={}", fields.get(FieldType.PG_REF_NUM.getName()));
			fields.put(FieldType.STATUS.getName(), StatusType.SENT_TO_BANK.getName());
			fields.put(FieldType.RESPONSE_CODE.getName(), ErrorType.PENDING.getResponseCode());
			fields.put(FieldType.RESPONSE_MESSAGE.getName(), ErrorType.PENDING.getResponseMessage());
			fields.put(FieldType.INTERNAL_ORIG_TXN_TYPE.getName(), fields.get(FieldType.TXNTYPE.getName()));
			fields.put(FieldType.RRN.getName(), transactionResponse.getRrn());
			fields.put(FieldType.ACQ_ID.getName(), transactionResponse.getAcqId());
			fields.put(FieldType.PG_RESP_CODE.getName(), ErrorType.PENDING.getResponseCode());
			fields.put(FieldType.PG_TXN_STATUS.getName(), StatusType.SENT_TO_BANK.getName());
			fields.put(FieldType.PG_TXN_MESSAGE.getName(), ErrorType.PENDING.getResponseMessage());

			fields.remove(FieldType.PAYMENTAGE_FINAL_RESPONSE.getName());
			return fields.getFields();
		}
		PaymentEdgeTransformer paymentEdgeTransformer = new PaymentEdgeTransformer(transactionResponse);
		paymentEdgeTransformer.updateResponse(fields);

		fields.put(FieldType.INTERNAL_ORIG_TXN_TYPE.getName(), fields.get(FieldType.TXNTYPE.getName()));
		fields.remove(FieldType.PAYMENTAGE_FINAL_RESPONSE.getName());
		ProcessManager.flow(updateProcessor, fields, true);
		return fields.getFields();

	}

	public Transaction toTransaction(String response,Fields fields) {

		Transaction transaction = new Transaction();

		String respSplit [] = response.split("~");
		
		for (String data : respSplit) {
			
			String dataSplit [] = data.split("=");
			
			String key = dataSplit[0];
			String value = dataSplit[1];
			
			if (key.equalsIgnoreCase("RESPONSE_CODE")) {
				transaction.setResponseCode(value);
			}
			
			else if (key.equalsIgnoreCase("STATUS")) {
				transaction.setStatus(value);
			}
			
			else if (key.equalsIgnoreCase("ACQ_ID")) {
				transaction.setAcqId(value);
			}
			
			else if (key.equalsIgnoreCase("RRN")) {
				transaction.setRrn(value);
			}
			
			else if (key.equalsIgnoreCase("PG_TXN_MESSAGE")) {
				transaction.setPgTxnMsg(value);
			}
			
			else if (key.equalsIgnoreCase("ORDER_ID")) {
				transaction.setOrderId(value);
			}
			
		}
		
		return transaction;
		
	}// toTransaction()

}
