package com.pay10.crm.reseller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.pay10.commons.action.AbstractSecureAction;
import com.pay10.commons.user.ChargingDetailsDao;
import com.pay10.commons.user.Merchants;
import com.pay10.commons.user.MultCurrencyCodeDao;
import com.pay10.commons.user.Resellerdailyupdate;
import com.pay10.commons.user.TdrSettingDao;
import com.pay10.commons.user.User;
import com.pay10.commons.user.UserCommissionDao;
import com.pay10.commons.user.UserDao;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.TxnType;

/**
 * Sweety
 */
public class ResellerDailyUpdatePageAction extends AbstractSecureAction {

	private static final long serialVersionUID = -5103892667503253761L;
	private static final Logger logger = LoggerFactory.getLogger(ResellerDailyUpdatePageAction.class.getName());

	private String resellerId;
	private List<String> paymentTypeList = new ArrayList<String>();
	private List<String> mopTypeList = new ArrayList<String>();
	private String mopType;
	private String paymentType;
	private String transactionType;
	private String dateFrom;
	private String dateTo;

	private List<User> resellerList = new ArrayList<User>();
	private List<User> merchantlist = new ArrayList<User>();
	private List<TxnType> txnTypelist = new ArrayList<TxnType>();

	private List<String> merchantarreList = new ArrayList<String>();
	private List<String> merchantarrename = new ArrayList<String>();

	private List<String> merchantbusname = new ArrayList<String>();

	private String merchantname;

	private List<String> merchantid = new ArrayList<String>();

	private List<Resellerdailyupdate> resellerDailyUpdate = new ArrayList<Resellerdailyupdate>();

	@Autowired
	private UserDao userDao;

	@Autowired
	private TdrSettingDao chargingDetailsDao;
	private String userType;
	private List<String> resellerarreList = new ArrayList<String>();
	private List<String> resellerarrename = new ArrayList<String>();

	private List<String> resellerbusname = new ArrayList<String>();
	private List<String> resellerid = new ArrayList<String>();
	
	@Autowired
	private MultCurrencyCodeDao multCurrencyCodeDao;
	@Autowired
	private UserCommissionDao commissiondao;
	
	Map<String, String> currencyMapList = new HashMap<String, String>();
	private List<String> currencyCode = new ArrayList<String>();
	private List<String> currencyName = new ArrayList<String>();
	
	private List<String> currencyCodearreList = new ArrayList<String>();
	private List<String> currencyarrename = new ArrayList<String>();
	private String merchantPayId;
	
	@SuppressWarnings("unchecked")
	@Override
	public String execute() {
		User user = (User) sessionMap.get(Constants.USER.getValue());
		User reseller = new User();
		List<User> resellers = new ArrayList<>();
		Map<String, String> currencyMap = new HashMap<String, String>();
	    
		if (StringUtils.equalsIgnoreCase(user.getUserGroup().getGroup(), "SMA")) {
			reseller.setPayId(user.getPayId());
			reseller.setBusinessName(user.getBusinessName());
			reseller.setEmailId(user.getEmailId());
			resellers.add(reseller);
			 setResellerList(resellers);
			logger.info("resellerList....={}", getResellerList().toString());
		} 
		if (StringUtils.equalsIgnoreCase(user.getUserGroup().getGroup(), "MA")) {
			reseller.setPayId(user.getPayId());
			reseller.setBusinessName(user.getBusinessName());
			reseller.setEmailId(user.getEmailId());
			resellers.add(reseller);
			 setResellerList(resellers);
			logger.info("resellerList....={}", getResellerList().toString());
		}
		if (StringUtils.equalsIgnoreCase(user.getUserGroup().getGroup(), "Agent")) {
			reseller.setPayId(user.getPayId());
			reseller.setBusinessName(user.getBusinessName());
			reseller.setEmailId(user.getEmailId());
			resellers.add(reseller);
			 setResellerList(resellers);
			logger.info("resellerList....={}", getResellerList().toString());
		}
	    	else {
	    		if(userType!=null) {
		     setResellerList(userDao.getResellersbyUserType(userType));
		     setMerchantlist(userDao.getResellersbyId(userType,resellerId));
	    	
		
	   
		setTxnTypelist(TxnType.gettxnType());

		logger.info("resellerId..." + resellerId);

		logger.info("Inside Reseller Daily update....." + getMerchantlist());
		
		List<String> getCurrency=commissiondao.findByUserdiffId(userType,resellerId,merchantPayId);
	    	
		for(String currency:getCurrency) {
			String cName = multCurrencyCodeDao.getCurrencyNamebyCode(currency);
			currencyMap.put(currency,cName);
		}

		currencyMapList.putAll(currencyMap);
		logger.info("currencyMapList....={}",currencyMapList);
	    		}
	    	}  
		return SUCCESS;
	}

	public String displayList() {
		try {

			List<User> merchantsByReseller = userDao.getResellersbyId(userType,resellerId);

			for (User merchantdat : merchantsByReseller) {
				String merchantpayid = merchantdat.getPayId();

				String merchantname = merchantdat.getBusinessName();

				merchantid.add(merchantpayid);
				merchantbusname.add(merchantname);

			}
			setMerchantarreList(merchantid);
			setMerchantarrename(merchantbusname);
			logger.info("merchantarreList...." + getMerchantarreList());
			return SUCCESS;
		} catch (Exception exception) {
			logger.error("Exception ", exception);
			return ERROR;
		}
	}

	public String getpaymentType() {

		if (StringUtils.isBlank(getMerchantname())) {
			return SUCCESS;
		}
		logger.info("Merchant PayId={}", getMerchantname());
		setPaymentTypeList(chargingDetailsDao.findPaymentTypeByPayId(getMerchantname()));
		return SUCCESS;

	}

	public String getmopType() {

		if (StringUtils.isBlank(getMerchantname())) {
			return SUCCESS;
		}
		logger.info("Merchant PayId={}", getMerchantname());
		logger.info("Merchant PaymentType={}", getPaymentType());
		setMopTypeList(chargingDetailsDao.findmopbypaymenttype(getMerchantname(), getPaymentType()));
		Collections.sort(getMopTypeList());
		return SUCCESS;
	}
	
	public String resellerList() {
		logger.info("userType...={}",userType);
		try {

			List<User> resellerByUsertype = userDao.getResellersbyUserType(userType);

			for (User merchantdat : resellerByUsertype) {
				String merchantpayid = merchantdat.getPayId();

				String merchantname = merchantdat.getBusinessName();

				resellerid.add(merchantpayid);
				resellerbusname.add(merchantname);

			}
			setResellerarreList(resellerid);
			setResellerarrename(resellerbusname);
			logger.info("resellerarreList...." + getResellerarreList());
			return SUCCESS;
		} catch (Exception exception) {
			logger.error("Exception ", exception);
			return ERROR;
		}
	}
	
	public String getCurrencyList() {
		logger.info("userType...={},resellerId={},merchantId={}",getUserType(),getResellerId(),getMerchantname());
		try {
			List<String> currencylist = commissiondao.findByUserdiffId(getUserType(),getResellerId(),getMerchantname());
			currencyCode.clear();
			currencyName.clear();
			for (String currencydata : currencylist) {
				
				String cName = multCurrencyCodeDao.getCurrencyNamebyCode(currencydata);

				currencyCode.add(currencydata);
				currencyName.add(cName);

			}
			setCurrencyCodearreList(currencyCode);
			setCurrencyarrename(currencyName);
	
			logger.info("currencyList :::::::::::::={},currencyCode...={}",getCurrencyarrename(),getCurrencyCodearreList());
			return SUCCESS;
		} catch (Exception exception) {
			logger.error("Exception ", exception);
			return ERROR;
		}
	
	}

	/*
	 * public String getResellerDailyUpdates() {
	 * 
	 * logger.info("Merchant PayId={}", getMerchantname());
	 * logger.info("Merchant PaymentType={}", getPaymentType());
	 * logger.info("MopType={}", getMopType()); logger.info("from Date={}",
	 * getDateFrom()); logger.info("To Date={}", getDateTo());
	 * logger.info("ResellerId={}", getResellerId());
	 * logger.info("TransactionType={}", getTransactionType()); return SUCCESS; }
	 */

	public List<User> getMerchantlist() {
		return merchantlist;
	}

	public void setMerchantlist(List<User> merchantlist) {
		this.merchantlist = merchantlist;
	}

	public String getResellerId() {
		return resellerId;
	}

	public void setResellerId(String resellerId) {
		this.resellerId = resellerId;
	}

	public String getMopType() {
		return mopType;
	}

	public void setMopType(String mopType) {
		this.mopType = mopType;
	}

	public String getTransactionType() {
		return transactionType;
	}

	public List<String> getPaymentTypeList() {
		return paymentTypeList;
	}

	public void setPaymentTypeList(List<String> paymentTypeList) {
		this.paymentTypeList = paymentTypeList;
	}

	public void setTransactionType(String transactionType) {
		this.transactionType = transactionType;
	}

	public String getDateFrom() {
		return dateFrom;
	}

	public void setDateFrom(String dateFrom) {
		this.dateFrom = dateFrom;
	}

	public List<TxnType> getTxnTypelist() {
		return txnTypelist;
	}

	public void setTxnTypelist(List<TxnType> txnTypelist) {
		this.txnTypelist = txnTypelist;
	}

	public List<String> getMerchantarreList() {
		return merchantarreList;
	}

	public void setMerchantarreList(List<String> merchantarreList) {
		this.merchantarreList = merchantarreList;
	}

	public List<String> getMerchantarrename() {
		return merchantarrename;
	}

	public void setMerchantarrename(List<String> merchantarrename) {
		this.merchantarrename = merchantarrename;
	}

	public List<String> getMerchantbusname() {
		return merchantbusname;
	}

	public void setMerchantbusname(List<String> merchantbusname) {
		this.merchantbusname = merchantbusname;
	}

	public String getMerchantname() {
		return merchantname;
	}

	public void setMerchantname(String merchantname) {
		this.merchantname = merchantname;
	}

	public List<String> getMerchantid() {
		return merchantid;
	}

	public void setMerchantid(List<String> merchantid) {
		this.merchantid = merchantid;
	}

	public List<String> getMopTypeList() {
		return mopTypeList;
	}

	public void setMopTypeList(List<String> mopTypeList) {
		this.mopTypeList = mopTypeList;
	}

	public String getPaymentType() {
		return paymentType;
	}

	public void setPaymentType(String paymentType) {
		this.paymentType = paymentType;
	}

	public String getDateTo() {
		return dateTo;
	}

	public void setDateTo(String dateTo) {
		this.dateTo = dateTo;
	}

	public List<Resellerdailyupdate> getResellerDailyUpdate() {
		return resellerDailyUpdate;
	}

	public void setResellerDailyUpdate(List<Resellerdailyupdate> resellerDailyUpdate) {
		this.resellerDailyUpdate = resellerDailyUpdate;
	}

	public String getUserType() {
		return userType;
	}

	public void setUserType(String userType) {
		this.userType = userType;
	}

	public List<String> getResellerarreList() {
		return resellerarreList;
	}

	public void setResellerarreList(List<String> resellerarreList) {
		this.resellerarreList = resellerarreList;
	}

	public List<String> getResellerarrename() {
		return resellerarrename;
	}

	public void setResellerarrename(List<String> resellerarrename) {
		this.resellerarrename = resellerarrename;
	}

	public List<String> getResellerbusname() {
		return resellerbusname;
	}

	public void setResellerbusname(List<String> resellerbusname) {
		this.resellerbusname = resellerbusname;
	}

	public Map<String, String> getCurrencyMapList() {
		return currencyMapList;
	}

	public void setCurrencyMapList(Map<String, String> currencyMapList) {
		this.currencyMapList = currencyMapList;
	}

	public List<String> getCurrencyCode() {
		return currencyCode;
	}

	public void setCurrencyCode(List<String> currencyCode) {
		this.currencyCode = currencyCode;
	}

	public List<String> getCurrencyName() {
		return currencyName;
	}

	public void setCurrencyName(List<String> currencyName) {
		this.currencyName = currencyName;
	}

	public List<String> getCurrencyCodearreList() {
		return currencyCodearreList;
	}

	public void setCurrencyCodearreList(List<String> currencyCodearreList) {
		this.currencyCodearreList = currencyCodearreList;
	}

	public List<String> getCurrencyarrename() {
		return currencyarrename;
	}

	public void setCurrencyarrename(List<String> currencyarrename) {
		this.currencyarrename = currencyarrename;
	}

	public List<User> getResellerList() {
		return resellerList;
	}

	public void setResellerList(List<User> resellerList) {
		this.resellerList = resellerList;
	}

	public String getMerchantPayId() {
		return merchantPayId;
	}

	public void setMerchantPayId(String merchantPayId) {
		this.merchantPayId = merchantPayId;
	}

}
