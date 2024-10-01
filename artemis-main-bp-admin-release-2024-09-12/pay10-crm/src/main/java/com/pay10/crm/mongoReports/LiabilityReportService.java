package com.pay10.crm.mongoReports;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.bson.BsonNull;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.pay10.commons.mongo.MongoInstance;
import com.pay10.commons.user.LiabilityReportObject;
import com.pay10.commons.user.User;
import com.pay10.commons.user.UserDao;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.MopType;
import com.pay10.commons.util.PropertiesManager;

@Service
public class LiabilityReportService {
	@Autowired
	private MongoInstance mongoInstance;
	private static final String prefix = "MONGO_DB_";

	@Autowired
	private UserDao userdao;

	private static Logger logger = LoggerFactory.getLogger(LiabilityReportService.class.getName());

	public List<BasicDBObject> getQuery(String payid, String acquirer, String dateFrom, String dateTo,
			String reportType) {
		BasicDBObject match = new BasicDBObject();

		if (!payid.equalsIgnoreCase("All")) {
			match.put("PAY_ID", payid);
		}

		if (reportType.equalsIgnoreCase("LiabilityAudit")) {
//			match.put("HOLD_DATE", new BasicDBObject("$ne", new BsonNull()));
//			match.put("RELEASE_DATE", new BasicDBObject("$ne", new BsonNull()));
			//match.put("HOLD_RELEASE", 0L);
			match.put("$and", Arrays.asList(
		            new BasicDBObject("HOLD_DATE", new BasicDBObject("$ne", new BsonNull())),
		            new BasicDBObject("RELEASE_DATE",new BasicDBObject("$ne", new BsonNull()))
		        ));
		} else if (reportType.equalsIgnoreCase("hold")) {
			match.put("HOLD_DATE", new BasicDBObject("$ne", new BsonNull()));
			match.put("RELEASE_DATE", new BsonNull());
			match.put("HOLD_RELEASE", 1L);
			
		} else {
			if (reportType.equalsIgnoreCase("Release")) {
				match.put("RELEASE_DATE", new BasicDBObject("$ne", new BsonNull()));
				match.put("HOLD_RELEASE", 1L);
			}
		}

		if (!acquirer.equalsIgnoreCase("All")) {
			match.put("ACQUIRER_TYPE", acquirer);
		}
		match.put("DATE_INDEX", new BasicDBObject("$gte", dateFrom).append("$lte", dateTo));

		BasicDBObject project1 = new BasicDBObject();
		project1.put("_id", 0L);
		project1.put("HOLD_RELEASE", 1L);
		if (reportType.equalsIgnoreCase("LiabilityAudit") || reportType.equalsIgnoreCase("Hold")) {
			project1.put("HOLD_DATE", 1L);
		}
		if (reportType.equalsIgnoreCase("LiabilityAudit") || reportType.equalsIgnoreCase("Release")) {
			project1.put("RELEASE_DATE", 1L);
		}
		project1.put("PAY_ID", 1L);
		project1.put("MOP_TYPE", 1L);
		project1.put("TOTAL_AMOUNT", 1L);
		project1.put("PG_REF_NUM", 1L);
		project1.put("DATE_INDEX", 1L);
		project1.put("TXNTYPE", 1L);
		project1.put("PAYMENT_TYPE", 1L);
		project1.put("ALIAS_STATUS", 1L);
		project1.put("ACQUIRER_TYPE", 1L);
		if (reportType.equalsIgnoreCase("LiabilityAudit")) {
			project1.put("LIABILITY_HOLD_REMARKS", 1L);
			project1.put("LIABILITY_RELEASE_REMARKS", 1L);
		} 
		if (reportType.equalsIgnoreCase("hold")) {
			project1.put("LIABILITY_HOLD_REMARKS", 1L);
			
		}
		project1.put("LIABILITY_HOLD_REMARKS", 1L);

		if (reportType.equalsIgnoreCase("LiabilityAudit")) {
			project1.put("DATE_DIFFERENCE_HOLD",
					new BasicDBObject("$toInt", new BasicDBObject("$substr", Arrays.asList("$HOLD_DATE", 8L, 2L))));
			project1.put("DATE_DIFFERENCE_RELEASE",
					new BasicDBObject("$toInt", new BasicDBObject("$substr", Arrays.asList("$RELEASE_DATE", 8L, 2L))));
		}

		BasicDBObject project = new BasicDBObject();
		project.put("HOLD_RELEASE", 1L);
		if (reportType.equalsIgnoreCase("LiabilityAudit") || reportType.equalsIgnoreCase("Hold")) {
			project.put("HOLD_DATE", 1L);
		}
		if (reportType.equalsIgnoreCase("LiabilityAudit") || reportType.equalsIgnoreCase("Release")) {
			project.put("RELEASE_DATE", 1L);
		}
		project.put("PAY_ID", 1L);
		project.put("MOP_TYPE", 1L);
		project.put("TOTAL_AMOUNT", 1L);
		project.put("PG_REF_NUM", 1L);
		project.put("DATE_INDEX", 1L);
		project.put("TXNTYPE", 1L);
		project.put("PAYMENT_TYPE", 1L);
		project.put("ALIAS_STATUS", 1L);
		project.put("ACQUIRER_TYPE", 1L);
		if (reportType.equalsIgnoreCase("LiabilityAudit")) {
			project.put("LIABILITY_HOLD_REMARKS", 1L);
			project.put("LIABILITY_RELEASE_REMARKS", 1L);
		} 
		if (reportType.equalsIgnoreCase("hold")) {
			project.put("LIABILITY_HOLD_REMARKS", 1L);
			
		}
		if (reportType.equalsIgnoreCase("LiabilityAudit")) {
			project.put("DIFFERENCE",
					new BasicDBObject("$subtract", Arrays.asList("$DATE_DIFFERENCE_RELEASE", "$DATE_DIFFERENCE_HOLD")));
		}

		List<BasicDBObject> query = Arrays.asList(new BasicDBObject("$match", match),
				new BasicDBObject("$project", project1), new BasicDBObject("$project", project));

		return query;
	}

	public List<LiabilityReportObject> getLiabilityReport(String merchant, String acquirer, String reportType,
			String dateFrom, String dateTo) {
		List<LiabilityReportObject> transactionList = new ArrayList<LiabilityReportObject>();
		try {
			MongoDatabase dbIns = mongoInstance.getDB();
			MongoCollection<Document> collection = dbIns.getCollection(
					PropertiesManager.propertiesMap.get(prefix + Constants.TRANSACTION_STATUS_COLLECTION.getValue()));

//			DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
//			Date dateto=dateFormat.parse(dateTo);
//			Date datefrom=dateFormat.parse(dateFrom);
//		
//			DateFormat dateFormat1 = new SimpleDateFormat("yyyyMMdd");
//			dateFrom = dateFormat1.format(datefrom);
//			dateTo = dateFormat1.format(dateto);

			logger.info("merchant : " + merchant + "\t acquirer : " + acquirer + "\treportType :" + reportType
					+ "\tdateFrom : " + dateFrom.replaceAll("-", "") + "\tdateTo :" + dateTo.replaceAll("-", ""));

			List<BasicDBObject> query = getQuery(merchant, acquirer, dateFrom.replaceAll("-", ""),
					dateTo.replaceAll("-", ""), reportType);
			logger.info("Query : " + query);

			MongoCursor<Document> mongoCursor = collection.aggregate(query).iterator();

			while (mongoCursor.hasNext()) {
				Document document = (Document) mongoCursor.next();
				logger.info(document.toJson());
				LiabilityReportObject liabilityReportObject = new LiabilityReportObject();

				String payid = document.getString("PAY_ID");
				liabilityReportObject.setBusinessName(userdao.getBusinessNameByPayId(payid));

				if (reportType.equalsIgnoreCase("LiabilityAudit") || reportType.equalsIgnoreCase("Hold")) {
					liabilityReportObject.setHoldDate(
							new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(document.getDate("HOLD_DATE")));
				}
				if (reportType.equalsIgnoreCase("LiabilityAudit") || reportType.equalsIgnoreCase("Release")) {
					liabilityReportObject.setReleaseDate(
							new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(document.getDate("RELEASE_DATE")));
				}

				liabilityReportObject.setMopType(MopType.getmopName(document.getString("MOP_TYPE")));
				liabilityReportObject.setTotalAmount(document.getString("TOTAL_AMOUNT"));
				liabilityReportObject.setPgRefNum(document.getString("PG_REF_NUM"));
				liabilityReportObject.setDateIndex(new SimpleDateFormat("yyyy-MM-dd").format(new SimpleDateFormat("yyyyMMdd").parse(document.getString("DATE_INDEX"))));
				liabilityReportObject.setTxnType(document.getString("TXNTYPE"));
				liabilityReportObject.setPaymentType(document.getString("PAYMENT_TYPE"));
				liabilityReportObject.setAliasStaus(document.getString("ALIAS_STATUS"));
				liabilityReportObject.setAcquirerType(document.getString("ACQUIRER_TYPE"));

				if (reportType.equalsIgnoreCase("LiabilityAudit")) {
					liabilityReportObject.setLiabilityHoldRemarks(document.getString(FieldType.LIABILITYHOLDREMARKS.getName()));
					liabilityReportObject.setLiabilityReleaseRemark(document.getString(FieldType.LIABILITYRELEASEREMARKS.getName()));
					
				} 
				if (reportType.equalsIgnoreCase("hold")) {
					liabilityReportObject.setLiabilityHoldRemarks(document.getString(FieldType.LIABILITYHOLDREMARKS.getName()));
					
				}
				
				if (reportType.equalsIgnoreCase("LiabilityAudit")) {
					liabilityReportObject.setDay(String.valueOf(Integer.parseInt(String.valueOf(document.get("DIFFERENCE")))+1));
				}
				transactionList.add(liabilityReportObject);

			}

		} catch (Exception e) {
			logger.error("Exception Occur in getLiabilityReport() : ", e);
			e.printStackTrace();
		}
		return transactionList;
	}

}
