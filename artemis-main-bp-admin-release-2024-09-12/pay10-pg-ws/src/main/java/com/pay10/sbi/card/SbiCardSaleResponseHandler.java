package com.pay10.sbi.card;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.fss.plugin.iPayPipe;
import com.pay10.commons.api.Hasher;
import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.exception.SystemException;
import com.pay10.commons.user.Account;
import com.pay10.commons.user.AccountCurrency;
import com.pay10.commons.user.User;
import com.pay10.commons.user.UserDao;
import com.pay10.commons.util.AcquirerType;
import com.pay10.commons.util.Amount;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.MopType;
import com.pay10.commons.util.PaymentType;
import com.pay10.commons.util.PropertiesManager;
import com.pay10.commons.util.StatusType;
import com.pay10.commons.util.TransactionManager;
import com.pay10.commons.util.Validator;
import com.pay10.pg.core.util.ProcessManager;
import com.pay10.pg.core.util.Processor;
import com.pay10.pg.core.util.SbiUtil;
import com.pay10.pg.history.Historian;

@Service
public class SbiCardSaleResponseHandler {

	private static Logger logger = LoggerFactory.getLogger(SbiCardSaleResponseHandler.class.getName());

	@Autowired
	private Validator generalValidator;
	
	@Autowired
	private Historian historian;

	@Autowired
	@Qualifier("updateProcessor")
	private Processor updateProcessor;

	@Autowired
	private SbiCardTransformer sbiTransformer;
	
	@Autowired
	private UserDao userDao;
	
	@Autowired
	private SbiUtil sbiUtil;

	/*
	 * public Map<String, String> processOld(Fields fields) throws SystemException,
	 * UnsupportedEncodingException { String newTxnId =
	 * TransactionManager.getNewTransactionId();
	 * fields.put(FieldType.TXN_ID.getName(), newTxnId);
	 * generalValidator.validate(fields); String doubleVerifcationrequest =
	 * doubleVerificationRequest(fields); String response =
	 * transactStatus(doubleVerifcationrequest, fields); String decrptedResponse =
	 * sbiUtil.decrypt(response); logger.info("Sbi double verification response " +
	 * fields.get(FieldType.PG_REF_NUM.getName()) + " " + decrptedResponse);
	 * Transaction transactionResponse = new Transaction(); transactionResponse =
	 * toTransaction(decrptedResponse);
	 * 
	 * sbiTransformer = new SbiCardTransformer(transactionResponse);
	 * sbiTransformer.updateResponse(fields);
	 * 
	 * //historian.addPreviousSaleFields(fields);
	 * fields.put(FieldType.INTERNAL_ORIG_TXN_TYPE.getName(),
	 * fields.get(FieldType.TXNTYPE.getName()));
	 * fields.remove(FieldType.SBI_RESPONSE_FIELD.getName());
	 * ProcessManager.flow(updateProcessor, fields, true); return
	 * fields.getFields();
	 * 
	 * }
	 */
	
	public Map<String, String> process(Fields fields) throws SystemException, UnsupportedEncodingException {
		String newTxnId = TransactionManager.getNewTransactionId();
		fields.put(FieldType.TXN_ID.getName(), newTxnId);
		Transaction transactionResponse = new Transaction();
		generalValidator.validate(fields);
		
		if (fields.get(FieldType.STATUS.getName()).equalsIgnoreCase("CAPTURED")) {

			iPayPipe pipe = processDoubleVerificationRequest(fields);
			String amountas = String.valueOf(Math.round(Double.valueOf(pipe.getAmt() )) * 100);
			logger.info("process:: doubleVerification Response for SBI-CARD status={}, pgRefNo={},amount={} ", fields.get(FieldType.STATUS.getName()),
					fields.get(FieldType.PG_REF_NUM.getName()),amountas);
			if (fields.get(FieldType.STATUS.getName()).equalsIgnoreCase("CAPTURED")) {

				if ((pipe.getResult() != null && !pipe.getResult().isEmpty()
						&& (pipe.getResult().equalsIgnoreCase("SUCCESS") || pipe.getResult().equalsIgnoreCase("APPROVED")
								|| pipe.getResult().equalsIgnoreCase("CAPTURED")))&&
						((pipe.getTrackId().equalsIgnoreCase(fields.get(FieldType.TXN_ID.getName())) )
										&&(amountas.equalsIgnoreCase(fields.get(FieldType.TOTAL_AMOUNT.getName()) ))
						))

				transactionResponse = toTransaction(pipe);
				sbiTransformer = new SbiCardTransformer(transactionResponse);
				sbiTransformer.updateResponse(fields);

			} else {
				logger.info(
						"doubleVerification:: failed. Status={},  resAmount={},txn_ID={}",
						pipe.getResult(), 
						amountas, pipe.getTrackId());
			
				fields.put(FieldType.STATUS.getName(), StatusType.DENIED.getName());
				fields.put(FieldType.RESPONSE_CODE.getName(), ErrorType.SIGNATURE_MISMATCH.getCode());
				fields.put(FieldType.RESPONSE_MESSAGE.getName(), ErrorType.SIGNATURE_MISMATCH.getResponseMessage());
			}

		}
		//historian.addPreviousSaleFields(fields);
		fields.put(FieldType.INTERNAL_ORIG_TXN_TYPE.getName(), fields.get(FieldType.TXNTYPE.getName()));
		fields.remove(FieldType.SBI_RESPONSE_FIELD.getName());
		ProcessManager.flow(updateProcessor, fields, true);
		return fields.getFields();

	}

	private iPayPipe processDoubleVerificationRequest(Fields fields) {
		iPayPipe pipe = new iPayPipe();
		String resourcePath = PropertiesManager.propertiesMap.get(fields.get(FieldType.MERCHANT_ID.getName())+"_SbiResourcePath");
		String keystorePath = PropertiesManager.propertiesMap.get(fields.get(FieldType.MERCHANT_ID.getName())+"_SbikeystorePath");
		String aliasName = PropertiesManager.propertiesMap.get(fields.get(FieldType.MERCHANT_ID.getName())+"_SbiAliasName");

		logger.info("resourcePath " + resourcePath + " keystorePath " + keystorePath + " aliasName " + aliasName);
		
		pipe.setTrackId(fields.get(FieldType.TXN_ID.getName())); // trckId
		pipe.setAlias(aliasName);
		pipe.setResourcePath(resourcePath);
		pipe.setAction("8");
		pipe.setAmt(Amount.toDecimal(fields.get(FieldType.AMOUNT.getName()), fields.get(FieldType.CURRENCY_CODE.getName())));
		pipe.setCurrency(fields.get(FieldType.CURRENCY_CODE.getName()));
		pipe.setTransId(fields.get(FieldType.PG_REF_NUM.getName()));
		pipe.setUdf1(null);
		pipe.setUdf2(null);
		pipe.setUdf3(null);
		pipe.setUdf4(null);
		pipe.setUdf5("TrackID");
		pipe.setKeystorePath(keystorePath);
		
		logger.info("request for dualverfication SBICARD pgrefno ={}",fields.get(FieldType.PG_REF_NUM.getName()));

		logger.info("Double verification Request For SBI-CARD PgRefNumber : " + pipe.getTransId() + " , TrackId :"
				+ pipe.getTrackId() + " , TxnAmount : " + pipe.getAmt() + " , AliasName : " + pipe.getAlias()
				+ " ,Currency : " + pipe.getCurrency() + " ,UDF5 : " + pipe.getUdf5());

		int result = pipe.performTransaction(); // To connect Payment Gateway
		logger.info("Double verification Response result From SBI :: "+result);
		
		if (result != 0) {
			pipe.getError();
			logger.info("Error getting while processing doble verification Request :: "+pipe.getError());
			logger.info("Error in doble verification :: "+pipe.getDebugMsg());
		} 
			
		logger.info("Double verification Response Received From SBI for SBI-CARD , TrackId : " + pipe.getTrackId() + ", Result : "
				+ pipe.getResult() + ", Amount : " + pipe.getAmt() + ", sbiRefNumber : " + pipe.getRef()
				+ ", sbiTransId : " + pipe.getTransId() + ", sbiPaymentId : " + pipe.getPaymentId() + ", Auth : "
				+ pipe.getAuth()+ ", TxnDate : "+pipe.getDate());
		logger.info("request for dualverfication SBICARD txnno ={},amount", pipe.getTrackId(),pipe.getAmt());

		return pipe;
	}

	public String transactStatus(String encRequest, Fields fields) throws SystemException {
		String hostUrl = PropertiesManager.propertiesMap.get("SbiStatusEnqURL");
		String merchantId = getTxnKey(fields);
		PostMethod postMethod = new PostMethod(hostUrl);
		postMethod.addParameter("encdata", encRequest);
		postMethod.addParameter("merchant_code", merchantId);

		return transact(postMethod, hostUrl);

	}
	
	public String getTxnKey(Fields fields) throws SystemException {

		User user = userDao.findPayId(fields.get(FieldType.PAY_ID.getName()));
		Account account = null;
		Set<Account> accounts = user.getAccounts();

		if (accounts == null || accounts.size() == 0) {
			logger.info("No account found for Pay ID = " + fields.get(FieldType.PAY_ID.getName()) + " and ORDER ID = "
					+ fields.get(FieldType.ORDER_ID.getName()));
		} else {
			for (Account accountThis : accounts) {
				if (accountThis.getAcquirerName()
						.equalsIgnoreCase(AcquirerType.getInstancefromCode(AcquirerType.SBI.getCode()).getName())) {
					account = accountThis;
					break;
				}
			}
		}

		AccountCurrency accountCurrency = account.getAccountCurrency(fields.get(FieldType.CURRENCY_CODE.getName()));
		String merchantId = accountCurrency.getMerchantId();
		return merchantId;

	}

	public String transact(HttpMethod httpMethod, String hostUrl) throws SystemException {
		String response = "";
		try {
			HttpClient httpClient = new HttpClient();
			httpClient.executeMethod(httpMethod);

			if (httpMethod.getStatusCode() == HttpStatus.SC_OK) {
				response = httpMethod.getResponseBodyAsString();
				logger.info("Response from SBI: " + response);
			} else {
				throw new SystemException(ErrorType.INTERNAL_SYSTEM_ERROR, "Network Exception with SBI "
						+ hostUrl.toString() + "recieved response code" + httpMethod.getStatusCode());
			}
		} catch (IOException ioException) {
			throw new SystemException(ErrorType.INTERNAL_SYSTEM_ERROR, ioException,
					"Network Exception with SBI " + hostUrl.toString());
		}
		return response;

	}

	public String doubleVerificationRequest(Fields fields) throws SystemException, UnsupportedEncodingException {
		StringBuilder req = new StringBuilder();
		req.append("Ref_no");
		req.append("=");
		req.append(fields.get(FieldType.PG_REF_NUM.getName()));
		req.append("|");
		req.append("Amount");
		req.append("=");
		req.append(Amount.toDecimal(fields.get(FieldType.AMOUNT.getName()), fields.get(FieldType.CURRENCY_CODE.getName())));
		String checksum = Hasher.getHash(req.toString());
		req.append("|");
		req.append(Constants.CHECKSUM);
		req.append("=");
		req.append(checksum.toLowerCase());
		logger.info("Double verification plain text request: " + fields.get(FieldType.PG_REF_NUM.getName()) + " " + req.toString());
		String encryptedRequest = sbiUtil.encrypt(req.toString());
		System.out.println("Double verification encrypted request "+ fields.get(FieldType.PG_REF_NUM.getName()) + " " + encryptedRequest);
		return encryptedRequest;
	}
	
	public Transaction toTransaction(iPayPipe pipe) {

		Transaction transaction = new Transaction();
		transaction.setStatus(pipe.getResult());
		transaction.setAcqId(pipe. getRef());
		transaction.setResponseMessage(pipe.getResult());
		return transaction;
	}
	
	/*
	 * public static void main(String[] args) {
	 * 
	 * iPayPipe pipe = new iPayPipe();
	 * 
	 * String resourcePath = "D:\\pg_config\\84000568"; //84000564 String
	 * keystorePath = "D:\\pg_config\\84000568"; //84000568 String aliasName = "";
	 * //mirazzbizBhartipay
	 * 
	 * logger.info("resourcePath " + resourcePath + " keystorePath " + keystorePath
	 * + " aliasName " + aliasName); String action = "8";
	 * 
	 * //pipe.setType("D"); pipe.setTrackId("1019120525223941"); //
	 * pipe.setAlias(aliasName); pipe.setResourcePath(resourcePath);
	 * pipe.setAction(action); pipe.setAmt("1.00"); pipe.setCurrency("356");
	 * pipe.setTransId("1019120525223941"); pipe.setUdf1(null); pipe.setUdf2(null);
	 * pipe.setUdf3(null); pipe.setUdf4(null); pipe.setUdf5("TrackID");
	 * pipe.setKeystorePath(keystorePath);
	 * //pipe.setResponseURL("https://uat.pay10.com/pgui/jsp/sbiResponse");
	 * //pipe.setErrorURL("https://uat.pay10.com/pgui/jsp/sbiResponse");
	 * 
	 * logger.info("Double verification Request For SBI-CARD PgRefNumber : " +
	 * pipe.getTrackId() + " , TrackId :" + pipe.getTransId() + " , TxnAmount : " +
	 * pipe.getAmt() + " , AliasName : " + pipe.getAlias() + " ,Currency : " +
	 * pipe.getCurrency() + " ,UDF5 : " + pipe.getUdf5());
	 * 
	 * int result = pipe.performTransaction(); // To connect Payment Gateway
	 * logger.info("Double verification Response result From SBI:: "+result); String
	 * resp1 = null; String resp = null; if (result != 0) { pipe.getError();
	 * logger.info("Error in doble verification :: "+pipe.getError());
	 * logger.info("Error in doble verification :: "+pipe.getDebugMsg()); } else {
	 * resp = pipe.getResult(); resp1 = pipe.getRawResponse();
	 * logger.info("Result :: "+pipe.getResult()); // gives the result value
	 * logger.info("Date :: "+pipe.getDate()); // contains post date
	 * logger.info("Ref :: "+pipe. getRef()); // contains RRN no generated by PG
	 * logger.info("TrackId :: "+pipe. getTrackId()); // contains merchant track ID
	 * logger.info("TransId :: "+pipe.getTransId()); // contains PG Transaction ID
	 * logger.info("Amount :: "+pipe.getAmt()); // contains transaction amount
	 * logger.info("PaymentId :: "+pipe.getPaymentId()); // contains payment ID
	 * logger.info("Auth :: "+pipe.getAuth()); // contains Auth code
	 * logger.info("ErrorText :: "+pipe.getError_text()); // contains get Error Text
	 * logger.info("Error :: "+pipe.getError()); }
	 * logger.info("Double verification Response Received From SBI, pgRefNumber : "
	 * + pipe.getTrackId() + ", Result : " + pipe.getResult() + ", Amount : " +
	 * pipe.getAmt() + ", sbiRefNumber : " + pipe.getRef() + ", sbiTransId : " +
	 * pipe.getTransId() + ", sbiPaymentId : " + pipe.getPaymentId() + ", Auth : " +
	 * pipe.getAuth()+ ", TxnDate : "+pipe.getDate());
	 * 
	 * }
	 */
}
