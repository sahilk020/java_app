package com.pay10.webhook.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

import com.pay10.webhook.consumer.BaseConsumer;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class BaseConsumerController<T extends BaseConsumer> {
	
	protected static final String BASE_PATH_PREFIX = "/api/v1/consumer/";
	
	@Autowired
	T consumer;
	
	@GetMapping(path = "/start")
	  public ResponseEntity<String> startConsumer() {
		log.info("Starting" + consumer.getClass().getSimpleName());
		consumer.start();
		return ResponseEntity.ok("Started"+ consumer.getClass().getSimpleName());
	  }
	
	@GetMapping(path = "/stop")
	  public ResponseEntity<String> stopConsumer() {
		log.info("Stopping" + consumer.getClass().getSimpleName());
		consumer.stop();
	    return ResponseEntity.ok("Stopped"+ consumer.getClass().getSimpleName());
	  }

}
