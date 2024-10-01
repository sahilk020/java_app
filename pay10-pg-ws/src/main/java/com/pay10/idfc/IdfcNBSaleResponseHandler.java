package com.pay10.idfc;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.google.gson.JsonObject;
import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.exception.SystemException;
import com.pay10.commons.user.Account;
import com.pay10.commons.user.AccountCurrency;
import com.pay10.commons.user.User;
import com.pay10.commons.user.UserDao;
import com.pay10.commons.util.AcquirerType;
import com.pay10.commons.util.Amount;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.PropertiesManager;
import com.pay10.commons.util.StatusType;
import com.pay10.commons.util.TransactionManager;
import com.pay10.commons.util.Validator;
import com.pay10.pg.core.util.ProcessManager;
import com.pay10.pg.core.util.Processor;

@Service
public class IdfcNBSaleResponseHandler {

	private static Logger logger = LoggerFactory.getLogger(IdfcNBSaleResponseHandler.class.getName());

	@Autowired
	private Validator generalValidator;

	@Autowired
	@Qualifier("updateProcessor")
	private Processor updateProcessor;

	@Autowired
	private UserDao userDao;

	@Autowired
	IdfcbankNBStatusEnquiryProcessor idfcbankNBStatusEnquiryProcessor;

	public static final String PIPE = "|";
	public static final String DOUBLE_PIPE = "||";

	public Map<String, String> process(Fields fields) throws SystemException {

		String newTxnId = TransactionManager.getNewTransactionId();
		fields.put(FieldType.TXN_ID.getName(), newTxnId);
		logger.info("fieldsin response action -------->>>" + fields.getFieldsAsString());
		// generalValidator.validate(fields);
		Transaction transactionResponse = new Transaction();
		logger.info("fieldsin response action -------->>>" + fields.getFieldsAsString());

		String pipedResponse = fields.get(FieldType.IDFC_FINAL_RESPONSE.getName());
		transactionResponse = toTransaction(pipedResponse, transactionResponse);
		logger.info("fieldsin response action -------->>>" + fields.getFieldsAsString());

		boolean doubleVer = doubleVerification(transactionResponse, fields);
		logger.info("process:: doubleVerification={}, pgRefNo={} ", doubleVer,
				fields.get(FieldType.PG_REF_NUM.getName()));
		if (doubleVer) {

			idfcTransformer idfctransformer = new idfcTransformer(transactionResponse);
			idfctransformer.updateResponse(fields);
		} else {
			fields.put(FieldType.STATUS.getName(), StatusType.DENIED.getName());
			fields.put(FieldType.RESPONSE_CODE.getName(), ErrorType.SIGNATURE_MISMATCH.getCode());
			fields.put(FieldType.RESPONSE_MESSAGE.getName(), ErrorType.SIGNATURE_MISMATCH.getResponseMessage());
		}

		// transactionResponse = toTransaction(pipedResponse);

		// federalBankTransformer = new FederalBankNBTransformer(transactionResponse);
		// federalBankTransformer.updateResponse(fields);

		fields.put(FieldType.INTERNAL_ORIG_TXN_TYPE.getName(), fields.get(FieldType.TXNTYPE.getName()));
		fields.remove(FieldType.IDFC_FINAL_RESPONSE.getName());
		ProcessManager.flow(updateProcessor, fields, true);
		return fields.getFields();

	}

	@SuppressWarnings("unlikely-arg-type")
	private boolean doubleVerification(Transaction transactionResponse, Fields fields) {

		try {

			if (!transactionResponse.getPAID().equalsIgnoreCase("Y")) {
				return true;
			}

			Map<String, String> keyMap = getTxnKey(fields);
			fields.put(FieldType.ADF1.getName(), keyMap.get("ADF1"));
			fields.put(FieldType.ADF2.getName(), keyMap.get("ADF2"));
			fields.put(FieldType.MERCHANT_ID.getName(), keyMap.get("MerchantId"));
			
				transactionResponse.setMCC(keyMap.get("ADF3"));

		
			logger.info("fieldsin response action -------->>>" + fields.getFieldsAsString());

			String dualResponseJson = idfcbankNBStatusEnquiryProcessor.verficationRequest(transactionResponse, fields);
			logger.info("dual verfication   for idfc acquirer response action -------->>>" + dualResponseJson);

			logger.info("idfc  Double verification Response = " + dualResponseJson);

			String toamount = Amount.toDecimal(fields.get(FieldType.TOTAL_AMOUNT.getName()),
					fields.get(FieldType.CURRENCY_CODE.getName()));
			JSONArray getArray = new JSONArray(dualResponseJson);
			// MID|PID|AMT|TxnType|ACCNO|NAR|BID|ResponseCode|ResponseMsg|PAID

			String pgRefNum = null;
			String amount = null;
			String txnCode = null;
			String txnStatus = null;
			for (int i = 0; i < getArray.length(); i++) {
				JSONObject obj = (JSONObject) getArray.get(i);

				if ((obj.get("k")).equals("PID")) {
					pgRefNum = obj.get("v").toString();

				}
				if ((obj.get("k")).equals("AMT")) {
					amount = obj.get("v").toString();

				}
				if ((obj.get("k")).equals("RESPONSECODE")) {
					txnCode = obj.get("v").toString();

				}
				if ((obj.get("k")).equals("RESPONSEMSG")) {
					txnStatus = obj.get("v").toString();

				}
			}

			logger.info("doubleVerification::  Idfc net banking response.  Code ={},Status={},  resAmount={},pgRefNo={}", txnCode,
					txnStatus,	amount, pgRefNum);

			if (txnCode != null && txnCode.equalsIgnoreCase("SUC000")&&txnStatus != null && txnStatus.equalsIgnoreCase("SUCCESS")
					&& fields.get(FieldType.PG_REF_NUM.getName()).equalsIgnoreCase(pgRefNum)
					&& toamount.equalsIgnoreCase(amount)) {

				return true;
			}
			logger.info("doubleVerification:: failed. Code ={},Status={},  resAmount={},pgRefNo={}", txnCode,
					txnStatus,	amount, pgRefNum);
			return false;
		}

		catch (Exception e) {
			logger.error("Exceptionn ", e);
			return false;
		}

	}

	String s = null;
	String s1 = "";

//	{"merchant_vpa":"bhartipay@cosb","remark":"upiPayment",
//		"errCode":"U67","status":"FAILURE","merchant":["qr.bhar"],"extTransactionId":"1319121130180535","customerName":"KATKAM PALLAVI ASHOK","responseTime":"Wed Nov 30 18:25:59 IST 2022",
//		"customer_vpa":"7838304007@cosb","rrn":"233418240004","txnId":"COBQRVA96H6ARW3AZ3ZVEOPHE49E86TN4D2"
//			,"checksum":"","amount":"500.00"}
	public Transaction toTransaction(String response, Transaction transactionResponse) {

		JSONArray getArray = new JSONArray(response);
		// MID|PID|AMT|TxnType|ACCNO|NAR|BID|ResponseCode|ResponseMsg|PAID

		String pgRefNum = null;
		for (int i = 0; i < getArray.length(); i++) {
			JSONObject obj = (JSONObject) getArray.get(i);
			if ((obj.get("k")).equals("PID")) {
				transactionResponse.setPID(obj.get("v").toString());

			}
			if ((obj.get("k")).equals("MID")) {
				transactionResponse.setMID(obj.get("v").toString());

			}
			if ((obj.get("k")).equals("AMT")) {
				transactionResponse.setAMT(obj.get("v").toString());

			}
			if ((obj.get("k")).equals("TXNTYPE")) {
				transactionResponse.setTxnType(obj.get("v").toString());

			}
			if ((obj.get("k")).equals("BID")) {
				transactionResponse.setBID(obj.get("v").toString());

			}

			if ((obj.get("k")).equals("RESPONSECODE")) {
				transactionResponse.setResponseCode(obj.get("v").toString());

			}

			if ((obj.get("k")).equals("RESPONSEMSG")) {
				transactionResponse.setResponseMsg(obj.get("v").toString());

			}
			if ((obj.get("k")).equals("PAID")) {
				transactionResponse.setPAID(obj.get("v").toString());

			}
			
		}
logger.info("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@"+transactionResponse.toString());
		return transactionResponse;
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
						.equalsIgnoreCase(AcquirerType.getInstancefromCode(AcquirerType.IDFC.getCode()).getName())) {
					account = accountThis;
					break;
				}
			}
		}

		AccountCurrency accountCurrency = account.getAccountCurrency(fields.get(FieldType.CURRENCY_CODE.getName()));

		keyMap.put("ADF1", accountCurrency.getAdf1());
		keyMap.put("ADF2", accountCurrency.getAdf2());
		keyMap.put("MerchantId", accountCurrency.getMerchantId());
		keyMap.put("ADF3", accountCurrency.getAdf3());

		return keyMap;

	}

}
