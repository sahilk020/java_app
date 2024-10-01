package com.pay10.notification.email.emailBuilder;

import java.io.File;
import java.util.Date;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.exception.SystemException;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.PropertiesManager;

@Component
public class Emailer {

	private Logger logger = LoggerFactory.getLogger(Emailer.class.getName());

	@Autowired
	private PropertiesManager propertiesManager;

	final String username = propertiesManager.propertiesMap.get(Constants.MAIL_USERNAME.getValue());
	final String password = propertiesManager.propertiesMap.get(Constants.MAIL_PASSWORD.getValue());

	/*
	 * static Properties properties = new Properties();{
	 * properties.put("mail.smtp.host", "mail.asiancheckout.com");
	 * properties.put("mail.smtp.socketFactory.port", "587");
	 * properties.put("mail.smtp.socketFactory.class",
	 * "javax.net.ssl.SSLSocketFactory"); properties.put("mail.smtp.auth", "true");
	 * properties.put("mail.smtp.port", "587"); }
	 */

	static Properties properties = new Properties();
	{
		properties.put("mail.smtp.host", propertiesManager.propertiesMap.get(Constants.MAIL_HOST.getValue()));
		properties.put("mail.smtp.socketFactory.port",
				propertiesManager.propertiesMap.get(Constants.SOCKETFACTORY_PORT.getValue()));
		properties.put("mail.smtp.socketFactory.class",
				propertiesManager.propertiesMap.get(Constants.SOCKETFACTORY_CLASS.getValue()));
		properties.put("mail.smtp.auth", propertiesManager.propertiesMap.get(Constants.MAIL_AUTH.getValue()));
		properties.put("mail.smtp.port", propertiesManager.propertiesMap.get(Constants.MAIL_PORT.getValue()));
		properties.put("mail.smtp.starttls.enable",
				propertiesManager.propertiesMap.get(Constants.MAIL_STARTTLS.getValue()));
		properties.put("mail.smtp.ssl.protocols",
				propertiesManager.propertiesMap.get(Constants.MAIL_PROTOCAL.getValue()));
	}

	Session session = Session.getInstance(properties, new javax.mail.Authenticator() {
		@Override
		protected PasswordAuthentication getPasswordAuthentication() {
			return new PasswordAuthentication(username, password);
		}
	});

	
	
	@SuppressWarnings({ "unlikely-arg-type", "static-access" })
	public void sendEmail(String body, String subject, String mailTo, String mailBcc, boolean flag) {

		String mailSender = PropertiesManager.propertiesMap.get(Constants.MAIL_SENDER.getValue());
		String username = PropertiesManager.propertiesMap.get(Constants.MAIL_USERNAME.getValue());
		String password = PropertiesManager.propertiesMap.get(Constants.MAIL_PASSWORD.getValue());
		InternetAddress[] toAddresses = null;
		InternetAddress[] bccAddresses = null;
		try {
			logger.info("Sending email in emailer service  at emailId : " + mailTo);
			Session session = Session.getDefaultInstance(properties, new javax.mail.Authenticator() {
				@Override
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(username, password);
				}
			});
			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(mailSender));
			// Sending Bulk Email
			if (StringUtils.isNotBlank(mailTo)) {
				toAddresses = InternetAddress.parse(mailTo);
				message.setRecipients(Message.RecipientType.TO, toAddresses);
			}

			if (StringUtils.isNotBlank(mailBcc)) {
				bccAddresses = InternetAddress.parse(mailBcc);
				message.setRecipients(Message.RecipientType.BCC, bccAddresses);
			}

			message.setSubject(subject);
			message.setSentDate(new Date());
			message.setContent(body, "text/html");
			Transport.send(message);
			logger.info("Email sent successfully at : " + mailTo);
		} catch (Exception exception) {
			logger.error("Unable to send email at : " + mailTo);
			logger.error(exception.getMessage());
		}

	}

	public void sendEmailWithTextAndAttachment(String body, String subject, String mailTo, String mailBcc, boolean flag,
			String fileName, File attachmment) {

		String mailSender = PropertiesManager.propertiesMap.get(Constants.MAIL_SENDER.getValue());
		String username = PropertiesManager.propertiesMap.get(Constants.MAIL_USERNAME.getValue());
		String password = PropertiesManager.propertiesMap.get(Constants.MAIL_PASSWORD.getValue());
		InternetAddress[] toAddresses = null;
		InternetAddress[] bccAddresses = null;
		try {
			logger.info("Sending email in emailer service  at emailId : " + mailTo);
			Session session = Session.getDefaultInstance(properties, new javax.mail.Authenticator() {
				@Override
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(username, password);
				}
			});
			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(mailSender));
			// Sending Bulk Email
			if (StringUtils.isNotBlank(mailTo)) {
				toAddresses = InternetAddress.parse(mailTo);
				message.setRecipients(Message.RecipientType.TO, toAddresses);
			}

			if (StringUtils.isNotBlank(mailBcc)) {
				bccAddresses = InternetAddress.parse(mailBcc);
				message.setRecipients(Message.RecipientType.BCC, bccAddresses);
			}

			BodyPart messageBodyPart = new MimeBodyPart();
			messageBodyPart.setContent(body, "text/html");
			Multipart multipart = new MimeMultipart();
			multipart.addBodyPart(messageBodyPart);

			messageBodyPart = new MimeBodyPart();
			DataSource source = new FileDataSource(attachmment);
			messageBodyPart.setDataHandler(new DataHandler(source));
			messageBodyPart.setFileName(fileName);
			multipart.addBodyPart(messageBodyPart);

			message.setSubject(subject);
			message.setSentDate(new Date());
			message.setContent(multipart);
			Transport.send(message);
			logger.info("Email sent successfully at : " + mailTo);
		} catch (Exception exception) {
			logger.error("Unable to send email at : " + mailTo);
			logger.error(exception.getMessage());
		}

	}

	
	public void sendEmailacquirer(String body, String subject, String mailTo, String mailBcc, boolean emailExceptionHandlerFlag)
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
			// throw new SystemException(ErrorType.EMAIL_ERROR,
			// ErrorType.EMAIL_ERROR.getResponseMessage());
		}
	}

}