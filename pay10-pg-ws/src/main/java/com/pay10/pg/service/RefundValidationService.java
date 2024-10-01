package com.pay10.pg.service;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.bson.Document;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mongodb.BasicDBObject;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.pay10.commons.dao.FieldsDao;
import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.exception.SystemException;
import com.pay10.commons.mongo.MongoInstance;
import com.pay10.commons.user.User;
import com.pay10.commons.user.UserDao;
import com.pay10.commons.util.Amount;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.PropertiesManager;
import com.pay10.commons.util.StatusType;
import com.pay10.commons.util.SystemProperties;
import com.pay10.commons.util.TransactionType;

/**
 * @author Shaiwal
 *
 */

@Service
public class RefundValidationService {

	private static Logger logger = LoggerFactory.getLogger(RefundValidationService.class.getName());
	private static final String prefix = "MONGO_DB_";
	private static final Collection<String> aLLDB_Fields = SystemProperties.getDBFields();

	@Autowired
	private MongoInstance mongoInstance;

	@Autowired
	private UserDao userDao;

	@Autowired
	private PropertiesManager propertiesManager;

	@Autowired
	private FieldsDao fieldsDao;

	public void checkRefund(Fields fields) {

		logger.info("Inside RefundValidationService , checkRefund");

		User user = userDao.findPayId(fields.get(FieldType.PAY_ID.getName()));

		if (StringUtils.isNotBlank(user.getResellerId())) {
			fields.put(FieldType.RESELLER_ID.getName(), user.getResellerId());
		}
		Fields previousFields = new Fields();

		BigDecimal totalRefundAmount = BigDecimal.ZERO;
		BigDecimal totalSaleAmount = BigDecimal.ZERO;
		String txnAmountInDecimal = Amount.toDecimal(fields.get(FieldType.AMOUNT.getName()),
				fields.get(FieldType.CURRENCY_CODE.getName()));
		BigDecimal currentRefundAmount = new BigDecimal(txnAmountInDecimal);

		try {

			MongoDatabase dbIns = mongoInstance.getDB();
			MongoCollection<Document> coll = dbIns
					.getCollection(PropertiesManager.propertiesMap.get(prefix + Constants.COLLECTION_NAME.getValue()));

			List<BasicDBObject> saleTxnQuery1 = new ArrayList<BasicDBObject>();
			saleTxnQuery1
					.add(new BasicDBObject(FieldType.PG_REF_NUM.getName(), fields.get(FieldType.PG_REF_NUM.getName())));

			if (!user.isSkipOrderIdForRefund()) {
				saleTxnQuery1
						.add(new BasicDBObject(FieldType.ORDER_ID.getName(), fields.get(FieldType.ORDER_ID.getName())));
			}
			saleTxnQuery1.add(new BasicDBObject(FieldType.TXNTYPE.getName(), TransactionType.SALE.getName()));

			saleTxnQuery1.add(new BasicDBObject(FieldType.STATUS.getName(), StatusType.CAPTURED.getName()));

			BasicDBObject saleConditionQuery = new BasicDBObject("$and", saleTxnQuery1);
			MongoCursor<Document> cursorSale = coll.find(saleConditionQuery).iterator();

			Document dbobjSale = cursorSale.next();
			String oid = dbobjSale.getString(FieldType.OID.toString());
			cursorSale.close();

			if (!StringUtils.isBlank(oid)) {

				List<BasicDBObject> saleTxnQuery2 = new ArrayList<BasicDBObject>();
				saleTxnQuery2.add(new BasicDBObject(FieldType.OID.getName(), oid));

				BasicDBObject allRecordsQuery = new BasicDBObject("$and", saleTxnQuery2);
				MongoCursor<Document> cursor = coll.find(allRecordsQuery).iterator();

				while (cursor.hasNext()) {

					Document dbObj = cursor.next();

					if (dbObj.getString(FieldType.TXNTYPE.getName()).equalsIgnoreCase(TransactionType.SALE.getName())
							&& dbObj.getString(FieldType.STATUS.getName())
									.equalsIgnoreCase(StatusType.CAPTURED.getName())) {

						previousFields = createAllForRefund(dbObj);
						fields.setPrevious(previousFields);
						totalSaleAmount = totalSaleAmount
								.add(new BigDecimal(dbObj.getString(FieldType.AMOUNT.getName())));

					}

					else if (dbObj.getString(FieldType.TXNTYPE.getName())
							.equalsIgnoreCase(TransactionType.REFUND.getName())
							&& dbObj.getString(FieldType.STATUS.getName())
									.equalsIgnoreCase(StatusType.CAPTURED.getName())) {

						totalRefundAmount = totalRefundAmount
								.add(new BigDecimal(dbObj.getString(FieldType.AMOUNT.getName())));

					}

				}

				totalRefundAmount = totalRefundAmount.add(currentRefundAmount);
				cursorSale.close();

				if (totalSaleAmount.compareTo(totalRefundAmount) >= 0) {

					String dailyRefundResponse = checkDailyrefundLimit(fields);

					if (dailyRefundResponse.equalsIgnoreCase(ErrorType.SUCCESS.getCode())) {
						fields.put(FieldType.RESPONSE_CODE.getName(), ErrorType.SUCCESS.getCode());
					} else {
						logger.info("Refund Denied totalRefundAmount = " + totalRefundAmount + "  totalSaleAmount =  "
								+ totalSaleAmount);
						fields.put(FieldType.RESPONSE_CODE.getName(), ErrorType.REFUND_DENIED.getCode());
					}

				} else {
					logger.info("Refund Rejected totalRefundAmount = " + totalRefundAmount + "  totalSaleAmount =  "
							+ totalSaleAmount);
					fields.put(FieldType.RESPONSE_CODE.getName(), ErrorType.REFUND_REJECTED.getCode());
				}
			}

			else {
				logger.info("Refund declined , TRANSACTION_NOT_FOUND ");
				fields.put(FieldType.RESPONSE_CODE.getName(), ErrorType.TRANSACTION_NOT_FOUND.getCode());
			}
		}

		catch (Exception e) {

			logger.error("Refund declined , TRANSACTION_NOT_FOUND ");
			fields.put(FieldType.RESPONSE_CODE.getName(), ErrorType.TRANSACTION_NOT_FOUND.getCode());
		}

	}

	private String checkDailyrefundLimit(Fields fields) {

		return ErrorType.SUCCESS.getCode();

	}

	private Fields createAllForRefund(Document documentObj) {

		Fields fields = new Fields();

		try {
			if (null != documentObj) {
				for (int j = 0; j < documentObj.size(); j++) {
					for (String columnName : aLLDB_Fields) {
						if (documentObj.get(columnName) != null) {
							fields.put(columnName, documentObj.get(columnName).toString());
						} else {

						}
					}
				}
			}

			return fields;
		}

		catch (Exception e) {
			logger.error("Exception in getting Sale transaction for previous fields " + e);
		}
		return fields;
	}

	void validateunselltedamound(Fields fields) throws SystemException {
		Double saleamount = 0d;
		Double refundamount = 0d;
		Calendar cal = Calendar.getInstance();
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		DateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd 00:00:00");
		logger.info("Today's date is ={}", dateFormat.format(cal.getTime()));
		String enddate = dateFormat.format(cal.getTime());
		cal.add(Calendar.DATE, -5);
		logger.info("5 day before refund date was ={}", dateFormat1.format(cal.getTime()));
		String startdate = dateFormat1.format(cal.getTime());

		Document match = new Document();
		match.put(FieldType.TXNTYPE.getName(), new Document("$in", Arrays.asList("SALE", "REFUND")));
		match.put(FieldType.STATUS.getName(), StatusType.CAPTURED.getName());
		match.put(FieldType.PAY_ID.getName(), fields.get(FieldType.PAY_ID.getName()));
		match.put(FieldType.CREATE_DATE.getName(),
				new Document("$gte", new SimpleDateFormat(startdate).toLocalizedPattern()).append("$lte",
						new SimpleDateFormat(enddate).toLocalizedPattern()));

		List<Document> allConditionQueryList = Arrays.asList(new Document("$match", match),
				new Document("$group", new Document("_id", "$TXNTYPE").append("AMOUNT",
						new Document("$sum", new Document("$toDecimal", "$AMOUNT")))));

		MongoDatabase dbIns = mongoInstance.getDB();
		MongoCollection<Document> coll = dbIns.getCollection(
				PropertiesManager.propertiesMap.get(prefix + Constants.TRANSACTION_STATUS_COLLECTION.getValue()));

		MongoCursor<Document> cursor = coll.aggregate(allConditionQueryList).iterator();

		while (cursor.hasNext()) {
			Document document1 = (Document) cursor.next();
			JSONObject document = new JSONObject(document1.toJson());
			System.out.println(document);
			String id = document.getString("_id");
			String amount = document.getJSONObject("AMOUNT").getString("$numberDecimal");

			System.out.println("id : " + id + "\tamount : " + amount);
			if (id.equalsIgnoreCase(TransactionType.SALE.getName())) {

				saleamount = Double.parseDouble(amount);

				logger.info("sale amount for 5 days" + saleamount);

			}
			if (id.equalsIgnoreCase(TransactionType.REFUND.getName())) {

				refundamount = Double.parseDouble(amount);

				logger.info("refund amount for 5 days" + refundamount);
			}
		}
		double totalunsettedamount = saleamount - refundamount;
		logger.info("total amount unsettled={}", totalunsettedamount);
		double baserefundamount = Double.parseDouble(Amount.toDecimal(fields.get(FieldType.AMOUNT.getName()), "356"));
		logger.info("basemount for refund={}", baserefundamount);
		if (totalunsettedamount < baserefundamount) {
			
			fields.put(FieldType.STATUS.getName(), StatusType.REJECTED.getName());
			fields.put(FieldType.RESPONSE_MESSAGE.getName(),  ErrorType.REJECTED.getResponseMessage());
			fields.put(FieldType.RESPONSE_CODE.getName(),ErrorType.REJECTED.getResponseCode());
			throw new SystemException(ErrorType.REFUND_REJECTED, "");
		}

	}

}
