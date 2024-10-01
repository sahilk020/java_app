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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.pay10.commons.audittrail.ActionMessageByAction;
import com.pay10.commons.audittrail.AuditTrail;
import com.pay10.commons.audittrail.AuditTrailService;
import com.pay10.commons.dao.RouterConfigurationDao;
import com.pay10.commons.dao.RouterRuleDao;
import com.pay10.commons.user.AccountCurrencyRegion;
import com.pay10.commons.user.CardHolderType;
import com.pay10.commons.user.RouterConfiguration;
import com.pay10.commons.user.RouterRule;
import com.pay10.commons.user.SurchargeDao;
import com.pay10.commons.user.User;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.MopType;
import com.pay10.commons.util.PaymentType;
import com.pay10.commons.util.PropertiesManager;
import com.pay10.commons.util.TDRStatus;

/**
 * @author Rahul,Shaiwal
 *
 */
public class RouterConfigurationEditAction extends AbstractSecureAction {

	private static final long serialVersionUID = -127596067586594948L;
	private static Logger logger = LoggerFactory.getLogger(RouterConfigurationEditAction.class.getName());

	@Autowired
	private RouterRuleDao routerRuleDao;

	@Autowired
	private RouterConfigurationDao routerConfigurationDao;

	@Autowired
	private SurchargeDao surchargeDao;

	@Autowired
	private AuditTrailService auditTrailService;

	private List<RouterRule> listData;
	private String response;

	private String routerConfig;
	private String identifier;
	private String mode;
	private User sessionUser = new User();

	@Override
	public String execute() {
		sessionUser = (User) sessionMap.get(Constants.USER.getValue());
		try {

			sessionUser = (User) sessionMap.get(Constants.USER.getValue());
			logger.info("Inside RouterConfigurationEditAction execute() ");
			String identifierArray[] = getIdentifier().split("-");

			String paymentType = identifierArray[0];
			String mopType = identifierArray[1];
			String payId = identifierArray[2];
			String transactionType = identifierArray[3];
			String currency = identifierArray[4];
			String paymentsRegion = identifierArray[5];
			String cardHolderType = identifierArray[6];
			String slabId = identifierArray[7];

			AccountCurrencyRegion acr;

			if (paymentsRegion.equalsIgnoreCase(AccountCurrencyRegion.DOMESTIC.toString())) {
				acr = AccountCurrencyRegion.DOMESTIC;
			} else {
				acr = AccountCurrencyRegion.INTERNATIONAL;
			}

			CardHolderType act;

			if (cardHolderType.equalsIgnoreCase(CardHolderType.COMMERCIAL.toString())) {
				act = CardHolderType.COMMERCIAL;
			} else {
				act = CardHolderType.CONSUMER;
			}

			RouterRule routerRule = routerRuleDao.findRuleByFieldsByPayId(payId, paymentType, mopType, currency,
					transactionType, paymentsRegion, cardHolderType);
			String updatedIdentifier = payId + currency + paymentType + mopType + transactionType + paymentsRegion
					+ cardHolderType + slabId;

			List<RouterConfiguration> prevConfigs = deleteRouterRuleConfiguration(updatedIdentifier);
			String prev = mapper.writeValueAsString(prevConfigs);
			if (getMode().equalsIgnoreCase("AUTO")) {
				addRouterRuleConfiguration(routerRule, slabId, prev);
				return SUCCESS;
			}

			String routerConfigSet[] = getRouterConfig().split(";");
			List<RouterConfiguration> routerConfigurationList = new ArrayList<RouterConfiguration>();
			for (String routerConfigBlock : routerConfigSet) {
				
				

				String routerConfigBlockSet[] = routerConfigBlock.split(",");

				String acquirer = routerConfigBlockSet[0];
				String status = routerConfigBlockSet[1];
				String paymentTypeName = routerConfigBlockSet[4];
				String mopTypeName = routerConfigBlockSet[5];
				String allowedFailureCount = routerConfigBlockSet[6];
				String alwaysOn = routerConfigBlockSet[7];
				String loadPercentage = routerConfigBlockSet[8];
				String priority = routerConfigBlockSet[9];
				String retryMinutes = routerConfigBlockSet[10];
				String minAmount = routerConfigBlockSet[11];
				String maxAmount = routerConfigBlockSet[12];
				String totalTxn = routerConfigBlockSet[13];
				String failedCount = routerConfigBlockSet[14];
				String routerType=routerConfigBlockSet[17];
				String routerMixAmount=routerConfigBlockSet[15];
				String routerMinAmount=routerConfigBlockSet[16];
				String vpaCount=routerConfigBlockSet[18];



				String paymentTypeCode = PaymentType.getInstance(paymentTypeName).getCode();
				String mopTypeCode = MopType.getInstance(mopTypeName).getCode();

				boolean isCurrentlyActive = false;
				boolean isAlwaysOn = false;

				if (status.equals("true")) {

					isCurrentlyActive = true;
					logger.info("Acquire currently active in manual mode == " + acquirer);
				}

				if (alwaysOn.equals("true")) {

					isAlwaysOn = true;
				}

				RouterConfiguration routerConfigurationToSave = new RouterConfiguration();

				Date date = new Date();

				routerConfigurationToSave.setIdentifier(updatedIdentifier);
				routerConfigurationToSave.setAcquirer(acquirer);
				routerConfigurationToSave.setCurrency(currency);
				routerConfigurationToSave.setPaymentType(paymentTypeCode);
				routerConfigurationToSave.setMopType(mopTypeCode);
				routerConfigurationToSave.setTransactionType(transactionType);
				routerConfigurationToSave.setMode(getMode());
				routerConfigurationToSave.setStatus(TDRStatus.ACTIVE);
				routerConfigurationToSave.setRequestedBy(sessionUser.getEmailId());
				routerConfigurationToSave.setUpdatedBy(sessionUser.getEmailId());
				routerConfigurationToSave.setAllowedFailureCount(Integer.valueOf(allowedFailureCount));
				routerConfigurationToSave.setCreatedDate(date);
				routerConfigurationToSave.setUpdatedDate(date);
				routerConfigurationToSave.setMerchant(payId);
				routerConfigurationToSave.setOnUsoffUs(false);
				routerConfigurationToSave.setCurrentlyActive(isCurrentlyActive);
				routerConfigurationToSave.setLoadPercentage(Integer.valueOf(loadPercentage));
				routerConfigurationToSave.setPriority(priority);
				routerConfigurationToSave.setAlwaysOn(isAlwaysOn);
				routerConfigurationToSave.setRetryMinutes(retryMinutes);
				routerConfigurationToSave.setAllowedFailureCount(5);
				routerConfigurationToSave.setPaymentsRegion(acr);
				routerConfigurationToSave.setCardHolderType(act);
				routerConfigurationToSave.setMinAmount(Double.valueOf(minAmount));
				routerConfigurationToSave.setMaxAmount(Double.valueOf(maxAmount));
				routerConfigurationToSave.setSlabId(slabId);
				routerConfigurationToSave.setTotalTxn(Long.parseLong(totalTxn));
				routerConfigurationToSave.setRoutingType(routerType);
				routerConfigurationToSave.setFailedCount(Integer.parseInt(failedCount));
				routerConfigurationToSave.setRouterMinAmount(routerMinAmount);
	routerConfigurationToSave.setRouterMixAmount(routerMixAmount);
	routerConfigurationToSave.setVpaCount(vpaCount);
				routerConfigurationDao.create(routerConfigurationToSave);
				routerConfigurationList.add(routerConfigurationToSave);
				
			}
			addAuditTrailDetails(mapper.writeValueAsString(routerConfigurationList), prev);
		} catch (Exception e) {

			logger.error("Unable to start auto mode in smart router " + e);
			return ERROR;
		}

		return SUCCESS;
	}

	@SuppressWarnings("unchecked")
	private void addAuditTrailDetails(String payload, String prevValue) throws JsonProcessingException {
		Map<String, ActionMessageByAction> actionMessagesByAction = (Map<String, ActionMessageByAction>) sessionMap
				.get(Constants.ACTION_MESSAGE_BY_ACTION.getValue());
		AuditTrail auditTrail = new AuditTrail(payload, prevValue,
				actionMessagesByAction.get("editRouterConfiguration"));
		auditTrailService.saveAudit(request, auditTrail);
	}

	public List<RouterConfiguration> deleteRouterRuleConfiguration(String identifier) {

		List<RouterConfiguration> routerConfigurationList = new ArrayList<RouterConfiguration>();
		try {
			logger.info("Remove existing  RouterConfiguration");
			routerConfigurationList = routerConfigurationDao.findRulesByIdentifier(identifier);

			List<RouterConfiguration> routerConfigurationPendingList = new ArrayList<RouterConfiguration>();
			routerConfigurationPendingList = routerConfigurationDao.findPendingRulesByIdentifier(identifier);

			if (routerConfigurationList.size() > 0) {

				for (RouterConfiguration routerConfiguration : routerConfigurationList) {

					routerConfigurationDao.delete(routerConfiguration.getId(), sessionUser);
				}

			}

			if (routerConfigurationPendingList.size() > 0) {

				for (RouterConfiguration routerConfiguration : routerConfigurationPendingList) {

					routerConfigurationDao.deletePending(routerConfiguration.getId(), sessionUser);
				}

			}

		}

		catch (Exception e) {
			logger.error("Exception occured in RouterRuleEditAction , cannot delete RouterRuleConfiguration   " + e);
		}
		return routerConfigurationList;
	}

	public void addRouterRuleConfiguration(RouterRule routerRule, String slabId, String prev) {

		List<RouterConfiguration> routerConfigurationList = new ArrayList<RouterConfiguration>();

		try {

			String[] acquirerMap = routerRule.getAcquirerMap().split(",");
			String identifier = routerRule.getMerchant() + routerRule.getCurrency() + routerRule.getPaymentType()
					+ routerRule.getMopType() + routerRule.getTransactionType() + routerRule.getPaymentsRegion()
					+ routerRule.getCardHolderType() + slabId;

			String slabAmountArrayString = PropertiesManager.propertiesMap
					.get(Constants.SWITCH_ACQUIRER_AMOUNT.getValue());

			// If no slab is set for this merchant , create a default slab with 00 as ID and
			// limit from 0.01 to 5000000.00
			if (!slabAmountArrayString.contains(routerRule.getMerchant())) {
				slabAmountArrayString = "00-0.01-5000000.00-" + routerRule.getMerchant() + "-ALL";
			}

			String[] slabAmountArray = slabAmountArrayString.split(",");

			String minAmount = "";
			String maxAmount = "";

			for (String currentSlab : slabAmountArray) {

				if (!currentSlab.contains(routerRule.getMerchant())) {
					continue;
				}

				String[] slabSplit = currentSlab.split("-");
				String paymentType = "ALL";
				String[] slabArray = currentSlab.split("-");

				if (!StringUtils.isBlank(slabSplit[4])) {
					paymentType = slabSplit[4];
				}

				if (!paymentType.equalsIgnoreCase(routerRule.getPaymentType())) {

					slabId = "00";
					minAmount = "0.01";
					maxAmount = "5000000.00";

				}

				if (slabId.equalsIgnoreCase(slabArray[0])) {

					minAmount = slabArray[1];
					maxAmount = slabArray[2];
				} else {
					continue;
				}
			}

			for (String acquirerString : acquirerMap) {

				String[] acquirerMapString = acquirerString.split("-");

				RouterConfiguration routerConfiguration = new RouterConfiguration();
				Date date = new Date();

				routerConfiguration.setIdentifier(identifier);
				routerConfiguration.setAcquirer(acquirerMapString[1]);
				routerConfiguration.setCurrency(routerRule.getCurrency());
				routerConfiguration.setPaymentType(routerRule.getPaymentType());
				routerConfiguration.setMopType(routerRule.getMopType());
				routerConfiguration.setTransactionType(routerRule.getTransactionType());
				routerConfiguration.setMode(getMode());
				routerConfiguration.setStatus(TDRStatus.ACTIVE);
				routerConfiguration.setAllowedFailureCount(5);
				routerConfiguration.setCreatedDate(date);
				routerConfiguration.setUpdatedDate(date);
				routerConfiguration.setMerchant(routerRule.getMerchant());
				routerConfiguration.setOnUsoffUs(routerRule.isOnUsFlag());
				routerConfiguration.setRetryMinutes("10");
				routerConfiguration.setRulePriority(acquirerMapString[0]);
				routerConfiguration.setFailureCount(0);
				routerConfiguration.setPaymentsRegion(routerRule.getPaymentsRegion());
				routerConfiguration.setCardHolderType(routerRule.getCardHolderType());
				routerConfiguration.setMinAmount(Double.valueOf(minAmount));
				routerConfiguration.setMaxAmount(Double.valueOf(maxAmount));
				routerConfiguration.setSlabId(slabId);
				routerConfiguration.setTotalTxn(Long.parseLong("5000000"));
				routerConfiguration.setFailedCount(Integer.parseInt("51"));
				routerConfiguration.setRequestedBy(sessionUser.getEmailId());
				String surcharge = surchargeDao.findDetailsByRouterConfiguration(routerConfiguration);

				if (surcharge.equalsIgnoreCase("NA")) {
					continue;
				}
				routerConfiguration.setSurcharge(surcharge);
				routerConfigurationList.add(routerConfiguration);

			}

			if (routerRule.getMerchant().equalsIgnoreCase("ALL MERCHANTS")) {

				int count = 1;

				for (RouterConfiguration routerConfiguration : routerConfigurationList) {

					RouterConfiguration routerConfigurationToSave = new RouterConfiguration();
					routerConfigurationToSave = routerConfiguration;

					if (count == 1) {
						routerConfigurationToSave.setCurrentlyActive(true);
						routerConfigurationToSave.setLoadPercentage(100);

						logger.info("Inside RouterConfigurationEditAction , currently active acquirer is  "
								+ routerConfigurationToSave.getAcquirer());
					}

					else {
						routerConfigurationToSave.setCurrentlyActive(false);
						routerConfigurationToSave.setLoadPercentage(0);
					}

					routerConfigurationToSave.setPriority(String.valueOf(count));
					routerConfiguration.setTotalTxn(5000000);
					routerConfiguration.setFailedCount(51);
					routerConfigurationDao.create(routerConfigurationToSave);
					count++;
				}

			}

			else {

				Comparator<RouterConfiguration> comp = (RouterConfiguration a, RouterConfiguration b) -> {

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

				for (RouterConfiguration entry : routerConfigurationList) {

					RouterConfiguration routerConfigurationToSave = new RouterConfiguration();
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
					
					routerConfigurationToSave.setTotalTxn(5000000);
					routerConfigurationToSave.setFailedCount(51);
					routerConfigurationDao.create(routerConfigurationToSave);
					count++;
				}
				addAuditTrailDetails(mapper.writeValueAsString(routerConfigurationList), prev);
			}

		} catch (Exception e) {
			logger.error("Error occured wile adding Router Configuration " + e);
		}

	}

	public List<RouterRule> getListData() {
		return listData;
	}

	public void setListData(List<RouterRule> listData) {
		this.listData = listData;
	}

	public String getResponse() {
		return response;
	}

	public void setResponse(String response) {
		this.response = response;
	}

	public String getRouterConfig() {
		return routerConfig;
	}

	public void setRouterConfig(String routerConfig) {
		this.routerConfig = routerConfig;
	}

	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}

}
