package com.pay10.crm.reseller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.pay10.commons.dao.ResellerDao;
import com.pay10.commons.user.Merchants;
import com.pay10.commons.user.MultCurrencyCodeDao;
import com.pay10.commons.user.ResellerPayout;
import com.pay10.commons.user.User;
import com.pay10.commons.user.UserCommissionDao;
import com.pay10.commons.user.UserDao;
import com.pay10.commons.util.Constants;
import com.pay10.crm.action.AbstractSecureAction;

/**
 * Sweety
 */

public class ResellerPayoutPageAction extends AbstractSecureAction {

	private static final long serialVersionUID = 2093775329549647338L;
	private static final Logger logger = LoggerFactory.getLogger(ResellerPayoutPageAction.class.getName());

	private List<Merchants> listReseller = new ArrayList<Merchants>();
	private List<ResellerPayout> resellerPayout = new ArrayList<ResellerPayout>();
	private ResellerPayout updateResellerPayout;

	private String resellerId;
	private String fromDate;
	private String toDate;
	private String id;
	private String utrNo;
	private String settlementDate;
	private String tds;

	@Autowired
	UserDao userDao;

	@Autowired
	ResellerDao resellerDao;
	private List<String> resellerarreList = new ArrayList<String>();
	private List<String> resellerarrename = new ArrayList<String>();

	private List<String> resellerbusname = new ArrayList<String>();
	private List<String> resellerid = new ArrayList<String>();
	Map<String, String> currencyMapList = new HashMap<String, String>();
	private List<String> currencyCode = new ArrayList<String>();
	private List<String> currencyName = new ArrayList<String>();
	
	private List<String> currencyCodearreList = new ArrayList<String>();
	private List<String> currencyarrename = new ArrayList<String>();
	private String userType;
	private String currency;

	private String batchNo;
	
	@Autowired
	private MultCurrencyCodeDao multCurrencyCodeDao;
	@Autowired
	private UserCommissionDao commissiondao;
	
	private List<Merchants> resellerData = new ArrayList<Merchants>();

	public String execute() {

		User user = (User) sessionMap.get(Constants.USER.getValue());
		Merchants reseller = new Merchants();
		List<Merchants> resellers = new ArrayList<>();
		if (StringUtils.equalsIgnoreCase(user.getUserGroup().getGroup(), "SMA")) {
			reseller.setPayId(user.getPayId());
			reseller.setBusinessName(user.getBusinessName());
			reseller.setEmailId(user.getEmailId());
			resellers.add(reseller);
			setListReseller(resellers);
			logger.info("resellerList....={}", getListReseller().toString());
		} 
		if (StringUtils.equalsIgnoreCase(user.getUserGroup().getGroup(), "MA")) {
			reseller.setPayId(user.getPayId());
			reseller.setBusinessName(user.getBusinessName());
			reseller.setEmailId(user.getEmailId());
			resellers.add(reseller);
			setListReseller(resellers);
			logger.info("resellerList....={}", getListReseller().toString());
		}
		if (StringUtils.equalsIgnoreCase(user.getUserGroup().getGroup(), "Agent")) {
			reseller.setPayId(user.getPayId());
			reseller.setBusinessName(user.getBusinessName());
			reseller.setEmailId(user.getEmailId());
			resellers.add(reseller);
			setListReseller(resellers);
			logger.info("resellerList....={}", getListReseller().toString());
		}
		else {
			if(userType!=null) {
				List<Merchants> getData = userDao.getResellersbyUserType(userType).stream().map((userOject)->{
					Merchants merData= new Merchants();
					merData.setPayId(userOject.getPayId());
					merData.setBusinessName(userOject.getBusinessName());
					merData.setEmailId(userOject.getEmailId());
					return merData;
				}).collect(Collectors.toList());
				setListReseller(getData);
			}
			
		}
		return INPUT;
	}

	public String getPayoutDetails() {

		User user = (User) sessionMap.get(Constants.USER.getValue());
		Merchants reseller = new Merchants();
		if (StringUtils.equalsIgnoreCase(user.getUserGroup().getGroup(), "SMA")) {
			reseller.setBusinessName(user.getBusinessName());
			reseller.setPayId(user.getPayId());
			reseller.setEmailId(user.getEmailId());
			reseller.setIsActive(true);
			List<Merchants> resellers = new ArrayList<>();
			resellers.add(reseller);
			setListReseller(resellers);
			resellerId = user.getPayId();
			logger.info("reseller Id...." + resellerId);
			logger.info("fromDate...." + fromDate);
			logger.info("toDate....." + toDate);
			setResellerPayout(resellerDao.getResellerPayoutDetails(userType,resellerId,currency, fromDate, toDate));
			logger.info("setResellerPayout....={}", getResellerPayout().toString());
		}	if (StringUtils.equalsIgnoreCase(user.getUserGroup().getGroup(), "MA")) {
			reseller.setBusinessName(user.getBusinessName());
			reseller.setPayId(user.getPayId());
			reseller.setEmailId(user.getEmailId());
			reseller.setIsActive(true);
			List<Merchants> resellers = new ArrayList<>();
			resellers.add(reseller);
			setListReseller(resellers);
			resellerId = user.getPayId();
			logger.info("reseller Id...." + resellerId);
			logger.info("fromDate...." + fromDate);
			logger.info("toDate....." + toDate);
			setResellerPayout(resellerDao.getResellerPayoutDetails(userType,resellerId,currency, fromDate, toDate));
			logger.info("setResellerPayout....={}", getResellerPayout().toString());
		} 	if (StringUtils.equalsIgnoreCase(user.getUserGroup().getGroup(), "Agent")) {
			reseller.setBusinessName(user.getBusinessName());
			reseller.setPayId(user.getPayId());
			reseller.setEmailId(user.getEmailId());
			reseller.setIsActive(true);
			List<Merchants> resellers = new ArrayList<>();
			resellers.add(reseller);
			setListReseller(resellers);
			resellerId = user.getPayId();
			logger.info("reseller Id...." + resellerId);
			logger.info("fromDate...." + fromDate);
			logger.info("toDate....." + toDate);
			setResellerPayout(resellerDao.getResellerPayoutDetails(userType,resellerId,currency, fromDate, toDate));
			logger.info("setResellerPayout....={}", getResellerPayout().toString());
		}else {

			logger.info("reseller Id...." + resellerId);
			logger.info("fromDate...." + fromDate);
			logger.info("toDate....." + toDate);
			setResellerPayout(resellerDao.getResellerPayoutDetails(userType,resellerId,currency, fromDate, toDate));
			logger.info("resellerPayoutDetailsList" + getResellerPayout());
		}
		return SUCCESS;

	}

	public String updateUtrNo() {

		logger.info("resellerId...." + resellerId);
		logger.info("batchNo...." + batchNo);
		resellerDao.updateResellerPayoutDetails(resellerId, batchNo, utrNo, getSettlementDate(), getTds());
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
		logger.info("userType...={},resellerId={},merchantId={}",getUserType(),getResellerId());
		try {
			List<String> currencylist = commissiondao.findBydiffUserId(getUserType(),getResellerId());
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
	public List<Merchants> getListReseller() {
		return listReseller;
	}

	public void setListReseller(List<Merchants> listReseller) {
		this.listReseller = listReseller;
	}

	public String getFromDate() {
		return fromDate;
	}

	public void setFromDate(String fromDate) {
		this.fromDate = fromDate;
	}

	public String getToDate() {
		return toDate;
	}

	public void setToDate(String toDate) {
		this.toDate = toDate;
	}

	public String getResellerId() {
		return resellerId;
	}

	public void setResellerId(String resellerId) {
		this.resellerId = resellerId;
	}

	public List<ResellerPayout> getResellerPayout() {
		return resellerPayout;
	}

	public void setResellerPayout(List<ResellerPayout> resellerPayout) {
		this.resellerPayout = resellerPayout;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public ResellerPayout getUpdateResellerPayout() {
		return updateResellerPayout;
	}

	public void setUpdateResellerPayout(ResellerPayout updateResellerPayout) {
		this.updateResellerPayout = updateResellerPayout;
	}

	public String getUtrNo() {
		return utrNo;
	}

	public void setUtrNo(String utrNo) {
		this.utrNo = utrNo;
	}

	public String getSettlementDate() {
		return settlementDate;
	}

	public void setSettlementDate(String settlementDate) {
		this.settlementDate = settlementDate;
	}

	public String getTds() {
		return tds;
	}

	public void setTds(String tds) {
		this.tds = tds;
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

	public List<String> getResellerid() {
		return resellerid;
	}

	public void setResellerid(List<String> resellerid) {
		this.resellerid = resellerid;
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

	public String getUserType() {
		return userType;
	}

	public void setUserType(String userType) {
		this.userType = userType;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public String getBatchNo() {
		return this.batchNo;
	}

	public void setBatchNo(String batchNo) {
		this.batchNo = batchNo;
	}

	public List<Merchants> getResellerData() {
		return resellerData;
	}

	public void setResellerData(List<Merchants> resellerData) {
		this.resellerData = resellerData;
	}
}
