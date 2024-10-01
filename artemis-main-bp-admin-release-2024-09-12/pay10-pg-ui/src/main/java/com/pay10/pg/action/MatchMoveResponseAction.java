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

public class MatchMoveResponseAction extends AbstractSecureAction implements ServletRequestAware {


	private static final long serialVersionUID = 3767554137946748319L;
	private static Logger logger = LoggerFactory.getLogger(MatchMoveResponseAction.class.getName());


	@Autowired
	TransactionControllerServiceProvider transactionControllerServiceProvider;


	@Autowired
	private ResponseCreator responseCreator;
	
	
	private Fields responseMap = null;
	private HttpServletRequest httpRequest;

	@Override
	public void setServletRequest(HttpServletRequest hReq) {
		httpRequest = hReq;
	}

	@Override
	public String execute() {
		
		logger.info("inside the MatchMoveResponseAction");
	
		try {
			                  
        Map<String, String[]> fieldMapObj = httpRequest.getParameterMap();
		
		Map<String, String> requestMap = new HashMap<String, String>();

		for (Entry<String, String[]> entry : fieldMapObj.entrySet()) {
			try {
				requestMap.put(entry.getKey().trim(), entry.getValue()[0].trim());
			} catch (ClassCastException classCastException) {
				logger.error("Exception", classCastException);
			}
		}

		    logger.info("Response Recieved from Matchmove Wallet" +requestMap);
		   
	     	Fields fields = new Fields();
			
			fields.logAllFields("MatchMove Response Recieved :");

			Object fieldsObj = sessionMap.get("FIELDS");
			if (null != fieldsObj) {
				
				fields.put((Fields) fieldsObj);
			}

			fields.put(FieldType.ACQ_ID.getName(),requestMap.get("checkout_ref_id"));
			fields.put(FieldType.RRN.getName(),requestMap.get("checkout_ref_id"));
			fields.put(FieldType.PG_RESP_CODE.getName(),requestMap.get("response_code"));
			fields.put(FieldType.PG_TXN_MESSAGE.getName(),requestMap.get("response_message"));
			
			fields.logAllFields("Updated matchmove validated mobile number Recieved Map TxnType = " + fields.get(FieldType.TXNTYPE.getName()) + " " + "Txn id = " + fields.get(FieldType.TXN_ID.getName()));
			fields.put(FieldType.ACQUIRER_TYPE.getName(), AcquirerType.MATCHMOVE.getCode());
			fields.put(FieldType.TXNTYPE.getName(),
					(String) sessionMap.get(FieldType.INTERNAL_ORIG_TXN_TYPE.getName()));
			fields.put(FieldType.INTERNAL_VALIDATE_HASH_YN.getName(), "N");
			fields.put((FieldType.PAYMENTS_REGION.getName()),
					(String) sessionMap.get(FieldType.PAYMENTS_REGION.getName()));
			fields.put((FieldType.CARD_HOLDER_TYPE.getName()),
					(String) sessionMap.get(FieldType.CARD_HOLDER_TYPE.getName()));
			fields.put((FieldType.OID.getName()), (String) sessionMap.get(FieldType.OID.getName()));
			Map<String, String> response = transactionControllerServiceProvider.transact(fields,
					Constants.TXN_WS_MATCHMOVE_PROCESSOR.getValue());
			responseMap = new Fields(response);
			
			String crisFlag = (String) sessionMap.get(FieldType.INTERNAL_IRCTC_YN.getName());
			if(StringUtils.isNotBlank(crisFlag)){
				responseMap.put(FieldType.INTERNAL_IRCTC_YN.getName(), crisFlag);
			}

			

			fields.put(FieldType.RETURN_URL.getName(), (String) sessionMap.get(FieldType.RETURN_URL.getName()));
			String cardIssuerBank = (String) sessionMap.get(FieldType.INTERNAL_CARD_ISSUER_BANK.getName());
			String cardIssuerCountry = (String) sessionMap.get(FieldType.INTERNAL_CARD_ISSUER_COUNTRY.getName());
			if(StringUtils.isNotBlank(cardIssuerBank)){
				responseMap.put(FieldType.CARD_ISSUER_BANK.getName(), cardIssuerBank);
			}
			if(StringUtils.isNotBlank(cardIssuerCountry)){
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
			responseMap.remove(FieldType.IS_INTERNAL_REQUEST.getName());
			responseCreator.create(responseMap);
			responseCreator.ResponsePost(responseMap);

		} catch (Exception exception) {
			logger.error("Exception", exception);
			return ERROR;
		}
		return Action.NONE;
	}

	
	
	

}
