package com.pay10.hdfc.upi;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.pay10.commons.exception.SystemException;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.TransactionType;
import com.pay10.pg.core.util.HdfcUpiUtil;

/**
 * @author Rahul
 *
 */
@Service
public class HdfcUpiIntegrator {

	private static Logger logger = LoggerFactory.getLogger(HdfcUpiIntegrator.class.getName());

	@Autowired
	@Qualifier("hdfcUpiTransactionConverter")
	private TransactionConverter converter;

	@Autowired
	@Qualifier("hdfcUpiTransactionCommunicator")
	private TransactionCommunicator communicator;

	@Autowired
	@Qualifier("hdfcUpiTransformer")
	private HdfcUpiTransformer upiTransformer;

	@Autowired
	@Qualifier("hdfcUpiFactory")
	private TransactionFactory TransactionFactory;

	@Autowired
	private HdfcUpiUtil hdfcUpiUtil;

	private String decryptedString;

	public void process(Fields fields) throws SystemException {
		Transaction transactionRequest = TransactionFactory.getInstance(fields);

		if (fields.get(FieldType.TXNTYPE.getName()).equalsIgnoreCase(TransactionType.ENROLL.getName())) {
			fields.put(FieldType.TXNTYPE.getName(), TransactionType.SALE.getName());
		}
		String txnType = fields.get(FieldType.TXNTYPE.getName());
		if (txnType.equals(TransactionType.SALE.getName())) {
			String response = vpaAddressValidator(fields);
			// compare the request
			//response="VE";
			if (response.equals(Constants.VPA_SUCCESSFULLY_STATUS_CODE)) {

				send(fields);
			} else {
				communicator.updateInvalidVpaResponse(fields, response);
			}
		} else {
			send(fields);
		}

	}// process

	public String vpaAddressValidator(Fields fields) throws SystemException {

		JSONObject request = converter.vpaValidatorRequest(fields);
		logger.info("VPA Validation request" + request);

		String encryptedResponse = communicator.getVPAResponse(request, fields);
		//String key = PropertiesManager.propertiesMap.get("HdfcUpiMerchantKey");
		String key = fields.get(FieldType.ADF1.getName());
		try {
			decryptedString = hdfcUpiUtil.decrypt(encryptedResponse, key);

		} catch (Exception e) {
			logger.error("Exception", e);
		}
		logger.info("HDFC VPA Validation API response " + decryptedString);

		String[] value_split = decryptedString.split("\\|");
		String vpaStatus = value_split[3];
		return vpaStatus;

	}

	public void send(Fields fields) throws SystemException {

		Transaction transactionResponse = new Transaction();

		JSONObject request = converter.perpareRequest(fields);
		logger.info("HDFC UPI Collect API request" + request);

		String encryptedResponse = communicator.getResponse(request, fields);

		String txnType = fields.get(FieldType.TXNTYPE.getName());

		if (txnType.equals(TransactionType.ENQUIRY.getName())) {
			transactionResponse = converter.toTransactionStatusEnquiry(encryptedResponse, fields);
			logger.info("HDFC UPI Status enquiry response " + transactionResponse);
		} else {
			transactionResponse = converter.toTransaction(encryptedResponse, fields);
		}
		logger.info("HDFC UPI Collect API response " + transactionResponse.getStatus());

		if (!txnType.equals(TransactionType.SALE.getName())) {
			upiTransformer = new HdfcUpiTransformer(transactionResponse);
			upiTransformer.updateResponse(fields, transactionResponse);

		} else {
			communicator.updateSaleResponse(fields, transactionResponse);
		}
	}

}
