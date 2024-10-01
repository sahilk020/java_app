package com.pay10.icici.upi;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.pay10.commons.exception.SystemException;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.PropertiesManager;
import com.pay10.pg.core.util.ICICIUpiUtil;


@Service
public class ICICIUpiStatusEnquiryProcessor {

	private static Logger logger = LoggerFactory.getLogger(ICICIUpiStatusEnquiryProcessor.class.getName());

	@Autowired
	@Qualifier("iciciUpiUtil")
	private ICICIUpiUtil iciciUpiUtil;

	@Autowired
	@Qualifier("iciciUpiTransactionConverter")
	private TransactionConverter converter;

	@Autowired
	@Qualifier("iciciUpiTransactionCommunicator")
	private TransactionCommunicator communicator;

	@Autowired
	@Qualifier("iciciUpiTransformer")
	private ICICIUpiTransformer iciciUpiTransformer;

	public void enquiryProcessor(Fields fields) {
		String request = statusEnquiryRequest(fields);
		String response = null;
		try {
			response = communicator.getResponse(request, fields);
		} catch (SystemException exception) {
			logger.error("Exception", exception);
		}

		updateFields(fields, response);

	}

	public String statusEnquiryRequest(Fields fields) {

		String key = PropertiesManager.propertiesMap.get("ICICIUPI_PUBLIC_KEY_PATH");
		String merchantId = fields.get(FieldType.MERCHANT_ID.getName());
		String mccCode = fields.get(FieldType.ADF4.getName());
		String payerAddress = fields.get(FieldType.PAYER_ADDRESS.getName());
		JSONObject json = new JSONObject();
		json.put(Constants.MERCHANT_ID, merchantId);
		json.put(Constants.MERCHANT_SUB_ID, fields.get(FieldType.ADF2.getName()));
		json.put(Constants.TERMINAL_ID, mccCode);
		json.put(Constants.MERCHANT_TRAN_ID, fields.get(FieldType.PG_REF_NUM.getName()));
		logger.info("icici upi Final Status check request----"+json.toString());
		String encryptedString = null;
		try {
			encryptedString = iciciUpiUtil.getEncrypted(json.toString(), key);
		} catch (Exception e) {
			logger.error("Exception : " + e.getMessage());
		}
		logger.info("icici upi Final Status check request- encryptedString---"+encryptedString);
		return encryptedString;
	}

	public void updateFields(Fields fields, String response) {

		Transaction transactionResponse = new Transaction();
		transactionResponse = converter.toTransactionStatusEnquiry(response, fields);
		iciciUpiTransformer = new ICICIUpiTransformer(transactionResponse);
		//iciciUpiTransformer.updateResponse(fields, transactionResponse);
	}

}
