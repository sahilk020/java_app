package com.pay10.pg.action;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
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
import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.exception.SystemException;
import com.pay10.commons.kms.AWSEncryptDecryptService;
import com.pay10.commons.user.Account;
import com.pay10.commons.user.AccountCurrency;
import com.pay10.commons.user.User;
import com.pay10.commons.user.UserDao;
import com.pay10.commons.util.AcquirerType;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.CrmFieldConstants;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.pg.action.service.RetryTransactionProcessor;
import com.pay10.pg.core.util.ResponseCreator;

/**
 * @author Rohit
 *
 */
public class PayuResponseAction extends AbstractSecureAction implements ServletRequestAware {

	private static Logger logger = LoggerFactory.getLogger(PayuResponseAction.class.getName());
	private static final long serialVersionUID = 6153922691432498231L;

	@Autowired
	TransactionControllerServiceProvider transactionControllerServiceProvider;

	@Autowired
	private ResponseCreator responseCreator;

	@Autowired
	private UserDao userDao;

	@Autowired
	private RetryTransactionProcessor retryTransactionProcessor;

	@Autowired
	private FieldsDao fieldsDao;
	
	@Autowired
	private AWSEncryptDecryptService aWSEncryptDecryptService;
	

	private Fields responseMap = null;
	private HttpServletRequest httpRequest;

	@Override
	public void setServletRequest(HttpServletRequest hReq) {
		this.httpRequest = hReq;
	}

	public PayuResponseAction() {
	}

	@Override
	public String execute() {
		try {

			String txnId = null;
			Map<String, String[]> fieldMapObj = httpRequest.getParameterMap();
			logger.info("response from payu"+fieldMapObj);
			Map<String, String> requestMap = new HashMap<String, String>();
			StringBuilder responseString = new StringBuilder();

			for (Entry<String, String[]> entry : fieldMapObj.entrySet()) {
				try {
					requestMap.put(entry.getKey(), entry.getValue()[0]);
					responseString.append(entry.getKey());
					responseString.append("=");
					responseString.append(entry.getValue()[0]);
					responseString.append(";");

					if (entry.getKey().equalsIgnoreCase("txnId")) {
						txnId = entry.getValue()[0];
					}

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
				logger.info("PAYU Request Map received >>> " + sb.toString());
			} else {
				logger.info("PAYU Request Map is empty ");
			}

			// Log all entries from sessionMap
			if (!sessionMap.isEmpty()) {

				StringBuilder sb = new StringBuilder();
				Iterator itr = sessionMap.entrySet().iterator();
				while (itr.hasNext()) {
					Map.Entry obj = (Entry) itr.next();
					sb.append(obj.getKey() + " = " + obj.getValue() + " ~");
				}
				logger.info("PAYU sessionMap values >>> " + sb.toString());
			} else {
				logger.info("PAYU Session Map is empty");
			}

			Fields fields = new Fields();

			Object fieldsObj = sessionMap.get("FIELDS");
			if (null != fieldsObj) {
				fields.put((Fields) fieldsObj);
			}

			// Check if fields is empty
			//if (StringUtils.isBlank(fields.get(FieldType.PAY_ID.getName()))) {
			if (StringUtils.isNotBlank(txnId)) {
				logger.info("PAYU  FIELDS is getting data from DB :"+txnId);

				fields = fieldsDao.getPreviousForPgRefNum(txnId);
				String internalRequestFields = null;
				internalRequestFields = fieldsDao.getPreviousForOID(fields.get(FieldType.OID.getName()));

				if (StringUtils.isBlank(internalRequestFields)) {
					logger.info("PAYU New Order entry not found for this OID , getting data from SENT TO BANK "
							+ fields.get(FieldType.OID.getName()));
					internalRequestFields = fieldsDao.getPreviousForOIDSTB(fields.get(FieldType.OID.getName()));
				} else {
					logger.info("PAYU New Order entry found for this OID in New Order - Pending txn"
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
					logger.info("PAYU Return URL found for ORDER ID " + paramMap.get(FieldType.ORDER_ID.getName())
							+ "Return URL >> " + paramMap.get(FieldType.RETURN_URL.getName()));
					fields.put(FieldType.RETURN_URL.getName(), paramMap.get(FieldType.RETURN_URL.getName()));
					sessionMap.put(FieldType.RETURN_URL.getName(), paramMap.get(FieldType.RETURN_URL.getName()));
				} else {
					logger.info(
							"PAYU Return URL not found for ORDER ID " + paramMap.get(FieldType.ORDER_ID.getName()));
				}

				if (StringUtils.isNotBlank(paramMap.get(FieldType.IS_MERCHANT_HOSTED.getName()))) {
					logger.info("PAYU IS_MERCHANT_HOSTED flag found for ORDER ID "
							+ paramMap.get(FieldType.ORDER_ID.getName()));
					fields.put(FieldType.IS_MERCHANT_HOSTED.getName(),
							paramMap.get(FieldType.IS_MERCHANT_HOSTED.getName()));
					sessionMap.put(FieldType.IS_MERCHANT_HOSTED.getName(),
							paramMap.get(FieldType.IS_MERCHANT_HOSTED.getName()));
				} else {
					logger.info("PAYU IS_MERCHANT_HOSTED not found for ORDER ID "
							+ paramMap.get(FieldType.ORDER_ID.getName()));
				}

			}

			fields.put(FieldType.PAYU_RESPONSE_FIELD.getName(), responseString.toString());
			fields.logAllFields("Payu Response Recieved :");

			fields.logAllFields("Updated 3DS Recieved Map TxnType = " + fields.get(FieldType.TXNTYPE.getName()) + " "
					+ "Txn id = " + fields.get(FieldType.TXN_ID.getName()));
			
			logger.info("PASSWORD" + sessionMap.get(FieldType.PASSWORD.getName()));
			logger.info("MERCHANT_ID " + sessionMap.get(FieldType.MERCHANT_ID.getName()));
			logger.info("txn_key " + sessionMap.get(FieldType.TXN_KEY.getName()));
			logger.info("salt" + sessionMap.get(FieldType.ADF3.getName()));
			fields.put(FieldType.TXN_KEY.getName(), (String) sessionMap.get(FieldType.TXN_KEY.getName()));
			fields.put(FieldType.ADF3.getName(), (String) sessionMap.get(FieldType.ADF3.getName()));
			if (StringUtils.isBlank((String) sessionMap.get(FieldType.PASSWORD.getName())) || 
					StringUtils.isBlank((String) sessionMap.get(FieldType.MERCHANT_ID.getName()))) {
				
				logger.info("PAYU Merchant Id and password NOT FOUND in session map for payu");
				Map<String,String> keyMap = getTxnKey(fields);
				
				if (StringUtils.isNotBlank(keyMap.get("merchantId"))) {
					logger.info("PAYU Merchant Id found from DB");
				}
				if (StringUtils.isNotBlank(keyMap.get("password"))) {
					logger.info("PAYU password found from DB");
				}
				if (StringUtils.isNotBlank(keyMap.get("salt"))) {
					logger.info("PAYU salt found from DB");
				}
				if (StringUtils.isNotBlank(keyMap.get("txnKey"))) {
					logger.info("PAYU txnKey found from DB");
				}
				
				fields.put(FieldType.PASSWORD.getName(), keyMap.get("password"));
				fields.put(FieldType.MERCHANT_ID.getName(), keyMap.get("merchantId"));
				fields.put(FieldType.ADF3.getName(), keyMap.get("salt"));
				fields.put(FieldType.TXN_KEY.getName(), keyMap.get("txnKey"));
			}
			else {
				
				logger.info("Merchant Id and password FOUND in session map for payu");
				fields.put(FieldType.PASSWORD.getName(), (String) sessionMap.get(FieldType.PASSWORD.getName()));
				fields.put(FieldType.MERCHANT_ID.getName(), (String) sessionMap.get(FieldType.MERCHANT_ID.getName()));
				
			}
			
			
//			fields.put(FieldType.CUST_NAME.getName(), (String) sessionMap.get(FieldType.CUST_NAME.getName()));
			
			fields.put(FieldType.ACQUIRER_TYPE.getName(), AcquirerType.PAYU.getCode());
			fields.logAllFields("Field pass to PayuSaleResponseHandler : "+fields.getFieldsAsString());
			
			if (StringUtils.isNotBlank((String) sessionMap.get(FieldType.INTERNAL_ORIG_TXN_TYPE.getName()))) {
				fields.put(FieldType.TXNTYPE.getName(),
						(String) sessionMap.get(FieldType.INTERNAL_ORIG_TXN_TYPE.getName()));
			}
			
			if (StringUtils.isNotBlank((String) sessionMap.get(FieldType.PAYMENTS_REGION.getName()))) {
				fields.put((FieldType.PAYMENTS_REGION.getName()),
						(String) sessionMap.get(FieldType.PAYMENTS_REGION.getName()));
			}
			if (StringUtils.isNotBlank((String) sessionMap.get(FieldType.ADF3.getName()))) {
				fields.put(FieldType.ADF3.getName(),
						(String) sessionMap.get(FieldType.ADF3.getName()));
			}
			
			if (StringUtils.isNotBlank((String) sessionMap.get(FieldType.TXN_KEY.getName()))) {
				fields.put((FieldType.TXN_KEY.getName()),
						(String) sessionMap.get(FieldType.TXN_KEY.getName()));
			}

			if (StringUtils.isNotBlank((String) sessionMap.get(FieldType.CARD_HOLDER_TYPE.getName()))) {
				fields.put((FieldType.CARD_HOLDER_TYPE.getName()),
						(String) sessionMap.get(FieldType.CARD_HOLDER_TYPE.getName()));
			}
			
			if (StringUtils.isNotBlank((String) sessionMap.get(FieldType.OID.getName()))) {
				fields.put((FieldType.OID.getName()), (String) sessionMap.get(FieldType.OID.getName()));

			}
			
			fields.put(FieldType.INTERNAL_VALIDATE_HASH_YN.getName(), "N");
			
			if (StringUtils.isNotBlank(fields.get(FieldType.INTERNAL_CARD_ISSUER_BANK.getName()))) {
				sessionMap.put(FieldType.INTERNAL_CARD_ISSUER_BANK.getName(),fields.get(FieldType.INTERNAL_CARD_ISSUER_BANK.getName()));
			}
			
			if (StringUtils.isNotBlank(fields.get(FieldType.INTERNAL_CARD_ISSUER_COUNTRY.getName()))) {
				sessionMap.put(FieldType.INTERNAL_CARD_ISSUER_COUNTRY.getName(),fields.get(FieldType.INTERNAL_CARD_ISSUER_COUNTRY.getName()));
			}
			
			Map<String, String> response = transactionControllerServiceProvider.transact(fields,
					Constants.TXN_WS_PAYU_PROCESSOR.getValue());
			responseMap = new Fields(response);

			String crisFlag = (String) sessionMap.get(FieldType.INTERNAL_IRCTC_YN.getName());
			if (StringUtils.isNotBlank(crisFlag)) {
				responseMap.put(FieldType.INTERNAL_IRCTC_YN.getName(), crisFlag);
			}
			String isMerchantHosted = (String) sessionMap.get(FieldType.IS_MERCHANT_HOSTED.getName());
			if (StringUtils.isNotBlank(isMerchantHosted)) {
				responseMap.put(FieldType.IS_MERCHANT_HOSTED.getName(), isMerchantHosted);
			}
			// Fetch user for retryTransaction ,SendEmailer and SmsSenser
			User user = userDao.getUserClass(responseMap.get(FieldType.PAY_ID.getName()));

			logger.info("Payu Response Before Retry Option, ResponseMap = {} ",responseMap);
			// Retry Transaction Block Start
			if (!responseMap.get(FieldType.RESPONSE_CODE.getName()).equals(ErrorType.SUCCESS.getCode())) {
				if (retryTransactionProcessor.retryTransaction(responseMap, sessionMap, user, fields)) {
					logger.info("Payu Retry Transactions");
					sessionMap.put(Constants.RETRY_MESSAGE.getValue(), CrmFieldConstants.RETRY_TRANSACTION.getValue());
					return "surchargePaymentPage";
				}
			}

			fields.put(FieldType.RETURN_URL.getName(), (String) sessionMap.get(FieldType.RETURN_URL.getName()));
			String cardIssuerBank = (String) sessionMap.get(FieldType.INTERNAL_CARD_ISSUER_BANK.getName());
			String cardIssuerCountry = (String) sessionMap.get(FieldType.INTERNAL_CARD_ISSUER_COUNTRY.getName());
			if (StringUtils.isNotBlank(cardIssuerBank)) {
				responseMap.put(FieldType.CARD_ISSUER_BANK.getName(), cardIssuerBank);
			}
			if (StringUtils.isNotBlank(cardIssuerCountry)) {
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
			responseMap.remove(FieldType.PASSWORD.getName());
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
	
	public Map<String,String> getTxnKey(Fields fields) throws SystemException {

		
		Map<String,String> keyMap = new HashMap<String,String>();
		
		User user = userDao.findPayId(fields.get(FieldType.PAY_ID.getName()));
		Account account = null;
		Set<Account> accounts = user.getAccounts();

		if (accounts == null || accounts.size() == 0) {
			logger.info("No account found for Pay ID = " + fields.get(FieldType.PAY_ID.getName()) + " and ORDER ID = "
					+ fields.get(FieldType.ORDER_ID.getName()));
		} else {
			for (Account accountThis : accounts) {
				if (accountThis.getAcquirerName()
						.equalsIgnoreCase(AcquirerType.getInstancefromCode(AcquirerType.PAYU.getCode()).getName())) {
					account = accountThis;
					break;
				}
			}
		}

		AccountCurrency accountCurrency = account.getAccountCurrency(fields.get(FieldType.CURRENCY_CODE.getName()));
		
		keyMap.put("merchantId", accountCurrency.getMerchantId());
		//keyMap.put("password", aWSEncryptDecryptService.decrypt(accountCurrency.getPassword()));
		keyMap.put("password", accountCurrency.getAdf2());
		keyMap.put("salt", accountCurrency.getAdf2());
		keyMap.put("txnKey", accountCurrency.getTxnKey());
		
		return keyMap;

	}
}
