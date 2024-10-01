package com.pay10.crm.dashboard;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;

import org.slf4j.Logger; import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pay10.commons.exception.SystemException;
import com.pay10.commons.util.MopType;
import com.pay10.commons.util.PaymentType;

@Component
public class PieChartService {
	@Autowired
	private BarChartQuery barChartQuery;

	private PieChart pieChart = new PieChart();
	
	private static Logger logger = LoggerFactory.getLogger(PieChartService.class
			.getName());

	public PieChartService() {

	}


	public PieChart getDashboardValues(String payId, String currency,
			String dateFrom, String dateTo) throws SystemException,
			ParseException {
		DateFormat df = new SimpleDateFormat("EEE MMM dd yyyy HH:mm:ss");
		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
		String startDate = sdf1.format(df.parse(dateFrom));
		String endDate = sdf1.format(df.parse(dateTo));
		
		HashMap<String,String> chartvalue=	barChartQuery.chartTotalSummary(payId, currency, startDate, endDate);
		pieChart.setVisa(chartvalue.get(MopType.VISA.getName()));
		pieChart.setMastercard(chartvalue.get(MopType.MASTERCARD.getName()));
		pieChart.setNet(chartvalue.get(PaymentType.NET_BANKING.getName()));
		pieChart.setAmex(chartvalue.get(MopType.AMEX.getName()));
		pieChart.setMaestro(chartvalue.get(MopType.MAESTRO.getName()));
		pieChart.setEzeeClick(chartvalue.get(MopType.EZEECLICK.getName()));
		pieChart.setOther(chartvalue.get("Other"));
		

		return pieChart;
	}



}
