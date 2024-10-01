package com.pay10.webhook.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class HttpService {

    @Autowired
    ObjectMapper objectMapper;

    @Qualifier("webhook-rest-template")
    @Autowired
    private RestTemplate restTemplate;

    public <I, O> ResponseEntity<O> postJSON(String url, I payLoad, Class<O> responseType) throws Exception {
        String payLoadJSON = objectMapper.writeValueAsString(payLoad);
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set(HttpHeaders.USER_AGENT, "BPGate");
        HttpEntity<String> entity = new HttpEntity<>(payLoadJSON, headers);
        ResponseEntity<O> resp = restTemplate.postForEntity(url, entity, responseType);
        return resp;
    }

    public <String, O> ResponseEntity<O> postStringJSON(String url, String payLoad, Class<O> responseType) throws Exception {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(payLoad, headers);
        ResponseEntity<O> resp = restTemplate.postForEntity((java.lang.String) url, entity, responseType);
        return resp;
    }

    public <I, O> ResponseEntity<O> postJSON(String url, I payLoad, HttpHeaders headers, Class<O> responseType) throws Exception {
        String payLoadJSON = objectMapper.writeValueAsString(payLoad);
        RestTemplate restTemplate = new RestTemplate();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(payLoadJSON, headers);
        ResponseEntity<O> resp = restTemplate.postForEntity(url, entity, responseType);
        return resp;
    }


}
