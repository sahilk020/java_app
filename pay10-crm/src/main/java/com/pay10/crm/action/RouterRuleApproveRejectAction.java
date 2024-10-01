package com.pay10.crm.action;
/*package com.mmadpay.crm.action;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.mmadpay.commons.dao.HibernateSessionProvider;
import com.mmadpay.commons.dao.RouterConfigurationDao;
import com.mmadpay.commons.dao.RouterRuleDao;
import com.mmadpay.commons.exception.ErrorType;
import com.mmadpay.commons.user.RouterConfiguration;
import com.mmadpay.commons.user.RouterRule;
import com.mmadpay.commons.user.ServiceTax;
import com.mmadpay.commons.user.SurchargeDao;
import com.mmadpay.commons.user.User;
import com.mmadpay.commons.user.UserType;
import com.mmadpay.commons.util.Constants;
import com.mmadpay.commons.util.CrmFieldType;
import com.mmadpay.commons.util.CrmValidator;
import com.mmadpay.commons.util.Currency;
import com.mmadpay.commons.util.MopType;
import com.mmadpay.commons.util.PaymentType;
import com.mmadpay.commons.util.PendingRequestEmailProcessor;
import com.mmadpay.commons.util.PropertiesManager;
import com.mmadpay.commons.util.TDRStatus;

*//**
 * @author Shaiwal
 *
 *//*
public class RouterRuleApproveRejectAction extends AbstractSecureAction {

	@Autowired
	private RouterRuleDao routerRuleDao;

	@Autowired
	private PendingRequestEmailProcessor pendingRequestEmailProcessor;

	@Autowired
	private RouterConfigurationDao routerConfigurationDao;

	@Autowired
	private SurchargeDao surchargeDao;

	@Autowired
	private CrmValidator validator;

	private static Logger logger = LoggerFactory.getLogger(RouterRuleApproveRejectAction.class.getName());
	private static final long serialVersionUID = -6517340843571949786L;

	private String id;
	private String operation;
	private Date currentDate = new Date();
	private User sessionUser;

	public String execute() {
		sessionUser = (User) sessionMap.get(Constants.USER.getValue());
		StringBuilder permissions = new StringBuilder();
		permissions.append(sessionMap.get(Constants.USER_PERMISSION.getValue()));

		try {
			if (sessionUser.getUserType().equals(UserType.ADMIN)
					|| permissions.toString().contains("Rule Engine")) {

				RouterRule routerRuleToUpdate = new RouterRule();
				routerRuleToUpdate = routerRuleDao.findPendingRule(Long.valueOf(id));
				String requestedAction = routerRuleToUpdate.getRequestedAction();

				if (routerRuleToUpdate != null) {

					if (operation.equals("accept")) {

						if (requestedAction.equalsIgnoreCase("DELETE")) {
							deleteRules(Long.valueOf(id));
						}

						else if (requestedAction.equalsIgnoreCase("UPDATE")) {
							createEditRules(routerRuleToUpdate);
						}

						else if (requestedAction.equalsIgnoreCase("CREATE")) {

							createEditRules(routerRuleToUpdate);
						}

						// pendingRequestEmailProcessor.processServiceTaxApproveRejectEmail("Approved",
						// emailId, userType,
						// businessType, serviceTaxToUpdate.getRequestedBy());

					} else {
						rejectRouterRuleRequest(routerRuleToUpdate, TDRStatus.REJECTED);
						// pendingRequestEmailProcessor.processServiceTaxApproveRejectEmail("Rejected",
						// emailId, userType,
						// businessType, serviceTaxToUpdate.getRequestedBy());
					}

				}

			}

			else {
				addActionMessage("Operation Denied, contact Admin");
			}

		} catch (Exception e) {
			logger.error("Service tax operaion failed");
			return ERROR;
		}

		return SUCCESS;
	}

	public String createEditRules(RouterRule pendingRouterRule) {

		sessionUser = (User) sessionMap.get(Constants.USER.getValue());
		try {
			// Find active router rule and disable rule
			RouterRule activeRouterRule = routerRuleDao.getMatchingRule(pendingRouterRule);
			
			if (activeRouterRule != null) {
				routerRuleDao.delete(activeRouterRule, sessionUser.getEmailId());
			}
			
			
			// Set status as active for pending router rule
			Session session = null;
			try {
				session = HibernateSessionProvider.getSession();
				Transaction tx = session.beginTransaction();
				Long id = pendingRouterRule.getId();
				session.load(pendingRouterRule, pendingRouterRule.getId());
				RouterRule rr = (RouterRule) session.get(RouterRule.class, id);
				rr.setStatus(TDRStatus.ACTIVE);
				rr.setUpdatedDate(currentDate);
				rr.setApprovedBy(sessionUser.getEmailId());
				session.update(rr);
				tx.commit();
				session.close();

			} catch (HibernateException e) {
				logger.error("Exception ", e);
			} finally {
				session.close();
			}
			
			

			// Find pending router configuration for this rule and set inactive
			List<RouterConfiguration> pendingRouterConfiguration = routerConfigurationDao
					.getPendingRulesByRouterRule(pendingRouterRule);

			if (pendingRouterConfiguration.size() > 0) {

				for (RouterConfiguration routerConfiguration : pendingRouterConfiguration) {
					routerConfigurationDao.delete(routerConfiguration.getId());
				}

			}

			// Find active router configuration for this rule and set inactive
			List<RouterConfiguration> activeRouterConfiguration = routerConfigurationDao
					.getActiveRulesByRouterRule(pendingRouterRule);

			if (activeRouterConfiguration.size() > 0) {

				for (RouterConfiguration routerConfiguration : activeRouterConfiguration) {
					routerConfigurationDao.delete(routerConfiguration.getId());
				}

			}

			// Create New Router configuration for this router rule
			addRouterRuleConfiguration(pendingRouterRule);
			addActionMessage(ErrorType.ROUTER_RULE_CREATED.getResponseMessage());

		} catch (Exception exception) {
		}
		return SUCCESS;
	}

	public void rejectRouterRuleRequest(RouterRule routerRule, TDRStatus status) {

		try {

			Session session = null;
			session = HibernateSessionProvider.getSession();
			Transaction tx = session.beginTransaction();
			Long id = routerRule.getId();
			session.load(routerRule, routerRule.getId());
			RouterRule rRule = (RouterRule) session.get(RouterRule.class, id);
			rRule.setStatus(status);
			rRule.setUpdatedDate(currentDate);
			rRule.setApprovedBy(sessionUser.getEmailId());
			session.update(rRule);
			tx.commit();
			session.close();

		} catch (HibernateException e) {
			e.printStackTrace();
		} finally {

		}

	}
	
	
	public String deleteRules(Long id) {
		
		RouterRule routerRule = routerRuleDao.findRule(id);
		
		// Find active Rules in Router Rule
		RouterRule routerRuleActive = routerRuleDao.getMatchingRule(routerRule);
		if (routerRuleActive != null) {
			routerRuleDao.delete(routerRuleActive,sessionUser.getEmailId());
		}
		
		
		// Find pending Rules in Router Rule
		RouterRule routerRulePending = routerRuleDao.getPendingMatchingRule(routerRule);
		if (routerRulePending != null) {
			routerRuleDao.delete(routerRulePending,sessionUser.getEmailId());
		}
		
		
		// Find pending Router Configuration for this router rule
		List<RouterConfiguration> routerConfigurationPending = routerConfigurationDao.getPendingRulesByRouterRule(routerRule);
		
		if (routerConfigurationPending.size() > 0) {

			for (RouterConfiguration routerConfiguration : routerConfigurationPending) {
				routerConfigurationDao.delete(routerConfiguration.getId());
			}

		}
		
		// Find active Router Configuration for this router rule
		List<RouterConfiguration> routerConfigurationActive = routerConfigurationDao.getActiveRulesByRouterRule(routerRule);
		
		if (routerConfigurationActive.size() > 0) {

			for (RouterConfiguration routerConfiguration : routerConfigurationActive) {
				routerConfigurationDao.delete(routerConfiguration.getId());
			}

		}
		
		addActionMessage("Router rule and related router configuration deleted");
		return SUCCESS;
	}



	public void validate() {
	}

	public void addRouterRuleConfiguration(RouterRule routerRule) {
		User sessionUser = (User) sessionMap.get(Constants.USER.getValue());

		try {

			String[] acquirerMap = routerRule.getAcquirerMap().split(",");
			// Present in YML file
			String slabAmountArrayString = PropertiesManager.propertiesMap
					.get(Constants.SWITCH_ACQUIRER_AMOUNT.getValue());
			StringBuilder tempSlab = new StringBuilder();
			// If no slab is set for this merchant , create a default slab with 00 as ID and
			// limit from 0.01 to 1000000.00

			if (!slabAmountArrayString.contains(routerRule.getMerchant())) {
				slabAmountArrayString = "00-0.01-1000000.00-" + routerRule.getMerchant() + "-ALL";
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
							&& slabSplit[4].equalsIgnoreCase(routerRule.getPaymentType())) {

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
				slabAmountArrayString = "00-0.01-1000000.00-" + routerRule.getMerchant() + "-ALL";
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

				if (!paymentType.equalsIgnoreCase(routerRule.getPaymentType())) {

					slabId = "00";
					minTransactionAmount = "0.01";
					maxTransactionAmount = "1000000.00";

				}

				List<RouterConfiguration> routerConfigurationList = new ArrayList<RouterConfiguration>();

				for (String acquirerString : acquirerMap) {

					String identifier = routerRule.getMerchant() + routerRule.getCurrency()
							+ routerRule.getPaymentType() + routerRule.getMopType() + routerRule.getTransactionType()
							+ routerRule.getPaymentsRegion().toString() + routerRule.getCardHolderType().toString()
							+ slabId;

					String[] acquirerMapString = acquirerString.split("-");

					RouterConfiguration routerConfiguration = new RouterConfiguration();
					Date date = new Date();

					routerConfiguration.setIdentifier(identifier);
					routerConfiguration.setAcquirer(acquirerMapString[1]);
					routerConfiguration.setRulePriority(acquirerMapString[0]);
					routerConfiguration.setCurrency(routerRule.getCurrency());
					routerConfiguration.setPaymentType(routerRule.getPaymentType());
					routerConfiguration.setMopType(routerRule.getMopType());
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
					routerConfiguration.setUpdatedBy(sessionUser.getEmailId());
					routerConfiguration.setRetryMinutes("10");
					routerConfiguration.setFailureCount(0);
					routerConfiguration.setPaymentsRegion(routerRule.getPaymentsRegion());
					routerConfiguration.setCardHolderType(routerRule.getCardHolderType());
					routerConfiguration.setSlabId(slabId);
					routerConfiguration.setMinAmount(Double.valueOf(minTransactionAmount));
					routerConfiguration.setMaxAmount(Double.valueOf(maxTransactionAmount));

					String surcharge = surchargeDao.findDetailsByRouterConfiguration(routerConfiguration);

					if (surcharge.equalsIgnoreCase("NA")) {
						continue;
					}

					routerConfiguration.setSurcharge(surcharge);

					routerConfigurationList.add(routerConfiguration);

				}

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
					routerConfigurationDao.create(routerConfigurationToSave);
					count++;
				}

			}

		}

		catch (Exception e) {

			logger.error("Error occured wile adding Router Configuration " + e.getMessage());
		}

	}


	public String getOperation() {
		return operation;
	}

	public void setOperation(String operation) {
		this.operation = operation;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

}
*/