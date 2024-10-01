package com.pay10.migs;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.pay10.commons.exception.SystemException;
import com.pay10.commons.user.UserDao;
import com.pay10.commons.util.AcquirerType;
import com.pay10.commons.util.ConfigurationConstants;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.Helper;
import com.pay10.commons.util.PropertiesManager;
import com.pay10.commons.util.SystemConstants;
import com.pay10.commons.util.TransactionType;

@Service("migsTransactionConverter")
public class TransactionConverter {

	@Autowired
	private UserDao userDao;
	
	@Autowired
	@Qualifier("propertiesManager")
	private PropertiesManager propertiesManager;

	private static Logger logger = LoggerFactory.getLogger(TransactionConverter.class.getName());

	@Autowired
	@Qualifier("migsTransaction")
	private Transaction transaction;

	private Object Y;

	private static final String RESPONSE_CODE = "vpc_TxnResponseCode";
	private static final String RESPONSE_MSG = "vpc_Message";
	private static final String TXN_ID = "vpc_MerchTxnRef";
	private static final String ORDER_INFO_ID = "vpc_OrderInfo";
	private static final String RRN = "vpc_ReceiptNo";
	private static final String MERCHANT_ID = "vpc_Merchant";
	private static final String PG_TRANSACTION_NO = "vpc_TransactionNo";
	private static final String SECURE_HASH = "vpc_SecureHash";
	private static final String COMMAND = "vpc_Command";
	private static final String AUTHORIZE_ID = "vpc_AuthorizeId";
	private static final String AMOUNT = "vpc_Amount";
	private static final String SECURE_HASH_TYPE = "vpc_SecureHashType";
	private static final String BATCH_NO = "vpc_BatchNo";
    private static final String DREXISTS = "vpc_DRExists";
    
	public Transaction getResponse(String response) {

		response = response + Constants.SEPARATOR; // to append & so that the
													// last member can be parsed

		transaction.setResponseCode(getValueofKey(response, RESPONSE_CODE));
		transaction.setMessage(getValueofKey(response, RESPONSE_MSG));
		transaction.setMerchTxnRef(getValueofKey(response, TXN_ID));
		transaction.setRRN(getValueofKey(response, RRN));
		transaction.setPgTransactionNo(getValueofKey(response, PG_TRANSACTION_NO));
		transaction.setOrderInfo(getValueofKey(response, ORDER_INFO_ID));
		transaction.setMerchantId(getValueofKey(response, MERCHANT_ID));
		transaction.setSecureHash(getValueofKey(response, SECURE_HASH));
		transaction.setCommand(getValueofKey(response, COMMAND));
		String txnType = transaction.getCommand();
		if (txnType.equalsIgnoreCase(TransactionType.REFUND.getName())) {
			transaction.setAuthorizeId(getValueofKey(response, BATCH_NO));
		} else {
			transaction.setAuthorizeId(getValueofKey(response, AUTHORIZE_ID));
		}
		transaction.setAmount(getValueofKey(response, AMOUNT));
		transaction.setSecureHashType(getValueofKey(response, SECURE_HASH_TYPE));
		return transaction;
	}
	public Transaction getResponseStatusEnquiry(String response) {
		response = response + Constants.SEPARATOR;
		String drExist = getValueofKey(response,DREXISTS);
		if(!drExist.equals(Y)){
		transaction.setDrExists(drExist);
		transaction.setAmount(getValueofKey(response, AMOUNT));
		transaction.setCommand(getValueofKey(response, COMMAND));
		transaction.setMerchantId(getValueofKey(response, MERCHANT_ID));
		transaction.setPgTransactionNo(getValueofKey(response, PG_TRANSACTION_NO));
		}else{
			transaction.setDrExists(drExist);
			transaction = getResponse(response);
		}
		return transaction;
	}
	private String getValueofKey(String text, String key) {
		String value = StringUtils.substringBetween(text, key + Constants.EQUATOR, Constants.SEPARATOR);
		try {
			if (!StringUtils.isBlank(value)) {
				value = URLDecoder.decode(value, SystemConstants.DEFAULT_ENCODING_UTF_8);
			}
		} catch (UnsupportedEncodingException unsupportedEncodingException) {
			logger.error("Exception : " + unsupportedEncodingException.getMessage());
			// continue with the current value if exception thrown
		}
		return value;
	}// getText

	@SuppressWarnings("incomplete-switch")
	public String getRequest(Fields fields) throws SystemException {
		String request = "";
		switch (TransactionType.getInstance(fields.get(FieldType.TXNTYPE.getName()))) {

		case ENROLL:
			request = getEnrollRequest(fields);
			fields.put(FieldType.ORIG_TXN_ID.getName(), fields.get(FieldType.TXN_ID.getName()));
			break;
		case SALE:
		case AUTHORISE:
			fields.put(FieldType.PG_REF_NUM.getName(), fields.get(FieldType.TXN_ID.getName()));
			if (fields.get(FieldType.ACQUIRER_TYPE.getName()).equals(AcquirerType.AXISMIGS.getCode())) {
				request = getAuthRequestForMigs(fields);
			} else {
				request = getAuthRequest(fields);
			}
			break;
		case CAPTURE:
			request = getCaptureRequest(fields);
			break;
		case REFUND:
			fields.put(FieldType.INTERNAL_ORIG_TXN_TYPE.getName(), fields.get(FieldType.TXNTYPE.getName()));
			fields.put(FieldType.PG_REF_NUM.getName(), fields.get(FieldType.TXN_ID.getName()));
			fields.put(FieldType.TOTAL_AMOUNT.getName(), fields.get(FieldType.AMOUNT.getName()));
			request = getRefundRequest(fields);
			break;
		case ENQUIRY:
			request = getStatusRequest(fields);
			break;
		}
		return request;
	}

	public String getEnrollRequest(Fields fields) throws SystemException {
		String url = Mapper.getTransactionUrl(fields);
		// Fetching Details from Property file

		Map<String, String> treeMap = new TreeMap<String, String>(
				propertiesManager.getAllProperties(PropertiesManager.getAmexpropertiesfile()));

		for (String key : treeMap.keySet()) {
			String value = treeMap.get(key);
			if (value.startsWith(Constants.CONFIG_SEPARATOR)) {
				value = value.replace(Constants.CONFIG_SEPARATOR, "");
			} else {
				value = fields.get(value);
			}

			if (key.equals(Constants.CARDEXP)) {
				value = MigsUtil.parseDate(value);
			}
			treeMap.put(key, value);
		}

		StringBuilder requestString = new StringBuilder();

		for (String key : treeMap.keySet()) {
			String value = treeMap.get(key);
			requestString.append(key);
			requestString.append(Constants.EQUATOR);
			requestString.append(value);
			requestString.append(Constants.SEPARATOR);
		}
		requestString.deleteCharAt(requestString.length() - 1);
		String secureHash = MigsUtil.calculateMac(requestString.toString(), fields.get(FieldType.TXN_KEY.getName()));

		StringBuilder httpRequest = new StringBuilder();
		httpRequest.append("<HTML>");
		httpRequest.append("<BODY OnLoad=\"OnLoadEvent();\" >");
		httpRequest.append("<form name=\"form1\" action=\"");
		httpRequest.append(url);
		httpRequest.append("\" method=\"post\">");

		for (String key : treeMap.keySet()) {
			String value = treeMap.get(key);
			httpRequest.append("<input type=\"hidden\" name=\"");
			httpRequest.append(key);
			httpRequest.append("\" value=\"");
			httpRequest.append(value);
			httpRequest.append("\">");
		}
		httpRequest.append("<input type=\"hidden\" name=\"vpc_SecureHash\" value=\"");
		httpRequest.append(secureHash);
		httpRequest.append("\">");
		httpRequest.append("<input type=\"hidden\" name=\"vpc_SecureHashType\" value=\"");
		httpRequest.append(Constants.HASHALGO);
		httpRequest.append("\">");
		httpRequest.append("</form>");
		httpRequest.append("<script language=\"JavaScript\">");
		httpRequest.append("function OnLoadEvent()");
		httpRequest.append("{document.form1.submit();}");
		httpRequest.append("</script>");
		httpRequest.append("</BODY>");
		httpRequest.append("</HTML>");

		return httpRequest.toString();
	}

	public String getAuthRequest(Fields fields) throws SystemException {
		// Fetching Details from Property file

		String requestFields = propertiesManager.getSystemProperty(Constants.AMEX_AUTH_FIELDS);
		String field43Fields = propertiesManager.getSystemProperty(Constants.AMEX_FIELD_43_FIELDS);
		StringBuilder requestString = new StringBuilder();
		Map<String, String> requestMap = new HashMap<String, String>();

		Collection<String> paramaters = Helper.parseFields(requestFields);
		for (String param : paramaters) {
			String[] parameterPair = param.split(Constants.EQUATOR);
			requestMap.put(parameterPair[0], parameterPair[1]);
		}
		for (String key : requestMap.keySet()) {
			String value = requestMap.get(key);
			if (value.startsWith(Constants.CONFIG_SEPARATOR)) {
				value = value.replace(Constants.CONFIG_SEPARATOR, "");
			} else {
				value = fields.get(value);
			}
			if (key.equals(Constants.CARDEXP)) {
				value = MigsUtil.parseDate(value);
			}
			requestMap.put(key, value);
			requestString.append(key);
			requestString.append(Constants.EQUATOR);
			String encodedValue;
			try {
				if (!StringUtils.isBlank(value)) {
					encodedValue = URLEncoder.encode(value, SystemConstants.DEFAULT_ENCODING_UTF_8);
				} else {
					encodedValue = value;
				}
			} catch (UnsupportedEncodingException unsupportedEncodingException) {
				logger.error("Exception encoding amex param" + unsupportedEncodingException);
				encodedValue = value;
			}
			requestString.append(encodedValue);
			requestString.append(Constants.SEPARATOR);
		}
		String field43String = getfield43Request(field43Fields, fields.get(FieldType.PAY_ID.getName()));
		if (StringUtils.isNotBlank(field43String)) {
			requestString.append(field43String);
		}
		requestString.deleteCharAt(requestString.length() - 1);
		return requestString.toString();
	}

	public String getAuthRequestForMigs(Fields fields) throws SystemException {
		// Fetching Details from Property file
		String requestFields = PropertiesManager.propertiesMap.get(Constants.AXIS_MIGS_AUTH_FIELDS);
		StringBuilder requestString = new StringBuilder();
		Map<String, String> requestMap = new HashMap<String, String>();

		Collection<String> paramaters = Helper.parseFields(requestFields);
		for (String param : paramaters) {
			String[] parameterPair = param.split(Constants.EQUATOR);
			requestMap.put(parameterPair[0], parameterPair[1]);
		}
		for (String key : requestMap.keySet()) {
			String value = requestMap.get(key);
			if (value.startsWith(Constants.CONFIG_SEPARATOR)) {
				value = value.replace(Constants.CONFIG_SEPARATOR, "");
			} else {
				value = fields.get(value);
			}
			if (key.equals(Constants.CARDEXP)) {
				value = MigsUtil.parseDate(value);
			}
			requestMap.put(key, value);
			requestString.append(key);
			requestString.append(Constants.EQUATOR);
			String encodedValue;
			try {
				if (!StringUtils.isBlank(value)) {
					encodedValue = URLEncoder.encode(value, SystemConstants.DEFAULT_ENCODING_UTF_8);
				} else {
					encodedValue = value;
				}
			} catch (UnsupportedEncodingException unsupportedEncodingException) {
				logger.error("Exception encoding amex param" + unsupportedEncodingException);
				encodedValue = value;
			}
			requestString.append(encodedValue);
			requestString.append(Constants.SEPARATOR);
		}
		requestString.deleteCharAt(requestString.length() - 1);
		return requestString.toString();
	}

	public String getfield43Request(String field43Fields, String payId) {
	//	Map<String, Object> userObjMap = new UserDao().getUserObjMap(payId);
		StringBuilder requestString = new StringBuilder();
		/*Collection<String> paramaters = Helper.parseFields(field43Fields);
		for (String param : paramaters) {
			String[] parameterPair = param.split(Constants.EQUATOR);
			requestString.append(parameterPair[0]);
			String value = parameterPair[1];
			requestString.append(Constants.EQUATOR);

			Field43ValueType field43ValueType = Field43ValueType.getInstanceFieldName(value);
			if (null == field43ValueType) {
				return null;
			}
			value = (String) userObjMap.get(value);
			switch (field43ValueType) {
			case SUB_MERCHANT_ID:
				if (!StringUtils.isBlank(value)) {
					value = value.substring(0, 10);
				}
				break;
			case COUNTRY:
				if (!StringUtils.isBlank(value)) {
					value = BinCountryMapperType.get3DigitCodeUsingName(value);
				}
				break;
			default:
				break;
			}
			if (StringUtils.isBlank(value)) {
				value = field43ValueType.getDummyValue();
			}
			String encodedValue;
			try {
				encodedValue = URLEncoder.encode(value, SystemConstants.DEFAULT_ENCODING_UTF_8);
			} catch (UnsupportedEncodingException unsupportedEncodingException) {
				logger.error("Exception preparing amex request" + unsupportedEncodingException);
				encodedValue = value;
			}
			requestString.append(encodedValue);
			requestString.append(Constants.SEPARATOR);
		}
*/		return requestString.toString();
	}

	public String getCaptureRequest(Fields fields) {
		StringBuilder request = new StringBuilder();

		request.append(appendDefaultParams(fields));
		request.append(Constants.SEPARATOR);
		request.append(Constants.COMMAND);
		request.append(Constants.EQUATOR);
		request.append(Constants.CAPTURE);

		return request.toString();
	}

	public String getRefundRequest(Fields fields) {
		StringBuilder request = new StringBuilder();

		request.append(appendDefaultParams(fields));
		request.append(Constants.SEPARATOR);
		request.append(Constants.COMMAND);
		request.append(Constants.EQUATOR);
		request.append(Constants.REFUND);

		return request.toString();
	}

	public String getStatusRequest(Fields fields) {
		StringBuilder request = new StringBuilder();
		if (fields.get(FieldType.ACQUIRER_TYPE.getName()).equals(AcquirerType.AXISMIGS.getCode())) {

			request.append(Constants.VERSION);
			request.append(Constants.EQUATOR);
			request.append(fields.get(FieldType.ADF1.getName()));
			request.append(Constants.SEPARATOR);
			request.append(Constants.COMMAND);
			request.append(Constants.EQUATOR);
			request.append(Constants.STATUS);
			request.append(Constants.ACCESSCODE);
			request.append(Constants.EQUATOR);
			request.append(fields.get(FieldType.PASSWORD.getName()));
			request.append(Constants.SEPARATOR);
			request.append(Constants.MERCHANT);
			request.append(Constants.EQUATOR);
			request.append(fields.get(FieldType.MERCHANT_ID.getName()));
			request.append(Constants.SEPARATOR);
			request.append(Constants.MERCHANTTXNREF);
			request.append(Constants.EQUATOR);
			request.append(fields.get(FieldType.PG_REF_NUM.getName()));
			request.append(Constants.SEPARATOR);
			request.append(Constants.USER);
			request.append(Constants.EQUATOR);
			request.append(fields.get(FieldType.ADF2.getName()));
			request.append(Constants.SEPARATOR);
			request.append(Constants.PASSWORD);
			request.append(Constants.EQUATOR);
			request.append(fields.get(FieldType.ADF3.getName()));
		} else {
			request.append(ConfigurationConstants.AMEX_EZEE_CLICK_SUPPORT_TXN_FIELDS.getValue());
			request.append(Constants.SEPARATOR);
			request.append(appendDefaultParams(fields));
			request.append(Constants.SEPARATOR);
			request.append(Constants.COMMAND);
			request.append(Constants.EQUATOR);
			request.append(Constants.STATUS);
		}

		return request.toString();
	}

	private StringBuilder appendDefaultParams(Fields fields) {

		StringBuilder request = new StringBuilder();
		// Diff params for both
		if (fields.get(FieldType.ACQUIRER_TYPE.getName()).equals(AcquirerType.AXISMIGS.getCode())) {
			request.append(Constants.VERSION);
			request.append(Constants.EQUATOR);
			request.append(fields.get(FieldType.ADF1.getName()));
			request.append(Constants.SEPARATOR);
			request.append(Constants.ACCESSCODE);
			request.append(Constants.EQUATOR);
			request.append(fields.get(FieldType.PASSWORD.getName()));
			request.append(Constants.SEPARATOR);
			request.append(Constants.MERCHANTTXNREF);
			request.append(Constants.EQUATOR);
			request.append(fields.get(FieldType.TXN_ID.getName()));
			request.append(Constants.SEPARATOR);
			request.append(Constants.MERCHANT);
			request.append(Constants.EQUATOR);
			request.append(fields.get(FieldType.MERCHANT_ID.getName()));
			request.append(Constants.SEPARATOR);
			request.append(Constants.AMOUNT);
			request.append(Constants.EQUATOR);
			request.append(fields.get(FieldType.AMOUNT.getName()));
			request.append(Constants.SEPARATOR);
			request.append(Constants.USER);
			request.append(Constants.EQUATOR);
			request.append(fields.get(FieldType.ADF2.getName()));
			request.append(Constants.SEPARATOR);
			request.append(Constants.PASSWORD);
			request.append(Constants.EQUATOR);
			request.append(fields.get(FieldType.ADF3.getName()));
			request.append(Constants.SEPARATOR);
			request.append(Constants.TRANS_NO);
			request.append(Constants.EQUATOR);
			request.append(fields.get(FieldType.ACQ_ID.getName()));

		} else {
			request.append(ConfigurationConstants.AMEX_EZEE_CLICK_SUPPORT_TXN_FIELDS.getValue());
			request.append(Constants.SEPARATOR);

			request.append(Constants.MERCHANT);
			request.append(Constants.EQUATOR);
			request.append(fields.get(FieldType.MERCHANT_ID.getName()));
			request.append(Constants.SEPARATOR);

			request.append(Constants.ACCESSCODE);
			request.append(Constants.EQUATOR);
			request.append(fields.get(FieldType.PASSWORD.getName()));
			request.append(Constants.SEPARATOR);

			request.append(Constants.MERCHANTTXNREF);
			request.append(Constants.EQUATOR);
			request.append(fields.get(FieldType.TXN_ID.getName()));
			request.append(Constants.SEPARATOR);

			request.append(Constants.AMOUNT);
			request.append(Constants.EQUATOR);
			request.append(fields.get(FieldType.AMOUNT.getName()));
			request.append(Constants.SEPARATOR);

			request.append(Constants.TRANS_NO);
			request.append(Constants.EQUATOR);
			request.append(fields.get(FieldType.ACQ_ID.getName()));
		}

		return request;
	}

	public String getEzeeClickRequest(Fields fields) throws SystemException {

		String encryptionKey = fields.get(FieldType.TXN_KEY.getName());

		String url = ConfigurationConstants.AMEX_EZEE_CLICK_TRANSACTION_URL.getValue();

		StringBuilder requestString = new StringBuilder();

		String mid = fields.get(FieldType.MERCHANT_ID.getName());
		String orderId = fields.get(FieldType.TXN_ID.getName());
		String tranAmount = fields.get(FieldType.AMOUNT.getName());
		String returnUrl = ConfigurationConstants.AMEX_EZEE_CLICK_RETURN_URL.getValue();

		requestString.append(mid);
		requestString.append("|");
		requestString.append(orderId);
		requestString.append("|");
		requestString.append(tranAmount);
		requestString.append("|");
		requestString.append(returnUrl);
		String request = "";

		request = MigsUtil.encrypt(requestString.toString(), encryptionKey);

		StringBuilder httpRequest = new StringBuilder();
		httpRequest.append("<HTML>");
		httpRequest.append("<BODY OnLoad=\"OnLoadEvent();\" >");
		httpRequest.append("<form name=\"form1\" action=\"");
		httpRequest.append(url);
		httpRequest.append("\" method=\"post\">");

		httpRequest.append("<input type=\"hidden\" name=\"merchantRequest\" value=\"");
		httpRequest.append(request);// request encrypted
		httpRequest.append("\">");
		httpRequest.append("<input type=\"hidden\" name=\"MID\" value=\"");
		httpRequest.append(mid);// MID
		httpRequest.append("\">");
		httpRequest.append("</form>");
		httpRequest.append("<script language=\"JavaScript\">");
		httpRequest.append("function OnLoadEvent()");
		httpRequest.append("{document.form1.submit();}");
		httpRequest.append("</script>");
		httpRequest.append("</BODY>");
		httpRequest.append("</HTML>");

		return httpRequest.toString();
	}

	public String getEzeeClickStatusRequest(Fields fields) throws SystemException {
		StringBuilder request = new StringBuilder();
		request.append(fields.get(FieldType.MERCHANT_ID.getName()));
		request.append(Constants.PIPE_SEPARATOR);
		request.append(fields.get(FieldType.ORIG_TXN_ID.getName()));
		request.append(Constants.PIPE_SEPARATOR);
		request.append("0");
		return MigsUtil.encrypt(request.toString(), fields.get(FieldType.TXN_KEY.getName()));
	}


}
