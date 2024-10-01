package com.crmws.service;

import com.crmws.dto.MerchantRequest;
import com.crmws.dto.MerchantResponse;
import com.crmws.dto.ResponseEnvelope;

public interface IntentPaymentLinkService {

	ResponseEnvelope<MerchantResponse> createPaymentLink(MerchantRequest request);

}
