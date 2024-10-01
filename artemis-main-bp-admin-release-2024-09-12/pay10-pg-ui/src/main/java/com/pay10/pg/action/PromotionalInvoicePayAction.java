package com.pay10.pg.action;

import java.io.PrintWriter;
import org.apache.struts2.ServletActionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;

import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.user.Invoice;
import com.pay10.commons.user.User;
import com.pay10.commons.user.UserDao;
import com.pay10.commons.util.Constants;
import com.pay10.pg.action.service.InvoicePaymentService;
import com.pay10.pg.action.service.InvoiceTrailReportService;

/**
 * @author ROHIT
 *
 */
public class PromotionalInvoicePayAction extends AbstractSecureAction {

	private static final long serialVersionUID = -5586160453950123409L;
	private static Logger logger = LoggerFactory.getLogger(PromotionalInvoicePayAction.class.getName());

	@Autowired
	private InvoicePaymentService invoicePaymentService;

	private String name;
	private String phoneNo;
	private String emailId;
	private String city;
	private String state;
	private String country;
	private String zip;
	private String custAddress;
	private boolean customerConsent;
	
	@Autowired
	private UserDao userDao;
	
	//Added By Sweety
	@Autowired
	InvoiceTrailReportService invoiceTrailReportService;
	
	@Override
	public String execute() {
		
		 logger.info("PromotionalPayAction Invoked...={}",customerConsent);

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
			invoice.setAddress(custAddress);
			invoice.setName(name);
			invoice.setEmail(emailId);
			invoice.setPhone(phoneNo);
			invoice.setZip(zip);
			invoice.setCity(city);
			invoice.setState(state);
			invoice.setCountry(country);
			invoice.setCustomerConsent(customerConsent);
			User user=userDao.findPayId1(invoice.getPayId());
			invoice.setPayText(user.getPayText());
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPhoneNo() {
		return phoneNo;
	}

	public void setPhoneNo(String phoneNo) {
		this.phoneNo = phoneNo;
	}

	public String getEmailId() {
		return emailId;
	}

	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getZip() {
		return zip;
	}

	public void setZip(String zip) {
		this.zip = zip;
	}

	public String getCustAddress() {
		return custAddress;
	}

	public void setCustAddress(String custAddress) {
		this.custAddress = custAddress;
	}

	public boolean isCustomerConsent() {
		return customerConsent;
	}

	public void setCustomerConsent(boolean customerConsent) {
		this.customerConsent = customerConsent;
	}

}
