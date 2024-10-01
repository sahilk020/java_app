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
 * @author Rahul
 *
 */
public class RupayResponseAction extends AbstractSecureAction implements ServletRequestAware {
	
	private static final long serialVersionUID = -706802972389481308L;

	@Autowired
	TransactionControllerServiceProvider transactionControllerServiceProvider;

	private static Logger logger = LoggerFactory.getLogger(RupayResponseAction.class.getName());
	

	private Fields responseMap = null;
	private HttpServletRequest httpRequest;
	private String redirectUrl;
	private Integer count;

	public RupayResponseAction() {
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

			String auth = requestMap.get("auth");
			String paymentId = requestMap.get("paymentid");
			String acqId = requestMap.get("tranid");
			String pgDateTime = requestMap.get("postdate");
			String avr = requestMap.get("avr");
			String rrn = requestMap.get("ref");
			String pgStatus = requestMap.get("result");      
			String pgResponseCode = requestMap.get("authRespCode");    

			Fields fields = new Fields();
			
			Object fieldsObj = sessionMap.get("FIELDS");
			if (null != fieldsObj) {
				fields.put((Fields) fieldsObj);
			}
			
			fields.put(FieldType.AUTH_CODE.getName(), auth);
			fields.put(FieldType.PAYMENT_ID.getName(), paymentId);
			fields.put(FieldType.ACQ_ID.getName(), acqId);
			fields.put(FieldType.PG_DATE_TIME.getName(), pgDateTime);
			fields.put(FieldType.AVR.getName(), avr);
			fields.put(FieldType.RRN.getName(), rrn);
			fields.put(FieldType.PG_TXN_MESSAGE.getName(), pgStatus);
			fields.put(FieldType.PG_RESP_CODE.getName(), pgResponseCode);

			fields.logAllFields("Updated 3DS Recieved Map :");
			fields.put(FieldType.ACQUIRER_TYPE.getName(), AcquirerType.FSS.getCode());
			fields.put(FieldType.TXNTYPE.getName(),
					(String) sessionMap.get(FieldType.INTERNAL_ORIG_TXN_TYPE.getName()));
			fields.put((FieldType.PAYMENTS_REGION.getName()),
					(String) sessionMap.get(FieldType.PAYMENTS_REGION.getName()));
			fields.put((FieldType.CARD_HOLDER_TYPE.getName()),
					(String) sessionMap.get(FieldType.CARD_HOLDER_TYPE.getName()));
			fields.put(FieldType.INTERNAL_VALIDATE_HASH_YN.getName(), "N");
			
			fields.remove(FieldType.TXN_ID.getName());
			fields.put((FieldType.OID.getName()), (String) sessionMap.get(FieldType.OID.getName()));
			Map<String, String> response = transactionControllerServiceProvider.transact(fields, Constants.TXN_WS_RUPAY_PROCESSOR.getValue());
			responseMap = new Fields(response);
			
			String crisFlag = (String) sessionMap.get(FieldType.INTERNAL_IRCTC_YN.getName());
			if(StringUtils.isNotBlank(crisFlag)){
				responseMap.put(FieldType.INTERNAL_IRCTC_YN.getName(), crisFlag);
			}

			// Fetch user for retryTransaction ,SendEmailer and SmsSenser

			User user = userDao.getUserClass(responseMap.get(FieldType.PAY_ID.getName()));

			// Retry Transaction Block Start
			if (!responseMap.get(FieldType.RESPONSE_CODE.getName()).equals(ErrorType.SUCCESS.getCode())) {

				if (retryTransactionProcessor.retryTransaction(responseMap, sessionMap, user, fields)) {
					addActionMessage(CrmFieldConstants.RETRY_TRANSACTION.getValue());
					return "paymentPage";
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
			responseMap.put(FieldType.INTERNAL_SHOPIFY_YN.getName(),
					(String) sessionMap.get(FieldType.INTERNAL_SHOPIFY_YN.getName()));
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

