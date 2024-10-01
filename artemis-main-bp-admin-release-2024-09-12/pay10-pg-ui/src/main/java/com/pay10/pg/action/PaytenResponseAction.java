package com.pay10.pg.action;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.pay10.commons.api.TransactionControllerServiceProvider;
import com.pay10.commons.dao.FieldsDao;
import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.exception.SystemException;
import com.pay10.commons.kms.AWSEncryptDecryptService;
import com.pay10.commons.user.Account;
import com.pay10.commons.user.AccountCurrency;
import com.pay10.commons.user.AccountCurrencyDao;
import com.pay10.commons.user.User;
import com.pay10.commons.user.UserDao;
import com.pay10.commons.util.AcquirerType;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.CrmFieldConstants;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.pg.action.service.RetryTransactionProcessor;
import com.pay10.pg.core.util.ResponseCreator;
import com.pay10.pg.core.util.ScramblerCustom;
import com.opensymphony.xwork2.Action;

public class PaytenResponseAction extends AbstractSecureAction implements ServletRequestAware {

	private static Logger logger = LoggerFactory.getLogger(PaytenResponseAction.class.getName());
	private static final long serialVersionUID = 6155942791032490231L;

	@Autowired
	TransactionControllerServiceProvider transactionControllerServiceProvider;

	@Autowired
	private ResponseCreator responseCreator;

	@Autowired
	private UserDao userDao;

	@Autowired
	private FieldsDao fieldsDao;

	@Autowired
	private AWSEncryptDecryptService aWSEncryptDecryptService;
	
	@Autowired
	private RetryTransactionProcessor retryTransactionProcessor;
	private Fields responseMap = null;
	private HttpServletRequest httpRequest;

	@Override
	public void setServletRequest(HttpServletRequest hReq) {
		this.httpRequest = hReq;
	}

	public PaytenResponseAction() {
	}

	@Override
	public String execute() {
		try {

			String transactionId = null;

			Map<String, String[]> fieldMapObj = httpRequest.getParameterMap();
			Map<String, String> requestMap = new HashMap<String, String>();
			StringBuilder responseString = new StringBuilder();

			for (Entry<String, String[]> entry : fieldMapObj.entrySet()) {
				try {
					requestMap.put(entry.getKey(), entry.getValue()[0]);
					responseString.append(entry.getKey());
					responseString.append("=");
					responseString.append(entry.getValue()[0]);
					responseString.append(";");

					if (entry.getKey().equalsIgnoreCase("transactionId")) {
						transactionId = entry.getValue()[0];
					}

				} catch (ClassCastException classCastException) {
					logger.error("Exception", classCastException);
				}
			}

			logger.info("transactionId Recieved >>> " + transactionId);
			// Log all entries from requestMap

			if (!requestMap.isEmpty()) {

				StringBuilder sb = new StringBuilder();
				Iterator itr = requestMap.entrySet().iterator();
				while (itr.hasNext()) {
					Map.Entry obj = (Entry) itr.next();
					sb.append(obj.getKey() + " = " + obj.getValue() + " ~");
				}
				logger.info("Payten Request Map received >>> " + sb.toString());
			} else {
				logger.info("Payten Request Map is empty ");
			}

			// Log all entries from sessionMap
			logger.info("Encrypted Response received from Payten: " + responseString.toString());
			String mid = requestMap.get("PAY_ID").toString();
			String encData = requestMap.get("ENCDATA").toString();
			Fields fields = new Fields();
			String decryptedResponse="";
			//coomented for removing session management
			if (/*!sessionMap.isEmpty()*/false) {

				StringBuilder sb = new StringBuilder();
				Iterator itr = sessionMap.entrySet().iterator();
				while (itr.hasNext()) {
					Map.Entry obj = (Entry) itr.next();
					if (obj.getKey().toString().equalsIgnoreCase(FieldType.TXN_KEY.getName())) {
						logger.info("Txn Key Present in FIELDS map");
						continue;
					}
					sb.append(obj.getKey() + " = " + obj.getValue() + " ~");
				}
			//	logger.info("Payten sessionMap values >>> " + sb.toString());
				Object fieldsObj = sessionMap.get("FIELDS");
				if (null != fieldsObj) {
					fields.put((Fields) fieldsObj);
				}
				// Check if fields is empty
				if (StringUtils.isBlank(fields.get(FieldType.PAY_ID.getName()))) {
					logger.info("Payten FIELDS is blank in session Map, getting data from DB for Payten");

					fields = fieldsDao.getPreviousForPgRefNum(transactionId);
					String internalRequestFields = null;
					internalRequestFields = fieldsDao.getPreviousForOID(fields.get(FieldType.OID.getName()));

					if (StringUtils.isBlank(internalRequestFields)) {
						logger.info("Payten New Order entry not found for this OID , getting data from SENT TO BANK "
								+ fields.get(FieldType.OID.getName()));
						internalRequestFields = fieldsDao.getPreviousForOIDSTB(fields.get(FieldType.OID.getName()));
					} else {
						logger.info("Payten New Order entry found for this OID in New Order - Pending txn"
								+ fields.get(FieldType.OID.getName()));
					}

					String[] paramaters = internalRequestFields.split("~");
					Map<String, String> paramMap = new HashMap<String, String>();
					for (String param : paramaters) {
						String[] parameterPair = param.split("=");
						if (parameterPair.length > 1) {
							paramMap.put(parameterPair[0].trim(), parameterPair[1].trim());
						}
					}

					if (StringUtils.isNotBlank(paramMap.get(FieldType.RETURN_URL.getName()))) {
						logger.info("Payten Return URL found for ORDER ID " + paramMap.get(FieldType.ORDER_ID.getName())
								+ "Return URL >> " + paramMap.get(FieldType.RETURN_URL.getName()));
						fields.put(FieldType.RETURN_URL.getName(), paramMap.get(FieldType.RETURN_URL.getName()));
						sessionMap.put(FieldType.RETURN_URL.getName(), paramMap.get(FieldType.RETURN_URL.getName()));
					} else {
						logger.info("Payten Return URL not found for ORDER ID " + paramMap.get(FieldType.ORDER_ID.getName()));
					}

					if (StringUtils.isNotBlank(paramMap.get(FieldType.IS_MERCHANT_HOSTED.getName()))) {
						logger.info(
								"Payten IS_MERCHANT_HOSTED flag found for ORDER ID " + paramMap.get(FieldType.ORDER_ID.getName()));
						fields.put(FieldType.IS_MERCHANT_HOSTED.getName(),
								paramMap.get(FieldType.IS_MERCHANT_HOSTED.getName()));
						sessionMap.put(FieldType.IS_MERCHANT_HOSTED.getName(),
								paramMap.get(FieldType.IS_MERCHANT_HOSTED.getName()));
					} else {
						logger.info(
								"Payten IS_MERCHANT_HOSTED not found for ORDER ID " + paramMap.get(FieldType.ORDER_ID.getName()));
					}

				}
				
				if (sessionMap.get(FieldType.MERCHANT_ID.getName()) == null) {
					logger.info("Payten Merchant Id id null in session map ");
				}
				
				if (sessionMap.get(FieldType.TXN_KEY.getName()) == null) {
					logger.info("Payten TXN KEY is null in session map ");
				}
				
				if (sessionMap.get(FieldType.ADF1.getName()) == null) {
					logger.info("Payten ADF1 null is null in session map ");
				}
				
				if (StringUtils.isBlank((String) sessionMap.get(FieldType.MERCHANT_ID.getName())) 
						|| 	StringUtils.isBlank((String) sessionMap.get(FieldType.TXN_KEY.getName()).toString())
						||  StringUtils.isBlank((String) sessionMap.get(FieldType.ADF1.getName()).toString())) {
					
					logger.info("Payten Merchant Id ,TXNKEY and password NOT PRESENT in session map ");
					
					Map<String , String> keyMap = getTxnKey(fields);
					fields.put(FieldType.MERCHANT_ID.getName(), keyMap.get("merchantId"));
					fields.put(FieldType.TXN_KEY.getName(), keyMap.get("txnKey"));
					fields.put(FieldType.ADF1.getName(), keyMap.get("adf1"));
					
				}
				else {
					logger.info("Payten Merchant Id ,TXNKEY and password already present in session map  ");
					fields.put(FieldType.MERCHANT_ID.getName(), sessionMap.get(FieldType.MERCHANT_ID.getName()).toString());
					fields.put(FieldType.TXN_KEY.getName(), sessionMap.get(FieldType.TXN_KEY.getName()).toString());
					fields.put(FieldType.ADF1.getName(), sessionMap.get(FieldType.ADF1.getName()).toString());
				}
				
				decryptedResponse = decrypt(mid, encData, fields.get(FieldType.ADF1.getName()));
				logger.info("Decrypted Response From Payten :"+decryptedResponse);
				
			} else {
				logger.info("Payten Session Map is empty");
				Map<String , String> keyMap = getTxnKey(mid);
				fields.put(FieldType.MERCHANT_ID.getName(), mid);
				fields.put(FieldType.TXN_KEY.getName(), keyMap.get("txnKey"));
				fields.put(FieldType.ADF1.getName(), keyMap.get("adf1"));
				logger.info("Payten Session Map is empty" + fields.getFieldsAsString());
				logger.info("Payten Session Map is empty" + mid);

				decryptedResponse = decrypt(mid, encData, fields.get(FieldType.ADF1.getName()));
				logger.info("Decrypted Response From Payten :"+decryptedResponse);
				String[] fieldArr = decryptedResponse.split("~");
				
				Map<String, String> paytenResponseMap = new HashMap<String, String>();

				for (String entry : fieldArr) {
					String[] namValuePair = entry.split("=", 2);
					if (namValuePair.length == 2) {
						paytenResponseMap.put(namValuePair[0], namValuePair[1]);
					} else {
						paytenResponseMap.put(namValuePair[0], "");
					}
				}
				 
					logger.info("Payten FIELDS is blank in session Map, getting data from DB for Payten");

					fields = fieldsDao.getPreviousForOrderIdSTB((String)paytenResponseMap.get(FieldType.ORDER_ID.getName()));
					String internalRequestFields = null;
					internalRequestFields = fieldsDao.getPreviousForOID(fields.get(FieldType.OID.getName()));

					if (StringUtils.isBlank(internalRequestFields)) {
						logger.info("Payten New Order entry not found for this OID , getting data from SENT TO BANK "
								+ fields.get(FieldType.OID.getName()));
						internalRequestFields = fieldsDao.getPreviousForOIDSTB(fields.get(FieldType.OID.getName()));
					} else {
						logger.info("Payten New Order entry found for this OID in New Order - Pending txn"
								+ fields.get(FieldType.OID.getName()));
					}

					String[] paramaters = internalRequestFields.split("~");
					Map<String, String> paramMap = new HashMap<String, String>();
					for (String param : paramaters) {
						String[] parameterPair = param.split("=");
						if (parameterPair.length > 1) {
							paramMap.put(parameterPair[0].trim(), parameterPair[1].trim());
						}
					}

					if (StringUtils.isNotBlank(paramMap.get(FieldType.RETURN_URL.getName()))) {
						logger.info("Payten Return URL found for ORDER ID " + paramMap.get(FieldType.ORDER_ID.getName())
								+ "Return URL >> " + paramMap.get(FieldType.RETURN_URL.getName()));
						fields.put(FieldType.RETURN_URL.getName(), paramMap.get(FieldType.RETURN_URL.getName()));
						sessionMap.put(FieldType.RETURN_URL.getName(), paramMap.get(FieldType.RETURN_URL.getName()));
					} else {
						logger.info("Payten Return URL not found for ORDER ID " + paramMap.get(FieldType.ORDER_ID.getName()));
					}

					if (StringUtils.isNotBlank(paramMap.get(FieldType.IS_MERCHANT_HOSTED.getName()))) {
						logger.info(
								"Payten IS_MERCHANT_HOSTED flag found for ORDER ID " + paramMap.get(FieldType.ORDER_ID.getName()));
						fields.put(FieldType.IS_MERCHANT_HOSTED.getName(),
								paramMap.get(FieldType.IS_MERCHANT_HOSTED.getName()));
						sessionMap.put(FieldType.IS_MERCHANT_HOSTED.getName(),
								paramMap.get(FieldType.IS_MERCHANT_HOSTED.getName()));
					} else {
						logger.info(
								"Payten IS_MERCHANT_HOSTED not found for ORDER ID " + paramMap.get(FieldType.ORDER_ID.getName()));
					}
					
				sessionMap.put(FieldType.INTERNAL_ORIG_TXN_TYPE.getName(),fields.get(FieldType.INTERNAL_ORIG_TXN_TYPE.getName()));
				sessionMap.put(FieldType.PAYMENTS_REGION.getName(),fields.get(FieldType.PAYMENTS_REGION.getName()));
				sessionMap.put(FieldType.CARD_HOLDER_TYPE.getName(),fields.get(FieldType.CARD_HOLDER_TYPE.getName()));
				sessionMap.put(FieldType.OID.getName(),fields.get(FieldType.OID.getName()));
				sessionMap.put(FieldType.INTERNAL_IRCTC_YN.getName(),fields.get(FieldType.INTERNAL_IRCTC_YN.getName()));
					
			}

			
			
			fields.put(FieldType.PAY10_FINAL_RESPONSE.getName(), decryptedResponse.toString());
			
			fields.logAllFields("Payten Response Recieved :");

			fields.logAllFields("Updated 3DS Recieved Map TxnType = " + fields.get(FieldType.TXNTYPE.getName()) + " "
					+ "Txn id = " + fields.get(FieldType.TXN_ID.getName()));
			fields.put(FieldType.ACQUIRER_TYPE.getName(), AcquirerType.PAY10.getCode());
			
			
			
			
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
			
			fields.put(FieldType.INTERNAL_VALIDATE_HASH_YN.getName(), "N");
			
			
			if (StringUtils.isNotBlank((String) sessionMap.get(FieldType.OID.getName()))) {
				fields.put((FieldType.OID.getName()), (String) sessionMap.get(FieldType.OID.getName()));

			}
			
			if (StringUtils.isNotBlank(fields.get(FieldType.INTERNAL_CARD_ISSUER_BANK.getName()))) {
				sessionMap.put(FieldType.INTERNAL_CARD_ISSUER_BANK.getName(),fields.get(FieldType.INTERNAL_CARD_ISSUER_BANK.getName()));
			}
			
			if (StringUtils.isNotBlank(fields.get(FieldType.INTERNAL_CARD_ISSUER_COUNTRY.getName()))) {
				sessionMap.put(FieldType.INTERNAL_CARD_ISSUER_COUNTRY.getName(),fields.get(FieldType.INTERNAL_CARD_ISSUER_COUNTRY.getName()));
			}
			
			Map<String, String> response = transactionControllerServiceProvider.transact(fields,
					Constants.TXN_WS_PAYTEN_PROCESSOR.getValue());
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
			responseMap.remove(FieldType.ADF1.getName());
			responseMap.remove(FieldType.MERCHANT_ID.getName());
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
						.equalsIgnoreCase(AcquirerType.getInstancefromCode(AcquirerType.PAY10.getCode()).getName())) {
					account = accountThis;
					break;
				}
			}
		}

		AccountCurrency accountCurrency = account.getAccountCurrency(fields.get(FieldType.CURRENCY_CODE.getName()));
		
		keyMap.put("merchantId", accountCurrency.getMerchantId());
		keyMap.put("txnKey", accountCurrency.getTxnKey());
		keyMap.put("adf1", accountCurrency.getAdf1());
		
		return keyMap;

	}
	
	
	public String decrypt(String payId, String encData, String key) {

		try {

			ScramblerCustom scrambler = new ScramblerCustom(key);
			String data = scrambler.decrypt(encData);
			return data;
		}

		catch (Exception e) {
			logger.error("Exception ", e);
			return null;
		}

	}
	
	
	public Map<String, String> getTxnKey(String merchantId) throws SystemException {

		Map<String, String> keyMap = new AccountCurrencyDao().findCustomFromMid(merchantId);
		return keyMap;

	}
}
