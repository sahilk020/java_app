package com.pay10.cosmos;

import java.io.IOException;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.exception.SystemException;
import com.pay10.commons.util.AcquirerType;
import com.pay10.commons.util.Amount;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.StatusType;
import com.pay10.commons.util.TransactionManager;
import com.pay10.commons.util.Validator;
import com.pay10.errormapping.ErrorMappingService;
import com.pay10.pg.core.util.CosmosUtil;
import com.pay10.pg.core.util.ProcessManager;
import com.pay10.pg.core.util.Processor;

@Service
public class CosmosUpiResponseHandler {
	private static Logger logger = LoggerFactory.getLogger(CosmosUpiResponseHandler.class.getName());
	@Autowired
	CosmosProcessor cosmosProcessor;
	@Autowired
	private Validator generalValidator;
	@Autowired
	@Qualifier("updateProcessor")
	private Processor updateProcessor;
	@Autowired
	private ErrorMappingService errorMappingService;

	public Map<String, String> process(Fields fields) throws SystemException, IOException {
		String newTxnId = TransactionManager.getNewTransactionId();
		logger.info("Cosmos UPI Response Handler " + fields.getFieldsAsString());
		fields.put(FieldType.TXN_ID.getName(), newTxnId);
		// fields.put("source",fields.get(FieldType.MERCHANT_ID.getName()));
		// generalValidator.validate(fields);
		// fields.put("terminalId",fields.get(FieldType.ADF2.getName()));

//     {"merchant_vpa":"bhartipay@cosb","remark":"upiPayment","errCode":"U30","status":"FAILURE",
//      "merchant":["qr.bhar"],"extTransactionId":"1319121130180535","customerName":"KATKAM PALLAVI ASHOK",
//      "responseTime":"Wed Nov 30 18:09:12 IST 2022","customer_vpa":"7838304007@cosb",
//      "rrn":"233418080003","txnId":"COBXFS8Y0WRPWC3YRRE0VMD19RN5989IXTS","checksum":"","amount":"500.00"}
		if ((!StringUtils.isEmpty(fields.get(FieldType.RESPONSE_MESSAGE.getName())))
				&& fields.get(FieldType.RESPONSE_MESSAGE.getName()).equals("SUCCESS")) {

			JSONObject dualResponseJson = cosmosProcessor.transactionStatus(fields);
			String source = dualResponseJson.getString("source");
			String channel = dualResponseJson.getString("channel");
			String terminalId = dualResponseJson.getString("terminalId");
			String extTransactionId = dualResponseJson.getString("extTransactionId");
			String status = dualResponseJson.getString("status");
			String dualVerificationChecksum = dualResponseJson.getString("checksum");
			JSONArray data = dualResponseJson.getJSONArray("data");
			String amount = data.getJSONObject(0).getString("amount");
			String respCode = data.getJSONObject(0).getString("respCode");
			String respMessage = data.getJSONObject(0).getString("respMessge"); // respMessage
			String txnTime = data.getJSONObject(0).getString("txnTime");
			String upiTxnId = data.getJSONObject(0).getString("upiTxnId");
			String upiId = data.getJSONObject(0).getString("upiId");
			String custRefNo = data.getJSONObject(0).getString("custRefNo");
			logger.info("cosmos  dual verification upi STATUS1 :{}", status);
			logger.info("cosmos dual verification upi RESPONSE_CODE1 :{}", respCode);
			logger.info("cosmos  dual verification upi AMOUNT1 :{}", amount);
			// logger.info("cosmos upi PG_REF_NUM1 :{}", "BHARTIPAY" +
			// fields.get(FieldType.PG_REF_NUM.getName()));

			String concatenatedString = source + channel + terminalId + extTransactionId + status + upiTxnId + respCode
					+ respMessage + txnTime + amount + upiId + custRefNo;
			String checksumKey = fields.get(FieldType.ADF4.getName());
			String dualVerificationChecksumCal = CosmosUtil.generateChecksum(concatenatedString, checksumKey);

			logger.info("cosmos upi dualVerificationChecksumCal...........{}", dualVerificationChecksumCal);
			String realtimeChecksum = fields.get("COSMOS_RESP_CHECKSUM");
			String realtimeChecksumCal = fields.get("COSMOS_RESP_CAL_CHECKSUM");
			// boolean isResponseChecksumMatched =
			// realtimeChecksum.equalsIgnoreCase(realtimeChecksumCal);

			boolean isDualChecksumMatched = dualVerificationChecksum.equalsIgnoreCase(dualVerificationChecksumCal);

			logger.info("isDualChecksumMatched.........................", isDualChecksumMatched);
			String cosmosResponseCode = fields.get(FieldType.RESPONSE_CODE.getName()) + '_'
					+ fields.get(FieldType.STATUS.getName());
			String toamount = Amount.toDecimal(fields.get(FieldType.TOTAL_AMOUNT.getName()),
					fields.get(FieldType.CURRENCY_CODE.getName()));
			logger.info("cosmos upi STATUS :{}", fields.get(FieldType.STATUS.getName()));
			logger.info("cosmos upi RESPONSE_CODE :{}", fields.get(FieldType.RESPONSE_CODE.getName()));
			logger.info("cosmos upi AMOUNT :{}", fields.get(FieldType.AMOUNT.getName()));
			logger.info("cosmos upi PG_REF_NUM :{}", "BHARTIPAY" + fields.get(FieldType.PG_REF_NUM.getName()));
			if (dualResponseJson.getString("status").equalsIgnoreCase(fields.get(FieldType.STATUS.getName())) // dual ka
																												// status
																												// &
																												// callback(CosmosResponseAction)
																												// ka
																												// status
					&& respCode.equalsIgnoreCase("0") // dual respCode & callback respCode
					&& amount.equalsIgnoreCase(toamount) // dual amount & response amount
																						// bhi same
					&& extTransactionId.equalsIgnoreCase("BHARTIPAY" + fields.get(FieldType.PG_REF_NUM.getName()))) // dual
																													// ka
																													// pgRef
																													// &&
																													// amount
																													// ka
																													// PgRef
			{
				Transaction transactionResponse = new Transaction();
				CosmosUpiTransformer cosmosUpiTransformer = new CosmosUpiTransformer(transactionResponse);
				cosmosUpiTransformer.updateResponseupi(fields);

			} else {
				fields.put(FieldType.STATUS.getName(), StatusType.DENIED.getName());
				fields.put(FieldType.RESPONSE_CODE.getName(), ErrorType.SIGNATURE_MISMATCH.getCode());
				//fields.put(FieldType.RESPONSE_MESSAGE.getName(), ErrorType.SIGNATURE_MISMATCH.getResponseMessage());
				fields.put(FieldType.RESPONSE_MESSAGE.getName(),respMessage);
			}
		} else {
			Transaction transactionResponse = new Transaction();
			CosmosUpiTransformer cosmosUpiTransformer = new CosmosUpiTransformer(transactionResponse);
			cosmosUpiTransformer.updateResponseupi(fields);

		}

		fields.remove("COSMOS_RESP_CHECKSUM");
		fields.remove("COSMOS_RESP_CAL_CHECKSUM");
		fields.put(FieldType.INTERNAL_ORIG_TXN_TYPE.getName(), fields.get(FieldType.TXNTYPE.getName()));
		// fields.remove(FieldType.SBI_RESPONSE_FIELD.getName());
		//String amount1 = Amount.formatAmount(fields.get(FieldType.AMOUNT.getName()), "356");
		//fields.put(FieldType.AMOUNT.getName(), amount1);
		ProcessManager.flow(updateProcessor, fields, true);
		return fields.getFields();

	}

}
