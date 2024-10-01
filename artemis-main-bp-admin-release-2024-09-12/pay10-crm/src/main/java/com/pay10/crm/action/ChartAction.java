package com.pay10.crm.action;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.ServletActionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.pay10.commons.user.Merchants;
import com.pay10.commons.user.TDRBifurcationReportDetails;
import com.pay10.commons.user.User;
import com.pay10.commons.user.UserDao;
import com.pay10.commons.util.Constants;
import com.pay10.crm.actionBeans.TransactionStatusBean;
import com.pay10.crm.mongoReports.MerchantDashboardService;
import com.pay10.crm.mongoReports.TransactionStatus;

public class ChartAction extends AbstractSecureAction {
	private static final long serialVersionUID = -1806801325621922073L;
	private static Logger logger = LoggerFactory.getLogger(ChartAction.class.getName());
	List<String> statuss = new ArrayList<>();
	
	@Autowired
	private UserDao userDao;
	@Autowired
	HttpServletRequest request = ServletActionContext.getRequest();

	HttpServletResponse response = ServletActionContext.getResponse();


	public String execute() {
		return SUCCESS;
	}

	public List<String> getStatuss() {
		return statuss;
	}

	public void setStatuss(List<String> statuss) {
		this.statuss = statuss;
	}

	private String dateRange;
	private String status;

	public String getDateRange() {
		return dateRange;
	}

	public void setDateRange(String dateRange) {
		this.dateRange = dateRange;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	
}
