package com.pay10.pg.action;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.gson.JsonObject;
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
import com.pay10.pg.core.util.ResponseCreator;

public class DemoResponseAction extends AbstractSecureAction implements ServletRequestAware {

	private static Logger logger = LoggerFactory.getLogger(DemoResponseAction.class.getName());
	private static final long serialVersionUID = 6155942791032490231L;

	@Autowired
	private TransactionControllerServiceProvider transactionControllerServiceProvider;

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

	public DemoResponseAction() {
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

			JsonObject resJson = new JsonObject();
			String pgRef = null;

			for (Entry<String, String> entry : requestMap.entrySet()) {

				resJson.addProperty(entry.getKey(), entry.getValue());
				if (entry.getKey().equalsIgnoreCase("orderId")) {
					pgRef = entry.getValue();
				}

			}

			logger.info("Response received from DEMO: " + resJson.toString());
			logger.info("Response Session Map: " + sessionMap);
//			if(sessionMap.isEmpty()) {
//				logger.info("Manish Session ");
//				//HttpServletResponse.
//				return Action.NONE;
//			}
			Fields fields = new Fields();
			Object fieldsObj = sessionMap.get("FIELDS");
			if (null != fieldsObj) {
				fields.put((Fields) fieldsObj);
			}

//			String txnKey = (String) sessionMap.get(FieldType.TXN_KEY.getName());
//			String merchantId = (String) sessionMap.get(FieldType.MERCHANT_ID.getName());

			// if (StringUtils.isBlank(txnKey)) {

			if (StringUtils.isNotBlank(pgRef)) {
				String pgRefNum = pgRef;

				fields = fieldsDao.getPreviousForPgRefNum(pgRefNum);
				String internalRequestFields = fieldsDao.getPreviousForOID(fields.get(FieldType.OID.getName()));

				if (StringUtils.isBlank(internalRequestFields)) {
					internalRequestFields = fieldsDao
							.getPreviousByOIDForSentToBank(fields.get(FieldType.OID.getName()));
				}

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

				if (StringUtils.isNotBlank(paramMap.get(FieldType.IS_MERCHANT_HOSTED.getName()))) {
					logger.info("IS_MERCHANT_HOSTED flag found for ORDER ID "
							+ paramMap.get(FieldType.ORDER_ID.getName()) + " in DEMO");
					fields.put(FieldType.IS_MERCHANT_HOSTED.getName(),
							paramMap.get(FieldType.IS_MERCHANT_HOSTED.getName()));
					sessionMap.put(FieldType.IS_MERCHANT_HOSTED.getName(),
							paramMap.get(FieldType.IS_MERCHANT_HOSTED.getName()));
				} else {
					logger.info("IS_MERCHANT_HOSTED not found for ORDER ID "
							+ paramMap.get(FieldType.ORDER_ID.getName()) + " in DEMO");
				}

				Map<String, String> paymentMap = new HashMap<String, String>();
				paymentMap = getTxnKey(fields);

//					String txnKey = paymentMap.get(FieldType.TXN_KEY.getName());
//					String merchantId = paymentMap.get(FieldType.MERCHANT_ID.getName());
				fields.put(FieldType.MERCHANT_ID.getName(), paymentMap.get(FieldType.MERCHANT_ID.getName()));
				fields.put(FieldType.TXN_KEY.getName(), paymentMap.get(FieldType.TXN_KEY.getName()));
			}

//			}
//			else {
//				Map<String, String> paymentMap = new HashMap<String, String>();
//				paymentMap = getTxnKey(fields);
//				
//				fields.put(FieldType.MERCHANT_ID.getName(), paymentMap.get(FieldType.MERCHANT_ID.getName()));
//				fields.put(FieldType.TXN_KEY.getName(), paymentMap.get(FieldType.TXN_KEY.getName()));
//			}
			fields.put(FieldType.DEMO_RESPONSE_FIELD.getName(), resJson.toString());
//			fields.put(FieldType.TXN_KEY.getName(), txnKey);
//			fields.put(FieldType.MERCHANT_ID.getName(), merchantId);
			fields.logAllFields("DEMO Response Recieved :");

//			Object fieldsObj = sessionMap.get("FIELDS");
//			if (null != fieldsObj) {
//				fields.put((Fields) fieldsObj);
//			}

			fields.logAllFields("Updated 3DS Recieved Map TxnType = " + fields.get(FieldType.TXNTYPE.getName()) + " "
					+ "Txn id = " + fields.get(FieldType.TXN_ID.getName()));
			fields.put(FieldType.ACQUIRER_TYPE.getName(), AcquirerType.DEMO.getCode());
			fields.put(FieldType.TXNTYPE.getName(),
					(String) sessionMap.get(FieldType.INTERNAL_ORIG_TXN_TYPE.getName()));
			fields.put(FieldType.INTERNAL_VALIDATE_HASH_YN.getName(), "N");
			fields.put((FieldType.PAYMENTS_REGION.getName()),
					(String) sessionMap.get(FieldType.PAYMENTS_REGION.getName()));
			fields.put((FieldType.CARD_HOLDER_TYPE.getName()),
					(String) sessionMap.get(FieldType.CARD_HOLDER_TYPE.getName()));
			// fields.put((FieldType.OID.getName()), (String)
			// sessionMap.get(FieldType.OID.getName()));
			Map<String, String> response = transactionControllerServiceProvider.transact(fields,
					Constants.TXN_WS_DEMO_PROCESSOR.getValue());
			responseMap = new Fields(response);

			String crisFlag = (String) sessionMap.get(FieldType.INTERNAL_IRCTC_YN.getName());
			if (StringUtils.isNotBlank(crisFlag)) {
				responseMap.put(FieldType.INTERNAL_IRCTC_YN.getName(), crisFlag);
			}
			String isMerchantHosted = (String) sessionMap.get(FieldType.IS_MERCHANT_HOSTED.getName());
			if (StringUtils.isNotBlank(isMerchantHosted)) {
				responseMap.put(FieldType.IS_MERCHANT_HOSTED.getName(), isMerchantHosted);
			}
			String returnUrl = (String) sessionMap.get(FieldType.RETURN_URL.getName());
			if (StringUtils.isNotBlank(returnUrl)) {
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

		User user = userDao.findPayId(fields.get(FieldType.PAY_ID.getName()));
		Account account = null;
		Set<Account> accounts = user.getAccounts();

		if (accounts == null || accounts.size() == 0) {
			logger.info("No account found for Pay ID = " + fields.get(FieldType.PAY_ID.getName()) + " and ORDER ID = "
					+ fields.get(FieldType.ORDER_ID.getName()));
		} else {
			for (Account accountThis : accounts) {
				if (accountThis.getAcquirerName()
						.equalsIgnoreCase(AcquirerType.getInstancefromCode(AcquirerType.DEMO.getCode()).getName())) {
					account = accountThis;
					break;
				}
			}
		}

		AccountCurrency accountCurrency = account.getAccountCurrency(fields.get(FieldType.CURRENCY_CODE.getName()));

		Map<String, String> responseMap = new HashMap<String, String>();
		responseMap.put(FieldType.MERCHANT_ID.getName(), accountCurrency.getMerchantId());
		responseMap.put(FieldType.TXN_KEY.getName(), accountCurrency.getTxnKey());

		return responseMap;

	}
}
