package com.pay10.crm.action;


import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.pay10.commons.dto.DashboardReportCount;
import com.pay10.commons.util.DataEncoder;

import com.pay10.crm.mongoReports.DashboardService;

public class GetDashoard extends AbstractSecureAction {

	private static final long serialVersionUID = -3306411281051464691L;
	private static Logger logger = LoggerFactory.getLogger(GetDashoard.class.getName());
	
	private DashboardReportCount count;
	

	@Autowired
	private DataEncoder encoder;
	
	@Autowired
	private DashboardService dashboardService;
	
	@Override
	public String execute() {
		count=encoder.encodeDashboardData(dashboardService.getDashboardReportCount());
		return SUCCESS;
	}
	
	public DashboardReportCount getCount() {
		return count;
	}

	public void setCount(DashboardReportCount count) {
		this.count = count;
	}


	
	
	
	
}
