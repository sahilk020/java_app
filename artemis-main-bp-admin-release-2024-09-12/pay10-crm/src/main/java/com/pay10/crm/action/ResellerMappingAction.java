package com.pay10.crm.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.user.Merchants;
import com.pay10.commons.user.PendingResellerMappingDao;
import com.pay10.commons.user.User;
import com.pay10.commons.user.UserDao;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.CrmFieldType;
import com.pay10.commons.util.CrmValidator;
import com.pay10.crm.actionBeans.MerchantMappingFactory;

/**
 * Neeraj, Rahul
 */
public class ResellerMappingAction extends AbstractSecureAction {

	@Autowired
	private PendingResellerMappingDao pendingResellerMappingDao;

	@Autowired
	private MerchantMappingFactory merchantMappingFactory;

	@Autowired
	private UserDao userDao;
	@Autowired
	private CrmValidator validator;

	private static Logger logger = LoggerFactory.getLogger(ResellerMappingAction.class.getName());
	private static final long serialVersionUID = -6604356643967237232L;
	private List<Merchants> listMerchant = new ArrayList<Merchants>();
	private List<Merchants> listReseller = new ArrayList<Merchants>();
	private List<Merchants> resellerMerchantList = new ArrayList<Merchants>();
	private Map<String, List<Merchants>> resellerList = new HashMap<String, List<Merchants>>();
	private String emailId;
	private String reseller;
	private String userType;
	private String response;
	private List<Merchants> merchantList = new ArrayList<Merchants>();

	@Override
	@SuppressWarnings("unchecked")
	public String execute() {
		//setListMerchant(userDao.getMerchantList());
		setListMerchant(userDao.getMerchantListForReseller());
		setListReseller(userDao.getResellerList());
		return INPUT;
	}

	@SuppressWarnings("unchecked")
	public String resellerMappingSave() {
		try {
			if (emailId != null && !emailId.equals("") && reseller != null && !reseller.equals("") && userType != null
					&& !userType.equals("")) {

				User sessionUser = (User) sessionMap.get(Constants.USER.getValue());
				setResellerList(
						merchantMappingFactory.getMerchantMappingFactory(emailId, reseller, userType, sessionUser));
				//setListMerchant(userDao.getMerchantList());
				setListMerchant(userDao.getMerchantList(sessionMap.get(Constants.SEGMENT.getValue()).toString(), sessionUser.getRole().getId()));
				setListReseller(userDao.getResellerList());
				setResponse("Reseller details updated");
			} else {
				setResponse("Something went wrong , reseller details not updated");
			}
		} catch (Exception exception) {
			setResponse("Something went wrong, reseller details not updated");
			logger.error("error" + exception);

		}
		return SUCCESS;

	}

	public String mappedMerchantList() {

		try {

			User userReseller = userDao.find(reseller);
			String resellerPayId = userReseller.getPayId();

			setMerchantList(userDao.getActiveResellerMerchants(resellerPayId));
			setResellerMerchantList(merchantList);
			setResponse("Success");
			return SUCCESS;
		}

		catch (Exception exception) {

			setResponse("Something went wrong, reseller details not updated");
			logger.error("error" + exception);
			return ERROR;
		}

	}

	@Override
	public void validate() {
		if (validator.validateBlankField(getEmailId())) {
			/*
			 * addFieldError(CrmFieldType.EMAILID.getName(), validator
			 * .getResonseObject().getResponseMessage());
			 */
		} else if (!(validator.isValidEmailId(getEmailId()))) {
			addFieldError(CrmFieldType.EMAILID.getName(), ErrorType.INVALID_FIELD_VALUE.getResponseMessage());
		}
		if ((validator.validateBlankField(getReseller()))) {
			/*
			 * addFieldError(CrmFieldType.RESELLER.getName(), validator
			 * .getResonseObject().getResponseMessage());
			 */
		} else if (!(validator.validateField(CrmFieldType.RESELLER, getReseller()))) {
			/*
			 * addFieldError(CrmFieldType.RESELLER.getName(), validator
			 * .getResonseObject().getResponseMessage());
			 */
		}
		if ((validator.validateBlankField(getUserType()))) {
			/*
			 * addFieldError(CrmFieldType.USER_TYPE.getName(), validator
			 * .getResonseObject().getResponseMessage());
			 */
		} else if (!(validator.validateField(CrmFieldType.USER_TYPE, getUserType()))) {
			addFieldError(CrmFieldType.USER_TYPE.getName(), validator.getResonseObject().getResponseMessage());
		}
		if ((validator.validateBlankField(getResponse()))) {
			/*
			 * addFieldError(CrmFieldType.RESPONSE.getName(), validator
			 * .getResonseObject().getResponseMessage());
			 */
		} else if (!(validator.validateField(CrmFieldType.RESPONSE, getResponse()))) {
			addFieldError(CrmFieldType.RESPONSE.getName(), validator.getResonseObject().getResponseMessage());
		}
	}

	public List<Merchants> getListMerchant() {
		return listMerchant;
	}

	public void setListMerchant(List<Merchants> listMerchant) {
		this.listMerchant = listMerchant;
	}

	public String getEmailId() {
		return emailId;
	}

	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}

	public List<Merchants> getListReseller() {
		return listReseller;
	}

	public void setListReseller(List<Merchants> listReseller) {
		this.listReseller = listReseller;
	}

	public Map<String, List<Merchants>> getResellerList() {
		return resellerList;
	}

	public void setResellerList(Map<String, List<Merchants>> resellerList) {
		this.resellerList = resellerList;
	}

	public String getReseller() {
		return reseller;
	}

	public void setReseller(String reseller) {
		this.reseller = reseller;
	}

	public String getUserType() {
		return userType;
	}

	public void setUserType(String userType) {
		this.userType = userType;
	}

	public String getResponse() {
		return response;
	}

	public void setResponse(String response) {
		this.response = response;
	}

	public List<Merchants> getResellerMerchantList() {
		return resellerMerchantList;
	}

	public void setResellerMerchantList(List<Merchants> resellerMerchantList) {
		this.resellerMerchantList = resellerMerchantList;
	}

	public List<Merchants> getMerchantList() {
		return merchantList;
	}

	public void setMerchantList(List<Merchants> merchantList) {
		this.merchantList = merchantList;
	}

}
