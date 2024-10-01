package com.pay10.commons.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.MapType;
import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.exception.SystemException;
import com.pay10.commons.user.Token;
import com.pay10.commons.util.ConfigurationConstants;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.PropertiesManager;

@Service("transactionControllerServiceProvider")
public class TransactionControllerServiceProvider {

	@Autowired
	PropertiesManager propertiesManager;

	private static Logger logger = LoggerFactory.getLogger(TransactionControllerServiceProvider.class.getName());

	public Map<String,String> decrypt(String payId, String encData) throws SystemException {

		String responseBody = "";
		String serviceUrl = ConfigurationConstants.CRYPTO_DECRYPTION_SERVICE_URL.getValue();
		Map<String, String> resMap = new HashMap<String, String>();
		try {
			JSONObject json = new JSONObject();
			json.put(FieldType.PAY_ID.getName(), payId);
			json.put(FieldType.ENCDATA.getName(), encData);
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
			throw new SystemException(ErrorType.INTERNAL_SYSTEM_ERROR, "Error communicating to crypto module");
		}
		return resMap;
	}
	
	public Map<String,String> encrypt(String payId, String encData) throws SystemException {

		String responseBody = "";
		String serviceUrl = ConfigurationConstants.CRYPTO_ENCRYPTION_SERVICE_URL.getValue();
		logger.info("CryptoEncryptionServiceUrl"+serviceUrl);
		Map<String, String> resMap = new HashMap<String, String>();
		try {
			JSONObject json = new JSONObject();
			json.put(FieldType.PAY_ID.getName(), payId);
			json.put(FieldType.ENCDATA.getName(), encData);
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
			throw new SystemException(ErrorType.INTERNAL_SYSTEM_ERROR, "Error communicating to crypto module");
		}
		return resMap;
	}
	public Map<String, Token> getAll(Fields fields) {
		String responseBody = "";
		String serviceUrl = PropertiesManager.propertiesMap.get("TransactionWSGetAllTokenURL");
		Map<String, Token> resMap = new HashMap<String, Token>();
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

		} catch (Exception e) {
			logger.error("exceptoin is " + e);
		}
		return resMap;
	}
	
	public Map<String, String> migsTransact(Fields fields, String url) throws SystemException {
		String responseBody = "";
		String serviceUrl = PropertiesManager.propertiesMap.get(url);
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

	public Map<String, String> transact(Fields fields, String url) throws SystemException {
		String responseBody = "";
		String serviceUrl = PropertiesManager.propertiesMap.get(url);
		logger.info("serviceUrl @@@@@@@@@@@@@@@"+serviceUrl);
		Map<String, String> resMap = new HashMap<String, String>();
		try {

			JSONObject json = new JSONObject();
			List<String> fieldTypeList = new ArrayList<String>(fields.getFields().keySet());
			for (String fieldType : fieldTypeList) {
				json.put(fieldType, fields.get(fieldType));
			}
			logger.info("Payment procees json request"+json);
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
			exception.printStackTrace();
			throw new SystemException(ErrorType.INTERNAL_SYSTEM_ERROR, "Error communicating to PG WS");
		}
		return resMap;
	}
	
	public Map<String, String> payoutTransact(Fields fields, String url) throws SystemException {
		String responseBody = "";
		String serviceUrl = PropertiesManager.propertiesMap.get(url);
		logger.info("Payout serviceUrl @@@@@@@@@@@@@@@"+serviceUrl);
		Map<String, String> resMap = new HashMap<String, String>();
		try {

			JSONObject json = new JSONObject();
			List<String> fieldTypeList = new ArrayList<String>(fields.getFields().keySet());
			for (String fieldType : fieldTypeList) {
				json.put(fieldType, fields.get(fieldType));
			}
			logger.info("Payment procees json request"+json);
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
			exception.printStackTrace();
			throw new SystemException(ErrorType.INTERNAL_SYSTEM_ERROR, "Error communicating to PG WS");
		}
		return resMap;
	}
	
	public Map<String, String> nodalSettlementTransact(Fields fields) throws SystemException {
		String responseBody = "";
		String serviceUrl = PropertiesManager.propertiesMap.get("TransactionWSNodalSettlementProcessor");
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
	
	public int uptimeService() {
		String serviceUrl = PropertiesManager.propertiesMap.get(Constants.PGWS_UPTIME_URL.getValue());
		String requestToken = PropertiesManager.propertiesMap.get(Constants.UPTIME_TOKEN.getValue());
		try {

			CloseableHttpClient httpClient = HttpClientBuilder.create().build();
			HttpGet request = new HttpGet(serviceUrl);
			request.addHeader("Authorization", requestToken);
			HttpResponse resp = httpClient.execute(request);
			int responseCode = resp.getStatusLine().getStatusCode();
			logger.info("Response Code : " + resp.getStatusLine().getStatusCode());
			return responseCode;
		} catch (Exception exception) {
			logger.error("Error communicating to PG WS" + exception);
		}
		return 500;
	}
	
	
	public Map<String, String> upiEnquiry(Fields fields, String url) throws SystemException {
		String responseBody = "";
				
		Map<String, String> resMap = new HashMap<String, String>();
		try {

			JSONObject json = new JSONObject();
			List<String> fieldTypeList = new ArrayList<String>(fields.getFields().keySet());
			for (String fieldType : fieldTypeList) {
				json.put(fieldType, fields.get(fieldType));
			}
			CloseableHttpClient httpClient = HttpClientBuilder.create().build();
			HttpPost request = new HttpPost(url);
			StringEntity params = new StringEntity(json.toString());
			request.addHeader("content-type", "application/json");
			request.setEntity(params);
			HttpResponse resp = httpClient.execute(request);
			responseBody = EntityUtils.toString(resp.getEntity());
			final ObjectMapper mapper = new ObjectMapper();
			final MapType type = mapper.getTypeFactory().constructMapType(Map.class, String.class, Object.class);
			resMap = mapper.readValue(responseBody, type);
		} catch (Exception exception) {
			logger.error("exception in getting upi status " + exception);
			throw new SystemException(ErrorType.INTERNAL_SYSTEM_ERROR, "Error communicating to PG WS");
		}
		return resMap;
	}
}
