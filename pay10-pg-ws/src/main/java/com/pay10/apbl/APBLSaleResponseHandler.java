package com.pay10.apbl;

import java.util.HashMap;
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
public class APBLSaleResponseHandler {

	private static Logger logger = LoggerFactory.getLogger(APBLSaleResponseHandler.class.getName());

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
		String response = fields.get(FieldType.APBL_RESPONSE_FIELD.getName());
		transactionResponse = toTransaction(response);

		APBLTransformer apblTransformer = new APBLTransformer(transactionResponse);
		apblTransformer.updateResponse(fields);

		fields.put(FieldType.INTERNAL_ORIG_TXN_TYPE.getName(), fields.get(FieldType.TXNTYPE.getName()));
		fields.remove(FieldType.APBL_RESPONSE_FIELD.getName());
		ProcessManager.flow(updateProcessor, fields, true);
		return fields.getFields();

	}

	public Transaction toTransaction(String response) {

		Transaction transaction = new Transaction();
		
		
		  if (StringUtils.isBlank(response)) {
			  
			  return transaction; }
		  
		  String responseArray [] = response.split(";"); 
		  
		  Map<String,String> responseMap = new HashMap<String,String>();
		  
		  
		  for (String data:responseArray ) {
		  
		  String dataArray [] = data.split("=");
		  
		   if  (dataArray.length > 1 ) { 
			   responseMap.put(dataArray[0], dataArray[1]);
			   }
		  
		  }
		  
		  
		  if (StringUtils.isNotBlank(responseMap.get(Constants.CODE))) {
		  transaction.setCODE(responseMap.get(Constants.CODE)); }
		  
		  if (StringUtils.isNotBlank(responseMap.get(Constants.STATUS))) {
		  transaction.setSTATUS(responseMap.get(Constants.STATUS)); }
		  
		  if (StringUtils.isNotBlank(responseMap.get(Constants.MSG))) {
		  transaction.setMSG(responseMap.get(Constants.MSG)); }
		  
		  if (StringUtils.isNotBlank(responseMap.get(Constants.TRAN_ID))) {
		  transaction.setTRAN_ID(responseMap.get(Constants.TRAN_ID)); }
		  
		
		return transaction;
		
	}// toTransaction()

}
