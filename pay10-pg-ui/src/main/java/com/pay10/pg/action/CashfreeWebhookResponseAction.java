package com.pay10.pg.action;

import java.io.BufferedReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.gson.JsonObject;
import com.opensymphony.xwork2.Action;
import com.pay10.commons.api.TransactionControllerServiceProvider;
import com.pay10.commons.dao.FieldsDao;
import com.pay10.commons.exception.SystemException;
import com.pay10.commons.user.Account;
import com.pay10.commons.user.AccountCurrency;
import com.pay10.commons.user.User;
import com.pay10.commons.user.UserDao;
import com.pay10.commons.util.AcquirerType;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.pg.core.util.ResponseCreator;

public class CashfreeWebhookResponseAction extends AbstractSecureAction implements ServletRequestAware {

	private static Logger logger = LoggerFactory.getLogger(CashfreeResponseAction.class.getName());
	private static final long serialVersionUID = 6155942791032490231L;

	@Autowired
	private TransactionControllerServiceProvider transactionControllerServiceProvider;

	@Autowired
	private ResponseCreator responseCreator;

	@Autowired
	private UserDao userDao;

	@Autowired
	private FieldsDao fieldsDao;

	private Fields responseMap = null;
	private HttpServletRequest httpRequest;
	
	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.httpRequest = request;
	}

	public CashfreeWebhookResponseAction() {
	}

	@Override
	public String execute() {
		try {
			logger.info("Request Received From CashFree CashfreeWebhookResponseAction:: ");
			
			StringBuffer jb = new StringBuffer();
			 String line = null;
			 try {
			  BufferedReader reader = httpRequest.getReader();
			  while ((line = reader.readLine()) != null)
			   jb.append(line);
			 } catch (Exception e) { /*report an error*/ }

			 logger.info("Response From CashfreeWebhook Json  :: "+jb.toString());
			 JSONObject jsonObject = new JSONObject(jb.toString());
			 
			 
			/*
			 * JSONObject dataInfo = null; JSONObject orderInfo = null; JSONObject
			 * paymentInfo = null;
			 */
			 
			 JSONObject dataInfo = new JSONObject(jsonObject.get("data").toString());
			 JSONObject orderInfo = new JSONObject(dataInfo.get("order").toString());
			 JSONObject paymentInfo = new JSONObject(dataInfo.get("payment").toString());
			/*
			 * JSONObject dataInfo = new JSONObject(jsonObject.get("data").toString());
			 * JSONObject orderInfo = new JSONObject(dataInfo.get("order").toString());
			 * JSONObject paymentInfo = new JSONObject(dataInfo.get("payment").toString());
			 * System.out.println("order "+orderInfo.get("order_id").toString());
			 * System.out.println("payment "+paymentInfo.get("payment_status").toString());
			 */
				
			/*
			 * if(jsonObject != null ) { logger.info("jsonObject "+jsonObject);
			 * 
			 * dataInfo = new JSONObject(jsonObject.get("data").toString()); orderInfo = new
			 * JSONObject(dataInfo.get("order").toString()); paymentInfo = new
			 * JSONObject(dataInfo.get("payment").toString()); }
			 */
			 logger.info("orderInfo "+orderInfo);
			 logger.info("paymentInfo "+paymentInfo);
			
		
			/*
			 * Map<String, String[]> fieldMapObj = httpRequest.getParameterMap();
			 * Map<String, String> requestMap = new HashMap<String, String>();
			 * 
			 * for (Entry<String, String[]> entry : fieldMapObj.entrySet()) { try {
			 * requestMap.put(entry.getKey(), entry.getValue()[0]);
			 * 
			 * } catch (ClassCastException classCastException) { logger.error("Exception",
			 * classCastException); } }
			 */

			JsonObject resJson = new JsonObject();
			String pgRef = orderInfo.get("order_id").toString();
			String paymentStatus = paymentInfo.get("payment_status").toString();

			/*
			 * for (Entry<String, String> entry : requestMap.entrySet()) {
			 * 
			 * resJson.addProperty(entry.getKey(), entry.getValue()); if
			 * (entry.getKey().equalsIgnoreCase("orderId")) { pgRef = entry.getValue(); }
			 * 
			 * }
			 */

			logger.info("Response received from Cashfree new flow webhook : " + resJson.toString());
			logger.info("pgRef : " + pgRef);
			logger.info("paymentStatus : " + paymentStatus);

			Fields fields = new Fields();

			
			if (StringUtils.isNotBlank(pgRef)) {
					String pgRefNum = pgRef;

					fields = fieldsDao.getPreviousForPgRefNum(pgRefNum);
					String internalRequestFields = fieldsDao.getPreviousForOID(fields.get(FieldType.OID.getName()));

					if (StringUtils.isBlank(internalRequestFields)) {
						internalRequestFields = fieldsDao
								.getPreviousByOIDForSentToBank(fields.get(FieldType.OID.getName()));
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
					String ipAddress = fieldsDao.getIpFromSTB(fields.get(FieldType.OID.getName()));
					fields.put(FieldType.INTERNAL_CUST_IP.getName(), ipAddress);
				logger.info(" cust ipaddress in casfree Acquirer = {}", ipAddress);
					fields.put(FieldType.RETURN_URL.getName(), paramMap.get(FieldType.RETURN_URL.getName()));
					sessionMap.put(FieldType.RETURN_URL.getName(), paramMap.get(FieldType.RETURN_URL.getName()));
					sessionMap.put(FieldType.INTERNAL_CUST_IP.getName(),ipAddress);

					if (StringUtils.isNotBlank(paramMap.get(FieldType.IS_MERCHANT_HOSTED.getName()))) {
						logger.info("IS_MERCHANT_HOSTED flag found for ORDER ID "
								+ paramMap.get(FieldType.ORDER_ID.getName()) + " in CASHFREE");
						fields.put(FieldType.IS_MERCHANT_HOSTED.getName(),
								paramMap.get(FieldType.IS_MERCHANT_HOSTED.getName()));
						sessionMap.put(FieldType.IS_MERCHANT_HOSTED.getName(),
								paramMap.get(FieldType.IS_MERCHANT_HOSTED.getName()));
					} else {
						logger.info("IS_MERCHANT_HOSTED not found for ORDER ID "
								+ paramMap.get(FieldType.ORDER_ID.getName()) + " in CASHFREE");
					}

					Map<String, String> paymentMap = new HashMap<String, String>();
					paymentMap = getTxnKey(fields);

					
					fields.put(FieldType.MERCHANT_ID.getName(), paymentMap.get(FieldType.MERCHANT_ID.getName()));
					fields.put(FieldType.TXN_KEY.getName(), paymentMap.get(FieldType.TXN_KEY.getName()));
				}

			

			fields.put(FieldType.CASHFREE_RESPONSE_FIELD.getName(), jsonObject.toString());
			fields.logAllFields("Cashfree Response Recieved New Flow webhhok :");

			Object fieldsObj = sessionMap.get("FIELDS");
			if (null != fieldsObj) {
				fields.put((Fields) fieldsObj);
			}

			fields.logAllFields("Updated 3DS Recieved Map TxnType = " + fields.get(FieldType.TXNTYPE.getName()) + " " + "Txn id = "
					+ fields.get(FieldType.TXN_ID.getName()));
			
			
			//fields.logAllFieldsUsingMasking("Updated 3DS Recieved Map TxnType = " + fields.get(FieldType.TXNTYPE.getName()) + " " + "Txn id = "
				//			+ fields.get(FieldType.TXN_ID.getName()));
			fields.put(FieldType.ACQUIRER_TYPE.getName(), AcquirerType.CASHFREE.getCode());
			fields.put(FieldType.TXNTYPE.getName(), (String) sessionMap.get(FieldType.INTERNAL_ORIG_TXN_TYPE.getName()));
			fields.put(FieldType.INTERNAL_VALIDATE_HASH_YN.getName(), "N");
			fields.put((FieldType.PAYMENTS_REGION.getName()),
					(String) sessionMap.get(FieldType.PAYMENTS_REGION.getName()));
			fields.put((FieldType.CARD_HOLDER_TYPE.getName()),
					(String) sessionMap.get(FieldType.CARD_HOLDER_TYPE.getName()));
			fields.put((FieldType.OID.getName()), (String) sessionMap.get(FieldType.OID.getName()));
			
			Map<String, String> response = transactionControllerServiceProvider.transact(fields,
					Constants.TXN_WS_CASHFREE_PROCESSOR.getValue());

			logger.info("response From PGWS -Cashfree " + response.toString());


		} catch (Exception exception) {
			logger.error("Exception", exception);
			return ERROR;
		}
		return Action.NONE;
	}
	
	public Map<String, String> getTxnKey(Fields fields) throws SystemException {

		User user = userDao.findPayId(fields.get(FieldType.PAY_ID.getName()));
		Account account = null;
		Set<Account> accounts = user.getAccounts();

		if (accounts == null || accounts.size() == 0) {
			logger.info("No account found for Pay ID = " + fields.get(FieldType.PAY_ID.getName()) + " and ORDER ID = "
					+ fields.get(FieldType.ORDER_ID.getName()));
		} else {
			for (Account accountThis : accounts) {
				if (accountThis.getAcquirerName().equalsIgnoreCase(
						AcquirerType.getInstancefromCode(AcquirerType.CASHFREE.getCode()).getName())) {
					account = accountThis;
					break;
				}
			}
		}

		AccountCurrency accountCurrency = account.getAccountCurrency(fields.get(FieldType.CURRENCY_CODE.getName()));

		Map<String, String> responseMap = new HashMap<String, String>();
		responseMap.put(FieldType.MERCHANT_ID.getName(), accountCurrency.getMerchantId());
		responseMap.put(FieldType.TXN_KEY.getName(), accountCurrency.getTxnKey());

		return responseMap;

	}
	
	public static void main(String[] args) {
		
		String reqData = "{    \"data\": {        \"customer_details\": {            \"customer_email\": \"sonu@pay10.com\",            \"customer_phone\": \"9767146866\",            \"customer_name\": null,            \"customer_id\": \"sonu\"        },        \"payment\": {            \"payment_message\": \"Transaction Success\",            \"cf_payment_id\": 901497882,            \"auth_id\": \"660456\",            \"payment_status\": \"SUCCESS\",            \"payment_time\": \"2022-04-20T15:21:37+05:30\",            \"payment_group\": \"debit_card\",            \"payment_amount\": 1,            \"bank_reference\": \"21100000000042612016\",            \"payment_method\": {                \"card\": {                    \"card_number\": \"416021XXXXXX4351\",                    \"card_network\": \"visa\",                    \"card_country\": \"IN\",                    \"channel\": null,                    \"card_type\": \"debit_card\",                    \"card_bank_name\": \"HDFC BANK\"                }            },            \"payment_currency\": \"INR\"        },        \"order\": {            \"order_currency\": \"INR\",            \"order_amount\": 1,            \"order_id\": \"1204520420152135\",            \"order_tags\": {                \"newKey\": null            }        }    },    \"type\": \"PAYMENT_SUCCESS_WEBHOOK\",    \"event_time\": \"2022-04-20T15:22:39+05:30\"}";
		JSONObject jsonObject = new JSONObject(reqData);
		
		System.out.println("jsonObject "+jsonObject);
		System.out.println("data "+jsonObject.get("data"));
		//String dataReq = jsonObject.get("data");
		JSONObject dataInfo = new JSONObject(jsonObject.get("data").toString());
		JSONObject orderInfo = new JSONObject(dataInfo.get("order").toString());
		JSONObject paymentInfo = new JSONObject(dataInfo.get("payment").toString());
		System.out.println("order "+orderInfo.get("order_id").toString());
		System.out.println("payment "+paymentInfo.get("payment_status").toString());
	}
}
