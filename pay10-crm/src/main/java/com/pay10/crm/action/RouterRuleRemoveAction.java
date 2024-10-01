package com.pay10.crm.action;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.pay10.commons.audittrail.ActionMessageByAction;
import com.pay10.commons.audittrail.AuditTrail;
import com.pay10.commons.audittrail.AuditTrailService;
import com.pay10.commons.dao.RouterConfigurationDao;
import com.pay10.commons.dao.RouterRuleDao;
import com.pay10.commons.user.RouterConfiguration;
import com.pay10.commons.user.RouterRule;
import com.pay10.commons.user.User;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.PropertiesManager;

/**
 * @author Rahul
 *
 */
public class RouterRuleRemoveAction extends AbstractSecureAction {

	private static Logger logger = LoggerFactory.getLogger(RouterRuleRemoveAction.class.getName());
	private static final long serialVersionUID = -4833112333995653577L;
	private ArrayList<Long> dataid;

	@Autowired
	private RouterRuleDao routerRuleDao;
	
	@Autowired
	AuditTrailService auditTrailService;

	@Autowired
	private RouterConfigurationDao routerConfigurationDao;

	private Long id;
	private String payid;
	private String response;

	@SuppressWarnings("unchecked")
	public String deleteRules() {

		User sessionUser = (User) sessionMap.get(Constants.USER.getValue());
		try {
			// Check and remove any pending router rule
			RouterRule routerRule = routerRuleDao.findRule(id);
			RouterRule pendingRouterRule = null;

			if (routerRule != null) {
				pendingRouterRule = routerRuleDao.getPendingMatchingRule(routerRule);

			}

			if (pendingRouterRule != null) {
				pendingRouterRule.setApprovedBy(sessionUser.getEmailId());
				routerRuleDao.delete(pendingRouterRule, sessionUser.getEmailId());
			}

			// Check any pending router configurations related to this router rule

			List<RouterConfiguration> pendingRouterConfigurationList = new ArrayList<RouterConfiguration>();
			if (routerRule != null) {
				pendingRouterConfigurationList = routerConfigurationDao.getPendingRulesByRouterRule(routerRule);
			}

			if (pendingRouterConfigurationList.size() > 0) {

				for (RouterConfiguration routerConfiguration : pendingRouterConfigurationList) {
					routerConfigurationDao.delete(routerConfiguration.getId());
				}

			}

			if (routerRule != null) {
				routerRule.setRequestedBy(sessionUser.getEmailId());
				routerRuleDao.delete(routerRule, sessionUser.getEmailId());
			}

			// Remove router configuration related to this rule
			if (routerRule != null) {
				deleteRouterRuleConfiguration(routerRule);
			}

			Map<String, ActionMessageByAction> actionMessagesByAction = (Map<String, ActionMessageByAction>) sessionMap
					.get(Constants.ACTION_MESSAGE_BY_ACTION.getValue());
			AuditTrail auditTrail = new AuditTrail(mapper.writeValueAsString(routerRule), null,
					actionMessagesByAction.get("deleteRouterRule"));
			auditTrailService.saveAudit(request, auditTrail);
			setResponse("Router Rule Deleted");
			addActionMessage("Router Rule Deleted");
			return SUCCESS;
		}

		catch (Exception e) {
			logger.error("Exception in deleting router rule " + e);
			setResponse("Router Rule Delete action Failed");
			addActionMessage("Router Rule Delete action Failed");
			return SUCCESS;
		}
	}

	public void deleteRouterRuleConfiguration(RouterRule routerRule) {

		try {

			logger.info("Remove existing  RouterConfiguration");

			String identifier = routerRule.getMerchant() + routerRule.getCurrency() + routerRule.getPaymentType()
					+ routerRule.getMopType() + routerRule.getTransactionType()
					+ routerRule.getPaymentsRegion().toString() + routerRule.getCardHolderType().toString();

			List<RouterConfiguration> routerConfigurationList = new ArrayList<RouterConfiguration>();

			String slabAmountArrayString = PropertiesManager.propertiesMap
					.get(Constants.SWITCH_ACQUIRER_AMOUNT.getValue());

			// If no slab is set for this merchant , zcreate a default slab with 00 as ID
			// and
			// limit from 0.01 to 5000000.00
			if (!slabAmountArrayString.contains(routerRule.getMerchant())) {
				slabAmountArrayString = "00-0.01-5000000.00-" + routerRule.getMerchant() + "-ALL";
			}

			String[] slabArray = slabAmountArrayString.split(",");

			for (String currentSlab : slabArray) {

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
					maxTransactionAmount = "5000000.00";

				}

				List<RouterConfiguration> routerConfigurationListBySlab = routerConfigurationDao
						.findRulesByIdentifier(identifier + slabId);
				routerConfigurationList.addAll(routerConfigurationListBySlab);
			}

			if (routerConfigurationList.size() > 0) {

				for (RouterConfiguration routerConfiguration : routerConfigurationList) {
					routerConfigurationDao.delete(routerConfiguration.getId());
				}

			}

		}

		catch (Exception e) {
			logger.error("Exception occured in RouterRuleEditAction , cannot delete RouterRuleConfiguration   "
					+ e.getMessage());
		}

	}

	@SuppressWarnings("unchecked")
	public String deletelist() {

		User sessionUser = (User) sessionMap.get(Constants.USER.getValue());

		try {
			for (Long id : dataid) {

				RouterRule routerRule = routerRuleDao.findRule(id);

				RouterRule pendingRouterRule = null;

				if (routerRule != null) {
					pendingRouterRule = routerRuleDao.getPendingMatchingRule(routerRule);

				}

				if (pendingRouterRule != null) {
					pendingRouterRule.setApprovedBy(sessionUser.getEmailId());
					routerRuleDao.delete(pendingRouterRule, sessionUser.getEmailId());
				}

				// Check any pending router configurations related to this router rule

				List<RouterConfiguration> pendingRouterConfigurationList = new ArrayList<RouterConfiguration>();
				if (routerRule != null) {
					pendingRouterConfigurationList = routerConfigurationDao.getPendingRulesByRouterRule(routerRule);
				}

				if (pendingRouterConfigurationList.size() > 0) {

					for (RouterConfiguration routerConfiguration : pendingRouterConfigurationList) {
						routerConfigurationDao.delete(routerConfiguration.getId());
					}

				}

				if (routerRule != null) {
					routerRule.setRequestedBy(sessionUser.getEmailId());
					routerRuleDao.delete(routerRule, sessionUser.getEmailId());
					Map<String, ActionMessageByAction> actionMessagesByAction = (Map<String, ActionMessageByAction>) sessionMap
							.get(Constants.ACTION_MESSAGE_BY_ACTION.getValue());
					AuditTrail auditTrail = new AuditTrail(mapper.writeValueAsString(routerRule), null,
							actionMessagesByAction.get("deleteRouterRule"));
					auditTrailService.saveAudit(request, auditTrail);
				}

				// Remove router configuration related to this rule
				if (routerRule != null) {
					deleteRouterRuleConfiguration(routerRule);
				}
			}
			setResponse("Router Rule Deleted");
			addActionMessage("Router Rule Deleted");
			return SUCCESS;
		}

		catch (Exception e) {
			logger.error("Exception in deleting router rule " + e);
			setResponse("Router Rule Delete action Failed");
			addActionMessage("Router Rule Delete action Failed");
			return SUCCESS;
		}
	}

	public String getPayid() {
		return payid;
	}

	public void setPayid(String payid) {
		this.payid = payid;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getResponse() {
		return response;
	}

	public void setResponse(String response) {
		this.response = response;
	}

	public ArrayList<Long> getDataid() {
		return dataid;
	}

	public void setDataid(ArrayList<Long> dataid) {
		this.dataid = dataid;
	}

}
