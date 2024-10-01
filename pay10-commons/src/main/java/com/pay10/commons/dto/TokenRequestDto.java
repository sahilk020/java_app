package com.pay10.commons.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TokenRequestDto {
	
	
	@JsonProperty("key") 
	private String key;
	
	@JsonProperty("command")
	private String command;
	
	@JsonProperty("hash")
	private String hash;
	
	@JsonProperty("var1")
	private String var1;
	
	@JsonProperty("var2")
	private String var2;
	
	@JsonProperty("var3")
	private String var3;
	
	@JsonProperty("var4")
	private String var4;
	
	@JsonProperty("var5")
	private String var5;
	
	@JsonProperty("var6")
	private String var6;
	
	@JsonProperty("var7")
	private String var7;
	
	@JsonProperty("var8")
	private String var8;

	public String getCommand() {
		return command;
	}

	public void setCommand(String command) {
		this.command = command;
	}

	public String getHash() {
		return hash;
	}

	public void setHash(String hash) {
		this.hash = hash;
	}

	public String getVar1() {
		return var1;
	}

	public void setVar1(String var1) {
		this.var1 = var1;
	}

	public String getVar2() {
		return var2;
	}

	public void setVar2(String var2) {
		this.var2 = var2;
	}

	public String getVar3() {
		return var3;
	}

	public void setVar3(String var3) {
		this.var3 = var3;
	}

	public String getVar4() {
		return var4;
	}

	public void setVar4(String var4) {
		this.var4 = var4;
	}

	public String getVar5() {
		return var5;
	}

	public void setVar5(String var5) {
		this.var5 = var5;
	}

	public String getVar6() {
		return var6;
	}

	public void setVar6(String var6) {
		this.var6 = var6;
	}

	public String getVar7() {
		return var7;
	}

	public void setVar7(String var7) {
		this.var7 = var7;
	}

	public String getVar8() {
		return var8;
	}

	public void setVar8(String var8) {
		this.var8 = var8;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	@Override
	public String toString() {
		return "TokenRequestDto [key=" + key + ", command=" + command + ", hash=" + hash + ", var1=" + var1 + ", var2="
				+ var2 + ", var3=" + var3 + ", var4=" + var4 + ", var5=" + var5 + ", var6=" + var6 + ", var7=" + var7
				+ ", var8=" + var8 + "]";
	}

	
	

}
