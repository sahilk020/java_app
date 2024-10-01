package com.pay10.pg.action;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.json.JSONObject;
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
import com.pay10.pg.core.camspay.util.CamsPayHasher;
import com.pay10.pg.core.util.ResponseCreator;

import bsh.This;

/**
 * @author Jay
 *
 */
public class CamsPayResponseAction extends AbstractSecureAction implements ServletRequestAware {

	private static Logger logger = LoggerFactory.getLogger(This.class.getName());
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

	private Fields responseMap = null;
	private HttpServletRequest httpRequest;

	@Override
	public void setServletRequest(HttpServletRequest hReq) {
		this.httpRequest = hReq;
	}

	public CamsPayResponseAction() {
	}

	@SuppressWarnings("rawtypes")
	@Override
	public String execute() {
		try {

			String txnId = null;
			
			Map<String, String[]> fieldMapObj = httpRequest.getParameterMap();
			Map<String, String> requestMap = new HashMap<String, String>();
			StringBuilder responseString = new StringBuilder();
			for (Entry<String, String[]> entry : fieldMapObj.entrySet()) {
				try {

					requestMap.put(entry.getKey(), entry.getValue()[0]);
					responseString.append(entry.getValue()[0]);
					logger.info("Key : "+entry.getKey());
					for(String s:entry.getValue())
					{
						logger.info("Value : "+s);
					}
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
				logger.info("execute:: request={}", sb.toString());
			} else {
				logger.info("execute:: request is empty");
			}

			// Log all entries from sessionMap
			if (!sessionMap.isEmpty()) {

				StringBuilder sb = new StringBuilder();
				Iterator itr = sessionMap.entrySet().iterator();
				while (itr.hasNext()) {
					Map.Entry obj = (Entry) itr.next();
					sb.append(obj.getKey() + " = " + obj.getValue() + " ~");
				}
				logger.info("execute:: sessionValue={}" + sb.toString());
			} else {
				logger.info("execute:: sessionValue is empty");
			}

			Fields fields = new Fields();

			Object fieldsObj = sessionMap.get("FIELDS");
			if (null != fieldsObj) {
				fields.put((Fields) fieldsObj);
			}
		
			logger.info("PASSWORD={}", sessionMap.get(FieldType.PASSWORD.getName()));
			logger.info("MERCHANT_ID={} ", sessionMap.get(FieldType.MERCHANT_ID.getName()));
			logger.info("txn_key={} ", sessionMap.get(FieldType.TXN_KEY.getName()));
			logger.info("salt={}", sessionMap.get(FieldType.ADF3.getName()));
			logger.info("iv={}", sessionMap.get(FieldType.ADF4.getName()));

			String decryptResponse = CamsPayHasher.decryptMessage(responseString.toString(), sessionMap.get(FieldType.ADF3.getName()).toString(),
					 sessionMap.get(FieldType.ADF4.getName()).toString());
				JSONObject respObj = new JSONObject(decryptResponse);
				txnId=respObj.getString("trxnid");

			// Check if fields is empty
			if (StringUtils.isNotBlank(txnId)) {
				logger.info("execute:: fields is blank in session. retrieve data from DB");
				fields = fieldsDao.getPreviousForPgRefNum(txnId);
				String internalRequestFields = null;
				internalRequestFields = fieldsDao.getPreviousForOID(fields.get(FieldType.OID.getName()));

				if (StringUtils.isBlank(internalRequestFields)) {
					logger.info("execute:: New order entry not found for OID={}, getting data from SENT TO BANK ",
							fields.get(FieldType.OID.getName()));
					internalRequestFields = fieldsDao.getPreviousForOIDSTB(fields.get(FieldType.OID.getName()));
				} else {
					logger.info("execute:: New Order entry found for OID={} in New Order - Pending txn",
							fields.get(FieldType.OID.getName()));
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
					logger.info("execute:: orderId={}, returnUrl={}", paramMap.get(FieldType.ORDER_ID.getName()),
							paramMap.get(FieldType.RETURN_URL.getName()));
					fields.put(FieldType.RETURN_URL.getName(), paramMap.get(FieldType.RETURN_URL.getName()));
					sessionMap.put(FieldType.RETURN_URL.getName(), paramMap.get(FieldType.RETURN_URL.getName()));
				} else {
					logger.info("execute:: return url not found. orderId={}",
							paramMap.get(FieldType.ORDER_ID.getName()));
				}

				if (StringUtils.isNotBlank(paramMap.get(FieldType.IS_MERCHANT_HOSTED.getName()))) {
					logger.info("execute:: IS_MERCHANT_HOSTED flag found. orderId={}",
							paramMap.get(FieldType.ORDER_ID.getName()));
					fields.put(FieldType.IS_MERCHANT_HOSTED.getName(),
							paramMap.get(FieldType.IS_MERCHANT_HOSTED.getName()));
					sessionMap.put(FieldType.IS_MERCHANT_HOSTED.getName(),
							paramMap.get(FieldType.IS_MERCHANT_HOSTED.getName()));
				} else {
					logger.info("execute:: IS_MERCHANT_HOSTED flag not found. orderId={}",
							paramMap.get(FieldType.ORDER_ID.getName()));
				}

			}

			fields.put(FieldType.CAMSPAY_RESPONSE_FIELD.getName(), responseString.toString());
			fields.logAllFields("execute:: CAMSPAY: Response Recieved=");

			fields.logAllFields(
					"execute:: CAMSPAY: Updated 3DS Recieved Map. txnType=" + fields.get(FieldType.TXNTYPE.getName())
							+ " " + "txnId=" + fields.get(FieldType.TXN_ID.getName()));
			logger.info("ipaddress={}", sessionMap.get(FieldType.INTERNAL_CUST_IP.getName()));

			logger.info("PASSWORD={}", sessionMap.get(FieldType.PASSWORD.getName()));
			logger.info("MERCHANT_ID={} ", sessionMap.get(FieldType.MERCHANT_ID.getName()));
			logger.info("txn_key={} ", sessionMap.get(FieldType.TXN_KEY.getName()));
			logger.info("salt={}", sessionMap.get(FieldType.ADF3.getName()));
			fields.put(FieldType.TXN_KEY.getName(), (String) sessionMap.get(FieldType.TXN_KEY.getName()));

			setConfigurationFromDb(fields);

			fields.put(FieldType.ACQUIRER_TYPE.getName(), AcquirerType.CAMSPAY.getCode());
			if (StringUtils.isNotBlank((String) sessionMap.get(FieldType.INTERNAL_ORIG_TXN_TYPE.getName()))) {
				fields.put(FieldType.TXNTYPE.getName(),
						(String) sessionMap.get(FieldType.INTERNAL_ORIG_TXN_TYPE.getName()));
			}

			if (StringUtils.isNotBlank((String) sessionMap.get(FieldType.PAYMENTS_REGION.getName()))) {
				fields.put((FieldType.PAYMENTS_REGION.getName()),
						(String) sessionMap.get(FieldType.PAYMENTS_REGION.getName()));
			}
			if (StringUtils.isNotBlank((String) sessionMap.get(FieldType.TXN_KEY.getName()))) {
				fields.put((FieldType.TXN_KEY.getName()), (String) sessionMap.get(FieldType.TXN_KEY.getName()));
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
				sessionMap.put(FieldType.INTERNAL_CARD_ISSUER_BANK.getName(),
						fields.get(FieldType.INTERNAL_CARD_ISSUER_BANK.getName()));
			}

			if (StringUtils.isNotBlank(fields.get(FieldType.INTERNAL_CARD_ISSUER_COUNTRY.getName()))) {
				sessionMap.put(FieldType.INTERNAL_CARD_ISSUER_COUNTRY.getName(),
						fields.get(FieldType.INTERNAL_CARD_ISSUER_COUNTRY.getName()));
			}
			fields.logAllFields("Field pass to CamsPaySaleResponseHandler : " + fields.getFieldsAsString());
			if (StringUtils.isNotBlank((String) sessionMap.get(FieldType.INTERNAL_CUST_IP.getName()))) {
				fields.put(FieldType.INTERNAL_CUST_IP.getName(),
						(String) sessionMap.get(FieldType.INTERNAL_CUST_IP.getName()));
			}
			
			Map<String, String> response = transactionControllerServiceProvider.transact(fields,
					Constants.TXN_WS_CAMSPAY_PROCESSOR.getValue());
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

	private void setConfigurationFromDb(Fields fields) throws SystemException {
		User user = userDao.findPayId(fields.get(FieldType.PAY_ID.getName()));
		Set<Account> accounts = user.getAccounts() == null ? new HashSet<>() : user.getAccounts();
		List<Account> accountList = accounts.stream()
				.filter(accountsFromSet -> StringUtils.equalsIgnoreCase(accountsFromSet.getAcquirerName(),
						AcquirerType.getInstancefromCode(AcquirerType.CAMSPAY.getCode()).getName()))
				.collect(Collectors.toList());
		Account account = accountList != null && accountList.size() > 0 ? accountList.get(0) : null;
		AccountCurrency accountCurrency = account.getAccountCurrency(fields.get(FieldType.CURRENCY_CODE.getName()));
		fields.put(FieldType.MERCHANT_ID.getName(), accountCurrency.getMerchantId());
		fields.put(FieldType.PASSWORD.getName(), accountCurrency.getAdf2());
		fields.put(FieldType.TXN_KEY.getName(), accountCurrency.getTxnKey());
		fields.put(FieldType.ADF1.getName(), accountCurrency.getAdf1());
		fields.put(FieldType.ADF2.getName(), accountCurrency.getAdf2());
		fields.put(FieldType.ADF3.getName(), accountCurrency.getAdf3());
		fields.put(FieldType.ADF4.getName(), accountCurrency.getAdf4());
		fields.put(FieldType.ADF5.getName(), accountCurrency.getAdf5());
		fields.put(FieldType.ADF6.getName(), accountCurrency.getAdf6());
		fields.put(FieldType.ADF7.getName(), accountCurrency.getAdf7());
		fields.put(FieldType.ADF8.getName(), accountCurrency.getAdf8());
		fields.put(FieldType.ADF9.getName(), accountCurrency.getAdf9());
		fields.put(FieldType.ADF10.getName(), accountCurrency.getAdf10());
	}
}
