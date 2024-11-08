package com.pay10.pinelabs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.pay10.commons.exception.SystemException;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.TransactionType;

@Service
public class PinelabsIntegrator {
	@Autowired
	@Qualifier("pinelabsTransactionConverter")
	private TransactionConverter converter;

	@Autowired
	@Qualifier("pinelabsTransactionCommunicator")
	private TransactionCommunicator communicator;

	@Autowired
	private TransactionFactory TransactionFactory;

	private PinelabsTransformer pinelabsTransformer = null;

	public void process(Fields fields) throws SystemException {

		send(fields);

	}// process

	public void send(Fields fields) throws SystemException {

		Transaction transactionRequest = new Transaction();
		Transaction transactionResponse = new Transaction();
		String request = "";
		if (fields.get(FieldType.TXNTYPE.getName()).equalsIgnoreCase(TransactionType.ENROLL.getName())) {
			fields.put(FieldType.TXNTYPE.getName(), TransactionType.SALE.getName());
		}
		System.out.println("Integrator--txn type-" + fields.get(FieldType.TXNTYPE.getName()));
		String txnType = fields.get(FieldType.TXNTYPE.getName());
		if (txnType.equals(TransactionType.SALE.getName())) {
			transactionRequest = TransactionFactory.getInstance(fields);
			String tokenRequest = converter.tokenRequest(fields);
			String xVerify = converter.tokenXverify(tokenRequest, fields);
			System.out.println("Integrator--xverify-" + xVerify);
			// fields.put(FieldType.ADF10.getName(), xVerify);
			String tokenResponse = communicator.TokenPostRequest(tokenRequest, xVerify);
			transactionResponse = converter.toTransactionToken(tokenResponse);

			if (transactionResponse.responseCode.equals("1")
					&& transactionResponse.getResponseMsg().equals("SUCCESS")) {
				fields.put(FieldType.ADF10.getName(), transactionResponse.getToken());
				request = converter.perpareRequest(fields, transactionRequest, transactionResponse.getToken());
				tokenResponse = communicator.PostRequest(request, transactionResponse.getToken());
				transactionResponse = converter.toTransactionPayment(tokenResponse);
				if (transactionResponse.responseCode.equals("1")
						&& transactionResponse.getResponseMsg().equals("SUCCESS")) {
					request = transactionResponse.getToken();
				}
			}
		}

		if (txnType.equals(TransactionType.SALE.getName())) {
			communicator.updateSaleResponse(fields, request);
		} else if (txnType.equals(TransactionType.REFUND.getName())) {
			request = converter.perpareRequest(fields, transactionRequest, "");
			String response = communicator.getResponse(request, fields);

			transactionResponse = converter.toTransactionRefund(response);
			pinelabsTransformer = new PinelabsTransformer(transactionResponse);

			if (fields.get(FieldType.TXNTYPE.getName()).equalsIgnoreCase(TransactionType.REFUND.getName())) {
				pinelabsTransformer.updateRefundResponse(fields);
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

	public PinelabsTransformer getPinelabsTransformer() {
		return pinelabsTransformer;
	}

	public void setPinelabsTransformer(PinelabsTransformer pinelabsTransformer) {
		this.pinelabsTransformer = pinelabsTransformer;
	}

}
