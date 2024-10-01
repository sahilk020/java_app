package com.pay10.commons.util;

import java.io.Serializable;
import java.util.Date;

import org.springframework.stereotype.Service;

import com.pay10.commons.user.User;

/**
 * @author shashi
 *
 */

@Service
public class SubAdmin implements Serializable {

	private static final long serialVersionUID = -5371568875441496496L;

	public SubAdmin() {

	}

	private String agentEmailId;
	private String agentFirstName;
	private String agentLastName;
	private String payId;
	private String agentExperience;
	private String agentMobile;
	private Boolean agentIsActive;
	private String groupName;
	private String segment;
	private String registrationDate;

	public void setSubAdmin(User user) {
		setAgentEmailId(user.getEmailId());
		setAgentFirstName(user.getFirstName());
		setAgentLastName(user.getLastName());
		setPayId(user.getPayId());

	}

	public String getSegment() {
		return segment;
	}
	
	public void setSegment(String segment) {
		this.segment = segment;
	}
	
	public String getAgentEmailId() {
		return agentEmailId;
	}

	public void setAgentEmailId(String agentEmailId) {
		this.agentEmailId = agentEmailId;
	}

	public String getAgentFirstName() {
		return agentFirstName;
	}

	public void setAgentFirstName(String agentFirstName) {
		this.agentFirstName = agentFirstName;
	}

	public String getAgentLastName() {
		return agentLastName;
	}

	public void setAgentLastName(String agentLastName) {
		this.agentLastName = agentLastName;
	}

	public String getAgentExperience() {
		return agentExperience;
	}

	public void setAgentExperience(String agentExperience) {
		this.agentExperience = agentExperience;
	}

	public String getAgentMobile() {
		return agentMobile;
	}

	public void setAgentMobile(String agentMobile) {
		this.agentMobile = agentMobile;
	}

	public Boolean getAgentIsActive() {
		return agentIsActive;
	}

	public void setAgentIsActive(Boolean agentIsActive) {
		this.agentIsActive = agentIsActive;
	}

	public String getPayId() {
		return payId;
	}

	public void setPayId(String payId) {
		this.payId = payId;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public String getRegistrationDate() {
		return registrationDate;
	}

	public void setRegistrationDate(String registrationDate) {
		this.registrationDate = registrationDate;
	}
}
