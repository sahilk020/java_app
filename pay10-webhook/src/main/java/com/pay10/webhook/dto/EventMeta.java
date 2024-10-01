package com.pay10.webhook.dto;

import java.util.Date;

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
public class EventMeta {

	@JsonProperty("name")
	private String name;
	
	@JsonProperty("type")
	private String type;
	
	@JsonProperty("category")
	private String category;
	
	@JsonProperty("version")
	private Integer version;
	
	@JsonProperty("associationId")
	private String associationId;
	
	@JsonProperty("trace_id")
	private String traceId;
	
	@JsonProperty("timestamp")
	private Date eventTime;
}
