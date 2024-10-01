package com.pay10.crm.action;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

import org.slf4j.Logger; import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;

import com.opensymphony.xwork2.ModelDriven;
import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.user.Invoice;
import com.pay10.commons.user.InvoiceTransactionDao;
import com.pay10.commons.util.Amount;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.CrmFieldConstants;
import com.pay10.commons.util.CrmFieldType;
import com.pay10.commons.util.CrmValidator;
import com.pay10.commons.util.Currency;
import com.pay10.commons.util.InvoiceType;
import com.pay10.commons.util.PropertiesManager;
import com.pay10.crm.actionBeans.InvoiceHasher;

public class InvoicePayEvent extends AbstractSecureAction implements
		ModelDriven<Invoice> {
	
	@Autowired
	private InvoiceTransactionDao InvoiceTransactionDao;
	
	@Autowired
	private CrmValidator validator;
	
	@Autowired
	private InvoiceHasher invoiceHasher;
	
	private Invoice invoice = new Invoice();
	private static final long serialVersionUID = 6999933886992616101L;
	private static Logger logger = LoggerFactory
			.getLogger(InvoicePayEvent.class.getName());
	private String svalue;
	private String invoiceUrl;
	private String hash;
	private String totalamount;
	private String enablePay;
	private String currencyName;

	
	@Override
	public String execute() {
		
		try {

			invoice = InvoiceTransactionDao.findByInvoiceId(svalue);
			if (null == invoice) {
				return ERROR;
			}
		
			if (invoice.getInvoiceType().equals(InvoiceType.SINGLE_PAYMENT.getName())){
				setInvoiceUrl(PropertiesManager.propertiesMap
						.get(CrmFieldConstants.INVOICE_URL.getValue())
						+ invoice.getInvoiceId());
			}
			else if(invoice.getInvoiceType().equals(InvoiceType.PROMOTIONAL_PAYMENT.getName())){
				setInvoiceUrl(PropertiesManager.propertiesMap
						.get(CrmFieldConstants.INVOICE_PROMOTIONAL_URL.getValue())
						+ invoice.getInvoiceId());
			}
			
			setHash(invoiceHasher.createInvoiceHash(invoice));
			totalamount = Amount.formatAmount(invoice.getTotalAmount(),
					invoice.getCurrencyCode());
			setCurrencyName(Currency.getAlphabaticCode(invoice.getCurrencyCode()));
			getEnablePayNow();

		} catch (Exception exception) {
			MDC.put(Constants.CRM_LOG_USER_PREFIX.getValue(),
					invoice.getPayId() + svalue);
			logger.error("Exception", exception);
			return ERROR;
		}
		return SUCCESS;
	}

	@Override
	public void validate() {

		if (validator.validateBlankField(getSvalue())) {
			addFieldError("svalue",
					ErrorType.INVALID_FIELD.getResponseMessage());
		} else if (!validator.validateField(CrmFieldType.INVOICE_ID,
				getSvalue())) {
			addFieldError("svalue",
					ErrorType.INVALID_FIELD.getResponseMessage());
		}
	}

	private void getEnablePayNow() throws ParseException {
		try {
			/*Date date = invoice.getCreateDate();
			Calendar cal = Calendar.getInstance();
			cal.setTime(date);
			cal.add(Calendar.DATE, Integer.parseInt(invoice.getExpiresDay()));
			cal.add(Calendar.HOUR, Integer.parseInt(invoice.getExpiresHour()));
			date = cal.getTime();
			Date dateobj = new Date();
			long diff = dateobj.getTime() - date.getTime();
			int diffInDays = (int) (diff / (24 * 60 * 60 * 1000));
			if (diffInDays > 0) {
				setEnablePay("FALSE");
			} else {
				setEnablePay("TRUE");
			}*/
			
//			Take expireTime from mongo and check with that.
			setEnablePay("TRUE");

		} catch (Exception exception) {
			logger.error("Exception", exception);
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

	public String getSvalue() {
		return svalue;
	}

	public void setSvalue(String svalue) {
		this.svalue = svalue;
	}

	public String getInvoiceUrl() {
		return invoiceUrl;
	}

	public void setInvoiceUrl(String invoiceUrl) {
		this.invoiceUrl = invoiceUrl;
	}

	public String getHash() {
		return hash;
	}

	public void setHash(String hash) {
		this.hash = hash;
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


}
