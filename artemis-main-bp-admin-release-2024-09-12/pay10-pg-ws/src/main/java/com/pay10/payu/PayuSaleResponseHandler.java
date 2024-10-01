package com.pay10.payu;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.exception.SystemException;
import com.pay10.commons.util.Amount;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.StatusType;
import com.pay10.commons.util.TransactionManager;
import com.pay10.commons.util.Validator;
import com.pay10.pg.core.payu.util.Constants;
import com.pay10.pg.core.payu.util.PayuHasher;
import com.pay10.pg.core.util.ProcessManager;
import com.pay10.pg.core.util.Processor;

@Service
public class PayuSaleResponseHandler {
	private static Logger logger = LoggerFactory.getLogger(PayuSaleResponseHandler.class.getName());

	@Autowired
	private Validator generalValidator;

	@Autowired
	@Qualifier("updateProcessor")
	private Processor updateProcessor;
	
	@Autowired
	private PayuStatusEnquiryProcessor payuStatusEnquiryProcessor;


	public Map<String, String> process(Fields fields) throws SystemException {
		
		
		String newTxnId = TransactionManager.getNewTransactionId();
		fields.put(FieldType.TXN_ID.getName(), newTxnId);
		generalValidator.validate(fields);
		String response = fields.get(FieldType.PAYU_RESPONSE_FIELD.getName());
		/*JSONObject resJsonBank = new JSONObject(response);
		String txn_response_msg=resJsonBank.getString("txn_response_msg");
*/
		Transaction transactionResponse = toTransaction(response, fields);
		boolean res = isHashMatching(transactionResponse, fields);
		boolean doubleVer = doubleVerification(transactionResponse, fields);
		logger.info("process:: doubleVerification={}, pgRefNo={}, orderId={}", doubleVer,
				fields.get(FieldType.PG_REF_NUM.getName()), fields.get(FieldType.ORDER_ID.getName()));

		/*
		 * PayuTransformer payuTransformer = new PayuTransformer(transactionResponse);
		 * payuTransformer.updateResponse(fields);
		 */
		
		
		  if(res && doubleVer) { 
			  PayuTransformer payuTransformer = new PayuTransformer(transactionResponse);
			  logger.info("fields for payusaleresponse"+fields.getFieldsAsString());
			  payuTransformer.updateResponse(fields);
		  } else {
		fields.put(FieldType.STATUS.getName(), StatusType.DENIED.getName());
		  fields.put(FieldType.RESPONSE_CODE.getName(), ErrorType.SIGNATURE_MISMATCH.getCode());
		  fields.put(FieldType.RESPONSE_MESSAGE.getName(),  transactionResponse.getResponseMsg()); 
		  }
		 

		
		fields.put(FieldType.INTERNAL_ORIG_TXN_TYPE.getName(), fields.get(FieldType.TXNTYPE.getName()));
		fields.remove(FieldType.PAYU_RESPONSE_FIELD.getName());
		ProcessManager.flow(updateProcessor, fields, true);
		return fields.getFields();

	}

private boolean doubleVerification(Transaction transactionResponse, Fields fields) throws SystemException {
		
		try {
			
			if (StringUtils.isBlank(transactionResponse.getResponseCode())){
				return false;
			}
			
			// Don't check for transactions other than successful transactions.
			if(!(transactionResponse.getResponseCode()).equalsIgnoreCase(Constants.E000)){
				return true;
			}
			
			fields.logAllFields("Payu Double verification fields : ");
			String request = payuStatusEnquiryProcessor.statusEnquiryRequest(fields);
			logger.info("PayU double verification reequest : order id : " + fields.get(FieldType.ORDER_ID.getName()) + " : " + request);
			String response = PayuStatusEnquiryProcessor.getResponse(request);
			logger.info("PayU double verification response : order id : " + fields.get(FieldType.ORDER_ID.getName()) + " : " + response);
			
			Transaction doubleVerTxnResponse = payuStatusEnquiryProcessor.toTransaction(response, fields.get(FieldType.INTERNAL_ORIG_TXN_TYPE.getName()),
					fields.get(FieldType.PG_REF_NUM.getName()));
			
			if(doubleVerTxnResponse == null) {
				logger.info("Double verification transaction response is null.");
				return false;
			}
			
			if(StringUtils.isBlank(doubleVerTxnResponse.getAmount()) || StringUtils.isBlank(transactionResponse.getAmount())) {
				logger.info("Amount is empty in txn response : " + transactionResponse.getAmount() + ", double verification : " + doubleVerTxnResponse.getAmount());
				return false;
			}
			
			String fieldsAmount = Amount.toDecimal(fields.get(FieldType.TOTAL_AMOUNT.getName()), 
					fields.get(FieldType.CURRENCY_CODE.getName()));
			
			
			
			logger.info("doubleVerTxnResponse={},transactionResponse={}",doubleVerTxnResponse.getTxnId(),transactionResponse.getTxnId());
			
			if((doubleVerTxnResponse.getResponseCode().equals(transactionResponse.getResponseCode()))
				&& doubleVerTxnResponse.getAmount().equals(fieldsAmount)
				&& doubleVerTxnResponse.getTxnId().equals(transactionResponse.getTxnId())) {
				return true;
			} else {
				
				logger.info(
						"doubleVerification:: failed.pgRefNo={}, orderId={}, doubleVerificationRescode={}, responseCode={}, doubleVerificationAmount={}, responseAmount={}, doubleVerificationTxnId={}, responseTxnId={}",
						fields.get(FieldType.PG_REF_NUM.getName()), fields.get(FieldType.ORDER_ID.getName()),doubleVerTxnResponse.getResponseCode(), transactionResponse.getResponseCode(),doubleVerTxnResponse.getAmount(),fieldsAmount,doubleVerTxnResponse.getTxnId(),transactionResponse.getTxnId());
				return false;
			}
			
		} catch(Exception e) {
			logger.info("Exception while payu double verification : " + e);
			logger.info(e.getMessage());
			return false;
		}
		
	}

	private boolean isHashMatching(Transaction transactionResponse, Fields fields) {

		if (StringUtils.isBlank(transactionResponse.getResponseCode())){
			return false;
		}
		
		// Don't check for transactions other than successful transactions.
		if(!(transactionResponse.getResponseCode()).equalsIgnoreCase(Constants.E000)){
			return true;
		}
		
		ErrorType errorType = null;
		 logger.info(""+fields.get(FieldType.TOTAL_AMOUNT.getName()));
		String salt = fields.get(FieldType.ADF3.getName());
		String merchantkey = fields.get(FieldType.TXN_KEY.getName());
		logger.info("salt"+salt+"key"+merchantkey);
		Map<String, String> reqMap = new HashMap<String, String>();
		reqMap.put(Constants.SALT, salt);
		reqMap.put(Constants.STATUS, transactionResponse.getStatus());
		reqMap.put(Constants.EMAIL, transactionResponse.getEmail());
		//String firstName = fields.get(FieldType.CUST_NAME.getName());
		String firstName = transactionResponse.getFirstName();
		if(StringUtils.isBlank(firstName)) {
			firstName = Constants.PAY10;
		}
		
		reqMap.put(Constants.FIRSTNAME, firstName);
		reqMap.put(Constants.AMOUNT, Amount.toDecimal(fields.get(FieldType.TOTAL_AMOUNT.getName()), fields.get(FieldType.CURRENCY_CODE.getName())));
		reqMap.put(Constants.TXNID, transactionResponse.getTxnId());
		reqMap.put(Constants.KEY, merchantkey);

		String resphash = transactionResponse.getHash();
		String calRespHash = PayuHasher.payuResponseHash(reqMap);
		
		if(StringUtils.isBlank(resphash) || StringUtils.isBlank(calRespHash)) {
			logger.info("Response hash or calculated hash is empty");
			errorType = ErrorType.SIGNATURE_MISMATCH;
			fields.put(FieldType.STATUS.getName(), StatusType.DENIED_BY_FRAUD.getName());
				fields.put(FieldType.RESPONSE_MESSAGE.getName(),transactionResponse.getResponseMsg());
			//	fields.put(FieldType.RESPONSE_MESSAGE.getName(), errorType.getResponseMessage());
			fields.put(FieldType.RESPONSE_CODE.getName(), errorType.getResponseCode());
			return false;
		}
		
		if(resphash.equals(calRespHash)) {
			return true;
		} else {
			logger.info("Response hash, calculated hash mismatch");
			logger.info("Response hash   : " + resphash);
			logger.info("Calculated hash : " + calRespHash);
			logger.info(reqMap.toString());
			errorType = ErrorType.SIGNATURE_MISMATCH;
			fields.put(FieldType.STATUS.getName(), StatusType.DENIED_BY_FRAUD.getName());
			fields.put(FieldType.RESPONSE_MESSAGE.getName(),transactionResponse.getResponseMsg());

			/*fields.put(FieldType.RESPONSE_MESSAGE.getName(), errorType.getResponseMessage());*/
			fields.put(FieldType.RESPONSE_CODE.getName(), errorType.getResponseCode());
			return false;
		}
	}

	
	public Transaction toTransaction(String response, Fields fields) {

		Transaction transaction = new Transaction();
		 

		if (StringUtils.isBlank(response)) {

			logger.info("Empty response received ");
			return transaction;
		}

		String respArray[] = response.trim().split(";");

		for (String data : respArray) {

			if (data.contains(Constants.ERROR_MESSAGE)) {

				String dataArray[] = data.split("=");
				transaction.setResponseMsg(dataArray[1]);
			}

			if (data.contains(Constants.ERROR)) {

				String dataArray[] = data.split("=");
				if (dataArray[0].equalsIgnoreCase(Constants.ERROR)) {

					if (StringUtils.isNotBlank(dataArray[1])) {
						transaction.setResponseCode(dataArray[1]);
					}
				}
			}

			if (data.contains(Constants.MIHPAYID)) {

				String dataArray[] = data.split("=");
				if (dataArray[0].equalsIgnoreCase(Constants.MIHPAYID)) {

					if (StringUtils.isNotBlank(dataArray[1])) {
						transaction.setMihPayuId(dataArray[1]);
					}

				}

			}

			if (data.contains(Constants.TXNID)) {

				String dataArray[] = data.split("=");

				if (dataArray[0].equalsIgnoreCase(Constants.TXNID)) {

					if (StringUtils.isNotBlank(dataArray[1])) {
						transaction.setTxnId(dataArray[1]);
					}

				}

			}
			if (data.contains(Constants.STATUS)) {

				String dataArray[] = data.split("=");

				if (dataArray[0].equalsIgnoreCase(Constants.STATUS)) {

					if (StringUtils.isNotBlank(dataArray[1])) {
						transaction.setStatus(dataArray[1]);
					}

				}

			}

			if (data.contains(Constants.BANK_REF_NUMB)) {

				String dataArray[] = data.split("=");
				if (dataArray[0].equalsIgnoreCase(Constants.BANK_REF_NUMB)) {

					if (dataArray.length > 1 && StringUtils.isNotBlank(dataArray[1])) {
						transaction.setBankRefNum(dataArray[1]);
					}
				}

			}

			if (data.contains(Constants.HASH)) {

				String dataArray[] = data.split("=");
				if (dataArray[0].equalsIgnoreCase(Constants.HASH)) {

					if (dataArray.length > 1 && StringUtils.isNotBlank(dataArray[1])) {
						transaction.setHash(dataArray[1]);
					}
				}

			}

			if (data.contains(Constants.EMAIL)) {

				String dataArray[] = data.split("=");
				if (dataArray[0].equalsIgnoreCase(Constants.EMAIL)) {

					if (dataArray.length > 1 && StringUtils.isNotBlank(dataArray[1])) {
						transaction.setEmail(dataArray[1]);
					}
				}

			}

			if (data.contains(Constants.FIRSTNAME)) {

				String dataArray[] = data.split("=");
				if (dataArray[0].equalsIgnoreCase(Constants.FIRSTNAME)) {

					if (dataArray.length > 1 && StringUtils.isNotBlank(dataArray[1])) {
						transaction.setFirstName(dataArray[1]);
					}
				}

			}

			if (data.contains(Constants.PRODUCT_INFO)) {

				String dataArray[] = data.split("=");
				if (dataArray[0].equalsIgnoreCase(Constants.PRODUCT_INFO)) {

					if (dataArray.length > 1 && StringUtils.isNotBlank(dataArray[1])) {
						transaction.setProductInfo(dataArray[1]);
					}
				}

			}

			if (data.contains(Constants.AMOUNT)) {

				String dataArray[] = data.split("=");
				if (dataArray[0].equalsIgnoreCase(Constants.AMOUNT)) {

					if (dataArray.length > 1 && StringUtils.isNotBlank(dataArray[1])) {
						transaction.setAmount(dataArray[1]);
					}
				}

			}
		}
		
		return transaction;
	}

}
