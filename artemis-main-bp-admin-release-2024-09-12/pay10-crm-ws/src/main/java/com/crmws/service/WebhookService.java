package com.crmws.service;

import java.util.List;

import com.pay10.commons.dto.WebhookRequest;

public interface WebhookService {

    List<WebhookRequest> saveData(WebhookRequest request);

    List<WebhookRequest> getData(String payId);

}
