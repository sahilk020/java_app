package com.pay10.webhook.worker;

import java.util.List;

import com.pay10.commons.user.EventProcessedStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.pay10.webhook.processor.WebhookProcessor;
import com.pay10.webhook.service.EventProcessedService;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class RetryEventWorker {
	
	@Autowired
	WebhookProcessor webhookProcessor;
	
	@Autowired
	EventProcessedService eventProcessedService; 
	
	
	@Scheduled(cron = "0 */15 * * * ?")
	public void startRetryProcessor()
	{
		log.info("Started Retry Webhook Worker");
		List<EventProcessedStatus> events = eventProcessedService.getFailedEvents();
		if(!CollectionUtils.isEmpty(events))
		{
			log.info("Total Events Picked :" + events.size());
			webhookProcessor.retryEvents(events);
		}
		
		
	}

}
