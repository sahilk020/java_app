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
import org.springframework.stereotype.Service;

import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.exception.SystemException;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.PropertiesManager;
import com.pay10.commons.util.StatusType;
import com.pay10.commons.util.TransactionType;

@Service("freeChargeTransactionCommunicator")
public class TransactionCommunicator {

	private static Logger logger = LoggerFactory.getLogger(TransactionCommunicator.class.getName());

	public void updateSaleResponse(Fields fields, String request) {
		StringBuilder req = new StringBuilder();
		req.append(request);

		fields.put(FieldType.FREECHARGE_FINAL_REQUEST.getName(), req.toString());
		fields.put(FieldType.STATUS.getName(), StatusType.SENT_TO_BANK.getName());
		fields.put(FieldType.RESPONSE_CODE.getName(), ErrorType.SUCCESS.getResponseCode());
		fields.put(FieldType.RESPONSE_MESSAGE.getName(), ErrorType.SUCCESS.getResponseMessage());

	}

	@SuppressWarnings("incomplete-switch")
	public String getResponse(String request, Fields fields) throws SystemException {

		String hostUrl = "";

		try {

			TransactionType transactionType = TransactionType.getInstance(fields.get(FieldType.TXNTYPE.getName()));
			switch (transactionType) {
			case SALE:
			case AUTHORISE:
				hostUrl = PropertiesManager.propertiesMap.get(Constants.SALE_REQUEST_URL);
				break;
			case ENROLL:
				break;
			case CAPTURE:
				break;
			case REFUND:
				hostUrl = PropertiesManager.propertiesMap.get(Constants.REFUND_URL);
				break;
			case STATUS:
				hostUrl = PropertiesManager.propertiesMap.get(Constants.STATUS_ENQ_URL);
				break;
			}

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
			dataoutputstream.writeBytes(request.toString());
			dataoutputstream.flush();
			dataoutputstream.close();
			BufferedReader bufferedreader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String decodedString;
			String serverResponse = "";
			while ((decodedString = bufferedreader.readLine()) != null) {
				serverResponse = serverResponse + decodedString;
			}
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
}
