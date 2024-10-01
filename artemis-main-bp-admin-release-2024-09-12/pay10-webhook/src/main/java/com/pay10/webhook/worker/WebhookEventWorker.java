package com.pay10.webhook.worker;

import java.util.List;

import com.pay10.commons.user.EventPayload;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.pay10.webhook.processor.WebhookProcessor;
import com.pay10.webhook.service.EventPayloadService;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class WebhookEventWorker {
	
	@Autowired
	WebhookProcessor webhookProcessor;
	
	@Autowired
	EventPayloadService eventPayloadService; 
	
	@Scheduled(cron = "0 */5 * * * ?")
	public void startProcessor()
	{
		log.info("Started Webhook Worker");
		List<EventPayload> events = eventPayloadService.getAndUpdateEventData(false);
		if(!CollectionUtils.isEmpty(events))
		{
			log.info("Total Events Picked :" + events.size());
			webhookProcessor.processEvents(events);
		}
		
		
	}

}
