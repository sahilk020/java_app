package com.pay10.webhook.controller;

import javax.websocket.server.PathParam;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.pay10.webhook.config.MongoConfig;
import com.pay10.webhook.consumer.MongoConsumer;

import lombok.extern.slf4j.Slf4j;

@RestController
@ConditionalOnProperty(
	    value = MongoConfig.ACTIVATION_KEY,
	    havingValue = "true")
@RequestMapping(path = MongoConsumerController.BASE_PATH)
@Slf4j
public class MongoConsumerController extends BaseConsumerController<MongoConsumer>{
	
	public static final String BASE_PATH = BASE_PATH_PREFIX + "mongo";
	
	@GetMapping(path = "/resume/{token}")
	  public ResponseEntity<String> resumeConsumer(@PathParam(value = "token") String token) {
		log.info("Resuming" + consumer.getClass().getSimpleName() + " with token : "+token);
		consumer.resume(token);
	    return ResponseEntity.ok("Resumed"+ consumer.getClass().getSimpleName()+ " with token : "+token);
	  }

}
