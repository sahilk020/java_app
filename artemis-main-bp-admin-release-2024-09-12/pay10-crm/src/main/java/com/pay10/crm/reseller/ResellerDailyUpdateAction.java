
	package com.pay10.crm.reseller;

	import java.util.ArrayList;
	import java.util.HashMap;
	import java.util.List;
	import java.util.Map;

	import org.apache.commons.lang3.StringUtils;
	import org.slf4j.Logger;
	import org.slf4j.LoggerFactory;
	import org.springframework.beans.factory.annotation.Autowired;

	import com.pay10.commons.action.AbstractSecureAction;

	import com.pay10.commons.dao.Resellerdailyupdatedao;


	import com.pay10.commons.user.ChargingDetailsDao;
	import com.pay10.commons.user.Merchants;
	import com.pay10.commons.user.Resellerdailyupdate;
	import com.pay10.commons.user.User;
	import com.pay10.commons.user.UserDao;
	import com.pay10.commons.util.TxnType;

	/**
	 * Sweety
	 */
	public class ResellerDailyUpdateAction extends AbstractSecureAction {

		private static final long serialVersionUID = -5103892667503253761L;
		private static final Logger logger = LoggerFactory.getLogger(ResellerDailyUpdateAction.class.getName());

		private String resellerId;
		private String mopType;
		private String paymentType;
		private String transactionType;
		private String dateFrom;
		private String dateTo;
		private String merchantname;
		private String currency;
		private String userType;

		private List<Resellerdailyupdate> resellerDailyUpdate = new ArrayList<Resellerdailyupdate>();

	
		@Autowired
		private Resellerdailyupdatedao resellerUpdateDao;

		@Autowired
		private UserDao userDao;

		HashMap<String, String> businessNames;


		public String getResellerDailyUpdates() {
			logger.info("Merchant PayId={}", getMerchantname());
			logger.info("Merchant PaymentType={}", getPaymentType());
			logger.info("MopType={}", getMopType());
			logger.info("from Date={}", getDateFrom());
			logger.info("To Date={}", getDateTo());
			logger.info("ResellerId={}", getResellerId());
			logger.info("currency",getCurrency());
			logger.info("userType",getUserType());
			businessNames = new HashMap<>();
			List<Resellerdailyupdate> resellerdailyupdates = resellerUpdateDao.getResellerDailyUpdate(getResellerId(), getMerchantname(),
					getPaymentType(), getMopType(), getDateFrom(), getDateTo(),getCurrency(),getUserType());
			resellerdailyupdates.forEach(rsd->{
				if(!businessNames.containsKey(rsd.getMerchant_payId())){
					businessNames.put(rsd.getMerchant_payId(), userDao.getBusinessNameByPayId(rsd.getMerchant_payId()));
				}
				rsd.setMerchant_payId(businessNames.get(rsd.getMerchant_payId()));
				if(!businessNames.containsKey(rsd.getReseller_payId())){
					businessNames.put(rsd.getReseller_payId(), userDao.getBusinessNameByPayId(rsd.getReseller_payId()));
				}
				rsd.setReseller_payId(businessNames.get(rsd.getReseller_payId()));
			});
			setResellerDailyUpdate(resellerdailyupdates);
			logger.info("");

			return SUCCESS;
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

		public void setTransactionType(String transactionType) {
			this.transactionType = transactionType;
		}

		public String getDateFrom() {
			return dateFrom;
		}

		public void setDateFrom(String dateFrom) {
			this.dateFrom = dateFrom;
		}

		public String getMerchantname() {
			return merchantname;
		}

		public void setMerchantname(String merchantname) {
			this.merchantname = merchantname;
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

		public String getCurrency() {
			return currency;
		}

		public void setCurrency(String currency) {
			this.currency = currency;
		}

		public String getUserType() {
			return userType;
		}

		public void setUserType(String userType) {
			this.userType = userType;
		}


	}

