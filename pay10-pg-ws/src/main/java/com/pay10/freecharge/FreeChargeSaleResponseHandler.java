package com.pay10.freecharge;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.exception.SystemException;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.StatusType;
import com.pay10.commons.util.TransactionManager;
import com.pay10.commons.util.Validator;
import com.pay10.pg.core.util.FreeChargeUtil;
import com.pay10.pg.core.util.ProcessManager;
import com.pay10.pg.core.util.Processor;
import com.pay10.pg.history.Historian;

@Service
public class FreeChargeSaleResponseHandler {

	private static Logger logger = LoggerFactory.getLogger(FreeChargeSaleResponseHandler.class.getName());

	@Autowired
	private Validator generalValidator;

	@Autowired
	private Historian historian;

	@Autowired
	@Qualifier("updateProcessor")
	private Processor updateProcessor;

	@Autowired
	private FreeChargeTransformer freeChargeTransformer;

	@Autowired
	private FreeChargeStatusEnquiryProcessor freeChargeStatusEnquiryProcessor;

	@Autowired
	private FreeChargeUtil freeChargeUtil;

	public Map<String, String> process(Fields fields) throws SystemException {
		String newTxnId = TransactionManager.getNewTransactionId();
		fields.put(FieldType.TXN_ID.getName(), newTxnId);
		generalValidator.validate(fields);
		Transaction transactionResponse = new Transaction();
		String freeChargeResponse = fields.get(FieldType.FREECHARGE_RESPONSE_FIELD.getName());
		transactionResponse = toTransaction(freeChargeResponse);

		freeChargeTransformer = new FreeChargeTransformer(transactionResponse);
		freeChargeTransformer.updateResponse(fields);

		// Double verification for captured transaction
		boolean isHashMatch = doubleVerification(fields, transactionResponse);

		// Re-verify Status for Failed Transactions
		if (!fields.get(FieldType.STATUS.getName()).equalsIgnoreCase(StatusType.CAPTURED.getName())) {
			statusEnquiry(fields);
		}

		if (!isHashMatch) {

			fields.put(FieldType.STATUS.getName(), StatusType.DECLINED.getName());
			fields.put(FieldType.RESPONSE_MESSAGE.getName(), "Bank Hash does not match calculated hash");
			fields.put(FieldType.RESPONSE_CODE.getName(), ErrorType.DECLINED.getCode());
		}

		//historian.addPreviousSaleFields(fields);
		fields.put(FieldType.INTERNAL_ORIG_TXN_TYPE.getName(), fields.get(FieldType.TXNTYPE.getName()));
		fields.remove(FieldType.FREECHARGE_RESPONSE_FIELD.getName());
		ProcessManager.flow(updateProcessor, fields, true);
		return fields.getFields();

	}

	public Transaction toTransaction(String freeChargeResponse) {

		Transaction transaction = new Transaction();

		String responseArray[] = freeChargeResponse.split("&&");

		Map<String, String> responseMap = new HashMap<String, String>();

		for (String item : responseArray) {

			String itemsArray[] = item.split("=");
			if (itemsArray.length > 1 && itemsArray[0] != null && itemsArray[1] != null) {
				String key = itemsArray[0];
				String value = itemsArray[1];
				responseMap.put(key, value);
			}
		}

		logger.info("Final Bank Response for FreeCharge : " + freeChargeResponse);

		if (StringUtils.isNotBlank(responseMap.get(Constants.AMOUNT))) {
			transaction.setAmount(responseMap.get(Constants.AMOUNT));
		}

		if (StringUtils.isNotBlank(responseMap.get(Constants.TXNID))) {
			transaction.setTxnId(responseMap.get(Constants.TXNID));
		}

		if (StringUtils.isNotBlank(responseMap.get(Constants.ERROR_CODE))) {
			transaction.setErrorCode(responseMap.get(Constants.ERROR_CODE));
		}

		if (StringUtils.isNotBlank(responseMap.get(Constants.ERROR_MSG))) {
			transaction.setErrorMessage(responseMap.get(Constants.ERROR_MSG));
		}

		if (StringUtils.isNotBlank(responseMap.get(Constants.STATUS))) {
			transaction.setStatus(responseMap.get(Constants.STATUS));
		}

		if (StringUtils.isNotBlank(responseMap.get(Constants.CHECKSUM))) {
			transaction.setChecksum(responseMap.get(Constants.CHECKSUM));
		}

		return transaction;
	}// toTransaction()

	public boolean doubleVerification(Fields fields, Transaction transaction) {

		try {

			if (fields.get(FieldType.STATUS.getName()).equalsIgnoreCase(StatusType.CAPTURED.getName())) {
				return true;
			}

			String freeChargeResponseSplit[] = fields.get(FieldType.FREECHARGE_RESPONSE_FIELD.getName()).split("&&");
			Map<String, String> hmReqFields = new TreeMap<String, String>();
			String bankHash = null;

			for (String param : freeChargeResponseSplit) {
				String paramSplit[] = param.split("=");
				if (paramSplit.length > 1 && paramSplit[0] != null && paramSplit[1] != null) {
					if (paramSplit[0].equalsIgnoreCase(Constants.CHECKSUM)) {
						bankHash = paramSplit[1];
					} else {
						hmReqFields.put(paramSplit[0], paramSplit[1]);
					}
				}
			}

			StringBuilder sb = new StringBuilder();
			sb.append("{");
			for (Entry<String, String> entry : hmReqFields.entrySet()) {
				sb.append("\"");
				sb.append(entry.getKey());
				sb.append("\"");
				sb.append(":");
				sb.append("\"");
				sb.append(entry.getValue());
				sb.append("\"");
				sb.append(",");
			}
			sb.deleteCharAt(sb.length() - 1);
			sb.append("}");

			String calculatedHash = freeChargeUtil.generateChecksum(sb.toString(),
					fields.get(FieldType.TXN_KEY.getName()));

			logger.info("Bank hash for Order Id = " + fields.get(FieldType.ORDER_ID.getName()) + "  >>>>  " + bankHash);
			logger.info("Calculated hash for Order Id = " + fields.get(FieldType.ORDER_ID.getName()) + "  >>>>  "
					+ calculatedHash);

			if (bankHash.equalsIgnoreCase(calculatedHash)) {

				logger.info("Bank Hash Matches with Calculated hash for FreeCharge txn");
				return true;
			} else {

				logger.info("Bank Hash does not match with Calculated hash for FreeCharge txn");
				return false;
			}
		} catch (Exception e) {

			logger.error(
					"Exception in double verification for PG REF NUM = " + fields.get(FieldType.PG_REF_NUM.getName()),
					e);
		}

		return false;

	}

	public boolean statusEnquiry(Fields fields) {

		try {
			logger.info("Before Double enquiry , Status for PG REF NUM = " + fields.get(FieldType.PG_REF_NUM.getName())
					+ " = " + fields.get(FieldType.STATUS.getName()));

			freeChargeStatusEnquiryProcessor.enquiryProcessor(fields);

			logger.info("After Double enquiry , Status for PG REF NUM = " + fields.get(FieldType.PG_REF_NUM.getName())
					+ " = " + fields.get(FieldType.STATUS.getName()));

		} catch (Exception e) {
			logger.error("Exception in freeCharge Double enquiry for PG REF NUM = "
					+ fields.get(FieldType.PG_REF_NUM.getName()), e);
		}

		return false;

	}

}
