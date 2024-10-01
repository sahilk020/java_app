package com.pay10.webhook.service;

import java.util.List;

import com.pay10.commons.user.EventPayload;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pay10.webhook.repository.EventPayloadRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@Transactional(rollbackFor = Exception.class)
public class EventPayloadService {
	
	@Autowired
	EventPayloadRepository eventPayloadRepository;
	

	public List<EventPayload> getAndUpdateEventData(boolean status)
	{
		List<EventPayload> events = eventPayloadRepository.findByStatus(status);
		events.stream().forEach(event ->{
			eventPayloadRepository.updateStatusById(true, event.getEventPayloadId());
		});
		
		return events;
	}
	
	
	public List<EventPayload> get(boolean status)
	{
		List<EventPayload> events = eventPayloadRepository.findByStatus(status);
		events.stream().forEach(event ->{
			eventPayloadRepository.updateStatusById(true, event.getEventPayloadId());
		});
		
		return events;
	}
	
	
	public EventPayload fetchEventPayload(Long eventPayloadId)
	{
		EventPayload event = eventPayloadRepository.findByEventPayloadId(eventPayloadId);
		return event;
	}
	
	
	public EventPayload fetchLatest()
	{
		EventPayload event = eventPayloadRepository.findFirstByOrderByEventTimeDesc();
		return event;
	}

}
