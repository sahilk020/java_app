package com.pay10.webhook.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.pay10.commons.user.Events;
import com.pay10.commons.user.SubscriberConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import com.pay10.webhook.repository.EventsRepository;
import com.pay10.webhook.repository.SubscriberConfigRepository;
import com.pay10.webhook.vo.ResponseEnvelope;
import com.pay10.webhook.vo.SubscriberVo;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class SubscriberConfigService {

    @Autowired
    SubscriberConfigRepository subscriberConfigRepository;

    @Autowired
    EventsRepository eventsRepository;

    public ResponseEnvelope<SubscriberVo> save(SubscriberVo subscriberVo) {
        if (!ObjectUtils.isEmpty(subscriberVo)) {
            try {
                SubscriberConfig subscriber = null;
                if (!ObjectUtils.isEmpty(subscriberVo.getSubscriberId()) && subscriberVo.getSubscriberId() > 0) {
                    subscriber = subscriberConfigRepository.findBySubscriberId(subscriberVo.getSubscriberId());
                    subscriber.setAssociationId(subscriberVo.getAssociationId());
                    subscriber.setPayInUrl(subscriberVo.getPayInUrl());
                    subscriber.setPayOutUrl(subscriberVo.getPayOutUrl());
                    subscriber.setActive(subscriberVo.isActive());
                    subscriber.setUpdatedOn(new Date());
                    List<Events> event = eventsRepository.findAll();
                    subscriber.setEvent(event);
                    subscriberConfigRepository.save(subscriber);
                    return new ResponseEnvelope<SubscriberVo>(HttpStatus.OK, "Subscription Updated Successfully", subscriberVo);

                } else {
                    subscriber = new SubscriberConfig();
                    subscriber.setAssociationId(subscriberVo.getAssociationId());
                    subscriber.setPayInUrl(subscriberVo.getPayInUrl());
                    subscriber.setPayOutUrl(subscriberVo.getPayOutUrl());
                    subscriber.setActive(subscriberVo.isActive());
                    subscriber.setCreatedOn(new Date());
                    List<Events> event = eventsRepository.findAll();
                    subscriber.setEvent(event);
                    subscriberConfigRepository.save(subscriber);
                    subscriberVo.setSubscriberId(subscriber.getSubscriberId());
                    return new ResponseEnvelope<SubscriberVo>(HttpStatus.CREATED, "Subscription Created Successfully", subscriberVo);
                }
            } catch (Exception e) {
                return new ResponseEnvelope<>(HttpStatus.INTERNAL_SERVER_ERROR, subscriberVo, "Internal Error");
            }
        } else {
            return new ResponseEnvelope<>(HttpStatus.BAD_REQUEST, subscriberVo, "Empty Subscriber");
        }
    }

    public ResponseEnvelope<SubscriberVo> update(SubscriberVo subscriberVo) {
        if (!ObjectUtils.isEmpty(subscriberVo) && !ObjectUtils.isEmpty(subscriberVo.getSubscriberId())) {
            try {
                SubscriberConfig subscriber = subscriberConfigRepository.findBySubscriberId(subscriberVo.getSubscriberId());
                subscriber.setAssociationId(subscriberVo.getAssociationId());
                subscriber.setPayInUrl(subscriberVo.getPayInUrl());
                subscriber.setPayOutUrl(subscriberVo.getPayOutUrl());
                subscriber.setActive(subscriberVo.isActive());
                subscriber.setUpdatedOn(new Date());
                List<Events> event = eventsRepository.findAll();
                subscriber.setEvent(event);
                subscriberConfigRepository.save(subscriber);
                return new ResponseEnvelope<SubscriberVo>(HttpStatus.OK, "Subscription Updated Successfully", subscriberVo);
            } catch (Exception e) {
                return new ResponseEnvelope<>(HttpStatus.INTERNAL_SERVER_ERROR, subscriberVo, "Internal Error");
            }
        } else {
            return new ResponseEnvelope<>(HttpStatus.BAD_REQUEST, subscriberVo, "Empty Subscriber");
        }
    }

    public ResponseEnvelope<SubscriberVo> fetchByAssociationAndEvent(String associationId, Long eventId) {
        SubscriberVo subscriberVo = null;

        try {
            SubscriberConfig subscriber = subscriberConfigRepository.findByAssociationIdAndEventId(associationId, eventId);
            if (!ObjectUtils.isEmpty(subscriber)) {
                subscriberVo = new SubscriberVo();
                subscriberVo.setSubscriberId(subscriber.getSubscriberId());
                subscriberVo.setAssociationId(subscriber.getAssociationId());
                subscriberVo.setPayInUrl(subscriber.getPayInUrl());
                subscriberVo.setPayOutUrl(subscriber.getPayOutUrl());
                subscriberVo.setActive(subscriber.isActive());
                List<Long> eventIds = new ArrayList<>();
                for (Events events : subscriber.getEvent()) {
                    eventIds.add(events.getEventId());
                }
                subscriberVo.setEventId(eventIds);

            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return new ResponseEnvelope<SubscriberVo>(HttpStatus.OK, subscriberVo);
    }


    public SubscriberConfig fetchByAssociationAndEventDetails(String associationId, String eventName, String type) {
        SubscriberConfig subscriber = subscriberConfigRepository.findByAssociationIdAndEventDetails(associationId, eventName, type);
        return subscriber;
    }


    public SubscriberConfig getSubscriber(Long subscriberId) {

        return subscriberConfigRepository.findBySubscriberId(subscriberId);
    }

}
