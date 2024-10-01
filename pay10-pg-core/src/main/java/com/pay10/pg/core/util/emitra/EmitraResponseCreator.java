package com.pay10.pg.core.util.emitra;

import java.io.PrintWriter;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.ServletActionContext;
import org.owasp.esapi.ESAPI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.pay10.commons.dao.FieldsDao;
import com.pay10.commons.dao.InvoiceDao;
import com.pay10.commons.dao.PaymentLinkDao;
import com.pay10.commons.exception.SystemException;
import com.pay10.commons.user.Invoice;
import com.pay10.commons.user.PaymentLink;
import com.pay10.commons.user.User;
import com.pay10.commons.user.UserDao;
import com.pay10.commons.util.ConfigurationConstants;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.InvoiceStatus;
import com.pay10.commons.util.InvoiceType;
import com.pay10.commons.util.PropertiesManager;
import com.pay10.commons.util.StatusType;
import com.pay10.pg.core.fraudPrevention.core.FraudRuleImplementor;
import com.pay10.pg.core.security.TransactionConverterIrctc;
import com.pay10.pg.core.util.EmailService;
import com.pay10.pg.core.util.Forwarder;
import com.pay10.pg.core.util.Processor;
import com.pay10.pg.core.util.TransactionResponser;

import bsh.This;

/**
 * This component is design to enhance response encryption for emitra specific
 * 
 * @author Jay
 */

@Service
public class EmitraResponseCreator extends Forwarder {

	private static Logger logger = LoggerFactory.getLogger(This.class.getName());
	private static final long serialVersionUID = 6021494405007391983L;
	@Autowired
	@Qualifier("responseProcessor")
	private Processor responseProcessor;

	@Autowired
	@Qualifier("userDao")
	private UserDao userDao;

	@Autowired
	InvoiceDao invoiceDao;

	@Autowired
	PaymentLinkDao paymentLinkDao;

	@Autowired
	@Qualifier("propertiesManager")
	private PropertiesManager propertiesManager;

	@Autowired
	@Qualifier("irctcTransactionConverter")
	private TransactionConverterIrctc converter;

	@Autowired
	TransactionResponser transactionResponser;

	@Autowired
	private FieldsDao fieldsDao;

	@Autowired
	private EmailService emailService;

	@Autowired
	private FraudRuleImplementor fraudRuleImplementor;

	@Autowired
	private EmitraScrambler emitraScrambler;

	public void create(Fields fields) {
		try {
			responseProcessor.preProcess(fields);
			responseProcessor.process(fields);
			responseProcessor.postProcess(fields);

		} catch (SystemException systemException) {
			logger.error("Exception", systemException);
			fields.clear();
		} catch (Exception exception) {
			logger.error("Exception", exception);
			fields.clear();
		}
	}

	/**
	 * This method is created for the emitra specific encryption.
	 */
	public void ResponsePostCustomEncryption(Fields fields) {
		String merchantHostedFlag = fields.get(FieldType.IS_MERCHANT_HOSTED.getName());
		logger.info("ResponsePost method IS_MERCHANT_HOSTED : {}", merchantHostedFlag);

		try {
			if (StringUtils.isNotBlank(fields.get(FieldType.STATUS.getName()))
					&& StringUtils.isNotBlank(fields.get(FieldType.PAY_ID.getName()))) {
				fieldsDao.sendCallback(fields);
			}
			PrintWriter out = ServletActionContext.getResponse().getWriter();

			if (null != merchantHostedFlag && "Y".equalsIgnoreCase(merchantHostedFlag.trim())) {
				try {
					fields.removeInternalFields();
					fields.removeSecureFields();
					fields.remove(FieldType.ORIG_TXN_ID.getName());
					fields.remove(FieldType.HASH.getName());
					transactionResponser.addHash(fields);
					fields.remove(FieldType.ORIG_TXN_ID.getName());
					fields.logAllFields("Merchant Hosted : Plain text response for  merchant: ");
					String finalResponse = createMerchantHostedPgResponse(fields);
					out.write(finalResponse);
					out.flush();
					out.close();
				} catch (Exception e) {
					logger.error("Exception", e);
				}
			} else {
				fields.removeInternalFields();
				fields.removeSecureFields();
				fields.remove(FieldType.ORIG_TXN_ID.getName());
				fields.remove(FieldType.HASH.getName());
				transactionResponser.addHash(fields);
				fields.remove(FieldType.ORIG_TXN_ID.getName());
				String finalResponse = createPgResponse(fields);
				logger.info("PG Hosted flow final response sent " + finalResponse);
				out.write(finalResponse);
				out.flush();
				out.close();
			}
		} catch (Exception e) {
			logger.error("Error sending response to merchant" + e);
		}
	}

	public String createPgResponse(Fields fields) throws SystemException {
		/*
		 * StringBuilder httpRequest = new StringBuilder();
		 * httpRequest.append("<HTML>");
		 * httpRequest.append("<BODY OnLoad=\"OnLoadEvent();\" >");
		 * httpRequest.append("<form name=\"form1\" action=\"");
		 * httpRequest.append(encodeString(fields.get(FieldType.RETURN_URL.getName())));
		 * httpRequest.append("\" method=\"post\">"); for (String key : fields.keySet())
		 * { httpRequest.append("<input type=\"hidden\" name=\"");
		 * httpRequest.append(encodeString(key)); httpRequest.append("\" value=\"");
		 * httpRequest.append(encodeString(fields.get(key))); httpRequest.append("\">");
		 * } httpRequest.append("</form>");
		 * httpRequest.append("<script language=\"JavaScript\">");
		 * httpRequest.append("function OnLoadEvent()");
		 * httpRequest.append("{document.form1.submit();}");
		 * httpRequest.append("</script>"); httpRequest.append("</BODY>");
		 * httpRequest.append("</HTML>");
		 */

		String encData = emitraScrambler.encrypt(fields.get(FieldType.PAY_ID.getName()), getFieldsForResponse(fields));
		StringBuilder httpRequest = new StringBuilder();
		httpRequest.append("<HTML>");
		httpRequest.append("<BODY OnLoad=\"OnLoadEvent();\" >");
		httpRequest.append("<form name=\"form1\" action=\"");
		httpRequest.append(encodeString(fields.get(FieldType.RETURN_URL.getName())));
		httpRequest.append("\" method=\"post\">");
		httpRequest.append("<input type=\"hidden\" name=\"");
		httpRequest.append(FieldType.ENCDATA.getName());
		httpRequest.append("\" value=\"");
		httpRequest.append(encData);
		httpRequest.append("\">");
		httpRequest.append("<input type=\"hidden\" name=\"");
		httpRequest.append(FieldType.PAY_ID.getName());
		httpRequest.append("\" value=\"");
		httpRequest.append(fields.get(FieldType.PAY_ID.getName()));
		httpRequest.append("\">");
		httpRequest.append("</form>");
		httpRequest.append("<script language=\"JavaScript\">");
		httpRequest.append("function OnLoadEvent()");
		httpRequest.append("{document.form1.submit();}");
		httpRequest.append("</script>");
		httpRequest.append("</BODY>");
		httpRequest.append("</HTML>");

		// TODO here I need to add the logic for sent notification api related thing
		User user = userDao.getUserClass(fields.get(FieldType.PAY_ID.getName()));

		logger.info("ResponseCreator  ,STATUS >>>>=" + fields.get(FieldType.STATUS.getName()));

		// deepak
		/*
		 * if(user.isNotificationApiEnableFlag()==true) {
		 * logger.info("user.isNotificationApiEnableFlag()==true"+user.
		 * isNotificationApiEnableFlag()); emailService.sendSmsPaymentToUser(fields,
		 * user); }
		 */

		// logger.info("---------- user ------------: "+user.toString());
		if (StatusType.getFailedStatusFromInternalStatus().contains(fields.get(FieldType.STATUS.getName()).toString())
				|| StatusType.USER_INACTIVE.getName()
						.equalsIgnoreCase(fields.get(FieldType.STATUS.getName()).toString())
				|| StatusType.ACQUIRER_TIMEOUT.getName()
						.equalsIgnoreCase(fields.get(FieldType.STATUS.getName()).toString())) {

			logger.info("mail send for failed transaction");
			// logger.info("--- user <-> isTransactionFailedAlertFlag()
			// --"+user.isTransactionFailedAlertFlag());
			/*
			 * if(user.isTransactionFailedAlertFlag()) {
			 * emailService.sendingEmailAndSmsNotificationForFailedTxns(fields, user); }
			 */
			emailService.sendingEmailAndSmsNotificationForFailedTxns(fields, user);

		} else if (StatusType.CAPTURED.getName().equalsIgnoreCase(fields.get(FieldType.STATUS.getName()))) {

			fraudRuleImplementor.applyRule(fields);
			if (fields.contains(FieldType.INVOICE_ID.getName())) {
				String invoiceId = fields.get(FieldType.INVOICE_ID.getName());
				Invoice invoice = invoiceDao.findByInvoiceId(invoiceId);
				String prodName = invoice.getProductName();
				if (invoice.getInvoiceType().getName().equals(InvoiceType.SINGLE_PAYMENT.getName())) {
					invoice.setInvoiceStatus(InvoiceStatus.PAID);
					invoiceDao.update(invoice);
				}
				if (!StringUtils.isEmpty(invoiceId)) {
					emailService.sendingEmailAndSmsNotificationForInvoiceSuccessTxns(fields, user, prodName);
				}
			} else if (fields.contains(FieldType.PAYMENT_LINK_ID.getName())) {
				String paymentLinkId = fields.get(FieldType.PAYMENT_LINK_ID.getName());
				PaymentLink paymentLink = paymentLinkDao.findByPaymentLinkId(paymentLinkId);
				if (null != paymentLink) {
					paymentLink.setInvoiceStatus(InvoiceStatus.PAID);
					paymentLinkDao.update(paymentLink);
				}
			}
			// deepak
			// send sms for successful transaction TO customer(PG hosted)

			logger.info("user.isNotificationApiEnableFlag()   pg  >>>>" + user.isNotificationApiEnableFlag());
			if (user.isNotificationApiEnableFlag() == true) {
				emailService.sendSmsPaymentToUser(fields, user);
			}

		}

		if (user.isNotificationApiEnableFlag() && !StringUtils.isEmpty(user.getNotificaionApi())) {
			emailService.sendingNotificationApi(fields, user);
		}

		return httpRequest.toString();
	}

	private String getFieldsForResponse(Fields fields) {
		StringBuilder allFields = new StringBuilder();
		for (Entry<String, String> entry : fields.getFields().entrySet()) {
			allFields.append(ConfigurationConstants.FIELD_SEPARATOR.getValue());
			allFields.append(entry.getKey());
			allFields.append(ConfigurationConstants.FIELD_EQUATOR.getValue());
			allFields.append(entry.getValue());
		}
		allFields.deleteCharAt(0);
		return allFields.toString();
	}

	public String createMerchantHostedPgResponse(Fields fields) throws SystemException {

		String encData = emitraScrambler.encrypt(fields.get(FieldType.PAY_ID.getName()), getFieldsForResponse(fields));
		StringBuilder httpRequest = new StringBuilder();
		httpRequest.append("<HTML>");
		httpRequest.append("<BODY OnLoad=\"OnLoadEvent();\" >");
		httpRequest.append("<form name=\"form1\" action=\"");
		httpRequest.append(encodeString(fields.get(FieldType.RETURN_URL.getName())));
		httpRequest.append("\" method=\"post\">");
		httpRequest.append("<input type=\"hidden\" name=\"");
		httpRequest.append(FieldType.ENCDATA.getName());
		httpRequest.append("\" value=\"");
		httpRequest.append(encData);
		httpRequest.append("\">");
		httpRequest.append("<input type=\"hidden\" name=\"");
		httpRequest.append(FieldType.PAY_ID.getName());
		httpRequest.append("\" value=\"");
		httpRequest.append(fields.get(FieldType.PAY_ID.getName()));
		httpRequest.append("\">");
		httpRequest.append("</form>");
		httpRequest.append("<script language=\"JavaScript\">");
		httpRequest.append("function OnLoadEvent()");
		httpRequest.append("{document.form1.submit();}");
		httpRequest.append("</script>");
		httpRequest.append("</BODY>");
		httpRequest.append("</HTML>");
		User user = userDao.getUserClass(fields.get(FieldType.PAY_ID.getName()));
		// deepak
		/*
		 * if(user.isNotificationApiEnableFlag()==true) {
		 * logger.info("user.isNotificationApiEnableFlag()==true merchant"+user.
		 * isNotificationApiEnableFlag()); emailService.sendSmsPaymentToUser(fields,
		 * user); }
		 */
		if (null != fields.get(FieldType.STATUS.getName()) && null != fields.get(FieldType.PAY_ID.getName())) {
			if (StatusType.getFailedStatusFromInternalStatus()
					.contains(fields.get(FieldType.STATUS.getName()).toString())
					|| StatusType.USER_INACTIVE.getName()
							.equalsIgnoreCase(fields.get(FieldType.STATUS.getName()).toString())
					|| StatusType.ACQUIRER_TIMEOUT.getName()
							.equalsIgnoreCase(fields.get(FieldType.STATUS.getName()).toString())) {
				if (user.isTransactionFailedAlertFlag()) {
					emailService.sendingEmailAndSmsNotificationForFailedTxns(fields, user);
				}
			}
		}

		if (user.isNotificationApiEnableFlag() && !StringUtils.isEmpty(user.getNotificaionApi())) {
			emailService.sendingNotificationApi(fields, user);
		}
		// deepak
		// send sms for successful transaction TO customer (nerchant hosted)

		if (StatusType.CAPTURED.getName().equalsIgnoreCase(fields.get(FieldType.STATUS.getName()))) {
			logger.info("NotificationApiEnableFlag   merchant  >>>>" + user.isNotificationApiEnableFlag());
			if (user.isNotificationApiEnableFlag() == true) {
				emailService.sendSmsPaymentToUser(fields, user);
			}
		}

		return httpRequest.toString();
	}

	public static String encodeString(String data) {
		return ESAPI.encoder().encodeForHTML(data);
	}

}
