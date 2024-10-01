package com.pay10.pg.action;

import com.opensymphony.xwork2.Action;
import com.pay10.commons.api.TransactionControllerServiceProvider;
import com.pay10.commons.dao.FieldsDao;
import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.exception.SystemException;
import com.pay10.commons.user.Account;
import com.pay10.commons.user.AccountCurrency;
import com.pay10.commons.user.User;
import com.pay10.commons.user.UserDao;
import com.pay10.commons.util.*;
import com.pay10.pg.core.util.CosmosUtil;
import com.pay10.pg.core.util.UpiHistorian;
import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class CosmosResponseAction extends AbstractSecureAction implements ServletRequestAware {
	private static Logger logger = LoggerFactory.getLogger(CosmosResponseAction.class.getName());
	private static final long serialVersionUID = 2382298172065463916L;

	private HttpServletRequest httpRequest;

	@Autowired
	private TransactionControllerServiceProvider transactionControllerServiceProvider;

	@Autowired
	private UpiHistorian upiHistorian;

	@Autowired
	private UserDao userDao;
	
	@Autowired
	private FieldsDao fieldsDao;

	public CosmosResponseAction() {
	}

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.httpRequest = request;
	}

	@Override
	public String execute() {

		Fields responseField = null;
		try {
			String decryptionKey = PropertiesManager.propertiesMap.get("cosmosupidecryptionKey");

			// String decryptionKey = "";
			BufferedReader rd = httpRequest.getReader();
			String line;
			StringBuffer responseString = new StringBuffer();
			while ((line = rd.readLine()) != null) {
				responseString.append(line);
				// responseString.append('');
			}
			rd.close();
			if (responseString.toString().isEmpty()) {
				logger.info("Buffered Reader cosmos upi response is empty ");
			} else {
				logger.info("Buffered Reader cosmos upi response contains data ");
				logger.info(" response >>>  " + responseString.toString());
			}
			String encData = responseString.toString();
			logger.info("Response from COSMOS UPI at callback : {} ", encData);
			// decrypt encData then convert into json

			// decryptedResponse = CosmosUtil.decrypt(encData, decryptionKey);

			JSONObject encDatajson = new JSONObject(encData);

			String data = null;
			if (encDatajson != null && encDatajson.getString("message") != null) {

				data = encDatajson.getString("message");
			}
			Fields fields = new Fields();

			String decryptedResponse = "";
			Map<String, String> res = null;

			if (data != null && StringUtils.isNotBlank(decryptionKey)) {
				decryptedResponse = CosmosUtil.decryptCallBackResponse(data, decryptionKey);
				/*
				 * { "channel": "api", "data": [ { "terminalId": "BHARTIPAY001-001", "respCode":
				 * "U30", "txnTime": "Thu Nov 10 18:33:23 IST 2022", "custRefNo":
				 * "231418320027", "upiTxnId": "COB96B730B2B5CA432CAB7F7BB784BD18CC", "amount":
				 * "102.00", "upiId": "7838304007@cosb", "respMessge": "FAIL", "customerName":
				 * " vijay", "mcc": "0000", "requestTime": "2022-11-10 18:32:25.0" } ],
				 * "checksum": "", "extTransactionId": "BHARTIPAY1089121110183220", "status":
				 * "SUCCESS", "merchant": [ "bhar" ], "source": "BHARTIPAY001" }
				 */
				logger.info("CosmosResponseAction COSMOS UPI decryptedResponse:{}" + decryptedResponse);
				JSONObject responseObj = new JSONObject(decryptedResponse);
				
				String extTransactionId = responseObj.getString("extTransactionId");
		
				fields.put(FieldType.ACQUIRER_TYPE.getName(), AcquirerType.COSMOS.getCode());
				fields.put(FieldType.PG_REF_NUM.getName(), extTransactionId.substring(9));
				fields.put(FieldType.TXNTYPE.getName(), TransactionType.SALE.getName());
				logger.info("Before findPrevious Fields :" + fields.getFieldsAsString());
				// mongodb database refres
				upiHistorian.findPrevious(fields);
				logger.info("After findPrevious Fields :" + fields.getFieldsAsString());
				
				// for upi intent cosmos
				String internalRequestFields=fields.get(FieldType.INTERNAL_REQUEST_FIELDS.getName());
				String[] paramaters = internalRequestFields.split("~");
				Map<String, String> paramMap = new HashMap<String, String>();
				for (String param : paramaters) {
					String[] parameterPair = param.split("=");
					if (parameterPair.length > 1) {
						paramMap.put(parameterPair[0].trim(), parameterPair[1].trim());
					}
				}
				boolean dualresponse=fieldsDao.findTransactionByPgRefFinalStatus(fields.get(FieldType.PG_REF_NUM.getName()));
				logger.info("cosmos validtion response for cosmos  "+dualresponse);
				if(dualresponse){
				 if ((StringUtils.isNotEmpty(paramMap.get(FieldType.UPI_INTENT.getName())) && paramMap.get(FieldType.UPI_INTENT.getName()).equalsIgnoreCase("1"))||fields.get(FieldType.PAYMENT_TYPE.getName()).equals(PaymentType.QRCODE.getCode())){
					 fields.put(FieldType.COSMOS_UPI_RESPONSE_FIELD.getName(),decryptedResponse);
					 res = transactionControllerServiceProvider.transact(fields,
								Constants.TXN_WS_COSMOSUPI_PROCESSOR_INTENT.getValue());
					
				 }else {
				String channel = responseObj.getString("channel");
				JSONArray dataArray = responseObj.getJSONArray("data");
				JSONObject dataobj = dataArray.getJSONObject(0);
				String terminalId = dataobj.getString("terminalId");
				String respCode = dataobj.getString("respCode");
				String txnTime = dataobj.getString("txnTime");
				String custRefNo = dataobj.getString("custRefNo");
				String upiTxnId = dataobj.getString("upiTxnId");
				String amount = dataobj.getString("amount");
				String upiId = dataobj.getString("upiId");

				// JSONArray merchantJSon = responseObj.getJSONArray("merchant");
				// JSONObject merchant = merchantJSon.getJSONObject(1);
				String respMessge = dataobj.getString("respMessge");
				String customerName = dataobj.getString("customerName");
				String mcc = dataobj.getString("mcc");
				String requestTime = dataobj.getString("requestTime");

				String checksum = responseObj.getString("checksum");// checksum from COSMOS
				String status = responseObj.getString("status");
				String source = responseObj.getString("source");
				String concatenatedString = channel + terminalId + respCode + txnTime + custRefNo + upiTxnId + amount
						+ upiId + respMessge + customerName + mcc + requestTime + status + source ;
				// String checksumKey = "";

				

				String checksumKey = fields.get(FieldType.ADF4.getName());

				String checksumKe = CosmosUtil.generateChecksum(concatenatedString, checksumKey);
				fields.put(FieldType.RRN.getName(), custRefNo);

				fields.put(FieldType.MERCHANT_ID.getName(), source);
				// fields.put(FieldType.ADF2.getName(), terminalId);
				// fields.put(FieldType.TERMINAL_ID.getName(), terminalId);

				
				if (StringUtils.isNotBlank(fields.get(FieldType.PAY_ID.getName()))) {
					logger.info("COSMOS: Fetch ADF2 & ADF4 From database basis on PAY_ID={}",fields.get(FieldType.PAY_ID.getName()));

					Map<String, String> keyMap = getTxnKey(fields);
					fields.put(FieldType.ADF1.getName(), keyMap.get("ADF1"));
					fields.put(FieldType.ADF2.getName(), keyMap.get("ADF2"));
					fields.put(FieldType.ADF4.getName(), keyMap.get("ADF4"));
					fields.put(FieldType.TXN_KEY.getName(), keyMap.get("txnKey"));

				}
				
				fields.put(FieldType.RESPONSE_CODE.getName(), respCode);
				fields.put(FieldType.RESPONSE_MESSAGE.getName(), respMessge);
				fields.put(FieldType.TERMINAL_ID.getName(), terminalId);
				//fields.put(FieldType.AMOUNT.getName(), amount);
				fields.put(FieldType.ACQ_ID.getName(), upiTxnId);
				// fields.put(FieldType.STATUS.getName(), status);
				fields.put(FieldType.STATUS.getName(), status);
			fields.put(FieldType.CARD_HOLDER_NAME.getName(), customerName);
				fields.put("COSMOS_RESP_CHECKSUM", checksum);
				fields.put("COSMOS_RESP_CAL_CHECKSUM", checksumKe);
				logger.info("Cosmos Upi fields prepapred and sent to upi processsor " + fields.getFieldsAsString());

				 res = transactionControllerServiceProvider.transact(fields,
						Constants.TXN_WS_COSMOSUPI_PROCESSOR.getValue());
			}
				res.remove(FieldType.ORIG_TXN_ID.getName());
				responseField = new Fields(res);
				res.remove(FieldType.ORIG_TXN_ID.getName());
			}

			
			else {
				logger.info("dual response in callback url  for cosmos"+fields.getFieldsAsString());

			}}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return Action.NONE;
	}

	public Map<String, String> getTxnKey(Fields fields) throws SystemException {

		Map<String, String> keyMap = new HashMap<String, String>();

		User user = userDao.findPayId(fields.get(FieldType.PAY_ID.getName()));
		Account account = null;
		Set<Account> accounts = user.getAccounts();

		if (accounts == null || accounts.size() == 0) {
			logger.info("No account found for Pay ID = " + fields.get(FieldType.PAY_ID.getName()) + " and ORDER ID = "
					+ fields.get(FieldType.ORDER_ID.getName()));
		} else {
			for (Account accountThis : accounts) {
				if (accountThis.getAcquirerName()
						.equalsIgnoreCase(AcquirerType.getInstancefromCode(AcquirerType.COSMOS.getCode()).getName())) {
					account = accountThis;
					break;
				}
			}
		}

		AccountCurrency accountCurrency = account.getAccountCurrency(fields.get(FieldType.CURRENCY_CODE.getName()));

		keyMap.put("ADF1", accountCurrency.getAdf1());
		keyMap.put("ADF2", accountCurrency.getAdf2());
		keyMap.put("ADF4", accountCurrency.getAdf4());
		keyMap.put("txnKey", accountCurrency.getTxnKey());

		return keyMap;

	}

}
