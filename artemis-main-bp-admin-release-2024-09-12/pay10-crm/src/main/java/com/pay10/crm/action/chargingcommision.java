package com.pay10.crm.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.user.Merchants;
import com.pay10.commons.user.MultCurrencyCode;
import com.pay10.commons.user.MultCurrencyCodeDao;
import com.pay10.commons.user.Resellercommision;
import com.pay10.commons.user.User;
import com.pay10.commons.user.UserDao;
import com.pay10.commons.user.resellercommisiondao;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.MopType;
import com.pay10.commons.util.PaymentType;

public class chargingcommision extends AbstractSecureAction {

	@Autowired
	private resellercommisiondao commision;

	@Autowired
	private UserDao userDao;

	private static Logger logger = LoggerFactory.getLogger(chargingcommision.class.getName());
	private static final long serialVersionUID = -6879974923614009981L;
	private List<User> merchantlist = new ArrayList<User>();

	public List<Merchants> listMerchant = new ArrayList<Merchants>();
	private List<String> merchantarreList = new ArrayList<String>();
	private List<String> merchantarrename = new ArrayList<String>();

	private List<String> merchantbusname = new ArrayList<String>();
	private String resellerId;
	private String merchantname;
	private String currencyData;

	private List<String> merchantid = new ArrayList<String>();
	private String merchantpayId;
	private List<Merchants> listReseller = new ArrayList<Merchants>();
	private List<Resellercommision> data = new ArrayList<Resellercommision>();
	Map<String, List<Resellercommision>> ResellercommisionMap = new HashMap<String, List<Resellercommision>>();
	private List<MultCurrencyCode> listCurrency = new ArrayList<>();
	@Autowired
	private resellercommisiondao commisiondao;
	
	@Autowired
	private MultCurrencyCodeDao multCurrencyCodeDao;
	
	private String payId;
	private List<String> currencyCode = new ArrayList<String>();
	private List<String> currencyName = new ArrayList<String>();
	
	private List<String> currencyCodearreList = new ArrayList<String>();
	private List<String> currencyarrename = new ArrayList<String>();
	@Override
	@SuppressWarnings("unchecked")
	public String execute() {
		User user = (User) sessionMap.get(Constants.USER.getValue());
		if (StringUtils.equalsIgnoreCase(user.getUserGroup().getGroup(), "Reseller")) {
			
			setListReseller(userDao.findByResellerId(user.getResellerId()));
		}
		else {	
		    setListReseller(userDao.getResellerList());
		}
		logger.info("get merchants list according to reseller..." + merchantpayId);
		setMerchantlist(userDao.getResellersbyid(merchantpayId));
		logger.info("get list..." + getMerchantlist());

		logger.info("get currency list according to reseller and merchant..." + merchantpayId);
		getListCurrency().add(multCurrencyCodeDao.findByCode(currencyData));
		logger.info("Currency List: {}", getListCurrency());
		
		try {
			data = commision.getchargingdetail(merchantpayId, merchantname,currencyData);
			for (PaymentType paymentType : PaymentType.values()) {

				List<Resellercommision> resellercommisiondata = new ArrayList<Resellercommision>();
				String paymentName = paymentType.getCode();
				logger.info("Payment Type for reseller commission...={}", paymentName);
				for (Resellercommision cDetail : data) {
					logger.info("cDetail..={}", cDetail.toString());

					if (cDetail.getTransactiontype().equals(paymentName)) {
						cDetail.setMop(MopType.getmopName(cDetail.getMop()));
						cDetail.setTransactiontype(PaymentType.getpaymentName(cDetail.getTransactiontype()));
						cDetail.setCurrency(multCurrencyCodeDao.getCurrencyNamebyCode(cDetail.getCurrency()));
						resellercommisiondata.add(cDetail);
					}
				}
				if (resellercommisiondata.size() != 0) {
					paymentName = PaymentType.getpaymentName(paymentName);
					ResellercommisionMap.put(paymentName, resellercommisiondata);

					logger.info("ResellerCommission.................." + ResellercommisionMap.toString());
				}
			}
			setCurrencyData(multCurrencyCodeDao.getCurrencyNamebyCode(currencyData));
		} catch (Exception exception) {
			logger.error("Exception", exception);
			addActionMessage(ErrorType.LOCAL_TAX_RATES_NOT_AVAILABLE.getResponseMessage());
		}
		return INPUT;
	}

	// To display page without using token
	@SuppressWarnings("unchecked")
	public String displayList() {
		User user = (User) sessionMap.get(Constants.USER.getValue());
		if (StringUtils.equalsIgnoreCase(user.getUserGroup().getGroup(), "Reseller")) {
			
			setListReseller(userDao.findByResellerId(user.getResellerId()));
		}else {
		
		setListReseller(userDao.getResellerList());
		}
		return INPUT;
	}

	public String getmerchantlist() {
		
		try {
			List<User> merchantlist = userDao.getResellersbyid(merchantpayId);

			for (User merchantdat : merchantlist) {
				String merchantpayid = merchantdat.getPayId();

				String merchantname = merchantdat.getBusinessName();

				merchantid.add(merchantpayid);
				merchantbusname.add(merchantname);

			}
			setMerchantarreList(merchantid);
			setMerchantarrename(merchantbusname);
			return SUCCESS;
		 }
		catch (Exception exception) {
			logger.error("Exception ", exception);
			return ERROR;
		 }
		}
	
	public String getCurrencyList() {

		try {
			List<String> currencylist = commisiondao.findByResellerIdAndPayId(merchantpayId,payId);
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

	public String getMerchantpayId() {
		return merchantpayId;
	}

	public void setMerchantpayId(String merchantpayId) {
		this.merchantpayId = merchantpayId;
	}

	public String getCurrencyData() {
		return currencyData;
	}

	public void setCurrencyData(String currencyData) {
		this.currencyData = currencyData;
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

	public Map<String, List<Resellercommision>> getResellercommisionMap() {
		return ResellercommisionMap;
	}

	public void setResellercommisionMap(Map<String, List<Resellercommision>> resellercommisionMap) {
		ResellercommisionMap = resellercommisionMap;
	}

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

	public String getMerchantname() {
		return merchantname;
	}

	public void setMerchantname(String merchantname) {
		this.merchantname = merchantname;
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

	public List<MultCurrencyCode> getListCurrency() {
		return listCurrency;
	}

	public void setListCurrency(List<MultCurrencyCode> listCurrency) {
		this.listCurrency = listCurrency;
	}

}
