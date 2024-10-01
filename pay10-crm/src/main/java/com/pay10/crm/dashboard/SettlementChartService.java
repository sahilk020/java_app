package com.pay10.crm.dashboard;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pay10.commons.exception.SystemException;
import com.pay10.commons.user.User;
import com.pay10.commons.util.DateCreater;

@Service
public class SettlementChartService {
	@Autowired
	private SettlementChartQuery settlementChartQuery;
	private static Logger logger = LoggerFactory.getLogger(LineChartService.class.getName());

	public List<Object> preparelist(String payId, String currency, String dateFrom, String dateTo,String paymentType,String acquirer,String transactionType,String mopType,User user)
			throws SystemException, ParseException {

		List<SettlementChart> settlementList = new ArrayList<SettlementChart>();
		List<String> dateList = new ArrayList<String>();
		List<Double> amountList = new ArrayList<Double>();
		List<Object> finalSettlementChartList = new ArrayList<Object>();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Calendar c = Calendar.getInstance();
		try {
			c.setTime(sdf.parse(dateTo));
		} catch (ParseException e) {
			e.printStackTrace();
		}

		c.add(Calendar.DAY_OF_MONTH, 1);
		String newDate = sdf.format(c.getTime());
		settlementList = settlementChartQuery.getSettledValues(payId, currency, dateFrom, newDate,paymentType,acquirer,transactionType,mopType,user);

		String dateFroms = dateFrom;
		String dateTos = dateTo;
		String[] splitdateFrom = dateFroms.split("-");
		String[] splitdateTo = dateTos.split("-");
		String strDateFrom = splitdateFrom[2] + "-" + splitdateFrom[1] + "-" + splitdateFrom[0];
		String strDatesTo = splitdateTo[2] + "-" + splitdateTo[1] + "-" + splitdateTo[0];
		int yrFrom = Integer.parseInt(splitdateFrom[0]);
		int yrTo = Integer.parseInt(splitdateTo[0]);

		Long Datediff = DateCreater.diffDate(strDateFrom, strDatesTo);

		if (Datediff > 30 && yrFrom < yrTo) {
			String diffYrdateFrom = splitdateFrom[1] + "/" + splitdateFrom[2] + "/" + splitdateFrom[0];
			String diffYrdateTo = splitdateTo[1] + "/" + splitdateTo[2] + "/" + splitdateTo[0];
			List<String> dummyList = prepareTwoDiffYrsMonthList(diffYrdateFrom, diffYrdateTo);
			int listCountFinal = dummyList.size();
			for (int k = 0; k < listCountFinal; k++) {
				dateList.add(k, dummyList.get(k));
				amountList.add(k, 0.00);
			}

			for (SettlementChart i : settlementList) {
				String createDate = i.getCreateDate();
				Double amount = i.getSettledAmount();
				String[] splitdate = createDate.split("-");
				int monthNum = Integer.parseInt(splitdate[1]) - 1;
				if (monthNum >= 0) {

					String[] monthNames = { "January", "February", "March", "April", "May", "June", "July", "August",
							"September", "October", "November", "December" };

					if (listCountFinal != 0) {
						for (int j = 0; j < listCountFinal; j++) {
							if (dateList.get(j).equals(monthNames[monthNum] + "-" + splitdate[0])) {
								dateList.set(j, dummyList.get(j));
								Double amt = amountList.get(j) + amount;
//								amount = (double) Math.round(amt);
								amountList.set(j, amt);
								break;
							}
						}
					}
				}
			}
			finalSettlementChartList.add(dateList);
			finalSettlementChartList.add(amountList);
			return finalSettlementChartList;

		}

		else if (Datediff > 30 && yrFrom == yrTo) {
			List<String> dummyList = prepareMonthList(dateFrom, dateTo);
			int listCountFinal = dummyList.size();
			for (int k = 0; k < listCountFinal; k++) {
				dateList.add(k, dummyList.get(k));
				amountList.add(k, 0.00);
			}

			for (SettlementChart i : settlementList) {
				String createDate = i.getCreateDate();
				Double amount = i.getSettledAmount();
				String[] splitdate = createDate.split("-");
				int monthNum = Integer.parseInt(splitdate[1]) - 1;
				if (monthNum >= 0) {

					String[] monthNames = { "January", "February", "March", "April", "May", "June", "July", "August",
							"September", "October", "November", "December" };

					if (listCountFinal != 0) {
						for (int j = 0; j < listCountFinal; j++) {
							if (dateList.get(j).equals(monthNames[monthNum])) {
								dateList.set(j, dummyList.get(j));
								Double amt = amountList.get(j) + amount;
//								amount = (double) Math.round(amt);
								amountList.set(j, amt);
								break;
							}
						}
					}
				}
			}
			finalSettlementChartList.add(dateList);
			finalSettlementChartList.add(amountList);
			return finalSettlementChartList;

		} else {
			List<String> dummyList = prepareDateList(dateFrom, dateTo);
			int dummyListCount = dummyList.size();
			for (int k = 0; k < dummyListCount; k++) {
				dateList.add(k, dummyList.get(k));
				amountList.add(k, 0.00);
			}

			for (SettlementChart i : settlementList) {

				String createDate = i.getCreateDate();
				Double amount = i.getSettledAmount();
				String[] splitdate = createDate.split(" ");
				String inputDate = splitdate[0];
				String[] splitdates = inputDate.split("-");
				String inputDates = splitdates[2]+"-"+splitdates[1]+"-"+splitdates[0];

				try {
					int ListCountFinal = dateList.size();
					int insertCount = 0;
					if (ListCountFinal != 0) {
						for (int j = 0; j < ListCountFinal; j++) {
							if (dateList.get(j).equals(inputDates)) {
								dateList.set(j, inputDates);
								double amt = amountList.get(j) + amount;
								//amount = (double) Math.round(amt);
								amountList.set(j, amt);
								insertCount = ListCountFinal;
								break;
							}
						}
						if (insertCount == 0) {
							dateList.add(inputDates);
							amountList.add(amount);
						}
					} else {
						dateList.add(inputDates);
						amountList.add(amount);
					}
				} catch (Exception exception) {
					logger.error("Exception", exception);
				}
			}
			finalSettlementChartList.add(dateList);
			finalSettlementChartList.add(amountList);
			return finalSettlementChartList;
		}
	}

	public List<String> prepareDateList(String dateFroms, String dateTos) throws ParseException {
		
		String[] splitdate = dateFroms.split("-");
		String dateFrom = splitdate[2]+"-"+splitdate[1]+"-"+splitdate[0];
		
		String[] splitdates = dateTos.split("-");
		String dateTo = splitdates[2]+"-"+splitdates[1]+"-"+splitdates[0];
		
		List<Date> dates = new ArrayList<Date>();
		List<String> month = new ArrayList<String>();
		 
		DateFormat formatter;

		formatter = new SimpleDateFormat("dd-MM-yyyy");
		Date startDate = (Date) formatter.parse(dateFrom);
		Date endDate = (Date) formatter.parse(dateTo);
		long interval = 24 * 1000 * 60 * 60; 
		long endTime = endDate.getTime(); 
		long curTime = startDate.getTime();
		while (curTime <= endTime) {
			dates.add(new Date(curTime));
			curTime += interval;
		}
		for (int i = 0; i < dates.size(); i++) {
			Date lDate = (Date) dates.get(i);
			String ds = formatter.format(lDate);
			month.add(ds);  
		}

		return month;
	
	}

	public List<String> prepareMonthList(String dateFrom, String dateTo) throws ParseException {

		List<String> month = new ArrayList<String>();
		String[] splitFromdate = dateFrom.split("-");
		String[] splitTodate = dateTo.split("-");
		int fromDateMonth = Integer.parseInt(splitFromdate[1]);
		int toDateMonth = Integer.parseInt(splitTodate[1]);

		if (fromDateMonth > toDateMonth) {

		}
		String[] monthNames = { "January", "February", "March", "April", "May", "June", "July", "August", "September",
				"October", "November", "December" };

		for (int k = fromDateMonth - 1; k < toDateMonth; k++) {
			month.add(monthNames[k]);
		}
		return month;
	}

	public List<String> prepareYearList(String dateFrom, String dateTo) throws ParseException {

		List<String> month = new ArrayList<String>();
		String[] splitFromdate = dateFrom.split("-");
		String[] splitTodate = dateTo.split("-");
		int fromDateMonth = Integer.parseInt(splitFromdate[1]);
		int toDateMonth = Integer.parseInt(splitTodate[1]);

		String[] monthNames = { "January", "February", "March", "April", "May", "June", "July", "August", "September",
				"October", "November", "December" };

		for (int k = fromDateMonth - 1; k < toDateMonth; k++) {
			month.add(monthNames[k]);
		}
		return month;
	}

	public List<String> prepareTwoDiffYrsMonthList(String dateFrom, String dateTo) throws ParseException {
		List<String> month = new ArrayList<String>();
		final String OLD_FORMAT = "MM/dd/yyyy";
		final String NEW_FORMAT = "MMMM-YYYY";
		SimpleDateFormat newFormat = new SimpleDateFormat(OLD_FORMAT);
		DateFormat formatter = new SimpleDateFormat(NEW_FORMAT);
		Date fromDate = newFormat.parse(dateFrom);
		Date toDate = newFormat.parse(dateTo);

		Calendar beginCalendar = Calendar.getInstance();
		Calendar finishCalendar = Calendar.getInstance();

		beginCalendar.setTimeInMillis(fromDate.getTime());
		finishCalendar.setTimeInMillis(toDate.getTime());

		String date;
		while (beginCalendar.before(finishCalendar)) {
			date = formatter.format(beginCalendar.getTime());
			month.add(date);
			beginCalendar.add(Calendar.MONTH, 1);
		}
		return month;

		 }
		}
