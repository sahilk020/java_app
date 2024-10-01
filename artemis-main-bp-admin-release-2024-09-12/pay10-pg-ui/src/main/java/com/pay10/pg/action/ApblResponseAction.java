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
 * @author Shaiwal
 *
 */
public class ApblResponseAction extends AbstractSecureAction implements ServletRequestAware {

	private static Logger logger = LoggerFactory.getLogger(ApblResponseAction.class.getName());
	private static final long serialVersionUID = 6155942791032490231L;

	@Autowired
	TransactionControllerServiceProvider transactionControllerServiceProvider;


	@Autowired
	private ResponseCreator responseCreator;

	private Fields responseMap = null;
	private HttpServletRequest httpRequest;

	@Override
	public void setServletRequest(HttpServletRequest hReq) {
		this.httpRequest = hReq;
	}

	public ApblResponseAction() {
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

			StringBuilder sb = new StringBuilder();
			
			for (Map.Entry<String, String> entry : requestMap.entrySet()) {
				String k = entry.getKey();
				String v = entry.getValue();
				sb.append(k);
				sb.append("=");
				sb.append(v);
				sb.append(";");
			}

			
			Fields fields = new Fields();
			fields.put(FieldType.APBL_RESPONSE_FIELD.getName(), sb.toString());
			fields.logAllFields("APBL Response Recieved :  " +  sb.toString());

			Object fieldsObj = sessionMap.get("FIELDS");
			if (null != fieldsObj) {
				fields.put((Fields) fieldsObj);
			}

			fields.logAllFields("Updated Fields Apbl TxnType = " + fields.get(FieldType.TXNTYPE.getName()) + " " + "Txn id = "+ fields.get(FieldType.TXN_ID.getName()));
			fields.put(FieldType.ACQUIRER_TYPE.getName(), AcquirerType.APBL.getCode());
			fields.put(FieldType.TXNTYPE.getName(),	(String) sessionMap.get(FieldType.INTERNAL_ORIG_TXN_TYPE.getName()));
			fields.put((FieldType.PAYMENTS_REGION.getName()),(String) sessionMap.get(FieldType.PAYMENTS_REGION.getName()));
			fields.put((FieldType.CARD_HOLDER_TYPE.getName()),(String) sessionMap.get(FieldType.CARD_HOLDER_TYPE.getName()));
			fields.put(FieldType.INTERNAL_VALIDATE_HASH_YN.getName(), "N");
			fields.put((FieldType.OID.getName()), (String) sessionMap.get(FieldType.OID.getName()));
			Map<String, String> response = transactionControllerServiceProvider.transact(fields,Constants.TXN_WS_APBL_PROCESSOR.getValue());
			
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
			
			responseMap.put(FieldType.INTERNAL_SHOPIFY_YN.getName(),(String) sessionMap.get(FieldType.INTERNAL_SHOPIFY_YN.getName()));
			
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
			logger.error("Exception", exception);
			return ERROR;
		}
		return Action.NONE;
	}
}
