package com.pay10.pg.action.service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.pay10.commons.api.Hasher;
import com.pay10.commons.exception.SystemException;
import com.pay10.commons.util.Amount;
import com.pay10.commons.util.Currency;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.PropertiesManager;
import com.pay10.pg.core.util.ConstantsIrctc;
import com.pay10.pg.core.util.MerchantIPayUtil;

@Service
public class ActionServiceImplIRCTC implements ActionServiceIRCTC {
	private static Logger logger = LoggerFactory.getLogger(ActionServiceImpl.class.getName());

	@Override
	public Fields prepareFieldsIRCTC(Map<String, String[]> map) throws SystemException {
		String decryptRequest = null;

		Fields fields = new Fields();
		
		for(Entry<String,String[]> entry:map.entrySet()) {
			logger.info("Parameter received from IRCTC etickting: key:" + entry.getKey() + " and value: " + Arrays.toString(entry.getValue()));
		}
		
		// get request string
		String request = map.get("encdata")[0];
		
		// String decryptRequest;
		try {
				decryptRequest = MerchantIPayUtil.decryptIrctc(request,
						PropertiesManager.propertiesMap.get(ConstantsIrctc.IRCTC_KEY),
						PropertiesManager.propertiesMap.get(ConstantsIrctc.IRCTC_IV));
				isCheckSumMatching(decryptRequest);
				mapFields(decryptRequest, fields);
			} catch (Exception exception) {
				
				logger.error("Exception", exception);
			}

		fields.removeInternalFields();
		fields.remove(FieldType.HASH.getName());
		fields.put(FieldType.HASH.getName(), Hasher.getHash(fields));

		return fields;
	}

	private boolean isCheckSumMatching(String decryptRequest) throws SystemException {
		String receivedChecksum = decryptRequest.substring(decryptRequest.lastIndexOf(Constants.EQUATOR) + 1);
		String checksumString = decryptRequest.substring(0, decryptRequest.lastIndexOf(Constants.IPAY_SEPARATOR));
		String checksum = Hasher.getHash(checksumString);
		return receivedChecksum.equalsIgnoreCase(checksum);
	}

	private void mapFields(String decryptRequest, Fields fields) {
		String[] values = decryptRequest.split(Constants.IPAY_RESPONSE_SEPARATOR);
		if (values.length == 10) {
			Map<String, String> receivedValues = new HashMap<>();
			for (String string : values) {
				String[] splitter = string.split(Constants.EQUATOR);
				String value = string.substring(string.indexOf(Constants.EQUATOR)+1,string.length());
				receivedValues.put(splitter[0], value);
			}
			String currenyCode = Currency.getNumericCode(receivedValues.get(Constants.CURRENCY_TYPE));

			fields.put(FieldType.PAY_ID.getName(), receivedValues.get(Constants.MERCHANT_CODE));
			fields.put(FieldType.AMOUNT.getName(),
					Amount.formatAmount(receivedValues.get(Constants.TXN_AMOUNT), currenyCode));
			fields.put(FieldType.ORDER_ID.getName(), receivedValues.get(Constants.RESERVATION_ID));
			fields.put(FieldType.CURRENCY_CODE.getName(), currenyCode);
			fields.put(FieldType.RETURN_URL.getName(), receivedValues.get(Constants.RESPONSE_URL));
		} else {
			logger.error("Response message has more/less parameters than expected: " + decryptRequest);
		}
	}
}
