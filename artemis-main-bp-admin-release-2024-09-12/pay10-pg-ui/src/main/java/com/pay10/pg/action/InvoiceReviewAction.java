package com.pay10.pg.action;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;

import com.opensymphony.xwork2.ModelDriven;
import com.pay10.commons.dao.InvoiceDao;
import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.user.Invoice;
import com.pay10.commons.user.InvoiceTrailReport;
import com.pay10.commons.user.User;
import com.pay10.commons.user.UserDao;
import com.pay10.commons.util.Amount;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.CrmFieldType;
import com.pay10.commons.util.CrmValidator;
import com.pay10.commons.util.Currency;
import com.pay10.commons.util.InvoiceType;
import com.pay10.pg.action.service.InvoiceReviewService;

/**
 * @author ROHIT
 *
 */
public class InvoiceReviewAction extends AbstractSecureAction implements ModelDriven<Invoice>{
	@Autowired
	private InvoiceDao invoiceDao;

	@Autowired
	private CrmValidator validator;
	
	@Autowired
	private InvoiceReviewService invoiceReviewService;

	private Invoice invoice = new Invoice();
	private static final long serialVersionUID = 6995933586292716101L;
	private static Logger logger = LoggerFactory.getLogger(InvoiceReviewAction.class.getName());
	private String svalue;
	private String totalamount;
	private String enablePay;
	private String currencyName;
	private String invoiceId;
	@Autowired
	private UserDao userDao;
	private String invoiceText;
	

	@Override
	public String execute() {

		
		try {
			if(!validate(svalue)) {
				return "invalidRequest";
			}
			//Get invoice object from DB
			invoice = invoiceDao.findByInvoiceId(svalue);
			User user=userDao.findPayId1(invoice.getPayId());
			invoice.setTncStatus(user.isTncStatus());
			invoice.setInvoiceText(user.getInvoiceText());
			invoice.setPayText(user.getPayText());
			logger.info("TNC Status...={}",invoice.isTncStatus());
			
			if (null == invoice) {
				return "invalidRequest";
			}
			ErrorType errorType = null;
			String invoicePage = "";
			totalamount = Amount.formatAmount(invoice.getTotalAmount(), invoice.getCurrencyCode());
			setCurrencyName(Currency.getAlphabaticCode(invoice.getCurrencyCode()));
			if (invoice.getInvoiceType().equals(InvoiceType.SINGLE_PAYMENT)) {
				errorType = invoiceReviewService.validateSingleInvoiceStatus(invoice);
				invoicePage = "single";
			}else if (invoice.getInvoiceType().equals(InvoiceType.PROMOTIONAL_PAYMENT)) {
				errorType = invoiceReviewService.validatePromotionalInvoiceStatus(invoice);
				invoicePage = "promotional";
			}
			addActionMessage(errorType.getResponseMessage());
			sessionMap.put(Constants.INVOICE_OBJ.getValue(), invoice);
			sessionMap.put(Constants.STATUS.getValue(), errorType);
			if(errorType.getResponseCode().equals(ErrorType.SUCCESS.getResponseCode())) {
				setEnablePay("TRUE");
			}else {
				setEnablePay("FALSE");
			}
			return invoicePage;
		} catch (Exception exception) {
			MDC.put(Constants.CRM_LOG_USER_PREFIX.getValue(), invoice.getPayId() + svalue);
			logger.error("Error validating Invoice", exception);
			return ERROR;
		}

	}


	public boolean validate(String sValue) {

		if (validator.validateBlankField(getSvalue())) {
			return false;
		} else if (!validator.validateField(CrmFieldType.INVOICE_ID, getSvalue())) {
			return false;
		}else {
			return true;
		}
	}

	@Override
	public Invoice getModel() {
		return invoice;
	}

	public Invoice getInvoice() {
		return invoice;
	}

	public void setInvoice(Invoice invoice) {
		this.invoice = invoice;
	}

	public String getInvoiceId() {
		return invoiceId;
	}

	public void setInvoiceId(String invoiceId) {
		this.invoiceId = invoiceId;
	}

	public String getSvalue() {
		return svalue;
	}

	public void setSvalue(String svalue) {
		this.svalue = svalue;
	}

	public String getTotalamount() {
		return totalamount;
	}

	public void setTotalamount(String totalamount) {
		this.totalamount = totalamount;
	}

	public String getEnablePay() {
		return enablePay;
	}

	public void setEnablePay(String enablePay) {
		this.enablePay = enablePay;
	}

	public String getCurrencyName() {
		return currencyName;
	}

	public void setCurrencyName(String currencyName) {
		this.currencyName = currencyName;
	}


	public String getInvoiceText() {
		return invoiceText;
	}


	public void setInvoiceText(String invoiceText) {
		this.invoiceText = invoiceText;
	}

}
