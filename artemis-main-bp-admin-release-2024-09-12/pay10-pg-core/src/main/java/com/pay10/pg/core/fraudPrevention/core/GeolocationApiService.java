package com.pay10.pg.core.fraudPrevention.core;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pay10.commons.dao.NeutrinoDetailsDao;
import com.pay10.commons.entity.NeutrinoDetails;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * This component provide the details of geo location details by IP address.
 * 
 * it's contains API to retrieve Country, State and city by IP address.
 * 
 * it is using neutrino API to retrieve details. also you can refer the
 * {@link https://www.neutrinoapi.com/} to find the details related to neutrino
 * API.
 * 
 * 
 * @author Gajera Jaykumar
 *
 */
@Component
public class GeolocationApiService {

	private static final Logger logger = LoggerFactory.getLogger(GeolocationApiService.class.getName());
	private String userId;
	private String apiKey;
	private String apiUrl;

	@Autowired
	private NeutrinoDetailsDao neutrinoDetailsDao;

	private ObjectMapper mapper = new ObjectMapper();

	public String getCountryByIp(String ipAddress) {
		Map<String, Object> locationDetailsByIp = invokeNeutrinoAPI(ipAddress);
		return (String) locationDetailsByIp.get("country-code");
	}

	public String getStateByIp(String ipAddress) {
		Map<String, Object> locationDetailsByIp = invokeNeutrinoAPI(ipAddress);
		return (String) locationDetailsByIp.get("region-code");
	}

	public String getCityByIp(String ipAddress) {
		Map<String, Object> locationDetailsByIp = invokeNeutrinoAPI(ipAddress);
		return (String) locationDetailsByIp.get("city");
	}

	@SuppressWarnings("unchecked")
	private Map<String, Object> invokeNeutrinoAPI(String ipAddress) {
		Map<String, Object> neutrinoRes = new HashMap<>();
		try {
			if (StringUtils.isBlank(userId)) {
				setApiDetails();
			}
			String param = StringUtils.join("?ip=", ipAddress, "&api-key=", apiKey, "&user-id=", userId);
			String finalUrl = StringUtils.join(apiUrl, param);
			OkHttpClient client = new OkHttpClient().newBuilder().build();
			Request request = new Request.Builder().url(finalUrl).get().build();
			Response response = client.newCall(request).execute();
			String responseBody = response.body().string();
			neutrinoRes = mapper.readValue(responseBody, HashMap.class);
			logger.info("invokeNeutrinoAPI:: completed. ipAddress={}", ipAddress);
		} catch (Exception ex) {
			logger.error("invokeNeutrinoAPI:: failed. ipAddress={}", ipAddress, ex);
		}
		return neutrinoRes;
	}

	private void setApiDetails() {
		NeutrinoDetails details = neutrinoDetailsDao.find();
		this.userId = details.getUserId();
		this.apiKey = details.getApiKey();
		this.apiUrl = details.getApiUrl();
	}
}
