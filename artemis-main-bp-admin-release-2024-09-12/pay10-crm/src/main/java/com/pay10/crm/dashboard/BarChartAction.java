package com.pay10.crm.dashboard;

import org.slf4j.Logger; import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.pay10.commons.action.AbstractSecureAction;
import com.pay10.commons.user.User;
import com.pay10.commons.user.UserDao;
import com.pay10.commons.user.UserType;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.CrmFieldConstants;

public class BarChartAction extends AbstractSecureAction {


	/**
	 * 
	 */
	private static final long serialVersionUID = -301362817589538298L;

	@Autowired
	private BarChartService getBarChartService;

	@Autowired
	private UserDao userDao;

	private static Logger logger = LoggerFactory.getLogger(BarChartAction.class.getName());
	
	private PieChart pieChart = new PieChart();
	private String emailId;
	private String currency;
	private String dateFrom;
	private String dateTo;

	@Override
	public String execute() {
		try {

			User user = (User) sessionMap.get(Constants.USER.getValue());
			if (user.getUserType().equals(UserType.RESELLER)) {
				// setPieChart(getBarChartService.prepareResellerlist(user.getPayId(),user.getResellerId(),getCurrency(),getDateFrom(),getDateTo()));

				if (getEmailId().equals(CrmFieldConstants.ALL_MERCHANTS.getValue())) {
					// setPieChart(getBarChartService.prepareResellerlist(getEmailId(),user.getResellerId(),getCurrency(),getDateFrom(),getDateTo()));

				} else {
					// setPieChart(getBarChartService.getDashboardValues(userDao.findPayIdByEmail(getEmailId()).getPayId(),getCurrency(),getDateFrom(),getDateTo()));
				}
				return SUCCESS;
			} else if (user.getUserType().equals(UserType.ACQUIRER)) {
				// setPieChart(getBarChartService.getAcquirerBarChartDashboardValues(user.getFirstName(),
				// getCurrency(), getDateFrom(),getDateTo()));

				return SUCCESS;
			} else {
				if (user.getUserType().equals(UserType.MERCHANT) || user.getUserType().equals(UserType.POSMERCHANT)) {
					setPieChart(getBarChartService.getDashboardValues(user.getPayId(), getCurrency(), getDateFrom(),
							getDateTo()));
				} else {
					if (getEmailId().equals(CrmFieldConstants.ALL_MERCHANTS.getValue())) {
						setPieChart(getBarChartService.getDashboardValues(getEmailId(), getCurrency(), getDateFrom(),
								getDateTo()));
					} else {
						User payId = userDao.findPayIdByEmail(getEmailId());
						setPieChart(
								getBarChartService.getDashboardValues(payId.getPayId(),
										getCurrency(), getDateFrom(), getDateTo()));

					}
				}
				return SUCCESS;
			}
		} catch (Exception exception) {
			logger.error("Exception", exception);
			return ERROR;
		}

	}

	public PieChart getPieChart() {
		return pieChart;
	}

	public void setPieChart(PieChart pieChart) {
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

}