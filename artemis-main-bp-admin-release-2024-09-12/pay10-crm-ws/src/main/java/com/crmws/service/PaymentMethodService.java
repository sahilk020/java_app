package com.crmws.service;



import org.springframework.http.ResponseEntity;

import com.pay10.commons.dto.PaymentMethod;

public interface PaymentMethodService {

	public ResponseEntity<PaymentMethod> getAllPaymentType(String channelType);
}
