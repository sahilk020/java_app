package com.pay10.phonepe;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.DatatypeConverter;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.exception.SystemException;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.StatusType;
import com.pay10.commons.util.TransactionManager;
import com.pay10.commons.util.Validator;
import com.pay10.pg.core.util.ProcessManager;
import com.pay10.pg.core.util.Processor;

@Service
public class PhonePeSaleResponseHandler {

	private static Logger logger = LoggerFactory.getLogger(PhonePeSaleResponseHandler.class.getName());

	@Autowired
	private Validator generalValidator;

	@Autowired
	@Qualifier("updateProcessor")
	private Processor updateProcessor;

	@Autowired
	private PhonePeEnquiryRetryer phonePeEnquiryRetryer;

	@Autowired
	private PhonePeStatusEnquiryProcessor phonePeStatusEnquiryProcessor;

	public Map<String, String> process(Fields fields) throws SystemException {

		String newTxnId = TransactionManager.getNewTransactionId();
		fields.put(FieldType.TXN_ID.getName(), newTxnId);
		generalValidator.validate(fields);
		Transaction transactionResponse = new Transaction();
		String response = fields.get(FieldType.PHONEPE_RESPONSE_FIELD.getName());
		transactionResponse = toTransaction(response, fields);

		boolean doubleVerify = doubleVerification(transactionResponse, fields);
		logger.info("process:: double verfication success={}, pgRefNo={}, orderId={}, amount={}", doubleVerify,
				fields.get(FieldType.PG_REF_NUM.getName()), fields.get(FieldType.ORDER_ID.getName()),
				fields.get(FieldType.TOTAL_AMOUNT.getName()));

		if (doubleVerify) {
			PhonePeTransformer phonepeTransformer = new PhonePeTransformer(transactionResponse);
			phonepeTransformer.updateResponse(fields);
		} else {
			fields.put(FieldType.STATUS.getName(), StatusType.DENIED.getName());
			fields.put(FieldType.RESPONSE_CODE.getName(), ErrorType.SIGNATURE_MISMATCH.getCode());
			fields.put(FieldType.RESPONSE_MESSAGE.getName(), ErrorType.SIGNATURE_MISMATCH.getResponseMessage());
		}

		fields.put(FieldType.INTERNAL_ORIG_TXN_TYPE.getName(), fields.get(FieldType.TXNTYPE.getName()));
		fields.remove(FieldType.PHONEPE_RESPONSE_FIELD.getName());
		ProcessManager.flow(updateProcessor, fields, true);
		return fields.getFields();
	}

	public Transaction toTransaction(String response, Fields fields) {

		Transaction transaction = new Transaction();

		if (StringUtils.isBlank(response)) {

			logger.info("Empty response received for Phone Pe");
			return transaction;
		}

		String respArray[] = response.replace(" ", "").split(";");
		Map<String, String> respArrMap = new HashMap<String, String>();

		for (String data : respArray) {

			// Create Map for Hash parameters
			String dataArr[] = data.split("=");
			respArrMap.put(dataArr[0], dataArr[1]);

			if (data.contains("code")) {

				String dataArray[] = data.split("=");
				transaction.setCode(dataArray[1]);
			}

			if (data.contains("providerReferenceId")) {

				String dataArray[] = data.split("=");
				transaction.setProviderReferenceId(dataArray[1]);

			}

			if (data.contains(Constants.TRANSACTION_ID)) {
				String dataArray[] = data.split("=");
				transaction.setTransactionId(dataArray[1]);
			}

			if (data.contains("checksum")) {

				String dataArray[] = data.split("=");
				transaction.setHashCodeAcquirer(dataArray[1]);

			}
		}

		StringBuilder hashBuilder = new StringBuilder();

		hashBuilder.append(respArrMap.get("code"));
		hashBuilder.append(respArrMap.get("merchantId"));
		hashBuilder.append(fields.get(FieldType.PG_REF_NUM.getName()));
		hashBuilder.append(fields.get(FieldType.TOTAL_AMOUNT.getName()));
		hashBuilder.append(respArrMap.get("providerReferenceId"));
		hashBuilder.append(respArrMap.get("param1"));
		hashBuilder.append(respArrMap.get("param2"));
		hashBuilder.append(respArrMap.get("param3"));
		hashBuilder.append(respArrMap.get("param4"));
		hashBuilder.append(respArrMap.get("param5"));
		hashBuilder.append(respArrMap.get("param6"));
		hashBuilder.append(respArrMap.get("param7"));
		hashBuilder.append(respArrMap.get("param8"));
		hashBuilder.append(respArrMap.get("param9"));
		hashBuilder.append(respArrMap.get("param10"));
		hashBuilder.append(respArrMap.get("param11"));
		hashBuilder.append(respArrMap.get("param12"));
		hashBuilder.append(respArrMap.get("param13"));
		hashBuilder.append(respArrMap.get("param14"));
		hashBuilder.append(respArrMap.get("param15"));
		hashBuilder.append(respArrMap.get("param16"));
		hashBuilder.append(respArrMap.get("param17"));
		hashBuilder.append(respArrMap.get("param18"));
		hashBuilder.append(respArrMap.get("param19"));
		hashBuilder.append(respArrMap.get("param20"));
		hashBuilder.append(fields.get(FieldType.TXN_KEY.getName()));

		String sha256String = null;
		try {

			MessageDigest md = MessageDigest.getInstance("SHA-256");
			byte[] digest = md.digest(hashBuilder.toString().getBytes(StandardCharsets.UTF_8));
			sha256String = DatatypeConverter.printHexBinary(digest).toLowerCase();
		} catch (Exception e) {
			logger.error("Exception in generating PhonePe hash String", e);
		}
		transaction.setAmount(fields.get(FieldType.TOTAL_AMOUNT.getName()));
		transaction.setHashCodePg(sha256String + Constants.SEPARATOR + fields.get(FieldType.ADF1.getName()));
		return transaction;

	}

	private boolean doubleVerification(Transaction transaction, Fields fields) {
		try {
			logger.info("doubleVerification:: transactions={}", new ObjectMapper().writeValueAsString(transaction));
			if (StringUtils.isBlank(transaction.getCode())) {
				logger.info("doubleVerification:: transaction.code={}", transaction.getCode());
				return false;
			}

			// Don't check for transactions other than successful transactions.
			if (!StringUtils.equalsIgnoreCase(transaction.getCode(), PhonePeResultType.PHONEPE000.getBankCode())) {
				return true;
			}
			fields.logAllFields("doubleVerification:: phonePe Double Verification Request : ");

			Transaction enquiryRes = statusEnquiry(fields);
			String pgRefNo = fields.get(FieldType.PG_REF_NUM.getName());
			String orderId = fields.get(FieldType.ORDER_ID.getName());
			if (enquiryRes == null) {
				logger.info("doubleVerification:: transaction response is null pgRefNo={}, orderId={}", pgRefNo,
						orderId);
				return false;
			}

			if (StringUtils.isBlank(enquiryRes.getAmount()) || StringUtils.isBlank(transaction.getAmount())) {
				logger.info("doubleVerification:: fieldsAmount={}, enquiryResponseAmount={}, pgRefNo={}, orderId={}",
						transaction.getAmount(), enquiryRes.getAmount(), pgRefNo, orderId);
				return false;
			}

			String fieldsAmount = fields.get(FieldType.TOTAL_AMOUNT.getName());
			if (enquiryRes.getCode().equals(transaction.getCode())
					&& enquiryRes.getAmount().equals(fields.get(FieldType.TOTAL_AMOUNT.getName()))
					&& StringUtils.equals(enquiryRes.getTransactionId(), transaction.getTransactionId())) {
				return true;
			}
			logger.info(
					"doubleVerification:: pgRefNo={}, orderId={}, enquiryResponseCode={}, fieldsResponseCode={}, enquiryResponseAmount={}, fieldsAmount={}, enquiryTransactionId={}, fieldsTransactionId={}",
					pgRefNo, orderId, enquiryRes.getPayResponseCode(), transaction.getPayResponseCode(),
					enquiryRes.getAmount(), fieldsAmount, enquiryRes.getTransactionId(),
					transaction.getTransactionId());
			return false;
		} catch (Exception ex) {
			logger.error("doubleVerification:: failed. pgRefNum={}", fields.get(FieldType.PG_REF_NUM.getName()), ex);
			return false;
		}
	}

	@SuppressWarnings("static-access")
	private Transaction statusEnquiry(Fields fields) throws SystemException {

		String request = phonePeStatusEnquiryProcessor.statusEnquiryRequest(fields);
		String pgRefNo = fields.get(FieldType.PG_REF_NUM.getName());
		String orderId = fields.get(FieldType.ORDER_ID.getName());

		logger.info("doubleVerification:: statusEnquiry: pgRefNo={}, orderId={}, request={}", pgRefNo, orderId, request);

		String response = phonePeStatusEnquiryProcessor.getResponse(request);
		logger.info("doubleVerification:: statusEnquiry: pgRefNo={}, orderId={}, response={}", pgRefNo, orderId, response);

		Transaction responseVerification = phonePeStatusEnquiryProcessor.toTransaction(response);
		if (StringUtils.equalsAnyIgnoreCase(responseVerification.getCode(), PhonePeResultType.PHONEPE004.getBankCode(),
				PhonePeResultType.PHONEPE006.getBankCode())) {
			responseVerification = phonePeEnquiryRetryer.retry(responseVerification, fields);
		}
		return responseVerification;
	}

}
