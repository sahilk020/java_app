package com.pay10.pg.action;

import java.security.spec.KeySpec;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.opensymphony.xwork2.Action;
import com.pay10.commons.api.TransactionControllerServiceProvider;
import com.pay10.commons.dao.FieldsDao;
import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.exception.SystemException;
import com.pay10.commons.user.AccountCurrencyDao;
import com.pay10.commons.user.User;
import com.pay10.commons.user.UserDao;
import com.pay10.commons.util.AcquirerType;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.CrmFieldConstants;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.pg.action.service.RetryTransactionProcessor;
import com.pay10.pg.core.util.DirecPayEncDecUtil;
import com.pay10.pg.core.util.ResponseCreator;

/**
 * @author Shaiwal
 *
 */
public class DirecpayResponseAction extends AbstractSecureAction implements ServletRequestAware {

	private static Logger logger = LoggerFactory.getLogger(DirecpayResponseAction.class.getName());
	private static final long serialVersionUID = 6155942791032490231L;
	private static byte[] SALT  = new byte[] { -57, -75, -103, -12, 75, 124, -127, 119 };
	private static Map<String,SecretKey> encDecMap = new HashMap<String,SecretKey>();

	@Autowired
	TransactionControllerServiceProvider transactionControllerServiceProvider;

	@Autowired
	private ResponseCreator responseCreator;

	private Fields responseMap = null;
	private HttpServletRequest httpRequest;
	
	@Autowired
	private UserDao userDao;
	
	@Autowired
	private RetryTransactionProcessor retryTransactionProcessor;
	
	@Autowired
	private FieldsDao fieldsDao;
	
	@Override
	public void setServletRequest(HttpServletRequest hReq) {
		this.httpRequest = hReq;
	}

	public DirecpayResponseAction() {
	}

	@Override
	public String execute() {
		try {
			Map<String, String[]> fieldMapObj = httpRequest.getParameterMap();
			Map<String, String> requestMap = new HashMap<String, String>();

			for (Entry<String, String[]> entry : fieldMapObj.entrySet()) {
				try {
					requestMap.put(entry.getKey(), entry.getValue()[0]);

				} catch (ClassCastException classCastException) {
					logger.error("Exception", classCastException);
				}
			}
			
			
			// Log all entries from requestMap

			if (!requestMap.isEmpty()) {

				StringBuilder sb = new StringBuilder();
				Iterator itr = requestMap.entrySet().iterator();
				while (itr.hasNext()) {
					Map.Entry obj = (Entry) itr.next();
					sb.append(obj.getKey() + " = " + obj.getValue() + " ~");
				}
				logger.info("Response Map received for Direcpay >>> " + sb.toString());
			} else {
				logger.info("Response Map is empty for Direcpay ");
			}

			// Log all entries from sessionMap

			if (!sessionMap.isEmpty()) {

				StringBuilder sb = new StringBuilder();
				Iterator itr = sessionMap.entrySet().iterator();
				while (itr.hasNext()) {
					Map.Entry obj = (Entry) itr.next();
					if (obj.getKey().toString().equalsIgnoreCase(FieldType.TXN_KEY.getName())) {
						logger.info("Txn Key Present in Session map");
						continue;
					}
					sb.append(obj.getKey() + " = " + obj.getValue() + " ~");
				}
				logger.info("sessionMap values for Direcpay>>> " + sb.toString());
			} else {
				logger.info("Session Map is empty for Direcpay");
			}

			String flag = requestMap.get("flag");
			String merchantId = requestMap.get("merchantId");
			String responseparams = requestMap.get("responseparams");
			
			String responseparamsArray [] = responseparams.split(Pattern.quote("||"));
			String encryptedResponse = responseparamsArray[1];
			
			logger.info("Encrypted Response for Direcpay >> " + encryptedResponse);
			
			String encryptionKey = null ;
			encryptionKey = (String) sessionMap.get(FieldType.TXN_KEY.getName());
			
			if (StringUtils.isBlank(encryptionKey)) {
				logger.info("Encryption key NOT FOUND in session map for Direcpay");
				encryptionKey = getTxnKey(merchantId);
				
				if (StringUtils.isNotBlank(encryptionKey)) 
				logger.info("Encryption key FOUND from db for Direcpay");
			}
			else {
				logger.info("Encryption key FOUND in session map for Direcpay");
			}
			
			SecretKey secretKey = null;
			
			if (encDecMap.get(encryptionKey) != null) {
				secretKey = encDecMap.get(encryptionKey);
			}
			else {
				try {
		            SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
		            KeySpec keySpec = new PBEKeySpec(encryptionKey.toCharArray(), SALT, 65536, 256);
		            SecretKey secretKeyTemp = secretKeyFactory.generateSecret(keySpec);
		            secretKey = new SecretKeySpec(secretKeyTemp.getEncoded(), "AES");
		    	}
		    	catch(Exception e) {
		    		logger.info("Exception in creating decryption key for direcpay",e);
		    	}
				encDecMap.put(encryptionKey, secretKey);
			}
		 
			DirecPayEncDecUtil aesDecrypt = new	DirecPayEncDecUtil(encryptionKey, encryptedResponse,secretKey);
			String decryptedResponse = aesDecrypt.decrypt();
			logger.info("Direcpay decrypted response >> " + decryptedResponse);
			
			Fields fields = new Fields();
			Object fieldsObj = sessionMap.get("FIELDS");
			if (null != fieldsObj) {
				fields.put((Fields) fieldsObj);
			}
			
			// Check if fields is empty
			if (StringUtils.isBlank(fields.get(FieldType.PAY_ID.getName()))) {
				logger.info("FIELDS is blank in session Map, getting data from DB");
				
				// Get PG REF NUM from Response
				String TxnRefNo = "";
				String decRespparamsArray [] = decryptedResponse.split(Pattern.quote("||"));
				String response_TxnData = "";
				response_TxnData = decRespparamsArray[1];
				String response_TxnDataArray [] = response_TxnData.split(Pattern.quote("|"));
				
				if (response_TxnDataArray [0].equalsIgnoreCase("111101")) {
					TxnRefNo = response_TxnDataArray[1];
				}
				
				fields = fieldsDao.getPreviousForPgRefNum(TxnRefNo);
				
				String internalRequestFields = null;
				internalRequestFields = fieldsDao.getPreviousForOID(fields.get(FieldType.OID.getName()));
				
				if (StringUtils.isBlank(internalRequestFields)) {
					logger.info("New Order entry not found for this OID , getting data from SENT TO BANK " + fields.get(FieldType.OID.getName()));
					internalRequestFields = fieldsDao.getPreviousForOIDSTB(fields.get(FieldType.OID.getName()));
				}
				else {
					logger.info("New Order entry found for this OID in New Order - Pending txn" + fields.get(FieldType.OID.getName()));
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
					logger.info("Return URL found for ORDER ID " + paramMap.get(FieldType.ORDER_ID.getName())+ "Return URL >> "+paramMap.get(FieldType.RETURN_URL.getName()));
					fields.put(FieldType.RETURN_URL.getName(), paramMap.get(FieldType.RETURN_URL.getName()));
					sessionMap.put(FieldType.RETURN_URL.getName(), paramMap.get(FieldType.RETURN_URL.getName()));
				}
				else {
					logger.info("Return URL not found for ORDER ID " + paramMap.get(FieldType.ORDER_ID.getName()));
				}
				
				if (StringUtils.isNotBlank(paramMap.get(FieldType.IS_MERCHANT_HOSTED.getName()))) {
					logger.info("IS_MERCHANT_HOSTED flag found for ORDER ID " + paramMap.get(FieldType.ORDER_ID.getName()));
					fields.put(FieldType.IS_MERCHANT_HOSTED.getName(), paramMap.get(FieldType.IS_MERCHANT_HOSTED.getName()));
					sessionMap.put(FieldType.IS_MERCHANT_HOSTED.getName(),paramMap.get(FieldType.IS_MERCHANT_HOSTED.getName()));
				}
				else {
					logger.info("IS_MERCHANT_HOSTED not found for ORDER ID " + paramMap.get(FieldType.ORDER_ID.getName()));
				}
				
			}
			
			fields.put(FieldType.DIRECPAY_RESPONSE_FIELD.getName(), decryptedResponse);
			fields.logAllFields("Direcpay Response Recieved :");

			fields.logAllFields("Direcpay response = " + fields.get(FieldType.TXNTYPE.getName()) + " " + "OrderId = "+ fields.get(FieldType.ORDER_ID.getName()));
			
			fields.put(FieldType.ACQUIRER_TYPE.getName(), AcquirerType.DIRECPAY.getCode());
			
			if (StringUtils.isNotBlank((String) sessionMap.get(FieldType.INTERNAL_ORIG_TXN_TYPE.getName()))) {
				fields.put(FieldType.TXNTYPE.getName(),
						(String) sessionMap.get(FieldType.INTERNAL_ORIG_TXN_TYPE.getName()));
			}
			
			if (StringUtils.isNotBlank((String) sessionMap.get(FieldType.PAYMENTS_REGION.getName()))) {
				fields.put((FieldType.PAYMENTS_REGION.getName()),
						(String) sessionMap.get(FieldType.PAYMENTS_REGION.getName()));
			}

			if (StringUtils.isNotBlank((String) sessionMap.get(FieldType.CARD_HOLDER_TYPE.getName()))) {
				fields.put((FieldType.CARD_HOLDER_TYPE.getName()),
						(String) sessionMap.get(FieldType.CARD_HOLDER_TYPE.getName()));
			}

			if (StringUtils.isNotBlank((String) sessionMap.get(FieldType.OID.getName()))) {
				fields.put((FieldType.OID.getName()), (String) sessionMap.get(FieldType.OID.getName()));

			}
			
			if (StringUtils.isNotBlank(fields.get(FieldType.INTERNAL_CARD_ISSUER_BANK.getName()))) {
				sessionMap.put(FieldType.INTERNAL_CARD_ISSUER_BANK.getName(),fields.get(FieldType.INTERNAL_CARD_ISSUER_BANK.getName()));
			}
			
			if (StringUtils.isNotBlank(fields.get(FieldType.INTERNAL_CARD_ISSUER_COUNTRY.getName()))) {
				sessionMap.put(FieldType.INTERNAL_CARD_ISSUER_COUNTRY.getName(),fields.get(FieldType.INTERNAL_CARD_ISSUER_COUNTRY.getName()));
			}
			
			fields.put(FieldType.INTERNAL_VALIDATE_HASH_YN.getName(), "N");
			
			Map<String, String> response = transactionControllerServiceProvider.transact(fields,
					Constants.TXN_WS_DIRECPAY_PROCESSOR.getValue());
			responseMap = new Fields(response);
			
			String crisFlag = (String) sessionMap.get(FieldType.INTERNAL_IRCTC_YN.getName());
			if(StringUtils.isNotBlank(crisFlag)){
				responseMap.put(FieldType.INTERNAL_IRCTC_YN.getName(), crisFlag);
			}
			String isMerchantHosted = (String) sessionMap.get(FieldType.IS_MERCHANT_HOSTED.getName());
			if(StringUtils.isNotBlank(isMerchantHosted)){
				responseMap.put(FieldType.IS_MERCHANT_HOSTED.getName(), isMerchantHosted);
			}
			// Fetch user for retryTransaction ,SendEmailer and SmsSenser
			User user = userDao.getUserClass(responseMap.get(FieldType.PAY_ID.getName()));

			// Retry Transaction Block Start
			if (!responseMap.get(FieldType.RESPONSE_CODE.getName()).equals(ErrorType.SUCCESS.getCode())) {
				if (retryTransactionProcessor.retryTransaction(responseMap, sessionMap, user, fields)) {
					sessionMap.put(Constants.RETRY_MESSAGE.getValue(), CrmFieldConstants.RETRY_TRANSACTION.getValue());
					return "surchargePaymentPage";
				}
			}
			
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
			responseMap.remove(FieldType.IS_INTERNAL_REQUEST.getName());
			responseCreator.create(responseMap);
			responseCreator.ResponsePost(responseMap);

		} catch (Exception exception) {
			logger.error("Exception", exception);
			return ERROR;
		}
		return Action.NONE;
	}
	
	public String getTxnKey(String merchantId) throws SystemException {

		String txnKey = new AccountCurrencyDao().findByMid(merchantId);
		return txnKey;

	}
	
}
