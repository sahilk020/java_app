package com.pay10.pg.action;

import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.apache.struts2.ServletActionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;

import com.pay10.commons.dao.InvoiceDao;
import com.google.gson.Gson;
import com.pay10.commons.dao.InvoiceDao;
import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.user.Invoice;
import com.pay10.commons.user.InvoiceTrailReport;
import com.pay10.commons.user.User;
import com.pay10.commons.user.UserDao;
import com.pay10.commons.util.Constants;
import com.pay10.pg.action.service.InvoicePaymentService;
import com.pay10.pg.action.service.InvoiceTrailReportService;

/**
 * @author ROHIT
 *
 */
public class InvoicePayAction extends AbstractSecureAction {

	private static final long serialVersionUID = -5586160453950123409L;
	private static Logger logger = LoggerFactory.getLogger(InvoicePayAction.class.getName());

	@Autowired
	private InvoicePaymentService invoicePaymentService;
	private boolean customerConsent;

	//Added By Sweety
	@Autowired
	InvoiceTrailReportService invoiceTrailReportService;


	//Added By Sweety

	@Autowired
	private UserDao userDao;

	@Override
	public String execute() {

		 logger.info("invoicePayAction Invoked...={}",customerConsent);
		Invoice invoice = null;
		try {

			invoice = (Invoice) sessionMap.get(Constants.INVOICE_OBJ.getValue());
			ErrorType invoiceErrorType = (ErrorType) sessionMap.get(Constants.STATUS.getValue());
			if (null == invoice) {
				return "invalidRequest";
			}
			if (!invoiceErrorType.getResponseCode().equals(ErrorType.SUCCESS.getCode())) {
				return "invalidRequest";
			}
			invoice.setCustomerConsent(customerConsent);

			//Added By Sweety to save customer consent and timestamp in new collection
			if(invoice.isCustomerConsent()) {
				invoiceTrailReportService.updateInvoiceTrailReport(invoice);
			}
			String finalRequest = invoicePaymentService.prepareFields(invoice);
			PrintWriter out = ServletActionContext.getResponse().getWriter();

			logger.info("Request  from invoice action to PG" + finalRequest);
			out.write(finalRequest);
			out.flush();
			out.close();

			return NONE;
		} catch (Exception exception) {
			MDC.put(Constants.CRM_LOG_USER_PREFIX.getValue(), invoice.getInvoiceId());
			logger.error("Exception posting request to PG ", exception);
			return ERROR;
		}
	}

	public boolean isCustomerConsent() {
		return customerConsent;
	}

	public void setCustomerConsent(boolean customerConsent) {
		this.customerConsent = customerConsent;
	}

}
