package com.pay10.pg.autodebit;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.pay10.commons.api.Hasher;
import com.pay10.commons.exception.SystemException;
import com.pay10.commons.util.Amount;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.TransactionManager;
import com.pay10.commons.util.Validator;
import com.pay10.pg.core.util.MerchantIPayUtil;
import com.pay10.pg.core.util.ProcessManager;
import com.pay10.pg.core.util.Processor;


@Service
public class AtlSaleResponseHandler {
	
	private static Logger logger = LoggerFactory.getLogger(AtlSaleResponseHandler.class.getName());

	@Autowired
	private Validator generalValidator;

	@Autowired
	@Qualifier("updateProcessor")
	private Processor updateProcessor;
	
	@Autowired
	private AtlTransformer atlTransformer;
	
	@Autowired
	@Qualifier("atlTransactionConverter")
	private TransactionConverter converter;
	
	@Autowired
	@Qualifier("atlTransactionCommunicator")
	private TransactionCommunicator transactionCommunicator;
	
	public Map<String, String> process(Fields fields) throws SystemException {
		
		String encryptedString = fields.get(FieldType.ATL_RESPONSE_FIELD.getName());
		String key = fields.get(FieldType.TXN_KEY.getName());
		String iv = fields.get(FieldType.PASSWORD.getName());
				
		String decryptedString = MerchantIPayUtil.decryptIrctc(encryptedString, key, iv); 
		
		isCheckSumMatching(decryptedString);
		
		mapResquestFields(decryptedString, fields);
		
		String newTxnId = TransactionManager.getNewTransactionId();
		fields.put(FieldType.TXN_ID.getName(), newTxnId);
		generalValidator.validate(fields);

		/*String txnMessage = fields.get(FieldType.PG_TXN_STATUS.getName());
		if (txnMessage.equalsIgnoreCase(Constants.SUCCESS)) {
			// Prepare request for wallet API call
			try{
			JSONObject request = walletApiRequest(fields);
			JSONObject response = transactionCommunicator.getResponse(request, fields);
			logger.info("Response received from RDS " + response);
				converter.toTransaction(response, fields);
			} catch (Exception exception){
				logger.error("Exception updating RDS request " + exception);
			}
		}	*/	
		atlTransformer.updateResponse(fields);
		
		fields.put(FieldType.INTERNAL_ORIG_TXN_TYPE.getName(), fields.get(FieldType.TXNTYPE.getName()));
		fields.remove(FieldType.ATL_RESPONSE_FIELD.getName());
		ProcessManager.flow(updateProcessor, fields, true);
		return fields.getFields();

	}
	
	private boolean isCheckSumMatching(String decryptRequest) throws SystemException {
		String receivedChecksum = decryptRequest.substring(decryptRequest.lastIndexOf("=") + 1);
		String checksumString = decryptRequest.substring(0, decryptRequest.lastIndexOf("|"));
		String checksum = Hasher.getHash(checksumString);
		return receivedChecksum.equalsIgnoreCase(checksum);
	}
	
	private void mapResquestFields(String decryptRequest, Fields fields) {
		String[] values = decryptRequest.split(Constants.ATL_RESPONSE_SEPARATOR);
		if (values.length == 8) {
			Map<String, String> receivedValues = new HashMap<>();
			for (String string : values) {
				String[] splitter = string.split(Constants.EQUATOR);
				receivedValues.put(splitter[0], splitter[1]);
			}
			
			fields.put(FieldType.ORDER_ID.getName(), receivedValues.get(Constants.RESERVATION_ID));
			fields.put(FieldType.AMOUNT.getName(), Amount.formatAmount(receivedValues.get(Constants.TXN_AMOUNT), fields.get(FieldType.CURRENCY_CODE.getName())));
			fields.put(FieldType.ACQ_ID.getName(), receivedValues.get(Constants.BANK_TXN_ID));
			fields.put(FieldType.PG_TXN_STATUS.getName(), receivedValues.get(Constants.STATUS));
			fields.put(FieldType.PG_TXN_MESSAGE.getName(), receivedValues.get(Constants.STATUS_DESC));
					
		} else {
			logger.error("Response message has more/less parameters than expected: " + decryptRequest);
		}
	}
	
	/*private JSONObject walletApiRequest(Fields fields){
		String amount =fields.get(FieldType.AMOUNT.getName());
		String currencyCode = fields.get(FieldType.CURRENCY_CODE.getName());
		amount = Amount.toDecimal(amount, currencyCode);
		String customerMobileNo = PropertiesManager.propertiesMap.get("RDSCustMobileNo");
		JSONObject request = new JSONObject();
		request.put(Constants.MOBILE_NO, customerMobileNo);
		request.put(Constants.ORDER_ID, fields.get(FieldType.ORDER_ID.getName()));
		request.put(Constants.AMOUNT, amount);
		request.put(Constants.TRANSACTION_TYPE, Constants.TRANSACTION_TYPE_VALUE);
		request.put(Constants.DESCRIPTION, Constants.DESCRIPTION_VALUE);
		
		return request;
		
	}*/

}
