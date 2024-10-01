package com.pay10.crm.dashboard;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger; import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.pay10.commons.user.User;
import com.pay10.commons.user.UserDao;
import com.pay10.commons.user.UserType;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.CrmFieldConstants;
import com.pay10.crm.action.AbstractSecureAction;

/**
 * Rajendra
 */

public class LineChartAction extends AbstractSecureAction {

	@Autowired
	private LineChartService getlineChartService;

	@Autowired
	private UserDao userDao;

	private static Logger logger = LoggerFactory.getLogger(LineChartAction.class.getName());
	private static final long serialVersionUID = 7154448717124485623L;
	private List<PieChart> pieChart = new ArrayList<PieChart>();
	private String emailId;
	private String currency;
	private String dateFrom;
	private String dateTo;
	
	private String paymentType;
	private String acquirer;
	private String transactionType;
	private String mopType;
	
	private User sessionUser = new User();
	
	@Override
	public String execute() {
		
		sessionUser = (User) sessionMap.get(Constants.USER.getValue());
		
		if (StringUtils.isBlank(transactionType)) {
			transactionType = "ALL";
		}
		
		try {
			Calendar date = Calendar.getInstance();
			date.set(Calendar.DAY_OF_MONTH, 1);
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
			dateFrom = df.format(date.getTime());
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.DAY_OF_MONTH, 1);
			Date date2 = cal.getTime();
			dateTo = df.format(date2);

			User user = (User) sessionMap.get(Constants.USER.getValue());
			if (user.getUserType().equals(UserType.RESELLER)) {

				if (getEmailId().equals(CrmFieldConstants.ALL_MERCHANTS.getValue())) {
					setPieChart(getlineChartService.preparelist(getEmailId(),
							getCurrency(), getDateFrom(), getDateTo(),sessionUser,paymentType,acquirer,transactionType,mopType));
				} else {
					setPieChart(getlineChartService.preparelist(userDao.getPayIdByEmailId(getEmailId()),
							getCurrency(), getDateFrom(), getDateTo(),sessionUser,paymentType,acquirer,transactionType,mopType));
				}
				return SUCCESS;
			} else if (user.getUserType().equals(UserType.ACQUIRER)) {
				// setPieChart(getlineChartService.linePreparelist(user.getFirstName(),
				// getCurrency(),getDateFrom(),getDateTo()));

				return SUCCESS;
			} else {
				if (user.getUserType().equals(UserType.MERCHANT) || user.getUserType().equals(UserType.POSMERCHANT)) {
					setPieChart(getlineChartService.preparelist(user.getPayId(), getCurrency(), getDateFrom(),
							getDateTo(),sessionUser,paymentType,acquirer,transactionType,mopType));
				}else if(user.getUserType().equals(UserType.SUBUSER)) {
					setPieChart(getlineChartService.preparelist(user.getParentPayId(), getCurrency(), getDateFrom(),
							getDateTo(),sessionUser,paymentType,acquirer,transactionType,mopType));
				} else {
					if (getEmailId().equals(CrmFieldConstants.ALL_MERCHANTS.getValue())) {
						setPieChart(getlineChartService.preparelist(getEmailId(), getCurrency(), getDateFrom(),
								getDateTo(),sessionUser,paymentType,acquirer,transactionType,mopType));
					} else {
						setPieChart(getlineChartService.preparelist(userDao.getPayIdByEmailId(getEmailId()),
								getCurrency(), getDateFrom(), getDateTo(),sessionUser,paymentType,acquirer,transactionType,mopType));
					}
				}
				return SUCCESS;
			}
		} catch (Exception exception) {
			logger.error("Exception", exception);
			return ERROR;
		}

	}

	public List<PieChart> getPieChart() {
		return pieChart;
	}

	public void setPieChart(List<PieChart> pieChart) {
		this.pieChart = pieChart;
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

	public LineChartService getGetlineChartService() {
		return getlineChartService;
	}

	public void setGetlineChartService(LineChartService getlineChartService) {
		this.getlineChartService = getlineChartService;
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

	public String getMopType() {
		return mopType;
	}

	public void setMopType(String mopType) {
		this.mopType = mopType;
	}
	
	
	public String getAcquirer() {
		return acquirer;
	}

	public void setAcquirer(String acquirer) {
		this.acquirer = acquirer;
	}
	
}
