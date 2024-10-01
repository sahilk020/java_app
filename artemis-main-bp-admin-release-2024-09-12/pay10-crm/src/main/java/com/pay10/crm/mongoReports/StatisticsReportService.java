package com.pay10.crm.mongoReports;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bson.Document;
import org.json.JSONArray;
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
import com.pay10.commons.mongo.MongoInstance;
import com.pay10.commons.user.Merchants;
import com.pay10.commons.user.StatisticsReportData;
import com.pay10.commons.user.UserDao;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.DateCreater;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.PaymentType;
import com.pay10.commons.util.PropertiesManager;

/**
 * @author Chandan
 *
 */

@Service
public class StatisticsReportService {

	private static Logger logger = LoggerFactory.getLogger(StatisticsReportService.class.getName());
	private static final String prefix = "MONGO_DB_";

	@Autowired
	PropertiesManager propertiesManager;

	@Autowired
	private MongoInstance mongoInstance;

	@Autowired
	private UserDao userdao;

	public JSONObject getSummaryData(String merchant, String fromDate, String toDate, String acquirer,
			String paymentType, String segment, long roleId) {

		logger.info("Inside StatisticsReportService Class, in getSummaryData Method !!");
		List<Merchants> merchantsList = new ArrayList<Merchants>();
		Map<String, String> mapBusinessName = new HashMap<String, String>();
		//merchantsList = userdao.getActiveMerchantList();
		merchantsList = userdao.getActiveMerchantList(segment, roleId);
		for (Merchants merchants : merchantsList) {
			mapBusinessName.put(merchants.getPayId(), merchants.getBusinessName());
		}
		try {
			List<StatisticsReportData> lstStatisticsReport = new ArrayList<StatisticsReportData>();
			List<StatisticsReportData> lstStatisticsReports = new ArrayList<StatisticsReportData>();

			PropertiesManager propManager = new PropertiesManager();
			LocalDate startDate = DateCreater.formatStringToLocalDate(fromDate);
			LocalDate endDate = DateCreater.formatStringToLocalDate(toDate).plusDays(1);
			/*
			 * Map<String, Double> mapAmount = new HashMap<String, Double>(); Map<String,
			 * Integer> mapCount = new HashMap<String, Integer>();
			 */

			for (LocalDate date = startDate; date.isBefore(endDate); date = date.plusDays(1)) {
				/*
				 * mapAmount.clear(); mapCount.clear();
				 */
				lstStatisticsReport.clear();
				List<BasicDBObject> paramConditionLst = new ArrayList<BasicDBObject>();
				BasicDBObject dateQuery = new BasicDBObject();
				BasicDBObject paymentTypeQuery = new BasicDBObject();
				BasicDBObject allParamQuery = new BasicDBObject();
				List<BasicDBObject> paymentTypeConditionLst = new ArrayList<BasicDBObject>();
				if (!startDate.toString().isEmpty()) {
					String currentDate = null;

					DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					Calendar cal = Calendar.getInstance();
					// add days to from date
					Date date1 = dateFormat.parse(date.toString() + " 00:00:00");
					String fromDT = date.toString() + " 00:00:00";
					cal.setTime(date1);
					cal.add(Calendar.DATE, 1);
					currentDate = dateFormat.format(cal.getTime());

					dateQuery.put(FieldType.CAPTURED_DATE.getName(),
							BasicDBObjectBuilder
									.start("$gte", new SimpleDateFormat(fromDT.toString()).toLocalizedPattern())
									.add("$lt", new SimpleDateFormat(currentDate).toLocalizedPattern()).get());
				}

				if (!merchant.equalsIgnoreCase("ALL")) {
					paramConditionLst.add(new BasicDBObject(FieldType.PAY_ID.getName(), merchant));
				}

				if (!acquirer.equalsIgnoreCase("ALL")) {
					paramConditionLst.add(new BasicDBObject(FieldType.ACQUIRER_TYPE.getName(), acquirer));
				}
				if (!paymentType.equalsIgnoreCase("ALL")) {
					if (paymentType.equalsIgnoreCase("CC-DC")) {

						paymentTypeConditionLst.add(
								new BasicDBObject(FieldType.PAYMENT_TYPE.getName(), PaymentType.CREDIT_CARD.getCode()));
						paymentTypeConditionLst.add(
								new BasicDBObject(FieldType.PAYMENT_TYPE.getName(), PaymentType.DEBIT_CARD.getCode()));
						paymentTypeQuery.append("$or", paymentTypeConditionLst);

					} else if (paymentType.equalsIgnoreCase("UPI")) {
						paramConditionLst
								.add(new BasicDBObject(FieldType.PAYMENT_TYPE.getName(), PaymentType.UPI.getCode()));
					}

				}
				if (!paramConditionLst.isEmpty()) {
					allParamQuery = new BasicDBObject("$and", paramConditionLst);
				}

				List<BasicDBObject> allConditionQueryList = new ArrayList<BasicDBObject>();

				if (!dateQuery.isEmpty()) {
					allConditionQueryList.add(dateQuery);
				}

				List<BasicDBObject> fianlList = new ArrayList<BasicDBObject>();

				if (!allParamQuery.isEmpty()) {
					fianlList.add(allParamQuery);
				}

				if (!allConditionQueryList.isEmpty()) {
					BasicDBObject allConditionQueryObj = new BasicDBObject("$and", allConditionQueryList);
					if (!allConditionQueryObj.isEmpty()) {
						fianlList.add(allConditionQueryObj);
					}
				}

				if (!paymentTypeQuery.isEmpty()) {
					fianlList.add(paymentTypeQuery);
				}

				BasicDBObject finalquery = new BasicDBObject("$and", fianlList);

				logger.info("Inside StatisticsReportService , getSummaryData , finalquery = " + finalquery);
				MongoDatabase dbIns = mongoInstance.getDB();
				MongoCollection<Document> coll = dbIns.getCollection(
						PropertiesManager.propertiesMap.get(prefix + Constants.SUMMARY_TRANSACTIONS_NAME.getValue()));

				MongoCursor<Document> cursor = coll.find(finalquery).iterator();
				double totalCapturedAmount = 0.0, totalSettledAmount = 0.0;
				int totalCapturedCount = 0, totalSettledCount = 0;
				int index = 0;
				boolean isExist = false;
				while (cursor.hasNext()) {
					Document doc = cursor.next();
					index = 0;
					isExist = false;
					StatisticsReportData statisticsReportData = null;
					for (StatisticsReportData statisticsReport : lstStatisticsReport) {
						if (statisticsReport.getMerchant().equals(doc.getString(FieldType.PAY_ID.toString()))
								&& statisticsReport.getCapturedTxnDate().equals(date.toString())) {
							statisticsReportData = statisticsReport;
							statisticsReportData.setCapturedTotalTxn(statisticsReportData.getCapturedTotalTxn() + 1);
							statisticsReportData.setCapturedTotalAmount(statisticsReportData.getCapturedTotalAmount()
									+ doc.getDouble(FieldType.TOTAL_AMOUNT.toString()));
							if (!doc.getString(FieldType.SETTLEMENT_DATE.toString()).equals("")) {
								String settledDate = DateCreater
										.formatDate(doc.getString(FieldType.SETTLEMENT_DATE.toString())).toString();
								if (statisticsReportData.getMapAmount().containsKey(settledDate)) {
									double settledAmount = statisticsReportData.getMapAmount().get(settledDate);
									Integer settledCount = statisticsReportData.getMapCount().get(settledDate);
									// Calculate Settled Amount
									settledAmount = settledAmount + doc.getDouble(FieldType.TOTAL_AMOUNT.toString());
									statisticsReportData.getMapAmount().put(settledDate, settledAmount);
									// Calculate Settled Count
									settledCount = settledCount + 1;
									statisticsReportData.getMapCount().put(settledDate, settledCount);
								} else {
									statisticsReportData.getMapAmount().put(settledDate,
											doc.getDouble(FieldType.TOTAL_AMOUNT.toString()));
									statisticsReportData.getMapCount().put(settledDate, 1);
								}
							}
							if (!doc.getString(FieldType.NODAL_CREDIT_DATE.toString()).equals("")) {
								String nodalDate = DateCreater
										.formatDate(doc.getString(FieldType.NODAL_CREDIT_DATE.toString())).toString();
								if (statisticsReportData.getNodalMapAmount().containsKey(nodalDate)) {
									double nodalAmount = statisticsReportData.getNodalMapAmount().get(nodalDate);
									Integer nodalCount = statisticsReportData.getNodalMapCount().get(nodalDate);
									// Calculate Settled Amount
									nodalAmount = nodalAmount + doc.getDouble(FieldType.TOTAL_AMOUNT.toString());
									statisticsReportData.getNodalMapAmount().put(nodalDate, nodalAmount);
									// Calculate Settled Count
									nodalCount = nodalCount + 1;
									statisticsReportData.getNodalMapCount().put(nodalDate, nodalCount);
								} else {
									statisticsReportData.getNodalMapAmount().put(nodalDate,
											doc.getDouble(FieldType.TOTAL_AMOUNT.toString()));
									statisticsReportData.getNodalMapCount().put(nodalDate, 1);
								}
							}

							lstStatisticsReport.set(index, statisticsReportData);
							isExist = true;

							break;
						}

						index++;
					}
					if (!isExist) {
						StatisticsReportData statisticsReport = new StatisticsReportData();
						statisticsReport.setMerchant(doc.getString(FieldType.PAY_ID.toString()));
						statisticsReport.setCapturedTxnDate(date.toString());
						statisticsReport.setCapturedTotalTxn(1);
						statisticsReport.setCapturedTotalAmount(doc.getDouble(FieldType.TOTAL_AMOUNT.toString()));
						statisticsReport.setMapAmount(new HashMap<String, Double>());
						statisticsReport.setMapCount(new HashMap<String, Integer>());
						if (!doc.getString(FieldType.SETTLEMENT_DATE.toString()).equals("")) {
							String settledDate = DateCreater
									.formatDate(doc.getString(FieldType.SETTLEMENT_DATE.toString())).toString();
							statisticsReport.getMapAmount().put(settledDate,
									doc.getDouble(FieldType.TOTAL_AMOUNT.toString()));
							statisticsReport.getMapCount().put(settledDate, 1);
						}
						statisticsReport.setNodalMapAmount(new HashMap<String, Double>());
						statisticsReport.setNodalMapCount(new HashMap<String, Integer>());
						if (!doc.getString(FieldType.NODAL_CREDIT_DATE.toString()).equals("")) {
							String nodalDate = DateCreater
									.formatDate(doc.getString(FieldType.NODAL_CREDIT_DATE.toString())).toString();
							statisticsReport.getNodalMapAmount().put(nodalDate,
									doc.getDouble(FieldType.TOTAL_AMOUNT.toString()));
							statisticsReport.getNodalMapCount().put(nodalDate, 1);
						}
						lstStatisticsReport.add(statisticsReport);
					}
				}
				cursor.close();

				String settledCount = "", settledAmount = "", settledDate = "";
				String nodalCount = "", nodalAmount = "", nodalDate = "";
				Double totalNodalAmount = 0.0;
				int totalNodalCount = 0;
				for (StatisticsReportData statisticsReport : lstStatisticsReport) {
					settledCount = "";
					settledAmount = "";
					settledDate = "";
					nodalCount = "";
					nodalAmount = "";
					nodalDate = "";
					totalNodalAmount = 0.0;
					totalNodalCount = 0;
					for (Map.Entry<String, Double> entry : statisticsReport.getMapAmount().entrySet()) {
						if (settledAmount.equals("")) {
							settledAmount = String.format("%.2f", entry.getValue());
							settledDate = entry.getKey().toString();
						} else {
							settledAmount = settledAmount + "," + String.format("%.2f", entry.getValue());
							settledDate = settledDate + "," + entry.getKey().toString();
						}
					}
					for (Map.Entry<String, Integer> entry : statisticsReport.getMapCount().entrySet()) {
						if (settledCount.equals("")) {
							settledCount = entry.getValue().toString();
						} else {
							settledCount = settledCount + "," + entry.getValue().toString();
						}
					}

					for (Map.Entry<String, Double> entry : statisticsReport.getNodalMapAmount().entrySet()) {
						if (nodalAmount.equals("")) {
							nodalAmount = String.format("%.2f", entry.getValue());
							nodalDate = entry.getKey().toString();
						} else {
							nodalAmount = nodalAmount + "," + String.format("%.2f", entry.getValue());
							nodalDate = nodalDate + "," + entry.getKey().toString();
						}
						totalNodalAmount += entry.getValue();
					}
					for (Map.Entry<String, Integer> entry : statisticsReport.getNodalMapCount().entrySet()) {
						if (nodalCount.equals("")) {
							nodalCount = entry.getValue().toString();
						} else {
							nodalCount = nodalCount + "," + entry.getValue().toString();
						}
						totalNodalCount += entry.getValue();
					}

					StatisticsReportData statisticsReportData = new StatisticsReportData();
					statisticsReportData.setMerchant(mapBusinessName.get(statisticsReport.getMerchant()));
					statisticsReportData.setCapturedTxnDate(date.toString());
					statisticsReportData.setCapturedTotalTxn(statisticsReport.getCapturedTotalTxn());
					statisticsReportData.setCapturedTotalAmount(statisticsReport.getCapturedTotalAmount());
					statisticsReportData.setSettledTotalTxn(settledCount);
					statisticsReportData.setSettledTotalAmount(settledAmount);
					statisticsReportData.setSettledTxnDate(settledDate);
					statisticsReportData.setNodalTotalTxn(nodalCount);
					statisticsReportData.setNodalTotalAmount(nodalAmount);
					statisticsReportData.setNodalTxnDate(nodalDate);
					statisticsReportData.setPendingTotalAmount(
							String.format("%.2f", (statisticsReport.getCapturedTotalAmount() - totalNodalAmount)));
					statisticsReportData.setPendingTotalTxn(
							Integer.toString(statisticsReport.getCapturedTotalTxn() - totalNodalCount));

					lstStatisticsReports.add(statisticsReportData);
				}
			}
			System.out.println("lstStatisticsReport count : " + lstStatisticsReports.size());
			JSONObject jsonObject = new JSONObject();
			jsonObject = createJSON(lstStatisticsReports);
			return jsonObject;
		}

		catch (Exception e) {
			logger.error("Exception occured in StatisticsReportService , getSummaryData , Exception = " + e);
			return null;
		}

	}

	private JSONObject createJSON(List<StatisticsReportData> lstTxnSummaryReport) {
		JSONObject jsonParent = new JSONObject();
		try {
			JSONArray arrayParent = new JSONArray();
			JSONObject json;
			for (StatisticsReportData txnSummaryReportData : lstTxnSummaryReport) {
				json = new JSONObject();
				json.put("merchant", txnSummaryReportData.getMerchant());
				json.put("txnDate", txnSummaryReportData.getCapturedTxnDate());
				json.put("CapNoTxn", txnSummaryReportData.getCapturedTotalTxn());
				json.put("CapAmount", String.format("%.2f", txnSummaryReportData.getCapturedTotalAmount()));

				JSONArray arrayCount = new JSONArray();
				String[] arrSettledCount = txnSummaryReportData.getSettledTotalTxn().split(",");
				for (int i = 0; i < arrSettledCount.length; i++) {
					if (!arrSettledCount[i].toString().equals(""))
						arrayCount.put(Integer.parseInt(arrSettledCount[i]));
					else
						arrayCount.put(0);
				}
				json.put("SettleNoTxn", arrayCount);

				JSONArray arrayAmount = new JSONArray();
				String[] arrSettledAmount = txnSummaryReportData.getSettledTotalAmount().split(",");
				for (int i = 0; i < arrSettledAmount.length; i++) {
					if (!arrSettledAmount[i].toString().equals(""))
						arrayAmount.put(Double.parseDouble(arrSettledAmount[i]));
					else
						arrayAmount.put(0.0);
				}
				json.put("SettleAmount", arrayAmount);

				JSONArray arrayDate = new JSONArray();
				String[] arrSettledDate = txnSummaryReportData.getSettledTxnDate().split(",");
				for (int i = 0; i < arrSettledDate.length; i++) {
					arrayDate.put(arrSettledDate[i]);
				}
				json.put("SettleDate", arrayDate);

				JSONArray arrayCnt = new JSONArray();
				String[] arrNodalCount = txnSummaryReportData.getNodalTotalTxn().split(",");
				for (int i = 0; i < arrNodalCount.length; i++) {
					if (!arrNodalCount[i].toString().equals(""))
						arrayCnt.put(Integer.parseInt(arrNodalCount[i]));
					else
						arrayCnt.put(0);
				}
				json.put("NodalNoTxn", arrayCnt);

				JSONArray arrayNodalAmt = new JSONArray();
				String[] arrNodalAmount = txnSummaryReportData.getNodalTotalAmount().split(",");
				for (int i = 0; i < arrNodalAmount.length; i++) {
					if (!arrNodalAmount[i].toString().equals(""))
						arrayNodalAmt.put(Double.parseDouble(arrNodalAmount[i]));
					else
						arrayNodalAmt.put(0.0);
				}
				json.put("NodalAmount", arrayNodalAmt);

				JSONArray arrayDt = new JSONArray();
				String[] arrNodalDate = txnSummaryReportData.getNodalTxnDate().split(",");
				for (int i = 0; i < arrNodalDate.length; i++) {
					arrayDt.put(arrNodalDate[i]);
				}
				json.put("NodalDate", arrayDt);

				json.put("PendingNoTxn", txnSummaryReportData.getPendingTotalTxn());
				json.put("PendingAmount", txnSummaryReportData.getPendingTotalAmount());

				arrayParent.put(json);

				jsonParent.put("response", arrayParent);
			}
		} catch (Exception e) {
			logger.error("Exception occured in createJSON , StatisticsReportService , Exception = " + e);
			return null;
		}

		return jsonParent;
	}
}
