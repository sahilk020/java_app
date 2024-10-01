package com.pay10.webhook.model;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "event_processed_status")
public class EventProcessedStatus {

	@Id
    @GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="event_processed_id")
	private Long eventProcessedId;
	
	@OneToOne
    @JoinColumn(name = "event_payload_id")
	private EventPayload eventpayload;
	
	@Column(name="subscriber_id")
	private Long subscriberId;
	
	@Column(name="webhook_url")
	private String webhookUrl;
	
	@Column(name="status")
	private String status;
	
	@Column(name="attempt")
	private Integer attempt;
	
	@Column(name="response_code")
	private Integer responseCode;
	
	@Column(name="responseMessage")
	private String responseMessage;
	
	@Column(name="response_time")
	private Integer responseTime;
	
	@Column(name="last_attempted_on")
	@Temporal(TemporalType.TIMESTAMP)
	private Date lastAttemptedOn;
	
	@Column(name="processed_on")
	@Temporal(TemporalType.TIMESTAMP)
	private Date processedOn;
	
}
