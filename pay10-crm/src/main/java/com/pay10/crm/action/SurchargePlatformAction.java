package com.pay10.crm.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger; import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.user.Merchants;
import com.pay10.commons.user.Surcharge;
import com.pay10.commons.user.SurchargeDetails;
import com.pay10.commons.user.SurchargeMappingPopulator;
import com.pay10.commons.user.User;
import com.pay10.commons.user.UserDao;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.CrmFieldType;
import com.pay10.commons.util.CrmValidator;
import com.pay10.commons.util.PaymentType;
import com.pay10.crm.actionBeans.SurchargeAcquirerDetailsFactory;
import com.pay10.crm.actionBeans.SurchargeDetailsFactory;
import com.pay10.crm.actionBeans.SurchargeMappingDetailsFactory;

public class SurchargePlatformAction extends AbstractSecureAction {
	
	@Autowired
	private UserDao userDao;
	
	@Autowired
	private CrmValidator validator;
	
	@Autowired
	private SurchargeAcquirerDetailsFactory surchargeAcquirerDetailProvider;
	
	@Autowired
	private SurchargeDetailsFactory surchargeDetailProvider;
	
	@Autowired
	SurchargeMappingDetailsFactory surchargeMappingDetailsFactory;
	
	private static Logger logger = LoggerFactory.getLogger(SurchargePlatformAction.class.getName());
	private static final long serialVersionUID = -6879974923614009981L;

	public List<Merchants> listMerchant = new ArrayList<Merchants>();
	private Map<String, List<SurchargeDetails>> aaData = new HashMap<String, List<SurchargeDetails>>();
	private Map<String, List<Surcharge>> acquirerData = new HashMap<String, List<Surcharge>>();
	private Map<String, List<SurchargeMappingPopulator>> surchargeMapData = new HashMap<String, List<SurchargeMappingPopulator>>();
	private String emailId;
	private String acquirer;
	private String paymentType;
	private User sessionUser = null;

	@Override
	@SuppressWarnings("unchecked")
	public String execute() {
		//setListMerchant(userDao.getMerchantList());
		sessionUser = (User) sessionMap.get(Constants.USER.getValue());
		setListMerchant(userDao.getMerchantList(sessionMap.get(Constants.SEGMENT.getValue()).toString(), sessionUser.getRole().getId()));

		try {
			if (emailId != null && paymentType != null) {

				User user = userDao.findPayIdByEmail(emailId);
				String payId = user.getPayId();

				String paymentTypeName = PaymentType.getpaymentName(paymentType);
				setAaData(surchargeDetailProvider.getSurchargeDetails(payId, paymentTypeName));
				setAcquirerData(surchargeAcquirerDetailProvider.getSurchargeAcquirerDetails(payId, paymentTypeName));

			/*	if (aaData.size() == 0) {
					addActionMessage(ErrorType.SURCHARGEDETAIL_NOT_FETCHED.getResponseMessage());
				}

				if (acquirerData.size() == 0) {
					addActionMessage(ErrorType.SURCHARGEDETAIL_NOT_FETCHED.getResponseMessage());
				}*/
			}
		} catch (Exception exception) {
			logger.error("Exception", exception);
		//	addActionMessage(ErrorType.SERVICETAX_NOT_SET.getResponseMessage());
		}

		try {
			if (emailId != null && paymentType != null) {

				User user = userDao.findPayIdByEmail(emailId);
				String payId = user.getPayId();

				PaymentType payType = PaymentType.getInstanceUsingCode(paymentType);
				setSurchargeMapData(surchargeMappingDetailsFactory.getSurchargeAcquirerDetails(payId, payType));

				/*if (surchargeMapData.size() == 0) {

					addActionMessage(ErrorType.SURCHARGEDETAIL_NOT_FETCHED.getResponseMessage());
				}*/

			}
		} catch (Exception exception) {
			logger.error("Exception", exception);
			addActionMessage(ErrorType.UNKNOWN.getResponseMessage());
		}

		return INPUT;
	}

	public Map<String, List<SurchargeMappingPopulator>> getSurchargeMapData() {
		return surchargeMapData;
	}

	public void setSurchargeMapData(Map<String, List<SurchargeMappingPopulator>> surchargeMapData) {
		this.surchargeMapData = surchargeMapData;
	}

	// To display page without using token
	@SuppressWarnings("unchecked")
	public String displayList() {
		//setListMerchant(userDao.getMerchantList());
		sessionUser = (User) sessionMap.get(Constants.USER.getValue());
		setListMerchant(userDao.getMerchantList(sessionMap.get(Constants.SEGMENT.getValue()).toString(), sessionUser.getRole().getId()));
		return INPUT;
	}

	@Override
	public void validate() {
		if ((validator.validateBlankField(getAcquirer()))) {
		} else if (!validator.validateField(CrmFieldType.ACQUIRER, getAcquirer())) {
			addFieldError(CrmFieldType.ACQUIRER.getName(), ErrorType.INVALID_FIELD.getResponseMessage());
		}
		if (validator.validateBlankField(getEmailId())) {
		} else if (!validator.validateField(CrmFieldType.EMAILID, getEmailId())) {
			addFieldError("emailId", ErrorType.INVALID_FIELD.getResponseMessage());
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

	public String getAcquirer() {
		return acquirer;
	}

	public void setAcquirer(String acquirer) {
		this.acquirer = acquirer;
	}

	public Map<String, List<SurchargeDetails>> getAaData() {
		return aaData;
	}

	public void setAaData(Map<String, List<SurchargeDetails>> aaData) {
		this.aaData = aaData;
	}

	public String getPaymentType() {
		return paymentType;
	}

	public void setPaymentType(String paymentType) {
		this.paymentType = paymentType;
	}

	public Map<String, List<Surcharge>> getAcquirerData() {
		return acquirerData;
	}

	public void setAcquirerData(Map<String, List<Surcharge>> acquirerData) {
		this.acquirerData = acquirerData;
	}
}
