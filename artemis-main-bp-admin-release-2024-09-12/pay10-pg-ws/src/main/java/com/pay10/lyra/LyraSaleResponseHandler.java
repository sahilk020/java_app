package com.pay10.lyra;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.pay10.commons.exception.SystemException;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.TransactionManager;
import com.pay10.pg.core.util.ProcessManager;
import com.pay10.pg.core.util.Processor;
import com.pay10.pg.history.Historian;

@Service
public class LyraSaleResponseHandler {

	private static Logger logger = LoggerFactory.getLogger(LyraSaleResponseHandler.class.getName());

	@Autowired
	private Historian historian;

	@Autowired
	@Qualifier("updateProcessor")
	private Processor updateProcessor;

	@Autowired
	private LyraTransformer lyraTransformer;

	public Map<String, String> process(Fields fields) throws SystemException {
		String newTxnId = TransactionManager.getNewTransactionId();
		fields.put(FieldType.TXN_ID.getName(), newTxnId);
		// generalValidator.validate(fields);
		Transaction transactionResponse = new Transaction();
		JSONObject response = new JSONObject(fields.get(FieldType.LYRA_RESPONSE_FIELD.getName()));
		transactionResponse = toTransaction(response);

		lyraTransformer = new LyraTransformer(transactionResponse);
		lyraTransformer.updateResponse(fields);

		historian.addPreviousSaleFields(fields);
		fields.put(FieldType.INTERNAL_ORIG_TXN_TYPE.getName(), fields.get(FieldType.TXNTYPE.getName()));
		fields.remove(FieldType.LYRA_RESPONSE_FIELD.getName());
		ProcessManager.flow(updateProcessor, fields, true);
		return fields.getFields();

	}

	public Transaction toTransaction(JSONObject response) {

		Transaction transaction = new Transaction();

		Map<String, String> responseMap = new HashMap<String, String>();
		Map<String, String> transactionDetailsMap = new HashMap<String, String>();
		String txn = response.get("transactions").toString();
		txn = txn.substring(1, txn.length() - 1);
		JSONObject txnJson = new JSONObject(txn);
		
		JSONObject transactionDetails = txnJson.getJSONObject("transactionDetails");
		for (Object key : response.keySet()) {

			String key1 = key.toString();
			String value = response.get(key.toString()).toString();
			responseMap.put(key1, value);
		}
		for (Object key : transactionDetails.keySet()) {

			String key1 = key.toString();
			String value = transactionDetails.get(key.toString()).toString();
			transactionDetailsMap.put(key1, value);
		}

		transaction.setStatus(responseMap.get("orderStatus"));
		transaction.setPgDateTime(responseMap.get("serverDate"));
		transaction.setUuid(txnJson.get("uuid").toString());
		transaction.setErrorCode(txnJson.get("detailedErrorCode").toString());
		transaction.setErrorMessage(txnJson.get("errorMessage").toString());
		transaction.setRrn(transactionDetailsMap.get("externalTransactionId"));
		return transaction;

	}

}
