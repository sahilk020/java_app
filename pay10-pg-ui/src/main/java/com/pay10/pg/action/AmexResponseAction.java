package com.pay10.pg.action;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.interceptor.ServletRequestAware;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.exception.SystemException;
import com.pay10.commons.user.User;
import com.pay10.commons.user.UserDao;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.CrmFieldConstants;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.StatusType;
import com.pay10.pg.action.service.RetryTransactionProcessor;
import com.pay10.pg.core.util.Processor;
import com.pay10.pg.core.util.ResponseCreator;

/**
 * @author Sunil, Neeraj
 *
 */
public class AmexResponseAction extends AbstractSecureAction implements
		ServletRequestAware {

	private static Logger logger = LoggerFactory.getLogger(AmexResponseAction.class.getName());
	private static final long serialVersionUID = 2943629615913246334L;

	private HttpServletRequest httpRequest;
	//@Autowired
//	private SecurityProcessor securityProcessor;  TODO
	@Autowired
	@Qualifier("responseProcessor")
	private Processor processor;
	
	@Autowired
	private UserDao userDao;
	
	@Autowired
	private RetryTransactionProcessor retryTransactionProcessor;
	
	/*
	 * @Autowired private SendTransactionEmail sendTransactionEmail;
	 */
	
	@Autowired
	private ResponseCreator responseCreator;
	
	public AmexResponseAction() {
	}

	@Override
	public void setServletRequest(HttpServletRequest hReq) {
		this.httpRequest = hReq;
	}

	@Override
	@SuppressWarnings("unchecked")
	public String execute() {
		try {
			Map<String, String[]> fieldMapObj = httpRequest.getParameterMap();
			Map<String, String> responseMap = new HashMap<String, String>();
			for (Entry<String, String[]> entry : fieldMapObj.entrySet()) {
				try {
					responseMap.put(entry.getKey().trim(),
							entry.getValue()[0].trim());

				} catch (ClassCastException classCastException) {
					logger.error("Exception", classCastException);
				}
			}
			Fields fields = (Fields) sessionMap.get(Constants.FIELDS.getValue());
			fields.putAll(responseMap);
			
		//	securityProcessor.authenticate(fields); TODO
		//	securityProcessor.addAcquirerFields(fields);

			fields.logAllFields("Amex 3DS map: ");
			try {

				fields.put(FieldType.CARD_NUMBER.getName(), (String) sessionMap
						.get(FieldType.CARD_NUMBER.getName()));
				fields.put(FieldType.CARD_EXP_DT.getName(), (String) sessionMap
						.get(FieldType.CARD_EXP_DT.getName()));
				fields.put(FieldType.CVV.getName(),
						(String) sessionMap.get(FieldType.CVV.getName()));
				fields.put((FieldType.INTERNAL_CARD_ISSUER_BANK.getName()),
						(String) sessionMap
								.get(FieldType.INTERNAL_CARD_ISSUER_BANK
										.getName()));
				fields.put((FieldType.INTERNAL_CARD_ISSUER_COUNTRY.getName()),
						(String) sessionMap
								.get(FieldType.INTERNAL_CARD_ISSUER_COUNTRY
										.getName()));
				fields.put((FieldType.PAYMENTS_REGION.getName()),
						(String) sessionMap.get(FieldType.PAYMENTS_REGION.getName()));
				fields.put((FieldType.CARD_HOLDER_TYPE.getName()),
						(String) sessionMap.get(FieldType.CARD_HOLDER_TYPE.getName()));

				fields.put(FieldType.OID.getName(),
						fields.get(FieldType.INTERNAL_ORIG_TXN_ID.getName()));

				
				processor.preProcess(fields);
				processor.process(fields);
				// run time transaction failed retry to payment page
				fields.put(FieldType.ACQUIRER_TYPE.getName(),
						(String) sessionMap.get(FieldType.ACQUIRER_TYPE
								.getName()));
				
				// Fetch user for retryTransaction ,SendEmailer and SmsSenser

				
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
    //TODO countryCode put in Fiels object.........................................
				String countryCode = (String) sessionMap.get(FieldType.INTERNAL_CUST_COUNTRY_NAME.getName());
				
			//	sendTransactionEmail.sendEmail(fields);
				
				//emailBuilder.postMan(fields, countryCode, user);

				fields.put(FieldType.RETURN_URL.getName(),
						(String) sessionMap.get(FieldType.RETURN_URL.getName()));
				fields.remove(FieldType.HASH.getName());
				processor.postProcess(fields);
			} catch (SystemException systemException) {
				logger.error("Exception", systemException);
				fields.put(FieldType.STATUS.getName(),
						StatusType.ERROR.getName());
				fields.put(FieldType.RESPONSE_MESSAGE.getName(),
						ErrorType.INTERNAL_SYSTEM_ERROR.getResponseMessage());
				fields.put(FieldType.RESPONSE_CODE.getName(),
						ErrorType.INTERNAL_SYSTEM_ERROR.getResponseCode());
				
				responseCreator.create(fields);
				responseCreator.ResponsePost(fields);
				return NONE;
			}
			fields.put(FieldType.INTERNAL_SHOPIFY_YN.getName(),
					(String) sessionMap.get(FieldType.INTERNAL_SHOPIFY_YN
							.getName()));

			if (sessionMap != null) {
				sessionMap.put(Constants.TRANSACTION_COMPLETE_FLAG.getValue(),
						Constants.Y_FLAG.getValue());
				sessionMap.invalidate();
			}
			
			responseCreator.ResponsePost(fields);
		} catch (Exception exception) {
			logger.error("Exception", exception);
			return ERROR;
		}
		return NONE;
	}
}
