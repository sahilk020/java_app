package com.pay10.crm.dashboard;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.exception.SystemException;
import com.pay10.commons.user.User;

@Service
public class LineChartService {
	@Autowired
	private BarChartQuery barChartQuery;
	private static Logger logger = LoggerFactory.getLogger(LineChartService.class.getName());

	public LineChartService() {
	}

	public List<PieChart> preparelist(String payId, String currency, String dateFrom, String dateTo, User user,String paymentType,String acquirer,String transactionType,String mopType)
			throws SystemException, ParseException {
		try {
			List<PieChart> finalPiechartList = new ArrayList<PieChart>();
			Map<String, PieChart> pieChartsMap = new HashMap<String, PieChart>();

			pieChartsMap = barChartQuery.totalTransactionRecord(payId, currency, dateFrom, dateTo,user,paymentType,acquirer,transactionType,mopType);

			List<String> dummyList = prepareDateList(dateFrom, dateTo);
			boolean flag = false;

			for (String date : dummyList) {

				for (String key : pieChartsMap.keySet()) {
					flag = false;
					
					if (!(StringUtils.isBlank(key))) {
						if (key.equals(date)) {
							PieChart pichat = pieChartsMap.get(key);
							if(pichat.getTxndate() == null) {
								pichat.setTxndate(date);
								pieChartsMap.put(key, pichat);
							} 							
							finalPiechartList.add(pieChartsMap.get(key));
							flag = true;
							break;
						}
					} else {
						PieChart pieChart = new PieChart();
						pieChart.setTotalSuccess("0");
						pieChart.setTotalFailed("0");
						pieChart.setTotalPending("0");
						pieChart.setTotalRefunded("0");
						pieChart.setTotalErrors("0");
						pieChart.setTotalTimeouts("0");
						pieChart.setTotalCancelled("0");
						pieChart.setTxndate((date));
						finalPiechartList.add(pieChart);
					}

				}
				if (!flag) {
					PieChart chart = new PieChart();
					chart.setTotalSuccess("0");
					chart.setTotalFailed("0");
					chart.setTotalPending("0");
					chart.setTotalRefunded("0");
					chart.setTotalErrors("0");
					chart.setTotalTimeouts("0");
					chart.setTotalCancelled("0");
					chart.setTxndate((date));
					finalPiechartList.add(chart);
				}
			}

			return finalPiechartList;
		} catch (Exception exception) {
			String message = "Error while total Transaction Record in database";
			logger.error(message, exception);
			throw new SystemException(ErrorType.DATABASE_ERROR, exception, message);
		}
	}

	public List<String> prepareDateList(String dateFrom, String dateTo) throws ParseException {

		List<String> dates = new ArrayList<String>();

		Calendar calendar = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");

		Date start = sdf.parse(dateFrom);
		Date end = sdf.parse(dateTo);
		calendar.setTime(start);
		while (calendar.getTime().before(end)) {
			Date result = calendar.getTime();
			String dateString = sdf1.format(result);
			dates.add(dateString);
			calendar.add(Calendar.DAY_OF_MONTH, 1);
		}
		return dates;
	}
}