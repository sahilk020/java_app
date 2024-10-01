package com.pay10.pg.action;

import java.security.spec.KeySpec;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
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
public class AxisBankResponseAction extends AbstractSecureAction implements ServletRequestAware {

	private static Logger logger = LoggerFactory.getLogger(AxisBankResponseAction.class.getName());
	private static final long serialVersionUID = 6155942791032490231L;
	private static byte[] SALT  = new byte[] { -57, -75, -103, -12, 75, 124, -127, 119 };
	private static Map<String,SecretKey> encDecMap = new HashMap<String,SecretKey>();

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

	public AxisBankResponseAction() {
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

			String flag = requestMap.get("flag");
			String merchantId = requestMap.get("merchantId");
			String responseparams = requestMap.get("responseparams");
			
			String responseparamsArray [] = responseparams.split(Pattern.quote("||"));
			String encryptedResponse = responseparamsArray[1];
			
			String encryptionKey = (String) sessionMap.get(FieldType.TXN_KEY.getName());
			SecretKey secretKey = null;
			
			if (encDecMap.get(encryptionKey) != null) {
				secretKey = encDecMap.get(encryptionKey);
			}
			else {
				try {
		            SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
		            KeySpec keySpec = new PBEKeySpec(encryptionKey.toCharArray(), SALT, 65536, 256);
		            SecretKey secretKeyTemp = secretKeyFactory.generateSecret(keySpec);
		            secretKey = new SecretKeySpec(secretKeyTemp.getEncoded(), "AES");
		    	}
		    	catch(Exception e) {
		    		e.printStackTrace();
		    	}
				encDecMap.put(encryptionKey, secretKey);
			}
		 
			String decryptedResponse = null;

			Fields fields = new Fields();
			fields.put(FieldType.DIRECPAY_RESPONSE_FIELD.getName(), decryptedResponse);
			fields.logAllFields("Direcpay Response Recieved :");

			Object fieldsObj = sessionMap.get("FIELDS");
			if (null != fieldsObj) {
				fields.put((Fields) fieldsObj);
			}

			fields.logAllFields("Direcpay response = " + fields.get(FieldType.TXNTYPE.getName()) + " " + "OrderId = "+ fields.get(FieldType.ORDER_ID.getName()));
			fields.put(FieldType.ACQUIRER_TYPE.getName(), AcquirerType.DIRECPAY.getCode());
			fields.put(FieldType.TXNTYPE.getName(),
					(String) sessionMap.get(FieldType.INTERNAL_ORIG_TXN_TYPE.getName()));
			fields.put((FieldType.PAYMENTS_REGION.getName()),
					(String) sessionMap.get(FieldType.PAYMENTS_REGION.getName()));
			fields.put((FieldType.ADF10.getName()),
					(String) sessionMap.get(FieldType.ADF10.getName()));
			fields.put((FieldType.ADF9.getName()),
					(String) sessionMap.get(FieldType.ADF9.getName()));
			fields.put((FieldType.CARD_HOLDER_TYPE.getName()),
					(String) sessionMap.get(FieldType.CARD_HOLDER_TYPE.getName()));
			fields.put(FieldType.INTERNAL_VALIDATE_HASH_YN.getName(), "N");
			fields.put((FieldType.OID.getName()), (String) sessionMap.get(FieldType.OID.getName()));
			Map<String, String> response = transactionControllerServiceProvider.transact(fields,
					Constants.TXN_WS_DIRECPAY_PROCESSOR.getValue());
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
