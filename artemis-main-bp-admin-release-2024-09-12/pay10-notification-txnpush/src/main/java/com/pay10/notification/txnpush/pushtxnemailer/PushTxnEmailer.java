package com.pay10.notification.txnpush.pushtxnemailer;

import java.util.Date;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger; import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.exception.SystemException;
import com.pay10.commons.util.PropertiesManager;

@Component
public class PushTxnEmailer {

	private  Logger logger = LoggerFactory.getLogger(PushTxnEmailer.class.getName());

	@Autowired
	@Qualifier("propertiesManager")
	private PropertiesManager propertiesManager;

	Properties properties = new Properties();

	{
		properties.put("mail.smtp.host", "smtp.office365.com");
		properties.put("mail.smtp.auth", "true");
		properties.put("mail.smtp.port", "25");
		properties.put("mail.smtp.starttls.enable", "true");
	}

	public void sendEmail(String body, String subject, String mailTo, String mailBcc, boolean emailExceptionHandlerFlag)
			throws Exception {

		if (StringUtils.isBlank(mailTo) && StringUtils.isBlank(mailBcc)) {
			return;
		}
		String mailSender = propertiesManager.getEmailProperty("MailSender");
		String username = propertiesManager.getEmailProperty("MailUserName");
		String password = propertiesManager.getEmailProperty("Password");
		String emailactiveflag = propertiesManager.getEmailProperty("EmailActiveFlag");
		InternetAddress[] toAddresses = null;
		InternetAddress[] bccAddresses = null;
		try {
			if (emailactiveflag != "true") {
				Session session = Session.getDefaultInstance(properties, new javax.mail.Authenticator() {
					@Override
					protected PasswordAuthentication getPasswordAuthentication() {
						return new PasswordAuthentication(username, password);
					}
				});
				Message message = new MimeMessage(session);
				message.setFrom(new InternetAddress(mailSender));
				// Sending Bulk Email
				if (!StringUtils.isBlank(mailTo) && !StringUtils.isBlank(mailBcc)) {
					toAddresses = InternetAddress.parse(mailTo);
					bccAddresses = InternetAddress.parse(mailBcc);
					message.setRecipients(Message.RecipientType.TO, toAddresses);
					message.setRecipients(Message.RecipientType.BCC, bccAddresses);
				} else if (!StringUtils.isBlank(mailTo)) {
					toAddresses = InternetAddress.parse(mailTo);
					message.setRecipients(Message.RecipientType.TO, toAddresses);
				} else {
					bccAddresses = InternetAddress.parse(mailBcc);
					message.setRecipients(Message.RecipientType.BCC, bccAddresses);
				}
				message.setSubject(subject);
				message.setSentDate(new Date());
				// set plain text message
				message.setContent(body, "text/html");
				// sends the e-mail
				Transport.send(message);
			} else {
				logger.info("Emailing Feature Disabled : " + "Email Recipient :" + mailTo + "Mail Subject :" + subject
						+ "Email Body :" + body);
			}
		} catch (Exception exception) {
			logger.error("Exception", exception);
		}
		if (emailExceptionHandlerFlag) {
			throw new SystemException(ErrorType.EMAIL_ERROR, ErrorType.EMAIL_ERROR.getResponseMessage());
		}
	}

	public void sendAttachmentFileEmail(String body, String subject, String mailTo, String mailBcc,
			boolean emailExceptionHandlerFlag) throws Exception {
		if (StringUtils.isBlank(mailTo) && StringUtils.isBlank(mailBcc)) {
			return;
		}

		String mailSender = propertiesManager.getEmailProperty("MailSender");
		String username = propertiesManager.getEmailProperty("MailUserName");
		String password = propertiesManager.getEmailProperty("Password");
		String emailactiveflag = propertiesManager.getEmailProperty("EmailActiveFlag");
		InternetAddress[] toAddresses = null;
		InternetAddress[] bccAddresses = null;
		try {
			if (emailactiveflag != "true") {
				Session session = Session.getDefaultInstance(properties, new javax.mail.Authenticator() {
					@Override
					protected PasswordAuthentication getPasswordAuthentication() {
						return new PasswordAuthentication(username, password);
					}
				});
				MimeBodyPart messageBodyPart = new MimeBodyPart();
				Multipart multipart = new MimeMultipart();
				messageBodyPart = new MimeBodyPart();
				Message message = new MimeMessage(session);
				message.setFrom(new InternetAddress(mailSender));
				// Sending Bulk Email
				if (!StringUtils.isBlank(mailTo) && !StringUtils.isBlank(mailBcc)) {
					toAddresses = InternetAddress.parse(mailTo);
					bccAddresses = InternetAddress.parse(mailBcc);
					message.setRecipients(Message.RecipientType.TO, toAddresses);
					message.setRecipients(Message.RecipientType.BCC, bccAddresses);
				} else if (!StringUtils.isBlank(mailTo)) {
					toAddresses = InternetAddress.parse(mailTo);
					message.setRecipients(Message.RecipientType.TO, toAddresses);
				} else {
					bccAddresses = InternetAddress.parse(mailBcc);
					message.setRecipients(Message.RecipientType.BCC, bccAddresses);
				}
				message.setSubject(subject);
				message.setSentDate(new Date());
				// set Attachment file
				DataSource source = new FileDataSource(body);
				messageBodyPart.setDataHandler(new DataHandler(source));
				messageBodyPart.setFileName(body);
				multipart.addBodyPart(messageBodyPart);
				message.setContent(multipart);
				// sends the e-mail
				Transport.send(message);
			} else {
				logger.info("Emailing Feature Disabled : " + "Email Recipient :" + mailTo + "Mail Subject :" + subject
						+ "Email Body :" + body);
			}
		} catch (Exception exception) {
			logger.error("Exception", exception);
		}
		if (emailExceptionHandlerFlag) {
			throw new SystemException(ErrorType.EMAIL_ERROR, ErrorType.EMAIL_ERROR.getResponseMessage());
		}
	}

}