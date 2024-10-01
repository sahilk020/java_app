package com.pay10.webhook.handler;

import com.pay10.commons.util.Constants;
import com.pay10.commons.util.PropertiesManager;
import com.pay10.webhook.consumer.MongoConsumer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.*;

@Component
public class MongoEventHandlerFactory {

	@Autowired
	private ApplicationContext applicationContext;

	@Autowired
	private PropertiesManager propertiesManager;

	private static ApplicationContext context;

	private static final Set<String> collectionNames = new HashSet<>();

	@PostConstruct
	public void init()
	{
		collectionNames.add(propertiesManager.getSystemProperty(MongoConsumer.prefix+ Constants.COLLECTION_NAME.getValue()));
		collectionNames.add(propertiesManager.getSystemProperty(MongoConsumer.prefix+ Constants.TRANSACTION_LEDGER_PO.getValue()));
		context = applicationContext;
	}
	
	@SuppressWarnings("rawtypes")
	public static MongoEventHandler getEventHandler(String type)
	{
		if(collectionNames.contains(type)) {
			return context.getBean(TransactionEventHandler.class);
		}
		return null;
	}



}
