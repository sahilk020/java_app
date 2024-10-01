package com.pay10.crm.action;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.pay10.commons.audittrail.ActionMessageByAction;
import com.pay10.commons.audittrail.AuditTrail;
import com.pay10.commons.audittrail.AuditTrailService;
import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.user.RouterConfigurationPayout;
import com.pay10.commons.user.RouterConfigurationPayoutDao;
import com.pay10.commons.user.RouterRuleDaoPayout;
import com.pay10.commons.user.RouterRulePayout;
import com.pay10.commons.user.SurchargeDao;
import com.pay10.commons.user.User;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.Currency;
import com.pay10.commons.util.PropertiesManager;
import com.pay10.commons.util.TDRStatus;

/**
 * @author Rahul
 *
 */
public class RouterRuleEditPayoutAction extends AbstractSecureAction {

	private static final long serialVersionUID = -127596067586594948L;
	private static Logger logger = LoggerFactory.getLogger(RouterRuleEditPayoutAction.class.getName());

	@Autowired
	private RouterRuleDaoPayout routerRuleDao;

	@Autowired
	private RouterConfigurationPayoutDao routerConfigurationDao;

	@Autowired
	private SurchargeDao surchargeDao;

	@Autowired
	private AuditTrailService auditTrailService;

	private List<RouterRulePayout> listData;
	private String token;
	private String response;
	private User sessionUser = new User();

	@SuppressWarnings("unchecked")
	public String editRules() {
		sessionUser = (User) sessionMap.get(Constants.USER.getValue());
		try {
			
			String sessionToken = (String) sessionMap.get("customToken");
			if (!StringUtils.equalsIgnoreCase(getToken(), sessionToken)) {
				return "invalid.token";
			}

			Date currentDate = new Date();
			String prevRules = mapper
					.writeValueAsString(routerRuleDao.findAllRuleByMerchant(listData.get(0).getPayId()));
			for (RouterRulePayout routerRule : listData) {
				String payId = routerRule.getPayId();

				long id = routerRule.getId();

				if (Long.valueOf(id) != null) {

					// Set current active rule as inactive
					routerRuleDao.delete(routerRule, sessionUser.getEmailId());

					// Set any pending rule as inactive
					RouterRulePayout pendingRouterRule = routerRuleDao.getPendingMatchingRule(routerRule);
					if (pendingRouterRule != null) {
						routerRuleDao.delete(pendingRouterRule, sessionUser.getEmailId());
					}

					// Delete all active and pending router configuration for this rule
					deleteRouterRuleConfiguration(routerRule);

					// Add new active router rule
					routerRule.setCreatedDate(currentDate);
					routerRule.setMerchant(routerRule.getPayId());
					routerRule.setCurrency(routerRule.getCurrency());
					routerRule.setMerchant(payId);
					routerRule.setChannel(routerRule.getChannel());
					routerRule.setStatus(TDRStatus.ACTIVE);
					routerRule.setRequestedBy(sessionUser.getEmailId());
					routerRuleDao.create(routerRule);

					// Add new active router configuration for this rule
					addRouterRuleConfiguration(routerRule);

					Map<String, ActionMessageByAction> actionMessagesByAction = (Map<String, ActionMessageByAction>) sessionMap
							.get(Constants.ACTION_MESSAGE_BY_ACTION.getValue());
					AuditTrail auditTrail = new AuditTrail(mapper.writeValueAsString(listData), prevRules,
							actionMessagesByAction.get("editRouterRule"));
					auditTrailService.saveAudit(request, auditTrail);
				}

				if (StringUtils.isEmpty(response)) {
					setResponse(ErrorType.ROUTER_RULE_UPDATED.getResponseMessage());
				}
			}

		} catch (Exception exception) {
			setResponse(ErrorType.INTERNAL_SYSTEM_ERROR.getResponseMessage());
		}
		return SUCCESS;
	}

	public void deleteRouterRuleConfiguration(RouterRulePayout routerRule) {

		try {

			logger.info("Remove existing  RouterConfiguration");
			Date date = new Date();

			List<RouterConfigurationPayout> routerConfigurationPendingList = new ArrayList<RouterConfigurationPayout>();
			routerConfigurationPendingList = routerConfigurationDao.getPendingRulesByRouterRule(routerRule);

			if (routerConfigurationPendingList.size() > 0) {

				for (RouterConfigurationPayout routerConfiguration : routerConfigurationPendingList) {
					routerConfiguration.setRequestedBy(sessionUser.getEmailId());
					routerConfiguration.setUpdatedBy(sessionUser.getEmailId());
					routerConfiguration.setUpdatedDate(date);
					routerConfigurationDao.delete(routerConfiguration);
				}

			}

			List<RouterConfigurationPayout> routerConfigurationList = new ArrayList<RouterConfigurationPayout>();
			routerConfigurationList = routerConfigurationDao.getActiveRulesByRouterRule(routerRule);

			if (routerConfigurationList.size() > 0) {

				for (RouterConfigurationPayout routerConfiguration : routerConfigurationList) {
					routerConfiguration.setRequestedBy(sessionUser.getEmailId());
					routerConfiguration.setUpdatedBy(sessionUser.getEmailId());
					routerConfiguration.setUpdatedDate(date);
					routerConfigurationDao.delete(routerConfiguration);
				}

			}

		}

		catch (Exception e) {
			logger.error("Exception occured in RouterRuleEditAction , cannot delete RouterRuleConfiguration   ", e);
		}

	}

	public void addRouterRuleConfiguration(RouterRulePayout routerRule) {
		User sessionUser = (User) sessionMap.get(Constants.USER.getValue());

		try {

			String[] acquirerMap = routerRule.getAcquirerMap().split(",");
			// Present in YML file
			String slabAmountArrayString = PropertiesManager.propertiesMap
					.get(Constants.SWITCH_ACQUIRER_AMOUNT.getValue());
			StringBuilder tempSlab = new StringBuilder();
			// If no slab is set for this merchant , create a default slab with 00 as ID and
			// limit from 0.01 to 5000000.00

			if (!slabAmountArrayString.contains(routerRule.getMerchant())) {
				slabAmountArrayString = "00-0.01-5000000.00-" + routerRule.getMerchant() + "-ALL";
			}

			// If any slab is found , check if slab is for this particular payment type
			else {

				String[] slabArray = slabAmountArrayString.split(",");
				for (String currentSlab : slabArray) {

					if (!currentSlab.contains(routerRule.getMerchant())) {
						continue;
					}

					String[] slabSplit = currentSlab.split("-");

					if (!StringUtils.isBlank(slabSplit[4])
							&& slabSplit[4].equalsIgnoreCase(routerRule.getChannel())) {

						tempSlab.append(currentSlab);
						tempSlab.append(",");
						continue;
					} else {
						continue;
					}
				}

			}

			if (StringUtils.isNotBlank(tempSlab.toString())) {
				slabAmountArrayString = tempSlab.toString();
			} else {
				slabAmountArrayString = "00-0.01-5000000.00-" + routerRule.getMerchant() + "-ALL";
			}

			String[] slabArray = slabAmountArrayString.split(",");

			for (String currentSlab : slabArray) {

				if (StringUtils.isBlank(currentSlab)) {
					continue;
				}

				if (!currentSlab.contains(routerRule.getMerchant())) {
					continue;
				}

				String[] slabSplit = currentSlab.split("-");
				String slabId = slabSplit[0];
				String minTransactionAmount = slabSplit[1];
				String maxTransactionAmount = slabSplit[2];
				String paymentType = "ALL";

				if (!StringUtils.isBlank(slabSplit[4])) {
					paymentType = slabSplit[4];
				}

				if (!paymentType.equalsIgnoreCase(routerRule.getChannel())) {

					slabId = "00";
					minTransactionAmount = "0.01";
					maxTransactionAmount = "5000000.00";

				}

				List<RouterConfigurationPayout> routerConfigurationList = new ArrayList<RouterConfigurationPayout>();

				for (String acquirerString : acquirerMap) {

					String identifier = routerRule.getMerchant() 
							+ routerRule.getChannel() + routerRule.getCurrency() + routerRule.getTransactionType()
							+ routerRule.getPaymentsRegion().toString() + routerRule.getCardHolderType().toString()
							+ slabId;

					String[] acquirerMapString = acquirerString.split("-");

					RouterConfigurationPayout routerConfiguration = new RouterConfigurationPayout();
					Date date = new Date();

					routerConfiguration.setIdentifier(identifier);
					routerConfiguration.setAcquirer(acquirerMapString[1]);
					routerConfiguration.setRulePriority(acquirerMapString[0]);
					routerConfiguration.setCurrency(routerRule.getCurrency());
					routerConfiguration.setChannel(routerRule.getChannel());
					routerConfiguration.setTransactionType(routerRule.getTransactionType());
					routerConfiguration.setMode("AUTO");
					routerConfiguration.setStatus(TDRStatus.ACTIVE);
					routerConfiguration.setAllowedFailureCount(5);
					routerConfiguration.setCreatedDate(date);
					routerConfiguration.setUpdatedDate(date);
					routerConfiguration.setMerchant(routerRule.getMerchant());
					routerConfiguration.setOnUsoffUs(routerRule.isOnUsFlag());
					routerConfiguration.setDown(false);
					routerConfiguration.setRequestedBy(sessionUser.getEmailId());
					routerConfiguration.setRetryMinutes("10");
					routerConfiguration.setFailureCount(0);
					routerConfiguration.setPaymentsRegion(routerRule.getPaymentsRegion());
					routerConfiguration.setCardHolderType(routerRule.getCardHolderType());
					routerConfiguration.setSlabId(slabId);
					routerConfiguration.setMinAmount(Double.valueOf(minTransactionAmount));
					routerConfiguration.setMaxAmount(Double.valueOf(maxTransactionAmount));


					
					routerConfigurationList.add(routerConfiguration);

				}

				Comparator<RouterConfigurationPayout> comp = (RouterConfigurationPayout a, RouterConfigurationPayout b) -> {

					if (Double.valueOf(b.getSurcharge()) > Double.valueOf(a.getSurcharge())) {
						return -1;
					} else if (Double.valueOf(b.getSurcharge()) < Double.valueOf(a.getSurcharge())) {
						return 1;
					} else {
						if (Double.valueOf(b.getRulePriority()) > Double.valueOf(a.getRulePriority())) {
							return -1;
						} else {
							return 1;
						}
					}
				};

				Collections.sort(routerConfigurationList, comp);

				int count = 1;

				for (RouterConfigurationPayout entry : routerConfigurationList) {

					RouterConfigurationPayout routerConfigurationToSave = new RouterConfigurationPayout();
					routerConfigurationToSave = entry;

					routerConfigurationToSave.setPriority(String.valueOf(count));
					if (count == 1) {
						routerConfigurationToSave.setLoadPercentage(100);
						routerConfigurationToSave.setCurrentlyActive(true);
					}

					else {
						routerConfigurationToSave.setLoadPercentage(0);
						routerConfigurationToSave.setCurrentlyActive(false);
					}
					routerConfigurationDao.create(routerConfigurationToSave);
					count++;
				}

			}

		}

		catch (Exception e) {

			logger.error("Error occured wile adding Router Configuration " + e.getMessage());
		}

	}

	public List<RouterRulePayout> getListData() {
		return listData;
	}

	public void setListData(List<RouterRulePayout> listData) {
		this.listData = listData;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getResponse() {
		return response;
	}

	public void setResponse(String response) {
		this.response = response;
	}

}
