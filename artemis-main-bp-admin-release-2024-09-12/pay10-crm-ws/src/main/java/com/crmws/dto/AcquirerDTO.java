package com.crmws.dto;

public class AcquirerDTO {

	private String code;
	private String name;

	public AcquirerDTO() {
	}

	public AcquirerDTO(String code, String name) {
		this.code = code;
		this.name = name;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
