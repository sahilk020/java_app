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
public class HourlyLineChartService {

	@Autowired
	private BarChartQuery barChartQuery;
	private static Logger logger = LoggerFactory.getLogger(HourlyLineChartService.class.getName());

	public HourlyLineChartService() {
	}

	public List<PieChart> preparelist(String payId, String currency, String dateFrom, String dateTo, User user,
			String paymentType, String acquirer, String transactionType, String mopType)
			throws SystemException, ParseException {
		try {
			List<PieChart> finalPiechartList = new ArrayList<PieChart>();
			Map<String, PieChart> pieChartsMap = new HashMap<String, PieChart>();

			pieChartsMap = barChartQuery.totalHourlyTransactionRecord(payId, currency, dateFrom, dateTo, user,
					paymentType, acquirer, transactionType, mopType);

			List<String> dummyList = prepareDateList(dateFrom, dateTo);
			boolean flag = false;

			for (String date : dummyList) {

				for (String key : pieChartsMap.keySet()) {
					flag = false;

					if (!(StringUtils.isBlank(key))) {
						if (key.equals(date)) {
							PieChart pichat = pieChartsMap.get(key);
							if (pichat.getTxndate() == null) {
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
						pieChart.setTotalRefunded("0");
						pieChart.setTotalCancelled("0");
						pieChart.setTxndate((date));
						finalPiechartList.add(pieChart);
					}

				}
				if (!flag) {
					PieChart chart = new PieChart();
					chart.setTotalSuccess("0");
					chart.setTotalFailed("0");
					chart.setTotalRefunded("0");
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

	// TODO here below method I need to prepare the hours list
	public List<String> prepareDateList(String dateFrom, String dateTo) throws ParseException {

		List<String> hours = new ArrayList<String>();

		Calendar calendar = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		SimpleDateFormat sdf1 = new SimpleDateFormat("HH");

		Date start = sdf.parse(dateFrom);
		Date end = sdf.parse(dateTo);
		calendar.setTime(start);
		while (calendar.getTime().before(end)) {
			Date result = calendar.getTime();
			String dateString = sdf1.format(result);
			hours.add(dateString);
			calendar.add(Calendar.HOUR_OF_DAY, 1);
		}
		return hours;
	}

}
