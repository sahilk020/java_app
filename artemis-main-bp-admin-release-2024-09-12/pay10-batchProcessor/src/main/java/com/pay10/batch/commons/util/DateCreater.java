package com.pay10.batch.commons.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pay10.commons.util.Constants;


public class DateCreater {
	private static Logger logger = LoggerFactory.getLogger(DateCreater.class.getName());
	public static DateTimeFormatter dateTimeFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
	
	public static String formatDateForReco(String date, String inputFormat){
		String recoDate = null;
		SimpleDateFormat inputDateFormat = new SimpleDateFormat(inputFormat);
		SimpleDateFormat outputDateFormat = new SimpleDateFormat(Constants.OUTPUT_DATE_FORMAT_DB.getValue());
		try {
			Date reco = inputDateFormat.parse(date);
			recoDate = outputDateFormat.format(reco);
		} catch (ParseException e) {
			logger.error("Exception", e);
		}
		return recoDate;
	}
	

	
	public static String addOneSecondInDate(String date){
		String capturedDate = null;
		DateFormat inputDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		SimpleDateFormat outputDateFormat = new SimpleDateFormat(Constants.OUTPUT_DATE_FORMAT_DB.getValue());
		try {
			Date dt = (Date)(inputDateFormat.parse(date));
			Calendar cal = Calendar.getInstance();
	        cal.setTime(dt);
	        cal.add(Calendar.SECOND, 1);
			capturedDate = outputDateFormat.format(cal.getTime());
		} catch (ParseException e) {
			logger.error("Exception", e);
		}
		return capturedDate;
	}
	
	public static String formatDateForDb(Date date){
		SimpleDateFormat outputDateFormat = new SimpleDateFormat(Constants.OUTPUT_DATE_FORMAT_DB.getValue());		
		return outputDateFormat.format(date);
	}
	
	public static String defaultCurrentDateTime() {
		SimpleDateFormat inputDateFormat = new SimpleDateFormat(Constants.OUTPUT_DATE_FORMAT_DB.getValue());
		Date currentDate = new Date();
		try {
			return inputDateFormat.format(currentDate);
		} catch (Exception exception) {
			logger.error("Exception", exception);
			return null;
		}
	}
	
	public static String dateformatCreater(String date){
		String dbFormatDateTime = null;
		StringBuilder stringBuilder = new StringBuilder();
		String year = date.substring(0,4);
		stringBuilder.append(year);
		stringBuilder.append("-");
		String month= date.substring(4, 6);
		stringBuilder.append(month);
		stringBuilder.append("-");
		String datec= date.substring(6,8);
		stringBuilder.append(datec);
		stringBuilder.append(" 00:00:00");
		//stringBuilder.append(Constants.OUTPUT_DATE_FORMAT_DB.getValue());
		
		dbFormatDateTime=stringBuilder.toString();
		
		return dbFormatDateTime;
	}
	
	public static Date formatStringToISODateTime(String date) {	
		date = date + ".000 UTC";
		DateFormat inputDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS ZZZ");
		try {			 
			 Date dt = inputDateFormat.parse(date);
			return dt;
		} catch (Exception exception) {
			logger.error("Exception", exception);
			return null;
		}		
	}
	
	public static String getCurrentDate(){
		try {
			
			Instant machineTimestamp = Instant.now();
			ZonedDateTime TimeinLA = machineTimestamp.atZone(ZoneId.of("Asia/Kolkata"));
			
			return TimeinLA.toLocalDateTime().toString();
			
		} catch(Exception exception) {
			logger.error("Exception", exception);
			return defaultCurrentDateTime();
		}
		
	}
	
	public static String formatDBRequestDate(String date) {		
		try {
			StringBuilder sbFormatDate = new StringBuilder();
			String[] parts = date.split(" ");
			String[] dateParts = parts[0].split("-");
			sbFormatDate.append(dateParts[0]);
			sbFormatDate.append(dateParts[1]);
			sbFormatDate.append(dateParts[2]);
			return sbFormatDate.toString();
		} catch (Exception exception) {
			logger.error("Exception", exception);
			return null;
		}		
	}
}