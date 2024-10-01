package com.pay10.crm.dashboard;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.user.User;
import com.pay10.commons.user.UserDao;
import com.pay10.commons.user.UserType;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.CrmFieldConstants;
import com.pay10.commons.util.CrmFieldType;
import com.pay10.commons.util.CrmValidator;
import com.pay10.crm.action.AbstractSecureAction;

public class StatisticsSettledAmountAction  extends AbstractSecureAction  {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2179649888987858770L;

	@Autowired
	StatisticsService getStatistics;

	@Autowired
	private CrmValidator validator;
	@Autowired
	BarChartQuery barChartQuery;
	@Autowired
	private UserDao userDao;

	private static Logger logger = LoggerFactory.getLogger(StatisticsSettledAmountAction.class.getName());

	private Statistics statistics = new Statistics();
	private String emailId;
	private String currency;
	private String dateFrom;
	private String dateTo;

	@Override
	public String execute() {
		try {
			
			DateFormat df = new SimpleDateFormat("EEE MMM dd yyyy HH:mm:ss");
			SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
			String startDate = sdf1.format(df.parse(dateFrom));
			String endDate = sdf1.format(df.parse(dateTo));
			User user = (User) sessionMap.get(Constants.USER.getValue());
			UserType userType = user.getUserType();
			if (user.getUserType().equals(UserType.RESELLER)) {
				if (getEmailId().equals(CrmFieldConstants.ALL_MERCHANTS.getValue())) {
					setStatistics(barChartQuery.statisticsSettledAmountSummary(getEmailId(), getCurrency(),startDate,endDate,userType,user));
				} else {
					User emailUser = userDao.findPayIdByEmail(getEmailId());
				
					setStatistics(barChartQuery.statisticsSettledAmountSummary(emailUser.getPayId(), getCurrency(),startDate,endDate,userType,user));
				}
				return SUCCESS;

			}
			else if(user.getUserType().equals(UserType.SUBADMIN)) {
				if (getEmailId().equals(CrmFieldConstants.ALL_MERCHANTS.getValue())) {
					setStatistics(barChartQuery.statisticsSettledAmountSummary(getEmailId(), getCurrency(),startDate,endDate,userType,user));
				} else {
					User emailUser = userDao.findPayIdByEmail(getEmailId());
				
					setStatistics(barChartQuery.statisticsSettledAmountSummary(emailUser.getPayId(), getCurrency(),startDate,endDate,userType,user));
				}
				return SUCCESS;
			}
			else {
				System.out.println("getting normal query.");
				if (user.getUserType().equals(UserType.MERCHANT) || user.getUserType().equals(UserType.SUBUSER)) {
					setStatistics(barChartQuery.statisticsSettledAmountSummary(user.getPayId(), getCurrency(),startDate,endDate,userType,user));
				} else {
					if (getEmailId().equals(CrmFieldConstants.ALL_MERCHANTS.getValue())) {
						setStatistics(barChartQuery.statisticsSettledAmountSummary(getEmailId(), getCurrency(),startDate,endDate,userType,user));
					} else {
						User emailUser = userDao.findPayIdByEmail(getEmailId());
					
						setStatistics(barChartQuery.statisticsSettledAmountSummary(emailUser.getPayId(), getCurrency(),startDate,endDate,userType,user));
					}
				}
				return SUCCESS;
			}
		} catch (Exception exception) {
			logger.error("Exception", exception);
			return ERROR;
		}

	}

	
	public String capture() {
		try {
			if (dateFrom == null) {
				return NONE;
			}
			DateFormat df = new SimpleDateFormat("EEE MMM dd yyyy HH:mm:ss");
			SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
			String startDate = sdf1.format(df.parse(dateFrom));
			String endDate = sdf1.format(df.parse(dateTo));
			User user = (User) sessionMap.get(Constants.USER.getValue());
			UserType userType = user.getUserType();
			if (user.getUserType().equals(UserType.RESELLER)) {
				if (getEmailId().equals(CrmFieldConstants.ALL_MERCHANTS.getValue())) {
					setStatistics(barChartQuery.statisticsSummaryCapture(getEmailId(), getCurrency(),startDate,endDate,userType,user));
				} else {
					User emailUser = userDao.findPayIdByEmail(getEmailId());
				
					setStatistics(barChartQuery.statisticsSummaryCapture(emailUser.getPayId(), getCurrency(),startDate,endDate,userType,user));
				}
				return SUCCESS;

			} else {
				System.out.println("getting capture query.");
				if (user.getUserType().equals(UserType.MERCHANT) || user.getUserType().equals(UserType.POSMERCHANT)) {
					setStatistics(barChartQuery.statisticsSummaryCapture(user.getPayId(), getCurrency(),startDate,endDate,userType,user));
				} else {
					if (getEmailId().equals(CrmFieldConstants.ALL_MERCHANTS.getValue())) {
						setStatistics(barChartQuery.statisticsSummaryCapture(getEmailId(), getCurrency(),startDate,endDate,userType,user));
					} else {
						User emailUser = userDao.findPayIdByEmail(getEmailId());
					
						setStatistics(barChartQuery.statisticsSummaryCapture(emailUser.getPayId(), getCurrency(),startDate,endDate,userType,user));
					}
				}
				return SUCCESS;
			}
		} catch (Exception exception) {
			logger.error("Exception", exception);
			return ERROR;
		}

	}
	
	
	public String refund() {
		try {
			
			DateFormat df = new SimpleDateFormat("EEE MMM dd yyyy HH:mm:ss");
			SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
			String startDate = sdf1.format(df.parse(dateFrom));
			String endDate = sdf1.format(df.parse(dateTo));
			User user = (User) sessionMap.get(Constants.USER.getValue());
			UserType userType = user.getUserType();
			if (user.getUserType().equals(UserType.RESELLER)) {
				if (getEmailId().equals(CrmFieldConstants.ALL_MERCHANTS.getValue())) {
					setStatistics(barChartQuery.statisticsSummaryRefund(getEmailId(), getCurrency(),startDate,endDate,userType,user));
				} else {
					User emailUser = userDao.findPayIdByEmail(getEmailId());
				
					setStatistics(barChartQuery.statisticsSummaryRefund(emailUser.getPayId(), getCurrency(),startDate,endDate,userType,user));
				}
				return SUCCESS;

			} else {
				System.out.println("getting refund query.");
				if (user.getUserType().equals(UserType.MERCHANT) || user.getUserType().equals(UserType.POSMERCHANT)) {
					setStatistics(barChartQuery.statisticsSummaryRefund(user.getPayId(), getCurrency(),startDate,endDate,userType,user));
				} else {
					if (getEmailId().equals(CrmFieldConstants.ALL_MERCHANTS.getValue())) {
						setStatistics(barChartQuery.statisticsSummaryRefund(getEmailId(), getCurrency(),startDate,endDate,userType,user));
					} else {
						User emailUser = userDao.findPayIdByEmail(getEmailId());
					
						setStatistics(barChartQuery.statisticsSummaryRefund(emailUser.getPayId(), getCurrency(),startDate,endDate,userType,user));
					}
				}
				return SUCCESS;
			}
		} catch (Exception exception) {
			logger.error("Exception", exception);
			return ERROR;
		}

	}
	
	
	@Override
	public void validate() {

		if (validator.validateBlankField(getEmailId())) {
		} else if (getEmailId().equals(CrmFieldConstants.ALL_MERCHANTS.getValue())) {
		} else if (!validator.validateField(CrmFieldType.EMAILID, getEmailId())) {
			addFieldError(CrmFieldType.EMAILID.getName(), ErrorType.INVALID_FIELD.getResponseMessage());
		}

		if (validator.validateBlankField(getCurrency())) {
		} else if (!validator.validateField(CrmFieldType.CURRENCY, getCurrency())) {
			addFieldError(CrmFieldType.CURRENCY.getName(), ErrorType.INVALID_FIELD.getResponseMessage());
		}
	}

	public Statistics getStatistics() {
		return statistics;
	}

	public void setStatistics(Statistics statistics) {
		this.statistics = statistics;
	}

	public String getEmailId() {
		return emailId;
	}

	public void setEmailId(String emailId) {
		this.emailId = emailId;
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
	

}
