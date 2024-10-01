package com.crmws.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
@AllArgsConstructor
public class LineChartReqDto {

	
	@JsonProperty("range")
	private String range;
	
	@JsonProperty("acquirer")
	private String acquirer;
	
	@JsonProperty("paymentType")
	private String paymentType;
	
	@JsonProperty("mop")
	private String mop;
	
	@JsonProperty("status")
	private String status;
	
	@JsonProperty("statusType")
	private String statusType;
	
	@JsonProperty("type")
	private LineChartType type;
	
	// Added By Sweety
	@JsonProperty("payId")
	private String payId;


}
