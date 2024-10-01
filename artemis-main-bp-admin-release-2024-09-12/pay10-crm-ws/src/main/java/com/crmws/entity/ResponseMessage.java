package com.crmws.entity;

import java.util.List;
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
public class ResponseMessage {

	private String respmessage;
	private HttpStatus httpStatus;
	public Map<String, List<String>> validationResult;
	
	public ResponseMessage(String respmessage, HttpStatus httpStatus) {
		super();
		this.respmessage = respmessage;
		this.httpStatus = httpStatus;
	}
	
	
}
