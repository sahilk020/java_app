package com.pay10.crm.action;


import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.pay10.commons.dto.DashboardReportCount;
import com.pay10.commons.util.DataEncoder;

import com.pay10.crm.mongoReports.DashboardService;

public class GetExceptionCount extends AbstractSecureAction {

	private static final long serialVersionUID = -3306411281051464691L;
	private static Logger logger = LoggerFactory.getLogger(GetExceptionCount.class.getName());
	
	private DashboardReportCount count;
	
	private String date;
	
	@Autowired
	private DataEncoder encoder;
	
	@Autowired
	private DashboardService dashboardService;
	
	@Override
	public String execute() {
		String sDate="";
		try {
			
				sDate = new SimpleDateFormat("yyyy-MM-dd").format(new SimpleDateFormat("yyyy-MM-dd").parse(date));
			
			
		} catch (ParseException e) {
			logger.error("Parse Date Exception in getExceptionCount()",e);
			e.printStackTrace();
		}
		String eDate=sDate+" 23:59:59";
		sDate=sDate+" 00:00:00";
		
		logger.info("Start Date : "+sDate+"\t End Date : "+eDate);
		count=encoder.encodeDashboardData(dashboardService.getCountException(sDate,eDate));
		return SUCCESS;
	}
		
	public DashboardReportCount getCount() {
		return count;
	}

	public void setCount(DashboardReportCount count) {
		this.count = count;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	
	
	
	
}
