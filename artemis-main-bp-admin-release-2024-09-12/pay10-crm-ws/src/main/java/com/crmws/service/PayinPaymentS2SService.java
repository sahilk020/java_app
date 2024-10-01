package com.crmws.service;

import java.util.Map;

import com.crmws.dto.PayinPaymentS2SRequest;
import com.crmws.dto.PayinPaymentS2SResponse;
import com.crmws.dto.ResponseEnvelope;

public interface PayinPaymentS2SService {

	Map<String, String> createPaymentLink(PayinPaymentS2SRequest request);

}
