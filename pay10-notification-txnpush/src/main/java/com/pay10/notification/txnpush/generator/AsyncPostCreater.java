package com.pay10.notification.txnpush.generator;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.HttpResponse;
import org.apache.http.client.fluent.Async;
import org.apache.http.client.fluent.Content;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.slf4j.Logger; import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.MapType;
import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.exception.SystemException;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.PropertiesManager;
import com.pay10.commons.util.threadpool.ThreadPoolProvider;


/**
 * @author Puneet
 *
 */
public class AsyncPostCreater {
	private final Logger logger = LoggerFactory.getLogger(AsyncPostCreater.class.getName());

	public void sendPost(Fields fields, String requestUrl) {
		sendPost(fields.getFields(), requestUrl);
	}

	public void sendPost(Map<String, String> requestMap, String requestUrl) {
		try {
			Async async = Async.newInstance().use(ThreadPoolProvider.getExecutorService());
			URIBuilder builder;

			builder = new URIBuilder(requestUrl);
			for (Entry<String, String> entry : requestMap.entrySet()) {
				builder.addParameter(entry.getKey(), entry.getValue());
			}

			URI requestURL = null;
			requestURL = builder.build();
			final Request request = Request.Post(requestURL);
			request.socketTimeout(3000);
			async.execute(request, new FutureCallback<Content>() {
				@Override
				public void failed(final Exception exception) {
					logger.error("Error sending async post: " + exception);
				}

				@Override
				public void completed(final Content content) {
					logger.info("Async request completed: " + request);
				}

				@Override
				public void cancelled() {
				}
			});
		} catch (Exception exception) {
			logger.error("Error while preparing async post: " + exception);
		}
	}
	
	public HttpResponse notificationApi(Fields fields, String url) throws SystemException {
		String responseBody = "";
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
			/*
			 * responseBody = EntityUtils.toString(resp.getEntity()); final ObjectMapper
			 * mapper = new ObjectMapper(); final MapType type =
			 * mapper.getTypeFactory().constructMapType(Map.class, String.class,
			 * Object.class); resMap = mapper.readValue(responseBody, type);
			 */
			return resp;
		} catch (Exception exception) {
			logger.error("exception is " + exception);
			throw new SystemException(ErrorType.INTERNAL_SYSTEM_ERROR, "Error communicating to PG WS");
		}
		//return resMap;
	}
}
