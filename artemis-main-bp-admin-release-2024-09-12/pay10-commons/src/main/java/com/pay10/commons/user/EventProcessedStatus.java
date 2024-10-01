package com.pay10.commons.user;



import javax.persistence.*;
import java.util.Date;


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

	public Long getEventProcessedId() {
		return eventProcessedId;
	}

	public void setEventProcessedId(Long eventProcessedId) {
		this.eventProcessedId = eventProcessedId;
	}

	public EventPayload getEventpayload() {
		return eventpayload;
	}

	public void setEventpayload(EventPayload eventpayload) {
		this.eventpayload = eventpayload;
	}

	public Long getSubscriberId() {
		return subscriberId;
	}

	public void setSubscriberId(Long subscriberId) {
		this.subscriberId = subscriberId;
	}

	public String getWebhookUrl() {
		return webhookUrl;
	}

	public void setWebhookUrl(String webhookUrl) {
		this.webhookUrl = webhookUrl;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Integer getAttempt() {
		return attempt;
	}

	public void setAttempt(Integer attempt) {
		this.attempt = attempt;
	}

	public Integer getResponseCode() {
		return responseCode;
	}

	public void setResponseCode(Integer responseCode) {
		this.responseCode = responseCode;
	}

	public String getResponseMessage() {
		return responseMessage;
	}

	public void setResponseMessage(String responseMessage) {
		this.responseMessage = responseMessage;
	}

	public Integer getResponseTime() {
		return responseTime;
	}

	public void setResponseTime(Integer responseTime) {
		this.responseTime = responseTime;
	}

	public Date getLastAttemptedOn() {
		return lastAttemptedOn;
	}

	public void setLastAttemptedOn(Date lastAttemptedOn) {
		this.lastAttemptedOn = lastAttemptedOn;
	}

	public Date getProcessedOn() {
		return processedOn;
	}

	public void setProcessedOn(Date processedOn) {
		this.processedOn = processedOn;
	}
}
