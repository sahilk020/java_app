package com.pay10.payu;

import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.exception.SystemException;
import com.pay10.commons.user.Token;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.PropertiesManager;
import com.pay10.commons.util.TransactionManager;
import com.pay10.commons.util.TransactionType;
import com.pay10.pg.core.payu.util.Constants;
import com.pay10.pg.core.payu.util.PayuHasher;
import com.pay10.pg.core.tokenization.TokenManager;

/**
 * @author Rohit
 *
 */
@Service("payuTransactionConverter")
public class TransactionConverter {

	private static final Logger logger = LoggerFactory.getLogger(TransactionConverter.class.getName());
	
	@Autowired
	private TokenManager tokenManager;


	@SuppressWarnings("incomplete-switch")
	public String perpareRequest(Fields fields, Transaction transaction) throws SystemException {

		String request = null;

		switch (TransactionType.getInstance(fields.get(FieldType.TXNTYPE.getName()))) {
		case AUTHORISE:
		case ENROLL:
			break;
		case REFUND:
			request = refundRequest(fields, transaction);
			break;
		case SALE:
			request = StringUtils.isEmpty(fields.get(FieldType.TOKEN_ID.getName()))?saleRequest(fields, transaction):expressRequest(fields, transaction);
			break;
		}
		return request.toString();

	}

	public String saleRequest(Fields fields, Transaction transaction) throws SystemException {

		try {
				String requestURL = PropertiesManager.propertiesMap.get("PAYUSaleUrl");
			logger.info("fields : "+fields.getFieldsAsString());
			logger.info("Request URL : "+requestURL);
			String expMonth = "";
			String expYear = "";
			String pg = transaction.getPg();

			String cardExpMonYr = transaction.getCcExpYr();
			if (cardExpMonYr != null) {
				expMonth = cardExpMonYr.substring(0, cardExpMonYr.length() - 4);
				expYear = cardExpMonYr.substring(cardExpMonYr.length() - 4);
			}

			TreeMap<String, String> payuParams = new TreeMap<String, String>();

			payuParams.put(Constants.KEY, transaction.getKey());
			payuParams.put(Constants.TXNID, transaction.getTxnId());
			payuParams.put(Constants.AMOUNT, transaction.getAmount());
			payuParams.put(Constants.PRODUCT_INFO, transaction.getProductInfo());
			payuParams.put(Constants.FIRSTNAME, transaction.getFirstName());
			payuParams.put(Constants.EMAIL, transaction.getEmail());
			payuParams.put(Constants.SURL, transaction.getSurl());
			payuParams.put(Constants.FURL, transaction.getFurl());
			
			
			if (pg.equals("UP")) {
			payuParams.put(Constants.PG, "UPI");
			payuParams.put(Constants.BANKCODE, "UPI");
			payuParams.put("vpa",fields.get(FieldType.PAYER_ADDRESS.getName()));
			}else {
				payuParams.put(Constants.PG, transaction.getPg());
				payuParams.put(Constants.BANKCODE, transaction.getBankCode());
		
				
				
			}
			

			

			if(pg.equals(Constants.CC)||pg.equals(Constants.DC)) {
				payuParams.put(Constants.CCNUM, transaction.getCcnum());
				payuParams.put(Constants.CCNAME, transaction.getCcname());
				payuParams.put(Constants.CCVV, transaction.getCcvv());
				payuParams.put(Constants.CCEXPMON, expMonth);
				payuParams.put(Constants.CCEXPYR, expYear);
			}
			payuParams.put("Consent_shared", transaction.getConsentShared());

			StringBuilder outputHtml = new StringBuilder();
			outputHtml.append("<html>");
			outputHtml.append("<head>");
			outputHtml.append("<title>Pay10 Merchant Checkout Page</title>");
			outputHtml.append("</head>");
			outputHtml.append("<body>");
			outputHtml.append("<center><h1>Please do not refresh this page...</h1></center>");
			outputHtml.append("<form method='post' action='" + requestURL + "' name='payu_form'>");

			for (Map.Entry<String, String> entry : payuParams.entrySet()) {
				outputHtml
						.append("<input type='hidden' name='" + entry.getKey() + "' value='" + entry.getValue() + "'>");
			}

			outputHtml.append("<input type='hidden' name='hash' value='" + transaction.getHash() + "'>");
			outputHtml.append("</form>");
			outputHtml.append("<script type='text/javascript'>");
			outputHtml.append("document.payu_form.submit();");
			outputHtml.append("</script>");
			outputHtml.append("</body>");
			outputHtml.append("</html>");
			
			logger.info("Request build for payu acquirer "+outputHtml.toString());

			return outputHtml.toString();
		}

		catch (Exception e) {
			logger.error("Exception in generating Payu sale request ", e);
		}

		return null;
	}

	public String refundRequest(Fields fields, Transaction transaction) throws SystemException {

		try {

			StringBuilder request = new StringBuilder();

			request.append(Constants.RKEY);
			request.append(transaction.getKey());
			request.append("&");
			request.append(Constants.RCOMMAND);
			request.append(Constants.CANCEL_REFUND_TRANSACTION);
			request.append("&");
			request.append(Constants.RHASH);
			request.append(transaction.getHash());
			request.append("&");
			request.append(Constants.RVAR1);
			request.append(transaction.getMihPayuId());
			request.append("&");
			request.append(Constants.RVAR2);
			request.append(transaction.getRefundToken());
			request.append("&");
			request.append(Constants.RVAR3);
			request.append(transaction.getRefundAmount());
			logger.info("payu>>>>>>>>>>>>>>>>>>>>>>>>>>"+request.toString());

			String post_data = request.toString();
			return post_data;

		}

		catch (Exception e) {
			logger.error("Exception in generating payu refund request", e);
		}
		return null;

	}
	
	private Token getTokenDetails(Fields fields)throws SystemException
	{
		try {
		Token token = tokenManager.getToken(fields);
		if(ObjectUtils.isEmpty(token))
		{
			throw new SystemException(ErrorType.INVALID_TOKEN, ErrorType.INVALID_TOKEN.getInternalMessage());
		}
		return token;
		
		}catch(Exception e)
		{
			throw new SystemException(ErrorType.INVALID_TOKEN,e, ErrorType.INVALID_TOKEN.getInternalMessage());
		}
	}
	
	
	public String expressRequest(Fields fields, Transaction transaction) throws SystemException {

		logger.info("Inside Express Payment");
		Token token = getTokenDetails(fields);
		try {
			String requestURL = PropertiesManager.propertiesMap.get("PAYUSaleUrl");

			logger.info("fields : "+fields.getFieldsAsString());
			logger.info("Request URL : "+requestURL);
			String expMonth = "";
			String expYear = "";
			String pg = transaction.getPg();

			String cardExpMonYr = transaction.getCcExpYr();
			if (cardExpMonYr != null) {
				expMonth = cardExpMonYr.substring(0, cardExpMonYr.length() - 4);
				expYear = cardExpMonYr.substring(cardExpMonYr.length() - 4);
			}

			TreeMap<String, String> payuParams = new TreeMap<String, String>();

			payuParams.put(Constants.KEY, transaction.getKey());
			payuParams.put(Constants.TXNID, transaction.getTxnId());
			payuParams.put(Constants.AMOUNT, transaction.getAmount());
			payuParams.put(Constants.PRODUCT_INFO, transaction.getProductInfo());
			payuParams.put("salt_version", "1");
			payuParams.put(Constants.FIRSTNAME, transaction.getFirstName());
			payuParams.put(Constants.EMAIL, transaction.getEmail());
			payuParams.put(Constants.SURL, transaction.getSurl());
			payuParams.put(Constants.FURL, transaction.getFurl());

			payuParams.put(Constants.PG, transaction.getPg());
		

			if(pg.equals(Constants.CC)||pg.equals(Constants.DC)) {
				payuParams.put("user_credentials", transaction.getKey()+":"+fields.get(FieldType.CUST_EMAIL.getName()));
				payuParams.put(Constants.CCVV, transaction.getCcvv());
				payuParams.put("store_card_token", token.getCardToken());
				payuParams.put("card_name", token.getCardLabel());
				
			}
			//payuParams.put("Consent_shared", transaction.getConsentShared());

			logger.info("Express Request data : "+payuParams);
			StringBuilder outputHtml = new StringBuilder();
			outputHtml.append("<html>");
			outputHtml.append("<head>");
			outputHtml.append("<title>Pay10 Merchant Checkout Page</title>");
			outputHtml.append("</head>");
			outputHtml.append("<body>");
			outputHtml.append("<center><h1>Please do not refresh this page...</h1></center>");
			outputHtml.append("<form method='post' action='" + requestURL + "' name='payu_form'>");

			for (Map.Entry<String, String> entry : payuParams.entrySet()) {
				outputHtml
						.append("<input type='hidden' name='" + entry.getKey() + "' value='" + entry.getValue() + "'>");
			}

			outputHtml.append("<input type='hidden' name='hash' value='" + transaction.getHash() + "'>");
			outputHtml.append("</form>");
			outputHtml.append("<script type='text/javascript'>");
			outputHtml.append("document.payu_form.submit();");
			outputHtml.append("</script>");
			outputHtml.append("</body>");
			outputHtml.append("</html>");
			
			logger.info("Request build for payu acquirer for express "+outputHtml.toString());

			return outputHtml.toString();
		}

		catch (Exception e) {
			logger.error("Exception in generating Payu sale express request ", e);
		}

		return null;
	}
	

	
	public Transaction toTransaction(String jsonResponse, String txnType) {

		Transaction transaction = new Transaction();

		if (StringUtils.isBlank(jsonResponse)) {

			logger.info("Empty response received for payu refund");
			return transaction;
		}

		JSONObject respObj = new JSONObject(jsonResponse);

		if (respObj.has(Constants.STATUS)) {

			JSONObject respBody = new JSONObject(jsonResponse);

			if (respBody.has(Constants.MSG)) {

				if (respBody.has(Constants.STATUS)) {

					String status = respBody.get(Constants.STATUS).toString();
					transaction.setStatus(status);
				}

				if (respBody.has(Constants.MSG)) {

					String msg = respBody.get(Constants.MSG).toString();
					transaction.setResponseMsg(msg);
				}
				if (respBody.has(Constants.MIHPAYID)) {

					String mihpayid = respBody.get(Constants.MIHPAYID).toString();
					transaction.setMihPayuId(mihpayid);
				}
				
				if (respBody.has("request_id")) {

					String request_id = respBody.get("request_id").toString();
					transaction.setRefundId(request_id);
				}
				if (respBody.has(Constants.ERROR_CODE)) {

					String error_code = respBody.get(Constants.ERROR_CODE).toString();
					transaction.setResponseCode(error_code);
				}
			}
		}

		return transaction;

	}

	public TransactionConverter() {

	}

}
