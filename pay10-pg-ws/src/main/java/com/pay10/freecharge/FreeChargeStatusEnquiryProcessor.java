package com.pay10.freecharge;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import javax.net.ssl.HttpsURLConnection;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.exception.SystemException;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.PropertiesManager;
import com.pay10.commons.util.StatusType;
import com.pay10.pg.core.util.FreeChargeUtil;

@Service
public class FreeChargeStatusEnquiryProcessor {

	@Autowired
	@Qualifier("freeChargeTransactionConverter")
	private TransactionConverter converter;

	@Autowired
	private FreeChargeUtil freeChargeUtil;

	@Autowired
	@Qualifier("freeChargeFactory")
	private TransactionFactory transactionFactory;

	private FreeChargeTransformer freeChargeTransformer;

	private static Logger logger = LoggerFactory.getLogger(FreeChargeStatusEnquiryProcessor.class.getName());

	public static final String REQUEST_OPEN_TAG = "{";
	public static final String REQUEST_CLOSE_TAG = "}";

	// FREECHARGE SALE REQUEST PARAMETERS
	public static final String MERCHANT_ID = "merchantId";
	public static final String MERCHANT_TXN_ID = "merchantTxnId";
	public static final String CHECKSUM = "checksum";
	public static final String TXNTYPE = "txnType";

	public void enquiryProcessor(Fields fields) throws SystemException {
		String request = statusEnquiryRequest(fields);
		String response = "";
		try {
			response = getResponse(request);
		} catch (SystemException exception) {
			logger.error("Exception", exception);
		}

		updateFields(fields, response);

	}

	public String statusEnquiryRequest(Fields fields) throws SystemException {
		StringBuilder sb = new StringBuilder();
		JSONObject jsonObj = new JSONObject();
		Transaction transaction = new Transaction();

		transaction = transactionFactory.getInstance(fields);
		String key = transaction.getTxnKey();

		sb.append(REQUEST_OPEN_TAG);
		getElement(MERCHANT_ID, transaction.getMerchantId(), sb);
		getElement(MERCHANT_TXN_ID, fields.get(FieldType.PG_REF_NUM.getName()), sb);
		getElement(TXNTYPE, transaction.getTxnType(), sb);
		sb.deleteCharAt(sb.length() - 1);
		sb.append(REQUEST_CLOSE_TAG);

		String checksum = freeChargeUtil.generateChecksum(sb.toString(), fields.get(FieldType.PASSWORD.getName()));

		jsonObj.put(CHECKSUM, checksum);
		jsonObj.put(MERCHANT_ID, transaction.getMerchantId());
		jsonObj.put(MERCHANT_TXN_ID, fields.get(FieldType.PG_REF_NUM.getName()));
		jsonObj.put(TXNTYPE, transaction.getTxnType());
		logger.info("Status enquiry Request to Freecharge " + jsonObj.toString());
		return jsonObj.toString();

	}

	public void getElement(String name, String value, StringBuilder xml) {
		if (null == value) {
			return;
		}

		xml.append("\"");
		xml.append(name);
		xml.append("\"");
		xml.append(":");
		xml.append("\"");
		xml.append(value);
		xml.append("\"");
		xml.append(",");

	}

	public static String getResponse(String request) throws SystemException {

		String hostUrl = "";

		Fields fields = new Fields();
		fields.put(FieldType.TXNTYPE.getName(), "STATUS");
		try {
			hostUrl = PropertiesManager.propertiesMap.get(Constants.STATUS_ENQ_URL);
			URL url = new URL(hostUrl);
			logger.info("Request message to FreeCharge: TxnType = " + fields.get(FieldType.TXNTYPE.getName()) + " "
					+ "Txn id = " + fields.get(FieldType.TXN_ID.getName()) + " " + request);
			URLConnection connection = null;
			connection = (HttpsURLConnection) url.openConnection();
			connection.setRequestProperty("Content-Type", "application/json");
			connection.setDoInput(true);
			connection.setDoOutput(true);
			connection.setUseCaches(false);

			DataOutputStream dataoutputstream = new DataOutputStream(connection.getOutputStream());
			dataoutputstream.writeBytes(request);
			dataoutputstream.flush();
			dataoutputstream.close();
			BufferedReader bufferedreader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String decodedString;
			String serverResponse = "";
			while ((decodedString = bufferedreader.readLine()) != null) {
				serverResponse = serverResponse + decodedString;
			}
			logger.info("Status Enq Response received from Freecharge " + serverResponse.toString());
//			JSONObject response = new JSONObject(serverResponse.toString());
			return serverResponse;
		} catch (IOException ioException) {
			MDC.put(FieldType.INTERNAL_CUSTOM_MDC.getName(), fields.getCustomMDC());
			fields.put(FieldType.STATUS.getName(), StatusType.ERROR.getName());
			logger.error("Network Exception with FreeCharge", ioException);
			throw new SystemException(ErrorType.INTERNAL_SYSTEM_ERROR, ioException,
					"Network Exception with FreeCharge " + hostUrl.toString());
		}
	}

	public void updateFields(Fields fields, String jsonObject) {

		Transaction transactionResponse = new Transaction();
		transactionResponse = converter.statusToTransaction(jsonObject);
		freeChargeTransformer = new FreeChargeTransformer(transactionResponse);
		freeChargeTransformer.updateStatusResponse(fields);

	}// toTransaction()

}
