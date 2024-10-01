package com.pay10.pg.action;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ExecutorService;

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
import com.pay10.commons.util.threadpool.ThreadPoolProvider;
import com.pay10.pg.action.service.RetryTransactionProcessor;
import com.pay10.pg.core.util.ISGPayDecryption;
import com.pay10.pg.core.util.ResponseCreator;

public class ISGPayResponseAction extends AbstractSecureAction implements ServletRequestAware {

	private static Logger logger = LoggerFactory.getLogger(ISGPayResponseAction.class.getName());
	private static final long serialVersionUID = 6155942791032490231L;

	@Autowired
	private TransactionControllerServiceProvider transactionControllerServiceProvider;

	@Autowired
	private ISGPayDecryption iSGPayDecryption;

	@Autowired
	private ResponseCreator responseCreator;

	@Autowired
	private UserDao userDao;

	@Autowired
	private FieldsDao fieldsDao;

	@Autowired
	private AWSEncryptDecryptService sWSEncryptDecryptService;
	
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
			Map<String, String> requestMap = new HashMap<String, String>();
			String pgRefNo = httpRequest.getParameter("pgRefNo");
			for (Entry<String, String[]> entry : fieldMapObj.entrySet()) {
				try {
					requestMap.put(entry.getKey(), entry.getValue()[0]);

				} catch (ClassCastException classCastException) {
					logger.error("Exception", classCastException);
				}
			}
			String EncData = requestMap.get("EncData");
			String respTid = requestMap.get("TerminalId");
			String respMid = requestMap.get("MerchantId");
			String respBankid = requestMap.get("BankId");

			LinkedHashMap<String, String> hmDecryptedValue = new LinkedHashMap<String, String>();

			hmDecryptedValue.put("EncData", EncData);
			hmDecryptedValue.put("TerminalId", respTid);
			hmDecryptedValue.put("MerchantId", respMid);
			hmDecryptedValue.put("BankId", respBankid);

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
				
				//added by sonu, add ipaddress for captured entry
				String ipAddress = fields.get(FieldType.INTERNAL_CUST_IP.getName());
				if (StringUtils.isBlank(ipAddress)) {
					ipAddress = fieldsDao.getIpAddressByOIDForPending(fields.get(FieldType.OID.getName()));
				}
				fields.put(FieldType.INTERNAL_CUST_IP.getName(), ipAddress);
				
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
			} else {
				//added by sonu, add ipaddress for captured entry
				//String ipAddress = fieldsDao.getIpAddressByPgRefNumForSTB(pgRefNo);
				//fields.put(FieldType.INTERNAL_CUST_IP.getName(), ipAddress);
				
				fields = fieldsDao.getPreviousForPgRefNum(pgRefNo);
				
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
					logger.info("Cust ipAddress in ISGPAY Acquirer = {}", ipAddress);
				}
				
				fields.put(FieldType.RETURN_URL.getName(), paramMap.get(FieldType.RETURN_URL.getName()));
				sessionMap.put(FieldType.RETURN_URL.getName(), paramMap.get(FieldType.RETURN_URL.getName()));
			}

			logger.info("Raw Response received from ISGPay: EncData = " + EncData + "MerchantId = " + respMid);

			String txnKey = (String) sessionMap.get(FieldType.TXN_KEY.getName());
			String password = null;
			String passCode = null;
			if (StringUtils.isNotBlank(txnKey)) {
				logger.info("Key found in session for ISGPAY decryption: ");
				txnKey = (String) sessionMap.get(FieldType.TXN_KEY.getName());
				password = (String) sessionMap.get(FieldType.PASSWORD.getName());
				passCode = (String) sessionMap.get(FieldType.ADF4.getName());
			} else {
				logger.info("Key not found in session for ISGPAY decryption: ");

				if (StringUtils.isBlank(fields.get(FieldType.PAY_ID.getName()))) {
					String txnId = pgRefNo;
					fields = fieldsDao.getPreviousForPgRefNum(txnId);
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
					//txnKey = getTxnKey(fields);
					String merchantDetail = getTxnKey(fields);
					String[] merchantParam = merchantDetail.split(",");
					Map<String, String> detailParamMap = new HashMap<String, String>();
					for (String param : merchantParam) {
						String[] parameterPair = param.split("=");
						if (parameterPair.length > 1) {
							detailParamMap.put(parameterPair[0].trim(), parameterPair[1].trim());
						}
					}
					txnKey = detailParamMap.get(FieldType.TXN_KEY.getName());
					password = detailParamMap.get(FieldType.PASSWORD.getName());
					passCode = detailParamMap.get(FieldType.ADF4.getName());
				} else {
					String merchantDetail = getTxnKey(fields);
					String[] merchantParam = merchantDetail.split(",");
					Map<String, String> detailParamMap = new HashMap<String, String>();
					for (String param : merchantParam) {
						String[] parameterPair = param.split("=");
						if (parameterPair.length > 1) {
							detailParamMap.put(parameterPair[0].trim(), parameterPair[1].trim());
						}
					}
					txnKey = detailParamMap.get(FieldType.TXN_KEY.getName());
					password = detailParamMap.get(FieldType.PASSWORD.getName());
					passCode = detailParamMap.get(FieldType.ADF4.getName());
				}
			}

			iSGPayDecryption.decrypt(hmDecryptedValue, txnKey, password);

			StringBuilder decrytedString = new StringBuilder();

			for (Map.Entry<String, String> entry : hmDecryptedValue.entrySet()) {
				String key = entry.getKey();
				String value = entry.getValue();
				decrytedString.append(key).append("=").append(value).append("||");
			}

			// Fields fields = new Fields();
			fields.put(FieldType.MERCHANT_ID.getName(), respMid);
			fields.put(FieldType.ADF1.getName(), respBankid);
			fields.put(FieldType.ADF2.getName(), respTid);
			fields.put(FieldType.ADF4.getName(), passCode);
			fields.put(FieldType.ISGPAY_RESPONSE_FIELD.getName(), decrytedString.toString());
			fields.put(FieldType.PASSWORD.getName(), password);
			fields.logAllFields("ISGPay Decrypted Response Recieved : " + decrytedString.toString());

			fields.logAllFields("ISGPAY Updated 3DS Recieved Map TxnType = " + fields.get(FieldType.TXNTYPE.getName())
					+ " " + "order id = " + fields.get(FieldType.ORDER_ID.getName()));
			fields.put(FieldType.ACQUIRER_TYPE.getName(), AcquirerType.ISGPAY.getCode());
			if (StringUtils.isNotBlank((String) sessionMap.get(FieldType.INTERNAL_CUST_IP.getName()))) {
				fields.put((FieldType.INTERNAL_CUST_IP.getName()),
						(String) sessionMap.get(FieldType.INTERNAL_CUST_IP.getName()));
			}
			if (StringUtils.isNotBlank((String) sessionMap.get(FieldType.INTERNAL_CUST_COUNTRY_NAME.getName()))) {
				fields.put((FieldType.INTERNAL_CUST_COUNTRY_NAME.getName()),
						(String) sessionMap.get(FieldType.INTERNAL_CUST_COUNTRY_NAME.getName()));
			}
			if (StringUtils.isNotBlank((String) sessionMap.get(FieldType.INTERNAL_ORIG_TXN_TYPE.getName()))) {
				fields.put(FieldType.TXNTYPE.getName(),
						(String) sessionMap.get(FieldType.INTERNAL_ORIG_TXN_TYPE.getName()));
			}
			fields.put(FieldType.INTERNAL_VALIDATE_HASH_YN.getName(), "N");
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
			
			Map<String, String> response = transactionControllerServiceProvider.transact(fields,
					Constants.TXN_WS_ISGPAY_PROCESSOR.getValue());
			responseMap = new Fields(response);

			String pgFlag = (String) sessionMap.get(FieldType.INTERNAL_PAYMENT_GATEWAY_YN.getName());
			if (StringUtils.isNotBlank(pgFlag)) {
				responseMap.put(FieldType.INTERNAL_PAYMENT_GATEWAY_YN.getName(), pgFlag);
			}


			User user = userDao.getUserClass(responseMap.get(FieldType.PAY_ID.getName()));


			Fields Fields = new Fields();
			Fields.put(FieldType.ORDER_ID.getName(), fields.get(FieldType.ORDER_ID.getName()));
			Fields.put(FieldType.STATUS.getName(), fields.get(FieldType.STATUS.getName()));
			Fields.put(FieldType.MOP_TYPE.getName(), fields.get(FieldType.MOP_TYPE.getName()));

			if (StringUtils.isNotBlank((String) sessionMap.get(FieldType.RETURN_URL.getName()))) {
				fields.put(FieldType.RETURN_URL.getName(), (String) sessionMap.get(FieldType.RETURN_URL.getName()));
			}
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
			responseMap.remove(FieldType.PASSWORD.getName());
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
				if (accountThis.getAcquirerName()
						.equalsIgnoreCase(AcquirerType.getInstancefromCode(AcquirerType.ISGPAY.getCode()).getName())) {
					account = accountThis;
					break;
				}
			}
		}

		AccountCurrency accountCurrency = account.getAccountCurrency(fields.get(FieldType.CURRENCY_CODE.getName()));
		String txnKey = accountCurrency.getTxnKey();
		//String password = sWSEncryptDecryptService.decrypt(	accountCurrency.getPassword());

		req.append(FieldType.TXN_KEY.getName());
		req.append("=");
		req.append(txnKey);
		req.append(",");
		req.append(FieldType.PASSWORD.getName());
		req.append("=");
		//req.append(password);
		//req.append(",");
		req.append(accountCurrency.getAdf5());
		req.append(",");
		req.append(FieldType.ADF4.getName());
		req.append("=");
		req.append(accountCurrency.getAdf4());
		req.append(",");

		return req.toString();
	}
}
