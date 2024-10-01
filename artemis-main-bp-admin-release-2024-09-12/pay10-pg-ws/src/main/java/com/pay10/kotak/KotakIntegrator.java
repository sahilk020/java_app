package com.pay10.kotak;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.pay10.commons.exception.SystemException;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.TransactionType;


@Service
public class KotakIntegrator {
	private static Logger logger = LoggerFactory.getLogger(KotakIntegrator.class.getName());

	@Autowired
	@Qualifier("kotakTransactionConverter")
	private TransactionConverter converter;
	
	@Autowired
	@Qualifier("kotakTransactionCommunicator")
	private TransactionCommunicator communicator;
	
	
	
	
	@Autowired
	private TransactionFactory TransactionFactory;
	
	private KotakTransformer kotakTransformer = null;

	public void process(Fields fields) throws SystemException {

		send(fields);

	}//process
	
	public void send(Fields fields) throws SystemException {
		
		Transaction transactionRequest = new Transaction();
		Transaction transactionResponse = new Transaction();
		if (fields.get(FieldType.TXNTYPE.getName()).equalsIgnoreCase(TransactionType.ENROLL.getName())) {
			fields.put(FieldType.TXNTYPE.getName(),TransactionType.SALE.getName());
		}
		transactionRequest = TransactionFactory.getInstance(fields);

		String request = converter.perpareRequest(fields, transactionRequest);
		logger.info("AuthenticationData  response " + fields.getFieldsAsString());

		String txnType = fields.get(FieldType.TXNTYPE.getName());
		if (txnType.equals(TransactionType.SALE.getName())) {
			//communicator.updateSaleResponse(fields, request);
		} else {
			Transaction refundResponse = new Transaction();
			refundResponse = toTransaction(request);
		

			
			if ((refundResponse.getAmount()).equalsIgnoreCase(fields.get(FieldType.AMOUNT.getName()))
					&&(refundResponse.getMerchantReference()).equalsIgnoreCase(fields.get(FieldType.ORDER_ID.getName()))){
			
				if(StringUtils.isBlank(fields.get(FieldType.PG_REF_NUM.getName()))){
					fields.put(FieldType.PG_REF_NUM.getName(), fields.get(FieldType.TXN_ID.getName()));
					}
				
				if(StringUtils.isBlank(fields.get(FieldType.ORIG_TXN_ID.getName()))){
					fields.put(FieldType.ORIG_TXN_ID.getName(), fields.get(FieldType.TXN_ID.getName()));
					}
				kotakTransformer = new KotakTransformer(refundResponse);
				kotakTransformer.updateresponseKotakCard(fields);
		}}

	}
	
	public Transaction toTransaction(String response) {
		Transaction transaction = new Transaction();

		JSONObject finalResponse = new JSONObject(response);

		transaction.setResponseMessage((String) finalResponse.get("ResponseMessage"));
		transaction.setResponseCode((String) finalResponse.get("ResponseCode"));
		transaction.setAmount((String) finalResponse.get("RefundAmount"));
		transaction.setAcqId((String) finalResponse.get("RetRefNo"));
		transaction.setMerchantReference((String) finalResponse.get("OrderId"));
		logger.info("transcation " + transaction.toString());
		return transaction;

	}

	public TransactionConverter getConverter() {
		return converter;
	}

	public void setConverter(TransactionConverter converter) {
		this.converter = converter;
	}

	public TransactionCommunicator getCommunicator() {
		return communicator;
	}

	public void setCommunicator(TransactionCommunicator communicator) {
		this.communicator = communicator;
	}

	/*public Transaction getTransactionRequest() {
		return transactionRequest;
	}

	public void setTransactionRequest(Transaction transactionRequest) {
		this.transactionRequest = transactionRequest;
	}

	public Transaction getTransactionResponse() {
		return transactionResponse;
	}

	public void setTransactionResponse(Transaction transactionResponse) {
		this.transactionResponse = transactionResponse;
	}*/

	public KotakTransformer getKotakTransformer() {
		return kotakTransformer;
	}

	public void setKotakTransformer(KotakTransformer kotakTransformer) {
		this.kotakTransformer = kotakTransformer;
	}

}
