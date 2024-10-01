package com.pay10.commons.api;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.json.JSONObject;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.MapType;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.PropertiesManager;
import org.apache.commons.lang3.StringUtils;

@Service("ShortUrlProvider")
public class ShortUrlProvider {
	private static Logger logger = LoggerFactory.getLogger(SmsControllerServiceProvider.class.getName());
	private String shortURL;
	public String responseBody;
	@Deprecated
	public String ShortUrl_old(String url){
		Map<String, String> resMap = new HashMap<String, String>();
		try {
			String serviceUrl = PropertiesManager.propertiesMap.get("ShortUrlServiceInvoice");
			JSONObject json = new JSONObject();
			json.put("long_url", url);
			
			CloseableHttpClient httpClient = HttpClientBuilder.create().build();
			HttpPost request = new HttpPost(serviceUrl);
			StringEntity params = new StringEntity(json.toString());
			params.getContent();
			request.addHeader("Content-type", "application/json");
			request.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + PropertiesManager.propertiesMap.get("BitlyAccessToken"));
			request.setEntity(params);
			CloseableHttpResponse  resp = httpClient.execute(request);
			responseBody = EntityUtils.toString(resp.getEntity());
			final ObjectMapper mapper = new ObjectMapper();
			final MapType type = mapper.getTypeFactory().constructMapType(Map.class, String.class, Object.class);
			resMap = mapper.readValue(responseBody, type);	
			shortURL = resMap.get("link");
			 
		}catch (Exception exception) {
			logger.error("error"+exception);
		}
		
		return shortURL;
	}
	private String invoiceNo;
	private String aliasName;
	
	
	public String getAliasName() {
		return aliasName;
	}


	public void setAliasName(String aliasName) {
		this.aliasName = aliasName;
	}


	public String ShortUrl(String url){
		Map<String, String> resMap = new HashMap<String, String>();
		try {
			String serviceUrl = StringUtils.join(PropertiesManager.propertiesMap.get("ShortUrlServiceInvoice"), PropertiesManager.propertiesMap.get("api_token"));
			String domainName = PropertiesManager.propertiesMap.get("TinyURLServiceDomain");
			logger.info("domain name...={}",domainName);
			JSONObject json = new JSONObject();
			json.put("url", url);
			//json.put("domain", "tiny.one");
			json.put("domain", domainName);
			
			if(aliasName==null && aliasName=="") {
				aliasName=Math.random()+"";
				}
			
			json.put("alias", aliasName);
			CloseableHttpClient httpClient = HttpClientBuilder.create().build();
			HttpPost request = new HttpPost(serviceUrl);
			StringEntity params = new StringEntity(json.toString());
			params.getContent();
			request.addHeader("Content-type", "application/json");
			request.setEntity(params);
			CloseableHttpResponse  resp = httpClient.execute(request);
			responseBody = EntityUtils.toString(resp.getEntity());
			JSONObject jo=new JSONObject(responseBody);
			if(jo.get("data")!=null) {
				
				JSONObject joData=new JSONObject(jo.get("data").toString());
				shortURL =joData.get("tiny_url").toString();
				logger.info("ShortUrl:: generated. shortUrl={}",shortURL);
			}
			
			
			 
		}catch (Exception exception) {
			logger.error("error"+exception);
		}
		
		return shortURL;
	}
}
