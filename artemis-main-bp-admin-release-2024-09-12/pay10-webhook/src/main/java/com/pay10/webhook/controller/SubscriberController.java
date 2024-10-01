package com.pay10.webhook.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.pay10.webhook.service.EventProcessedService;
import com.pay10.webhook.service.SubscriberConfigService;
import com.pay10.webhook.vo.EventsProcessedVo;
import com.pay10.webhook.vo.ResponseEnvelope;
import com.pay10.webhook.vo.SubscriberVo;

@RestController
@RequestMapping(SubscriberController.BASE_PATH)
@CrossOrigin(origins = "*")
public class SubscriberController {
	
	public static final String BASE_PATH = "/api/v1/subscription";
	
	@Autowired
	SubscriberConfigService subscriberConfigService;
	
	@Autowired
	EventProcessedService eventProcessedService;
	
	@GetMapping
	public @ResponseBody
	ResponseEntity<ResponseEnvelope<SubscriberVo>> fetchByAssociationAndEvent(
			@RequestParam(value = "associationId",required = true)String associationId,
			@RequestParam(value = "eventId",required = false,defaultValue = "1")Long eventId
			) {

        ResponseEnvelope<SubscriberVo> response = subscriberConfigService.fetchByAssociationAndEvent(associationId,eventId);
        return new ResponseEntity<>(response, response.getHttpStatus());
    }
	
	@PostMapping
	public @ResponseBody
	ResponseEntity<ResponseEnvelope<SubscriberVo>> save(@Validated @RequestBody SubscriberVo subscriber) {

        ResponseEnvelope<SubscriberVo> response = subscriberConfigService.save(subscriber);
        return new ResponseEntity<>(response, response.getHttpStatus());
    }
	
	
	@GetMapping("/events")
	public @ResponseBody
	ResponseEntity<ResponseEnvelope<List<EventsProcessedVo>>> fetchSubscriptionEvents(
			@RequestParam(value = "associationId",required = true)String associationId,
			@RequestParam(value = "eventId",required = false,defaultValue = "1")Long eventId
			) {

        ResponseEnvelope<List<EventsProcessedVo>> response = eventProcessedService.fetchByAssociationAndEvent(associationId,eventId);
        return new ResponseEntity<>(response, response.getHttpStatus());
    }

}
