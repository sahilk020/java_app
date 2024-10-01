package com.pay10.crm.action;

import java.math.BigInteger;
import java.util.List;

import org.slf4j.Logger; import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.user.ExceptionReport;
import com.pay10.commons.user.User;
import com.pay10.commons.user.UserType;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.DataEncoder;
import com.pay10.commons.util.DateCreater;
import com.pay10.crm.mongoReports.ExceptionReportData;

public class ExceptionReportController extends AbstractSecureAction {
	
	private static final long serialVersionUID = 8626282226799143239L;
	private static Logger logger = LoggerFactory.getLogger(ExceptionReportController.class.getName());
	
	private String status;
	private String dateFrom;
	private String dateTo;
	private String merchant;
	private String acquirer;

	private BigInteger recordsTotal;
	private BigInteger recordsFiltered;
	private int draw; 
	private int length;
	private int start;

	private List<ExceptionReport> aaData;

	@Autowired
	private DataEncoder encoder;
	
	@Autowired
	private ExceptionReportData exceptionReportData;

	@Override
	public String execute() {

		int totalCount;
		User sessionUser = (User) sessionMap.get(Constants.USER.getValue());
		setDateFrom(DateCreater.toDateTimeformatCreater(dateFrom));
		setDateTo(DateCreater.formDateTimeformatCreater(dateTo));
		try {
			if (sessionUser.getUserType().equals(UserType.RESELLER)) {
				totalCount = exceptionReportData.getDataCount(getMerchant(), getAcquirer(), getStatus(),
						getDateFrom(), getDateTo(), ErrorType.MERCHANT_EXCEPTION.getResponseMessage());
				BigInteger bigInt = BigInteger.valueOf(totalCount);
				setRecordsTotal(bigInt);
				if (getLength() == -1) {
					setLength(getRecordsTotal().intValue());
				}
				aaData = encoder.encodeExceptionReportObj(exceptionReportData.getData(getMerchant(), getAcquirer(), getStatus(), getDateFrom(),
						getDateTo(), getStart(), getLength(), ErrorType.MERCHANT_EXCEPTION.getResponseMessage()));
				recordsFiltered = recordsTotal;

				return SUCCESS;
			} else {
				totalCount = exceptionReportData.getDataCount(getMerchant(), getAcquirer(), getStatus(),
						getDateFrom(), getDateTo(), ErrorType.MERCHANT_EXCEPTION.getResponseMessage());
				BigInteger bigInt = BigInteger.valueOf(totalCount);
				setRecordsTotal(bigInt);
				if (getLength() == -1) {
					setLength(getRecordsTotal().intValue());
				}
				aaData = encoder.encodeExceptionReportObj(exceptionReportData.getData(getMerchant(), getAcquirer(), getStatus(), getDateFrom(),
						getDateTo(), getStart(), getLength(),ErrorType.MERCHANT_EXCEPTION.getResponseMessage()));
				recordsFiltered = recordsTotal;
				return SUCCESS;
			}

		} catch (Exception exception) {
			logger.error("Exception", exception);
			return ERROR;
		}

	}

	public String bankException() {

		int totalCount;
		User sessionUser = (User) sessionMap.get(Constants.USER.getValue());
		setDateFrom(DateCreater.toDateTimeformatCreater(dateFrom));
		setDateTo(DateCreater.formDateTimeformatCreater(dateTo));
		try {
			if (sessionUser.getUserType().equals(UserType.RESELLER)) {
				totalCount = exceptionReportData.getDataCount(getMerchant(), getAcquirer(), getStatus(),
						getDateFrom(), getDateTo(), ErrorType.BANK_EXCEPTION.getResponseMessage());
				BigInteger bigInt = BigInteger.valueOf(totalCount);
				setRecordsTotal(bigInt);
				if (getLength() == -1) {
					setLength(getRecordsTotal().intValue());
				}
				aaData = encoder.encodeExceptionReportObj(exceptionReportData.getData(getMerchant(), getAcquirer(), getStatus(), getDateFrom(),
						getDateTo(), getStart(), getLength(), ErrorType.BANK_EXCEPTION.getResponseMessage()));
				recordsFiltered = recordsTotal;

				return SUCCESS;
			} else {
				totalCount = exceptionReportData.getDataCount(getMerchant(), getAcquirer(), getStatus(),
						getDateFrom(), getDateTo(), ErrorType.BANK_EXCEPTION.getResponseMessage());
				
					BigInteger bigInt = BigInteger.valueOf(totalCount);
					setRecordsTotal(bigInt);
					if (getLength() == -1) {
						setLength(getRecordsTotal().intValue());
					}
					/*if(totalCount == 0) {
						setLength(10);
					}*/
				
					/*if(totalCount == 0) {
						aaData = null;
					} else {*/
						aaData = encoder.encodeExceptionReportObj(exceptionReportData.getData(getMerchant(), getAcquirer(), getStatus(), getDateFrom(),
								getDateTo(), getStart(), getLength(), ErrorType.BANK_EXCEPTION.getResponseMessage()));
						recordsFiltered = recordsTotal;
					/*}*/
				
				return SUCCESS;
			}

		} catch (Exception exception) {
			logger.error("Exception", exception);
			return ERROR;
		}

	}
	
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
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

	public String getMerchant() {
		return merchant;
	}

	public void setMerchant(String merchant) {
		this.merchant = merchant;
	}

	public String getAcquirer() {
		return acquirer;
	}

	public void setAcquirer(String acquirer) {
		this.acquirer = acquirer;
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

	public List<ExceptionReport> getAaData() {
		return aaData;
	}

	public void setAaData(List<ExceptionReport> aaData) {
		this.aaData = aaData;
	}
	
	@Override
	public void validate() {
		logger.info("Inside validate");
	}
}
