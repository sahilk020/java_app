package com.pay10.crm.action;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.user.SummaryObject;
import com.pay10.commons.user.User;
import com.pay10.commons.user.UserType;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.CrmFieldType;
import com.pay10.commons.util.CrmValidator;
import com.pay10.commons.util.DateCreater;
import com.pay10.commons.util.PropertiesManager;
import com.pay10.crm.actionBeans.SessionUserIdentifier;

public class DownloadSummaryReportAction extends AbstractSecureAction {

	private static final long serialVersionUID = -1862272004872627431L;

	private static Logger logger = LoggerFactory.getLogger(DownloadSummaryReportAction.class.getName());

	SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.ENGLISH);
	private String currency;
	private String dateFrom;
	private String dateTo;
	private String merchantPayId;
	private String paymentType;
	private String mopType;
	private String transactionType;
	private String status;
	private String acquirer;
	private String paymentMethods;
	private InputStream fileInputStream;
	private String filename;
	private String paymentsRegion;
	private String cardHolderType;
	private String pgRefNum;
	private boolean dateLimit = false;

	public static final BigDecimal ONE_HUNDRED = new BigDecimal(100);

	@Autowired
	private SummaryReportQuery summaryReportQuery;

	@Autowired
	private CrmValidator validator;
	@Autowired
    private PropertiesManager propertiesManager;

	@Autowired
	private SessionUserIdentifier userIdentifier;

	@Override
	@SuppressWarnings("resource")
	public String execute() {

		User sessionUser = (User) (sessionMap.get(Constants.USER.getValue()));

		logger.info("Inside download summary report action");
		if (StringUtils.isBlank(acquirer)) {
			acquirer = "ALL";

		}

		if (StringUtils.isBlank(paymentsRegion)) {
			paymentsRegion = "ALL";

		}

		if (StringUtils.isBlank(mopType)) {
			mopType = "ALL";

		}

		if (StringUtils.isBlank(transactionType)) {
			transactionType = "ALL";

		}

		if (StringUtils.isBlank(merchantPayId)) {
			merchantPayId = "ALL";

		}

		if (StringUtils.isBlank(paymentType)) {
			paymentType = "ALL";

		}
	    logger.info("Date From : {}  ",getDateFrom());
		logger.info("Date TO : {}  ",getDateTo());
		logger.info("Date Diff : {}  ",DateCreater.diffDate(getDateFrom(), getDateTo()));
		if (DateCreater.diffDate(getDateFrom(), getDateTo()) > 31) {
			dateLimit = true;
		}

		setDateFrom(DateCreater.toDateTimeformatCreater(dateFrom));
		setDateTo(DateCreater.formDateTimeformatCreater(dateTo));

		String merchaPayId = userIdentifier.getMerchantPayId(sessionUser, merchantPayId);

		sessionUser = (User) sessionMap.get(Constants.USER.getValue());
		List<SummaryObject> transactionList = new ArrayList<SummaryObject>();
		try {
			transactionList = summaryReportQuery.summaryReportDownloadNew(dateFrom, dateTo, merchaPayId, paymentType,
					acquirer, currency, sessionUser, getPaymentsRegion(), getCardHolderType(), "", mopType,
					transactionType);
		} catch (Exception e) {
			logger.error("Exception", e);
		}

		logger.info("List generated successfully for Download summary Report , size = " + transactionList.size());
		SXSSFWorkbook wb = new SXSSFWorkbook(100);
		Row row;
		int rownum = 0;
		// Create a blank sheet
		Sheet sheet = wb.createSheet("Summary Report");

		row = sheet.createRow(0);

		if (sessionUser.getUserType().equals(UserType.MERCHANT)) {

			row.createCell(0).setCellValue("Sr No");
			row.createCell(1).setCellValue("Txn Id");
			row.createCell(2).setCellValue("Pg Ref Num");
			row.createCell(3).setCellValue("Payment Method");
			row.createCell(4).setCellValue("Mop Type");
			row.createCell(5).setCellValue("Order Id");
			row.createCell(6).setCellValue("Business Name");
			row.createCell(7).setCellValue("Currency");
			row.createCell(8).setCellValue("Transaction Type");
			row.createCell(9).setCellValue("Capture Date");
			row.createCell(10).setCellValue("Settlement Date");
			row.createCell(11).setCellValue("Transaction Region");
			row.createCell(12).setCellValue("Card Holder Type");
			row.createCell(13).setCellValue("Transaction Amount");
			row.createCell(14).setCellValue("Total Amount");
			row.createCell(15).setCellValue("TDR/SC");
			row.createCell(16).setCellValue("GST");
			row.createCell(17).setCellValue("Net Amount");
			row.createCell(18).setCellValue("ACQ ID");
			row.createCell(19).setCellValue("RRN");
			row.createCell(20).setCellValue("Post Settled Flag");
		}

		else {

			// For admin and Subadmin
			row.createCell(0).setCellValue("Sr No");
			row.createCell(1).setCellValue("Txn Id");
			row.createCell(2).setCellValue("Pg Ref Num");
			row.createCell(3).setCellValue("Payment Method");
			row.createCell(4).setCellValue("Mop Type");
			row.createCell(5).setCellValue("Order Id");
			row.createCell(6).setCellValue("Business Name");
			row.createCell(7).setCellValue("Currency");
			row.createCell(8).setCellValue("Transaction Type");
			row.createCell(9).setCellValue("Capture Date");
			row.createCell(10).setCellValue("Settlement Date");
			row.createCell(11).setCellValue("Transaction Region");
			row.createCell(12).setCellValue("Card Holder Type");
			row.createCell(13).setCellValue("Acquirer");
			row.createCell(14).setCellValue("Transaction Amount");
			row.createCell(15).setCellValue("Total Amount");
			row.createCell(16).setCellValue("TDR/SC (Acquirer)");
			row.createCell(19).setCellValue("GST (Acquirer)");
			row.createCell(17).setCellValue("TDR/SC (Cashlesso)");
			row.createCell(20).setCellValue("GST (Cashlesso)");
			row.createCell(18).setCellValue("Total TDR/SC");
			row.createCell(21).setCellValue("Total GST");
			row.createCell(22).setCellValue("Net Amount");
			row.createCell(23).setCellValue("ACQ ID");
			row.createCell(24).setCellValue("RRN");
			row.createCell(25).setCellValue("Post Settled Flag");

		}

		if (!dateLimit && transactionList.size() < 800000) {

			for (SummaryObject transactionSearch : transactionList) {
				row = sheet.createRow(++rownum);
				Object[] objArr = null;

				if (sessionUser.getUserType().equals(UserType.MERCHANT)) {
					objArr = transactionSearch.merchantDownloadSummaryReport();
				}

				else {
					objArr = transactionSearch.myCsvMethodDownloadSummaryReport();
				}

				int cellnum = 0;
				for (Object obj : objArr) {
					// this line creates a cell in the next column of that row
					Cell cell = row.createCell(cellnum++);
					if (obj instanceof String)
						cell.setCellValue((String) obj);
					else if (obj instanceof Integer)
						cell.setCellValue((Integer) obj);

				}
			}
		} else {

			if (dateLimit) {

				row = sheet.createRow(rownum++);
				Cell cell = row.createCell(1);
				cell.setCellValue("No. of days can not be more than 31");

			} else {
				row = sheet.createRow(rownum++);
				Cell cell = row.createCell(1);
				cell.setCellValue("Data limit exceeded for excel file generation , please select a smaller date range");
			}

		}

		try {
			String FILE_EXTENSION = ".xlsx";
			DateFormat df = new SimpleDateFormat("yyyyMMddhhmmss");
			filename = "Summary_Report_" + df.format(new Date()) + FILE_EXTENSION;
			File file = new File(PropertiesManager.propertiesMap.get(Constants.DOWNLOAD_PATH.getValue()) +File.separator+filename);
            
            logger.info("moni........"+PropertiesManager.propertiesMap.get(Constants.DOWNLOAD_PATH.getValue()));
			// this Writes the workbook
			FileOutputStream out = new FileOutputStream(file);
			wb.write(out);
			out.flush();
			out.close();
			wb.dispose();
			fileInputStream = new FileInputStream(file);
			logger.info("moni>>>>>>"+file);
			addActionMessage(filename + " written successfully on disk.");
		} catch (Exception exception) {
			logger.error("Exception", exception);
		}

		logger.info("File generated successfully for Download summary Report");
		return SUCCESS;

	}

	@Override
	public void validate() {

		if (validator.validateBlankField(getDateTo())) {
		} else if (!validator.validateField(CrmFieldType.DATE_TO, getDateTo())) {
			addFieldError(CrmFieldType.DATE_TO.getName(), ErrorType.INVALID_FIELD.getResponseMessage());
		}
		if (validator.validateBlankField(getDateFrom())) {
		} else if (!validator.validateField(CrmFieldType.DATE_FROM, getDateFrom())) {
			addFieldError(CrmFieldType.DATE_FROM.getName(), ErrorType.INVALID_FIELD.getResponseMessage());
		}
	}

//		if (!validator.validateBlankField(getDateTo())) {
//			if (DateCreater.formatStringToDate(DateCreater.formatFromDate(getDateFrom()))
//					.compareTo(DateCreater.formatStringToDate(DateCreater.formatFromDate(getDateTo()))) > 0) {
//				addFieldError(CrmFieldType.DATE_TO.getName(), ErrorType.INVALID_FIELD.getResponseMessage());
//			} else if (DateCreater.diffDate(getDateFrom(), getDateTo()) > 31) {
//				addFieldError(CrmFieldType.DATE_FROM.getName(), ErrorType.INVALID_FIELD.getResponseMessage());
//			}
//		}

	public InputStream getFileInputStream() {
		return fileInputStream;
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

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public String getPaymentsRegion() {
		return paymentsRegion;
	}

	public void setPaymentsRegion(String paymentsRegion) {
		this.paymentsRegion = paymentsRegion;
	}

	public String getCardHolderType() {
		return cardHolderType;
	}

	public void setCardHolderType(String cardHolderType) {
		this.cardHolderType = cardHolderType;
	}

	public String getPaymentType() {
		return paymentType;
	}

	public void setPaymentType(String paymentType) {
		this.paymentType = paymentType;
	}

	public String getTransactionType() {
		return transactionType;
	}

	public void setTransactionType(String transactionType) {
		this.transactionType = transactionType;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getMerchantPayId() {
		return merchantPayId;
	}

	public void setMerchantPayId(String merchantPayId) {
		this.merchantPayId = merchantPayId;
	}

	public String getAcquirer() {
		return acquirer;
	}

	public void setAcquirer(String acquirer) {
		this.acquirer = acquirer;
	}

	public String getPaymentMethods() {
		return paymentMethods;
	}

	public void setPaymentMethods(String paymentMethods) {
		this.paymentMethods = paymentMethods;
	}

	public String getMopType() {
		return mopType;
	}

	public void setMopType(String mopType) {
		this.mopType = mopType;
	}

	public String getPgRefNum() {
		return pgRefNum;
	}

	public void setPgRefNum(String pgRefNum) {
		this.pgRefNum = pgRefNum;
	}

}
