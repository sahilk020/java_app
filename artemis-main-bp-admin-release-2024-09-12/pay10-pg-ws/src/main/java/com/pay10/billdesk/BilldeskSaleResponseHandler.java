package com.pay10.billdesk;

import java.util.Map;

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
import com.pay10.commons.util.PropertiesManager;
import com.pay10.commons.util.StatusType;
import com.pay10.commons.util.TransactionManager;
import com.pay10.commons.util.TransactionType;
import com.pay10.commons.util.Validator;
import com.pay10.pg.core.util.BillDeskChecksumUtil;
import com.pay10.pg.core.util.ProcessManager;
import com.pay10.pg.core.util.Processor;

@Service
public class BilldeskSaleResponseHandler {

	private static Logger logger = LoggerFactory.getLogger(BilldeskSaleResponseHandler.class.getName());

	@Autowired
	private Validator generalValidator;

	@Autowired
	@Qualifier("updateProcessor")
	private Processor updateProcessor;

	@Autowired
	@Qualifier("billdeskTransactionConverter")
	private TransactionConverter transactionConverter;

	@Autowired
	private BilldeskTransformer billdeskTransformer;

	@Autowired
	private BillDeskChecksumUtil billDeskChecksumUtil;
	
	@Autowired
	@Qualifier("billdeskTransactionCommunicator")
	private TransactionCommunicator transactionCommunicator;

	public Map<String, String> process(Fields fields) throws SystemException {
		String newTxnId = TransactionManager.getNewTransactionId();
		fields.put(FieldType.TXN_ID.getName(), newTxnId);
		fields.put(FieldType.TXNTYPE.getName(),TransactionType.SALE.getCode());
		Transaction transactionResponse = new Transaction();
		String response = fields.get(FieldType.BILLDESK_RESPONSE_FIELD.getName());
		boolean res = isHashMatching(response, fields);
		boolean doubleVer = doubleVerification(response, fields);
		fields.remove(FieldType.BILLDESK_RESPONSE_FIELD.getName());
		generalValidator.validate(fields);

		if (res == true && doubleVer == true) {
			transactionResponse = toTransaction(response, transactionResponse);
			billdeskTransformer = new BilldeskTransformer(transactionResponse);
			billdeskTransformer.updateResponse(fields);
		} else {
			fields.put(FieldType.STATUS.getName(), StatusType.DENIED.getName());
			fields.put(FieldType.RESPONSE_CODE.getName(), ErrorType.SIGNATURE_MISMATCH.getCode());
			fields.put(FieldType.RESPONSE_MESSAGE.getName(), ErrorType.SIGNATURE_MISMATCH.getResponseMessage());
		}
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

		String transactionResponseSplit[] = transactionResponse.split("\\|");
		// Dont check for failed txns
		if (!transactionResponseSplit[14].equalsIgnoreCase("0300")) {
			return true;
		}

		String bankHash = transactionResponseSplit[transactionResponseSplit.length - 1];

		String amount = fields.get(FieldType.TOTAL_AMOUNT.getName());
		amount = Amount.toDecimal(amount, fields.get(FieldType.CURRENCY_CODE.getName()));

		StringBuilder paddedAmount = new StringBuilder();
		StringBuilder hashString = new StringBuilder();

		
		for (int i = 0; i < transactionResponseSplit.length - 1; i++) {

			if (i == 4) {
				int amountLength = amount.length();
				int zerosNeeded = 11 - amountLength;

				for (int j = 0; j < zerosNeeded; j++) {
					paddedAmount.append("0");
				}

				paddedAmount.append(amount);
				hashString.append(paddedAmount.toString());
				hashString.append("|");
				continue;

			}
			hashString.append(transactionResponseSplit[i]);

			if (i == transactionResponseSplit.length - 2) {
				//
			} else {
				hashString.append("|");
			}
		}

		logger.info("HASH Sting generated = " + hashString.toString());
		String calculatedHash = billDeskChecksumUtil.getHash(hashString.toString(),
				fields.get(FieldType.TXN_KEY.getName()));

		if (bankHash.contentEquals(calculatedHash)) {
			return true;
		} else {
			logger.info("HASH from Bank did not match with generated hash");
			logger.info("Bank Hash = "+bankHash);
			logger.info("Calculated Hash = "+calculatedHash);
			return false;
		}

	}

	private boolean doubleVerification(String transactionResponse, Fields fields) throws SystemException {
		
		try {
			
			String transactionResponseSplit [] = transactionResponse.split("\\|");
			
			// Skip for unsuccessful transactions
			if (!transactionResponseSplit [14].equalsIgnoreCase("0300")) {
				return true;
			}
			
			String request = transactionConverter.statusEnquiryRequest(fields, null);
			String hostUrl = PropertiesManager.propertiesMap.get(Constants.STATUS_ENQ_REQUEST_URL);
			String response = transactionCommunicator.statusEnqPostRequest(request, hostUrl);
			
			logger.info("Bank Response = "+transactionResponse);
			logger.info("Double Verification Response = "+response);
			
			
			
			String responseSplit [] = response.split("\\|");
			
			String responseStatus = responseSplit [31];
			
			if (responseStatus.equalsIgnoreCase("N")) {
				return true;
			}
			
			String transactionResponseAuthCode = transactionResponseSplit [14];
			String responseAuthCode = responseSplit [15];
			
			
			if (transactionResponseAuthCode.equals(responseAuthCode)) {
				return true;
			}
			else {
				logger.info("Double Verification Response donot match");
				return false;
			}
		}
		
		catch(Exception e) {
			logger.error("Exceptionn ",e);
			return false;
		}
	
		

	}
}
