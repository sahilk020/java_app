package com.pay10.camspay;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
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
import com.pay10.pg.core.camspay.util.CamsPayHasher;
import com.pay10.pg.core.util.ProcessManager;
import com.pay10.pg.core.util.Processor;

import bsh.This;

@Service
public class CamsPaySaleResponseHandler {
	private static Logger logger = LoggerFactory.getLogger(This.class.getName());

	@Autowired
	private Validator generalValidator;

	@Autowired
	@Qualifier("updateProcessor")
	private Processor updateProcessor;

	@Autowired
	private CamsPayStatusEnquiryProcessor camsPayStatusEnquiryProcessor;

	public Map<String, String> process(Fields fields) throws SystemException {

		String newTxnId = TransactionManager.getNewTransactionId();
		fields.put(FieldType.TXN_ID.getName(), newTxnId);
		generalValidator.validate(fields);
		String response = fields.get(FieldType.CAMSPAY_RESPONSE_FIELD.getName());
		try {
			response = CamsPayHasher.decryptMessage(response, fields.get(FieldType.ADF3.getName()),
					fields.get(FieldType.ADF4.getName()));
		} catch (Exception ex) {
			logger.error("process:: failed. response={}, fields={}", response, fields.getFieldsAsString(), ex);
		}
		Transaction transactionResponse = camsPayStatusEnquiryProcessor.toTransaction(response,
				fields.get(FieldType.TXNTYPE.getName()));

		boolean doubleVer = doubleVerification(transactionResponse, fields);
		logger.info("process:: doubleVerification={}, pgRefNo={}, orderId={}", doubleVer,
				fields.get(FieldType.PG_REF_NUM.getName()), fields.get(FieldType.ORDER_ID.getName()));
		if (doubleVer) {
			CamsPayTransformer camsPayTransformer = new CamsPayTransformer(transactionResponse);
			logger.info("process:: fields={}", fields.getFieldsAsString());
			camsPayTransformer.updateResponse(fields);
		} else {
			fields.put(FieldType.STATUS.getName(), StatusType.DENIED.getName());
			fields.put(FieldType.RESPONSE_CODE.getName(), ErrorType.SIGNATURE_MISMATCH.getCode());
			fields.put(FieldType.RESPONSE_MESSAGE.getName(), ErrorType.SIGNATURE_MISMATCH.getResponseMessage());
		}

		fields.put(FieldType.INTERNAL_ORIG_TXN_TYPE.getName(), fields.get(FieldType.TXNTYPE.getName()));
		fields.remove(FieldType.CAMSPAY_RESPONSE_FIELD.getName());
		ProcessManager.flow(updateProcessor, fields, true);
		return fields.getFields();

	}

	private boolean doubleVerification(Transaction transactionResponse, Fields fields) throws SystemException {
		logger.info("transactionResponse>> >>camspay>>" + transactionResponse);

		try {

			if (StringUtils.isBlank(transactionResponse.getResponseCode())) {
				return false;
			}
			// Don't check for transactions other than successful transactions.
			if (!(transactionResponse.getResponseCode()).equalsIgnoreCase(CamsPayResultType.RC111.getBankCode())) {
				return true;
			}

			String request = camsPayStatusEnquiryProcessor.statusEnquiryRequest(fields);
			logger.info("doubleVerification::request. camspay orderId={},pgRefNum={},amount={}, request={}",
					fields.get(FieldType.ORDER_ID.getName()), fields.get(FieldType.PG_REF_NUM.getName()),
					fields.get(FieldType.TOTAL_AMOUNT.getName()), request);

			String apiKey = fields.get(FieldType.ADF9.getName());
			String response = CamsPayStatusEnquiryProcessor.getResponse(request, apiKey,
					fields.get(FieldType.ADF6.getName()), fields.get(FieldType.ADF7.getName()));
			logger.info("doubleVerification::response. camspay orderId={},pgRefNum={}, response={},amount={}",
					fields.get(FieldType.ORDER_ID.getName()), fields.get(FieldType.PG_REF_NUM.getName()), response,
					fields.get(FieldType.TOTAL_AMOUNT.getName()));

			Transaction doubleVerTxnResponse = camsPayStatusEnquiryProcessor.toTransactionForInquiry(response,
					fields.get(FieldType.INTERNAL_ORIG_TXN_TYPE.getName()));
			logger.info("doubleVerTxnResponse >>camspay >>> " + doubleVerTxnResponse);

			if (doubleVerTxnResponse == null) {
				logger.info("doubleVerification:: transaction response is null.");
				return false;
			}

			if (StringUtils.isBlank(doubleVerTxnResponse.getAmount())
					|| StringUtils.isBlank(transactionResponse.getAmount())) {
				logger.info("doubleVerification:: amount={}, doubleVerificationAmount={}",
						transactionResponse.getAmount(), doubleVerTxnResponse.getAmount());
				return false;
			}

			String fieldsAmount = Amount.toDecimal(fields.get(FieldType.TOTAL_AMOUNT.getName()),
					fields.get(FieldType.CURRENCY_CODE.getName()));
			logger.info("dual verfication for camspay response txnid={},amount={}", transactionResponse.getTxnId(),
					fieldsAmount);
			if ((doubleVerTxnResponse.getResponseCode().equals(transactionResponse.getResponseCode()))
					&& doubleVerTxnResponse.getAmount().equals(fieldsAmount)
					&& doubleVerTxnResponse.getTxnId().equals(transactionResponse.getTxnId())) {
				logger.info("dual verification for camspay is successful");
				transactionResponse.setTxnId(doubleVerTxnResponse.getTxnId());
				return true;
			} else {
				logger.info(
						"doubleVerification::failed. pgRefNo={}, orderId={}, response mismatched responseCode={}, doubleVerificationResponseCode={}, fieldsAmount={}, doubleVerificationAmount={}",
						fields.get(FieldType.PG_REF_NUM.getName()), fields.get(FieldType.ORDER_ID.getName()),
						transactionResponse.getResponseCode(), doubleVerTxnResponse.getResponseCode(), fieldsAmount,
						doubleVerTxnResponse.getAmount());
				transactionResponse.setTxnId(doubleVerTxnResponse.getTxnId());
				return false;
			}

		} catch (Exception e) {
			logger.error("doubleVerification:: failed.", e);
			return false;
		}

	}
}
