package com.pay10.pg.action;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

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
import com.pay10.commons.exception.SystemException;
import com.pay10.commons.user.Account;
import com.pay10.commons.user.AccountCurrency;
import com.pay10.commons.user.User;
import com.pay10.commons.user.UserDao;
import com.pay10.commons.util.AcquirerType;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.pg.action.service.RetryTransactionProcessor;
import com.pay10.pg.core.util.ResponseCreator;

public class LyraResponseAction extends AbstractSecureAction implements ServletRequestAware {

	private static final long serialVersionUID = -1559265512834182028L;
	private static Logger logger = LoggerFactory.getLogger(LyraResponseAction.class.getName());

	private HttpServletRequest httpRequest;

	@Override
	public void setServletRequest(HttpServletRequest hReq) {
		this.httpRequest = hReq;

	}

	@Autowired
	private TransactionControllerServiceProvider transactionControllerServiceProvider;

	@Autowired
	private ResponseCreator responseCreator;

	@Autowired
	private UserDao userDao;

	@Autowired
	private FieldsDao fieldsDao;

	@Autowired
	private RetryTransactionProcessor retryTransactionProcessor;


	private Fields responseMap = null;

	public String execute() {
		try {
			Map<String, String[]> fieldMapObj = httpRequest.getParameterMap();
			Map<String, String> requestMap = new HashMap<String, String>();

			for (Entry<String, String[]> entry : fieldMapObj.entrySet()) {
				try {
					requestMap.put(entry.getKey(), ((String[]) entry.getValue())[0]);

				} catch (ClassCastException classCastException) {
					logger.error("Exception", classCastException);
				}
			}
			Fields fields = new Fields();
			Object fieldsObj = sessionMap.get("FIELDS");
			if (null != fieldsObj) {
				fields.put((Fields) fieldsObj);
			}

			String transData = requestMap.get("kr-answer");
			logger.info("Response received from LYRA : " + transData);
			String trackId = null;

			// Check if fields is empty
			if (StringUtils.isBlank(fields.get(FieldType.PAY_ID.getName()))) {
				logger.info("FIELDS is blank in session Map, getting data from DB");
				JSONObject response = new JSONObject(transData);
				JSONObject orderDetails = response.getJSONObject("orderDetails");
				Map<String, String> txnMap = new HashMap<String, String>();
				for (Object key : orderDetails.keySet()) {

					String key1 = key.toString();
					String value = orderDetails.get(key.toString()).toString();
					txnMap.put(key1, value);
				}
				trackId = txnMap.get("orderId");
				fields = fieldsDao.getPreviousForPgRefNum(trackId);
				String internalRequestFields = fields.get(FieldType.INTERNAL_REQUEST_FIELDS.getName());
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
				fields.put(FieldType.RETURN_URL.getName(), paramMap.get(FieldType.RETURN_URL.getName()));
				sessionMap.put(FieldType.RETURN_URL.getName(), paramMap.get(FieldType.RETURN_URL.getName()));
				if (StringUtils.isNotBlank(paramMap.get(FieldType.INTERNAL_CUST_IP.getName()))) {
					fields.put((FieldType.INTERNAL_CUST_IP.getName()),
							paramMap.get(FieldType.INTERNAL_CUST_IP.getName()));
				}
				if (StringUtils.isNotBlank(paramMap.get(FieldType.INTERNAL_CUST_COUNTRY_NAME.getName()))) {
					fields.put((FieldType.INTERNAL_CUST_COUNTRY_NAME.getName()),
							paramMap.get(FieldType.INTERNAL_CUST_COUNTRY_NAME.getName()));
				}
			}

			fields.put(FieldType.LYRA_RESPONSE_FIELD.getName(), transData);

			fields.logAllFields("Updated 3DS Recieved Map TxnType = " + fields.get(FieldType.TXNTYPE.getName()) + " "
					+ "Txn id = " + fields.get(FieldType.TXN_ID.getName()));
			fields.put(FieldType.ACQUIRER_TYPE.getName(), AcquirerType.LYRA.getCode());

			if (StringUtils.isNotBlank((String) sessionMap.get(FieldType.INTERNAL_ORIG_TXN_TYPE.getName()))) {
				fields.put(FieldType.TXNTYPE.getName(),
						(String) sessionMap.get(FieldType.INTERNAL_ORIG_TXN_TYPE.getName()));
			}
			if (StringUtils.isNotBlank((String) sessionMap.get(FieldType.INTERNAL_CUST_IP.getName()))) {
				fields.put((FieldType.INTERNAL_CUST_IP.getName()),
						(String) sessionMap.get(FieldType.INTERNAL_CUST_IP.getName()));
			}
			if (StringUtils.isNotBlank((String) sessionMap.get(FieldType.INTERNAL_CUST_COUNTRY_NAME.getName()))) {
				fields.put((FieldType.INTERNAL_CUST_COUNTRY_NAME.getName()),
						(String) sessionMap.get(FieldType.INTERNAL_CUST_COUNTRY_NAME.getName()));
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

			fields.put(FieldType.INTERNAL_VALIDATE_HASH_YN.getName(), "N");

			Map<String, String> response = transactionControllerServiceProvider.transact(fields,
					Constants.TXN_WS_LYRA_PROCESSOR.getValue());
			responseMap = new Fields(response);


			// Fetch user for retryTransaction ,SendEmailer and SmsSenser

			User user = userDao.getUserClass(responseMap.get(FieldType.PAY_ID.getName()));


			if (StringUtils.isNotBlank((String) sessionMap.get(FieldType.RETURN_URL.getName()))) {
				fields.put((FieldType.RETURN_URL.getName()), (String) sessionMap.get(FieldType.RETURN_URL.getName()));

			}

			Fields Fields = new Fields();
			Fields.put(FieldType.ORDER_ID.getName(), fields.get(FieldType.ORDER_ID.getName()));
			Fields.put(FieldType.STATUS.getName(), fields.get(FieldType.STATUS.getName()));
			Fields.put(FieldType.MOP_TYPE.getName(), fields.get(FieldType.MOP_TYPE.getName()));

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
			responseMap.put(FieldType.IS_MERCHANT_HOSTED.getName(),
					(String) sessionMap.get(FieldType.IS_MERCHANT_HOSTED.getName()));
			if (sessionMap != null) {
				sessionMap.put(Constants.TRANSACTION_COMPLETE_FLAG.getValue(), Constants.Y_FLAG.getValue());
				sessionMap.invalidate();
			}
			responseMap.remove(FieldType.HASH.getName());
			responseMap.remove(FieldType.TXN_KEY.getName());
			responseMap.remove(FieldType.ACQUIRER_TYPE.getName());
			responseMap.put(FieldType.IS_INTERNAL_REQUEST.getName(), "N");
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
				if (accountThis.getAcquirerName()
						.equalsIgnoreCase(AcquirerType.getInstancefromCode(AcquirerType.LYRA.getCode()).getName())) {
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
