package com.pay10.crm.action;

import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.pay10.commons.dto.ReportingCollection;
import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.user.TransactionSearchNew;
import com.pay10.commons.user.User;
import com.pay10.commons.user.UserType;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.CrmFieldConstants;
import com.pay10.commons.util.CrmFieldType;
import com.pay10.commons.util.CrmValidator;
import com.pay10.commons.util.DataEncoder;
import com.pay10.commons.util.DateCreater;
import com.pay10.crm.actionBeans.SessionUserIdentifier;
import com.pay10.crm.mongoReports.DashboardService;
import com.pay10.crm.mongoReports.TxnReports;

public class DashboardReportForException extends AbstractSecureAction {

	@Autowired
	private DataEncoder encoder;

	@Autowired
	private TxnReports txnReports;

	private static Logger logger = LoggerFactory.getLogger(DashboardReportForException.class.getName());

	private static final long serialVersionUID = -6919220389124792416L;
	@Autowired
	private DashboardService dashboardService;

	private String t;
	private String dateFrom;
	private String dateTo;
	private String date;
	private int draw;
	private int length;
	private int start;
	//add new filename
	private String fileName;

	private BigInteger recordsTotal = BigInteger.ZERO;
	private BigInteger recordsFiltered = BigInteger.ZERO;

	private List<ReportingCollection> aaData = new ArrayList<ReportingCollection>();

	@Override
	public String execute() {

		try {

			logger.info("Search Type : " + t + "\tstart : " + start + "\tlength : " + length + "\t" + fileName);
			
			if (!StringUtils.isBlank(dateTo) && !StringUtils.isBlank(dateFrom)) {
				dateTo=dateTo+" 23:59:59";
				dateFrom=dateFrom+" 00:00:00";
				
				BigInteger bigInt = BigInteger.valueOf(dashboardService.dashboardExceptionReportCount(t, start, length,dateFrom,dateTo,fileName));// ,ipAddress,totalAmount));
				setRecordsTotal(bigInt);
				if (getLength() == -1) {
					setLength(getRecordsTotal().intValue());
				}
				aaData = dashboardService.dashboardExceptionReportFile(t, start, length,dateFrom,dateTo,fileName);
				recordsFiltered = recordsTotal;
			}else {
				String startDate=new SimpleDateFormat("yyyy-MM-dd").format(new SimpleDateFormat("yyyy-MM-dd").parse(date));
				String endDate=startDate+" 23:59:59";
				startDate=startDate+" 00:00:00";
				
				BigInteger bigInt = BigInteger.valueOf(dashboardService.dashboardExceptionReportCount(t, start, length,startDate,endDate,fileName));// ,ipAddress,totalAmount));
				setRecordsTotal(bigInt);
				if (getLength() == -1) {
					setLength(getRecordsTotal().intValue());
				}
				
				aaData = dashboardService.dashboardExceptionReportFile(t, start, length,startDate,endDate,fileName);

				recordsFiltered = recordsTotal;
			}

		} catch (Exception exception) {
			logger.error("Exception", exception);
			return ERROR;
		}
		return SUCCESS;

	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public List<ReportingCollection> getAaData() {
		return aaData;
	}

	public void setAaData(List<ReportingCollection> aaData) {
		this.aaData = aaData;
	}

	public int getDraw() {
		return draw;
	}

	public void setDraw(int draw) {
		this.draw = draw;
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

	public String getT() {
		return t;
	}

	public void setT(String t) {
		this.t = t;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
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
	
}
