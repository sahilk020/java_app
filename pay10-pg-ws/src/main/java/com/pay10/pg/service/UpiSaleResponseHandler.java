package com.pay10.pg.service;

import java.util.Map;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.pay10.commons.exception.SystemException;
import com.pay10.commons.util.AcquirerType;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.TransactionManager;
import com.pay10.commons.util.Validator;
import com.pay10.hdfc.upi.HdfcUpiTransformer;
import com.pay10.idfcUpi.TransactionCommunicator;
import com.pay10.idfcUpi.TransactionConverter;
import com.pay10.pg.core.util.ProcessManager;
import com.pay10.pg.core.util.Processor;

@Service
public class UpiSaleResponseHandler {
	
	private static Logger logger = LoggerFactory.getLogger(UpiSaleResponseHandler.class.getName());
	
	@Autowired
	private Validator generalValidator;
	@Autowired
	@Qualifier("idfcUpiTransactionConverter")
	private TransactionConverter converter;
	
	@Autowired
	@Qualifier("idfcUpiTransactionCommunicator")
	private TransactionCommunicator communicator;

	@Autowired
	@Qualifier("hdfcUpiTransformer")
	private HdfcUpiTransformer hdfcUpiTransformer;

	@Autowired
	@Qualifier("updateProcessor")
	private Processor updateProcessor;
	
	public Map<String, String> process(Fields fields) throws SystemException {
		if (fields.get(FieldType.ACQUIRER_TYPE.getName()).equalsIgnoreCase("HDFC")) {
			hdfcUpiTransformer.updateResponsehdfc(fields);

		}
		logger.info("Inside UPI Sale Response Handler");
		String newTxnId = TransactionManager.getNewTransactionId();
		fields.put(FieldType.TXN_ID.getName(), newTxnId);
		
		if (fields.get(FieldType.RESPONSE_CODE.getName()).equals(Constants.KOTAK_UPI_CHECKSUM_FAILURE_CODE.getValue())) {
			
			fields.put(FieldType.INTERNAL_ORIG_TXN_TYPE.getName(), fields.get(FieldType.TXNTYPE.getName()));
			fields.put((FieldType.PAYMENTS_REGION.getName()),
					"DOMESTIC");
			fields.put((FieldType.CARD_HOLDER_TYPE.getName()),
					"CONSUMER");	
			ProcessManager.flow(updateProcessor, fields, true);
		}
		
		else {
			
		//generalValidator.validate(fields);
				
		fields.put(FieldType.INTERNAL_ORIG_TXN_TYPE.getName(), fields.get(FieldType.TXNTYPE.getName()));
		fields.put((FieldType.PAYMENTS_REGION.getName()),"DOMESTIC");
		fields.put((FieldType.CARD_HOLDER_TYPE.getName()),"CONSUMER");	
		ProcessManager.flow(updateProcessor, fields, true);
		}
		
		if (fields.get(FieldType.ACQUIRER_TYPE.getName()).equals(AcquirerType.IDFCUPI.getCode())) {
			JSONObject merchantStatusRes =  converter.merchatStatusUpdateResponse(fields);
		    //communicator.getVpaResponse(merchantStatusRes, fields);	
			fields.put((FieldType.IDFCUPI_RESPONSE_FIELD.getName()),
					merchantStatusRes.toString());	
		}
		return fields.getFields();
	
	
	}

}
