package com.pay10.pg.action;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

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
public class MobikwikResponseAction extends AbstractSecureAction implements ServletRequestAware {

	private static Logger logger = LoggerFactory.getLogger(MobikwikResponseAction.class.getName());
	private static final long serialVersionUID = 6155942791032490231L;

	@Autowired
	TransactionControllerServiceProvider transactionControllerServiceProvider;

	@Autowired
	private ResponseCreator responseCreator;

	@Autowired
	private FieldsDao fieldsDao;

	@Autowired
	private UserDao userDao;

	@Autowired
	private RetryTransactionProcessor retryTransactionProcessor;

	private Fields responseMap = null;
	private HttpServletRequest httpRequest;

	@Override
	public void setServletRequest(HttpServletRequest hReq) {
		this.httpRequest = hReq;
	}

	public MobikwikResponseAction() {
	}

	@Override
	public String execute() {
		try {

			String orderid = null;

			Map<String, String[]> fieldMapObj = httpRequest.getParameterMap();
			Map<String, String> requestMap = new HashMap<String, String>();

			for (Entry<String, String[]> entry : fieldMapObj.entrySet()) {
				try {
					requestMap.put(entry.getKey(), entry.getValue()[0]);
					if (entry.getKey().equalsIgnoreCase("orderid")) {
						orderid = entry.getValue()[0];
					}

				} catch (ClassCastException classCastException) {
					logger.error("Exception", classCastException);
				}
			}

			StringBuilder sbMap = new StringBuilder();

			for (Map.Entry<String, String> entry : requestMap.entrySet()) {
				String k = entry.getKey();
				String v = entry.getValue();
				sbMap.append(k);
				sbMap.append("=");
				sbMap.append(v);
				sbMap.append(";");
			}

			// Log all entries from requestMap

			if (!requestMap.isEmpty()) {

				StringBuilder sb = new StringBuilder();
				Iterator itr = requestMap.entrySet().iterator();
				while (itr.hasNext()) {
					Map.Entry obj = (Entry) itr.next();
					sb.append(obj.getKey() + " = " + obj.getValue() + " ~");
				}
				logger.info("Mobikwik Request Map received >>> " + sb.toString());
			} else {
				logger.info("Mobikwik Request Map is empty ");
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
				logger.info("Mobikwik sessionMap values >>> " + sb.toString());
			} else {
				logger.info("Mobikwik Session Map is empty");
			}

			Fields fields = new Fields();

			Object fieldsObj = sessionMap.get("FIELDS");
			if (null != fieldsObj) {
				fields.put((Fields) fieldsObj);
			}

			// Check if fields is empty
			if (StringUtils.isBlank(fields.get(FieldType.PAY_ID.getName()))) {
				logger.info("Mobikwik FIELDS is blank in session Map, getting data from DB for BOB");

				fields = fieldsDao.getPreviousForPgRefNum(orderid);
				String internalRequestFields = null;
				internalRequestFields = fieldsDao.getPreviousForOID(fields.get(FieldType.OID.getName()));

				if (StringUtils.isBlank(internalRequestFields)) {
					logger.info("Mobikwik New Order entry not found for this OID , getting data from SENT TO BANK "
							+ fields.get(FieldType.OID.getName()));
					internalRequestFields = fieldsDao.getPreviousForOIDSTB(fields.get(FieldType.OID.getName()));
				} else {
					logger.info("Mobikwik New Order entry found for this OID in New Order - Pending txn"
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
					logger.info("Mobikwik Return URL found for ORDER ID " + paramMap.get(FieldType.ORDER_ID.getName())
							+ "Return URL >> " + paramMap.get(FieldType.RETURN_URL.getName()));
					fields.put(FieldType.RETURN_URL.getName(), paramMap.get(FieldType.RETURN_URL.getName()));
					sessionMap.put(FieldType.RETURN_URL.getName(), paramMap.get(FieldType.RETURN_URL.getName()));
				} else {
					logger.info("Return URL not found for ORDER ID " + paramMap.get(FieldType.ORDER_ID.getName()));
				}

				if (StringUtils.isNotBlank(paramMap.get(FieldType.IS_MERCHANT_HOSTED.getName()))) {
					logger.info("Mobikwik IS_MERCHANT_HOSTED flag found for ORDER ID "
							+ paramMap.get(FieldType.ORDER_ID.getName()));
					fields.put(FieldType.IS_MERCHANT_HOSTED.getName(),
							paramMap.get(FieldType.IS_MERCHANT_HOSTED.getName()));
					sessionMap.put(FieldType.IS_MERCHANT_HOSTED.getName(),
							paramMap.get(FieldType.IS_MERCHANT_HOSTED.getName()));
				} else {
					logger.info(
							"IS_MERCHANT_HOSTED not found for ORDER ID " + paramMap.get(FieldType.ORDER_ID.getName()));
				}

			}

			fields.put(FieldType.MOBIKWIK_RESPONSE_FIELD.getName(), sbMap.toString());
			fields.logAllFields("Mobikwik Response Recieved :  " + sbMap.toString());

			fields.logAllFields("Updated Fields mobikwik TxnType = " + fields.get(FieldType.TXNTYPE.getName()) + " "
					+ "Txn id = " + fields.get(FieldType.TXN_ID.getName()));
			fields.put(FieldType.ACQUIRER_TYPE.getName(), AcquirerType.MOBIKWIK.getCode());

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

			fields.put(FieldType.INTERNAL_VALIDATE_HASH_YN.getName(), "N");

			Map<String, String> response = transactionControllerServiceProvider.transact(fields,
					Constants.TXN_WS_MOBIKWIK_PROCESSOR.getValue());

			responseMap = new Fields(response);

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

			String crisFlag = (String) sessionMap.get(FieldType.INTERNAL_IRCTC_YN.getName());
			if (StringUtils.isNotBlank(crisFlag)) {
				responseMap.put(FieldType.INTERNAL_IRCTC_YN.getName(), crisFlag);
			}

			String cardIssuerBank = (String) sessionMap.get(FieldType.INTERNAL_CARD_ISSUER_BANK.getName());
			String cardIssuerCountry = (String) sessionMap.get(FieldType.INTERNAL_CARD_ISSUER_COUNTRY.getName());

			fields.put(FieldType.RETURN_URL.getName(), (String) sessionMap.get(FieldType.RETURN_URL.getName()));

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
			logger.error("Exception in mobikwik respose action", exception);
			return ERROR;
		}
		return Action.NONE;
	}
}
