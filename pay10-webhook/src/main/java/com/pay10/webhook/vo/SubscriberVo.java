package com.pay10.webhook.vo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class SubscriberVo {
	
	@JsonProperty("subscriber_id")
	private Long subscriberId;
	
	@JsonProperty("association_id")
	private String associationId;
	
	@JsonProperty("pay_in_url")
	private String payInUrl;

	@JsonProperty("pay_out_url")
	private String payOutUrl;

	//setting default value as 1 as only one event is live
	@JsonProperty("event_id")
	private List<Long> eventId;
	
	@JsonProperty("active")
	private boolean active;


}
