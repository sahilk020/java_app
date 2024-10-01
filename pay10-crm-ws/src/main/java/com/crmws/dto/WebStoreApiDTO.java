package com.crmws.dto;

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
public class WebStoreApiDTO {
	
	private String pay_id;
	private String salt;
	private String merchant_hosted_key;
	private String merchant_name;

}
