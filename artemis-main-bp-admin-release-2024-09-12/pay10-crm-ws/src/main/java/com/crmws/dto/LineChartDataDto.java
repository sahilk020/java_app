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
public class LineChartDataDto {
	
	@JsonProperty("date")
	private String date;
	
	@JsonProperty("percentage")
	private Long percentage;
	
	@JsonProperty("totalFieldCount")
	private Long totalFieldCount;
	
	@JsonProperty("totalCount")
	private Long totalCount;
	
	
	
	

}
