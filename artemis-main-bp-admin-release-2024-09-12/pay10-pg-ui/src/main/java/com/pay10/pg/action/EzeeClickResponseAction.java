package com.pay10.pg.action;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.interceptor.ServletRequestAware;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.opensymphony.xwork2.Action;
import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.exception.SystemException;
import com.pay10.commons.user.User;
import com.pay10.commons.user.UserDao;
import com.pay10.commons.util.AcquirerType;
import com.pay10.commons.util.CrmFieldConstants;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.pg.action.service.RetryTransactionProcessor;
import com.pay10.pg.core.util.ResponseCreator;

/**
 * @author Puneet, neeraj
 *
 */
public class EzeeClickResponseAction extends AbstractSecureAction implements
		ServletRequestAware {

	private static final long serialVersionUID = 3630584240564345729L;
	private static Logger logger = LoggerFactory.getLogger(EzeeClickResponseAction.class.getName());

	private HttpServletRequest httpRequest;
	//@Autowired
//	private SecurityProcessor securityProcessor; TODO
	@Autowired
	private UserDao userDao;
	@Autowired
	private RetryTransactionProcessor retryTransactionProcessor;
	/*
	 * @Autowired private SendTransactionEmail sendTransactionEmail;
	 */
	@Autowired
	private ResponseCreator responseCreator;
	@Override
	@SuppressWarnings("unchecked")
	public String execute() {
		try {
			Map<String, String[]> fieldMapObj = httpRequest.getParameterMap();
			Map<String, String> responseMap = new HashMap<String, String>();
		//	String response = ((String[]) fieldMapObj  TODO
		//			.get(Constants.EZEE_CLICK_RESPONSE))[0].trim();
		//	responseMap.put(Constants.EZEE_CLICK_RESPONSE, response);
			Fields fields = (Fields) sessionMap
					.get(com.pay10.commons.util.Constants.FIELDS.getValue());
			fields.put(FieldType.ACQUIRER_TYPE.getName(),
					AcquirerType.EZEECLICK.getCode());
			
		//	securityProcessor.authenticate(fields); TODO
		//	securityProcessor.addAcquirerFields(fields);
			fields.putAll(responseMap);

			try {
				/*Processor processor = new EzeeClickResponseProcessor();
				processor.preProcess(fields);
				processor.process(fields);*/

				// Fetch user for Re-try Transaction ,Emailer and Sms Processor

				
				User user = userDao.getUserClass(fields.get(FieldType.PAY_ID
						.getName()));
				// Retry Transaction Block Start
				if (!fields.get(FieldType.RESPONSE_CODE.getName()).equals(
						ErrorType.SUCCESS.getCode())) {
					
					if (retryTransactionProcessor.retryTransaction(fields,
							sessionMap, user, fields)) {
						addActionMessage(CrmFieldConstants.RETRY_TRANSACTION
								.getValue());
						return "paymentPage";
					}
				}
				// Retry Transaction Block End

				// Sending Email for Transaction Status to merchant
				fields.put(FieldType.PAY_ID.getName(),
						(String) sessionMap.get(FieldType.PAY_ID.getName()));
				fields.put(FieldType.ACQUIRER_TYPE.getName(),
						(String) sessionMap.get(FieldType.ACQUIRER_TYPE
								.getName()));
				
				String countryCode = (String) sessionMap
						.get(FieldType.INTERNAL_CUST_COUNTRY_NAME.getName());
				
     //TODO countryCode put in Fiels object.........................................
		//		sendTransactionEmail.sendEmail(fields);
				//emailBuilder.postMan(fields, countryCode, user);
				
				fields.put(FieldType.RETURN_URL.getName(),
						(String) sessionMap.get(FieldType.RETURN_URL.getName()));

			//	processor.postProcess(fields); TODO
			} catch (SystemException systemException) {
				// TODO....update fields as error??
				logger.error("Exception", systemException);
			}
			fields.put(FieldType.INTERNAL_SHOPIFY_YN.getName(),
					(String) sessionMap.get(FieldType.INTERNAL_SHOPIFY_YN
							.getName()));
			if (sessionMap != null) {
				sessionMap.clear();
			}
			
			responseCreator.ResponsePost(fields);
		} catch (Exception exception) {
			logger.error("Exception", exception);
		}
		return Action.NONE;
	}

	@Override
	public void setServletRequest(HttpServletRequest hReq) {
		this.httpRequest = hReq;
	}
}
