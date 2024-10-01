package com.pay10.commons.user;



import javax.persistence.*;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


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
	
	//@Convert(converter = HashMapConverter.class)
	@Lob
	@Column(name="data")
    private String data =null;

	public EventPayload(String eventTraceId, String eventName, String eventType,String category, String associationId,String hash, boolean status,
			Date createdOn,Date eventTime, String data) {
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

	public EventPayload() {
	}

	public Long getEventPayloadId() {
		return eventPayloadId;
	}

	public void setEventPayloadId(Long eventPayloadId) {
		this.eventPayloadId = eventPayloadId;
	}

	public String getEventTraceId() {
		return eventTraceId;
	}

	public void setEventTraceId(String eventTraceId) {
		this.eventTraceId = eventTraceId;
	}

	public String getEventName() {
		return eventName;
	}

	public void setEventName(String eventName) {
		this.eventName = eventName;
	}

	public String getEventType() {
		return eventType;
	}

	public void setEventType(String eventType) {
		this.eventType = eventType;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getAssociationId() {
		return associationId;
	}

	public void setAssociationId(String associationId) {
		this.associationId = associationId;
	}

	public String getHash() {
		return hash;
	}

	public void setHash(String hash) {
		this.hash = hash;
	}

	public boolean isStatus() {
		return status;
	}

	public void setStatus(boolean status) {
		this.status = status;
	}

	public Date getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}

	public Date getEventTime() {
		return eventTime;
	}

	public void setEventTime(Date eventTime) {
		this.eventTime = eventTime;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}
}
