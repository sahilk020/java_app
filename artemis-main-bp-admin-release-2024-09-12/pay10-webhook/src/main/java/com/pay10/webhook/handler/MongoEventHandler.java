package com.pay10.webhook.handler;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Date;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.pay10.commons.dao.FieldsDao;
import com.pay10.commons.user.EventPayload;
import com.pay10.pg.core.util.ResponseProcessor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.NotImplementedException;
import org.bson.Document;
import org.springframework.util.ObjectUtils;
import org.springframework.util.SerializationUtils;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pay10.webhook.dto.EventMeta;
import com.pay10.webhook.dto.MongoEvent;
import com.pay10.webhook.repository.EventPayloadRepository;

@Slf4j
public abstract class MongoEventHandler<T> extends BaseEventHandler<MongoEvent<T>>{
	
	private EventPayloadRepository payloadRepository;
	
	private ObjectMapper mapper;



	public MongoEventHandler(EventPayloadRepository payloadRepository,ObjectMapper mapper) {
		this.payloadRepository =payloadRepository;
		this.mapper = mapper;

	}

	
	@Override
	public boolean validateEvent(MongoEvent<T> event) {
		return (validateEventMeta(event.getMeta()) && validateEventPayload(event.getPayload()));
	}

	@Override
	public abstract void saveEventData(MongoEvent<T> event) throws Exception ;

	public boolean validateEventMeta(EventMeta meta) {
		// TODO Auto-generated method stub
		if(ObjectUtils.isEmpty(meta) || StringUtils.isEmpty(meta.getTraceId()))
		{
			return false;
		}
		if(StringUtils.isEmpty(meta.getType()) || !meta.getType().equalsIgnoreCase("MONGO_EVENT"))
		{
			return false;
		}
		return true;
	}
	
	public boolean validateEventPayload(T payload)
	{
		throw new NotImplementedException("Validate Event Payload Method Not Implemented");
	}
	
	public MongoEvent<T> transformEventData(Document payload,String name,String traceId,Date createdOn) 
	{
		throw new NotImplementedException("Transform Event Method Not Implemented");
	}
	
	
	
	
	
	
	
	

}
