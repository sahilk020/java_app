package com.crmws.controller;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import com.crmws.service.impl.PaymentTypeServiceImpl;
import com.pay10.commons.dto.PaymentMethod;

@CrossOrigin
@RestController
@RequestMapping("PaymentMethod")
public class GetPaymentType {
	
	@Autowired
	private PaymentTypeServiceImpl paymentTypeServiceImpl;

	@GetMapping("/PaymentType/{paymentType}")
	public ResponseEntity<PaymentMethod> paymentType(@PathVariable String paymentType) {
		return paymentTypeServiceImpl.getAllPaymentType(paymentType);
		
		
	}
}
