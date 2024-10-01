package com.pay10.yesbank.netbanking;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.exception.SystemException;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.PropertiesManager;
import com.pay10.commons.util.StatusType;
import com.pay10.commons.util.TransactionManager;
import com.pay10.pg.core.util.ProcessManager;
import com.pay10.pg.core.util.Processor;

@Service
public class YesbankNBSaleResponseHandler {

	private static Logger logger = LoggerFactory.getLogger(YesbankNBSaleResponseHandler.class.getName());

	@Autowired
	@Qualifier("updateProcessor")
	private Processor updateProcessor;

	@Autowired
	private YesbankNBTransformer yesbankTransformer;

	@Autowired
	private TransactionConverter transactionConverter;

	@Autowired
	@Qualifier("yesbankNBTransactionCommunicator")
	private TransactionCommunicator transactionCommunicator;

	public static final String PIPE = "|";
	public static final String DOUBLE_PIPE = "||";

	public Map<String, String> process(Fields fields) throws SystemException {

		String newTxnId = TransactionManager.getNewTransactionId();
		fields.put(FieldType.TXN_ID.getName(), newTxnId);
		// generalValidator.validate(fields);
		Transaction transactionResponse = new Transaction();
		String pipedResponse = fields.get(FieldType.YESBANKNB_RESPONSE_FIELD.getName());

		String[] paramatersRespo = pipedResponse.split("&");
		Map<String, String> paramMapRespo = new HashMap<String, String>();
		logger.info("Fields In pipedResponse:{}"+fields.getFieldsAsString());
		for (String param : paramatersRespo) {
			String[] parameterPair = param.split("=");
			if (parameterPair.length > 1) {
				paramMapRespo.put(parameterPair[0], parameterPair[1]);


			}
		}


		String Message = paramMapRespo.get("Message");



		transactionResponse = toTransaction(pipedResponse);

		boolean doubleVer = doubleVerification(transactionResponse, fields);
		logger.info("process:: doubleVerification={}, pgRefNo={}, orderId={}", doubleVer,
				fields.get(FieldType.PG_REF_NUM.getName()), fields.get(FieldType.ORDER_ID.getName()));
		if (doubleVer) {
			yesbankTransformer = new YesbankNBTransformer(transactionResponse);
			yesbankTransformer.updateResponse(fields);
		} else {

			fields.put(FieldType.STATUS.getName(), StatusType.DENIED.getName());
			fields.put(FieldType.RESPONSE_CODE.getName(), ErrorType.DENIED_BY_FRAUD.getCode());
			fields.put(FieldType.RESPONSE_MESSAGE.getName(), transactionResponse.getMessage());
		}

		fields.put(FieldType.INTERNAL_ORIG_TXN_TYPE.getName(), fields.get(FieldType.TXNTYPE.getName()));
		fields.remove(FieldType.YESBANKNB_RESPONSE_FIELD.getName());
		ProcessManager.flow(updateProcessor, fields, true);
		return fields.getFields();

	}

	private boolean doubleVerification(Transaction transactionResponse, Fields fields) {

		try {

			// Skip if txStatus is not present in response
			if (null == transactionResponse) {
				return true;
			}

			// Skip for unsuccessful transactions if
			if (transactionResponse.getBankRefNo().equalsIgnoreCase("0")) {
				return true;
			}

			String encrequest = transactionConverter.statusEnquiryRequest(fields, transactionResponse);
			String hostUrl = PropertiesManager.propertiesMap.get(Constants.YESBANK_STATUS_ENQ_REQUEST_URL);

			String response = transactionCommunicator.getEnqResponse(encrequest, hostUrl, fields);
			Transaction transactionStatusResponse = transactionConverter.toTransactionStatus(response);

			if (transactionStatusResponse.getFlgSuccess() != null
					&& transactionStatusResponse.getFlgSuccess().equalsIgnoreCase("S")
					&& StringUtils.equalsIgnoreCase(transactionStatusResponse.getMerchantRefNumber(),
							transactionResponse.getMerchantRefNumber())
					&& StringUtils.equalsIgnoreCase(transactionStatusResponse.getTxnAmount(),
							transactionResponse.getTxnAmount())) {

				return true;
			}
			logger.info(
					"doubleVerification:: failed. pgRefNo={}, orderId={}, resStatus={}, doubleVerStatus={}, resAmount={}, doubleVerAmount={}, resTxnId={}, doubleVerTxnId={}",
					fields.get(FieldType.PG_REF_NUM.getName()), fields.get(FieldType.ORDER_ID.getName()),
					transactionResponse.getFlgSuccess(), transactionStatusResponse.getFlgSuccess(),
					transactionResponse.getTxnAmount(), transactionStatusResponse.getTxnAmount(),
					transactionResponse.getMerchantRefNumber(), transactionStatusResponse.getMerchantRefNumber());
			return false;
		}

		catch (Exception e) {
			logger.error("Exceptionn ", e);
			return false;
		}

	}

	public Transaction toTransaction(String pipedResponse) {
		Transaction transaction = new Transaction();
		transaction = transactionConverter.toTransaction(pipedResponse);
		return transaction;
	}// toTransaction()

}
