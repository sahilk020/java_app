package com.pay10.pg.action;

import java.io.BufferedReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

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

public class QuomoNotificationResponseAction extends AbstractSecureAction implements ServletRequestAware {
	private static Logger logger = LoggerFactory.getLogger(QuomoNotificationResponseAction.class.getName());
	private static final long serialVersionUID = 6155942791032490231L;

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

	public QuomoNotificationResponseAction() {
	}

	@Override
	public String execute() {
		try {
			
			BufferedReader rd = httpRequest.getReader();
			String line;
			StringBuffer responseString = new StringBuffer();
			while ((line = rd.readLine()) != null) {
				responseString.append(line);
			}
			rd.close();
			if (responseString.toString().isEmpty()) {
				logger.info("Buffered Reader QuomoNotificationResponseAction response is empty ");
			} else {
				logger.info("Buffered Reader QuomoNotificationResponseAction response contains data ");
				logger.info(" response >>>  " + responseString.toString());
			}
			String encData = responseString.toString();
			logger.info("Response from QUOMO UPI at callback : {} ", encData);
			JSONObject encDatajson = new JSONObject(encData);
			logger.info("Encrypted Response for QuomoNotificationResponseAction >> " + encDatajson);
			
			String pgRefNum = encDatajson.has("order_id") ? encDatajson.getString("order_id") : null;
			logger.info("Quomo pgRefNum : "+pgRefNum);
			Fields fields = new Fields();
			Object fieldsObj = sessionMap.get("FIELDS");
			if (null != fieldsObj) {
				fields.put((Fields) fieldsObj);
			}

			// Check if fields is empty
			if (StringUtils.isNotBlank(pgRefNum)) {
				fields = fieldsDao.getPreviousForPgRefNum(pgRefNum);
				
				String internalRequestFields = fields.get(FieldType.INTERNAL_REQUEST_FIELDS.getName());
				String[] paramaters = internalRequestFields.split("~");
				Map<String, String> paramMap = new HashMap<String, String>();
				for (String param : paramaters) {
					String[] parameterPair = param.split("=");
					if (parameterPair.length > 1) {
						paramMap.put(parameterPair[0].trim(), parameterPair[1].trim());
					}
				}
				
				if(StringUtils.isBlank(fields.get(FieldType.INTERNAL_CUST_IP.getName()))) {
					String ipAddress = fieldsDao.getIpFromSTB(fields.get(FieldType.OID.getName()));
					
					if(StringUtils.isBlank(ipAddress)) {
						ipAddress = fieldsDao.getIPFromInitiateRequest(fields.get(FieldType.OID.getName()));
					}
					
					fields.put(FieldType.INTERNAL_CUST_IP.getName(), ipAddress);
				}
				
				fields.put(FieldType.RETURN_URL.getName(), paramMap.get(FieldType.RETURN_URL.getName()));
				sessionMap.put(FieldType.RETURN_URL.getName(), paramMap.get(FieldType.RETURN_URL.getName()));
			}

			fields.put(FieldType.QUOMO_RESPONSE_FIELD.getName(), encData);
			fields.logAllFields("Quomo Response Recieved: ");

			logger.info("Updated Fields Quomo TxnType = " + fields.get(FieldType.TXNTYPE.getName()) + ", Txn id = " + fields.get(FieldType.PG_REF_NUM.getName()));
			fields.put(FieldType.ACQUIRER_TYPE.getName(), AcquirerType.QUOMO.getCode());
			
			if (StringUtils.isNotBlank((String) sessionMap.get(FieldType.INTERNAL_ORIG_TXN_TYPE.getName()))) {
				fields.put(FieldType.TXNTYPE.getName(), (String) sessionMap.get(FieldType.INTERNAL_ORIG_TXN_TYPE.getName()));
			}
			
			if (StringUtils.isNotBlank((String) sessionMap.get(FieldType.PAYMENTS_REGION.getName()))) {
				fields.put((FieldType.PAYMENTS_REGION.getName()), (String) sessionMap.get(FieldType.PAYMENTS_REGION.getName()));
			}

			if (StringUtils.isNotBlank((String) sessionMap.get(FieldType.CARD_HOLDER_TYPE.getName()))) {
				fields.put((FieldType.CARD_HOLDER_TYPE.getName()), (String) sessionMap.get(FieldType.CARD_HOLDER_TYPE.getName()));
			}
			
			if (StringUtils.isNotBlank(fields.get(FieldType.INTERNAL_CARD_ISSUER_BANK.getName()))) {
				sessionMap.put(FieldType.INTERNAL_CARD_ISSUER_BANK.getName(),fields.get(FieldType.INTERNAL_CARD_ISSUER_BANK.getName()));
			}
			
			if (StringUtils.isNotBlank(fields.get(FieldType.INTERNAL_CARD_ISSUER_COUNTRY.getName()))) {
				sessionMap.put(FieldType.INTERNAL_CARD_ISSUER_COUNTRY.getName(),fields.get(FieldType.INTERNAL_CARD_ISSUER_COUNTRY.getName()));
			}
			
			fields.put(FieldType.INTERNAL_VALIDATE_HASH_YN.getName(), "N");
			
			setConfigurationFromDb(fields);
			Map<String, String> response = transactionControllerServiceProvider.transact(fields, Constants.TXN_WS_QUOMO_PROCESSOR.getValue());

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
			responseMap.remove(FieldType.RESP_TXN_KEY.getName());
			responseMap.remove(FieldType.RESP_IV.getName());
			responseMap.remove(FieldType.TXN_KEY.getName());
			responseMap.remove(FieldType.IV.getName());
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

	public Map<String, String> getTxnKey(String merchantId) throws SystemException {

		Map<String, String> keyMap = new AccountCurrencyDao().findCustom(merchantId);
		return keyMap;

	}

	private void setConfigurationFromDb(Fields fields) throws SystemException {
		User user = userDao.findPayId(fields.get(FieldType.PAY_ID.getName()));
		Set<Account> accounts = user.getAccounts() == null ? new HashSet<>() : user.getAccounts();
		List<Account> accountList = accounts.stream()
				.filter(accountsFromSet -> StringUtils.equalsIgnoreCase(accountsFromSet.getAcquirerName(),
						AcquirerType.getInstancefromCode(AcquirerType.QUOMO.getCode()).getName()))
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
