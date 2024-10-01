package com.pay10.notification.email.emailBodyCreater;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import com.pay10.commons.user.ResponseObject;
import com.pay10.commons.util.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pay10.commons.user.MerchantSignupNotifier;

/*
 @author Sunil
 */
@Component
public class EmailBodyCreator {

	@Autowired
	MerchantKeySaltService merchantKeySaltService;

	private Logger logger = LoggerFactory.getLogger(EmailBodyCreator.class);
	private PropertiesManager propertiesManager = new PropertiesManager();
	// private StringBuilder content = new StringBuilder();
	public static SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public String bodyTransactionEmail(Map<String, String> responseMap, String heading, String message) {
		String body = null;
		StringBuilder content = new StringBuilder();
		try {
			makeHeader();
			content.append("<tr>");
			content.append("<td align='left' style='font:normal 14px Arial;'><p><em>Dear  " + heading + ",</td>");
			content.append("<br /><br />" + message + "</em></p>");
			content.append(
					"<p><table width='96%' border='0' align='center' cellpadding='0' cellspacing='0' class='product-spec'>");
			content.append("<tr>");
			content.append("<th colspan='2' align='left' valign='middle'>Payment Summary</th>");
			content.append("</tr>");
			content.append("<tr>");
			content.append("<td width='27%'>Payment Amount :</td>");
			content.append("<td width='73%'>" + Amount.toDecimal(responseMap.get(CrmFieldConstants.AMOUNT.getValue()),
					responseMap.get(CrmFieldConstants.CURRENCYCODE.getValue())) + "</td>");
			content.append("</tr>");
			content.append("<tr>");
			content.append("<td>Order ID:</td>");
			content.append("<td>" + responseMap.get(CrmFieldConstants.ORDER_ID.getValue()) + "</td>");
			content.append("</tr>");
			content.append("<tr>");
			content.append("<td>Transaction ID: 	</td>");
			content.append("<td>" + responseMap.get(CrmFieldConstants.TXN_ID.getValue()) + "</td>");
			content.append("</tr>");
			content.append("<tr>");
			content.append("<td>Transaction Status:</td>");
			content.append("<td>" + responseMap.get(CrmFieldConstants.TXN_STATUS) + "</td>");
			content.append("</tr>");
			content.append("<tr>");
			content.append("<td>Transaction Response:</td>");
			content.append("<td>" + responseMap.get(CrmFieldConstants.RESPONSE_MESSAGE.getValue()) + "</td>");
			content.append("</tr>");
			content.append("<tr>");
			content.append("<td>Transaction Date &amp; Time:</td>");
			content.append("<td>" + responseMap.get(CrmFieldConstants.RESPONSE_DATE_TIME.getValue()) + "</td>");
			content.append("</tr>");
			content.append("<tr>");
			content.append("<td>Customer Name:</td>");
			content.append("<td>" + responseMap.get(CrmFieldConstants.CUST_NAME.getValue()) + "</td>");
			content.append("</tr>");
			content.append("<tr>");
			content.append("<td>Customer Email:</td>");
			content.append("<td>" + responseMap.get(CrmFieldConstants.CUST_EMAIL.getValue()) + "</td>");
			content.append("</tr>");
			content.append("</table>");
			content.append("</p>");
			makeFooter();

			body = content.toString();

		} catch (Exception exception) {
			logger.error("Exception", exception);
		}
		return body;
	}


	@SuppressWarnings({ "unused", "static-access" })
	public String AcquirerMasterSwitch(String PaymentType, String Acquirer, String Updateby,String Status , String updateon) {
		String body = null;
		StringBuilder content = new StringBuilder();
		try {
			makeHeader();
			content.append("<tr>");
			content.append("<td align='left' style='font:normal 14px Arial;'><em><p>Dear Team,<br /><br />");
			content.append("Please find the below Smart Routing Acquirer Master Switch activity as below<br /><br />");
			content.append("Acquirer Name : "+Acquirer+".<br />");
			content.append("Payment Type : "+PaymentType+".<br />");
			if(Status.equalsIgnoreCase("ACTIVE")) {
				content.append("Action:  INACTIVE To ACTIVE<br />");

			}else {
				content.append("Action:  ACTIVE To INACTIVE<br />");


			}
			content.append("Action InitiatedBy : "+Updateby+"<br />");
			content.append("Action InitiatedOn : "+updateon+"<br /><br /><br /><br />");
			content.append("Regards,<br />");
			content.append("BPGATE Team.<br />");
			content.append("</p></em>");
			content.append(
					"<table width='20%' border='0' align='left' cellpadding='0.' cellspacing='0' class='product-spec1'>");
			content.append("<tr>");

			content.append("</tr>");
			content.append("</table>");
			content.append("<br />");
			content.append("<br />");
			makeFooter();
			body = content.toString();
		} catch (Exception exception) {
			logger.error("Exception", exception);
		}
		return body;
	}

	@SuppressWarnings({ "unused", "static-access" })
	public String AcquirerMasterSwitchCreation(String PaymentType, String Acquirer, String Updateby,String Status , String updateon,String fromDate,String ToDate) {
		String body = null;
		StringBuilder content = new StringBuilder();
		try {



			makeHeader();
			content.append("<tr>");
			content.append("<td align='left' style='font:normal 14px Arial;'><em><p>Dear Team,<br /><br />");
			if(Status.equalsIgnoreCase("ACTIVE")) {
				content.append("Please find below the details for the Downtime Schedule Created for "+Acquirer+"<br /><br />");
			}else {
				content.append("Please find below the details for the Downtime Schedule DELETED for "+Acquirer+"<br /><br />");

			}
			content.append("Acquirer Name : "+Acquirer+".<br />");
			content.append("Payment Type : "+PaymentType+".<br />");
			if(Status.equalsIgnoreCase("ACTIVE")) {
				content.append("Action: Schedule Creation<br />");

			}else {
				content.append("Action:  : Schedule Deleted<br />");


			}
			content.append("Start Time & Date : "+fromDate+"<br />");
			content.append("End Time & Date : "+ToDate+"<br />");
			content.append("Action Initiated On: "+updateon+"<br />");

			content.append("Action Initiated By : "+Updateby+"<br /><br /><br /><br />");
			content.append("Regards,<br />");
			content.append("BPGATE Team.<br />");
			content.append("</p></em>");
			content.append(
					"<table width='20%' border='0' align='left' cellpadding='0.' cellspacing='0' class='product-spec1'>");
			content.append("<tr>");

			content.append("</tr>");
			content.append("</table>");
			content.append("<br />");
			content.append("<br />");
			makeFooter();
			body = content.toString();
		} catch (Exception exception) {
			logger.error("Exception", exception);
		}
		return body;
	}

	@SuppressWarnings({ "unused", "static-access" })
	public String AcquirerMasterSwitchschadular(String PaymentType, String Acquirer, String Updateby,String Status , String updateon,String fromDate,String ToDate) {
		String body = null;
		StringBuilder content = new StringBuilder();
		try {




			makeHeader();
			content.append("<tr>");
			content.append("<td align='left' style='font:normal 14px Arial;'><em><p>Dear Team,<br /><br />");
			content.append("ALERT!<br/><br/>");

			if(Status.equalsIgnoreCase("ACTIVE")) {
				content.append("Please be informed the Downtime Schedule for "+Acquirer+" has been initiated. Below are the details for the schedule<br /><br />");
			}else {
				content.append("Please be informed the Downtime Schedule for "+Acquirer+" has been COMPLETED. Below are the details for the schedule.<br /><br />");

			}
			content.append("Acquirer Name : "+Acquirer+".<br />");
			content.append("Payment Type : "+PaymentType+".<br />");
			if(Status.equalsIgnoreCase("ACTIVE")) {
				content.append("Action: Schedule Initiated<br />");

			}else {
				content.append("Action:  Schedule Completed<br />");


			}
			content.append("Start Time & Date : "+fromDate+"<br />");
			content.append("End Time & Date : "+ToDate+"<br />");
			content.append("Action Initiated On: "+updateon+"<br />");

			content.append("Action Initiated By : "+Updateby+"<br /><br /><br /><br />");
			content.append("Regards,<br />");
			content.append("BPGATE Team.<br />");
			content.append("</p></em>");
			content.append(
					"<table width='20%' border='0' align='left' cellpadding='0.' cellspacing='0' class='product-spec1'>");
			content.append("<tr>");

			content.append("</tr>");
			content.append("</table>");
			content.append("<br />");
			content.append("<br />");
			makeFooter();
			body = content.toString();
		} catch (Exception exception) {
			logger.error("Exception", exception);
		}
		return body;
	}
	public String transactionFailled(Map<String, String> responseMap) {
		String body = null;
		StringBuilder content = new StringBuilder();
		try {
			makeHeader();
			content.append("<tr>");
			content.append("<td align='left' style='font:normal 14px Arial;'><p><em>Dear User,<br />");
			content.append("<br />");
			content.append("Thank you for paying with ");
			content.append(EmailerConstants.GATEWAY.getValue());
			content.append(
					". Your Payment has been Failled. If you wish to pay again for the services please click on the below given button, which will redirect you to our payment page.</em></p>");
			content.append(
					"<p><table width='20%' border='0' align='center' cellpadding='0.' cellspacing='0' class='product-spec1'>");
			content.append("<tr>");
			content.append(
					"<td height='30' align='center' valign='middle' bgcolor='#fff'><a href='#'>Reprocess Pay</a></td>");
			content.append("</tr>");
			content.append("</table>");
			content.append(
					"<p><table width='96%' border='0' align='center' cellpadding='0.' cellspacing='0' class='product-spec'>");
			content.append("<tr>");
			content.append("<th colspan='2' align='left' valign='middle'>Payment Summary</th>");
			content.append("</tr>");
			content.append("<tr>");
			content.append("<td width='27%'>Payment Amount (Rs):</td>");
			content.append("<td width='73%'>" + Amount.toDecimal(responseMap.get(CrmFieldConstants.AMOUNT.getValue()),
					responseMap.get(CrmFieldConstants.CURRENCYCODE.getValue())) + "</td>");
			content.append("</tr>");
			content.append("<tr>");
			content.append("<td>Order ID:</td>");
			content.append("<td>" + responseMap.get(CrmFieldConstants.ORDER_ID.getValue()) + "</td>");
			content.append("</tr>");
			content.append("<tr>");
			content.append("<td>Transaction ID: 	</td>");
			content.append("<td>" + responseMap.get(CrmFieldConstants.TXN_ID.getValue()) + "</td>");
			content.append("</tr>");
			content.append("<tr>");
			content.append("<td>Transaction Status:</td>");
			content.append("<td>" + responseMap.get(CrmFieldConstants.TXN_STATUS.getValue()) + "</td>");
			content.append("</tr>");
			content.append("<tr>");
			content.append("<td>Transaction Response:</td>");
			content.append("<td>" + responseMap.get(CrmFieldConstants.RESPONSE_MESSAGE.getValue()) + "</td>");
			content.append("</tr>");
			content.append("<tr>");
			content.append("<td>Transaction Date &amp; Time:</td>");
			content.append("<td>" + responseMap.get(CrmFieldConstants.RESPONSE_DATE_TIME.getValue()) + "</td>");
			content.append("</tr>");
			content.append("<tr>");
			content.append("<td>Customer Name:</td>");
			content.append("<td>" + responseMap.get(CrmFieldConstants.CUST_NAME.getValue()) + "</td>");
			content.append("</tr>");
			content.append("<tr>");
			content.append("<td>Customer Email:</td>");
			content.append("<td>" + responseMap.get(CrmFieldConstants.CUST_EMAIL.getValue()) + "</td>");
			content.append("</tr>");
			content.append("</table>");
			content.append("</p>");
			makeFooter();
			body = content.toString();

		} catch (Exception exception) {
			logger.error("Exception", exception);
		}
		return body;
	}

	public String bodyPaytm(Fields responseMap) {
		String body = null;
		try {
			StringBuilder contant = new StringBuilder();
			contant.append("Dear Merchant" + "," + "\n\n");
			contant.append("Thank you for transaction with ");
			contant.append(EmailerConstants.COMPANY.getValue());
			contant.append(".\n\nPayment Amount (Rs)		: " + responseMap.get("TXNAMOUNT") + "\n");
			contant.append(
					"Transaction Auth Code	: " + responseMap.get(CrmFieldConstants.AUTH_CODE.getValue()) + "\n");
			contant.append("Transaction ID			: " + responseMap.get(CrmFieldConstants.TXN_ID.getValue()) + "\n");
			contant.append("Transaction Status		: " + responseMap.get("RESPONSE_MESSAGE") + "\n");
			contant.append("Transaction Response	: " + responseMap.get("RESPMSG") + "\n");
			contant.append("Transaction Date & Time	: " + responseMap.get("TXNDATE") + "\n");
			contant.append("Assuring you of our best service at all times.\n");
			contant.append("In case any Further query, please do tell us at ");
			contant.append(EmailerConstants.CONTACT_US_EMAIL.getValue());
			contant.append(" \n\n We Care!\n");
			contant.append(EmailerConstants.GATEWAY.getValue());
			contant.append("Team");
			body = contant.toString();

		} catch (Exception exception) {
			logger.error("Exception", exception);
		}
		return body;
	}

	public String bodyDirecPay(Fields responseMap) {
		String body = null;
		try {
			StringBuilder contant = new StringBuilder();
			contant.append("Dear Merchant" + "," + "\n\n");
			contant.append("Thank you for transaction with ");
			contant.append(EmailerConstants.COMPANY.getValue());
			contant.append(".\n\n");
			contant.append("Payment Amount (Rs)		: " + responseMap.get("TXNAMOUNT") + "\n");
			contant.append(
					"Transaction Auth Code	: " + responseMap.get(CrmFieldConstants.AUTH_CODE.getValue()) + "\n");
			contant.append("Transaction ID			: " + responseMap.get(CrmFieldConstants.TXN_ID.getValue()) + "\n");
			contant.append("Transaction Status		: " + responseMap.get(CrmFieldConstants.RESPONSE_MESSAGE.getValue())
					+ "\n");
			contant.append("Transaction Response	: " + responseMap.get("RESPMSG") + "\n");
			contant.append("Transaction Date & Time	: " + responseMap.get("TXNDATE") + "\n");
			contant.append("Assuring you of our best service at all times." + "\n");
			contant.append("In case any Further query, please do tell us at");
			contant.append(EmailerConstants.CONTACT_US_EMAIL.getValue());
			contant.append(" \n\n We Care!" + "\n");
			contant.append(EmailerConstants.GATEWAY.getValue());
			contant.append("Team");
			body = contant.toString();

		} catch (Exception exception) {
			logger.error("Exception", exception);
		}
		return body;
	}

	public String refundEmailBody(Map<String, String> fields, String senderType, String businessName) {
		String body = null;
		StringBuilder content = new StringBuilder();
		try {
			makeHeader();
			content.append("<tr>");
			content.append("<td align='left' style='font:normal 14px Arial;'>");
			content.append("<p><em>Dear " + senderType + ",</em></p>");
			content.append(
					"<p><table width='96%' border='0' align='center' cellpadding='0.' cellspacing='0' class='product-spec'>");
			content.append("<tr>");
			content.append("<th colspan='2' align='left' valign='middle'>Refund Processed Successful. </th>");
			content.append("</tr>");
			content.append("<tr>");
			content.append("<td width='27%'>Merchant Name :- </td>");
			content.append("<td width='73%'>" + businessName + "</td>");
			content.append("</tr>");
			content.append("<tr>");
			content.append("<td>Order Id :-  </td>");
			content.append("<td>" + fields.get(CrmFieldType.ORDER_ID.toString()) + "</td>");
			content.append("</tr>");
			content.append("<tr>");
			content.append("<td>Amount :- </td>");
			content.append("<td>" + Amount.toDecimal(fields.get(CrmFieldConstants.AMOUNT.getValue()),
					fields.get(CrmFieldConstants.CURRENCYCODE.getValue())) + "</td>");
			content.append("</tr>");
			content.append("</table></p>");
			makeFooter();
			body = content.toString();
		} catch (Exception exception) {
			logger.error("Exception", exception);
		}
		return body;
	}
	//vijay......................
	public String accountValidation(String accountValidationID, String url) {
		String body = null;
		StringBuilder content = new StringBuilder();
		try {
//			makeHeader();
			content.append("<tr>");
			content.append("<td align='left' style='font:normal 14px Arial;'><p><em>Dear Merchant,<br /><br />");
			content.append("Welcome to ");
			content.append(EmailerConstants.GATEWAY.getValue());
			content.append(", you have been successfully registered with us.<br /><br />");
			content.append("Please click on below button to verify and activate your ");
			content.append(EmailerConstants.GATEWAY.getValue());
			content.append(" Account:<br />");
			content.append("</em></p>");
			content.append(
					"<table width='20%' border='0' align='left' cellpadding='0.' cellspacing='0' class='product-spec1'>");
			content.append("<tr>");
			content.append("<td height='30' align='center' valign='middle' bgcolor='#fff' color='#fff'><a href='"
					+ url + "?id=" + accountValidationID + "'>Verify Account</a></td>");
			content.append("</tr>");
			content.append("</table>");
			content.append("<br /><br />");
			makeFooter();
			body = content.toString();
		} catch (Exception exception) {
			logger.error("Exception", exception);
		}
		return body;
	}

	public String signupConfirmation(String businessName, String emailId, String mobile) {
		String body = null;
		StringBuilder content = new StringBuilder();
		try {
			makeHeader();
			content.append("<tr>");
			content.append("<td align='left' style='font:normal 14px Arial;'><p><em>Dear Merchant,<br /><br />");
			content.append("Congratulations for Signing-up with ");
			content.append(EmailerConstants.GATEWAY.getValue());
			content.append(
					".<br /><table width='96%' border='0' align='center' cellpadding='0.' cellspacing='0' class='product-spec'>");
			content.append("<tr>");
			content.append("<th colspan='2' align='left' valign='middle'>Merchant Signup Details</th>");
			content.append("</tr>");
			content.append("<tr>");
			content.append("<td width='27%'>Business  Name :- ");
			content.append("<br /></td>");
			content.append("<td width='73%'>" + businessName + "</td>");
			content.append("</tr>");
			content.append("<tr>");
			content.append("<td>Email  Id :-</td>");
			content.append("<td> " + emailId + " </td>");
			content.append("</tr>");
			content.append("<tr>");
			content.append("<td>Phone  No :- </td>");
			content.append("<td>" + mobile + "</td>");
			content.append("</tr>");
			content.append("</table>");
			content.append("</p>");
			makeFooter();
			body = content.toString();
		} catch (Exception exception) {
			logger.error("Exception", exception);
		}
		return body;
	}

	public String addUser(String firstName, String accountValidationID, String url1,String url2) {
		String body = null;
		StringBuilder content = new StringBuilder();
		try {


			//vijay
//			content.append(makeHeader());
			content.append("<tr>");
			content.append(
					"<td align='left' style='font:normal 14px Arial;'><em><p>Dear " + firstName + ",<br /><br />");
			content.append("Please click on below button to set your account password.<br /><br />");
			content.append("</p></em>");
			content.append("<td height='30' align='center' valign='middle' bgcolor='#fff' color='#fff'>" +
					"<p><a href='" + url1 + "?id=" + accountValidationID + "'>1.verify your account</a></p></td>");
			content.append("<td height='30' align='center' valign='middle' bgcolor='#fff' color='#fff'>" +
					"<p><a href='" + url2 + "?id=" + accountValidationID + "'>2.Set Password</a></p></td>");
			content.append("</tr>");
			//content.append("<br />");
			content.append("<br />");
			content.append(makeFooter());
			body = content.toString();
		} catch (Exception exception) {
			logger.error("Exception", exception);
		}
		return body;
	}

	public String merchantTransactionDetails(String firstName, String payId, String salt, String merchantHostedEncryptionKey, String requestUrl, String email) {
		String body = null;
		StringBuilder content = new StringBuilder();
		try {
			content.append(makeHeader());
			content.append("<tr>");
			content.append(
					"<td align='left' style='font:normal 14px Arial;'><em><p>Dear " + firstName + ",<br /><br />");
			content.append("Below are details to make transaction in BPGATE.<br /><br />");
			content.append("Pay ID : '"+payId+"'<br /><br />");
			content.append("Email ID : '"+email+"'<br /><br />");
			content.append("Salt : '"+salt+"'<br /><br />");
			content.append("Merchant Hosted Encryption Key : '"+merchantHostedEncryptionKey+"'<br /><br />");
			content.append("Request URL : '"+requestUrl+"'<br /><br />");
			content.append("<br /><br />");

			content.append("</p></em>");
			content.append(
					"<table width='20%' border='0' align='left' cellpadding='0.' cellspacing='0' class='product-spec1'>");
			content.append("<tr>");

			content.append("</tr>");
			content.append("</table>");
			content.append("<br />");
			content.append("<br />");
			content.append(makeFooter());
			body = content.toString();
		} catch (Exception exception) {
			logger.error("Exception", exception);
		}
		return body;
	}



	public String emailPendingRequest(String messageBody) {
		String body = null;
		StringBuilder content = new StringBuilder();
		try {
			makeHeader();
			content.append("<tr>");
			content.append("<td align='left' style='font:normal 14px Arial;'><em><p>Dear User" + ",<br /><br />");
			content.append("<br /><br />" + messageBody + "</em></p>");
			content.append("</p></em>");
			content.append(
					"<table width='20%' border='0' align='left' cellpadding='0.' cellspacing='0' class='product-spec1'>");
			content.append("<tr>");
			content.append("</tr>");
			content.append("</table>");
			content.append("<br />");
			content.append("<br />");
			makeFooter();
			body = content.toString();
		} catch (Exception exception) {
			logger.error("Exception", exception);
		}
		return body;
	}

	@SuppressWarnings({ "unused", "static-access" })
	public String failedTxnsNotification(String customerName, String merchnatBusinessName, String amount,
										 String messageBody, String currencyCode) {
		String body = null;
		StringBuilder content = new StringBuilder();
		try {
			makeHeader();
			content.append("<tr>");
			if (StringUtils.isNotBlank(customerName)) {
				content.append("<td align='left' style='font:normal 14px Arial;'><em><p>Dear " + customerName
						+ ",<br /><br />");
			} else if (StringUtils.isNotBlank(merchnatBusinessName)) {
				content.append("<td align='left' style='font:normal 14px Arial;'><em><p>Dear " + merchnatBusinessName
						+ ",<br /><br />");
			} else {
				content.append(
						"<td align='left' style='font:normal 14px Arial;'><em><p>Dear Customer" + ",<br /><br />");
			}
			Amount af = new Amount();
			String actualAmount = amount;
			amount = af.toDecimal(actualAmount, currencyCode);

			String line0 = "We have observed that the payment which you tried to make on <b>" + merchnatBusinessName
					+ "</b> did not go through. We completely understand that it can be really frustrating for you. We would like you to know more about the reason of the unsuccessful attempt of the transaction. <br /><br />";
			String line1 = "Your transaction on <b>" + merchnatBusinessName + "</b> for <b>" + amount
					+ "</b> just failed due to <b>" + messageBody + "</b> <br /><br />";
			String line2 = "Please feel free to write to us at no-reply@bpgate.net for any further queries you may have. <br /><br />";
			String reasonString = "Following are some of the common reasons & their explanation for unsuccessful Transactions: <br /><br /> - Insufficient Funds.<br />	- Do Not Honor - It means the CVV/Expiry Date have been incorrectly entered. <br /> - Hotlisted Card - The Card is Blocked by the user.<br /> - Visa/Mastercard/NPCI Directory didn\'t respond in the specified time - BPGATE suggest users to try making the payment after sometime. <br /><br /><br />";
			String line3 = "Always happy to serve you. <br /><br /><br />";
			String line4 = "Regards,<br /> Team BPGATE";
			content.append("<br /><br />" + line0 + line1 + reasonString + line2 + line3 + line4 + "</em></p>");
			content.append("</p></em>");
			content.append(
					"<table width='20%' border='0' align='left' cellpadding='0.' cellspacing='0' class='product-spec1'>");
			content.append("<tr>");
			content.append("</tr>");
			content.append("</table>");
			content.append("<br />");
			content.append("<br />");
			makeFooter();
			body = content.toString();
		} catch (Exception exception) {
			logger.error("Exception", exception);
		}
		return body;
	}

	public String resetPasswordConfirmation(String customerName, String merchnatBusinessName, String messageBody) {
		String body = null;
		StringBuilder content = new StringBuilder();
		try {
			makeHeader();
			content.append("<tr>");
			if (StringUtils.isNotBlank(customerName)) {
				content.append("<td align='left' style='font:normal 14px Arial;'><em><p>Dear " + customerName
						+ ",<br /><br />");
			} else if (StringUtils.isNotBlank(merchnatBusinessName)) {
				content.append("<td align='left' style='font:normal 14px Arial;'><em><p>Dear " + merchnatBusinessName
						+ ",<br /><br />");
			} else {
				content.append("<td align='left' style='font:normal 14px Arial;'><em><p>Dear User," + ",<br /><br />");
			}

			content.append("<br /><br />" + messageBody.split(",")[0] + ".<br />" + messageBody.split(",")[1]
					+ "<br /><br /><br />Regards,<br /> Team BPGATE" + "</em></p>");

			content.append("</p></em>");
			content.append(
					"<table width='20%' border='0' align='left' cellpadding='0.' cellspacing='0' class='product-spec1'>");
			content.append("<tr>");
			content.append("</tr>");
			content.append("</table>");
			content.append("<br />");
			content.append("<br />");
			makeFooter();
			body = content.toString();
		} catch (Exception exception) {
			logger.error("Exception", exception);
		}
		return body;
	}

	@SuppressWarnings({ "unused", "static-access" })
	public String resetPasswordSecurityAlert(String customerName, String merchnatBusinessName, String messageBody) {
		String body = null;
		StringBuilder content = new StringBuilder();
		try {
			makeHeader();
			content.append("<tr>");
			if (StringUtils.isNotBlank(customerName)) {
				content.append("<td align='left' style='font:normal 14px Arial;'><em><p>Dear " + customerName
						+ ",<br /><br />");
			} else if (StringUtils.isNotBlank(merchnatBusinessName)) {
				content.append("<td align='left' style='font:normal 14px Arial;'><em><p>Dear " + merchnatBusinessName
						+ ",<br /><br />");
			} else {
				content.append("<td align='left' style='font:normal 14px Arial;'><em><p>Dear User," + ",<br /><br />");
			}

			content.append("<br /><br />" + messageBody.split(",")[0] + ".<br />" + messageBody.split(",")[1]
					+ "<br /><br /><br />Regards,<br /> Team BPGATE" + "</em></p>");

			content.append("</p></em>");
			content.append(
					"<table width='20%' border='0' align='left' cellpadding='0.' cellspacing='0' class='product-spec1'>");
			content.append("<tr>");
			content.append("</tr>");
			content.append("</table>");
			content.append("<br />");
			content.append("<br />");
			makeFooter();
			body = content.toString();
		} catch (Exception exception) {
			logger.error("Exception", exception);
		}
		return body;
	}

	@SuppressWarnings({ "unused", "static-access" })
	public String changePasswordConfirmation(String customerName, String merchnatBusinessName, String messageBody) {
		String body = null;
		StringBuilder content = new StringBuilder();
		try {
			makeHeader();
			content.append("<tr>");
			if (StringUtils.isNotBlank(customerName)) {
				content.append("<td align='left' style='font:normal 14px Arial;'><em><p>Dear " + customerName
						+ ",<br /><br />");
			} else if (StringUtils.isNotBlank(merchnatBusinessName)) {
				content.append("<td align='left' style='font:normal 14px Arial;'><em><p>Dear " + merchnatBusinessName
						+ ",<br /><br />");
			} else {
				content.append("<td align='left' style='font:normal 14px Arial;'><em><p>Dear User," + ",<br /><br />");
			}

			content.append("<br /><br /> " + messageBody.split(",")[0] + ".<br />" + messageBody.split(",")[1]
					+ "<br /><br /><br />Regards,<br /> Team BPGATE" + "</em></p>");

			content.append("</p></em>");
			content.append(
					"<table width='20%' border='0' align='left' cellpadding='0.' cellspacing='0' class='product-spec1'>");
			content.append("<tr>");
			content.append("</tr>");
			content.append("</table>");
			content.append("<br />");
			content.append("<br />");
			makeFooter();
			body = content.toString();
		} catch (Exception exception) {
			logger.error("Exception", exception);
		}
		return body;
	}

	public String passwordReset(String accountValidationID, String url) {
		String body = null;
		StringBuilder content = new StringBuilder();
		try {
//			makeHeader();
			content.append("<tr>");
			content.append("<td align='left' style='font:normal 14px Arial;'><em><p>Dear User,<br /><br />");
			content.append("There was recently a request to change the password for your account.<br /><br />");
			content.append(
					"If you requested this password change, please click on below button to reset your account password.<br />");
			content.append("</p></em>");
			content.append(
					"<table width='20%' border='0' align='left' cellpadding='0.' cellspacing='0' class='product-spec1'>");
			content.append("<tr>");
			content.append("<td height='30' align='center' valign='middle' bgcolor='#fff' color='#fff'><a href='"
					+ url + "?id=" + accountValidationID + "'>Reset Password</a></td>");
			content.append("</tr>");
			content.append("</table>");
			content.append("<br />");
			content.append("<br />");
			makeFooter();
			body = content.toString();
		} catch (Exception exception) {
			logger.error("Exception", exception);
		}
		return body;
	}

	public String passwordUpdate() {
		String body = null;
		StringBuilder content = new StringBuilder();
		try {
			makeHeader();
			content.append("<tr>");
			content.append("<td align='left' style='font:normal 14px Arial;'><p><em>Dear User,<br /><br />");
			content.append("Your CRM password has been updated successfully.<br />");
			content.append("</p>");
			makeFooter();
			body = content.toString();
		} catch (Exception exception) {
			logger.error("Exception", exception);
		}
		return body;
	}

	public String invoiceLink(String url, String customerName, String merchant) {
		String body = null;
		StringBuilder content = new StringBuilder();
		try {
			makeHeader();
			content.append("<tr>");
			content.append(
					"<td align='left' style='font:normal 14px Arial;'><p><em>Dear '" + customerName + "',<br /><br />");
			content.append("Greetings!<br /><br />");
			content.append(
					"We would request your payment for the Invoice already shared with you. Pls click here  <a href='"
							+ url + "'>Pay Now</a> for making the Payment.<br /><br />");
			content.append("Please feel free to contact us in case any changes are needed.<br /><br />");
			content.append("Looking forward to Serve You!<br /><br />");
			content.append("<br /><br />");
			content.append("Regards<br /><br />");
			content.append("Team '" + merchant + "'<br /><br />");
			content.append("</tr>");

			makeFooter();
			body = content.toString();
		} catch (Exception exception) {
			logger.error("Exception", exception);
		}
		return body;
	}

	public String invoiceSuccessTxn(String emailID, String subject, String customerName, String merchnatBusinessName,
									String amount, String currencyCode, String invoiceNo) {
		String body = null;
		StringBuilder content = new StringBuilder();
		try {
			makeHeader();
			content.append("<tr>");
			content.append(
					"<td align='left' style='font:normal 15px Arial;'><p><em>Dear  " + customerName + ",<br /><br />");
			content.append(
					"Your payment of " + currencyCode + " " + amount + " is successfully processed for invoice number "
							+ invoiceNo + " at " + merchnatBusinessName + ".");
			content.append(
					"<br /><br />Please contact us for any further queries.<br /><br />Regards, <br />Team BPGATE");
			content.append(
					"<table width='20%' border='0' align='left' cellpadding='0.' cellspacing='0' class='product-spec1'>");
			content.append("<tr>");
			content.append("</tr>");
			content.append("</table>");
			makeFooter();
			body = content.toString();
		} catch (Exception exception) {
			logger.error("Exception", exception);
		}
		return body;
	}

	public String invoiceSuccessTxnMerchant(String customerName, String merchantBusinessName, String amount,
											String currencyCode, String invoiceNo, String orderId, String pgRefNum, String custPhone) {
		String body = null;
		StringBuilder content = new StringBuilder();
		try {
			makeHeader();
			content.append("<tr>");
			content.append("<td align='left' style='font:normal 15px Arial;'><p><em>Dear " + merchantBusinessName
					+ ",<br /><br />");
			content.append("Payment of " + currencyCode + " " + amount + "/- reciveved against Invoice Number "
					+ invoiceNo + ".<br /><br /><br />Following are the details:");
			content.append("<br />Order ID : " + orderId);
			content.append("<br />PG REF NUM : " + pgRefNum);
			content.append("<br />Customer Name : " + customerName);
			content.append("<br />Customer Phone : " + custPhone);
			content.append(
					"<br /><br /><br />Please contact us for any further queries.<br /><br />Regards, <br />Team BPGATE");
			content.append(
					"<table width='20%' border='0' align='left' cellpadding='0.' cellspacing='0' class='product-spec1'>");
			content.append("<tr>");
			content.append("</tr>");
			content.append("</table>");
			makeFooter();
			body = content.toString();
		} catch (Exception exception) {
			logger.error("Exception", exception);
		}
		return body;
	}

	public String documentUpload() {
		String body = null;
		try {
			StringBuilder contant = new StringBuilder();
			contant.append("Dear Merchant," + "\n\n");
			contant.append("Your Document uploaded successfully" + "\n\n");
			contant.append("Assuring you of our best service at all times.\n");
			contant.append("In case any Further query, please do tell us at ");
			contant.append(EmailerConstants.CONTACT_US_EMAIL.getValue());
			contant.append(" \n\n We Care!" + "\n");
			contant.append(EmailerConstants.GATEWAY.getValue());
			contant.append(" Team");
			body = contant.toString();
		} catch (Exception exception) {
			logger.error("Exception", exception);
		}
		return body;
	}

	public String transactionAuthenticationLink(Map<String, String> fields, String url, String txnId) {
		String body = null;
		StringBuilder content = new StringBuilder();
		try {
			makeHeader();
			content.append("<tr>");
			content.append("<td align='left' style='font:normal 14px Arial;'><p><em>Dear Customer,<br />");
			content.append("<br />");
			content.append("Thank you for paying with ");
			content.append(EmailerConstants.GATEWAY.getValue());
			content.append(".<br /><br />");
			content.append("Please click on the below button to authenticate your recent transaction at ");
			content.append(EmailerConstants.GATEWAY.getValue());
			content.append(".</em><br />");
			content.append(
					"<table width='20%' border='0' align='left' cellpadding='0.' cellspacing='0' class='product-spec1'>");
			content.append("<tr>");
			content.append("<td height='30' align='center' valign='middle' bgcolor='#fff' color='#fff'><a href='"
					+ url + "?id=" + txnId + "'>Authentication</a></td>");
			content.append("</tr>");
			content.append("</table></p>");
			content.append("<br /><br />");
			makeFooter();
			body = content.toString();
		} catch (Exception exception) {
			logger.error("Exception", exception);
		}
		return body;
	}

	public String remittanceEmailBody(String utr, String payId, String merchant, String datefrom, String netAmount,
									  String remittedDate, String remittedAmount, String status) {
		String body = null;
		StringBuilder content = new StringBuilder();
		try {
			makeHeader();
			content.append("<tr>");
			content.append("<td align='left' style='font:normal 14px Arial;'>");
			content.append("<p><em>Dear Customer,</em></p>");
			content.append(
					"<p><table width='96%' border='0' align='center' cellpadding='0.' cellspacing='0' class='product-spec'>");
			content.append("<tr>");
			content.append("<th colspan='2' align='left' valign='middle'>Remittance Processed Successful. </th>");
			content.append("</tr>");
			content.append("<tr>");
			content.append("<td width='27%'>Merchant Name :- </td>");
			content.append("<td width='73%'>" + merchant + " </td>");
			content.append("</tr>");
			content.append("<tr>");
			content.append("<td>Transaction Date :-  </td>");
			content.append("<td>" + datefrom + "</td>");
			content.append("</tr>");
			content.append("<tr>");
			content.append("<td>Remitted Amount :- </td>");
			content.append("<td>" + remittedAmount + "</td>");
			content.append("</tr>");
			content.append("<tr>");
			content.append("<td>Remitted Date :- </td>");
			content.append("<td>" + remittedDate + "</td>");
			content.append("</tr>");
			content.append("<tr>");
			content.append("<td>UTR :- </td>");
			content.append("<td>" + utr + "</td>");
			content.append("</tr>");
			content.append("</table></p>");
			makeFooter();
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
		content.append("<title>Payment Acknowledgement</title>");
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
				+ propertiesManager.getSystemProperty("emailerLogoURL") + "' width='45' height='45' /></td>");
		content.append("</tr>");
		return content;
	}

	private StringBuilder makeFooter() {
		StringBuilder content = new StringBuilder();
		content.append("<p><em>Assuring you of our best services at all times.</em></p><br />");
		content.append(
				"<p><em>If you have any questions about your transaction or any other matter, please feel free to contact us at ");
		content.append(EmailerConstants.CONTACT_US_EMAIL.getValue());
		content.append(".</em></p>");
		//content.append("<p></p></td>");
		//content.append("</tr>");
		content.append("<tr>");
		content.append(
				"<td align='left' valign='middle' bgcolor='#fbfbfb' style='border-top:1px solid #d4d4d4; border-bottom-left-radius:10px;");
		content.append(
				"border-bottom-right-radius:10px;'><table width='100%' border='0' cellspacing='0' cellpadding='0.'>");
		content.append("<tr>");
		content.append("<p><em><td align='left' valign='middle' style='font-family:Arial;'><strong>We Care!<br /></p></em>");
		content.append(EmailerConstants.GATEWAY.getValue());
		content.append(" Team</strong></td><br />");
		content.append("<p><td align='right' valign='bottom' style='font:normal 12px Arial;'>© 2024 ");
		content.append(EmailerConstants.WEBSITE.getValue());
		content.append(" All rights reserved.</td></p>");
		content.append("</tr>");
		content.append("</table></td>");
		content.append("</tr>");
		content.append("</table>");
		content.append("</body>");
		content.append("</html>");
		return content;
	}

	private StringBuilder makeHeaderNotifier() {
		StringBuilder content = new StringBuilder();
		content.append("<html xmlns='http://www.w3.org/1999/xhtml'>");
		content.append("<head>");
		content.append("<meta http-equiv='Content-Type' content='text/html; charset=utf-8'/>");
		content.append("<title>New Merchant SignUp</title>");
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
				+ PropertiesManager.propertiesMap.get("emailerLogoURL") + "' width='277' height='45' /></td>");
		content.append("</tr>");
		return content;
	}

	private StringBuilder makeFooterNotifier() {
		StringBuilder content = new StringBuilder();
		content.append("<p><em>Assuring you of our best services at all times.</em></p>");
		content.append(
				"<p><em>If you have any questions about your transaction or any other matter, please feel free to contact us at ");
		content.append(EmailerConstants.CONTACT_US_EMAIL.getValue());
		content.append(".</em></p>");
		content.append("<p></p></td>");
		content.append("</tr>");
		content.append("<tr>");
		content.append(
				"<td align='left' valign='middle' bgcolor='#fbfbfb' style='border-top:1px solid #d4d4d4; border-bottom-left-radius:10px;");
		content.append(
				"border-bottom-right-radius:10px;'><table width='100%' border='0' cellspacing='0' cellpadding='0.'>");
		content.append("<tr>");
		content.append("<td align='left' valign='middle' style='font-family:Arial;'><strong>We Care!<br />");
		content.append(EmailerConstants.GATEWAY.getValue());
		content.append(" Team</strong></td>");
		content.append("<td align='right' valign='bottom' style='font:normal 12px Arial;'>© 2019-2020 ");
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

	public String serviceTaxUpdate(String emailContent) {
		String body = null;
		StringBuilder content = new StringBuilder();
		try {
			content.append("<tr>");
			content.append("<td align='left' style='font:normal 14px Arial;'><p><em>Dear Merchant,<br /><br />");
			content.append(
					"<table width='20%' border='0' align='left' cellpadding='0.' cellspacing='0' class='product-spec1'>");
			content.append("<tr>");
			content.append("<br /><br />" + emailContent + "</em></p>");
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

	public String AttachmentFileEmail(String fileName) {
		String body = null;
		StringBuilder content = new StringBuilder();
		try {
			content.append("<tr>");
			content.append("<td align='left' style='font:normal 14px Arial;'><p><em>Dear Bank,<br /><br />");
			content.append(
					"<table width='20%' border='0' align='left' cellpadding='0.' cellspacing='0' class='product-spec1'>");
			content.append("<tr>");
			content.append("<br /><br />" + fileName + "</em></p>");
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


	public String merchantSignupNotifier(MerchantSignupNotifier merchantSignupNotifier) {

		String body = null;
		StringBuilder content = new StringBuilder();
		try {
			makeHeaderNotifier();
			content.append("<tr>");
			content.append("<td align='left' style='font:normal 14px Arial;'><p><em>Dear Admin ,<br /><br />");
			content.append("A new merchant been has registered with ");
			content.append(EmailerConstants.COMPANY.getValue());
			content.append("<br /><br />");
			content.append("Merchant Name : " + merchantSignupNotifier.getMerchantName() + "<br /><br />");
			content.append("Merchant Phone : " + merchantSignupNotifier.getMerchantPhone() + "<br /><br />");
			content.append("Merchant Email : " + merchantSignupNotifier.getMerchantEmail() + "<br /><br />");
			content.append("<br /><br />");
			makeFooterNotifier();
			body = content.toString();

		} catch (Exception exception) {
			logger.error("Exception", exception);
		}
		return body;
	}

	public String databaseFailureNotifier() {
		String body = null;
		StringBuilder content = new StringBuilder();
		try {
			makeHeaderNotifier();
			content.append("<tr>");
			content.append("<td align='left' style='font:normal 14px Arial;'><p><em>Dear Team ,<br /><br />");
			content.append("The status monitoring application is not able to reach mongoDB service, please check.");
			content.append("<br /><br />");
			makeFooterNotifier();
			body = content.toString();

		} catch (Exception exception) {
			logger.error("Exception", exception);
		}
		return body;
	}

	public String transactionsFetchFailureNotifier(String timeSpan) {
		String body = null;
		StringBuilder content = new StringBuilder();
		try {
			makeHeaderNotifier();
			content.append("<tr>");
			content.append("<td align='left' style='font:normal 14px Arial;'><p><em>Dear Team ,<br /><br />");
			content.append("No transactions has been received in last " + timeSpan
					+ "minutes. Please verify the PG interface.");
			content.append("<br /><br />");
			makeFooterNotifier();
			body = content.toString();

		} catch (Exception exception) {
			logger.error("Exception", exception);
		}
		return body;
	}

	public String totalSuccessRateFailureNotifier(String thresholdRate, String actualRate, String timeSpan,
												  String SuccessTransaction, String TotalTransactions) {

		String body = null;
		StringBuilder content = new StringBuilder();
		try {
			makeHeaderNotifier();
			content.append("<tr>");
			content.append("<td align='left' style='font:normal 14px Arial;'><p><em>Dear Team ,<br /><br />");
			content.append("Total SuccessRate in last " + timeSpan + " minutes is " + actualRate
					+ "% which is less than the threshold success rate " + thresholdRate
					+ "%. Please verify the PG interface.");
			content.append(" <br /><br />Total No of Transactions  =" + TotalTransactions);
			content.append("<br /><br />Total No of Success Transactions  =" + SuccessTransaction);
			content.append("<br /><br />");
			makeFooterNotifier();
			body = content.toString();

		} catch (Exception exception) {
			logger.error("Exception", exception);
		}
		return body;
	}

	public String individualSuccessRateFailureNotifier(String thresholdRate, String actualRate, String timeSpan,
													   String individualKey, String SuccessTransaction, String TotalTransactions) {
		String[] strArr = individualKey.split("-");
		String acqName = strArr[0];
		String paymentType = strArr[1];
		String mopType = strArr[2];
		String merchantName = strArr[3];
		String body = null;
		StringBuilder content = new StringBuilder();
		try {
			makeHeaderNotifier();
			content.append("<tr>");
			content.append("<td align='left' style='font:normal 14px Arial;'><p><em>Dear Team ,<br /><br />");
			content.append("SuccessRate of " + merchantName + " merchant" + " for " + acqName + " acquirer with "
					+ paymentType + " paymentType and " + mopType + " mop type in last " + timeSpan + " minutes is "
					+ actualRate + "% which is less than the threshold success rate " + thresholdRate
					+ "%. Please verify the PG interface.");
			content.append(" <br /><br />Total No of Transactions  =" + TotalTransactions);
			content.append("<br /><br />Total No of Success Transactions  =" + SuccessTransaction);
			content.append("<br /><br />");
			makeFooterNotifier();
			body = content.toString();

		} catch (Exception exception) {
			logger.error("Exception", exception);
		}
		return body;
	}

	public String nodalTransactionDisputeRaiseNotifier(String payId, String paymentType, String creationDate,
													   String timeBefore) {
		String body = null;
		StringBuilder content = new StringBuilder();
		try {
			makeHeaderNotifier();
			content.append("<tr>");
			content.append("<td align='left' style='font:normal 14px Arial;'><p><em>Dear Team ,<br /><br />");
			content.append("The nodal transaction with Pay Id " + payId + " and PaymentType " + paymentType
					+ " was initiated on " + creationDate + ".It's been " + timeBefore
					+ " we haven't got final status even after checking with them. Kindly raise a dispute with the bank for the same.");
			content.append("<br /><br />");
			makeFooterNotifier();
			body = content.toString();

		} catch (Exception exception) {
			logger.error("Exception", exception);
		}
		return body;
	}

	public String repeatedMopTypeEmail(String messageBody) {
		String body = null;
		StringBuilder content = new StringBuilder();
		try {
			makeHeader();
			content.append("<tr>");
			content.append("<td align='left' style='font:normal 14px Arial;'><em><p>Dear User," + "<br />");

			content.append("<br /><br />" + messageBody + "<br /><br /><br />Regards,<br /> Team BPGATE" + "</em></p>");

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

	public String firstTxnAlertEmail(String businessName) {

		String body = null;
		StringBuilder content = new StringBuilder();
		String messageBody = StringUtils.join("We would like to inform you that ", businessName, " started transacting on ", dateTimeFormat.format(new Date()));
		try {
			makeHeader();
			content.append("<tr>");
			content.append("<td align='left' style='font:normal 14px Arial;'><em><p>Dear Business team," + "<br />");

			content.append("<br /><br />" + messageBody + "<br /><br /><br />Regards,<br /> Team BPGATE" + "</em></p>");

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

	public String velocityBlockingEmail(String messageBody) {
		String body = null;
		StringBuilder content = new StringBuilder();
		try {
			makeHeader();
			content.append("<tr>");
			content.append("<td align='left' style='font:normal 14px Arial;'><em><p>Dear User," + "<br />");

			content.append("<br /><br />" + messageBody + "<br /><br /><br />Regards,<br /> Team BPGATE" + "</em></p>");

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

	public String FailedCntExceedBlockingEmail(String acquirerName, String paymentType, String mopType) {
		String body = null;
		StringBuilder content = new StringBuilder();
		try {
			makeHeader();
			content.append("<tr>");
			content.append("<td align='left' style='font:normal 15px Arial;'><p><em>Dear Team,<br /><br />");
			content.append("The Failed count limit exceeded for acquirer name : "+ acquirerName +".<br /><br /><br />Following are the details:");
			content.append("<br />Payment Type : " + paymentType);
			content.append("<br />Mop Type : " + mopType);
			content.append(
					"<br /><br /><br />Please check with acquirer.<br /><br />Regards, <br />Team BPGATE");
			content.append(
					"<table width='20%' border='0' align='left' cellpadding='0.' cellspacing='0' class='product-spec1'>");
			content.append("<tr>");
			content.append("</tr>");
			content.append("</table>");
			makeFooter();
			body = content.toString();
		} catch (Exception exception) {
			logger.error("Exception", exception);
		}
		return body;
	}

	public String merchantFraudRuleAlert(Map<String, String> reqMap) {
		String body = null;
		StringBuilder content = new StringBuilder();
		try {
			makeHeader();
			content.append("<tr>");
			content.append("<td align='left' style='font:normal 15px Arial;'><p><em>Dear Team,<br /><br />");
			content.append("The "+ reqMap.get("merchantName") +" merchant has been break FRM Rule.<br /><br /><br />Following are the details:");
			content.append("<br />Merchant Name : " + reqMap.get("merchantName"));
			content.append("<br />Fraud Type : " + reqMap.get("fraudType"));
			content.append("<br />OrderId : " + reqMap.get("orderId"));
			content.append("<br />Payment Type : " + reqMap.get("paymentType"));
			content.append(
					"<br /><br /><br />Please check with merchant.<br /><br />Regards, <br />Team BPGATE");
			content.append(
					"<table width='20%' border='0' align='left' cellpadding='0.' cellspacing='0' class='product-spec1'>");
			content.append("<tr>");
			content.append("</tr>");
			content.append("</table>");
			makeFooter();
			body = content.toString();
		} catch (Exception exception) {
			logger.error("Exception", exception);
		}
		return body;
	}

	public String acquirerDownURLAlert(Map<String, String> reqMap) {
		String body = null;
		StringBuilder content = new StringBuilder();
		try {
			makeHeader();
			content.append("<tr>");
			content.append("<td align='left' style='font:normal 15px Arial;'><p><em>Dear Team,<br /><br />");
			content.append("The "+ reqMap.get("aqcquirerName") +" acquirer has been down.<br /><br /><br />Following are the details:");
			content.append("<br />Acquirer Name : " + reqMap.get("aqcquirerName"));
			content.append("<br />Payment Type : " + reqMap.get("paymentType"));
			content.append("<br />Failed Count : " + reqMap.get("failedCnt"));
			content.append("<br />Acquirer DownTime : " + reqMap.get("downTime"));
			content.append(
					"<br /><br /><br />Please check with Acquirer.<br /><br />Regards, <br />Team BPGATE");
			content.append(
					"<table width='20%' border='0' align='left' cellpadding='0.' cellspacing='0' class='product-spec1'>");
			content.append("<tr>");
			content.append("</tr>");
			content.append("</table>");
			makeFooter();
			body = content.toString();
		} catch (Exception exception) {
			logger.error("Exception", exception);
		}
		return body;
	}



}