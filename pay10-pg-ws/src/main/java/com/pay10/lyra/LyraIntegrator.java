package com.pay10.lyra;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pay10.commons.exception.SystemException;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.TransactionType;

@Service
public class LyraIntegrator {

	@Autowired
	private TransactionConverter converter;

	@Autowired
	private TransactionCommunicator communicator;

	@Autowired
	private TransactionFactory TransactionFactory;

	private LyraTransformer lyraTransformer = null;

	public void process(Fields fields) throws SystemException {

		send(fields);

	}

	public void send(Fields fields) throws SystemException {

		Transaction transactionRequest = new Transaction();
		Transaction transactionResponse = new Transaction();

		transactionRequest = TransactionFactory.getInstance(fields);
		String request = converter.perpareRequest(fields, transactionRequest);
		JSONObject response = communicator.getResponse(request, fields);
		String txnType = fields.get(FieldType.TXNTYPE.getName());
		if (txnType.equals(TransactionType.ENROLL.getName())) {
			transactionResponse = converter.toTransaction(response);
		} else {
			transactionResponse = converter.refundToTransaction(response);
		}
		lyraTransformer = new LyraTransformer(transactionResponse);
		lyraTransformer.updateResponse(fields);
	}

}
