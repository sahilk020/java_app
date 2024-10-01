package com.pay10.webhook.consumer;

import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.annotation.PostConstruct;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.pay10.commons.user.EventPayload;
import com.pay10.commons.user.SubscriberConfig;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.PropertiesManager;
import com.pay10.commons.util.StatusType;
import org.apache.commons.lang3.ObjectUtils;
import org.bson.BsonDocument;
import org.bson.BsonTimestamp;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoChangeStreamCursor;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.changestream.ChangeStreamDocument;
import com.pay10.webhook.config.MongoConfig;
import com.pay10.webhook.dto.MongoEvent;
import com.pay10.webhook.handler.MongoEventHandler;
import com.pay10.webhook.handler.MongoEventHandlerFactory;
import com.pay10.webhook.service.EventPayloadService;
import com.pay10.webhook.service.SubscriberConfigService;

import lombok.extern.slf4j.Slf4j;

@Component
@ConditionalOnProperty(value = MongoConfig.ACTIVATION_KEY, havingValue = "true")
@Slf4j
public class MongoConsumer extends BaseConsumer {
	
	public static final String prefix = "MONGO_DB_";

	@Autowired
	private EventPayloadService eventPayloadService;
	
	@Autowired
	SubscriberConfigService subscriberConfigService;
	
	@Autowired
	private MongoConfig config;

	@Autowired
	private MongoClient client;

	private MongoChangeStreamCursor<ChangeStreamDocument<Document>> cursor;

	private MongoEventHandler eventHandler;

	private AtomicBoolean stopped;
	
	@PostConstruct
	private void init() {
		stopped = new AtomicBoolean(true);
	}

	private void connect() {
		stopped.set(false);
	}
	
	@Autowired
	PropertiesManager propertiesManager;


	@Override
	public void start() {
		connect();
		EventPayload event = eventPayloadService.fetchLatest();
		log.info("Collection Key: {}",prefix+Constants.COLLECTION_NAME.getValue());
		log.info("Collection Name List To Watch: {}",Arrays.asList( propertiesManager.getSystemProperty(prefix+Constants.COLLECTION_NAME.getValue()), propertiesManager.getSystemProperty(prefix+Constants.TRANSACTION_LEDGER_PO.getValue())));
		if (!ObjectUtils.isEmpty(event) && !ObjectUtils.isEmpty(event.getEventTime())) {
			Instant resumeInstant = Instant.ofEpochSecond((event.getEventTime().getTime()/ 1000));
			BsonTimestamp resumeTimestamp = new BsonTimestamp((int)resumeInstant.getEpochSecond(),1);
			log.info("Resuming From "+ Instant.ofEpochSecond(resumeTimestamp.getTime()));
			cursor = client.getDatabase(config.getDatabase())
					.watch(Arrays.asList(Aggregates.match(
							Filters.in("ns.coll", Arrays.asList( propertiesManager.getSystemProperty(prefix+Constants.COLLECTION_NAME.getValue()), propertiesManager.getSystemProperty(prefix+Constants.TRANSACTION_LEDGER_PO.getValue())))
					),Aggregates.match(Filters.and(Filters.nin("fullDocument.STATUS", Arrays.asList(StatusType.SETTLED_RECONCILLED.getName(),StatusType.SETTLED_SETTLE.getName(),
									StatusType.SENT_TO_BANK.getName(),StatusType.CHARGEBACK_INITIATED.getName(),StatusType.CHARGEBACK_REVERSAL.getName(),StatusType.FORCE_CAPTURED.getName(),
									StatusType.ERROR.getName(),StatusType.INVALID.getName(),StatusType.PENDING.getName())),
							Filters.in("operationType", Collections.singletonList("insert"))))), Document.class)
					.startAtOperationTime(resumeTimestamp).cursor();
		} else {
			log.info("Starting From "+ Instant.now());
			cursor = client.getDatabase(config.getDatabase())
					.watch(Arrays.asList(Aggregates.match(
							Filters.in("ns.coll", Arrays.asList( propertiesManager.getSystemProperty(prefix+Constants.COLLECTION_NAME.getValue()), propertiesManager.getSystemProperty(prefix+Constants.TRANSACTION_LEDGER_PO.getValue())))
					),Aggregates.match(Filters.and(Filters.nin("fullDocument.STATUS", Arrays.asList(StatusType.SETTLED_RECONCILLED.getName(),StatusType.SETTLED_SETTLE.getName(),
									StatusType.SENT_TO_BANK.getName(),StatusType.CHARGEBACK_INITIATED.getName(),StatusType.CHARGEBACK_REVERSAL.getName(),StatusType.FORCE_CAPTURED.getName(),
									StatusType.ERROR.getName(),StatusType.INVALID.getName(),StatusType.PENDING.getName())),
							Filters.in("operationType", Collections.singletonList("insert"))))), Document.class)
					.cursor();
		}

		while (cursor.hasNext()) {
			ChangeStreamDocument<Document> change = cursor.next();
			Instant timestamp = Instant.ofEpochSecond(change.getClusterTime().getTime());
			String collection = change.getDatabaseName()==null?propertiesManager.getSystemProperty(prefix+Constants.COLLECTION_NAME.getValue()):change.getNamespace().getCollectionName();
			log.info("Event Time "+ timestamp);
			processMessage(change.getFullDocument(), collection,
			change.getResumeToken().getString("_data").getValue(),Date.from(timestamp));
			 
			 
		}

	}

	@Override
	public void stop() {
		log.info("Closing connections");
		stopped.set(true);
		cursor.close();
	}

	public void resume(String resumeToken) {
		log.info("Collection Key: {}",prefix+Constants.COLLECTION_NAME.getValue());
		log.info("Collection Name List To Watch: {}",Arrays.asList( propertiesManager.getSystemProperty(prefix+Constants.COLLECTION_NAME.getValue()), propertiesManager.getSystemProperty(prefix+Constants.TRANSACTION_LEDGER_PO.getValue())));
		log.info("Resuming after Token : "+resumeToken);  
		String jsonDoc = "{'_data' : '" + resumeToken + "'}";
		BsonDocument resumeTokenDoc = BsonDocument.parse(jsonDoc);
		if (stopped.get()) {
			connect();
			cursor = client.getDatabase(config.getDatabase())
					.watch(Arrays.asList(Aggregates.match(
							Filters.in("ns.coll", Arrays.asList( propertiesManager.getSystemProperty(prefix+Constants.COLLECTION_NAME.getValue()), propertiesManager.getSystemProperty(prefix+Constants.TRANSACTION_LEDGER_PO.getValue())))
					),Aggregates.match(Filters.and(Filters.eq("fullDocument.STATUS", "Captured"),
							Filters.in("operationType", Collections.singletonList("insert"))))), Document.class)
					.resumeAfter(resumeTokenDoc).cursor();
		}
		while (!stopped.get() && cursor.hasNext()) {
			ChangeStreamDocument<Document> change = cursor.next();
			Instant timestamp = Instant.ofEpochSecond(change.getClusterTime().getTime());
			String collection = change.getNamespace()==null?propertiesManager.getSystemProperty(prefix+Constants.COLLECTION_NAME.getValue()):change.getNamespace().getCollectionName();
			log.info("Event Time"+ timestamp);
			processMessage(change.getFullDocument(), collection,
					change.getResumeToken().getString("_data").getValue(),Date.from(timestamp));
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void processMessage(Document message, String name, String traceId, Date eventTime) {
		this.eventHandler = MongoEventHandlerFactory.getEventHandler(name);
		MongoEvent event = eventHandler.transformEventData(message, name, traceId,eventTime);
		if(!eventSubscribed(event))
		{
			log.info("Event Not Subscribed with traceId = "+traceId+" And Association = "+event.getMeta().getAssociationId());
			return;
		}
		eventHandler.validateEvent(event);
		try {
			log.info("Saving Event Payload with traceId = "+traceId+" And Event Time = "+event.getMeta().getEventTime());
			eventHandler.saveEventData(event);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("rawtypes")
	private boolean eventSubscribed(MongoEvent event)
	{
		SubscriberConfig subscriberConfig = null;
		try {
			subscriberConfig= subscriberConfigService.fetchByAssociationAndEventDetails(event.getMeta().getAssociationId(), event.getMeta().getName(), event.getMeta().getType());
		}catch (Exception e)
		{
			return false;
		}

		return subscriberConfig!=null;
	}

}
