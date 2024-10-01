package com.pay10.crm.action;


import java.util.Map;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.interceptor.ServletRequestAware;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.pay10.commons.api.EmailControllerServiceProvider;
import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.user.MerchantSignupNotifier;
import com.pay10.commons.user.ResponseObject;
import com.pay10.commons.user.User;
import com.pay10.commons.user.UserDao;
import com.pay10.commons.user.UserType;
import com.pay10.commons.util.BusinessType;
import com.pay10.commons.util.CrmFieldConstants;
import com.pay10.commons.util.CrmFieldType;
import com.pay10.commons.util.CrmValidator;
import com.pay10.commons.util.PropertiesManager;
import com.pay10.crm.actionBeans.CreateNewUser;

/**
 * @author Puneet,Neeraj
 *
 */

public class SignupAction extends AbstractSecureAction implements ServletRequestAware{

	@Autowired
	private CreateNewUser createUser;
	
	@Autowired
	private EmailControllerServiceProvider emailControllerServiceProvider;
	
	@Autowired
	private CrmValidator validator;
	
	@Autowired
	private PropertiesManager propertiesManager;
	
	@Autowired
	private UserDao userDao;
	
	private static Logger logger = LoggerFactory.getLogger(SignupAction.class.getName());
	private static final long serialVersionUID = 5995449017764989418L;

	private String emailId;
	private String password;
	private String businessName;
	private String mobile;
	private String otp;
	private String confirmPassword;
//	private String captcha;
	private String userRoleType;
	private String industryCategory;
	private String industrySubCategory;
	ResponseObject responseObject = new ResponseObject();
	private Map<String, String> industryCategoryList = new TreeMap<String, String>();
	private Map<String, String> industrySubCategoryList = new TreeMap<String, String>();
	@Override
	public String execute() {
		
		try {
			
			try {
				String otpFromSession =  (String) sessionMap.get(mobile);
				
				if (!otpFromSession.equalsIgnoreCase(otp)) {
					//addActionMessage("OTP Entered is incorrect");
					addFieldError("invalidOtp", "OTP Entered is incorrect");
					setOtp("");
//					setCaptcha("");
					return INPUT;
				}
			}catch (Exception exception){
				addFieldError("invalidOtp", "Please Generate the OTP ");
				setOtp("");
//				setCaptcha("");
				return INPUT;	
			}

			
			if (userRoleType.equals(CrmFieldConstants.USER_RESELLER_TYPE.getValue())) {
				responseObject = createUser.createUser(getUserInstance(), UserType.RESELLER, "");
			} else {
				responseObject = createUser.createUser(getUserInstance(), UserType.MERCHANT, "");
			}
			if (!ErrorType.SUCCESS.getResponseCode().equals(responseObject.getResponseCode())) {
				addActionMessage(responseObject.getResponseMessage());
				return INPUT;
			}
			// Sending Email for Email Validation
			emailControllerServiceProvider.emailValidator(responseObject);

			if (PropertiesManager.propertiesMap.get("sendEmailOnSignup") != null &&  
					PropertiesManager.propertiesMap.get("sendEmailOnSignupList") != null &&
					PropertiesManager.propertiesMap.get("sendEmailOnSignup").equalsIgnoreCase("Y")) {
				
				String emailList = PropertiesManager.propertiesMap.get("sendEmailOnSignupList");
				User merchant = getUserInstance();
				
				if (emailList.length() > 0 && merchant != null) {
					String emailListArray[] = emailList.split(",");
					
					for (String email : emailListArray) {
						
						MerchantSignupNotifier merchantSignupNotifier = new MerchantSignupNotifier();
						merchantSignupNotifier.setMerchantEmail(merchant.getEmailId());
						merchantSignupNotifier.setMerchantName(merchant.getBusinessName());
						merchantSignupNotifier.setMerchantPhone(merchant.getMobile());
						merchantSignupNotifier.setEmail(email.trim());
						merchantSignupNotifier.setReceiverName(email.trim());
						emailControllerServiceProvider.merchantSignupNotifier(merchantSignupNotifier);
						
					}
					
				}
				
			}
			
			return SUCCESS;
		} catch (Exception exception) {
			logger.error("Exception", exception);
			return ERROR;
		}
	}

	
	private User getUserInstance() {

		try {
			User user = new User();
			user.setEmailId(getEmailId().toLowerCase());
			user.setPassword(getPassword());
			user.setMobile(getMobile());
			user.setBusinessName(getBusinessName());
			user.setPasswordExpired(false); // Set password expiry to false by default.
			if (userRoleType.equals(CrmFieldConstants.USER_RESELLER_TYPE.getValue())) {
			} else {
				user.setIndustryCategory(industryCategory);
				user.setIndustrySubCategory(industrySubCategory);
			}
			return user;
		}

		catch (Exception e) {
			logger.error("Exception in getUserInstance() , exception = " + e);
			return null;

		}

	}

	@Override
	public void validate() {
		String userType = userRoleType;
		// Validate blank and invalid fields
//		if (validator.validateBlankField(getCaptcha())) {
//			addFieldError(CrmFieldType.CAPTCHA.getName(), validator.getResonseObject().getResponseMessage());
//		}
//		else {
//			String sessionCaptcha = (String) sessionMap.get(CaptchaServlet.CAPTCHA_KEY);
//			if (!captcha.equals(sessionCaptcha)) {
//				addFieldError(CrmFieldType.CAPTCHA.getName(), CrmFieldConstants.INVALID_CAPTCHA.getValue());
//				setCaptcha("");
//			}
//		}
		//Clean Session Captcha
//		sessionMap.put(CaptchaServlet.CAPTCHA_KEY,"");
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

		
	    if(!(userType.equalsIgnoreCase(UserType.RESELLER.toString()))){
			if (validator.validateBlankField(getIndustryCategory())) {
				addFieldError(CrmFieldType.INDUSTRY_CATEGORY.getName(), validator.getResonseObject().getResponseMessage());
			} else if (!(validator.validateField(CrmFieldType.INDUSTRY_CATEGORY, getIndustryCategory()))) {
				addFieldError(CrmFieldType.INDUSTRY_CATEGORY.getName(), ErrorType.INVALID_FIELD_VALUE.getResponseMessage());
			}
			if (validator.validateBlankField(getIndustrySubCategory())) {
				addFieldError(CrmFieldType.INDUSTRY_SUB_CATEGORY.getName(), validator.getResonseObject().getResponseMessage());
			} else if (!(validator.validateField(CrmFieldType.INDUSTRY_SUB_CATEGORY, getIndustrySubCategory()))) {
				addFieldError(CrmFieldType.INDUSTRY_SUB_CATEGORY.getName(), ErrorType.INVALID_FIELD_VALUE.getResponseMessage());
			}
			}
		
		
		

		Map<String, String> industryCategoryLinkedMap = BusinessType.getIndustryCategoryList();
		industryCategoryList.putAll(industryCategoryLinkedMap);
		
		 Map<String,String>	industryCategoryLinkedMap1 = BusinessType.getIndustryCategoryList();
		 industrySubCategoryList.putAll(industryCategoryLinkedMap1);
	}
	
	
	

	public Map<String, String> getIndustrySubCategoryList() {
		return industrySubCategoryList;
	}

	public void setIndustrySubCategoryList(Map<String, String> industrySubCategoryList) {
		this.industrySubCategoryList = industrySubCategoryList;
	}

	public String getConfirmPassword() {
		return confirmPassword;
	}

	public void setConfirmPassword(String confirmPassword) {
		this.confirmPassword = confirmPassword;
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

//	public String getCaptcha() {
//		return captcha;
//	}
//
//	public void setCaptcha(String captcha) {
//		this.captcha = captcha;
//	}

	public String getUserRoleType() {
		return userRoleType;
	}

	public void setUserRoleType(String userRoleType) {
		this.userRoleType = userRoleType;
	}

	public String getIndustryCategory() {
		return industryCategory;
	}

	public void setIndustryCategory(String industryCategory) {
		this.industryCategory = industryCategory;
	}

	public String getIndustrySubCategory() {
		return industrySubCategory;
	}

	public void setIndustrySubCategory(String industrySubCategory) {
		this.industrySubCategory = industrySubCategory;
	}

	@Override
	public void setServletRequest(HttpServletRequest request) {
		// TODO Auto-generated method stub

	}

	public Map<String, String> getIndustryCategoryList() {
		return industryCategoryList;
	}

	public void setIndustryCategoryList(Map<String, String> industryCategoryList) {
		this.industryCategoryList = industryCategoryList;
	}


	public String getOtp() {
		return otp;
	}


	public void setOtp(String otp) {
		this.otp = otp;
	}
}
