package com.pay10.ingenico;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.pay10.commons.exception.SystemException;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.TransactionManager;
import com.pay10.commons.util.Validator;
import com.pay10.pg.core.util.ProcessManager;
import com.pay10.pg.core.util.Processor;

@Service
public class IngenicoSaleResponseHandler {

	private static Logger logger = LoggerFactory.getLogger(IngenicoSaleResponseHandler.class.getName());

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
		String response = fields.get(FieldType.INGENICO_RESPONSE_FIELD.getName());
		transactionResponse = toTransaction(response);

		IngenicoTransformer ingenicoTransformer = new IngenicoTransformer(transactionResponse);
		ingenicoTransformer.updateResponse(fields);

		fields.put(FieldType.INTERNAL_ORIG_TXN_TYPE.getName(), fields.get(FieldType.TXNTYPE.getName()));
		fields.remove(FieldType.INGENICO_RESPONSE_FIELD.getName());
		ProcessManager.flow(updateProcessor, fields, true);
		return fields.getFields();

	}

	public Transaction toTransaction(String response) {

		Transaction transaction = new Transaction();
		
		if (StringUtils.isBlank(response)) {
			return transaction;
		}
		
		String responseArray [] = response.split(Pattern.quote("|") );
		Map<String,String> responseMap = new HashMap<String,String>();
		
		
		for (String data:responseArray ) {
		
			String dataArray [] = data.split("=");
			
			// Auth Code Sent is blank , hence check array length > 1 for response map
			if (dataArray.length > 1 ) {
				responseMap.put(dataArray[0], dataArray[1]);
			}
			
		}
		
		if (StringUtils.isNotBlank(responseMap.get(Constants.txn_status))) {
			transaction.setTxn_status(responseMap.get(Constants.txn_status));
		}
		
		if (StringUtils.isNotBlank(responseMap.get(Constants.txn_msg))) {
			transaction.setTxn_msg(responseMap.get(Constants.txn_msg));
		}
		
		if (StringUtils.isNotBlank(responseMap.get(Constants.txn_err_msg))) {
			transaction.setTxn_err_msg(responseMap.get(Constants.txn_err_msg));
		}
		
		if (StringUtils.isNotBlank(responseMap.get(Constants.clnt_txn_ref))) {
			transaction.setClnt_txn_ref(responseMap.get(Constants.clnt_txn_ref));
		}
		
		if (StringUtils.isNotBlank(responseMap.get(Constants.tpsl_bank_cd))) {
			transaction.setTpsl_bank_cd(responseMap.get(Constants.tpsl_bank_cd));
		}
		
		if (StringUtils.isNotBlank(responseMap.get(Constants.tpsl_txn_id))) {
			transaction.setTpsl_txn_id(responseMap.get(Constants.tpsl_txn_id));
		}
		
		if (StringUtils.isNotBlank(responseMap.get(Constants.tpsl_rfnd_id))) {
			transaction.setTpsl_rfnd_id(responseMap.get(Constants.tpsl_rfnd_id));
		}
		
		if (StringUtils.isNotBlank(responseMap.get(Constants.rqst_token))) {
			transaction.setRqst_token(responseMap.get(Constants.rqst_token));
		}
		
		return transaction;
		
	}// toTransaction()


}
