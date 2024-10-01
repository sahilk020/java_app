package com.pay10.crm.fraudPrevention.action;

import java.math.BigInteger;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.pay10.commons.user.MultCurrencyCodeDao;
import com.pay10.commons.user.UserDao;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.pay10.commons.dao.FraudPreventionMongoService;
import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.user.User;
import com.pay10.commons.user.UserType;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.CrmFieldConstants;
import com.pay10.commons.util.CrmFieldType;
import com.pay10.commons.util.CrmValidator;
import com.pay10.commons.util.Currency;
import com.pay10.commons.util.FraudPreventionObj;
import com.pay10.commons.util.Weekdays;
import com.pay10.crm.action.AbstractSecureAction;

/**
 * @author Rajendra
 *
 */

public class RulesSearchAction  extends AbstractSecureAction {


	private static final long serialVersionUID = -6323553936458486881L;

	private static Logger logger = LoggerFactory.getLogger(RulesSearchAction.class.getName());

	private String payId;
	private String searchString;
	private String rule;
	private int draw;
	private int length;
	private int start;
	private String currency;
	private List<FraudPreventionObj> fraudRuleList = new ArrayList<FraudPreventionObj>();
	private BigInteger recordsTotal = BigInteger.ZERO;
	public BigInteger recordsFiltered = BigInteger.ZERO;
	
	@Autowired
	private FraudPreventionMongoService fraudPreventionMongoService;
	@Autowired
	private MultCurrencyCodeDao multCurrencyCodeDao;
	
	
	@Override
	public String execute() {
		try {
			User sessionUser = (User) sessionMap.get(Constants.USER.getValue());
			if (sessionUser.getUserType().equals(UserType.SUPERADMIN)
					|| sessionUser.getUserType().equals(UserType.ADMIN)
					|| sessionUser.getUserType().equals(UserType.SUBADMIN)
					|| sessionUser.getUserType().equals(UserType.MERCHANT)) {
				
				if(StringUtils.isNotBlank(payId)) {
					setPayId(payId);
				}else {
					payId = sessionUser.getPayId();	
				}
				fraudRuleList = fraudPreventionMongoService.findFpbyRuleSearch(payId, searchString, rule,currency);
				logger.info("Fraud Rule List{}",fraudRuleList);
				for (FraudPreventionObj fraudRule : fraudRuleList) {
				fraudRule.setPayId(fraudRule.getPayId());
				boolean alwaysOnFlag = fraudRule.isAlwaysOnFlag();
				if (alwaysOnFlag == false  && !StringUtils.equals(fraudRule.getDateActiveFrom(), "NA")) {

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
					currency = multCurrencyCodeDao.getCurrencyNamebyCode(currency);
					fraudRule.setCurrency(currency);
				}
				}
			}

			BigInteger bigInt = BigInteger.valueOf(fraudRuleList.size());
			setRecordsTotal(bigInt);
			if (getLength() == -1) {
				setLength(getRecordsTotal().intValue());
			}
			recordsFiltered = recordsTotal;
			
		} catch (Exception e) {
			logger.error("Exception in getting transaction summary count data " + e);
			return NONE;
		}

		return SUCCESS;
	}

	@Override
	public void validate() {
		CrmValidator validator = new CrmValidator();

		if (validator.validateBlankField(getPayId())
				|| getPayId().equals(CrmFieldConstants.ALL.getValue())) {
		} else if (!validator.validateField(CrmFieldType.PAY_ID, getPayId())) {
			addFieldError(CrmFieldType.PAY_ID.getName(), ErrorType.INVALID_FIELD.getResponseMessage());
		}

	}


	public String getPayId() {
		return payId;
	}

	public void setPayId(String payId) {
		this.payId = payId;
	}

	public String getSearchString() {
		return searchString;
	}

	public void setSearchString(String searchString) {
		this.searchString = searchString;
	}
	
	public String getRule() {
		return rule;
	}

	public void setRule(String rule) {
		this.rule = rule;
	}
	
	public List<FraudPreventionObj> getFraudRuleList() {
		return fraudRuleList;
	}

	public void setFraudRuleList(List<FraudPreventionObj> fraudRuleList) {
		this.fraudRuleList = fraudRuleList;
	}
	
	public int getDraw() {
		return draw;
	}

	public void setDraw(int draw) {
		this.draw = draw;
	}

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public int getStart() {
		return start;
	}

	public void setStart(int start) {
		this.start = start;
	}

	public BigInteger getRecordsTotal() {
		return recordsTotal;
	}

	public void setRecordsTotal(BigInteger recordsTotal) {
		this.recordsTotal = recordsTotal;
	}

	public BigInteger getRecordsFiltered() {
		return recordsFiltered;
	}

	public void setRecordsFiltered(BigInteger recordsFiltered) {
		this.recordsFiltered = recordsFiltered;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}
}
