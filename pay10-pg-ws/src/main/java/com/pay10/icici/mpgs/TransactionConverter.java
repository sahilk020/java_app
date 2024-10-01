package com.pay10.icici.mpgs;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pay10.commons.exception.SystemException;
import com.pay10.commons.util.Currency;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.StatusType;
import com.pay10.commons.util.TransactionType;
import com.pay10.pg.core.util.AcquirerTxnAmountProvider;

/**
 * @author Rahul
 *
 */
@Service("iciciMpgsTransactionConverter")
public class TransactionConverter {

	@Autowired
	private AcquirerTxnAmountProvider acquirerTxnAmountProvider;

	@SuppressWarnings("incomplete-switch")
	public JSONObject perpareRequest(Fields fields, Transaction transaction, Transaction transactionResponse)
			throws SystemException {

		JSONObject request = new JSONObject();

		switch (TransactionType.getInstance(fields.get(FieldType.TXNTYPE.getName()))) {
		case AUTHORISE:
			break;
		case ENROLL:
			request = enrollRequest(fields, transaction);
			break;
		case REFUND:
			request = refundRequest(fields);
			break;
		case SALE:
			request = saleRequest(fields, transaction, transactionResponse);
			break;
		case CAPTURE:
			break;
		case STATUS:
			// request = statusEnquiryRequest(fields, transaction);
			break;
		}
		return request;

	}

	public JSONObject enrollRequest(Fields fields, Transaction transaction) throws SystemException {

		String amount = acquirerTxnAmountProvider.amountProvider(fields);
		String currencyCode = Currency.getAlphabaticCode(fields.get(FieldType.CURRENCY_CODE.getName()));

		JSONObject subSecure = new JSONObject();
		subSecure.put(Constants.RESPONSE_URL, fields.get(FieldType.RETURN_URL.getName()));
		subSecure.put(Constants.PAGE_GENERATION, Constants.CUSTOMIZED);

		JSONObject authenticationRedirect = new JSONObject();
		authenticationRedirect.put(Constants.AUTHENTICATION_REDIRECT, subSecure);

		JSONObject order = new JSONObject();
		order.put(Constants.AMOUNT, amount);
		order.put(Constants.CURRENCY, currencyCode);

		JSONObject expiry = new JSONObject();
		expiry.put(Constants.MONTH, transaction.getExpMonth());
		expiry.put(Constants.YEAR, transaction.getExpYear());

		JSONObject card = new JSONObject();
		card.put(Constants.EXPIRY, expiry);
		card.put(Constants.NUMBER, fields.get(FieldType.CARD_NUMBER.getName()));

		JSONObject cardType = new JSONObject();
		cardType.put(Constants.CARD, card);

		JSONObject provided = new JSONObject();
		provided.put(Constants.PROVIDED, cardType);

		JSONObject main = new JSONObject();
		main.put(Constants.API_OPERATION, Constants.CHECK_ENROLLMENT);
		main.put(Constants.SOURCE_FUND, provided);
		main.put(Constants.THREE_D_SECURE, authenticationRedirect);
		main.put(Constants.ORDER, order);

		return main;

	}

	public JSONObject acsProcessRequest(Fields fields) {

		JSONObject secure = new JSONObject();
		secure.put(Constants.PARES, fields.get(FieldType.PARES.getName()).concat("zxcv"));

		JSONObject main = new JSONObject();
		main.put(Constants.API_OPERATION, Constants.PROCESS_ACS_RESULT);
		main.put(Constants.THREE_D_SECURE, secure);
		return main;

	}

	public JSONObject saleRequest(Fields fields, Transaction transaction, Transaction transactionResponse)
			throws SystemException {
		String gatewayRecommendationResult = transactionResponse.getGatewayRecommendation();
		if (!gatewayRecommendationResult.equalsIgnoreCase("PROCEED")) {
			return null;
		}
		String amount = acquirerTxnAmountProvider.amountProvider(fields);
		String currencyCode = Currency.getAlphabaticCode(fields.get(FieldType.CURRENCY_CODE.getName()));

		JSONObject order = new JSONObject();
		order.put(Constants.AMOUNT, amount);
		order.put(Constants.CURRENCY, currencyCode);
		order.put(Constants.REFERENCE, fields.get(FieldType.PG_REF_NUM.getName()));

		JSONObject expiry = new JSONObject();
		expiry.put(Constants.MONTH, transaction.getExpMonth());
		expiry.put(Constants.YEAR, transaction.getExpYear());

		JSONObject card = new JSONObject();
		card.put(Constants.EXPIRY, expiry);
		card.put(Constants.NUMBER, fields.get(FieldType.CARD_NUMBER.getName()));
		card.put(Constants.SECURITY_CODE, fields.get(FieldType.CVV.getName()));

		JSONObject cardnumber = new JSONObject();
		cardnumber.put(Constants.CARD, card);

		JSONObject provided = new JSONObject();
		provided.put(Constants.PROVIDED, cardnumber);

		JSONObject source = new JSONObject();
		source.put(Constants.TYPE, "CARD");
		source.put(Constants.PROVIDED, cardnumber);

		JSONObject main = new JSONObject();
		main.put(Constants.API_OPERATION, Constants.PAY);
		main.put(Constants.SECURE_ID, fields.get(FieldType.ORIG_TXN_ID.getName()));
		main.put(Constants.SOURCE_FUND, source);
		main.put(Constants.ORDER, order);

		return main;

	}

	public JSONObject refundRequest(Fields fields) throws SystemException {
		String amount = acquirerTxnAmountProvider.amountProvider(fields);
		String currencyCode = Currency.getAlphabaticCode(fields.get(FieldType.CURRENCY_CODE.getName()));
		JSONObject transactionJson = new JSONObject();
		transactionJson.put(Constants.AMOUNT, amount);
		transactionJson.put(Constants.CURRENCY, currencyCode);
		JSONObject main = new JSONObject();
		main.put(Constants.TRANSACTION, transactionJson);
		main.put(Constants.API_OPERATION, Constants.REFUND);
		return main;
	}

	public Transaction toTransaction(JSONObject response) {

		Transaction transaction = new Transaction();

		Map<String, String> secureMap = new HashMap<String, String>();
		Map<String, String> responseMap = new HashMap<String, String>();
		Map<String, String> customizedMap = new HashMap<String, String>();

		JSONObject secure = new JSONObject();
		secure = response.getJSONObject(Constants.THREE_D_SECURE);

		JSONObject authenticationRedirect = new JSONObject();
		authenticationRedirect = secure.getJSONObject(Constants.AUTHENTICATION_REDIRECT);

		JSONObject customized = new JSONObject();
		customized = authenticationRedirect.getJSONObject(Constants.LOWER_CUSTOMIZED);

		JSONObject responseJson = new JSONObject();
		responseJson = response.getJSONObject(Constants.RESPONSE);

		for (Object key : secure.keySet()) {

			String key1 = key.toString();
			String value = secure.get(key.toString()).toString();
			secureMap.put(key1, value);
		}
		for (Object key : responseJson.keySet()) {

			String key1 = key.toString();
			String value = responseJson.get(key.toString()).toString();
			responseMap.put(key1, value);
		}
		for (Object key : customized.keySet()) {

			String key1 = key.toString();
			String value = customized.get(key.toString()).toString();
			customizedMap.put(key1, value);
		}

		transaction.setAcsURL((customizedMap.get(Constants.ACS_URL)));
		transaction.setXid((secureMap.get(Constants.XID)));
		transaction.setPaReq((customizedMap.get(Constants.PAREQ)));
		transaction.setVeResEnrolled((secureMap.get(Constants.VERES_ENROLLED)));
		transaction.setGatewayRecommendation(responseMap.get(Constants.GATEWAY_RECOMMENDATION));
		return transaction;

	}

	public Transaction toSaleTransaction(JSONObject response) {


		Transaction transaction = new Transaction();

		Map<String, String> auth = new HashMap<String, String>();
		Map<String, String> responseMap = new HashMap<String, String>();

		JSONObject responseJson = new JSONObject();
		responseJson = response.getJSONObject(Constants.RESPONSE);

		for (Object key : responseJson.keySet()) {

			String key1 = key.toString();
			String value = responseJson.get(key.toString()).toString();
			responseMap.put(key1, value);
		}

		String gatewayRecommendation = responseMap.get("gatewayRecommendation");
		if (gatewayRecommendation == null) {

			JSONObject authorizationResponse = new JSONObject();
			authorizationResponse = response.getJSONObject("transaction");

			for (Object key : authorizationResponse.keySet()) {

				String key1 = key.toString();
				String value = authorizationResponse.get(key.toString()).toString();
				auth.put(key1, value);
			}
			transaction.setStatus(response.getString("result"));
			transaction.setAcquirerCode(responseMap.get("acquirerCode"));
			transaction.setGatewayCode(responseMap.get("gatewayCode"));
			transaction.setRrn(auth.get("receipt"));
			transaction.setAcq(auth.get("receipt"));
			transaction.setXid(responseMap.get("xid"));
			transaction.setGatewayRecommendation("NA");
		} else {
			transaction.setGatewayCode(StatusType.CANCELLED.getName());
			transaction.setStatus(StatusType.CANCELLED.getName());
			transaction.setAcquirerCode("NA");
			transaction.setRrn("NA");
			transaction.setAcq("NA");
			transaction.setXid(responseMap.get("xid"));
			transaction.setGatewayRecommendation(responseMap.get("gatewayRecommendation"));
			
			transaction.setGatewayCode(responseMap.get("gatewayRecommendation"));
		}

		return transaction;

	}
}
