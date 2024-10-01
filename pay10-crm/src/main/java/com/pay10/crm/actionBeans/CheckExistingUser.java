package com.pay10.crm.actionBeans;

import org.springframework.stereotype.Service;

import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.user.CompanyProfile;
import com.pay10.commons.user.CompanyProfileDao;
import com.pay10.commons.user.ResponseObject;
import com.pay10.commons.user.User;
import com.pay10.commons.user.UserDao;

/**
 * @author Puneet
 * 
 */

@Service
public class CheckExistingUser {
	
	private User checkedUser = new User();
	private ResponseObject responseObject= new ResponseObject();
	private CompanyProfile cProfile = new CompanyProfile();
	
     public ResponseObject checkuser(String emailId) {
			checkedUser = new UserDao().find(emailId);
			if (null != checkedUser){
				responseObject.setResponseCode(ErrorType.USER_UNAVAILABLE.getResponseCode());
				responseObject.setResponseMessage(ErrorType.USER_UNAVAILABLE.getResponseMessage());
			 } else {
				
				responseObject.setResponseCode(ErrorType.USER_AVAILABLE.getResponseCode());
				responseObject.setResponseMessage(ErrorType.USER_AVAILABLE.getResponseMessage());
				}
			return responseObject;
		}
     
     public ResponseObject checkTenant(String emailId) {
    	 	cProfile = new CompanyProfileDao().getCompanyProfileByEmailId(emailId);
			if (null != cProfile){
				responseObject.setResponseCode(ErrorType.TENANT_AVAILABLE.getResponseCode());
				responseObject.setResponseMessage(ErrorType.TENANT_AVAILABLE.getResponseMessage());
			 } else {
				responseObject.setResponseCode(ErrorType.TENANT_UNAVAILABLE.getResponseCode());
				responseObject.setResponseMessage(ErrorType.TENANT_UNAVAILABLE.getResponseMessage());
			}
			return responseObject;
	}

	public ResponseObject checkTenantByTenantId(String tenantId) {
	 	cProfile = new CompanyProfileDao().getCompanyProfileByTenantId(tenantId);
		if (null != cProfile){
			responseObject.setResponseCode(ErrorType.TENANT_AVAILABLE.getResponseCode());
			responseObject.setResponseMessage(ErrorType.TENANT_AVAILABLE.getResponseMessage());
		 } else {
			responseObject.setResponseCode(ErrorType.TENANT_UNAVAILABLE.getResponseCode());
			responseObject.setResponseMessage(ErrorType.TENANT_UNAVAILABLE.getResponseMessage());
		}
		return responseObject;	
	}
}
