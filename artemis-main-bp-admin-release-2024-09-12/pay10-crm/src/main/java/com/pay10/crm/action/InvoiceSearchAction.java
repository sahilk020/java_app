package com.pay10.crm.action;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.pay10.commons.dao.InvoiceDao;
import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.user.Invoice;
import com.pay10.commons.user.User;
import com.pay10.commons.user.UserType;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.CrmFieldConstants;
import com.pay10.commons.util.CrmFieldType;
import com.pay10.commons.util.CrmValidator;
import com.pay10.commons.util.DataEncoder;
import com.pay10.commons.util.DateCreater;

public class InvoiceSearchAction extends AbstractSecureAction {
	
	@Autowired
	private CrmValidator validator;
	
	@Autowired
	private DataEncoder encoder;
	
	@Autowired
	private InvoiceDao getInvoice;

	private static Logger logger = LoggerFactory.getLogger(InvoiceSearchAction.class.getName());
	private static final long serialVersionUID = 8559806979618843084L;

	private List<Invoice> aaData = new ArrayList<>();
	private String invoiceNo;
	private String customerEmail;
	private String payId;
	private String currency;
	private String dateFrom;
	private String dateTo;
	private String invoiceType;
	private int length;
	private int draw;
	private int start;
	private BigInteger recordsTotal = BigInteger.ZERO;
	public BigInteger recordsFiltered = BigInteger.ZERO;
	@SuppressWarnings("unchecked")
	@Override
	public String execute() {
		User sessionUser = (User) sessionMap.get(Constants.USER.getValue());

		try {
		
		if(sessionUser.getUserType().equals(UserType.ADMIN)) {
			Map<String, Object> map = getInvoice.getInvoiceList(getDateFrom(), getDateTo(), getPayId(), getInvoiceNo(), getCustomerEmail(), getCurrency(),getInvoiceType(), getStart(), getLength());
					
			List<Invoice> list = (List<Invoice>) map.get("list");
			setAaData(encoder.encodeInvoiceSearchObj(list));
			BigInteger bigInt = BigInteger.valueOf((Long) map.get("count"));
			setRecordsTotal(bigInt);
			setRecordsFiltered(bigInt);
		}else if(sessionUser.getUserType().equals(UserType.MERCHANT)){
			Map<String, Object> map = getInvoice.getInvoiceList(
					getDateFrom(), getDateTo(), sessionUser.getPayId(), getInvoiceNo(),
					getCustomerEmail(), getCurrency(),getInvoiceType(), getStart(), getLength());

			List<Invoice> list = (List<Invoice>) map.get("list");
			setAaData(encoder.encodeInvoiceSearchObj(list));
			BigInteger bigInt = BigInteger.valueOf((Long) map.get("count"));
			setRecordsTotal(bigInt);
			setRecordsFiltered(bigInt);
			
		}else if(sessionUser.getUserType().equals(UserType.SUBUSER)) {
			Map<String, Object> map = getInvoice.getInvoiceList(
					getDateFrom(), getDateTo(), sessionUser.getParentPayId(), getInvoiceNo(),
					getCustomerEmail(), getCurrency(),getInvoiceType(), getStart(), getLength());
			
			List<Invoice> list = (List<Invoice>) map.get("list"); 
			setAaData(encoder.encodeInvoiceSearchObj(list));
			BigInteger bigInt = BigInteger.valueOf((Long) map.get("count"));
			setRecordsTotal(bigInt);
			setRecordsFiltered(bigInt);
		}else if(sessionUser.getUserType().equals(UserType.SUBADMIN)) {
			Map<String, Object> map = getInvoice.getInvoiceList(
					getDateFrom(), getDateTo(), getPayId(), getInvoiceNo(),
					getCustomerEmail(), getCurrency(),getInvoiceType(), getStart(), getLength());
			
			List<Invoice> list = (List<Invoice>) map.get("list");
			setAaData(encoder.encodeInvoiceSearchObj(list));
			BigInteger bigInt = BigInteger.valueOf((Long) map.get("count"));
			setRecordsTotal(bigInt);
			setRecordsFiltered(bigInt);
		}
			} catch (Exception exception) {
			logger.error("Exception", exception);
		}
		return SUCCESS;
		
	}

	@Override
	public void validate() {

		if (validator.validateBlankField(getInvoiceNo())) {
		} else if (!validator.validateField(CrmFieldType.INVOICE_NUMBER,
				getInvoiceNo())) {
			addFieldError("invoiceNo",
					ErrorType.INVALID_FIELD.getResponseMessage());
		}

		if (validator.validateBlankField(getCurrency())) {
		} else if (!validator.validateField(CrmFieldType.CURRENCY,
				getCurrency())) {
			addFieldError(CrmFieldType.CURRENCY.getName(),
					ErrorType.INVALID_FIELD.getResponseMessage());
		}

		if (validator.validateBlankField(getDateFrom())) {
		} else if (!validator.validateField(CrmFieldType.DATE_FROM,
				getDateFrom())) {
			addFieldError(CrmFieldType.DATE_FROM.getName(),
					ErrorType.INVALID_FIELD.getResponseMessage());
		}

		if (validator.validateBlankField(getDateTo())) {
		} else if (!validator.validateField(CrmFieldType.DATE_TO, getDateTo())) {
			addFieldError(CrmFieldType.DATE_TO.getName(),
					ErrorType.INVALID_FIELD.getResponseMessage());
		}
		
		if(!validator.validateBlankField(getDateTo())){
	 	       if(DateCreater.formatStringToDate(DateCreater.formatFromDate(getDateFrom())).compareTo(DateCreater.formatStringToDate(DateCreater.formatFromDate(getDateTo()))) > 0) {
	 	        	addFieldError(CrmFieldType.DATE_FROM.getName(), CrmFieldConstants.FROMTO_DATE_VALIDATION.getValue());
	 	        }
	 	        else if(DateCreater.diffDate(getDateFrom(), getDateTo()) > 31) {
	 	        	addFieldError(CrmFieldType.DATE_FROM.getName(), CrmFieldConstants.DATE_RANGE.getValue());
	 	        }
	         }

		if (validator.validateBlankField(getPayId())
				|| getPayId()
						.equals(CrmFieldConstants.ALL.getValue())) {
		} else if (!validator.validateField(CrmFieldType.PAY_ID,
				getPayId())) {
			addFieldError(CrmFieldType.PAY_ID.getName(),
					ErrorType.INVALID_FIELD.getResponseMessage());
		}

		if (validator.validateBlankField(getCustomerEmail())) {
		} else if (!validator.validateField(CrmFieldType.CUSTOMER_EMAIL_ID,
				getCustomerEmail())) {
			addFieldError(CrmFieldType.CUSTOMER_EMAIL_ID.getName(),
					ErrorType.INVALID_FIELD.getResponseMessage());
		}
	}

	public List<Invoice> getAaData() {
		return aaData;
	}

	public void setAaData(List<Invoice> aaData) {
		this.aaData = aaData;
	}

	public String getInvoiceNo() {
		return invoiceNo;
	}

	public void setInvoiceNo(String invoiceNo) {
		this.invoiceNo = invoiceNo;
	}

	public String getCustomerEmail() {
		return customerEmail;
	}

	public void setCustomerEmail(String customerEmail) {
		this.customerEmail = customerEmail;
	}

	public String getPayId() {
		return payId;
	}

	public void setPayId(String payId) {
		this.payId = payId;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public String getDateFrom() {
		return dateFrom;
	}

	public void setDateFrom(String dateFrom) {
		this.dateFrom = dateFrom;
	}

	public String getDateTo() {
		return dateTo;
	}

	public void setDateTo(String dateTo) {
		this.dateTo = dateTo;
	}

	public String getInvoiceType() {
		return invoiceType;
	}

	public void setInvoiceType(String invoiceType) {
		this.invoiceType = invoiceType;
	}

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public int getStart() {
		return start;
	}

	public void setStart(int start) {
		this.start = start;
	}

	public int getDraw() {
		return draw;
	}

	public void setDraw(int draw) {
		this.draw = draw;
	}

	public BigInteger getRecordsTotal() {
		return recordsTotal;
	}

	public void setRecordsTotal(BigInteger recordsTotal) {
		this.recordsTotal = recordsTotal;
	}

	public BigInteger getRecordsFiltered() {
		return recordsFiltered;
	}

	public void setRecordsFiltered(BigInteger recordsFiltered) {
		this.recordsFiltered = recordsFiltered;
	}

	
}
