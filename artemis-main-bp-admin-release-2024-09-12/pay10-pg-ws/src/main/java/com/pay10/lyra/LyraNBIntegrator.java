package com.pay10.lyra;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.exception.SystemException;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.PaymentType;
import com.pay10.commons.util.StatusType;
import com.pay10.commons.util.TransactionType;

@Service
public class LyraNBIntegrator {

	@Autowired
	private TransactionConverter converter;

	@Autowired
	private TransactionCommunicator communicator;

	@Autowired
	private TransactionFactory TransactionFactory;

	private LyraTransformer lyraTransformer = null;

	public void process(Fields fields) throws SystemException {

		send(fields);

	}// process

	public void send(Fields fields) throws SystemException {

		Transaction transactionRequest = new Transaction();
		Transaction transactionResponse = new Transaction();

		transactionRequest = TransactionFactory.getInstance(fields);
		String txnType = fields.get(FieldType.TXNTYPE.getName());
		if (txnType.equals(TransactionType.SALE.getName())) {
			String createRequest = converter.createChargeRequest(fields);
			JSONObject response = communicator.getNBResponse(createRequest, fields, transactionResponse);
			transactionResponse = converter.nbChargeToTransaction(response);
			if (StringUtils.isNotBlank(transactionResponse.getUuid())) {
				fields.put(FieldType.ACQ_ID.getName(), transactionResponse.getUuid());
				String uuidRequest = converter.uuidRequest(fields, transactionRequest);
				transactionResponse.setChargeFlag("Y");
				response = communicator.getNBResponse(uuidRequest, fields, transactionResponse);
				if (fields.get(FieldType.PAYMENT_TYPE.getName()).equalsIgnoreCase(PaymentType.UPI.getCode())) {
					transactionResponse = converter.upiUuidToTransaction(response);
					communicator.updateUpiSaleResponse(fields, transactionResponse);
				} else {
					transactionResponse = converter.nbUuidToTransaction(response);
					communicator.updateSaleResponse(fields, transactionResponse);
				}
			} else {
				fields.put(FieldType.STATUS.getName(), StatusType.REJECTED.getName());
				fields.put(FieldType.RESPONSE_CODE.getName(), ErrorType.REJECTED.getCode());
				fields.put(FieldType.RESPONSE_MESSAGE.getName(), ErrorType.REJECTED.getInternalMessage());
			}
		} else {
			String refundRequest = converter.createNBRefundRequest(fields);
			JSONObject response = communicator.getNBResponse(refundRequest, fields, transactionResponse);
			transactionResponse = converter.nbRefundToTransaction(response);
			lyraTransformer = new LyraTransformer(transactionResponse);
			lyraTransformer.updateNBRefundResponse(fields);
		}
	}

}
