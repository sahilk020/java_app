package com.crmws.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.pay10.commons.user.Status;
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
public class LineChartRespDto {
	
	
	@JsonProperty("label")
	private String label;
	
	@JsonProperty("data")
	private List<LineChartDataDto> data;
	
	@JsonProperty("dateArray")
	private List<String> dateArray;

}
