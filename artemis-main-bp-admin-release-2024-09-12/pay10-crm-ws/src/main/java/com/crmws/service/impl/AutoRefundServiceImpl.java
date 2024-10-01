package com.crmws.service.impl;

import static com.pay10.commons.common.EncryptDecrypt.getRandomDEKKey;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.type.MapType;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.crmws.dao.AutoRefundRepository;
import com.crmws.dto.AutorefundDto;
import com.crmws.service.AutoRefundservice;
import com.pay10.commons.api.Hasher;
import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.exception.SystemException;
import com.pay10.commons.util.Amount;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.PropertiesManager;
import com.pay10.commons.util.TransactionManager;
import com.pay10.commons.util.TransactionType;

@Service
public class AutoRefundServiceImpl implements AutoRefundservice {
	private static Logger logger = LoggerFactory.getLogger(AutoRefundServiceImpl.class.getName());

	@Autowired
	AutoRefundRepository autoRefundRepository;

	public AutorefundDto GetPayidByPgrefno(String PgRefno) {
		return autoRefundRepository.GetMerchantPayidByPgRefNo(PgRefno);
	}

	@Override
	public boolean getrefundSatusByPayid(AutorefundDto payId) {
		String merchantId = payId.getPayid();
		String createDate = payId.getCreateDate();
		return autoRefundRepository.getRefundStatus(merchantId, createDate);
	}

	@Override
	public String refundintiated(String pgRef) {

		List<Map> refundData = autoRefundRepository.refundintiated(pgRef);
		refundData.stream().forEach((n) -> {
			
			Fields fields = new Fields();
			fields.put(FieldType.ORDER_ID.getName(), n.get("ORDER_ID").toString());
			fields.put(FieldType.REFUND_FLAG.getName(), "R");
			fields.put(FieldType.AMOUNT.getName(),
					(Amount.formatAmount(n.get("AMOUNT").toString(), n.get("CURRENCY_CODE").toString())));
			fields.put(FieldType.PG_REF_NUM.getName(), n.get("PG_REF_NUM").toString());
			fields.put(FieldType.REFUND_ORDER_ID.getName(), getId());
			fields.put(FieldType.CURRENCY_CODE.getName(), n.get("CURRENCY_CODE").toString());
			fields.put(FieldType.TXNTYPE.getName(), TransactionType.REFUND.getName());
			fields.put(FieldType.PAY_ID.getName(), n.get("PAY_ID").toString());
			logger.info("Request for auto refund in pgws   : " + fields.getFieldsAsString());
			// String refundurl = "http://localhost:8080/pgws/internalRefund";
			String refundurl = PropertiesManager.propertiesMap.get("RefundURL");
			logger.info("url for refund intiated={}", refundurl);
			Map<String, String> response = null;
			try {
				fields.put(FieldType.HASH.getName(), Hasher.getHash(TransactionManager.getNewTransactionId()));
				response = autoRefundCall(fields, refundurl);
			} catch (SystemException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			logger.info("response for pgws  " + response);
		});
		return pgRef;
	}

	public static Map<String, String> autoRefundCall(Fields fields, String url) throws SystemException {
		String responseBody = "";
		// String serviceUrl = PropertiesManager.propertiesMap.get(url);
		String serviceUrl = url;
		Map<String, String> resMap = new HashMap<String, String>();
		try {

			JSONObject json = new JSONObject();
			List<String> fieldTypeList = new ArrayList<String>(fields.getFields().keySet());
			for (String fieldType : fieldTypeList) {
				json.put(fieldType, fields.get(fieldType));
			}
			CloseableHttpClient httpClient = HttpClientBuilder.create().build();
			HttpPost request = new HttpPost(serviceUrl);
			StringEntity params = new StringEntity(json.toString());
			request.addHeader("content-type", "application/json");
			request.setEntity(params);
			HttpResponse resp = httpClient.execute(request);
			responseBody = EntityUtils.toString(resp.getEntity());
			final ObjectMapper mapper = new ObjectMapper();
			final MapType type = mapper.getTypeFactory().constructMapType(Map.class, String.class, Object.class);
			resMap = mapper.readValue(responseBody, type);
		} catch (Exception exception) {
			logger.error("exception is " + exception);
			throw new SystemException(ErrorType.INTERNAL_SYSTEM_ERROR, "Error communicating to PG WS");
		}
		return resMap;
	}
	private static String getId() {
		return Long.toString(System.currentTimeMillis());
	}
}
