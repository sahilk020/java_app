package com.crmws.controller;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.crmws.dto.MerchantRequest;
import com.crmws.dto.MerchantResponse;
import com.crmws.dto.ResponseEnvelope;
import com.crmws.service.IntentPaymentLinkService;

@RestController
@RequestMapping("/api/v1/intentPaymentLink")
public class IntentPaymentController {
	private static Logger logger = LoggerFactory.getLogger(IntentPaymentController.class.getName());

	@Autowired
	IntentPaymentLinkService intendPaymentLinkService;
	
	@PostMapping(produces = {MediaType.APPLICATION_JSON_VALUE})
	public ResponseEntity<ResponseEnvelope<MerchantResponse>> payment(@Validated @RequestBody MerchantRequest request) {
		
			logger.info("------------ Start Intent Payment Link Generation -----------------------"+new Date());
			ResponseEnvelope<MerchantResponse> response = intendPaymentLinkService.createPaymentLink(request);
			logger.info("------------ Completed Intent Payment Link Generation -----------------------"+new Date());
	        return new ResponseEntity<>(response, response.getHttpStatus());
	  
	}
}
