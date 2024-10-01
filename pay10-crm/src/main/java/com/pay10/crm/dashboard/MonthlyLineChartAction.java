package com.pay10.crm.dashboard;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.pay10.commons.user.User;
import com.pay10.commons.user.UserDao;
import com.pay10.commons.user.UserType;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.CrmFieldConstants;
import com.pay10.crm.action.AbstractSecureAction;

public class MonthlyLineChartAction extends AbstractSecureAction {


	@Autowired
	private MonthlyLineChartService getlineChartService;
	
	@Autowired
	private UserDao userDao;

	private static Logger logger = LoggerFactory.getLogger(MonthlyLineChartAction.class.getName());
	private static final long serialVersionUID = 7154448717124485623L;
	private List<PieChart> pieChart = new ArrayList<PieChart>();
	private String emailId;
	private String currency;
	private String dateFrom;
	private String dateTo;
	private User sessionUser = new User();
	@Override
	public String execute() {
		
		sessionUser = (User) sessionMap.get(Constants.USER.getValue());
		
		try {

			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			Calendar c = Calendar.getInstance();
			c.setTime(sdf.parse(dateTo));
			c.add(Calendar.DAY_OF_MONTH, 1);
			dateTo = sdf.format(c.getTime());
			
			User user = (User) sessionMap.get(Constants.USER.getValue());
			if (user.getUserType().equals(UserType.RESELLER)) {

				if (getEmailId().equals(CrmFieldConstants.ALL_MERCHANTS.getValue())) {
					setPieChart(getlineChartService.preparelist(getEmailId(),
							getCurrency(), getDateFrom(), getDateTo(),sessionUser));
				} else {
					setPieChart(getlineChartService.preparelist(userDao.getPayIdByEmailId(getEmailId()),
							getCurrency(), getDateFrom(), getDateTo(),sessionUser));
				}
				return SUCCESS;
			} else if (user.getUserType().equals(UserType.ACQUIRER)) {
				// setPieChart(getlineChartService.linePreparelist(user.getFirstName(),
				// getCurrency(),getDateFrom(),getDateTo()));

				return SUCCESS;
			} else {
				if (user.getUserType().equals(UserType.MERCHANT) || user.getUserType().equals(UserType.POSMERCHANT)) {
					setPieChart(getlineChartService.preparelist(user.getPayId(), getCurrency(), getDateFrom(),
							getDateTo(),sessionUser));
				}else if(user.getUserType().equals(UserType.SUBUSER)) {
					setPieChart(getlineChartService.preparelist(user.getParentPayId(), getCurrency(), getDateFrom(),
							getDateTo(),sessionUser));
				}  else {
					if (getEmailId().equals(CrmFieldConstants.ALL_MERCHANTS.getValue())) {
						setPieChart(getlineChartService.preparelist(getEmailId(), getCurrency(), getDateFrom(),
								getDateTo(),sessionUser));
					} else {
						setPieChart(getlineChartService.preparelist(userDao.getPayIdByEmailId(getEmailId()),
								getCurrency(), getDateFrom(), getDateTo(),sessionUser));
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

	public MonthlyLineChartService getGetlineChartService() {
		return getlineChartService;
	}

	public void setGetlineChartService(MonthlyLineChartService getlineChartService) {
		this.getlineChartService = getlineChartService;
	}
	

}
