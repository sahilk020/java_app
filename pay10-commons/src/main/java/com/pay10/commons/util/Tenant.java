package com.pay10.commons.util;

import java.io.Serializable;

import org.springframework.stereotype.Service;

import com.pay10.commons.user.CompanyProfile;
import com.pay10.commons.user.User;

/**
 * @author Rajendra
 *
 */

@Service
public class Tenant  implements Serializable {
	
	private static final long serialVersionUID = -5371568875441496496L;

	public Tenant() {

	}

	private String tenantId;
	private String tenantCompanyName;
	private String tenantMobile;
	private String tenantEmailId;
	private String tenantPgUrl;
	private String tenantStatus;
	
	public void Tenant(CompanyProfile cp) {
		setTenantId(cp.getTenantId().toString());
		setTenantCompanyName(cp.getCompanyName());
		setTenantMobile(cp.getMobile());
		setTenantEmailId(cp.getEmailId());
		setTenantPgUrl(cp.getPgUrl());
		setTenantStatus(cp.getTenantStatus());
	}

	public String getTenantEmailId() {
		return tenantEmailId;
	}

	public void setTenantEmailId(String tenantEmailId) {
		this.tenantEmailId = tenantEmailId;
	}

	public String getTenantId() {
		return tenantId;
	}

	public void setTenantId(String tenantId) {
		this.tenantId = tenantId;
	}

	public String getTenantMobile() {
		return tenantMobile;
	}

	public void setTenantMobile(String tenantMobile) {
		this.tenantMobile = tenantMobile;
	}

	public String getTenantCompanyName() {
		return tenantCompanyName;
	}

	public void setTenantCompanyName(String tenantCompanyName) {
		this.tenantCompanyName = tenantCompanyName;
	}

	public String getTenantPgUrl() {
		return tenantPgUrl;
	}

	public void setTenantPgUrl(String tenantPgUrl) {
		this.tenantPgUrl = tenantPgUrl;
	}

	public String getTenantStatus() {
		return tenantStatus;
	}

	public void setTenantStatus(String tenantStatus) {
		this.tenantStatus = tenantStatus;
	}


	
}
