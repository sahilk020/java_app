package com.pay10.commons.dao;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.bson.BsonNull;
import org.bson.Document;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.google.gson.Gson;
import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.pay10.commons.entity.TdrWaveOffSettingEntity;
import com.pay10.commons.mongo.MongoInstance;
import com.pay10.commons.user.TransactionSearchDownloadObject;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.MopType;
import com.pay10.commons.util.PaymentType;
import com.pay10.commons.util.PropertiesManager;
import com.pay10.commons.util.StatusType;
import com.pay10.commons.util.TransactionType;

@Repository
public class TdrWaveOffSettingRepository extends HibernateAbstractDao {
	private static final Logger logger = LoggerFactory.getLogger(TdrWaveOffSettingRepository.class.getName());
	@Autowired
	private MongoInstance mongoInstance;
	private static final String prefix = "MONGO_DB_";

	public TdrWaveOffSettingEntity getTdrFromPayIdAndMinAndMaxAmount(String payid, double amount) {
		try (Session session = HibernateSessionProvider.getSession();) {
			@SuppressWarnings("deprecation")
			Criteria criteria = session.createCriteria(TdrWaveOffSettingEntity.class);
			criteria.add(Restrictions.eq("payId", payid));
			criteria.add(Restrictions.gt("minAmount", amount));
			criteria.add(Restrictions.lt("maxAmount", amount));
			return (TdrWaveOffSettingEntity) criteria.list().get(0);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public TdrWaveOffSettingEntity getTdr(long payid, String paymentType, double amount) {
		try (Session session = HibernateSessionProvider.getSession();) {
//			CriteriaBuilder cb = session.getCriteriaBuilder();
//			CriteriaQuery<TdrWaveOffSettingEntity> cq = cb.createQuery(TdrWaveOffSettingEntity.class);
//			Root<TdrWaveOffSettingEntity> root = cq.from(TdrWaveOffSettingEntity.class);
//
//			cq.where(cb.and(cb.equal(root.get("payId"), payid), cb.equal(root.get("paymentType"), paymentType),
//					cb.lessThanOrEqualTo(root.get("minAmount"), amount),
//					cb.greaterThanOrEqualTo(root.get("maxAmount"), amount)));
//
//			Query<TdrWaveOffSettingEntity> query = session.createQuery(cq);
			
			Query query2=session.createQuery("FROM TdrWaveOffSettingEntity WHERE payId='"+payid+"' AND paymentType='"+paymentType+"' AND '"+amount+"' BETWEEN minAmount AND maxAmount");
			
			
			logger.info("getTdr Query :" + query2.getQueryString());
			List list = query2.getResultList();
			if (list == null && list.size()==0) {
				return null;
			} else {

				TdrWaveOffSettingEntity entity = (TdrWaveOffSettingEntity) list.get(0);
				logger.info("DB VALUES from DB: \n" + new Gson().toJson(entity));
				return entity;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public List<TransactionSearchDownloadObject> getYesterDayMisTransation(String Payid, String dateIndex,
			String counternumber) {
		DecimalFormat decimalFormat = new DecimalFormat();
		decimalFormat.setMaximumFractionDigits(2);

		List<TransactionSearchDownloadObject> list = new ArrayList<>();

		List<String> pop = getPaymentTypeByPayid(Payid);
		
		MongoDatabase dbIns = mongoInstance.getDB();
		MongoCollection<Document> collection = dbIns.getCollection(
				PropertiesManager.propertiesMap.get(prefix + Constants.TRANSACTION_STATUS_COLLECTION.getValue()));
		
		for (String type : pop) {
			
			String typeOfPayment = PaymentType.getPaymentCodeFromInstance(type);
			String paymentName = PaymentType.getpaymentName(typeOfPayment);

			final String bsesCno = PropertiesManager.propertiesMap.get("ENERGY"+typeOfPayment);
			
			TransactionSearchDownloadObject downloadObject = new TransactionSearchDownloadObject();
			downloadObject.setPaymentMethods(paymentName);
			downloadObject.setCounterNumber(bsesCno);

// Total Transaction

			// Total Transaction Amount

			double totalAmount = 0;
			BasicDBObject match = new BasicDBObject();
			match.put(FieldType.DATE_INDEX.getName(), dateIndex);
			match.put(FieldType.STATUS.getName(), StatusType.CAPTURED.getName());
			match.put(FieldType.TXNTYPE.getName(), TransactionType.SALE.getName());
			match.put(FieldType.PAY_ID.getName(), Payid.trim());
			match.put(FieldType.UDF10.getName(), "ENERGY");
			match.put(FieldType.PAYMENT_TYPE.getName(), typeOfPayment);

			List<BasicDBObject> query = Arrays.asList(new BasicDBObject("$match", match),
					new BasicDBObject("$group", new BasicDBObject("_id", new BsonNull()).append("SUM(AMOUNT)",
							new BasicDBObject("$sum", new BasicDBObject("$toDouble", "$AMOUNT")))));

			logger.info("Total Transaction Amount Query By Payment Type : " + query);

			MongoCursor<Document> cursor = collection.aggregate(query).iterator();

			while (cursor.hasNext()) {
				Document document = (Document) cursor.next();
				totalAmount = Double.parseDouble(String.valueOf(document.get("SUM(AMOUNT)")));
			}
			downloadObject.setTotalTransactionAmount(decimalFormat.format(totalAmount).replaceAll(",", ""));
			
			
			// Total Transaction Number
		
			double totaltransaction = 0;
			BasicDBObject match3 = new BasicDBObject();
			match3.put(FieldType.DATE_INDEX.getName(), dateIndex);
			match3.put(FieldType.STATUS.getName(), StatusType.CAPTURED.getName());
			match3.put(FieldType.TXNTYPE.getName(), TransactionType.SALE.getName());
			match3.put(FieldType.PAY_ID.getName(), Payid.trim());
			match3.put(FieldType.UDF10.getName(), "ENERGY");
			match3.put(FieldType.PAYMENT_TYPE.getName(), typeOfPayment);

			List<BasicDBObject> query3 = Arrays.asList(new BasicDBObject("$match", match3), new BasicDBObject("$group",
					new BasicDBObject("_id", "$PAY_ID").append("sum", new BasicDBObject("$sum", 1L))));

			logger.info("Total Transaction Count Query By Payment Type : " + query3);

			MongoCursor<Document> cursor3 = collection.aggregate(query3).iterator();

			while (cursor3.hasNext()) {
				Document document = (Document) cursor3.next();
				totaltransaction = Double.parseDouble(String.valueOf(document.get("sum")));
			}
			downloadObject.setTotalTransactionCount(String.valueOf(totaltransaction));
			
			
// Total Surcharge Transaction
			
			// Total Surcharge Transaction Amount
			double totalSurchargeAmount = 0;
			BasicDBObject match2 = new BasicDBObject();
			match2.put(FieldType.DATE_INDEX.getName(), dateIndex);
			match2.put(FieldType.STATUS.getName(), StatusType.CAPTURED.getName());
			match2.put(FieldType.TXNTYPE.getName(), TransactionType.SALE.getName());
			match2.put(FieldType.PAY_ID.getName(), Payid.trim());
			match2.put(FieldType.UDF10.getName(), "ENERGY");
			match2.put(FieldType.PAYMENT_TYPE.getName(), typeOfPayment);
			match2.put(FieldType.SURCHARGE_FLAG.getName(), "Y");

			List<BasicDBObject> query2 = Arrays.asList(new BasicDBObject("$match", match2),
					new BasicDBObject("$group", new BasicDBObject("_id", new BsonNull()).append("SUM(AMOUNT)",
							new BasicDBObject("$sum", new BasicDBObject("$toDouble", "$AMOUNT")))));

			logger.info("Total Surcharge Transaction Amount Query : " + query2);

			MongoCursor<Document> cursor2 = collection.aggregate(query2).iterator();

			while (cursor2.hasNext()) {
				Document document = (Document) cursor2.next();
				totalSurchargeAmount = Double.parseDouble(String.valueOf(document.get("SUM(AMOUNT)")));
			}
			downloadObject.setTotalSurchargeTransactionAmount(decimalFormat.format(totalSurchargeAmount).replaceAll(",", ""));
			
			// Total Surcharge Transaction Count
			double totalSurchargetransaction = 0;
			BasicDBObject match5 = new BasicDBObject();
			match5.put(FieldType.DATE_INDEX.getName(), dateIndex);
			match5.put(FieldType.STATUS.getName(), StatusType.CAPTURED.getName());
			match5.put(FieldType.TXNTYPE.getName(), TransactionType.SALE.getName());
			match5.put(FieldType.PAY_ID.getName(), Payid.trim());
			match5.put(FieldType.UDF10.getName(), "ENERGY");
			match5.put(FieldType.PAYMENT_TYPE.getName(), typeOfPayment);
			match5.put(FieldType.SURCHARGE_FLAG.getName(), "Y");

			List<BasicDBObject> query5 = Arrays.asList(new BasicDBObject("$match", match5), new BasicDBObject("$group",
					new BasicDBObject("_id", "$PAY_ID").append("sum", new BasicDBObject("$sum", 1L))));

			logger.info("Total Surcharge Transaction Count Query : " + query5);

			MongoCursor<Document> cursor5 = collection.aggregate(query5).iterator();

			while (cursor5.hasNext()) {
				Document document = (Document) cursor5.next();
				totalSurchargetransaction = Double.valueOf(String.valueOf(document.get("sum")));
			}
			downloadObject.setTotalSurchargeTransactionCount(String.valueOf(totalSurchargetransaction));
			
// Total Transaction Without Surcharge
			
			// Total Transaction Amount Without Surcharge
			double totalMdrAmount = 0;
			BasicDBObject match1 = new BasicDBObject();
			match1.put(FieldType.DATE_INDEX.getName(), dateIndex);
			match1.put(FieldType.STATUS.getName(), StatusType.CAPTURED.getName());
			match1.put(FieldType.TXNTYPE.getName(), TransactionType.SALE.getName());
			match1.put(FieldType.PAY_ID.getName(), Payid.trim());
			match1.put(FieldType.UDF10.getName(), "ENERGY");
			match1.put(FieldType.PAYMENT_TYPE.getName(), typeOfPayment);
			match1.put(FieldType.SURCHARGE_FLAG.getName(), new BsonNull());

			List<BasicDBObject> query1 = Arrays.asList(new BasicDBObject("$match", match1),
					new BasicDBObject("$group", new BasicDBObject("_id", new BsonNull()).append("SUM(AMOUNT)",
							new BasicDBObject("$sum", new BasicDBObject("$toDouble", "$AMOUNT")))));

			logger.info("Total Transaction Amount Without Surcharge Query : " + query1);

			MongoCursor<Document> cursor1 = collection.aggregate(query1).iterator();

			while (cursor1.hasNext()) {
				Document document = (Document) cursor1.next();
				totalMdrAmount = Double.valueOf(String.valueOf(document.get("SUM(AMOUNT)")));
			}
			downloadObject.setTotalMdrTransactionAmount(decimalFormat.format(totalMdrAmount).replaceAll(",", ""));
			// Total Transaction Count Without Surcharge
			double totalMdrtransaction = 0;
			BasicDBObject match4 = new BasicDBObject();
			match4.put(FieldType.DATE_INDEX.getName(), dateIndex);
			match4.put(FieldType.STATUS.getName(), StatusType.CAPTURED.getName());
			match4.put(FieldType.TXNTYPE.getName(), TransactionType.SALE.getName());
			match4.put(FieldType.PAY_ID.getName(), Payid.trim());
			match4.put(FieldType.UDF10.getName(), "ENERGY");
			match4.put(FieldType.PAYMENT_TYPE.getName(), typeOfPayment);
			match4.put(FieldType.SURCHARGE_FLAG.getName(), new BsonNull());

			List<BasicDBObject> query4 = Arrays.asList(new BasicDBObject("$match", match4), new BasicDBObject("$group",
					new BasicDBObject("_id", "$PAY_ID").append("sum", new BasicDBObject("$sum", 1L))));

			logger.info("Total Transaction Count Without Surcharge Query : " + query4);

			MongoCursor<Document> cursor4 = collection.aggregate(query4).iterator();

			while (cursor4.hasNext()) {
				Document document = (Document) cursor4.next();
				totalMdrtransaction = Double.valueOf(String.valueOf(document.get("sum")));
			}
			downloadObject.setTotalMdrTransactionCount(String.valueOf(totalMdrtransaction));

// Total MDR Charge
		
		//Find PG TDR
			double totalpgMDR = 0;
			BasicDBObject match7 = new BasicDBObject();
			match7.put(FieldType.DATE_INDEX.getName(), dateIndex);
			match7.put(FieldType.STATUS.getName(), StatusType.CAPTURED.getName());
			match7.put(FieldType.TXNTYPE.getName(), TransactionType.SALE.getName());
			match7.put(FieldType.PAY_ID.getName(), Payid.trim());
			match7.put(FieldType.UDF10.getName(), "ENERGY");
			match7.put(FieldType.PAYMENT_TYPE.getName(), typeOfPayment);
			match7.put(FieldType.SURCHARGE_FLAG.getName(), new BsonNull());

			List<BasicDBObject> query7 = Arrays.asList(new BasicDBObject("$match", match7),
					new BasicDBObject("$group", new BasicDBObject("_id", new BsonNull()).append("SUM(AMOUNT)",
							new BasicDBObject("$sum", new BasicDBObject("$toDouble", "$PG_TDR_SC")))));

			logger.info("Find PG TDR Query : " + query7);

			MongoCursor<Document> cursor7 = collection.aggregate(query7).iterator();

			while (cursor7.hasNext()) {
				Document document = (Document) cursor7.next();
				totalpgMDR = Double.parseDouble(String.valueOf(document.get("SUM(AMOUNT)")));
			}
		//Find ACQUIRER TDR
			double totalacqMDR = 0;
			BasicDBObject match6 = new BasicDBObject();
			match6.put(FieldType.DATE_INDEX.getName(), dateIndex);
			match6.put(FieldType.STATUS.getName(), StatusType.CAPTURED.getName());
			match6.put(FieldType.TXNTYPE.getName(), TransactionType.SALE.getName());
			match6.put(FieldType.PAY_ID.getName(), Payid.trim());
			match6.put(FieldType.UDF10.getName(), "ENERGY");
			match6.put(FieldType.PAYMENT_TYPE.getName(), typeOfPayment);
			match6.put(FieldType.SURCHARGE_FLAG.getName(), new BsonNull());

			List<BasicDBObject> query6 = Arrays.asList(new BasicDBObject("$match", match6),
					new BasicDBObject("$group", new BasicDBObject("_id", new BsonNull()).append("SUM(AMOUNT)",
							new BasicDBObject("$sum", new BasicDBObject("$toDouble", "$ACQUIRER_TDR_SC")))));

			logger.info("Find ACQUIRER TDR Query : " + query6);

			MongoCursor<Document> cursor6 = collection.aggregate(query6).iterator();

			while (cursor6.hasNext()) {
				Document document = (Document) cursor6.next();
				totalacqMDR = Double.parseDouble(String.valueOf(document.get("SUM(AMOUNT)")));
			}	
			double totalMdrCharge=totalacqMDR+totalpgMDR;
		downloadObject.setTotalMdrCharge(decimalFormat.format(totalMdrCharge).replaceAll(",", ""));	

// Total GST Charge
		
		//Find Acquirer GST
		double acquirerGST = 0;
		BasicDBObject match8 = new BasicDBObject();
		match8.put(FieldType.DATE_INDEX.getName(), dateIndex);
		match8.put(FieldType.STATUS.getName(), StatusType.CAPTURED.getName());
		match8.put(FieldType.TXNTYPE.getName(), TransactionType.SALE.getName());
		match8.put(FieldType.PAY_ID.getName(), Payid.trim());
		match8.put(FieldType.UDF10.getName(), "ENERGY");
		match8.put(FieldType.PAYMENT_TYPE.getName(), typeOfPayment);
		match8.put(FieldType.SURCHARGE_FLAG.getName(), new BsonNull());

		List<BasicDBObject> query8 = Arrays.asList(new BasicDBObject("$match", match8),
				new BasicDBObject("$group", new BasicDBObject("_id", new BsonNull()).append("SUM(AMOUNT)",
						new BasicDBObject("$sum", new BasicDBObject("$toDouble", "$ACQUIRER_GST")))));

		logger.info("Find Acquirer GST Query : " + query8);

		MongoCursor<Document> cursor8 = collection.aggregate(query8).iterator();

		while (cursor8.hasNext()) {
			Document document = (Document) cursor8.next();
			acquirerGST = Double.parseDouble(String.valueOf(document.get("SUM(AMOUNT)")));
		}	
		//Find PG TDR
		double pgGst = 0;
		BasicDBObject match9 = new BasicDBObject();
		match9.put(FieldType.DATE_INDEX.getName(), dateIndex);
		match9.put(FieldType.STATUS.getName(), StatusType.CAPTURED.getName());
		match9.put(FieldType.TXNTYPE.getName(), TransactionType.SALE.getName());
		match9.put(FieldType.PAY_ID.getName(), Payid.trim());
		match9.put(FieldType.UDF10.getName(), "ENERGY");
		match9.put(FieldType.PAYMENT_TYPE.getName(), typeOfPayment);
		match9.put(FieldType.SURCHARGE_FLAG.getName(), new BsonNull());

		List<BasicDBObject> query9 = Arrays.asList(new BasicDBObject("$match", match9),
				new BasicDBObject("$group", new BasicDBObject("_id", new BsonNull()).append("SUM(AMOUNT)",
						new BasicDBObject("$sum", new BasicDBObject("$toDouble", "$PG_GST")))));

		logger.info("Find PG TDR Query : " + query9);

		MongoCursor<Document> cursor9 = collection.aggregate(query9).iterator();

		while (cursor9.hasNext()) {
			Document document = (Document) cursor9.next();
			pgGst = Double.parseDouble(String.valueOf(document.get("SUM(AMOUNT)")));
		}
		double totalGST=acquirerGST+pgGst;
		downloadObject.setTotalGst(decimalFormat.format(totalGST).replaceAll(",", ""));	
		
		double netAmount=totalAmount-totalGST-totalMdrCharge;
		downloadObject.setTotalNetAmount(decimalFormat.format(netAmount).replaceAll(",", ""));
		list.add(downloadObject);
		}
		return list;
	}
	public List<String> getPaymentTypes(String payId, String dateIndex){
		List<String>paymentTypes=new ArrayList<>();
		MongoDatabase dbIns = mongoInstance.getDB();
		MongoCollection<Document> collection = dbIns.getCollection(
				PropertiesManager.propertiesMap.get(prefix + Constants.TRANSACTION_STATUS_COLLECTION.getValue()));
		
		BasicDBObject match=new BasicDBObject();
		match.put(FieldType.PAYMENT_TYPE.getName(), new BasicDBObject("$ne", 
			    new BsonNull()));
		match.put(FieldType.DATE_INDEX.getName(), dateIndex);
		
		match.put(FieldType.PAY_ID.getName(), payId);
		match.put(FieldType.UDF10.getName(), "ENERGY");
		
		BasicDBObject group=new BasicDBObject();
		
		group.put("_id", FieldType.PAYMENT_TYPE.getName());
		
		
		BasicDBObject project=new BasicDBObject();
		project.put(FieldType.PAYMENT_TYPE.getName(), "$_id");
		project.put("_id", 0L);
		
		List<BasicDBObject> query =Arrays.asList(new BasicDBObject("$match",match),new BasicDBObject("$group",group),new BasicDBObject("$project",project));
		
		logger.info("getPaymentTypes() Query : "+query);
		
		MongoCursor<Document>cursor=collection.aggregate(query).iterator();
		
		while (cursor.hasNext()) {
			Document document = (Document) cursor.next();
			String paymentTyp=document.getString("PAYMENT_TYPE");
			paymentTypes.add(paymentTyp);
		}
		
		return paymentTypes;
	}
	public List<TransactionSearchDownloadObject> getYesterDayTransationForTxtFile(String Payid, String dateIndex,String paymentType)
			throws ParseException {
		List<TransactionSearchDownloadObject> list = new ArrayList<>();

		MongoDatabase dbIns = mongoInstance.getDB();
		MongoCollection<Document> collection = dbIns.getCollection(
				PropertiesManager.propertiesMap.get(prefix + Constants.TRANSACTION_STATUS_COLLECTION.getValue()));
		BasicDBObject match = new BasicDBObject();
		match.put(FieldType.DATE_INDEX.getName(), dateIndex);
		match.put(FieldType.STATUS.getName(), StatusType.CAPTURED.getName());
		match.put(FieldType.PAY_ID.getName(), Payid.trim());
		match.put(FieldType.TXNTYPE.getName(), TransactionType.SALE.getName());
		match.put(FieldType.UDF10.getName(), "ENERGY");
		match.put(FieldType.PAYMENT_TYPE.getName(), paymentType);

		List<BasicDBObject> query = Arrays.asList(new BasicDBObject("$match", match));

		logger.info("getYesterDayTransation Query : " + query);

		MongoCursor<Document> cursor = collection.aggregate(query).iterator();

		while (cursor.hasNext()) {
			TransactionSearchDownloadObject transactionSearchDownloadObject = new TransactionSearchDownloadObject();
			Document document = (Document) cursor.next();
			transactionSearchDownloadObject.setOrderId(String.valueOf(document.get(FieldType.ORDER_ID.getName())));
			transactionSearchDownloadObject.setUdf5(String.valueOf(document.get(FieldType.UDF9.getName())));
			transactionSearchDownloadObject.setDateindex(String.valueOf(document.get(FieldType.DATE_INDEX.getName())));
			transactionSearchDownloadObject
					.setGrossAmount(String.valueOf(document.get(FieldType.TOTAL_AMOUNT.getName())));
			transactionSearchDownloadObject.setAmount(String.valueOf(document.get(FieldType.AMOUNT.getName())));
			transactionSearchDownloadObject
					.setAcquirerGst(String.valueOf(document.get(FieldType.ACQUIRER_GST.getName()) != null
							? document.get(FieldType.ACQUIRER_GST.getName())
							: "0"));
			transactionSearchDownloadObject.setPgGst(String.valueOf(
					document.get(FieldType.PG_GST.getName()) != null ? document.get(FieldType.PG_GST.getName()) : "0"));
			transactionSearchDownloadObject
					.setAcquirerTdrSC(String.valueOf(document.get(FieldType.ACQUIRER_TDR_SC.getName()) != null
							? document.get(FieldType.ACQUIRER_TDR_SC.getName())
							: "0"));
			transactionSearchDownloadObject.setPgTdrSC(String.valueOf(
					document.get(FieldType.PG_TDR_SC.getName()) != null ? document.get(FieldType.PG_TDR_SC.getName())
							: "0"));
			transactionSearchDownloadObject.setPaymentMethods(
					PaymentType.getpaymentName(String.valueOf(document.get(FieldType.PAYMENT_TYPE.getName()))));
			transactionSearchDownloadObject
					.setMopType(MopType.getmopName((String.valueOf(document.get(FieldType.MOP_TYPE.getName())))));
			transactionSearchDownloadObject
					.setSurchargeFlag(document.getString(FieldType.SURCHARGE_FLAG.getName()) != null
							? document.getString(FieldType.SURCHARGE_FLAG.getName())
							: "N");
			list.add(transactionSearchDownloadObject);

		}

		return list;
	}

	public List<TransactionSearchDownloadObject> getYesterDayTransation(String Payid, String dateIndex)
			throws ParseException {
		List<TransactionSearchDownloadObject> list = new ArrayList<>();

		MongoDatabase dbIns = mongoInstance.getDB();
		MongoCollection<Document> collection = dbIns.getCollection(
				PropertiesManager.propertiesMap.get(prefix + Constants.TRANSACTION_STATUS_COLLECTION.getValue()));
		BasicDBObject match = new BasicDBObject();
		match.put(FieldType.DATE_INDEX.getName(), dateIndex);
		match.put(FieldType.STATUS.getName(), StatusType.CAPTURED.getName());
		match.put(FieldType.PAY_ID.getName(), Payid.trim());
		match.put(FieldType.TXNTYPE.getName(), TransactionType.SALE.getName());
		match.put(FieldType.UDF10.getName(), "ENERGY");

		List<BasicDBObject> query = Arrays.asList(new BasicDBObject("$match", match));

		logger.info("getYesterDayTransation Query : " + query);

		MongoCursor<Document> cursor = collection.aggregate(query).iterator();

		while (cursor.hasNext()) {
			TransactionSearchDownloadObject transactionSearchDownloadObject = new TransactionSearchDownloadObject();
			Document document = (Document) cursor.next();
			transactionSearchDownloadObject.setOrderId(String.valueOf(document.get(FieldType.ORDER_ID.getName())));
			transactionSearchDownloadObject.setUdf5(String.valueOf(document.get(FieldType.UDF9.getName())));
			transactionSearchDownloadObject.setDateindex(String.valueOf(document.get(FieldType.DATE_INDEX.getName())));
			transactionSearchDownloadObject
					.setGrossAmount(String.valueOf(document.get(FieldType.TOTAL_AMOUNT.getName())));
			transactionSearchDownloadObject.setAmount(String.valueOf(document.get(FieldType.AMOUNT.getName())));
			transactionSearchDownloadObject
					.setAcquirerGst(String.valueOf(document.get(FieldType.ACQUIRER_GST.getName()) != null
							? document.get(FieldType.ACQUIRER_GST.getName())
							: "0"));
			transactionSearchDownloadObject.setPgGst(String.valueOf(
					document.get(FieldType.PG_GST.getName()) != null ? document.get(FieldType.PG_GST.getName()) : "0"));
			transactionSearchDownloadObject
					.setAcquirerTdrSC(String.valueOf(document.get(FieldType.ACQUIRER_TDR_SC.getName()) != null
							? document.get(FieldType.ACQUIRER_TDR_SC.getName())
							: "0"));
			transactionSearchDownloadObject.setPgTdrSC(String.valueOf(
					document.get(FieldType.PG_TDR_SC.getName()) != null ? document.get(FieldType.PG_TDR_SC.getName())
							: "0"));
			transactionSearchDownloadObject.setPaymentMethods(
					PaymentType.getpaymentName(String.valueOf(document.get(FieldType.PAYMENT_TYPE.getName()))));
			transactionSearchDownloadObject
					.setMopType(MopType.getmopName((String.valueOf(document.get(FieldType.MOP_TYPE.getName())))));
			transactionSearchDownloadObject
					.setSurchargeFlag(document.getString(FieldType.SURCHARGE_FLAG.getName()) != null
							? document.getString(FieldType.SURCHARGE_FLAG.getName())
							: "N");
			list.add(transactionSearchDownloadObject);

		}

		return list;
	}

	public double getYesterDayTransationAmount(String Payid, String dateIndex) throws ParseException {

		double totalAmount = 0;
		MongoDatabase dbIns = mongoInstance.getDB();
		MongoCollection<Document> collection = dbIns.getCollection(
				PropertiesManager.propertiesMap.get(prefix + Constants.TRANSACTION_STATUS_COLLECTION.getValue()));
		BasicDBObject match = new BasicDBObject();
		match.put(FieldType.DATE_INDEX.getName(), dateIndex);
		match.put(FieldType.STATUS.getName(), StatusType.CAPTURED.getName());
		match.put(FieldType.TXNTYPE.getName(), TransactionType.SALE.getName());
		match.put(FieldType.PAY_ID.getName(), Payid.trim());

		List<BasicDBObject> query = Arrays.asList(new BasicDBObject("$match", match),
				new BasicDBObject("$group", new BasicDBObject("_id", new BsonNull()).append("SUM(AMOUNT)",
						new BasicDBObject("$sum", new BasicDBObject("$toDouble", "$AMOUNT")))));

		logger.info("getYesterDayTransationAmount() Query : " + query);

		MongoCursor<Document> cursor = collection.aggregate(query).iterator();

		while (cursor.hasNext()) {
			Document document = (Document) cursor.next();
			totalAmount = Double.parseDouble(String.valueOf(document.get("SUM(AMOUNT)")));
		}

		return totalAmount;
	}

	public BigDecimal[] getTdrReports(String payid, String paymentType, String amount) {
		DecimalFormat decimalFormat = new DecimalFormat();
		decimalFormat.setMaximumFractionDigits(2);

		DecimalFormat decimalFormat1 = new DecimalFormat();
		decimalFormat1.setMaximumFractionDigits(3);

		logger.info("payid : " + payid + "\tpaymentType : " + paymentType + "\ttotalamount :" + amount);

		BigDecimal sucharge = new BigDecimal(0);
		BigDecimal suchargeamount = new BigDecimal(0);

		BigDecimal totalAmount[] = new BigDecimal[2];

		String paymenttype = paymentType.replaceAll("=", "");
		double totalamount = (Double.valueOf(amount));
		long payID = Long.parseLong(payid);

		TdrWaveOffSettingEntity entity = getTdr(payID, paymenttype, totalamount);
		if (entity == null) {
			totalAmount[0] = sucharge;
			totalAmount[1] = BigDecimal.valueOf(totalamount);
		} else {
			if (entity.getIsExampted().equalsIgnoreCase("N")) {
				double percentage = entity.getPgPercentage() + entity.getBankPercentage();
				double tdr = ((percentage / (double) 100) * totalamount);
				double gst = tdr * (entity.getGst() / 100);
				double tdrWithGst = tdr + gst;
				sucharge = BigDecimal.valueOf(tdrWithGst);
				suchargeamount = BigDecimal.valueOf((totalamount));

				totalAmount[0] = BigDecimal
						.valueOf(Double.valueOf(decimalFormat1.format(sucharge.doubleValue()).replaceAll(",", "")));
				totalAmount[1] = BigDecimal.valueOf(
						Double.valueOf(decimalFormat1.format(suchargeamount.doubleValue()).replaceAll(",", "")));

			} else {
				totalAmount[0] = sucharge;
				totalAmount[1] = BigDecimal
						.valueOf(Double.valueOf(decimalFormat.format(totalamount).replaceAll(",", "")));
			}
		}
		return totalAmount;
	}

	public BigDecimal[] getTdr(String payid, String paymentType, String amount) {
		DecimalFormat decimalFormat = new DecimalFormat();
		decimalFormat.setMaximumFractionDigits(2);

		DecimalFormat decimalFormat1 = new DecimalFormat();
		decimalFormat1.setMaximumFractionDigits(3);

		logger.info("payid : " + payid + "\tpaymentType : " + paymentType + "\ttotalamount :" + amount);

		BigDecimal sucharge = new BigDecimal(0);
		BigDecimal suchargeamount = new BigDecimal(0);

		BigDecimal totalAmount[] = new BigDecimal[2];

		String paymenttype = paymentType.replaceAll("=", "");
		double totalamount = (Double.valueOf(amount) / 100);
		long payID = Long.parseLong(payid);

		TdrWaveOffSettingEntity entity = getTdr(payID, paymenttype, totalamount);
		if (entity == null) {
			totalAmount[0] = sucharge;
			totalAmount[1] = BigDecimal.valueOf(totalamount);
		} else {
			if (entity.getIsExampted().equalsIgnoreCase("N")) {
				double percentage = entity.getPgPercentage() + entity.getBankPercentage();
				double tdr = ((percentage / (double) 100) * totalamount);
				double gst = tdr * (entity.getGst() / 100);
				double tdrWithGst = tdr + gst;
				sucharge = BigDecimal.valueOf(tdrWithGst);
				suchargeamount = BigDecimal.valueOf((totalamount));

				totalAmount[0] = BigDecimal
						.valueOf(Double.valueOf(decimalFormat1.format(sucharge.doubleValue()).replaceAll(",", "")));
				totalAmount[1] = BigDecimal.valueOf(
						Double.valueOf(decimalFormat1.format(suchargeamount.doubleValue()).replaceAll(",", "")));

			} else {
				totalAmount[0] = sucharge;
				totalAmount[1] = BigDecimal
						.valueOf(Double.valueOf(decimalFormat.format(totalamount).replaceAll(",", "")));
			}
		}
		return totalAmount;
	}

	public void save(TdrWaveOffSettingEntity entity) {
		super.save(entity);
	}

	public List<String> getPaymentTypeByPayid(String payid) {
		try (Session session = HibernateSessionProvider.getSession();) {
			logger.info("getPaymentTypeByPayid :" + payid);
			String sql = "SELECT DISTINCT paymentType FROM TdrSetting WHERE payId = :payId AND status = :status AND tdrStatus = :tdrStatus";
			Query query = session.createNativeQuery(sql);
			query.setParameter("payId", Long.valueOf(payid));
			query.setParameter("status", "ACTIVE");
			query.setParameter("tdrStatus", "ACTIVE");
			List<String> resultList = query.getResultList();
			return resultList;
			//return Arrays.asList("CREDIT_CARD", "DEBIT_CARD", "NET_BANKING", "UPI");
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
