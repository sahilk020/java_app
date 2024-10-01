package com.pay10.crm.fraudRiskManagement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.pay10.commons.user.*;
import com.pay10.commons.util.*;
import com.pay10.crm.fraudPrevention.actionBeans.FraudAmountBalanceChecker;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.gson.Gson;
import com.pay10.commons.action.AbstractSecureAction;
import com.pay10.commons.dao.FraudPreventionMongoService;
import com.pay10.pg.core.fraudPrevention.model.FraudRuleModel;

public class FraudRiskManagement extends AbstractSecureAction {

	/**
	 * Sweety
	 */
	private static final long serialVersionUID = -9194285172796237446L;

	private List<Merchants> listMerchants = new ArrayList<Merchants>();

	private Map<String,String> paymentList = new HashMap<>();

	private Map<String,String> currencyList =new HashMap<>();

	public List<String> currencyListDao = new ArrayList<String>();

	public List<String> paymentListDao = new ArrayList<String>();
	private static Logger logger = LoggerFactory.getLogger(FraudRiskManagement.class.getName());

	@Autowired
	private UserDao userDao;

	@Autowired
	private MultCurrencyCodeDao multCurrencyCodeDao;

	@Autowired
	private FraudAmountBalanceChecker fraudAmountBalanceChecker;
	private User sessionUser = null;
	private String merchantPayId;
	private String minTicketSize;
	private String maxTicketSize;
	private String daily;
	private String weekly;
	private String monthly;
	private String merchantProfile;
	private String mop;
	private String mopDaily;
	private String mopWeekly;
	private String mopMonthly;
	private String id;
	private String ticketId;
	private String volumeId;


	private String selectedCurrency;

	private String selectedPaymentType;

	private String displaySymbol;




	private FraudRuleModel fraudRuleModel = new FraudRuleModel();

	FraudPreventionObj fraudPrevention = new FraudPreventionObj();
	private List<FraudRiskModel> fraudObj = new ArrayList<FraudRiskModel>();
	private List<FraudRiskModel> FraudDataByVolume= new ArrayList<FraudRiskModel>();
	private List<FraudRiskModel> FraudDataByTicket= new ArrayList<FraudRiskModel>();
	
	@Autowired
	private FraudPreventionMongoService fraudPreventionMongoService;

	@SuppressWarnings("unchecked")
	public String execute() {
		try {
			sessionUser = (User) sessionMap.get(Constants.USER.getValue());
			setListMerchants(userDao.getMerchantList(sessionMap.get(Constants.SEGMENT.getValue()).toString(),
					sessionUser.getRole().getId()));


			return SUCCESS;
		} catch (Exception exception) {
			logger.error("Exception ", exception);
			return ERROR;
		}
	}
	public String getCurrencyListTdr() {

		logger.info("Merchant PayId :::"+merchantPayId);
		logger.info("getMerchantPayId() :::"+getMerchantPayId());
		currencyListDao = userDao.findCurrencyByPayId(getMerchantPayId());
		logger.info("currencyListDao ::::"+currencyListDao);
		for (String currencyCode : currencyListDao){
			String currencyName  = multCurrencyCodeDao.getCurrencyNamebyCode(currencyCode);
			logger.info("currencyName :::"+currencyName);
			currencyList.put(currencyCode,currencyName);
		}
		logger.info("Currency List :::"+currencyList);
		setCurrencyList(currencyList);


		return SUCCESS;
	}



	public String saveFrmDetails() {

		logger.info("inside Fraud RiskManagement........");
		logger.info(
				"merchantPayId...={},minTicketSize...={},maxTicketSize...={},daily...={},weekly...={},monthly...={},merchantProfile..={},mop..={},mopDaily..={},mopWeekly..={},mopMonthly...={},selectedCurrency...={}",
				getMerchantPayId(), getMinTicketSize(), getMaxTicketSize(), getDaily(), getWeekly(), getMonthly(),
				getMerchantProfile(), getMop(), getMopDaily(), getMopWeekly(), getMopMonthly(),getSelectedCurrency());

		String id= getId(); 
		String volumeId=getVolumeId();
		String ticketId=getTicketId();
		
		fraudPrevention.setPayId(getMerchantPayId());
		fraudPrevention.setStatus(TDRStatus.ACTIVE);
		fraudPrevention.setCurrency(getSelectedCurrency());
		fraudPrevention.setFixedAmountFlag(fraudRuleModel.isFixedAmountFlag());
		fraudPrevention.setRepetationCount(fraudRuleModel.getRepetationCount());
		fraudPrevention.setAlwaysOnFlag(true);
		fraudPrevention.setMerchantProfile(getMerchantProfile());
		FraudRuleType fraudRuleType = FraudRuleType.getInstance("BLOCK_TICKET_LIMIT");

		FraudRuleType blockTxn = FraudRuleType.getInstance("BLOCK_TRANSACTION_LIMIT");
		FraudRuleType block_mop = FraudRuleType.getInstance("BLOCK_MOP_LIMIT");
		

		boolean isExist = fraudPreventionMongoService.CheckFrmRuleExist(getMerchantPayId(),selectedCurrency);
		if (isExist && StringUtils.isNotBlank(ticketId)) {
			//TODO UPDATE Ticket Timit
			addTicketLimit(fraudRuleType, ticketId, true);
		}else {
			//TODO Add Ticket Timit
			addTicketLimit(fraudRuleType, ticketId, false);
		}
		if (isExist && StringUtils.isNotBlank(volumeId)) {
			//TODO Update Txn Amount
			addFRMTxnLimit(blockTxn, volumeId, true);
		}else {
			//TODO Add Txn Amount
			addFRMTxnLimit(blockTxn, volumeId, false);
		}
		if (isExist && StringUtils.isNotBlank(id)) {
			//TODO Update MOP Amount
			addFRMMopLimit(block_mop, id,true);
		}else {
			//TODO Add Txn Amount
			addFRMMopLimit(block_mop, id,false);
		}
		
		return SUCCESS;

	}

	public String getFrmDetails() {

		logger.info("Merchant PayId :::"+merchantPayId);
		logger.info("GET Merchant PayId ::"+getMerchantPayId()+ "Selected Currency ::"+getSelectedCurrency());
		paymentListDao = userDao.findPaymentTypeByPayIdAndCurrency(getMerchantPayId(),getSelectedCurrency());

		if(StringUtils.isNotBlank(getSelectedCurrency())){
			String currencySymbol = multCurrencyCodeDao.getSymbolbyCode(getSelectedCurrency());
			logger.info("Currency Symbol :::"+currencySymbol);
			setDisplaySymbol(currencySymbol);
		}

		for (String data : paymentListDao){
			PaymentType paymentType = PaymentType.getInstanceUsingStringValue(data);
			paymentList.put(paymentType.getCode(),paymentType.getName());
		}
		logger.info("Payment List Map ::: "+paymentList);
		setFraudObj(fraudPreventionMongoService.getFrmDetails(getMerchantPayId(),getSelectedCurrency()));
		setPaymentList(paymentList);
		// logger.info("getFrm Details..={}", new Gson().toJson(getFraudObj()));



		for(FraudRiskModel fraudRiskModel : getFraudObj()) {

			double dailyTransact = 0;
			double weeklyTransact = 0;
			double monthlyTransact = 0;

			Document data = fraudAmountBalanceChecker.getPaymentBalance(getMerchantPayId(),getSelectedCurrency(),fraudRiskModel.getPaymentType());
			if(data!=null) {
				dailyTransact = data.getDouble("dailyLimit");
				weeklyTransact = data.getDouble("weeklyLimit");
				monthlyTransact = data.getDouble("monthlyLimit");

			}
			fraudRiskModel.setDisplayDailyQuota(String.format("%.2f", Double.parseDouble(fraudRiskModel.getDailyAmount()) - dailyTransact));
			fraudRiskModel.setDisplayWeeklyQuota(String.format("%.2f", Double.parseDouble(fraudRiskModel.getWeeklyAmount()) - weeklyTransact));
			fraudRiskModel.setDisplayMonthlyQuota(String.format("%.2f", Double.parseDouble(fraudRiskModel.getMonthlyAmount()) - monthlyTransact));

		}
		return SUCCESS;
	}

	public String getFrmDetailsByVolume() {
		logger.info("PaymentType :::"+selectedPaymentType);
		
		setFraudDataByVolume(fraudPreventionMongoService.getFraudDataByVolume(getMerchantPayId(),getSelectedCurrency()));
	   	logger.info("getFrmDetailsByVolume..={}", new Gson().toJson(getFraudDataByVolume()));

		for(FraudRiskModel fraudRiskModel : getFraudDataByVolume()) {

			long dailyVolume = 0;
			long weeklyVolume = 0;
			long monthlyVolume = 0;
			Document data = fraudAmountBalanceChecker.getPaymentBalance(getMerchantPayId(),getSelectedCurrency(),selectedPaymentType);
			if(data!=null) {

				dailyVolume = data.getLong("dailyVolume");
				weeklyVolume = data.getLong("weeklyVolume");
				monthlyVolume = data.getLong("monthlyVolume");
			}

			fraudRiskModel.setDisplayDailyVolumeQuota(Long.toString(Integer.parseInt(fraudRiskModel.getDailyAmount()) - dailyVolume));
			fraudRiskModel.setDisplayWeeklyVolumeQuota(Long.toString(Integer.parseInt(fraudRiskModel.getWeeklyAmount()) - weeklyVolume));
			fraudRiskModel.setDisplayMonthlyVolumeQuota(Long.toString(Integer.parseInt(fraudRiskModel.getMonthlyAmount()) - monthlyVolume));

		}
		return SUCCESS;
	}

	
	public String getFrmDetailsByTicket() {
		
		setFraudDataByTicket(fraudPreventionMongoService.getFraudDataByTicket(getMerchantPayId(),getSelectedCurrency()));
	   logger.info("getFrmDetailsByVolume..={}", new Gson().toJson(getFraudDataByVolume()));
		return SUCCESS;
	}
	
	private void addTicketLimit(FraudRuleType fraudRuleType,String ticketId, boolean updateFlag) {
		try {
			if(ticketId == "") {
			fraudPrevention.setId(TransactionManager.getNewTransactionId());
			}
			fraudPrevention.setFraudType(fraudRuleType);
			fraudPrevention.setMinTransactionAmount(getMinTicketSize());
			fraudPrevention.setMaxTransactionAmount(getMaxTicketSize());
			
			fraudPrevention.setBlockDailyType("N");
			fraudPrevention.setBlockWeeklyType("N");
			fraudPrevention.setBlockMonthlyType("N");
			
			if (StringUtils.isNotBlank(ticketId) && updateFlag) {
				fraudPrevention.setId(ticketId);
				fraudPreventionMongoService.updateAdminFraudRule(fraudPrevention);
				return;
			}
			fraudPreventionMongoService.create(fraudPrevention);
			
			} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void addFRMMopLimit(FraudRuleType block_mop, String id, boolean updateFlag) {
		try {
			if(id== "") {
				fraudPrevention.setId(TransactionManager.getNewTransactionId());	
			}
			fraudPrevention.setFraudType(block_mop);
			fraudPrevention.setPaymentType(getMop());
			fraudPrevention.setDailyAmount(getMopDaily());
			fraudPrevention.setBlockDaily("Daily");

			fraudPrevention.setWeeklyAmount(getMopWeekly());
			fraudPrevention.setBlockWeekly("Weekly");

			fraudPrevention.setMonthlyAmount(getMopMonthly());
			fraudPrevention.setBlockMonthly("Monthly");
			
			fraudPrevention.setBlockDailyType("N");
			fraudPrevention.setBlockWeeklyType("N");
			fraudPrevention.setBlockMonthlyType("N");

			if (StringUtils.isNotBlank(id) && updateFlag) {
				fraudPrevention.setId(id);
				fraudPreventionMongoService.updateAdminFraudRule(fraudPrevention);
				return;
			}
			
			fraudPreventionMongoService.create(fraudPrevention);
			

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void addFRMTxnLimit(FraudRuleType blockTxn,String volumeId, boolean updateFlag) {
		try {
			if(volumeId=="") {
			fraudPrevention.setId(TransactionManager.getNewTransactionId());
			}
			fraudPrevention.setFraudType(blockTxn);
			fraudPrevention.setBlockDaily("Daily");
			fraudPrevention.setDailyAmount(getDaily());

			fraudPrevention.setBlockWeekly("Weekly");
			fraudPrevention.setWeeklyAmount(getWeekly());

			fraudPrevention.setBlockMonthly("Monthly");
			fraudPrevention.setMonthlyAmount(getMonthly());
			
			fraudPrevention.setBlockDailyType("N");
			fraudPrevention.setBlockWeeklyType("N");
			fraudPrevention.setBlockMonthlyType("N");

			if (StringUtils.isNotBlank(volumeId) && updateFlag) {
				fraudPrevention.setId(volumeId);
				fraudPreventionMongoService.updateAdminFraudRule(fraudPrevention);
				return;
			}
			
			fraudPreventionMongoService.create(fraudPrevention);
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public List<FraudRiskModel> getFraudDataByVolume() {
		return FraudDataByVolume;
	}

	public void setFraudDataByVolume(List<FraudRiskModel> fraudDataByVolume) {
		FraudDataByVolume = fraudDataByVolume;
	}
	public String getMop() {
		return mop;
	}

	public void setMop(String mop) {
		this.mop = mop;
	}

	public String getMopDaily() {
		return mopDaily;
	}

	public void setMopDaily(String mopDaily) {
		this.mopDaily = mopDaily;
	}

	public String getMopWeekly() {
		return mopWeekly;
	}

	public void setMopWeekly(String mopWeekly) {
		this.mopWeekly = mopWeekly;
	}

	public String getMopMonthly() {
		return mopMonthly;
	}

	public void setMopMonthly(String mopMonthly) {
		this.mopMonthly = mopMonthly;
	}

	public List<Merchants> getListMerchants() {
		return listMerchants;
	}

	public void setListMerchants(List<Merchants> listMerchants) {
		this.listMerchants = listMerchants;
	}

	public String getMerchantPayId() {
		return merchantPayId;
	}

	public void setMerchantPayId(String merchantPayId) {
		this.merchantPayId = merchantPayId;
	}

	public String getMinTicketSize() {
		return minTicketSize;
	}

	public String getTicketId() {
		return ticketId;
	}

	public void setTicketId(String ticketId) {
		this.ticketId = ticketId;
	}

	public String getVolumeId() {
		return volumeId;
	}

	public void setVolumeId(String volumeId) {
		this.volumeId = volumeId;
	}

	public void setMinTicketSize(String minTicketSize) {
		this.minTicketSize = minTicketSize;
	}

	public String getMaxTicketSize() {
		return maxTicketSize;
	}

	public void setMaxTicketSize(String maxTicketSize) {
		this.maxTicketSize = maxTicketSize;
	}

	public String getDaily() {
		return daily;
	}

	public void setDaily(String daily) {
		this.daily = daily;
	}

	public String getWeekly() {
		return weekly;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setWeekly(String weekly) {
		this.weekly = weekly;
	}

	public String getMonthly() {
		return monthly;
	}

	public void setMonthly(String monthly) {
		this.monthly = monthly;
	}

	public String getMerchantProfile() {
		return merchantProfile;
	}

	public void setMerchantProfile(String merchantProfile) {
		this.merchantProfile = merchantProfile;
	}

	public List<FraudRiskModel> getFraudObj() {
		return fraudObj;
	}

	public void setFraudObj(List<FraudRiskModel> fraudObj) {
		this.fraudObj = fraudObj;
	}
	
	public List<FraudRiskModel> getFraudDataByTicket() {
		return FraudDataByTicket;
	}

	public void setFraudDataByTicket(List<FraudRiskModel> fraudDataByTicket) {
		FraudDataByTicket = fraudDataByTicket;
	}

	public Map<String, String> getCurrencyList() {
		return currencyList;
	}

	public void setCurrencyList(Map<String, String> currencyList) {
		this.currencyList = currencyList;
	}

	public List<String> getCurrencyListDao() {
		return currencyListDao;
	}

	public void setCurrencyListDao(List<String> currencyListDao) {
		this.currencyListDao = currencyListDao;
	}

	public String getSelectedCurrency() {
		return selectedCurrency;
	}

	public void setSelectedCurrency(String selectedCurrency) {
		this.selectedCurrency = selectedCurrency;
	}

	public Map<String,String> getPaymentList() {
		return paymentList;
	}

	public void setPaymentList(Map<String,String> paymentList) {
		this.paymentList = paymentList;
	}

	public String getSelectedPaymentType() {
		return selectedPaymentType;
	}

	public void setSelectedPaymentType(String selectedPaymentType) {
		this.selectedPaymentType = selectedPaymentType;
	}

	public String getDisplaySymbol() {
		return displaySymbol;
	}

	public void setDisplaySymbol(String displaySymbol) {
		this.displaySymbol = displaySymbol;
	}
}
