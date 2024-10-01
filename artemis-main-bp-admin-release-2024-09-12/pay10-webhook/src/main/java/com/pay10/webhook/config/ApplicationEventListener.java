package com.pay10.webhook.config;

import java.util.List;

import javax.annotation.PreDestroy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.pay10.webhook.consumer.BaseConsumer;

import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
public class ApplicationEventListener {
	
	List<BaseConsumer> consumers;
	
	@Autowired
	public ApplicationEventListener(List<BaseConsumer> consumers) {
		this.consumers = consumers;
	}

	@EventListener(ApplicationReadyEvent.class)
	public void initializeConsumers() {
	   
	    if(!CollectionUtils.isEmpty(consumers))
	    {
	    	 log.info("Starting Consumers");
	    	 consumers.forEach(consumer->{
	    		 consumer.start();
	    	 });
	    }
	   
	}
	
	@PreDestroy
    public void shutdownConsumers() {
		
		if(!CollectionUtils.isEmpty(consumers))
	    {
			log.info("Shutting down Consumers");
	    	 consumers.forEach(consumer->{
	    		 consumer.stop();
	    	 });
	    }
    }

}
