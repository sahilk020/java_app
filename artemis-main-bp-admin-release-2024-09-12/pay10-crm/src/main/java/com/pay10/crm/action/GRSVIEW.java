package com.pay10.crm.action;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.pay10.commons.user.CompanyProfileDao;
import com.pay10.commons.user.CompanyProfileUi;
import com.pay10.commons.user.Merchants;
import com.pay10.commons.user.User;
import com.pay10.commons.user.UserDao;
import com.pay10.commons.user.UserType;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.Currency;
import com.pay10.commons.util.StatusType;
import com.pay10.commons.util.StatusTypeUI;
import com.pay10.commons.util.TxnType;
import com.pay10.crm.actionBeans.CurrencyMapProvider;


public class GRSVIEW extends AbstractSecureAction {

	
	private static final long serialVersionUID = -3306411281051464691L;
	private static Logger logger = LoggerFactory.getLogger(GRSVIEW.class.getName());
	@Autowired
	private UserDao userDao;
	
	@Autowired
	private CurrencyMapProvider currencyMapProvider;
	
	private List<CompanyProfileUi> companyList = new ArrayList<CompanyProfileUi>();
	private User sessionUser = null;
	private List<Merchants> merchantList = new ArrayList<Merchants>();
	private List<StatusTypeUI> lst;
	private List<StatusType> statusList;
	private List<TxnType> txnTypelist;
	private Map<String, String> currencyMap = new HashMap<String, String>();
	
	
	
	@Override
	public String execute() {
		logger.info("ChargebackDashoard Page Executed");
		sessionUser = (User) sessionMap.get(Constants.USER.getValue());
		if (sessionUser.getUserType().equals(UserType.ADMIN) || sessionUser.getUserType().equals(UserType.SUBADMIN)
				|| sessionUser.getUserType().equals(UserType.SUPERADMIN)
				|| sessionUser.getUserType().equals(UserType.SUBSUPERADMIN)) {
			// setMerchantList(userDao.getMerchantActiveList());
			setMerchantList(userDao.getMerchantActiveList(sessionMap.get(Constants.SEGMENT.getValue()).toString(),
					sessionUser.getRole().getId()));
			currencyMap = Currency.getAllCurrency();
			companyList = new CompanyProfileDao().getAllCompanyProfileList();
		} else if (sessionUser.getUserType().equals(UserType.RESELLER)) {
			setMerchantList(userDao.getResellerMerchantList(sessionUser.getResellerId()));
			currencyMap = Currency.getAllCurrency();
		} else if (sessionUser.getUserType().equals(UserType.MERCHANT)
				|| sessionUser.getUserType().equals(UserType.SUBUSER)
				|| sessionUser.getUserType().equals(UserType.SUBACQUIRER)) {
			Merchants merchant = new Merchants();
			merchant.setEmailId(sessionUser.getEmailId());
			merchant.setBusinessName(sessionUser.getBusinessName());
			merchant.setPayId(sessionUser.getPayId());
			merchantList.add(merchant);
			if (sessionUser.getUserType().equals(UserType.SUBUSER)
					|| sessionUser.getUserType().equals(UserType.SUBACQUIRER)) {
				String parentMerchantPayId = sessionUser.getParentPayId();
				User parentMerchant = userDao.findPayId(parentMerchantPayId);
				merchant.setMerchant(parentMerchant);
				merchantList.add(merchant);
				Object[] obj = merchantList.toArray();
				for (Object sortList : obj) {
					if (merchantList.indexOf(sortList) != merchantList.lastIndexOf(sortList)) {
						merchantList.remove(merchantList.lastIndexOf(sortList));
					}
				}
			}
			currencyMap = currencyMapProvider.currencyMap(sessionUser);
		}
		setTxnTypelist(TxnType.gettxnType());
		setLst(StatusTypeUI.getStatusType());
		setStatusList(StatusType.getStatusType());
		
		
		return SUCCESS;
	}

	public List<Merchants> getMerchantList() {
		return merchantList;
	}

	public void setMerchantList(List<Merchants> merchantList) {
		this.merchantList = merchantList;
	}

	public List<StatusTypeUI> getLst() {
		return lst;
	}

	public void setLst(List<StatusTypeUI> lst) {
		this.lst = lst;
	}

	public List<StatusType> getStatusList() {
		return statusList;
	}

	public void setStatusList(List<StatusType> statusList) {
		this.statusList = statusList;
	}

	public List<TxnType> getTxnTypelist() {
		return txnTypelist;
	}

	public void setTxnTypelist(List<TxnType> txnTypelist) {
		this.txnTypelist = txnTypelist;
	}

	public Map<String, String> getCurrencyMap() {
		return currencyMap;
	}

	public void setCurrencyMap(Map<String, String> currencyMap) {
		this.currencyMap = currencyMap;
	}

	public List<CompanyProfileUi> getCompanyList() {
		return companyList;
	}

	public void setCompanyList(List<CompanyProfileUi> companyList) {
		this.companyList = companyList;
	}
	
	
}