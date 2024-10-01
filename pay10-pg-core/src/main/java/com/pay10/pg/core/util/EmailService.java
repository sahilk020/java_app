package com.pay10.pg.core.util;

import java.io.IOException;
import java.util.concurrent.ExecutorService;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pay10.commons.api.EmailControllerServiceProvider;
import com.pay10.commons.api.SmsControllerServiceProvider;
import com.pay10.commons.user.User;
import com.pay10.commons.util.Amount;
import com.pay10.commons.util.Currency;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.threadpool.ThreadPoolProvider;
import com.pay10.notification.txnpush.generator.AsyncPostCreater;

/**
 * @author Rajendra
 */

@Service
public class EmailService {

	private static Logger logger = LoggerFactory.getLogger(EmailService.class.getName());
	@Autowired
	private SmsControllerServiceProvider smsControllerServiceProvider;

	public void sendingEmailAndSmsNotificationForFailedTxns(Fields fields, User user) {
		ExecutorService emailExecutor = ThreadPoolProvider.getExecutorService();
		emailExecutor.execute(new Runnable() {
			@Override
			public void run() {
				try {
					String merchantEmail = fields.get(FieldType.CUST_EMAIL.getName()).toString();
					String merchantPhone = fields.get(FieldType.CUST_PHONE.getName()).toString();
					String customerName = null;
					String amount = null;
					String createDate = null;
					String currencyCode = null;
					if (StringUtils.isNotBlank(fields.get(FieldType.CUST_NAME.getName()))) {
						customerName = fields.get(FieldType.CUST_NAME.getName()).toString();
					}
					if (StringUtils.isNotBlank(fields.get(FieldType.AMOUNT.getName()))) {
						amount = fields.get(FieldType.AMOUNT.getName()).toString();
					}
					if (StringUtils.isNotBlank(fields.get(FieldType.CURRENCY_CODE.getName()))) {
						currencyCode = fields.get(FieldType.CURRENCY_CODE.getName()).toString();
					}
					if (StringUtils.isNotBlank(fields.get(FieldType.RESPONSE_DATE_TIME.getName()))) {
						createDate = fields.get(FieldType.RESPONSE_DATE_TIME.getName()).toString();
					}
					String merchnatBusinessName = user.getBusinessName().toString();
					String subject = null;
					if (StringUtils.isNotBlank(customerName)) {
						subject = customerName + " | Transaction Failed Alert | " + createDate;
					} else {
						subject = merchnatBusinessName + " | Transaction Failed Alert | " + createDate;
					}

					if (StringUtils.isNotBlank(merchantEmail)) {
						EmailControllerServiceProvider ecsp = new EmailControllerServiceProvider();
						ecsp.failedTxnsNotification(merchantEmail, subject, customerName, merchnatBusinessName, amount,
								fields.get(FieldType.PG_TXN_MESSAGE.getName()).toString(), currencyCode);
					}
					if (StringUtils.isNotBlank(merchantPhone)) {
						SmsControllerServiceProvider scsp = new SmsControllerServiceProvider();
						scsp.sendTxnFailedSms(merchantPhone, subject, customerName, merchnatBusinessName, amount,
								fields.get(FieldType.PG_TXN_MESSAGE.getName()).toString(), currencyCode);
					}

				} catch (Exception exception) {
					logger.error("Exception in sending Email or SMS ", exception);
				}
			}
		});
		emailExecutor.shutdown();
	}

	public void sendingNotificationApi(Fields fields, User user) {
		ExecutorService notificationApi = ThreadPoolProvider.getExecutorService();
		notificationApi.execute(new Runnable() {
			@Override
			public void run() {
				try {

					AsyncPostCreater apc = new AsyncPostCreater();
					apc.notificationApi(fields, user.getNotificaionApi());

				} catch (Exception exception) {
					logger.error("Exception in sending Email or SMS ", exception);
				}
			}
		});
		notificationApi.shutdown();
	}

	public void sendingEmailAndSmsNotificationForInvoiceSuccessTxns(Fields fields, User user, String prodName) {
		ExecutorService emailExecutor = ThreadPoolProvider.getExecutorService();
		emailExecutor.execute(new Runnable() {
			@Override
			public void run() {
				try {
					sendInvoicePaymentEmailToUser(fields, user, prodName);
					sendInvoicePaymentEmailToMerchant(fields, user, prodName);
				} catch (Exception e) {
					logger.error("Failed to send email to user or merchant");
					logger.error(e.getMessage());
				}
			}
		});

		emailExecutor.shutdown();
	}

	public void sendInvoicePaymentEmailToMerchant(Fields fields, User user, String prodName) {
//		String merchantName = user.getFirstName();
		String customerName = fields.get(FieldType.CUST_NAME.getName());
		String amount = fields.get(FieldType.AMOUNT.getName());
		String currencyCode = fields.get(FieldType.CURRENCY_CODE.getName());
		String invoiceNo = fields.get(FieldType.INVOICE_NO.getName());
		String merchantBusinessName = user.getBusinessName();
		String customerEmail = fields.get(FieldType.CUST_EMAIL.getName());
		String merchantEmail = user.getEmailId();

		String orderId = fields.get(FieldType.ORDER_ID.getName());
		String pgRefNum = fields.get(FieldType.PG_REF_NUM.getName());
		String custPhone = fields.get(FieldType.CUST_PHONE.getName());

		currencyCode = Currency.getAlphabaticCode(currencyCode);
		amount = Amount.toDecimal(amount, currencyCode);
		String subject = "Payment of " + currencyCode + " " + amount + "/- receieved against Invoice no. " + invoiceNo
				+ "  from " + customerEmail;

		if (StringUtils.isNotBlank(merchantEmail)) {
			EmailControllerServiceProvider ecsp = new EmailControllerServiceProvider();
			ecsp.invoiceSuccessTxnsNotificationToMerchant(merchantEmail, subject, customerName, merchantBusinessName,
					amount, currencyCode, invoiceNo, orderId, pgRefNum, custPhone);
		} else {
			logger.error("Invalid Customer email");
		}
	}

	public void sendInvoicePaymentEmailToUser(Fields fields, User user, String prodName) {

		try {
			String merchantEmail = fields.get(FieldType.CUST_EMAIL.getName());
			String customerName = null;
			String amount = null;
			String createDate = null;
			String currencyCode = null;
			String invoiceNo = fields.get(FieldType.INVOICE_NO.getName());

			if (StringUtils.isNotBlank(fields.get(FieldType.CUST_NAME.getName()))) {
				customerName = fields.get(FieldType.CUST_NAME.getName());
			}
			if (StringUtils.isNotBlank(fields.get(FieldType.AMOUNT.getName()))) {
				amount = fields.get(FieldType.AMOUNT.getName());
			}
			if (StringUtils.isNotBlank(fields.get(FieldType.CURRENCY_CODE.getName()))) {
				currencyCode = fields.get(FieldType.CURRENCY_CODE.getName());
				currencyCode = Currency.getAlphabaticCode(currencyCode);
			}
			if (StringUtils.isNotBlank(fields.get(FieldType.RESPONSE_DATE_TIME.getName()))) {
				createDate = fields.get(FieldType.RESPONSE_DATE_TIME.getName());
			}
			String merchnatBusinessName = user.getBusinessName();
			String subject = null;
			if (StringUtils.isNotBlank(customerName)) {
				subject = customerName + " | Invoice Transaction Success Alert | " + createDate;
			} else {
				subject = merchnatBusinessName + " |Invoice Transaction Success Alert | " + createDate;
			}

			if (StringUtils.isNotBlank(merchantEmail)) {
				EmailControllerServiceProvider ecsp = new EmailControllerServiceProvider();
				ecsp.InvoiceSuccessTxnsNotification(merchantEmail, subject, customerName, merchnatBusinessName, amount,
						currencyCode, invoiceNo);
			}

//        	Uncomment to send SMS.
//        	String merchantPhone = fields.get(FieldType.CUST_PHONE.getName()).toString();
//        	if(StringUtils.isNotBlank(merchantPhone)) {
//        		SmsControllerServiceProvider scsp = new SmsControllerServiceProvider();		         
//        		scsp.sendInvoiceSuccessTxnSms(merchantPhone,customerName,merchnatBusinessName,amount,orderNo,pgRefNo,prodName,currencyCode,message);	                		
//        	}

		} catch (Exception exception) {
			logger.error("Exception in sending Email or SMS ", exception);
		}
	}

	// deepak send sms for successful transaction
	public void sendSmsPaymentToUser(Fields fields, User user) {

		try {

			if (StringUtils.isNotBlank(fields.get(FieldType.CUST_PHONE.getName()).toString())) {
				logger.info("CUST_PHONE    >>>>>>>" + fields.get(FieldType.CUST_PHONE.getName()).toString());
				smsControllerServiceProvider.sendSmsKaleyra(fields, user);
				// smsControllerServiceProvider.sendSmsKaleyra(merchantPhone);
			}

		} catch (Exception exception) {
			logger.error("Exception in sending Email or SMS ", exception);
		}
	}

	// Added By Sweety

	public void sendSmsPaymentToMerchant(Fields fields, User user) {

		try {

			if (StringUtils.isNotBlank(user.getMobile())) {
				logger.info("Merchant Mobile No.={}",user.getMobile());
				smsControllerServiceProvider.sendSmsToMerchant(fields, user);
				// smsControllerServiceProvider.sendSmsKaleyra(merchantPhone);
			}

		} catch (Exception exception) {
			logger.error("Exception in sending Email or SMS ", exception);
		}
	}

}
