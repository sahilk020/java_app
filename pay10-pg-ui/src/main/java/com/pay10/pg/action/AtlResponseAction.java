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
import com.pay10.commons.util.AcquirerType;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.pg.core.util.ResponseCreator;

/**
 * @author Rahul
 *
 */
public class AtlResponseAction extends AbstractSecureAction implements ServletRequestAware {
	
	
	private static final long serialVersionUID = 6586101235318020905L;

	private static Logger logger = LoggerFactory.getLogger(AtlResponseAction.class.getName());
	
	private Fields responseMap = null;
	private HttpServletRequest httpRequest;

	@Override
	public void setServletRequest(HttpServletRequest hReq) {
		this.httpRequest = hReq;
	}

	public AtlResponseAction() {
	}
	
	@Autowired
	private ResponseCreator responseCreator;
	
	@Autowired
	TransactionControllerServiceProvider transactionControllerServiceProvider;

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

			String transData = requestMap.get("encdata");
			String key = (String) sessionMap.get(FieldType.TXN_KEY.getName());
			String iv = (String) sessionMap.get(FieldType.PASSWORD.getName());
								
			Fields fields = new Fields();
			fields.put(FieldType.ATL_RESPONSE_FIELD.getName(), transData);
			fields.logAllFields("ATL Response Recieved :");

			Object fieldsObj = sessionMap.get("FIELDS");
			if (null != fieldsObj) {
				fields.put((Fields) fieldsObj);
			}
			
			fields.put(FieldType.TXN_KEY.getName(), key);
			fields.put(FieldType.PASSWORD.getName(), iv);
			fields.put(FieldType.ACQUIRER_TYPE.getName(), AcquirerType.ATL.getCode());
			fields.put(FieldType.TXNTYPE.getName(),
					(String) sessionMap.get(FieldType.INTERNAL_ORIG_TXN_TYPE.getName()));
			fields.put(FieldType.INTERNAL_VALIDATE_HASH_YN.getName(), "N");
			fields.put((FieldType.OID.getName()), (String) sessionMap.get(FieldType.OID.getName()));
			Map<String, String> response = transactionControllerServiceProvider.transact(fields,
					Constants.TXN_WS_ATL_PROCESSOR.getValue());
			responseMap = new Fields(response);
			
			String crisFlag = (String) sessionMap.get(FieldType.INTERNAL_IRCTC_YN.getName());
			if(StringUtils.isNotBlank(crisFlag)){
				responseMap.put(FieldType.INTERNAL_IRCTC_YN.getName(), crisFlag);
			}

			// Fetch user for retryTransaction ,SendEmailer and SmsSenser
			/*User user = userDao.getUserClass(responseMap.get(FieldType.PAY_ID.getName()));

			// Retry Transaction Block Start
			if (!responseMap.get(FieldType.RESPONSE_CODE.getName()).equals(ErrorType.SUCCESS.getCode())) {

				if (retryTransactionProcessor.retryTransaction(responseMap, sessionMap, user)) {
					addActionMessage(CrmFieldConstants.RETRY_TRANSACTION.getValue());
					return "paymentPage";
				}

			}*/

			/*
			 * Object previousFields =
			 * sessionMap.get(Constants.FIELDS.getValue()); Fields sessionFields
			 * = null; if (null != previousFields) { sessionFields = (Fields)
			 * previousFields; } else { // TODO: Handle }
			 * sessionFields.put(responseMap);
			 */
			// Retry Transaction Block End
			// Sending Email for Transaction Status to merchant TODO...
			/*
			 * String countryCode = (String)
			 * sessionMap.get(FieldType.INTERNAL_CUST_COUNTRY_NAME.getName());
			 * emailBuilder.postMan(responseMap, countryCode, user);
			 */

			fields.put(FieldType.RETURN_URL.getName(), (String) sessionMap.get(FieldType.RETURN_URL.getName()));
			responseMap.put(FieldType.INTERNAL_SHOPIFY_YN.getName(),
					(String) sessionMap.get(FieldType.INTERNAL_SHOPIFY_YN.getName()));
			if (sessionMap != null) {
				sessionMap.put(Constants.TRANSACTION_COMPLETE_FLAG.getValue(), Constants.Y_FLAG.getValue());
				sessionMap.invalidate();
			}
			responseMap.remove(FieldType.TXN_KEY.getName());
			responseMap.remove(FieldType.ACQUIRER_TYPE.getName());
			responseMap.remove(FieldType.HASH.getName());
			responseCreator.create(responseMap);
			responseCreator.ResponsePost(responseMap);

		} catch (Exception exception) {
			logger.error("Exception", exception);
			return ERROR;
		}
		return Action.NONE;
	}
}
