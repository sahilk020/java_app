package com.pay10.webhook.vo;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class EventsProcessedVo {
	
	
	@JsonProperty("webhook_url")
	private String webhookUrl;
	
	@JsonProperty("payload")
	private String payload;
	
	@JsonProperty("status")
	private String status;
	
	@JsonProperty("processed_on")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm",timezone = "Asia/Kolkata")
	private Date processedOn;

}
