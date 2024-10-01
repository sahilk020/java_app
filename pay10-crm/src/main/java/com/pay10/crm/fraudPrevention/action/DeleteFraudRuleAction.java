package com.pay10.crm.fraudPrevention.action;

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
import com.pay10.commons.dao.FraudPreventionMongoService;
import com.pay10.commons.user.Merchants;
import com.pay10.commons.user.User;
import com.pay10.commons.user.UserType;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.CrmValidator;
import com.pay10.commons.util.FraudPreventionObj;
import com.pay10.crm.action.AbstractSecureAction;
import com.pay10.pg.core.fraudPrevention.model.FraudPreventionDao;

/**
 * @author Harpreet,Rajendra
 *
 */
public class DeleteFraudRuleAction extends AbstractSecureAction {

	@Autowired
	private FraudPreventionDao fraudPreventionDao;

	private static final long serialVersionUID = -2472986893677385590L;

	private static Logger logger = LoggerFactory.getLogger(DeleteFraudRuleAction.class.getName());
	private List<Merchants> merchantList = new ArrayList<Merchants>();
	// request param for deleteFraudPreventionRules
	private String payId;
	private String ruleId;
	private String response;
	private String currency;

	@Autowired
	private FraudPreventionMongoService fraudPreventionMongoService;

	@Autowired
	private AuditTrailService auditTrailService;

	// to update fraud rule from database
	@SuppressWarnings("unchecked")
	@Override
	public String execute() {
		try {
			if (!StringUtils.isBlank(ruleId)) {
				User sessionUser = (User) sessionMap.get(Constants.USER.getValue());
				FraudPreventionObj fraudPrevention = new FraudPreventionObj();

				if (sessionUser.getUserType().equals(UserType.ADMIN)
						|| sessionUser.getUserType().equals(UserType.SUBADMIN)) {
					fraudPrevention.setPayId(sessionUser.getPayId());
				} else if (sessionUser.getUserType().equals(UserType.MERCHANT)) {
					fraudPrevention.setPayId(payId);
					fraudPrevention.setCurrency(getCurrency());
				} else {
					setResponse("Try again, Something went wrong!");
					return ERROR;
				}
				boolean adminAllRules = false, adminFewRules = false;
				fraudPrevention.setId(ruleId);
				fraudPrevention.setUpdatedBy(sessionUser.getEmailId().toString());
		 		FraudPreventionObj fpDbObj = fraudPreventionMongoService.activefpRulefindById(payId,ruleId,getCurrency());
				logger.info("FraudPrevention OBJ:{}",fpDbObj);
				fraudPrevention.setPayId(fpDbObj.getPayId());
				fraudPrevention.setFraudType(fpDbObj.getFraudType());
				fraudPrevention.setCurrency(fpDbObj.getCurrency());
			//	fraudPrevention.setCurrency(fpDbObj.getCurrency());
				if(sessionUser.getUserType().equals(UserType.MERCHANT)) {
					if(null != fpDbObj) {
						if(null != fpDbObj.getCreatedBy() && fpDbObj.getCreatedBy().equalsIgnoreCase(sessionUser.getEmailId()) && null != fpDbObj.getUpdatedBy() && fpDbObj.getUpdatedBy().equalsIgnoreCase(sessionUser.getEmailId())) {
							fraudPreventionMongoService.update(fraudPrevention);													
						}else {
							adminAllRules = true;
						}
					}
					if (adminAllRules) {
						setResponse("This restriction was set from the admin side, you can not delete it");
					} else {
						setResponse("Fraud Rule is deleted Successfully");
					}

				} else {
					fraudPreventionMongoService.update(fraudPrevention);
					setResponse("Fraud Rule is deleted Successfully");
				}
				String ruleIdObj = "{\"ruleId\":\"" + ruleId + "\"}";
				Map<String, ActionMessageByAction> actionMessagesByAction = (Map<String, ActionMessageByAction>) sessionMap
						.get(Constants.ACTION_MESSAGE_BY_ACTION.getValue());
				AuditTrail auditTrail = new AuditTrail(ruleIdObj, null, actionMessagesByAction.get("deleteFraudRule"));
				auditTrailService.saveAudit(request, auditTrail);
				return SUCCESS;
			} else {
				setResponse("Try again, Something went wrong!");
				return ERROR;
			}
		} catch (Exception exception) {
			logger.error("Fraud Prevention System - Exception :" + exception);
			setResponse("Try again, Something went wrong!");
			return ERROR;
		}
	}

	@Override
	public void validate() {
		CrmValidator validator = new CrmValidator();
		if (!validator.validateBlankField(ruleId)) {
			setResponse("Try again, Something went wrong!");
		}
		if (!StringUtils.isBlank(payId)) {
			if (!validator.validateBlankField(payId)) {
				setResponse("Try again, Something went wrong!");
			}
		}
		/*if(!StringUtils.isBlank(currency)){
			if(!validator.validateBlankField(currency)) {
				setResponse("Try again, Something went wrong with Selected Currency!");
			}
		}*/
	}

	public List<Merchants> getMerchantList() {
		return merchantList;
	}

	public void setMerchantList(List<Merchants> merchantList) {
		this.merchantList = merchantList;
	}

	public String getPayId() {
		return payId;
	}

	public void setPayId(String payId) {
		this.payId = payId;
	}

	public String getRuleId() {
		return ruleId;
	}

	public void setRuleId(String ruleId) {
		this.ruleId = ruleId;
	}

	public String getResponse() {
		return response;
	}

	public void setResponse(String response) {
		this.response = response;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}
}