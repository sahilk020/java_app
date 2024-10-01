
package com.pay10.pg.action;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.net.ssl.HttpsURLConnection;
import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.interceptor.ServletRequestAware;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.opensymphony.xwork2.Action;
import com.pay10.commons.api.TransactionControllerServiceProvider;
import com.pay10.commons.dao.FieldsDao;
import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.exception.SystemException;
import com.pay10.commons.user.AccountCurrencyDao;
import com.pay10.commons.util.AcquirerType;
import com.pay10.commons.util.Amount;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.PropertiesManager;
import com.pay10.commons.util.StatusType;
import com.pay10.commons.util.TransactionType;

import com.pay10.pg.core.util.HdfcUpiUtil;
import com.pay10.pg.core.util.UpiHistorian;

public class HdfcUpiResponseAction extends AbstractSecureAction implements ServletRequestAware {

	private static Logger logger = LoggerFactory.getLogger(HdfcUpiResponseAction.class.getName());

	private static final long serialVersionUID = 2382298172065463916L;

	private HttpServletRequest httpRequest;

	@Autowired
	TransactionControllerServiceProvider transactionControllerServiceProvider;

	@Autowired
	AccountCurrencyDao accountCurrencyDao;
	@Autowired
	private HdfcUpiUtil hdfcUpiUtil;

	@Autowired
	private UpiHistorian upiHistorian;

	@Autowired
	private FieldsDao fieldsDao;
	
	
	
	@Override
	public void setServletRequest(HttpServletRequest request) {
		httpRequest = request;
	}

	@Override
	@SuppressWarnings("unchecked")
	public String execute() {

		Fields responseField = null;
		try {
			Map<String, String[]> fieldMapObj = httpRequest.getParameterMap();
			Map<String, String> responseMap = new HashMap<String, String>();

			for (Entry<String, String[]> entry : fieldMapObj.entrySet()) {
				try {
					responseMap.put(entry.getKey().trim(), entry.getValue()[0].trim());
				} catch (ClassCastException classCastException) {
					logger.error("Exception", classCastException);
				}
			}

			Fields fields = new Fields();
			Object fieldsObj = sessionMap.get("FIELDS");

			if (null != fieldsObj) {
				fields.put((Fields) fieldsObj);
			}
			PropertiesManager propertiesManager = new PropertiesManager();
			String encrypted = responseMap.get("meRes");
			String pgMerchantId = responseMap.get("pgMerchantId");
String key=accountCurrencyDao.getHdfcKeyByMid(pgMerchantId);
			String decryptedString = "";
			try {
				decryptedString = hdfcUpiUtil.decrypt(encrypted, key);

			} catch (Exception e) { // TODO

				e.printStackTrace();
			}

			String[] value_split = decryptedString.split("\\|");
			logger.info("HDFC UPI APPROVED COLLECT RESPONSE  " + decryptedString);

			String receivedResponseCode = value_split[4];

		
			
			String responseCode = value_split[6];
			String responseMsg = value_split[4];
			String txnMsg = value_split[5];
			String payeeApprovalNum = value_split[0];
			String ReferenceId = value_split[10];
			String dateTime = value_split[3];
			String customerReference = value_split[9];
			String txnId = value_split[1];
			String address = value_split[18];
			String vpa = value_split[8];

			String[] merchantDetails = address.split("\\!");
			String merchantVPA = merchantDetails[0];

			logger.info("Merchant VPA " + merchantVPA);
			 Thread.sleep(2000); //for uat only it will remove in prodution  by abhi
			fields.put(FieldType.ACQUIRER_TYPE.getName(), AcquirerType.HDFC.getCode());
			fields.put(FieldType.PG_REF_NUM.getName(), txnId);
			fields.put(FieldType.TXNTYPE.getName(), TransactionType.SALE.getName());
			logger.info("fields before historian " + fields.getFieldsAsString());
			fields.put(FieldType.MERCHANT_ID.getName(), pgMerchantId);

			upiHistorian.findPrevious(fields);
			boolean dualVerification = enquiryProcessor(fields,key);

			logger.info("After historian " + fields.getFieldsAsString());
			logger.info("dual verfucation  " + dualVerification);

			if(dualVerification) {
				fields.put(FieldType.HDFC_DUAL_VERFICATION.getName(), "true");

				}else {
					fields.put(FieldType.HDFC_DUAL_VERFICATION.getName(), "False");

				}
				fields.put(FieldType.HDFC_ACQUIRER_CODE.getName(), receivedResponseCode);


			
			fields.put(FieldType.UDF1.getName(), merchantVPA);
			fields.put(FieldType.CARD_MASK.getName(), vpa);

			fields.put(FieldType.ACQ_ID.getName(), payeeApprovalNum);
			fields.put(FieldType.RRN.getName(), customerReference);
			fields.put(FieldType.PG_DATE_TIME.getName(), dateTime);
			fields.put(FieldType.AUTH_CODE.getName(), ReferenceId);
			fields.put(FieldType.INTERNAL_VALIDATE_HASH_YN.getName(), "N");

			logger.info("fields send to transact " + fields.getFieldsAsString());

			Map<String, String> res = transactionControllerServiceProvider.transact(fields,
					Constants.TXN_WS_UPI_PROCESSOR.getValue());
			responseField = new Fields(res);

			logger.info("Response received from WS " + responseField);

			return Action.NONE;

		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Error in hdfc bank UPI callback = " + e);
		}

		return Action.NONE;
	}

	@SuppressWarnings("unlikely-arg-type")
	public String getStatusType(String receivedResponse) {
		String status = null;
		if (receivedResponse.equals(Constants.HDFC_UPI_SUCCESS_CODE.getValue())) {
			status = StatusType.CAPTURED.getName();
		} else if (receivedResponse.equals(Constants.HDFC_UPI_ERROR_RESPONSE_CODE.getValue())) {
			status = StatusType.FAILED.getName();
		} else {
			status = StatusType.REJECTED.getName();
		}

		return status;
	}

	@SuppressWarnings("unlikely-arg-type")
	public ErrorType getErrorType(String receivedResponse) {
		ErrorType error = null;

		if (receivedResponse.equals(Constants.HDFC_UPI_SUCCESS_CODE.getValue())) {
			error = ErrorType.SUCCESS;
		} else if (receivedResponse.equals(Constants.HDFC_UPI_ERROR_RESPONSE_CODE.getValue())) {
			error = ErrorType.INVALID_REQUEST_FIELD;
		} else if (receivedResponse.equals(Constants.HDFC_UPI_INVALID_REQUEST_FIELD_CODE.getValue())) {
			error = ErrorType.CANCELLED;
		} else if (receivedResponse.equals(Constants.HDFC_UPI_CANCELLED_CODE.getValue())) {
			error = ErrorType.INTERNAL_SYSTEM_ERROR;
		} else if (receivedResponse.equals(Constants.HDFC_UPI_INTERNAL_SYSTEM_ERROR_CODE.getValue())) {
			error = ErrorType.CANCELLED;
		} else {
			error = ErrorType.DECLINED;
		}

		return error;
	}

	public boolean enquiryProcessor(Fields fields,String key) {
		JSONObject request = statusEnquiryRequest(fields,key);
		String response = null;
		try {
			response = getResponse(request, fields);
		


				String	decryptedString = hdfcUpiUtil.decrypt(response, key);
			logger.info("decryptedString : " + decryptedString);
			String[] value_split = decryptedString.split("\\|");
			logger.info("HDFC UPI dual verfication RESPONSE  " + decryptedString);
			String fieldsAmount = Amount.toDecimal(fields.get(FieldType.TOTAL_AMOUNT.getName()),
					fields.get(FieldType.CURRENCY_CODE.getName()));
			String receivedResponseCode = value_split[6];
			String receivedResponsemessage = value_split[4];
			String amount =value_split[2];
		
			if (receivedResponseCode.equalsIgnoreCase("00")&& receivedResponsemessage.equalsIgnoreCase("SUCCESS")
					&& fieldsAmount.equalsIgnoreCase(amount)){

				return true;
			}

			

		} catch (Exception e) {
			logger.error("Exception : " + e.getMessage());
		}
		return false;
		

	}
	public JSONObject statusEnquiryRequest(Fields fields,String Key) {
		Fields fields1 = null;
		String merchantId = fields.get(FieldType.MERCHANT_ID.getName());
		String pgRefNum = fields.get(FieldType.PG_REF_NUM.getName());
		

		StringBuilder request = new StringBuilder();
		request.append(merchantId);
		request.append("|");
		request.append(pgRefNum);
		request.append("|");
		request.append("0");
		request.append("||||||||||NA|NA");
logger.info("request for dual verfication :"+request);
		String encryptedString = null;
		try {
			logger.info("request for dual verfication :"+request+Key);

			encryptedString = hdfcUpiUtil.encrypt(request.toString(), Key);
		} catch (Exception exception) {
			logger.error("Exception", exception);
		}

		JSONObject json = new JSONObject();
		json.put("requestMsg", encryptedString);
		json.put("pgMerchantId", merchantId);
		JSONObject requestjson = json;
		return requestjson;

	}
	
	@SuppressWarnings("incomplete-switch")
	public String getResponse(JSONObject request, Fields fields) throws SystemException {
		StringBuilder serverResponse = new StringBuilder();
		String hostUrl = "";

		
			hostUrl = PropertiesManager.propertiesMap.get("HdfcUpiStatusEnqUrl");

	

		 logger.info("Request sent to bank " + request+"url "+hostUrl);
		HttpsURLConnection connection = null;
		HttpURLConnection simulatorConn = null;
		if (hostUrl.contains("https")) {
			try {

				// Create connection

				URL url = new URL(hostUrl);
				connection = (HttpsURLConnection) url.openConnection();

				connection.setRequestMethod("POST");
				connection.setRequestProperty("Content-Type", "application/json");
				connection.setRequestProperty("Content-Length", request.toString());
				connection.setRequestProperty("Content-Language", "en-US");

				connection.setUseCaches(false);
				connection.setDoOutput(true);
				connection.setDoInput(true);

				// Send request
				OutputStream outputStream = connection.getOutputStream();
				DataOutputStream wr = new DataOutputStream(outputStream);
				wr.writeBytes(request.toString());
				wr.close();

				// Get Response
				InputStream is = connection.getInputStream();
				BufferedReader rd = new BufferedReader(new InputStreamReader(is));
				String line;
				int code = ((HttpURLConnection) connection).getResponseCode();
				int firstDigitOfCode = Integer.parseInt(Integer.toString(code).substring(0, 1));
				if(firstDigitOfCode == 4 || firstDigitOfCode == 5){
					 fields.put(FieldType.STATUS.getName(),StatusType.ACQUIRER_DOWN.getName());
					 logger.error("Response code of txn :" + code);
					 throw new SystemException(ErrorType.ACUIRER_DOWN,
								 "Network Exception with Hdfc Upi "
										+ hostUrl.toString());
					}
				

				while ((line = rd.readLine()) != null) {
					serverResponse.append(line);

				}
				rd.close();

			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (connection != null) {
					connection.disconnect();
				}
			}
		} else {
			try {
				URL url = new URL(hostUrl);
				simulatorConn = (HttpURLConnection) url.openConnection();

				simulatorConn.setRequestMethod("POST");
				simulatorConn.setRequestProperty("Content-Type", "application/json");
				simulatorConn.setRequestProperty("Content-Length", request.toString());
				simulatorConn.setRequestProperty("Content-Language", "en-US");

				simulatorConn.setUseCaches(false);
				simulatorConn.setDoOutput(true);
				simulatorConn.setDoInput(true);

				// Send request
				OutputStream outputStream = simulatorConn.getOutputStream();
				DataOutputStream wr = new DataOutputStream(outputStream);
				wr.writeBytes(request.toString());
				wr.close();

				// Get Response
				InputStream is = simulatorConn.getInputStream();
				BufferedReader rd = new BufferedReader(new InputStreamReader(is));
				String line;

				while ((line = rd.readLine()) != null) {
					serverResponse.append(line);

				}
				rd.close();

			} catch (Exception exception) {
				logger.error("Exception", exception);
			} finally {
				if (simulatorConn != null) {
					simulatorConn.disconnect();
				}

			}
		}

		// JSONObject response = new JSONObject(serverResponse.toString());
		return serverResponse.toString();
	}
	
	
}
