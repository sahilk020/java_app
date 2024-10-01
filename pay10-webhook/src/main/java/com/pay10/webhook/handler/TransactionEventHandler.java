package com.pay10.webhook.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pay10.commons.api.Hasher;
import com.pay10.commons.dao.FieldsDao;
import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.exception.SystemException;
import com.pay10.commons.user.EventPayload;
import com.pay10.commons.util.*;
import com.pay10.pg.core.util.ResponseProcessor;
import com.pay10.webhook.consumer.MongoConsumer;
import com.pay10.webhook.dto.EventMeta;
import com.pay10.webhook.dto.MongoEvent;
import com.pay10.webhook.repository.EventPayloadRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import javax.annotation.PostConstruct;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.Map;

@Component
@Slf4j
public class TransactionEventHandler extends MongoEventHandler<String> {
	
	private static EventPayloadRepository eventPayloadRepository;
	
	private static ObjectMapper objectMapper;


	@Autowired
	ObjectMapper mapper;

	@Autowired
	PropertiesManager propertiesManager;

	@Autowired
	EventPayloadRepository payloadRepository;

	@Autowired
	private ResponseProcessor responseProcessor;

	@Autowired
	private FieldsDao fieldsDao ;

	@Autowired
	private Hasher hasher;
	
	public TransactionEventHandler() {
		super(eventPayloadRepository,objectMapper);
		
	}
	
	@PostConstruct
    private void init() {
        eventPayloadRepository = payloadRepository;
        objectMapper = mapper;

    }

	@Override
	public boolean validateEventPayload(String payload) {
		// TODO Auto-generated method stub
		return true;
	}
	

	@Override
	public MongoEvent<String> transformEventData(Document payload,String name,String traceId,Date createdOn) {
		if(!ObjectUtils.isEmpty(payload))
		{
			EventMeta eventMeta = new EventMeta(name, payload.getString("STATUS"), "MONGO_EVENT", 1, payload.getString("PAY_ID"), traceId, createdOn);
			//TransactionPayload data = objectMapper.convertValue(payload, new TypeReference<TransactionPayload>() {});
			String data=createResponsePayload(name,payload);
			return new MongoEvent<String>(data,eventMeta);
		}
		return null;
	}

	@Override
	public void saveEventData(MongoEvent<String> event) throws NoSuchAlgorithmException, JsonProcessingException {
		/*final MessageDigest digester = MessageDigest.getInstance("SHA-256");
		byte[] byteData = SerializationUtils.serialize(event.getPayload()+ event.getMeta().getTraceId() +event.getMeta().getEventTime().toLocaleString());
		digester.update(byteData);*/
		/*String hash = Base64.getEncoder().encodeToString(digester.digest());*/

		//String data = mapper.writeValueAsString();
		log.info("Payload "+event.getPayload());
		EventPayload eventPayload = new EventPayload(event.getMeta().getTraceId(),event.getMeta().getName(),
				event.getMeta().getType(),event.getMeta().getCategory(), event.getMeta().getAssociationId(),"",false,new Date(),event.getMeta().getEventTime(),event.getPayload());
		payloadRepository.save(eventPayload);
	}


	private String createResponsePayload(String name, Document payload) {
		log.info("Payload To Be Processed : "+payload.toJson());
		log.info("FieldsDao : "+fieldsDao);
		Fields fields = new Fields();
		for(Map.Entry<String,Object> entry : payload.entrySet()){
			if(entry.getValue()!=null) {
				fields.put(entry.getKey(), entry.getValue().toString());
			}
		}
		String applicationKeyForRemoval;
		if(name.equals(propertiesManager.getSystemProperty(MongoConsumer.prefix+Constants.TRANSACTION_LEDGER_PO.getValue()))) {
			log.info("Webhook Pay-out response");
			applicationKeyForRemoval = Constants.RESPONSE_WEBHOOK_PAY_OUT.getValue();
		}else{
			log.info("Webhook Pay-in response");
			applicationKeyForRemoval = Constants.RESPONSE_WEBHOOK_PAY_IN.getValue();
		}
		try {
			if (StringUtils.isNotBlank(fields.get(FieldType.RESPONSE_CODE.getName())) && fields
					.get(FieldType.RESPONSE_CODE.getName()).equals(ErrorType.DUPLICATE_RESPONSE.getCode())) {
				return null;
			}
		} catch (Exception exception) {
			log.error("Exception", exception);
			fields.clear();
		}

		Map<String, String> responseMap = fields.getFields();
		String responseString="" ;
		log.info("Final Webhook Response Payload: {}", responseMap);
		if(null != responseMap && responseMap.containsKey("STATUS")) {
			if(responseMap.get(FieldType.STATUS.getName()).equalsIgnoreCase("Failed")) {
				if(responseMap.containsKey(FieldType.RESPONSE_MESSAGE.getName())
						&& responseMap.get(FieldType.RESPONSE_MESSAGE.getName()).equalsIgnoreCase("SUCCESS")) {
					responseMap.put(FieldType.RESPONSE_MESSAGE.getName(), "Transaction Failed");
				}
			}
		}
		responseMap.remove(FieldType.INTERNAL_CUSTOM_MDC.getName());
		responseMap.remove(FieldType.HASH.getName());
		try {
			Fields response = new Fields(responseMap);
			resolveStatus(response);
			fieldsDao.removeFieldsByPropertyFile(response, applicationKeyForRemoval);
			response.put(FieldType.HASH.getName(), Hasher.getHash(response));
			Map<String, String> responseMapWithHash = response.getFields();
			responseString = objectMapper.writeValueAsString(responseMapWithHash);
		}catch (Exception exception) {
			log.error("Exception", exception);
			fields.clear();
		}
		log.info("Final Webhook Response Payload : {}", responseString);
		return responseString;
	}


	public void resolveStatus(Fields fields) {
		String status = fields.get(FieldType.STATUS.getName());
		if (org.apache.commons.lang3.StringUtils.isNotBlank(status) && (status.equalsIgnoreCase(StatusType.CAPTURED.getName()) || status.equalsIgnoreCase(StatusType.FAILED.getName()) || status.equalsIgnoreCase("REFUND_INITIATED"))) {
		} else if(org.apache.commons.lang3.StringUtils.isNotBlank(status) && (status.equalsIgnoreCase(StatusType.DENIED.getName()))){
			fields.put(FieldType.STATUS.getName(),"REQUEST ACCEPTED");
			fields.put(FieldType.RESPONSE_CODE.getName(), "026");
		}else if(status.equalsIgnoreCase("REQUEST ACCEPTED") || status.equalsIgnoreCase("Pending") || status.equalsIgnoreCase(StatusType.SENT_TO_BANK.getName())){
			fields.put(FieldType.STATUS.getName(),"REQUEST ACCEPTED");
			fields.put(FieldType.RESPONSE_CODE.getName(), "000");
		}else {
			fields.put(FieldType.STATUS.getName(), StatusType.FAILED.getName());
		}
	}



}
