package com.pay10.crm.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.pay10.commons.user.Merchants;
import com.pay10.commons.user.MultCurrencyCode;
import com.pay10.commons.user.MultCurrencyCodeDao;
import com.pay10.commons.user.User;
import com.pay10.commons.user.UserDao;
import com.pay10.commons.user.UserType;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.Currency;
import com.pay10.commons.util.StatusType;
import com.pay10.commons.util.TxnType;
import com.pay10.crm.actionBeans.CurrencyMapProvider;

/**
 * @author Chandan
 *
 */
public class SaleMerchantNameAction extends AbstractSecureAction {
	
	@Autowired
	private UserDao userDao;
	
	@Autowired
	private CurrencyMapProvider currencyMapProvider;
	

	private static Logger logger = LoggerFactory.getLogger(SaleMerchantNameAction.class.getName());
	private static final long serialVersionUID = -5990800125330748024L;

	private List<Merchants> merchantList = new ArrayList<Merchants>();
	private Map<String, String> currencyMap = new HashMap<String, String>();
	private User sessionUser = null;
	private List<StatusType> lst;
	private List<TxnType> txnTypelist;
	
	@Autowired 
	private MultCurrencyCodeDao currencyCodeDao;

	@Override
	@SuppressWarnings("unchecked")
	public String execute() {
		try {
			sessionUser = (User) sessionMap.get(Constants.USER.getValue());
			if(sessionUser.getUserType().equals(UserType.ADMIN)
					|| sessionUser.getUserType().equals(UserType.SUBADMIN)) {
				//setMerchantList(userDao.getMerchantActiveList());
				setMerchantList(userDao.getMerchantActiveList(sessionMap.get(Constants.SEGMENT.getValue()).toString(), sessionUser.getRole().getId()));
//				currencyMap = Currency.getAllCurrency();
				currencyMap = getAllCurrency();

			}else if(sessionUser.getUserType().equals(UserType.RESELLER)) {
				setMerchantList(userDao.getResellerMerchantList(sessionUser.getResellerId()));
//				currencyMap = Currency.getAllCurrency();
				currencyMap = getAllCurrency();

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
 				currencyMap = currencyMapProvider.currencyMap(sessionUser);
			}
			setTxnTypelist(TxnType.gettxnType());
			setLst(StatusType.getStatusType());
		} catch (Exception exception) {
			logger.error("Exception", exception);
			return ERROR;
		}

		return INPUT;
	}

	public String subUserList() {
		try {
			sessionUser = (User) sessionMap.get(Constants.USER.getValue());
			if (sessionUser.getUserType().equals(UserType.MERCHANT)) {
				setMerchantList(userDao.getSubUserList(sessionUser
						.getPayId()));
				currencyMap = Currency.getSupportedCurreny(sessionUser);
			} else if (sessionUser.getUserType().equals(UserType.SUBUSER) || sessionUser.getUserType().equals(UserType.SUBACQUIRER)) {
				Merchants merchant = new Merchants();
				User user = new User();
				user = userDao.findPayId(sessionUser.getParentPayId());
				merchant.setEmailId(user.getEmailId());
				merchant.setBusinessName(user.getBusinessName());
				merchant.setPayId(user.getPayId());
				merchantList.add(merchant);
				currencyMap = Currency.getSupportedCurreny(sessionUser);
			}

			setLst(StatusType.getStatusType());
			setTxnTypelist(TxnType.gettxnType());
		} catch (Exception exception) {
			logger.error("Exception", exception);
			return ERROR;
		}

		return INPUT;
	}

	
	public  Map<String,String> getAllCurrency(){
		Map<String,String> data=new HashMap<String,String>();
		List<MultCurrencyCode> dataList=currencyCodeDao.getCurrencyCode();
		for(MultCurrencyCode code:dataList) {
			data.put(code.getCode(), code.getName());
		}
		
		
		return data;
		
	}

	public List<Merchants> getMerchantList() {
		return merchantList;
	}

	public void setMerchantList(List<Merchants> merchantList) {
		this.merchantList = merchantList;
	}

	public List<StatusType> getLst() {
		return lst;
	}

	public void setLst(List<StatusType> lst) {
		this.lst = lst;
	}

	public Map<String, String> getCurrencyMap() {
		return currencyMap;
	}

	public void setCurrencyMap(Map<String, String> currencyMap) {
		this.currencyMap = currencyMap;
	}

	public List<TxnType> getTxnTypelist() {
		return txnTypelist;
	}

	public void setTxnTypelist(List<TxnType> txnTypelist) {
		this.txnTypelist = txnTypelist;
	}


}
