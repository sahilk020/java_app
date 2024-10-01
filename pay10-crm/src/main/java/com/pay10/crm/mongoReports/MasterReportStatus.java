package com.pay10.crm.mongoReports;

import java.io.FileWriter;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bson.BsonNull;
import org.bson.Document;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.pay10.commons.mongo.MongoInstance;
import com.pay10.commons.user.MasterReportDto;
import com.pay10.commons.user.UserDao;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.PropertiesManager;
import com.pay10.commons.util.StatusType;

@Component
public class MasterReportStatus {
	private static Logger logger = LoggerFactory.getLogger(MasterReportStatus.class.getName());

	@Autowired
	private MongoInstance mongoInstance;

	@Autowired
	private UserDao userdao;

	private static final String prefix = "MONGO_DB_";

	public String[] getDateBetween(String date_range) {
		// 07/06/2022 - 06/07/2022
		String[] date = new String[2];
		String splitdate[] = date_range.split(" ");
		try {
			SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
			SimpleDateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd");
			date[0] = dateFormat1.format(dateFormat.parse(splitdate[0]));
			date[1] = dateFormat1.format(dateFormat.parse(splitdate[2]));

		} catch (Exception e) {
			logger.info("Exception Occur in class TransactionStatus in getDateBetween(): " + e);
			e.printStackTrace();
		}
		return date;
	}

	public int getMasterReportCount(String type, String date, String yearmonth, String fmmonth, String tmmonth) {
		List<BasicDBObject> query = getQueryForNoSurchargeCount(type, date, yearmonth, fmmonth, tmmonth, 0, 0);
		logger.info("getMasterReport Query : " + query);
		MongoDatabase dbIns = mongoInstance.getDB();
		MongoCollection<Document> coll = dbIns.getCollection(
				PropertiesManager.propertiesMap.get(prefix + Constants.TRANSACTION_STATUS_COLLECTION.getValue()));
		return coll.aggregate(query).into(new ArrayList<>()).size();
	
	}

	public static int getNumberOfDaysInMonth(int year, int month) {
		LocalDate date = LocalDate.of(year, month, 1);
		return date.lengthOfMonth();
	}

	public List<BasicDBObject> getQueryForNoSurcharge(String type, String date, String yearmonth, String fmmonth,
			String tmmonth, int start, int length) {
		BasicDBObject skip = new BasicDBObject("$skip", start);
		BasicDBObject limit = new BasicDBObject("$limit", length);
		List<BasicDBObject> basicDBObjects = null;
		if (type.equalsIgnoreCase("tpd")) {
			basicDBObjects = Arrays.asList(
					new BasicDBObject("$match",
							new BasicDBObject("DATE_INDEX", date.replaceAll("-", ""))
									.append("STATUS", StatusType.SETTLED_SETTLE.getName())
									.append("ACQUIRER_TYPE", new BasicDBObject("$ne", new BsonNull()))
									.append("SURCHARGE_FLAG", new BasicDBObject("$ne", new BsonNull()))),
					new BasicDBObject("$project",
							new BasicDBObject("_id", 0L).append("CREATE_DATE", 1L).append("ACQUIRER_TYPE", 1L)
									.append("TXNTYPE", 1L).append("STATUS", 1L).append("AMOUNT", 1L)
									.append("TOTAL_AMOUNT", 1L).append("ACQUIRER_TDR_SC", 1L).append("ACQUIRER_GST", 1L)
									.append("PG_TDR_SC", 1L).append("PG_GST", 1L)
									.append("DATE_INDEX",
											new BasicDBObject("$substr", Arrays.asList("$DATE_INDEX", 6L, 2L)))),
					new BasicDBObject("$group",
							new BasicDBObject("_id",
									new BasicDBObject("ACQUIRER_TYPE", "$ACQUIRER_TYPE").append("DATE", "$DATE_INDEX"))
											.append("count", new BasicDBObject("$sum", 1L))
											.append("AMOUNT_SUM",
													new BasicDBObject("$sum",
															new BasicDBObject("$toDouble", "$AMOUNT")))
											.append("TOTALAMOUNT_SUM",
													new BasicDBObject("$sum",
															new BasicDBObject("$toDouble", "$TOTAL_AMOUNT")))
											.append("PG_TDR_SC_SUM",
													new BasicDBObject("$sum",
															new BasicDBObject("$toDouble", "$PG_TDR_SC")))
											.append("PG_GST_SUM",
													new BasicDBObject("$sum",
															new BasicDBObject("$toDouble", "$PG_GST")))
											.append("ACQUIRER_TDRSC_SUM",
													new BasicDBObject("$sum",
															new BasicDBObject("$toDouble", "$ACQUIRER_TDR_SC")))
											.append("ACQUIRER_GST_SUM",
													new BasicDBObject("$sum",
															new BasicDBObject("$toDouble", "$ACQUIRER_GST")))
											.append("DATA", new BasicDBObject("$push", "$$ROOT"))),skip,limit);
		} else if (type.equalsIgnoreCase("tm")) {
			String days = String.valueOf(getNumberOfDaysInMonth(Integer.parseInt(yearmonth.split("-")[0]),
					Integer.parseInt(yearmonth.split("-")[1])));
			basicDBObjects = Arrays.asList(
					new BasicDBObject("$match",
							new BasicDBObject("DATE_INDEX",
									new Document("$gte", yearmonth.replaceAll("-", "") + "01").append("$lte",
											yearmonth.replaceAll("-", "") + days))
													.append("STATUS", StatusType.SETTLED_SETTLE.getName())
													.append("ACQUIRER_TYPE", new BasicDBObject("$ne", new BsonNull()))
													.append("SURCHARGE_FLAG",
															new BasicDBObject("$eq", new BsonNull()))),
					new BasicDBObject("$project",
							new BasicDBObject("_id", 0L).append("CREATE_DATE", 1L).append("ACQUIRER_TYPE", 1L)
									.append("TXNTYPE", 1L).append("STATUS", 1L).append("AMOUNT", 1L)
									.append("TOTAL_AMOUNT", 1L).append("ACQUIRER_TDR_SC", 1L).append("ACQUIRER_GST", 1L)
									.append("PG_TDR_SC", 1L).append("PG_GST", 1L)
									.append("DATE_INDEX",
											new BasicDBObject("$substr", Arrays.asList("$DATE_INDEX", 6L, 2L)))),
					new BasicDBObject("$group",
							new BasicDBObject("_id",
									new BasicDBObject("ACQUIRER_TYPE", "$ACQUIRER_TYPE").append("DATE", "$DATE_INDEX"))
											.append("count", new BasicDBObject("$sum", 1L))
											.append("AMOUNT_SUM",
													new BasicDBObject("$sum",
															new BasicDBObject("$toDouble", "$AMOUNT")))
											.append("TOTALAMOUNT_SUM",
													new BasicDBObject("$sum",
															new BasicDBObject("$toDouble", "$TOTAL_AMOUNT")))
											.append("PG_TDR_SC_SUM",
													new BasicDBObject("$sum",
															new BasicDBObject("$toDouble", "$PG_TDR_SC")))
											.append("PG_GST_SUM",
													new BasicDBObject("$sum",
															new BasicDBObject("$toDouble", "$PG_GST")))
											.append("ACQUIRER_TDRSC_SUM",
													new BasicDBObject("$sum",
															new BasicDBObject("$toDouble", "$ACQUIRER_TDR_SC")))
											.append("ACQUIRER_GST_SUM",
													new BasicDBObject("$sum",
															new BasicDBObject("$toDouble", "$ACQUIRER_GST")))
											.append("DATA", new BasicDBObject("$push", "$$ROOT"))),skip,limit);
		} else {
			String days = String.valueOf(getNumberOfDaysInMonth(Integer.parseInt(tmmonth.split("-")[0]),
					Integer.parseInt(tmmonth.split("-")[1])));
			basicDBObjects = Arrays.asList(
					new BasicDBObject("$match",
							new BasicDBObject("DATE_INDEX",
									new Document("$gte", fmmonth.replaceAll("-", "") + "01").append("$lte",
											tmmonth.replaceAll("-", "") + days))
													.append("STATUS", StatusType.SETTLED_SETTLE.getName())
													.append("ACQUIRER_TYPE", new BasicDBObject("$ne", new BsonNull()))
													.append("SURCHARGE_FLAG",
															new BasicDBObject("$eq", new BsonNull()))),
					new BasicDBObject("$project",
							new BasicDBObject("_id", 0L).append("CREATE_DATE", 1L).append("ACQUIRER_TYPE", 1L)
									.append("TXNTYPE", 1L).append("STATUS", 1L).append("AMOUNT", 1L)
									.append("TOTAL_AMOUNT", 1L).append("ACQUIRER_TDR_SC", 1L).append("ACQUIRER_GST", 1L)
									.append("PG_TDR_SC", 1L).append("PG_GST", 1L)
									.append("DATE_INDEX",
											new BasicDBObject("$substr", Arrays.asList("$DATE_INDEX", 0L, 6L)))),
					new BasicDBObject("$group",
							new BasicDBObject("_id",
									new BasicDBObject("ACQUIRER_TYPE", "$ACQUIRER_TYPE").append("DATE", "$DATE_INDEX"))
											.append("count", new BasicDBObject("$sum", 1L))
											.append("AMOUNT_SUM",
													new BasicDBObject("$sum",
															new BasicDBObject("$toDouble", "$AMOUNT")))
											.append("TOTALAMOUNT_SUM",
													new BasicDBObject("$sum",
															new BasicDBObject("$toDouble", "$TOTAL_AMOUNT")))
											.append("PG_TDR_SC_SUM",
													new BasicDBObject("$sum",
															new BasicDBObject("$toDouble", "$PG_TDR_SC")))
											.append("PG_GST_SUM",
													new BasicDBObject("$sum",
															new BasicDBObject("$toDouble", "$PG_GST")))
											.append("ACQUIRER_TDRSC_SUM",
													new BasicDBObject("$sum",
															new BasicDBObject("$toDouble", "$ACQUIRER_TDR_SC")))
											.append("ACQUIRER_GST_SUM",
													new BasicDBObject("$sum",
															new BasicDBObject("$toDouble", "$ACQUIRER_GST")))
											.append("DATA", new BasicDBObject("$push", "$$ROOT"))),skip,limit);

		}

		return basicDBObjects;

	}
	public List<BasicDBObject> getQueryForNoSurchargeCount(String type, String date, String yearmonth, String fmmonth,
			String tmmonth, int start, int length) {
		
		List<BasicDBObject> basicDBObjects = null;
		if (type.equalsIgnoreCase("tpd")) {
			basicDBObjects = Arrays.asList(
					new BasicDBObject("$match",
							new BasicDBObject("DATE_INDEX", date.replaceAll("-", ""))
									.append("STATUS", StatusType.SETTLED_SETTLE.getName())
									.append("ACQUIRER_TYPE", new BasicDBObject("$ne", new BsonNull()))
									.append("SURCHARGE_FLAG", new BasicDBObject("$ne", new BsonNull()))),
					new BasicDBObject("$project",
							new BasicDBObject("_id", 0L).append("CREATE_DATE", 1L).append("ACQUIRER_TYPE", 1L)
									.append("TXNTYPE", 1L).append("STATUS", 1L).append("AMOUNT", 1L)
									.append("TOTAL_AMOUNT", 1L).append("ACQUIRER_TDR_SC", 1L).append("ACQUIRER_GST", 1L)
									.append("PG_TDR_SC", 1L).append("PG_GST", 1L)
									.append("DATE_INDEX",
											new BasicDBObject("$substr", Arrays.asList("$DATE_INDEX", 6L, 2L)))),
					new BasicDBObject("$group",
							new BasicDBObject("_id",
									new BasicDBObject("ACQUIRER_TYPE", "$ACQUIRER_TYPE").append("DATE", "$DATE_INDEX"))
											.append("count", new BasicDBObject("$sum", 1L))
											.append("AMOUNT_SUM",
													new BasicDBObject("$sum",
															new BasicDBObject("$toDouble", "$AMOUNT")))
											.append("TOTALAMOUNT_SUM",
													new BasicDBObject("$sum",
															new BasicDBObject("$toDouble", "$TOTAL_AMOUNT")))
											.append("PG_TDR_SC_SUM",
													new BasicDBObject("$sum",
															new BasicDBObject("$toDouble", "$PG_TDR_SC")))
											.append("PG_GST_SUM",
													new BasicDBObject("$sum",
															new BasicDBObject("$toDouble", "$PG_GST")))
											.append("ACQUIRER_TDRSC_SUM",
													new BasicDBObject("$sum",
															new BasicDBObject("$toDouble", "$ACQUIRER_TDR_SC")))
											.append("ACQUIRER_GST_SUM",
													new BasicDBObject("$sum",
															new BasicDBObject("$toDouble", "$ACQUIRER_GST")))
											.append("DATA", new BasicDBObject("$push", "$$ROOT"))));
		} else if (type.equalsIgnoreCase("tm")) {
			String days = String.valueOf(getNumberOfDaysInMonth(Integer.parseInt(yearmonth.split("-")[0]),
					Integer.parseInt(yearmonth.split("-")[1])));
			basicDBObjects = Arrays.asList(
					new BasicDBObject("$match",
							new BasicDBObject("DATE_INDEX",
									new Document("$gte", yearmonth.replaceAll("-", "") + "01").append("$lte",
											yearmonth.replaceAll("-", "") + days))
													.append("STATUS", StatusType.SETTLED_SETTLE.getName())
													.append("ACQUIRER_TYPE", new BasicDBObject("$ne", new BsonNull()))
													.append("SURCHARGE_FLAG",
															new BasicDBObject("$eq", new BsonNull()))),
					new BasicDBObject("$project",
							new BasicDBObject("_id", 0L).append("CREATE_DATE", 1L).append("ACQUIRER_TYPE", 1L)
									.append("TXNTYPE", 1L).append("STATUS", 1L).append("AMOUNT", 1L)
									.append("TOTAL_AMOUNT", 1L).append("ACQUIRER_TDR_SC", 1L).append("ACQUIRER_GST", 1L)
									.append("PG_TDR_SC", 1L).append("PG_GST", 1L)
									.append("DATE_INDEX",
											new BasicDBObject("$substr", Arrays.asList("$DATE_INDEX", 6L, 2L)))),
					new BasicDBObject("$group",
							new BasicDBObject("_id",
									new BasicDBObject("ACQUIRER_TYPE", "$ACQUIRER_TYPE").append("DATE", "$DATE_INDEX"))
											.append("count", new BasicDBObject("$sum", 1L))
											.append("AMOUNT_SUM",
													new BasicDBObject("$sum",
															new BasicDBObject("$toDouble", "$AMOUNT")))
											.append("TOTALAMOUNT_SUM",
													new BasicDBObject("$sum",
															new BasicDBObject("$toDouble", "$TOTAL_AMOUNT")))
											.append("PG_TDR_SC_SUM",
													new BasicDBObject("$sum",
															new BasicDBObject("$toDouble", "$PG_TDR_SC")))
											.append("PG_GST_SUM",
													new BasicDBObject("$sum",
															new BasicDBObject("$toDouble", "$PG_GST")))
											.append("ACQUIRER_TDRSC_SUM",
													new BasicDBObject("$sum",
															new BasicDBObject("$toDouble", "$ACQUIRER_TDR_SC")))
											.append("ACQUIRER_GST_SUM",
													new BasicDBObject("$sum",
															new BasicDBObject("$toDouble", "$ACQUIRER_GST")))
											.append("DATA", new BasicDBObject("$push", "$$ROOT"))));
		} else {
			String days = String.valueOf(getNumberOfDaysInMonth(Integer.parseInt(tmmonth.split("-")[0]),
					Integer.parseInt(tmmonth.split("-")[1])));
			basicDBObjects = Arrays.asList(
					new BasicDBObject("$match",
							new BasicDBObject("DATE_INDEX",
									new Document("$gte", fmmonth.replaceAll("-", "") + "01").append("$lte",
											tmmonth.replaceAll("-", "") + days))
													.append("STATUS", StatusType.SETTLED_SETTLE.getName())
													.append("ACQUIRER_TYPE", new BasicDBObject("$ne", new BsonNull()))
													.append("SURCHARGE_FLAG",
															new BasicDBObject("$eq", new BsonNull()))),
					new BasicDBObject("$project",
							new BasicDBObject("_id", 0L).append("CREATE_DATE", 1L).append("ACQUIRER_TYPE", 1L)
									.append("TXNTYPE", 1L).append("STATUS", 1L).append("AMOUNT", 1L)
									.append("TOTAL_AMOUNT", 1L).append("ACQUIRER_TDR_SC", 1L).append("ACQUIRER_GST", 1L)
									.append("PG_TDR_SC", 1L).append("PG_GST", 1L)
									.append("DATE_INDEX",
											new BasicDBObject("$substr", Arrays.asList("$DATE_INDEX", 0L, 6L)))),
					new BasicDBObject("$group",
							new BasicDBObject("_id",
									new BasicDBObject("ACQUIRER_TYPE", "$ACQUIRER_TYPE").append("DATE", "$DATE_INDEX"))
											.append("count", new BasicDBObject("$sum", 1L))
											.append("AMOUNT_SUM",
													new BasicDBObject("$sum",
															new BasicDBObject("$toDouble", "$AMOUNT")))
											.append("TOTALAMOUNT_SUM",
													new BasicDBObject("$sum",
															new BasicDBObject("$toDouble", "$TOTAL_AMOUNT")))
											.append("PG_TDR_SC_SUM",
													new BasicDBObject("$sum",
															new BasicDBObject("$toDouble", "$PG_TDR_SC")))
											.append("PG_GST_SUM",
													new BasicDBObject("$sum",
															new BasicDBObject("$toDouble", "$PG_GST")))
											.append("ACQUIRER_TDRSC_SUM",
													new BasicDBObject("$sum",
															new BasicDBObject("$toDouble", "$ACQUIRER_TDR_SC")))
											.append("ACQUIRER_GST_SUM",
													new BasicDBObject("$sum",
															new BasicDBObject("$toDouble", "$ACQUIRER_GST")))
											.append("DATA", new BasicDBObject("$push", "$$ROOT"))));

		}

		return basicDBObjects;

	}

	public List<BasicDBObject> getQueryForSurcharge(String type, String date, String yearmonth, String fmmonth,
			String tmmonth, int start, int length) {

		if (type.equalsIgnoreCase("tpd")) {

		} else if (type.equalsIgnoreCase("tm")) {

		} else {

		}

		return null;

	}

	public List<MasterReportDto> getMasterReport(String type, String date, String yearmonth, String fmmonth,
			String tmmonth, int start, int length) {
		List<MasterReportDto> list = new ArrayList<>();

		MongoDatabase dbIns = mongoInstance.getDB();
		MongoCollection<Document> coll = dbIns.getCollection(
				PropertiesManager.propertiesMap.get(prefix + Constants.TRANSACTION_STATUS_COLLECTION.getValue()));
		try {
			List<BasicDBObject> query = getQueryForNoSurcharge(type, date, yearmonth, fmmonth, tmmonth, start, length);
			logger.info("getMasterReport Query : " + query);

			
			
			MongoCursor<Document> cursor = coll.aggregate(query).iterator();
			MasterReportDto dto = null;
			while (cursor.hasNext()) {
				dto = new MasterReportDto();
				Document document = (Document) cursor.next();
				String document1 = document.toJson();
				JSONObject jsonObject = new JSONObject(document1);
				JSONObject jo = jsonObject.getJSONObject("_id");
				JSONObject jo1 = jsonObject.getJSONObject("count");
				dto.setAcquirer(jo.get("ACQUIRER_TYPE") != null ? jo.getString("ACQUIRER_TYPE") : "N/A");
				dto.setCountOftransaction(String.valueOf(jo1.get("$numberLong")));
				dto.setBaseAmount(String.format("%.2f",Double.valueOf(String.valueOf(jsonObject.get("AMOUNT_SUM")))));
				dto.setTotalAmount(String.format("%.2f",Double.valueOf(String.valueOf(jsonObject.get("TOTALAMOUNT_SUM")))));
				dto.setPgTDRAmountTotal(String.format("%.2f",Double.valueOf(String.valueOf(jsonObject.get("PG_TDR_SC_SUM")))));
				dto.setgSTOnPgTDR(String.format("%.2f",Double.valueOf(String.valueOf(jsonObject.get("PG_GST_SUM")))));
				dto.setAcquirerTDRAmontTotal(String.format("%.2f",Double.valueOf(String.valueOf(jsonObject.get("ACQUIRER_TDRSC_SUM")))));
				dto.setgSTOnAcquirerTDR(String.format("%.2f",Double.valueOf(String.valueOf(jsonObject.get("ACQUIRER_GST_SUM")))));
				dto.setTransactionStatusDate(jo.getString("DATE"));
				dto.setTxnType("SALE");
				dto.setStatus(StatusType.SETTLED_SETTLE.getName());

				dto.setAmountReceivedInNodal(String.format("%.2f",
						(Double.valueOf(dto.getTotalAmount()) - (Double.valueOf(dto.getAcquirerTDRAmontTotal())
								+ Double.valueOf(dto.getgSTOnAcquirerTDR())))));
				dto.setAmountPayableToMerchant(String.format("%.2f",(Double.valueOf(dto.getAmountReceivedInNodal())-(Double.valueOf(dto.getPgTDRAmountTotal())+Double.valueOf(dto.getgSTOnPgTDR())))));
			
				list.add(dto);
			}
			logger.info("getMasterReport List Size : " + list.size());
			logger.info("getMasterReport List value : " + new Gson().toJson(list));

		} catch (Exception e) {
			logger.error("Exception Occur in getMasterReport() ", e);
			e.printStackTrace();
		}
		return list;
	}
}
