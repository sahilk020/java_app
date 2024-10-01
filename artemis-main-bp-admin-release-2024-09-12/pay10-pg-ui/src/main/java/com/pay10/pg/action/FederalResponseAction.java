package com.pay10.pg.action;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.DatatypeConverter;

import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.opensymphony.xwork2.Action;
import com.pay10.commons.api.TransactionControllerServiceProvider;
import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.exception.SystemException;
import com.pay10.commons.util.AcquirerType;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.PropertiesManager;
import com.pay10.pg.core.util.ResponseCreator;
import com.pay10.pg.core.util.TransactionResponser;

/**
 * @author Rahul
 *
 */
public class FederalResponseAction extends AbstractSecureAction implements ServletRequestAware {
	
	
	@Autowired
	TransactionControllerServiceProvider transactionControllerServiceProvider;

	private static final long serialVersionUID = -6828584232906675035L;
	private static Logger logger = LoggerFactory.getLogger(FederalResponseAction.class.getName());
	
	private Fields responseMap = null;
	private HttpServletRequest httpRequest;
	private String redirectUrl;
	private Integer count;

	public FederalResponseAction() {
	}

	@Override
	public void setServletRequest(HttpServletRequest hReq) {
		this.httpRequest = hReq;
	}

	@Autowired
	private TransactionResponser transactionResponser;

	/*
	 * @Autowired private EmailBuilder emailBuilder;
	 */
	@Autowired
	private ResponseCreator responseCreator;

	@Override
	@SuppressWarnings("unchecked")
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

			for (Map.Entry<String,String> entry : requestMap.entrySet()) {
				  String key = entry.getKey();
				  String value = entry.getValue();
				logger.info("response map after trim :  key = " +key+"      value = "+ value);
				}
			
			Fields fields = new Fields();
			Object fieldsObj = sessionMap.get("FIELDS");
			if (null != fieldsObj) {
				fields.put((Fields) fieldsObj);
			}
			
			String eci = requestMap.get(Constants.FEDERAL_ECI.getValue());
			String cavv = requestMap.get(Constants.FEDERAL_CAVV.getValue());
			String mpiErrorCode = requestMap.get(Constants.FEDERAL_MPI_ERROR_CODE.getValue());
			String xid = requestMap.get(Constants.FEDERAL_XID.getValue());
			String amount = requestMap.get(Constants.FEDERAL_AMOUNT.getValue());
			String receivedHash = requestMap.get(Constants.FEDERAL_MERCHANT_HASH.getValue());
			String md = requestMap.get(Constants.FEDERAL_MD.getValue());
			String currency = requestMap.get(Constants.FEDERAL_CURRENCY.getValue());
			String ID = requestMap.get(Constants.FEDERAL_ID.getValue());
			String status = requestMap.get(Constants.FEDERAL_STATUS.getValue());
			
			fields.put(FieldType.KEY_ID.getName(), ID);
			//fields.put(FieldType.AMOUNT.getName(), amount);
			fields.put(FieldType.CURRENCY_CODE.getName(), currency);
			fields.put(FieldType.RESPONSE_CODE.getName(), mpiErrorCode);
			
			fields.put(FieldType.FEDERAL_MPI_ID.getName(), ID);
			fields.put(FieldType.FEDERAL_MD.getName(), md);
			fields.put(FieldType.FEDERAL_ECI.getName(), eci);
			fields.put(FieldType.FEDERAL_XID.getName(), xid);
			fields.put(FieldType.FEDERAL_CAVV.getName(), cavv);
			fields.put(FieldType.FEDERAL_STATUS.getName(), status);
						
				
			String calculatedHash = getHash(fields);
			if (!calculatedHash.equals(receivedHash)) {
				StringBuilder hashMessage = new StringBuilder("Merchant hash =");
				hashMessage.append(receivedHash);
				hashMessage.append(", Calculated Hash=");
				hashMessage.append(calculatedHash);
				
				logger.error(hashMessage.toString());
				if(!PropertiesManager.propertiesMap.get("alloFailedResponseHash").equals("Y")){
					return handleInvalidHash();
				}
			}
			
			if(!mpiErrorCode.equals(Constants.SUCCESS_CODE.getValue()) || !status.equals(Constants.Y_FLAG.getValue())){
				logger.info("Failure received from Fedral 3DS system");
				Fields field = (Fields) sessionMap.get(Constants.FIELDS.getValue());
				field.put(FieldType.INTERNAL_VALIDATE_HASH_YN.getName(), "N");
				field.put(FieldType.FEDERAL_MPIERROR_CODE.getName(), mpiErrorCode);
				field.put(FieldType.FEDERAL_STATUS.getName(), status);
				field.put((FieldType.OID.getName()), (String) sessionMap.get(FieldType.OID.getName()));
				field.put(FieldType.ORIG_TXN_ID.getName(),
						(String) sessionMap.get(FieldType.INTERNAL_ORIG_TXN_ID.getName()));
				fields.put((FieldType.PAYMENTS_REGION.getName()),
						(String) sessionMap.get(FieldType.PAYMENTS_REGION.getName()));
				fields.put((FieldType.CARD_HOLDER_TYPE.getName()),
						(String) sessionMap.get(FieldType.CARD_HOLDER_TYPE.getName()));
				field.put(FieldType.TXNTYPE.getName(),
						(String) sessionMap.get(FieldType.INTERNAL_ORIG_TXN_TYPE.getName()));
				//field.put(FieldType.FEDERAL_RESPONSE_MESSAGE.getName(), ErrorType.INVALID_FIELD.getResponseMessage());
				Map<String, String> response = transactionControllerServiceProvider.transact(field, Constants.TXN_WS_FEDERAL_RETURN_URL.getValue());
				Fields processedResponse = new Fields(response);
				String crisFlag = (String) sessionMap.get(FieldType.INTERNAL_IRCTC_YN.getName());
				if(StringUtils.isNotBlank(crisFlag)){
					processedResponse.put(FieldType.INTERNAL_IRCTC_YN.getName(), crisFlag);
				}
				responseCreator.create(processedResponse);
				transactionResponser.removeInvalidResponseFields(processedResponse);
				responseCreator.ResponsePost(processedResponse);
				return Action.NONE;
			}
			
			fields.logAllFields("Updated 3DS Recieved Map :");
			fields.put(FieldType.TXNTYPE.getName(),
					(String) sessionMap.get(FieldType.INTERNAL_ORIG_TXN_TYPE.getName()));
			fields.put(FieldType.CARD_NUMBER.getName(),
					(String) sessionMap.get(FieldType.CARD_NUMBER.getName()));
			fields.put(FieldType.CARD_EXP_DT.getName(),
					(String) sessionMap.get(FieldType.CARD_EXP_DT.getName()));
			fields.put(FieldType.CVV.getName(),
					(String) sessionMap.get(FieldType.CVV.getName()));
			fields.put((FieldType.OID.getName()), (String) sessionMap.get(FieldType.OID.getName()));
			fields.put(FieldType.ORIG_TXN_ID.getName(),
					(String) sessionMap.get(FieldType.INTERNAL_ORIG_TXN_ID.getName()));
			fields.put(FieldType.INTERNAL_VALIDATE_HASH_YN.getName(), "N");
			fields.put(FieldType.ACQUIRER_TYPE.getName(), AcquirerType.FEDERAL.getCode());
								
			Map<String, String> response = transactionControllerServiceProvider.transact(fields, Constants.TXN_WS_INTERNAL.getValue());
			responseMap = new Fields(response);
			String crisFlag = (String) sessionMap.get(FieldType.INTERNAL_IRCTC_YN.getName());
			if(StringUtils.isNotBlank(crisFlag)){
				responseMap.put(FieldType.INTERNAL_IRCTC_YN.getName(), crisFlag);
			}

			Object previousFields = sessionMap.get(Constants.FIELDS.getValue());
			Fields sessionFields = null;
			if (null != previousFields) {
				sessionFields = (Fields) previousFields;
			} else {
				// TODO: Handle
			}

			// Sending Email for Transaction Status to merchant

			String countryCode = (String) sessionMap.get(FieldType.INTERNAL_CUST_COUNTRY_NAME.getName());

			// TODO emailBuilder.postMan(responseMap, countryCode, user);

			sessionFields.put(responseMap);
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
			responseMap.remove(FieldType.TXN_KEY.getName());
			responseMap.remove(FieldType.ACQUIRER_TYPE.getName());
			responseCreator.ResponsePost(responseMap);

		} catch (Exception exception) {
			logger.error("Exception", exception);
			return ERROR;
		}
		return Action.NONE;
	}
	
	public String handleInvalidHash() throws SystemException{
		Fields field = (Fields) sessionMap.get(Constants.FIELDS.getValue());
		field.put(FieldType.INTERNAL_VALIDATE_HASH_YN.getName(), "N");
		field.put(FieldType.FEDERAL_RESPONSE_MESSAGE.getName(), ErrorType.INVALID_HASH.getResponseMessage());
		field.put(FieldType.FEDERAL_MPIERROR_CODE.getName(), ErrorType.INVALID_HASH.getResponseCode());
		field.put(FieldType.TXNTYPE.getName(),
				(String) sessionMap.get(FieldType.INTERNAL_ORIG_TXN_TYPE.getName()));
		field.put(FieldType.OID.getName(),
				(String) sessionMap.get(FieldType.OID.getName()));
		Map<String, String> response = transactionControllerServiceProvider.transact(field, Constants.TXN_WS_FEDERAL_RETURN_URL.getValue());
		Fields processedResponse = new Fields(response);
		String crisFlag = (String) sessionMap.get(FieldType.INTERNAL_IRCTC_YN.getName());
		if(StringUtils.isNotBlank(crisFlag)){
			processedResponse.put(FieldType.INTERNAL_IRCTC_YN.getName(), crisFlag);
		}
		responseCreator.create(processedResponse);
		transactionResponser.removeInvalidResponseFields(processedResponse);
		responseCreator.ResponsePost(processedResponse);
		return Action.NONE;
	}
	
	public String getHash(Fields fields) throws SystemException {
		String response = null;
		String cavv = fields.get(FieldType.FEDERAL_CAVV.getName());
		
		String hashKey = (String) sessionMap.get(FieldType.TXN_KEY.getName());
		StringBuilder request = new StringBuilder();
		request.append(fields.get(FieldType.FEDERAL_MPI_ID.getName()));
		request.append(Constants.DIRECPAY_SEPARATOR.getValue());
		request.append(fields.get(FieldType.FEDERAL_ECI.getName()));
		request.append(Constants.DIRECPAY_SEPARATOR.getValue());
		request.append(fields.get(FieldType.FEDERAL_STATUS.getName()));
		request.append(Constants.DIRECPAY_SEPARATOR.getValue());
		
		if(cavv != null && StringUtils.isNotBlank(cavv)){
		request.append(fields.get(FieldType.FEDERAL_CAVV.getName()));
		request.append(Constants.DIRECPAY_SEPARATOR.getValue());
		}
		request.append(fields.get(FieldType.AMOUNT.getName()));
		request.append(Constants.DIRECPAY_SEPARATOR.getValue());
		request.append(fields.get(FieldType.CURRENCY_CODE.getName()));
		request.append(Constants.DIRECPAY_SEPARATOR.getValue());
		request.append(fields.get(FieldType.RESPONSE_CODE.getName()));
		request.append(Constants.DIRECPAY_SEPARATOR.getValue());
		request.append(fields.get(FieldType.FEDERAL_XID.getName()));
		request.append(Constants.DIRECPAY_SEPARATOR.getValue());
		request.append(fields.get(FieldType.FEDERAL_MD.getName()));
		request.append(Constants.DIRECPAY_SEPARATOR.getValue());
		request.append(hashKey);
		
		MessageDigest messageDigest = null;
		try {
			messageDigest = MessageDigest.getInstance(PropertiesManager.propertiesMap.get("FederalHash_algorithm"));
		} catch (NoSuchAlgorithmException exception) {
			logger.error("Exception in FederalResponseAction "+exception.getMessage());
		}
		 try {
			response = new String(encodeMessageHash(messageDigest.digest(request.toString().getBytes("UTF-8"))));
		} catch (UnsupportedEncodingException exception) {
			logger.error("Exception in FederalResponseAction "+exception.getMessage());
		}
	
		return response;
	}
	
	public static String encodeMessageHash(byte[] data)
	{
	 String res = DatatypeConverter.printBase64Binary(data);
	          
	   return res;
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
