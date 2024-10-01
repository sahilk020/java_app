package com.pay10.crm.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.pay10.commons.user.*;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.gson.Gson;
import com.pay10.commons.dao.ChargingDetailsFactory;
import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.util.AcquirerType;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.CrmFieldType;
import com.pay10.commons.util.CrmValidator;

public class TdrSettingMap extends AbstractSecureAction {

	@Autowired
	private ChargingDetailsFactory chargingDetailProvider;

	@Autowired
	private MultCurrencyCodeDao multCurrencyCodeDao;
	
	@Autowired
	private UserDao userDao;
	
	@Autowired
	private CrmValidator validator;
	
	private static Logger logger = LoggerFactory
			.getLogger(TdrSettingMap.class.getName());
	private static final long serialVersionUID = -6879974923614009981L;
	private List<MultCurrencyCode> currencyMap = new ArrayList<MultCurrencyCode>();
	public List<Merchants> listMerchant = new ArrayList<Merchants>();
	private Map<String, List<TdrSetting>> aaData = new HashMap<String, List<TdrSetting>>();
	private String emailId;
	private String acquirer;
	private String paymentRegion;
	private String cardHolderType;
	private boolean netbankingMaster;
	private User sessionUser = null;
	private String payId;
	private String igst;
	private String paymentType;
	private String currencyData;

	
	//Added By Sweety
	public List<String> acquirerList = new ArrayList<String>();

	@Autowired
	private MultCurrencyCodeDao currencyCodeDao;
	
	@Override
	@SuppressWarnings("unchecked")
	public String execute() {
		
		if(StringUtils.isNotBlank(getEmailId())) {
		sessionUser = (User) sessionMap.get(Constants.USER.getValue());
		logger.info("TDR Charging Details File request :: ");
		setListMerchant(userDao.getMerchantList(sessionMap.get(Constants.SEGMENT.getValue()).toString(), sessionUser.getRole().getId()));
		//setListMerchant(userDao.getMerchantList());
		List<String> acquirerList=userDao.getAcquirerTypeListFromTdrSetting(userDao.getPayIdByEmailId(emailId));
		List<String> list = new ArrayList<String>();

//		setCurrencyMap(currencyCodeDao.getCurrencyCode());
		logger.info("new Gson().toJson(currencyMap) :::"+new Gson().toJson(currencyMap));
//		list.add(0, "Select Acquirer");
		for (String acquirer : acquirerList) {
			list.add(AcquirerType.getInstancefromName(acquirer).getCode());
		}
		setAcquirerList(list);

		List<String> currencyListDao1 = userDao.findByAcquireAndPayId(acquirer, emailId);
		logger.info("currencyListDao1 :::::"+currencyListDao1);
		List<MultCurrencyCode> currencyCodes = currencyCodeDao.getCurrencyCode();
		logger.info("currencyCodes :::::"+currencyCodes);

		List<String> currencyDropdown = new ArrayList<>();
//		setCurrencyMap(currencyCodes.stream().filter(multCurrencyCode ->
//			 currencyListDao1.contains(multCurrencyCode.getCode())).collect(Collectors.toList()));

		List<MultCurrencyCode> finalCurrency = new ArrayList<>();
		for (MultCurrencyCode data : currencyCodes){

			logger.info("Data:::::::::"+data.getCode());
			if (currencyListDao1.contains(data.getCode())){
				logger.info("True");
				finalCurrency.add(data);
			}
		}

		logger.info("Final Currency :::: "+finalCurrency);
		setCurrencyMap(finalCurrency);



		try {
			if (emailId != null && acquirer != null) {
				logger.info("Payment Region :::"+paymentRegion);
				setAaData(chargingDetailProvider.getTdrSettingMap(emailId,
						acquirer,paymentRegion,cardHolderType,currencyData));
				logger.info("AaData ::: "+getAaData());
				
				Set<String> set = new HashSet<>();
		        List<String> duplicates = new ArrayList<>();
				
				for (Map.Entry<String, List<TdrSetting>> entry : aaData.entrySet()) {
					String key = entry.getKey();
					List<TdrSetting> val = entry.getValue();
					
					if (key.equalsIgnoreCase("NET_BANKING")) {
						for (TdrSetting string : val) {
							setPayId(string.getPayId());
							setIgst(""+string.getIgst());
							setPaymentType(string.getPaymentType());
							if (!set.add(string.getMopType())) {
				                duplicates.add(string.getMopType());
				            }
						}
					}
					
					
				}
			
				setNetbankingMaster(!duplicates.isEmpty()?true:false);
				logger.info("NetbankingMaster :"+isNetbankingMaster());
				
			}
		} catch (Exception exception) {
			logger.error("Exception", exception);
			addActionMessage(ErrorType.LOCAL_TAX_RATES_NOT_AVAILABLE.getResponseMessage());
		}
		return INPUT;
		}
		else {
			return SUCCESS;
		}
	}

	// To display page without using token
	@SuppressWarnings("unchecked")
	public String displayList() {
		sessionUser = (User) sessionMap.get(Constants.USER.getValue());
//		setCurrencyMap(currencyCodeDao.getCurrencyCode());
		logger.info(new Gson().toJson(currencyMap));
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
	
	public String getCurrencyData() {
		return currencyData;
	}

	public void setCurrencyData(String currencyData) {
		this.currencyData = currencyData;
	}

	public List<MultCurrencyCode> getCurrencyMap() {
		return currencyMap;
	}

	public void setCurrencyMap(List<MultCurrencyCode> currencyMap) {
		this.currencyMap = currencyMap;
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

	public Map<String, List<TdrSetting>> getAaData() {
		return aaData;
	}

	public void setAaData(Map<String, List<TdrSetting>> aaData) {
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

	public boolean isNetbankingMaster() {
		return netbankingMaster;
	}

	public void setNetbankingMaster(boolean netbankingMaster) {
		this.netbankingMaster = netbankingMaster;
	}

	public String getPayId() {
		return payId;
	}

	public void setPayId(String payId) {
		this.payId = payId;
	}

	public String getIgst() {
		return igst;
	}

	public void setIgst(String igst) {
		this.igst = igst;
	}

	public String getPaymentType() {
		return paymentType;
	}

	public void setPaymentType(String paymentType) {
		this.paymentType = paymentType;
	}

	
	
}
