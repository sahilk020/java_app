package com.pay10.commons.user;

import java.io.Serializable;

/**
 * @author Rajendra
 */

public class CompanyProfileUi  implements Serializable {
	
	public CompanyProfileUi(){
		
	}	
	
	private static final long serialVersionUID = -5829524589073475754L;
	
	private String companyName;
	private String tenantId;
	private String tenantNumber;
	
	public void setCompanyProfileUi(CompanyProfile cp){
		setCompanyName(cp.getCompanyName());
		setTenantId(cp.getTenantId().toString());
		setTenantNumber(cp.getTenantNumber());
	}
	
	public String getCompanyName() {
		return companyName;
	}
	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}
	public String getTenantId() {
		return tenantId;
	}
	public void setTenantId(String tenantId) {
		this.tenantId = tenantId;
	}
	public String getTenantNumber() {
		return tenantNumber;
	}
	public void setTenantNumber(String tenantNumber) {
		this.tenantNumber = tenantNumber;
	}

	
}
