package com.crmws.entity;

import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;

import com.pay10.commons.dto.GRS;

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
public class ResponseMessageForGRS {

	private String respmessage;
	private HttpStatus httpStatus;
	public List<GRS> grs;
}
