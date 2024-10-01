package com.pay10.bindb.service.Impl;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.pay10.bindb.service.BinRangeDTO;
import com.pay10.bindb.service.BinRangeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.http.*;

import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;
import java.io.IOException;

// Added by RR Date 26-Nov-2021
@Service
@Scope(value = "session", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class BinRangeServiceImpl implements BinRangeService {
	private final static String USER_AGENT = "Mozilla/5.0";
	private static Logger logger = LoggerFactory.getLogger(BinRangeServiceImpl.class.getName());

	@Value("${binrange.api.user}")
	private String user;
	@Value("${binrange.api.key}")
	private String apiKey;

	public BinRangeDTO getCommunicator(String requestUrl,String cardBin) throws IOException {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

		logger.info("Bin Range API URL : {}",requestUrl);
		MultiValueMap<String, String> map= new LinkedMultiValueMap<>();
		map.add("user-id",user);
		map.add("api-key",apiKey);
		map.add("bin-number",cardBin);

		HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(map, headers);

		RestTemplate restTemplate = new RestTemplate();
		restTemplate.setErrorHandler(new DefaultResponseErrorHandler(){
			@Override
			public void handleError(ClientHttpResponse res) throws IOException {
				logger.info("ERROR :: " + res.getStatusText());
			}
		});
		ResponseEntity<String> response = restTemplate.exchange( requestUrl, HttpMethod.POST, request , String.class );
		//Added By RR
		logger.info("Netrunio Bin Range API Response :: {} ", response.getBody());

		Gson gson = new Gson();
		JsonObject json = gson.fromJson(response.getBody(), JsonObject.class);
		if(StringUtils.isEmpty(json.get("api-error"))) {
			BinRangeDTO dto = new BinRangeDTO();
			dto.setCardType(json.get("card-type").getAsString());
			dto.setCountry(json.get("country").getAsString());
			dto.setCountryCode(json.get("country-code").getAsString());
			dto.setCardBrand(json.get("card-brand").getAsString());
			dto.setCommercial(json.get("is-commercial").getAsBoolean());
			dto.setBinNumber(json.get("bin-number").getAsString());
			dto.setIssuer(json.get("issuer").getAsString());
			dto.setIssuerWebsite(json.get("issuer-website").getAsString());
			dto.setValid(json.get("valid").getAsBoolean());
			dto.setCardType(json.get("card-type").getAsString());
			dto.setPrepaid(json.get("is-prepaid").getAsBoolean());
			dto.setCardCategory(json.get("card-category").getAsString());
			dto.setCurrencyCode(json.get("currency-code").getAsString());

			return dto;
		}else{
			throw new RuntimeException(response.getBody());
		}

	}
/*
	public static void main(String[] args) {
		try {
			BinRangeServiceImpl o = new BinRangeServiceImpl();
			BinRangeDTO dto = o.getCommunicator("https://neutrinoapi.net/bin-lookup", "518159");
			System.out.println(dto.toString());
		}catch (Exception e){
			e.printStackTrace();
		}
	}*/

}
