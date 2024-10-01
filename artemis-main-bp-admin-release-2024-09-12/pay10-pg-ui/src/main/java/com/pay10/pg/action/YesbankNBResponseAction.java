/**
 * 
 */
package com.pay10.pg.action;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
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
import com.pay10.commons.dao.FieldsDao;
import com.pay10.commons.util.AcquirerType;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.pg.core.util.ResponseCreator;
import com.pay10.pg.core.util.YesBankNBEncDecService;

/**
 * @author Jeetu
 *
 */
public class YesbankNBResponseAction extends AbstractSecureAction implements ServletRequestAware {

	private static Logger logger = LoggerFactory.getLogger(YesbankNBResponseAction.class.getName());
	private static final long serialVersionUID = 6155942791032490231L;

	@Autowired
	TransactionControllerServiceProvider transactionControllerServiceProvider;

	@Autowired
	private ResponseCreator responseCreator;

	@Autowired
	private YesBankNBEncDecService yesBankNBEncDecService;
	@Autowired
	private FieldsDao fieldsDao;

	private Fields responseMap = null;
	private HttpServletRequest httpRequest;

	@Override
	public void setServletRequest(HttpServletRequest hReq) {
		this.httpRequest = hReq;
	}

	public YesbankNBResponseAction() {
	}

	@Override
	public String execute() {
		try {

			httpRequest.setCharacterEncoding("UTF-8");
			Map<String, String[]> fieldMapObj = httpRequest.getParameterMap();
			Map<String, String> requestMap = new HashMap<String, String>();

			for (Entry<String, String[]> entry : fieldMapObj.entrySet()) {
				try {
					requestMap.put(entry.getKey(), entry.getValue()[0]);

				} catch (ClassCastException classCastException) {
					logger.error("Exception", classCastException);
				}
			}
			String decryptedResponse = "";
			String enc = requestMap.get("encdata");
			String decryptionKey = (String) sessionMap.get(FieldType.TXN_KEY.getName());

			if (StringUtils.isNotBlank(enc) && StringUtils.isNotBlank(decryptionKey)) {
				String encdata = httpRequest.getQueryString();
				if (encdata.contains("encdata")) {
					encdata = encdata.substring(8);
					decryptedResponse = yesBankNBEncDecService.decrypt(encdata, decryptionKey);
				}

			}
			String trackId = "";
			String strArr[] = decryptedResponse.split("&");
			if (strArr.length > 0) {
				List<String> strList = Arrays.asList(strArr);
				for (String str : strList) {
					String keyList[] = str.split("=");
					if (keyList.length > 1) {
						if (keyList[0].equals("fldMerchRefNbr")) {
							trackId = keyList[1];
						}
					}
				}
			}


			Fields fields = new Fields();
			Object fieldsObj = sessionMap.get("FIELDS");
			if (null != fieldsObj) {
				fields.put((Fields) fieldsObj);
			}
			
			

			// Check if fields is empty
			if (StringUtils.isBlank(fields.get(FieldType.PAY_ID.getName()))) {
				logger.info("FIELDS is blank in session Map, getting data from DB for Yesbank NB");

				fields = fieldsDao.getPreviousForPgRefNum(trackId);
				String internalRequestFields = null;
				internalRequestFields = fieldsDao.getPreviousForOID(fields.get(FieldType.OID.getName()));

				if (StringUtils.isBlank(internalRequestFields)) {
					logger.info("New Order entry not found for this OID , getting data from SENT TO BANK "
							+ fields.get(FieldType.OID.getName()));
					internalRequestFields = fieldsDao.getPreviousForOIDSTB(fields.get(FieldType.OID.getName()));
				} else {
					logger.info("New Order entry found for this OID in New Order - Pending txn"
							+ fields.get(FieldType.OID.getName()));
				}

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
					logger.info("cust ipaddress in Pinelabs Acquirer = {}", ipAddress);
				}
				
				
				if (StringUtils.isNotBlank(paramMap.get(FieldType.RETURN_URL.getName()))) {
					logger.info("Return URL found for ORDER ID " + paramMap.get(FieldType.ORDER_ID.getName())
							+ "Return URL >> " + paramMap.get(FieldType.RETURN_URL.getName()));
					fields.put(FieldType.RETURN_URL.getName(), paramMap.get(FieldType.RETURN_URL.getName()));
					sessionMap.put(FieldType.RETURN_URL.getName(), paramMap.get(FieldType.RETURN_URL.getName()));
				} else {
					logger.info("Return URL not found for ORDER ID " + paramMap.get(FieldType.ORDER_ID.getName()));
				}

				if (StringUtils.isNotBlank(paramMap.get(FieldType.IS_MERCHANT_HOSTED.getName()))) {
					logger.info(
							"IS_MERCHANT_HOSTED flag found for ORDER ID " + paramMap.get(FieldType.ORDER_ID.getName()));
					fields.put(FieldType.IS_MERCHANT_HOSTED.getName(),
							paramMap.get(FieldType.IS_MERCHANT_HOSTED.getName()));
					sessionMap.put(FieldType.IS_MERCHANT_HOSTED.getName(),
							paramMap.get(FieldType.IS_MERCHANT_HOSTED.getName()));
				} else {
					logger.info(
							"IS_MERCHANT_HOSTED not found for ORDER ID " + paramMap.get(FieldType.ORDER_ID.getName()));
				}

			} else {

				if (StringUtils.isNotBlank(trackId)) {
					String pgRefNum = trackId;
					fields = fieldsDao.getPreviousForPgRefNum(pgRefNum);
					
					String internalRequestFields = fields.get(FieldType.INTERNAL_REQUEST_FIELDS.getName());
					String[] paramaters = internalRequestFields.split("~");
					Map<String, String> paramMap = new HashMap<String, String>();
					for (String param : paramaters) {
						String[] parameterPair = param.split("=");
						if (parameterPair.length > 1) {
							paramMap.put(parameterPair[0].trim(), parameterPair[1].trim());
						}
					}
					
					if(StringUtils.isBlank(fields.get(FieldType.INTERNAL_CUST_IP.getName()))) {
						String ipAddress = fieldsDao.getIpFromSTB(fields.get(FieldType.OID.getName()));
						
						if(StringUtils.isBlank(ipAddress)) {
							ipAddress = fieldsDao.getIPFromInitiateRequest(fields.get(FieldType.OID.getName()));
						}
						
						fields.put(FieldType.INTERNAL_CUST_IP.getName(), ipAddress);
						logger.info("cust ipaddress in Pinelabs Acquirer = {}", ipAddress);
					}
					
					if (StringUtils.isNotBlank(paramMap.get(FieldType.RETURN_URL.getName()))) {
						fields.put(FieldType.RETURN_URL.getName(), paramMap.get(FieldType.RETURN_URL.getName()));
						sessionMap.put(FieldType.RETURN_URL.getName(), paramMap.get(FieldType.RETURN_URL.getName()));
					}
				}
			}

			fields.put(FieldType.YESBANKNB_RESPONSE_FIELD.getName(), decryptedResponse);
			fields.logAllFields("YESBank Net Banking Response Recieved :");
			
			fields.logAllFields("YESBank Net Banking response - " + fields.get(FieldType.TXNTYPE.getName()) + " "
					+ "OrderId = " + fields.get(FieldType.ORDER_ID.getName()) + " Txn Date ---"
					+ sessionMap.get(FieldType.ADF10.getName()));
			fields.put(FieldType.ACQUIRER_TYPE.getName(), AcquirerType.YESBANKNB.getCode());
			fields.put(FieldType.TXNTYPE.getName(),
					(String) sessionMap.get(FieldType.INTERNAL_ORIG_TXN_TYPE.getName()));
			fields.put((FieldType.PAYMENTS_REGION.getName()),
					(String) sessionMap.get(FieldType.PAYMENTS_REGION.getName()));
			fields.put((FieldType.CARD_HOLDER_TYPE.getName()),
					(String) sessionMap.get(FieldType.CARD_HOLDER_TYPE.getName()));
			fields.put(FieldType.INTERNAL_VALIDATE_HASH_YN.getName(), "N");
			//fields.put((FieldType.OID.getName()), (String) sessionMap.get(FieldType.OID.getName()));
			if (StringUtils.isNotBlank((String) sessionMap.get(FieldType.OID.getName()))) {
				fields.put((FieldType.OID.getName()), (String) sessionMap.get(FieldType.OID.getName()));
			}
			fields.put((FieldType.ADF1.getName()), (String) sessionMap.get(FieldType.ADF1.getName()));
			fields.put((FieldType.ADF2.getName()), (String) sessionMap.get(FieldType.ADF2.getName()));
			fields.put((FieldType.ADF10.getName()), (String) sessionMap.get(FieldType.ADF10.getName()));
			fields.put((FieldType.TXN_KEY.getName()), (String) sessionMap.get(FieldType.TXN_KEY.getName()));
			Map<String, String> response = transactionControllerServiceProvider.transact(fields,
					Constants.TXN_WS_YESBANKNB_PROCESSOR.getValue());
			responseMap = new Fields(response);

			String crisFlag = (String) sessionMap.get(FieldType.INTERNAL_IRCTC_YN.getName());
			if (StringUtils.isNotBlank(crisFlag)) {
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
			logger.error("YESBANK NB Exception", exception);

			return ERROR;
		}

		return Action.NONE;
	}
}
