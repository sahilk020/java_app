package com.pay10.camspay;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.google.gson.JsonObject;
import com.pay10.commons.exception.SystemException;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.PaymentType;
import com.pay10.commons.util.TransactionType;
import com.pay10.pg.core.camspay.util.CamsPayHasher;

import bsh.This;

@Service
public class CamsPayIntegrator {

	private static final Logger logger = LoggerFactory.getLogger(This.class.getName());

	@Autowired
	@Qualifier("camsPayTransactionConverter")
	private TransactionConverter converter;

	@Autowired
	@Qualifier("camsPayTransactionCommunicator")
	private TransactionCommunicator communicator;

	@Autowired
	@Qualifier("camsPayFactory")
	private TransactionFactory transactionFactory;

	private CamsPayTransformer camsPayTransformer = null;

	public void process(Fields fields) throws SystemException {
		logger.info("process:: Initialized. fields={}", fields.getFieldsAsString());
		send(fields);
	}

	// process
	public void send(Fields fields) throws SystemException {

		logger.info("fields values: "+fields.getFieldsAsString());
		Transaction transactionResponse = new Transaction();

		transactionFactory.getInstance(fields);

		String request = converter.perpareRequest(fields);

		String txnType = fields.get(FieldType.TXNTYPE.getName());
		logger.info("send:: txnType={}", txnType);
		if (txnType.equals(TransactionType.SALE.getName())) {
			communicator.updateSaleResponse(fields, request);
		} else {
			if(fields.get(FieldType.PAYMENT_TYPE.getName()).equalsIgnoreCase(PaymentType.UPI.getCode())) {
				String response = communicator.getResponse(request, fields);
				logger.info("Refund Response "+response);
				JSONObject json = new JSONObject(response);
				logger.info("Refund Response :: "+json.getString("res"));
				String decryptedResp = null ;
				try {
					decryptedResp = CamsPayHasher.decryptMessage(json.getString("res"),"01WERdzfsdg$#$","camspayaesvector");
				} catch (Exception e) {
					logger.info("Exception in camspay decryptedResp "+e);
				}
				logger.info("decryptedResp for camspay refund :"+decryptedResp);
				transactionResponse = converter.toTransaction(decryptedResp, fields.get(FieldType.TXNTYPE.getName()));
				camsPayTransformer = new CamsPayTransformer(transactionResponse);
				//camsPayTransformer.updateRefundResponse(fields);
			}
		}

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

	public CamsPayTransformer getCamsPayTransformer() {
		return camsPayTransformer;
	}

	public void setCamsPayTransformer(CamsPayTransformer camsPayTransformer) {
		this.camsPayTransformer = camsPayTransformer;
	}

}
