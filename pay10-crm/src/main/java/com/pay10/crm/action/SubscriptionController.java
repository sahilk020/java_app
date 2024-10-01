package com.pay10.crm.action;


import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.pay10.commons.user.Merchants;
import com.pay10.commons.user.User;
import com.pay10.commons.user.UserDao;
import com.pay10.commons.user.UserType;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.Currency;

public class SubscriptionController extends AbstractSecureAction {

	private static final long serialVersionUID = -3306411281051464691L;
	private static Logger logger = LoggerFactory.getLogger(SubscriptionController.class.getName());
	@Autowired
	private UserDao userDao;
	
	
	private User sessionUser = null;
	private List<Merchants> merchantList = new ArrayList<Merchants>();
	
	public List<Merchants> getMerchantList() {
		return merchantList;
	}



	public void setMerchantList(List<Merchants> merchantList) {
		this.merchantList = merchantList;
	}

	@Override
	public String execute() {
		logger.info("CreatePlan Page Executed");
		sessionUser = (User) sessionMap.get(Constants.USER.getValue());
		if(sessionUser.getUserType().equals(UserType.ADMIN)
				|| sessionUser.getUserType().equals(UserType.SUBADMIN)) {
			//setMerchantList(userDao.getMerchantActiveList());
			setMerchantList(userDao.getMerchantActiveList(sessionMap.get(Constants.SEGMENT.getValue()).toString(), sessionUser.getRole().getId()));
		}else if(sessionUser.getUserType().equals(UserType.RESELLER)) {
			setMerchantList(userDao.getResellerMerchantList(sessionUser.getResellerId()));
		}else if(sessionUser.getUserType().equals(UserType.MERCHANT) || sessionUser.getUserType().equals(UserType.SUBUSER)  || sessionUser.getUserType().equals(UserType.SUBACQUIRER)) {
			Merchants merchant = new Merchants();
			merchant.setEmailId(sessionUser.getEmailId());
			merchant.setBusinessName(sessionUser.getBusinessName());
			merchant.setPayId(sessionUser.getPayId());
			merchantList.add(merchant);
			if(sessionUser.getUserType().equals(UserType.SUBUSER) || sessionUser.getUserType().equals(UserType.SUBACQUIRER)){
				String parentMerchantPayId = sessionUser.getParentPayId();
				User parentMerchant = userDao
						.findPayId(parentMerchantPayId);
				merchant.setMerchant(parentMerchant);
				merchantList.add(merchant);
				Object[] obj = merchantList.toArray();
				for(Object sortList : obj){
					if(merchantList.indexOf(sortList) != merchantList.lastIndexOf(sortList)){
						merchantList.remove(merchantList.lastIndexOf(sortList));
					}
				}
			}
		}
		return SUCCESS;
	}
}
