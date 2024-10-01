package com.pay10.pg.action;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.crypto.SecretKey;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.json.JSONArray;
import org.json.JSONObject;
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
import com.pay10.pg.core.util.IdfcChecksum;
import com.pay10.pg.core.util.ResponseCreator;

/**
 * @author Shaiwal
 *
 */
public class idfcResponseAction extends AbstractSecureAction implements ServletRequestAware {

	private static Logger logger = LoggerFactory.getLogger(idfcResponseAction.class.getName());
	private static final long serialVersionUID = 6155942791032490231L;
	private static byte[] SALT = new byte[] { -57, -75, -103, -12, 75, 124, -127, 119 };
	private static Map<String, SecretKey> encDecMap = new HashMap<String, SecretKey>();

	@Autowired
	TransactionControllerServiceProvider transactionControllerServiceProvider;

	@Autowired
	private ResponseCreator responseCreator;

	@Autowired
	private IdfcChecksum idfcChecksum;

	@Autowired
	AccountCurrencyDao accountCurrencyDao;
	@Autowired
	private FieldsDao fieldsDao;

	private Fields responseMap = null;
	private HttpServletRequest httpRequest;

	@Override
	public void setServletRequest(HttpServletRequest hReq) {
		this.httpRequest = hReq;
	}

	public idfcResponseAction() {
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
logger.info("decrytp string"+requestMap.get("encParams"));
			String decryptedResponse = "";
			String QR = requestMap.get("encParams");
			String mid = requestMap.get("mid");
			logger.info(
					" NetBanking Encrypted Response Received From idfc : " + mid.toString() + "mid" + QR.toString());
			String key = accountCurrencyDao.getHdfcKeyByMid(mid);
			logger.info("key from dao " + key);

			if (StringUtils.isNotBlank(QR) && StringUtils.isNotBlank(key)) {

				// decryptedResponse = axisBankNBEncDecService.AESDecrypt(QR,decryptionKey);
				decryptedResponse = idfcChecksum.decrypt(QR, key);
			}

			logger.info("idfc NetBanking Decrypted Response Received From idfc : " + decryptedResponse);

			String pgRefNum = getPgRefNumFronResponse(decryptedResponse);
			Fields fields = new Fields();
			

			if (StringUtils.isNotBlank(pgRefNum)) {

				logger.info("Get Fields Data From DB For idfc, PG_REF_NUM : " + pgRefNum);
				fields = fieldsDao.getPreviousForPgRefNum(pgRefNum);
			
			}
			String internalRequestFields=fields.get(FieldType.INTERNAL_REQUEST_FIELDS.getName());
			String[] paramaters = internalRequestFields.split("~");
			Map<String, String> paramMap = new HashMap<String, String>();
			for (String param : paramaters) {
				String[] parameterPair = param.split("=");
				if (parameterPair.length > 1) {
					paramMap.put(parameterPair[0].trim(), parameterPair[1].trim());
				}
			}
			fields.put(FieldType.IDFC_FINAL_RESPONSE.getName(), decryptedResponse);
			fields.logAllFields("idfc Net Banking Response Recieved :");
			String returnurl = null;
			if (StringUtils.isNotEmpty(paramMap.get(FieldType.RETURN_URL.getName()))){
				 returnurl=paramMap.get(FieldType.RETURN_URL.getName());

			}
			logger.info("return url in response action "+returnurl);
			fields.logAllFields("idfc Net Banking response = " + fields.get(FieldType.TXNTYPE.getName()) + " "
					+ "OrderId = " + fields.get(FieldType.ORDER_ID.getName()));
			fields.put(FieldType.ACQUIRER_TYPE.getName(), AcquirerType.IDFC.getCode());
				Map<String, String> response = transactionControllerServiceProvider.transact(fields,
					Constants.TXN_WS_IDFCBANK_NB_PROCESSOR.getValue());
				responseMap = new Fields(response);
				
				String crisFlag = (String) sessionMap.get(FieldType.INTERNAL_IRCTC_YN.getName());
				if(StringUtils.isNotBlank(crisFlag)){
					responseMap.put(FieldType.INTERNAL_IRCTC_YN.getName(), crisFlag);
				}

				if (StringUtils.isNotBlank(returnurl)) {
					responseMap.put(FieldType.RETURN_URL.getName(), returnurl);
				}
				responseMap.put(FieldType.INTERNAL_SHOPIFY_YN.getName(),
						(String) sessionMap.get(FieldType.INTERNAL_SHOPIFY_YN.getName()));
				if (sessionMap != null) {
					sessionMap.put(Constants.TRANSACTION_COMPLETE_FLAG.getValue(), Constants.Y_FLAG.getValue());
					sessionMap.invalidate();
				}
				
				logger.info("fields"+fields.getFieldsAsString());
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

	public static String getPgRefNumFronResponse(String response) {

		JSONArray getArray = new JSONArray(response);

		String pgRefNum = null;
		for (int i = 0; i < getArray.length(); i++) {
			JSONObject obj = (JSONObject) getArray.get(i);
			if ((obj.get("k")).equals("PID")) {
				pgRefNum = obj.get("v").toString();

			}

		}
		return pgRefNum;

	}

}
