package com.crmws.dto;



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
public class ChargeBackReasonDTO {
	private long id;
	private String cbReasonCode;
	private String cbReasonDescription;
}
