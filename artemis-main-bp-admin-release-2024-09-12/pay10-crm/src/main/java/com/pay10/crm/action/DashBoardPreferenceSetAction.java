package com.pay10.crm.action;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.user.ChargingDetails;
import com.pay10.commons.user.DashboardPreferenceSet;
import com.pay10.commons.user.DashboardPreferenceSetDao;
import com.pay10.commons.user.FunnelChatData;
import com.pay10.commons.user.User;
import com.pay10.commons.user.UserDao;
import com.pay10.commons.user.UserType;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.CrmFieldConstants;
import com.pay10.commons.util.CrmFieldType;
import com.pay10.commons.util.CrmValidator;
import com.pay10.commons.util.DateCreater;
import com.pay10.crm.actionBeans.AnalyticsDataService;

/**
 * @author Rajendra
 *
 */

public class DashBoardPreferenceSetAction  extends AbstractSecureAction {

	private static final long serialVersionUID = -6323553936458486881L;

	private static Logger logger = LoggerFactory.getLogger(DashBoardPreferenceSetAction.class.getName());

	private String merchantEmailId;
	private String preferenceSetConstant;
	
	@Autowired
	private DashboardPreferenceSetDao dashboardPreferenceSetDao;
	
	private DashboardPreferenceSet dashboardPreferenceSet = new DashboardPreferenceSet();
	
	private static DashboardPreferenceSetDao dpsDao = new DashboardPreferenceSetDao();
	
	@Override
	public String execute() {
		
		try {
			User sessionUser = (User) sessionMap.get(Constants.USER.getValue());
			if (sessionUser.getUserType().equals(UserType.SUPERADMIN) || sessionUser.getUserType().equals(UserType.ADMIN)
					|| sessionUser.getUserType().equals(UserType.SUBADMIN)
					|| sessionUser.getUserType().equals(UserType.ASSOCIATE)
					|| sessionUser.getUserType().equals(UserType.RESELLER)
					|| sessionUser.getUserType().equals(UserType.MERCHANT)) {
				String userEmailId = sessionUser.getEmailId().toString();				
				/*
				 * if (!merchantEmailId.equalsIgnoreCase("All")) { User merchant =
				 * userDao.findPayIdByEmail(merchantEmailId); merchantPayId =
				 * merchant.getPayId(); } else { merchantPayId = merchantEmailId; }
				 */
				dashboardPreferenceSet = dpsDao.getDashboardPreferenceSetByEmailId(userEmailId);
				if(dashboardPreferenceSet != null) {
					setPreferenceSetConstant(dashboardPreferenceSet.getPreferenceSetConstant());
				}
			}

		}
		catch(Exception e) {
			logger.error("Exception in getting transaction summary count data " +e);
		}
		
		
		return SUCCESS;
	}

	@Override
	public void validate() {
		CrmValidator validator = new CrmValidator();
		
		// Present we are taking the email Id from Session User object 
		/*
		 * if (validator.validateBlankField(getMerchantEmailId()) ||
		 * getMerchantEmailId().equals(CrmFieldConstants.ALL.getValue())) { } else if
		 * (!validator.validateField(CrmFieldType.MERCHANT_EMAIL_ID,
		 * getMerchantEmailId())) {
		 * addFieldError(CrmFieldType.MERCHANT_EMAIL_ID.getName(),
		 * ErrorType.INVALID_FIELD.getResponseMessage()); }
		 */

	}

	public DashboardPreferenceSet getDashboardPreferenceSet() {
		return dashboardPreferenceSet;
	}

	public void setDashboardPreferenceSet(DashboardPreferenceSet dashboardPreferenceSet) {
		this.dashboardPreferenceSet = dashboardPreferenceSet;
	}
	

	public String getPreferenceSetConstant() {
		return preferenceSetConstant;
	}

	public void setPreferenceSetConstant(String preferenceSetConstant) {
		this.preferenceSetConstant = preferenceSetConstant;
	}

	public String getMerchantEmailId() {
		return merchantEmailId;
	}

	public void setMerchantEmailId(String merchantEmailId) {
		this.merchantEmailId = merchantEmailId;
	}	

}
