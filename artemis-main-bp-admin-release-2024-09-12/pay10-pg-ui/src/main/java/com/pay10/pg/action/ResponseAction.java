package com.pay10.pg.action;

import java.util.HashMap;
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
 * @author Sunil, Neeraj,Rahul
 *
 */
public class ResponseAction extends AbstractSecureAction implements ServletRequestAware {
	@Autowired
	TransactionControllerServiceProvider transactionControllerServiceProvider;

	private static Logger logger = LoggerFactory.getLogger(ResponseAction.class.getName());
	private static final long serialVersionUID = 2943629615913246334L;

	private Fields responseMap = null;
	private HttpServletRequest httpRequest;
	private String redirectUrl;
	private Integer count;

	public ResponseAction() {
	}

	@Override
	public void setServletRequest(HttpServletRequest hReq) {
		this.httpRequest = hReq;
	}

	@Autowired
	private UserDao userDao;
	@Autowired
	private RetryTransactionProcessor retryTransactionProcessor;
	@Autowired
	private ResponseCreator responseCreator;

	@Override
	public String execute() {
		Fields fields = new Fields();
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

			String paRes = requestMap.get("PaRes");
			String md = requestMap.get("MD");

			
			fields.put(FieldType.MD.getName(), md);
			fields.put(FieldType.PARES.getName(), paRes);

			fields.logAllFields("3DS Recieved Map :");

			Object fieldsObj = sessionMap.get("FIELDS");

			if (null != fieldsObj) {
				fields.put((Fields) fieldsObj);
			}

			fields.logAllFields("Updated 3DS Recieved Map TxnType = " + fields.get(FieldType.TXNTYPE.getName()) + " " + "Txn id = " + fields.get(FieldType.TXN_ID.getName()));
			fields.put(FieldType.ACQUIRER_TYPE.getName(), AcquirerType.HDFC.getCode());
			fields.put(FieldType.PASSWORD.getName(),(String) sessionMap.get(FieldType.PASSWORD.getName()));
			fields.put(FieldType.MERCHANT_ID.getName(),(String) sessionMap.get(FieldType.MERCHANT_ID.getName()));
			fields.put(FieldType.TXNTYPE.getName(),
					(String) sessionMap.get(FieldType.INTERNAL_ORIG_TXN_TYPE.getName()));
			fields.put(FieldType.INTERNAL_VALIDATE_HASH_YN.getName(), "N");
			fields.put((FieldType.INTERNAL_CARD_ISSUER_BANK.getName()),
					(String) sessionMap.get(FieldType.INTERNAL_CARD_ISSUER_BANK.getName()));
			fields.put((FieldType.INTERNAL_CARD_ISSUER_COUNTRY.getName()),
					(String) sessionMap.get(FieldType.INTERNAL_CARD_ISSUER_COUNTRY.getName()));
			fields.put((FieldType.PAYMENTS_REGION.getName()),
					(String) sessionMap.get(FieldType.PAYMENTS_REGION.getName()));
			fields.put((FieldType.CARD_HOLDER_TYPE.getName()),
					(String) sessionMap.get(FieldType.CARD_HOLDER_TYPE.getName()));
			fields.remove(FieldType.TXN_ID.getName());
			fields.put((FieldType.OID.getName()), (String) sessionMap.get(FieldType.OID.getName()));
			Map<String, String> response = transactionControllerServiceProvider.transact(fields, Constants.TXN_WS_INTERNAL.getValue());
			responseMap = new Fields(response);
			
			String crisFlag = (String) sessionMap.get(FieldType.INTERNAL_IRCTC_YN.getName());
			if(StringUtils.isNotBlank(crisFlag)){
				responseMap.put(FieldType.INTERNAL_IRCTC_YN.getName(), crisFlag);
			}
			String isMerchantHosted = (String) sessionMap.get(FieldType.IS_MERCHANT_HOSTED.getName());
			if(StringUtils.isNotBlank(isMerchantHosted)){
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

	/*		Object previousFields = sessionMap.get(Constants.FIELDS.getValue());
			Fields sessionFields = null;
			if (null != previousFields) {
				sessionFields = (Fields) previousFields;
			} else {
				// TODO: Handle
			}
			sessionFields.put(responseMap);*/
			// Retry Transaction Block End
			// Sending Email for Transaction Status to merchant  TODO...
/*			String countryCode = (String) sessionMap.get(FieldType.INTERNAL_CUST_COUNTRY_NAME.getName());
			emailBuilder.postMan(responseMap, countryCode, user);*/

			
			fields.put(FieldType.RETURN_URL.getName(), (String) sessionMap.get(FieldType.RETURN_URL.getName()));
			String cardIssuerBank = (String) sessionMap.get(FieldType.INTERNAL_CARD_ISSUER_BANK.getName());
			String cardIssuerCountry = (String) sessionMap.get(FieldType.INTERNAL_CARD_ISSUER_COUNTRY.getName());
			if(StringUtils.isNotBlank(cardIssuerBank)){
				responseMap.put(FieldType.CARD_ISSUER_BANK.getName(), cardIssuerBank);
			}
			if(StringUtils.isNotBlank(cardIssuerCountry)){
				responseMap.put(FieldType.CARD_ISSUER_COUNTRY.getName(), cardIssuerCountry);
			}
	
			if (sessionMap != null) {
				sessionMap.put(Constants.TRANSACTION_COMPLETE_FLAG.getValue(), Constants.Y_FLAG.getValue());
				sessionMap.invalidate();
			}
			responseMap.remove(FieldType.HASH.getName());
			responseCreator.create(responseMap);
			responseCreator.ResponsePost(responseMap);

		} catch (Exception exception) {
			logger.error("Exception", exception);
			return ERROR;
		}
		return Action.NONE;
	}

	public String getRedirectUrl() {
		return redirectUrl;
	}

	public void setRedirectUrl(String redirectUrl) {
		this.redirectUrl = redirectUrl;
	}

	public Integer getCount() {
		return count;
	}

	public void setCount(Integer count) {
		this.count = count;
	}

}
