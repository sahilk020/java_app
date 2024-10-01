package com.pay10.citiunionbank;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.exception.SystemException;
import com.pay10.commons.user.Account;
import com.pay10.commons.user.AccountCurrency;
import com.pay10.commons.user.User;
import com.pay10.commons.user.UserDao;
import com.pay10.commons.util.AcquirerType;
import com.pay10.commons.util.Amount;
import com.pay10.commons.util.Currency;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.PropertiesManager;
import com.pay10.commons.util.StatusType;
import com.pay10.commons.util.TransactionManager;
import com.pay10.commons.util.Validator;
import com.pay10.pg.core.pageintegrator.CityUnionBankIUntil;
import com.pay10.pg.core.util.AxisBankNBEncDecService;
import com.pay10.pg.core.util.ProcessManager;
import com.pay10.pg.core.util.Processor;

@Service
public class CityUnionBankNBSaleResponseHandler {
private static final  int TIMEOUT =60000;
	private static Logger logger = LoggerFactory.getLogger(CityUnionBankNBSaleResponseHandler.class.getName());

	@Autowired
	@Qualifier("cityUnionBankTransactionConverter")
	private TransactionConverter converter;

	@Autowired
	private Validator generalValidator;
	
	@Autowired
	private UserDao userDao;
	
	
	@Autowired
	private   CityUnionBankIUntil cityUnionBankIUntil;

	@Autowired
	@Qualifier("updateProcessor")
	private Processor updateProcessor;

	public static final String PIPE = "|";
	public static final String DOUBLE_PIPE = "||";

	public Map<String, String> process(Fields fields) throws SystemException {

		String newTxnId = TransactionManager.getNewTransactionId();
		fields.put(FieldType.TXN_ID.getName(), newTxnId);
		Transaction transactionResponse = new Transaction();
		String pipedResponse = fields.get(FieldType.CITY_UNION_BANK_FINAL_REQUEST.getName());
		transactionResponse = toTransaction(pipedResponse);
		boolean doubleVer = doubleVerification(transactionResponse, fields);
		logger.info("process:: doubleVerification={}, pgRefNo={} ", doubleVer,
				fields.get(FieldType.PG_REF_NUM.getName()));
		if (doubleVer) 
		{
			CityunionBankNBTransformer cityunionBankNBTransformer = new CityunionBankNBTransformer(
					transactionResponse);
			cityunionBankNBTransformer.updateResponse(fields);

		} 
		else 
		{
			fields.put(FieldType.STATUS.getName(), StatusType.DENIED.getName());
			fields.put(FieldType.RESPONSE_CODE.getName(), ErrorType.SIGNATURE_MISMATCH.getCode());
			fields.put(FieldType.RESPONSE_MESSAGE.getName(), ErrorType.SIGNATURE_MISMATCH.getResponseMessage());
		}

		fields.put(FieldType.INTERNAL_ORIG_TXN_TYPE.getName(), fields.get(FieldType.TXNTYPE.getName()));
		fields.remove(FieldType.CITY_UNION_BANK_RESPONSE_FIELD.getName());
		ProcessManager.flow(updateProcessor, fields, true);
		return fields.getFields();

	}

	private boolean doubleVerification(Transaction transactionResponse, Fields fields) {

			try {
				if (!transactionResponse.getPaid().equalsIgnoreCase("Y")) {
					return true;
				}

				Map<String, String> keyMap = getTxnKey(fields);
				fields.put(FieldType.ADF1.getName(), keyMap.get("ADF1"));
				fields.put(FieldType.ADF2.getName(), keyMap.get("ADF2"));
				fields.put(FieldType.ADF3.getName(), keyMap.get("ADF3"));

				fields.put(FieldType.ADF4.getName(), keyMap.get("ADF4"));
				fields.put(FieldType.ADF5.getName(), keyMap.get("ADF5"));
				fields.put(FieldType.ADF9.getName(), keyMap.get("ADF9"));
				fields.put(FieldType.ADF10.getName(), keyMap.get("ADF10"));
				fields.put(FieldType.ADF11.getName(), keyMap.get("ADF11"));

				fields.put(FieldType.TXN_KEY.getName(), keyMap.get("txnKey"));
				fields.put(FieldType.MERCHANT_ID.getName(), keyMap.get("MerchantId"));
				String request = statusEnquiryRequest(fields,transactionResponse);
				String hostUrl = PropertiesManager.propertiesMap.get("CITYUNIONBANK_SALE_URL");
				String response =Statusenquirerresponse(request, hostUrl);
				logger.info("Response doubleVerification city union bank={}",response);
				String []arrdata=response.split("<body>");
				String []arrdata1=arrdata[1].split("</body>");

				String decResponse=decrytResponse(arrdata1[0],fields);
				String [] data=decResponse.split("-");
	logger.info("Response doubleVerification city unionbank pgrefno={},amount={}",
			fields.get(FieldType.PG_REF_NUM.getName()),fields.get(FieldType.TOTAL_AMOUNT.getName()));
				logger.info( "Response doubleVerification city union bank = {}",response);
				String toamount = Amount.toDecimal(fields.get(FieldType.TOTAL_AMOUNT.getName()),
						fields.get(FieldType.CURRENCY_CODE.getName()));			
				
if(data[0].equalsIgnoreCase("Y")&&data[1].equalsIgnoreCase(transactionResponse.getBid())) {
					return true;
				}
				
				logger.info(
						"doubleVerification::doubleVerification failed.   resAmount={},pgRefNo={}",
						fields.get(FieldType.TOTAL_AMOUNT.getName()),fields.get(FieldType.PG_REF_NUM.getName()));
		
			}

			catch (Exception e) {
				logger.error("Exceptionn ", e);
			}
			return false;

		}

	public String decrytResponse(String response,Fields fields) {
		Transaction transaction = new Transaction();
		String key = PropertiesManager.propertiesMap.get("CITYUNIONKEY");
		String  IV = PropertiesManager.propertiesMap.get("CITYUNIONKIV");
		logger.info("city union bankenc={}",response);
		 response=cityUnionBankIUntil.getDeryptedPDEK(IV,key,response);
			logger.info("city union bankdec={}",response);

		String responseArray [] = response.split("&");
		logger.info("city union bank={}",response);

	
	
			String data2 = cityUnionBankIUntil.getDeryptedPDEK(fields.get(FieldType.ADF10.getName()),fields.get(FieldType.ADF11.getName()),response);
					
		
		logger.info("doubleVerification Response city union bank={} " , data2);
		return data2;

	}

	public String Statusenquirerresponse(String request, String hostUrl) {
		String response = "";
		try {
			logger.info(
					"Request doubleVerification  city union bank={},url={}" , request , hostUrl);
			HttpURLConnection connection = null;
		hostUrl =hostUrl.concat("?MDATA="+request);
			URL url;
			url = new URL(hostUrl);
			connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			connection.setUseCaches(false);
			connection.setDoInput(true);
			connection.setDoOutput(true);
			connection.setConnectTimeout(TIMEOUT);
			connection.setReadTimeout(TIMEOUT);

			// Send request
			DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
			wr.writeBytes("");
			wr.flush();
			wr.close();

			// Get Response
			InputStream is = connection.getInputStream();

			BufferedReader bufferedreader = new BufferedReader(new InputStreamReader(is));
			String decodedString;

			while ((decodedString = bufferedreader.readLine()) != null) {
				response = response + decodedString;
			}

			bufferedreader.close();

			logger.info("Response doubleVerification  city union bank={}" ,response);

		} catch (Exception e) {
			logger.error("Exception in getting doubleVerification for city union bank ", e);
			return response;
		}
		return response;

	}

	public String statusEnquiryRequest(Fields fields, Transaction transaction) {
		
		String currency = Currency.getAlphabaticCode(fields.get(FieldType.CURRENCY_CODE.getName()));
		String merchantCode = fields.get(FieldType.MERCHANT_ID.getName());
		String iv = fields.get(FieldType.ADF10.getName());

		String encryptKey = fields.get(FieldType.ADF11.getName());
		String civ = fields.get(FieldType.ADF4.getName());

		String cencryptKey = fields.get(FieldType.ADF5.getName());
		String HandleId = fields.get(FieldType.ADF9.getName());
		String Pid = fields.get(FieldType.ADF3.getName());
		
		StringBuilder salerequest = new StringBuilder();

		salerequest.append("HandleID=");
		salerequest.append(HandleId);

		salerequest.append("&PID=");
		salerequest.append(Pid);
		salerequest.append("&merchantcode=");

		salerequest.append(merchantCode);
		salerequest.append("&trantype=V&");
		logger.info(" raw doubleVerification request  City Union bank ={}"  ,salerequest);
//		BRN=8006177963688806&AMT=4.0&RU=https://test.timesofmoney.com/direcpay/secure/transactionRe 
//			sponse.jsp&NAR=CA&ACCNO=NA
		StringBuilder dateEnc = new StringBuilder();
		dateEnc.append("BRN=");
		dateEnc.append(fields.get(FieldType.PG_REF_NUM.getName()));
		dateEnc.append("&AMT=");
		
		dateEnc.append(Amount.toDecimal(fields.get(FieldType.TOTAL_AMOUNT.getName()), "356"));
		//dateEnc.append("20.00");

		
		dateEnc.append("&CHECKSUM=");
		logger.info(fields.get(FieldType.PG_REF_NUM.getName())
				+Amount.toDecimal(fields.get(FieldType.TOTAL_AMOUNT.getName()), "356"));
		dateEnc.append(cityUnionBankIUntil.calculateChecksum(fields.get(FieldType.PG_REF_NUM.getName())
				+Amount.toDecimal(fields.get(FieldType.TOTAL_AMOUNT.getName()), "356")));
		logger.info(" encrypt doubleVerification request  before data encryption union bank={}" , dateEnc);

		String encryptyDate = cityUnionBankIUntil.getEncryptedString(iv, encryptKey, dateEnc.toString());
		logger.info(" encrypt doubleVerification request   Data  citry union bank={}" , encryptyDate);
		salerequest.append("DATA=");
		salerequest.append(encryptyDate);
		logger.info(" raw doubleVerification request  city union bank ={}",salerequest);
		String encryptyRequest = cityUnionBankIUntil.getEncryptedString(civ, cencryptKey, salerequest.toString());
		logger.info(" encrypt  doubleVerification request  city union bank={}" , encryptyRequest);

		logger.info("Request doubleVerification  CITY UNION BANK={}", encryptyRequest);

		return encryptyRequest;


	}

	public Transaction toTransaction(String pipedResponse) {

		Transaction transaction = new Transaction();

		String responseArray[] = pipedResponse.split("&");

		for (String data : responseArray) 
		{

			if (data.contains("STATUS")) 
			{
				String dataArray[] = data.split("=");
				transaction.setPaid(dataArray[1]);
			}

			else if (data.contains("BRN")) 
			{
				String dataArray[] = data.split("=");
				transaction.setPrn(dataArray[1]);
			}

			else if(data.contains("TID"))
			{
				String dataArray[] = data.split("=");
				transaction.setBid(dataArray[1]);
			}

			

			else if (data.contains("AMT")) {
				String dataArray[] = data.split("=");
				transaction.setAmt(dataArray[1]);
			}

			
			 else {
				continue;
			}

		}
		logger.info("CITY UNION BANK RESPONSE IN TRANSACTION ={}",transaction);

		return transaction;
	}// toTransaction()

	public Map<String, String> getTxnKey(Fields fields) throws SystemException {

		Map<String, String> keyMap = new HashMap<String, String>();

		User user = userDao.findPayId(fields.get(FieldType.PAY_ID.getName()));
		Account account = null;
		Set<Account> accounts = user.getAccounts();

		if (accounts == null || accounts.size() == 0) {
			logger.info("No account found for Pay ID = " + fields.get(FieldType.PAY_ID.getName()) + " and ORDER ID = "
					+ fields.get(FieldType.ORDER_ID.getName()));
		} else {
			for (Account accountThis : accounts) {
				if (accountThis.getAcquirerName()
						.equalsIgnoreCase(AcquirerType.getInstancefromCode(AcquirerType.CITYUNIONBANK.getCode()).getName())) {
					account = accountThis;
					break;
				}
			}
		}

		AccountCurrency accountCurrency = account.getAccountCurrency(fields.get(FieldType.CURRENCY_CODE.getName()));

		keyMap.put("ADF1", accountCurrency.getAdf1());
		keyMap.put("ADF2", accountCurrency.getAdf2());
		keyMap.put("ADF4", accountCurrency.getAdf4());
		keyMap.put("ADF5", accountCurrency.getAdf5());
		keyMap.put("ADF9", accountCurrency.getAdf9());
		keyMap.put("ADF10", accountCurrency.getAdf10());
		keyMap.put("ADF11", accountCurrency.getAdf11());
		keyMap.put("ADF3", accountCurrency.getAdf3());

		keyMap.put("txnKey", accountCurrency.getTxnKey());
		keyMap.put("MerchantId", accountCurrency.getMerchantId());

		return keyMap;

	}
}
