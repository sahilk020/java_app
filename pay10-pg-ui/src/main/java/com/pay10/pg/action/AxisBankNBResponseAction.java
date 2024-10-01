package com.pay10.pg.action;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.crypto.SecretKey;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.interceptor.ServletRequestAware;
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
import com.pay10.pg.core.util.AxisBankNBEncDecService;
import com.pay10.pg.core.util.ResponseCreator;

/**
 * @author Shaiwal
 *
 */
public class AxisBankNBResponseAction extends AbstractSecureAction implements ServletRequestAware {

	private static Logger logger = LoggerFactory.getLogger(AxisBankNBResponseAction.class.getName());
	private static final long serialVersionUID = 6155942791032490231L;
	private static byte[] SALT  = new byte[] { -57, -75, -103, -12, 75, 124, -127, 119 };
	private static Map<String,SecretKey> encDecMap = new HashMap<String,SecretKey>();

	@Autowired
	TransactionControllerServiceProvider transactionControllerServiceProvider;

	@Autowired
	private ResponseCreator responseCreator;

	@Autowired
	private AxisBankNBEncDecService axisBankNBEncDecService;
	
	@Autowired
	private FieldsDao fieldsDao;
	
	private Fields responseMap = null;
	private HttpServletRequest httpRequest;

	@Override
	public void setServletRequest(HttpServletRequest hReq) {
		this.httpRequest = hReq;
	}

	public AxisBankNBResponseAction() {
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

			logger.info("AxisBank NetBanking Encrypted Response Received From AxisBank : "+requestMap.toString());
						
			String decryptedResponse = "";
			String QR = requestMap.get("QR");
			String decryptionKey = (String) sessionMap.get(FieldType.TXN_KEY.getName());
			String iv = (String) sessionMap.get(FieldType.IV.getName());
			String salt = (String) sessionMap.get(FieldType.PASSWORD.getName());
			
			logger.info("decryptionKey "+decryptionKey);
			logger.info("iv "+iv);
			logger.info("salt "+salt);
			if (StringUtils.isNotBlank(QR) && StringUtils.isNotBlank(decryptionKey)) {
				
				//decryptedResponse = axisBankNBEncDecService.AESDecrypt(QR,decryptionKey);
				decryptedResponse = axisBankNBEncDecService.decrypt(salt,iv,decryptionKey,QR);
			}
			
			logger.info("AxisBank NetBanking Decrypted Response Received From AxisBank : "+decryptedResponse);
			
			String pgRefNum = getPgRefNumFronResponse(decryptedResponse);
			Fields fields = new Fields();
			Object fieldsObj = sessionMap.get("FIELDS");
			if (null != fieldsObj) {
				fields.put((Fields) fieldsObj);
			}
			
			if (StringUtils.isNotBlank(pgRefNum)) {
				
				logger.info("Get Fields Data From DB For AXISBANK, PG_REF_NUM : "+pgRefNum);
				fields = fieldsDao.getPreviousForPgRefNum(pgRefNum);
				//String internalRequestFields = fieldsDao.getPreviousForOID(fields.get(FieldType.OID.getName()));

				String internalRequestFields = fields.get(FieldType.INTERNAL_REQUEST_FIELDS.getName());
				/*
				 * if (StringUtils.isBlank(internalRequestFields)) { internalRequestFields =
				 * fieldsDao
				 * .getPreviousByOIDForSentToBank(fields.get(FieldType.OID.getName())); }
				 */
				
				
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
					logger.info(" cust ipaddress in AxisBank-NB Acquirer = {}", ipAddress);
				}
				
				/*
				 * String ipAddress =
				 * fieldsDao.getIpFromSTB(fields.get(FieldType.OID.getName()));
				 * if(StringUtils.isBlank(ipAddress)) {
				 * ipAddress=fieldsDao.getIPFromInitiateRequest(fields.get(FieldType.OID.getName
				 * ())); } fields.put(FieldType.INTERNAL_CUST_IP.getName(), ipAddress);
				 * logger.info("cust ipaddress in AXISBANK Acquirer = {}", ipAddress);
				 */
				
				fields.put(FieldType.RETURN_URL.getName(), paramMap.get(FieldType.RETURN_URL.getName()));
				sessionMap.put(FieldType.RETURN_URL.getName(), paramMap.get(FieldType.RETURN_URL.getName()));
				//sessionMap.put(FieldType.INTERNAL_CUST_IP.getName(),ipAddress);

				if (StringUtils.isNotBlank(paramMap.get(FieldType.IS_MERCHANT_HOSTED.getName()))) {
					logger.info("IS_MERCHANT_HOSTED flag found for ORDER ID "
							+ paramMap.get(FieldType.ORDER_ID.getName()) + " in AXISBANK");
					fields.put(FieldType.IS_MERCHANT_HOSTED.getName(),
							paramMap.get(FieldType.IS_MERCHANT_HOSTED.getName()));
					sessionMap.put(FieldType.IS_MERCHANT_HOSTED.getName(),
							paramMap.get(FieldType.IS_MERCHANT_HOSTED.getName()));
				} else {
					logger.info("IS_MERCHANT_HOSTED not found for ORDER ID "
							+ paramMap.get(FieldType.ORDER_ID.getName()) + " in AXISBANK");
				}

			}
			
			fields.put(FieldType.AXISBANK_NB_RESPONSE_FIELD.getName(), decryptedResponse);
			fields.logAllFields("AxisBank Net Banking Response Recieved :");

			

			fields.logAllFields("AxisBank Net Banking response = " + fields.get(FieldType.TXNTYPE.getName()) + " " + "OrderId = "+ fields.get(FieldType.ORDER_ID.getName()));
			fields.put(FieldType.ACQUIRER_TYPE.getName(), AcquirerType.AXISBANK.getCode());
			fields.put(FieldType.TXNTYPE.getName(),
					(String) sessionMap.get(FieldType.INTERNAL_ORIG_TXN_TYPE.getName()));
			fields.put(FieldType.TXN_KEY.getName(),
					(String) sessionMap.get(FieldType.TXN_KEY.getName()));
			fields.put((FieldType.PAYMENTS_REGION.getName()),
					(String) sessionMap.get(FieldType.PAYMENTS_REGION.getName()));
			fields.put((FieldType.CARD_HOLDER_TYPE.getName()),
					(String) sessionMap.get(FieldType.CARD_HOLDER_TYPE.getName()));
			fields.put(FieldType.INTERNAL_VALIDATE_HASH_YN.getName(), "N");
			fields.put((FieldType.ADF10.getName()),
					(String) sessionMap.get(FieldType.ADF10.getName()));
			fields.put((FieldType.ADF11.getName()),
					(String) sessionMap.get(FieldType.ADF11.getName()));
			fields.put((FieldType.ADF9.getName()),
					(String) sessionMap.get(FieldType.ADF9.getName()));
			//fields.put((FieldType.OID.getName()), (String) sessionMap.get(FieldType.OID.getName()));
			if (StringUtils.isNotBlank((String) sessionMap.get(FieldType.OID.getName()))) {
				fields.put((FieldType.OID.getName()), (String) sessionMap.get(FieldType.OID.getName()));
			}
			Map<String, String> response = transactionControllerServiceProvider.transact(fields,
					Constants.TXN_WS_AXISBANK_NB_PROCESSOR.getValue());
			responseMap = new Fields(response);
			
			String crisFlag = (String) sessionMap.get(FieldType.INTERNAL_IRCTC_YN.getName());
			if(StringUtils.isNotBlank(crisFlag)){
				responseMap.put(FieldType.INTERNAL_IRCTC_YN.getName(), crisFlag);
			}

			if (StringUtils.isNotBlank((String) sessionMap.get(FieldType.RETURN_URL.getName()))) {
				fields.put(FieldType.RETURN_URL.getName(), (String) sessionMap.get(FieldType.RETURN_URL.getName()));
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
	
	public static String getPgRefNumFronResponse(String response) {
		
		String responseArray [] = response.split("&");
		
		for (String data : responseArray) {
			
			if (data.contains("PRN")) {
				String dataArray [] = data.split("=");
				return dataArray[1];
			}
		}
		return null;
	}
	
}
