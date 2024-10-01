package com.pay10.crm.action;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.pay10.commons.api.SmsSender;
import com.pay10.commons.util.CrmFieldType;
import com.pay10.commons.util.CrmValidator;

public class GenerateSignupOtpAction extends AbstractSecureAction {

	@Autowired
	private CrmValidator validator;

	@Autowired
	private SmsSender smsSender;

	private static Logger logger = LoggerFactory.getLogger(LoginOtpAction.class.getName());
	private static final long serialVersionUID = -8550757805728588270L;

	private String mobile;
	private String response;

	@Override
	public String execute() {

		logger.info("Inside Generate Signup OTP");
		try {
			int num = (int) ((Math.random() * 99999) + 100000);
			String otp = Integer.toString(num);

			String message = "OTP for Merchant Signup on Pay10 is " + otp;
			logger.info(message);
			smsSender.sendSMS(mobile, message);
			sessionMap.put(mobile, otp);
			setResponse("OTP Sent to mobile number " + mobile);
			return SUCCESS;

		} catch (Exception ex) {
			setResponse("Unable to generate OTP");
			logger.error("inside the forgetpin action get error message");
			logger.error(ex.getMessage());
			return INPUT;
		}

	}

	@Override
	public void validate() {
		String counterKey = "counter";
		if ((validator.validateBlankFields(getMobile()))) {
			addFieldError(CrmFieldType.MOBILE.getName(), validator.getResonseObject().getResponseMessage());
			setResponse("Invalid Mobile number");
			setMobile(null);
		} else if (!(validator.validateField(CrmFieldType.MOBILE, getMobile()))) {
			addFieldError(CrmFieldType.MOBILE.getName(), validator.getResonseObject().getResponseMessage());
			setResponse("Invalid Mobile number.");
			setMobile(null);
		}

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

	public String getResponse() {
		return response;
	}

	public void setResponse(String response) {
		this.response = response;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

}
