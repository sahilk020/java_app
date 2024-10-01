package com.pay10.crm.action;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger; import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.pay10.commons.api.EmailControllerServiceProvider;
import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.user.CompanyProfile;
import com.pay10.commons.user.CompanyProfileDao;
import com.pay10.commons.user.CompanyProfileUi;
import com.pay10.commons.user.ResponseObject;
import com.pay10.commons.user.User;
import com.pay10.commons.user.UserType;
import com.pay10.commons.util.CrmFieldConstants;
import com.pay10.commons.util.CrmFieldType;
import com.pay10.commons.util.CrmValidator;
import com.pay10.commons.util.States;
import com.pay10.crm.actionBeans.CheckExistingUser;
import com.pay10.crm.actionBeans.CreateNewAdmin;

/**
 * @ Neeraj , Rajendra
 */

public class SignupAdminAction extends AbstractSecureAction {


	@Autowired
	private EmailControllerServiceProvider emailControllerServiceProvider;
	
	@Autowired
	private CrmValidator validator;
	
	@Autowired
	CreateNewAdmin createUser;
	
	private static final long serialVersionUID = 908867511652353218L;
	private static Logger logger = LoggerFactory.getLogger(SignupAdminAction.class.getName());
	private String emailId;
	private String password;
	private String businessName;
	private String mobile;
	private String confirmPassword;
	private String tenantId;
	private String tenantNumber;
	private String companyName;
	
	private List<CompanyProfileUi> companyList = new ArrayList<CompanyProfileUi>();
	
	@Override
	public String execute() {
		
		ResponseObject responseObject = new ResponseObject();
		try {
			CheckExistingUser checkExistingUser = new CheckExistingUser();
			responseObject = checkExistingUser.checkTenantByTenantId(getTenantId());
			if(ErrorType.TENANT_AVAILABLE.getResponseCode().equals(responseObject.getResponseCode())) {
				responseObject = createUser.createUser(getUserInstance(), UserType.ADMIN);
				if (!ErrorType.SUCCESS.getResponseCode().equals(responseObject.getResponseCode())) {
					addActionMessage(responseObject.getResponseMessage());

				}
				if (ErrorType.SUCCESS.getResponseCode().equals(responseObject.getResponseCode())) {
					addActionMessage("User successfully registered.");
				}
				// Sending Email for Email Validation
				emailControllerServiceProvider.emailValidator(responseObject);
								
			}else {
				addActionMessage("Tenant is Not available, Please Enter valid Tenent ID");
			}
			companyList = new CompanyProfileDao().getAllCompanyProfileList();
			return SUCCESS;
			
		} catch (Exception exception) {
			logger.error("Exception", exception);
			return ERROR;
		}
	}
	
	


	//to provide default Company Name
	public String getDefaultCompanyName(){
		if(StringUtils.isBlank(companyName)){
			return "ALL";
		}else{
			if(companyList.size() != 0) {
				for (CompanyProfileUi cpu : companyList) {
					if(cpu.getCompanyName().equalsIgnoreCase(companyName)) {
						return companyName;										
					}
				}
			}
			return "ALL";
		}
	}
	
	private User getUserInstance() {
		
		try{
			
			User user = new User();
			user.setEmailId(getEmailId());
			user.setPassword(getPassword());
			user.setMobile(getMobile());
			user.setBusinessName(getBusinessName());
			user.setTenantId(Long.parseLong(getTenantId()));
			user.setCompanyName(getCompanyName());
			user.setTenantNumber(getTenantNumber());
			return user;
		}
		
		catch(Exception e){
			logger.error("Signup admin failed " + e);
			return null;
		}
	
	}


	@Override
	public void validate() {

		// Validate blank and invalid fields
		if (validator.validateBlankField(getPassword())) {
			addFieldError(CrmFieldType.PASSWORD.getName(), validator.getResonseObject().getResponseMessage());
			if (validator.validateBlankField(getConfirmPassword())) {
				addFieldError(CrmFieldConstants.CONFIRM_PASSWORD.getValue(),
						validator.getResonseObject().getResponseMessage());
			}
		} else if (validator.validateBlankField(getConfirmPassword())) {
			addFieldError(CrmFieldConstants.CONFIRM_PASSWORD.getValue(),
					validator.getResonseObject().getResponseMessage());
		} else if (!(getPassword().equals(getConfirmPassword()))) {
			addFieldError(CrmFieldType.PASSWORD.getName(), ErrorType.PASSWORD_MISMATCH.getResponseMessage());
		} else if (!(validator.isValidPasword(getPassword()))) {
			addFieldError(CrmFieldType.PASSWORD.getName(), ErrorType.INVALID_FIELD_VALUE.getResponseMessage());
		}

		if ((validator.validateBlankField(getBusinessName()))) {
			addFieldError(CrmFieldType.BUSINESS_NAME.getName(), validator.getResonseObject().getResponseMessage());
		} else if (!(validator.validateField(CrmFieldType.BUSINESS_NAME, getBusinessName()))) {
			addFieldError(CrmFieldType.BUSINESS_NAME.getName(), validator.getResonseObject().getResponseMessage());
		}

		if (validator.validateBlankField(getMobile())) {
			addFieldError(CrmFieldType.MOBILE.getName(), validator.getResonseObject().getResponseMessage());
		} else if (!(validator.validateField(CrmFieldType.MOBILE, getMobile()))) {
			addFieldError(CrmFieldType.MOBILE.getName(), validator.getResonseObject().getResponseMessage());
		}

		if (validator.validateBlankField(getEmailId())) {
			addFieldError(CrmFieldType.EMAILID.getName(), validator.getResonseObject().getResponseMessage());
		} else if (!(validator.isValidEmailId(getEmailId()))) {
			addFieldError(CrmFieldType.EMAILID.getName(), ErrorType.INVALID_FIELD_VALUE.getResponseMessage());
		}
	}

	public List<CompanyProfileUi> getCompanyList() {
		return companyList;
	}

	public void setCompanyList(List<CompanyProfileUi> companyList) {
		this.companyList = companyList;
	}
	
	public String getEmailId() {
		return emailId;
	}

	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getTenantNumber() {
		return tenantNumber;
	}
	public void setTenantNumber(String tenantNumber) {
		this.tenantNumber = tenantNumber;
	}
	public String getCompanyName() {
		return companyName;
	}
	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}
	
	public String getBusinessName() {
		return businessName;
	}

	public void setBusinessName(String businessName) {
		this.businessName = businessName;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getConfirmPassword() {
		return confirmPassword;
	}

	public void setConfirmPassword(String confirmPassword) {
		this.confirmPassword = confirmPassword;
	}
	
	public String getTenantId() {
		return tenantId;
	}
	public void setTenantId(String tenantId) {
		this.tenantId = tenantId;
	}

	
	
	

}
