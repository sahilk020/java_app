package com.pay10.paymentEdge;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.exception.SystemException;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.PropertiesManager;
import com.pay10.commons.util.StatusType;
import com.pay10.commons.util.TransactionType;

@Service("payementEdgeTransactionCommunicator")
public class TransactionCommunicator {

	
//	@Autowired
//	private S2SService s2SService; 
	private static Logger logger = LoggerFactory.getLogger(TransactionCommunicator.class.getName());

	public void updateSaleResponse(Fields fields, String request) {

//		if(fields.get(FieldType.PAYMENT_TYPE.getName()).equalsIgnoreCase("UP")) {
//			try {
//				updateS2sCall(fields);
//			} catch (SystemException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}else {
		fields.put(FieldType.PAYMENTEDGE_FINAL_REQUEST.getName(), request.toString());
		fields.put(FieldType.STATUS.getName(), StatusType.SENT_TO_BANK.getName());
		fields.put(FieldType.RESPONSE_CODE.getName(), ErrorType.SUCCESS.getResponseCode());
		fields.put(FieldType.RESPONSE_MESSAGE.getName(), ErrorType.SUCCESS.getResponseMessage());
//		}
	}

	
	@SuppressWarnings("incomplete-switch")
	public String getResponse(String request, Fields fields) throws SystemException {

		String hostUrl = "";
		  
		try {

			TransactionType transactionType = TransactionType.getInstance(fields.get(FieldType.TXNTYPE.getName()));
			switch (transactionType) {
			case SALE:
			case AUTHORISE:
				break;
			case ENROLL:
				break;
			case CAPTURE:
				break;
			case REFUND:
				hostUrl = PropertiesManager.propertiesMap.get(Constants.PAYMENTEDGE_REFUND_URL);
				break;
			case STATUS:
				hostUrl = PropertiesManager.propertiesMap.get(Constants.PAYMENTEDGE_STATUS_ENQ_URL);
				break;
			}

			logger.info("Request message to PaymentEdge : TxnType = " + fields.get(FieldType.TXNTYPE.getName()) + " "
					+ "Order Id = " + fields.get(FieldType.ORDER_ID.getName()) + "  Request = " + request);

			URL url = new URL(hostUrl);
			
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Type", "application/json");
			connection.setDoOutput(true);

			DataOutputStream requestWriter = new DataOutputStream(connection.getOutputStream());
			requestWriter.writeBytes(request.toString());
			requestWriter.close();
			String responseData = "";
			InputStream is = connection.getInputStream();
			BufferedReader responseReader = new BufferedReader(new InputStreamReader(is));
			if ((responseData = responseReader.readLine()) != null) {
				logger.info("PaymentEdge Refund Response >>> "+responseData);
			}
			responseReader.close();
			
			return responseData;

		} catch (Exception e) {
			logger.error("Exception in PaymentEdge txn Communicator", e);
		}
		return null;

	}


	public static String getTextBetweenTags(String text, String tag1, String tag2) {

		int leftIndex = text.indexOf(tag1);
		if (leftIndex == -1) {
			return null;
		}

		int rightIndex = text.indexOf(tag2);
		if (rightIndex != -1) {
			leftIndex = leftIndex + tag1.length();
			return text.substring(leftIndex, rightIndex);
		}

		return null;
	}

}
