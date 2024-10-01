package com.pay10.notification.email.emailBuilder;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.user.MerchantSignupNotifier;
import com.pay10.commons.user.ResponseObject;
import com.pay10.commons.user.User;
import com.pay10.commons.user.UserDao;
import com.pay10.commons.user.UserType;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.CrmFieldConstants;
import com.pay10.commons.util.EmailerConstants;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.PropertiesManager;
import com.pay10.notification.email.emailBodyCreater.EmailBodyCreator;

/**
 * @author Neeraj
 *
 */
@Component
public class EmailBuilder {

	private Logger logger = LoggerFactory.getLogger(EmailBuilder.class.getName());
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
	private EmailBodyCreator emailBodyCreator;

	@Autowired
	@Qualifier("propertiesManager")
	private PropertiesManager propertiesManager;

	@Autowired
	Emailer emailer;

	public void transactionEmailer(Map<String, String> responseMap, String senderType, User user) throws Exception {
		String heading = null;
		String message = null;
		String sendToEmail = null;
		String defaultMerchantEmail = null;
		try {
			if (senderType.equals(UserType.MERCHANT.toString())) {
				heading = CrmFieldConstants.MERCHANT_HEADING.getValue();
				message = CrmFieldConstants.MERCHANT_MESSAGE.getValue();
				sendToEmail = user.getTransactionEmailId();
				defaultMerchantEmail = user.getEmailId();
				if (responseMap.get(FieldType.RESPONSE_CODE.getName()).equals(Constants.SUCCESS_CODE.getValue())) {
					body = emailBodyCreator.bodyTransactionEmail(responseMap, heading, message);
					emailer.sendEmail(body, message, defaultMerchantEmail, sendToEmail, isEmailExceptionHandlerFlag());
				} else {
					body = emailBodyCreator.transactionFailled(responseMap);
					emailer.sendEmail(body, message, defaultMerchantEmail, sendToEmail, isEmailExceptionHandlerFlag());
				}

			}
		} catch (Exception exception) {
			logger.error("Exception", exception);
		}
	}

	// transactionCustomerEmailerFlag()
	public void transactionCustomerEmail(Map<String, String> responseMap) throws Exception {
		String heading = null;
		String message = null;
		String sendToEmail = null;
		try {
			heading = CrmFieldConstants.CUSTOMER_HEADING.getValue();
			message = CrmFieldConstants.CUSTOMER_MESSAGE.getValue();
			sendToEmail = responseMap.get(FieldType.CUST_EMAIL.getName());

			if (responseMap.get(FieldType.RESPONSE_CODE.getName()).equals(Constants.SUCCESS_CODE.getValue())) {
				body = emailBodyCreator.bodyTransactionEmail(responseMap, heading, message);
				subject = EmailerConstants.COMPANY.getValue() + Constants.PAYMENT_RECEIVED_ACKNOWLEDGEMENT.getValue();
				toEmail = sendToEmail;
				setEmailExceptionHandlerFlag(false);
				emailer.sendEmail(getBody(), getSubject(), getToEmail(), getEmailToBcc(),
						isEmailExceptionHandlerFlag());
				// transaction Failed Acknowledgement
			} else {
				body = emailBodyCreator.transactionFailled(responseMap);
				subject = EmailerConstants.COMPANY.getValue() + Constants.PAYMENT_RECEIVED_ACKNOWLEDGEMENT.getValue();
				toEmail = sendToEmail;
				setEmailExceptionHandlerFlag(false);
				emailer.sendEmail(getBody(), getSubject(), getToEmail(), getEmailToBcc(),
						isEmailExceptionHandlerFlag());
			}

		} catch (Exception exception) {
			logger.error("Exception", exception);
		}
	}

	// Preparing Refund Transaction Email for Customer
	public void transactionRefundEmail(Map<String, String> fields, String senderType, String getToEmail,
									   String businessName) throws Exception {
		String headingRefund = null;
		try {
			if (senderType.equals(UserType.MERCHANT.toString())) {
				headingRefund = CrmFieldConstants.MERCHANT_HEADING.getValue();
			} else {
				headingRefund = CrmFieldConstants.CUSTOMER_HEADING.getValue();
			}
			body = emailBodyCreator.refundEmailBody(fields, headingRefund, businessName);
			subject = EmailerConstants.COMPANY.getValue() + Constants.REFUND_FOR_ODER_ID.getValue()
					+ fields.get(FieldType.ORDER_ID.getName());
			toEmail = getToEmail;
			setEmailExceptionHandlerFlag(false);
			emailer.sendEmail(getBody(), getSubject(), getToEmail(), getEmailToBcc(), isEmailExceptionHandlerFlag());
		} catch (Exception exception) {
			logger.error("Exception", exception);
		}
	}

	// Preparing Email for merchant registration validation
	public void emailValidator(ResponseObject responseObject) throws Exception {
		try {
			User user = userDao.findByEmailId(responseObject.getEmail());
			String url = PropertiesManager.propertiesMap.get(Constants.EMAIL_VALIDATORURL.getValue());
			if(StringUtils.equalsAnyIgnoreCase(
					user.getUserType().toString(),
					UserType.MERCHANT.toString(),
					UserType.RESELLER.toString(),
					UserType.SMA.toString(),
					UserType.SUBUSER.toString(),
					UserType.MA.toString(),
					UserType.Agent.toString())
			){
				url= PropertiesManager.propertiesMap.get(Constants.EMAIL_MERCHANT_VALIDATOR_URL.getValue());
			}
			//vijay............................................
			body = emailBodyCreator.accountValidation(responseObject.getAccountValidationID(),url);
			subject = Constants.ACCOUNT_VALIDATION_EMAIL.getValue() + EmailerConstants.COMPANY.getValue();
			toEmail = responseObject.getEmail();
			setEmailExceptionHandlerFlag(true);
			emailer.sendEmail(getBody(), getSubject(), getToEmail(), getEmailToBcc(), isEmailExceptionHandlerFlag());
		} catch (Exception exception) {
			logger.error("Exception", exception);
		}
	}

	// Email (with create password link) to sub-admin on creation for setting
	// password
	public void emailAddUser(ResponseObject responseObject) throws Exception {
		try {
			User user = userDao.findByEmailId(responseObject.getEmail());
			String url1 = PropertiesManager.propertiesMap.get(Constants.EMAIL_VALIDATORURL.getValue());
			String url2 = PropertiesManager.propertiesMap.get(Constants.CREATE_PASSWORD_URL.getValue());
			if(StringUtils.equalsAnyIgnoreCase(
					user.getUserType().toString(),
					UserType.MERCHANT.toString(),
					UserType.RESELLER.toString(),
					UserType.SMA.toString(),
					UserType.SUBUSER.toString(),
					UserType.MA.toString(),
					UserType.Agent.toString())
			){

				url1= PropertiesManager.propertiesMap.get(Constants.EMAIL_MERCHANT_VALIDATOR_URL.getValue());
				url2= PropertiesManager.propertiesMap.get(Constants.CREATE_MERCHANT_PASSWORD_URL.getValue());
			}
			//vijay
			body = emailBodyCreator.addUser(responseObject.getName(), responseObject.getAccountValidationID(),url1,url2);
			subject = Constants.ACCOUNT_VALIDATION_EMAIL.getValue() + EmailerConstants.COMPANY.getValue();
			//subject = Constants.SET_PASSWORD_EMAIL.getValue() + EmailerConstants.COMPANY.getValue();
			toEmail = responseObject.getEmail();
			setEmailExceptionHandlerFlag(false);
			emailer.sendEmail(getBody(), getSubject(), getToEmail(), getEmailToBcc(), isEmailExceptionHandlerFlag());
		} catch (Exception exception) {
			logger.error("Exception", exception);
		}
	}


	public void emailMerchantTransactionDetails(ResponseObject responseObject) throws Exception {
		try {
			body = emailBodyCreator.merchantTransactionDetails(
					responseObject.getName(),
					responseObject.getPayId(),
					responseObject.getSalt(),
					responseObject.getMerchantHostedEncryptionKey(),
					responseObject.getRequestUrl(),
					responseObject.getEmail()

			);
			subject = Constants.MERCHANT_TRANSACTION_DETAILS.getValue() + EmailerConstants.COMPANY.getValue();
			toEmail = responseObject.getEmail();
			setEmailExceptionHandlerFlag(false);
			emailer.sendEmail(getBody(), getSubject(), getToEmail(), getEmailToBcc(), isEmailExceptionHandlerFlag());
		} catch (Exception exception) {
			logger.error("Exception", exception);
		}
	}

	// Preparing Email for password change to merchant
	public void emailPasswordChange(ResponseObject responseObject, String emailId) throws Exception {
		try {
			body = emailBodyCreator.passwordUpdate();
			subject = EmailerConstants.COMPANY.getValue() + Constants.CRM_PASSWORD_CHANGE_ACKNOWLEDGEMENT.getValue();
			toEmail = emailId;
			setEmailExceptionHandlerFlag(false);
			emailer.sendEmail(getBody(), getSubject(), getToEmail(), getEmailToBcc(), isEmailExceptionHandlerFlag());
		} catch (Exception exception) {
			logger.error("Exception", exception);
		}
	}

	// Preparing Email for password reset to merchant
	public void passwordResetEmail(String accountValidationKey, String emailId) throws Exception {
		try {
			User user = userDao.findByEmailId(emailId);
			String url = PropertiesManager.propertiesMap.get(Constants.RESET_PASSWORD_URL.getValue());
			if(StringUtils.equalsAnyIgnoreCase(
					user.getUserType().toString(),
					UserType.MERCHANT.toString(),
					UserType.RESELLER.toString(),
					UserType.SMA.toString(),
					UserType.SUBUSER.toString(),
					UserType.MA.toString(),
					UserType.Agent.toString())
			){
				url= PropertiesManager.propertiesMap.get(Constants.RESET_MERCHANT_PASSWORD_URL.getValue());
			}
			body = emailBodyCreator.passwordReset(accountValidationKey,url);
			subject = Constants.RESET_PASSWORD_EMAIL.getValue() + EmailerConstants.COMPANY.getValue();
			toEmail = emailId;
			setEmailExceptionHandlerFlag(true);
			emailer.sendEmail(getBody(), getSubject(), getToEmail(), getEmailToBcc(), isEmailExceptionHandlerFlag());
		} catch (Exception exception) {
			logger.error("Exception", exception);
		}
	}

	// Preparing Email for invoice
	public void invoiceLinkEmail(String url, String emailId, String customerName, String merchant) {
		try {
			logger.info("Creating email body in email service for Email-ID : " + emailId);
			body = emailBodyCreator.invoiceLink(url, customerName, merchant);
			subject = Constants.INVOICE_LINK.getValue();
			toEmail = emailId;
			setEmailExceptionHandlerFlag(true);
			try {
				emailer.sendEmail(getBody(), getSubject(), getToEmail(), getEmailToBcc(),
						isEmailExceptionHandlerFlag());
			} catch (Exception exception) {
				responseMessage.append(ErrorType.EMAIL_ERROR.getResponseMessage());
				responseMessage.append(emailId);
				responseMessage.append("\n");
				logger.debug("Error!! Unable to send email Emailer fail: " + exception);
				return;
			}
		} catch (Exception exception) {
			logger.error("Unable to send email at : ", emailId);
			logger.error(exception.getMessage());
		}
	}

	// prepare email for invoice success transaction
	public void invoiceSuccessTxnEmail(String emailID, String subject, String customerName, String merchnatBusinessName,
									   String amount, String currencyCode, String invoiceNo) {
		try {
			body = emailBodyCreator.invoiceSuccessTxn(emailID, subject, customerName, merchnatBusinessName, amount,
					currencyCode, invoiceNo);
			subject = "Invoice Transaction Successful";
			toEmail = emailID;
			setEmailExceptionHandlerFlag(true);
			try {
				emailer.sendEmail(getBody(), subject, getToEmail(), getEmailToBcc(), isEmailExceptionHandlerFlag());
			} catch (Exception exception) {
				responseMessage.append(ErrorType.EMAIL_ERROR.getResponseMessage());
				responseMessage.append(emailID);
				responseMessage.append("\n");
				logger.debug("Error!! Unable to send Invoice Txn email Emailer fail: " + exception);
				return;
			}
		} catch (Exception exception) {
			logger.error("Exception", exception);
		}
	}

	// prepare email for invoice success transaction
	public void invoiceSuccessTxnEmailMerchant(Map<String, String> reqMap) {
		String emailId = reqMap.get("emailId");
		String subject = reqMap.get("subject");
		String customerName = reqMap.get("merchant");
		String merchantBusinessName = reqMap.get("businessName");
		String amount = reqMap.get("totalAmount");
		String currencyCode = reqMap.get("currencyCode");
		String invoiceNo = reqMap.get("invoiceNo");
		String orderId = reqMap.get("orderId");
		String pgRefNum = reqMap.get("pgRefNum");
		String custPhone = reqMap.get("phone");

		try {
			body = emailBodyCreator.invoiceSuccessTxnMerchant(customerName, merchantBusinessName, amount, currencyCode,
					invoiceNo, orderId, pgRefNum, custPhone);
			toEmail = emailId;
			setEmailExceptionHandlerFlag(true);
			try {
				emailer.sendEmail(getBody(), subject, getToEmail(), getEmailToBcc(), isEmailExceptionHandlerFlag());
			} catch (Exception exception) {
				responseMessage.append(ErrorType.EMAIL_ERROR.getResponseMessage());
				responseMessage.append(emailId);
				responseMessage.append("\n");
				logger.debug("Error!! Unable to send Invoice Txn email Emailer fail: " + exception);
			}
		} catch (Exception exception) {
			logger.error("Exception", exception);
		}
	}

	// Preparing Transaction Email
	public void transactionAuthenticationEmail(Map<String, String> fields) throws Exception {
		try {
			body = emailBodyCreator.transactionAuthenticationLink(fields,
					propertiesManager.getEmailProperty(Constants.EMAIL_TRANSACTION_AUTHENTICATION.getValue()),
					fields.get(FieldType.TXN_ID.getName()));
			subject = EmailerConstants.COMPANY.getValue() + Constants.PAYMENT_ATHENTICATION_ACKNOWLEDGEMENT.getValue()
					+ fields.get(FieldType.TXN_ID.getName());
			toEmail = fields.get(FieldType.CUST_EMAIL.getName());
			setEmailExceptionHandlerFlag(false);
			emailer.sendEmail(getBody(), getSubject(), getToEmail(), getEmailToBcc(), isEmailExceptionHandlerFlag());
		} catch (Exception exception) {
			logger.error("Exception", exception);
		}
	}

	// Remittance Email to merchant
	public void remittanceProcessEmail(String utr, String payId, String merchant, String datefrom, String netAmount,
									   String remittedDate, String remittedAmount, String status) {
		try {
			User user = userDao.getUserClass(payId);
			body = emailBodyCreator.remittanceEmailBody(utr, payId, merchant, datefrom, netAmount, remittedDate,
					remittedAmount, status);
			subject = EmailerConstants.COMPANY.getValue() + Constants.REMITTANCE_PROCESSED.getValue() + payId;
			toEmail = user.getEmailId();
			setEmailExceptionHandlerFlag(false);
			emailer.sendEmail(getBody(), getSubject(), getToEmail(), getEmailToBcc(), isEmailExceptionHandlerFlag());
		} catch (Exception exception) {
			logger.error("Exception", exception);
		}

	}

	// Merchant signup email to Admin Users
	public void merchantSignupEmail(MerchantSignupNotifier merchantSignupNotifier) throws Exception {
		try {
			body = emailBodyCreator.merchantSignupNotifier(merchantSignupNotifier);
			subject = Constants.MERCHANT_SIGNUP_NOTIFICATION.getValue() + EmailerConstants.COMPANY.getValue();
			toEmail = merchantSignupNotifier.getEmail();
			setEmailExceptionHandlerFlag(true);
			emailer.sendEmail(getBody(), getSubject(), getToEmail(), getEmailToBcc(), isEmailExceptionHandlerFlag());
		} catch (Exception exception) {
			logger.error("Exception", exception);
		}
	}

	public void sendBulkEmailServiceTaxUpdate(String emailID, String subject, String messageBody) {
		try {
			messageBody = emailBodyCreator.serviceTaxUpdate(messageBody);
			emailer.sendEmail(messageBody, subject, Constants.PG_DEMO_EMAIL.getValue(), emailID,
					isEmailExceptionHandlerFlag()); // Sending

		} catch (Exception exception) {
			logger.error("Exception", exception);
		}
	}

	public void sendPendingRequestUpdateEmail(String emailID, String subject, String messageBody) {
		try {
			messageBody = emailBodyCreator.emailPendingRequest(messageBody);
			emailer.sendEmail(messageBody, subject, Constants.PG_DEMO_EMAIL.getValue(), emailID,
					isEmailExceptionHandlerFlag()); // Sending

		} catch (Exception exception) {
			logger.error("Exception", exception);
		}
	}

	public void failedTxnsNotificationEmail(String emailID, String subject, String merchant, String businessName,
											String totalAmount, String messageBody, String currencyCode) {
		try {
			body = emailBodyCreator.failedTxnsNotification(merchant, businessName, totalAmount, messageBody,
					currencyCode);
			subject = subject;
			toEmail = emailID;
			setEmailExceptionHandlerFlag(true);
			emailer.sendEmail(getBody(), subject, getToEmail(), getEmailToBcc(), isEmailExceptionHandlerFlag());

		} catch (Exception exception) {
			logger.error("Exception", exception);
		}
	}

	public void resetPasswordConfirmation(String emailID, String subject, String name, String businessName,
										  String messageBody) {
		body = emailBodyCreator.resetPasswordConfirmation(name, businessName, messageBody);
		subject = subject;
		toEmail = emailID;
		setEmailExceptionHandlerFlag(true);
		emailer.sendEmail(body, subject, getToEmail(), getEmailToBcc(), isEmailExceptionHandlerFlag());
	}

	public void resetPasswordSecurityAlert(String emailID, String subject, String name, String businessName,
										   String messageBody) {
		body = emailBodyCreator.resetPasswordSecurityAlert(name, businessName, messageBody);
		subject = subject;
		toEmail = emailID;
		setEmailExceptionHandlerFlag(true);
		emailer.sendEmail(body, subject, getToEmail(), getEmailToBcc(), isEmailExceptionHandlerFlag());
	}

	public void changePasswordConfirmation(String emailID, String subject, String name, String businessName,
										   String messageBody) {
		body = emailBodyCreator.changePasswordConfirmation(name, businessName, messageBody);
		subject = subject;
		toEmail = emailID;
		setEmailExceptionHandlerFlag(true);
		emailer.sendEmail(body, subject, getToEmail(), getEmailToBcc(), isEmailExceptionHandlerFlag());
	}

	public void sendMisReport(String emailID, String subject, String fileName) {
		try {
			emailer.sendAttachmentFileEmail(fileName, subject, "", emailID, isEmailExceptionHandlerFlag());
		} catch (Exception exception) {
			logger.error("Exception", exception);
		}

	}

	public void databaseConnectionFailureEmail(String emailID) {
		try {
			body = emailBodyCreator.databaseFailureNotifier();
			subject = "Critical | Warning | Mongo Service Not reachable";
			toEmail = emailID;
			setEmailExceptionHandlerFlag(true);
			try {
				emailer.sendEmail(getBody(), getSubject(), getToEmail(), getEmailToBcc(),
						isEmailExceptionHandlerFlag());
			} catch (Exception exception) {
				responseMessage.append(ErrorType.EMAIL_ERROR.getResponseMessage());
				responseMessage.append(emailID);
				responseMessage.append("\n");
				logger.debug("Error!! Unable to send database connection failure email Emailer fail: " + exception);
				return;
			}
		} catch (Exception exception) {
			logger.error("Exception", exception);
		}
	}

	public void transactionsFetchFailureEmail(String emailID, String timeSpan) {
		try {
			body = emailBodyCreator.transactionsFetchFailureNotifier(timeSpan);
			subject = "Critical | Warning | No transaction received in last " + timeSpan + "minutes";
			toEmail = emailID;
			setEmailExceptionHandlerFlag(true);
			try {
				emailer.sendEmail(getBody(), getSubject(), getToEmail(), getEmailToBcc(),
						isEmailExceptionHandlerFlag());
			} catch (Exception exception) {
				responseMessage.append(ErrorType.EMAIL_ERROR.getResponseMessage());
				responseMessage.append(emailID);
				responseMessage.append("\n");
				logger.debug("Error!! Unable to send database connection failure email Emailer fail: " + exception);
				return;
			}
		} catch (Exception exception) {
			logger.error("Exception", exception);
		}
	}

	public void totalSuccessRateFailureEmail(String emailID, String thresholdRate, String actualRate, String timeSpan,
											 String SuccessTransaction, String TotalTransactions) {

		try {
			body = emailBodyCreator.totalSuccessRateFailureNotifier(thresholdRate, actualRate, timeSpan,
					SuccessTransaction, TotalTransactions);
			subject = "Critical | Warning | Total Success Rate is below " + thresholdRate + "% in last " + timeSpan
					+ " minutes ";

			toEmail = emailID;
			setEmailExceptionHandlerFlag(true);
			try {
				emailer.sendEmail(getBody(), getSubject(), getToEmail(), getEmailToBcc(),
						isEmailExceptionHandlerFlag());
			} catch (Exception exception) {
				responseMessage.append(ErrorType.EMAIL_ERROR.getResponseMessage());
				responseMessage.append(emailID);
				responseMessage.append("\n");
				logger.debug("Error!! Unable to send database connection failure email Emailer fail: " + exception);
				return;
			}
		} catch (Exception exception) {
			logger.error("Exception", exception);
		}
	}

	public void individualSuccessRateFailureEmail(String emailID, String thresholdRate, String actualRate,
												  String timeSpan, String individualKey, String SuccessTransaction, String TotalTransactions) {
		try {
			body = emailBodyCreator.individualSuccessRateFailureNotifier(thresholdRate, actualRate, timeSpan,
					individualKey, SuccessTransaction, TotalTransactions);
			subject = "Critical | Warning | Success Rate for " + individualKey + " is below " + thresholdRate
					+ "% in last " + timeSpan + " minutes";
			toEmail = emailID;
			setEmailExceptionHandlerFlag(true);
			try {
				emailer.sendEmail(getBody(), getSubject(), getToEmail(), getEmailToBcc(),
						isEmailExceptionHandlerFlag());
			} catch (Exception exception) {
				responseMessage.append(ErrorType.EMAIL_ERROR.getResponseMessage());
				responseMessage.append(emailID);
				responseMessage.append("\n");
				logger.debug("Error!! Unable to send database connection failure email Emailer fail: " + exception);
				return;
			}
		} catch (Exception exception) {
			logger.error("Exception", exception);
		}

	}

	public void nodalTransactionDisputeRaiseEmail(String emailID, String payId, String paymentType, String creationDate,
												  String timeBefore) {
		try {
			body = emailBodyCreator.nodalTransactionDisputeRaiseNotifier(payId, paymentType, creationDate, timeBefore);
			subject = "Dispute Raise Request";
			toEmail = emailID;
			setEmailExceptionHandlerFlag(true);
			try {
				emailer.sendEmail(getBody(), getSubject(), getToEmail(), getEmailToBcc(),
						isEmailExceptionHandlerFlag());
			} catch (Exception exception) {
				responseMessage.append(ErrorType.EMAIL_ERROR.getResponseMessage());
				responseMessage.append(emailID);
				responseMessage.append("\n");
				logger.debug("Error!! Unable to send dispute raise email . Emailer fail: " + exception);
				return;
			}
		} catch (Exception exception) {
			logger.error("Exception", exception);
		}
	}

	public void repeatedMopTypeEmail(String emailID, String subject, String messageBody) {
		body = emailBodyCreator.repeatedMopTypeEmail(messageBody);
		this.subject = subject;
		toEmail = emailID;
		setEmailExceptionHandlerFlag(true);
		emailer.sendEmail(body, subject, getToEmail(), getEmailToBcc(), isEmailExceptionHandlerFlag());
	}

	public void firstTxnAlertEmail(String emailID, String businessName) {
		body = emailBodyCreator.firstTxnAlertEmail(businessName);
		this.subject = "First Transaction Alert";
		List<String> emailIds = Arrays.asList(StringUtils.split(emailID, ","));
		emailIds.forEach(emailId -> {
			toEmail = emailId;
			setEmailExceptionHandlerFlag(true);
			emailer.sendEmail(body, subject, getToEmail(), getEmailToBcc(), isEmailExceptionHandlerFlag());
		});
	}

	public void velocityBlockingEmail(String emailID, String subject, String messageBody, MultipartFile attachment) {
		body = emailBodyCreator.velocityBlockingEmail(messageBody);
		this.subject = subject;
		toEmail = emailID;
		setEmailExceptionHandlerFlag(true);
		File file = (File) attachment;
		emailer.sendEmailWithTextAndAttachment(body, subject, getToEmail(), getEmailToBcc(),
				isEmailExceptionHandlerFlag(), file.getName(), file);
	}

	public void failedCntExceedEmail(String acquirerName, String paymentType, String mopType) {
		body = emailBodyCreator.FailedCntExceedBlockingEmail(acquirerName, paymentType, mopType);
		subject = "Transaction Failed Count Limit Exceeded In Smart Routing";
		toEmail = PropertiesManager.propertiesMap.get("failedCntAlertEmail");
		setEmailExceptionHandlerFlag(true);
		emailer.sendEmail(body, subject, getToEmail(), getEmailToBcc(), isEmailExceptionHandlerFlag());
	}

	public void merchantFraudRuleAlert(Map<String, String> reqMap) {
		body = emailBodyCreator.merchantFraudRuleAlert(reqMap);
		subject = "Merchant Fraud Rule Alert";
		toEmail = PropertiesManager.propertiesMap.get("merchantFraudRuleAlert");
		setEmailExceptionHandlerFlag(true);
		emailer.sendEmail(body, subject, getToEmail(), getEmailToBcc(), isEmailExceptionHandlerFlag());
	}

	public void acquirerMasterSwitchNotificationEmail(String paymentType, String Acquirer, String updateBy,
													  String status,String updateon) {
		try {
			body = emailBodyCreator.AcquirerMasterSwitch(paymentType, Acquirer, updateBy,status,updateon);
			if(status.equalsIgnoreCase("ACTIVE")) {
				subject = "Smart Routing Acquirer Master Switch - Manually | ACTIVE  for "+Acquirer+" Acquirer For "+paymentType;
			}else {
				subject = "Smart Routing Acquirer Master Switch - Manually | INACTIVE for "+Acquirer+" Acquirer For "+paymentType;

			}
			String SenderList="abhishek.tiwari@pay10.com,sonu@pay10.com,virendra.singh@pay10.com";
			String [] data=SenderList.split(",");
//		for (int i=0;i<data.length;i++) {
//			toEmail =data[i];
			setEmailExceptionHandlerFlag(true);
			emailer.sendEmailacquirer(body, subject,SenderList, getEmailToBcc(), true);

//			}


		} catch (Exception exception) {
			logger.error("Exception", exception);
		}
	}

	public void acquirerMasterSwitchschadularNotificationEmail(String paymentType, String Acquirer, String updateBy,
															   String status,String updateon ,String fromDate,String toDate) {
		try {
			body = emailBodyCreator.AcquirerMasterSwitchschadular(paymentType, Acquirer, updateBy,status,updateon,fromDate,toDate);
			if(status.equalsIgnoreCase("ACTIVE")) {
				subject = " Smart Routing | Acquirer Downtime Schedule INITIATED for "+Acquirer;
			}else {
				subject = " : Smart Routing | Acquirer Downtime Schedule COMPLETED for "+Acquirer ;

			}
			String SenderList="abhishek.tiwari@pay10.com,sonu@pay10.com,virendra.singh@pay10.com";
			String [] data=SenderList.split(",");
//		for (int i=0;i<data.length;i++) {
//			toEmail =data[i];
			setEmailExceptionHandlerFlag(true);
			emailer.sendEmailacquirer(body, subject,SenderList, getEmailToBcc(), true);

//			}


		} catch (Exception exception) {
			logger.error("Exception", exception);
		}
	}

	public void acquirerMasterSwitchCreationNotificationEmail(String paymentType, String Acquirer, String updateBy,
															  String status,String updateon ,String fromDate,String toDate) {
		try {
			body = emailBodyCreator.AcquirerMasterSwitchCreation(paymentType, Acquirer, updateBy,status,updateon,fromDate,toDate);
			if(status.equalsIgnoreCase("ACTIVE")) {
				subject = " Smart Routing | Acquirer Downtime Schedule CREATED for"+Acquirer;
			}else {
				subject = " Smart Routing | Acquirer Downtime Schedule DELETED for "+Acquirer;

			}
			String SenderList="abhishek.tiwari@pay10.com,sonu@pay10.com,virendra.singh@pay10.com";
			String [] data=SenderList.split(",");
//		for (int i=0;i<data.length;i++) {
//			toEmail =data[i];
			setEmailExceptionHandlerFlag(true);
			emailer.sendEmailacquirer(body, subject,SenderList, getEmailToBcc(), true);

//			}


		} catch (Exception exception) {
			logger.error("Exception", exception);
		}
	}

	public void acquirerDownURLAlert(Map<String, String> reqMap) {
		body = emailBodyCreator.acquirerDownURLAlert(reqMap);
		subject = "Acquirer Down Alert";
		toEmail = PropertiesManager.propertiesMap.get("acquirerDownAlert");
		setEmailExceptionHandlerFlag(true);
		emailer.sendEmail(body, subject, getToEmail(), getEmailToBcc(), isEmailExceptionHandlerFlag());
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
