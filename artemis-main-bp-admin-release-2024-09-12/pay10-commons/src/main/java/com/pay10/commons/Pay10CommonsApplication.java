package com.pay10.commons;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pay10.commons.util.ObjectToUrlEncodedConverter;

@SpringBootApplication
public class Pay10CommonsApplication {

	public static void main(String[] args) {
		SpringApplication.run(Pay10CommonsApplication.class, args);
	}
	
	@Autowired
	ObjectMapper mapper;
	
	@Bean("common-rest-template")
	public RestTemplate restTemplate() {
		final RestTemplate restTemplate = new RestTemplate();
		List<HttpMessageConverter<?>> messageConverters = new ArrayList<>();
		MappingJackson2HttpMessageConverter converter = new
		MappingJackson2HttpMessageConverter();
		converter.setSupportedMediaTypes(Collections.singletonList(MediaType.ALL));
		messageConverters.add(converter);
		messageConverters.add(new ObjectToUrlEncodedConverter(mapper));
		restTemplate.setMessageConverters(messageConverters);
		 
		return restTemplate;
	}
	
	
	public MappingJackson2HttpMessageConverter getMappingJackson2HttpMessageConverter() {
	    MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter = new MappingJackson2HttpMessageConverter();
	    mappingJackson2HttpMessageConverter.setSupportedMediaTypes(Collections.singletonList(MediaType.APPLICATION_FORM_URLENCODED));
	    return mappingJackson2HttpMessageConverter;
	}

}
