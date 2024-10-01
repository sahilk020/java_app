package com.crmws.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

@Component
@Slf4j
public class CityService {

	@Autowired
	private ObjectMapper mapper;

	@SuppressWarnings("unchecked")
	public List<String> getAllCitiesByState(String countryName, String stateName) {
		OkHttpClient client1 = new OkHttpClient().newBuilder().build();
		MediaType mediaType = MediaType.parse("application/json");
		RequestBody cityReq = RequestBody.create(mediaType,
				"{\r\n    \"country\": \"" + countryName + "\",\r\n\"state\":\"" + stateName + "\"}");
		Request cityRequest = new Request.Builder().url("https://countriesnow.space/api/v0.1/countries/state/cities")
				.method("POST", cityReq).addHeader("Content-Type", "application/json").build();
		List<String> cityList = new ArrayList<>();
		try {
			try (Response response = client1.newCall(cityRequest).execute()) {
				String responseBody = response.body().string();
				Map<String, Object> cityRes = mapper.readValue(responseBody, HashMap.class);
				if (cityRes != null) {
					cityList = (List<String>) cityRes.get("data");
				}
			}
		} catch (Exception ex) {
			log.error("getAllCitiesByState:: failed, country={}, state={}", countryName, stateName, ex);
		}
		return cityList;
	}
}
