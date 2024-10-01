package com.pay10.TFP;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.json.Json;
import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.exception.SystemException;
import com.pay10.commons.user.Account;
import com.pay10.commons.user.AccountCurrency;
import com.pay10.commons.user.User;
import com.pay10.commons.user.UserDao;
import com.pay10.commons.util.*;
import com.pay10.errormapping.ErrorMappingDTO;
import com.pay10.errormapping.Impl.ErrorMappingDAOImpl;
import com.pay10.jammuandkashmir.JammuandKashmirBankNBTransformer;
import com.pay10.pg.core.util.FederalBankNBEncDecService;
import com.pay10.pg.core.util.ProcessManager;
import com.pay10.pg.core.util.Processor;
import com.pay10.shivalikNB.ShivalikBankNBTransformer;
import com.pay10.shivalikNB.ShivaliknbProcessor;
import com.pay10.shivalikNB.ShivaliknbResponseHandler;
import com.pay10.shivalikNB.Transaction;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Service
public class TFPResponseHandler {
	private static Logger logger = LoggerFactory.getLogger(TFPResponseHandler.class.getName());
	@Autowired
	TFPProcessor tfpProcessor;
	@Autowired
	private Validator generalValidator;
	@Autowired
	private UserDao userDao;

	@Autowired
	private TFPStausEnquiryProcessor tFPStausEnquiryProcessor;
	@Autowired
	@Qualifier("updateProcessor")
	private Processor updateProcessor;

	public Map<String, String> process(Fields fields) throws SystemException {
		try {

			String newTxnId = TransactionManager.getNewTransactionId();
			fields.put(FieldType.TXN_ID.getName(), newTxnId);
			logger.info("TFP Response Handler1: " + fields.getFieldsAsString());

			String Response = fields.get(FieldType.TFP_FINAL_RESPONSE.getName());
			logger.info("TFP Response Handler1: " + Response);

            Map<String, String> callBackResponse = toMap(Response);
			logger.info("TFP Response Handler1: " + callBackResponse);


			boolean doubleVer = doubleVerification(callBackResponse, fields);
			logger.info("process:: doubleVerification={}, pgRefNo={} ", doubleVer,
					fields.get(FieldType.PG_REF_NUM.getName()));
			if (doubleVer == true) {
				TFPTransformer tFPTransformer = new TFPTransformer();
				tFPTransformer.updateResponse(fields, callBackResponse);

			} else {
				fields.put(FieldType.STATUS.getName(), StatusType.DENIED.getName());
				fields.put(FieldType.RESPONSE_CODE.getName(), ErrorType.SIGNATURE_MISMATCH.getCode());
				fields.put(FieldType.RESPONSE_MESSAGE.getName(), ErrorType.SIGNATURE_MISMATCH.getResponseMessage());
			}

			fields.put(FieldType.INTERNAL_ORIG_TXN_TYPE.getName(), fields.get(FieldType.TXNTYPE.getName()));
			fields.remove(FieldType.TFP_FINAL_RESPONSE.getName());
			logger.info("Fields before call updateProcessor in TFP Response Handler:{}", fields.getFieldsAsString());
			logger.info("Fields before call updateProcessor in TFP updateProcessor :{}", updateProcessor.toString());
			logger.info("Fields before call fields in TFP fields:{}", fields.getFieldsAsString());
			ProcessManager.flow(updateProcessor, fields, true);

			return fields.getFields();
		} catch (Exception e) {
			logger.error("Exceptionn ", e);
		}
		return fields.getFields();

	}

	private boolean doubleVerification(Map<String, String> callBackResponse, Fields fields) {
		try {
			if (!callBackResponse.get(FieldType.STATUS.getName()).equalsIgnoreCase("Captured")) {
				return true;
			}

			Map<String, String> keyMap = getTxnKey(fields);
			fields.put(FieldType.ADF3.getName(), keyMap.get("ADF3"));
			fields.put(FieldType.ADF5.getName(), keyMap.get("ADF5"));
			fields.put(FieldType.RRN.getName(), callBackResponse.get(FieldType.TXN_ID.getName()));
			String request = tFPStausEnquiryProcessor.statusEnquiry(fields);
			 JSONArray dataArray = new JSONArray(request);
             JSONObject response = dataArray.getJSONObject(0);
			logger.info("Dual Verfication  response. " + request.toString());

			logger.info("Dual Verfication  response. " + response.toString());
			String toamount = Amount.toDecimal(fields.get(FieldType.TOTAL_AMOUNT.getName()),
					fields.get(FieldType.CURRENCY_CODE.getName()));
			if (response.getString("AMOUNT").equalsIgnoreCase(toamount)
					&& response.getString("STATUS").equalsIgnoreCase("Captured")
					&& response.getString((FieldType.RESPONSE_CODE.getName())).equalsIgnoreCase("000")
					&& response.getString("ORDER_ID").equalsIgnoreCase(fields.get(FieldType.PG_REF_NUM.getName()))) {

				return true;
			}
			logger.info("Dual Verfication  failed.   resAmount={},pgRefNo={}" + response.toString());

			return false;
		}

		catch (Exception e) {
			logger.error("Exceptionn ", e);
			return false;
		}

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
						.equalsIgnoreCase(AcquirerType.getInstancefromCode(AcquirerType.TFP.getCode()).getName())) {
					account = accountThis;
					break;
				}
			}
		}

		AccountCurrency accountCurrency = account.getAccountCurrency(fields.get(FieldType.CURRENCY_CODE.getName()));

		keyMap.put("ADF5", accountCurrency.getAdf5());
		keyMap.put("ADF3", accountCurrency.getAdf3());

		return keyMap;

	}
	
	public Map<String ,String> toMap(String data){
		  String content = data.substring(1, data.length() - 1);

	        // Split into key-value pairs
	        String[] pairs = content.split(", ");

	        // Populate map
	        Map<String, String> map = new HashMap<>();
	        for (String pair : pairs) {
	            String[] keyValue = pair.split("=");
	            String key = keyValue[0];
	            String value = keyValue[1];
	            map.put(key, value);
	        }
			return map;

	}
}
