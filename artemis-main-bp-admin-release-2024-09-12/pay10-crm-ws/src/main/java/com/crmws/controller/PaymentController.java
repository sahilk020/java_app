package com.crmws.controller;

import java.net.URI;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.ResponseEntity.BodyBuilder;
import org.springframework.http.ResponseEntity.HeadersBuilder;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.crmws.dto.MerchantRequest;
import com.crmws.dto.MerchantResponse;
import com.crmws.dto.ResponseEnvelope;
import com.crmws.service.PaymentLinkApiService;


@RestController
@RequestMapping("/api/v1/paymentLink")
public class PaymentController {
	
	@Autowired
	PaymentLinkApiService paymentService;
	
	@PostMapping(produces = {MediaType.APPLICATION_JSON_VALUE})
	public ResponseEntity<ResponseEnvelope<MerchantResponse>> payment(@Validated @RequestBody MerchantRequest request)
	{
		
			ResponseEnvelope<MerchantResponse> response = paymentService.createPaymentLink(request);
	        return new ResponseEntity<>(response, response.getHttpStatus());
	   
		
        		
	 }

}
