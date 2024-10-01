package com.pay10.notification.txnpush.pushtxnemailbuilder;
import org.slf4j.Logger; import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.pay10.commons.user.UserDao;
import com.pay10.notification.txnpush.pushtxnemailbodycreater.PushTxnEmailBodyCreator;
import com.pay10.notification.txnpush.pushtxnemailer.PushTxnEmailer;

@Service
public class PushTxnEmailBuilder {
	private Logger logger = LoggerFactory.getLogger(PushTxnEmailBuilder.class.getName());
	private String body;
	private String subject;
	private String toEmail;
	private String emailToBcc;
	private boolean emailExceptionHandlerFlag;
	private StringBuilder responseMessage = new StringBuilder();

	@Autowired
	@Qualifier("userDao")
	private UserDao userDao;

	@Autowired
	private PushTxnEmailBodyCreator pushTxnEmailBodyCreator;
	
	@Autowired
	PushTxnEmailer pushTxnEmailer;
	
	public void updateRequestNotification(NotificationDetail notificationDetail) {
		try {
		       String messageBody = pushTxnEmailBodyCreator.updateRequestNotificationEmail(notificationDetail.getMessage());
		       pushTxnEmailer.sendEmail(messageBody, notificationDetail.getSubject() ,notificationDetail.getNotifierEmailId(), "", isEmailExceptionHandlerFlag()); // Sending
		} catch (Exception exception) {
			logger.debug("Exception", exception);
		}
	}
	public void updateApproveRejectNotificationForServiceTax(NotificationDetail notificationDetail) {
		try {
			String messageBody = pushTxnEmailBodyCreator.updateApproveRejectNotificationEmail(notificationDetail.getMessage());
			pushTxnEmailer.sendEmail(messageBody, notificationDetail.getSubject() ,notificationDetail.getNotifierEmailId(), "", isEmailExceptionHandlerFlag()); // Sending
		} catch (Exception exception) {
			logger.debug("Exception", exception);
		}
		
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getToEmail() {
		return toEmail;
	}

	public void setToEmail(String toEmail) {
		this.toEmail = toEmail;
	}

	public boolean isEmailExceptionHandlerFlag() {
		return emailExceptionHandlerFlag;
	}

	public void setEmailExceptionHandlerFlag(boolean emailExceptionHandlerFlag) {
		this.emailExceptionHandlerFlag = emailExceptionHandlerFlag;

	}

	public String getEmailToBcc() {
		return emailToBcc;
	}

	public void setEmailToBcc(String mailBcc) {
		this.emailToBcc = mailBcc;
	}

	public StringBuilder getResponseMessage() {
		return responseMessage;
	}

	public void setResponseMessage(StringBuilder responseMessage) {
		this.responseMessage = responseMessage;
	}

	



	

}



