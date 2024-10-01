package com.crmws.entity;


import java.util.Map;
import org.springframework.http.HttpStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ResponseMessageChargebackDashboard {
	private String responseMessage;
	private HttpStatus httpStatus;
	public Map<String, Long> response;
}
