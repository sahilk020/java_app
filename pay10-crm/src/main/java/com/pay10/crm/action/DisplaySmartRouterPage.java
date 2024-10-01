package com.pay10.crm.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.pay10.commons.user.AccountCurrencyRegion;
import com.pay10.commons.user.CardHolderType;
import com.pay10.commons.user.Merchants;
import com.pay10.commons.user.MultCurrencyCode;
import com.pay10.commons.user.MultCurrencyCodeDao;
import com.pay10.commons.user.User;
import com.pay10.commons.user.UserDao;
import com.pay10.commons.util.Constants;

/**
 * @author Rahul
 *
 */
public class DisplaySmartRouterPage extends AbstractSecureAction {

	private static final long serialVersionUID = -3054395368649186178L;
	private static Logger logger = LoggerFactory.getLogger(DisplaySmartRouterPage.class.getName());

	private Map<String, String> merchantList = new TreeMap<String, String>();
	private Map<String, String> regionTypeList = new TreeMap<String, String>();
	private Map<String, String> cardTypeList = new TreeMap<String, String>();
	private Map<String, String> currencyMapList = new TreeMap<String, String>();

	@Autowired
	private UserDao userDao;
	private User sessionUser = null;
	
	@Autowired
	private MultCurrencyCodeDao currencyCodeDao;


	@SuppressWarnings("unchecked")
	@Override
	public String execute() {

		try {
			sessionUser = (User) sessionMap.get(Constants.USER.getValue());
			List<Merchants> merchantsList = new ArrayList<Merchants>();
			Map<String, String> merchantMap = new HashMap<String, String>();
			List<MultCurrencyCode> currencyList = new ArrayList<MultCurrencyCode>();
			Map<String, String> currencyMap = new HashMap<String, String>();

			//merchantsList = userDao.getActiveMerchantList();
			merchantsList = userDao.getActiveMerchantList(sessionMap.get(Constants.SEGMENT.getValue()).toString(), sessionUser.getRole().getId());
			currencyList= currencyCodeDao.getCurrencyCode();

			for (CardHolderType cardType :  CardHolderType.values()) {
				cardTypeList.put(cardType.name(), cardType.name());
			}
			
			for (AccountCurrencyRegion regionType :  AccountCurrencyRegion.values()) {
				regionTypeList.put(regionType.name(), regionType.name());
			}
			
			for (Merchants merchant : merchantsList) {

				merchantMap.put(merchant.getPayId(),merchant.getBusinessName());
			}
			for (MultCurrencyCode currency : currencyList) {
				currencyMap.put(currency.getCode(),currency.getName());
				
			}
			currencyMapList.putAll(currencyMap);
			logger.info("currencyMapList....={}",currencyMapList);

			merchantList.putAll(merchantMap);
			return SUCCESS;

		}

		catch (Exception e) {

			logger.error("Exception in execute()  ==  "+e);
		}
		return ERROR;

	}

	public Map<String, String> getMerchantList() {
		return merchantList;
	}

	public void setMerchantList(Map<String, String> merchantList) {
		this.merchantList = merchantList;
	}

	
	public Map<String, String> getCurrencyMapList() {
		return currencyMapList;
	}

	public void setCurrencyMapList(Map<String, String> currencyMapList) {
		this.currencyMapList = currencyMapList;
	}

	public Map<String, String> getRegionTypeList() {
		return regionTypeList;
	}

	public void setRegionTypeList(Map<String, String> regionTypeList) {
		this.regionTypeList = regionTypeList;
	}

	public Map<String, String> getCardTypeList() {
		return cardTypeList;
	}

	public void setCardTypeList(Map<String, String> cardTypeList) {
		this.cardTypeList = cardTypeList;
	}

}