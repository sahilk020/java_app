package com.crmws.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.crmws.dao.impl.WebhookDaoImpl;
import com.crmws.service.WebhookService;
import com.pay10.commons.dto.WebhookRequest;
import com.pay10.commons.entity.PGWebHookPostConfigURL;

@Service
public class WebhookServiceImpl implements WebhookService {

	private static final Logger logger = LoggerFactory.getLogger(WebhookServiceImpl.class.getName());

	@Autowired
	private WebhookDaoImpl webhookDao;

	@SuppressWarnings("unchecked")
	@Override
	public List<WebhookRequest> saveData(WebhookRequest request) {
	    List<WebhookRequest> webhookList = new ArrayList<>();
	    try {
	        PGWebHookPostConfigURL entityReq = new PGWebHookPostConfigURL();
	        entityReq.setPayId(request.getPayId());

	        // Handling Pay-In URL
	        if (request.getPayInUrl() != null) {
	            entityReq.setWebhookType("PAYIN");
	            entityReq.setWebhookUrl(request.getPayInUrl());

	            if (StringUtils.isNotBlank(request.getPayInId())) {
	                PGWebHookPostConfigURL dbResult = webhookDao.findById(Long.parseLong(request.getPayInId()));
	                if (dbResult != null) {
	                    if (!dbResult.getWebhookUrl().equals(request.getPayInUrl()) || 
	                        !(request.isPayInActive() ? "ACTIVE" : "INACTIVE").equalsIgnoreCase(dbResult.getStatus())) {
	                        dbResult.setWebhookUrl(request.getPayInUrl());
	                        dbResult.setStatus(request.isPayInActive() ? "ACTIVE" : "INACTIVE");
	                        dbResult.setUpdatedOn(new Date());
	                        webhookDao.update(dbResult);
	                    }
	                }
	            } else {
	                entityReq.setStatus(request.isPayInActive() ? "ACTIVE" : "INACTIVE");
	                entityReq.setCreatedOn(new Date());
	                webhookDao.save(entityReq);
	            }
	        } else {
	            // Handle the case where the Pay-In URL is removed
	            if (StringUtils.isNotBlank(request.getPayInId())) {
	                PGWebHookPostConfigURL dbResult = webhookDao.findById(Long.parseLong(request.getPayInId()));
	                if (dbResult != null) {
	                    dbResult.setWebhookUrl("");
	                    dbResult.setStatus("INACTIVE");
	                    dbResult.setUpdatedOn(new Date());
	                    webhookDao.update(dbResult);
	                }
	            }
	        }

	        // Handling Pay-Out URL
	        if (request.getPayOutUrl() != null) {
	            entityReq.setWebhookType("PAYOUT");
	            entityReq.setWebhookUrl(request.getPayOutUrl());

	            if (StringUtils.isNotBlank(request.getPayOutId())) {
	                PGWebHookPostConfigURL dbResult = webhookDao.findById(Long.parseLong(request.getPayOutId()));
	                if (dbResult != null) {
	                    if (!dbResult.getWebhookUrl().equals(request.getPayOutUrl()) || 
	                        !(request.isPayOutActive() ? "ACTIVE" : "INACTIVE").equalsIgnoreCase(dbResult.getStatus())) {
	                        dbResult.setWebhookUrl(request.getPayOutUrl());
	                        dbResult.setStatus(request.isPayOutActive() ? "ACTIVE" : "INACTIVE");
	                        dbResult.setUpdatedOn(new Date());
	                        webhookDao.update(dbResult);
	                    }
	                }
	            } else {
	                entityReq.setStatus(request.isPayOutActive() ? "ACTIVE" : "INACTIVE");
	                entityReq.setCreatedOn(new Date());
	                webhookDao.save(entityReq);
	            }
	        } else {
	        	
	            // Handle the case where the Pay-Out URL is removed
	            if (StringUtils.isNotBlank(request.getPayOutId())) {
	                PGWebHookPostConfigURL dbResult = webhookDao.findById(Long.parseLong(request.getPayOutId()));
	                if (dbResult != null) {
	                    dbResult.setWebhookUrl("");
	                    dbResult.setStatus("INACTIVE");
	                    dbResult.setUpdatedOn(new Date());
	                    webhookDao.update(dbResult);
	                }
	            }
	        }

	        webhookList.add(request);
	    } catch (Exception e) {
	        logger.error(e.getMessage());
	        e.printStackTrace();
	    }
	    return webhookList;
	}


	@SuppressWarnings("unchecked")
	@Override
	public List<WebhookRequest> getData(String payId) {
		List<PGWebHookPostConfigURL> getlist = webhookDao.findByPayId(payId);
		List<WebhookRequest> sendRequest = new ArrayList<>();

		for (PGWebHookPostConfigURL data : getlist) {
			WebhookRequest webhookRequest = new WebhookRequest();
			webhookRequest.setPayId(data.getPayId());
			webhookRequest.setId(String.valueOf(data.getId()));
//			webhookRequest.setActive(data.getStatus().equalsIgnoreCase("ACTIVE") ? true : false);
			if (data.getWebhookType() != null && data.getWebhookType().equalsIgnoreCase("PAYIN")) {
				webhookRequest.setPayInUrl(data.getWebhookUrl());
				webhookRequest.setPayInActive(data.getStatus().equalsIgnoreCase("ACTIVE") ? true : false);
			}
			if (data.getWebhookType() != null && data.getWebhookType().equalsIgnoreCase("PAYOUT")) {
				webhookRequest.setPayOutUrl(data.getWebhookUrl());
				webhookRequest.setPayOutActive(data.getStatus().equalsIgnoreCase("ACTIVE") ? true : false);
			}
			sendRequest.add(webhookRequest);

		}
		return sendRequest;
	}

}
