package com.pay10.icici.mpgs;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.pay10.commons.exception.SystemException;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.TransactionType;

/**
 * @author Rahul
 *
 */

@Service
public class IciciMpgsIntegrator {

	@Autowired
	@Qualifier("iciciMpgsTransactionConverter")
	private TransactionConverter converter;

	@Autowired
	@Qualifier("iciciMpgsTransactionCommunicator")
	private TransactionCommunicator communicator;

	@Autowired
	private TransactionFactory TransactionFactory;

	private IciciMpgsTransformer iciciMpgsTransformer = null;

	public void process(Fields fields) throws SystemException {

		send(fields);

	}// process

	public void send(Fields fields) throws SystemException {

		Transaction transactionRequest = new Transaction();
		Transaction transactionResponse = new Transaction();
		JSONObject request = new JSONObject();
		JSONObject response = new JSONObject();
		String txnType = fields.get(FieldType.TXNTYPE.getName());
		if (txnType.equalsIgnoreCase(TransactionType.SALE.getName())) {
			request = converter.acsProcessRequest(fields);
			response = communicator.getAcsResponse(request, fields);
			transactionResponse = converter.toTransaction(response);
		}

		transactionRequest = TransactionFactory.getInstance(fields);
		request = converter.perpareRequest(fields, transactionRequest, transactionResponse);
		response = communicator.getResponse(request, fields);
		if (txnType.equalsIgnoreCase(TransactionType.ENROLL.getName())) {
			transactionResponse = converter.toTransaction(response);
		} else {
			transactionResponse = converter.toSaleTransaction(response);
		}
		if (txnType.equalsIgnoreCase(TransactionType.SALE.getName())) {
			fields.put(FieldType.ORIG_TXN_ID.getName(), fields.get(FieldType.TXN_ID.getName()));
		}
		iciciMpgsTransformer = new IciciMpgsTransformer(transactionResponse);
		iciciMpgsTransformer.updateResponse(fields);
	}

}
