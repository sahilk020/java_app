package com.pay10.pg.action;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
import com.pay10.pg.core.util.IciciUtil;
import com.pay10.pg.core.util.ResponseCreator;

public class IciciNBResponseAction extends AbstractSecureAction implements ServletRequestAware {

	private static Logger logger = LoggerFactory.getLogger(IciciNBResponseAction.class.getName());
	private static final long serialVersionUID = 6155942791032490231L;

	@Autowired
	TransactionControllerServiceProvider transactionControllerServiceProvider;

	@Autowired
	private ResponseCreator responseCreator;

	@Autowired
	private UserDao userDao;

	@Autowired
	private FieldsDao fieldsDao;
	private Fields responseMap = null;
	private HttpServletRequest httpRequest;

	@Override
	public void setServletRequest(HttpServletRequest hReq) {
		this.httpRequest = hReq;
	}

	@Override
	public String execute() {
		try {

			Map<String, String[]> fieldMapObj = httpRequest.getParameterMap();
			Map<String, String> requestMap = new HashMap<>();
			Map<String, String> responseFieldMap = new HashMap<>();
			for (Entry<String, String[]> entry : fieldMapObj.entrySet()) {
				try {
					requestMap.put(entry.getKey().trim(), ((String[]) entry.getValue())[0].trim());
				} catch (ClassCastException classCastException) {
					logger.error("ICICI Response Action-Exception:", classCastException);
				}
			}

			// Log all entries from requestMap

			/*
			 * if (!requestMap.isEmpty()) {
			 * 
			 * StringBuilder sb = new StringBuilder(); Iterator itr =
			 * requestMap.entrySet().iterator(); while (itr.hasNext()) { Map.Entry obj =
			 * (Entry) itr.next(); sb.append(obj.getKey() + " = " + obj.getValue() + " ~");
			 * } logger.info("Request Map received >>> " + sb.toString()); } else {
			 * logger.info("Request Map is empty "); }
			 */

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
				logger.info("sessionMap values >>> " + sb.toString());
			} else {
				logger.info("Session Map is empty");
			}

			Fields fields = new Fields();
			Object fieldsObj = sessionMap.get("FIELDS");
			if (null != fieldsObj) {
				fields.put((Fields) fieldsObj);
			}

			String es = requestMap.get("ES");
			String trackId = requestMap.get("pgrefnum");

			// Check if fields is empty
			if (StringUtils.isBlank(fields.get(FieldType.PAY_ID.getName()))) {
				logger.info("FIELDS is blank in session Map, getting data from DB for ICICI NB");

				fields = fieldsDao.getPreviousForPgRefNum(trackId);
				String internalRequestFields = null;
				internalRequestFields = fieldsDao.getPreviousForOID(fields.get(FieldType.OID.getName()));

				if (StringUtils.isBlank(internalRequestFields)) {
					logger.info("New Order entry not found for this OID , getting data from SENT TO BANK "
							+ fields.get(FieldType.OID.getName()));
					internalRequestFields = fieldsDao.getPreviousForOIDSTB(fields.get(FieldType.OID.getName()));
				} else {
					logger.info("New Order entry found for this OID in New Order - Pending txn"
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
					logger.info("Return URL found for ORDER ID " + paramMap.get(FieldType.ORDER_ID.getName())
							+ "Return URL >> " + paramMap.get(FieldType.RETURN_URL.getName()));
					fields.put(FieldType.RETURN_URL.getName(), paramMap.get(FieldType.RETURN_URL.getName()));
					sessionMap.put(FieldType.RETURN_URL.getName(), paramMap.get(FieldType.RETURN_URL.getName()));
				} else {
					logger.info("Return URL not found for ORDER ID " + paramMap.get(FieldType.ORDER_ID.getName()));
				}

				if (StringUtils.isNotBlank(paramMap.get(FieldType.IS_MERCHANT_HOSTED.getName()))) {
					logger.info(
							"IS_MERCHANT_HOSTED flag found for ORDER ID " + paramMap.get(FieldType.ORDER_ID.getName()));
					fields.put(FieldType.IS_MERCHANT_HOSTED.getName(),
							paramMap.get(FieldType.IS_MERCHANT_HOSTED.getName()));
					sessionMap.put(FieldType.IS_MERCHANT_HOSTED.getName(),
							paramMap.get(FieldType.IS_MERCHANT_HOSTED.getName()));
				} else {
					logger.info(
							"IS_MERCHANT_HOSTED not found for ORDER ID " + paramMap.get(FieldType.ORDER_ID.getName()));
				}

			}

			logger.info("Encrypted Response received from ICICI NB: " + es);
			String txnKey = (String) sessionMap.get(FieldType.TXN_KEY.getName());

			if (StringUtils.isNotBlank(txnKey)) {
				logger.info("Key found in session for ICICI NB decryption: ");

			} else {
				logger.info("Key not found in session for ICICI NB decryption: ");

				if (StringUtils.isBlank(fields.get(FieldType.PAY_ID.getName()))) {
					fields = fieldsDao.getPreviousForPgRefNum(trackId);
					String internalRequestFields = fieldsDao.getPreviousForOID(fields.get(FieldType.OID.getName()));
					String[] paramaters = internalRequestFields.split("~");
					Map<String, String> paramMap = new HashMap<String, String>();
					for (String param : paramaters) {
						String[] parameterPair = param.split("=");
						if (parameterPair.length > 1) {
							paramMap.put(parameterPair[0].trim(), parameterPair[1].trim());
						}
					}
					fields.put(FieldType.RETURN_URL.getName(), paramMap.get(FieldType.RETURN_URL.getName()));
					sessionMap.put(FieldType.RETURN_URL.getName(), paramMap.get(FieldType.RETURN_URL.getName()));
					txnKey = getTxnKey(fields);
				} else {
					txnKey = getTxnKey(fields);
				}

			}
			IciciUtil iciciUtil = new IciciUtil(txnKey);
			String decrytedString = iciciUtil.decrypt(es);
			logger.info("Decrypted response received from ICICI NB: " + decrytedString);

			if (!StringUtils.isEmpty(decrytedString)) {
				responseFieldMap = Stream.of(decrytedString.split("&"))
						.collect(Collectors.toMap(t -> t.split("=")[0], t -> t.split("=")[1]));
			}
			logger.info("ICICI Response Action-Response map from ICICI Bank: " + responseFieldMap);

			fields.put(FieldType.ICICI_NB_RESPONSE_FIELD.getName(), decrytedString);

			fields.logAllFields("Updated 3DS Recieved Map TxnType = " + fields.get(FieldType.TXNTYPE.getName()) + " "
					+ "Txn id = " + fields.get(FieldType.TXN_ID.getName()));
			fields.put(FieldType.ACQUIRER_TYPE.getName(), AcquirerType.ICICIBANK.getCode());

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
				sessionMap.put(FieldType.INTERNAL_CARD_ISSUER_BANK.getName(),
						fields.get(FieldType.INTERNAL_CARD_ISSUER_BANK.getName()));
			}

			if (StringUtils.isNotBlank(fields.get(FieldType.INTERNAL_CARD_ISSUER_COUNTRY.getName()))) {
				sessionMap.put(FieldType.INTERNAL_CARD_ISSUER_COUNTRY.getName(),
						fields.get(FieldType.INTERNAL_CARD_ISSUER_COUNTRY.getName()));
			}

			fields.put(FieldType.INTERNAL_VALIDATE_HASH_YN.getName(), "N");
			Map<String, String> response = transactionControllerServiceProvider.transact(fields,
					Constants.TXN_WS_ICICI_NB_PROCESSOR.getValue());
			responseMap = new Fields(response);

			String crisFlag = (String) sessionMap.get(FieldType.INTERNAL_IRCTC_YN.getName());
			if (StringUtils.isNotBlank(crisFlag)) {
				responseMap.put(FieldType.INTERNAL_IRCTC_YN.getName(), crisFlag);
			}
			String isMerchantHosted = (String) sessionMap.get(FieldType.IS_MERCHANT_HOSTED.getName());
			if (StringUtils.isNotBlank(isMerchantHosted)) {
				responseMap.put(FieldType.IS_MERCHANT_HOSTED.getName(), isMerchantHosted);
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

	public String getTxnKey(Fields fields) throws SystemException {

		User user = userDao.findPayId(fields.get(FieldType.PAY_ID.getName()));
		Account account = null;
		Set<Account> accounts = user.getAccounts();

		if (accounts == null || accounts.size() == 0) {
			logger.info("No account found for Pay ID = " + fields.get(FieldType.PAY_ID.getName()) + " and ORDER ID = "
					+ fields.get(FieldType.ORDER_ID.getName()));
		} else {
			for (Account accountThis : accounts) {
				if (accountThis.getAcquirerName().equalsIgnoreCase(
						AcquirerType.getInstancefromCode(AcquirerType.ICICIBANK.getCode()).getName())) {
					account = accountThis;
					break;
				}
			}
		}

		AccountCurrency accountCurrency = account.getAccountCurrency(fields.get(FieldType.CURRENCY_CODE.getName()));
		String txnKey = accountCurrency.getTxnKey();
		return txnKey;

	}
}
