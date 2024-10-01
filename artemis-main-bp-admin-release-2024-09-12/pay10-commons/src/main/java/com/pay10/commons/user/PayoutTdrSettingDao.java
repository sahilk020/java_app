package com.pay10.commons.user;

import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;

import org.bson.BsonNull;
import org.bson.Document;
import org.hibernate.HibernateException;
import org.hibernate.ObjectNotFoundException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.pay10.commons.dao.HibernateAbstractDao;
import com.pay10.commons.dao.HibernateSessionProvider;
import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.exception.SystemException;
import com.pay10.commons.mongo.MongoInstance;
import com.pay10.commons.util.Amount;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.PropertiesManager;
import com.pay10.commons.util.StatusTypePayout;
import com.pay10.commons.util.TDRStatus;

@Component
public class PayoutTdrSettingDao extends HibernateAbstractDao {
	private static Logger logger = LoggerFactory.getLogger(PayoutTdrSettingDao.class.getName());

	@Autowired
	private PropertiesManager propertiesManager;

	private static final String prefix = "MONGO_DB_";
	@Autowired
	private MongoInstance mongoInstance;

	public boolean getruleenginval(String Acquirer, String Currency, String Channel, String payId) {
		BigInteger count = new BigInteger("0");
		Session session = HibernateSessionProvider.getSession();
		Transaction tx = session.beginTransaction();
		try {
			logger.info("Start in Tdr Setting Detial" + Acquirer + Currency + Channel + payId);
			String sqlQuery = "select COUNT(*) from TdrSettingPayout where acquirerName=:acquirerName and payId=:payId and currency =:currency and channel=:channel ";
			count = (BigInteger) session.createNativeQuery(sqlQuery).setParameter("payId", payId)
					.setParameter("acquirerName", Acquirer).setParameter("currency", Currency)
					.setParameter("channel", Channel).getSingleResult();
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
	
	public Fields calculateTdrAndSurcharge(Fields fields) throws SystemException {

		String commission = "0";
		MongoDatabase dbIns = mongoInstance.getDB();
		MongoCollection<Document> coll = dbIns
				.getCollection(PropertiesManager.propertiesMap.get(prefix + Constants.COMMISSION_PO.getValue()));
		BasicDBObject match = new BasicDBObject();

		match.put("payId", fields.get(FieldType.PAY_ID.getName()));
		// match.put(FieldType.ACQUIRER_TYPE.getName(),
		// fields.get(FieldType.ACQUIRER_TYPE.getName()));
		match.put("currency", fields.get(FieldType.CURRENCY_CODE.getName()));
		match.put("status", "ACTIVE");
		match.put("createDate",
				new BasicDBObject("$lte", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())));

		List<BasicDBObject> queryExecute = Arrays.asList(new BasicDBObject("$match", match));

		MongoCursor<Document> cursor = coll.aggregate(queryExecute).iterator();

		logger.info("Query for getting commsion : " + queryExecute);

		String preference = "";

		if (cursor.hasNext()) {
			Document document = (Document) cursor.next();
			preference = document.getString("type");
			commission = document.getString("value");
			// commission=commission.multiply(new BigDecimal(100));
			fields.put(FieldType.ACQUIRER_TYPE.getName(), document.getString("acquirer"));
		} else {
			throw new SystemException(ErrorType.ACQUIRER_NOT_FOUND, "ACQUIRER_NOT_FOUND");
		}

		String amount = fields.get(FieldType.AMOUNT.getName());
		// String comm=Amount.formatAmount(commission,
		// fields.get(FieldType.CURRENCY_CODE.getName()));

		if (preference.equalsIgnoreCase("flat")) {

			String comm = Amount.formatAmount(commission, "356");

			int totalamount = (Integer.parseInt(comm) + Integer.parseInt(amount));

			fields.put(FieldType.TOTAL_AMOUNT.getName(), String.valueOf(totalamount));
			fields.put(FieldType.SURCHARGE_AMOUNT.getName(), comm);

		} else {
			String comm = Amount.toDecimal(Amount.formatAmount(commission, "356"), "356");
			double com = ((Double.parseDouble(comm) / 100) * (Double.parseDouble(Amount.toDecimal(amount, "356"))));
			String sur = Amount.formatAmount(String.valueOf(com), "356");

			fields.put(FieldType.TOTAL_AMOUNT.getName(),
					String.valueOf((Integer.valueOf(sur) + Integer.valueOf(amount))));
			fields.put(FieldType.SURCHARGE_AMOUNT.getName(), sur);
		}

		return fields;
	}

	public void frm(Fields fields) throws SystemException {
		MongoDatabase dbIns = mongoInstance.getDB();
		MongoCollection<Document> coll = dbIns
				.getCollection(PropertiesManager.propertiesMap.get(prefix + Constants.PO_FRM.getValue()));

		BasicDBObject match = new BasicDBObject();

		match.put(FieldType.PAY_ID.getName(), fields.get(FieldType.PAY_ID.getName()));
		match.put(FieldType.CURRENCY_CODE.getName(), fields.get(FieldType.CURRENCY_CODE.getName()));
		match.put(FieldType.CHANNEL.getName(), fields.get(FieldType.PAY_TYPE.getName()));
		match.put(FieldType.STATUS.getName(), TDRStatus.ACTIVE.getName());

		List<BasicDBObject> queryExecute = Arrays.asList(new BasicDBObject("$match", match));

		logger.info("Query for FRM : "+match);
		
		MongoCursor<Document> cursor = coll.aggregate(queryExecute).iterator();
		double amount = Double.valueOf(Amount.toDecimal(fields.get(FieldType.AMOUNT.getName()),
				fields.get(FieldType.CURRENCY_CODE.getName())));
		if (cursor.hasNext()) {
			Document document = cursor.next();
			double minTicketSize = Double.valueOf(document.getString(FieldType.MIN_TICKET_SIZE.getName()));
			double maxTicketSize = Double.valueOf(document.getString(FieldType.MAX_TICKET_SIZE.getName()));

			double dailyLimit = Double.valueOf(document.getString(FieldType.DAILY_LIMIT.getName()));
			double dailyVolume = Double.valueOf(document.getString(FieldType.DAILY_VOLUME.getName()));
			double weeklyLimit = Double.valueOf(document.getString(FieldType.WEEKLY_LIMIT.getName()));
			double weeklyVolume = Double.valueOf(document.getString(FieldType.WEEKLY_VOLUME.getName()));
			double monthlyLimit = Double.valueOf(document.getString(FieldType.MONTHLY_LIMIT.getName()));
			double monthlyVolume = Double.valueOf(document.getString(FieldType.MONTHLY_VOLUME.getName()));
			logger.info("A " + (amount < minTicketSize));
			if (amount < minTicketSize) {
				throw new SystemException(ErrorType.PO_FRM_MIN_TICKET_SIZE,
						ErrorType.PO_FRM_MIN_TICKET_SIZE.getResponseCode());
			}
			logger.info("B " + (amount > maxTicketSize));
			if (amount > maxTicketSize) {
				throw new SystemException(ErrorType.PO_FRM_MAX_TICKET_SIZE,
						ErrorType.PO_FRM_MAX_TICKET_SIZE.getResponseCode());
			}
			logger.info("C " + (dailyLimit > getDailyLimit(fields)));
			if (dailyLimit > getDailyLimit(fields)) {
				logger.info("D " + (dailyVolume < getDailyVolume(fields)));
				if (dailyVolume < getDailyVolume(fields)) {
					throw new SystemException(ErrorType.PO_FRM_DAILY_VOLUME,
							ErrorType.PO_FRM_DAILY_VOLUME.getResponseCode());
				}
			} else {
				throw new SystemException(ErrorType.PO_FRM_DAILY_LIMIT, ErrorType.PO_FRM_DAILY_LIMIT.getResponseCode());
			}
			logger.info("E " + (weeklyLimit > getWeeklyLimit(fields)));
			if (weeklyLimit > getWeeklyLimit(fields)) {
				logger.info("F " + (weeklyVolume < getWeeklyVolume(fields)));
				if (weeklyVolume < getWeeklyVolume(fields)) {
					throw new SystemException(ErrorType.PO_FRM_WEEKLY_VOLUME,
							ErrorType.PO_FRM_WEEKLY_VOLUME.getResponseCode());
				}
			} else {
				throw new SystemException(ErrorType.PO_FRM_WEEKLY_LIMIT,
						ErrorType.PO_FRM_WEEKLY_LIMIT.getResponseCode());
			}
			logger.info("G " + (monthlyLimit > getMonthlyLimit(fields)));
			if (monthlyLimit > getMonthlyLimit(fields)) {
				logger.info("H " + (monthlyVolume < getMonthlyVolume(fields)));
				if (monthlyVolume < getMonthlyVolume(fields)) {
					throw new SystemException(ErrorType.PO_FRM_MONTHLY_VOLUME,
							ErrorType.PO_FRM_MONTHLY_VOLUME.getResponseCode());
				}
			} else {
				throw new SystemException(ErrorType.PO_FRM_MONTHLY_LIMIT,
						ErrorType.PO_FRM_MONTHLY_LIMIT.getResponseCode());
			}

		} else {
			throw new SystemException(ErrorType.PO_FRM, ErrorType.PO_FRM.getResponseCode());
		}
		logger.info("Final Payour FRM processor Response ..... ");
	}

	public double getDailyLimit(Fields fields) {
		MongoDatabase dbIns = mongoInstance.getDB();
		MongoCollection<Document> coll = dbIns.getCollection(
				PropertiesManager.propertiesMap.get(prefix + Constants.TRANSACTION_LEDGER_PO.getValue()));

		List<Document> queryExecute = Arrays.asList(
				new Document("$match", new Document(FieldType.PAY_ID.getName(), fields.get(FieldType.PAY_ID.getName()))
						.append(FieldType.CURRENCY_CODE.getName(), fields.get(FieldType.CURRENCY_CODE.getName()))
						.append(FieldType.PAY_TYPE.getName(), fields.get(FieldType.PAY_TYPE.getName()))
						.append(FieldType.DATE_INDEX.getName(), new SimpleDateFormat("yyyyMMdd").format(new Date()))
						.append(FieldType.STATUS.getName(), StatusTypePayout.REQUEST_ACCEPTED.getName())),
				new Document("$group", new Document("_id", new BsonNull()).append("count", new Document("$sum", 1L))),
				new Document("$project", new Document("_id", 0L).append("count", 1L)));

		MongoCursor<Document> cursor = coll.aggregate(queryExecute).iterator();

		logger.info("getDailyLimit Query :" + queryExecute);

		if (cursor.hasNext()) {
			Document document = cursor.next();
			double daillyLimit = Double.valueOf(""+document.get("count"));
			logger.info("getDailyLimit  :" + daillyLimit);
			return daillyLimit;
		} else {
			return 0;
		}

	}

	public double getDailyVolume(Fields fields) {
		MongoDatabase dbIns = mongoInstance.getDB();
		MongoCollection<Document> coll = dbIns.getCollection(
				PropertiesManager.propertiesMap.get(prefix + Constants.TRANSACTION_LEDGER_PO.getValue()));

		List<Document> queryExecute = Arrays.asList(
				new Document("$match", new Document(FieldType.PAY_ID.getName(), fields.get(FieldType.PAY_ID.getName()))
						.append(FieldType.CURRENCY_CODE.getName(), fields.get(FieldType.CURRENCY_CODE.getName()))
						.append(FieldType.PAY_TYPE.getName(), fields.get(FieldType.PAY_TYPE.getName()))
						.append(FieldType.DATE_INDEX.getName(), new SimpleDateFormat("yyyyMMdd").format(new Date()))
						.append(FieldType.STATUS.getName(), StatusTypePayout.REQUEST_ACCEPTED.getName())),
				new Document("$group",
						new Document("_id", new BsonNull()).append("sum",
								new Document("$sum", new Document("$toDouble", "$AMOUNT")))),
				new Document("$project", new Document("_id", 0L).append("sum", 1L)));

		MongoCursor<Document> cursor = coll.aggregate(queryExecute).iterator();

		logger.info("getDailyVolume Query :" + queryExecute);

		if (cursor.hasNext()) {
			Document document = cursor.next();
			double daillyLimit = Double.valueOf(""+document.get("sum"));
			logger.info("getDailyVolume  :" + daillyLimit);
			return daillyLimit;
		} else {
			return 0;
		}

	}

	public double getWeeklyLimit(Fields fields) {

		LocalDate today = LocalDate.now();
		LocalDate startOfWeek = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
		LocalDate endOfWeek = today.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));

		String startDate = startOfWeek.toString().replaceAll("-", "");
		String endDate = endOfWeek.toString().replaceAll("-", "");

		MongoDatabase dbIns = mongoInstance.getDB();
		MongoCollection<Document> coll = dbIns.getCollection(
				PropertiesManager.propertiesMap.get(prefix + Constants.TRANSACTION_LEDGER_PO.getValue()));

		List<Document> queryExecute = Arrays.asList(
				new Document("$match", new Document(FieldType.PAY_ID.getName(), fields.get(FieldType.PAY_ID.getName()))
						.append(FieldType.CURRENCY_CODE.getName(), fields.get(FieldType.CURRENCY_CODE.getName()))
						.append(FieldType.PAY_TYPE.getName(), fields.get(FieldType.PAY_TYPE.getName()))
						.append(FieldType.DATE_INDEX.getName(), new Document("$gte", startDate).append("$lte", endDate))
						.append(FieldType.STATUS.getName(), StatusTypePayout.REQUEST_ACCEPTED.getName())),
				new Document("$group", new Document("_id", new BsonNull()).append("count", new Document("$sum", 1L))),
				new Document("$project", new Document("_id", 0L).append("count", 1L)));

		MongoCursor<Document> cursor = coll.aggregate(queryExecute).iterator();

		logger.info("getWeeklyLimit Query :" + queryExecute);

		if (cursor.hasNext()) {
			Document document = cursor.next();
			double daillyLimit = Double.valueOf(""+document.get("count"));
			logger.info("getWeeklyLimit  :" + daillyLimit);
			return daillyLimit;
		} else {
			return 0;
		}

	}

	public double getWeeklyVolume(Fields fields) {

		LocalDate today = LocalDate.now();
		LocalDate startOfWeek = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
		LocalDate endOfWeek = today.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));

		String startDate = startOfWeek.toString().replaceAll("-", "");
		String endDate = endOfWeek.toString().replaceAll("-", "");

		MongoDatabase dbIns = mongoInstance.getDB();
		MongoCollection<Document> coll = dbIns.getCollection(
				PropertiesManager.propertiesMap.get(prefix + Constants.TRANSACTION_LEDGER_PO.getValue()));

		List<Document> queryExecute = Arrays.asList(
				new Document("$match", new Document(FieldType.PAY_ID.getName(), fields.get(FieldType.PAY_ID.getName()))
						.append(FieldType.CURRENCY_CODE.getName(), fields.get(FieldType.CURRENCY_CODE.getName()))
						.append(FieldType.PAY_TYPE.getName(), fields.get(FieldType.PAY_TYPE.getName()))
						.append(FieldType.DATE_INDEX.getName(), new Document("$gte", startDate).append("$lte", endDate))
						.append(FieldType.STATUS.getName(), StatusTypePayout.REQUEST_ACCEPTED.getName())),
				new Document("$group",
						new Document("_id", new BsonNull()).append("sum",
								new Document("$sum", new Document("$toDouble", "$AMOUNT")))),
				new Document("$project", new Document("_id", 0L).append("sum", 1L)));

		MongoCursor<Document> cursor = coll.aggregate(queryExecute).iterator();

		logger.info("getWeeklyLimit Query :" + queryExecute);

		if (cursor.hasNext()) {
			Document document = cursor.next();
			double daillyLimit = Double.valueOf(""+document.get("sum"));
			logger.info("getWeeklyLimit  :" + daillyLimit);
			return daillyLimit;
		} else {
			return 0;
		}

	}

	public double getMonthlyLimit(Fields fields) {
		// Get the current date
		LocalDate currentDate = LocalDate.now();

		// Set the day of the month to 1 to get the start of the current month
		LocalDate firstDayOfMonth = currentDate.withDayOfMonth(1);

		// Format the date as "yyyyMMdd"
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
		String formattedDate = firstDayOfMonth.format(formatter);

		MongoDatabase dbIns = mongoInstance.getDB();
		MongoCollection<Document> coll = dbIns.getCollection(
				PropertiesManager.propertiesMap.get(prefix + Constants.TRANSACTION_LEDGER_PO.getValue()));

		List<Document> queryExecute = Arrays.asList(
				new Document("$match", new Document(FieldType.PAY_ID.getName(), fields.get(FieldType.PAY_ID.getName()))
						.append(FieldType.CURRENCY_CODE.getName(), fields.get(FieldType.CURRENCY_CODE.getName()))
						.append(FieldType.PAY_TYPE.getName(), fields.get(FieldType.PAY_TYPE.getName()))
						.append(FieldType.DATE_INDEX.getName(),
								new Document("$gte", formattedDate).append("$lte",
										new SimpleDateFormat("yyyyMMdd").format(new Date())))
						.append(FieldType.STATUS.getName(), StatusTypePayout.REQUEST_ACCEPTED.getName())),
				new Document("$group", new Document("_id", new BsonNull()).append("count", new Document("$sum", 1L))),
				new Document("$project", new Document("_id", 0L).append("count", 1L)));

		MongoCursor<Document> cursor = coll.aggregate(queryExecute).iterator();

		logger.info("getMonthlyLimit Query :" + queryExecute);

		if (cursor.hasNext()) {
			Document document = cursor.next();
			double daillyLimit = Double.valueOf(""+document.get("count"));
			logger.info("getMonthlyLimit  :" + daillyLimit);
			return daillyLimit;
		} else {
			return 0;
		}

	}

	public double getMonthlyVolume(Fields fields) {
		// Get the current date
		LocalDate currentDate = LocalDate.now();

		// Set the day of the month to 1 to get the start of the current month
		LocalDate firstDayOfMonth = currentDate.withDayOfMonth(1);

		// Format the date as "yyyyMMdd"
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
		String formattedDate = firstDayOfMonth.format(formatter);

		MongoDatabase dbIns = mongoInstance.getDB();
		MongoCollection<Document> coll = dbIns.getCollection(
				PropertiesManager.propertiesMap.get(prefix + Constants.TRANSACTION_LEDGER_PO.getValue()));

		List<Document> queryExecute = Arrays.asList(
				new Document("$match", new Document(FieldType.PAY_ID.getName(), fields.get(FieldType.PAY_ID.getName()))
						.append(FieldType.CURRENCY_CODE.getName(), fields.get(FieldType.CURRENCY_CODE.getName()))
						.append(FieldType.PAY_TYPE.getName(), fields.get(FieldType.PAY_TYPE.getName()))
						.append(FieldType.DATE_INDEX.getName(),
								new Document("$gte", formattedDate).append("$lte",
										new SimpleDateFormat("yyyyMMdd").format(new Date())))
						.append(FieldType.STATUS.getName(), StatusTypePayout.REQUEST_ACCEPTED.getName())),
				new Document("$group",
						new Document("_id", new BsonNull()).append("sum",
								new Document("$sum", new Document("$toDouble", "$AMOUNT")))),
				new Document("$project", new Document("_id", 0L).append("sum", 1L)));
		MongoCursor<Document> cursor = coll.aggregate(queryExecute).iterator();

		logger.info("getMonthlyLimit Query :" + queryExecute);

		if (cursor.hasNext()) {
			Document document = cursor.next();
			double daillyLimit = Double.valueOf(""+document.get("sum"));
			logger.info("getMonthlyVolume  :" + daillyLimit);
			return daillyLimit;
		} else {
			return 0;
		}

	}
}
