package com.pay10.notification.sms.smsCreater;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang3.StringUtils;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mongodb.BasicDBObject;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.pay10.commons.mongo.MongoInstance;
import com.pay10.commons.user.InvoiceNumberCounter;
import com.pay10.commons.user.InvoiceNumberCounterDao;
import com.pay10.commons.user.UserDao;
import com.pay10.commons.util.AcquirerType;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.PropertiesManager;
import com.pay10.commons.util.StatusType;
import com.pay10.commons.util.TransactionType;

@Service
public class GenerateInvoiceNumberService {

	@Autowired
	private MongoInstance mongoInstance;

	@Autowired
	PropertiesManager propertiesManager;
	
	@Autowired
	private UserDao userdao;
	
	private static InvoiceNumberCounterDao incdo = new InvoiceNumberCounterDao();
	
	private static Logger logger = LoggerFactory.getLogger(GenerateInvoiceNumberService.class.getName());
	private static final String prefix = "MONGO_DB_";
	
	public void generateInvoiceNumber(String fromDate, String toDate , String collection) {


		logger.info("Inside update ACq id for MPGS");
		try {
			
			BasicDBObject dateQuery = new BasicDBObject();
			BasicDBObject statusQuery = new BasicDBObject();
			BasicDBObject acquierQuery = new BasicDBObject();
			BasicDBObject txntypeQuery = new BasicDBObject();
			BasicDBObject allParamQuery = new BasicDBObject();
			if (!fromDate.isEmpty()) {

				String currentDate = null;
				if (!toDate.isEmpty()) {
					currentDate = toDate;
				} else {
					DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					Calendar cal = Calendar.getInstance();
					currentDate = dateFormat.format(cal.getTime());
				}

				
				if (collection.equalsIgnoreCase("collectionName")) {
					dateQuery.put(FieldType.CREATE_DATE.getName(),
							BasicDBObjectBuilder.start("$gte", new SimpleDateFormat(fromDate).toLocalizedPattern())
									.add("$lte", new SimpleDateFormat(toDate).toLocalizedPattern()).get());
				}
				else {
					dateQuery.put(FieldType.CAPTURED_DATE.getName(),
							BasicDBObjectBuilder.start("$gte", new SimpleDateFormat(fromDate).toLocalizedPattern())
									.add("$lte", new SimpleDateFormat(toDate).toLocalizedPattern()).get());
				}
				
			}

			
			List<BasicDBObject> dateIndexConditionList = new ArrayList<BasicDBObject>();
			BasicDBObject dateIndexConditionQuery = new BasicDBObject();
			String startString = new SimpleDateFormat(fromDate).toLocalizedPattern();
			String endString = new SimpleDateFormat(toDate).toLocalizedPattern();

			DateFormat format1 = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.ENGLISH);
			Date dateStart = format1.parse(startString);
			Date dateEnd = format1.parse(endString);


			
			LocalDate incrementingDate = dateStart.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
			LocalDate endDate = dateEnd.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

			List<String> allDatesIndex = new ArrayList<>();

			while (!incrementingDate.isAfter(endDate)) {
				allDatesIndex.add(incrementingDate.toString().replaceAll("-", ""));
				incrementingDate = incrementingDate.plusDays(1);
			}

			for (String dateIndex : allDatesIndex) {
				dateIndexConditionList.add(new BasicDBObject("DATE_INDEX", dateIndex));
			}
			if (StringUtils.isNotBlank(PropertiesManager.propertiesMap.get(Constants.USE_DATE_INDEX.getValue()))
					&& PropertiesManager.propertiesMap.get(Constants.USE_DATE_INDEX.getValue()).equalsIgnoreCase("Y")) {
				dateIndexConditionQuery.append("$or", dateIndexConditionList);
			}
						
			statusQuery.put(FieldType.STATUS.getName(), StatusType.CAPTURED.getName());

				List<BasicDBObject> allConditionQueryList = new ArrayList<BasicDBObject>();
				if (!dateQuery.isEmpty()) {
					allConditionQueryList.add(dateQuery);
				}
				
				if (!dateIndexConditionQuery.isEmpty()) {
					allConditionQueryList.add(dateIndexConditionQuery);
				}
				
				
				if (!statusQuery.isEmpty()) {
					allConditionQueryList.add(statusQuery);
				}
				
				
				BasicDBObject allConditionQueryObj = new BasicDBObject("$and", allConditionQueryList);
				List<BasicDBObject> fianlList = new ArrayList<BasicDBObject>();

				if (!allParamQuery.isEmpty()) {
					fianlList.add(allParamQuery);
				}
				if (!allConditionQueryObj.isEmpty()) {
					fianlList.add(allConditionQueryObj);
				}

				BasicDBObject finalquery = new BasicDBObject("$and", fianlList);
				logger.info("finalquery processing for CAPTURED_DATE index update = " + finalquery);
				MongoDatabase dbIns = mongoInstance.getDB();
				
				
				if (StringUtils.isBlank(collection)) {
					logger.info("Please provide collection value  ");
				}
				else{
					
					logger.info("Collection name is "+collection);
					MongoCollection<Document> coll = dbIns
							.getCollection(PropertiesManager.propertiesMap.get(prefix + collection));
					
					BasicDBObject match = new BasicDBObject("$match", finalquery);
					BasicDBObject projectElement = new BasicDBObject();
					projectElement.put("PAY_ID", 1);
					BasicDBObject project = new BasicDBObject("$project", projectElement);
					
					List<BasicDBObject> payIdPipeline = Arrays.asList(match, project);
					AggregateIterable<Document> payIdTotal = coll.aggregate(payIdPipeline);
					payIdTotal.allowDiskUse(true);

					// Unique Pay Id's getting from the txn collection 
					// Removing the Duplicates
					HashSet payIdDistinctset = new HashSet();
					MongoCursor<Document> payIdTotalCursor = (MongoCursor<Document>) payIdTotal.iterator();

					while (payIdTotalCursor.hasNext()) {
						payIdDistinctset.add(payIdTotalCursor.next().get("PAY_ID"));
					}
					payIdTotalCursor.close();
					payIdDistinctset.remove("0");
					ArrayList<String> payIdAr = new ArrayList<String>(payIdDistinctset);
					
					// Creating the invoice number and unique pay id's and inserting into DB based on Monthly wise  
					if(payIdAr != null && payIdAr.size() > 0) {
						InvoiceNumberCounter inc=new InvoiceNumberCounter();
						inc = incdo.getLatestInvoiceNumberByPayId();
						int invoiceNumber = inc.getInvoiceNumber();
						long invoiceId=inc.getInvoiceId();
						DateFormat adf1 = new SimpleDateFormat("dd-MM-yyyy");
						Date MonthDate = adf1.parse(toDate);

						DateFormat df1 = new SimpleDateFormat("yyMM");
						String todayDate1 = df1.format(MonthDate);
						
						for(int k=0;k<payIdAr.size()-1;k++) {
							String newPayId = payIdAr.get(k)+todayDate1;
							invoiceNumber +=1;
							invoiceId +=1L;
							inc.setInvoiceId(invoiceId);
							inc.setPayId(newPayId);
							inc.setInvoiceNumber(invoiceNumber);
							incdo.create(inc);
						}
					}
					
				}
			
			
		} catch (Exception e) {
			logger.error("Exception occured while updating acq id " + e);
		}
	
	}
	
}
