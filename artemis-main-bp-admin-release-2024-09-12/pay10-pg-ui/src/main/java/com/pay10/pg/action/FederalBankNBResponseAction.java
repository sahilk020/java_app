package com.pay10.pg.action;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.crypto.SecretKey;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.opensymphony.xwork2.Action;
import com.pay10.commons.api.TransactionControllerServiceProvider;
import com.pay10.commons.dao.FieldsDao;
import com.pay10.commons.util.AcquirerType;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.pg.core.util.FederalBankNBEncDecService;
import com.pay10.pg.core.util.ResponseCreator;

public class FederalBankNBResponseAction  extends AbstractSecureAction implements ServletRequestAware {

	private static Logger logger = LoggerFactory.getLogger(FederalBankNBResponseAction.class.getName());
	private static final long serialVersionUID = 6155942791032490231L;
	private static byte[] SALT  = new byte[] { -57, -75, -103, -12, 75, 124, -127, 119 };
	private static Map<String,SecretKey> encDecMap = new HashMap<String,SecretKey>();

	@Autowired
	TransactionControllerServiceProvider transactionControllerServiceProvider;

	@Autowired
	private ResponseCreator responseCreator;

	@Autowired
	private FederalBankNBEncDecService federalBankNBEncDecService;  
	
	private Fields responseMap = null;
	private HttpServletRequest httpRequest;
	@Autowired
	private FieldsDao fieldsDao;

	@Override
	public void setServletRequest(HttpServletRequest hReq) {
		this.httpRequest = hReq;
	}

	public FederalBankNBResponseAction() {
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
					logger.error("1- FederalBankNBResponseAction Exception", classCastException);
				}
			}

			logger.info("FederalBankNBResponseAction requestMap >>> "+requestMap.toString());
						
			String decryptedResponse = "";
			String enc = requestMap.get("encrypted_data");
			String decryptionKey = (String) sessionMap.get(FieldType.ADF3.getName());
			
			logger.info("FederalBankNBResponseAction Response encrypted_data :: " + enc);
			if (StringUtils.isNotBlank(enc) && StringUtils.isNotBlank(decryptionKey)) {	
				decryptedResponse = FederalBankNBEncDecService.decrypt(enc,decryptionKey);
			}
			
			Fields fields = new Fields();
			Object fieldsObj = sessionMap.get("FIELDS");
			if (null != fieldsObj) {
				fields.put((Fields) fieldsObj);
			}
			
			String pgRefNum = null; 
			if(decryptedResponse != null && decryptedResponse.startsWith("{")) {
				JSONObject resJson = new JSONObject(decryptedResponse);
				if (decryptedResponse.contains("order_id")) {
					pgRefNum = resJson.getString("order_id");
				}
			}
			
			if(StringUtils.isNotBlank(pgRefNum)) {
				
				fields = fieldsDao.getPreviousForPgRefNum(pgRefNum);
				
				String internalRequestFields = fields.get(FieldType.INTERNAL_REQUEST_FIELDS.getName());
				String[] paramaters = internalRequestFields.split("~");
				Map<String, String> paramMap = new HashMap<String, String>();
				
				
				//db
				for (String param : paramaters) {				    
					String[] parameterPair = param.split("=");
					if (parameterPair.length > 1 ) {
					    if(parameterPair[0].trim().equalsIgnoreCase("RETURN_URL")) {
					        String[] tempArrayRetURL=param.split("=",2);
					        logger.info(" Split_RETURN_URL : "+tempArrayRetURL[0].trim()+"  :     "+tempArrayRetURL[1].trim());
					        paramMap.put(tempArrayRetURL[0].trim(), tempArrayRetURL[1].trim());
					    }else {
					        paramMap.put(parameterPair[0].trim(), parameterPair[1].trim());    
					    }
						
					}
				}
				
				/*
				 * for (String param : paramaters) { String[] parameterPair = param.split("=");
				 * if (parameterPair.length > 1) { paramMap.put(parameterPair[0].trim(),
				 * parameterPair[1].trim()); } }
				 */
				
				if(StringUtils.isBlank(fields.get(FieldType.INTERNAL_CUST_IP.getName()))) {
					String ipAddress = fieldsDao.getIpFromSTB(fields.get(FieldType.OID.getName()));
					
					if(StringUtils.isBlank(ipAddress)) {
						ipAddress = fieldsDao.getIPFromInitiateRequest(fields.get(FieldType.OID.getName()));
					}
					
					fields.put(FieldType.INTERNAL_CUST_IP.getName(), ipAddress);
					logger.info("Cust ipaddress in FederalBank-NB Acquirer = {}", ipAddress);
				}
				
				fields.put(FieldType.RETURN_URL.getName(), paramMap.get(FieldType.RETURN_URL.getName()));
				sessionMap.put(FieldType.RETURN_URL.getName(), paramMap.get(FieldType.RETURN_URL.getName()));
			}
			
			fields.put(FieldType.FEDERALBANK_NB_RESPONSE_FIELD.getName(), decryptedResponse);
			fields.logAllFields(
                "FederalBank Net Banking response - "
                + fields.get(FieldType.TXNTYPE.getName())
                + " "
                + "OrderId = " 
                + fields.get(FieldType.ORDER_ID.getName())
            );
			
			fields.put(FieldType.ACQUIRER_TYPE.getName(), AcquirerType.NB_FEDERAL.getCode());
			fields.put(FieldType.TXNTYPE.getName(), (String) sessionMap.get(FieldType.INTERNAL_ORIG_TXN_TYPE.getName()));
			fields.put((FieldType.PAYMENTS_REGION.getName()), (String) sessionMap.get(FieldType.PAYMENTS_REGION.getName()));
			fields.put((FieldType.CARD_HOLDER_TYPE.getName()), (String) sessionMap.get(FieldType.CARD_HOLDER_TYPE.getName()));
			fields.put(FieldType.INTERNAL_VALIDATE_HASH_YN.getName(), "N");
			//fields.put((FieldType.OID.getName()), (String) sessionMap.get(FieldType.OID.getName()));
			if (StringUtils.isNotBlank((String) sessionMap.get(FieldType.OID.getName()))) {
				fields.put((FieldType.OID.getName()), (String) sessionMap.get(FieldType.OID.getName()));
			}
			fields.put((FieldType.TXN_KEY.getName()), (String) sessionMap.get(FieldType.TXN_KEY.getName()));
			fields.put((FieldType.ADF1.getName()), (String) sessionMap.get(FieldType.ADF1.getName()));
			fields.put((FieldType.ADF2.getName()), (String) sessionMap.get(FieldType.ADF2.getName()));
			fields.put((FieldType.ADF3.getName()), (String) sessionMap.get(FieldType.ADF3.getName()));
			
			
			Map<String, String> response = transactionControllerServiceProvider.transact(fields,
					Constants.TXN_WS_FEDERALBANKNB_PROCESSOR.getValue());
			responseMap = new Fields(response);
			
			String crisFlag = (String) sessionMap.get(FieldType.INTERNAL_IRCTC_YN.getName());
			if (StringUtils.isNotBlank(crisFlag)) {
				responseMap.put(FieldType.INTERNAL_IRCTC_YN.getName(), crisFlag);
			}

			if (StringUtils.isNotBlank((String) sessionMap.get(FieldType.RETURN_URL.getName()))) {
				fields.put(FieldType.RETURN_URL.getName(), (String) sessionMap.get(FieldType.RETURN_URL.getName()));
			}
			
			responseMap.put(FieldType.INTERNAL_SHOPIFY_YN.getName(), (String) sessionMap.get(FieldType.INTERNAL_SHOPIFY_YN.getName()));
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
			logger.error("2- FederalBankNBResponseAction Exception", exception);

			return ERROR;
		}

		return Action.NONE;
	}
}