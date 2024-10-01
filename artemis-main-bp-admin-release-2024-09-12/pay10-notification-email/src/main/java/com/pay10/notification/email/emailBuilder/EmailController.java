package com.pay10.notification.email.emailBuilder;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.pay10.commons.user.MerchantSignupNotifier;
import com.pay10.commons.user.ResponseObject;

@RestController
public class EmailController {
	@Autowired
	private EmailBuilder emailBuilder;

	private static Logger logger = LoggerFactory.getLogger(EmailController.class.getName());

	@RequestMapping(method = RequestMethod.POST, value = "/emailValidator")
	public ResponseObject emailValidator(@RequestBody final ResponseObject responseObject) throws Exception {
		emailBuilder.emailValidator(responseObject);
		return responseObject;

	}

	@RequestMapping(method = RequestMethod.POST, value = "/emailAddUser", consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseObject addUser(@RequestBody ResponseObject responseObject) throws Exception {
		logger.info("Request Received For addUser : " + responseObject.toString());
		emailBuilder.emailAddUser(responseObject);
		return responseObject;
	}

	@RequestMapping(method = RequestMethod.POST, value = "/emailMerchantTransactionDetails", consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseObject merchantTransactionDetails(@RequestBody ResponseObject responseObject) throws Exception {
		logger.info("Request Received For merchantTransactionDetails : " + responseObject.toString());
		emailBuilder.emailMerchantTransactionDetails(responseObject);
		return responseObject;
	}

	@RequestMapping(method = RequestMethod.POST, value = "/emailPasswordChange/{emailId}", consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseObject passwordChange(@RequestBody ResponseObject responseObject, @PathVariable String emailId)
			throws Exception {
		emailBuilder.emailPasswordChange(responseObject, emailId);
		return responseObject;

	}

	@RequestMapping(method = RequestMethod.POST, value = "/passwordResetEmail", consumes = MediaType.APPLICATION_JSON_VALUE)
	public void passwordReset(@RequestParam String accountValidationID, @RequestParam String email) throws Exception {
		emailBuilder.passwordResetEmail(accountValidationID, email);

	}

	@RequestMapping(method = RequestMethod.POST, value = "/invoiceLinkEmail", consumes = MediaType.APPLICATION_JSON_VALUE)
	public void invoiceLink(@RequestBody Map<String, String> reqmap) {
		emailBuilder.invoiceLinkEmail(reqmap.get("url"), reqmap.get("emailId"), reqmap.get("name"),
				reqmap.get("merchant"));

	}

	@RequestMapping(method = RequestMethod.POST, value = "/invoiceSuccessTxnsNotification", consumes = MediaType.APPLICATION_JSON_VALUE)
	public void InvoiceSuccessTxnsNotification(@RequestBody Map<String, String> reqmaps) {
		emailBuilder.invoiceSuccessTxnEmail(reqmaps.get("emailId"), reqmaps.get("subject"), reqmaps.get("merchant"),
				reqmaps.get("businessName"), reqmaps.get("totalAmount"), reqmaps.get("currencyCode"),
				reqmaps.get("invoiceNo"));
	}

	// Send Invoice payment successful email to merchant.
	@RequestMapping(method = RequestMethod.POST, value = "/invoiceSuccessTxnsNotificationMerchant", consumes = MediaType.APPLICATION_JSON_VALUE)
	public void invoiceSuccessTxnsNotificationMerchant(@RequestBody Map<String, String> reqMap) {
		emailBuilder.invoiceSuccessTxnEmailMerchant(reqMap);
	}

	@RequestMapping(method = RequestMethod.POST, value = "/remittanceProcessEmail", consumes = MediaType.APPLICATION_JSON_VALUE)
	public void remittanceProcess(@RequestParam String utr, @RequestParam String payId, @RequestParam String merchant,
								  @RequestParam String datefrom, @RequestParam String netAmount, @RequestParam String remittedDate,
								  @RequestParam String remittedAmount, @RequestParam String status) {
		emailBuilder.remittanceProcessEmail(utr, payId, merchant, datefrom, netAmount, remittedDate, remittedAmount,
				status);

	}

	@RequestMapping(method = RequestMethod.POST, value = "/sendBulkEmailServiceTaxUpdate", consumes = MediaType.APPLICATION_JSON_VALUE)
	public void sendBulkEmailServiceTax(@RequestParam String emailID, @RequestParam String subject,
										@RequestParam String messageBody) {
		emailBuilder.sendBulkEmailServiceTaxUpdate(emailID, subject, messageBody);

	}

	@RequestMapping(method = RequestMethod.POST, value = "/emailPendingRequest")
	public void emailPendingRequest(@RequestParam String email, @RequestParam String subject,
									@RequestParam String messageBody) {
		emailBuilder.sendPendingRequestUpdateEmail(email, subject, messageBody);

	}

	@RequestMapping(method = RequestMethod.POST, value = "/failedTxnsNotification", consumes = MediaType.APPLICATION_JSON_VALUE)
	public void failedTxnsNotification(@RequestBody Map<String, String> reqmap) {
		emailBuilder.failedTxnsNotificationEmail(reqmap.get("emailId"), reqmap.get("subject"), reqmap.get("merchant"),
				reqmap.get("businessName"), reqmap.get("totalAmount"), reqmap.get("messageBody"),
				reqmap.get("currencyCode"));

	}

	@RequestMapping(method = RequestMethod.POST, value = "/resetPasswordConfirmation", consumes = MediaType.APPLICATION_JSON_VALUE)
	public void resetPasswordConfirmation(@RequestBody Map<String, String> reqmap) {
		emailBuilder.resetPasswordConfirmation(reqmap.get("emailId"), reqmap.get("subject"), reqmap.get("name"),
				reqmap.get("businessName"), reqmap.get("messageBody"));

	}

	@RequestMapping(method = RequestMethod.POST, value = "/changePasswordConfirmation", consumes = MediaType.APPLICATION_JSON_VALUE)
	public void changePasswordConfirmation(@RequestBody Map<String, String> reqmap) {
		emailBuilder.changePasswordConfirmation(reqmap.get("emailId"), reqmap.get("subject"), reqmap.get("name"),
				reqmap.get("businessName"), reqmap.get("messageBody"));

	}

	@RequestMapping(method = RequestMethod.POST, value = "/resetPasswordSecurityAlert", consumes = MediaType.APPLICATION_JSON_VALUE)
	public void resetPasswordSecurityAlert(@RequestBody Map<String, String> reqmap) {
		emailBuilder.resetPasswordSecurityAlert(reqmap.get("emailId"), reqmap.get("subject"), reqmap.get("name"),
				reqmap.get("businessName"), reqmap.get("messageBody"));

	}

	@RequestMapping(method = RequestMethod.POST, value = "/merchantSignupNotifier")
	public void merchantSignupNotifier(@RequestBody final MerchantSignupNotifier merchantSignupNotifier)
			throws Exception {
		emailBuilder.merchantSignupEmail(merchantSignupNotifier);
		logger.info("Merchant Sign Up Notification send to " + merchantSignupNotifier.getEmail());
	}

	@RequestMapping(method = RequestMethod.POST, value = "/databaseConnectionFailureNotifier", consumes = MediaType.APPLICATION_JSON_VALUE)
	public void databaseConnectionFailureNotification(@RequestBody Map<String, String> reqMap) {
		emailBuilder.databaseConnectionFailureEmail(reqMap.get("email"));

	}

	@RequestMapping(method = RequestMethod.POST, value = "/transactionsFetchNotifier", consumes = MediaType.APPLICATION_JSON_VALUE)
	public void transactionsFetchFailureNotification(@RequestBody Map<String, String> reqMap) {
		emailBuilder.transactionsFetchFailureEmail(reqMap.get("email"), reqMap.get("timeSpan"));

	}

	@RequestMapping(method = RequestMethod.POST, value = "/totalSuccessRateNotifier", consumes = MediaType.APPLICATION_JSON_VALUE)
	public void totalSuccessRateFailureNotification(@RequestBody Map<String, String> reqMap) {
		emailBuilder.totalSuccessRateFailureEmail(reqMap.get("email"), reqMap.get("thresholdRate"),
				reqMap.get("actualRate"), reqMap.get("timeSpan"), reqMap.get("SuccessTransaction"),
				reqMap.get("TotalTransactions"));
	}

	@RequestMapping(method = RequestMethod.POST, value = "/individualSuccessRateNotifier", consumes = MediaType.APPLICATION_JSON_VALUE)
	public void individualSuccessRateFailureNotification(@RequestBody Map<String, String> reqMap) {
		emailBuilder.individualSuccessRateFailureEmail(reqMap.get("email"), reqMap.get("thresholdRate"),
				reqMap.get("actualRate"), reqMap.get("timeSpan"), reqMap.get("individualKey"),
				reqMap.get("SuccessTransaction"), reqMap.get("TotalTransactions"));

	}

	@RequestMapping(method = RequestMethod.POST, value = "/nodalTransactionDisputeNotifier", consumes = MediaType.APPLICATION_JSON_VALUE)
	public void nodalTransactionDisputeNotification(@RequestBody Map<String, String> reqMap) {

		emailBuilder.nodalTransactionDisputeRaiseEmail(reqMap.get("email"), reqMap.get("payId"),
				reqMap.get("paymentType"), reqMap.get("creationDate"), reqMap.get("timeBefore"));

	}

	@RequestMapping(method = RequestMethod.POST, value = "/repeatedMopTypeEmail", consumes = MediaType.APPLICATION_JSON_VALUE)
	public void repeatedMopTypeEmail(@RequestBody Map<String, String> reqMap) {

		emailBuilder.repeatedMopTypeEmail(reqMap.get("emailId"), reqMap.get("subject"), reqMap.get("messageBody"));

	}

	@PostMapping("/firstTxnAlert")
	public void sendFirstTransactionAlert(@RequestBody Map<String, String> reqMap) {
		emailBuilder.firstTxnAlertEmail(reqMap.get("emailId"), reqMap.get("businessName"));
	}

	@RequestMapping(method = RequestMethod.POST, value = "/velocityBlockingEmail")
	public void velocityBlockingEmail(@RequestParam("file") MultipartFile file,
									  @RequestBody Map<String, String> reqMap) {
		emailBuilder.velocityBlockingEmail(reqMap.get("emailId"), reqMap.get("subject"), reqMap.get("messageBody"),
				file);
	}

	@PostMapping("/failedCntExceedAlert")
	public void txnFailedCntExceedAlert(@RequestBody Map<String, String> reqMap) {
		emailBuilder.failedCntExceedEmail(reqMap.get("acquirerName"), reqMap.get("paymentType"), reqMap.get("mopType"));
	}

	@PostMapping("/merchantFraudRuleAlert")
	public void merchantFraudRuleAlert(@RequestBody Map<String, String> reqMap) {
		emailBuilder.merchantFraudRuleAlert(reqMap);
	}


	@RequestMapping(method = RequestMethod.POST, value = "/acquirerMasterSwitchSchadularNotification", consumes = MediaType.APPLICATION_JSON_VALUE)
	public void acquirerMasterSwitchSchadularNotification(@RequestBody Map<String, String> reqmap) {
		emailBuilder.acquirerMasterSwitchschadularNotificationEmail(reqmap.get("PAYMENTTYPE"), reqmap.get("ACQUIRER"), reqmap.get("UPDATEBY"),
				reqmap.get("STATUS"),reqmap.get("UPDATEDON"),reqmap.get("FROMDATE"),reqmap.get("TODATE"));

	}

	@RequestMapping(method = RequestMethod.POST, value = "/acquirerMasterSwitchCreationNotification", consumes = MediaType.APPLICATION_JSON_VALUE)
	public void acquirerMasterSwitchCreationNotification(@RequestBody Map<String, String> reqmap) {
		emailBuilder.acquirerMasterSwitchCreationNotificationEmail(reqmap.get("PAYMENTTYPE"), reqmap.get("ACQUIRER"), reqmap.get("UPDATEBY"),
				reqmap.get("STATUS"),reqmap.get("UPDATEDON"),reqmap.get("FROMDATE"),reqmap.get("TODATE"));

	}


	@RequestMapping(method = RequestMethod.POST, value = "/acquirerMasterSwitchNotification", consumes = MediaType.APPLICATION_JSON_VALUE)
	public void acquirerMasterSwitchNotification(@RequestBody Map<String, String> reqmap) {
		emailBuilder.acquirerMasterSwitchNotificationEmail(reqmap.get("PAYMENTTYPE"), reqmap.get("ACQUIRER"), reqmap.get("UPDATEBY"),
				reqmap.get("STATUS"),reqmap.get("UPDATEDON"));

	}


	@PostMapping("/acquirerDownURL")
	public void acquirerDownURLAlert(@RequestBody Map<String, String> reqMap) {
		emailBuilder.acquirerDownURLAlert(reqMap);
	}

}
