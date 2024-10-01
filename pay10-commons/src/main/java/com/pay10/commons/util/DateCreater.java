package com.pay10.commons.util;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.WeekFields;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DateCreater {
	private static Logger logger = LoggerFactory.getLogger(DateCreater.class.getName());
	public static DateTimeFormatter dateTimeFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
	public static DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");

	public static String defaultFromDate() {
		SimpleDateFormat inputDateFormat = new SimpleDateFormat(CrmFieldConstants.INPUT_DATE_FORMAT.getValue());
		Date currentDate = new Date();
		try {
			return inputDateFormat.format(currentDate);
		} catch (Exception exception) {
			logger.error("Exception", exception);
			return null;
		}
	}

	public static String defaultToDate() {
		SimpleDateFormat inputDateFormat = new SimpleDateFormat(CrmFieldConstants.INPUT_DATE_FORMAT.getValue());
		Calendar cal = Calendar.getInstance();
		Date currentDate = new Date();
		try {
			cal.setTime(currentDate);
			cal.add(Calendar.DAY_OF_MONTH, -30);
			return inputDateFormat.format(cal.getTime());
		} catch (Exception exception) {
			logger.error("Exception", exception);
			return null;
		}
	}

	public static String formatFromDate(String dateFrom) {
		SimpleDateFormat inputDateFormat = new SimpleDateFormat(CrmFieldConstants.INPUT_DATE_FORMAT.getValue()); // get
																													// date
																													// format
																													// as
																													// is
																													// from
																													// front
																													// end
		// SimpleDateFormat outputDateFormat = new
		// SimpleDateFormat(CrmFieldConstants.OUTPUT_DATE_FORMAT.getValue()); // convert
		// date in this format

		try {
			Date fromDate = (inputDateFormat.parse(dateFrom));
			// dateFrom = outputDateFormat.format(fromDate);
			Calendar cal = Calendar.getInstance();
			cal.setTime(fromDate);
			dateFrom = cal.get(Calendar.YEAR) + "-" + (cal.get(Calendar.MONTH) + 1) + "-" + cal.get(Calendar.DATE);

			return dateFrom;
		} catch (Exception exception) {
			logger.error("Exception", exception);
			return null;
		}
	}

	public static String formatDateforChargeback(String date) {

		try {
			String[] parts = date.split("-");
			date = parts[2] + "-" + parts[1] + "-" + parts[0];
			return date;
		} catch (Exception exception) {
			logger.error("Exception", exception);
			return null;
		}
	}

	public static String formatToDate(String dateTo) {
		SimpleDateFormat inputDateFormat = new SimpleDateFormat(CrmFieldConstants.INPUT_DATE_FORMAT.getValue()); // get
																													// date
																													// format
																													// as
																													// is
																													// from
																													// front
																													// end
		// SimpleDateFormat outputDateFormat = new
		// SimpleDateFormat(CrmFieldConstants.OUTPUT_DATE_FORMAT.getValue()); // convert
		// date in this format
		Calendar cal = Calendar.getInstance();

		try {
			Date toDate = inputDateFormat.parse(dateTo);
			cal.setTime(toDate);
			cal.add(Calendar.DAY_OF_MONTH, 1);
			// dateTo = outputDateFormat.format(cal.getTime());
			dateTo = cal.get(Calendar.YEAR) + "-" + (cal.get(Calendar.MONTH) + 1) + "-" + cal.get(Calendar.DATE);
			return dateTo;
		} catch (Exception exception) {
			logger.error("Exception", exception);
			return null;
		}
	}

	public static Date formatStringToDate(String date) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		try {
			Date dt = formatter.parse(date);
			return dt;
		} catch (Exception exception) {
			logger.error("Exception", exception);
			return null;
		}
	}

	public static long diffDate(String date1, String date2) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		try {
			Date dt1 = formatter.parse(formatFromDate(date1));
			Date dt2 = formatter.parse(formatFromDate(date2));
			long diff = dt2.getTime() - dt1.getTime();
			long diffDays = diff / (24 * 60 * 60 * 1000);
			return diffDays;
		} catch (Exception exception) {
			logger.error("Exception", exception);
			return 0;
		}
	}

	public static String formatDateForDb(Date date) {
		SimpleDateFormat outputDateFormat = new SimpleDateFormat(CrmFieldConstants.OUTPUT_DATE_FORMAT_DB.getValue());
		return outputDateFormat.format(date);
	}

	public static String defaultCurrentDateTime() {
		SimpleDateFormat inputDateFormat = new SimpleDateFormat(CrmFieldConstants.DATE_TIME_FORMAT.getValue());
		Date currentDate = new Date();
		try {
			return inputDateFormat.format(currentDate);
		} catch (Exception exception) {
			logger.error("Exception", exception);
			return null;
		}
	}

	@Deprecated
	public static Date currentDateTime() {
		// DateFormat inputDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		try {
			Calendar cal = Calendar.getInstance();
			Date dt = cal.getTime();
			return dt;
		} catch (Exception exception) {
			logger.error("Exception", exception);
			return null;
		}
	}

	public static Date formatStringToDateTime(String date) {
		DateFormat inputDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		try {
			Date dt = inputDateFormat.parse(date);
			return dt;
		} catch (Exception exception) {
			logger.error("Exception", exception);
			return null;
		}
	}

	public static Date convertStringToDateTime(String date) {
		DateFormat inputDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			Date dt = inputDateFormat.parse(date);
			return dt;
		} catch (Exception exception) {
			logger.error("Exception", exception);
			return null;
		}
	}

	public static String formatSaleDateTime(String date) {
		try {
			StringBuilder sbFormatDate = new StringBuilder();
			String[] parts = date.split(" ");
			String[] dateParts = parts[0].split("-");
			sbFormatDate.append(dateParts[0]);
			sbFormatDate.append(dateParts[1]);
			sbFormatDate.append(dateParts[2]);
			if (parts.length == 2) {
				String[] timeParts = parts[1].split(":");
				sbFormatDate.append(timeParts[0]);
				sbFormatDate.append(timeParts[1]);
				sbFormatDate.append(timeParts[2]);
			}
			return sbFormatDate.toString();
		} catch (Exception exception) {
			logger.error("Exception", exception);
			return null;
		}
	}

	// Format dd-MM-yyyy to YYYY-MM-dd
	public static String formatDateTime(String date) {
		try {
			StringBuilder sbFormatDate = new StringBuilder();
			String[] parts = date.split(" ");
			String[] dateParts = parts[0].split("-");
			sbFormatDate.append(dateParts[2]);
			sbFormatDate.append("-");
			sbFormatDate.append(dateParts[1]);
			sbFormatDate.append("-");
			sbFormatDate.append(dateParts[0]);
			sbFormatDate.append(" ");
			sbFormatDate.append(parts[1]);
			/*
			 * if(parts.length == 2) { String[] timeParts = parts[1].split(":");
			 * sbFormatDate.append(timeParts[0]); sbFormatDate.append(timeParts[1]);
			 * sbFormatDate.append(timeParts[2]); }
			 */
			return sbFormatDate.toString();
		} catch (Exception exception) {
			logger.error("Exception", exception);
			return null;
		}
	}

	public static String formatDate(String date) {
		try {
			StringBuilder sbFormatDate = new StringBuilder();
			String[] parts = date.split(" ");
			String[] dateParts = parts[0].split("-");
			sbFormatDate.append(dateParts[2]);
			sbFormatDate.append("-");
			sbFormatDate.append(dateParts[1]);
			sbFormatDate.append("-");
			sbFormatDate.append(dateParts[0]);
			sbFormatDate.append(" ");
			return sbFormatDate.toString();
		} catch (Exception exception) {
			logger.error("Exception", exception);
			return null;
		}
	}

	public static String formatSaleDate(String date) {
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

	public static String formatSettleDate(String date) {
		try {
			StringBuilder sbFormatDate = new StringBuilder();
			String[] parts = date.split(" ");
			String[] dateParts = parts[0].split("-");

			sbFormatDate.append(dateParts[2]);
			sbFormatDate.append(dateParts[1]);
			sbFormatDate.append(dateParts[0]);
			return sbFormatDate.toString();
		} catch (Exception exception) {
			logger.error("Exception", exception);
			return null;
		}
	}

	public static String formatCaptureDate(String date) {
		try {
			StringBuilder sbFormatDate = new StringBuilder();
			String[] parts = date.split(" ");
			String[] dateParts = parts[0].split("-");

			sbFormatDate.append(dateParts[2]);
			sbFormatDate.append(dateParts[1]);
			sbFormatDate.append(dateParts[0]);
			return sbFormatDate.toString();
		} catch (Exception exception) {
			logger.error("Exception", exception);
			return null;
		}
	}

	//Added By sweety
	public static String formatDate1(String date) {
		try {
			StringBuilder sbFormatDate = new StringBuilder();
			String[] parts = date.split(" ");
			String[] dateParts = parts[0].split("-");

			sbFormatDate.append(dateParts[2]);
			sbFormatDate.append("-");
			sbFormatDate.append(dateParts[1]);
			sbFormatDate.append("-");
			sbFormatDate.append(dateParts[0]);
			return sbFormatDate.toString();
		} catch (Exception exception) {
			logger.error("Exception", exception);
			return null;
		}
	}
	public static String toDateTimeformatCreater(String date) {
		String dbFormatDateTime = null;
		StringBuilder stringBuilder = new StringBuilder();
		String year = date.substring(6, 10);
		stringBuilder.append(year);
		stringBuilder.append("-");
		String month = date.substring(3, 5);
		stringBuilder.append(month);
		stringBuilder.append("-");
		String datec = date.substring(0, 2);
		stringBuilder.append(datec);
		stringBuilder.append(CrmFieldConstants.TO_TIME_FORMAT.getValue());

		dbFormatDateTime = stringBuilder.toString();
		logger.info("dbFormatDateTime",dbFormatDateTime);
		

		return dbFormatDateTime;

	}

	public static String toDateTimeformatCreaterWithHhMmSs(String date) {

		String hourMinsSec = date.substring(11, date.length()).trim();
		String dbFormatDateTime = null;
		StringBuilder stringBuilder = new StringBuilder();
		String year = date.substring(6, 10);
		stringBuilder.append(year);
		stringBuilder.append("-");
		String month = date.substring(3, 5);
		stringBuilder.append(month);
		stringBuilder.append("-");
		String datec = date.substring(0, 2);
		stringBuilder.append(datec);

		dbFormatDateTime = stringBuilder.toString();

		return dbFormatDateTime.concat(" ").concat(hourMinsSec);

	}

	public static String formDateTimeformatCreater(String date) {
		String dbFormatDateTime = null;
		StringBuilder stringBuilder = new StringBuilder();
		String year = date.substring(6, 10);
		stringBuilder.append(year);
		stringBuilder.append("-");
		String month = date.substring(3, 5);
		stringBuilder.append(month);
		stringBuilder.append("-");
		String datec = date.substring(0, 2);
		stringBuilder.append(datec);
		stringBuilder.append(CrmFieldConstants.FROM_TIME_FORMAT.getValue());

		dbFormatDateTime = stringBuilder.toString();

		return dbFormatDateTime;

	}

	public static String formatDateReco(String recoDate) {
		String dbFormatDateTime = null;
		StringBuilder stringBuilder = new StringBuilder();
		String year = recoDate.substring(0, 4);
		stringBuilder.append(year);
		// stringBuilder.append("-");
		String month = recoDate.substring(5, 7);
		stringBuilder.append(month);
		// stringBuilder.append("-");
		String datec = recoDate.substring(8, 10);
		stringBuilder.append(datec);
		// stringBuilder.append(CrmFieldConstants.FROM_TIME_FORMAT.getValue());

		dbFormatDateTime = stringBuilder.toString();

		return dbFormatDateTime;

	}

	public static String formatDateTransaction(String recoDate) {
		String dbFormatDateTime = null;
		StringBuilder stringBuilder = new StringBuilder();
		String year = recoDate.substring(0, 4);
		stringBuilder.append(year);
		// stringBuilder.append("-");
		String month = recoDate.substring(5, 7);
		stringBuilder.append(month);
		// stringBuilder.append("-");
		String datec = recoDate.substring(8, 10);
		stringBuilder.append(datec);
		// stringBuilder.append(CrmFieldConstants.FROM_TIME_FORMAT.getValue());

		dbFormatDateTime = stringBuilder.toString();

		return dbFormatDateTime;

	}

	public static String formatDateSettlement(String settleDate) {
		String dbFormatDateTime = null;
		StringBuilder stringBuilder = new StringBuilder();
		String year = settleDate.substring(0, 4);
		stringBuilder.append(year);
		// stringBuilder.append("-");
		String month = settleDate.substring(5, 7);
		stringBuilder.append(month);
		// stringBuilder.append("-");
		String datec = settleDate.substring(8, 10);
		stringBuilder.append(datec);

		/*
		 * String hour= settleDate.substring(11,13); stringBuilder.append(hour);
		 * 
		 * String minute= settleDate.substring(14,16); stringBuilder.append(minute);
		 * 
		 * String second= settleDate.substring(17,19); stringBuilder.append(second);
		 */

		// stringBuilder.append(CrmFieldConstants.FROM_TIME_FORMAT.getValue());

		dbFormatDateTime = stringBuilder.toString();

		return dbFormatDateTime;

	}

	public static LocalDate formatStringToLocalDate(String date) {
		// DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd
		// HH:MM:SS");

		// convert String to LocalDate
		LocalDate localDate = LocalDate.parse(date, dateTimeFormat);

		return localDate;
	}

	// TODO review return type -->because our format can't be parsed in
	// LocalDateTime
	public static String subtractHours(LocalDateTime currentStamp, long hours) {
		LocalDateTime finalStamp = currentStamp.minusHours(hours);
		return finalStamp.format(dateTimeFormat);
	}

	public static String subtractDays(LocalDateTime currentStamp, long days) {
		LocalDateTime finalStamp = currentStamp.minusDays(days);
		return finalStamp.format(dateTimeFormat);
	}

	public static String subtractWeeks(LocalDateTime currentStamp, long weeks) {
		LocalDateTime finalStamp = currentStamp.minusWeeks(weeks);
		return finalStamp.format(dateTimeFormat);
	}

	public static String subtractMonths(LocalDateTime currentStamp, long months) {
		LocalDateTime finalStamp = currentStamp.minusMonths(months);
		return finalStamp.format(dateTimeFormat);
	}

	public static String subtractYear(LocalDateTime currentStamp, long years) {
		LocalDateTime finalStamp = currentStamp.minusYears(years);
		return finalStamp.format(dateTimeFormat);
	}

	public static LocalDateTime now() {
		return LocalDateTime.now();
	}

	public static String formatDateForReco(String date, String inputFormat) {
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

	public static String addOneSecondInDate(String date) {
		String capturedDate = null;
		DateFormat inputDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		SimpleDateFormat outputDateFormat = new SimpleDateFormat(Constants.OUTPUT_DATE_FORMAT_DB.getValue());
		try {
			Date dt = (Date) (inputDateFormat.parse(date));
			Calendar cal = Calendar.getInstance();
			cal.setTime(dt);
			cal.add(Calendar.SECOND, 1);
			capturedDate = outputDateFormat.format(cal.getTime());
		} catch (ParseException e) {
			logger.error("Exception", e);
		}
		return capturedDate;
	}

	public static String dateformatCreater(String date) {
		String dbFormatDateTime = null;
		StringBuilder stringBuilder = new StringBuilder();
		String year = date.substring(0, 4);
		stringBuilder.append(year);
		stringBuilder.append("-");
		String month = date.substring(4, 6);
		stringBuilder.append(month);
		stringBuilder.append("-");
		String datec = date.substring(6, 8);
		stringBuilder.append(datec);
		stringBuilder.append(" 00:00:00");
		// stringBuilder.append(Constants.OUTPUT_DATE_FORMAT_DB.getValue());

		dbFormatDateTime = stringBuilder.toString();

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

	public static String getCurrentDate() {
		try {

			Instant machineTimestamp = Instant.now();
			ZonedDateTime TimeinLA = machineTimestamp.atZone(ZoneId.of("Asia/Kolkata"));

			return TimeinLA.toLocalDateTime().toString();

		} catch (Exception exception) {
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

	public static int diffMinutes(Timestamp timestamp1, Timestamp timestamp2) {
		long milliseconds = timestamp2.getTime() - timestamp1.getTime();
		long minutes = TimeUnit.MILLISECONDS.toMinutes(milliseconds);
		int diffTmMinutes = (int) minutes;
		return diffTmMinutes;
	}

	public static String getCurrentDateWithSTime() {
		try {

			Instant machineTimestamp = Instant.now();
			ZonedDateTime TimeinLA = machineTimestamp.atZone(ZoneId.of("Asia/Kolkata"));
			return StringUtils.join(dateFormat.format(TimeinLA.toLocalDateTime()), " 00:00:00");

		} catch (Exception exception) {
			logger.error("Exception", exception);
			return defaultCurrentDateTime();
		}
	}

	public static String getCurrentWeekStartDate() {
		try {

			DayOfWeek firstDayOfWeek = WeekFields.of(Locale.getDefault()).getFirstDayOfWeek();
			return StringUtils.join(LocalDate.now().with(TemporalAdjusters.previousOrSame(firstDayOfWeek)),
					" 00:00:00");

		} catch (Exception exception) {
			logger.error("Exception", exception);
			return defaultCurrentDateTime();
		}
	}

	public static String getCurrentMonthStartDate() {
		try {
			Instant machineTimestamp = Instant.now();
			ZonedDateTime TimeinLA = machineTimestamp.atZone(ZoneId.of("Asia/Kolkata"));
			return StringUtils.join(dateFormat.format(TimeinLA.withDayOfMonth(1).toLocalDate()), " 00:00:00");

		} catch (Exception exception) {
			logger.error("Exception", exception);
			return defaultCurrentDateTime();
		}
	}

	public static String getCurrentYearStartDate() {
		try {
			Instant machineTimestamp = Instant.now();
			ZonedDateTime TimeinLA = machineTimestamp.atZone(ZoneId.of("Asia/Kolkata"));
			return StringUtils.join(dateFormat.format(TimeinLA.withDayOfYear(1).toLocalDate()), " 00:00:00");

		} catch (Exception exception) {
			logger.error("Exception", exception);
			return defaultCurrentDateTime();
		}
	}
}