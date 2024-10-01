package com.pay10.crm.action;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger; import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.pay10.commons.api.SmsSender;
import com.pay10.commons.user.LoginOtp;
import com.pay10.commons.user.LoginOtpDao;
import com.pay10.commons.user.User;
import com.pay10.commons.util.CrmFieldType;
import com.pay10.commons.util.CrmValidator;

public class LoginOtpAction extends AbstractSecureAction {
	

	@Autowired
	private CrmValidator validator;
	
	@Autowired
	private SmsSender smsSender;

	@Autowired
	private LoginOtpDao otpDao;
	
	
	private static Logger logger = LoggerFactory.getLogger(LoginOtpAction.class.getName());
	private static final long serialVersionUID = -1271576830462438772L;
	
	private String emailId;
	private String response;
	
	
	@Override
	public String execute() {
		
		logger.info("inside LoginOtpAction");
		try {
			LoginOtp otp = new LoginOtp();
			DateFormat df = new SimpleDateFormat("dd/MM/yy HH:mm:ss");
			Calendar calobj = Calendar.getInstance();
			Calendar calobj1 = Calendar.getInstance();
			String currentDate=df.format(calobj1.getTime());
			calobj.add(Calendar.MINUTE, 15);
			String expDate = df.format(calobj.getTime());
			User userModel = otpDao.getUserData(emailId);
			int num = (int) ((Math.random() * 99999) + 100000);
			String val = Integer.toString(num);
			try {
		     if (StringUtils.isBlank(userModel.getMobile()) || (userModel.getMobile().length() > 10)) {
		    	    setResponse("Invalid Mobile Number");
					return INPUT;
				}
			LoginOtp loginOtp = otpDao.checkOtp(userModel.getEmailId());
			if(loginOtp!=null)
			{
			    String expiryDate=loginOtp.getExpiryDate();
			    Date d1 = null;
			    Date d2 = null;
			    try {
			        d1 = df.parse(currentDate);
			        d2 = df.parse(expiryDate);
			    } catch (Exception e) {
			        e.printStackTrace();
			    }

		
			    long diff = d2.getTime() - d1.getTime();
			    long diffMinutes = diff / (60 * 1000) % 60;
			    long diffHours = diff / (60 * 60 * 1000);
		
               if(diffHours>=0 && diffMinutes>=0)
               {
            	if (loginOtp.getStatus().equals("Active")) {
            		
                String otpMessage=maskString(userModel.getMobile(), 2, 6, '*');
                String message = "One Time Password for Pay10 CRM is " + loginOtp.getOtp();
				message += " .Please use this OTP to Login.Please do not share your OTP with anyone.";
				String response=smsSender.sendSMS(userModel.getMobile(), message);
				if(response==null)
				{
					setResponse("Unable to send OTP");
					return INPUT;
				}
   			    setResponse("OTP has been sent to "+otpMessage);
   				return INPUT;
               }
               }
               else
               {
				loginOtp.setStatus("InActive");
				otpDao.update(loginOtp);
				otp.setPayId(userModel.getPayId());
				otp.setMobileNo(userModel.getMobile());
				otp.setOtp(val);
				otp.setMessage("Pay10 team");
				otp.setExpiryDate(expDate);
				otp.setStatus("Active");
				otp.setEmailId(emailId);
				otp.setCreateDate(currentDate);
				otp.setUpdateDate(currentDate);
				otp.setRequestedBy(emailId);
				otpDao.create(otp);
				String otpMessage=maskString(userModel.getMobile(), 2, 6, '*');
				String message = "One Time Password for Pay10 CRM is " + val;
				message += " .Please use this OTP to Login.Please do not share your OTP with anyone.";
				String response=smsSender.sendSMS(userModel.getMobile(), message);
				if(response==null)
				{
					setResponse("Unable to send OTP");
					return INPUT;
				}
				setResponse("OTP has been sent to "+otpMessage);
				return INPUT;
			}
			}
			else
			{
				otp.setPayId(userModel.getPayId());
				otp.setMobileNo(userModel.getMobile());
				otp.setOtp(val);
				otp.setMessage("Pay10 team");
				otp.setExpiryDate(expDate);
				otp.setStatus("Active");
				otp.setEmailId(emailId);
				otp.setCreateDate(currentDate);
				otp.setRequestedBy(emailId);
				otpDao.create(otp);
				String otpMessage=maskString(userModel.getMobile(), 2, 6, '*');
				String message = "One Time Password for Pay10 CRM is " + val;
				message += " .Please use this OTP to Login.Please do not share your OTP with anyone.";
				String response=smsSender.sendSMS(userModel.getMobile(), message);
				if(response==null)
				{
					setResponse("Unable to send OTP");
					return INPUT;
					
				}
				setResponse("OTP has been sent to "+otpMessage);
				return SUCCESS;
			}
			}
			catch (Exception ex) {
				otp.setPayId(userModel.getPayId());
				otp.setMobileNo(userModel.getMobile());
				otp.setOtp(val);
				otp.setMessage("Pay10 team");
				otp.setExpiryDate(expDate);
				otp.setStatus("Active");
				otp.setEmailId(emailId);
				otp.setCreateDate(currentDate);
				otp.setUpdateDate(currentDate);
				otp.setRequestedBy(emailId);
				otpDao.create(otp);
				String otpMessage=maskString(userModel.getMobile(), 2, 6, '*');
				String message = "One Time Password for Pay10 CRM is " + val;
				message += " .Please use this OTP to Login.Please do not share your OTP with anyone.";
				String response=smsSender.sendSMS(userModel.getMobile(), message);
				if(response==null)
					
				{
					setResponse("Unable to send OTP");
					return INPUT;
				}
				setResponse("OTP has been sent to "+otpMessage);
				return SUCCESS;
			}
			
		    } catch (Exception ex) {
		    	setResponse("Invalid User");
			    logger.info("inside login otp action get error message",ex);
			    return INPUT;
		}
		
		
           return SUCCESS;
	}
	
	@Override
	public void validate() {
		

		if ((validator.validateBlankFields(getEmailId()))) {
			addFieldError(CrmFieldType.EMAILID.getName(), validator.getResonseObject().getResponseMessage());
			return;
		} 
		
		String counterKey = "counter";
		if (!sessionMap.containsKey(counterKey)) {
			logger.info("Inserting counter key");
			sessionMap.put(counterKey, 0);
		}
		if (getFieldErrors().isEmpty()) {
			int counter = Integer.parseInt(sessionMap.get(counterKey).toString()) + 1;
			logger.info("Counter : " + counter);
			sessionMap.put(counterKey, counter);
			if (counter > 5) {
				setResponse("Limit for OTP generation exceeded. Please try again after 15 minutes.");
				addFieldError("OTP_LIMIT", "Limit exceeded");
				sessionMap.put(counterKey, 5);
			}
		}

	}
	

	
	 private static String maskString(String strText, int start, int end, char maskChar) 
		        throws Exception{
		        
		        if(strText == null || strText.equals(""))
		            return "";
		        
		        if(start < 0)
		            start = 0;
		        
		        if( end > strText.length())
		            end = strText.length();
		            
		        if(start > end)
		            throw new Exception("End index cannot be greater than start index");
		        
		        int maskLength = end - start;
		        
		        if(maskLength == 0)
		            return strText;
		        
		        StringBuilder sbMaskString = new StringBuilder(maskLength);
		        
		        for(int i = 0; i < maskLength; i++){
		            sbMaskString.append(maskChar);
		        }
		        return strText.substring(0, start) 
		            + sbMaskString.toString() 
		            + strText.substring(start + maskLength);
		    }
	
	
	public String getEmailId() {
		return emailId;
	}

	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}

	public String getResponse() {
		return response;
	}

	public void setResponse(String response) {
		this.response = response;
	}
	
	
	
	
	

}
