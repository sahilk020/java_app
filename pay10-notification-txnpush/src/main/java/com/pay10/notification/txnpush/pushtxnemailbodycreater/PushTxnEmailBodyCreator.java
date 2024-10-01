package com.pay10.notification.txnpush.pushtxnemailbodycreater;

import org.slf4j.Logger; import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.pay10.commons.util.EmailerConstants;
import com.pay10.commons.util.PropertiesManager;

@Component
public class PushTxnEmailBodyCreator {
	private Logger logger = LoggerFactory.getLogger(PushTxnEmailBodyCreator.class);
	private String body;
	@Autowired
	@Qualifier("propertiesManager")
	private PropertiesManager propertiesManager;
	private StringBuilder content = new StringBuilder();

	public String updateRequestNotificationEmail(String messageBody) {
		try {
			content.append("<tr>");
			content.append("<td align='left' style='font:normal 14px Arial;'><p><em>Dear Merchant,<br /><br />");
			content.append("<table width='20%' border='0' align='left' cellpadding='0.' cellspacing='0' class='product-spec1'>");
			content.append("<tr>");
			content.append("<br /><br />" +messageBody + "</em></p>");
			content.append("</tr>");
			makeHeader();
			content.append("</table>");
			content.append("</td>");
			content.append("</tr>");
			content.append("<br /><br />");
			makeFooter();
			body = content.toString();

		} catch (Exception exception) {
			logger.error("Exception", exception);
		}
		return body;
	}

	 private StringBuilder makeFooter(){
			
		 	content.append("<p><em>Assuring you of our best services at all times.</em></p>");
			content.append("<p><em>If you have any questions about your transaction or any other matter, please feel free to contact us at ");
			content.append(EmailerConstants.CONTACT_US_EMAIL.getValue());
			content.append(" or by phone at ");
			 content.append(EmailerConstants.PHONE_NO.getValue());
			 content.append(".</em></p>");
			content.append("<p></p></td>");
			content.append("</tr>");
			content.append("<tr>");
			content.append("<td align='left' valign='middle' bgcolor='#fbfbfb' style='border-top:1px solid #d4d4d4; border-bottom-left-radius:10px;");
			content.append("border-bottom-right-radius:10px;'><table width='100%' border='0' cellspacing='0' cellpadding='0.'>");
			content.append("<tr>");
			content.append("<td align='left' valign='middle' style='font-family:Arial;'><strong>We Care!<br />");
			content.append(EmailerConstants.GATEWAY.getValue());
			content.append(" Team</strong></td>");
			content.append("<td align='right' valign='bottom' style='font:normal 12px Arial;'>© 2016 ");
			content.append(EmailerConstants.WEBSITE.getValue()); 
			content.append(" All rights reserved.</td>");
			content.append("</tr>");
			content.append("</table></td>");
			content.append("</tr>");
			content.append("</table>");
			content.append("</body>");
			content.append("</html>");
			return content;
	 }
	 
	 private StringBuilder makeHeader(){
			
			content.append("<html xmlns='http://www.w3.org/1999/xhtml'>");
			content.append("<head>");
			content.append("<meta http-equiv='Content-Type' content='text/html; charset=utf-8'/>");
			content.append("<title>Payment Acknowledgement</title>");
			content.append("<style>");
			content.append("table.product-spec{border:1px solid #eaeaea; border-bottom:1px solid");
			content.append("#dedede;font-family:Arial,Helvetica,sans-serif;background:#eeeeee}table.product-spec th{font-size:16px;");
			content.append("font-weight:bold;padding:5px;border-right:1px solid #dedede; border-bottom:1px solid #dedede; background:#0271bb;");
			content.append("color:#ffffff;}table.product-spec td{font-size:12px;padding:6px; border-right:1px solid #dedede; border-bottom:1px solid");
			content.append("#dedede;background-color:#ffffff;}");
			content.append("table.product-spec td p { font:normal 12px Arial; color:#6f6f6f; padding:0px; margin:0px; line-height:12px; }");
			content.append("</style>");
			content.append("</head>");
			content.append("<body>");
			content.append("<br /><br />");
			content.append("<table width='700' border='0' align='center' cellpadding='7' cellspacing='0' style='border:1px solid #d4d4d4;");
			content.append("background-color:#ffffff; border-radius:10px;'>");
			content.append("<tr>");
			content.append("<td height='60' align='center' valign='middle' bgcolor='#fbfbfb' style='border-bottom:1px solid #d4d4d4;");
			content.append("border-top-left-radius:10px; border-top-right-radius:10px;'><img src='"
					+ propertiesManager.getSystemProperty("emailerLogoURL")
					+ "' width='277' height='45' /></td>");
			content.append("</tr>");
			return content;		
		}

	public String updateApproveRejectNotificationEmail(String message) {
		try {
			content.append("<tr>");
			content.append("<td align='left' style='font:normal 14px Arial;'><p><em>Dear Merchant,<br /><br />");
			content.append("<table width='20%' border='0' align='left' cellpadding='0.' cellspacing='0' class='product-spec1'>");
			content.append("<tr>");
			content.append("<br /><br />" +message + "</em></p>");
			content.append("</tr>");
			makeHeader();
			content.append("</table>");
			content.append("</td>");
			content.append("</tr>");
			content.append("<br /><br />");
			makeFooter();
			body = content.toString();

		} catch (Exception exception) {
			logger.error("Exception", exception);
		}
		return body;
	}

}
