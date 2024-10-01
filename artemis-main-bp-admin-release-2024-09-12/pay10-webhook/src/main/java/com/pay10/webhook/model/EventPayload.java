package com.pay10.webhook.model;

import java.util.Date;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.pay10.webhook.util.HashMapConverter;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@Entity
@Table(name = "event_payload")
public class EventPayload {
	
	@Id
    @GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="event_payload_id")
	private Long eventPayloadId;
	
	@Column(name="event_trace_id")
	private String eventTraceId;
	
	@Column(name="event_name")
	private String eventName;
	
	@Column(name="event_type")
	private String eventType;
	
	@Column(name="event_category")
	private String category;
	
	@Column(name="association_id")
	private String associationId;
	
	@Column(name="hash")
	private String hash;
	
	@Column(name="status")
	private boolean status;
	
	@Column(name="created_on")
	@Temporal(TemporalType.TIMESTAMP)
	private Date createdOn;
	
	@Column(name="event_time")
	@Temporal(TemporalType.TIMESTAMP)
	private Date eventTime;
	
	@Convert(converter = HashMapConverter.class)
	@Lob
	@Column(name="data")
    private Map<String, Object> data;

	public EventPayload(String eventTraceId, String eventName, String eventType,String category, String associationId,String hash, boolean status,
			Date createdOn,Date eventTime, Map<String, Object> data) {
		super();
		this.eventTraceId = eventTraceId;
		this.eventName = eventName;
		this.eventType = eventType;
		this.category=category;
		this.associationId = associationId;
		this.hash = hash;
		this.status = status;
		this.createdOn = createdOn;
		this.eventTime = eventTime;
		this.data = data;
	}
	
	

}
