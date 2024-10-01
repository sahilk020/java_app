package com.pay10.crm.action;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.user.User;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.CrmFieldConstants;
import com.pay10.commons.util.CrmFieldType;
import com.pay10.commons.util.CrmValidator;
import com.pay10.commons.util.DateCreater;
import com.pay10.crm.mongoReports.StatisticsReportService;

/**
 * @author Chandan
 *
 */

public class StatisticsReportAction extends AbstractSecureAction {

	private static final long serialVersionUID = 8180378645787578712L;

	private static Logger logger = LoggerFactory.getLogger(StatisticsReportAction.class.getName());

	private String merchant;
	private String dateFrom;
	private String dateTo;
	private String acquirer;
	private String paymentType;
	private JSONObject aaData = new JSONObject();
	private String strJson;
	private User sessionUser = null;
	
	@Autowired
	StatisticsReportService statisticsReportService;

	@Override
	public String execute() {
		try {
			JSONObject jsonSummary = new JSONObject();
			// jsonObj1 = txnSummaryReportService.getSummaryData("2019-03-29 00:00:00",
			// "2019-04-02 00:00:00", "YESBANKCB", "CC-DC");
			sessionUser = (User) sessionMap.get(Constants.USER.getValue());
			String segment = sessionMap.get(Constants.SEGMENT.getValue()).toString();
			setDateFrom(DateCreater.toDateTimeformatCreater(dateFrom));
			setDateTo(DateCreater.toDateTimeformatCreater(dateTo));
			jsonSummary = statisticsReportService.getSummaryData(merchant,dateFrom, dateTo, acquirer, paymentType, segment, sessionUser.getRole().getId());
			setStrJson(jsonSummary.toString());
			return INPUT;
		} catch (Exception e) {
			logger.error("Exception occured in execute method , StatisticsReportAction , Exception = " + e);
			return ERROR;
		}
	}

	@Override
	public void validate() {
		CrmValidator validator = new CrmValidator();

		if (validator.validateBlankField(getDateFrom())) {
		} else if (!validator.validateField(CrmFieldType.DATE_FROM, getDateFrom())) {
			addFieldError(CrmFieldType.DATE_FROM.getName(), ErrorType.INVALID_FIELD.getResponseMessage());
		}

		if (validator.validateBlankField(getDateTo())) {
		} else if (!validator.validateField(CrmFieldType.DATE_TO, getDateTo())) {
			addFieldError(CrmFieldType.DATE_TO.getName(), ErrorType.INVALID_FIELD.getResponseMessage());
		}

		if (!validator.validateBlankField(getDateTo())) {
			if (DateCreater.formatStringToDate(DateCreater.formatFromDate(getDateFrom()))
					.compareTo(DateCreater.formatStringToDate(DateCreater.formatFromDate(getDateTo()))) > 0) {
				addFieldError(CrmFieldType.DATE_FROM.getName(), CrmFieldConstants.FROMTO_DATE_VALIDATION.getValue());
			} else if (DateCreater.diffDate(getDateFrom(), getDateTo()) > 31) {
				addFieldError(CrmFieldType.DATE_FROM.getName(), CrmFieldConstants.DATE_RANGE.getValue());
			}
		}

		if (validator.validateBlankField(getAcquirer())
				|| getAcquirer().equals(CrmFieldConstants.SELECT_ACQUIRER.getValue())) {
			addFieldError(CrmFieldType.ACQUIRER.getName(), ErrorType.INVALID_FIELD.getResponseMessage());
		}

		if (validator.validateBlankField(getPaymentType())
				|| getPaymentType().equals(CrmFieldConstants.SELECT_PAYMENT.getValue())) {
			addFieldError(CrmFieldType.PAYMENT_TYPE.getName(), ErrorType.INVALID_FIELD.getResponseMessage());
		}

	}

	public JSONObject getAaData() {
		return aaData;
	}

	public void setAaData(JSONObject aaData) {
		this.aaData = aaData;
	}

	public String getStrJson() {
		return strJson;
	}

	public void setStrJson(String strJson) {
		this.strJson = strJson;
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

	public String getAcquirer() {
		return acquirer;
	}

	public void setAcquirer(String acquirer) {
		this.acquirer = acquirer;
	}

	public String getPaymentType() {
		return paymentType;
	}

	public void setPaymentType(String paymentType) {
		this.paymentType = paymentType;
	}

	public String getMerchant() {
		return merchant;
	}

	public void setMerchant(String merchant) {
		this.merchant = merchant;
	}

}
