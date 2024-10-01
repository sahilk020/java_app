package com.pay10.pg.action;

import com.opensymphony.xwork2.Action;
import com.pay10.commons.api.TransactionControllerServiceProvider;
import com.pay10.commons.dao.FieldsDao;
import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.exception.SystemException;
import com.pay10.commons.user.*;
import com.pay10.commons.util.*;
import com.pay10.pg.action.service.RetryTransactionProcessor;
import com.pay10.pg.core.util.AtomEncDecUtil;
import com.pay10.pg.core.util.ResponseCreator;
import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

/**
 * @author Shaiwal
 *
 */
public class AtomResponseAction extends AbstractSecureAction implements ServletRequestAware {

	private static Logger logger = LoggerFactory.getLogger(AtomResponseAction.class.getName());
	private static final long serialVersionUID = 6155942791032490231L;

	@Autowired
	TransactionControllerServiceProvider transactionControllerServiceProvider;

	@Autowired
	private AtomEncDecUtil atomEncDecUtil;

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

	public AtomResponseAction() {
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
				logger.info("Response Map received for ATOM >>> " + sb.toString());
			} else {
				logger.info("Response Map is empty for ATOM ");
			}

			// Log all entries from sessionMap

			if (!sessionMap.isEmpty()) {

				StringBuilder sb = new StringBuilder();
				Iterator itr = sessionMap.entrySet().iterator();
				while (itr.hasNext()) {
					Map.Entry obj = (Entry) itr.next();
					if (obj.getKey().toString().equalsIgnoreCase(FieldType.RESP_TXN_KEY.getName())) {
						logger.info("ATOM Txn Key Present in Session map");
						continue;
					}

					if (obj.getKey().toString().equalsIgnoreCase(FieldType.RESP_IV.getName())) {
						logger.info("ATOM IV Present in Session map");
						continue;
					}
					sb.append(obj.getKey() + " = " + obj.getValue() + " ~");
				}
				logger.info("sessionMap values for ATOM > >> " + sb.toString());
			} else {
				logger.info("Session Map is empty for ATOM");
			}

			String encData = null;
			String login = null;

			logger.info("Encrypted Response for Direcpay >> " + encData);
			encData = requestMap.get("encdata");
			login = requestMap.get("login");

			String txnKey = (String) sessionMap.get(FieldType.RESP_TXN_KEY.getName());
			String iv = (String) sessionMap.get(FieldType.RESP_IV.getName());

			if (StringUtils.isBlank(txnKey) || StringUtils.isBlank(iv)) {
				logger.info("Encryption key NOT FOUND in session map for ATOM");
				Map<String, String> keyMap = getTxnKey(login);

				iv = keyMap.get("respIv");
				txnKey = keyMap.get("respTxnKey");

				if (StringUtils.isNotBlank(iv) && StringUtils.isNotBlank(txnKey)) {
					logger.info("Encryption key FOUND from db for ATOM");
				}

			} else {
				logger.info("Encryption key FOUND in session map for ATOM");
			}

			String decrytedString = null;
			String mer_txn = null;

			if (StringUtils.isNotBlank(encData)) {
				decrytedString = atomEncDecUtil.decrypt(encData, txnKey, iv);
			} else {
				logger.info("No Response received for atom ");
			}

			logger.info("Decrypted Response for ATOM >>> " + decrytedString);

			for (String data : decrytedString.split("&")) {
				if (data.contains("mer_txn")) {
					mer_txn = data.split("=")[1];
					break;
				}
			}

			Fields fields = new Fields();

			Object fieldsObj = sessionMap.get("FIELDS");
			if (null != fieldsObj) {
				fields.put((Fields) fieldsObj);
			}

			// Check if fields is empty
			if (StringUtils.isNotBlank(mer_txn)) {
				logger.info("ATOM FIELDS is blank in session Map for ISGPAY , getting data from DB");

				fields = fieldsDao.getPreviousForPgRefNum(mer_txn);

				String internalRequestFields = null;
				internalRequestFields = fieldsDao.getPreviousForOID(fields.get(FieldType.OID.getName()));

				if (StringUtils.isBlank(internalRequestFields)) {
					logger.info("ATOM New Order entry not found for this OID , getting data from SENT TO BANK "
							+ fields.get(FieldType.OID.getName()));
					internalRequestFields = fieldsDao.getPreviousForOIDSTB(fields.get(FieldType.OID.getName()));
				} else {
					logger.info("ATOM New Order entry found for this OID in New Order - Pending txn"
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
					logger.info("ATOM Return URL found for ORDER ID " + paramMap.get(FieldType.ORDER_ID.getName())
							+ "Return URL >> " + paramMap.get(FieldType.RETURN_URL.getName()));
					fields.put(FieldType.RETURN_URL.getName(), paramMap.get(FieldType.RETURN_URL.getName()));
					sessionMap.put(FieldType.RETURN_URL.getName(), paramMap.get(FieldType.RETURN_URL.getName()));
				} else {
					logger.info("ATOM Return URL not found for ORDER ID " + paramMap.get(FieldType.ORDER_ID.getName()));
				}

				if (StringUtils.isNotBlank(paramMap.get(FieldType.IS_MERCHANT_HOSTED.getName()))) {
					logger.info(
							"ATOM IS_MERCHANT_HOSTED flag found for ORDER ID " + paramMap.get(FieldType.ORDER_ID.getName()));
					fields.put(FieldType.IS_MERCHANT_HOSTED.getName(),
							paramMap.get(FieldType.IS_MERCHANT_HOSTED.getName()));
					sessionMap.put(FieldType.IS_MERCHANT_HOSTED.getName(),
							paramMap.get(FieldType.IS_MERCHANT_HOSTED.getName()));
				} else {
					logger.info(
							"ATOM IS_MERCHANT_HOSTED not found for ORDER ID " + paramMap.get(FieldType.ORDER_ID.getName()));
				}

			}

			fields.put(FieldType.ATOM_RESPONSE_FIELD.getName(), decrytedString);
			fields.logAllFields("ATOM Response Recieved :  ");

			fields.logAllFields("Updated Fields Atom TxnType = " + fields.get(FieldType.TXNTYPE.getName()) + " "
					+ "Txn id = " + fields.get(FieldType.TXN_ID.getName()));
			
			
			fields.put(FieldType.ACQUIRER_TYPE.getName(), AcquirerType.ATOM.getCode());
			
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
			
			if (StringUtils.isNotBlank(fields.get(FieldType.INTERNAL_CARD_ISSUER_BANK.getName()))) {
				sessionMap.put(FieldType.INTERNAL_CARD_ISSUER_BANK.getName(),fields.get(FieldType.INTERNAL_CARD_ISSUER_BANK.getName()));
			}
			
			if (StringUtils.isNotBlank(fields.get(FieldType.INTERNAL_CARD_ISSUER_COUNTRY.getName()))) {
				sessionMap.put(FieldType.INTERNAL_CARD_ISSUER_COUNTRY.getName(),fields.get(FieldType.INTERNAL_CARD_ISSUER_COUNTRY.getName()));
			}
			
			fields.put(FieldType.INTERNAL_VALIDATE_HASH_YN.getName(), "N");
			
			
			if (StringUtils.isNotBlank((String) sessionMap.get(FieldType.OID.getName()))) {
				fields.put((FieldType.OID.getName()), (String) sessionMap.get(FieldType.OID.getName()));

			}
			setConfigurationFromDb(fields);
			Map<String, String> response = transactionControllerServiceProvider.transact(fields,
					Constants.TXN_WS_ATOM_PROCESSOR.getValue());

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
						AcquirerType.getInstancefromCode(AcquirerType.ATOM.getCode()).getName()))
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
