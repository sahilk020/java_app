package com.pay10.crm.action;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.pay10.commons.dao.RouterConfigurationDao;
import com.pay10.commons.dao.RouterRuleDao;
import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.user.Merchants;
import com.pay10.commons.user.MultCurrencyCode;
import com.pay10.commons.user.MultCurrencyCodeDao;
import com.pay10.commons.user.RouterConfiguration;
import com.pay10.commons.user.User;
import com.pay10.commons.user.UserDao;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.MopType;
import com.pay10.commons.util.PaymentType;

public class RouterConfigurationAction extends AbstractSecureAction {
	private List<RoutingTypeDto> routingTypeDto = new ArrayList<RoutingTypeDto>();

	@Autowired
	private UserDao userDao;

	@Autowired
	private RouterRuleDao routerRuleDao;

	@Autowired
	private RouterConfigurationDao routerConfigurationDao;

	private static Logger logger = LoggerFactory.getLogger(RouterConfigurationAction.class.getName());
	private static final long serialVersionUID = -6879974923614009981L;

	public List<Merchants> listMerchant = new ArrayList<Merchants>();
	private Map<String, String> merchantList = new TreeMap<String, String>();
	private Map<String, String> currencyMapList = new TreeMap<String, String>();

	
	public String merchantName;
	public String paymentMethod;
	public String cardHolderType;
	public String currencyMethod;
	
	@Autowired
	private MultCurrencyCodeDao currencyCodeDao;

	private Map<String, List<RouterConfiguration>> routerRuleData = new HashMap<String, List<RouterConfiguration>>();
	private User sessionUser = null;
	
	@Override
	@SuppressWarnings("unchecked")
	public String execute() {

		try {
			sessionUser = (User) sessionMap.get(Constants.USER.getValue());
			List<Merchants> merchantsList = new ArrayList<Merchants>();
			Map<String, String> merchantMap = new HashMap<String, String>();

			//merchantsList = userDao.getActiveMerchantList();
			merchantsList = userDao.getActiveMerchantList(sessionMap.get(Constants.SEGMENT.getValue()).toString(), sessionUser.getRole().getId());

			List<MultCurrencyCode> currencyList = new ArrayList<MultCurrencyCode>();
			Map<String, String> currencyMap = new HashMap<String, String>();

			currencyList= currencyCodeDao.getCurrencyCode();

			for (Merchants merchant : merchantsList) {

				merchantMap.put(merchant.getPayId(),merchant.getBusinessName());
			}
			merchantList.putAll(merchantMap);

			for (MultCurrencyCode currency : currencyList) {

				currencyMap.put(currency.getCode(),currency.getName());
			}
			currencyMapList.putAll(currencyMap);

			Map<String, List<RouterConfiguration>> routerRuleDataMap = new HashMap<String, List<RouterConfiguration>>();

			List<RouterConfiguration> routerConfigurationListAll = new ArrayList<RouterConfiguration>();

			routerConfigurationListAll = routerConfigurationDao.getActiveRulesByMerchant(getMerchantName(),getCardHolderType(),getPaymentMethod(),getCurrencyMethod());

			Set<String> uniqueKeySet = new HashSet<String>();

			for (RouterConfiguration routerConfiguration : routerConfigurationListAll) {

				String key = routerConfiguration.getPaymentType() + "-" + routerConfiguration.getMopType() + "-"
						+ routerConfiguration.getMerchant() + "-" + routerConfiguration.getTransactionType() + "-"
						+ routerConfiguration.getCurrency()+ "-"+routerConfiguration.getPaymentsRegion()+ "-"+routerConfiguration.getCardHolderType()+ "-"+routerConfiguration.getSlabId();

				uniqueKeySet.add(key);
			}

			DecimalFormat decimalFormat=new DecimalFormat("0.000");
			
			for (String uniqueKey : uniqueKeySet) {
				List<RouterConfiguration> routerConfigurationList = new ArrayList<RouterConfiguration>();

				for (RouterConfiguration routerConfiguration : routerConfigurationListAll) {

					String key = routerConfiguration.getPaymentType() + "-" + routerConfiguration.getMopType() + "-"
							+ routerConfiguration.getMerchant() + "-" + routerConfiguration.getTransactionType() + "-"
							+ routerConfiguration.getCurrency()+ "-"+routerConfiguration.getPaymentsRegion()+ "-"+routerConfiguration.getCardHolderType()+ "-"+routerConfiguration.getSlabId();
					
					//User user = userDao.findPayId(routerConfiguration.getMerchant());
					String currencyName = routerConfiguration.getCurrency();
					if (key.equalsIgnoreCase(uniqueKey)) {

						String paymentTypeName = PaymentType.getpaymentName(routerConfiguration.getPaymentType());
						String mopTypeName = MopType.getmopName(routerConfiguration.getMopType());

						routerConfiguration.setPaymentTypeName(paymentTypeName);
						routerConfiguration.setMopTypeName(mopTypeName);
						routerConfiguration.setCurrencyName(currencyName);


						if (routerConfiguration.isCurrentlyActive()) {
							routerConfiguration.setStatusName("ACTIVE");
						}

						else {
							routerConfiguration.setStatusName("STAND BY");
						}
						
						routerConfiguration.setTotalTxn((routerConfiguration.getTotalTxn()==0)?5000000:routerConfiguration.getTotalTxn());
						routerConfiguration.setFailedCount((routerConfiguration.getFailedCount()==0)?51:routerConfiguration.getFailedCount());
						decimalFormat.format(routerConfiguration.getTotalTxn());
						
						routerConfigurationList.add(routerConfiguration);
					}

				}
				
				routerRuleDataMap.put(uniqueKey, routerConfigurationList);
			}

			 List<RoutingTypeDto> routingTypeDto1 = new ArrayList<RoutingTypeDto>();
			 RoutingTypeDto routingType=new RoutingTypeDto("NormalBase", "NormalBase");
			 RoutingTypeDto routingType1=new RoutingTypeDto("sequenceBase", "sequenceBase");
			 RoutingTypeDto routingType2=new RoutingTypeDto("AmountBase", "AmountBase");
			 routingTypeDto1.add(routingType);
			 routingTypeDto1.add(routingType1);
			 routingTypeDto1.add(routingType2);

			 
			setRoutingTypeDto(routingTypeDto1);
			setRouterRuleData(routerRuleDataMap);
			return SUCCESS;

		} catch (Exception exception) {
			logger.error("Exception", exception);
			addActionMessage(ErrorType.UNKNOWN.getResponseMessage());
		}

		return INPUT;
	}

	@Override
	public void validate() {

	}

	public List<Merchants> getListMerchant() {
		return listMerchant;
	}

	public void setListMerchant(List<Merchants> listMerchant) {
		this.listMerchant = listMerchant;
	}

	public Map<String, List<RouterConfiguration>> getRouterRuleData() {
		return routerRuleData;
	}

	public String getCurrencyMethod() {
		return currencyMethod;
	}

	public void setCurrencyMethod(String currencyMethod) {
		this.currencyMethod = currencyMethod;
	}

	public void setRouterRuleData(Map<String, List<RouterConfiguration>> routerRuleData) {
		this.routerRuleData = routerRuleData;
	}

	public String getMerchantName() {
		return merchantName;
	}

	public void setMerchantName(String merchantName) {
		this.merchantName = merchantName;
	}

	public Map<String, String> getMerchantList() {
		return merchantList;
	}

	public void setMerchantList(Map<String, String> merchantList) {
		this.merchantList = merchantList;
	}

	public String getPaymentMethod() {
		return paymentMethod;
	}

	public void setPaymentMethod(String paymentMethod) {
		this.paymentMethod = paymentMethod;
	}
	
	public Map<String, String> getCurrencyMapList() {
		return currencyMapList;
	}

	public void setCurrencyMapList(Map<String, String> currencyMapList) {
		this.currencyMapList = currencyMapList;
	}


	public String getCardHolderType() {
		return cardHolderType;
	}

	public void setCardHolderType(String cardHolderType) {
		this.cardHolderType = cardHolderType;
	}

	public List<RoutingTypeDto> getRoutingTypeDto() {
		return routingTypeDto;
	}

	public void setRoutingTypeDto(List<RoutingTypeDto> routingTypeDto) {
		this.routingTypeDto = routingTypeDto;
	}

	

}
