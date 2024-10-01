package com.pay10.pg.action.service;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.opensymphony.xwork2.Action;
import com.pay10.commons.api.TransactionControllerServiceProvider;
import com.pay10.commons.dao.FieldsDao;
import com.pay10.commons.user.AccountCurrencyDao;
import com.pay10.commons.util.AcquirerType;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.PropertiesManager;
import com.pay10.pg.action.AbstractSecureAction;
import com.pay10.pg.core.pageintegrator.CityUnionBankIUntil;
import com.pay10.pg.core.util.ResponseCreator;

public class CityUnionBankResponseAction  extends AbstractSecureAction implements ServletRequestAware {
	private static Logger logger = LoggerFactory.getLogger(CityUnionBankResponseAction.class.getName());
	private static final long serialVersionUID = 6155942791032490231L;

	
	String key = PropertiesManager.propertiesMap.get("CITYUNIONKEY");
	String  IV = PropertiesManager.propertiesMap.get("CITYUNIONKIV");

	
	@Autowired
	private TransactionControllerServiceProvider transactionControllerServiceProvider;

	@Autowired
	private ResponseCreator responseCreator;

	
	@Autowired
	private  FieldsDao fieldsDao;
	
	private Fields responseMap = null;
	private HttpServletRequest httpRequest;

	@Override
	public void setServletRequest(HttpServletRequest hReq) {
		this.httpRequest = hReq;
	}

	public CityUnionBankResponseAction() {
	}
	
	@Autowired
	private CityUnionBankIUntil cityUnionBankIUntil;
	
	@Autowired
	private AccountCurrencyDao accountCurrencyDao;

	@Override
	public String execute() {
		try {
			String decryptedResponse = "";
			String QR = httpRequest.getParameter("MDATA");
		logger.info("decryptionKey key ={},IV={}",key,IV);
			if (StringUtils.isNotBlank(QR)) {
				logger.info("decryption String ={}",QR);

				decryptedResponse = cityUnionBankIUntil.getDeryptedPDEK(IV,key,QR);
			}
			Map<String, String> keymap= new HashMap<String, String>();
			logger.info("cityunion bank  Decrypted Response Received  : ={}",decryptedResponse);
			String Responsedata="";
			String responseArray [] = decryptedResponse.split("&");
			for (String data : responseArray) {
			if (data.contains("DATA")) {
					String dataArray [] = data.split("=");
				
					Responsedata=dataArray[1];
				}
				
				if (data.contains("merchantcode")) {
					String dataArray [] = data.split("=");
				
					keymap =accountCurrencyDao.FindinAndKeyCityUnion(dataArray[1]);
				}
				}
		logger.info("response for city union bankkey={},Responsedata={}",key,Responsedata);
			
		String	decrypteddata = cityUnionBankIUntil.getDeryptedPDEK
				(keymap.get("respIv"),keymap.get("respTxnKey"),Responsedata);

		String responseArraydata [] = decrypteddata.split("&");
		String pgRefNum ="";
for (String data : responseArraydata) {
		if (data.contains("BRN")) {
					String dataArray [] = data.split("=");
				
					pgRefNum=dataArray[1];
				}
			}
			Fields fields = new Fields();
			if (StringUtils.isNotBlank(pgRefNum)) {
				
				logger.info("Get Fields Data From DB For city, PG_REF_NUM :={} ",pgRefNum);
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
					logger.info(" cust ipaddress in cityunionbank-NB Acquirer = {}", ipAddress);
				}
				fields.put(FieldType.RETURN_URL.getName(), paramMap.get(FieldType.RETURN_URL.getName()));
				sessionMap.put(FieldType.RETURN_URL.getName(), paramMap.get(FieldType.RETURN_URL.getName()));
				if (StringUtils.isNotBlank(paramMap.get(FieldType.IS_MERCHANT_HOSTED.getName()))) {
					logger.info("IS_MERCHANT_HOSTED flag found for ORDER ID ={}"
							, paramMap.get(FieldType.ORDER_ID.getName()));
					fields.put(FieldType.IS_MERCHANT_HOSTED.getName(),
							paramMap.get(FieldType.IS_MERCHANT_HOSTED.getName()));
					sessionMap.put(FieldType.IS_MERCHANT_HOSTED.getName(),
							paramMap.get(FieldType.IS_MERCHANT_HOSTED.getName()));
				} else {
					logger.info("IS_MERCHANT_HOSTED not found for ORDER ID ={}"
							, paramMap.get(FieldType.ORDER_ID.getName()) );
				}
			}
				fields.put(FieldType.CITY_UNION_BANK_RESPONSE_FIELD.getName(), decrypteddata);
			fields.logAllFields("City union bank Net Banking Response Recieved :");
	fields.put(FieldType.ACQUIRER_TYPE.getName(), AcquirerType.CITYUNIONBANK.getCode());
				fields.put(FieldType.INTERNAL_VALIDATE_HASH_YN.getName(), "N");
			
			Map<String, String> response = transactionControllerServiceProvider.transact(fields,
					Constants.TXN_WS_CITYUNIONBANK_PROCESSOR.getValue());
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
			responseMap.remove(FieldType.TXN_KEY.getName());
			responseMap.remove(FieldType.ACQUIRER_TYPE.getName());
			responseCreator.create(responseMap);
			responseCreator.ResponsePost(responseMap);
		} catch (Exception exception) {
			logger.error("Exception", exception);
			return ERROR;
		}
		return Action.NONE;
	}
	
	
}
