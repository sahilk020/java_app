package com.pay10.pg.action;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.net.ssl.HttpsURLConnection;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.opensymphony.xwork2.Action;
import com.pay10.commons.api.TransactionControllerServiceProvider;
import com.pay10.commons.dao.FieldsDao;
import com.pay10.commons.exception.SystemException;
import com.pay10.commons.kms.AWSEncryptDecryptService;
import com.pay10.commons.user.Account;
import com.pay10.commons.user.AccountCurrency;
import com.pay10.commons.user.User;
import com.pay10.commons.user.UserDao;
import com.pay10.commons.util.AcquirerType;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.PropertiesManager;
import com.pay10.pg.action.service.RetryTransactionProcessor;
import com.pay10.pg.core.util.ResponseCreator;
import com.pay10.pg.core.util.TransactionResponser;

public class LyraNBResponseAction extends AbstractSecureAction implements ServletRequestAware {

	private static final long serialVersionUID = -4969809762504663180L;
	private static Logger logger = LoggerFactory.getLogger(LyraNBResponseAction.class.getName());

	private HttpServletRequest httpRequest;

	@Override
	public void setServletRequest(HttpServletRequest hReq) {
		this.httpRequest = hReq;

	}

	@Autowired
	private TransactionControllerServiceProvider transactionControllerServiceProvider;

	@Autowired
	private AWSEncryptDecryptService encryptDecryptService;

	@Autowired
	private ResponseCreator responseCreator;

	@Autowired
	private UserDao userDao;

	@Autowired
	private FieldsDao fieldsDao;

	@Autowired
	private RetryTransactionProcessor retryTransactionProcessor;

	@Autowired
	private TransactionResponser transactionResponser;

	private Fields responseMap = null;

	public String execute() {
		try {
			String pgRefNo = httpRequest.getParameter("pgRefNo");
			Fields fields = new Fields();
			Object fieldsObj = sessionMap.get("FIELDS");
			if (null != fieldsObj) {
				fields.put((Fields) fieldsObj);
			}

			// Check if fields is empty
			if (StringUtils.isBlank(fields.get(FieldType.PAY_ID.getName()))) {
				logger.info("FIELDS is blank in session Map, getting data from DB");

				fields = fieldsDao.getPreviousForPgRefNum(pgRefNo);
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
				if (StringUtils.isNotBlank(paramMap.get(FieldType.INTERNAL_CUST_IP.getName()))) {
					fields.put((FieldType.INTERNAL_CUST_IP.getName()),
							paramMap.get(FieldType.INTERNAL_CUST_IP.getName()));
				}
				if (StringUtils.isNotBlank(paramMap.get(FieldType.INTERNAL_CUST_COUNTRY_NAME.getName()))) {
					fields.put((FieldType.INTERNAL_CUST_COUNTRY_NAME.getName()),
							paramMap.get(FieldType.INTERNAL_CUST_COUNTRY_NAME.getName()));
				}
			}

			String merchantDetail = getTxnKey(fields);
			String[] merchantParam = merchantDetail.split(",");
			Map<String, String> detailParamMap = new HashMap<String, String>();
			for (String param : merchantParam) {
				String[] parameterPair = param.split("=");
				if (parameterPair.length > 1) {
					detailParamMap.put(parameterPair[0].trim(), parameterPair[1].trim());
				}
			}
			String lyraResponse = getStatus(fields, detailParamMap);
			logger.info("Lyra NB response for PG_REF_NUM " + fields.get(FieldType.PG_REF_NUM.getName()) + "=" + lyraResponse);
			fields.put(FieldType.LYRA_RESPONSE_FIELD.getName(), lyraResponse);

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
					Constants.TXN_WS_LYRA_NB_PROCESSOR.getValue());
			responseMap = new Fields(response);

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
		StringBuilder req = new StringBuilder();
		User user = userDao.findPayId(fields.get(FieldType.PAY_ID.getName()));
		Account account = null;
		Set<Account> accounts = user.getAccounts();

		if (accounts == null || accounts.size() == 0) {
			logger.info("No account found for Pay ID = " + fields.get(FieldType.PAY_ID.getName()) + " and ORDER ID = "
					+ fields.get(FieldType.ORDER_ID.getName()));
		} else {
			for (Account accountThis : accounts) {
				if (accountThis.getAcquirerName().equalsIgnoreCase(
						AcquirerType.getInstancefromCode(AcquirerType.LYRA.getCode()).getName())) {
					account = accountThis;
					break;
				}
			}
		}

		AccountCurrency accountCurrency = account.getAccountCurrency(fields.get(FieldType.CURRENCY_CODE.getName()));
		String mId = accountCurrency.getMerchantId();
		String password = encryptDecryptService.decrypt(accountCurrency.getPassword());

		req.append(FieldType.MERCHANT_ID.getName());
		req.append("=");
		req.append(mId);
		req.append(",");
		req.append(FieldType.PASSWORD.getName());
		req.append("=");
		req.append(password);
		req.append(",");

		return req.toString();
	}

	public String getStatus(Fields fields, Map<String, String> detailParamMap) throws SystemException {
		try {
			HttpsURLConnection connection = null;
			StringBuilder serverResponse = new StringBuilder();
			String hostUrl = PropertiesManager.propertiesMap.get("LyraNBChargeUrl");
			hostUrl = hostUrl + "/" + fields.get(FieldType.ACQ_ID.getName());
			URL url = new URL(hostUrl);
			String userName = detailParamMap.get(FieldType.MERCHANT_ID.getName());
			String pass = detailParamMap.get(FieldType.PASSWORD.getName());

			String authString = userName + ":" + pass;
			byte[] authEncBytes = Base64.encodeBase64(authString.getBytes());
			String authStringEnc = new String(authEncBytes);

			connection = (HttpsURLConnection) url.openConnection();
			connection.setRequestProperty("Authorization", "Basic " + authStringEnc);
			connection.setRequestMethod("GET");
			connection.setRequestProperty("Content-Type", "application/json");
			connection.setRequestProperty("Content-Language", "en-US");
			connection.setUseCaches(false);
			connection.setDoOutput(true);
			connection.setDoInput(true);
			InputStream is = connection.getInputStream();
			BufferedReader rd = new BufferedReader(new InputStreamReader(is));
			String line;
			while ((line = rd.readLine()) != null) {
				serverResponse.append(line);
				serverResponse.append('\r');
			}
			rd.close();
			String str = serverResponse.toString();
			return str;

		} catch (IOException e) {
			logger.error("Error communicating with Lyra NB get status , " ,e);
		}
		return null;

	}

}
