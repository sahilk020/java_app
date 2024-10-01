package com.pay10.commons.user;

import java.math.BigInteger;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.hibernate.query.Query;


import org.hibernate.HibernateException;
import org.hibernate.ObjectNotFoundException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.pay10.commons.dao.HibernateAbstractDao;
import com.pay10.commons.dao.HibernateSessionProvider;
import com.pay10.commons.exception.DataAccessLayerException;
import com.pay10.commons.util.CurrencyNumber;

@Component("TdrSettingPayoutDao")

public class TdrSettingPayoutDao extends HibernateAbstractDao {
	private static Logger logger = LoggerFactory.getLogger(TdrSettingPayoutDao.class.getName());

	public String getCurrenyStatus(String payId, String currency, String acquirerName, String channel) {

		Session session = HibernateSessionProvider.getSession();
		String sqlQuery = "from TdrSettingPayout where payId='" + payId + "' and currency='" + currency
				+ "' and channel='" + channel + "' and acquirerName ='" + acquirerName + "'and status='ACTIVE' ";

		logger.info("Query : {}", sqlQuery);
		List<TdrSettingPayout> list = session.createQuery(sqlQuery, TdrSettingPayout.class).getResultList();
		logger.info("LIST SIZE : {}", list.size());
		if (list.isEmpty()) {
			return "INACTIVE";
		} else {
			return "ACTIVE";
		}

	}

	public TdrSettingPayout getTdrSettingPayoutDetail(String payId, String cannel, String acquirerType,
			String currency) {
		try {
			Session session = HibernateSessionProvider.getSession();
			String sqlQuery = "from TdrSettingPayout where payId='" + payId + "' and channel='" + cannel
					+ "' and acquirerName ='" + acquirerType + "'and currency ='" + currency + "'";

			logger.info("Query : {}", sqlQuery);
			return session.createQuery(sqlQuery, TdrSettingPayout.class).getSingleResult();
		} catch (Exception e) {
			return null;
		}

	}

	public void create(TdrSettingPayout chargingDetails) throws DataAccessLayerException {
		super.save(chargingDetails);
	}

	public void delete(TdrSettingPayout chargingDetails) throws DataAccessLayerException {
		super.delete(chargingDetails);
	}

	public void update(TdrSettingPayout chargingDetails) throws DataAccessLayerException {
		super.saveOrUpdate(chargingDetails);
	}

	public TdrSettingPayout getTdrAndSurcharge(String payId, String channel, String acquirerType,
			String transactionType, String currency, String amount, String date, String paymentRegion,
			String cardHolderType) {

		Session session = HibernateSessionProvider.getSession();
		String sqlQuery = "from TdrSettingPayout where payId='" + payId + "' and channel='" + channel
				+ "' and acquirerName ='" + acquirerType + "' and transactionType='" + transactionType
				+ "' and currency='" + currency + "'and '" + date + "' >= fromDate" + " and '" + amount
				+ "' BETWEEN minTransactionAmount and maxTransactionAmount and paymentRegion='" + paymentRegion
				+ "' and type='" + cardHolderType + "' and status='" + "ACTIVE" + "' and tdrStatus='" + "ACTIVE" + "'";

		logger.info("Query : {}", sqlQuery);
		List<TdrSettingPayout> list = session.createQuery(sqlQuery, TdrSettingPayout.class).getResultList();
		logger.info("LIST SIZE : {}", list.size());
		if (!list.isEmpty()) {
			TdrSettingPayout entity =  list.get(0);
			logger.info("DB VALUES from DB: {}", new Gson().toJson(entity));
			return entity;

		} else {
			return null;
		}

	}


	public boolean checkIfExists(TdrSettingPayout tdrSettingPayout) {

		Session session = HibernateSessionProvider.getSession();
		String sqlQuery = "FROM TdrSettingPayout WHERE " +
				"payId=:payId AND " +
				"channel=:channel AND " +
				"acquirerName=:acquirerName AND " +
				"transactionType=:transactionType AND " +
				"currency=:currency AND " +
				"fromDate=:fromDate AND " +
				"paymentRegion=:paymentRegion AND " +
				"type=:type AND " +
				"status='ACTIVE' AND " +
				"tdrStatus='ACTIVE' AND " +

				"(NOT (maxTransactionAmount < :minTransactionAmount OR  :maxTransactionAmount < minTransactionAmount))";
		
		if(tdrSettingPayout.getId()!=null)
			sqlQuery+=	"AND	id!= "+ tdrSettingPayout.getId();
		
		

		List<TdrSettingPayout> list = session.createQuery(sqlQuery,TdrSettingPayout.class)
				.setParameter("payId", tdrSettingPayout.getPayId())
				.setParameter("channel", tdrSettingPayout.getChannel())
				.setParameter("acquirerName",tdrSettingPayout.getAcquirerName())
				.setParameter("transactionType", tdrSettingPayout.getTransactionType())
				.setParameter("currency", tdrSettingPayout.getCurrency())
				.setParameter("fromDate", tdrSettingPayout.getFromDate())
				.setParameter("paymentRegion", tdrSettingPayout.getPaymentRegion())
				.setParameter("type", tdrSettingPayout.getType())
				.setParameter("minTransactionAmount", tdrSettingPayout.getMinTransactionAmount())
				.setParameter("maxTransactionAmount", tdrSettingPayout.getMaxTransactionAmount())
//				.setParameter("id", tdrSettingPayout.getId())
				.getResultList();
		logger.info("List size : {}", list.size());
		return !list.isEmpty();
	}

	public List<String> getAcquierByPayId(String payId) {
		List<String> list = new ArrayList<String>();

		Session session = HibernateSessionProvider.getSession();
		Transaction tx = session.beginTransaction();
		try {
			list = session.createQuery(
					"Select acquirerName from TdrSettingPayout RC where RC.payId = :payId and status = :status group by acquirerName")
					.setParameter("payId", payId).setParameter("status", "ACTIVE")

					.setCacheable(true).getResultList();
			tx.commit();
			return list;
		} catch (ObjectNotFoundException objectNotFound) {
			handleException(objectNotFound, tx);
		} catch (HibernateException hibernateException) {
			handleException(hibernateException, tx);
		} finally {
			autoClose(session);
		}
		return list;

	}

	public List<TdrSettingPayout> getTdrPayoutAcquirer(String acquirerName, String payId, String channel) {

		List<TdrSettingPayout> chanList = new ArrayList<TdrSettingPayout>();

		Session session = HibernateSessionProvider.getSession();
		Transaction tx = session.beginTransaction();
		try {
			chanList = session.createQuery(
					"from TdrSettingPayout RC where RC.payId = :payId and RC.acquirerName = :acquirerName and RC.channel = :channel and RC.status = :status")
					.setParameter("payId", payId).setParameter("channel", channel)
					.setParameter("acquirerName", acquirerName).setParameter("status", "ACTIVE").setCacheable(true)
					.getResultList();
			tx.commit();
			return chanList;
		} catch (ObjectNotFoundException objectNotFound) {
			handleException(objectNotFound, tx);
		} catch (HibernateException hibernateException) {
			handleException(hibernateException, tx);
		} finally {
			autoClose(session);
		}
		return chanList;

	}

	public List<String> getchannelByAcquier(String acquirerName, String payId) {
		List<String> chanList = new ArrayList<String>();

		Session session = HibernateSessionProvider.getSession();
		Transaction tx = session.beginTransaction();
		try {
			chanList = session.createQuery(
					"Select channel from TdrSettingPayout RC where RC.payId = :payId and RC.acquirerName = :acquirerName and RC.status = :status group by channel")
					.setParameter("payId", payId).setParameter("acquirerName", acquirerName)
					.setParameter("status", "ACTIVE").setCacheable(true).getResultList();
			tx.commit();
			return chanList;
		} catch (ObjectNotFoundException objectNotFound) {
			handleException(objectNotFound, tx);
		} catch (HibernateException hibernateException) {
			handleException(hibernateException, tx);
		} finally {
			autoClose(session);
		}
		return chanList;

	}

	public List<String> getCurrencyListByPayId(String acquirerName, String payId, String channel) {
		List<String> chanList = new ArrayList<String>();

		Session session = HibernateSessionProvider.getSession();
		Transaction tx = session.beginTransaction();
		try {
			chanList = session.createQuery(
					"Select channel from TdrSettingPayout RC where RC.payId = :payId and RC.acquirerName = :acquirerName and RC.status = :status group by channel")
					.setParameter("payId", payId).setParameter("acquirerName", acquirerName)
					.setParameter("status", "ACTIVE").setCacheable(true).getResultList();
			tx.commit();
			return chanList;
		} catch (ObjectNotFoundException objectNotFound) {
			handleException(objectNotFound, tx);
		} catch (HibernateException hibernateException) {
			handleException(hibernateException, tx);
		} finally {
			autoClose(session);
		}
		return chanList;

	}
	
	public List<String> getCurrencyList(String acquirerName, String payId, String channel) {
		List<String> currencyList = new ArrayList<String>();

		Session session = HibernateSessionProvider.getSession();
		Transaction tx = session.beginTransaction();
		try {
			currencyList = session.createQuery(
					"Select currency from TdrSettingPayout RC where RC.payId = :payId and RC.acquirerName = :acquirerName and RC.status = :status and RC.channel =:channel group by currency")
					.setParameter("payId", payId).setParameter("acquirerName", acquirerName).setParameter("channel", channel)
					.setParameter("status", "ACTIVE").setCacheable(true).getResultList();
			tx.commit();
			return currencyList;
		} catch (ObjectNotFoundException objectNotFound) {
			handleException(objectNotFound, tx);
		} catch (HibernateException hibernateException) {
			handleException(hibernateException, tx);
		} finally {
			autoClose(session);
		}
		return currencyList;

	}

	public TdrSetting getTdrAndSurcharge(String payId, String paymentType, String acquirerType, String mopType,
			String transactionType, String currency, String amount, String date, String paymentRegion,
			String cardHolderType) {

		Session session = HibernateSessionProvider.getSession();
		String sqlQuery = "from TdrSetting where payId='" + payId + "' and paymentType='" + paymentType
				+ "' and acquirerName ='" + acquirerType + "' and mopType='" + mopType + "'" + " and transactionType='"
				+ transactionType + "' and currency='" + currency + "'and '" + date + "' >= fromDate"
				+ " and '" + amount + "' BETWEEN minTransactionAmount and maxTransactionAmount and paymentRegion='"
				+ paymentRegion + "' and type='" + cardHolderType + "' and status='" + "ACTIVE" + "' and tdrStatus='"
				+ "ACTIVE" + "'";

		logger.info("Query :" + sqlQuery);
		List list = session.createQuery(sqlQuery).getResultList();
		logger.info("LIST SIZE :" + list.size());
		if (list.size() > 0) {
			TdrSetting entity = (TdrSetting) list.get(0);
			logger.info("DB VALUES from DB: \n" + new Gson().toJson(entity));
			return entity;

		} else {
			return null;
		}

	}

	public String[] calculateTdrAndSurcharge(String payId, String channel, String acquirerType, String transactionType,
			String currency, String amount, String date, String paymentRegion, String cardHolderType)
			throws ParseException {
		String[] val = new String[5];
		TdrSettingPayout tdrSetting = getTdrAndSurcharge(payId, channel, acquirerType, transactionType, currency,
				amount, date, paymentRegion, cardHolderType);

		if (tdrSetting != null) {
			double igst = tdrSetting.getIgst();
			double amountInDouble = Double.valueOf(amount);

			double merchantMdr = 0;

			double bankMdr = 0;
			double bankGst = 0;

			double pgMdr = 0;
			double pgGst = 0;

			// calculate merchant mdr
			if (tdrSetting.getMerchantPreference().equalsIgnoreCase("PERCENTAGE")) {
				double merchantTdr = tdrSetting.getMerchantTdr();
				double merchantTdrAmount = ((merchantTdr / 100) * amountInDouble);

				if (merchantTdrAmount < tdrSetting.getMerchantMinTdrAmt()) {
					merchantMdr = tdrSetting.getMerchantMinTdrAmt();
				} else if (merchantTdrAmount > tdrSetting.getMerchantMaxTdrAmt()) {
					merchantMdr = tdrSetting.getMerchantMaxTdrAmt();
				} else {
					merchantMdr = merchantTdrAmount;
				}

			} else {
				merchantMdr = tdrSetting.getMerchantTdr();
			}

			// calculate bank mdr

			if (tdrSetting.getBankPreference().equalsIgnoreCase("PERCENTAGE")) {
				double bankTdr = tdrSetting.getBankTdr();
				double bankTdrAmount = ((bankTdr / 100) * amountInDouble);

				if (bankTdrAmount < tdrSetting.getBankMinTdrAmt()) {
					bankMdr = tdrSetting.getBankMinTdrAmt();
				} else if (bankTdrAmount > tdrSetting.getMerchantMaxTdrAmt()) {
					bankMdr = tdrSetting.getMerchantMaxTdrAmt();
				} else {
					bankMdr = bankTdrAmount;
				}
			} else {
				bankMdr = tdrSetting.getBankTdr();
			}

			// calculate pg mdr
			pgMdr = merchantMdr - bankMdr;

			// calculate gst

			// bank gst
			bankGst = ((igst / 100) * bankMdr);
			// pg gst
			pgGst = ((igst / 100) * pgMdr);

//			DecimalFormat decimalFormat = new DecimalFormat();
//			decimalFormat.setMaximumFractionDigits(2);
//			Integer.parseInt(CurrencyNumber.getDecimalfromInstance("INR"));
//			String decimal = CurrencyNumber.getDecimalfromCode(currency);
//			
//			String.format("%."+decimal+"f", 9.57437);
			String decimal = 	CurrencyNumber.getDecimalfromInstance(currency);
			val[0] = String.format("%." + decimal + "f", bankMdr);
			val[1] = String.format("%." + decimal + "f", bankGst);
			val[2] = String.format("%." + decimal + "f", pgMdr);
			val[3] = String.format("%." + decimal + "f", pgGst);
			val[4] = tdrSetting.getEnableSurcharge() == true ? "Y" : "N";
		} else {
			val[0] = "0";
			val[1] = "0";
			val[2] = "0";
			val[3] = "0";
			val[4] = "NONE";

		}
		return val;
	}
	

	public List<String> getAcquirerTypeList(String payId) {

		Session session = HibernateSessionProvider.getSession();
		Transaction tx = session.beginTransaction();
		List<String> list = new ArrayList<String>();
		try {
			Query query = session
					.createSQLQuery("SELECT distinct acquirerName FROM TdrSettingPayout where  payId=:payId");
			query.setString("payId", payId);
			list = query.list();
			System.out.println("groupList " + list);

			tx.commit();
			logger.info("aquireer data in dao class" + list);
			return list;

		} catch (ObjectNotFoundException objectNotFound) {
			handleException(objectNotFound, tx);
		} catch (HibernateException hibernateException) {
			handleException(hibernateException, tx);
		} finally {
			autoClose(session);
		}
		return list;
	}

public List<String> getMopTypelist(String payId, String Acquirer1, String paytype) {
		
		Session session = HibernateSessionProvider.getSession();
		Transaction tx = session.beginTransaction();
		List<String> paylist = new ArrayList<String>();
		try {
			Query query = session.createSQLQuery(
					"SELECT distinct currency FROM TdrSettingPayout where acquirerName=:acquirerName and payId=:payId and channel=:channel and status='ACTIVE'");
			query.setString("payId", payId);
			query.setString("acquirerName", Acquirer1);
			query.setString("channel", paytype);
			
			//System.out.println("SELECT distinct mopType FROM TdrSetting where acquirerName=:acquirerName and payId=:payId and paymentType like '%"+paytype+"%' ");
			paylist = query.list();

			tx.commit();
			return paylist;

		} catch (ObjectNotFoundException objectNotFound) {
			handleException(objectNotFound, tx);
		} catch (HibernateException hibernateException) {
			handleException(hibernateException, tx);
		} finally {
			autoClose(session);
		}
		return paylist;
	}

	public List<String> getPaymentType(String payId, String acquirer1) {

		Session session = HibernateSessionProvider.getSession();
		Transaction tx = session.beginTransaction();
		List<String> moplist = new ArrayList<String>();
		logger.info("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
		try {

			Query query = session.createSQLQuery(
					"SELECT distinct channel FROM TdrSettingPayout where acquirerName= :acquirerName and payId=:payId and status='ACTIVE'");
			query.setString("payId", payId);
			query.setString("acquirerName", acquirer1);
			moplist = query.list();
			logger.info("bbbbbbbbbbbbbbbbbbbbbbaaaaaaaaaaaaa" + moplist);

			tx.commit();
			return moplist;

		} catch (ObjectNotFoundException objectNotFound) {
			handleException(objectNotFound, tx);
		} catch (HibernateException hibernateException) {
			handleException(hibernateException, tx);
		} finally {
			autoClose(session);
		}
		return moplist;
	}

	public boolean getruleenginval(String Acquirer, String currency, String channel, String payId) {
		BigInteger count = new BigInteger("0");
		Session session = HibernateSessionProvider.getSession();
		Transaction tx = session.beginTransaction();
		try {
			logger.info("Start in Tdr Setting Detial" + Acquirer + currency + channel + payId);
			String sqlQuery = "select COUNT(*) from TdrSettingPayout where acquirerName=:acquirerName and payId=:payId and currency =:currency and channel=:channel ";
			count = (BigInteger) session.createNativeQuery(sqlQuery).setParameter("payId", payId)
					.setParameter("acquirerName", Acquirer).setParameter("currency", currency)
					.setParameter("channel", channel).getSingleResult();
			tx.commit();
			logger.info("cout in Tdr Detial" + count);
			if (count.intValue() > 0) {
				return true;
			} else {
				return false;
			}
		} catch (ObjectNotFoundException objectNotFound) {
			handleException(objectNotFound, tx);
		} catch (HibernateException hibernateException) {
			handleException(hibernateException, tx);
		} finally {
			autoClose(session);
		}
		return false;
	}

}
