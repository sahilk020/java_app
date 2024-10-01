package com.pay10.crm.action;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.user.CompanyProfile;
import com.pay10.commons.user.CompanyProfileDao;
import com.pay10.commons.user.User;
import com.pay10.commons.user.UserDao;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.CrmFieldConstants;
import com.pay10.commons.util.CrmFieldType;
import com.pay10.commons.util.CrmValidator;
import com.pay10.commons.util.DateCreater;
import com.pay10.commons.util.PropertiesManager;

/**
 * @author Rajendra
 *
 */

public class MonthlyInvoiceReportAction extends AbstractSecureAction {

	private static final long serialVersionUID = -1471532495305994799L;
	private static Logger logger = LoggerFactory.getLogger(MonthlyInvoiceReportAction.class.getName());
	private String currency;
	private String year;
	private String month;
	private String payId;
	private InputStream fileInputStream;
	private String filename;

	private static final String companyProfileTenantId = Constants.DEFAULT_COMPANY_TENANTID.getValue();

	@Autowired
	private CompanyProfileDao companyProfileDao;

	@Autowired
	private UserDao userDao;

	@Autowired
	private CrmValidator validator;

	@Autowired
	private SettlementReportQuery srq;

	@Autowired
	private MonthlyInvoiceService mis;

	@Override
	public String execute() {
		Map<String, String> dataProviderMap = new HashMap<String, String>();
		String dateFrom;
		String dateTo;
		SimpleDateFormat sdf = new SimpleDateFormat(CrmFieldConstants.INPUT_DATE_FORMAT.getValue());
		Calendar c = Calendar.getInstance();
		c.set(Calendar.MONTH, Integer.parseInt(month) - 1);
		c.set(Calendar.YEAR, Integer.parseInt(year));
		c.set(Calendar.DAY_OF_MONTH, 1);
		dateFrom = sdf.format(c.getTime());

		Calendar c2 = Calendar.getInstance();
		c2.set(Calendar.MONTH, Integer.parseInt(month));
		c2.set(Calendar.YEAR, Integer.parseInt(year));
		c2.set(Calendar.DAY_OF_MONTH, 0);
		dateTo = sdf.format(c2.getTime());

		String invoiceDateTo = dateTo;

		dateFrom = DateCreater.toDateTimeformatCreater(dateFrom);
		dateTo = DateCreater.formDateTimeformatCreater(dateTo);

		try {

			logger.info("Inside Monthly invoice Report Download");

			CompanyProfile cp = new CompanyProfile();
			cp = companyProfileDao
					.getCompanyProfileByTenantId(PropertiesManager.propertiesMap.get(companyProfileTenantId));
			User user = new User();
			user = userDao.getUserClass(payId);
			Map<String, String> amountMap = new HashMap<String, String>();
			amountMap = srq.settlementReportDownload(payId, currency, dateFrom, dateTo, cp, user);

			logger.info("List generated successfully for Monthly Invoice Report , size = " + amountMap.size());

			// Here below map is used for the other values sending in map.
			dataProviderMap.put("MAIN_HEADING", CrmFieldConstants.MESSAGE_MIS_HEADING.getValue());

			if(amountMap.size() == 0) {
				addActionMessage("There is no transaction happened for this Month,So no MIS Report");
				return INPUT;
			}
			
			DateFormat df = new SimpleDateFormat("yyyyMMddhhmmss");
			filename = "Invoice_" + df.format(new Date()) + ".pdf";
			File file = new File(filename);
			file = mis.createPdfFile(dataProviderMap, cp, user, amountMap, invoiceDateTo, file);
			fileInputStream = new FileInputStream(file);
			IOUtils.closeQuietly(new FileInputStream(file));

			return SUCCESS;
		} catch (Exception e) {
			logger.error("Failed to Generate the Monthly Invoice Report, Exception is: " + e);
			addActionMessage("Failed to download the file");
			return INPUT;
		}
	}

	@Override
	public void validate() {

		if (validator.validateBlankField(getYear())) {
		} else if (!validator.validateField(CrmFieldType.INVOICE_YEAR, getYear())) {
			addFieldError(CrmFieldType.INVOICE_YEAR.getName(), ErrorType.INVALID_FIELD.getResponseMessage());
		}

		if (validator.validateBlankField(getMonth())) {
		} else if (!validator.validateField(CrmFieldType.INVOICE_MONTH, getMonth())) {
			addFieldError(CrmFieldType.INVOICE_MONTH.getName(), ErrorType.INVALID_FIELD.getResponseMessage());
		}

		if (validator.validateBlankField(getCurrency())) {
		} else if (!validator.validateField(CrmFieldType.CURRENCY, getCurrency())) {
			addFieldError(CrmFieldType.CURRENCY.getName(), ErrorType.INVALID_FIELD.getResponseMessage());
		}

		if (validator.validateBlankField(getPayId())) {
		} else if (!validator.validateField(CrmFieldType.PAY_ID, getPayId())) {
			addFieldError(CrmFieldType.PAY_ID.getName(), ErrorType.INVALID_FIELD.getResponseMessage());
		}

	}

	public InputStream getFileInputStream() {
		return fileInputStream;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = year;
	}

	public String getMonth() {
		return month;
	}

	public void setMonth(String month) {
		this.month = month;
	}

	public String getPayId() {
		return payId;
	}

	public void setPayId(String payId) {
		this.payId = payId;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

}
