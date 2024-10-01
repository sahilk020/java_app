package com.pay10.easebuzz;

import java.util.Map;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;

import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.exception.SystemException;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.PropertiesManager;
import com.pay10.commons.util.StatusType;
import com.pay10.commons.util.TransactionManager;
import com.pay10.commons.util.TransactionType;
import com.pay10.commons.util.Validator;
import com.pay10.pg.core.util.CashfreeChecksumUtil;
import com.pay10.pg.core.util.EasebuzzChecksumUtil;
import com.pay10.pg.core.util.ProcessManager;
import com.pay10.pg.core.util.Processor;

@Service
public class EasebuzzSaleResponseHandler {

	private static Logger logger = LoggerFactory.getLogger(EasebuzzSaleResponseHandler.class.getName());

	@Autowired
	private Validator generalValidator;

	@Autowired
	@Qualifier("updateProcessor")
	private Processor updateProcessor;

	@Autowired
	@Qualifier("easebuzzTransactionConverter")
	private TransactionConverter transactionConverter;

	@Autowired
	private EasebuzzTransformer easebuzzTransformer;

	@Autowired
	private EasebuzzChecksumUtil easebuzzChecksumUtil;

	@Autowired
	@Qualifier("easebuzzTransactionCommunicator")
	private TransactionCommunicator transactionCommunicator;

	public Map<String, String> process(Fields fields) throws SystemException {

		logger.info("fields >>>>>>>>>>>>> :: " + fields.getFieldsAsString());
		
		String newTxnId = TransactionManager.getNewTransactionId();
		fields.put(FieldType.TXN_ID.getName(), newTxnId);
		fields.put(FieldType.TXNTYPE.getName(), TransactionType.SALE.getCode());
		Transaction transactionResponse = new Transaction();
		String response = fields.get(FieldType.EASEBUZZ_RESPONSE_FIELD.getName());
		//logger.info("fields >>>>>>>>>>>>> :: " + fields.getFieldsAsString());
		logger.info("EASEBUZZ RESPONSE FIELD Response :: " + response);
		transactionResponse = toTransaction(response, transactionResponse);
		//JSONObject resJsonNB = new JSONObject(response);
		boolean doubleVer = doubleVerification(response, fields);
		/*
		 * if(response.contains("mode") &&
		 * resJsonNB.get("mode").toString().equalsIgnoreCase("NB")) { doubleVer = true;
		 * }else { doubleVer = doubleVerification(response, fields); }
		 */
		// boolean res = isHashMatching(response, fields);
		

		logger.info("Easebuzz Double Verification Response : "+doubleVer);
		fields.remove(FieldType.EASEBUZZ_RESPONSE_FIELD.getName());
		//generalValidator.validate(fields);

		if (doubleVer == true) {
			
			logger.info("Easebuzz TransactionResponse >>>>>>>>>>>>>>>>>>>>> "+transactionResponse);
			easebuzzTransformer = new EasebuzzTransformer(transactionResponse);
			logger.info("fields "+fields.getFieldsAsString());
			easebuzzTransformer.updateResponse(fields);
		} else {
			fields.put(FieldType.STATUS.getName(), StatusType.DENIED.getName());
			fields.put(FieldType.RESPONSE_CODE.getName(), ErrorType.SIGNATURE_MISMATCH.getCode());
			fields.put(FieldType.RESPONSE_MESSAGE.getName(), transactionResponse.getTxMsg());
		}
		fields.put(FieldType.INTERNAL_ORIG_TXN_TYPE.getName(), fields.get(FieldType.TXNTYPE.getName()));
		ProcessManager.flow(updateProcessor, fields, true);
		return fields.getFields();

	}

	public Transaction toTransaction(String response, Transaction transactionResponse) {
		Transaction transaction = new Transaction();
		transaction = transactionConverter.toTransaction(response);
		return transaction;
	}

	private boolean isHashMatching(String transactionResponse, Fields fields) throws SystemException {

		JSONObject resJson = new JSONObject(transactionResponse.toString());

		String responseSignature = (String) resJson.get(Constants.signature);

		resJson.remove(Constants.signature);
		String resSignatureCalculated = "";
		// easebuzzChecksumUtil.checkSaleResponseHash(resJson,fields.get(FieldType.TXN_KEY.getName()));

		logger.info("Order Id " + fields.get(FieldType.ORDER_ID.getName()) + "  bank response signature == "
				+ responseSignature);
		logger.info("Order Id " + fields.get(FieldType.ORDER_ID.getName()) + "  calculated signature == "
				+ resSignatureCalculated);

		if (responseSignature.contentEquals(resSignatureCalculated)) {
			return true;
		} else {
			logger.info("Signature from Bank did not match with generated Signature");
			logger.info("Bank Hash = " + responseSignature);
			logger.info("Calculated Hash = " + resSignatureCalculated);
			return false;
		}

	}

	private boolean doubleVerification(String transactionResponse, Fields fields) throws SystemException {

		try {

			JSONObject resJson = new JSONObject(transactionResponse);

			// Skip if txStatus is not present in response
			if (!transactionResponse.contains(Constants.status)) {
				return true;
			}

			// Skip for unsuccessful transactions if
			if (transactionResponse.contains(Constants.status)
					&& !resJson.get(Constants.status).toString().equalsIgnoreCase("SUCCESS")) {
				return true;
			}

			Transaction transaction = new Transaction();
			transaction.setAmount(resJson.getString("amount"));
			transaction.setEmail(resJson.getString("email"));
			transaction.setPhone(resJson.getString("phone"));
			transaction.setTxnid(resJson.getString("txnid"));
			transaction.setMerchantKey(resJson.getString("key"));
			transaction.setSalt(fields.get(FieldType.TXN_KEY.getName()));

			logger.info("transaction data before double Verification "+transaction.toString());
			String checksum = transactionConverter.checksumForDoubleVerification(transaction);
			// fields.put(FieldType.HASH.getName(), StatusType.DENIED.getName());
			// String request = transactionConverter.statusEnquiryRequest(fields, null);
			transaction.setHash(checksum);
			MultiValueMap<String, String> finalRequest = transactionConverter.doubleVerificationRequest(transaction);
			logger.info("Easebuzz Double Verification Request = " + finalRequest);
			String hostUrl = PropertiesManager.propertiesMap.get(Constants.STATUS_ENQ_REQUEST_URL);
			// String response = transactionCommunicator.statusEnqPostRequest(request, hostUrl);
			String response = transactionCommunicator.doubleVeriPostRequest(finalRequest, hostUrl);
			logger.info("Bank Response = " + response);
			logger.info("Easebuzz Double Verification Response = " + response);

			JSONObject resJsonBank = new JSONObject(response);
			
			if((boolean) resJsonBank.get(Constants.status)) {
				
				JSONObject msgJsonResp = new JSONObject(resJsonBank.get("msg").toString());

				if (resJson.get(Constants.status).toString().equals(msgJsonResp.get(Constants.status).toString())
						&& resJson.get(Constants.Amount).toString().equals(msgJsonResp.get(Constants.Amount).toString())
						&& resJson.get(Constants.txnid).toString().equals(msgJsonResp.get(Constants.txnid).toString())) {
					return true;
				} else {
					logger.info("Double Verification Response donot match for Easebuzz");
					return false;
				}
			}else {
				logger.info("Double Verification Response false for Easebuzz");
				return false;
			}
			

		}

		catch (Exception e) {
			logger.error("Exceptionn ", e);
			return false;
		}

	}
	
	public static void main(String[] args) {
		String response = "{\"status\":true,\"msg\":{\"txnid\":\"1032120121090538\",\"firstname\":\"sonu\",\"email\":\"sonu@pay10.com\",\"phone\":\"09767146866\",\"key\":\"L0O2G1PMIR\",\"mode\":\"DC\",\"unmappedstatus\":\"NA\",\"cardCategory\":\"NA\",\"addedon\":\"2022-01-21 03:35:38\",\"payment_source\":\"Easebuzz\",\"PG_TYPE\":\"NA\",\"bank_ref_num\":\"202138004243\",\"bankcode\":\"NA\",\"error\":\"Successful Transaction\",\"error_Message\":\"Successful Transaction\",\"name_on_card\":\"sonu\",\"upi_va\":\"NA\",\"cardnum\":\"416021XXXXXX4351\",\"issuing_bank\":\"NA\",\"easepayid\":\"K49MZSPX83\",\"amount\":\"1.0\",\"net_amount_debit\":\"1.0\",\"cash_back_percentage\":\"50.0\",\"deduction_percentage\":\"0.35\",\"merchant_logo\":\"NA\",\"surl\":\"http://localhost:8080/pgui/jsp/easebuzzResponse\",\"furl\":\"http://localhost:8080/pgui/jsp/easebuzzResponse\",\"productinfo\":\"NA\",\"udf10\":\"\",\"udf9\":\"\",\"udf8\":\"\",\"udf7\":\"\",\"udf6\":\"\",\"udf5\":\"\",\"udf4\":\"\",\"udf3\":\"\",\"udf2\":\"\",\"udf1\":\"\",\"card_type\":\"Debit Card\",\"hash\":\"fd70d0e611b0804335e283e69c06e454c8a63397e8c7bc815d35fe3685e566ea71dd386a085708eec70175b1e8512a1d1c8fc2b012bb9048d4a7e6eb4d32f6b6\",\"status\":\"success\",\"bank_name\":\"NA\"}}";
		JSONObject resJsonBank = new JSONObject(response);
		System.out.println("resJsonBank "+resJsonBank);
		JSONObject msgJsonResp = new JSONObject(resJsonBank.get("msg").toString());
		System.out.println("msgJsonResp "+msgJsonResp.toString());
		System.out.println(""+msgJsonResp.get("status").toString());
	}
}
