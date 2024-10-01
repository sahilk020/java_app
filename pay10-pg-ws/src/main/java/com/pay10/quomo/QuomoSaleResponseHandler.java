package com.pay10.quomo;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

import javax.xml.bind.DatatypeConverter;

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
import com.pay10.pg.core.util.ProcessManager;
import com.pay10.pg.core.util.Processor;

@Service
public class QuomoSaleResponseHandler {

	private static Logger logger = LoggerFactory.getLogger(QuomoSaleResponseHandler.class.getName());

	@Autowired
	@Qualifier("updateProcessor")
	private Processor updateProcessor;

	@Autowired
	private QuomoStatusEnquiryProcessor quomoStatusEnquiryProcessor;

	public Map<String, String> process(Fields fields) throws SystemException {

		logger.info("QuomoSaleResponseHandler, process "+fields.getFieldsAsString());
		String newTxnId = TransactionManager.getNewTransactionId();
		fields.put(FieldType.TXN_ID.getName(), newTxnId);
		String response = fields.get(FieldType.QUOMO_RESPONSE_FIELD.getName());
		logger.info("QUOMO_RESPONSE_FIELD response "+response);
		Transaction transactionResponse = quomoStatusEnquiryProcessor.toTransaction(response,
				fields.get(FieldType.TXNTYPE.getName()));

		boolean doubleVer = doubleVerification(transactionResponse, fields);
		logger.info("process:: doubleVerification={}, pgRefNo={}, orderId={}", doubleVer,
				fields.get(FieldType.PG_REF_NUM.getName()), fields.get(FieldType.ORDER_ID.getName()));

		if (doubleVer) {
			QuomoTransformer camsPayTransformer = new QuomoTransformer(transactionResponse);
			logger.info("process:: fields={}", fields.getFieldsAsString());
			camsPayTransformer.updateResponse(fields);
		} else {
			fields.put(FieldType.STATUS.getName(), StatusType.DENIED.getName());
			fields.put(FieldType.RESPONSE_CODE.getName(), ErrorType.SIGNATURE_MISMATCH.getCode());
			fields.put(FieldType.RESPONSE_MESSAGE.getName(), ErrorType.SIGNATURE_MISMATCH.getResponseMessage());
		}

		fields.put(FieldType.INTERNAL_ORIG_TXN_TYPE.getName(), fields.get(FieldType.TXNTYPE.getName()));
		fields.remove(FieldType.QUOMO_RESPONSE_FIELD.getName());
		ProcessManager.flow(updateProcessor, fields, true);
		return fields.getFields();
	}

	private boolean doubleVerification(Transaction transactionResponse, Fields fields) throws SystemException {
		logger.info("transactionResponse>> >>camspay>>" + transactionResponse);
		try {

			if (StringUtils.isBlank(transactionResponse.getStatus())) {
				return false;
			}
			// Don't check for transactions other than successful transactions.
			if (!(transactionResponse.getStatus()).equalsIgnoreCase("Successful")) {
				return true;
			}

			String request = quomoStatusEnquiryProcessor.statusEnquiryRequest(fields, transactionResponse);
			logger.info("doubleVerification::request. Quomo orderId={},pgRefNum={},amount={}, request={}",
					fields.get(FieldType.ORDER_ID.getName()), fields.get(FieldType.PG_REF_NUM.getName()),
					fields.get(FieldType.TOTAL_AMOUNT.getName()), request);

			String response = quomoStatusEnquiryProcessor.sendPostEnquiryRequest(request);
			
			logger.info("doubleVerification::response. Qupmo orderId={},pgRefNum={}, response={},amount={}",
					fields.get(FieldType.ORDER_ID.getName()), fields.get(FieldType.PG_REF_NUM.getName()), response,
					fields.get(FieldType.TOTAL_AMOUNT.getName()));

			Transaction doubleVerTxnResponse = quomoStatusEnquiryProcessor.toTransactionForInquiry(response,
					fields.get(FieldType.INTERNAL_ORIG_TXN_TYPE.getName()));
			logger.info("doubleVerTxnResponse >>Quomo >>> " + doubleVerTxnResponse);

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
			logger.info("dual verfication for Quomo response txnid={},amount={}", transactionResponse.getTxnId(), fieldsAmount);
			if ((doubleVerTxnResponse.getStatus().equals(transactionResponse.getStatus()))
					&& doubleVerTxnResponse.getTxnId().equals(transactionResponse.getTxnId())) {
				logger.info("dual verification for Quomo is successful");
				transactionResponse.setTxnId(doubleVerTxnResponse.getTxnId());
				return true;
			} else {
				logger.info("doubleVerification::failed. pgRefNo={}, orderId={}, response mismatched responseCode={}, doubleVerificationResponseCode={}, fieldsAmount={}, doubleVerificationAmount={}",
						fields.get(FieldType.PG_REF_NUM.getName()), fields.get(FieldType.ORDER_ID.getName()),
						transactionResponse.getStatus(), doubleVerTxnResponse.getStatus(), fieldsAmount, doubleVerTxnResponse.getAmount());
				transactionResponse.setTxnId(doubleVerTxnResponse.getTxnId());
				return false;
			}

		} catch (Exception e) {
			logger.error("doubleVerification:: failed.", e);
			return false;
		}

	}
	
	public static String signData(Map<String, Object> reqmap) throws NoSuchAlgorithmException {
		logger.info("Quomo Request for signData ::: " + reqmap);
		StringBuilder reqParam = new StringBuilder();
		reqParam.append(reqmap.get("md5Key"));
		reqParam.append(reqmap.get("merchantId"));
		reqParam.append(reqmap.get("businessEmail"));
		reqParam.append(reqmap.get("productName"));
		reqParam.append(reqmap.get("orderId"));
		reqParam.append(reqmap.get("depositMethodId"));
		reqParam.append(reqmap.get("bankId"));
		reqParam.append(reqmap.get("depositAmount"));
		reqParam.append(reqmap.get("currency"));
		reqParam.append("NA");
		reqParam.append("NA");
		reqParam.append("NA");
		reqParam.append("NA");
		reqParam.append(reqmap.get("customerName"));
		reqParam.append(reqmap.get("customerIp"));
		reqParam.append(reqmap.get("customerEmail"));
		reqParam.append(reqmap.get("customerPhoneNo"));
		reqParam.append(reqmap.get("customerAddress"));
		reqParam.append(reqmap.get("note"));
		reqParam.append(reqmap.get("websiteUrl"));
		reqParam.append(reqmap.get("requestTime"));
		reqParam.append(reqmap.get("successUrl"));
		reqParam.append(reqmap.get("failUrl"));
		reqParam.append(reqmap.get("callbackNotiUrl"));
		logger.info("request prepaid for signData :: " + reqParam.toString());
		MessageDigest md = MessageDigest.getInstance("MD5");
		byte[] digest = md.digest(reqParam.toString().getBytes());
		String myHash = DatatypeConverter.printHexBinary(digest).toUpperCase();
		logger.info("Quomo,sign_data myHash :: " + myHash);
		return myHash;
	}

}
