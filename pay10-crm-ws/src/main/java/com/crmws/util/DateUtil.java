package com.crmws.util;

import java.text.SimpleDateFormat;
import java.util.Date;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DateUtil {

	public static String[] getDateBetween(String dateRange) {
		// 07/06/2022 - 06/07/2022
		String[] date = new String[2];
		String splitdate[] = dateRange.split(" - ");
		try {
			SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
			SimpleDateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd");
			date[0] = dateFormat1.format(dateFormat.parse(splitdate[0]));
			date[1] = dateFormat1.format(dateFormat.parse(splitdate[1]));

		} catch (Exception e) {
			log.info("Exception Occur in class getting Date Range: " + e);
			e.printStackTrace();
		}
		return date;
	}
	
	public static String getCurrentTime(String pattern) {
		return new SimpleDateFormat(pattern).format(new Date());
	}
	

}
