package com.pay10.crm.reseller;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.pay10.commons.action.AbstractSecureAction;
import com.pay10.commons.user.ChargingDetailsDao;
import com.pay10.commons.user.Merchants;
import com.pay10.commons.user.TdrSettingDao;
import com.pay10.commons.user.User;
import com.pay10.commons.user.UserDao;
import com.pay10.commons.util.Constants;

/**
 * Sweety
 */
public class ResellerCommissionReportAction extends AbstractSecureAction {

	private static final long serialVersionUID = -2426848101538794598L;
	private static final Logger logger = LoggerFactory.getLogger(ResellerCommissionReportAction.class.getName());

	private List<String> paymentTypeList = new ArrayList<String>();
	private List<Merchants> resellerList = new ArrayList<Merchants>();
	private List<User> merchantlist = new ArrayList<User>();

	private String resellerId;
	private String paymentType;
	private String dateFrom;
	private String dateTo;
	private String merchantname;

	@Autowired
	private UserDao userDao;

	@Autowired
	private TdrSettingDao chargingDetailsDao;

	@SuppressWarnings("unchecked")
	@Override
	public String execute() {

		User user = (User) sessionMap.get(Constants.USER.getValue());
		Merchants reseller = new Merchants();
		if (StringUtils.equalsIgnoreCase(user.getUserGroup().getGroup(), "Reseller")) {
			reseller.setBusinessName(user.getBusinessName());
			reseller.setPayId(user.getPayId());
			reseller.setEmailId(user.getEmailId());
			reseller.setIsActive(true);
			List<Merchants> resellers = new ArrayList<>();
			resellers.add(reseller);
			setResellerList(resellers);
			//resellerId = user.getPayId();
		} else {
			setResellerList(userDao.getResellerList());
		}
		setMerchantlist(userDao.getResellersbyid(resellerId));

		setPaymentTypeList(chargingDetailsDao.findPaymentTypeByPayId(getMerchantname()));

		logger.info("resellerId..." + resellerId);

		logger.info("Inside Reseller Daily update....." + getMerchantlist());

		return SUCCESS;
	}

	public List<Merchants> getResellerList() {
		return resellerList;
	}

	public void setResellerList(List<Merchants> resellerList) {
		this.resellerList = resellerList;
	}

	public List<User> getMerchantlist() {
		return merchantlist;
	}

	public void setMerchantlist(List<User> merchantlist) {
		this.merchantlist = merchantlist;
	}

	public List<String> getPaymentTypeList() {
		return paymentTypeList;
	}

	public void setPaymentTypeList(List<String> paymentTypeList) {
		this.paymentTypeList = paymentTypeList;
	}

	public String getDateFrom() {
		return dateFrom;
	}

	public void setDateFrom(String dateFrom) {
		this.dateFrom = dateFrom;
	}

	public String getDateTo() {
		return dateTo;
	}

	public void setDateTo(String dateTo) {
		this.dateTo = dateTo;
	}

	public String getMerchantname() {
		return merchantname;
	}

	public void setMerchantname(String merchantname) {
		this.merchantname = merchantname;
	}

}
