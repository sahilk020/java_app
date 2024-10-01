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

import com.pay10.commons.api.TransactionControllerServiceProvider;
import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.exception.SystemException;
import com.pay10.commons.user.User;
import com.pay10.commons.user.UserDao;
import com.pay10.commons.util.AcquirerType;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.StatusType;
import com.pay10.pg.action.service.RetryTransactionProcessor;
import com.pay10.pg.core.util.ResponseCreator;


/**
 * @author Sunil, Neeraj
 *
 */
public class MigsResponseAction extends AbstractSecureAction implements
		ServletRequestAware {

	private  Logger logger = LoggerFactory.getLogger(MigsResponseAction.class
			.getName());
	private static final long serialVersionUID = 2943629615913246334L;
	
	private Fields responseMap = null;

	private HttpServletRequest httpRequest;
    @Autowired
	TransactionControllerServiceProvider transactionControllerServiceProvider;
	
	@Autowired
	private UserDao userDao;
	
	@Autowired
	private RetryTransactionProcessor retryTransactionProcessor;
	
	/*
	 * @Autowired private SendTransactionEmail sendTransactionEmail;
	 */
	
	@Autowired
	private ResponseCreator responseCreator;
	
	public MigsResponseAction() {
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
			Map<String, String> migsResponseMap = new HashMap<String, String>();
			
			for (Entry<String, String[]> entry : fieldMapObj.entrySet()) {
				try {
					migsResponseMap.put(entry.getKey().trim(),
							entry.getValue()[0].trim());

				} catch (ClassCastException classCastException) {
					logger.error("Exception", classCastException);
				}
			}
			Fields fields = (Fields) sessionMap.get(Constants.FIELDS.getValue());
			String oId= migsResponseMap.get("vpc_OrderInfo");
			String currencyCode= fields.get("CURRENCY_CODE");
			String vpc_locale= migsResponseMap.get("vpc_Locale");
			fields.putAll(migsResponseMap);
			fields.logAllFields("Migs 3DS map: ");
			logger.info("MIGS ResponseMap Received ->3DS map " + migsResponseMap);
			try {
				
				fields.put((FieldType.INTERNAL_ORIG_TXN_TYPE.getName()),
						(String) sessionMap
								.get(FieldType.INTERNAL_ORIG_TXN_TYPE
										.getName()));
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
				fields.put(FieldType.OID.getName(), oId);
				fields.put(FieldType.CURRENCY_CODE.getName(),currencyCode);
				fields.put(FieldType.VPC_LOCALE.getName(), vpc_locale);
				fields.put(FieldType.ACQUIRER_TYPE.getName(), AcquirerType.AXISMIGS.getCode());
				
				Map<String, String> response = transactionControllerServiceProvider.migsTransact(fields, Constants.TXN_MIGS_PROCESSOR.getValue());
				responseMap = new Fields(response);
				
				String crisFlag = (String) sessionMap.get(FieldType.INTERNAL_IRCTC_YN.getName());
				if(StringUtils.isNotBlank(crisFlag)){
					responseMap.put(FieldType.INTERNAL_IRCTC_YN.getName(), crisFlag);
				}	
								
				// TODO... run time transaction failed retry to payment page
				/*fields.put(FieldType.ACQUIRER_TYPE.getName(),
						(String) sessionMap.get(FieldType.ACQUIRER_TYPE
								.getName()));*/
				
				// Fetch user for retryTransaction ,SendEmailer and SmsSenser
				
				User user = userDao.getUserClass(responseMap.get(FieldType.PAY_ID.getName()));
				
				//TODO.. Retry Transaction Temporary block when retry transaction will be checked then will do work on this.
				// Retry Transaction Block Start
				/*if (!responseMap.get(FieldType.RESPONSE_CODE.getName()).equals(
						ErrorType.SUCCESS.getCode())) {
					
					if (retryTransactionProcessor.retryTransaction(fields,
							sessionMap, user)) {
						addActionMessage(CrmFieldConstants.RETRY_TRANSACTION
								.getValue());
						return "paymentPage";
					}
				}*/
				// Retry Transaction Block End

				// Sending Email for Transaction Status to merchant
				//TODO countryCode put in Fiels object.........................................
				String countryCode = (String) sessionMap.get(FieldType.INTERNAL_CUST_COUNTRY_NAME.getName());
				
			//	sendTransactionEmail.sendEmail(responseMap);
				
				//emailBuilder.postMan(fields, countryCode, user);

				responseMap.put(FieldType.RETURN_URL.getName(),
						(String) sessionMap.get(FieldType.RETURN_URL.getName()));
			} catch (SystemException systemException) {
				logger.error("Exception", systemException);
				responseMap.put(FieldType.STATUS.getName(),
						StatusType.ERROR.getName());
				responseMap.put(FieldType.RESPONSE_MESSAGE.getName(),
						ErrorType.INTERNAL_SYSTEM_ERROR.getResponseMessage());
				responseMap.put(FieldType.RESPONSE_CODE.getName(),
						ErrorType.INTERNAL_SYSTEM_ERROR.getResponseCode());
				String crisFlag = (String) sessionMap.get(FieldType.INTERNAL_IRCTC_YN.getName());
				if(StringUtils.isNotBlank(crisFlag)){
					responseMap.put(FieldType.INTERNAL_IRCTC_YN.getName(), crisFlag);
				}	
				responseCreator.create(responseMap);
				responseCreator.ResponsePost(responseMap);
				return NONE;
			}
			responseMap.put(FieldType.INTERNAL_SHOPIFY_YN.getName(),
					(String) sessionMap.get(FieldType.INTERNAL_SHOPIFY_YN
							.getName()));

			if (sessionMap != null) {
				sessionMap.put(Constants.TRANSACTION_COMPLETE_FLAG.getValue(),
						Constants.Y_FLAG.getValue());
				sessionMap.invalidate();
			}
			
			responseCreator.ResponsePost(responseMap);
		} catch (Exception exception) {
			logger.error("Exception", exception);
			return ERROR;
		}
		return NONE;
	}
}
