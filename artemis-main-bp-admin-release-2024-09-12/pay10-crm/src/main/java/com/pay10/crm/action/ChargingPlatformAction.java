package com.pay10.crm.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.pay10.commons.dao.ChargingDetailsFactory;
import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.user.ChargingDetails;
import com.pay10.commons.user.Merchants;
import com.pay10.commons.user.User;
import com.pay10.commons.user.UserDao;
import com.pay10.commons.util.AcquirerType;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.CrmFieldType;
import com.pay10.commons.util.CrmValidator;

public class ChargingPlatformAction extends AbstractSecureAction {

	@Autowired
	private ChargingDetailsFactory chargingDetailProvider;
	
	@Autowired
	private UserDao userDao;
	
	@Autowired
	private CrmValidator validator;
	
	private static Logger logger = LoggerFactory
			.getLogger(ChargingPlatformAction.class.getName());
	private static final long serialVersionUID = -6879974923614009981L;

	public List<Merchants> listMerchant = new ArrayList<Merchants>();
	private Map<String, List<ChargingDetails>> aaData = new HashMap<String, List<ChargingDetails>>();
	private String emailId;
	private String acquirer;
	private String paymentRegion;
	private String cardHolderType;
	private User sessionUser = null;
	
	//Added By Sweety
	public List<String> acquirerList = new ArrayList<String>();

	
	@Override
	@SuppressWarnings("unchecked")
	public String execute() {
		
		sessionUser = (User) sessionMap.get(Constants.USER.getValue());
		logger.info("TDR Charging Details File request :: ");
		setListMerchant(userDao.getMerchantList(sessionMap.get(Constants.SEGMENT.getValue()).toString(), sessionUser.getRole().getId()));
		//setListMerchant(userDao.getMerchantList());
		List<String> acquirerList=userDao.getAcquirerTypeList(userDao.getPayIdByEmailId(emailId));
		List<String> list = new ArrayList<String>();
//		list.add(0, "Select Acquirer");
		for (String acquirer : acquirerList) {
			list.add(AcquirerType.getInstancefromName(acquirer).getCode());
		}
		setAcquirerList(list);

		try {
			if (emailId != null && acquirer != null) {
				setAaData(chargingDetailProvider.getChargingDetailsMap(emailId,
						acquirer));
				logger.info("AaData : "+getAaData());
				System.out.println("AaData : "+getAaData());
//				if(aaData.size()==0){
//					addActionMessage(ErrorType.CHARGINGDETAIL_NOT_FETCHED.getResponseMessage());
//				}
			}
		} catch (Exception exception) {
			logger.error("Exception", exception);
			addActionMessage(ErrorType.LOCAL_TAX_RATES_NOT_AVAILABLE.getResponseMessage());
		}
		return INPUT;
	}

	// To display page without using token
	@SuppressWarnings("unchecked")
	public String displayList() {
		sessionUser = (User) sessionMap.get(Constants.USER.getValue());
		setListMerchant(userDao.getMerchantList(sessionMap.get(Constants.SEGMENT.getValue()).toString(), sessionUser.getRole().getId()));
		//setListMerchant(userDao.getMerchantList());
		return INPUT;
	}

	
	
	@Override
	public void validate() {
		if ((validator.validateBlankField(getAcquirer()))) {
		} else if (!validator.validateField(CrmFieldType.ACQUIRER,
				getAcquirer())) {
			addFieldError(CrmFieldType.ACQUIRER.getName(),
					ErrorType.INVALID_FIELD.getResponseMessage());
		}
		if (validator.validateBlankField(getEmailId())) {
		} else if (!validator.validateField(CrmFieldType.EMAILID, getEmailId())) {
			addFieldError("emailId",
					ErrorType.INVALID_FIELD.getResponseMessage());
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

	public Map<String, List<ChargingDetails>> getAaData() {
		return aaData;
	}

	public void setAaData(Map<String, List<ChargingDetails>> aaData) {
		this.aaData = aaData;
	}

	public String getPaymentRegion() {
		return paymentRegion;
	}

	public void setPaymentRegion(String paymentRegion) {
		this.paymentRegion = paymentRegion;
	}

	public String getCardHolderType() {
		return cardHolderType;
	}

	public void setCardHolderType(String cardHolderType) {
		this.cardHolderType = cardHolderType;
	}

	public List<String> getAcquirerList() {
		return acquirerList;
	}

	public void setAcquirerList(List<String> acquirerList) {
		this.acquirerList = acquirerList;
	}
}
