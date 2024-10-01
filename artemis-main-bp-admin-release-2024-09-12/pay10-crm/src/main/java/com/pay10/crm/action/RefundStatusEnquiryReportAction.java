package com.pay10.crm.action;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.user.StatusEnquiryObject;
import com.pay10.commons.user.User;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.CrmFieldType;
import com.pay10.commons.util.CrmValidator;
import com.pay10.commons.util.DataEncoder;
import com.pay10.commons.util.DateCreater;
import com.pay10.crm.mongoReports.StatusEnquiryReport;

public class RefundStatusEnquiryReportAction extends AbstractSecureAction {

	
	private static final long serialVersionUID = -4993521494377376817L;

	private static Logger logger = LoggerFactory.getLogger(RefundStatusEnquiryReportAction.class.getName());
	
	@Autowired
	private CrmValidator validator;

	@Autowired
	private DataEncoder encoder;

	@Autowired
	private StatusEnquiryReport statusEnquiryReport;

	private String dateFrom;
	private String dateTo;


	private List<StatusEnquiryObject> aaData;
	private User sessionUser = new User();

	@Override
	public String execute() {
		sessionUser = (User) sessionMap.get(Constants.USER.getValue());
		try {
			
			setDateFrom(DateCreater.toDateTimeformatCreater(dateFrom));
			setDateTo(DateCreater.formDateTimeformatCreater(dateTo));

			aaData = encoder.encodeEnquiryTransactionObj(statusEnquiryReport.getStatusEnquiry(dateFrom, dateTo));
			return SUCCESS;

		} catch (Exception exception) {
			logger.error("Exception", exception);
			return ERROR;
		}
	}

	@Override
	public void validate() {

		if (validator.validateBlankField(getDateFrom())) {
		} else if (!validator.validateField(CrmFieldType.DATE_FROM, getDateFrom())) {
			addFieldError(CrmFieldType.DATE_FROM.getName(), ErrorType.INVALID_FIELD.getResponseMessage());
		}
		if (validator.validateBlankField(getDateTo())) {
		} else if (!validator.validateField(CrmFieldType.DATE_TO, getDateTo())) {
			addFieldError(CrmFieldType.DATE_TO.getName(), ErrorType.INVALID_FIELD.getResponseMessage());
		}
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

	public List<StatusEnquiryObject> getAaData() {
		return aaData;
	}

	public void setAaData(List<StatusEnquiryObject> aaData) {
		this.aaData = aaData;
	}
	
	
	

}
