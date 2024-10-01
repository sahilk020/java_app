package com.pay10.crm.action;

import java.util.Date;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.pay10.commons.audittrail.ActionMessageByAction;
import com.pay10.commons.audittrail.AuditTrail;
import com.pay10.commons.audittrail.AuditTrailService;
import com.pay10.commons.dao.SuccessRateThresholdSetting;
import com.pay10.commons.user.SuccessRateThresholdSettingDao;
import com.pay10.commons.user.User;
import com.pay10.commons.user.UserDao;
import com.pay10.commons.user.UserType;
import com.pay10.commons.util.Constants;

public class SchedularChangeSettingAction extends AbstractSecureAction {
	private static final long serialVersionUID = 4403892852296115603L;
	private static Logger logger = LoggerFactory.getLogger(SchedularChangeSettingAction.class.getName());
	private String acquirerName;
	private String paymentType;
	private String mopType;
	private String merchant;
	private String successRate;
	private String email;
	private String action;
	private String payId;

	private User sessionUser = new User();

	@Autowired
	private UserDao userDao;

	@Autowired
	private SuccessRateThresholdSettingDao successRateThresholdSettingDao;

	@Autowired
	private AuditTrailService auditTrailService;

	@SuppressWarnings("unchecked")
	@Override
	public String execute() {
		sessionUser = (User) sessionMap.get(Constants.USER.getValue());
		try {
			if (sessionUser.getUserType().equals(UserType.ADMIN)
					|| sessionUser.getUserType().equals(UserType.SUBADMIN)) {
				SuccessRateThresholdSetting setting = new SuccessRateThresholdSetting();
				Map<String, ActionMessageByAction> actionMessagesByAction = (Map<String, ActionMessageByAction>) sessionMap
						.get(Constants.ACTION_MESSAGE_BY_ACTION.getValue());
				ActionMessageByAction actionMessageByAction = new ActionMessageByAction();
				String prevSetting = "";
				if (action.equals("UPDATE")) {
					try {
						SuccessRateThresholdSetting prevSettings = successRateThresholdSettingDao
								.getByMerchantAndAcquirer(getMerchant(), getAcquirerName(), getPaymentType(),
										getMopType());
						prevSetting = mapper.writeValueAsString(prevSettings);
					} catch (Exception ex) {
						// no-op
					}
					actionMessageByAction = actionMessagesByAction.get("schedularChangeSettingAction");
					updateEntry(setting);
				} else if (action.equals("DELETE")) {
					actionMessageByAction = actionMessagesByAction.get("schedularDeleteAction");
					deleteEntry(setting);
				} else if (action.equals("NEW")) {
					actionMessageByAction = actionMessagesByAction.get("schedularAddAction");
					if (mopType.equals("Others")) {
						String[] mopTypes = { "1004", "1013", "1012", "1001", "1002", "1030", "1005" };

						for (String str : mopTypes) {
							mopType = str;
							createNewEntry(setting);
						}
					} else {
						createNewEntry(setting);
					}
				}
				AuditTrail auditTrail = new AuditTrail(mapper.writeValueAsString(setting), prevSetting,
						actionMessageByAction);
				auditTrailService.saveAudit(request, auditTrail);
			}
			return SUCCESS;
		} catch (Exception exception) {
			logger.error("Exception", exception);
			return ERROR;
		}

	}

	public String getAcquirerName() {
		return acquirerName;
	}

	public void setAcquirerName(String acquirerName) {
		this.acquirerName = acquirerName;
	}

	public String getPaymentType() {
		return paymentType;
	}

	public void setPaymentType(String paymentType) {
		this.paymentType = paymentType;
	}

	public String getMopType() {
		return mopType;
	}

	public void setMopType(String mopType) {
		this.mopType = mopType;
	}

	public String getSuccessRate() {
		return successRate;
	}

	public void setSuccessRate(String successRate) {
		this.successRate = successRate;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getMerchant() {
		return merchant;
	}

	public void setMerchant(String merchant) {
		this.merchant = merchant;
	}

	public String getPayId() {
		return payId;
	}

	public void setPayId(String payId) {
		this.payId = payId;
	}

	public void createNewEntry(SuccessRateThresholdSetting setting) {

		setting.setAcquirerName(acquirerName);
		setting.setPaymentType(paymentType);
		setting.setMopType(mopType);

		if (merchant.equals("ALL"))
			setting.setMerchant("ALL");
		else
			setting.setMerchant(userDao.getMerchantByPayId(merchant));
		setting.setPayId(merchant);
		setting.setEmail(email);
		setting.setSuccessRate(Integer.parseInt(successRate));
		setting.setUpdateDate(new Date());
		String updatedBy = null;
		if (sessionUser.getFirstName() != null)
			updatedBy = sessionUser.getFirstName();
		else
			updatedBy = "ADMIN";
		setting.setUpdatedBy(updatedBy);
		successRateThresholdSettingDao.save(setting);
	}

	public void updateEntry(SuccessRateThresholdSetting setting) {
		setting.setAcquirerName(acquirerName);
		setting.setPaymentType(paymentType);
		setting.setMopType(mopType);
		setting.setMerchant(merchant);
		setting.setPayId(payId);
		setting.setEmail(email);
		setting.setSuccessRate(Integer.parseInt(successRate));
		setting.setUpdateDate(new Date());
		String updatedBy = null;
		if (sessionUser.getFirstName() != null)
			updatedBy = sessionUser.getFirstName();
		else
			updatedBy = "ADMIN";
		setting.setUpdatedBy(updatedBy);
		successRateThresholdSettingDao.updateSuccessThresholdSetting(setting);
	}

	public void deleteEntry(SuccessRateThresholdSetting setting) {
		setting.setAcquirerName(acquirerName);
		setting.setPaymentType(paymentType);
		setting.setMopType(mopType);
		setting.setMerchant(merchant);
		setting.setPayId(payId);
		setting.setEmail(email);
		setting.setSuccessRate(Integer.parseInt(successRate));
		setting.setUpdateDate(new Date());
		String updatedBy = null;
		if (sessionUser.getFirstName() != null)
			updatedBy = sessionUser.getFirstName();
		else
			updatedBy = "ADMIN";
		setting.setUpdatedBy(updatedBy);
		successRateThresholdSettingDao.deleteSuccessThresholdSetting(setting);
	}

}
