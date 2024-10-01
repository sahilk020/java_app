package com.pay10.pg.action;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.net.ssl.HttpsURLConnection;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.codec.binary.Base64;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.opensymphony.xwork2.Action;
import com.pay10.commons.api.TransactionControllerServiceProvider;
import com.pay10.commons.exception.SystemException;
import com.pay10.commons.kms.AWSEncryptDecryptService;
import com.pay10.commons.user.Account;
import com.pay10.commons.user.AccountCurrency;
import com.pay10.commons.user.User;
import com.pay10.commons.user.UserDao;
import com.pay10.commons.util.AcquirerType;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.PropertiesManager;
import com.pay10.commons.util.TransactionType;
import com.pay10.pg.core.util.UpiHistorian;

public class LyraUpiResponseAction extends AbstractSecureAction implements ServletRequestAware {

	private static final long serialVersionUID = 1741520724921491462L;

	private HttpServletRequest httpRequest;

	@Autowired
	private UpiHistorian upiHistorian;

	@Autowired
	private TransactionControllerServiceProvider transactionControllerServiceProvider;

	@Autowired
	private UserDao userDao;

	@Autowired
	private AWSEncryptDecryptService awsEncryptDecryptService;

	private static Logger logger = LoggerFactory.getLogger(LyraUpiResponseAction.class.getName());

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.httpRequest = request;
	}

	public String execute() {
		Fields responseField = null;

		try {
			String pgRefNo = httpRequest.getParameter("pgRefNo");
			
			logger.info("Received Lyra response for Pg ref num = " + pgRefNo);
			
			if (pgRefNo.contains("u003d")) {
				
				pgRefNo = pgRefNo.replace("u003d", "");
				logger.info("Updated for Lyra, Pg ref num = " + pgRefNo);
			}
			
			BufferedReader inputBuffered = httpRequest.getReader();
			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = inputBuffered.readLine()) != null) {
				response.append(inputLine);
			}
			inputBuffered.close();
			logger.info("Lyra UPI Response >>> " + response.toString());

			Fields fields = new Fields();
			fields.put(FieldType.ACQUIRER_TYPE.getName(), AcquirerType.LYRA.getCode());
			fields.put(FieldType.TXNTYPE.getName(), TransactionType.SALE.getName());
			fields.put(FieldType.PG_REF_NUM.getName(), pgRefNo);
			logger.info("LYRA UPI PG_REF_NUM " + pgRefNo);
			upiHistorian.findPrevious(fields);
			logger.info("LYRA UPI PAY ID " + fields.get(FieldType.PAY_ID.getName()));
			String merchantDetail = getTxnKey(fields);
			String[] merchantParam = merchantDetail.split(",");
			Map<String, String> detailParamMap = new HashMap<String, String>();
			
			//db
			for (String param : merchantParam) {				    
				String[] parameterPair = param.split("=");
				if (parameterPair.length > 1 ) {
				    if(parameterPair[0].trim().equalsIgnoreCase("RETURN_URL")) {
				        String[] tempArrayRetURL=param.split("=",2);
				        logger.info(" Split_RETURN_URL : "+tempArrayRetURL[0].trim()+"  :     "+tempArrayRetURL[1].trim());
				        detailParamMap.put(tempArrayRetURL[0].trim(), tempArrayRetURL[1].trim());
				    }else {
				    	detailParamMap.put(parameterPair[0].trim(), parameterPair[1].trim());    
				    }
					
				}
			}
			
			/*
			 * for (String param : merchantParam) { String[] parameterPair =
			 * param.split("="); if (parameterPair.length > 1) {
			 * detailParamMap.put(parameterPair[0].trim(), parameterPair[1].trim()); } }
			 */
			String lyraResponse = getStatus(fields, detailParamMap);
			logger.info("Lyra UPI staus enquiry response for PG_REF_NUM " + fields.get(FieldType.PG_REF_NUM.getName())
					+ "=" + lyraResponse);
			fields.put(FieldType.LYRA_RESPONSE_FIELD.getName(), lyraResponse);

			Map<String, String> resp = transactionControllerServiceProvider.transact(fields,
					Constants.TXN_WS_LYRA_NB_PROCESSOR.getValue());
			responseField = new Fields(resp);
			logger.info("Response received from WS for Lyra upi " + responseField.getFieldsAsString());

			return Action.NONE;
		} catch (Exception e) {
			logger.error("Error in Lyra UPI callback = ", e);
		}

		return Action.NONE;
	}

	public String getTxnKey(Fields fields) throws SystemException {
		StringBuilder req = new StringBuilder();
		User user = userDao.findPayId(fields.get(FieldType.PAY_ID.getName()));
		Account account = null;
		Set<Account> accounts = user.getAccounts();
		if (accounts == null || accounts.size() == 0) {
			logger.info("No account found for Pay ID = " + fields.get(FieldType.PAY_ID.getName()) + " and ORDER ID = "
					+ fields.get(FieldType.ORDER_ID.getName()));
		} else {
			for (Account accountThis : accounts) {

				if (accountThis.getAcquirerName()
						.equalsIgnoreCase(AcquirerType.getInstancefromCode(AcquirerType.LYRA.getCode()).getName())) {
					account = accountThis;
					break;
				}
			}

		}
		AccountCurrency accountCurrency = account.getAccountCurrency(fields.get(FieldType.CURRENCY_CODE.getName()));
		String mId = accountCurrency.getMerchantId();
		String password = awsEncryptDecryptService.decrypt(accountCurrency.getPassword());

		req.append(FieldType.MERCHANT_ID.getName());
		req.append("=");
		req.append(mId);
		req.append(",");
		req.append(FieldType.PASSWORD.getName());
		req.append("=");
		req.append(password);
		req.append(",");
		return req.toString();
	}

	public String getStatus(Fields fields, Map<String, String> detailParamMap) throws SystemException {
		try {
			HttpsURLConnection connection = null;
			StringBuilder serverResponse = new StringBuilder();
			String hostUrl = PropertiesManager.propertiesMap.get("LyraNBChargeUrl");
			hostUrl = hostUrl + "/" + fields.get(FieldType.ACQ_ID.getName());
			logger.info("Status enqury request to Lyra PG REF NUM" + fields.get(FieldType.PG_REF_NUM.getName()) + "="
					+ hostUrl);
			URL url = new URL(hostUrl);
			String userName = detailParamMap.get(FieldType.MERCHANT_ID.getName());
			String pass = detailParamMap.get(FieldType.PASSWORD.getName());

			String authString = userName + ":" + pass;
			byte[] authEncBytes = Base64.encodeBase64(authString.getBytes());
			String authStringEnc = new String(authEncBytes);

			connection = (HttpsURLConnection) url.openConnection();
			connection.setRequestProperty("Authorization", "Basic " + authStringEnc);
			connection.setRequestMethod("GET");
			connection.setRequestProperty("Content-Type", "application/json");
			connection.setRequestProperty("Content-Language", "en-US");
			connection.setUseCaches(false);
			connection.setDoOutput(true);
			connection.setDoInput(true);
			InputStream is = connection.getInputStream();
			BufferedReader rd = new BufferedReader(new InputStreamReader(is));
			String line;
			while ((line = rd.readLine()) != null) {
				serverResponse.append(line);
				serverResponse.append('\r');
			}
			rd.close();
			String str = serverResponse.toString();
			return str;

		} catch (IOException e) {
			logger.error("Error communicating with Lyra UPI get status , " + e);
		}
		return null;

	}

}
