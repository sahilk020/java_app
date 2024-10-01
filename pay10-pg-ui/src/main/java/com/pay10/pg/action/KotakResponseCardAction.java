package com.pay10.pg.action;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.gson.Gson;
import com.opensymphony.xwork2.Action;
import com.pay10.commons.api.TransactionControllerServiceProvider;
import com.pay10.commons.dao.FieldsDao;
import com.pay10.commons.util.AcquirerType;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.pg.core.util.KotakCardCheckSum;
import com.pay10.pg.core.util.KotakNBUtils;
import com.pay10.pg.core.util.ResponseCreator;

public class KotakResponseCardAction extends AbstractSecureAction implements ServletRequestAware {

	private static Logger logger = LoggerFactory.getLogger(KotakResponseAction.class.getName());
	private static final long serialVersionUID = 6155942791032490231L;

	@Autowired
	TransactionControllerServiceProvider transactionControllerServiceProvider;

	@Autowired
	private KotakCardCheckSum kotakCardCheckSum;

	@Autowired
	private ResponseCreator responseCreator;

	private Fields responseMap = null;
	private HttpServletRequest httpRequest;
@Override
	public void setServletRequest(HttpServletRequest hReq) {
		this.httpRequest = hReq;
	}

	public KotakResponseCardAction() {
	}
	
	@Autowired
	private FieldsDao fieldsDao;
	
	@Override
	public String execute() {
		try {
			logger.info("Response REceived For Kotak Bank");
			Map<String, String[]> fieldMapObj = httpRequest.getParameterMap();
			Map<String, String> requestMap = new HashMap<String, String>();

			for (Entry<String, String[]> entry : fieldMapObj.entrySet()) {
				try {
					requestMap.put(entry.getKey(), entry.getValue()[0]);

				} catch (ClassCastException classCastException) {
					logger.error("Exception", classCastException);
				}
			}

			logger.info("========= requestMap =========== "+requestMap.toString());
		
			
			
		
			
			Fields fields = new Fields();
		//	fields.put(FieldType.PASSWORD.getName(), password);
			fields.logAllFields("Kotak Response Recieved :");
			fields = fieldsDao.getPrivousFieldsByOderId(requestMap.get("OrderId"));
fields.put(FieldType.KOTAK_RESPONSE_FIELD.getName(),requestMap.toString() );
			String internalRequestFields=fields.get(FieldType.INTERNAL_REQUEST_FIELDS.getName());
			String[] paramaters = internalRequestFields.split("~");
			Map<String, String> paramMap = new HashMap<String, String>();
			for (String param : paramaters) {
				String[] parameterPair = param.split("=");
				if (parameterPair.length > 1) {
					paramMap.put(parameterPair[0].trim(), parameterPair[1].trim());
				}
			}
			String returnurl = null;
			if (StringUtils.isNotEmpty(paramMap.get(FieldType.RETURN_URL.getName()))){
				 returnurl=paramMap.get(FieldType.RETURN_URL.getName());

			}

			fields.logAllFields("Updated 3DS Recieved Map TxnType = " + fields.get(FieldType.TXNTYPE.getName()) + " " + "Txn id = "+ fields.get(FieldType.TXN_ID.getName()));
			fields.put(FieldType.ACQUIRER_TYPE.getName(), AcquirerType.KOTAK_CARD.getCode());
	
			fields.put(FieldType.INTERNAL_VALIDATE_HASH_YN.getName(), "N");
			
			
			logger.info("feilds in kotak actionresponse"+fields.getFieldsAsString());
			Map<String, String> response = transactionControllerServiceProvider.transact(fields,
					Constants.TXN_WS_KOTAK_CARD_PROCESSOR.getValue());
			responseMap = new Fields(response);
			
			String crisFlag = (String) sessionMap.get(FieldType.INTERNAL_IRCTC_YN.getName());
			if(StringUtils.isNotBlank(crisFlag)){
				responseMap.put(FieldType.INTERNAL_IRCTC_YN.getName(), crisFlag);
			}

			// Fetch user for retryTransaction ,SendEmailer and SmsSenser
			/*User user = userDao.getUserClass(responseMap.get(FieldType.PAY_ID.getName()));

			// Retry Transaction Block Start
			if (!responseMap.get(FieldType.RESPONSE_CODE.getName()).equals(ErrorType.SUCCESS.getCode())) {

				if (retryTransactionProcessor.retryTransaction(responseMap, sessionMap, user)) {
					addActionMessage(CrmFieldConstants.RETRY_TRANSACTION.getValue());
					return "paymentPage";
				}

			}*/

			/*
			 * Object previousFields =
			 * sessionMap.get(Constants.FIELDS.getValue()); Fields sessionFields
			 * = null; if (null != previousFields) { sessionFields = (Fields)
			 * previousFields; } else { // TODO: Handle }
			 * sessionFields.put(responseMap);
			 */
			// Retry Transaction Block End
			// Sending Email for Transaction Status to merchant TODO...
			/*
			 * String countryCode = (String)
			 * sessionMap.get(FieldType.INTERNAL_CUST_COUNTRY_NAME.getName());
			 * emailBuilder.postMan(responseMap, countryCode, user);
			 */

			fields.put(FieldType.RETURN_URL.getName(), (String) sessionMap.get(FieldType.RETURN_URL.getName()));
			String cardIssuerBank = (String) sessionMap.get(FieldType.INTERNAL_CARD_ISSUER_BANK.getName());
			String cardIssuerCountry = (String) sessionMap.get(FieldType.INTERNAL_CARD_ISSUER_COUNTRY.getName());
			if(StringUtils.isNotBlank(cardIssuerBank)){
				responseMap.put(FieldType.CARD_ISSUER_BANK.getName(), cardIssuerBank);
			}
			
			if (StringUtils.isNotBlank(returnurl)) {
				responseMap.put(FieldType.RETURN_URL.getName(), returnurl);
			}
			if(StringUtils.isNotBlank(cardIssuerCountry)){
				responseMap.put(FieldType.CARD_ISSUER_COUNTRY.getName(), cardIssuerCountry);
			}
			
			responseMap.put(FieldType.INTERNAL_SHOPIFY_YN.getName(),
					(String) sessionMap.get(FieldType.INTERNAL_SHOPIFY_YN.getName()));
			if (sessionMap != null) {
				sessionMap.put(Constants.TRANSACTION_COMPLETE_FLAG.getValue(), Constants.Y_FLAG.getValue());
				sessionMap.invalidate();
			}
			responseMap.remove(FieldType.HASH.getName());
			responseMap.remove(FieldType.TXN_KEY.getName());
			responseMap.remove(FieldType.ACQUIRER_TYPE.getName());
			responseMap.remove(FieldType.PASSWORD.getName());
			responseMap.remove(FieldType.IS_INTERNAL_REQUEST.getName());
			responseCreator.create(responseMap);
			responseCreator.ResponsePost(responseMap);

		} catch (Exception exception) {
			logger.error("Exception", exception);
			return ERROR;
		}
		return Action.NONE;
	}
	
	

}

