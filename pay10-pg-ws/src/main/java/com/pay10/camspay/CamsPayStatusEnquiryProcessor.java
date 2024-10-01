package com.pay10.camspay;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.MapType;
import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.exception.SystemException;
import com.pay10.commons.util.AcquirerType;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.PropertiesManager;
import com.pay10.commons.util.StatusType;
import com.pay10.commons.util.TransactionType;
import com.pay10.pg.core.camspay.dto.CamsPayStatusInquiryRequest;
import com.pay10.pg.core.camspay.util.CamsPayHasher;
import com.pay10.pg.core.camspay.util.Constants;

import bsh.This;

@Service
public class CamsPayStatusEnquiryProcessor {

	@Autowired
	@Qualifier("camsPayTransactionConverter")
	private TransactionConverter converter;

	private static final ObjectMapper mapper = new ObjectMapper();

	private static Logger logger = LoggerFactory.getLogger(This.class.getName());

	public void enquiryProcessor(Fields fields) {
		String request = statusEnquiryRequest(fields);
		String response = "";
		try {
			String apiKey = fields.get(FieldType.ADF9.getName());
			response = getResponse(request, apiKey, fields.get(FieldType.ADF6.getName()),
					fields.get(FieldType.ADF7.getName()));
			updateFields(fields, response);
		} catch (SystemException exception) {
			logger.error("enquiryProcessor:: failed.", exception);
		} catch (Exception e) {
			logger.error("enquiryProcessor:: failed.", e);
		}

	}

	public String statusEnquiryRequest(Fields fields) {
		String pgRefNo = fields.get(FieldType.PG_REF_NUM.getName());
		try {
			CamsPayStatusInquiryRequest request = new CamsPayStatusInquiryRequest();
			request.setMerchantid(fields.get(FieldType.MERCHANT_ID.getName()));
			request.setRequeryKey(fields.get(FieldType.ADF10.getName()));
			request.setSubbillerid(fields.get(FieldType.ADF8.getName()));
			request.setTrxnno(pgRefNo);
			String encRequest = CamsPayHasher.encryptMessage(mapper.writeValueAsString(request),
					fields.get(FieldType.ADF6.getName()), fields.get(FieldType.ADF7.getName()));
			logger.info("dual verification for camspay request :: created. pgRefNo={}", pgRefNo);
			return encRequest;
		} catch (Exception e) {
			logger.error("statusEnquiryRequest:: failed. pgRefNo={}", pgRefNo, e);
			return null;
		}
	}

	public static String getResponse(String requestStr, String apiKey, String encKey, String iv)
			throws SystemException {

		if (StringUtils.isBlank(requestStr)) {
			logger.info("getResponse:: request empty.");
			return null;
		}
		Fields fields = new Fields();
		fields.put(FieldType.TXNTYPE.getName(), "STATUS");
		try {
			String hostUrl = PropertiesManager.propertiesMap.get(Constants.CAMSPAY_STATUS_ENQ_URL);
			logger.info("getResponse:: txnType={}, orderId={}, request={}", fields.get(FieldType.TXNTYPE.getName()),
					fields.get(FieldType.ORDER_ID.getName()), requestStr);

			RestTemplate template = new RestTemplate();
			HttpHeaders headers = new HttpHeaders();
			headers.add("content-type", "application/json");
			headers.add("apikey", apiKey);
			JSONObject json = new JSONObject();
			json.put("req", requestStr);
			logger.info("getResponse:: apiKey={}, request={}, hostUrl={}", apiKey, requestStr, hostUrl);
			HttpEntity<String> request = new HttpEntity<String>(json.toString(), headers);
			ResponseEntity<String> resultAsJson = template.exchange(hostUrl, HttpMethod.POST, request, String.class);
			MapType type = mapper.getTypeFactory().constructMapType(Map.class, String.class, String.class);
			Map<String, String> resMap = mapper.readValue(resultAsJson.getBody(), type);
			return CamsPayHasher.decryptMessage(resMap.getOrDefault("res", ""), encKey, iv);
		} catch (Exception e) {
			logger.error("getResponse:: failed.", e);
			return null;
		}
	}

	public void updateFields(Fields fields, String jsonResponse) {
		Transaction transaction = new Transaction();
		transaction = toTransactionForInquiry(jsonResponse, fields.get(FieldType.INTERNAL_ORIG_TXN_TYPE.getName()));
		String status = null;
		ErrorType errorType = null;
		String pgTxnMsg = null;
		if (fields.get(FieldType.INTERNAL_ORIG_TXN_TYPE.getName()).equalsIgnoreCase(TransactionType.SALE.getCode())) {
			if ((StringUtils.isNotBlank(transaction.getResponseCode()))
					&& ((transaction.getResponseCode()).equalsIgnoreCase(CamsPayResultType.RC111.getBankCode()))) {
				status = StatusType.CAPTURED.getName();
				errorType = ErrorType.SUCCESS;
				pgTxnMsg = StringUtils.isNotBlank(transaction.getResponseMsg()) ? transaction.getResponseMsg()
						: ErrorType.SUCCESS.getResponseMessage();
			} else {
				if ((StringUtils.isNotBlank(transaction.getResponseCode()))) {
					String respCode = StringUtils.isNotBlank(transaction.getResponseCode())
							? transaction.getResponseCode()
							: null;
					CamsPayResultType resultInstance = CamsPayResultType.getInstanceFromName(respCode);
					if (resultInstance != null) {
						errorType = ErrorType.getInstanceFromCode(resultInstance.getBankCode());
						status = resultInstance.getStatus();
						pgTxnMsg = StringUtils.isNotBlank(transaction.getResponseMsg()) ? transaction.getResponseMsg()
								: resultInstance.getMessage();
					} else {
						status = StatusType.FAILED_AT_ACQUIRER.getName();
						errorType = ErrorType.getInstanceFromCode(Constants.ERROR022);
						pgTxnMsg = StringUtils.isNotBlank(transaction.getResponseMsg()) ? transaction.getResponseMsg()
								: ErrorType.FAILED.getResponseMessage();
					}
				}
			}
			fields.put(FieldType.STATUS.getName(), status);
			fields.put(FieldType.RESPONSE_MESSAGE.getName(), errorType.getResponseMessage());
			fields.put(FieldType.RESPONSE_CODE.getName(), errorType.getResponseCode());
			fields.put(FieldType.RRN.getName(), transaction.getBankRefNum());
			fields.put(FieldType.ACQ_ID.getName(), transaction.getTxnId());
			//fields.put(FieldType.ACQ_ID.getName(), AcquirerType.CAMSPAY.getCode());
			fields.put(FieldType.PG_RESP_CODE.getName(), transaction.getResponseCode());
			String txnStatus = StringUtils.isNotBlank(transaction.getStatus()) ? transaction.getStatus()
					: errorType.getResponseCode();
			fields.put(FieldType.PG_TXN_STATUS.getName(), txnStatus);
			fields.put(FieldType.PG_TXN_MESSAGE.getName(), pgTxnMsg);
		}
	}

	public Transaction toTransaction(String jsonResponse, String txnType) {
		Transaction transaction = new Transaction();
		if (StringUtils.isBlank(jsonResponse)) {
			logger.info("toTransaction:: empty response from acquirer.");
			return transaction;
		}
		JSONObject respObj = new JSONObject(jsonResponse);
		logger.info("toTransaction:: respObj={}", respObj);
		transaction.setResponseCode(respObj.getString("resc"));
		transaction.setResponseMsg(respObj.getString("msg"));
		transaction.setAmount(respObj.getString("amount"));
		transaction.setTxnId(respObj.getString("trxnid"));
		transaction.setBankRefNum(respObj.getString("camspayrefno"));
		return transaction;

	}

	public Transaction toTransactionForInquiry(String jsonResponse, String txnType) {
		Transaction transaction = new Transaction();
		if (StringUtils.isBlank(jsonResponse)) {
			logger.info("toTransactionForInquiry:: empty response from acquirer.");
			return transaction;
		}
		JSONObject respObj = new JSONObject(jsonResponse);
		logger.info("toTransactionForInquiry:: respObj={}", respObj);
		JSONObject respObjData = respObj.getJSONObject("status");
		transaction.setResponseCode(respObjData.getString("errorcode"));
		transaction.setResponseMsg(respObjData.getString("message"));
		JSONObject txnData = respObjData.getJSONArray("data").getJSONObject(0);
		transaction.setAmount(txnData.getString("amount"));
		transaction.setTxnId(txnData.getString("trxnno"));
		transaction.setBankRefNum(txnData.getString("camspayrefno"));
		transaction.setStatus(txnData.getString("trxnstatus"));
		return transaction;

	}

}
