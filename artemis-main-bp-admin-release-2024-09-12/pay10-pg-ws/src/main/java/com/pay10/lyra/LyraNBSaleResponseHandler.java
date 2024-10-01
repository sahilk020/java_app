package com.pay10.lyra;

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
public class LyraNBSaleResponseHandler {
	private static Logger logger = LoggerFactory.getLogger(LyraNBSaleResponseHandler.class.getName());

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
		Transaction transactionResponse = new Transaction();
		JSONObject response = new JSONObject(fields.get(FieldType.LYRA_RESPONSE_FIELD.getName()));
		transactionResponse = toTransaction(response);

		lyraTransformer = new LyraTransformer(transactionResponse);
		lyraTransformer.updateNBResponse(fields);

		historian.addPreviousSaleFields(fields);
		fields.put(FieldType.INTERNAL_ORIG_TXN_TYPE.getName(), fields.get(FieldType.TXNTYPE.getName()));
		fields.remove(FieldType.LYRA_RESPONSE_FIELD.getName());
		ProcessManager.flow(updateProcessor, fields, true);
		return fields.getFields();

	}

	public Transaction toTransaction(JSONObject response) {

		Transaction transaction = new Transaction();
		transaction.setStatus(response.get("status").toString());
		transaction.setPgDateTime(response.get("date").toString());
		transaction.setUuid(response.get("uuid").toString());

		return transaction;

	}

}
