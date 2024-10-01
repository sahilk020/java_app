package com.pay10.crm.action;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import org.slf4j.Logger; import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.pay10.commons.api.EmailControllerServiceProvider;
import com.pay10.commons.user.ForgetPin;
import com.pay10.commons.user.LoginOtpDao;
import com.pay10.commons.user.ResponseObject;
import com.pay10.commons.user.User;
import com.pay10.commons.util.CrmFieldConstants;
import com.pay10.commons.util.CrmFieldType;
import com.pay10.commons.util.CrmValidator;

public class ValidateOtpAction extends AbstractSecureAction {

	private static Logger logger = LoggerFactory.getLogger(ValidateOtpAction.class.getName());
	private static final long serialVersionUID = 622840002482852289L;

	@Autowired
	private CrmValidator validator;

	@Autowired
	private LoginOtpDao otpDao;
	
	@Autowired
	private EmailControllerServiceProvider emailControllerServiceProvider;
	
	private String response;
	private String emailId;
	private String captcha;
	private String captchaCode;
	private String otp;

	@Override
	public String execute() {
		logger.info("inside validate otp action !!");
		ResponseObject responseObject = new ResponseObject();
		try {
			
			 User userModel = otpDao.getUserData(emailId);	
			 try
			 {
			 ForgetPin loginOtp=otpDao.checkExpirePasswordOtp(otp,emailId);
						
			 if(loginOtp!=null)
			 {
				 DateFormat df = new SimpleDateFormat("dd/MM/yy HH:mm:ss");
				 Calendar calobj = Calendar.getInstance();
				 String startTime=df.format(calobj.getTime());
				 String expiryTime=loginOtp.getExpiryDate();
				    Date d1 = null;
				    Date d2 = null;
				    try {
				        d1 = df.parse(startTime);
				        d2 = df.parse(expiryTime);
				    } catch (Exception e) {
				        e.printStackTrace();
				    }
				    long diff = d2.getTime() - d1.getTime();
				    long diffMinutes = diff / (60 * 1000) % 60;
				    long diffHours = diff / (60 * 60 * 1000);
				    if(diffHours>=0 && diffMinutes>=0)
		               {  	
				    	addFieldError(CrmFieldType.EMAILID.getName(), responseObject.getResponseMessage());		
				    	loginOtp.setStatus("InActive");
						otpDao.updatePin(loginOtp);
						sessionMap.put("payId",userModel.getPayId());
						return SUCCESS;
	                   }
				    else
				    {
				        loginOtp.setStatus("InActive");
						otpDao.updatePin(loginOtp);
				    	addFieldError(CrmFieldType.CAPTCHA.getName(),"OTP has been expired !!");		
				        return INPUT;
				    }
			     }
			  else
			     {
				  emailControllerServiceProvider.passwordResetSecurityAlertEmail(userModel);
				  addFieldError(CrmFieldType.CAPTCHA.getName(),"Please enter Correct OTP");
				  setCaptcha("");
			      setOtp("");
			      return INPUT;
			     }
			 }
			 catch (Exception ex) {
				 emailControllerServiceProvider.passwordResetSecurityAlertEmail(userModel);
				 addFieldError(CrmFieldType.CAPTCHA.getName(),"Please enter Correct OTP");
				 setCaptcha("");
			     setOtp("");
			     return INPUT;
			}
			
			
		    } catch (Exception e) {
		    	addFieldError(CrmFieldType.EMAILID.getName(),"Invalid User");
		    	setCaptcha("");
		    	setOtp("");
		    	setResponse("Invalid User");
			    logger.info("get error message");
			    return INPUT;
		}	
           
	}
	
	
	
	@Override
	public void validate() {
		
//		if (validator.validateBlankField(getCaptcha())) {
//			addFieldError(CrmFieldType.CAPTCHA.getName(), validator.getResonseObject().getResponseMessage());
//		} else {
//			String sessionCaptcha = (String) sessionMap.get(CaptchaServlet.CAPTCHA_KEY);
//			if (!captcha.equalsIgnoreCase(sessionCaptcha)) {
//				setCaptcha("");
//				addFieldError(CrmFieldType.CAPTCHA.getName(), CrmFieldConstants.INVALID_CAPTCHA_TEXT.getValue());
//
//			}
//		}

		if ((validator.validateBlankFields(getEmailId()))) {
			addFieldError(CrmFieldType.EMAILID.getName(), validator.getResonseObject().getResponseMessage());
			return;
		}

		if (validator.validateBlankFields(getOtp())) {
			addFieldError(CrmFieldType.OTP.getName(), validator.getResonseObject().getResponseMessage());
			return;
		}

	}

	
	public LoginOtpDao getOtpDao() {
		return otpDao;
	}



	public void setOtpDao(LoginOtpDao otpDao) {
		this.otpDao = otpDao;
	}



	public String getResponse() {
		return response;
	}



	public void setResponse(String response) {
		this.response = response;
	}



	public String getEmailId() {
		return emailId;
	}



	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}



	public String getCaptcha() {
		return captcha;
	}



	public void setCaptcha(String captcha) {
		this.captcha = captcha;
	}



	public String getCaptchaCode() {
		return captchaCode;
	}



	public void setCaptchaCode(String captchaCode) {
		this.captchaCode = captchaCode;
	}



	public String getOtp() {
		return otp;
	}



	public void setOtp(String otp) {
		this.otp = otp;
	}


}
