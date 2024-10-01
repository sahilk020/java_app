package com.pay10.crm.action;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.pay10.commons.action.AbstractSecureAction;
import com.pay10.commons.user.Merchants;
import com.pay10.commons.user.User;
import com.pay10.commons.user.UserDao;
import com.pay10.commons.user.UserType;
import com.pay10.commons.util.Constants;

public class ResellerRevenueAction extends AbstractSecureAction {

	@Autowired
	private UserDao userDao;

	public String resellerId;
	public List<Merchants> listMerchant = new ArrayList<Merchants>();
	public List<Merchants> listReseller = new ArrayList<Merchants>();
	private static final long serialVersionUID = 6136070413613356019L;

	@SuppressWarnings("unchecked")
	public String execute() {
		try {
			User sessionUser = (User) sessionMap.get(Constants.USER.getValue());
			if (sessionUser.getUserType().equals(UserType.SUPERADMIN)
					|| sessionUser.getUserType().equals(UserType.ADMIN)
					|| sessionUser.getUserType().equals(UserType.SUBADMIN)) {
			//setListMerchant(userDao.getMerchantList());
			setListMerchant(userDao.getMerchantList(sessionMap.get(Constants.SEGMENT.getValue()).toString(), sessionUser.getRole().getId()));
			setListReseller(userDao.getResellerList());
			} else if(sessionUser.getUserType().equals(UserType.RESELLER)) {
				setListMerchant(userDao.getMerchantListByResellerID(sessionUser.getResellerId()));
			}
		} catch (Exception e) {
			return ERROR;
		}
		return INPUT;
	}
	
	public String getMerchantByResellerPayId() {
		try {
			setListMerchant(userDao.getMerchantListByResellerID(resellerId));
		} catch (Exception ex) {
			return ERROR;
		}
		return SUCCESS;
	}
	
	public List<Merchants> getListMerchant() {
		return listMerchant;
	}
	public void setListMerchant(List<Merchants> listMerchant) {
		this.listMerchant = listMerchant;
	}
	public List<Merchants> getListReseller() {
		return listReseller;
	}
	public void setListReseller(List<Merchants> listReseller) {
		this.listReseller = listReseller;
	}
	public String getResellerId() {
		return resellerId;
	}
	public void setResellerId(String resellerId) {
		this.resellerId = resellerId;
	}
}
