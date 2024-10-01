package com.pay10.crm.action;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.pay10.commons.dao.RouterConfigurationDao;
import com.pay10.commons.user.AccountCurrencyRegion;
import com.pay10.commons.user.CardHolderType;
import com.pay10.commons.user.RouterConfiguration;
import com.pay10.commons.user.UserDao;
import com.pay10.commons.util.MopType;
import com.pay10.commons.util.PaymentType;

/**
 * @author Amitosh
 *
 */

public class ViewSmartRouterConfigurationAction extends AbstractSecureAction {

	private static final long serialVersionUID = 3961301960418036999L;

	@Autowired
	private UserDao userDao;

	@Autowired
	private RouterConfigurationDao routerConfigurationDao;

	private static Logger logger = LoggerFactory.getLogger(ViewSmartRouterConfigurationAction.class.getName());
	public List<RouterConfiguration> aaData = new ArrayList<RouterConfiguration>();

	private String merchants;
	private String paymentMethod;
	private String cardHolderType;

	@Override
	public String execute() {

		logger.info("Inside ViewSmartRouterConfigurationAction , execute()");

		try {
			if (merchants != null && paymentMethod != null && cardHolderType!=null) {
				aaData = getRouterConfiguration();
			}
		} catch (Exception exception) {
			logger.error("Exception", exception);
		}
		return SUCCESS;
	}

	@SuppressWarnings("unchecked")
	protected List<RouterConfiguration> getRouterConfiguration() {

		List<RouterConfiguration> routerConfigurationList = new ArrayList<RouterConfiguration>();

		try {
			List<Object[]> routerConfigurationListRaw = routerConfigurationDao.getRouterConfigurationList(merchants, paymentMethod, cardHolderType);

			for (Object[] objects : routerConfigurationListRaw) {

				String merchantName = userDao.getMerchantByPayId((String) objects[0]);
				String mopTypeName = MopType.getmopName((String) objects[6]);
				String paymentTypeName = PaymentType.getpaymentName((String) objects[7]);

				RouterConfiguration routerConfiguration = new RouterConfiguration();
				routerConfiguration.setMerchant(merchantName);
				routerConfiguration.setAcquirer((String) objects[1]);
				routerConfiguration.setCardHolderType((CardHolderType) objects[2]);
				routerConfiguration.setPaymentsRegion((AccountCurrencyRegion) objects[8]);
				routerConfiguration.setLoadPercentage((int) objects[3]);
				routerConfiguration.setMaxAmount((double) objects[4]);
				routerConfiguration.setMinAmount((double) objects[5]);
				routerConfiguration.setMopType(mopTypeName);
				routerConfiguration.setPaymentType(paymentTypeName);
				routerConfiguration.setPaymentsRegion((AccountCurrencyRegion) objects[8]);
				routerConfigurationList.add(routerConfiguration);

			}
			return routerConfigurationList;
		} catch (Exception e) {
			logger.error("Exception caught : " + e);
		}
		return routerConfigurationList;
	}

	public List<RouterConfiguration> getAaData() {
		return aaData;
	}

	public void setAaData(List<RouterConfiguration> aaData) {
		this.aaData = aaData;
	}

	protected void handleException(HibernateException hException, Transaction tx) {
		tx.rollback();
		throw hException;
	}

	public String getMerchants() {
		return merchants;
	}

	public void setMerchants(String merchants) {
		this.merchants = merchants;
	}

	public String getPaymentMethod() {
		return paymentMethod;
	}

	public void setPaymentMethod(String paymentMethod) {
		this.paymentMethod = paymentMethod;
	}


	public String getCardHolderType() {
		return cardHolderType;
	}

	public void setCardHolderType(String cardHolderType) {
		this.cardHolderType = cardHolderType;
	}

}
