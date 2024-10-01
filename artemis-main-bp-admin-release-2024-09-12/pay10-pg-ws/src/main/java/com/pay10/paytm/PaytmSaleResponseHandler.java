package com.pay10.paytm;

import java.util.Map;

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
public class PaytmSaleResponseHandler {

	private static Logger logger = LoggerFactory.getLogger(PaytmSaleResponseHandler.class.getName());

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
		String response = fields.get(FieldType.PAYTM_RESPONSE_FIELD.getName());
		transactionResponse = toTransaction(response);

		PaytmTransformer paytmTransformer = new PaytmTransformer(transactionResponse);
		paytmTransformer.updateResponse(fields);

		fields.put(FieldType.INTERNAL_ORIG_TXN_TYPE.getName(), fields.get(FieldType.TXNTYPE.getName()));
		fields.remove(FieldType.PAYTM_RESPONSE_FIELD.getName());
		ProcessManager.flow(updateProcessor, fields, true);
		return fields.getFields();

	}

	public Transaction toTransaction(String response) {

		Transaction transaction = new Transaction();

		if (StringUtils.isBlank(response)) {
			
			logger.info("Empty response received ");
			return transaction;
		}
		
		String respArray [] =  response.replace(" ","").split(";");
		
		for (String data : respArray) {
			
			if (data.contains("RESPMSG")) {
				
				String dataArray [] = data.split("=");
				transaction.setRESPMSG(dataArray[1]);
			}
			
			if (data.contains("RESPCODE")) {
				
				String dataArray [] = data.split("=");
				transaction.setRESPCODE(dataArray[1]);
				
			}
			
			if (data.contains("TXNID")) {
				
				String dataArray [] = data.split("=");
				
				if (dataArray[0].equalsIgnoreCase("TXNID")) {
					
					if (StringUtils.isNotBlank(dataArray[1])) {
						transaction.setTXNID(dataArray[1]);
					}
					
				}
				
			}
			
			if (data.contains("BANKTXNID")) {
			
				String dataArray [] = data.split("=");
				if (dataArray[0].equalsIgnoreCase("BANKTXNID")) {
					
					if (dataArray.length > 1 && StringUtils.isNotBlank(dataArray[1])) {
						transaction.setBANKTXNID(dataArray[1]);
					}
				}
				
			}
		}
		
		return transaction;
		
	}// toTransaction()

}
