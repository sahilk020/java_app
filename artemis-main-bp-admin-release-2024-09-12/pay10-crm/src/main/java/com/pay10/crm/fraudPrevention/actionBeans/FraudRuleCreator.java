package com.pay10.crm.fraudPrevention.actionBeans;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pay10.commons.dao.FraudPreventionMongoService;
import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.exception.SystemException;
import com.pay10.commons.user.ResponseObject;
import com.pay10.commons.user.User;
import com.pay10.commons.util.FraudPreventionObj;
import com.pay10.commons.util.FraudRuleType;
import com.pay10.commons.util.TDRStatus;
import com.pay10.commons.util.TransactionManager;
import com.pay10.commons.util.Weekdays;
import com.pay10.pg.core.fraudPrevention.core.CheckExistingFraudRule;
import com.pay10.pg.core.fraudPrevention.model.FraudRuleModel;

/**
 * @author Harpreet, Rahul
 *
 */

@Service
public class FraudRuleCreator {

	@Autowired
	private CheckExistingFraudRule checkExistingFraudRule;

	private static Logger logger = LoggerFactory.getLogger(FraudRuleCreator.class.getName());

	@Autowired
	private ResponseObject ruleCheckResponse;

	@Autowired
	private FraudPreventionMongoService fraudPreventionMongoService;

	@Autowired
	private ResponseObject finalResponse;

	public ResponseObject createFraudRule(FraudRuleModel fraudRuleModel, String createdBy, User sessionUser) {
		FraudPreventionObj fraudPrevention = new FraudPreventionObj();

		//logger.info("FraudRuleCreator   createFraudRule currency ={}", fraudRuleModel.getMerchantCurrency());

		try {
			logger.info("************************************************");

			fraudPrevention.setId(fraudRuleModel.getId());
			fraudPrevention.setPayId(fraudRuleModel.getPayId());
			FraudRuleType fraudRuleType = FraudRuleType.getInstance(fraudRuleModel.getFraudType());
			fraudPrevention.setFraudType(fraudRuleType);
		 	fraudPrevention.setCreatedBy(createdBy);
			//Added By Sweety
			fraudRuleModel.setCreatedBy(createdBy);
			fraudRuleModel.setUpdatedBy(createdBy);
			fraudPrevention.setCurrency(fraudRuleModel.getCurrency());

			switch (fraudRuleType) {
			case BLOCK_NO_OF_TXNS:
				fraudPrevention.setMinutesTxnLimit(fraudRuleModel.getMinutesTxnLimit());
				fraudPrevention.setNoOfTransactionAllowed(fraudRuleModel.getNoOfTransactionAllowed());
				fraudRuleModel.setAlwaysOnFlag(true);
				fraudPrevention.setBlockTimeUnits(fraudRuleModel.getBlockTimeUnits());
				commonActions(fraudPrevention, fraudRuleModel, sessionUser);
				break;
			case BLOCK_TXN_AMOUNT:
				fraudPrevention.setCurrency(fraudRuleModel.getCurrency());
				fraudPrevention.setMinTransactionAmount(fraudRuleModel.getMinTransactionAmount());
				fraudPrevention.setMaxTransactionAmount(fraudRuleModel.getMaxTransactionAmount());
				fraudPrevention.setFixedAmountFlag(fraudRuleModel.isFixedAmountFlag());
				fraudPrevention.setTransactionAmount(fraudRuleModel.getTransactionAmount());
				fraudPrevention.setRepetationCount(fraudRuleModel.getRepetationCount());
				fraudRuleModel.setAlwaysOnFlag(true);
				fraudPrevention.setBlockTimeUnits(fraudRuleModel.getBlockTimeUnits());
				commonActionscurrency(fraudPrevention, fraudRuleModel, sessionUser);
				break;
			case BLOCK_TXN_AMOUNT_VELOCITY:
				fraudPrevention.setCurrency(fraudRuleModel.getCurrency());
				fraudPrevention.setMaxTransactionAmount(fraudRuleModel.getMaxTransactionAmount());
				fraudPrevention.setUserIdentifier(fraudRuleModel.getUserIdentifier());
				fraudPrevention.setEmailToNotify(fraudRuleModel.getEmailToNotify());
				fraudPrevention.setRepetationCount(fraudRuleModel.getRepetationCount());
				fraudPrevention.setBlockTimeUnits(fraudRuleModel.getBlockTimeUnits());
				fraudPrevention.setAlwaysOnFlag(true);
				fraudPrevention.setStatusVelocity(fraudRuleModel.getStatusVelocity());
				fraudPrevention.setMonitoringType(fraudRuleModel.getMonitoringType());
				commonActionscurrency(fraudPrevention, fraudRuleModel, sessionUser);
				break;
			case BLOCK_IP_ADDRESS:
				String iPAddress = fraudRuleModel.getIpAddress();
				fraudPrevention.setIpAddress(iPAddress);
				commonActions(fraudPrevention, fraudRuleModel, sessionUser);
				break;
			case BLOCK_EMAIL_ID:
				String emailIds = fraudRuleModel.getEmail();
				fraudPrevention.setEmail(emailIds);
				commonActions(fraudPrevention, fraudRuleModel, sessionUser);
				break;
			case BLOCK_PHONE_NUMBER:
				String phoneNums = fraudRuleModel.getPhone();
				fraudPrevention.setPhone(phoneNums);
				commonActions(fraudPrevention, fraudRuleModel, sessionUser);
				break;
			case BLOCK_USER_COUNTRY:
				List<String> countries = Arrays.asList(StringUtils.split(fraudRuleModel.getUserCountry(), ","));
				if (countries.size() > 1) {
					countries.forEach(country -> {
						fraudPrevention.setUserCountry(country);
						ruleCheckResponse = checkExistingFraudRule.exists(fraudPrevention);
						if (ruleCheckResponse.getResponseCode() == ErrorType.FRAUD_RULE_ALREADY_EXISTS
								.getResponseCode()) {
							countries.remove(country);
						}
					});
					fraudRuleModel.setUserCountry(StringUtils.join(countries, ","));
				}
				fraudPrevention.setUserCountry(fraudRuleModel.getUserCountry());
				fraudRuleModel.setAlwaysOnFlag(true);
				commonActions(fraudPrevention, fraudRuleModel, sessionUser);
				break;
			case BLOCK_CARD_ISSUER_COUNTRY:
				List<String> issuerCountries = Arrays.asList(StringUtils.split(fraudRuleModel.getIssuerCountry(), ","));
				if (issuerCountries.size() > 1) {
					issuerCountries.forEach(country -> {
						fraudPrevention.setIssuerCountry(country);
						ruleCheckResponse = checkExistingFraudRule.exists(fraudPrevention);
						if (ruleCheckResponse.getResponseCode() == ErrorType.FRAUD_RULE_ALREADY_EXISTS
								.getResponseCode()) {
							issuerCountries.remove(country);
						}
					});
					fraudRuleModel.setIssuerCountry(StringUtils.join(issuerCountries, ","));
				}
				fraudPrevention.setIssuerCountry(fraudRuleModel.getIssuerCountry());
				fraudRuleModel.setAlwaysOnFlag(true);
				commonActions(fraudPrevention, fraudRuleModel, sessionUser);
				break;
			case BLOCK_CARD_BIN:
				String binValues = fraudRuleModel.getNegativeBin();
				fraudPrevention.setNegativeBin(binValues);
				commonActions(fraudPrevention, fraudRuleModel, sessionUser);
				break;
			case BLOCK_CARD_NO:
				String cardValues = fraudRuleModel.getNegativeCard();
				fraudPrevention.setNegativeCard(cardValues);
				commonActions(fraudPrevention, fraudRuleModel, sessionUser);
				break;
			case BLOCK_CARD_TXN_THRESHOLD:
				fraudPrevention.setPerCardTransactionAllowed(fraudRuleModel.getPerCardTransactionAllowed());
				fraudPrevention.setNegativeCard(fraudRuleModel.getNegativeCard());
				commonActions(fraudPrevention, fraudRuleModel, sessionUser);
				break;
			case BLOCK_MACK_ADDRESS:
				String[] mackAddressArray = StringUtils.split(fraudRuleModel.getMackAddress(), ",");
				for (String mackAddress : mackAddressArray) {
					fraudPrevention.setMackAddress(mackAddress);
					commonActions(fraudPrevention, fraudRuleModel, sessionUser);
				}
				break;
			case REPEATED_MOP_TYPES:
				fraudPrevention.setBlockTimeUnits(fraudRuleModel.getBlockTimeUnits());
				fraudPrevention.setEmailToNotify(fraudRuleModel.getEmailToNotify());
				fraudPrevention.setPercentageOfRepeatedMop(fraudRuleModel.getPercentageOfRepeatedMop());
				fraudPrevention.setPaymentType(fraudRuleModel.getPaymentType());
				commonActions(fraudPrevention, fraudRuleModel, sessionUser);
				break;
			case FIRST_TRANSACTIONS_ALERT:
				fraudPrevention.setEmailToNotify(fraudRuleModel.getEmailToNotify());
				fraudPrevention.setTransactionAmount(fraudRuleModel.getTransactionAmount());
				commonActionscurrency(fraudPrevention, fraudRuleModel, sessionUser);
				break;
			case BLOCK_USER_STATE:
				List<String> stateCodes = Arrays.asList(StringUtils.split(fraudRuleModel.getStateCode(), ","));
				if (stateCodes.size() > 1) {
					stateCodes.forEach(state -> {
						fraudPrevention.setStateCode(state);
						ruleCheckResponse = checkExistingFraudRule.exists(fraudPrevention);
						if (ruleCheckResponse.getResponseCode() == ErrorType.FRAUD_RULE_ALREADY_EXISTS
								.getResponseCode()) {
							stateCodes.remove(state);
						}
					});
					fraudRuleModel.setStateCode(StringUtils.join(stateCodes, ","));
				}
				fraudPrevention.setStateCode(fraudRuleModel.getStateCode());
				fraudPrevention.setCountry(fraudRuleModel.getCountry());
				commonActions(fraudPrevention, fraudRuleModel, sessionUser);
				break;
			case BLOCK_USER_CITY:
				List<String> cities = Arrays.asList(StringUtils.split(fraudRuleModel.getCity(), ","));
				if (cities.size() > 1) {
					cities.forEach(city -> {
						fraudPrevention.setCity(city);
						ruleCheckResponse = checkExistingFraudRule.exists(fraudPrevention);
						if (ruleCheckResponse.getResponseCode() == ErrorType.FRAUD_RULE_ALREADY_EXISTS
								.getResponseCode()) {
							cities.remove(city);
						}
					});
					fraudRuleModel.setCity(StringUtils.join(cities, ","));
				}
				fraudPrevention.setCity(fraudRuleModel.getCity());
				fraudPrevention.setCountry(fraudRuleModel.getCountry());
				fraudPrevention.setState(fraudRuleModel.getState());
				commonActions(fraudPrevention, fraudRuleModel, sessionUser);
				break;
			case BLOCK_REPEATED_MOP_TYPE_FOR_SAME_DETAIL:
				fraudPrevention.setMonitoringType(fraudRuleModel.getMonitoringType());
				fraudPrevention.setRepetationCount(fraudRuleModel.getRepetationCount());
				fraudPrevention.setBlockTimeUnits(fraudRuleModel.getBlockTimeUnits());
				fraudPrevention.setEmailToNotify(fraudRuleModel.getEmailToNotify());
				fraudPrevention.setCurrency(fraudRuleModel.getCurrency());

				commonActionscurrency(fraudPrevention, fraudRuleModel, sessionUser);
				break;
			case BLOCK_VPA_ADDRESS:
				List<String> vpaAddress = Arrays.asList(StringUtils.split(fraudRuleModel.getVpaAddress(), ","));
				if (vpaAddress.size() > 1) {
					vpaAddress.forEach(vpaAdd -> {
						fraudPrevention.setVpaAddress(vpaAdd);
						ruleCheckResponse = checkExistingFraudRule.exists(fraudPrevention);
						if (ruleCheckResponse.getResponseCode() == ErrorType.FRAUD_RULE_ALREADY_EXISTS
								.getResponseCode()) {
							vpaAddress.remove(vpaAdd);
						}
					});
					fraudRuleModel.setVpaAddress(StringUtils.join(vpaAddress, ","));
				}
				fraudPrevention.setVpaAddress(fraudRuleModel.getVpaAddress());
			 	commonActions(fraudPrevention, fraudRuleModel, sessionUser);
				break;
			}

			finalResponse.setResponseCode(ruleCheckResponse.getResponseCode());
			finalResponse.setResponseMessage(ruleCheckResponse.getResponseMessage());
			logger.info("Fraud Rule creation response " + ruleCheckResponse.getResponseMessage());
			return finalResponse;

		} catch (Exception exception) {
			logger.error("error" + exception);
			return ruleCheckResponse;
		}
	}

	public void commonActions(FraudPreventionObj fraudPrevention, FraudRuleModel fraudRuleModel, User sessionUser)
			throws SystemException {

	 	String id = fraudPrevention.getId();
		// generating rule id
		if (StringUtils.isBlank(fraudPrevention.getId())) {
			logger.info("Fraud Prevention ID is blank: "+ fraudPrevention.getId());
			fraudPrevention.setId(TransactionManager.getNewTransactionId());
		}
		boolean adminFpOverrideCondition = false;
		ruleCheckResponse = checkExistingFraudRule.exists(fraudPrevention);
		logger.info(" after checking the existing fraud rules " + ruleCheckResponse);
		if (StringUtils.isBlank(fraudPrevention.getId())
				&& ruleCheckResponse.getResponseCode().equals(ErrorType.FRAUD_RULE_ALREADY_EXISTS.getResponseCode())
				&& !StringUtils.equalsIgnoreCase(fraudRuleModel.getFraudType(),
						FraudRuleType.FIRST_TRANSACTIONS_ALERT.getValue())) {
			throw new SystemException(ErrorType.FRAUD_RULE_ALREADY_EXISTS,
					ErrorType.FRAUD_RULE_ALREADY_EXISTS.getResponseMessage());
		}
		FraudPreventionObj dbfraudPrevention = fraudPreventionMongoService.getexistingFraudRule(fraudPrevention);
		if (null != dbfraudPrevention && (null != dbfraudPrevention.getCreatedBy())) {
			adminFpOverrideCondition = true;
			if (StringUtils.isNotBlank(dbfraudPrevention.getId())) {
				logger.info("Fraud Rule Creator deFraudPrevention id: "+dbfraudPrevention.getId());
				fraudPrevention.setId(dbfraudPrevention.getId());
			}
		} else {
			logger.info("db fraud Prevention Id is not null");
		}

		fraudPrevention.setStatus(TDRStatus.ACTIVE);

		boolean alwaysOnFlag = fraudRuleModel.isAlwaysOnFlag();
		StringBuilder datefrom = new StringBuilder();
		StringBuilder dateTo = new StringBuilder();
		String formattedStartTime = "";
		String formattedEndTime = "";
		String dayCode = "";
		if (alwaysOnFlag == false && StringUtils.isNotBlank(fraudRuleModel.getDateActiveFrom())) {

			// Date from and date to format changed
			String dateActiveFrom = fraudRuleModel.getDateActiveFrom();
			String dateActiveTo = fraudRuleModel.getDateActiveTo();
			datefrom = datefrom.append(dateActiveFrom.substring(6)).append("").append(dateActiveFrom.substring(3, 5))
					.append("").append(dateActiveFrom.substring(0, 2));

			dateTo = dateTo.append(dateActiveTo.substring(6)).append("").append(dateActiveTo.substring(3, 5)).append("")
					.append(dateActiveTo.substring(0, 2));

			// Start time and end time format changes
			String startTime = fraudRuleModel.getStartTime();
			String endTime = fraudRuleModel.getEndTime();
			formattedStartTime = startTime.replaceAll(":", "");
			formattedEndTime = endTime.replaceAll(":", "");

			// weekdays format changed
			String days = fraudRuleModel.getRepeatDays();
			String[] details = days.split(",");
			StringBuilder dayCodes = new StringBuilder();

			for (String dayname : details) {
				Weekdays dayInstance = Weekdays.getInstanceIgnoreCase(dayname);
				dayCode = dayInstance.getCode();
				dayCodes.append(dayCode);
				dayCodes.append(",");
			}

			dayCode = dayCodes.toString().substring(0, dayCodes.length() - 1);

		} else {
			dayCode = "NA";
			formattedEndTime = "NA";
			formattedStartTime = "NA";
			dateTo.append("NA");
			datefrom.append("NA");

		}

		fraudPrevention.setDateActiveFrom(datefrom.toString());
		fraudPrevention.setDateActiveTo(dateTo.toString());
		fraudPrevention.setStartTime(formattedStartTime);
		fraudPrevention.setEndTime(formattedEndTime);
		fraudPrevention.setRepeatDays(dayCode);
		fraudPrevention.setAlwaysOnFlag(fraudRuleModel.isAlwaysOnFlag());

		if (adminFpOverrideCondition) {
			fraudPrevention.setUpdatedBy(sessionUser.getEmailId().toString());
			fraudPreventionMongoService.updateAdminFraudRule(fraudPrevention);
			if (StringUtils.isBlank(id)) {
				ruleCheckResponse.setResponseCode(ErrorType.FRAUD_RULE_SUCCESS.getResponseCode());
				ruleCheckResponse.setResponseMessage(ErrorType.FRAUD_RULE_SUCCESS.getResponseMessage());
			} else {
				ruleCheckResponse.setResponseCode(ErrorType.FRAUD_RULE_UPDATE_SUCCESS.getResponseCode());
				ruleCheckResponse.setResponseMessage(ErrorType.FRAUD_RULE_UPDATE_SUCCESS.getResponseMessage());
			}
		} else if (!adminFpOverrideCondition
				&& ruleCheckResponse.getResponseCode().equals(ErrorType.FRAUD_RULE_NOT_EXIST.getResponseCode())) {
			fraudPreventionMongoService.create(fraudPrevention);
			ruleCheckResponse.setResponseCode(ErrorType.FRAUD_RULE_SUCCESS.getResponseCode());
			ruleCheckResponse.setResponseMessage(ErrorType.FRAUD_RULE_SUCCESS.getResponseMessage());
		}

	}
	

	public void commonActionscurrency(FraudPreventionObj fraudPrevention, FraudRuleModel fraudRuleModel, User sessionUser)
			throws SystemException {

		String id = fraudPrevention.getId();
		// generating rule id
		if (StringUtils.isBlank(fraudPrevention.getId())) {
			fraudPrevention.setId(TransactionManager.getNewTransactionId());
		}
		boolean adminFpOverrideCondition = false;
		ruleCheckResponse = checkExistingFraudRule.existscurrency(fraudPrevention);
		logger.info(" after checking the existing fraud rules " + ruleCheckResponse);
		if (StringUtils.isBlank(fraudPrevention.getId())
				&& ruleCheckResponse.getResponseCode().equals(ErrorType.FRAUD_RULE_ALREADY_EXISTS.getResponseCode())
				&& !StringUtils.equalsIgnoreCase(fraudRuleModel.getFraudType(),
						FraudRuleType.FIRST_TRANSACTIONS_ALERT.getValue())) {
			throw new SystemException(ErrorType.FRAUD_RULE_ALREADY_EXISTS,
					ErrorType.FRAUD_RULE_ALREADY_EXISTS.getResponseMessage());
		}
		FraudPreventionObj dbfraudPrevention = fraudPreventionMongoService.getexistingFraudRule(fraudPrevention);
		if (null != dbfraudPrevention && (null != dbfraudPrevention.getCreatedBy())) {
			adminFpOverrideCondition = true;
			if (StringUtils.isNotBlank(dbfraudPrevention.getId())) {
				fraudPrevention.setId(dbfraudPrevention.getId());
			}
		} else {
			logger.info("FRAUD_RULE_NOT_EXIST");
		}

		fraudPrevention.setStatus(TDRStatus.ACTIVE);

		boolean alwaysOnFlag = fraudRuleModel.isAlwaysOnFlag();
		StringBuilder datefrom = new StringBuilder();
		StringBuilder dateTo = new StringBuilder();
		String formattedStartTime = "";
		String formattedEndTime = "";
		String dayCode = "";
		if (alwaysOnFlag == false && StringUtils.isNotBlank(fraudRuleModel.getDateActiveFrom())) {

			// Date from and date to format changed
			String dateActiveFrom = fraudRuleModel.getDateActiveFrom();
			String dateActiveTo = fraudRuleModel.getDateActiveTo();
			datefrom = datefrom.append(dateActiveFrom.substring(6)).append("").append(dateActiveFrom.substring(3, 5))
					.append("").append(dateActiveFrom.substring(0, 2));

			dateTo = dateTo.append(dateActiveTo.substring(6)).append("").append(dateActiveTo.substring(3, 5)).append("")
					.append(dateActiveTo.substring(0, 2));

			// Start time and end time format changes
			String startTime = fraudRuleModel.getStartTime();
			String endTime = fraudRuleModel.getEndTime();
			formattedStartTime = startTime.replaceAll(":", "");
			formattedEndTime = endTime.replaceAll(":", "");

			// weekdays format changed
			String days = fraudRuleModel.getRepeatDays();
			String[] details = days.split(",");
			StringBuilder dayCodes = new StringBuilder();

			for (String dayname : details) {
				Weekdays dayInstance = Weekdays.getInstanceIgnoreCase(dayname);
				dayCode = dayInstance.getCode();
				dayCodes.append(dayCode);
				dayCodes.append(",");
			}

			dayCode = dayCodes.toString().substring(0, dayCodes.length() - 1);

		} else {
			dayCode = "NA";
			formattedEndTime = "NA";
			formattedStartTime = "NA";
			dateTo.append("NA");
			datefrom.append("NA");

		}

		fraudPrevention.setDateActiveFrom(datefrom.toString());
		fraudPrevention.setDateActiveTo(dateTo.toString());
		fraudPrevention.setStartTime(formattedStartTime);
		fraudPrevention.setEndTime(formattedEndTime);
		fraudPrevention.setRepeatDays(dayCode);
		fraudPrevention.setAlwaysOnFlag(fraudRuleModel.isAlwaysOnFlag());

		if (adminFpOverrideCondition) {
			fraudPrevention.setUpdatedBy(sessionUser.getEmailId().toString());
			fraudPreventionMongoService.updateIndividaulMerchant(fraudPrevention);
			if (StringUtils.isBlank(id)) {
				ruleCheckResponse.setResponseCode(ErrorType.FRAUD_RULE_SUCCESS.getResponseCode());
				ruleCheckResponse.setResponseMessage(ErrorType.FRAUD_RULE_SUCCESS.getResponseMessage());
			} else {
				ruleCheckResponse.setResponseCode(ErrorType.FRAUD_RULE_UPDATE_SUCCESS.getResponseCode());
				ruleCheckResponse.setResponseMessage(ErrorType.FRAUD_RULE_UPDATE_SUCCESS.getResponseMessage());
			}
		} else if (!adminFpOverrideCondition
				&& ruleCheckResponse.getResponseCode().equals(ErrorType.FRAUD_RULE_NOT_EXIST.getResponseCode())) {
			fraudPreventionMongoService.create(fraudPrevention);
			ruleCheckResponse.setResponseCode(ErrorType.FRAUD_RULE_SUCCESS.getResponseCode());
			ruleCheckResponse.setResponseMessage(ErrorType.FRAUD_RULE_SUCCESS.getResponseMessage());
		}

	}
}