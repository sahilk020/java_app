package com.pay10.crm.mongoReports;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.pay10.commons.dao.BSESMonthlyInvoiceReportDAO;
import com.pay10.commons.entity.BSESMonthlyInvoiceReportEntity;
import com.pay10.commons.mongo.MongoInstance;
import com.pay10.commons.user.BSESMonthlyInvoiceReportDetails;
import com.pay10.commons.user.User;
import com.pay10.commons.user.UserDao;

import com.pay10.commons.util.Constants;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.MopType;
import com.pay10.commons.util.PaymentType;
import com.pay10.commons.util.PropertiesManager;
import com.pay10.commons.util.StatusType;

@Component
public class BSESInvoiceReport {
	final String bsesPayid = PropertiesManager.propertiesMap.get(Constants.BSESPAYID.getValue());
	private static Logger logger = LoggerFactory.getLogger(BSESInvoiceReport.class.getName());
	private static final String prefix = "MONGO_DB_";

	@Autowired
	PropertiesManager propertiesManager;

	@Autowired
	private MongoInstance mongoInstance;

	@Autowired
	private UserDao userdao;

	@Autowired
	BSESMonthlyInvoiceReportDAO bsesMonthlyInvoiceReportDAO;

	public static int generateDates(String stringdate) throws ParseException {
		Date date = new SimpleDateFormat("yyyyMM").parse(stringdate);
		Calendar calendar1 = Calendar.getInstance();
		calendar1.setTime(date);
		return calendar1.getActualMaximum(Calendar.DAY_OF_MONTH);
	}

	public List<BSESMonthlyInvoiceReportDetails> BSESMonthlyInvoiceReportList(String payId, String year, String month) {
		List<BSESMonthlyInvoiceReportDetails> monthlyInvoiceReportList = new ArrayList<BSESMonthlyInvoiceReportDetails>();
		try {

			List<String> payidList = null;
			if (!payId.equalsIgnoreCase("All")) {
				boolean a = bsesMonthlyInvoiceReportDAO.getBSESMonthlyInvoiceReportEntity(payId);
				if (a == true) {
					payidList = Arrays.asList(payId.split(","));
					monthlyInvoiceReportList = getData(payidList, year, month);
				} else {
					return monthlyInvoiceReportList;
				}

			} else {
				List<String> payIdlist = bsesMonthlyInvoiceReportDAO.getAllDistinctPayIds();
				if (payIdlist.size() > 0) {
					payidList = payIdlist;
					monthlyInvoiceReportList = getData(payidList, year, month);
				} else {
					return monthlyInvoiceReportList;
				}
			}

		} catch (Exception e) {
			logger.info("Exception occure in BSESMonthlyInvoiceReportList() :", e);
		}
		return monthlyInvoiceReportList;

	}

	public List<BSESMonthlyInvoiceReportDetails> getData(List<String> payid, String year, String month)
			throws ParseException {
		List<BSESMonthlyInvoiceReportDetails> bsesMonthlyInvoiceReportDetails = new ArrayList<>();
		MongoDatabase dbIns = mongoInstance.getDB();

		Map<String, User> userMap = new HashMap<String, User>();
		MongoCollection<Document> coll = dbIns.getCollection(
				PropertiesManager.propertiesMap.get(prefix + Constants.TRANSACTION_STATUS_COLLECTION.getValue()));
		BasicDBObject match = new BasicDBObject();
		match.put(FieldType.PAY_ID.getName(), new BasicDBObject("$in", payid));

		if (!StringUtils.isBlank(year) && !StringUtils.isBlank(month)) {

			String stDate = "";

			String concatenated = year + month;
			String edDate = concatenated + String.valueOf(generateDates(concatenated));

			stDate = year + month + "01";
			match.put(FieldType.DATE_INDEX.getName(), new BasicDBObject("$gte", stDate).append("$lte", edDate));

		}

		match.put(FieldType.STATUS.getName(), new BasicDBObject("$in", Arrays.asList(StatusType.CAPTURED.getName(),
				StatusType.SETTLED_RECONCILLED.getName(), StatusType.SETTLED_SETTLE.getName())));

		match.put(FieldType.PAYMENT_TYPE.getName(), new BasicDBObject("$in",
				Arrays.asList(PaymentType.CREDIT_CARD.getCode(), PaymentType.NET_BANKING.getCode())));

		List<BasicDBObject> queryExecute = Arrays.asList(new BasicDBObject("$match", match));
		logger.info("Query to fetch data : " + queryExecute);
		MongoCursor<Document> cursor = coll.aggregate(queryExecute).iterator();
		BSESMonthlyInvoiceReportDetails bsesMonthlyInvoiceReportDetails2 = null;
		User user1 = null;
		while (cursor.hasNext()) {
			Document document = (Document) cursor.next();

			String payId = document.getString(FieldType.PAY_ID.getName());
			String payementType = document.getString(FieldType.PAYMENT_TYPE.getName());
			String amount = document.getString(FieldType.AMOUNT.getName());

			BSESMonthlyInvoiceReportEntity bsesMonthlyInvoiceReportEntity = bsesMonthlyInvoiceReportDAO
					.findByPayIdAndPaymentTypeAndAmount(payId, amount, payementType);

			if (bsesMonthlyInvoiceReportEntity != null) {

				if (bsesMonthlyInvoiceReportEntity.getIsmonthlyInvoice().equalsIgnoreCase("Y")) {

					user1 = new User();
					if (userMap.get(payId) != null) {
						user1 = userMap.get(payId);
					} else {
						user1 = userdao.findPayId(payId);
						userMap.put(payId, user1);
					}

					bsesMonthlyInvoiceReportDetails2 = new BSESMonthlyInvoiceReportDetails();
					bsesMonthlyInvoiceReportDetails2.setMerchantName(user1.getBusinessName());
					bsesMonthlyInvoiceReportDetails2.setAmount(amount);
					bsesMonthlyInvoiceReportDetails2.setPaymentType(PaymentType.getpaymentName(payementType));
					bsesMonthlyInvoiceReportDetails2.setPayId(payId);
					bsesMonthlyInvoiceReportDetails2.setTxnType(document.getString(FieldType.TXNTYPE.getName()));
					bsesMonthlyInvoiceReportDetails2.setMopType(MopType.getmopName(document.getString(FieldType.MOP_TYPE.getName())));
					bsesMonthlyInvoiceReportDetails2.setOrderId(document.getString(FieldType.ORDER_ID.getName()));
					bsesMonthlyInvoiceReportDetails2.setPgRefNo(document.getString(FieldType.PG_REF_NUM.getName()));
					bsesMonthlyInvoiceReportDetails2.setTransactionDate(document.getString(FieldType.CREATE_DATE.getName()));
					bsesMonthlyInvoiceReportDetails2.setUdf9(document.getString(FieldType.UDF9.getName()) != null
							? document.getString(FieldType.UDF9.getName())
							: "N/A");
					bsesMonthlyInvoiceReportDetails2.setUdf10(document.getString(FieldType.UDF10.getName()) != null
							? document.getString(FieldType.UDF10.getName())
							: "N/A");
					bsesMonthlyInvoiceReportDetails2.setUdf11(document.getString(FieldType.UDF11.getName()) != null
							? document.getString(FieldType.UDF11.getName())
							: "N/A");

					double gst = bsesMonthlyInvoiceReportEntity.getGst();
					String preference = bsesMonthlyInvoiceReportEntity.getMonthlyInvoicePreference();
					double rate = bsesMonthlyInvoiceReportEntity.getMonthlyInvoiceRate();
					double amt = Double.parseDouble(amount);

					double a = 0;
					double b = 0;

					if (preference.equalsIgnoreCase("P")) {
						a = ((rate / 100) * amt);
						b = ((gst / 100) * a);
					} else {

						a = rate;
						b = (gst / 100) * a;

					}
					double total = (a + b);
					bsesMonthlyInvoiceReportDetails2.setInvoiceGst(String.format("%.2f", b));
					bsesMonthlyInvoiceReportDetails2.setInvoiceValue(String.format("%.2f", a));
					bsesMonthlyInvoiceReportDetails2.setTotalInvoiceValue(String.format("%.2f", total));
					bsesMonthlyInvoiceReportDetails.add(bsesMonthlyInvoiceReportDetails2);

				}

			}

		}
		return bsesMonthlyInvoiceReportDetails;
	}
}