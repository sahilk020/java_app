package com.pay10.commons.dao;

import java.io.Serializable;
import java.util.List;
import org.hibernate.HibernateException;
import org.hibernate.ObjectNotFoundException;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.pay10.commons.user.ChargingDetailsDao;
import com.pay10.commons.user.RouterConfiguration;
import com.pay10.commons.user.RouterRule;

@Service
public class ResetAllMerchantMappingDao extends HibernateAbstractDao implements Serializable, Cloneable {

	@Autowired
	private RouterRuleDao routerRuleDao;
	private Session session = null;
	private Transaction tx = null;

	public String resetMethodExecutor(String payId, String merchantEmail) {
		String status = "Operation Failed !";
		session = HibernateSessionProvider.getSession();
		tx = session.beginTransaction();
		try {

			if (resetRouterConfigurationMerchantMapping(payId).equalsIgnoreCase("SUCCESS")
					&& resetRuleEngineMerchantMapping(payId).equalsIgnoreCase("SUCCESS")
					&& resetTDRMapping(payId).equalsIgnoreCase("SUCCESS")
					&& resetMerchantAcquirerProperties(payId).equalsIgnoreCase("SUCCESS")
					&& deleteAccountCurrencyMapping(merchantEmail).equalsIgnoreCase("SUCCESS")) {
				tx.commit();
				status = "Operation Success !";
			} else {
				if (tx.isActive()) {
					tx.rollback();
				}
			}

		} catch (Exception e) {
			if (!tx.isActive()) {
				tx.rollback();
				
			}
		} finally {
			autoClose(session);
		}

		return status;

	}

	public String resetRuleEngineMerchantMapping(String payId) {
		String status = "FAILED";
		try {
			List<RouterRule> routerRules = routerRuleDao.fetchRouterRuleByPayId(payId);
			routerRules.stream().forEach(routerRule -> permanentDeleteRouterRule(routerRule));
			status = "SUCCESS";
		} catch (Exception e) {
			System.out.println(e);
		}
		return status;
	}

	public String resetRouterConfigurationMerchantMapping(String payId) {
		String status = "FAILED";
		try {
			List<RouterConfiguration> listRouterConfig = routerRuleDao.fetchRouterConfigByPayId(payId);
			listRouterConfig.stream().forEach(routerConfig -> permanentDeleteRouterConfig(routerConfig));
			status = "SUCCESS";
		} catch (Exception e) {
			System.out.println(e);
		}
		return status;
	}

	@SuppressWarnings({ "deprecation", "unchecked" })
	public String resetTDRMapping(String payId) {

		SQLQuery sqlQuery = null;
		String status = "FAILED";
		try {

			int i = session.createSQLQuery(
					"delete from Account_TdrSetting where tdrSetting_id in (select id from TdrSetting where payid='"
							+ payId + "')").executeUpdate();
			
			if(i>0) {
				int j = session.createSQLQuery("delete from TdrSetting where payid='"+payId+"'").executeUpdate();
				status = (j > 0) ? "SUCCESS" : "FAILED";
			}
			
		} catch (ObjectNotFoundException objectNotFound) {
			handleException(objectNotFound, tx);
		} catch (HibernateException hibernateException) {
			handleException(hibernateException, tx);
		}

		return status;
	}

	@SuppressWarnings({ "deprecation", "unchecked" })
	public String deleteAccountCurrencyMapping(String userEmailId) {

		String status = "FAILED";
		try {
			List<Integer> accounts_ids = session
					.createSQLQuery("select accounts_id from User_Account where user_emailid='" + userEmailId + "'")
					.getResultList();

			if (accounts_ids.size() > 0) {
				List<Integer> accountCurrencySet_ids = session
						.createSQLQuery(
								"select accountCurrencySet_id from Account_AccountCurrency where account_id in (:list)")
						.setParameterList("list", accounts_ids).getResultList();
				if (accountCurrencySet_ids.size() > 0) {
					int k = session.createSQLQuery("delete from Account_AccountCurrency where account_id in (:list)")
							.setParameterList("list", accounts_ids).executeUpdate();
					if (k > 0) {
						int i = session.createSQLQuery("delete from AccountCurrency where id in (:list)")
								.setParameterList("list", accountCurrencySet_ids).executeUpdate();
						if (i > 0) {
							int p = session
									.createSQLQuery("delete from User_Account where user_emailid='" + userEmailId + "'")
									.executeUpdate();

							if (p > 0) {
								int q = session.createSQLQuery(
										"update User set userstatus='PENDING' where emailid='" + userEmailId + "'")
										.executeUpdate();
								status = (q > 0) ? "SUCCESS" : "FAILED";

							}
						}
					}

				}
			}


		} catch (ObjectNotFoundException objectNotFound) {
			handleException(objectNotFound, tx);
		} catch (HibernateException hibernateException) {
			handleException(hibernateException, tx);
		}

		return status;
	}

	public String resetMerchantAcquirerProperties(String merchantpayid) {

		String status = "FAILED";
		try {
			int i = session
					.createSQLQuery(
							"delete from MerchantAcquirerProperties where merchantpayid='" + merchantpayid + "'")
					.executeUpdate();
			status = (i > 0) ? "SUCCESS" : "FAILED";
			
		} catch (ObjectNotFoundException objectNotFound) {
			handleException(objectNotFound, tx);
		} catch (HibernateException hibernateException) {
			handleException(hibernateException, tx);
		}

		return status;
	}

	public void permanentDeleteRouterRule(RouterRule routerRule) {

		try {
			super.delete(routerRule);
			
		} catch (ObjectNotFoundException objectNotFound) {
			handleException(objectNotFound, tx);
		} catch (HibernateException hibernateException) {
			handleException(hibernateException, tx);
		}

	}

	public void permanentDeleteRouterConfig(RouterConfiguration routerConfiguration) {

		try {
			super.delete(routerConfiguration);
			
		} catch (ObjectNotFoundException objectNotFound) {
			handleException(objectNotFound, tx);
		} catch (HibernateException hibernateException) {
			handleException(hibernateException, tx);
		}

	}
}
