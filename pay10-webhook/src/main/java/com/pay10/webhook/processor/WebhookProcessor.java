package com.pay10.webhook.processor;

import java.time.Duration;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pay10.commons.user.EventPayload;
import com.pay10.commons.user.EventProcessedStatus;
import com.pay10.commons.user.SubscriberConfig;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.PropertiesManager;
import com.pay10.webhook.consumer.MongoConsumer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.client.RestClientResponseException;

import com.pay10.webhook.service.EventPayloadService;
import com.pay10.webhook.service.EventProcessedService;
import com.pay10.webhook.service.SubscriberConfigService;
import com.pay10.webhook.util.HttpService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class WebhookProcessor {

    @Autowired
    HttpService httpService;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    EventProcessedService eventProcessedService;

    @Autowired
    SubscriberConfigService subscriberConfigService;

    @Autowired
    EventPayloadService eventPayloadService;

    @Autowired
    PropertiesManager propertiesManager;

    public void processEvents(List<EventPayload> events) {
        events.stream().forEachOrdered(event -> {

            //fetch subscriber from association and event
            SubscriberConfig subscriberConfig = subscriberConfigService.fetchByAssociationAndEventDetails(event.getAssociationId(), event.getEventName(), event.getEventType());
            if (!ObjectUtils.isEmpty(subscriberConfig)) {
                if(event.getEventName().equalsIgnoreCase(propertiesManager.getSystemProperty(MongoConsumer.prefix + Constants.TRANSACTION_LEDGER_PO.getValue()))) {
                    pushToWebhook(subscriberConfig.getPayOutUrl(), event.getData(),
                            null, subscriberConfig.getSubscriberId(), event.getEventPayloadId(), false);
                }else{
                    pushToWebhook(subscriberConfig.getPayInUrl(), event.getData(),
                            null, subscriberConfig.getSubscriberId(), event.getEventPayloadId(), false);
                }
            }
        });
    }

    public void retryEvents(List<EventProcessedStatus> events) {
        events.stream().forEachOrdered(event -> {

            //fetch event payload and subscriber
            if (eligibleForRetry(event.getEventProcessedId(), event.getProcessedOn(), event.getLastAttemptedOn(), event.getAttempt())) {
                Long subscriberId = event.getSubscriberId();
                pushToWebhook(event.getWebhookUrl(), event.getEventpayload().getData(),
                        event.getEventProcessedId(), subscriberId, event.getEventpayload().getEventPayloadId(), true);
            }

        });
    }


    private void pushToWebhook(String webhookUrl, String payload, Long eventProcessId,
                               Long subscriberId, Long eventPayloadId, boolean retry) {

        try {
            Map<String, Object> data = objectMapper.readValue(payload, new TypeReference<Map<String, Object>>() {
            });
            ResponseEntity<String> response = httpService.postJSON(webhookUrl, data, String.class);
            if (response.getStatusCodeValue() >= 200 && response.getStatusCodeValue() < 300) {
                //status = success
                processApiResponse(response.getBody(), response.getStatusCodeValue(), "SUCCESS",
                        eventProcessId, subscriberId, webhookUrl, eventPayloadId, retry);
            } else {
                //status = fail
                processApiResponse(response.getBody(), response.getStatusCodeValue(), "FAIL",
                        eventProcessId, subscriberId, webhookUrl, eventPayloadId, retry);
            }
        } catch (RestClientResponseException e) {
            // TODO Auto-generated catch block
            log.error(e.getResponseBodyAsString());
            processApiResponse(e.getResponseBodyAsString(), e.getRawStatusCode(), "FAIL",
                    eventProcessId, subscriberId, webhookUrl, eventPayloadId, retry);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            log.error(e.getMessage());
            processApiResponse(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value(), "FAIL",
                    eventProcessId, subscriberId, webhookUrl, eventPayloadId, retry);
        }
    }


    private void processApiResponse(String response, Integer statusCode, String status,
                                    Long eventProcessId, Long subscriberId, String webhookUrl, Long eventPayloadId, boolean retry) {

        if (retry) {
            EventProcessedStatus eventProcessedStatus = eventProcessedService.getEventPayload(eventProcessId);
            eventProcessedStatus.setAttempt(eventProcessedStatus.getAttempt() + 1);
            eventProcessedStatus.setLastAttemptedOn(new Date());
            eventProcessedStatus.setResponseMessage(response);
            eventProcessedStatus.setResponseCode(statusCode);
            eventProcessedStatus.setStatus(status);
            eventProcessedStatus.setWebhookUrl(webhookUrl);
            eventProcessedService.save(eventProcessedStatus);
        } else {
            EventProcessedStatus eventProcessedStatus = new EventProcessedStatus();
            eventProcessedStatus.setAttempt(1);
            eventProcessedStatus.setLastAttemptedOn(new Date());
            eventProcessedStatus.setProcessedOn(new Date());
            eventProcessedStatus.setResponseMessage(response);
            eventProcessedStatus.setResponseCode(statusCode);
            eventProcessedStatus.setStatus(status);
            eventProcessedStatus.setSubscriberId(subscriberId);
            eventProcessedStatus.setWebhookUrl(webhookUrl);
            EventPayload eventPayload = eventPayloadService.fetchEventPayload(eventPayloadId);
            eventProcessedStatus.setEventpayload(eventPayload);
            eventProcessedService.save(eventProcessedStatus);
        }
    }


    private boolean eligibleForRetry(Long eventProcessId, Date processedOn, Date lastAttemptedOn, int attempts) {
        Long intervalDiff = Duration.between(lastAttemptedOn.toInstant(), processedOn.toInstant()).toMillis();
        Integer maxRetrivalMs = 48 * 60 * 60 * 1000;

        if (intervalDiff > maxRetrivalMs || attempts >= 2) {
            eventProcessedService.updateStatus(eventProcessId, "DISABLED");
            return false;
        }

        Long current_interval_time = Duration.between(new Date().toInstant(), lastAttemptedOn.toInstant()).toMillis();
        Long next_retrival = getNextRetrivalTime(attempts);
        if (attempts > 1 && current_interval_time < next_retrival) {
            return false;
        }
        return true;
    }

    private Long getNextRetrivalTime(int attempts) {
        Long interval = (long) (Math.pow(attempts + 1, 3) * 3930);
        return interval;
    }
}
