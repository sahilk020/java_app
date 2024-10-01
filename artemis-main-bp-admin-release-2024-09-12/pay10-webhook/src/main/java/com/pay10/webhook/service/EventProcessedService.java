package com.pay10.webhook.service;

import java.util.ArrayList;
import java.util.List;

import com.pay10.commons.user.EventProcessedStatus;
import com.pay10.commons.user.SubscriberConfig;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pay10.webhook.repository.EventProcessedRepository;
import com.pay10.webhook.repository.SubscriberConfigRepository;
import com.pay10.webhook.vo.EventsProcessedVo;
import com.pay10.webhook.vo.ResponseEnvelope;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class EventProcessedService {

	
	@Autowired
	EventProcessedRepository processedRepository;
	
	@Autowired
	SubscriberConfigRepository subscriberConfigRepository;
	
	@Autowired
	ObjectMapper mapper;


	
	public List<EventProcessedStatus> getFailedEvents()
	{
		List<EventProcessedStatus> events = processedRepository.findByStatus("FAIL");
		
		return events;
	}
	
	public EventProcessedStatus getEventPayload(Long eventProcessId)
	{
		EventProcessedStatus event = processedRepository.findByEventProcessedId(eventProcessId);
		
		return event;
	}
	
	public void updateStatus(Long eventProcessId,String status)
	{
		EventProcessedStatus event = processedRepository.findByEventProcessedId(eventProcessId);
		event.setStatus(status);
		processedRepository.save(event);
	}
	
	public void save(EventProcessedStatus event)
	{
		processedRepository.save(event);
	}
	
	public ResponseEnvelope<List<EventsProcessedVo>> fetchByAssociationAndEvent(String associationId, Long eventId)
	{
		List<EventsProcessedVo> eventsProcessedList = new ArrayList<>();
		try {
		SubscriberConfig subscriber = subscriberConfigRepository.findByAssociationIdAndEventId(associationId,eventId);
		if(!ObjectUtils.isEmpty(subscriber)) {
			List<EventProcessedStatus> processedStatusList=processedRepository.findBySubscriberId(subscriber.getSubscriberId());
			if(!CollectionUtils.isEmpty(processedStatusList))
			{
				for(EventProcessedStatus processedEvent : processedStatusList)
				{
					String payload = mapper.writeValueAsString(processedEvent.getEventpayload().getData());
					EventsProcessedVo eventsProcessedVo = new EventsProcessedVo(processedEvent.getWebhookUrl(),payload,
							processedEvent.getStatus(),processedEvent.getProcessedOn());
					eventsProcessedList.add(eventsProcessedVo);
				}
				
			}
			
		}
		}catch (Exception e) {
			log.error(e.getMessage());
		}
		return new ResponseEnvelope<List<EventsProcessedVo>>(HttpStatus.OK,eventsProcessedList);
	}
}
