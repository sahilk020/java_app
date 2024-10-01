package com.pay10.crm.fraudPrevention.action;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger; import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.pay10.commons.user.FraudPrevention;
import com.pay10.commons.user.User;
import com.pay10.commons.user.UserDao;
import com.pay10.commons.util.CrmFieldConstants;
import com.pay10.commons.util.Currency;
import com.pay10.commons.util.Weekdays;
import com.pay10.crm.action.AbstractSecureAction;
import com.pay10.pg.core.fraudPrevention.model.FraudPreventionDao;

/**
 * @author Harpreet
 *
 */
public class ViewFraudRule extends AbstractSecureAction {

	@Autowired
	private FraudPreventionDao fraudPreventionDao;
	
	@Autowired
	private UserDao userDao;

	private static final long serialVersionUID = -303816138858895252L;
	private static Logger logger = LoggerFactory.getLogger(ViewFraudRule.class.getName());
	private List<FraudPrevention> fraudRuleList = new ArrayList<>();
	// private List<FraudPrevention> fraudRule = new ArrayList<>();

	private String payId;

	// retrieving fraud rule list
	@Override
	public String execute() {
		try {
			if (payId.equalsIgnoreCase(CrmFieldConstants.ALL.getValue())) {
				fraudRuleList = fraudPreventionDao.getFraudRuleList(CrmFieldConstants.ALL.getValue());
				
				for (FraudPrevention fraudRule : fraudRuleList) {
					String payId = fraudRule.getPayId();
					if(!(payId.equals("ALL"))){
						User merchant = userDao.findPayId(payId);
						fraudRule.setPayId(merchant.getBusinessName());
					}
					boolean alwaysOnFlag = fraudRule.isAlwaysOnFlag();
					if (alwaysOnFlag == false) {

						String dateFrom = fraudRule.getDateActiveFrom();
						String dateTo = fraudRule.getDateActiveTo();
						SimpleDateFormat originalFormatter = new SimpleDateFormat("yyyyMMdd");
						SimpleDateFormat newFormatter = new SimpleDateFormat("dd/MM/yyyy");

						// parsing date string using original format
						ParsePosition pos = new ParsePosition(0);
						ParsePosition pos1 = new ParsePosition(0);
						Date dateFromString = originalFormatter.parse(dateFrom, pos);
						Date dateToString = originalFormatter.parse(dateTo, pos1);

						// Now you have a date object and you can convert it to
						// the
						// new format
						String dateStringInNewFormat = newFormatter.format(dateFromString);
						String dateToStringInNewFormat = newFormatter.format(dateToString);
						fraudRule.setDateActiveFrom(dateStringInNewFormat);
						fraudRule.setDateActiveTo(dateToStringInNewFormat);

						// Change Start time format
						String startTimeValue = fraudRule.getStartTime();
						StringBuilder startTime = new StringBuilder();
						String hour = startTimeValue.substring(0, 2);
						String mintnue = startTimeValue.substring(2, 4);
						String second = startTimeValue.substring(4, 6);

						startTime.append(hour);
						startTime.append(":");
						startTime.append(mintnue);
						startTime.append(":");
						startTime.append(second);

						fraudRule.setStartTime(startTime.toString());

						// Change End time format
						String endTimeValue = fraudRule.getEndTime();
						StringBuilder EndTime = new StringBuilder();
						String endtimehour = endTimeValue.substring(0, 2);
						String endtimemintnue = endTimeValue.substring(2, 4);
						String endtimesecond = endTimeValue.substring(4, 6);

						EndTime.append(endtimehour);
						EndTime.append(":");
						EndTime.append(endtimemintnue);
						EndTime.append(":");
						EndTime.append(endtimesecond);

						fraudRule.setEndTime(EndTime.toString());

						// Change repeat day format
						String repeatDays = fraudRule.getRepeatDays();
						String[] details = repeatDays.split(",");
						StringBuilder dayCodes = new StringBuilder();

						for (String dayname : details) {
							Weekdays dayInstance = Weekdays.getday(dayname);
							String dayName = dayInstance.getName();
							dayCodes.append(dayName);
							dayCodes.append(",");
						}

						String dayCode = dayCodes.toString().substring(0, dayCodes.length() - 1);
						fraudRule.setRepeatDays(dayCode);
					}
					String currency = fraudRule.getCurrency();
					if ((currency != null)) {
						currency = Currency.getAlphabaticCode(currency);
						fraudRule.setCurrency(currency);
					}

				}
				return SUCCESS;
			} else {
				fraudRuleList = fraudPreventionDao.getFraudRuleListbyPayId(payId);
				for (FraudPrevention fraudRule : fraudRuleList) {
				User merchant = userDao.findPayId(payId);
				fraudRule.setPayId(merchant.getBusinessName());
				boolean alwaysOnFlag = fraudRule.isAlwaysOnFlag();
				if (alwaysOnFlag == false) {

					String dateFrom = fraudRule.getDateActiveFrom();
					String dateTo = fraudRule.getDateActiveTo();
					SimpleDateFormat originalFormatter = new SimpleDateFormat("yyyyMMdd");
					SimpleDateFormat newFormatter = new SimpleDateFormat("dd/MM/yyyy");

					// parsing date string using original format
					ParsePosition pos = new ParsePosition(0);
					ParsePosition pos1 = new ParsePosition(0);
					Date dateFromString = originalFormatter.parse(dateFrom, pos);
					Date dateToString = originalFormatter.parse(dateTo, pos1);

					// Now you have a date object and you can convert it to
					// the
					// new format
					String dateStringInNewFormat = newFormatter.format(dateFromString);
					String dateToStringInNewFormat = newFormatter.format(dateToString);
					fraudRule.setDateActiveFrom(dateStringInNewFormat);
					fraudRule.setDateActiveTo(dateToStringInNewFormat);

					// Change Start time format
					String startTimeValue = fraudRule.getStartTime();
					StringBuilder startTime = new StringBuilder();
					String hour = startTimeValue.substring(0, 2);
					String mintnue = startTimeValue.substring(2, 4);
					String second = startTimeValue.substring(4, 6);

					startTime.append(hour);
					startTime.append(":");
					startTime.append(mintnue);
					startTime.append(":");
					startTime.append(second);

					fraudRule.setStartTime(startTime.toString());

					// Change End time format
					String endTimeValue = fraudRule.getEndTime();
					StringBuilder EndTime = new StringBuilder();
					String endtimehour = endTimeValue.substring(0, 2);
					String endtimemintnue = endTimeValue.substring(2, 4);
					String endtimesecond = endTimeValue.substring(4, 6);

					EndTime.append(endtimehour);
					EndTime.append(":");
					EndTime.append(endtimemintnue);
					EndTime.append(":");
					EndTime.append(endtimesecond);

					fraudRule.setEndTime(EndTime.toString());

					// Change repeat day format
					String repeatDays = fraudRule.getRepeatDays();
					String[] details = repeatDays.split(",");
					StringBuilder dayCodes = new StringBuilder();

					for (String dayname : details) {
						Weekdays dayInstance = Weekdays.getday(dayname);
						String dayName = dayInstance.getName();
						dayCodes.append(dayName);
						dayCodes.append(",");
					}

					String dayCode = dayCodes.toString().substring(0, dayCodes.length() - 1);
					fraudRule.setRepeatDays(dayCode);
				}
				String currency = fraudRule.getCurrency();
				if ((currency != null)) {
					currency = Currency.getAlphabaticCode(currency);
					fraudRule.setCurrency(currency);
				}
				}
				return SUCCESS;
			}
		} catch (Exception excetpion) {
			logger.error("Fraud Prevention System - Exception :" + excetpion);
			return ERROR;
		}
	}

	@Override
	public void validate() {
		// TODO
	}

	public List<FraudPrevention> getFraudRuleList() {
		return fraudRuleList;
	}

	public void setFraudRuleList(List<FraudPrevention> fraudRuleList) {
		this.fraudRuleList = fraudRuleList;
	}

	public String getPayId() {
		return payId;
	}

	public void setPayId(String payId) {
		this.payId = payId;
	}
}