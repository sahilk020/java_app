package com.pay10.pg.cris;

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
import com.pay10.commons.util.PropertiesManager;
import com.pay10.commons.util.TransactionType;
import com.pay10.pg.core.security.TransactionConverterIrctc;
import com.pay10.pg.core.util.ConstantsIrctc;
import com.pay10.pg.core.util.MerchantIPayUtil;
import com.pay10.requestrouter.RequestRouter;

/**
 * @author Puneet
 * Double verification service for IRCTC/CRIS
 */
@Service("crisDoubleVerificationService")
public class DoubleVerificationService {
	private static Logger logger = LoggerFactory.getLogger(DoubleVerificationService.class.getName());
	
	@Autowired
	@Qualifier("irctcTransactionConverter")
	private TransactionConverterIrctc converter;
	
	@Autowired
	private RequestRouter router;

	public String verifyTransaction(String request) {
		logger.info("Double verification encdata: " + request);
		Fields fields = new Fields();
		try {
			String key = PropertiesManager.propertiesMap.get(ConstantsIrctc.IRCTC_KEY);
			String iv = PropertiesManager.propertiesMap.get(ConstantsIrctc.IRCTC_IV);
			// decrypt
			String decryptedRequest = MerchantIPayUtil.decryptIrctc(request, key, iv);
			String[] values = decryptedRequest.split(Constants.IPAY_RESPONSE_SEPARATOR);
			Map<String, String> receivedValues = new HashMap<>();
			for (String string : values) {
				String[] splitter = string.split(Constants.EQUATOR);
				receivedValues.put(splitter[0], splitter[1]);
				
			}
			logger.info("Double verification Request Order id : " + receivedValues.get(Constants.RESERVATION_ID) + "PG_REF_NUM : " + receivedValues.get(Constants.BANK_TXN_ID) + "Decrypted request : " + decryptedRequest);
			// checksum  TODO..handle invalid checksum
			isCheckSumMatching(decryptedRequest);
			// map fields
			mapResquestFields(decryptedRequest, fields);
			
			String requestTxnId = fields.get(FieldType.PG_REF_NUM.getName());
			// send fields to custom router
			fields.putAll(routeFields(fields));
			
			//compare with request txn id
			compareTxnId(requestTxnId, fields);
			//map response fields
			StringBuilder response = converter.mapDoubleVerificationFields(fields);
			String[] resvalues = response.toString().split(Constants.IPAY_RESPONSE_SEPARATOR);
			Map<String, String> responseValues = new HashMap<>();
			for (String string : resvalues) {
				String[] splitter = string.split(Constants.EQUATOR);
				responseValues.put(splitter[0], splitter[1]);
				
			}
			logger.info("Double verification Response Order id : " + responseValues.get(Constants.RESERVATION_ID) + "PG_REF_NUM : " + responseValues.get(Constants.BANK_TXN_ID) + "Decrypted response : " + responseValues);
			response = converter.mapChecksum(response);
			logger.info("Double verifiction decrypted response: " + response);
			// encrypt and send
			return MerchantIPayUtil.encryptIRCTC(response.toString(), key, iv).toUpperCase();
		} catch (Exception exception) {
			logger.error("Error processing IRCTC double verification request: " + exception);
			return null;
		}
	}
	
	public Map<String,String> routeFields(Fields fields) throws SystemException {
		fields.logAllFields("Raw Request:");
		fields.removeInternalFields();
		fields.clean();
		fields.removeExtraFields();
		// To put request blob
		String fieldsAsString = fields.getFieldsAsBlobString();
		fields.put(FieldType.INTERNAL_REQUEST_FIELDS.getName(), fieldsAsString);
		fields.logAllFields("Refine Request:");
		fields.put(FieldType.TXNTYPE.getName(), TransactionType.STATUS.getName());
		fields.put(FieldType.HASH.getName(), Hasher.getHash(fields));
		Map<String, String> responseMap = router.route(fields);
		responseMap.remove(FieldType.INTERNAL_CUSTOM_MDC.getName());
		return responseMap;
	}

	private void mapResquestFields(String decryptRequest, Fields fields) {
		String[] values = decryptRequest.split(Constants.IPAY_RESPONSE_SEPARATOR);
		if (values.length == 5) {
			Map<String, String> receivedValues = new HashMap<>();
			for (String string : values) {
				String[] splitter = string.split(Constants.EQUATOR);
				receivedValues.put(splitter[0], splitter[1]);
			}
			fields.put(FieldType.PAY_ID.getName(), receivedValues.get(Constants.MERCHANT_CODE));
			fields.put(FieldType.ORDER_ID.getName(), receivedValues.get(Constants.RESERVATION_ID));
			fields.put(FieldType.AMOUNT.getName(), Amount.formatAmount(receivedValues.get(Constants.TXN_AMOUNT), Constants.CURRENCY_CODE));
			fields.put(FieldType.PG_REF_NUM.getName(), receivedValues.get(Constants.BANK_TXN_ID));
			fields.put(FieldType.CURRENCY_CODE.getName(), Constants.CURRENCY_CODE);
			
		} else {
			logger.error("Response message has more/less parameters than expected: " + decryptRequest);
		}
	}
	
	private boolean isCheckSumMatching(String decryptRequest) throws SystemException {
		String receivedChecksum = decryptRequest.substring(decryptRequest.lastIndexOf("=") + 1);
		String checksumString = decryptRequest.substring(0, decryptRequest.lastIndexOf("|"));
		String checksum = Hasher.getHash(checksumString);
		return receivedChecksum.equalsIgnoreCase(checksum);
	}
	
	public void compareTxnId( String bankId, Fields fields){
		String txnId = fields.get(FieldType.PG_REF_NUM.getName());
		if(!(txnId.equals(bankId))){
			fields.put(FieldType.STATUS.getName(), "FAILED");
			fields.put(FieldType.PG_REF_NUM.getName(), bankId);
		}
		
	}
}
