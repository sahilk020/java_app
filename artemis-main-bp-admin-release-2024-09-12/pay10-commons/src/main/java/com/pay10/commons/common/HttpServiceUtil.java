package com.pay10.commons.common;

import java.net.URI;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;


@Component
public class HttpServiceUtil {

	@Autowired
	@Qualifier("common-rest-template")
	private RestTemplate restTemplate;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	private static Logger logger = LoggerFactory.getLogger(HttpServiceUtil.class.getName());
	
	public <I, O> O get(URI uri, I body, ParameterizedTypeReference<O> responseType){
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		O o = restTemplate.exchange(uri, HttpMethod.GET, getHttpEntity(body,headers), responseType).getBody();
		return o;
	
	}
	
	public <I, O> O post(String url, I body, ParameterizedTypeReference<O> responseType){
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		O o = restTemplate.exchange(url,HttpMethod.POST, getHttpEntity(body,headers), responseType).getBody();
		return o;
		
	}
	
	public <I, O> O postUrlEncoded(String url, MultiValueMap<String, String> body, ParameterizedTypeReference<O> responseType){
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(body,
				headers);
		ResponseEntity<O> response = restTemplate.exchange(url,HttpMethod.POST,request, responseType);
		logger.info("Response Recieved "+response.getBody());
		
		return response.getBody();
		
	}
	
	/*
	 * public <I, O> O postUrlEncoded(String url, I body,
	 * ParameterizedTypeReference<O> responseType){ HttpHeaders headers = new
	 * HttpHeaders(); headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
	 * logger.info("Payu url : "+url); logger.info("Payu url : "+url); HttpEntity<I>
	 * entity = getHttpEntity(body,headers); logger.info("Enityt obj : "+entity);
	 * 
	 * ResponseEntity<O> response = restTemplate.exchange(url,HttpMethod.POST,
	 * entity, responseType); logger.info("Response Recieved "+response.toString());
	 * return response.getBody();
	 * 
	 * }
	 */
	
	
	public <I, O> O postUrlEncoded(String url, Map<String, Object> body, ParameterizedTypeReference<O> responseType){
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		logger.info("Payu url : "+url);
		logger.info("Payu url : "+url);
		HttpEntity<I> entity = getHttpEntity(body,headers);
		logger.info("Enityt obj : "+entity);
		
		ResponseEntity<O> response = restTemplate.exchange(url,HttpMethod.POST, entity, responseType);
		logger.info("Response Recieved "+response.toString());
		return response.getBody();
		
	}
	
	public <I, O> O put(String url, String username,I body, ParameterizedTypeReference<O> responseType){
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		//createHeader(username, password, headers);
		headers.add("username", username);
		O o = restTemplate.exchange(url,HttpMethod.PUT,getHttpEntity(body,headers),responseType).getBody();
		return o;
	}

	public <I, O> O postwithheaders(String url, I body, Class<O> responseType,HttpHeaders headers){

		O o = restTemplate.postForObject(url, getHttpEntity(body,headers), responseType);
		return o;
	}


	private void createHeader(String username, String password, HttpHeaders headers) {
        String auth = username + ":" + password;
        byte[] authentication = auth.getBytes();
        byte[] base64Authentication = Base64.encodeBase64(authentication);
        String baseCredential = new String(base64Authentication);
        headers.add("Authorization", "Basic " + baseCredential);
    }


    private <I> HttpEntity getHttpEntity(I body, HttpHeaders headers) {
		HttpEntity<I> entity = null;
		try {
			entity = new HttpEntity<>(body, headers);
		}catch (Exception e){
			throw new RuntimeException(e);
		}
		return entity;
	}

	
}
