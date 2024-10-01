package com.pay10.commons.api;

import java.io.File;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.SendFailedException;
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

import com.google.gson.Gson;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.PropertiesManager;

@SuppressWarnings("static-access")
@Component
public class VelocityEmailer {

	private Logger logger = LoggerFactory.getLogger(VelocityEmailer.class.getName());

	@Autowired
	private PropertiesManager propertiesManager;

	final String username = propertiesManager.propertiesMap.get(Constants.MAIL_USERNAME.getValue());
	final String password = propertiesManager.propertiesMap.get(Constants.MAIL_PASSWORD.getValue());

//	static Properties properties = new Properties();
//	{
//		properties.put("mail.smtp.host", "smtp.office365.com");
//		properties.put("mail.smtp.socketFactory.port", "587");
//		properties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
//		properties.put("mail.smtp.auth", "true");
//		properties.put("mail.smtp.port", "587");
//		properties.put("mail.smtp.starttls.enable", "true");
//		properties.put("mail.smtp.ssl.protocols", "TLSv1.2");
//	}

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

			body = velocityBlockingEmail(body);
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

	private String velocityBlockingEmail(String messageBody) {
		String body = null;
		StringBuilder content = new StringBuilder();
		try {
			makeHeader();
			content.append("<tr>");
			content.append("<td align='left' style='font:normal 14px Arial;'><em><p>Dear Risk Team," + "<br />");

			content.append("<br /><br />" + messageBody + "<br /><br /><br />Regards,<br /> Team BestPay" + "</em></p>");

			content.append("</p></em>");
			content.append(
					"<table width='20%' border='0' align='left' cellpadding='0.' cellspacing='0' class='product-spec1'>");
			content.append("<tr>");
			content.append("</tr>");
			content.append("</table>");
			content.append("<br />");
			content.append("<br />");
			body = content.toString();
		} catch (Exception exception) {
			logger.error("Exception", exception);
		}
		return body;
	}

	private StringBuilder makeHeader() {
		StringBuilder content = new StringBuilder();
		content.append("<html xmlns='http://www.w3.org/1999/xhtml'>");
		content.append("<head>");
		content.append("<meta http-equiv='Content-Type' content='text/html; charset=utf-8'/>");
		content.append("<title>Notification</title>");
		content.append("<style>");
		content.append("table.product-spec{border:1px solid #eaeaea; border-bottom:1px solid");
		content.append(
				"#dedede;font-family:Arial,Helvetica,sans-serif;background:#eeeeee}table.product-spec th{font-size:16px;");
		content.append(
				"font-weight:bold;padding:5px;border-right:1px solid #dedede; border-bottom:1px solid #dedede; background:#0271bb;");
		content.append(
				"color:#ffffff;}table.product-spec td{font-size:12px;padding:6px; border-right:1px solid #dedede; border-bottom:1px solid");
		content.append("#dedede;background-color:#ffffff;}");
		content.append(
				"table.product-spec td p { font:normal 12px Arial; color:#6f6f6f; padding:0px; margin:0px; line-height:12px; }");
		content.append("</style>");
		content.append("</head>");
		content.append("<body>");
		content.append("<br /><br />");
		content.append(
				"<table width='700' border='0' align='center' cellpadding='7' cellspacing='0' style='border:1px solid #d4d4d4;");
		content.append("background-color:#ffffff; border-radius:10px;'>");
		content.append("<tr>");
		content.append(
				"<td height='60' align='center' valign='middle' bgcolor='#fbfbfb' style='border-bottom:1px solid #d4d4d4;");
		content.append("border-top-left-radius:10px; border-top-right-radius:10px;'><img src='"
				+ propertiesManager.getSystemProperty("emailerLogoURL") + "' width='277' height='45' /></td>");
		content.append("</tr>");
		return content;
	}

	// My email

	public void sendEmailWithTextAndAttachmentLiability(String body, String subject, String mailTo[], String type) {

		String mailSender = PropertiesManager.propertiesMap.get(Constants.MAIL_SENDER.getValue());
//		String username = PropertiesManager.propertiesMap.get(Constants.MAIL_USERNAME.getValue());
//		String password = PropertiesManager.propertiesMap.get(Constants.MAIL_PASSWORD.getValue());
		InternetAddress[] recipientAddress = new InternetAddress[mailTo.length];
		InternetAddress[] bccAddresses = null;
		logger.info("Username : " + username + "\tPassword : " + password);
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
			if (mailTo != null) {

				int counter = 0;
				for (String recipient : mailTo) {
					recipientAddress[counter] = new InternetAddress(recipient.trim());
					counter++;
				}
				message.setRecipients(Message.RecipientType.TO, recipientAddress);
			}

			body = velocityBlockingEmailLiability(body, type);
			BodyPart messageBodyPart = new MimeBodyPart();
			messageBodyPart.setContent(body, "text/html");
			Multipart multipart = new MimeMultipart();
			multipart.addBodyPart(messageBodyPart);

//			messageBodyPart = new MimeBodyPart();
//			DataSource source = new FileDataSource(attachmment);
//			messageBodyPart.setDataHandler(new DataHandler(source));
//			messageBodyPart.setFileName(fileName);
//			multipart.addBodyPart(messageBodyPart);

			message.setSubject(subject);
			message.setSentDate(new Date());
			message.setContent(multipart);
			Transport.send(message);
			logger.info("Email sent successfully at : " + Arrays.asList(mailTo));

		} catch (SendFailedException e) {
			logger.error("Unable to send email at : " + mailTo);
			logger.error(e.getMessage());
		} catch (MessagingException e) {
			logger.error("Unable to send email at : " + mailTo);
			logger.error(e.getMessage());
		} catch (Exception exception) {
			logger.error("Unable to send email at : " + mailTo);
			logger.error(exception.getMessage());
		}

	}

	private String velocityBlockingEmailLiability(String messageBody, String type) {
		String body = null;
		StringBuilder content = new StringBuilder();
		try {
			
			if (type.equalsIgnoreCase("Chargeback")) {
				makeHeaderLiability("Notification For Transaction Dispute");
			}else {
				makeHeaderLiability("Notification For Liability "+type);
				
			}
			content.append("<tr>");
			content.append("<td align='left' style='font:normal 14px Arial;'><em><p>Dear Team," + "<br />");

			content.append("<br /><br />" + messageBody + "<br /><br /><br />Regards,<br /> Team BestPay" + "</em></p>");

			content.append("</p></em>");
			content.append(
					"<table width='20%' border='0' align='left' cellpadding='0.' cellspacing='0' class='product-spec1'>");
			content.append("<tr>");
			content.append("</tr>");
			content.append("</table>");
			content.append("<br />");
			content.append("<br />");
			body = content.toString();
		} catch (Exception exception) {
			logger.error("Exception", exception);
		}
		return body;
	}

	private StringBuilder makeHeaderLiability(String type) {
		StringBuilder content = new StringBuilder();
		content.append("<html xmlns='http://www.w3.org/1999/xhtml'>");
		content.append("<head>");
		content.append("<meta http-equiv='Content-Type' content='text/html; charset=utf-8'/>");

		content.append("<title>"+type+"</title>");

		content.append("<title>Notification For Liability " + type + "</title>");
		content.append("<style>");
		content.append("table.product-spec{border:1px solid #eaeaea; border-bottom:1px solid");
		content.append(
				"#dedede;font-family:Arial,Helvetica,sans-serif;background:#eeeeee}table.product-spec th{font-size:16px;");
		content.append(
				"font-weight:bold;padding:5px;border-right:1px solid #dedede; border-bottom:1px solid #dedede; background:#0271bb;");
		content.append(
				"color:#ffffff;}table.product-spec td{font-size:12px;padding:6px; border-right:1px solid #dedede; border-bottom:1px solid");
		content.append("#dedede;background-color:#ffffff;}");
		content.append(
				"table.product-spec td p { font:normal 12px Arial; color:#6f6f6f; padding:0px; margin:0px; line-height:12px; }");
		content.append("</style>");
		content.append("</head>");
		content.append("<body>");
		content.append("<br /><br />");
		content.append(
				"<table width='700' border='0' align='center' cellpadding='7' cellspacing='0' style='border:1px solid #d4d4d4;");
		content.append("background-color:#ffffff; border-radius:10px;'>");
		content.append("<tr>");
		content.append(
				"<td height='60' align='center' valign='middle' bgcolor='#fbfbfb' style='border-bottom:1px solid #d4d4d4;");
		content.append("border-top-left-radius:10px; border-top-right-radius:10px;'><img src='"
				+ propertiesManager.getSystemProperty("emailerLogoURL") + "' width='277' height='45' /></td>");
		content.append("</tr>");
		return content;
	}
	// mail for BSES






	// mail for BSES

		public String sendEmailBses(String body, String subject, String mailTo[], Map<String, File> files) {
			String response = "Failed";
			String mailSender = PropertiesManager.propertiesMap.get(Constants.MAIL_SENDER.getValue());

			InternetAddress[] recipientAddress = new InternetAddress[mailTo.length];
			InternetAddress[] bccAddresses = null;
			logger.info("Username : " + username + "\tPassword : " + password);
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
				if (mailTo != null) {

					int counter = 0;
					for (String recipient : mailTo) {
						recipientAddress[counter] = new InternetAddress(recipient.trim());
						counter++;
					}
					message.setRecipients(Message.RecipientType.TO, recipientAddress);
				}
				body = bsesBlockingEmail(body,subject);

				// body = velocityBlockingEmail(body);
				BodyPart messageBodyPart = new MimeBodyPart();
				messageBodyPart.setContent(body, "text/html");
				Multipart multipart = new MimeMultipart();
				multipart.addBodyPart(messageBodyPart);

				
				for (Map.Entry<String, File> entry : files.entrySet()) {
					String key = entry.getKey();
					File value = entry.getValue();

					messageBodyPart = new MimeBodyPart();
					DataSource source = new FileDataSource(value);
					messageBodyPart.setDataHandler(new DataHandler(source));
					messageBodyPart.setFileName(key);
					multipart.addBodyPart(messageBodyPart);
				}
				message.setSubject(subject);
				message.setSentDate(new Date());
				message.setContent(multipart);
				Transport.send(message);
				logger.info("Email sent successfully at : " + Arrays.asList(mailTo));
				response = "Email sent successfully at : " + new Gson().toJson(mailTo);
			} catch (SendFailedException e) {
				logger.error("Unable to send email at : " + mailTo);
				logger.error(e.getMessage());
				response = "Unable to send email at" + new Gson().toJson(mailTo);
				e.printStackTrace();
			} catch (MessagingException e) {
				logger.error("Unable to send email at : " + mailTo);
				logger.error(e.getMessage());
				response = "Unable to send email at" + new Gson().toJson(mailTo);
				e.printStackTrace();
			} catch (Exception exception) {
				logger.error("Unable to send email at : " + mailTo);
				logger.error(exception.getMessage());
				response = "Unable to send email at" + new Gson().toJson(mailTo);
				exception.printStackTrace();
			}
			return response;
		}

		private String bsesBlockingEmail(String messageBody,String subject) {
			String body = null;
			StringBuilder content = new StringBuilder();
			try {
				bsesMakeHeader(subject);
				content.append("<tr>");
				content.append("<td align='left' style='font:normal 14px Arial;'><em><p>Dear Team," + "<br />");

				content.append("<br /><br />" + messageBody + "<br /><br /><br />Regards,<br /> Team BestPay" + "</em></p>");

				content.append("</p></em>");
				content.append(
						"<table width='20%' border='0' align='left' cellpadding='0.' cellspacing='0' class='product-spec1'>");
				content.append("<tr>");
				content.append("</tr>");
				content.append("</table>");
				content.append("<br />");
				content.append("<br />");
				body = content.toString();
			} catch (Exception exception) {
				logger.error("Exception", exception);
			}
			return body;
		}

		private StringBuilder bsesMakeHeader(String subject) {
			StringBuilder content = new StringBuilder();
			content.append("<html xmlns='http://www.w3.org/1999/xhtml'>");
			content.append("<head>");
			content.append("<meta http-equiv='Content-Type' content='text/html; charset=utf-8'/>");
			content.append("<title>"+subject+"</title>");
			content.append("<style>");
			content.append("table.product-spec{border:1px solid #eaeaea; border-bottom:1px solid");
			content.append(
					"#dedede;font-family:Arial,Helvetica,sans-serif;background:#eeeeee}table.product-spec th{font-size:16px;");
			content.append(
					"font-weight:bold;padding:5px;border-right:1px solid #dedede; border-bottom:1px solid #dedede; background:#0271bb;");
			content.append(
					"color:#ffffff;}table.product-spec td{font-size:12px;padding:6px; border-right:1px solid #dedede; border-bottom:1px solid");
			content.append("#dedede;background-color:#ffffff;}");
			content.append(
					"table.product-spec td p { font:normal 12px Arial; color:#6f6f6f; padding:0px; margin:0px; line-height:12px; }");
			content.append("</style>");
			content.append("</head>");
			content.append("<body>");
			content.append("<br /><br />");
			content.append(
					"<table width='700' border='0' align='center' cellpadding='7' cellspacing='0' style='border:1px solid #d4d4d4;");
			content.append("background-color:#ffffff; border-radius:10px;'>");
			content.append("<tr>");
			content.append(
					"<td height='60' align='center' valign='middle' bgcolor='#fbfbfb' style='border-bottom:1px solid #d4d4d4;");
			content.append("border-top-left-radius:10px; border-top-right-radius:10px;'><img src='"
					+ propertiesManager.getSystemProperty("emailerLogoURL") + "' width='277' height='45' /></td>");
			content.append("</tr>");
			return content;
		}

	
	
	//Chargeback Mail
	
	public void sendEmailChargeback(String body, String subject, String mailTo[], String type) {

		String mailSender = PropertiesManager.propertiesMap.get(Constants.MAIL_SENDER.getValue());
//		String username = PropertiesManager.propertiesMap.get(Constants.MAIL_USERNAME.getValue());
//		String password = PropertiesManager.propertiesMap.get(Constants.MAIL_PASSWORD.getValue());
		InternetAddress[] recipientAddress = new InternetAddress[mailTo.length];
		InternetAddress[] bccAddresses = null;
		logger.info("Username : " + username + "\tPassword : " + password);
		try {
			logger.info("Sending email in emailer service  at emailId : " + new Gson().toJson(mailTo));
			Session session = Session.getDefaultInstance(properties, new javax.mail.Authenticator() {
				@Override
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(username, password);
				}
			});
			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(mailSender));
			// Sending Bulk Email
			if (mailTo != null) {

				int counter = 0;
				for (String recipient : mailTo) {
					recipientAddress[counter] = new InternetAddress(recipient.trim());
					counter++;
				}
				message.setRecipients(Message.RecipientType.TO, recipientAddress);
			}

			body = emailChargebackBody(body, type);
			BodyPart messageBodyPart = new MimeBodyPart();
			messageBodyPart.setContent(body, "text/html");
			Multipart multipart = new MimeMultipart();
			multipart.addBodyPart(messageBodyPart);

			message.setSubject(subject);
			message.setSentDate(new Date());
			message.setContent(multipart);
			Transport.send(message);
			logger.info("Email sent successfully at : " + Arrays.asList(mailTo));

		} catch (SendFailedException e) {
			logger.error("Unable to send email at : " + mailTo);
			logger.error(e.getMessage());
		} catch (MessagingException e) {
			logger.error("Unable to send email at : " + mailTo);
			logger.error(e.getMessage());
		} catch (Exception exception) {
			logger.error("Unable to send email at : " + mailTo);
			logger.error(exception.getMessage());
		}

	}

	private String emailChargebackBody(String messageBody, String type) {
		String body = null;
		StringBuilder content = new StringBuilder();
		try {
			
			makeHeaderChargeback(type);
			content.append("<tr>");
			//content.append("<td align='left' style='font:normal 14px Arial;'><em><p>Dear Team," + "<br />");

			content.append("<br /><br />" + messageBody + "<br /><br />");

			content.append("</p></em>");
			content.append(
					"<table width='20%' border='0' align='left' cellpadding='0.' cellspacing='0' class='product-spec1'>");
			content.append("<tr>");
			content.append("</tr>");
			content.append("</table>");
			content.append("<br />");
			content.append("<br />");
			body = content.toString();
		} catch (Exception exception) {
			logger.error("Exception", exception);
		}
		return body;
	}

	private StringBuilder makeHeaderChargeback(String type) {
		StringBuilder content = new StringBuilder();
		content.append("<html xmlns='http://www.w3.org/1999/xhtml'>");
		content.append("<head>");
		content.append("<meta http-equiv='Content-Type' content='text/html; charset=utf-8'/>");
		content.append("<title>"+type+"</title>");
		content.append("<style>");
		content.append("table.product-spec{border:1px solid #eaeaea; border-bottom:1px solid");
		content.append(
				"#dedede;font-family:Arial,Helvetica,sans-serif;background:#eeeeee}table.product-spec th{font-size:16px;");
		content.append(
				"font-weight:bold;padding:5px;border-right:1px solid #dedede; border-bottom:1px solid #dedede; background:#0271bb;");
		content.append(
				"color:#ffffff;}table.product-spec td{font-size:12px;padding:6px; border-right:1px solid #dedede; border-bottom:1px solid");
		content.append("#dedede;background-color:#ffffff;}");
		content.append(
				"table.product-spec td p { font:normal 12px Arial; color:#6f6f6f; padding:0px; margin:0px; line-height:12px; }");
		content.append("</style>");
		content.append("</head>");
		content.append("<body>");
		content.append("<br /><br />");
		content.append(
				"<table width='700' border='0' align='center' cellpadding='7' cellspacing='0' style='border:1px solid #d4d4d4;");
		content.append("background-color:#ffffff; border-radius:10px;'>");
		content.append("<tr>");
		content.append(
				"<td height='60' align='center' valign='middle' bgcolor='#fbfbfb' style='border-bottom:1px solid #d4d4d4;");
		content.append("border-top-left-radius:10px; border-top-right-radius:10px;'><img src='"
				+ propertiesManager.getSystemProperty("emailerLogoURL") + "' width='277' height='45' /></td>");
		content.append("</tr>");
		return content;
	}

	
}