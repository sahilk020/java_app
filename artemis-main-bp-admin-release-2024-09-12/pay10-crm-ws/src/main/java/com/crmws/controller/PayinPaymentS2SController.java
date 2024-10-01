package com.crmws.controller;

import java.util.Date;
import java.util.Map;

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

import com.crmws.dto.PayinPaymentS2SRequest;
import com.crmws.dto.PayinPaymentS2SResponse;
import com.crmws.dto.ResponseEnvelope;
import com.crmws.service.PayinPaymentS2SService;

@RestController
@RequestMapping("/api/v1/payin/s2s/payment")
public class PayinPaymentS2SController {
	private static Logger logger = LoggerFactory.getLogger(PayinPaymentS2SController.class.getName());

	@Autowired
	PayinPaymentS2SService payinPaymentS2SService;
	
	@PostMapping(produces = { MediaType.APPLICATION_JSON_VALUE })
	public Map<String, String> payinS2SPayment(@Validated @RequestBody PayinPaymentS2SRequest request) {
		Map<String, String> response = null;
		logger.info("Start payin payment redirect url generation -----------------------" + new Date());
		response = payinPaymentS2SService.createPaymentLink(request);
		logger.info("Completed payin payment redirect url generation -----------------------" + new Date());
		return response;

	}
}
