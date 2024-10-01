package com.pay10.commons.util;

import java.math.BigDecimal;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pay10.commons.dao.RouterConfigurationDao;
import com.pay10.commons.dao.ServiceTaxDao;
import com.pay10.commons.user.AccountCurrencyRegion;
import com.pay10.commons.user.ChargingDetails;
import com.pay10.commons.user.ChargingDetailsDao;
import com.pay10.commons.user.RouterConfiguration;
import com.pay10.commons.user.ServiceTax;
import com.pay10.commons.user.Surcharge;
import com.pay10.commons.user.SurchargeDao;
import com.pay10.commons.user.SurchargeDetails;
import com.pay10.commons.user.SurchargeDetailsDao;
import com.pay10.commons.user.User;
import com.pay10.commons.user.UserDao;

/**
 * @author Shaiwal
 *
 */

@Service
public class StaticDataProvider {

	@Autowired
	private UserDao userDao;

	@Autowired
	private ServiceTaxDao serviceTaxDao;

	@Autowired
	private SurchargeDao surchargeDao;

	@Autowired
	private SurchargeDetailsDao surchargeDetailsDao;

	@Autowired
	private ChargingDetailsDao chargingDetailsDao;

	@Autowired
	private RouterConfigurationDao routerConfigurationDao;

	@Autowired
	private PropertiesManager propertiesManager;

	private static Map<String, User> userMap = new ConcurrentHashMap<String, User>();
	private static Map<String, SurchargeDetails> surchargeDetailsMap = new ConcurrentHashMap<String, SurchargeDetails>();
	private static Map<String, List<Surcharge>> surchargeMap = new ConcurrentHashMap<String, List<Surcharge>>();
	private static Map<String, ChargingDetails> chargingDetailsMap = new ConcurrentHashMap<String, ChargingDetails>();
	private static Map<String, List<RouterConfiguration>> routerConfigurationMap = new ConcurrentHashMap<String, List<RouterConfiguration>>();
	private static Map<String, BigDecimal> serviceTaxMap = new ConcurrentHashMap<String, BigDecimal>();
	private static Map<String, List<ChargingDetails>> chargingDetailsListMap = new ConcurrentHashMap<String, List<ChargingDetails>>();

	private static Logger logger = LoggerFactory.getLogger(StaticDataProvider.class.getName());
	private static Instant checkedTimestamp = Instant.now();


	public User getUserData(String payId) {
		if (PropertiesManager.propertiesMap.get("useStaticData") != null
				&& PropertiesManager.propertiesMap.get("useStaticData").equalsIgnoreCase("Y")) {

			// evict user cached data by payId, after evicted, refresh checked timestamp to current timestamp
			Instant currentTimestamp = Instant.now();
			if (Duration.between(checkedTimestamp, currentTimestamp).toMinutes() >= 5) {
				logger.info("remove User cached:{}, checkedTimestamp:{}, currentTimestamp:{}", payId, checkedTimestamp, currentTimestamp);
                userMap.remove(payId);
				checkedTimestamp = currentTimestamp;
			}

			if (userMap.get(payId) != null) {

				return userMap.get(payId);

			} else {
				User user = userDao.findPayId(payId);
				if (user != null) {
					userMap.put(payId, user);
					return user;
				} else {
					return null;
				}

			}

		} else {
			return userDao.findPayId(payId);
		}

	}

	public void setUserData(User user) {

		userMap.put(user.getPayId(), user);
	}

	public BigDecimal getServiceTaxData(String industryCategory) {

		if (propertiesManager.propertiesMap.get("useStaticData") != null
				&& propertiesManager.propertiesMap.get("useStaticData").equalsIgnoreCase("Y")) {

			if (serviceTaxMap.get(industryCategory) != null) {
				return serviceTaxMap.get(industryCategory);
			} else {
				ServiceTax servicetax = serviceTaxDao.findServiceTax(industryCategory);
				if (servicetax != null) {
					serviceTaxMap.put(industryCategory, servicetax.getServiceTax());
					return servicetax.getServiceTax();
				} else {
					return null;
				}

			}
		} else {
			return serviceTaxDao.findServiceTax(industryCategory).getServiceTax();
		}

	}

	public List<Surcharge> getSurchargeData(String payId, String paymentType, String acquirer, String mopType,
			boolean allowOnOff, String paymentsRegion) {

		if (propertiesManager.propertiesMap.get("useStaticData") != null
				&& propertiesManager.propertiesMap.get("useStaticData").equalsIgnoreCase("Y")) {

			String identifier = payId + "&" + paymentType + "&" + acquirer + "&" + mopType + "&" + allowOnOff + "&"
					+ paymentsRegion;

			if (surchargeMap.get(identifier) != null) {
				return surchargeMap.get(identifier);

			} else {

				AccountCurrencyRegion acr = null;

				if (paymentsRegion.equalsIgnoreCase(AccountCurrencyRegion.DOMESTIC.toString())) {

					acr = AccountCurrencyRegion.DOMESTIC;
				} else {
					acr = AccountCurrencyRegion.INTERNATIONAL;
				}

				List<Surcharge> surchargeList = surchargeDao.findSurchargeForTxn(payId, paymentType, acquirer, mopType,
						allowOnOff, acr);

				if (surchargeList != null && surchargeList.size() > 0) {
					surchargeMap.put(identifier, surchargeList);
					return surchargeList;
				} else {
					return null;
				}

			}
		}

		else {

			AccountCurrencyRegion acr = null;

			if (paymentsRegion.equalsIgnoreCase(AccountCurrencyRegion.DOMESTIC.toString())) {

				acr = AccountCurrencyRegion.DOMESTIC;
			} else {
				acr = AccountCurrencyRegion.INTERNATIONAL;
			}

			return surchargeDao.findSurchargeForTxn(payId, paymentType, acquirer, mopType, allowOnOff, acr);
		}

	}

	public SurchargeDetails getSurchargeDetailsData(String payId, String paymentTypeCode, String paymentsRegion) {

		if (propertiesManager.propertiesMap.get("useStaticData") != null
				&& propertiesManager.propertiesMap.get("useStaticData").equalsIgnoreCase("Y")) {

			String identifier = payId + "&" + paymentTypeCode + "&" + paymentsRegion;

			if (surchargeDetailsMap.get(identifier) != null) {
				return surchargeDetailsMap.get(identifier);

			} else {
				SurchargeDetails surchargeDetails = surchargeDetailsDao.findDetailsByRegion(payId, paymentTypeCode,
						paymentsRegion);
				if (surchargeDetails != null) {
					surchargeDetailsMap.put(identifier, surchargeDetails);
					return surchargeDetails;
				} else {
					return null;
				}

			}

		}

		else {

			return surchargeDetailsDao.findDetailsByRegion(payId, paymentTypeCode, paymentsRegion);

		}

	}

	public ChargingDetails getChargingDetailsData(String payId, String paymentType, String acquirer, String mopType,
			String transactionType, String currency) {

		if (propertiesManager.propertiesMap.get("useStaticData") != null
				&& propertiesManager.propertiesMap.get("useStaticData").equalsIgnoreCase("Y")) {

			String identifier = payId + "&" + paymentType + "&" + acquirer + "&" + mopType + "&" + transactionType + "&"
					+ currency;
			if (chargingDetailsMap.get(identifier) != null) {

				return chargingDetailsMap.get(identifier);

			} else {

				PaymentType paymentTypeIns = PaymentType.getInstanceUsingCode(paymentType);
				MopType mopTypeIns = MopType.getmop(mopType);
				TransactionType transactionTypeIns = TransactionType.getInstanceFromCode(transactionType);
				String acquirerName = AcquirerType.getAcquirerName(acquirer);

				ChargingDetails chargingDetails = chargingDetailsDao.findActiveChargingDetail(mopTypeIns,
						paymentTypeIns, transactionTypeIns, acquirerName, currency, payId);
				if (chargingDetails != null) {
					chargingDetailsMap.put(identifier, chargingDetails);
					return chargingDetails;
				} else {
					return null;
				}

			}
		}

		else {

			PaymentType paymentTypeIns = PaymentType.getInstanceUsingCode(paymentType);
			MopType mopTypeIns = MopType.getmop(mopType);
			TransactionType transactionTypeIns = TransactionType.getInstanceFromCode(transactionType);
			String acquirerName = AcquirerType.getAcquirerName(acquirer);

			return chargingDetailsDao.findActiveChargingDetail(mopTypeIns, paymentTypeIns, transactionTypeIns,
					acquirerName, currency, payId);
		}

	}

	public List<RouterConfiguration> getRouterConfigData(String identifier) {

		if (propertiesManager.propertiesMap.get("useStaticData") != null
				&& propertiesManager.propertiesMap.get("useStaticData").equalsIgnoreCase("Y")) {

			if (routerConfigurationMap.get(identifier) != null) {

				return routerConfigurationMap.get(identifier);

			} else {
				List<RouterConfiguration> rulesList = new ArrayList<RouterConfiguration>();
				rulesList = routerConfigurationDao.findActiveAcquirersByIdentifier(identifier);

				if (rulesList != null && rulesList.size() > 0) {
					routerConfigurationMap.put(identifier, rulesList);
					return rulesList;
				} else {
					return null;
				}

			}
		}

		else {
			return routerConfigurationDao.findActiveAcquirersByIdentifier(identifier);
		}

	}

	public List<ChargingDetails> getChargingDetailsList(String payId) {

		if (propertiesManager.propertiesMap.get("useStaticData") != null
				&& propertiesManager.propertiesMap.get("useStaticData").equalsIgnoreCase("Y")) {

			List<ChargingDetails> chargingDetailsList = new ArrayList<ChargingDetails>();
			if (chargingDetailsListMap.get(payId) != null) {

				return chargingDetailsListMap.get(payId);

			} else {
				
				chargingDetailsList = chargingDetailsDao.getAllActiveChargingDetails(payId);

				if (chargingDetailsList != null && chargingDetailsList.size() > 0) {
					chargingDetailsListMap.put(payId, chargingDetailsList);
					return chargingDetailsList;
				} else {
					return chargingDetailsList;
				}

			}
		}

		else {
			List<ChargingDetails> chargingDetailsList = new ArrayList<ChargingDetails>();
			chargingDetailsList = chargingDetailsDao.getAllActiveChargingDetails(payId);
			return chargingDetailsList;
		}

	}

	public static void updateMapValues() {

		try {

			UserDao userDao = new UserDao();
			SurchargeDao surchargeDao = new SurchargeDao();
			SurchargeDetailsDao surchargeDetailsDao = new SurchargeDetailsDao();
			ChargingDetailsDao chargingDetailsDao = new ChargingDetailsDao();
			ServiceTaxDao serviceTaxDao = new ServiceTaxDao();
			RouterConfigurationDao routerConfigurationDao = new RouterConfigurationDao();
			logger.info("Updating user map");
			for (String key : userMap.keySet()) {
				User user = userDao.findPayId(key);
				if (user != null) {
					userMap.put(key, user);
				//	logger.info("userMap updated for payId == " + key);
				} else {
				//	logger.info("User not found for identifier , removing from map " + key);
					userMap.remove(key);
				}
			}

			//logger.info("Updating Surcharge map");
			for (String key : surchargeMap.keySet()) {

				String keyArray[] = key.split("&");

				String payId = keyArray[0];
				String paymentType = keyArray[1];
				String acquirer = keyArray[2];
				String mopType = keyArray[3];
				String allowOnOff = keyArray[4];
				String paymentsRegion = keyArray[5];

				AccountCurrencyRegion acr = null;

				if (paymentsRegion.equalsIgnoreCase(AccountCurrencyRegion.DOMESTIC.toString())) {

					acr = AccountCurrencyRegion.DOMESTIC;
				} else {
					acr = AccountCurrencyRegion.INTERNATIONAL;
				}

				boolean onOff = false;

				if (allowOnOff.equalsIgnoreCase("true")) {
					onOff = true;
				} else {
					onOff = false;
				}

				List<Surcharge> surchargelist = surchargeDao.findSurchargeForTxn(payId, paymentType, acquirer, mopType,
						onOff, acr);

				if (surchargelist != null && surchargelist.size() > 0) {
					surchargeMap.put(key, surchargelist);
			//		logger.info("surchargeMap updated for identifier == " + key);
				}

				else {
			//		logger.info("Surcharge not found for identifier , removing from map " + key);
					surchargeMap.remove(key);
				}

			}

			logger.info("Updating Surcharge Details map");
			for (String key : surchargeDetailsMap.keySet()) {

				String keyArray[] = key.split("&");
				String payId = keyArray[0];
				String paymentType = keyArray[1];
				String paymentsRegion = keyArray[2];

				SurchargeDetails surchargeDetails = surchargeDetailsDao.findDetailsByRegion(payId, paymentType,
						paymentsRegion);

				if (surchargeDetails != null) {
					surchargeDetailsMap.put(key, surchargeDetails);
				//	logger.info("surchargeDetailsMap updated for identifier == " + key);

				} else {
				//	logger.info("SurchargeDetails not found for identifier , removing from map " + key);
					surchargeDetailsMap.remove(key);
				}

			}

		//	logger.info("Updating ChargingDetails map");
			for (String key : chargingDetailsMap.keySet()) {

				String keyArray[] = key.split("&");
				String payId = keyArray[0];
				String paymentType = keyArray[1];
				String acquirer = keyArray[2];
				String mopType = keyArray[3];
				String transactionType = keyArray[4];
				String currency = keyArray[5];

				PaymentType paymentTypeIns = PaymentType.getInstanceUsingCode(paymentType);
				MopType mopTypeIns = MopType.getmop(mopType);
				TransactionType transactionTypeIns = TransactionType.getInstanceFromCode(transactionType);
				String acquirerName = AcquirerType.getAcquirerName(acquirer);

				if (paymentTypeIns != null && mopTypeIns != null && transactionTypeIns != null
						&& StringUtils.isNotBlank(acquirerName)) {
					ChargingDetails chargingDetails = chargingDetailsDao.findActiveChargingDetail(mopTypeIns,
							paymentTypeIns, transactionTypeIns, acquirerName, currency, payId);

					if (chargingDetails != null) {
						chargingDetailsMap.put(key, chargingDetails);
				//		logger.info("chargingDetailsMap updated for identifier == " + key);
					} else {
				//		logger.info("Charging details not found for identifier , removing from map " + key);
						chargingDetailsMap.remove(key);
					}

				}
			}

		//	logger.info("Updating Service Tax map");
			for (String key : serviceTaxMap.keySet()) {

				ServiceTax servicetax = serviceTaxDao.findServiceTax(key);
				if (servicetax != null) {
					serviceTaxMap.put(key, servicetax.getServiceTax());
					logger.info("serviceTaxMap updated for identifier == " + key);
				} else {
					logger.info("servicetax  not found for identifier , removing from map " + key);
					serviceTaxMap.remove(key);

				}

			}

		//	logger.info("Updating Router Configuration map");
			for (String key : routerConfigurationMap.keySet()) {

				List<RouterConfiguration> rulesList = null;
				rulesList = routerConfigurationDao.findActiveAcquirersByIdentifier(key);

				if (rulesList != null && rulesList.size() > 0) {
					routerConfigurationMap.put(key, rulesList);
					logger.info("RouterConfigurationMap updated for identifier == " + key);
				} else {
					logger.info("RouterConfiguration not found for identifier , removing from map " + key);
					routerConfigurationMap.remove(key);
				}
			}
		//	logger.info("Static Map Provider updated successfully");

		//	logger.info("Updating charging details list map");
			for (String key : chargingDetailsListMap.keySet()) {

				List<ChargingDetails> chargingDetailsList = new ArrayList<ChargingDetails>();
				chargingDetailsList = chargingDetailsDao.getAllActiveChargingDetails(key);

				if (chargingDetailsList != null && chargingDetailsList.size() > 0) {
					chargingDetailsListMap.put(key, chargingDetailsList);
					logger.info("chargingDetailsListMap updated for payId == " + key);
				} else {
					logger.info("No charging details found for payId , removing from map , payId == " + key);
					chargingDetailsListMap.remove(key);
				}
			}

		//	logger.info("Static Map Provider updated successfully");

		} catch (Exception e) {
			logger.error("Exception in updating static map provider", e);
		}

	}

}
