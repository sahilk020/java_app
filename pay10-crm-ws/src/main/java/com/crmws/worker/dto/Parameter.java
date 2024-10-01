package com.crmws.worker.dto;


import javax.validation.constraints.NotEmpty;

import com.crmws.worker.constant.ParameterDataType;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class Parameter {
	
	@NotEmpty private String key;
	
	private boolean mandatory = true;
	
	private Object defaultValue;
	
	private ParameterDataType dataType;
	
	public static Parameter initialize(
		      String key,
		      boolean mandatory,
		      Object defaultValue,
		      ParameterDataType dataType
		      )
	{
		return new Parameter(key,mandatory,defaultValue,dataType);
	}

	private Parameter(@NotEmpty String key, boolean mandatory, Object defaultValue, ParameterDataType dataType) {
		super();
		this.key = key;
		this.mandatory = mandatory;
		this.defaultValue = defaultValue;
		this.dataType = dataType;
	}

	private Parameter() {
		super();
	}
	
	

}
