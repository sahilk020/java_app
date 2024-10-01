package com.pay10.commons.user;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import javax.persistence.criteria.*;

import com.pay10.commons.util.*;
import org.apache.commons.lang.StringUtils;
import org.hibernate.HibernateException;
import org.hibernate.ObjectNotFoundException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.NativeQuery;
import org.hibernate.query.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.pay10.commons.dao.HibernateAbstractDao;
import com.pay10.commons.dao.HibernateSessionProvider;
import com.pay10.commons.exception.DataAccessLayerException;

@Component
public class TdrSettingDao extends HibernateAbstractDao {
	private static Logger logger = LoggerFactory.getLogger(TdrSettingDao.class.getName());
	private static Map<String, List<TdrSetting>> chargingDetailsListMap = new ConcurrentHashMap<String, List<TdrSetting>>();
	@Autowired
	private PropertiesManager propertiesManager;

	public TdrSettingDao() {
		super();
	}

	public void create(TdrSetting chargingDetails) throws DataAccessLayerException {
		super.save(chargingDetails);
	}

	public void delete(TdrSetting chargingDetails) throws DataAccessLayerException {
		super.delete(chargingDetails);
	}

	public void update(TdrSetting chargingDetails) throws DataAccessLayerException {
		super.saveOrUpdate(chargingDetails);
	}

	@SuppressWarnings("rawtypes")
	public List findAll() throws DataAccessLayerException {
		return super.findAll(TdrSetting.class);
	}

	public TdrSetting find(Long id) throws DataAccessLayerException {
		return (TdrSetting) super.find(TdrSetting.class, id);
	}

	public TdrSetting find(String name) throws DataAccessLayerException {
		return (TdrSetting) super.find(TdrSetting.class, name);
	}

	public TdrSetting getTdrAndSurcharge(String payId, String paymentType, String acquirerType, String mopType,
			String transactionType, String currency, String amount, String date,String paymentRegion, String cardHolderType) {

		Session session = HibernateSessionProvider.getSession();
		String sqlQuery = "from TdrSetting where payId='" + payId + "' and paymentType='" + paymentType
				+ "' and acquirerName ='" + acquirerType + "' and mopType='" + mopType + "'" + " and transactionType='"
				+ transactionType + "' and currency='" + currency + "'and '" + date + "' >= fromDate"
				+ " and '" + amount + "' BETWEEN minTransactionAmount and maxTransactionAmount and paymentRegion='"+paymentRegion+"' and type='"+cardHolderType+"' and status='"+"ACTIVE"+"' and tdrStatus='"+"ACTIVE"+"' ORDER BY fromDate DESC";

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
	public TdrSetting getTdrAndSurchargeAfterTransaction(String payId, String paymentType, String acquirerType, String mopType, String currency, String amount, String date,String paymentRegion, String cardHolderType) {

		Session session = HibernateSessionProvider.getSession();
		String sqlQuery = "from TdrSetting where payId='" + payId + "' and paymentType='" + paymentType
				+ "' and acquirerName ='" + acquirerType + "' and mopType='" + mopType + "' and currency='" + currency + "'and updatedAt<'" + date + "' and '" + amount + "' BETWEEN minTransactionAmount and maxTransactionAmount and paymentRegion='"+paymentRegion+"' and type='"+cardHolderType+"' ORDER BY updatedAt DESC";

//		logger.info("Query :" + sqlQuery);
		List list = session.createQuery(sqlQuery).getResultList();
//		logger.info("LIST SIZE :" + list.size());
		if (list.size() > 0) {
			TdrSetting entity = (TdrSetting) list.get(0);
//			logger.info("DB VALUES from DB: \n" + new Gson().toJson(entity));
			return entity;

		} else {
			return null;
		}

	}

	public int updateSurchargeForPaymentType(String payId, String paymenttype) {

		List list = getTdrAndSurchargeByPaymentType(payId, paymenttype);
		int rowsUpdated = 0;
		boolean flag = false;
		for (Object object : list) {
			TdrSetting setting = (TdrSetting) object;
			if (setting.getEnableSurcharge() == true) {
				flag = true;
			}
		}

		if (flag == true) {
			Session session = HibernateSessionProvider.getSession();
			Transaction transaction = session.beginTransaction();
			CriteriaBuilder cb = session.getCriteriaBuilder();
			CriteriaUpdate<TdrSetting> update = cb.createCriteriaUpdate(TdrSetting.class);

			// Set the update expression
			update.set("enableSurcharge", true);

			// Add where clauses
			Root<TdrSetting> root = update.from(TdrSetting.class);
			Predicate condition1 = cb.equal(root.get("payId"), payId);
			Predicate condition2 = cb.equal(root.get("paymentType"), paymenttype);
			Predicate condition3 = cb.equal(root.get("status"), "ACTIVE");
			Predicate condition4 = cb.equal(root.get("tdrStatus"), "ACTIVE");
			update.where(cb.and(condition1, condition2, condition3, condition4));

			// Execute the update
			rowsUpdated = session.createQuery(update).executeUpdate();
			transaction.commit();
			session.close();
		}

		return rowsUpdated;
	}

	public List getTdrAndSurchargeByPaymentType(String payId, String paymentType) {
		Session session = HibernateSessionProvider.getSession();
		CriteriaBuilder cb = session.getCriteriaBuilder();
		CriteriaQuery<TdrSetting> cq = cb.createQuery(TdrSetting.class);
		Root<TdrSetting> root = cq.from(TdrSetting.class);

		cq.where(cb.and(cb.equal(root.get("payId"), payId), cb.equal(root.get("paymentType"), paymentType),
				cb.equal(root.get("status"), "ACTIVE"), cb.equal(root.get("tdrStatus"), "ACTIVE")));

		Query<TdrSetting> query = session.createQuery(cq);
		logger.info("getTdrAndSurcharge Query :" + query.getQueryString());
		List list = query.getResultList();
		return list;
	}

	public boolean isTDRSettingSet(String payId) {
		BigInteger count = new BigInteger("0");
		Session session = HibernateSessionProvider.getSession();
		Transaction tx = session.beginTransaction();
		try {

			// String sqlQuery = "select count(*) from ChargingDetails where payId=:payId
			// and status='ACTIVE' and merchantTDR >= 0";
			String sqlQuery = "select count(*) from TdrSetting where payId=:payId and status='ACTIVE' and tdrStatus='ACTIVE' and merchantTdr >= 0";
			count = (BigInteger) session.createNativeQuery(sqlQuery).setParameter("payId", payId).getSingleResult();
			tx.commit();
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

	public List<TdrSetting> getAllActiveTdrSettingDetails(String payId) {
		List<TdrSetting> chargingDetailsList = new ArrayList<TdrSetting>();
		Session session = HibernateSessionProvider.getSession();
		Transaction tx = session.beginTransaction();
		try {

			chargingDetailsList = session
					.createQuery("from TdrSetting  where payId= :payId and status='ACTIVE' and merchantTdr >= 0")
					.setParameter("payId", payId).getResultList();
			tx.commit();
		} catch (ObjectNotFoundException objectNotFound) {
			handleException(objectNotFound, tx);
		} catch (HibernateException hibernateException) {
			handleException(hibernateException, tx);
		} finally {
			autoClose(session);
		}
		return chargingDetailsList;
	}

	public List<TdrSetting> getChargingDetailsList(String payId) {

		if (propertiesManager.propertiesMap.get("useStaticData") != null
				&& propertiesManager.propertiesMap.get("useStaticData").equalsIgnoreCase("Y")) {

			List<TdrSetting> chargingDetailsList = new ArrayList<TdrSetting>();
			if (chargingDetailsListMap.get(payId) != null) {

				return chargingDetailsListMap.get(payId);

			} else {

				chargingDetailsList = getAllActiveTdrSettingDetails(payId);

				if (chargingDetailsList != null && chargingDetailsList.size() > 0) {
					chargingDetailsListMap.put(payId, chargingDetailsList);
					return chargingDetailsList;
				} else {
					return chargingDetailsList;
				}

			}
		}

		else {
			List<TdrSetting> chargingDetailsList = new ArrayList<TdrSetting>();
			chargingDetailsList = getAllActiveTdrSettingDetails(payId);
			return chargingDetailsList;
		}

	}

	public boolean getruleenginval(String Acquirer, String Mop, String Payment, String payId) {
		BigInteger count = new BigInteger("0");
		Session session = HibernateSessionProvider.getSession();
		Transaction tx = session.beginTransaction();
		try {
			logger.info("Start in Tdr Setting Detial" + Acquirer + Mop + Payment + payId);
			String sqlQuery = "select COUNT(*) from TdrSetting where acquirerName=:acquirerName and payId=:payId and mopType =:mopType and paymentType=:paymentType ";
			count = (BigInteger) session.createNativeQuery(sqlQuery).setParameter("payId", payId)
					.setParameter("acquirerName", Acquirer).setParameter("mopType", Mop)
					.setParameter("paymentType", Payment).getSingleResult();
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

	public String[] calculateTdrAndSurcharge(String payId, String paymentType, String acquirerType, String mopType,
			String transactionType, String currency, String amount, String date,String paymentRegion, String cardHolderType) throws ParseException {
		String[] val = new String[5];
		TdrSetting tdrSetting = getTdrAndSurcharge(payId, PaymentType.getInstanceUsingCode(paymentType).toString(),
				acquirerType, MopType.getInstanceUsingCode(mopType).toString(), transactionType, currency, amount,
				date,paymentRegion,cardHolderType);

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
//			
			String decimal=CurrencyNumber.getDecimalfromCode(currency);
//			
//			String.format("%."+decimal+"f", 9.57437);
			
			val[0] = String.format("%."+decimal+"f",bankMdr);
			val[1] = String.format("%."+decimal+"f",bankGst);
			val[2] = String.format("%."+decimal+"f",pgMdr);
			val[3] = String.format("%."+decimal+"f",pgGst);
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

	public boolean findTdrByPaymentTypeAndPayidAndMinTransactionAmountAndMaxTransaction(String paymentType,
			String payId, String mopType, Date fromDate,Boolean enableSurcharge,Double minTransactionAmount, Double maxTransactionAmount,String paymentRegion, String type,String acquirer ,String currency) {
		logger.info("payId : "+payId+"\tmopType : "+mopType+"\tfromDate : "+fromDate+"\tenableSurcharge : "+enableSurcharge+"\tminTransactionAmount : "+minTransactionAmount+"\tmaxTransactionAmount : "+maxTransactionAmount +"\tpaymentRegion : "+paymentRegion+"\t : "+type );	boolean flag = false;
		Session session = HibernateSessionProvider.getSession();
		CriteriaBuilder cb = session.getCriteriaBuilder();
		CriteriaQuery<TdrSetting> cq = cb.createQuery(TdrSetting.class);
		Root<TdrSetting> root = cq.from(TdrSetting.class);

		Predicate predicate = cb.equal(cb.literal(fromDate), root.get("fromDate"));
		Predicate predicate2 = cb.between(cb.literal(minTransactionAmount), root.get("minTransactionAmount"), root.get("maxTransactionAmount"));
		Predicate predicate3 = cb.between(cb.literal(maxTransactionAmount), root.get("minTransactionAmount"), root.get("maxTransactionAmount"));
		
//		Predicate predicate4 = cb.between(cb.literal(paymentRegion), root.get("paymentRegion"), root.get("maxTransactionAmount"));
//		Predicate predicate5 = cb.between(cb.literal(type), root.get("type"), root.get("maxTransactionAmount"));
		
		Predicate finalPredicate= cb.or(predicate);
		Predicate finalPredicate1= cb.or(predicate2,predicate3);
		//Predicate finalPredicate2= cb.or(predicate4,predicate5);

		cq.where(cb.and
						(
								cb.equal(root.get("payId"), payId), 
								cb.equal(root.get("paymentType"), paymentType),
								cb.equal(root.get("status"), "ACTIVE"), 
								cb.equal(root.get("tdrStatus"), "ACTIVE"),
								cb.equal(root.get("currency"), currency), 
								cb.equal(root.get("mopType"), mopType),
								//cb.equal(root.get("enableSurcharge"), enableSurcharge),
								cb.equal(root.get("paymentRegion"), paymentRegion),
								cb.equal(root.get("type"), type),
								cb.equal(root.get("acquirerName"), acquirer)
								),
						finalPredicate,finalPredicate1
						);

		Query<TdrSetting> query = session.createQuery(cq);
		logger.info("findTdrByPaymentTypeAndPayidAndMinTransactionAmountAndMaxTransaction Query :"
				+ query.getQueryString());
		List<TdrSetting> list = query.getResultList();

		if (list.size() > 0) {
			return true;
		} else {
			return false;
		}
	}

	public boolean findSurcharge(String payId, String paymentType, Date fromDate, boolean enableSurcharge,
			double minTransactionAmount, double maxTransactionAmount, double merchantTdr,String paymentRegion, String type,String currencyCode) {
		boolean flag = false;
		Session session = HibernateSessionProvider.getSession();
		CriteriaBuilder cb = session.getCriteriaBuilder();
		CriteriaQuery<TdrSetting> cq = cb.createQuery(TdrSetting.class);
		Root<TdrSetting> root = cq.from(TdrSetting.class);

		Predicate predicate = cb.equal(cb.literal(fromDate), root.get("fromDate"));

		Predicate predicate2 = cb.between(cb.literal(minTransactionAmount), root.get("minTransactionAmount"), root.get("maxTransactionAmount"));
		Predicate predicate3 = cb.between(cb.literal(maxTransactionAmount), root.get("minTransactionAmount"), root.get("maxTransactionAmount"));
		
//		Predicate predicate4 = cb.between(cb.literal(paymentRegion), root.get("paymentRegion"), root.get("maxTransactionAmount"));
//		Predicate predicate5 = cb.between(cb.literal(type), root.get("type"), root.get("maxTransactionAmount"));
		
		Predicate finalPredicate= cb.or(predicate);
		Predicate finalPredicate1= cb.or(predicate2,predicate3);
		//Predicate finalPredicate2= cb.or(predicate4,predicate5);

		
		cq.where(
					cb.and
						(
								cb.equal(root.get("payId"), payId), 
								cb.equal(root.get("paymentType"), paymentType),
								cb.equal(root.get("status"), "ACTIVE"), 
								cb.equal(root.get("tdrStatus"), "ACTIVE"),			
//								cb.equal(root.get("enableSurcharge"), enableSurcharge),
								cb.equal(root.get("paymentRegion"), paymentRegion),
								cb.equal(root.get("currency"), currencyCode)
//								cb.equal(root.get("type"), type)
								),
						finalPredicate,finalPredicate1);

		Query<TdrSetting> query = session.createQuery(cq);
		List<TdrSetting> list = query.getResultList();
		logger.info("findSurcharge Query :" + query.getQueryString());
		if (list.size() > 0) {
			for (TdrSetting tdrSetting : list) {
				if (tdrSetting.getMerchantTdr() != merchantTdr) {
					flag = true;
					break;
				} else {
					flag = false;
				}
			}

		}
		return flag;
	}

	public BigDecimal getCheckSurcharge(String paymentType, Double amount, String payId, Date date,String paymentRegion,String currency) {
		logger.info("getCheckSurcharge : paymentType : "+paymentType+"\t amount:"+amount +"\tpayId :"+payId+"\tdate:"+new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date) +"\tpaymentRegion : "+paymentRegion);
		DecimalFormat decimalFormat = new DecimalFormat();
		decimalFormat.setMaximumFractionDigits(2);
		
		BigDecimal bigDecimal=BigDecimal.ZERO;
		Session session = HibernateSessionProvider.getSession();
		CriteriaBuilder cb = session.getCriteriaBuilder();
		CriteriaQuery<TdrSetting> cq = cb.createQuery(TdrSetting.class);
		Root<TdrSetting> root = cq.from(TdrSetting.class);
		
		Predicate predicate = cb.greaterThanOrEqualTo(cb.literal(date), root.get("fromDate"));
		Predicate predicate2 = cb.between(cb.literal(amount), root.get("minTransactionAmount"), root.get("maxTransactionAmount"));
		Predicate finalPredicate= cb.and(predicate,predicate2);
		cq.where(cb.and(cb.equal(root.get("payId"), payId),cb.equal(root.get("paymentRegion"), paymentRegion),cb.equal(root.get("currency"), currency), cb.equal(root.get("paymentType"), PaymentType.getInstanceUsingCode(paymentType).toString()),
				cb.equal(root.get("status"), "ACTIVE"), cb.equal(root.get("tdrStatus"), "ACTIVE")
				),finalPredicate);
		cq.orderBy(cb.desc(root.get("fromDate")));
		Query<TdrSetting> query = session.createQuery(cq);
		List<TdrSetting> list = query.getResultList();
		logger.info("getCheckSurcharge Query :" + query.getQueryString()+"\t size"+list.size());
		String decimal=CurrencyNumber.getDecimalfromCode(currency);
		if (list.size() > 0) {
			/* for (TdrSetting tdrSetting : list) { */
			
			TdrSetting tdrSetting=list.get(0);
			logger.info("Tdr Data : "+new Gson().toJson(tdrSetting));
				if (tdrSetting.getEnableSurcharge()) {
					
					if (tdrSetting.getMerchantPreference().equalsIgnoreCase("PERCENTAGE")) {
						double amt=((tdrSetting.getMerchantTdr()/100)*amount);
						double gst=((tdrSetting.getIgst()/100)*amt);
						double amtWithGst=amt+gst;
						bigDecimal=new BigDecimal(String.format("%."+decimal+"f",amtWithGst));
					}else {
						double amt=(tdrSetting.getMerchantTdr());
						double gst=((tdrSetting.getIgst()/100)*amt);
						double amtWithGst=amt+gst;
						bigDecimal=new BigDecimal(String.format("%."+decimal+"f",amtWithGst));
					}
					
				}
				/* } */

		}
		return bigDecimal;
		
	}

	public BigDecimal getCheckSurcharge(String paymentType, Double amount, String payId, Date date, String paymentRegion, String currency, String mopType) {
		logger.info("getCheckSurcharge : paymentType={}, amount={}, payId={}, date={}, paymentRegion={}, mopType={}",
				paymentType, amount, payId, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date), paymentRegion, mopType);
		DecimalFormat decimalFormat = new DecimalFormat();
		decimalFormat.setMaximumFractionDigits(2);

		BigDecimal bigDecimal = BigDecimal.ZERO;
		Session session = HibernateSessionProvider.getSession();
		CriteriaBuilder cb = session.getCriteriaBuilder();
		CriteriaQuery<TdrSetting> cq = cb.createQuery(TdrSetting.class);
		Root<TdrSetting> root = cq.from(TdrSetting.class);

		Predicate predicate = cb.greaterThanOrEqualTo(cb.literal(date), root.get("fromDate"));
		Predicate predicate2 = cb.between(cb.literal(amount), root.get("minTransactionAmount"), root.get("maxTransactionAmount"));
		Predicate finalPredicate = cb.and(predicate, predicate2);
		cq.where(
				cb.and(
						cb.equal(root.get("payId"), payId),
						cb.equal(root.get("paymentRegion"), paymentRegion),
						cb.equal(root.get("currency"), currency),
						cb.equal(root.get("paymentType"), PaymentType.getInstanceUsingCode(paymentType).toString()),
						cb.equal(root.get("status"), "ACTIVE"),
						cb.equal(root.get("tdrStatus"), "ACTIVE"),
						cb.equal(root.get("mopType"), mopType)
		), finalPredicate);
		cq.orderBy(cb.desc(root.get("fromDate")));
		Query<TdrSetting> query = session.createQuery(cq);
		List<TdrSetting> list = query.getResultList();
		logger.info("getCheckSurcharge Query={}, size={}", query.getQueryString(), list.size());
		String decimal = CurrencyNumber.getDecimalfromCode(currency);
		if (!list.isEmpty()) {
			TdrSetting tdrSetting = list.get(0);
			logger.info("Tdr Data={}", new Gson().toJson(tdrSetting));
			if (tdrSetting.getEnableSurcharge()) {
				if (tdrSetting.getMerchantPreference().equalsIgnoreCase("PERCENTAGE")) {
					double amt = ((tdrSetting.getMerchantTdr() / 100) * amount);
					double gst = ((tdrSetting.getIgst() / 100) * amt);
					double amtWithGst = amt + gst;
					bigDecimal = new BigDecimal(String.format("%." + decimal + "f", amtWithGst));
				} else {
					double amt = (tdrSetting.getMerchantTdr());
					double gst = ((tdrSetting.getIgst() / 100) * amt);
					double amtWithGst = amt + gst;
					bigDecimal = new BigDecimal(String.format("%." + decimal + "f", amtWithGst));
				}

			}
		}
		return bigDecimal;

	}

	public List<TdrSetting> getAllTdrByPayIdAndAcquirerTypeAndPaymentTypeAndPaymentRegionAndType(String payId, String acquirerName,
			String paymentType, String paymentRegion, String type,String currencyCode) {
		
		logger.info("payId : "+payId+"\tacquirerName : "+acquirerName+"\tpaymentType : "+paymentType+"\tpaymentRegion : "+paymentRegion+"\ttype : "+type);
		
		Session session = HibernateSessionProvider.getSession();
		CriteriaBuilder cb = session.getCriteriaBuilder();
		CriteriaQuery<TdrSetting> cq = cb.createQuery(TdrSetting.class);
		Root<TdrSetting> root = cq.from(TdrSetting.class);
		cq.where(
				cb.and
					(
							cb.equal(root.get("payId"), payId), 
							cb.equal(root.get("paymentType"), paymentType),
							cb.equal(root.get("acquirerName"), acquirerName),
							cb.equal(root.get("paymentRegion"), paymentRegion),
							cb.equal(root.get("type"), type),
							cb.equal(root.get("currency"), currencyCode)
							));

	Query<TdrSetting> query = session.createQuery(cq);
	List<TdrSetting> list = query.getResultList();
	return list;
	}
	
	
	public List<TdrSetting> getTdrAndSurchargeByMerchantId(String payId) {
		Session session = HibernateSessionProvider.getSession();
		CriteriaBuilder cb = session.getCriteriaBuilder();
		CriteriaQuery<TdrSetting> cq = cb.createQuery(TdrSetting.class);
		Root<TdrSetting> root = cq.from(TdrSetting.class);

		cq.where(cb.and(cb.equal(root.get("payId"), payId),
				cb.equal(root.get("status"), "ACTIVE"), cb.equal(root.get("tdrStatus"), "ACTIVE")));

		Query<TdrSetting> query = session.createQuery(cq);
		logger.info("getTdrAndSurchargeBymerchantId Query :" + query.getQueryString());
		List<TdrSetting> list = query.getResultList();
		return list;
	}
	
	public List<TdrSetting> getAllActiveTdrSettingDetailsNew(String payId,String currency) {
		List<TdrSetting> chargingDetailsList = new ArrayList<TdrSetting>();
		Session session = HibernateSessionProvider.getSession();
		Transaction tx = session.beginTransaction();
		try {
 
			chargingDetailsList = session
					.createQuery("from TdrSetting  where payId= :payId and currency= :currency and status='ACTIVE' and tdrStatus='ACTIVE' and merchantTdr >= 0")
					.setParameter("payId", payId).setParameter("currency", currency).getResultList();
			tx.commit();
		} catch (ObjectNotFoundException objectNotFound) {
			handleException(objectNotFound, tx);
		} catch (HibernateException hibernateException) {
			handleException(hibernateException, tx);
		} finally {
			autoClose(session);
		}
		return chargingDetailsList;
	}
	
	//AddedBy Sweety
	public List<TdrSetting> getTdrAndSurchargeMerchantId(String payId) {
		Session session = HibernateSessionProvider.getSession();
		CriteriaBuilder cb = session.getCriteriaBuilder();
		CriteriaQuery<TdrSetting> cq = cb.createQuery(TdrSetting.class);
		Root<TdrSetting> root = cq.from(TdrSetting.class);

		cq.where(cb.and(cb.equal(root.get("payId"), payId),
				cb.equal(root.get("status"), "ACTIVE"),cb.or(cb.equal(root.get("tdrStatus"), "PENDING"), cb.equal(root.get("tdrStatus"), "ACTIVE"))));

		Query<TdrSetting> query = session.createQuery(cq);
		logger.info("queryString..."+query);
		logger.info("getTdrAndSurchargeMerchantId Query :" + query.getQueryString());
		List<TdrSetting> list = query.getResultList();
		return list;
	}

	@SuppressWarnings("unchecked")
	public List<Object[]> findDistinctMopDetails(String payId) {
		Session session = HibernateSessionProvider.getSession();
		try {
			return session.createNativeQuery(
					"SELECT distinct mopType, paymentType,currency FROM TdrSetting where PayId=:PayId and status='ACTIVE'")
					.setParameter("PayId", payId).getResultList();
		} finally {
			autoClose(session);
		}
	}
	

	@SuppressWarnings("unchecked")
	public List<String> findPaymentTypeByPayId(String payId) {
		Session session = HibernateSessionProvider.getSession();
		try {
			logger.info("findPaymentTypeByPayId:: payId = {}", payId);
			return session.createNativeQuery("SELECT distinct paymentType FROM TdrSetting where PayId=:PayId and status='ACTIVE'")
					.setParameter("PayId", payId).getResultList();

		} finally {
			autoClose(session);
		}
	}
	
	@SuppressWarnings("unchecked")
	public List<String> findmopbypaymenttype(String payId, String paytype) {
		Session session = HibernateSessionProvider.getSession();
		try {
			logger.info("findmopbypaymenttype:: payId = {}, paymentType={}", payId, paytype);
			return session.createNativeQuery(
					"SELECT distinct mopType FROM TdrSetting where PayId=:PayId and paymentType=:paytype and status='ACTIVE'")
					.setParameter("PayId", payId).setParameter("paytype", paytype).getResultList();
		} finally {
			autoClose(session);
		}
	}
}
