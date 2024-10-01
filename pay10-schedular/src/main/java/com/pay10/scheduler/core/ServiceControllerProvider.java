package com.pay10.scheduler.core;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
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

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.MapType;
import com.pay10.commons.dao.FieldsDao;
import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.exception.SystemException;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.PropertiesManager;
import com.pay10.commons.util.StatusType;
import com.pay10.commons.util.TransactionManager;

@Service("serviceControllerProvider")
public class ServiceControllerProvider {

	private static final Logger logger = LoggerFactory.getLogger(ServiceControllerProvider.class);

	private static final ObjectMapper mapper = new ObjectMapper();

	@Autowired
	private FieldsDao fieldsDao;

	public String makeStatusEnquiry(JSONObject jsonObject, String serviceUrl) throws SystemException {
		try {
			String responseBody = "";
			jsonObject.put("UPDATED_BY_SCHEDULER", "Y");
			HttpPost request = new HttpPost(serviceUrl);
			RequestConfig config = RequestConfig.custom().setConnectTimeout(180000).setConnectionRequestTimeout(180000)
					.setSocketTimeout(180000).build();
			request.setConfig(config);
			CloseableHttpClient httpClient = HttpClientBuilder.create().setDefaultRequestConfig(config).build();
			try {
				StringEntity params = new StringEntity(jsonObject.toString());
				request.addHeader("content-type", "application/json");
				request.setEntity(params);
				HttpResponse resp = httpClient.execute(request);
				HttpEntity response = resp.getEntity();
				responseBody = EntityUtils.toString(response);
				return responseBody.toString();
			} catch (Exception e) {
				logger.error("Expired " + e);
			}
		} catch (Exception exception) {
			logger.error("Error making status enquiry ", exception);
			throw new SystemException(ErrorType.INTERNAL_SYSTEM_ERROR, "Error making status enquiry");
		}
		return null;
	}

	public void bankStatusEnquiry(JSONObject data, String bankStatusEnquiryUrl) throws SystemException {

		try {
			String serviceUrl = bankStatusEnquiryUrl;

			data.put("UPDATED_BY_SCHEDULER", "Y");
			logger.info("Status Enquiry Service Url: " + serviceUrl);
			logger.info("Merchant data :" + data);

			HttpPost request = new HttpPost(serviceUrl);
			RequestConfig config = RequestConfig.custom().setConnectTimeout(3600000)
					.setConnectionRequestTimeout(3600000).setSocketTimeout(3600000).build();
			request.setConfig(config);

			CloseableHttpClient httpClient = HttpClientBuilder.create().setDefaultRequestConfig(config).build();

			try {
				StringEntity params = new StringEntity(data.toString());

				request.addHeader("content-type", "application/json");
				request.setEntity(params);
				HttpResponse resp = httpClient.execute(request);
				HttpEntity response = resp.getEntity();
				// JSONObject json = new JSONObject(EntityUtils.toString(response));
				logger.info("Status Enquiry Response : {}", EntityUtils.toString(response));
				// logger.info("Status Enquiry Response Status : {}",json.getString("STATUS"));
			} catch (Exception e) {
				logger.error("Status Enquiry Expired ", e);
			}

		} catch (Exception exception) {
			logger.error("Error communicating with Status Enquiry API ", exception);
			throw new SystemException(ErrorType.INTERNAL_SYSTEM_ERROR, "Error communicating with Status Enquiry API");
		}
	}

	public static String resellerseculdra(String data, String serviceUrl) throws SystemException {
		try {
			String data1 = "true";
			logger.info("Status Enquiry Service Url: " + serviceUrl);
			logger.info("Merchant data :" + data);

			HttpPost request = new HttpPost(serviceUrl);
			RequestConfig config = RequestConfig.custom().setConnectTimeout(3600000)
					.setConnectionRequestTimeout(3600000).setSocketTimeout(3600000).build();
			request.setConfig(config);

			CloseableHttpClient httpClient = HttpClientBuilder.create().setDefaultRequestConfig(config).build();

			try {
				StringEntity params = new StringEntity(data1);

				request.addHeader("content-type", "application/json");
				request.setEntity(params);
				HttpResponse resp = httpClient.execute(request);
				HttpEntity response = resp.getEntity();
				// JSONObject json = new JSONObject(EntityUtils.toString(response));
				logger.info("Reseller scheduler Response : {}", EntityUtils.toString(response));
				// logger.info("Status Enquiry Response Status : {}",json.getString("STATUS"));
			} catch (Exception e) {
				logger.error("Reseller scheduler", e);
			}

		} catch (Exception exception) {
			logger.error("Error communicating with resellerseculdra ", exception);
			throw new SystemException(ErrorType.INTERNAL_SYSTEM_ERROR,
					"Error communicating with Reseller scheduler API");
		}
		return serviceUrl;
	}

	public void bankPendingTxnStatusEnquiry(JSONObject data, String bankStatusEnquiryUrl) throws SystemException {

		try {
			String serviceUrl = bankStatusEnquiryUrl;

			data.put("UPDATED_BY_SCHEDULER", "Y");
			logger.info(" Pending Status Enquiry Service Url ={}", serviceUrl);
			logger.info("Pending transaction Merchant data ={}", data);

			HttpPost request = new HttpPost(serviceUrl);
			RequestConfig config = RequestConfig.custom().setConnectTimeout(3600000)
					.setConnectionRequestTimeout(3600000).setSocketTimeout(3600000).build();
			request.setConfig(config);

			CloseableHttpClient httpClient = HttpClientBuilder.create().setDefaultRequestConfig(config).build();

			try {
				StringEntity params = new StringEntity(data.toString());

				request.addHeader("content-type", "application/json");
				request.setEntity(params);
				HttpResponse resp = httpClient.execute(request);
				HttpEntity response = resp.getEntity();
				logger.info("Pending Status Enquiry Response : {}", EntityUtils.toString(response));
			} catch (Exception e) {
				logger.error("Pending Status Enquiry Expired ", e);
			}

		} catch (Exception exception) {
			logger.error("Error communicating with Pending Status Enquiry API ", exception);
			throw new SystemException(ErrorType.INTERNAL_SYSTEM_ERROR,
					"Error communicating with Pending Status Enquiry API");
		}
	}

	public void bankPendingTxnStatusEnquiryThirdTime(JSONObject data, String bankStatusEnquiryUrl, String oid)
			throws SystemException {

		try {

			String serviceUrl = bankStatusEnquiryUrl;
			String oId = oid;
			data.put("UPDATED_BY_SCHEDULER", "Y");
			logger.info(" Pending Status Enquiry Service Url ={}", serviceUrl);
			logger.info("Pending transaction Merchant data ={},oid={}", data, oId);

			HttpPost request = new HttpPost(serviceUrl);
			RequestConfig config = RequestConfig.custom().setConnectTimeout(3600000)
					.setConnectionRequestTimeout(3600000).setSocketTimeout(3600000).build();
			request.setConfig(config);

			CloseableHttpClient httpClient = HttpClientBuilder.create().setDefaultRequestConfig(config).build();

			try {

				logger.info("dataForpendingStatusEnquiryThirdTime...={}", data.toString());
				StringEntity params = new StringEntity(data.toString());
				request.addHeader("content-type", "application/json");
				request.setEntity(params);
				HttpResponse resp = httpClient.execute(request);
				String response = EntityUtils.toString(resp.getEntity());

				logger.info("Pending Status Enquiry Response : {}", response);

				JSONObject json = new JSONObject(response);
				String status = (String) json.get("STATUS");
				logger.info("status...={}", status);
				if (StringUtils.equalsAnyIgnoreCase(StatusType.SENT_TO_BANK.getName(), status)
						|| StringUtils.equalsAnyIgnoreCase(StatusType.PENDING.getName(), status)) {
					Map<String, String> dataa = mapper.readValue(response, new TypeReference<Map<String, String>>() {
					});
					Fields fields = new Fields(dataa);
					fields.put(FieldType.TXN_ID.getName(), TransactionManager.getNewTransactionId());
					fields.put(FieldType.OID.getName(), oId);
					if (!StringUtils.isBlank(fields.get(FieldType.TXNTYPE.getName()))
							&& !StringUtils.isBlank(fields.get(FieldType.ORIG_TXNTYPE.getName()))
							&& StringUtils.equalsAnyIgnoreCase(fields.get(FieldType.TXNTYPE.getName()), "STATUS")) {
						fields.put(FieldType.TXNTYPE.getName(), fields.get(FieldType.ORIG_TXNTYPE.getName()));
					}
					if (StringUtils.isBlank(fields.get(FieldType.SURCHARGE_FLAG.getName()))) {
						fields.put(FieldType.SURCHARGE_FLAG.getName(), "N");
					}
					fields.put(FieldType.STATUS.getName(), StatusType.FAILED.getName());
					fields.put(FieldType.RESPONSE_MESSAGE.getName(), "No Such Transaction found");
					fields.put(FieldType.RESPONSE_CODE.getName(), ErrorType.NO_SUCH_TRANSACTION.getCode());
					logger.info("inserting data..={}", fields.getFieldsAsString());
					try {
						fieldsDao.insertTransaction(fields);
					} catch (SystemException e) {
						logger.error("fetchPendingTxnData:: failed to save transaction. fields={}",
								fields.getFieldsAsString(), e);
					}
				}

			} catch (Exception e) {
				logger.error("Pending Status Enquiry Expired ", e);
			}

		} catch (Exception exception) {
			logger.error("Error communicating with Pending Status Enquiry API ", exception);
			throw new SystemException(ErrorType.INTERNAL_SYSTEM_ERROR,
					"Error communicating with Pending Status Enquiry API");
		}
	}

	public void bankRefundService(JSONObject data, String bankStatusEnquiryUrl) throws SystemException {

		try {
			String serviceUrl = bankStatusEnquiryUrl;

			logger.info("Refund Service Url: " + serviceUrl);
			logger.info("BankRefundService, Merchant data :" + data);

			HttpPost request = new HttpPost(serviceUrl);
			RequestConfig config = RequestConfig.custom().setConnectTimeout(3600000)
					.setConnectionRequestTimeout(3600000).setSocketTimeout(3600000).build();
			request.setConfig(config);

			CloseableHttpClient httpClient = HttpClientBuilder.create().setDefaultRequestConfig(config).build();

			try {
				StringEntity params = new StringEntity(data.toString());

				request.addHeader("content-type", "application/json");
				request.setEntity(params);
				HttpResponse resp = httpClient.execute(request);
				HttpEntity response = resp.getEntity();
				// JSONObject json = new JSONObject(EntityUtils.toString(response));
				logger.info("Refund Response : {}", EntityUtils.toString(response));
				// logger.info("Status Enquiry Response Status : {}",json.getString("STATUS"));
			} catch (Exception e) {
				logger.error("Refund Expired ", e);
			}

		} catch (Exception exception) {
			logger.error("Error communicating with Refund API ", exception);
			throw new SystemException(ErrorType.INTERNAL_SYSTEM_ERROR, "Error communicating with Refund API");
		}
	}

	public void chargebackBulkUpload(String bankStatusEnquiryUrl) throws SystemException {

		try {
			String serviceUrl = bankStatusEnquiryUrl;

			logger.info(" Pending Status Enquiry Service Url ={}", serviceUrl);

			HttpPost request = new HttpPost(serviceUrl);
			RequestConfig config = RequestConfig.custom().setConnectTimeout(3600000)
					.setConnectionRequestTimeout(3600000).setSocketTimeout(3600000).build();
			request.setConfig(config);

			CloseableHttpClient httpClient = HttpClientBuilder.create().setDefaultRequestConfig(config).build();
			try {
				request.addHeader("content-type", "application/json");
				HttpResponse resp = httpClient.execute(request);
				HttpEntity response = resp.getEntity();
				logger.info("chargebackBulkUpload : {}", EntityUtils.toString(response));
			} catch (Exception e) {
				logger.error("chargebackBulkUpload ", e);
			}
		} catch (Exception exception) {
			logger.error("chargebackBulkUpload", exception);
			throw new SystemException(ErrorType.INTERNAL_SYSTEM_ERROR,
					"Error communicating with Pending Status Enquiry API");
		}
	}

	public void bsesMail(String bankStatusEnquiryUrl) throws SystemException {

		try {
			String serviceUrl = bankStatusEnquiryUrl;

			logger.info(" Pending Status Enquiry Service Url ={}", serviceUrl);

			HttpPost request = new HttpPost(serviceUrl);
			RequestConfig config = RequestConfig.custom().setConnectTimeout(3600000)
					.setConnectionRequestTimeout(3600000).setSocketTimeout(3600000).build();
			request.setConfig(config);

			CloseableHttpClient httpClient = HttpClientBuilder.create().setDefaultRequestConfig(config).build();
			try {
				request.addHeader("content-type", "application/json");
				HttpResponse resp = httpClient.execute(request);
				HttpEntity response = resp.getEntity();
				logger.info("bsesMail : {}", EntityUtils.toString(response));
			} catch (Exception e) {
				logger.error("bsesMail ", e);
			}
		} catch (Exception exception) {
			logger.error("bsesMail", exception);
			throw new SystemException(ErrorType.INTERNAL_SYSTEM_ERROR,
					"Error communicating with Pending Status Enquiry API");
		}
	}

	public void chargebackAutoClose(String chargebackAutoCloseUrl) throws SystemException {

		try {
			String serviceUrl = chargebackAutoCloseUrl;

			logger.info(" Pending Status Enquiry Service Url ={}", serviceUrl);

			HttpGet request = new HttpGet(serviceUrl);
			RequestConfig config = RequestConfig.custom().setConnectTimeout(3600000)
					.setConnectionRequestTimeout(3600000).setSocketTimeout(3600000).build();
			request.setConfig(config);
			CloseableHttpClient httpClient = HttpClientBuilder.create().setDefaultRequestConfig(config).build();
			try {
				request.addHeader("content-type", "application/json");
				HttpResponse resp = httpClient.execute(request);
				HttpEntity response = resp.getEntity();
				logger.info("chargebackAutoClose : {}", EntityUtils.toString(response));
			} catch (Exception e) {
				logger.error("chargebackAutoClose ", e);
			}
		} catch (Exception exception) {
			logger.error("chargebackAutoClose", exception);
			throw new SystemException(ErrorType.INTERNAL_SYSTEM_ERROR,
					"Error communicating with Pending Status Enquiry API");
		}
	}


		public Map<String, String> getApiCall(Fields fields, String url) throws SystemException {
			String responseBody = "";
			logger.info("Payout serviceUrl "+url);
			Map<String, String> resMap = new HashMap<String, String>();
			try {

				JSONObject json = new JSONObject();
				List<String> fieldTypeList = new ArrayList<String>(fields.getFields().keySet());
				for (String fieldType : fieldTypeList) {
					json.put(fieldType, fields.get(fieldType));
				}
				logger.info("Payment procees json request"+json);
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
				logger.error("exception is " + exception);
				exception.printStackTrace();
				throw new SystemException(ErrorType.INTERNAL_SYSTEM_ERROR, "Error communicating to PG WS");
			}
			return resMap;
		
	}
}
