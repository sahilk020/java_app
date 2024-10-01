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

import com.opensymphony.xwork2.Action;
import com.pay10.commons.api.TransactionControllerServiceProvider;
import com.pay10.commons.dao.FieldsDao;
import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.exception.SystemException;
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
 * @author Shaiwal
 *
 */
public class PhonePeResponseAction extends AbstractSecureAction implements ServletRequestAware {

	private static Logger logger = LoggerFactory.getLogger(PhonePeResponseAction.class.getName());
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
	private RetryTransactionProcessor retryTransactionProcessor;
	private Fields responseMap = null;
	private HttpServletRequest httpRequest;

	@Override
	public void setServletRequest(HttpServletRequest hReq) {
		this.httpRequest = hReq;
	}

	public PhonePeResponseAction() {
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

			// Log all entries from requestMap

			if (!requestMap.isEmpty()) {

				StringBuilder sb = new StringBuilder();
				Iterator itr = requestMap.entrySet().iterator();
				while (itr.hasNext()) {
					Map.Entry obj = (Entry) itr.next();
					sb.append(obj.getKey() + " = " + obj.getValue() + " ~");
				}
				logger.info("Phone Pe Request Map received >>> " + sb.toString());
			} else {
				logger.info("Phone Pe Request Map is empty ");
			}

			// Log all entries from sessionMap

			if (!sessionMap.isEmpty()) {

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
				logger.info("Phone Pe sessionMap values >>> " + sb.toString());
			} else {
				logger.info("Phone Pe Session Map is empty");
			}

			logger.info("Response received from PhonePe: " + responseString.toString());
			Fields fields = new Fields();

			Object fieldsObj = sessionMap.get("FIELDS");
			if (null != fieldsObj) {
				fields.put((Fields) fieldsObj);
			}

			// Check if fields is empty
			if (StringUtils.isNotBlank(transactionId)) {
				logger.info("PhonePe FIELDS is blank in session Map, getting data from DB for Phonepe");

				fields = fieldsDao.getPreviousForPgRefNum(transactionId);
				String internalRequestFields = null;
				internalRequestFields = fieldsDao.getPreviousForOID(fields.get(FieldType.OID.getName()));

				if (StringUtils.isBlank(internalRequestFields)) {
					logger.info("PhonePe New Order entry not found for this OID , getting data from SENT TO BANK "
							+ fields.get(FieldType.OID.getName()));
					internalRequestFields = fieldsDao.getPreviousForOIDSTB(fields.get(FieldType.OID.getName()));
				} else {
					logger.info("PhonePe New Order entry found for this OID in New Order - Pending txn"
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
					logger.info("PhonePe Return URL found for ORDER ID " + paramMap.get(FieldType.ORDER_ID.getName())
							+ "Return URL >> " + paramMap.get(FieldType.RETURN_URL.getName()));
					fields.put(FieldType.RETURN_URL.getName(), paramMap.get(FieldType.RETURN_URL.getName()));
					sessionMap.put(FieldType.RETURN_URL.getName(), paramMap.get(FieldType.RETURN_URL.getName()));
				} else {
					logger.info(
							"PhonePe Return URL not found for ORDER ID " + paramMap.get(FieldType.ORDER_ID.getName()));
				}

				if (StringUtils.isNotBlank(paramMap.get(FieldType.IS_MERCHANT_HOSTED.getName()))) {
					logger.info("PhonePe IS_MERCHANT_HOSTED flag found for ORDER ID "
							+ paramMap.get(FieldType.ORDER_ID.getName()));
					fields.put(FieldType.IS_MERCHANT_HOSTED.getName(),
							paramMap.get(FieldType.IS_MERCHANT_HOSTED.getName()));
					sessionMap.put(FieldType.IS_MERCHANT_HOSTED.getName(),
							paramMap.get(FieldType.IS_MERCHANT_HOSTED.getName()));
				} else {
					logger.info("PhonePe IS_MERCHANT_HOSTED not found for ORDER ID "
							+ paramMap.get(FieldType.ORDER_ID.getName()));
				}

			}

			fields.put(FieldType.MERCHANT_ID.getName(), requestMap.get("merchantId"));
			fields.put(FieldType.PHONEPE_RESPONSE_FIELD.getName(), responseString.toString());

			if (StringUtils.isBlank((String) sessionMap.get(FieldType.ADF1.getName()))
					|| StringUtils.isBlank((String) sessionMap.get(FieldType.TXN_KEY.getName()).toString())) {
				logger.info("PHONE PE ADF1 and TXNKEY NOT PRESENT in session map ");

				Map<String, String> keyMap = getTxnKey(fields);
				fields.put(FieldType.ADF1.getName(), keyMap.get("adf1"));
				fields.put(FieldType.TXN_KEY.getName(), keyMap.get("txnKey"));

			} else {
				logger.info("PHONE PE ADF1 and TXNKEY ARE PRESENT in session map ");
				fields.put(FieldType.ADF1.getName(), sessionMap.get(FieldType.ADF1.getName()).toString());
				fields.put(FieldType.TXN_KEY.getName(), sessionMap.get(FieldType.TXN_KEY.getName()).toString());
			}

			fields.logAllFields("PhonePe Response Recieved :");

			fields.logAllFields("Updated 3DS Recieved Map TxnType = " + fields.get(FieldType.TXNTYPE.getName()) + " "
					+ "Txn id = " + fields.get(FieldType.TXN_ID.getName()));
			fields.put(FieldType.ACQUIRER_TYPE.getName(), AcquirerType.PHONEPE.getCode());

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
				sessionMap.put(FieldType.INTERNAL_CARD_ISSUER_BANK.getName(),
						fields.get(FieldType.INTERNAL_CARD_ISSUER_BANK.getName()));
			}

			if (StringUtils.isNotBlank(fields.get(FieldType.INTERNAL_CARD_ISSUER_COUNTRY.getName()))) {
				sessionMap.put(FieldType.INTERNAL_CARD_ISSUER_COUNTRY.getName(),
						fields.get(FieldType.INTERNAL_CARD_ISSUER_COUNTRY.getName()));
			}

			Map<String, String> response = transactionControllerServiceProvider.transact(fields,
					Constants.TXN_WS_PHONEPE_PROCESSOR.getValue());
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
						.equalsIgnoreCase(AcquirerType.getInstancefromCode(AcquirerType.PHONEPE.getCode()).getName())) {
					account = accountThis;
					break;
				}
			}
		}

		AccountCurrency accountCurrency = account.getAccountCurrency(fields.get(FieldType.CURRENCY_CODE.getName()));

		keyMap.put("adf1", accountCurrency.getAdf1());
		keyMap.put("txnKey", accountCurrency.getTxnKey());

		return keyMap;

	}

}
