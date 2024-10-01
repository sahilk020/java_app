package com.pay10.cashfree;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pay10.commons.dao.CashfreeRefundDao;
import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.exception.SystemException;
import com.pay10.commons.user.Account;
import com.pay10.commons.user.AccountCurrency;
import com.pay10.commons.user.CashfreeRefundDTO;
import com.pay10.commons.user.CashfreeTxnRespDTO;
import com.pay10.commons.user.User;
import com.pay10.commons.user.UserDao;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.PropertiesManager;
import com.pay10.commons.util.StatusType;
import com.pay10.commons.util.TransactionManager;
import com.pay10.commons.util.TransactionType;
import com.pay10.commons.util.Validator;
import com.pay10.pg.core.util.CashfreeChecksumUtil;
import com.pay10.pg.core.util.ProcessManager;
import com.pay10.pg.core.util.Processor;

@Service
public class CashfreeSaleResponseHandler {

	private static Logger logger = LoggerFactory.getLogger(CashfreeSaleResponseHandler.class.getName());

	@Autowired
	private Validator generalValidator;

	@Autowired
	@Qualifier("updateProcessor")
	private Processor updateProcessor;

	@Autowired
	@Qualifier("cashfreeTransactionConverter")
	private TransactionConverter transactionConverter;

	@Autowired
	private CashfreeTransformer cashfreeTransformer;

	@Autowired
	private CashfreeChecksumUtil cashFreeChecksumUtil;

	@Autowired
	@Qualifier("cashfreeTransactionCommunicator")
	private TransactionCommunicator transactionCommunicator;
	
	@Autowired
	CashfreeRefundDao cashfreeRefundDao;
	
	@Autowired
	UserDao userDao;


	public Map<String, String> process(Fields fields) throws SystemException {

		String newTxnId = TransactionManager.getNewTransactionId();
		fields.put(FieldType.TXN_ID.getName(), newTxnId);
		fields.put(FieldType.TXNTYPE.getName(), TransactionType.SALE.getCode());
		Transaction transactionResponse = new Transaction();
		String response = fields.get(FieldType.CASHFREE_RESPONSE_FIELD.getName());

		//boolean res = isHashMatching(response, fields);
		boolean doubleVer = doubleVerification(response, fields);
		/*
		 * String simulatorFlag =
		 * PropertiesManager.propertiesMap.get("CashfreeSimulator"); boolean res;
		 * boolean doubleVer; if (simulatorFlag.equalsIgnoreCase("Y")) { res = true;
		 * doubleVer = true; } else { res = isHashMatching(response, fields); doubleVer
		 * = doubleVerification(response, fields); }
		 */

		logger.info("doubleVerification flag for Cashfree : "+doubleVer);
		fields.remove(FieldType.CASHFREE_RESPONSE_FIELD.getName());
		generalValidator.validate(fields);

		if (doubleVer) {
		 
			transactionResponse = toTransaction(response, transactionResponse);
			cashfreeTransformer = new CashfreeTransformer(transactionResponse);
			cashfreeTransformer.updateResponse(fields);
		
		  } else { fields.put(FieldType.STATUS.getName(), StatusType.DENIED.getName());
		  fields.put(FieldType.RESPONSE_CODE.getName(),
		  ErrorType.SIGNATURE_MISMATCH.getCode());
		  fields.put(FieldType.RESPONSE_MESSAGE.getName(),
		  ErrorType.SIGNATURE_MISMATCH.getResponseMessage()); }
		 
		fields.put(FieldType.INTERNAL_ORIG_TXN_TYPE.getName(), fields.get(FieldType.TXNTYPE.getName()));
		ProcessManager.flow(updateProcessor, fields, true);
		return fields.getFields();

	}

	public Transaction toTransaction(String response, Transaction transactionResponse) {
		Transaction transaction = new Transaction();
		transaction = transactionConverter.toTransaction(response);
		return transaction;
	}

	private boolean isHashMatching(String transactionResponse, Fields fields) throws SystemException {

		JSONObject resJson = new JSONObject(transactionResponse.toString());

		String responseSignature = (String) resJson.get(Constants.signature);

		resJson.remove(Constants.signature);
		String resSignatureCalculated = cashFreeChecksumUtil.checkSaleResponseHash(resJson,
				fields.get(FieldType.TXN_KEY.getName()));

		logger.info("Order Id " + fields.get(FieldType.ORDER_ID.getName()) + "  bank response signature == "
				+ responseSignature);
		logger.info("Order Id " + fields.get(FieldType.ORDER_ID.getName()) + "  calculated signature == "
				+ resSignatureCalculated);

		if (responseSignature.contentEquals(resSignatureCalculated)) {
			return true;
		} else {
			logger.info("Signature from Bank did not match with generated Signature");
			logger.info("Bank Hash = " + responseSignature);
			logger.info("Calculated Hash = " + resSignatureCalculated);
			return false;
		}

	}

	private boolean doubleVerification(String transactionResponse, Fields fields) throws SystemException {

		try {

			JSONObject resJson = new JSONObject(transactionResponse);
			JSONObject dataInfo = new JSONObject(resJson.get("data").toString());
			JSONObject paymentInfo = new JSONObject(dataInfo.get("payment").toString());
			
			// payment_status = "SUCCESS"
			if (!transactionResponse.contains("payment_status")) {
				return true;
			}
			
			if (transactionResponse.contains("payment_status") 
					&& !paymentInfo.get("payment_status").toString().equalsIgnoreCase("SUCCESS")) {
				return true;
			}

			
			String request = transactionConverter.statusEnquiryRequest(fields, null);
			
			logger.info("Double Verification Request for Cashfree = " + request);
			String hostUrl = PropertiesManager.propertiesMap.get(Constants.STATUS_ENQ_REQUEST_URL);
			String response = transactionCommunicator.statusEnqPostRequest(request, hostUrl);
			logger.info("Double Verification Response from Cashfree = " + response);

			JSONObject resJsonBank = new JSONObject(response);

			if ((paymentInfo.get("payment_status").toString().equalsIgnoreCase(resJsonBank.get(Constants.txStatus).toString()))
					&& (paymentInfo.get("cf_payment_id").toString()
							.equalsIgnoreCase(resJsonBank.get("referenceId").toString()))) {
				return true;
			} else {
				logger.info("Double Verification Response donot match for Cashfree");
				return false;
			}

		}

		catch (Exception e) {
			logger.error("Exceptionn ", e);
			return false;
		}

	}
	
	public String refundScheduler() {

		List<CashfreeRefundDTO> response;
		try {
			response = cashfreeRefundDao.getAllPendingRefundTxn();
			logger.info("response :: " + response);

			response.forEach(action -> {

				logger.info("PayId :: " + action.getPayId());

				User user = new User();
				user = userDao.findPayId(action.getPayId());

				// logger.info("user >>>>>>>>>>>>>>>>>>> "+user.toString());
				Set<Account> accountSet = new HashSet<Account>();
				accountSet = user.getAccounts();
				logger.info("accountSet " + accountSet.size());

				String merchantId = null;
				String secretKey = null;
				for (Account account : accountSet) {
					Set<AccountCurrency> accountCurrency = new HashSet<AccountCurrency>();
					accountCurrency = account.getAccountCurrencySet();

					logger.info("accountCurrency " + accountCurrency.size());

					for (AccountCurrency accountCur : accountCurrency) {
						logger.info("user >>>>>>>>>>>>>>>>>>> " + accountCur.getTxnKey());
						logger.info("user >>>>>>>>>>>>>>>>>>> " + accountCur.getMerchantId());

						merchantId = accountCur.getMerchantId();
						secretKey = accountCur.getTxnKey();
					}

				}

				logger.info("merchantId :: " + merchantId);
				logger.info("secretKey :: " + secretKey);
				logger.info("orderId :: " + action.getOrderId());
				logger.info("RefundOrderId :: " + action.getRefundOrderId());
				logger.info("PgRefNo :: " + action.getPgRefNo());
				
				String hostUrl = PropertiesManager.propertiesMap.get(Constants.REFUND_STATUS_REQUEST_URL);
				
				try {
					String refundResponse = transactionCommunicator.getRefundStatus(hostUrl, merchantId, secretKey, action.getRefundOrderId(), action.getPgRefNo());
					logger.info("Cashfree Refund Resposne Received For PgRefNo : "+action.getPgRefNo() + " & RefundOrderId : "+action.getRefundOrderId() );
					JSONObject refundResp = new JSONObject(refundResponse);
					if(refundResp.has("refund_status")) {
						if(refundResp.getString("refund_status").equalsIgnoreCase("SUCCESS") || refundResp.getString("refund_status").equalsIgnoreCase("FAILED")) {
							cashfreeRefundDao.updateRefundStatus(refundResp);
						}
					}
					
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			});

		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";

	}
	
	public Map<String, String> txnStatus(Map<String, String> reqmap) throws SystemException {
		
		 CashfreeTxnRespDTO response = cashfreeRefundDao.getCashfreeTxnStatus(reqmap);
		 
		 ObjectMapper oMapper = new ObjectMapper();
		 Map<String, String> map = oMapper.convertValue(response, Map.class);
	       
	    return map;
	}
}
