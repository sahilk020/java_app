package com.pay10.commons.user;

import java.util.Objects;

public class MopTypeDto {

	private String code;
	private String name;
	private String uiName;

	public MopTypeDto() {

	}
	public MopTypeDto(String code, String name, String uiName) {
		super();
		this.code = code;
		this.name = name;
		this.uiName = uiName;
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

	public String getUiName() {
		return uiName;
	}

	public void setUiName(String uiName) {
		this.uiName = uiName;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		MopTypeDto that = (MopTypeDto) o;
		return Objects.equals(code, that.code);
	}

	@Override
	public int hashCode() {
		return Objects.hash(code);
	}

	@Override
	public String toString() {
		return "MopTypeDto{" +
				"code='" + code + '\'' +
				", name='" + name + '\'' +
				", uiName='" + uiName + '\'' +
				'}';
	}
}
