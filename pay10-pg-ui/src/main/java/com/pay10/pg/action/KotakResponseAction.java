package com.pay10.pg.action;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.opensymphony.xwork2.Action;
import com.pay10.commons.api.TransactionControllerServiceProvider;
import com.pay10.commons.dao.FieldsDao;
import com.pay10.commons.util.AcquirerType;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.pg.core.util.KotakNBUtils;
import com.pay10.pg.core.util.ResponseCreator;

public class KotakResponseAction  extends AbstractSecureAction implements ServletRequestAware {

	private static Logger logger = LoggerFactory.getLogger(KotakResponseAction.class.getName());
	private static final long serialVersionUID = 6155942791032490231L;

	@Autowired
	TransactionControllerServiceProvider transactionControllerServiceProvider;

	@Autowired
	private KotakNBUtils kotakNBUtils;

	@Autowired
	private ResponseCreator responseCreator;
	
	@Autowired
	private FieldsDao fieldsDao;

	private Fields responseMap = null;
	private HttpServletRequest httpRequest;

	@Override
	public void setServletRequest(HttpServletRequest hReq) {
		this.httpRequest = hReq;
	}

	public KotakResponseAction() {
	}

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
			String transData = requestMap.get("msg");
			logger.info("Response received from kotak: " + transData);
			String txnKey = (String) sessionMap.get(FieldType.TXN_KEY.getName());
			logger.info("Key from kotak: " + txnKey);
			String decrytedString = kotakNBUtils.decrypt(transData, txnKey);
			logger.info("decrytedString "+decrytedString);
			String password = (String) sessionMap.get(FieldType.PASSWORD.getName());
			
			Fields fields = new Fields();
//			fields.put(FieldType.KOTAK_RESPONSE_FIELD.getName(), decrytedString);
//			logger.info("kotak response fields..={}",FieldType.KOTAK_RESPONSE_FIELD.getName());
//			fields.put(FieldType.PASSWORD.getName(), password);
			fields.logAllFields("Kotak Response Recieved :");
			//Added By Sweety
			String pgRefNo = null;
//			for (String data : decrytedString.split("&")) {
//				if (data.contains("pgRefNo")) {
//					pgRefNo = data.split("=")[1];
//					break;
//				}
//			}
			
			String [] data=decrytedString.split("\\|");
			logger.info("data in kotak response action..={}",Arrays.asList(data));
			pgRefNo=data[3];
			logger.info("decrytedString pgRefNum "+pgRefNo);
			
			Object fieldsObj = sessionMap.get("FIELDS");
			if (null != fieldsObj) {
				fields.put((Fields) fieldsObj);
			}
			// Check if fields is empty
			if (StringUtils.isNotBlank(pgRefNo)) {
				logger.info("KOTAK FIELDS is blank in session Map for KOTAk , getting data from DB...={}",pgRefNo);

				fields = fieldsDao.getPreviousForPgRefNum(pgRefNo);

				String internalRequestFields = null;
				internalRequestFields = fieldsDao.getPreviousForOID(fields.get(FieldType.OID.getName()));

				if (StringUtils.isBlank(internalRequestFields)) {
					logger.info("KOTAK New Order entry not found for this OID , getting data from SENT TO BANK "
							+ fields.get(FieldType.OID.getName()));
					internalRequestFields = fieldsDao.getPreviousForOIDSTB(fields.get(FieldType.OID.getName()));
				} else {
					logger.info("KOTAK New Order entry found for this OID in New Order - Pending txn"
							+ fields.get(FieldType.OID.getName()));
				}

				String[] paramaters = internalRequestFields.split("~");
				Map<String, String> paramMap = new HashMap<String, String>();
				
				//db
				for (String param : paramaters) {				    
					String[] parameterPair = param.split("=");
					if (parameterPair.length > 1 ) {
					    if(parameterPair[0].trim().equalsIgnoreCase("RETURN_URL")) {
					        String[] tempArrayRetURL=param.split("=",2);
					        logger.info(" Split_RETURN_URL : "+tempArrayRetURL[0].trim()+"  :     "+tempArrayRetURL[1].trim());
					        paramMap.put(tempArrayRetURL[0].trim(), tempArrayRetURL[1].trim());
					    }else {
					        paramMap.put(parameterPair[0].trim(), parameterPair[1].trim());    
					    }
						
					}
				}
				
				/*
				 * for (String param : paramaters) { String[] parameterPair = param.split("=");
				 * if (parameterPair.length > 1) { paramMap.put(parameterPair[0].trim(),
				 * parameterPair[1].trim()); } }
				 */

				if (StringUtils.isNotBlank(paramMap.get(FieldType.RETURN_URL.getName()))) {
					logger.info("KOTAK Return URL found for ORDER ID " + paramMap.get(FieldType.ORDER_ID.getName())
							+ "Return URL >> " + paramMap.get(FieldType.RETURN_URL.getName()));
					fields.put(FieldType.RETURN_URL.getName(), paramMap.get(FieldType.RETURN_URL.getName()));
					sessionMap.put(FieldType.RETURN_URL.getName(), paramMap.get(FieldType.RETURN_URL.getName()));
				} else {
					logger.info("KOTAK Return URL not found for ORDER ID " + paramMap.get(FieldType.ORDER_ID.getName()));
				}

				if (StringUtils.isNotBlank(paramMap.get(FieldType.IS_MERCHANT_HOSTED.getName()))) {
					logger.info(
							"KOTAK IS_MERCHANT_HOSTED flag found for ORDER ID " + paramMap.get(FieldType.ORDER_ID.getName()));
					fields.put(FieldType.IS_MERCHANT_HOSTED.getName(),
							paramMap.get(FieldType.IS_MERCHANT_HOSTED.getName()));
					sessionMap.put(FieldType.IS_MERCHANT_HOSTED.getName(),
							paramMap.get(FieldType.IS_MERCHANT_HOSTED.getName()));
				} else {
					logger.info(
							"KOTAK IS_MERCHANT_HOSTED not found for ORDER ID " + paramMap.get(FieldType.ORDER_ID.getName()));
				}

			}
		
			//Added By Sweety
			fields.put(FieldType.KOTAK_RESPONSE_FIELD.getName(), decrytedString);
			logger.info("kotak response fields..={}",fields.get(FieldType.KOTAK_RESPONSE_FIELD.getName()));
			fields.put(FieldType.PASSWORD.getName(), password);
			
			fields.logAllFields("Updated 3DS Recieved Map TxnType = " + fields.get(FieldType.TXNTYPE.getName()) + " " + "Txn id = "+ fields.get(FieldType.TXN_ID.getName()));
			fields.put(FieldType.ACQUIRER_TYPE.getName(), AcquirerType.KOTAK.getCode());
			fields.put(FieldType.TXNTYPE.getName(),
					(String) sessionMap.get(FieldType.INTERNAL_ORIG_TXN_TYPE.getName()));
			fields.put(FieldType.INTERNAL_VALIDATE_HASH_YN.getName(), "N");
			fields.put((FieldType.PAYMENTS_REGION.getName()),
					(String) sessionMap.get(FieldType.PAYMENTS_REGION.getName()));
			fields.put((FieldType.CARD_HOLDER_TYPE.getName()),
					(String) sessionMap.get(FieldType.CARD_HOLDER_TYPE.getName()));
			fields.put((FieldType.ADF2.getName()),
					(String) sessionMap.get(FieldType.ADF2.getName()));
			fields.put((FieldType.ADF3.getName()),
					(String) sessionMap.get(FieldType.ADF3.getName()));
		
			fields.put((FieldType.ADF4.getName()),
					(String) sessionMap.get(FieldType.ADF4.getName()));
			fields.put((FieldType.ADF5.getName()),
					(String) sessionMap.get(FieldType.ADF5.getName()));
		
			fields.put((FieldType.ADF10.getName()),
					(String) sessionMap.get(FieldType.ADF10.getName()));
			fields.put((FieldType.MERCHANT_ID.getName()),
					(String) sessionMap.get(FieldType.MERCHANT_ID.getName()));
		
			logger.info("feilds in kotak actionresponse"+fields.getFieldsAsString());
			//fields.put((FieldType.OID.getName()), (String) sessionMap.get(FieldType.OID.getName()));
			if (StringUtils.isNotBlank((String) sessionMap.get(FieldType.OID.getName()))) {
				fields.put((FieldType.OID.getName()), (String) sessionMap.get(FieldType.OID.getName()));

			}
			Map<String, String> response = transactionControllerServiceProvider.transact(fields,
					Constants.TXN_WS_KOTAK_PROCESSOR.getValue());
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
